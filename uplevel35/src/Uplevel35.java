/**
 * Cấu hình và log dùng chung cho runtime uplevel35 (level 25 -> 35).
 *
 * Mọi giá trị có thể ghi đè bằng -D khi chạy java (xem scripts/start-workers.sh).
 */
public final class Uplevel35 {
    /** Nhân vật level < MIN_LEVEL hoặc >= TARGET_LEVEL bị bỏ qua. */
    public static final int MIN_LEVEL = Integer.getInteger("nso.uplevel35.min", 25).intValue();
    public static final int TARGET_LEVEL = Integer.getInteger("nso.uplevel35.target", 35).intValue();

    /** Chế độ test đường đi: >= 0 thì chỉ đi tới map này rồi dừng (không làm nhiệm vụ). */
    public static final int TEST_MAP = Integer.getInteger("nso.uplevel35.testmap", -1).intValue();

    /** Bật nhật ký chi tiết (task, item, npc) để dò lỗi. */
    public static final boolean DEBUG = Boolean.getBoolean("nso.uplevel35.debug");

    private static final java.util.Hashtable lastLogAt = new java.util.Hashtable();

    private Uplevel35() {
    }

    public static void log(String message) {
        System.out.println("UPLEVEL35 " + message);
    }

    /** Log giới hạn tần suất theo key, tránh spam khi vòng lặp chạy mỗi 100ms. */
    public static void logEvery(String key, long intervalMs, String message) {
        long now = System.currentTimeMillis();
        Long last = (Long) lastLogAt.get(key);
        if (last != null && now - last.longValue() < intervalMs) {
            return;
        }
        lastLogAt.put(key, new Long(now));
        log(message);
    }

    public static void debug(String message) {
        if (DEBUG) {
            log("DEBUG " + message);
        }
    }

    /** Gọi từ Controller (patch 002) khi NPC trả lời. */
    public static void onNpcMessage(int npcTemplateId, String text) {
        if (!(Code.fieldAB instanceof As35)) {
            return;
        }
        String safe = text == null ? "" : text.replace('\n', ' ').replace('\r', ' ');
        log("NPC " + npcTemplateId + ": [" + safe + "]");
        ((As35) Code.fieldAB).onNpcMessage(npcTemplateId, safe);
    }

    public static boolean inLevelRange(int level) {
        return level >= MIN_LEVEL && level < TARGET_LEVEL;
    }

    /** Nhiệm vụ cuối (TaskMatsurugi35) chưa xong: vẫn chạy dù đã đủ TARGET_LEVEL. */
    public static boolean hasTaskWork(Char me) {
        return me.ctaskId == TaskMatsurugi35.TASK_ID;
    }

    /** Có chạy nhân vật này không (gọi từ AccountAutoManager, patch 001). */
    public static boolean shouldRun(Char me) {
        return inLevelRange(me.clevel) || (me.clevel >= TARGET_LEVEL && hasTaskWork(me));
    }
}
