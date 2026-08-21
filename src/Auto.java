
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public abstract class Auto {

    public boolean fieldAA;
    public int fieldAB;
    public int fieldAC;
    public boolean fieldAD;
    public int fieldAE;
    public int fieldAF;
    private int fieldAV;
    private int fieldAW;
    public int fieldAG;
    public long fieldAH;
    public long fieldAI;
    public Auto fieldAJ;
    public static boolean fieldAK;
    public static Skill fieldAL;
    public static boolean fieldAM;
    public static MyVector fieldAN;
    private static MyVector fieldAX;
    public static int fieldAO;
    private static boolean fieldAY;
    private static long fieldAZ;
    public static MyVector fieldAP;
    public static MyVector fieldAQ;
    protected long fieldAR = 0L;
    protected long fieldAS = 0L;
    protected long fieldAT = 0L;
    protected boolean fieldAU = false;
    private static MyVector fieldBA;
    private static long fieldBB = 0L;
    private byte fieldBC;
    private ByteArrayInputStream fieldBD;
    private DataInputStream fieldBE;

    private static void fieldAM() {
        fieldAN = new MyVector();
        fieldAX = new MyVector();
        fieldAO = 0;
        fieldAY = false;
        fieldAZ = -1L;
        fieldAP = new MyVector();
        fieldAQ = new MyVector();
        fieldBA = new MyVector();
    }

    public static void fieldAA(Mob var0) {
        if (var0.isBoss || var0.status != 0 && var0.levelBoss != 3 && var0.maxHp != var0.getTemplate().hp) {
            if (!var0.isBoss && var0.levelBoss == 0) {
                if (var0.maxHp == 10 * var0.getTemplate().hp) {
                    var0.levelBoss = 1;
                } else {
                    if (var0.maxHp != 100 * var0.getTemplate().hp && var0.templateId != 89) {
                        return;
                    }

                    var0.levelBoss = 2;
                }
            }

            if (!fieldAN.contains(var0)) {
                fieldAN.addElement(var0);
            }
        }

    }

    public static void fieldAB(Mob var0) {
        fieldAN.removeElement(var0);
    }

    public static void fieldAB() {
        fieldAN.removeAllElements();
    }

    public static void fieldAB(Char var0) {
        if (var0 != Char.getMyChar()) {
            if (fieldAX.contains(var0)) {
                if (var0.cTypePk != 3 && var0.killCharId != Char.getMyChar().charID) {
                    fieldAX.removeElement(var0);
                    return;
                }
            } else if (var0.cTypePk == 3 || var0.killCharId == Char.getMyChar().charID) {
                fieldAX.addElement(var0);
                if (LockGame.fieldAB && Res.abs(Char.getMyChar().cx - var0.cx) <= 300 && Res.abs(Char.getMyChar().cy - var0.cy) <= 300) {
                    LockGame.fieldAD();
                }
            }
        }

    }

    public static void fieldAC() {
        fieldAX.removeAllElements();
    }

    public void fieldAD() {
        this.fieldAB = -1;
        this.fieldAC = -1;
        this.fieldAD = false;
        this.fieldAJ = null;
        this.fieldAG = Char.getMyChar().yen;
        this.fieldAH = Char.getMyChar().cEXP;
        this.fieldAI = System.currentTimeMillis();
        this.fieldAA = false;
        Code.fieldAS = -1;
        Code.fieldAW = 0;
        fieldAK = Char.getMyChar().isHuman;
        fieldAL = Char.getMyChar().myskill;
        this.fieldAE();
    }

    protected void fieldAE() {
        fieldAM = false;
        Code.fieldBC = System.currentTimeMillis();
    }

    protected static boolean fieldAC(Char var0) {
        return var0.cHP <= 0 || var0.statusMe == 14 || var0.statusMe == 5;
    }

    protected static boolean fieldAF() {
        return fieldAC(Char.getMyChar());
    }

    public static void fieldAA(long var0) {
        try {
            Thread.sleep(var0);
        } catch (InterruptedException var2) {
        }
    }

    public final void fieldAA(byte var1) {
        this.fieldBC = var1;
    }

    public final void fieldAA(byte[] var1) {
        this.fieldBD = new ByteArrayInputStream(var1);
        this.fieldBE = new DataInputStream(this.fieldBD);
    }

    protected final void fieldAG() {
        switch (this.fieldBC) {
            case 60:
            case 61: {
                try {
                    if (Char.getMyChar().myskill != null && Char.getMyChar().charID == this.fieldBE.readInt() && Char.getMyChar().cMP > Char.getMyChar().myskill.manaUse) {
                        Char var10000 = Char.getMyChar();
                        var10000.cMP -= Char.getMyChar().myskill.manaUse;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            default:
        }
    }

    protected final void fieldAA(int var1, int var2, int var3, int var4) {
        if ((var1 < 139 || var1 > 148) && TileMap.mapID >= 139 && TileMap.mapID <= 148) {
            fieldAH();
        } else {

            if (TileMap.mapID != var1) {
                if (!TileMap.fieldAK(var1)) {
                    if (TileMap.isLangCo(var1)) {
                        fieldAA(1L);
                    }

                    return;
                }

                if (var2 >= -1 && TileMap.zoneID != var2) {
                    fieldAA(1L);
                } else {
                    fieldAA(1L);
                }
            }

            if (var2 == -1) {
                if (Code.fieldAV) {
                    int[] var5 = Code.fieldAX;
                    Code.fieldAW = 0;
                    fieldAA(this.fieldAC = var5[0]);
                } else {
                    this.fieldAB(var2);
                }
            } else if (var2 >= 0) {
                fieldAA(var2);
            }

            if (TileMap.zoneID == this.fieldAC && var3 > 0 && var4 > 0 && Char.getMyChar().cx != var3 && Char.getMyChar().cy != var4) {
                if (this instanceof TuDanh || this instanceof PkBoss) {
                    Char.fieldAC(var3, var4);
                    return;
                }

                this.fieldAC(fieldAA(var3, var4));
            }

        }
    }

    protected static void fieldAH() {
        Char var0 = Char.getMyChar();
        if (!Char.fieldAJ(37) && !Char.fieldAJ(35)) {
            Npc var1;
            if ((var1 = GameScr.fieldAI(13)) != null && Math.abs(var1.cx - var0.cx) <= 400 && Math.abs(var1.cy - var0.cy) <= 400) {
                Char.fieldAC(var1.cx > 400 ? var1.cx - 400 : var1.cx + 400, var1.cy);
            }

            Service.gI().openUIZone();
        } else {
            Char.fieldAC(var0.cx, TileMap.pxh);
        }

        long var3 = System.currentTimeMillis();

        while (var0.cHP > 0 && System.currentTimeMillis() - var3 < 5L) {
            fieldAA(1L);
        }

    }

    protected static void fieldAA(boolean var0) {
        Char var1 = Char.getMyChar();
        if (var0) {
            if (fieldAY) {
                if (System.currentTimeMillis() - fieldAZ < 200L) {
                    return;
                }

                fieldAY = false;
            } else if (Char.fieldFI && GameScr.vParty.size() > 0) {
                for (int var3 = 0; var3 < GameScr.vParty.size(); ++var3) {
                    Party var2;
                    if ((var2 = (Party) GameScr.vParty.elementAt(var3)).charId != var1.charID && var2.c != null && var2.c.cHP > 0 && var2.c.nClass.classId == 6) {
                        GameScr.fieldAC("Chờ hồi sinh!");
                        fieldAZ = System.currentTimeMillis();
                        fieldAY = true;
                        return;
                    }
                }
            }
        }

        fieldAA(50L);
        fieldAN.removeAllElements();
        fieldAM = false;
        LockGame.fieldAA = true;
        CodePhu.fieldAA();
        LockGame.fieldAA = false;
        fieldAA(50L);
    }

    protected static void fieldAA(int var0) {
        if (TileMap.zoneID != var0) {
            Npc var1 = GameScr.fieldAI(13);
            int var2 = -1;
            if (var1 != null && var1.statusMe != 15) {
                if (Math.abs(var1.cx - Char.getMyChar().cx) > 22 || Math.abs(var1.cy - Char.getMyChar().cy) > 22) {
                    Char.fieldAC(var1.cx, var1.cy);
                    try {
                        Thread.sleep(1L);
                        return;
                    } catch (InterruptedException var8) {
                    }
                }
            } else {
                if (TileMap.mapID != 99 && TileMap.mapID != 103 && TileMap.mapID != 134 && TileMap.mapID != 135 && TileMap.mapID != 136 && TileMap.mapID != 137) {
                    return;
                }

                if ((var2 = Char.fieldAI(37)) < 0 && (var2 = Char.fieldAI(35)) < 0) {
                    return;
                }
            }

            if (System.currentTimeMillis() - fieldBB < 1L) {
                return;
            }

            Service.gI().requestChangeZone(var0, var2);
            TileMap.fieldAF();
            fieldBB = System.currentTimeMillis();
            try {
                Thread.sleep(1L);
                return;
            } catch (InterruptedException var8) {
            }
        }

    }

    protected final void fieldAB(int var1) {
        if (!this.fieldAA || Code.fieldAH == null || Char.getMyChar().cName.equals(Code.fieldAH)) {
            GameScr var2 = GameScr.gI();
            Npc var3 = GameScr.fieldAI(13);
            int var4 = -1;
            if (var3 != null && var3.statusMe != 15) {
                if (Math.abs(var3.cx - Char.getMyChar().cx) > 22 || Math.abs(var3.cy - Char.getMyChar().cy) > 22) {
                    Char.fieldAC(var3.cx, var3.cy);
                    try {
                        Thread.sleep(1L);
                        return;
                    } catch (InterruptedException var8) {
                    }
                }
            } else {
                if (TileMap.mapID != 99 && TileMap.mapID != 103 && TileMap.mapID != 134 && TileMap.mapID != 135 && TileMap.mapID != 136 && TileMap.mapID != 137) {
                    this.fieldAC = TileMap.zoneID;
                    fieldBB = System.currentTimeMillis();
                    return;
                }

                if ((var4 = Char.fieldAI(37)) < 0 && (var4 = Char.fieldAI(35)) < 0) {
                    this.fieldAC = TileMap.zoneID;
                    fieldBB = System.currentTimeMillis();
                    return;
                }
            }

            if (System.currentTimeMillis() - fieldBB < 1L) {
                return;
            }

            Service.gI().openUIZone();
            LockGame.fieldAE();
            int var7 = -1;
            if (var1 < 0) {
                var1 = var2.zones.length - 1;
            } else if (var1 >= var2.zones.length) {
                var1 = 0;
            }

            if (this instanceof TaThu) {
                var7 = (var1 / 5 + 1) * 5 % var2.zones.length;
            } else if (!Char.fieldFD) {
                var7 = (var1 + 1) % var2.zones.length;
            } else {
                int var5 = -1;

                for (int var6 = (var1 + 1) % var2.zones.length; var6 != var1; var6 = (var6 + 1) % var2.zones.length) {
                    if (var5 == -1 || var2.zones[var6] < var5) {
                        var7 = var6;
                        var5 = var2.zones[var6];
                    }
                }
            }

            Service.gI().requestChangeZone(var7, var4);
            this.fieldAC = var7;
            TileMap.fieldAF();
            if (this.fieldAN()) {
                Service.gI().chatParty("khu " + var7);
            }

            fieldBB = System.currentTimeMillis();
            try {
                Thread.sleep(1L);
                return;
            } catch (InterruptedException var8) {
            }
        }

    }

    private static boolean fieldAA(Mob var0, int var1) {
        if (var0.templateId == 202 && var0.status == 8) {
            return false;
        } else {
            return var1 < 0 || var0.templateId == var1;
        }
    }

    private static boolean fieldAC(int var0, int var1) {
        return var1 < 0 || var0 == 0 && (var1 & 1) > 0 || var0 == 1 && (var1 & 2) > 0 || var0 == 2 && (var1 & 4) > 0 || var0 == 3 && (var1 & 8) > 0;
    }

    public final int fieldAA(boolean var1, boolean var2, boolean var3) {
        if (this.fieldAD) {
            return -1;
        } else {
            int var4 = 0;
            if (var1) {
                var4 = 1;
            }

            if (var2) {
                var4 |= 2;
            }

            if (var3) {
                var4 |= 4;
            }

            return var4;
        }
    }

    protected static boolean fieldAA(Char var0, Char var1) {
        return var1.statusMe != 14 && var1.statusMe != 5 && var1.statusMe != 15 && (var1.cTypePk == 3 || var0.cTypePk == 3 || var1.cTypePk == 1 && var0.cTypePk == 1 || var1.cTypePk == 5 && var0.cTypePk == 4 || var1.cTypePk == 4 && var0.cTypePk == 5 || var0.killCharId >= 0 && var0.killCharId == var1.charID || var0.testCharId >= 0 && var0.testCharId == var1.charID || var1.killCharId >= 0 && var1.killCharId == var0.charID);
    }

    protected final void fieldAC(Mob var1) {
        if (var1 != null) {
            int var2 = var1.xFirst;
            int var3 = var1.yFirst;
            Char var4 = Char.getMyChar();
            if (TileMap.mapID == 35) {
                if (var1.xFirst == 1428 && var1.yFirst == 528) {
                    var2 = 1452;
                    var3 = 552;
                } else if (var1.xFirst == 1284 && var1.yFirst == 528) {
                    var2 = 1308;
                    var3 = 552;
                } else if (var1.xFirst == 1836 && var1.yFirst == 648) {
                    var2 = 1812;
                    var3 = 672;
                }
            } else if (TileMap.mapID == 37) {
                if ((var1.xFirst == 876 || var1.xFirst == 900) && var1.yFirst == 408) {
                    var2 = 900;
                    var3 = 432;
                } else if ((var1.xFirst == 828 || var1.xFirst == 852) && var1.yFirst == 360) {
                    var2 = 852;
                    var3 = 384;
                } else if ((var1.xFirst == 924 || var1.xFirst == 876) && var1.yFirst == 624) {
                    var2 = 924;
                    var3 = 648;
                } else if (var1.xFirst == 732 && var1.yFirst == 600 || var1.xFirst == 756 && var1.yFirst == 576) {
                    var2 = 756;
                    var3 = 600;
                }
            }

            if (Char.fieldAD(var2, var3)) {
                this.fieldAV = this.fieldAE;
                this.fieldAW = this.fieldAF;
                this.fieldAE = var4.cx;
                this.fieldAF = var4.cy;
                var4.mobFocus = var1;
                fieldAA(1L);
                return;
            }

            var4.mobFocus = null;
        }

    }

    protected static void fieldAD(Char var0) {
        if (var0 != null) {
            Char var1 = Char.getMyChar();
            Char.fieldAC(var0.cx, TileMap.fieldAD(var0.cx, var0.cy));
            var1.charFocus = var0;
            fieldAA(1L);
        }

    }

    public static void fieldAA(SkillPaint var0) {
        if (fieldAP.size() > 0 || fieldAQ.size() > 0) {
            EffectPaint[] var1 = new EffectPaint[fieldAP.size() + fieldAQ.size()];

            int var2;
            for (var2 = 0; var2 < fieldAP.size(); ++var2) {
                var1[var2] = new EffectPaint();
                var1[var2].effCharPaint = GameScr.efs[var0.id - 1];
                var1[var2].eMob = (Mob) fieldAP.elementAt(var2);
            }

            for (var2 = 0; var2 < fieldAQ.size(); ++var2) {
                var1[var2 + fieldAP.size()] = new EffectPaint();
                var1[var2 + fieldAP.size()].effCharPaint = GameScr.efs[var0.id - 1];
                var1[var2 + fieldAP.size()].eChar = (Char) fieldAQ.elementAt(var2);
            }

            if (var1.length > 1) {
                EPosition var4 = new EPosition();
                if (var1[0].eMob != null) {
                    var4 = new EPosition(var1[0].eMob.x, var1[0].eMob.y);
                } else if (var1[0].eChar != null) {
                    var4 = new EPosition(var1[0].eChar.cx, var1[0].eChar.cy);
                }

                MyVector var5 = new MyVector();

                for (int var3 = 1; var3 < var1.length; ++var3) {
                    if (var1[var3].eMob != null) {
                        var5.addElement(new EPosition(var1[var3].eMob.x, var1[var3].eMob.y));
                    } else if (var1[var3].eChar != null) {
                        var5.addElement(new EPosition(var1[var3].eChar.cx, var1[var3].eChar.cy));
                    }

                    if (var3 > 5) {
                        break;
                    }
                }

                Lightning.gameAA(var5, var4, true, Char.getMyChar().gameAT());
            }

            Char.getMyChar().gameKI = var1;
        }

    }

    private boolean fieldAN() {
        return this.fieldAA && GameScr.vParty.size() > 0 && ((Party) GameScr.vParty.firstElement()).charId == Char.getMyChar().charID;
    }

    protected final boolean fieldAI() {
        return this.fieldAA && GameScr.vParty.size() > 0 && ((Party) GameScr.vParty.firstElement()).charId != Char.getMyChar().charID;
    }

    private void fieldAO() {
        if (Code.fieldAV) {
            fieldAA(this.fieldAC = Code.fieldAX[Code.fieldAW = (Code.fieldAW + 1) % Code.fieldAX.length]);
            if (this.fieldAN()) {
                Service.gI().chatParty("khu " + this.fieldAC);
                return;
            }
        } else {
            this.fieldAB(TileMap.zoneID);
        }

    }

    private boolean fieldAA(int var1, int var2, int var3) {
        if (var1 >= 4) {
            return false;
        } else {
            for (int var4 = 0; var4 < fieldAN.size(); ++var4) {
                Mob var5;
                if ((var5 = (Mob) fieldAN.elementAt(var4)).levelBoss != 0 && var5.hp > 0 && var5.status != 0) {
                    boolean var6;
                    label71:
                    {
                        if (var5.levelBoss == 3) {
                            if (this instanceof TaThu || this instanceof TuDanh) {
                                var6 = false;
                                break label71;
                            }
                        } else if ((!var5.isBoss || (var1 & 6) == 6) && (var5.levelBoss != 1 || (var1 & 2) != 0) && (var5.levelBoss != 2 || (var1 & 4) != 0)) {
                            var6 = false;
                            break label71;
                        }

                        var6 = true;
                    }

                    if (var6 && Res.abs(var2 - var5.xFirst) <= 200 && Res.abs(var3 - var5.yFirst) <= 100) {
                        return true;
                    }
                } else {
                    fieldAN.removeElement(var5);
                    --var4;
                }
            }

            return false;
        }
    }

    private boolean fieldAD(int var1, int var2) {
        if (Char.fieldFJ && !(this instanceof TaThu)) {
            for (int var3 = 0; var3 < fieldAX.size(); ++var3) {
                Char var4;
                if (!fieldAC(var4 = (Char) fieldAX.elementAt(var3)) && Res.abs(var1 - var4.cx) <= 300 && Res.abs(var2 - var4.cy) <= 300) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    protected static Mob fieldAA(int var0, int var1) {
        Mob var2 = null;
        Char var3 = Char.getMyChar();
        int var4 = var0 - var3.gameBE() - 10;
        int var5 = var0 + var3.gameBE() + 10;
        int var6 = var1 - var3.gameBF() - (var3.nClass.classId != 0 && var3.nClass.classId != 1 && var3.nClass.classId != 3 && var3.nClass.classId != 5 ? 0 : 40);
        int var12;
        if ((var12 = var1 + var3.gameBF()) > var1 + 30) {
            var12 = var1 + 30;
        }

        int var7 = -1;

        for (int var8 = 0; var8 < GameScr.vMob.size(); ++var8) {
            Mob var9 = (Mob) GameScr.vMob.elementAt(var8);
            int var10 = Math.abs(var0 - var9.x);
            int var11 = Math.abs(var1 - var9.y);
            var10 = var10 > var11 ? var10 : var11;
            if (var4 <= var9.x && var9.x <= var5 && var6 <= var9.y && var9.y <= var12 && var9.status != 0 && var9.status != 1 && (var7 == -1 || var10 < var7)) {
                var2 = var9;
                var7 = var10;
            }
        }

        return var2;
    }

    protected final void fieldAA(int var1, boolean var2) {
        if (Code.fieldAS < 0 || Code.fieldAS >= Code.fieldAT.size()) {
            Code.fieldAS = 0;
        }

        while (true) {
            int var3 = ((Integer) Code.fieldAT.elementAt(Code.fieldAS)).intValue();
            int var4 = ((Integer) Code.fieldAU.elementAt(Code.fieldAS)).intValue();
            Mob var5 = fieldAA(var3, var4);
            if (!this.fieldAA(var1, var3, var4) && !this.fieldAD(var3, var4) && var5 != null && !this.fieldAA(var1, var5.x, var5.y)) {
                this.fieldAV = Char.getMyChar().cx;
                this.fieldAW = Char.getMyChar().cy;
                Char.fieldAC(var3, var4);
                Char.getMyChar().mobFocus = var5;
                Service.gI().sendAttackMobFast(var5.mobId);
                try {
                    Thread.sleep(1L);
                    return;
                } catch (InterruptedException var8) {
                }
                return;
            }

            if (++Code.fieldAS == Code.fieldAT.size()) {
                Code.fieldAS = 0;
                if (Char.fieldFB && var2) {
                    this.fieldAO();
                }
            }
        }
    }

    protected Mob fieldAA(Char var1, int var2, int var3, Char var4, boolean var5) {
        if (Code.fieldAR && Code.fieldAT.size() > 0) {
            this.fieldAA(var3, var5);
            return fieldAA(var1.cx, var1.cy);
        } else {
            var4 = var4;
            int var6 = var3;
            int var7 = var2;
            var3 = var1.cy;
            var2 = var1.cx;
            Auto var20 = this;
            int var8 = -1;
            int var9 = -1;
            int var10 = -1;
            Mob var11 = null;
            MyVector var12 = GameScr.vMob;
            int var13 = 0;

            Mob var21;
            while (true) {
                if (var13 >= var12.size()) {
                    var21 = var11;
                    break;
                }

                Mob var14;
                if ((var14 = (Mob) var12.elementAt(var13)) != null && var14.hp > 0 && var14.status != 0 && var14.status != 1 && fieldAA(var14, var7) && fieldAC(var14.levelBoss, var6) && (var4 == null || var4.charID == Char.getMyChar().charID || Res.gameAA(var14.xFirst, var14.yFirst, var4.cx, var4.cy) <= 1000) && !var20.fieldAA(var6, var14.x, var14.y) && !var20.fieldAD(var14.x, var14.y)) {
                    if (var20.fieldAD) {
                        if (var20.fieldAB != 157 && var20.fieldAB != 158 && var20.fieldAB != 159) {
                            if (var8 == -1 || var14.levelBoss < var10 || var14.yFirst < var8 || var14.yFirst == var8 && var14.xFirst < var9) {
                                var10 = var14.levelBoss;
                                var8 = var14.yFirst;
                                var9 = var14.xFirst;
                                var11 = var14;
                            }
                        } else if (var14.isBoss) {
                            var21 = var14;
                            break;
                        }
                    } else if (Code.fieldAN == -1 || Res.gameAA(Code.fieldAO, Code.fieldAP, var14.xFirst, var14.yFirst) <= Code.fieldAN) {
                        MyVector var15 = var12;
                        Mob var16 = var14;
                        int var17 = 0;

                        int var18;
                        for (var18 = 0; var18 < var15.size(); ++var18) {
                            Mob var19;
                            if ((var19 = (Mob) var15.elementAt(var18)) != null && var19.hp > 0 && var19.status != 0 && var19.status != 1 && fieldAA(var16, var7) && fieldAC(var16.levelBoss, var6) && Res.abs(var19.x - var16.x) <= 100 && Res.abs(var19.y - var16.y) <= 50) {
                                ++var17;
                            }
                        }

                        if (var17 > fieldAL.maxFight) {
                            var17 = fieldAL.maxFight;
                        }

                        var17 = var16.levelBoss << 4 | var17 & 15;
                        var18 = var4 != null && var4.charID != Char.getMyChar().charID ? Res.gameAA(var4.cx, var4.cy, var14.xFirst, var14.yFirst) : Res.gameAA(var2, var3, var14.xFirst, var14.yFirst);
                        if (var17 > var10 || var17 == var10 && var18 < var8) {
                            var10 = var17;
                            var8 = var18;
                            var11 = var14;
                        }
                    }
                }

                ++var13;
            }

            if (var21 != null) {
                this.fieldAC(var21);
                return var21;
            } else {
                if (System.currentTimeMillis() - this.fieldAR > 1L && !this.fieldAJ()) {
                    if (this.fieldAD) {
                        int var22;
                        if ((var22 = TileMap.fieldAH(TileMap.mapID)) >= 0) {
                            this.fieldAB = var22;
                        }

                        this.fieldAE = this.fieldAF = -1;
                        fieldAA(5L);
                    } else if (var5 && Char.fieldFB) {
                        this.fieldAO();
                    }
                }

                return null;
            }
        }
    }

    protected final Char fieldAA(Char var1, int var2) {
        for (int var3 = 0; var3 < GameScr.vCharInMap.size(); ++var3) {
            Char var4;
            if ((var4 = (Char) GameScr.vCharInMap.elementAt(var3)) != null && !fieldAC(var4) && !this.fieldAA(var2, var4.cx, var4.cy) && !this.fieldAD(var4.cx, var4.cy) && !Code.fieldAD(var4.cName) && SavePk.fieldAC(var4.cName) && (var4.cTypePk == 1 || var4.killCharId == var1.charID || var1.cPk < 15)) {
                return var4;
            }
        }

        return null;
    }

    protected static Char fieldAE(Char var0) {
        Char var1 = var0;
        int var2 = var0.cy;
        int var3 = var0.cx;
        var0 = null;
        Char var4 = Char.getMyChar();
        int var5 = var3 - var4.gameBE() - 10;
        int var6 = var3 + var4.gameBE() + 10;
        int var7 = var2 - var4.gameBF() - (var4.nClass.classId != 0 && var4.nClass.classId != 1 && var4.nClass.classId != 3 && var4.nClass.classId != 5 ? 0 : 40);
        int var8 = var2 + var4.gameBF() + (var4.nClass.classId != 0 && var4.nClass.classId != 1 && var4.nClass.classId != 3 && var4.nClass.classId != 5 ? 0 : 40);
        int var9 = -1;

        for (int var10 = 0; var10 < GameScr.vCharInMap.size(); ++var10) {
            Char var11 = (Char) GameScr.vCharInMap.elementAt(var10);
            int var12 = Math.abs(var3 - var11.cx);
            int var13 = Math.abs(var2 - var11.cy);
            var12 = var12 > var13 ? var12 : var13;
            if (var5 <= var11.cx && var11.cx <= var6 && var7 <= var11.cy && var11.cy <= var8 && !fieldAC(var11) && fieldAA(var4, var11) && !Code.fieldAD(var11.cName) && (var9 == -1 || var12 < var9)) {
                var0 = var11;
                var9 = var12;
            }
        }

        return var1.charFocus = var0;
    }

    protected final void fieldAB(int var1, int var2) {
        Char var3 = Char.getMyChar();
        if (!fieldAL()) {
            Char var4 = this.fieldAA && GameScr.vParty.size() > 0 ? ((Party) GameScr.vParty.firstElement()).c : null;
            boolean var5 = !this.fieldAA || Code.fieldAH == null || var3.cName.equals(Code.fieldAH) && LockGame.fieldBH();
            Mob var6 = var3.mobFocus;
            Char var7 = var3.charFocus;
            if (Code.fieldBO && (var7 == null || Code.fieldAD(var7.cName) || !SavePk.fieldAC(var7.cName) && !fieldAA(var3, var7)) && (var7 = this.fieldAA(var3, var2)) == null) {
                var7 = fieldAE(var3);
            }

            boolean var8 = var7 != null && SavePk.fieldAC(var7.cName);
            if (var7 == null && this.fieldAU) {
                Service.gI().changePk(0);
                this.fieldAU = false;
            }

            if (Code.fieldBO && var3.cPk >= 5 && System.currentTimeMillis() - this.fieldAT > 5000L) {
                Item var9;
                if ((var9 = Char.fieldAF(257)) != null && var9.template.id == 257) {
                    Service.gI().useItem(var9.indexUI);
                }

                this.fieldAT = System.currentTimeMillis();
            }

            if (Code.fieldAR && Code.fieldAT.size() > 0 && Code.fieldAS < 0) {
                this.fieldAA(var2, var5);
                return;
            }

            boolean var16 = false;
            if (this.fieldAA(var2, var3.cx, var3.cy) || this.fieldAD(var3.cx, var3.cy) || var6 != null && this.fieldAA(var2, var6.x, var6.y)) {
                fieldAA(300L);
                GameScr.fieldAC("Né");
                if (Char.fieldFB && var5) {
                    this.fieldAO();
                    var16 = true;
                } else {
                    var16 = false;
                }

                if (var16) {
                    return;
                }

                var16 = true;
                var6 = null;
            }

            if (var6 == null || var6.status == 0 || !fieldAA(var6, var1) || !fieldAC(var6.levelBoss, var2) || System.currentTimeMillis() - this.fieldAR > 5000L) {
                var6 = this.fieldAA(var3, var1, var2, var4, var5);
            }

            if (var6 == null && var16 && this.fieldAV > 0 && this.fieldAW > 0) {
                Char.fieldAC(this.fieldAV, this.fieldAW);
            }

            int var13;
            Char var20;
            if (Char.fieldFI && GameScr.vParty.size() > 0 && var3.nClass.classId == 6 && var3.cHP > 0) {
                for (int var10 = 0; var10 < var3.vSkillFight.size(); ++var10) {
                    Skill var11;
                    if ((var11 = (Skill) var3.vSkillFight.elementAt(var10)) != null && var11.template.type == 4) {
                        if (!var11.gameAA()) {
                            for (var13 = 0; var13 < GameScr.vParty.size(); ++var13) {
                                Party var14;
                                if ((var14 = (Party) GameScr.vParty.elementAt(var13)).charId != var3.charID && var14.c != null && var14.c.cHP <= 0) {
                                    var20 = var14.c;
                                    if (Math.abs(var3.cx - var20.cx) > 50 || Math.abs(var3.cy - var20.cy) > 50) {
                                        Char.fieldAC(var20.cx, var20.cy);
                                    }

                                    fieldAA(500L);
                                    Service.gI().buffLive(var14.charId);
                                    var11.lastTimeUseThisSkill = System.currentTimeMillis();
                                    var11.paintCanNotUseSkill = true;
                                    var3.gameAB(GameScr.sks[var11.template.id], 0);
                                    fieldAA(1L);
                                    return;
                                }
                            }
                        }
                        break;
                    }
                }
            }

            if (Char.fieldFC && !this.fieldAD && (var6 == null || var6.levelBoss == 0 && (var2 & 6) != 0)) {
                boolean var17 = (var2 & 2) != 0;
                var5 = (var2 & 4) != 0;

                for (var13 = 0; var13 < fieldAN.size(); ++var13) {
                    Mob var19;
                    if ((var19 = (Mob) fieldAN.elementAt(var13)).hp > 0 && var19.status != 0 && var19.status != 1 && !this.fieldAA(var2, var19.x, var19.y) && !this.fieldAD(var19.x, var19.y) && fieldAA(var19, var1) && (var17 && var19.levelBoss == 1 || var5 && var19.levelBoss == 2)) {
                        var6 = var19;
                        this.fieldAC(var19);
                        break;
                    }
                }
            }

            if (fieldAL != null) {
                int var15;
                Skill var18;
                int var21;
                if ((var18 = fieldAL).gameAA() && (Char.isABuff || this instanceof As20)) {
                    label787:
                    {
                        var21 = 0;

                        Skill var22;
                        label589:
                        while (true) {
                            if (var21 >= var3.vSkillFight.size()) {
                                break label787;
                            }

                            if ((var22 = (Skill) var3.vSkillFight.elementAt(var21)) != null && System.currentTimeMillis() - var22.lastTimeUseThisSkill >= (long) var22.coolDown - 300L) {
                                if (var22.template.type == 2) {
                                    if ((var3.fieldAA == null && Char.fieldEG || var22.template.id < 67 || var22.template.id > 72) && (Char.fieldEH || var22.template.id != 31) && (var22.template.id != 15 || !Char.fieldEI || var3.cHP < var3.cMaxHP * Char.aHpValue / 100 && var3.isHuman) && (var22.template.id != 6 || var3.isHuman)) {
                                        var15 = (int) (System.currentTimeMillis() / 1000L);
                                        var13 = 0;

                                        while (true) {
                                            if (var13 >= var3.vEff.size()) {
                                                break label589;
                                            }

                                            Effect var12;
                                            if ((var12 = (Effect) var3.vEff.elementAt(var13)) != null && (var12.template.iconId == var22.template.iconId || var22.template.id == 58 && var12.template.type == 7) && var12.timeLenght - (var15 - var12.timeStart) >= 2) {
                                                break;
                                            }

                                            ++var13;
                                        }
                                    }
                                } else if (var22.template.type == 3 && var6 != null && var6.levelBoss == 0 && var6.hp > var6.maxHp / 2) {
                                    if (var22.template.id != 4 || Char.fieldEI && var3.cHP < var3.cMaxHP * Char.aHpValue / 100) {
                                        break;
                                    }
                                } else if ((var22.template.id == 7 || var22.template.id == 16 || var22.template.id == 25 || var22.template.id == 34 || var22.template.id == 43) && var6 != null && (var6.levelBoss != 0 || var6.hp >= var6.maxHp / 2) && (var22.template.id != 7 && var22.template.id != 16 || !var6.isFire) && (var22.template.id != 25 && var22.template.id != 34 || var6.isIce) && (var22.template.id != 43 || var6.isWind)) {
                                    break;
                                }
                            }

                            ++var21;
                        }

                        var18 = var22;
                        fieldAA(500L);
                    }
                }

                if (var18.template.type == 2) {
                    if (System.currentTimeMillis() - var18.lastTimeUseThisSkill >= (long) (var18.coolDown)) {
                        var18.lastTimeUseThisSkill = System.currentTimeMillis();
                        Service.gI().selectSkill(var18.template.id);
                        Service.gI().sendUseSkillMyBuff();
                        if (!Code.fieldBF) {
                            var3.gameAB(GameScr.sks[var18.template.id], 0);
                        }
                    } else {
                        var18.paintCanNotUseSkill = true;
                    }
                } else {
                    Mob var23;
                    if (Code.fieldBO && var7 != null && !fieldAC(var7) && (var8 || fieldAA(var3, var7))) {
                        if (var8) {
                            if ((var18.template.type == 1 || var18.template.type == 3) && (Res.abs(var3.cx - var7.cx) > var18.dx + 30 || Res.abs(var3.cy - var7.cy) > var18.dy + 30) && System.currentTimeMillis() - this.fieldAS > 1500L) {
                                fieldAD(var7);
                                this.fieldAS = System.currentTimeMillis();
                            }

                            if (var7.killCharId != var3.charID && var7.cTypePk != 3) {
                                this.fieldAU = true;
                                Service.gI().changePk(3);
                            }
                        }

                        var21 = var18.dx;
                        var13 = var18.dy;
                        fieldAP.removeAllElements();
                        fieldAQ.removeAllElements();
                        fieldAQ.addElement(var7);

                        for (var15 = 0; var15 < GameScr.vCharInMap.size() && fieldAP.size() + fieldAQ.size() < var18.maxFight; ++var15) {
                            if ((var20 = (Char) GameScr.vCharInMap.elementAt(var15)).cHP > 0 && var20.statusMe != 14 && var20.statusMe != 5 && var20.statusMe != 15 && !var20.equals(var7) && (var20.cTypePk == 3 || var3.cTypePk == 3 || var20.cTypePk == 1 && var3.cTypePk == 1 || var3.killCharId >= 0 && var3.killCharId == var20.charID || var3.testCharId >= 0 && var3.testCharId == var20.charID || var20.killCharId == var3.charID) && !Code.fieldAD(var20.cName) && var7.cx - var21 <= var20.cx && var20.cx <= var7.cx + var21 && var7.cy - var13 <= var20.cy && var20.cy <= var7.cy + var13) {
                                fieldAQ.addElement(var20);
                            }
                        }

                        for (var15 = 0; var15 < GameScr.vMob.size() && fieldAP.size() + fieldAQ.size() < var18.maxFight; ++var15) {
                            if ((var23 = (Mob) GameScr.vMob.elementAt(var15)).status != 0 && var23.status != 1 && var7.cx - var21 <= var23.x && var23.x <= var7.cx + var21 && var7.cy - var13 <= var23.y && var23.y <= var7.cy + var13 && fieldAC(var23.levelBoss, var2) && (var1 == -1 || var23.templateId == var1)) {
                                fieldAP.addElement(var23);
                            }
                        }

                        if (System.currentTimeMillis() - var18.lastTimeUseThisSkill >= (long) (var18.coolDown)) {
                            var18.lastTimeUseThisSkill = System.currentTimeMillis();
                            Service.gI().selectSkill(var18.template.id);
                            Service.gI().sendPlayerAttack((MyVector) fieldAP, (MyVector) fieldAQ, (int) 2);
                            if (!Code.fieldBF) {
                                var3.gameAB(GameScr.sks[var18.template.id], 0);
                            }
                        } else {
                            var18.paintCanNotUseSkill = true;
                        }
                    } else {
                        if (var6 == null || var1 != -1 && var6.templateId != var1 || !fieldAC(var6.levelBoss, var2)) {
                            return;
                        }

                        if ((var18.template.type == 1 || var18.template.type == 3) && (Res.abs(var3.cx - var6.xFirst) > var18.dx + 30 || Res.abs(var3.cy - var6.yFirst) > var18.dy + 30)) {
                            var3.mobFocus = null;
                            return;
                        }

                        var21 = var18.dx;
                        var13 = var18.dy;
                        fieldAP.removeAllElements();
                        fieldAQ.removeAllElements();
                        fieldAP.addElement(var6);

                        for (var15 = 0; var15 < GameScr.vMob.size() && fieldAP.size() + fieldAQ.size() < var18.maxFight; ++var15) {
                            if ((var23 = (Mob) GameScr.vMob.elementAt(var15)).status != 0 && var23.status != 1 && !var23.equals(var6) && var6.xFirst - 100 <= var23.xFirst && var23.xFirst <= var6.xFirst + 100 && var6.yFirst - 50 <= var23.yFirst && var23.yFirst <= var6.yFirst + 50 && fieldAC(var23.levelBoss, var2) && (var1 == -1 || var23.templateId == var1)) {
                                fieldAP.addElement(var23);
                            }
                        }

                        for (var15 = 0; var15 < GameScr.vCharInMap.size() && fieldAP.size() + fieldAQ.size() < var18.maxFight; ++var15) {
                            if ((var20 = (Char) GameScr.vCharInMap.elementAt(var15)).cHP > 0 && var20.statusMe != 14 && var20.statusMe != 5 && var20.statusMe != 15 && (var20.cTypePk == 3 || var3.cTypePk == 3 || var20.cTypePk == 1 && var3.cTypePk == 1 || var3.killCharId >= 0 && var3.killCharId == var20.charID || var3.testCharId >= 0 && var3.testCharId == var20.charID || var20.killCharId == var3.charID) && !Code.fieldAD(var20.cName) && var6.x - var21 <= var20.cx && var20.cx <= var6.x + var21 && var6.y - var13 <= var20.cy && var20.cy <= var6.y + var13) {
                                fieldAQ.addElement(var20);
                            }
                        }

                        if (System.currentTimeMillis() - var18.lastTimeUseThisSkill >= (long) (var18.coolDown)) {
                            var18.lastTimeUseThisSkill = System.currentTimeMillis();
                            Service.gI().selectSkill(var18.template.id);
                            Service.gI().sendPlayerAttack((MyVector) fieldAP, (MyVector) fieldAQ, (int) 1);
                            if (!Code.fieldBF) {
                                var3.gameAB(GameScr.sks[var18.template.id], 0);
                            }
                        } else {
                            var18.paintCanNotUseSkill = true;
                        }
                    }
                }

                this.fieldAR = System.currentTimeMillis();
            }
        }

    }

    protected boolean fieldAJ() {
        if (!(this instanceof TaThu) && !Code.fieldAQ) {
            Char var1 = Char.getMyChar();
            int var2 = Code.fieldAM < 0 ? -1 : Code.fieldAM;

            for (int var3 = 0; var3 < GameScr.vItemMap.size(); ++var3) {
                ItemMap var4;
                if (!(var4 = (ItemMap) GameScr.vItemMap.elementAt(var3)).fieldAK && (var1.nClass.classId == 1 && var4.template.id == 218 || var4.template.type == 19 || Code.fieldAA(var4.template) && (Char.fieldBF() > 2 || var4.template.isUpToUp && Char.fieldAJ(var4.template.id))) && (var2 < 0 || Res.gameAA(var1.cx, var1.cy, var4.xEnd, var4.yEnd) < var2) && !this.fieldAD(var4.x, var4.y)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    protected final void fieldAC(int var1) {
        if (!Code.fieldAQ) {
            Char var2 = Char.getMyChar();
            if (!fieldAL()) {
                fieldBA.removeAllElements();
                int var3 = this.fieldAA(Char.fieldFD, Char.fieldFE, Char.fieldFF);

                int var4;
                for (var4 = 0; var4 < GameScr.vItemMap.size(); ++var4) {
                    ItemMap var5;
                    if (!(var5 = (ItemMap) GameScr.vItemMap.elementAt(var4)).fieldAK && (var2.nClass.classId == 1 && var5.template.id == 218 || (Code.fieldAA(var5.template) || var5.template.id == var1) && (Char.fieldBF() > 2 || var5.template.type == 19 || var5.template.isUpToUp && Char.fieldAJ(var5.template.id))) && !this.fieldAA(var3, var5.xEnd, var5.yEnd) && !this.fieldAD(var5.xEnd, var5.yEnd) && (Code.fieldAM < 0 || Res.gameAA(var2.cx, var2.cy, var5.xEnd, var5.yEnd) < Code.fieldAM)) {
                        fieldBA.addElement(var5);
                    }
                }

                if (fieldBA.size() > 0) {
                    var4 = var2.cx;
                    int var9 = var2.cy;
                    Mob var8 = var2.mobFocus;

                    label56:
                    for (var3 = 0; var3 < fieldBA.size(); ++var3) {
                        fieldAA(1L);
                        ItemMap var6;
                        Char.fieldAC((var6 = (ItemMap) fieldBA.elementAt(var3)).xEnd, TileMap.fieldAD(var6.xEnd, var6.yEnd));
                        var2.itemFocus = var6;

                        for (int var7 = 0; var7 < 4 && var6.status != 2 && !var6.fieldAK; ++var7) {
                            Service.gI().pickItem(var6.itemMapID);
                            if (LockGame.lockpick()) {
                                break;
                            }

                            if (this.fieldAD(var2.cx, var2.cy) || var2.cHP <= 0) {
                                break label56;
                            }
                        }

                        var6.fieldAK = true;
                        var6.fieldAL = System.currentTimeMillis();
                    }

                    fieldAA(1L);
                    Char.fieldAC(var4, var9);
                    var2.mobFocus = var8;
                }
            }
        }

    }

    protected final void fieldAK() {
        if (TileMap.mapID != 22) {
            this.fieldAA(22, -2, -1, -1);
        } else {
            Char var1;
            if ((var1 = Char.getMyChar()).fieldAA != null) {
                GameScr.fieldAB(12, 3, 0);
                LockGame.fieldBC();
            } else {
                for (int var2 = 0; var2 < var1.vSkillFight.size(); ++var2) {
                    Skill var3;
                    if ((var3 = (Skill) var1.vSkillFight.elementAt(var2)) != null && !var3.gameAA() && var3.template.id >= 67 && var3.template.id <= 72) {
                        Service.gI().selectSkill(var3.template.id);
                        Service.gI().sendUseSkillMyBuff();
                        LockGame.fieldBC();
                        break;
                    }
                }

                fieldAA(200L);
            }
        }
    }

    protected static boolean fieldAL() {
        Char var0;
        if ((var0 = Char.getMyChar()).isHuman && var0.cHP < var0.cMaxHP) {
            for (int var1 = 0; var1 < var0.vEff.size(); ++var1) {
                Effect var2;
                if ((var2 = (Effect) var0.vEff.elementAt(var1)) != null && var2.template.type == 12) {
                    return true;
                }
            }
        }

        return false;
    }

    protected abstract void fieldAA();

    public String toString() {
        return "";
    }

    static {
        fieldAM();
    }
}
