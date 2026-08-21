
public final class Npc extends Char {

    public NpcTemplate template;
    public static NpcTemplate[] arrNpcTemplate;

    public Npc(int var1, int var2, int var3, int var4, int var5) {
        super.cx = var3;
        super.cy = var4;
        super.statusMe = var2;
        this.template = arrNpcTemplate[var5];
    }

    public static void npcBE() {
        for (int var0 = 0; var0 < GameScr.vNpc.size(); ++var0) {
            Npc var1;
            (var1 = (Npc) GameScr.vNpc.elementAt(var0)).effTask = null;
            var1.indexEffTask = -1;
        }

    }

    public final void pxw() {
        if (super.effTask == null) {
            label45:
            {
                label51:
                {
                    byte[] var1 = new byte[]{-1, 9, 9, 10, 10, 11, 11};
                    if (Char.getMyChar().ctaskId >= 9 && Char.getMyChar().ctaskId <= 10 && Char.getMyChar().nClass.classId > 0 && var1[Char.getMyChar().nClass.classId] == this.template.npcTemplateId) {
                        if (Char.getMyChar().taskMaint != null) {
                            if (Char.getMyChar().taskMaint == null || Char.getMyChar().taskMaint.index + 1 != Char.getMyChar().taskMaint.subNames.length) {
                                break label45;
                            }

                            super.effTask = GameScr.efs[62];
                            break label51;
                        }
                    } else {
                        GameScr.gI();
                        byte var2 = GameScr.gameBF();
                        if (Char.getMyChar().taskMaint != null || var2 != this.template.npcTemplateId) {
                            if (Char.getMyChar().taskMaint == null || var2 != this.template.npcTemplateId) {
                                break label45;
                            }

                            if (Char.getMyChar().taskMaint.index + 1 == Char.getMyChar().taskMaint.subNames.length) {
                                super.effTask = GameScr.efs[62];
                                break label51;
                            }
                        }
                    }

                    super.effTask = GameScr.efs[57];
                }

                super.indexEffTask = 0;
            }
        }

        super.pxw();
    }

    public final void gameAA(mGraphics var1) {
        if (this.gameAQ()) {
            if (super.statusMe != 15) {
                if (super.cTypePk != 0) {
                    super.gameAA(var1);
                } else if (this.template != null) {
                    if (this.template.npcTemplateId == 13) {
                        if (Char.getMyChar().npcFocus != null && Char.getMyChar().npcFocus.equals(this)) {
                            SmallImage.gameAA(var1, 988, super.cx, super.cy - super.ch - 1, 0, 33);
                        }

                        SmallImage.gameAA(var1, 1060, super.cx, super.cy, 0, 33);
                        mFont.tahoma_7_white.gameAA(var1, String.valueOf(TileMap.zoneID), super.cx, super.cy - 10 - 2 - mFont.tahoma_7.gameAC(), 2);
                    } else if (this.template.npcTemplateId == 31) {
                        if (Char.getMyChar().npcFocus != null && Char.getMyChar().npcFocus.equals(this)) {
                            SmallImage.gameAA(var1, 988, super.cx, super.cy - super.ch - 1, 0, 33);
                        }

                        SmallImage.gameAA(var1, 1291, super.cx, super.cy, 0, 33);
                    } else if (this.template.npcTemplateId == 27) {
                        if (Char.getMyChar().npcFocus != null && Char.getMyChar().npcFocus.equals(this)) {
                            SmallImage.gameAA(var1, 988, super.cx, super.cy - super.ch - 1, 0, 33);
                        }

                        SmallImage.gameAA(var1, 1224, super.cx, super.cy, 0, 33);
                    } else {
                        Part var2 = GameScr.parts[this.template.headId];
                        Part var3 = GameScr.parts[this.template.legId];
                        Part var4 = GameScr.parts[this.template.bodyId];
                        if (super.cdir == 1) {
                            SmallImage.gameAA(var1, var2.pi[Char.CharInfo[super.cf][0][0]].id, super.cx + Char.CharInfo[super.cf][0][1] + var2.pi[Char.CharInfo[super.cf][0][0]].dx, super.cy - Char.CharInfo[super.cf][0][2] + var2.pi[Char.CharInfo[super.cf][0][0]].dy, 0, 0);
                            SmallImage.gameAA(var1, var3.pi[Char.CharInfo[super.cf][1][0]].id, super.cx + Char.CharInfo[super.cf][1][1] + var3.pi[Char.CharInfo[super.cf][1][0]].dx, super.cy - Char.CharInfo[super.cf][1][2] + var3.pi[Char.CharInfo[super.cf][1][0]].dy, 0, 0);
                            SmallImage.gameAA(var1, var4.pi[Char.CharInfo[super.cf][2][0]].id, super.cx + Char.CharInfo[super.cf][2][1] + var4.pi[Char.CharInfo[super.cf][2][0]].dx, super.cy - Char.CharInfo[super.cf][2][2] + var4.pi[Char.CharInfo[super.cf][2][0]].dy, 0, 0);
                        } else {
                            SmallImage.gameAA(var1, var2.pi[Char.CharInfo[super.cf][0][0]].id, super.cx - Char.CharInfo[super.cf][0][1] - var2.pi[Char.CharInfo[super.cf][0][0]].dx, super.cy - Char.CharInfo[super.cf][0][2] + var2.pi[Char.CharInfo[super.cf][0][0]].dy, 2, 24);
                            SmallImage.gameAA(var1, var3.pi[Char.CharInfo[super.cf][1][0]].id, super.cx - Char.CharInfo[super.cf][1][1] - var3.pi[Char.CharInfo[super.cf][1][0]].dx, super.cy - Char.CharInfo[super.cf][1][2] + var3.pi[Char.CharInfo[super.cf][1][0]].dy, 2, 24);
                            SmallImage.gameAA(var1, var4.pi[Char.CharInfo[super.cf][2][0]].id, super.cx - Char.CharInfo[super.cf][2][1] - var4.pi[Char.CharInfo[super.cf][2][0]].dx, super.cy - Char.CharInfo[super.cf][2][2] + var4.pi[Char.CharInfo[super.cf][2][0]].dy, 2, 24);
                        }

                        if (super.indexEffTask >= 0 && super.effTask != null && super.cTypePk == 0) {
                            SmallImage.gameAA(var1, super.effTask.arrEfInfo[super.indexEffTask].idImg, super.cx + super.effTask.arrEfInfo[super.indexEffTask].dx, super.cy + super.effTask.arrEfInfo[super.indexEffTask].dy, 0, 3);
                            if (GameCanvas.gameTick % 2 == 0) {
                                ++super.indexEffTask;
                                if (super.indexEffTask >= super.effTask.arrEfInfo.length) {
                                    super.indexEffTask = 0;
                                }
                            }
                        }

                        if (Char.getMyChar().npcFocus != null && Char.getMyChar().npcFocus.equals(this)) {
                            mFont.tahoma_7_yellow.gameAA(var1, this.template.name + " [" + this.template.npcTemplateId + "]", super.cx, super.cy - super.ch - mFont.tahoma_7.gameAC() - 7, 2, mFont.tahoma_7_grey);
                            SmallImage.gameAA(var1, 988, super.cx, super.cy - super.ch - 2, 0, 33);
                        } else {
                            mFont.tahoma_7_yellow.gameAA(var1, this.template.name + " [" + this.template.npcTemplateId + "]", super.cx, super.cy - super.ch - 3 - mFont.tahoma_7.gameAC(), 2, mFont.tahoma_7_grey);
                        }
                    }
                }
            }
        }
    }
}
