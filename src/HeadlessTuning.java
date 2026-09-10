/** Opt-in NVHN tuning. Uses only APIs available to the original client. */
public final class HeadlessTuning {
    public static final boolean ENABLED = "true".equals(System.getProperty("nso.nvhn.headless"));
    public static final int TICK_MS = integer("nso.tick.ms", 50, 0, 1000);
    public static final boolean SKIP_PERIODIC_GC = flag("nso.skip.periodic.gc");
    public static final boolean SKIP_AUTO_POPUP = flag("nso.skip.auto.popup");
    public static final boolean SKIP_DECORATIONS = flag("nso.skip.decorations");
    public static final boolean EVENT_SENDER = flag("nso.event.sender");
    public static volatile int effectCount;
    public static volatile int effectiveTickMillis;

    private HeadlessTuning() { }

    private static boolean flag(String name) {
        return ENABLED && !"false".equals(System.getProperty(name));
    }

    private static int integer(String name, int fallback, int min, int max) {
        if (!ENABLED) return 0;
        String value = System.getProperty(name);
        if (value == null) return fallback;
        int parsed = Integer.parseInt(value);
        if (parsed < min || parsed > max) throw new IllegalArgumentException(name + " out of range");
        return parsed;
    }

    public static int tickMillis(int rmsValue) {
        return TICK_MS > 0 ? TICK_MS : rmsValue;
    }
}
