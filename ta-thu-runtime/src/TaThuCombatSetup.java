/** Restores volatile combat settings when resuming a saved Ta Thu task. */
public final class TaThuCombatSetup {
    private static final int COMBAT_SKILL_INDEX = 4;

    private TaThuCombatSetup() {
    }

    public static void configureForResume() {
        Char me = Char.getMyChar();
        Char.fieldFG = true;
        Char.fieldFH = true;

        int foodLevel = me.clevel / 10 * 10;
        if (foodLevel < 10) {
            foodLevel = 10;
        } else if (foodLevel > 50) {
            foodLevel = 50;
        }
        Char.aFoodValue = foodLevel;
        Char.isAFood = true;

        Skill selected = null;
        String label = "skill hiện tại";
        if (me.nClass != null && me.nClass.skillTemplates != null
                && me.nClass.skillTemplates.length > 0) {
            if (me.clevel >= 30 && me.nClass.skillTemplates.length > COMBAT_SKILL_INDEX) {
                selected = me.gameAA(me.nClass.skillTemplates[COMBAT_SKILL_INDEX]);
                label = "skill bảng index=4";
            }
            if (selected == null || selected.point <= 0) {
                selected = me.gameAA(me.nClass.skillTemplates[0]);
                label = "skill bảng index=0";
            }
        }
        if (selected != null && selected.point > 0) {
            me.myskill = selected;
            Auto.fieldAL = selected;
            Service.gI().selectSkill(selected.template.id);
            System.out.println("AUTO TA THU RESUME SETUP: " + label
                    + " skillId=" + selected.template.id + " point=" + selected.point
                    + " type=" + selected.template.type + " cooldown=" + selected.coolDown
                    + " range=" + selected.dx + "x" + selected.dy
                    + " maxFight=" + selected.maxFight + " mana=" + selected.manaUse
                    + " foodLevel=" + foodLevel);
        } else {
            System.out.println("AUTO TA THU RESUME SETUP: không tìm thấy skill đã học; foodLevel="
                    + foodLevel);
        }
    }
}
