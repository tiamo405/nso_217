
public final class Skill {

    public SkillTemplate template;
    public short skillId;
    public int point;
    public int level;
    public int coolDown;
    public long lastTimeUseThisSkill;
    public int dx;
    public int dy;
    public int maxFight;
    public int manaUse;
    public SkillOption[] options;
    public boolean paintCanNotUseSkill = false;

    public final void gameAA(int var1, int var2, mGraphics var3) {
        SmallImage.gameAA(var3, this.template.iconId, var1, var2, 0, StaticObj.VCENTER_HCENTER);
        long var6;
        if ((var6 = System.currentTimeMillis() - this.lastTimeUseThisSkill) < (long) this.coolDown) {
            var3.gameAA(3355443);
            if (this.paintCanNotUseSkill && GameCanvas.gameTick % 6 > 2) {
                var3.gameAA(4473924);
            }

            int var8 = (int) (var6 * 18L / (long) this.coolDown);
            var3.gameAC(var1 - 9, var2 - 9 + var8, 18, 18 - var8);
        } else {
            this.paintCanNotUseSkill = false;
        }
    }

    public final boolean gameAA() {
        return System.currentTimeMillis() - this.lastTimeUseThisSkill < (long) this.coolDown;
    }

    public final int fieldAB() {
        return Code.fieldBI ? Code.fieldBJ : this.dx;
    }

    public final int fieldAC() {
        return Code.fieldBK ? Code.fieldBL : this.dy;
    }

    public final int fieldAD() {
        return Code.fieldBM ? Code.fieldBN : this.maxFight;
    }
}
