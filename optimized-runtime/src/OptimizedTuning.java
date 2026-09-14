/**
 * Tùy chỉnh hiệu năng tối đa cho Ultra-Optimized Standalone Runtime.
 * Đọc cấu hình từ System Properties (hoặc được truyền từ launcher script qua -D).
 */
public final class OptimizedTuning {
    // Luôn bật khi chạy trên runtime này
    public static final boolean ENABLED = true;

    // Chu kỳ tick mặc định: 80ms (12.5 FPS) - tối ưu ăn khớp với luồng Auto (100ms)
    public static final int TICK_MS = integer("nso.tick.ms", 80, 20, 1000);

    // Chặn triệt để paint, repaint, serviceRepaints
    public static final boolean SKIP_PAINT = flag("nso.skip.paint", true);

    // Bỏ qua định kỳ gọi System.gc() của game client
    public static final boolean SKIP_PERIODIC_GC = flag("nso.skip.periodic.gc", true);

    // Bỏ qua các dialog popup phiền toái
    public static final boolean SKIP_AUTO_POPUP = flag("nso.skip.auto.popup", true);

    // Bỏ qua toàn bộ hoạt ảnh đồ họa (mây, đèn lồng, quái bay, camera easing)
    public static final boolean SKIP_DECORATIONS = flag("nso.skip.decorations", true);

    // Sử dụng cơ chế nạp bản đồ khi cần (Lazy Loading) thay vì nạp sẵn 160 maps
    public static final boolean LAZY_MAP = flag("nso.lazy.map", true);

    public static volatile int effectiveTickMillis = 80;

    private OptimizedTuning() { }

    private static boolean flag(String name, boolean defaultValue) {
        String value = System.getProperty(name);
        if (value == null) return defaultValue;
        return !"false".equalsIgnoreCase(value) && !"0".equals(value);
    }

    private static int integer(String name, int fallback, int min, int max) {
        String value = System.getProperty(name);
        if (value == null) return fallback;
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < min || parsed > max) return fallback;
            return parsed;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static int tickMillis(int rmsValue) {
        return TICK_MS > 0 ? TICK_MS : rmsValue;
    }
}
