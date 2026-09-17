import java.io.*;
import java.util.concurrent.*;

/** Offline integration checks; no game login or external sockets. */
public final class HeadlessRuntimeTest {
    private static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }

    private static void queueLifecycle() throws Exception {
        final OutboundQueue queue = new OutboundQueue();
        final long first = queue.open();
        final Message one = new Message((byte) 1);
        queue.add(one);
        final CountDownLatch taken = new CountDownLatch(1);
        Thread waiting = new Thread(new Runnable() {
            public void run() {
                try {
                    check(queue.take(first).message == one, "FIFO first packet");
                    taken.countDown();
                } catch (InterruptedException ex) { throw new AssertionError(ex); }
            }
        });
        waiting.start();
        check(!taken.await(50, TimeUnit.MILLISECONDS), "must wait for key");
        queue.ready();
        check(taken.await(2, TimeUnit.SECONDS), "key readiness wakes sender");
        waiting.join(2000);
        queue.add(new Message((byte) 2));
        long next = queue.open();
        check(!queue.current(first) && queue.take(first) == null, "old connection invalidated");
        check(queue.stats()[0] == 0 && queue.stats()[3] == 1, "stale packet discarded");
        queue.ready();
        final CountDownLatch closed = new CountDownLatch(1);
        final long token = next;
        Thread idle = new Thread(new Runnable() {
            public void run() {
                try { check(queue.take(token) == null, "close returns no packet"); closed.countDown(); }
                catch (InterruptedException ex) { throw new AssertionError(ex); }
            }
        });
        idle.start();
        // Check that an idle queue does not poll repeatedly.
        long deadline = System.currentTimeMillis() + 2000;
        while (idle.getState() != Thread.State.WAITING && System.currentTimeMillis() < deadline) Thread.yield();
        check(idle.getState() == Thread.State.WAITING, "idle sender blocks");
        long waits = queue.stats()[2];
        Thread.sleep(50);
        check(queue.stats()[2] == waits, "idle queue does not poll");
        queue.close();
        check(closed.await(2, TimeUnit.SECONDS), "close wakes idle sender");
        idle.join(2000);
        queue.add(new Message((byte) 3));
        check(queue.stats()[0] == 0, "closed queue rejects new packets");
    }

    private static void wireOrder() throws Exception {
        final Session_ME session = new Session_ME();
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final CountDownLatch flushes = new CountDownLatch(2);
        session.dos = new DataOutputStream(new FilterOutputStream(bytes) {
            public void flush() throws IOException { super.flush(); flushes.countDown(); }
        });
        session.connected = true;
        session.startSender();
        Message a = new Message((byte) 7); a.writer().writeByte(42);
        Message b = new Message((byte) 8); b.writer().writeByte(43);
        session.sendMessage(a); session.sendMessage(b);
        Thread.sleep(30);
        check(bytes.size() == 0, "session gates packets on key");
        session.dis = new DataInputStream(new ByteArrayInputStream(new byte[0]));
        MessageCollector collector = new MessageCollector(session);
        Message handshake = new Message();
        java.lang.reflect.Field reader = Message.class.getDeclaredField("dis");
        reader.setAccessible(true);
        reader.set(handshake, new DataInputStream(new ByteArrayInputStream(new byte[]{3, 5, 12, 2})));
        java.lang.reflect.Method accept = MessageCollector.class.getDeclaredMethod("gameAA", Message.class);
        accept.setAccessible(true);
        accept.invoke(collector, handshake);
        check(java.util.Arrays.equals(session.key, new byte[]{5, 9, 11}), "chained key exchange decoding");
        check(flushes.await(2, TimeUnit.SECONDS), "packets sent after handshake");
        byte[] actual = bytes.toByteArray();
        byte[] expected = {7, 0, 1, 42, 8, 0, 1, 43};
        for (int i = 0; i < expected.length; i++) expected[i] ^= session.key[i % session.key.length];
        check(java.util.Arrays.equals(actual, expected), "wire framing and FIFO unchanged");
        session.gameAD();
        check(!session.connected && !session.getKeyComplete, "disconnect resets handshake");
    }

    private static void staleConnection() throws Exception {
        Session_ME session = new Session_ME();
        session.connected = true;
        session.dos = new DataOutputStream(new ByteArrayOutputStream());
        session.dis = new DataInputStream(new ByteArrayInputStream(new byte[0]));
        session.startSender();
        long old = session.connectionGeneration();
        MessageCollector oldCollector = new MessageCollector(session);
        session.closeSender();
        session.startSender();
        session.installKey(new byte[]{7}, session.connectionGeneration());
        session.installKey(new byte[]{99}, old);
        check(session.key[0] == 7, "old handshake cannot replace new key");
        check(!session.sendQueued(new Message((byte) 1), old), "old sender cannot write into new socket");
        oldCollector.run();
        check(session.connected && session.currentConnection(session.connectionGeneration()),
                "old receiver cannot disconnect new session");
        session.gameAD();
    }

    private static void metricsLifecycle() throws Exception {
        HeadlessMetrics.install();
        check(BotMetrics.sink != null, "metrics installed");
        BotMetrics.begin("test,account", "character\"one");
        BotMetrics.event("game_ready", "2", 40);
        BotMetrics.event("daily_started", "", 0);
        Thread.sleep(20);
        BotMetrics.returningTask();
        TaskOrder task = new TaskOrder((byte) 0, 3, 3, "", "", 1, 2);
        BotMetrics.taskRemoved(task);
        BotMetrics.taskRemoved(task); // duplicate ack must not double-count
        BotMetrics.event("reconnect", "", 1);
        BotMetrics.begin("test,account", "character\"one");
        BotMetrics.event("game_ready", "2", 40);
        BotMetrics.event("daily_started", "", 0);
        BotMetrics.event("daily_finished", "", 1);
        BotMetrics.event("cave_started", "", 0);
        BotMetrics.finish("completed", "offline fixture");
        BotMetrics.finish("skipped", "duplicate callback");
        BotMetrics.begin("test,account", "character_two");
        // The shutdown hook must mark this last attempt interrupted.
    }

    private static void dailyTaskMessageClassification() {
        String unavailable = "Hiện tại không có nhiệm vụ phù hợp với cấp độ và tiến trình của con. "
                + "Hãy hoàn thành nhiệm vụ chính tuyến để mở khóa thêm khu vực.";
        String acceptanceRequired = "Hãy nhận nhiệm vụ mỗi ngày từ ta rồi mới sử dụng tính năng này.";
        check(AutoNvhn.isDailyTaskUnavailableMessage(unavailable),
                "NPC25 unavailable daily-task message must be recognized");
        check(AutoNvhn.isTaskAcceptanceRequiredMessage(acceptanceRequired),
                "NPC25 task-acceptance message must be recognized");
        check(!AutoNvhn.isDailyTaskUnavailableMessage("Đây là lần nhận nhiệm vụ thứ 1 trong ngày hôm nay."),
                "normal NPC25 task message must not be treated as unavailable");
        check(!AutoNvhn.isTaskAcceptanceRequiredMessage("Đây là lần nhận nhiệm vụ thứ 1 trong ngày hôm nay."),
                "normal NPC25 task message must not require recovery");
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && "baseline".equals(args[0])) {
            check(HeadlessTuning.tickMillis(30) == 30, "baseline respects RMS");
            check(!HeadlessTuning.SKIP_PERIODIC_GC && !HeadlessTuning.SKIP_AUTO_POPUP
                    && !HeadlessTuning.SKIP_DECORATIONS && !HeadlessTuning.EVENT_SENDER,
                    "all optimization switches disabled");
            System.out.println("PASS: baseline configuration");
            return;
        }
        check(HeadlessTuning.tickMillis(30) == 50, "RMS cannot override explicit tick");
        queueLifecycle();
        wireOrder();
        staleConnection();
        metricsLifecycle();
        dailyTaskMessageClassification();
        System.out.println("PASS: queue lifecycle, handshake, wire FIFO, metrics lifecycle");
    }
}
