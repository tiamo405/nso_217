
public class As10 extends Auto {

    public As10() {
        super.fieldAD();
    }

    /** Cờ loại quái cho Auto.fieldAB: 1 thường | 2 tinh anh | 4 thủ lĩnh (không gồm 8 = boss thế giới). */
    static final int UPLEVEL_MOB_MASK = 1 | 2 | 4;

    /**
     * Đánh quái mobId (-1 = mọi quái), cho phép cả tinh anh / thủ lĩnh (nhiều exp hơn).
     * Bật Char.fieldFC ("Săn TATL") để Auto.fieldAB ưu tiên tinh anh/thủ lĩnh khi có trên map,
     * kể cả khi RMS cài đặt cũ đang lưu false.
     */
    protected final void uplevelFight(int mobId) {
        Char.fieldFC = true;
        this.fieldAB(mobId, UPLEVEL_MOB_MASK);
    }

    public boolean fieldAA(Char var1) {
        return var1.ctaskId >= 9;
    }

    public void fieldAA(Char var1, byte var2, byte var3) {
        switch (var1.ctaskId) {
            case 0:
                NpcTemplate var5 = Npc.arrNpcTemplate[var3];

                for (int var7 = 0; var7 < var5.menu.length; ++var7) {
                    if (var5.menu[var7][0].equals("Nói chuyện")) {
                        GameScr.fieldAB(var3, var7, 0);
                        LockGame.fieldAO();
                        return;
                    }
                }

                return;
            case 1:
                if (var1.taskMaint.index == 0) {
                    GameScr.fieldAB(var3, 0, 0);
                    Service.gI().getTask(var3, 1, -1);
                  //  LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 1) {
                    Service.gI().getTask(var3, 0, -1);
               //     LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 2) {
                    Service.gI().getTask(var3, 1, -1);
                 //   LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 3) {
                    Service.gI().getTask(var3, 2, -1);
                 //   LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 4) {
                    Service.gI().getTask(var3, 0, -1);
                  //  LockGame.fieldAO();
                    return;
                }
                break;
            case 2:
                if (var1.taskMaint.index == 0) {
                    if (var1.arrItemBag[0] != null) {
                        Service.gI().useItem(0);
                    }

                    LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 1) {
                    if (TileMap.mapID == 22) {
                        this.fieldAC(-1);
                        this.uplevelFight(0);
                        return;
                    }

                    this.fieldAA(22, -1, -1, -1);
                    return;
                }
                break;
            case 3:
                if (var1.taskMaint.index == 0) {
                    cuong.sleep(2000L);
                    GameScr.fieldAB(4, 0, 0);
                    Service.gI().buyItem(9, 0, 3);
                    LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 1) {
                    if (var1.arrItemBag[0] != null) {
                        Service.gI().useItem(0);
                    }

                    LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 2) {
                    if (TileMap.mapID == 23) {
                        this.fieldAC(-1);
                        this.uplevelFight(1);
                        return;
                    }

                    this.fieldAA(23, -1, -1, -1);
                    return;
                }

                if (var1.taskMaint.index == 3) {
                    if (TileMap.mapID == 23) {
                        this.fieldAC(-1);
                        this.uplevelFight(2);
                        return;
                    }

                    this.fieldAA(23, -1, -1, -1);
                    return;
                }
                break;
            case 4:
                if (var1.taskMaint.index == 0) {
                    if (TileMap.mapID == 21) {
                        this.fieldAC(-1);
                        this.uplevelFight(-1);
                        return;
                    }

                    this.fieldAA(21, -1, -1, -1);
                    return;
                }

                if (var1.taskMaint.index == 1) {
                    if (TileMap.mapID == 21) {
                        this.fieldAC(209);
                        this.uplevelFight(3);
                        return;
                    }

                    this.fieldAA(21, -1, -1, -1);
                    return;
                }

                if (var1.taskMaint.index == 2) {
                    if (TileMap.mapID == 23) {
                        this.fieldAC(210);
                        this.uplevelFight(4);
                        return;
                    }

                    this.fieldAA(23, -1, -1, -1);
                    return;
                }
                break;
            case 5:
                if (var1.taskMaint.index == 0) {
                    if (TileMap.mapID == 20) {
                        this.fieldAC(-1);
                        this.uplevelFight(3);
                        return;
                    }

                    this.fieldAA(20, -1, -1, -1);
                    return;
                }

                if (var1.taskMaint.index == 1) {
                    if (TileMap.mapID == 20) {
                        this.fieldAC(211);
                        this.uplevelFight(54);
                        return;
                    }

                    this.fieldAA(20, -1, -1, -1);
                    return;
                }
                break;
            case 6:
                if (var1.taskMaint.index == 0) {
                    if (TileMap.mapID == 26) {
                        this.fieldAC(-1);
                        this.uplevelFight(-1);
                        return;
                    }

                    this.fieldAA(26, -1, -1, -1);
                    return;
                }

                if (var1.taskMaint.index == 1) {
                    super.fieldAA(2, -2, -1, -1);
                    cuong.sleep(500L);
                    return;
                }

                if (var1.taskMaint.index == 2) {
                    super.fieldAA(71, -2, -1, -1);
                    cuong.sleep(500L);
                    return;
                }

                if (var1.taskMaint.index == 3) {
                    super.fieldAA(26, -2, -1, -1);
                    cuong.sleep(500L);
                    return;
                }
                break;
            case 7:
                if (var1.taskMaint.index == 0) {
                    if (TileMap.mapID == 71) {
                        this.fieldAC(-1);
                        this.uplevelFight(-1);
                        return;
                    }

                    this.fieldAA(71, -1, -1, -1);
                    return;
                }

                if (var1.taskMaint.index == 1) {
                    super.fieldAA(var2, -2, -1, -1);
                    GameScr.fieldAB(var3, 0, 0);
                    Service.gI().getTask(var3, 1, -1);
                  //  LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 2) {
                    Service.gI().getTask(var3, 0, -1);
                //    LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 3) {
                    Service.gI().getTask(var3, 1, -1);
                 //   LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 4) {
                    Service.gI().getTask(var3, 1, -1);
                 //   LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 5) {
                    Service.gI().getTask(var3, 2, -1);
                //   LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 6) {
                    GameScr.fieldAB(var3, 0, 0);
                    Service.gI().getTask(var3, 2, -1);
                 //   LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 7) {
                    Service.gI().getTask(var3, 0, -1);
                 //   LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 8) {
                    Service.gI().getTask(var3, 2, -1);
                   // LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 9) {
                    Service.gI().getTask(var3, 2, -1);
                 //   LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 10) {
                    Service.gI().getTask(var3, 1, -1);
                //    LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 11) {
                    GameScr.fieldAB(var3, 0, 0);
                    Service.gI().getTask(var3, 0, -1);
                  //  LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 12) {
                    Service.gI().getTask(var3, 1, -1);
                 //   LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 13) {
                    Service.gI().getTask(var3, 2, -1);
                 //   LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 14) {
                    Service.gI().getTask(var3, 2, -1);
                 //   LockGame.fieldAO();
                    return;
                }

                if (var1.taskMaint.index == 15) {
                    Service.gI().getTask(var3, 1, -1);
                //    LockGame.fieldAO();
                    return;
                }
                break;
            case 8:
                if (var1.taskMaint.index == 0) {
                    if (TileMap.mapID == 26) {
                        this.fieldAC(-1);
                        this.uplevelFight(-1);
                        return;
                    }

                    this.fieldAA(26, -1, -1, -1);
                    return;
                }

                super.fieldAA(var2, -2, -1, -1);
                GameScr.fieldAH(var3);
                NpcTemplate var6 = Npc.arrNpcTemplate[var3];

                for (int var4 = 0; var4 < var6.menu.length; ++var4) {
                    if (var6.menu[var4][0].equals("Nói chuyện")) {
                        GameScr.fieldAB(var3, var4, 0);
                        LockGame.fieldAO();
                        cuong.sleep(1000L);
                        return;
                    }
                }
        }

    }

    public final void fieldAA() {
        Char var1 = Char.getMyChar();
        if (this.fieldAA(var1)) {
            GameScr.fieldAC("Xong!");
            if (this instanceof As20) {
                System.out.println("UPLEVEL DONE name=" + var1.cName + " level=" + var1.clevel
                        + " task=" + var1.ctaskId);
                AccountAutoManager.onAs20Finished();
                return;
            }
            Code.fieldAG();
        } else {
            byte var2 = GameScr.gameBE();
            byte var3 = GameScr.gameBF();
            if (Char.getMyChar().cHP <= 0) {
                Auto.fieldAA(false);
            } else if (var1.taskMaint == null) {
                if (this instanceof As20 && var1.ctaskId == 17) {
                    this.fieldAA(var1, var2, var3);
                    return;
                }
                if (this instanceof As20 && var1.ctaskId == 9
                        && var1.nClass != null && var1.nClass.classId == 0) {
                    if (var1.clevel < 10) {
                        if (TileMap.mapID != 26) {
                            this.fieldAA(26, -1, -1, -1);
                            return;
                        }
                        this.uplevelFight(-1);
                        this.fieldAC(-1);
                    } else {
                        Item weapon = var1.arrItemBody == null ? null : var1.arrItemBody[1];
                        if ("Chưa vào lớp".equals(var1.nClass.name) && weapon != null) {
                            System.out.println("UPLEVEL AS20 unequip-before-class item="
                                    + weapon.template.name + "#" + weapon.template.id);
                            Service.gI().itemBodyToBag((int) weapon.template.type);
                            LockGame.fieldAQ();
                            return;
                        }
                        this.fieldAA(var1, var2, var3);
                    }
                    return;
                }

                if (TileMap.mapID != var2) {
                    super.fieldAA(var2, -2, -1, -1);
                } else {
                    GameScr.fieldAB(var3, 0, 0);
                    Service.gI().getTask(var3, 0, -1);
                    LockGame.fieldAO();
                    super.fieldAC = -1;
                }
            } else if (var1.taskMaint.index >= var1.taskMaint.subNames.length - 1) {
                if (TileMap.mapID != var2) {
                    super.fieldAA(var2, -2, -1, -1);
                } else {
                    GameScr.fieldAB(var3, 0, 0);
                    Service.gI().getTask(var3, 0, -1);
                    LockGame.fieldAO();
                    super.fieldAC = -1;
                }
            } else {
                this.fieldAA(var1, var2, var3);
            }
        }
    }

    public String toString() {
        return "Auto Nhiệm Vụ 10";
    }
}
