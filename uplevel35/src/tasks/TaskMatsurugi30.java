/**
 * Nhiệm vụ Matsurugi (làng Oshin, npc 23 map 48) — mốc level 30.
 *
 *  bước 0: đạt level 30 -> đánh quái map 34
 *  bước 1: đánh Quỷ băng (mob 30) ở map 51
 *  bước 2: đánh Quỷ hoa (mob 31) ở map 34
 *  bước cuối: As35 tự về Matsurugi trả nhiệm vụ.
 */
final class TaskMatsurugi30 extends Uplevel35Task {
    static final int TASK_ID = Integer.getInteger("nso.uplevel35.task.matsurugi30", 21).intValue();
    static final int LEVEL_REQUIRED = 30;
    static final int LEVEL_MAP = 34;
    static final int ICE_MAP = 51;
    static final int ICE_MOB = 30;
    static final int FLOWER_MAP = 34;
    static final int FLOWER_MOB = 31;

    int taskId() {
        return TASK_ID;
    }

    String label() {
        return "Matsurugi30";
    }

    void reset() {
    }

    void tick(As35 bot, Char me, Task task) {
        switch (task.index) {
            case 0:
                bot.levelUp(LEVEL_REQUIRED, LEVEL_MAP, -1);
                return;
            case 1:
                bot.grind(ICE_MAP, ICE_MOB, -1);
                return;
            case 2:
                bot.grind(FLOWER_MAP, FLOWER_MOB, -1);
                return;
            default:
                Uplevel35.logEvery("matsurugi30-step", 10000L, "Matsurugi30 bước " + task.index
                        + " chưa có logic: " + As35.describeTask(me));
        }
    }
}
