public final class OptimizedMain {
    private OptimizedMain() {
    }

    public static void main(String[] args) throws Exception {
        if (System.getProperty("microedition.platform") == null) {
            System.setProperty("microedition.platform", "NSOUltraOptimized");
        }

        System.out.println("==================================================");
        System.out.println("  NSO ULTRA-OPTIMIZED STANDALONE RUNTIME STARTED");
        System.out.println("  Tick: " + OptimizedTuning.TICK_MS + "ms | SkipPaint: " + OptimizedTuning.SKIP_PAINT
                + " | SkipDeco: " + OptimizedTuning.SKIP_DECORATIONS + " | LazyMap: " + OptimizedTuning.LAZY_MAP);
        System.out.println("==================================================");

        GameMidlet midlet = new GameMidlet();
        midlet.startApp();

        while (true) {
            try {
                Thread.sleep(60000L);
            } catch (InterruptedException ignored) {
                break;
            }
        }
    }
}
