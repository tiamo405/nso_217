
public final class PkBoss extends Auto {

    private int fieldAV;
    private int fieldAW;

    public PkBoss(int var1) {
        super.fieldAD();
        super.fieldAB = var1;
        super.fieldAC = -2;
        this.fieldAV = 0;
    }

    private static boolean fieldAA(ItemTemplate var0) {
        if (var0.type == 19) {
            return false;
        } else if (var0.type != 16 && var0.type != 17) {
            if (var0.type == 26) {
                return var0.level >= 6;
            } else if (var0.fieldAA()) {
                return var0.level >= 70;
            } else if (var0.fieldAB()) {
                return false;
            } else {
                if (var0.type == 27) {
                    if (var0.id == 38 || var0.description.startsWith("Vật phẩm sự kiện") || var0.description.startsWith("Vật phẩm Sự kiện")) {
                        return false;
                    }

                    if (var0.name.startsWith("Sách võ công")) {
                        if (var0.level == 100) {
                            return true;
                        }

                        return false;
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    private static void fieldAM() {
        Char var0 = Char.getMyChar();

        for (int var1 = 0; var1 < GameScr.vItemMap.size(); ++var1) {
            ItemMap var2;
            if (fieldAA((var2 = (ItemMap) GameScr.vItemMap.elementAt(var1)).template) && Res.gameAA(var0.cx, var0.cy, var2.xEnd, var2.yEnd) <= 100 && (Char.fieldBF() > 2 || var2.template.isUpToUp && Char.fieldAJ(var2.template.id))) {
                Service.gI().pickItem(var2.itemMapID);
                cuong.sleep(50L);
            }
        }

    }

    protected final boolean fieldAJ() {
        Char var1 = Char.getMyChar();

        for (int var2 = 0; var2 < GameScr.vItemMap.size(); ++var2) {
            ItemMap var3;
            if (fieldAA((var3 = (ItemMap) GameScr.vItemMap.elementAt(var2)).template) && Res.gameAA(var1.cx, var1.cy, var3.xEnd, var3.yEnd) <= 100 && (Char.fieldBF() > 2 || var3.template.isUpToUp && Char.fieldAJ(var3.template.id))) {
                return true;
            }
        }

        return false;
    }

    private void fieldAN() {
        Char var1 = Char.getMyChar();
        if (!Auto.fieldAL()) {
            Mob var2 = var1.mobFocus;
            Char var3 = var1.charFocus;
            if (Code.fieldBO && (var3 == null || Code.fieldAC(var3.cName) || !Auto.fieldAA(var1, var3))) {
                var3 = Auto.fieldAE(var1);
            }

            int var4;
            if (var3 == null && var2 == null || !var2.isBoss || System.currentTimeMillis() - super.fieldAR > 60000L) {
                MyVector var10 = GameScr.vMob;
                var4 = 0;

                Mob var10000;
                while (true) {
                    if (var4 >= var10.size()) {
                        var10000 = null;
                        break;
                    }

                    Mob var5;
                    if ((var5 = (Mob) var10.elementAt(var4)) != null && var5.isBoss && var5.templateId != 223 && var5.hp > 0 && var5.status != 0 && var5.status != 1) {
                        var10000 = var5;
                        break;
                    }

                    ++var4;
                }

                var2 = var10000;
                if (var10000 == null && (System.currentTimeMillis() - super.fieldAR > 60000L || System.currentTimeMillis() - super.fieldAR > 10000L && !this.fieldAJ())) {
                    Code.fieldAC();
                    return;
                }

                this.fieldAC(var2);
            }

            int var6;
            Char var8;
            if (GameScr.vParty.size() > 0 && var1.nClass.classId == 6 && var1.cHP > 0) {
                for (var4 = 0; var4 < var1.vSkillFight.size(); ++var4) {
                    Skill var12;
                    if ((var12 = (Skill) var1.vSkillFight.elementAt(var4)) != null && var12.template.type == 4) {
                        if (!var12.gameAA()) {
                            for (var6 = 0; var6 < GameScr.vParty.size(); ++var6) {
                                Party var7;
                                if ((var7 = (Party) GameScr.vParty.elementAt(var6)).charId != var1.charID && var7.c != null && var7.c.cHP <= 0) {
                                    var8 = var7.c;
                                    if (Math.abs(var1.cx - var8.cx) > 50 || Math.abs(var1.cy - var8.cy) > 50) {
                                        Char.fieldAC(var8.cx, var8.cy);
                                    }

                                    cuong.sleep(500L);
                                    Service.gI().buffLive(var7.charId);
                                    var12.lastTimeUseThisSkill = System.currentTimeMillis();
                                    var12.paintCanNotUseSkill = true;
                                    var1.gameAB(GameScr.sks[var12.template.id], 0);
                                    cuong.sleep(1000L);
                                    return;
                                }
                            }
                        }
                        break;
                    }
                }
            }

            if (Auto.fieldAL != null) {
                Skill var11;
                int var13;
                int var15;
                if ((var11 = Auto.fieldAL).gameAA() && Char.isABuff) {
                    label518:
                    {
                        var13 = 0;

                        Skill var14;
                        label445:
                        while (true) {
                            if (var13 >= var1.vSkillFight.size()) {
                                break label518;
                            }

                            if ((var14 = (Skill) var1.vSkillFight.elementAt(var13)) != null && System.currentTimeMillis() - var14.lastTimeUseThisSkill >= (long) var14.coolDown - 300L) {
                                if (var14.template.type == 2) {
                                    if ((var1.fieldAA == null && Char.fieldEG || var14.template.id < 67 || var14.template.id > 72) && (Char.fieldEH || var14.template.id != 31) && (var14.template.id != 15 || var1.cHP < var1.cMaxHP * Char.aHpValue / 100 && var1.isHuman) && (var14.template.id != 6 || var1.isHuman)) {
                                        var15 = (int) (System.currentTimeMillis() / 1000L);
                                        int var16 = 0;

                                        while (true) {
                                            if (var16 >= var1.vEff.size()) {
                                                break label445;
                                            }

                                            Effect var9;
                                            if ((var9 = (Effect) var1.vEff.elementAt(var16)) != null && (var9.template.iconId == var14.template.iconId || var14.template.id == 58 && var9.template.type == 7) && var9.timeLenght - (var15 - var9.timeStart) >= 2) {
                                                break;
                                            }

                                            ++var16;
                                        }
                                    }
                                } else if (var14.template.type == 3 && var2 != null && var2.levelBoss == 0 && var2.hp > var2.maxHp / 2) {
                                    if (var14.template.id != 4 || Char.fieldEI && var1.cHP < var1.cMaxHP * Char.aHpValue / 100) {
                                        break;
                                    }
                                } else if ((var14.template.id == 7 || var14.template.id == 16 || var14.template.id == 25 || var14.template.id == 34 || var14.template.id == 43) && var2 != null && (var2.levelBoss != 0 || var2.hp >= var2.maxHp / 2) && (var14.template.id != 7 && var14.template.id != 16 || !var2.isFire) && (var14.template.id != 25 && var14.template.id != 34 || var2.isIce) && (var14.template.id != 43 || var2.isWind)) {
                                    break;
                                }
                            }

                            ++var13;
                        }

                        var11 = var14;
                        cuong.sleep(500L);
                    }
                }

                Service.gI().selectSkill(var11.template.id);
                if (var11.template.type == 2) {
                    Service.gI().sendUseSkillMyBuff();
                } else {
                    Mob var17;
                    if (Code.fieldBO && var3 != null && !Auto.fieldAC(var3) && Auto.fieldAA(var1, var3)) {
                        var13 = var11.dx;
                        var6 = var11.dy;
                        Auto.fieldAP.removeAllElements();
                        Auto.fieldAQ.removeAllElements();
                        Auto.fieldAQ.addElement(var3);

                        for (var15 = 0; var15 < GameScr.vCharInMap.size() && Auto.fieldAP.size() + Auto.fieldAQ.size() < var11.maxFight; ++var15) {
                            if ((var8 = (Char) GameScr.vCharInMap.elementAt(var15)).cHP > 0 && var8.statusMe != 14 && var8.statusMe != 5 && var8.statusMe != 15 && !var8.equals(var3) && (var8.cTypePk == 3 || var1.cTypePk == 3 || var8.cTypePk == 1 && var1.cTypePk == 1 || var1.killCharId >= 0 && var1.killCharId == var8.charID || var1.testCharId >= 0 && var1.testCharId == var8.charID || var8.killCharId == var1.charID) && !Code.fieldAD(var8.cName) && var3.cx - var13 <= var8.cx && var8.cx <= var3.cx + var13 && var3.cy - var6 <= var8.cy && var8.cy <= var3.cy + var6) {
                                Auto.fieldAQ.addElement(var8);
                            }
                        }

                        for (var15 = 0; var15 < GameScr.vMob.size() && Auto.fieldAP.size() + Auto.fieldAQ.size() < var11.maxFight; ++var15) {
                            if ((var17 = (Mob) GameScr.vMob.elementAt(var15)).status != 0 && var17.status != 1 && var17.isBoss && var3.cx - var13 <= var17.x && var17.x <= var3.cx + var13 && var3.cy - var6 <= var17.y && var17.y <= var3.cy + var6) {
                                Auto.fieldAP.addElement(var17);
                            }
                        }

                        Service.gI().sendPlayerAttack((MyVector) Auto.fieldAP, (MyVector) Auto.fieldAQ, (int) 2);
                    } else {
                        if (var2 == null || var2 == null || !var2.isBoss || var2.status == 0 || var2.hp <= 0) {
                            return;
                        }

                        if ((var11.template.type == 1 || var11.template.type == 3) && (Res.abs(var1.cx - var2.xFirst) > var11.dx + 30 || Res.abs(var1.cy - var2.yFirst) > var11.dy + 30)) {
                            this.fieldAC(var2);
                        }

                        var13 = var11.dx;
                        var6 = var11.dy;
                        Auto.fieldAP.removeAllElements();
                        Auto.fieldAQ.removeAllElements();
                        Auto.fieldAP.addElement(var2);

                        for (var15 = 0; var15 < GameScr.vMob.size() && Auto.fieldAP.size() + Auto.fieldAQ.size() < var11.maxFight; ++var15) {
                            if ((var17 = (Mob) GameScr.vMob.elementAt(var15)).status != 0 && var17.status != 1 && !var17.equals(var2) && var17.isBoss && var2.xFirst - 100 <= var17.xFirst && var17.xFirst <= var2.xFirst + 100 && var2.yFirst - 50 <= var17.yFirst && var17.yFirst <= var2.yFirst + 50) {
                                Auto.fieldAP.addElement(var17);
                            }
                        }

                        for (var15 = 0; var15 < GameScr.vCharInMap.size() && Auto.fieldAP.size() + Auto.fieldAQ.size() < var11.maxFight; ++var15) {
                            if ((var8 = (Char) GameScr.vCharInMap.elementAt(var15)).cHP > 0 && var8.statusMe != 14 && var8.statusMe != 5 && var8.statusMe != 15 && (var8.cTypePk == 3 || var1.cTypePk == 3 || var8.cTypePk == 1 && var1.cTypePk == 1 || var1.killCharId >= 0 && var1.killCharId == var8.charID || var1.testCharId >= 0 && var1.testCharId == var8.charID || var8.killCharId == var1.charID) && !Code.fieldAD(var8.cName) && var2.x - var13 <= var8.cx && var8.cx <= var2.x + var13 && var2.y - var6 <= var8.cy && var8.cy <= var2.y + var6) {
                                Auto.fieldAQ.addElement(var8);
                            }
                        }

                        Service.gI().sendPlayerAttack((MyVector) Auto.fieldAP, (MyVector) Auto.fieldAQ, (int) 1);
                    }
                }

                if (System.currentTimeMillis() - var11.lastTimeUseThisSkill >= (long) var11.coolDown) {
                    var11.lastTimeUseThisSkill = System.currentTimeMillis();
                    var11.paintCanNotUseSkill = true;
                    if (!Code.fieldBF) {
                        var1.gameAB(GameScr.sks[var11.template.id], 0);
                    }
                }

                super.fieldAR = System.currentTimeMillis();
                if (var11.template.id == 15) {
                    cuong.sleep(2000L);
                }
            }

        }
    }

    public final void fieldAA() {
        if (Auto.fieldAF()) {
            boolean var10000;
            if (!Char.fieldEW) {
                var10000 = true;
            } else {
                label98:
                {
                    label137:
                    {
                        if (TileMap.mapID == super.fieldAB && TileMap.zoneID == super.fieldAC) {
                            int var5 = 0;

                            label91:
                            while (true) {
                                Party var7;
                                if (var5 >= GameScr.vParty.size()) {
                                    var5 = 0;

                                    while (true) {
                                        if (var5 >= GameScr.vParty.size()) {
                                            break label137;
                                        }

                                        if ((var7 = (Party) GameScr.vParty.elementAt(var5)).c != null) {
                                            if (var7.name.equals(Char.getMyChar().cName)) {
                                                break label137;
                                            }
                                            break label91;
                                        }

                                        ++var5;
                                    }
                                }

                                if ((var7 = (Party) GameScr.vParty.elementAt(var5)).c != null && var7.c.cHP > 0) {
                                    break;
                                }

                                ++var5;
                            }
                        }

                        var10000 = false;
                        break label98;
                    }

                    var10000 = true;
                }
            }

            if (!var10000) {
                Auto.fieldAA(true);
            }

        } else {
            Char var1 = Char.getMyChar();
            boolean var2 = Code.fieldAH == null || var1.cName.equals(Code.fieldAH);
            int var6;
            if (super.fieldAC == -2) {
                if (super.fieldAB != TileMap.mapID) {
                    this.fieldAA(super.fieldAB, -2, -1, -1);
                    return;
                }

                if (var2) {
                    this.fieldAW = (this.fieldAW - 1) % 30;
                    if (this.fieldAW < 15) {
                        this.fieldAW = 29;
                    }

                    Auto.fieldAA(this.fieldAW);

                    for (var6 = 0; var6 < GameScr.vMob.size(); ++var6) {
                        Mob var3;
                        if ((var3 = (Mob) GameScr.vMob.elementAt(var6)).isBoss && var3.hp > 0 && var3.status != 0) {
                            Service.gI().chatParty("pkm " + super.fieldAB);
                            Service.gI().chatParty("pkk " + (super.fieldAC = TileMap.zoneID));
                            break;
                        }
                    }

                    if (this.fieldAW == 15 && ++this.fieldAV == 2) {
                        Service.gI().chatParty("pke");
                        Code.fieldAC();
                        return;
                    }
                }
            } else if (super.fieldAB == TileMap.mapID && super.fieldAC == TileMap.zoneID) {
                this.fieldAN();
                fieldAM();
                if (var1.cMP < var1.cMaxMP * Char.aMpValue / 100) {
                    var1.gameAE(17);
                }

                if (var1.cHP < var1.cMaxHP * Char.aHpValue / 100) {
                    var6 = (int) (System.currentTimeMillis() / 1000L);

                    for (int var8 = 0; var8 < var1.vEff.size(); ++var8) {
                        Effect var4;
                        if ((var4 = (Effect) var1.vEff.elementAt(var8)).template.id == 21 && var4.timeLenght - (var6 - var4.timeStart) >= 2) {
                            return;
                        }
                    }

                    Char.getMyChar().gameAE(16);
                    return;
                }
            } else {
                this.fieldAA(super.fieldAB, super.fieldAC, super.fieldAE, super.fieldAF);
            }

        }
    }

    public final String toString() {
        return "PK Boss";
    }
}
