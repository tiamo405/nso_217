public final class HeadlessMain {
    private HeadlessMain() {
    }

    public static void main(String[] args) throws Exception {
        if (System.getProperty("microedition.platform") == null) {
            System.setProperty("microedition.platform", "NSOHeadless");
        }

        // Validate tuning before starting any client threads or connections.
        if (HeadlessTuning.ENABLED) {
            System.out.println("HEADLESS TUNING: tick_ms=" + HeadlessTuning.TICK_MS
                    + " skip_gc=" + HeadlessTuning.SKIP_PERIODIC_GC
                    + " skip_popup=" + HeadlessTuning.SKIP_AUTO_POPUP
                    + " skip_decorations=" + HeadlessTuning.SKIP_DECORATIONS
                    + " event_sender=" + HeadlessTuning.EVENT_SENDER);
        }
        HeadlessMetrics.install();
        GameMidlet midlet = new GameMidlet();
        midlet.startApp();

        while (true) {
            Thread.sleep(60000L);
        }
    }
}
