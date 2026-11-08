/**
 * Nhiệm vụ Bà Rei (Làng Chài) — mốc level 26.
 *
 *  bước 0: đạt level 26        -> đánh quái map 50
 *  bước 1: nhặt Hầu tửu        -> đánh quái id 26 ở map 50
 *  bước 2: nhặt Xác châu chấu  -> đánh quái id 27 ở map 11
 *  bước cuối: As35 tự về Bà Rei trả nhiệm vụ.
 */
final class TaskBaRei26 extends Uplevel35Task {
    static final int TASK_ID = Integer.getInteger("nso.uplevel35.task.barei26", 18).intValue();
    static final int LEVEL_REQUIRED = 26;
    static final int LEVEL_MAP = 50;
    static final int HAU_TUU_MAP = 50;
    static final int HAU_TUU_MOB = 26;
    static final String HAU_TUU_NAME = "hầu tửu";
    static final int CHAU_CHAU_MAP = 11;
    static final int CHAU_CHAU_MOB = 27;
    static final String CHAU_CHAU_NAME = "châu chấu";

    int taskId() {
        return TASK_ID;
    }

    String label() {
        return "BaRei26";
    }

    void tick(As35 bot, Char me, Task task) {
        switch (task.index) {
            case 0:
                bot.levelUp(LEVEL_REQUIRED, LEVEL_MAP, -1);
                return;
            case 1:
                bot.grind(HAU_TUU_MAP, HAU_TUU_MOB, As35.itemIdByName(HAU_TUU_NAME));
                return;
            case 2:
                bot.grind(CHAU_CHAU_MAP, CHAU_CHAU_MOB, As35.itemIdByName(CHAU_CHAU_NAME));
                return;
            default:
                Uplevel35.logEvery("barei26-step", 10000L, "BaRei26 bước " + task.index
                        + " chưa có logic: " + As35.describeTask(me));
        }
    }
}
