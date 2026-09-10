import java.io.*;
import java.lang.management.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Passive process/character measurements. Never connects to the game server. */
public final class HeadlessMetrics implements BotMetrics.Sink {
    private final PrintWriter samples, events, results;
    private final com.sun.management.OperatingSystemMXBean os;
    private final String processId = ManagementFactory.getRuntimeMXBean().getName();
    private final String runId = UUID.randomUUID().toString();
    private final String profile = System.getProperty("nso.metrics.label", "default");
    private final String pass;
    private String account = "", character = "", attempt = "", phase = "idle";
    private int sequence, level, classId, tasks, reconnects, errors, sampleCount;
    private long lastErrorLogNs;
    private boolean dailyFinished, didWork;
    private long startNs, startCpu, startGcMs, startGcCount, phaseNs, previousNs, previousCpu;
    private long rssSum, rssPeak;
    private final Map<String, Long> phases = new LinkedHashMap<String, Long>();

    private HeadlessMetrics(Path directory) throws IOException {
        Files.createDirectories(directory);
        os = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        pass = Files.exists(Paths.get(System.getProperty("user.home"), "worker.first-pass.done")) ? "2" : "1";
        samples = writer(directory.resolve("metrics.csv"),
                "run_id,process_id,label,time_ms,attempt_id,phase,rss_kb,process_hwm_kb,heap_used_bytes,heap_committed_bytes,cpu_ns,cpu_percent,gc_count,gc_ms,effects,queue_depth,sent,queue_waits,dropped,max_queue_delay_ms,tick_ms,skip_gc,skip_popup,skip_decorations,event_sender");
        events = writer(directory.resolve("character-events.csv"),
                "run_id,label,pass,attempt_id,account,character,time_ms,elapsed_ms,cpu_ms,event,detail,value");
        results = writer(directory.resolve("character-results.csv"),
                "run_id,label,pass,attempt_id,account,character,level,class_id,outcome,reason,daily_finished,did_daily_work,confirmed_task_returns,reconnects,wall_ms,cpu_ms,rss_mean_kb,rss_sample_peak_kb,gc_count,gc_ms,select_ms,prepare_ms,daily_ms,flip_ms,cave_ms,reconnect_ms,error_count");
    }

    public static void install() {
        if (!HeadlessTuning.ENABLED || "false".equals(System.getProperty("nso.metrics"))) return;
        try {
            final HeadlessMetrics metrics = new HeadlessMetrics(Paths.get(System.getProperty("nso.metrics.dir",
                    System.getProperty("user.home") + "/metrics")));
            BotMetrics.sink = metrics;
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() { metrics.shutdown(); }
            }, "NSO-Metrics-Shutdown"));
            Thread sampler = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try { Thread.sleep(5000L); metrics.sample(); }
                        catch (InterruptedException ex) { return; }
                        catch (Exception ex) { System.err.println("HEADLESS METRICS: " + ex); }
                    }
                }
            }, "NSO-Metrics");
            sampler.setDaemon(true);
            sampler.start();
            System.out.println("HEADLESS METRICS: run_id=" + metrics.runId + " label=" + metrics.profile);
        } catch (Exception ex) {
            System.err.println("HEADLESS METRICS disabled: " + ex);
        }
    }

    private static PrintWriter writer(Path path, String header) throws IOException {
        boolean empty = !Files.exists(path) || Files.size(path) == 0;
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(path.toFile(), true), StandardCharsets.UTF_8)), true);
        if (empty) writer.println(header);
        return writer;
    }

    private static void row(PrintWriter writer, Object... values) {
        StringBuilder line = new StringBuilder();
        for (Object value : values) {
            if (line.length() > 0) line.append(',');
            line.append('"').append(String.valueOf(value).replace("\"", "\"\"")).append('"');
        }
        writer.println(line);
    }

    private long cpu() { return os.getProcessCpuTime(); }

    private static long[] gc() {
        long count = 0, time = 0;
        for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            count += Math.max(0, bean.getCollectionCount());
            time += Math.max(0, bean.getCollectionTime());
        }
        return new long[]{count, time};
    }

    private static long[] rss() {
        long resident = -1, highWater = -1;
        try {
            for (String line : Files.readAllLines(Paths.get("/proc/self/status"), StandardCharsets.UTF_8)) {
                if (line.startsWith("VmRSS:")) resident = Long.parseLong(line.trim().split("\\s+")[1]);
                if (line.startsWith("VmHWM:")) highWater = Long.parseLong(line.trim().split("\\s+")[1]);
            }
        } catch (IOException ignored) { }
        return new long[]{resident, highWater};
    }

    public synchronized void begin(String username, String name) {
        if (!attempt.isEmpty() && account.equals(username) && character.equals(name)) {
            event("character_reselected", "", 0);
            setPhase("select");
            return;
        }
        finish("interrupted", "another character selected");
        account = username;
        character = name;
        attempt = runId + "-" + (++sequence);
        startNs = System.nanoTime();
        startCpu = cpu();
        long[] gc = gc();
        startGcCount = gc[0]; startGcMs = gc[1];
        tasks = reconnects = errors = sampleCount = level = classId = 0;
        lastErrorLogNs = 0;
        dailyFinished = didWork = false;
        rssSum = rssPeak = 0;
        phases.clear(); phase = "select"; phaseNs = startNs;
        event("selected", "", 0);
        sample();
    }

    public synchronized void event(String kind, String detail, int value) {
        if (attempt.isEmpty()) return;
        if ("error".equals(kind)) {
            ++errors;
            long now = System.nanoTime();
            if (lastErrorLogNs != 0 && now - lastErrorLogNs < 1000000000L) return;
            lastErrorLogNs = now;
        }
        if ("game_ready".equals(kind)) {
            level = value;
            try { classId = Integer.parseInt(detail); } catch (NumberFormatException ignored) { }
            setPhase("prepare");
        } else if ("daily_started".equals(kind)) setPhase("daily");
        else if ("daily_finished".equals(kind)) { dailyFinished = true; didWork |= value != 0; }
        else if ("flip_started".equals(kind)) setPhase("flip");
        else if ("cave_started".equals(kind)) setPhase("cave");
        else if ("reconnect".equals(kind)) { ++reconnects; setPhase("reconnect"); }
        else if ("task_return_confirmed".equals(kind)) { ++tasks; didWork = true; }
        row(events, runId, profile, pass, attempt, account, character, System.currentTimeMillis(),
                (System.nanoTime() - startNs) / 1000000L, (cpu() - startCpu) / 1000000L, kind, detail, value);
    }

    private void setPhase(String next) {
        setPhase(next, System.nanoTime());
    }

    private void setPhase(String next, long now) {
        Long accumulated = phases.get(phase);
        phases.put(phase, (accumulated == null ? 0 : accumulated) + now - phaseNs);
        phase = next; phaseNs = now;
    }

    private long phaseMillis(String name) {
        Long value = phases.get(name);
        return value == null ? 0 : value / 1000000L;
    }

    synchronized void sample() {
        long now = System.nanoTime(), cpu = cpu();
        long[] rss = rss(), gc = gc();
        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        // Read no game objects until character selection has initialized the client.
        long[] queue = sequence == 0 ? new long[5] : Session_ME.gI().senderStats();
        String cpuPercent = previousNs == 0 ? "" : String.format(Locale.ROOT, "%.3f",
                100.0 * (cpu - previousCpu) / (now - previousNs));
        previousNs = now; previousCpu = cpu;
        row(samples, runId, processId, profile, System.currentTimeMillis(), attempt, phase,
                rss[0], rss[1], heap.getUsed(), heap.getCommitted(), cpu, cpuPercent, gc[0], gc[1],
                HeadlessTuning.effectCount, queue[0], queue[1], queue[2], queue[3], queue[4],
                HeadlessTuning.effectiveTickMillis, HeadlessTuning.SKIP_PERIODIC_GC,
                HeadlessTuning.SKIP_AUTO_POPUP, HeadlessTuning.SKIP_DECORATIONS, HeadlessTuning.EVENT_SENDER);
        if (!attempt.isEmpty() && rss[0] >= 0) {
            ++sampleCount; rssSum += rss[0]; rssPeak = Math.max(rssPeak, rss[0]);
        }
    }

    public synchronized void finish(String outcome, String reason) {
        if (attempt.isEmpty()) return;
        sample();
        long endNs = System.nanoTime(), endCpu = cpu();
        long[] gc = gc();
        setPhase("idle", endNs);
        row(results, runId, profile, pass, attempt, account, character, level, classId, outcome, reason,
                dailyFinished, didWork, tasks, reconnects, (endNs - startNs) / 1000000L,
                (endCpu - startCpu) / 1000000L, sampleCount == 0 ? -1 : rssSum / sampleCount,
                sampleCount == 0 ? -1 : rssPeak, gc[0] - startGcCount, gc[1] - startGcMs,
                phaseMillis("select"), phaseMillis("prepare"), phaseMillis("daily"), phaseMillis("flip"),
                phaseMillis("cave"), phaseMillis("reconnect"), errors);
        event("finished", outcome, 0);
        attempt = "";
    }

    private synchronized void shutdown() {
        finish("interrupted", "JVM shutdown");
        samples.close(); events.close(); results.close();
    }
}
