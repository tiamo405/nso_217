/** Optional observer installed by HeadlessMain; no desktop APIs in shared src/. */
public final class BotMetrics {
    public interface Sink {
        void begin(String account, String character);
        void event(String kind, String detail, int value);
        void finish(String outcome, String reason);
    }

    public static volatile Sink sink;
    private static boolean pendingReturn;

    private BotMetrics() { }

    public static void begin(String account, String character) {
        try { if (sink != null) sink.begin(account, character); }
        catch (RuntimeException ex) { disable(ex); }
    }

    public static void event(String kind, String detail, int value) {
        if ("reconnect".equals(kind)) clearPendingReturn();
        try { if (sink != null) sink.event(kind, detail, value); }
        catch (RuntimeException ex) { disable(ex); }
    }

    public static void finish(String outcome, String reason) {
        clearPendingReturn();
        try { if (sink != null) sink.finish(outcome, reason); }
        catch (RuntimeException ex) { disable(ex); }
    }

    private static synchronized void clearPendingReturn() { pendingReturn = false; }

    private static void disable(RuntimeException ex) {
        sink = null;
        System.err.println("HEADLESS METRICS observer disabled: " + ex);
    }

    public static synchronized void returningTask() {
        pendingReturn = true;
    }

    public static synchronized void taskRemoved(TaskOrder task) {
        if (task.taskId == 0 && pendingReturn && task.count >= task.maxCount) {
            pendingReturn = false;
            event("task_return_confirmed", "", 1);
        }
    }
}
