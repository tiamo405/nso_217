import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Trạng thái worker cho web monitor (uplevel_monitor/).
 *
 * Một thread daemon mỗi 5 giây ghi {@code ${user.home}/status.properties} (ghi file tạm rồi đổi tên,
 * nên monitor không đọc phải file dở) và mỗi 60 giây (hoặc khi lên cấp / đổi nhiệm vụ) in một dòng
 * {@code <PREFIX> STATUS ts=... } vào stdout để log luôn có mốc thời gian.
 *
 * Dùng chung cho uplevel (overrides/) và uplevel35 (build.sh copy file này). Chỉ đọc biến static của
 * client, không khóa AccountAutoManager, nên không ảnh hưởng luồng game.
 */
public final class UplevelStatus {
    private static final long WRITE_INTERVAL_MS = 5000L;
    private static final long LOG_INTERVAL_MS = 60000L;

    private static Thread thread;
    private static String prefix = "UPLEVEL";
    private static String runtime = "uplevel";

    private static long lastExp = Long.MIN_VALUE;
    private static String lastProgressKey = "";
    private static long progressAt;
    private static String lastLogKey = "";
    private static long lastLogAt;

    private UplevelStatus() {
    }

    /** Gọi nhiều lần cũng chỉ chạy một thread. */
    public static synchronized void start(String logPrefix, String runtimeName) {
        prefix = logPrefix;
        runtime = runtimeName;
        if (thread != null) {
            return;
        }
        progressAt = System.currentTimeMillis();
        thread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        tick();
                    } catch (Throwable ex) {
                        System.out.println(prefix + " STATUS error=" + ex);
                    }
                    try {
                        Thread.sleep(WRITE_INTERVAL_MS);
                    } catch (InterruptedException ex) {
                        return;
                    }
                }
            }
        }, "uplevel-status");
        thread.setDaemon(true);
        thread.start();
    }

    private static void tick() throws Exception {
        long now = System.currentTimeMillis();
        Char me = Char.getMyChar();
        boolean inGame = me != null && me.cName != null && GameCanvas.currentScreen == GameScr.instance
                && GameScr.instance != null;

        String account = SelectServerScr.uname == null ? "-" : SelectServerScr.uname;
        String name = inGame ? me.cName : "-";
        int level = inGame ? me.clevel : -1;
        long exp = inGame ? me.gameBE : -1L;
        long need = inGame ? expNeed(me.clevel) : -1L;
        int bagTotal = -1;
        int bagFree = -1;
        if (inGame && me.arrItemBag != null) {
            Item[] bag = me.arrItemBag;
            bagTotal = bag.length;
            bagFree = 0;
            for (int i = 0; i < bag.length; i++) {
                if (bag[i] == null) {
                    bagFree++;
                }
            }
        }
        String task = "-";
        if (inGame) {
            Task t = me.taskMaint;
            boolean hasCount = t != null && t.counts != null && t.index >= 0 && t.index < t.counts.length
                    && t.counts[t.index] > 0;
            task = me.ctaskId + (t == null ? "" : ":" + t.index) + (hasCount ? " " + t.count + "/" + t.counts[t.index] : "");
        }

        // progressAt = lần cuối exp/level/nhiệm vụ/map đổi -> monitor phát hiện bot "sống nhưng đứng yên".
        String progressKey = name + "|" + level + "|" + task + "|" + (inGame ? TileMap.mapID : -1);
        if (!progressKey.equals(lastProgressKey) || exp != lastExp) {
            lastProgressKey = progressKey;
            lastExp = exp;
            progressAt = now;
        }

        String ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(now));
        StringBuilder p = new StringBuilder(512);
        prop(p, "runtime", runtime);
        prop(p, "worker", System.getProperty("nso.worker.name", "-"));
        prop(p, "pid", pid());
        prop(p, "ts", ts);
        prop(p, "updatedAt", String.valueOf(now));
        prop(p, "progressAt", String.valueOf(progressAt));
        prop(p, "phase", inGame ? "ingame" : "login");
        prop(p, "account", account);
        prop(p, "name", name);
        prop(p, "level", String.valueOf(level));
        prop(p, "exp", String.valueOf(exp));
        prop(p, "expNeed", String.valueOf(need));
        prop(p, "bagFree", String.valueOf(bagFree));
        prop(p, "bagTotal", String.valueOf(bagTotal));
        prop(p, "luong", String.valueOf(inGame ? me.luong : -1));
        prop(p, "xu", String.valueOf(inGame ? me.xu : -1));
        prop(p, "yen", String.valueOf(inGame ? me.yen : -1));
        prop(p, "hp", inGame ? me.cHP + "/" + me.cMaxHP : "-");
        prop(p, "map", inGame ? String.valueOf(TileMap.mapID) : "-");
        prop(p, "mapName", inGame && TileMap.mapName != null ? TileMap.mapName : "-");
        prop(p, "zone", inGame ? String.valueOf(TileMap.zoneID) : "-");
        prop(p, "task", task);
        writeAtomic(p.toString());

        String logKey = name + "|" + level + "|" + (inGame ? String.valueOf(me.ctaskId) : "-");
        if (!logKey.equals(lastLogKey) || now - lastLogAt >= LOG_INTERVAL_MS) {
            lastLogKey = logKey;
            lastLogAt = now;
            System.out.println(prefix + " STATUS ts=" + ts + " acc=" + account + " name=" + name
                    + " level=" + level + " exp=" + exp + "/" + need
                    + " bag=" + bagFree + "/" + bagTotal
                    + " luong=" + (inGame ? me.luong : -1) + " xu=" + (inGame ? me.xu : -1)
                    + " yen=" + (inGame ? me.yen : -1)
                    + " map=" + (inGame ? TileMap.mapID : -1) + " task=" + task.replace(' ', '/'));
        }
    }

    private static long expNeed(int level) {
        return GameScr.exps != null && level >= 0 && level < GameScr.exps.length ? GameScr.exps[level] : -1L;
    }

    private static String pid;

    private static String pid() {
        if (pid == null) {
            // Java 8: RuntimeMXBean name dạng "pid@host".
            String n = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
            int at = n.indexOf('@');
            pid = at > 0 ? n.substring(0, at) : "-";
        }
        return pid;
    }

    /** key=value, một dòng; thay xuống dòng trong value để file luôn parse được. */
    private static void prop(StringBuilder out, String key, String value) {
        String v = value == null ? "" : value.replace('\n', ' ').replace('\r', ' ');
        out.append(key).append('=').append(v).append('\n');
    }

    private static void writeAtomic(String content) throws Exception {
        File dir = new File(System.getProperty("user.home", "."));
        File target = new File(dir, "status.properties");
        File tmp = new File(dir, "status.properties.tmp");
        Writer w = new OutputStreamWriter(new FileOutputStream(tmp), "UTF-8");
        try {
            w.write(content);
        } finally {
            w.close();
        }
        if (!tmp.renameTo(target)) {
            target.delete();
            tmp.renameTo(target);
        }
    }
}
