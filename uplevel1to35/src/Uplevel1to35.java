/**
 * Điều phối runtime uplevel1to35: một nhân vật chạy nối tiếp
 *   giai đoạn 1 (As20, uplevel/)   nhiệm vụ chính tuyến 0 -> 17 (lv 1 -> ~25)
 *   giai đoạn 2 (As35, uplevel35/) nhiệm vụ chính tuyến 18 -> 23 (lv 25 -> 35)
 *
 * Giai đoạn chọn theo ctaskId (không theo level) nên đăng nhập lại / đổi JVM luôn vào đúng chỗ.
 * AccountAutoManager (transform của uplevel1to35) gọi phase() trong onGameReady và onAs20Finished.
 */
public final class Uplevel1to35 {
    public static final int PHASE_DONE = 0;
    public static final int PHASE_AS20 = 1;
    public static final int PHASE_AS35 = 2;

    /** ctaskId đầu tiên của giai đoạn 2 (As20 coi ctaskId >= 18 là xong). */
    public static final int AS35_FIRST_TASK = Integer.getInteger("nso.uplevel1to35.as35.first", 18).intValue();
    /** ctaskId cuối cùng As35 có logic; vượt quá thì nhân vật coi như đã xong. */
    public static final int AS35_LAST_TASK = Integer.getInteger("nso.uplevel1to35.as35.last", 23).intValue();

    private Uplevel1to35() {
    }

    public static void log(String message) {
        System.out.println("UPLEVEL1TO35 " + message);
    }

    /**
     * TEST đường chuyển As20 -> As35 trên nhân vật đã ở ctaskId >= 18: -Dnso.uplevel1to35.test.as20first=true
     * ép lần vào game đầu tiên chạy As20; As20 thấy xong ngay -> onAs20Finished -> đổi JVM -> As35.
     * File đánh dấu trong user.home (home của worker) để chỉ ép đúng một lần.
     */
    private static final boolean TEST_AS20_FIRST = Boolean.getBoolean("nso.uplevel1to35.test.as20first");
    private static final String TEST_MARKER = "uplevel1to35-test-as20first.done";

    private static java.io.File testMarker() {
        return new java.io.File(System.getProperty("user.home", "."), TEST_MARKER);
    }

    /** Giai đoạn sau khi As20 báo xong (gọi từ onAs20Finished): kết thúc chế độ test nếu đang bật. */
    public static int phaseAfterAs20(Char me) {
        if (TEST_AS20_FIRST && !testMarker().exists()) {
            try {
                testMarker().createNewFile();
            } catch (java.io.IOException ex) {
                log("TEST không tạo được " + testMarker() + ": " + ex);
            }
            log("TEST as20first: As20 đã báo xong, chuyển sang giai đoạn thật");
        }
        return phase(me);
    }

    public static int phase(Char me) {
        if (me == null) {
            return PHASE_DONE;
        }
        if (TEST_AS20_FIRST && !testMarker().exists()) {
            log("TEST as20first: ép As20 cho " + me.cName + " ctaskId=" + me.ctaskId);
            return PHASE_AS20;
        }
        if (me.ctaskId < AS35_FIRST_TASK) {
            return PHASE_AS20;
        }
        if (me.ctaskId > AS35_LAST_TASK) {
            return PHASE_DONE;
        }
        // Đủ level đích thì thôi, trừ nhiệm vụ mốc 35 (TaskMatsurugi35) chưa trả.
        if (me.clevel < Uplevel35.TARGET_LEVEL || Uplevel35.hasTaskWork(me)) {
            return PHASE_AS35;
        }
        return PHASE_DONE;
    }

    public static String phaseName(int phase) {
        return phase == PHASE_AS20 ? "as20" : phase == PHASE_AS35 ? "as35" : "done";
    }

    public static String describe(Char me) {
        return me == null ? "-" : me.cName + " level=" + me.clevel + " ctaskId=" + me.ctaskId
                + " phase=" + phaseName(phase(me));
    }
}
