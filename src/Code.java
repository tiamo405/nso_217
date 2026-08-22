
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.Vector;

public final class Code implements Runnable {

    public static Code fieldAA;
    private static boolean fieldCA;
    private static Thread fieldCB;
    public static Auto fieldAB;
    private static TanSat fieldCC;
    public static Stanima fieldAC;
    public static AutoNvhn fieldAD;
    public static TaThu fieldAE;
    private static TuDanh fieldCD;
    private static Buff fieldCE;
    public static MyVector luyenngoc = new MyVector();
    public static String fieldAH;
    public static MyVector fieldAI;
    public static MyVector fieldAJ;
    private static long fieldCK;
    private static long fieldCL;
    public static short[] fieldAK;
    public static short[] dell;
    public static short[] throwne = new short[120];

    public static int fieldAM;
    public static int fieldAN;
    public static int fieldAO;
    public static int fieldAP;
    public static boolean fieldAQ;
    public static boolean fieldAR;
    public static int fieldAS;
    public static MyVector fieldAT;
    public static MyVector fieldAU;
    public static boolean fieldAV;
    public static int fieldAW;
    public static int[] fieldAX;
    public static boolean fieldAY;
    public static int speedGame;
    private static long fieldCM;
    private static MyVector fieldCN;
    private static MyVector fieldCO;
    private static long fieldCP;
    public static MyVector fieldBA;
    public static MyVector fieldBB;
    public static long fieldBC;
    public static long fieldBD;
    public static boolean fieldBE;
    public static boolean fieldBF;
    public static boolean fieldBG;
    public static int fieldBH;
    public static boolean fieldBI;
    public static int fieldBJ;
    public static boolean fieldBK;
    public static int fieldBL;
    public static boolean fieldBM;
    public static int fieldBN;
    public static boolean fieldBO;
    public static String fieldBP;
    public static int fieldBQ;
    public static int fieldBR;
    public static int fieldBS;
    public static int fieldBT;
    private static String[] fieldCQ;
    public static int fieldBU;
    public static int fieldBV;
    public static int fieldBW;
    public static int fieldBX;
    public static int fieldBY;
    public static long fieldBZ;
    private static boolean fieldCR;
    private static long fieldCS = 0L;
    private static final Random fieldCT = new Random();
    private static long fieldCU = 0L;

    private static void fieldAR() {
        fieldAA = new Code();
        fieldCA = false;
        fieldCC = new TanSat();
        fieldAC = new Stanima();
        fieldAD = new AutoNvhn();
        fieldAE = new TaThu();
        fieldCD = new TuDanh();
        fieldCE = new Buff();

        fieldCK = 0L;
        fieldCL = 0L;
        fieldAK = new short[120];
        dell = new short[120];
        fieldAM = 150;
        fieldAN = -1;
        fieldAO = -1;
        fieldAP = -1;
        fieldAQ = false;
        fieldAR = false;
        fieldAT = new MyVector();
        fieldAU = new MyVector();
        fieldAV = false;
        fieldAX = new int[0];
        speedGame = 30;
        fieldCM = 0L;
        fieldCN = new MyVector();
        fieldCO = new MyVector();
        fieldCP = 0L;
        fieldBA = new MyVector();
        fieldBB = new MyVector();

        int var0;
        for (var0 = 0; var0 < fieldAK.length; ++var0) {
            fieldAK[var0] = -1;
        }

        for (int varDel = 0; varDel < dell.length; ++varDel) {
            dell[varDel] = -1;
        }
        for (int varThrow = 0; varThrow < throwne.length; ++varThrow) {
            throwne[varThrow] = -1;
        }
        fieldAH = null;
        fieldAI = new MyVector();
        fieldAJ = new MyVector();

        try {
            byte[] var11 = RMS.gameAA("V6Group");
            if (var11 == null || var11.length == 0) {
                throw new IOException("V6Group RMS empty");
            }
            ByteArrayInputStream var7 = new ByteArrayInputStream(var11);
            DataInputStream var1;
            if ((fieldAH = (var1 = new DataInputStream(var7)).readUTF()).equals("")) {
                fieldAH = null;
            }

            byte var2 = var1.readByte();

            int var3;
            for (var3 = 0; var3 < var2; ++var3) {
                fieldAI.addElement(var1.readUTF());
            }

            int var10 = var1.readInt();

            for (var3 = 0; var3 < var10; ++var3) {
                fieldAJ.addElement(var1.readUTF());
            }

            var1.close();
            var7.close();
        } catch (Exception var6) {
            fieldAH = null;
            fieldAI.removeAllElements();
            fieldAJ.removeAllElements();
        }

        try {
            fieldCQ = fieldAC(fieldAK("text.txt"), "\n");
        } catch (Exception var4) {
            var4.printStackTrace();
            fieldCQ = new String[0];
        }

        fieldBC = 0L;
        fieldBD = 0L;
        fieldBE = false;
        fieldBF = false;
        fieldBG = false;
        fieldBH = 5;
        fieldBI = false;
        fieldBJ = 100;
        fieldBK = false;
        fieldBL = 100;
        fieldBM = false;
        fieldBN = 100;
        fieldBO = false;
        fieldBU = 10;
        fieldBV = 15;
        fieldBW = -1;
        fieldBX = -1;
        fieldBY = -1;
        fieldBZ = 50L;
        String var9;
        if ((var9 = RMS.loadRMSString("chipAutopk")) != null) {
            String[] var8 = fieldAC(var9, ";");

            try {
                fieldBU = Integer.parseInt(var8[0]);
                fieldBV = Integer.parseInt(var8[1]);
                fieldBW = Integer.parseInt(var8[2]);
                fieldBX = Integer.parseInt(var8[3]);
                fieldBY = Integer.parseInt(var8[4]);
                fieldBZ = Long.parseLong(var8[5]);
                return;
            } catch (NumberFormatException var5) {
            }
        }

    }

    public final void fieldAA() {
        if (!fieldCA) {
            if (fieldAB != null) {
                fieldAB.fieldAE();
            }

            fieldCM = System.currentTimeMillis();
            fieldCA = true;
            (fieldCB = new Thread(this)).start();
        }

    }

    public static void fieldAB() {
        fieldCA = false;
        if (fieldCB != null) {
            LockGame.fieldBK();
            fieldCB.interrupt();
        }

    }

    public static void fieldAA(Auto var0) {
        var0.fieldAJ = fieldAB;
        fieldAB = var0;
    }

    public static void fieldAC() {
        LockGame.fieldBK();
        fieldAB = fieldAB.fieldAJ;
    }

    public static void fieldAA(int var0, int var1) {
        fieldCC.fieldAA(var0, var1, Char.fieldEX ? -1 : TileMap.zoneID);
        fieldAA((Auto) fieldCC);
    }

    private static void fieldAC(int var0, int var1) {
        fieldAC.fieldAA(var0, var1, Char.fieldEX ? -1 : TileMap.zoneID, false, false);
        fieldAA((Auto) fieldAC);
    }

    private static void fieldAA(boolean var0, boolean var1) {
        fieldAC.fieldAA(-1, TileMap.mapID, TileMap.zoneID, var0, var1);
        fieldAC.fieldAA = true;
        fieldAA((Auto) fieldAC);
    }

    public static void fieldAD() {
        fieldAD.fieldAD();
        fieldAA((Auto) fieldAD);
    }

    public static void fieldAE() {
        fieldAE.fieldAD();
        fieldAA((Auto) fieldAE);
    }

    private static void fieldAS() {
        fieldCD.fieldAD();
        fieldAA((Auto) fieldCD);
    }

    private static void fieldAB(boolean var0, boolean var1) {
        fieldCE.fieldAA(TileMap.mapID, TileMap.zoneID, var0, var1);
        fieldAA((Auto) fieldCE);
    }

    public static void fieldAG() {
        LockGame.fieldBK();
        fieldAB = null;
    }

    public static MyVector fieldAH() {
        MyVector var0 = new MyVector();

        for (int var1 = 0; var1 < fieldAJ.size(); ++var1) {
            var0.addElement(var1 + ". " + (String) fieldAJ.elementAt(var1));
        }

        return var0;
    }

    private static void fieldAI(String var0) {
        if (!fieldAJ.contains(var0)) {
            fieldAJ.addElement(var0);
            fieldAI();
        }

    }

    private static void fieldAJ(String var0) {
        if (fieldAJ.contains(var0)) {
            fieldAJ.removeElement(var0);
            fieldAI();
        }

    }

    private static void fieldAV() {
        fieldAJ.removeAllElements();
        fieldAI();
    }

    public static boolean fieldAA(String var0) {
        return fieldAJ.contains(var0);
    }

    public static void fieldAI() {
        ByteArrayOutputStream var0 = new ByteArrayOutputStream();
        DataOutputStream var1 = new DataOutputStream(var0);

        try {
            var1.writeUTF(fieldAH == null ? "" : fieldAH);
            var1.writeByte(fieldAI.size());

            int var2;
            for (var2 = 0; var2 < fieldAI.size(); ++var2) {
                var1.writeUTF((String) fieldAI.elementAt(var2));
            }

            var1.writeInt(fieldAJ.size());

            for (var2 = 0; var2 < fieldAJ.size(); ++var2) {
                var1.writeUTF((String) fieldAJ.elementAt(var2));
            }

            var1.flush();
            var0.flush();
            RMS.gameAA("V6Group", var0.toByteArray());
        } catch (Exception var3) {
        }
    }

    public static void fieldAA(byte var0, byte[] var1) {
        if (fieldAB != null) {
            try {
                fieldAB.fieldAA(var0);
                fieldAB.fieldAA(var1);
                fieldAB.fieldAG();
                return;
            } catch (Exception var2) {
            }
        }

    }

    public static boolean fieldAB(String var0) {
        if (fieldAH != null && !fieldAD(var0)) {
            String var1;
            if ((var1 = Char.getMyChar().cName).equals(fieldAH)) {
                if (fieldAC(var0)) {
                    return true;
                }
            } else if (GameScr.vParty.size() > 1 && var1.equals(((Party) GameScr.vParty.firstElement()).name) && var0.equals(fieldAH)) {
                return true;
            }

            return false;
        } else {
            return false;
        }
    }

    public static boolean fieldAC(String var0) {
        for (int var1 = 0; var1 < fieldAI.size(); ++var1) {
            if (var0.equals(fieldAI.elementAt(var1))) {
                return true;
            }
        }

        return false;
    }

    public static boolean fieldAD(String var0) {
        if (var0.equals(Char.getMyChar().cName)) {
            return true;
        } else {
            for (int var1 = 0; var1 < GameScr.vParty.size(); ++var1) {
                if (((Party) GameScr.vParty.elementAt(var1)).name.equals(var0)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean fieldAJ() {
        for (int var0 = 0; var0 < Char.fieldFT - 1; ++var0) {
            if (Char.fieldAK(var0) >= 4) {
                return true;
            }
        }

        return false;
    }

    private static int fieldAG(int var0) {
        int var1 = 0;
        Char var2 = Char.getMyChar();

        for (int var3 = 0; var3 < var2.arrItemBag.length; ++var3) {
            Item var4;
            if ((var4 = var2.arrItemBag[var3]) != null && var4.template.type == 18 && var4.template.level == var0) {
                ++var1;
            }
        }

        return var1;
    }

    public final void run() {
        try {
            long var1;
            for (; fieldCA; Auto.fieldAA((var1 = System.currentTimeMillis() - var1) < 100L ? 100L - var1 : 0L)) {
                var1 = System.currentTimeMillis();

                try {
                    Char var3 = Char.getMyChar();
                    int var4 = Char.fieldBF();
                    int var5;
                    int var6;
                    int var24;
                    if (fieldAB != null) {

                        if (fieldAH != null && System.currentTimeMillis() - fieldCK > 5000L) {
                            if (fieldAH.equals(var3.cName)) {
                                if (!Auto.fieldAK && GameScr.vParty.size() <= 0) {
                                    Service.gI().createParty();
                                }
                            } else if (GameScr.fieldAA(fieldAH) != null && GameScr.vParty.size() == 0) {
                                Service.gI().pleaseInputParty(fieldAH);
                            }

                            fieldCK = System.currentTimeMillis();
                        }

                        if (fieldBD > 0L) {
                            long var9;
                            if ((var9 = System.currentTimeMillis()) - fieldBC >= fieldBD) {
                                fieldBD = 0L;
                                LockGame.fieldBK();
                                fieldAB = null;
                                Session_ME.instance.gameAD();
                                Controller.gI().fieldAD();
                                return;
                            }

                            fieldBD -= var9 - fieldBC;
                            fieldBC = var9;
                        }

                        CodePhu.fieldAB();
                        CodePhu.fieldAD();
                        fieldAB.fieldAA();
                        if (var3.isHuman == Auto.fieldAK && (var3.myskill == null || var3.myskill.template.id != Auto.fieldAL.template.id)) {
                            var3.myskill = Auto.fieldAL;
                        }

                        if (Char.fieldEZ && Auto.fieldAM) {
                            Auto.fieldAM = false;
                            if (!(fieldAB instanceof TaThu) && !(fieldAB instanceof PkBoss) && !TileMap.isLangCo(TileMap.mapID)) {
                                Auto.fieldAH();
                            }
                        }

                        int var10;
                        boolean var22;
                        if (var3.statusMe != 14 && var3.statusMe != 5 && var3.cHP > 0) {
                            if (Char.isAMP && System.currentTimeMillis() - fieldCL > 500L && Char.getMyChar().cMP < Char.getMyChar().cMaxMP * Char.aMpValue / 100) {
                                Char.getMyChar().gameAE(17);
                                fieldCL = System.currentTimeMillis();
                            }

                            if (Char.isAHP && System.currentTimeMillis() - var3.lastUseHP > 2000L && Char.getMyChar().cHP < Char.getMyChar().cMaxHP * Char.aHpValue / 100) {
                                var22 = false;
                                var5 = (int) (System.currentTimeMillis() / 1000L);

                                for (var10 = 0; var10 < Char.getMyChar().vEff.size(); ++var10) {
                                    Effect var7;
                                    if ((var7 = (Effect) Char.getMyChar().vEff.elementAt(var10)).template.id == 21 && var7.timeLenght - (var5 - var7.timeStart) >= 2) {
                                        var22 = true;
                                        break;
                                    }
                                }

                                if (!var22) {
                                    Char.getMyChar().gameAE(16);
                                    var3.lastUseHP = System.currentTimeMillis();
                                }
                            }
                        }

                        if (var3.sPoint > 0 && (Char.fieldFH || fieldAB instanceof As20) && Auto.fieldAL != null && Auto.fieldAL.point < Auto.fieldAL.template.maxPoint) {
                            SkillTemplate var18 = Auto.fieldAL.template;
                            var5 = 0;

                            for (var10 = Auto.fieldAL.point + 1; var10 <= var18.maxPoint && var18.skills[var10].level <= var3.clevel && var5 < var3.sPoint; ++var10) {
                                ++var5;
                            }

                            if (var5 > 0) {
                                GameScr.fieldAC("Cộng skill " + var18.name + " " + var5 + " điểm");
                                Service.gI().upSkill(var18.id, var5);
                                if (LockGame.fieldAU()) {
                                    Session_ME.gI().fieldAD();
                                }
                            }
                        }

                        if (var3.pPoint > 0 && (Char.fieldFG || fieldAB instanceof As20)) {
                            var6 = var3.gameAC() ? 3 : 0;
                            if (var3.pPoint >= 100) {
                                GameScr.fieldAC("Cộng tiềm năng ");
                                Service.gI().upPotential(2, 40);
                                Service.gI().upPotential(var6, 60);
                            } else {
                                GameScr.fieldAC("Cộng tiềm năng ");
                                Service.gI().upPotential(var6, var3.pPoint);
                            }

                            LockGame.fieldAW();
                        }

                        Item var20;
                        if (CodePhu.fieldAH) {
                            for (var6 = 0; var6 < var3.arrItemBag.length; ++var6) {
                                if (fieldAD(var20 = var3.arrItemBag[var6])) {
                                    var20.fieldAU = System.currentTimeMillis();
                                    Service.gI().saleItem1(var20.indexUI, 1);
                                }
                            }

                            for (int varThrow = 0; varThrow < var3.arrItemBag.length; ++varThrow) {
                                if (ThrowAD(var20 = var3.arrItemBag[varThrow])) {
                                    var20.fieldAU = System.currentTimeMillis();
                                    Service.gI().throwItem(var20.indexUI);
                                }
                            }
                        }
                        TileMap.fieldBZ[138] = new short[]{(short) TanSat.mapid};

                        int var12;
                        if (fieldCO.size() > 0) {
                            int[] var17 = new int[]{150000, 247500, 408375, 673819, 1111801, 2056832, 4010822, 7420021, 12243035};
                            byte[] var8 = new byte[]{3, 5, 9, 4, 7, 10, 5, 7, 9};

                            for (var10 = 0; var10 < fieldCO.size(); ++var10) {
                                var5 = (var20 = (Item) fieldCO.elementAt(var10)).fieldAU();
                                if (var20.fieldAW) {
                                    if (System.currentTimeMillis() - var20.fieldAY > 1L || var20.fieldAX < var5) {
                                        var20.fieldAW = false;
                                    }
                                } else if (var5 >= 0 && var5 < 9) {
                                    MyVector var11 = Char.fieldAH(var5 < 3 ? 455 : (var5 < 6 ? 456 : 457));
                                    var12 = var17[var5];
                                    byte var13 = var8[var5];
                                    if (var3.yen >= var12 && var11.size() >= var13) {
                                        Item[] var23 = new Item[24];

                                        for (var12 = 0; var12 < var13; ++var12) {
                                            Item var14 = (Item) var11.elementAt(var11.size() - 1);
                                            var23[var12] = var14;
                                            var3.arrItemBag[var14.indexUI] = null;
                                            var11.removeElementAt(var11.size() - 1);
                                        }

                                        Service.gI().tinhluyen1(var20, var23);
                                        var20.fieldAW = true;
                                        var20.fieldAX = var5;
                                        var20.fieldAY = System.currentTimeMillis();
                                    }
                                } else {
                                    fieldCO.removeElementAt(var10--);
                                }
                            }
                        }

                        MyVector var19;
                        Item[] var21;
                        if (Char.fieldEU && var4 > 0) {
                            var19 = Char.fieldAH(455);

                            while (var19.size() >= 9) {
                                var21 = new Item[24];

                                for (var10 = 0; var10 < 9; ++var10) {
                                    var20 = (Item) var19.elementAt(var19.size() - 1);
                                    var21[var10] = var20;
                                    var3.arrItemBag[var20.indexUI] = null;
                                    var19.removeElementAt(var19.size() - 1);
                                }

                                Service.gI().luyenthach1(var21);
                            }

                            var4 = Char.fieldBF();
                        }

                        if (Char.fieldEV && var4 > 0) {
                            var19 = Char.fieldAH(456);

                            while (var19.size() >= 9) {
                                var21 = new Item[24];

                                for (var10 = 0; var10 < 9; ++var10) {
                                    var20 = (Item) var19.elementAt(var19.size() - 1);
                                    var21[var10] = var20;
                                    var3.arrItemBag[var20.indexUI] = null;
                                    var19.removeElementAt(var19.size() - 1);
                                }

                                Service.gI().luyenthach1(var21);
                            }

                            var4 = Char.fieldBF();
                        }

                        if (System.currentTimeMillis() - fieldCP > 2000L) {
                            for (var6 = 0; var6 < fieldBA.size(); ++var6) {
                                var5 = ((Integer) fieldBA.elementAt(var6)).intValue();
                                if ((var10 = ((Integer) fieldBB.elementAt(var6)).intValue()) < 5000) {
                                    fieldBA.removeElementAt(var6);
                                    fieldBB.removeElementAt(var6);
                                    --var6;
                                } else if ((var20 = Char.fieldAG(var5)) != null) {
                                    Service.gI().sendToSaleItem(var20, var10);
                                }
                            }

                            var4 = Char.fieldBF();
                            fieldCP = System.currentTimeMillis();
                        }

                        if (TileMap.mapID != 138 && TileMap.isLangCo(TileMap.mapID) && (!Char.fieldAJ(35) && !Char.fieldAJ(37) || Char.isAFood && Char.fieldEY && Char.aFoodValue <= 50 && var4 > 1 && fieldAG(Char.aFoodValue) == 0)) {
                            TileMap.fieldAJ(0);
                            TileMap.fieldAF();
                        }

                        if (TileMap.isLang(TileMap.mapID) || TileMap.isTruong(TileMap.mapID)) {
                            if ((Char.fieldEY || fieldAB instanceof As10) && var4 > 1 && var3.ctaskId > 3 && (var10 = fieldAB instanceof As10 ? (var3.ctaskId >= 9 ? 10 : 1) : Char.aFoodValue) <= 50 && fieldAG(var10) == 0) {
                                var5 = 2;

                                for (var6 = 0; var6 < var3.vEff.size(); ++var6) {
                                    if (((Effect) var3.vEff.elementAt(var6)).template.type == 0) {
                                        --var5;
                                        break;
                                    }
                                }

                                GameScr.fieldAB(4, 0, 0);
                                if (var10 == 50) {
                                    Service.gI().buyItem1(9, 7, var5);
                                } else {
                                    Service.gI().buyItem1(9, var10 / 10, var5);
                                }

                                LockGame.fieldAG();
                            }

                            if (TileMap.mapID == 138 && var4 > 1 && !Char.fieldAJ(35) && !Char.fieldAJ(37)) {
                                GameScr.fieldAB(4, 0, 0);
                                Service.gI().buyItem1(9, 6, 1);
                                LockGame.fieldAG();
                                ++var4;
                            }

                            if (var4 < 10 && !(fieldAB instanceof As10) && Char.fieldEM && var3.ctaskId > 9 && var4 > 0 && fieldAJ()) {
                                if (var22 = TileMap.isTruong(TileMap.mapID)) {
                                    if ((var20 = Char.fieldAF(37)) == null && (var20 = Char.fieldAF(35)) == null) {
                                        GameScr.fieldAB(4, 0, 0);
                                        Service.gI().buyItem1(9, 6, 1);
                                        LockGame.fieldAG();
                                        Auto.fieldAA(100L);
                                        var20 = Char.fieldAF(35);
                                    }

                                    if (var20 != null) {
                                        Service.gI().useItemChangeMap(var20.indexUI, 5);
                                        TileMap.fieldAF();
                                    }
                                }

                                if (TileMap.isLang(TileMap.mapID)) {
                                    GameScr.fieldAB(6, 1, 1);
                                    LockGame.fieldAQ();
                                    Vector var26 = new Vector();

                                    label439:
                                    for (var10 = 0; var10 < Char.fieldFT - 1; ++var10) {
                                        var26.removeAllElements();

                                        for (var24 = 0; var24 < var3.arrItemBag.length; ++var24) {
                                            Item var28;
                                            if ((var28 = var3.arrItemBag[var24]) != null && var28.template.id == var10) {
                                                var26.addElement(var28);
                                            }
                                        }

                                        while (var26.size() >= 4) {
                                            var24 = 1;

                                            for (var5 = var10; var5 < Char.fieldFT - 1 && GameScr.coinUpCrystals[var5] <= var3.yen && var24 << 2 <= var26.size() && var24 < 16; ++var5) {
                                                var24 <<= 2;
                                            }

                                            if (var24 == 1) {
                                                break label439;
                                            }

                                            GameScr.arrItemUpPeal = new Item[24];

                                            for (var12 = 0; var12 < var24; ++var12) {
                                                Item var29 = (Item) var26.elementAt(0);
                                                GameScr.arrItemUpPeal[var12] = var29;
                                                var3.arrItemBag[var29.indexUI] = null;
                                                var26.removeElementAt(0);
                                            }

                                            Service.gI().crystalCollectLock1(GameScr.arrItemUpPeal);
                                            LockGame.fieldAA();
                                            if (GameScr.arrItemUpPeal[0] != null) {
                                                var3.arrItemBag[GameScr.arrItemUpPeal[0].indexUI] = GameScr.arrItemUpPeal[0];
                                            }
                                        }
                                    }

                                    GameCanvas.gameAJ();
                                }

                                if (Char.getMyChar().arrItemBox == null) {
                                    Service.gI().requestItem(4);
                                    LockGame.fieldAS();
                                }

                                GameScr.fieldAB(5, 0, 0);
                                var5 = 0;

                                for (var10 = Char.fieldBG(); var5 < var3.arrItemBag.length; ++var5) {
                                    if ((var20 = var3.arrItemBag[var5]) != null && var20.template.id == Char.fieldFT - 1 && var10 > 0) {
                                        Service.gI().itemBagToBox(var20.indexUI);
                                        --var10;
                                    }
                                }

                                if (var22) {
                                    Auto.fieldAH();
                                }

                                var4 = Char.fieldBF();
                                Service.gI().bagSort();
                                LockGame.fieldAS();
                                Thread.sleep(1000L);
                                Controller.gI().fieldAD();
                            }
                        }
                    }

                    if (fieldAQ) {
                        var6 = 100;
                        ItemMap var25 = null;
                        var24 = 0;

                        while (true) {
                            if (var24 >= GameScr.vItemMap.size()) {
                                if (var25 != null) {
                                    Service.gI().pickItem(var25.itemMapID);
                                    Auto.fieldAA(50L);
                                }
                                break;
                            }

                            ItemMap var30 = (ItemMap) GameScr.vItemMap.elementAt(var24);
                            var5 = Res.gameAA(var3.cx, var3.cy, var30.xEnd, var30.yEnd);
                            if ((var6 == -1 || var5 < var6) && (fieldAA(var30.template) || var3.nClass.classId == 1 && var30.template.id == 218) && (var4 > 2 || var30.template.type == 19 || var30.template.isUpToUp && Char.fieldAJ(var30.template.id))) {
                                var6 = var5;
                                var25 = var30;
                            }

                            ++var24;
                        }
                    }

                    if (fieldAY && var3.gameBE * 100L / GameScr.exps[var3.clevel] >= 95L) {
                        LockGame.fieldBK();
                        fieldAB = null;
                        Session_ME.gI().gameAC();
                        GameCanvas.instance.gameAN();
                    }
//                    if (luyenngoc.size() > 0) {
//                        for (int i = 0; i < luyenngoc.size(); ++i) {
//                            GameScr.gI().gameBH();
//                             Service.gI().buyItem1(14, 107, 2);
//
//                            GameScr.gI().gameAD(46);
//
//                            GameScr.itemSplit = (Item) luyenngoc.elementAt(i);
//                            if (Char.fieldAH(652).size() > 0
//                                    || Char.fieldAH(653).size() > 0
//                                    || Char.fieldAH(654).size() > 0
//                                    || Char.fieldAH(655).size() > 0) {
//                                GameScr.arrItemSplit = new Item[1];
//                                int i1 = 0;
//                                for (int j = 0; j < Char.getMyChar().arrItemBag.length; ++j) {
//                                    if (i1 >= 1
//                                            || Char.getMyChar().arrItemBag[j] == null
//                                            || Char.getMyChar().arrItemBag[j].upgrade >= 2
//                                            || Char.getMyChar().arrItemBag[j].upgrade != 1
//                                            || Char.getMyChar().arrItemBag[j].isLock
//                                            || Char.getMyChar().arrItemBag[j].template.id != 652
//                                            && Char.getMyChar().arrItemBag[j].template.id != 653
//                                            && Char.getMyChar().arrItemBag[j].template.id != 654
//                                            && Char.getMyChar().arrItemBag[j].template.id != 655) {
//                                        continue;
//                                    }
//                                    GameScr.arrItemSplit[i1++] = Char.getMyChar().arrItemBag[j];
//                                    Char.getMyChar().arrItemBag[Char.getMyChar().arrItemBag[j].indexUI] = null;
//                                }
//                                cuong.sleep(600L);
//                                Service.gI().ngockham((byte) 1, null, GameScr.itemSplit, GameScr.arrItemSplit);
//
//                                cuong.sleep(700L);
//
//                                continue;
//                            }
//                            luyenngoc.removeElementAt(i--);
//                        }
//                    }
                    if (luyenngoc.size() > 0) {
                        for (int i = 0; i < luyenngoc.size(); ++i) {
                            GameScr.gI().gameBH();
                            // Service.gI().gameAB(14, 56, 28);

                            GameScr.gI().gameAD(46);

                            GameScr.itemSplit = (Item) luyenngoc.elementAt(i);
                            if (Char.fieldAH(652).size() > 2
                                    || Char.fieldAH(653).size() > 2
                                    || Char.fieldAH(654).size() > 2
                                    || Char.fieldAH(655).size() > 2) {
                                GameScr.arrItemSplit = new Item[24];
                                int i1 = 0;
                                for (int j = 0; j < Char.getMyChar().arrItemBag.length; ++j) {
                                    if (i1 >= 24
                                            || Char.getMyChar().arrItemBag[j] == null
                                            || Char.getMyChar().arrItemBag[j].upgrade >= 2
                                            || Char.getMyChar().arrItemBag[j].upgrade != 1
                                            || Char.getMyChar().arrItemBag[j].isLock
                                            || Char.getMyChar().arrItemBag[j].template.id != 652
                                            && Char.getMyChar().arrItemBag[j].template.id != 653
                                            && Char.getMyChar().arrItemBag[j].template.id != 654
                                            && Char.getMyChar().arrItemBag[j].template.id != 655) {
                                        continue;
                                    }
                                    GameScr.arrItemSplit[i1++] = Char.getMyChar().arrItemBag[j];
                                    Char.getMyChar().arrItemBag[Char.getMyChar().arrItemBag[j].indexUI] = null;
                                }
                                cuong.sleep(600L);
                                Service.gI().ngockham((byte) 1, null, GameScr.itemSplit, GameScr.arrItemSplit);

                                cuong.sleep(700L);

                                continue;
                            }
                            luyenngoc.removeElementAt(i--);
                        }
                    }
                    if (System.currentTimeMillis() - fieldCM > 1000L) {
                        label353:
                        for (var6 = 0; var6 < fieldCN.size(); ++var6) {
                            ItemTemplate var27 = ItemTemplates.gameAA((short) (var5 = ((Integer) fieldCN.elementAt(var6)).intValue()));
                            if (Char.fieldAJ(var5)) {
                                for (var24 = 0; var24 < var3.vEff.size(); ++var24) {
                                    Effect var31;
                                    if ((var31 = (Effect) var3.vEff.elementAt(var24)) != null && var31.template.iconId == var27.iconID) {
                                        continue label353;
                                    }
                                }

                                if ((var24 = Char.fieldAI(var5)) >= 0) {
                                    Service.gI().useItem(var24);
                                    continue;
                                }
                            }

                            fieldCN.removeElementAt(var6);
                            --var6;
                        }

                        fieldCM = System.currentTimeMillis();
                    }
                } catch (Exception var15) {
                }

                if (Char.getMyChar().isCaptcha) {
                    LockGame.fieldAI();
                }
            }

        } catch (Exception var16) {
        }
    }

    public static boolean gameAF(Item item) {
        return luyenngoc.contains(item);
    }

    public static void gameAG(Item item) {
        if (!luyenngoc.contains(item)) {
            luyenngoc.addElement(item);
        }
    }

    public static void gameAH(Item item) {
        luyenngoc.removeElement(item);
    }

    public static boolean fieldAA(int var0) {
        return fieldCN.contains(new Integer(var0));
    }

    public static void fieldAB(int var0) {
        Integer var1 = new Integer(var0);
        if (!fieldCN.contains(var1)) {
            fieldCN.addElement(var1);
        }

    }

    public static void fieldAC(int var0) {
        fieldCN.removeElement(new Integer(var0));
    }

    public static boolean fieldAA(Item var0) {
        return fieldCO.contains(var0);
    }

    public static void fieldAB(Item var0) {
        if (!fieldCO.contains(var0)) {
            fieldCO.addElement(var0);
        }

    }

    public static void fieldAC(Item var0) {
        fieldCO.removeElement(var0);
    }

    public static boolean fieldAD(int var0) {
        return fieldBA.contains(new Integer(var0));
    }

    public static int fieldAE(int var0) {
        return (var0 = fieldBA.indexOf(new Integer(var0))) >= 0 ? ((Integer) fieldBB.elementAt(var0)).intValue() : 0;
    }

    public static void fieldAB(int var0, int var1) {
        Integer var2 = new Integer(var0);
        if (!fieldBA.contains(var2)) {
            fieldBA.addElement(var2);
            fieldBB.addElement(new Integer(var1));
        }

    }

    public static void fieldAF(int var0) {
        if ((var0 = fieldBA.indexOf(new Integer(var0))) >= 0) {
            fieldBA.removeElementAt(var0);
            fieldBB.removeElementAt(var0);
        }

    }

    public static MyVector fieldAK() {
        MyVector var0 = new MyVector();

        for (int var1 = 0; var1 < fieldBA.size(); ++var1) {
            int var2 = ((Integer) fieldBA.elementAt(var1)).intValue();
            int var3 = ((Integer) fieldBB.elementAt(var1)).intValue();
            ItemTemplate var4 = ItemTemplates.gameAA((short) var2);
            var0.addElement(var1 + ". " + var4.name + " id " + var2 + " giá " + var3);
        }

        return var0;
    }

    public static void fieldAL() {
        Char var0 = Char.getMyChar();

        for (int var1 = 0; var1 < fieldCO.size(); ++var1) {
            Item var2;
            if ((var2 = (Item) fieldCO.elementAt(var1)).indexUI >= 0 && var2.indexUI < var0.arrItemBag.length) {
                if (var0.arrItemBag[var2.indexUI] != null && var0.arrItemBag[var2.indexUI].fieldAU() >= 0 && var0.arrItemBag[var2.indexUI].fieldAU() < 9) {
                    fieldCO.setElementAt(var0.arrItemBag[var2.indexUI], var1);
                } else {
                    fieldCO.removeElementAt(var1--);
                }
            }
        }

    }

    public static String fieldAM() {
        String var0 = "";

        for (int var1 = 0; var1 < fieldAX.length; ++var1) {
            var0 = var0 + (var1 == fieldAX.length - 1 ? String.valueOf(fieldAX[var1]) : fieldAX[var1] + " ");
        }

        return var0;
    }

    public static void fieldAE(String var0) {
        String[] var4;
        int[] var1 = new int[(var4 = fieldAC(var0, " ")).length];

        for (int var2 = 0; var2 < var4.length; ++var2) {
            try {
                var1[var2] = Integer.parseInt(var4[var2]);
            } catch (Exception var3) {
                var1[var2] = -1;
            }
        }

        fieldAX = var1;
    }

    public static void fieldAA(short var0) {
        int var1;
        for (var1 = 0; var1 < fieldAK.length; ++var1) {
            if (fieldAK[var1] == var0) {
                return;
            }
        }

        var1 = -1;

        for (int var2 = 0; var2 < fieldAK.length; ++var2) {
            if (fieldAK[var2] < 0) {
                var1 = var2;
                break;
            }
        }

        if (var1 == -1) {
            var1 = fieldAK.length;
            short[] var4;
            System.arraycopy(var4 = new short[fieldAK.length + 10], 0, fieldAK, 0, fieldAK.length);

            for (int var3 = fieldAK.length; var3 < var4.length; ++var3) {
                var4[var3] = -1;
            }

            fieldAK = var4;
        }

        fieldAK[var1] = var0;
    }

    public static void fieldAB(short var0) {
        for (int var1 = 0; var1 < fieldAK.length; ++var1) {
            if (fieldAK[var1] == var0) {
                fieldAK[var1] = -1;
            }
        }

    }

    public static void fieldAN() {
        for (int var0 = 0; var0 < fieldAK.length; ++var0) {
            if (fieldAK[var0] > 0) {
                for (int var1 = 0; var1 <= var0; ++var1) {
                    if (fieldAK[var1] == -1) {
                        fieldAK[var1] = fieldAK[var0];
                        fieldAK[var0] = -1;
                        break;
                    }
                }
            }
        }

    }

    public static void fieldAC(short var0) {
        int var1;
        for (var1 = 0; var1 < dell.length; ++var1) {
            if (dell[var1] == var0) {
                return;
            }
        }

        var1 = -1;

        for (int var2 = 0; var2 < dell.length; ++var2) {
            if (dell[var2] < 0) {
                var1 = var2;
                break;
            }
        }

        if (var1 == -1) {
            var1 = dell.length;
            short[] var4;
            System.arraycopy(var4 = new short[dell.length + 10], 0, dell, 0, dell.length);

            for (int var3 = dell.length; var3 < var4.length; ++var3) {
                var4[var3] = -1;
            }

            dell = var4;
        }

        dell[var1] = var0;
    }

    public static void ThrowAC(short var0) {
        int var1;
        for (var1 = 0; var1 < throwne.length; ++var1) {
            if (throwne[var1] == var0) {
                return;
            }
        }

        var1 = -1;

        for (int var2 = 0; var2 < throwne.length; ++var2) {
            if (throwne[var2] < 0) {
                var1 = var2;
                break;
            }
        }

        if (var1 == -1) {
            var1 = throwne.length;
            short[] var4;
            System.arraycopy(var4 = new short[throwne.length + 10], 0, throwne, 0, throwne.length);

            for (int var3 = throwne.length; var3 < var4.length; ++var3) {
                var4[var3] = -1;
            }

            throwne = var4;
        }

        throwne[var1] = var0;
    }

    public static void fieldAD(short var0) {
        for (int var1 = 0; var1 < dell.length; ++var1) {
            if (dell[var1] == var0) {
                dell[var1] = -1;
            }
        }

    }

    public static void ThrowAD(short var0) {
        for (int var1 = 0; var1 < throwne.length; ++var1) {
            if (throwne[var1] == var0) {
                throwne[var1] = -1;
            }
        }

    }

    public static void ThrowAO() {
        for (int var0 = 0; var0 < throwne.length; ++var0) {
            if (throwne[var0] > 0) {
                for (int var1 = 0; var1 <= var0; ++var1) {
                    if (throwne[var1] == -1) {
                        throwne[var1] = throwne[var0];
                        throwne[var0] = -1;
                        break;
                    }
                }
            }
        }

    }

    public static void fieldAO() {
        for (int var0 = 0; var0 < dell.length; ++var0) {
            if (dell[var0] > 0) {
                for (int var1 = 0; var1 <= var0; ++var1) {
                    if (dell[var1] == -1) {
                        dell[var1] = dell[var0];
                        dell[var0] = -1;
                        break;
                    }
                }
            }
        }

    }

    public static boolean fieldAA(ItemTemplate var0) {
        if (fieldAB instanceof As20) {
            if (var0.type == 19) {
                return true;
            } else if ((var0.type == 16 || var0.type == 17) && var0.level == 10) {
                return true;
            } else {
                Char var3 = Char.getMyChar();
                if (Char.fieldBF() <= 6) {
                    return false;
                } else if ((var3.ctaskId < 13 || var3.ctaskId == 13 && var3.arrItemBody[1] != null && var3.arrItemBody[1].upgrade < 2) && var0.type == 26 && var0.id > 0) {
                    return true;
                } else {
                    int var2 = var3.cgender == 1 ? 124 : 125;
                    return var3.ctaskId <= 12 && (var0.id == 174 && !Char.fieldAJ(174) || var0.id == var2 && !Char.fieldAJ(var2));
                }
            }
        } else if (fieldAB instanceof As10) {
            return var0.type == 19;
        } else if (var0.type == 19) {
            return Char.isAPickYen;
        } else if (var0.type != 16 && var0.type != 17) {
            if (var0.type == 26) {
                return Char.isAPickYHMS && var0.id >= Char.fieldFS - 1;
            } else if (var0.fieldAA()) {
                return (Char.fieldEN || fieldAB instanceof Stanima) && var0.level >= Char.fieldFU;
            } else if (var0.fieldAB()) {
                return Char.fieldEP;
            } else {
                if (var0.type == 27) {
                    if (var0.description.startsWith("Vật phẩm sự kiện") || var0.description.startsWith("Vật phẩm Sự kiện")) {
                        return Char.fieldEQ;
                    }

                    if (var0.name.startsWith("Sách võ công")) {
                        return Char.fieldES;
                    }

                    if (TileMap.isLangCo(TileMap.mapID) && var0.id == 38) {
                        return false;
                    }
                }

                for (int var1 = 0; var1 < fieldAK.length; ++var1) {
                    if (fieldAK[var1] > 0 && var0.id == fieldAK[var1]) {
                        return true;
                    }
                }

                return Char.fieldER;
            }
        } else {
            return Char.isAPickYHM && var0.level >= Char.fieldFR;
        }
    }

    public static boolean fieldAD(Item var0) {
        if (fieldAB instanceof As10) {
            return false;
        } else if (var0 == null) {
            return false;
        } else if (var0.upgrade > 0) {
            var0.fieldAV = true;
            return false;
        } else {
            for (int var1 = 0; var1 < dell.length; ++var1) {
                if (dell[var1] > 0 && var0.template.id == dell[var1]) {
                    return true;
                }
            }

            if (!var0.fieldAV && System.currentTimeMillis() - var0.fieldAU >= 5000L) {
                if (fieldAB instanceof Stanima && !Char.fieldEN && var0.template.type < 10 && var0.template.level < 70) {
                    return true;
                } else if (var0.template.type < 10) {
                    return var0.template.level < 50 && var0.template.type != 1;
                } else if (var0.template.type == 26 && var0.template.id < (Char.isAPickYHMS ? Char.fieldFS : Char.fieldFT) - 1) {
                    return true;
                } else {
                    if (var0.template.type < 10 || var0.template.type >= 29 && var0.template.type <= 32) {
                        if (var0.template.type < 10 && !Char.fieldEN) {
                            if (var0.template.level < 50) {
                                return true;
                            }

                            return false;
                        }

                        if (!var0.fieldAS && System.currentTimeMillis() - var0.fieldAT > 5000L) {
                            var0.fieldAT = System.currentTimeMillis();
                            Service.gI().requestItemInfo(var0.typeUI, var0.indexUI);
                            if (!LockGame.fieldAS() || !var0.fieldAS) {
                                return false;
                            }
                        }

                        if (var0.fieldAB(85)) {
                            var0.fieldAV = true;
                            return false;
                        }

                        if (var0.template.type >= 29 && var0.template.type <= 32) {
                            if (var0.saleCoinLock != 0) {
                                var0.fieldAV = true;
                                return false;
                            }

                            return true;
                        }

                        if (var0.saleCoinLock == 5) {
                            return true;
                        }

                        boolean var5;
                        label190:
                        {
                            Item var4 = var0;
                            if (var0.options != null) {
                                for (int var2 = 0; var2 < var4.options.size(); ++var2) {
                                    ItemOption var3;
                                    if ((var3 = (ItemOption) var4.options.elementAt(var2)) != null && var3.optionTemplate.type == 2) {
                                        var5 = true;
                                        break label190;
                                    }
                                }
                            }

                            var5 = false;
                        }

                        if (!var5) {
                            return true;
                        }

                        if (var0.template.type == 1) {
                            if (var0.fieldAB(0) && var0.fieldAB(1)) {
                                if (!var0.fieldAB(8) && !var0.fieldAB(9)) {
                                    return true;
                                }

                                if (!var0.fieldAB(10)) {
                                    return true;
                                }

                                var0.fieldAV = true;
                                return false;
                            }

                            return true;
                        }

                        if (!var0.fieldAB(6) || !var0.fieldAB(7)) {
                            return true;
                        }

                        if (var0.fieldAC(0) < 2) {
                            return true;
                        }

                        if (var0.template.type == 8 && !var0.fieldAB(16)) {
                            return true;
                        }

                        if (Char.fieldEO) {
                            if (var0.fieldAC(0) <= 2 && var0.fieldAC(1) <= 2 && var0.fieldAC(2) <= 2 && var0.fieldAC(3) <= 1 && var0.fieldAC(4) <= 1 && var0.fieldAC(5) <= 1 && var0.fieldAC(6) <= 1 && var0.fieldAC(7) <= 1) {
                                return true;
                            }

                            var0.fieldAV = true;
                            return false;
                        }
                    }

                    var0.fieldAV = true;
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    public static boolean ThrowAD(Item var0) {
        if (var0 == null) {
            return false;
        } else {
            for (int var1 = 0; var1 < throwne.length; ++var1) {
                if (throwne[var1] > 0 && var0.template.id == throwne[var1]) {
                    return true;
                }
            }

            return false;

        }
    }

    public static void fieldAP() {
        Char var0 = Char.getMyChar();
        if (!Char.fieldAJ(37) && !Char.fieldAJ(35)) {
            Npc var1;
            if ((var1 = GameScr.fieldAI(13)) != null && Math.abs(var1.cx - var0.cx) <= 200 && Math.abs(var1.cy - var0.cy) <= 200) {
                Char.fieldAC(var1.cx > 200 ? var1.cx - 200 : var1.cx + 200, var1.cy);
            }

            Service.gI().openUIZone();
        } else {
            Char.fieldAC(var0.cx, TileMap.pxh);
        }
    }

    public final boolean fieldAF(String var1) {
        int var2 = 0;
        StringBuffer var3 = new StringBuffer();
        StringBuffer var4 = new StringBuffer();

        int var5;
        label1082:
        for (var5 = 0; var5 < var1.length(); ++var5) {
            char var6;
            if ((var6 = var1.charAt(var5)) >= '0' && var6 <= '9' || var6 == ' ') {
                while (true) {
                    if (var5 >= var1.length() || (var6 = var1.charAt(var5)) < '0' || var6 > '9') {
                        break label1082;
                    }

                    var4.append(var6);
                    ++var5;
                }
            }

            var3.append(var6);
        }

        String var33 = var3.toString().toLowerCase();
        if (var4.length() > 0) {
            try {
                var2 = Integer.parseInt(var4.toString());
            } catch (Exception var26) {
            }
        }

        if (var33.equals("s")) {
            if (var2 == 0) {
                GameScr.fieldAC("Chạy đi đou với tốc độ 0?");
            } else if (var2 > 100) {
                GameScr.fieldAC("Tốc giày nên để <= 100 để ko bị giật!");
            } else {
                GameScr.fieldAC("Fake tốc chạy " + var2);
                fieldBH = var2;
                fieldBG = true;
            }

            return true;
        } else if (var33.equals("rs")) {
            GameScr.fieldAC("Reset tốc chạy");
            fieldBG = false;
            return true;
        } else if (var33.equals("n")) {
            if (var2 == 0) {
                var2 = 100;
            }

            GameScr.fieldAC("Fake tầm ngang " + var2);
            fieldBI = true;
            fieldBJ = var2;
            return true;
        } else if (var33.equals("c")) {
            if (var2 == 0) {
                var2 = 100;
            }

            GameScr.fieldAC("Fake tầm cao " + var2);
            fieldBK = true;
            fieldBL = var2;
            return true;
        } else if (var33.equals("m")) {
            if (var2 == 0) {
                var2 = 1;
            }

            GameScr.fieldAC("Fake lan " + var2);
            fieldBM = true;
            fieldBN = var2;
            return true;
        } else if (var33.equals("rsk")) {
            GameScr.fieldAC("Reset fake tầm lan skill");
            fieldBM = false;
            fieldBI = false;
            fieldBK = false;
            return true;
        } else if (!var33.equals("bang") && !var33.equals("fz")) {
            if (!var33.equals("bangb") && !var33.equals("fb")) {
                if (!var33.equals("bangs") && !var33.equals("fs")) {
                    if (!var33.equals("pbang") && !var33.equals("wz")) {
                        if (var33.equals("u")) {
                            if (var2 == 0) {
                                var2 = 50;
                            }

                            GameScr.fieldAC("Khinh kông " + var2);
                            Char.fieldAC(Char.getMyChar().cx, Char.getMyChar().cy - var2);
                            return true;
                        } else if (var33.equals("d")) {
                            if (var2 == 0) {
                                var2 = 50;
                            }

                            GameScr.fieldAC("Độn thổ " + var2);
                            Char.fieldAC(Char.getMyChar().cx, Char.getMyChar().cy + var2);
                            return true;
                        } else if (var33.equals("l")) {
                            if (var2 == 0) {
                                var2 = 50;
                            }

                            GameScr.fieldAC("Dịch trái " + var2);
                            Char.fieldAC(Char.getMyChar().cx - var2, Char.getMyChar().cy);
                            return true;
                        } else if (var33.equals("r")) {
                            if (var2 == 0) {
                                var2 = 50;
                            }

                            GameScr.fieldAC("Dịch phải " + var2);
                            Char.fieldAC(Char.getMyChar().cx + var2, Char.getMyChar().cy);
                            return true;
                        } else {
                            Char var37;
                            if (var33.equals("g")) {
                                if ((var37 = Char.getMyChar()).charFocus != null) {
                                    GameScr.fieldAC("MoveTo " + var37.charFocus.cName);
                                    Char.fieldAC(var37.charFocus.cx, var37.charFocus.cy);
                                } else if (var37.npcFocus != null) {
                                    GameScr.fieldAC("MoveTo " + var37.npcFocus.cName);
                                    Char.fieldAC(var37.npcFocus.cx, var37.npcFocus.cy);
                                } else if (var37.mobFocus != null) {
                                    GameScr.fieldAC("MoveTo " + var37.mobFocus.getTemplate().name);
                                    Char.fieldAC(var37.mobFocus.xFirst, var37.mobFocus.yFirst);
                                } else if (var37.itemFocus != null) {
                                    GameScr.fieldAC("MoveTo " + var37.itemFocus.template.name);
                                    Char.fieldAC(var37.itemFocus.x, var37.itemFocus.y);
                                }

                                return true;
                            }
                            if (var33.equals("skin")) {
                                if (Char.getMyChar().charFocus != null) {
                                    Char.getMyChar().head = Char.getMyChar().charFocus.head;
                                    Char.getMyChar().ID_Body = Char.getMyChar().charFocus.ID_Body;
                                    Char.getMyChar().ID_PP = Char.getMyChar().charFocus.ID_PP;
                                    Char.getMyChar().ID_HAIR = Char.getMyChar().charFocus.ID_HAIR;
                                    Char.getMyChar().ID_LEG = Char.getMyChar().charFocus.ID_LEG;
                                    Char.getMyChar().ID_HORSE = Char.getMyChar().charFocus.ID_HORSE;
                                    Char.getMyChar().gameMO = Char.getMyChar().charFocus.gameMO;
                                    Char.getMyChar().gameMP = Char.getMyChar().charFocus.gameMP;
                                    Char.getMyChar().ID_MAT_NA = Char.getMyChar().charFocus.ID_MAT_NA;
                                    Char.getMyChar().ID_Bien_Hinh = Char.getMyChar().charFocus.ID_Bien_Hinh;
                                    Char.getMyChar().ID_WEA_PONE = Char.getMyChar().charFocus.ID_WEA_PONE;
                                    Char.getMyChar().ID_SUSANO = Char.getMyChar().charFocus.ID_SUSANO;
                                    Char.getMyChar().leg = Char.getMyChar().charFocus.leg;
                                    Char.getMyChar().body = Char.getMyChar().charFocus.body;

                                    Char.getMyChar().cName = Char.getMyChar().charFocus.cName;
                                    GameScr.fieldAC("Fake " + Char.getMyChar().charFocus.cName);
                                }
                                if (Char.getMyChar().npcFocus != null) {
                                    Char.getMyChar().head = (short) Char.getMyChar().npcFocus.template.headId;
                                    Char.getMyChar().leg = (short) Char.getMyChar().npcFocus.template.legId;
                                    Char.getMyChar().body = (short) Char.getMyChar().npcFocus.template.bodyId;
                                    Char.getMyChar().cName = Char.getMyChar().npcFocus.template.name;
                                    GameScr.fieldAC("Fake " + Char.getMyChar().npcFocus.template.name);
                                }
                                return true;
                            } else if (var33.equals("ta")) {
                                GameScr.gI().gameAD((int) 9);
                                return true;
                            } else if (var33.equals("sw")) {
                                GameScr.gI().gameAD((int) 36);
                                return true;
                            } else if (var33.equals("aq")) {
                                if ((var37 = Char.getMyChar()).mobFocus != null) {
                                    GameScr.vMob.removeElement(var37.mobFocus);
                                }

                                return true;
                            } else if (var33.equals("z")) {
                                GameScr.fieldAC((Char.fieldFB ? "Tắt" : "Bật") + " auto chuyển map");
                                Char.fieldFB = !Char.fieldFB;
                                return true;
                            } else if (var33.equals("rm")) {
                                GameScr.fieldAC((Char.fieldEW ? "Tắt" : "Bật") + " auto next map");
                                Char.fieldEW = !Char.fieldEW;
                                return true;
                            } else if (var33.equals("x")) {
                                if (var2 == 0) {
                                    var2 = -1;
                                }

                                GameScr.fieldAC("KC Nhặt " + var2);
                                fieldAM = var2;
                                return true;
                            } else if (var33.equals("kts")) {
                                if (var2 == 0) {
                                    var2 = -1;
                                }

                                GameScr.fieldAC("KC Tàn sát " + var2);
                                fieldAO = Char.getMyChar().cx;
                                fieldAP = Char.getMyChar().cy;
                                fieldAN = var2;
                                return true;
                            } else {
                                Mob var29;
                                if (var33.equals("ts")) {
                                    if ((var29 = Mob.fieldAB(var2)) == null) {
                                        GameScr.fieldAC("Tàn sát all");
                                        fieldAA(-1, TileMap.mapID);
                                    } else {
                                        GameScr.fieldAC("Tàn sát " + var29.getTemplate().name + " lv " + var2);
                                        fieldAA(var29.templateId, TileMap.mapID);
                                    }

                                    return true;
                                } else {
                                    MobTemplate var10000;
                                    MobTemplate var27;
                                    if (var33.equals("tsx")) {
                                        var10000 = var2 >= 0 && var2 < Mob.arrMobTemplate.length ? Mob.arrMobTemplate[var2] : null;
                                        var27 = var10000;
                                        if (var10000 == null) {
                                            GameScr.fieldAC("Tàn sát all");
                                            fieldAA(-1, TileMap.mapID);
                                        } else {
                                            GameScr.fieldAC("Tàn sát " + var27.name + " id " + var2);
                                            fieldAA(var27.mobTemplateId, TileMap.mapID);
                                        }

                                        return true;
                                    } else if (var33.equals("tsa")) {
                                        GameScr.fieldAC("Tàn sát all");
                                        fieldAA(-1, TileMap.mapID);
                                        return true;
                                    } else if (var33.equals("anv")) {
                                        if (TileMap.mapID != 1 && TileMap.mapID != 27 && TileMap.mapID != 72) {
                                            GameScr.fieldAC("Bạn phải đứng ở trường để Auto");
                                            return true;
                                        } else {
                                            GameScr.fieldAC("Auto Nhiệm Vụ Hằng Ngày");
                                            fieldAD();
                                            return true;
                                        }
                                    } else if (var33.equals("att")) {
                                        GameScr.fieldAC("Auto Tà Thú");
                                        fieldAE();
                                        return true;
                                    } else if (var33.equals("ak")) {
                                        if (fieldAB == fieldCD) {
                                            GameScr.fieldAC("Tắt tự đánh");
                                            fieldAG();
                                        } else {
                                            GameScr.fieldAC("Bật tự đánh");
                                            fieldAS();
                                        }

                                        return true;
                                    } else if (var33.equals("pk")) {
                                        fieldBO = !fieldBO;
                                        GameScr.fieldAC((fieldBO ? " Bật " : " Tắt ") + "auto pk!");
                                        return true;
                                    } else if (!var33.equals("e") && !var33.equals("pe")) {
                                        if (var33.equals("k")) {
                                            GameScr.fieldAC("Chuyển Khu: " + var2);
                                            GameScr.gI().fieldAJ(var2);
                                            return true;
                                        } else if (var33.equals("ltd")) {
                                            if (!TileMap.isTruong(TileMap.mapID) && !TileMap.isLang(TileMap.mapID)) {
                                                GameScr.fieldAC("Hãy đứng ở làng hoặc trường để lưu tọa độ");
                                            } else {
                                                GameScr.fieldAH(5);
                                                Service.gI().openMenu(5);
                                                Service.gI().menu((byte) 0, 5, 1, 0);
                                            }

                                            return true;
                                        } else if (var33.equals("nm")) {
                                            GameScr.fieldAC("Next map: " + var2);
                                            TileMap.fieldAM(var2);
                                            return true;
                                        } else if (var33.equals("gm")) {
                                            if (var2 < TileMap.mapNames.length && var2 >= 0) {
                                                GameScr.fieldAC("Go to: " + TileMap.mapNames[var2]);
                                                TileMap.fieldAL(var2);
                                                return true;
                                            } else {
                                                return true;
                                            }
                                        } else if (var33.equals("npc")) {
                                            if (var2 < Npc.arrNpcTemplate.length) {
                                                GameScr.fieldAC("Act NPC: " + Npc.arrNpcTemplate[var2].name);
                                                GameScr.fieldAH(var2);
                                            }

                                            return true;
                                        } else if (var33.equals("hs")) {
                                            GameScr.fieldAC("Next to hirosaki");
                                            TileMap.fieldAL(1);
                                            return true;
                                        } else if (var33.equals("hr")) {
                                            GameScr.fieldAC("Next to haruna");
                                            TileMap.fieldAL(27);
                                            return true;
                                        } else if (var33.equals("oz")) {
                                            GameScr.fieldAC("Next to Ozawa(Oozaka)");
                                            TileMap.fieldAL(72);
                                            return true;
                                        } else if (var33.equals("kj")) {
                                            GameScr.fieldAC("Next to Kojin");
                                            TileMap.fieldAL(10);
                                            return true;
                                        } else if (var33.equals("sz")) {
                                            GameScr.fieldAC("Next to Sanzu");
                                            TileMap.fieldAL(17);
                                            return true;
                                        } else if (var33.equals("tn")) {
                                            GameScr.fieldAC("Next to Tone");
                                            TileMap.fieldAL(22);
                                            return true;
                                        } else if (var33.equals("lc")) {
                                            GameScr.fieldAC("Next to Chài");
                                            TileMap.fieldAL(32);
                                            return true;
                                        } else if (var33.equals("ck")) {
                                            GameScr.fieldAC("Next to Chakumi");
                                            TileMap.fieldAL(38);
                                            return true;
                                        } else if (var33.equals("eg")) {
                                            GameScr.fieldAC("Next to Echigo");
                                            TileMap.fieldAL(43);
                                            return true;
                                        } else if (var33.equals("os")) {
                                            GameScr.fieldAC("Next to Oshin");
                                            TileMap.fieldAL(48);
                                            return true;
                                        } else if (var33.equals("mnv")) {
                                            GameScr.fieldAC("Next to Map Nhiệm Vụ");
                                            TileMap.fieldAL(GameScr.gameBE());
                                            return true;
                                        } else if (var33.equals("mnvp")) {
                                            GameScr.fieldAC("Next to Map Nhiệm Vụ Phụ");
                                            TaskOrder var36;
                                            if ((var36 = Char.fieldAM(0)) != null) {
                                                TileMap.fieldAL(var36.mapId);
                                            }

                                            return true;
                                        } else {
                                            ItemMap var34;
                                            if (var33.equals("add")) {
                                                GameScr.fieldAC("Thêm vật phẩm vào ds nhặt");
                                                if ((var34 = Char.getMyChar().itemFocus) != null) {
                                                    fieldAA(var34.template.id);
                                                }

                                                return true;
                                            } else if (var33.equals("del")) {
                                                GameScr.fieldAC("Xóa vật phẩm khỏi ds nhặt");
                                                if ((var34 = Char.getMyChar().itemFocus) != null) {
                                                    fieldAB(var34.template.id);
                                                }

                                                return true;
                                            } else {
                                                ItemTemplate var32;
                                                if (var33.equals("ait")) {
                                                    if ((var32 = ItemTemplates.gameAA((short) var2)) != null) {
                                                        GameScr.fieldAC("Thêm " + var32.name + " vào ds nhặt");
                                                        fieldAA(var32.id);
                                                    }

                                                    return true;
                                                } else if (var33.equals("dit")) {
                                                    if ((var32 = ItemTemplates.gameAA((short) var2)) != null) {
                                                        GameScr.fieldAC("Xóa " + var32.name + " khỏi ds nhặt");
                                                        fieldAB(var32.id);
                                                    }

                                                    return true;
                                                } else if (var33.equals("ajt")) {
                                                    if ((var32 = ItemTemplates.gameAA((short) var2)) != null) {
                                                        GameScr.fieldAC("Thêm " + var32.name + " vào ds nhặt");
                                                        fieldAC(var32.id);
                                                    }

                                                    return true;
                                                } else if (var33.equals("djt")) {
                                                    if ((var32 = ItemTemplates.gameAA((short) var2)) != null) {
                                                        GameScr.fieldAC("Xóa " + var32.name + " khỏi ds nhặt");
                                                        fieldAD(var32.id);
                                                    }

                                                    return true;
                                                } else if (var33.equals("cnhat")) {
                                                    if (fieldAQ) {
                                                        GameScr.fieldAC("Bật nhặt xa");
                                                    } else {
                                                        GameScr.fieldAC("Bật hút VP");
                                                    }

                                                    fieldAQ = !fieldAQ;
                                                    return true;
                                                } else if (var33.equals("ruong")) {
                                                    GameScr.gI().gameBR();
                                                    return true;
                                                } else if (var33.equals("vpnhat")) {
                                                    GameScr.gI().gameAD((int) 46);
                                                    return true;
                                                } else if (var33.equals("die")) {
                                                    fieldAP();
                                                    return true;
                                                } else if (var33.equals("dcvt")) {
                                                    if (fieldAR) {
                                                        GameScr.fieldAC("Tắt đánh chuyển vị trí");
                                                    } else {
                                                        GameScr.fieldAC("Bật đánh chuyển vị trí");
                                                    }

                                                    fieldAR = !fieldAR;
                                                    if (Char.fieldFI) {
                                                        Service.gI().chatParty("dcvt " + (fieldAR ? 1 : 0));
                                                    }

                                                    return true;
                                                } else if (var33.equals("avt")) {
                                                    GameScr.fieldAC("Thêm vị trí " + fieldAT.size());
                                                    fieldAT.addElement(new Integer(Char.getMyChar().cx));
                                                    fieldAU.addElement(new Integer(Char.getMyChar().cy));
                                                    if (Char.fieldFI) {
                                                        Service.gI().chatParty("avt " + Char.getMyChar().cx + " " + Char.getMyChar().cy);
                                                    }

                                                    return true;
                                                } else if (var33.equals("dvt")) {
                                                    GameScr.fieldAC("Xóa hết vị trí");
                                                    fieldAT.removeAllElements();
                                                    fieldAU.removeAllElements();
                                                    if (Char.fieldFI) {
                                                        Service.gI().chatParty("dvt");
                                                    }

                                                    return true;
                                                } else if (var33.equals("dvtx")) {
                                                    GameScr.fieldAC("Xóa vị trí " + var2);
                                                    fieldAT.removeElementAt(var2);
                                                    fieldAU.removeElementAt(var2);
                                                    if (Char.fieldFI) {
                                                        Service.gI().chatParty("dtvx " + var2);
                                                    }

                                                    return true;
                                                } else if (var33.equals("dck")) {
                                                    if (fieldAV = !fieldAV) {
                                                        GameScr.fieldAC("Tắt đánh chuyển khu");
                                                    } else {
                                                        GameScr.fieldAC("Bật đánh chuyển khu");
                                                        GameCanvas.inputDlg.gameAA("Khu", new Command("Đặt", 1100090), 1);
                                                        GameCanvas.inputDlg.tfInput.setText(fieldAM());
                                                    }

                                                    return true;
                                                } else if (var33.equals("pt")) {
                                                    if (!Char.getMyChar().cName.equals(fieldAH)) {
                                                        GameScr.fieldAC("Bạn không là nhóm trưởng");
                                                        return true;
                                                    } else {
                                                        GameScr.fieldAC("PT nhóm");

                                                        for (var5 = 0; var5 < fieldAI.size(); ++var5) {
                                                            if (!fieldAD(var1 = (String) fieldAI.elementAt(var5))) {
                                                                Service.gI().addParty(var1);
                                                            }

                                                            if (fieldAB instanceof PkBoss) {
                                                                Service.gI().chatPrivate(var1, "pkm " + fieldAB.fieldAB);
                                                                Service.gI().chatPrivate(var1, "pkk " + fieldAB.fieldAC);
                                                            } else if (fieldAB != null) {
                                                                Service.gI().chatPrivate(var1, "map " + fieldAB.fieldAB);
                                                                Service.gI().chatPrivate(var1, "khu " + fieldAB.fieldAC);
                                                            }
                                                        }

                                                        return true;
                                                    }
                                                } else if (var33.equals("sn")) {
                                                    GameScr.fieldAC("Lưu nhóm");
                                                    fieldAI();
                                                    return true;
                                                } else if (var33.equals("tsn")) {
                                                    if (GameScr.vParty.size() > 0 && ((Party) GameScr.vParty.firstElement()).charId == Char.getMyChar().charID) {
                                                        if ((var29 = Mob.fieldAB(var2)) == null) {
                                                            GameScr.fieldAC("Tàn sát nhóm all");
                                                            fieldAA(-1, TileMap.mapID);
                                                        } else {
                                                            GameScr.fieldAC("Tàn sát nhóm " + var29.getTemplate().name + " lv " + var2);
                                                            fieldAA(var29.templateId, TileMap.mapID);
                                                        }

                                                        fieldCC.fieldAA = true;
                                                        Service.gI().chatParty("ts " + fieldCC.fieldAB + " " + fieldCC.fieldAC + " " + fieldCC.fieldAV);
                                                        return true;
                                                    } else {
                                                        GameScr.fieldAC("Chưa có nhóm hoặc bạn không là nhóm trưởng");
                                                        return true;
                                                    }
                                                } else if (var33.equals("tsnx")) {
                                                    if (GameScr.vParty.size() > 0 && ((Party) GameScr.vParty.firstElement()).charId == Char.getMyChar().charID) {
                                                        var10000 = var2 >= 0 && var2 < Mob.arrMobTemplate.length ? Mob.arrMobTemplate[var2] : null;
                                                        var27 = var10000;
                                                        if (var10000 == null) {
                                                            GameScr.fieldAC("Tàn sát nhóm all");
                                                            fieldAA(-1, TileMap.mapID);
                                                        } else {
                                                            GameScr.fieldAC("Tàn sát nhóm " + var27.name + " id " + var2);
                                                            fieldAA(var27.mobTemplateId, TileMap.mapID);
                                                        }

                                                        fieldCC.fieldAA = true;
                                                        Service.gI().chatParty("ts " + fieldCC.fieldAB + " " + fieldCC.fieldAC + " " + fieldCC.fieldAV);
                                                        return true;
                                                    } else {
                                                        GameScr.fieldAC("Chưa có nhóm hoặc bạn không là nhóm trưởng");
                                                        return true;
                                                    }
                                                } else if (var33.equals("tsan")) {
                                                    if (GameScr.vParty.size() > 0 && ((Party) GameScr.vParty.firstElement()).charId == Char.getMyChar().charID) {
                                                        GameScr.fieldAC("Tàn sát nhóm all");
                                                        fieldAA(-1, TileMap.mapID);
                                                        fieldCC.fieldAA = true;
                                                        Service.gI().chatParty("tsa " + fieldCC.fieldAB + " " + fieldCC.fieldAC);
                                                        return true;
                                                    } else {
                                                        GameScr.fieldAC("Chưa có nhóm hoặc bạn không là nhóm trưởng");
                                                        return true;
                                                    }
                                                } else if (var33.equals("attn")) {
                                                    if (GameScr.vParty.size() > 0 && ((Party) GameScr.vParty.firstElement()).charId == Char.getMyChar().charID) {
                                                        GameScr.fieldAC("Auto Tà Thú Nhóm");
                                                        fieldAE();
                                                        fieldAE.fieldAA = true;
                                                        Service.gI().chatParty("att " + fieldAE.fieldAB + " " + fieldAE.fieldAC + " " + fieldAE.fieldAV);
                                                        return true;
                                                    } else {
                                                        GameScr.fieldAC("Chưa có nhóm hoặc bạn không là nhóm trưởng");
                                                        return true;
                                                    }
                                                } else if (var33.equals("buff")) {
                                                    GameScr.fieldAC("Bật Buff HS Xa");
                                                    fieldAB(true, true);
                                                    return true;
                                                } else if (var33.equals("bux")) {
                                                    GameScr.fieldAC("Bật Buff Xa");
                                                    fieldAB(true, false);
                                                    return true;
                                                } else if (var33.equals("hsx")) {
                                                    GameScr.fieldAC("Bật HS Xa");
                                                    fieldAB(false, true);
                                                    return true;
                                                } else {
                                                    int var28;
                                                    if (var33.equals("cy")) {
                                                        if (fieldAB == null) {
                                                            GameScr.fieldAC("Bạn chưa up yên");
                                                        } else {
                                                            var28 = Char.getMyChar().yen - fieldAB.fieldAG;
                                                            var2 = (int) ((System.currentTimeMillis() - fieldAB.fieldAI) / 1000L);
                                                            GameScr.fieldAC("Up " + var28 + " trong " + NinjaUtil.gameAB(var2) + " perh=" + var28 / var2 * 3600);
                                                        }

                                                        return true;
                                                    } else if (var33.equals("clv")) {
                                                        if (fieldAB == null) {
                                                            GameScr.fieldAC("Bạn chưa up level");
                                                        } else {
                                                            long var41;
                                                            float var31 = (float) ((var41 = Char.getMyChar().cEXP - fieldAB.fieldAH) * 10000L / GameScr.exps[Char.getMyChar().clevel]) / 100.0F;
                                                            var28 = (int) ((System.currentTimeMillis() - fieldAB.fieldAI) / 1000L);
                                                            long var43;
                                                            float var35 = (float) ((var43 = var41 * 3600L / (long) var28) * 10000L / GameScr.exps[Char.getMyChar().clevel]) / 100.0F;
                                                            GameScr.fieldAC("Up " + var41 + " - " + var31 + "% trong " + NinjaUtil.gameAB(var28) + " perh=" + var43 + " - " + var35 + "%");
                                                        }

                                                        return true;
                                                    } else if (var33.equals("st")) {
                                                        if ((var29 = Mob.fieldAB(var2)) == null) {
                                                            GameScr.fieldAC("Stanima all");
                                                            fieldAC(-1, TileMap.mapID);
                                                        } else {
                                                            GameScr.fieldAC("Stanima " + var29.getTemplate().name + " lv " + var2);
                                                            fieldAC(var29.templateId, TileMap.mapID);
                                                        }

                                                        return true;
                                                    } else if (var33.equals("sta")) {
                                                        GameScr.fieldAC("Stanima all");
                                                        fieldAC(-1, TileMap.mapID);
                                                        return true;
                                                    } else if (var33.equals("stn")) {
                                                        if (GameScr.vParty.size() > 0 && ((Party) GameScr.vParty.firstElement()).charId == Char.getMyChar().charID) {
                                                            if ((var29 = Mob.fieldAB(var2)) == null) {
                                                                GameScr.fieldAC("Stanima nhóm all");
                                                                fieldAC(-1, TileMap.mapID);
                                                            } else {
                                                                GameScr.fieldAC("Stanima nhóm " + var29.getTemplate().name + " lv " + var2);
                                                                fieldAC(var29.templateId, TileMap.mapID);
                                                            }

                                                            fieldAC.fieldAA = true;
                                                            Service.gI().chatParty("st " + fieldAC.fieldAB + " " + fieldAC.fieldAC + " " + fieldAC.fieldAW);
                                                            return true;
                                                        } else {
                                                            GameScr.fieldAC("Chưa có nhóm hoặc bạn không là nhóm trưởng");
                                                            return true;
                                                        }
                                                    } else if (var33.equals("stan")) {
                                                        if (GameScr.vParty.size() > 0 && ((Party) GameScr.vParty.firstElement()).charId == Char.getMyChar().charID) {
                                                            GameScr.fieldAC("Stanima nhóm all");
                                                            fieldAC(-1, TileMap.mapID);
                                                            fieldAC.fieldAA = true;
                                                            Service.gI().chatParty("sta " + fieldAC.fieldAB + " " + fieldAC.fieldAC);
                                                            return true;
                                                        } else {
                                                            GameScr.fieldAC("Chưa có nhóm hoặc bạn không là nhóm trưởng");
                                                            return true;
                                                        }
                                                    } else if (var33.equals("stx")) {
                                                        var10000 = var2 >= 0 && var2 < Mob.arrMobTemplate.length ? Mob.arrMobTemplate[var2] : null;
                                                        var27 = var10000;
                                                        if (var10000 == null) {
                                                            GameScr.fieldAC("Tàn sát all");
                                                            fieldAA(-1, TileMap.mapID);
                                                        } else {
                                                            GameScr.fieldAC("Tàn sát " + var27.name + " id " + var2);
                                                            fieldAC(var27.mobTemplateId, TileMap.mapID);
                                                        }

                                                        return true;
                                                    } else if (!var33.equals("stnx")) {
                                                        if (var33.equals("sts")) {
                                                            GameScr.fieldAC("Step Stanima");
                                                            fieldAC.fieldAM();
                                                            if (Char.getMyChar().cName.equals(fieldAH) && GameScr.vParty.size() > 0) {
                                                                Service.gI().chatParty("sts");
                                                            }

                                                            return true;
                                                        } else if (var33.equals("stb")) {
                                                            if (GameScr.vParty.size() > 0 && ((Party) GameScr.vParty.firstElement()).charId != Char.getMyChar().charID) {
                                                                if (Char.getMyChar().nClass.classId != 6) {
                                                                    GameScr.fieldAC("Bạn không phải là quạt");
                                                                    return true;
                                                                } else {
                                                                    GameScr.fieldAC("Stanima Buff HS");
                                                                    fieldAA(true, true);
                                                                    return true;
                                                                }
                                                            } else {
                                                                GameScr.fieldAC("Chưa có nhóm hoặc bạn là nhóm trưởng");
                                                                return true;
                                                            }
                                                        } else if (var33.equals("stbx")) {
                                                            if (GameScr.vParty.size() > 0 && ((Party) GameScr.vParty.firstElement()).charId != Char.getMyChar().charID) {
                                                                if (Char.getMyChar().nClass.classId != 6) {
                                                                    GameScr.fieldAC("Bạn không phải là quạt");
                                                                    return true;
                                                                } else {
                                                                    GameScr.fieldAC("Stanima Buff");
                                                                    fieldAA(true, false);
                                                                    return true;
                                                                }
                                                            } else {
                                                                GameScr.fieldAC("Chưa có nhóm hoặc bạn là nhóm trưởng");
                                                                return true;
                                                            }
                                                        } else if (var33.equals("sths")) {
                                                            if (GameScr.vParty.size() > 0 && ((Party) GameScr.vParty.firstElement()).charId != Char.getMyChar().charID) {
                                                                if (Char.getMyChar().nClass.classId != 6) {
                                                                    GameScr.fieldAC("Bạn không phải là quạt");
                                                                    return true;
                                                                } else {
                                                                    GameScr.fieldAC("Stanima HS");
                                                                    fieldAA(false, true);
                                                                    return true;
                                                                }
                                                            } else {
                                                                GameScr.fieldAC("Chưa có nhóm hoặc bạn là nhóm trưởng");
                                                                return true;
                                                            }
                                                        } else if (var33.equals("pkb")) {
                                                            GameScr.fieldAC("PK Thần Thú");
                                                            fieldAA((Auto) (new PkBoss(TileMap.mapID)));
                                                            if (fieldAH != null && Char.getMyChar().cName.equals(fieldAH) && GameScr.vParty.size() > 1) {
                                                                Service.gI().chatParty("pkm " + TileMap.mapID);
                                                            }

                                                            return true;
                                                        } else if (var33.equals("pkk")) {
                                                            GameScr.fieldAC("PK Thần Thú");
                                                            PkBoss var40;
                                                            (var40 = new PkBoss(TileMap.mapID)).fieldAC = var2;
                                                            fieldAA((Auto) var40);
                                                            if (fieldAH != null && Char.getMyChar().cName.equals(fieldAH) && GameScr.vParty.size() > 1) {
                                                                Service.gI().chatParty("pkm " + TileMap.mapID);
                                                                Service.gI().chatParty("pkk " + var2);
                                                            }

                                                            return true;
                                                        } else if (var33.equals("lb")) {
                                                            var1 = "";

                                                            for (var2 = 0; var2 < GameScr.vMob.size(); ++var2) {
                                                                Mob var39;
                                                                if ((var39 = (Mob) GameScr.vMob.elementAt(var2)).isBoss) {
                                                                    var1 = var1 + var39.getTemplate().name + " lv: " + var39.level + ", ";
                                                                }
                                                            }

                                                            GameScr.fieldAC("Mob: " + var1);
                                                            return true;
                                                        } else if (var33.equals("tb")) {
                                                            (new Thread(new TimBoss())).start();
                                                            return true;
                                                        } else if (var33.equals("h")) {
                                                            Calendar var38 = Res.fieldAB();
                                                            GameScr.fieldAC("Time " + var38.get(11) + ":" + var38.get(12) + ":" + var38.get(13));
                                                            return true;
                                                        } else if (var33.equals("f")) {
                                                            GameScr.gI().gameAD(var2);
                                                            return true;
                                                        } else if (var1.equals("hd9x")) {
                                                            GameScr.fieldAC("Hang động 9x");
                                                            fieldAA((Auto) (new Hd9x()));
                                                            if (GameScr.vParty.size() > 0 && ((Party) GameScr.vParty.firstElement()).charId == Char.getMyChar().charID) {
                                                                Service.gI().chatParty("hd9x");
                                                            }

                                                            return true;
                                                        } else {
                                                            if (var1.length() == 4) {
                                                                if (var1.equals("as10")) {
                                                                    fieldAA((Auto) (new As10()));
                                                                    return true;
                                                                }

                                                                if (var1.equals("as20")) {
                                                                    fieldAA((Auto) (new As20(0)));
                                                                    return true;
                                                                }

                                                                if (var1.equals("a20k")) {
                                                                    fieldAA((Auto) (new As20(1)));
                                                                    return true;
                                                                }

                                                                if (var1.equals("a20t")) {
                                                                    fieldAA((Auto) (new As20(2)));
                                                                    return true;
                                                                }

                                                                if (var1.equals("a20u")) {
                                                                    fieldAA((Auto) (new As20(3)));
                                                                    return true;
                                                                }

                                                                if (var1.equals("a20c")) {
                                                                    fieldAA((Auto) (new As20(4)));
                                                                    return true;
                                                                }

                                                                if (var1.equals("a20d")) {
                                                                    fieldAA((Auto) (new As20(5)));
                                                                    return true;
                                                                }

                                                                if (var1.equals("a20q")) {
                                                                    fieldAA((Auto) (new As20(6)));
                                                                    return true;
                                                                }
                                                            } else {
                                                                if (var33.equals("boss")) {
                                                                    GameScr.fieldAC("Auto Boss " + var2);
                                                                    fieldAA((Auto) (new ChoBoss(var2)));
                                                                    return true;
                                                                }

                                                                if (var33.equals("kpk")) {
                                                                    GameScr.fieldAC("Khu PK " + var2);
                                                                    Auto.fieldAO = var2;
                                                                    return true;
                                                                }

                                                                if (var33.equals("cpk")) {
                                                                    GameScr.fieldAC("Xóa ds PK");
                                                                    SavePk.fieldAB();
                                                                    return true;
                                                                }

                                                                String[] var15;
                                                                if (var1.startsWith("apk")) {
                                                                    if ((var15 = fieldAC(var1, " ")).length > 1) {
                                                                        GameScr.fieldAC("Thêm " + var15[1] + " vào ds PK");
                                                                        SavePk.fieldAA(var15[1]);
                                                                    } else if (Char.getMyChar().charFocus != null) {
                                                                        GameScr.fieldAC("Thêm " + Char.getMyChar().charFocus.cName + " vào ds PK");
                                                                        SavePk.fieldAA(Char.getMyChar().charFocus.cName);
                                                                    }

                                                                    return true;
                                                                }

                                                                if (var1.startsWith("dpk")) {
                                                                    if ((var15 = fieldAC(var1, " ")).length > 1) {
                                                                        GameScr.fieldAC("Xóa " + var15[1] + " khỏi ds PK");
                                                                        SavePk.fieldAB(var15[1]);
                                                                    } else if (Char.getMyChar().charFocus != null) {
                                                                        GameScr.fieldAC("Xóa " + Char.getMyChar().charFocus.cName + " khỏi ds PK");
                                                                        SavePk.fieldAB(Char.getMyChar().charFocus.cName);
                                                                    }

                                                                    return true;
                                                                }

                                                                if (var33.equals("chs")) {
                                                                    GameScr.fieldAC("Xóa ds HS");
                                                                    fieldAV();
                                                                    return true;
                                                                }

                                                                if (var1.startsWith("ahs")) {
                                                                    if ((var15 = fieldAC(var1, " ")).length > 1) {
                                                                        GameScr.fieldAC("Thêm " + var15[1] + " vào ds HS");
                                                                        fieldAI(var15[1]);
                                                                    } else if (Char.getMyChar().charFocus != null) {
                                                                        GameScr.fieldAC("Thêm " + Char.getMyChar().charFocus.cName + " vào ds HS");
                                                                        fieldAI(Char.getMyChar().charFocus.cName);
                                                                    }

                                                                    return true;
                                                                }

                                                                if (var33.equals("dhs")) {
                                                                    if ((var15 = fieldAC(var1, " ")).length > 1) {
                                                                        GameScr.fieldAC("Xóa " + var15[1] + " khỏi ds HS");
                                                                        fieldAJ(var15[1]);
                                                                    } else if (Char.getMyChar().charFocus != null) {
                                                                        GameScr.fieldAC("Xóa " + Char.getMyChar().charFocus.cName + " khỏi ds PK");
                                                                        fieldAJ(Char.getMyChar().charFocus.cName);
                                                                    }

                                                                    return true;
                                                                }

                                                                ItemTemplate var16;

                                                                int var30;
                                                                if (var1.startsWith("asw")) {
                                                                    var15 = fieldAC(var1, " ");

                                                                    try {
                                                                        var2 = Integer.parseInt(var15[1]);
                                                                        var30 = Integer.parseInt(var15[2]);
                                                                        ItemTemplate var42 = ItemTemplates.gameAA((short) var2);
                                                                        GameScr.fieldAC("Thêm: " + var42.name + " giá: " + var30 + " vào ds bán Shinwa");
                                                                        fieldAB(var2, var30);
                                                                    } catch (Exception var21) {
                                                                        var21.printStackTrace();
                                                                    }

                                                                    return true;
                                                                }

                                                                if (var1.startsWith("rsw")) {
                                                                    var15 = fieldAC(var1, " ");

                                                                    try {
                                                                        var16 = ItemTemplates.gameAA((short) (var2 = Integer.parseInt(var15[1])));
                                                                        if (fieldAD(var2)) {
                                                                            var28 = fieldAE(var2);
                                                                            GameScr.fieldAC("Xóa: " + var16.name + " giá: " + var28 + " khỏi ds bán Shinwa");
                                                                            fieldAF(var2);
                                                                        } else {
                                                                            GameScr.fieldAC("Item " + var16.name + " chưa có trong ds bán Shinwa");
                                                                        }
                                                                    } catch (Exception var22) {
                                                                        var22.printStackTrace();
                                                                    }

                                                                    return true;
                                                                }

                                                                if (var1.startsWith("tnx")) {
                                                                    if (TileMap.isTruong(TileMap.mapID)) {
                                                                        var15 = fieldAC(var1, " ");

                                                                        try {
                                                                            var2 = Integer.parseInt(var15[1]);
                                                                        } catch (Exception var25) {
                                                                            return false;
                                                                        }

                                                                        int var18;
                                                                        try {
                                                                            var18 = Integer.parseInt(var15[2]);
                                                                        } catch (Exception var24) {
                                                                            var18 = 0;
                                                                        }

                                                                        var30 = var18;

                                                                        try {
                                                                            var18 = Integer.parseInt(var15[3]);
                                                                        } catch (Exception var23) {
                                                                            var18 = 0;
                                                                        }

                                                                        GameScr.fieldAC("Auto làm " + var2 + " tiên nữ");
                                                                        (new Thread(new AutoNpc(var2, var30, var18))).start();
                                                                        return true;
                                                                    }

                                                                    GameScr.fieldAC("Hãy đứng Trường để auto làm tiên nữ");
                                                                }
                                                            }

                                                            return false;
                                                        }
                                                    } else if (GameScr.vParty.size() > 0 && ((Party) GameScr.vParty.firstElement()).charId == Char.getMyChar().charID) {
                                                        var10000 = var2 >= 0 && var2 < Mob.arrMobTemplate.length ? Mob.arrMobTemplate[var2] : null;
                                                        var27 = var10000;
                                                        if (var10000 == null) {
                                                            GameScr.fieldAC("Stanima nhóm all");
                                                            fieldAC(-1, TileMap.mapID);
                                                        } else {
                                                            GameScr.fieldAC("Stanima nhóm " + var27.name + " id " + var2);
                                                            fieldAC(var27.mobTemplateId, TileMap.mapID);
                                                        }

                                                        fieldAC.fieldAA = true;
                                                        Service.gI().chatParty("st " + fieldAC.fieldAB + " " + fieldAC.fieldAC + " " + fieldAC.fieldAW);
                                                        return true;
                                                    } else {
                                                        GameScr.fieldAC("Chưa có nhóm hoặc bạn không là nhóm trưởng");
                                                        return true;
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        GameScr.fieldAC("End Auto");
                                        fieldAG();
                                        if (Char.fieldFI) {
                                            Service.gI().chatParty("pe");
                                        }

                                        return true;
                                    }
                                }
                            }
                        }
                    } else {
                        GameScr.fieldAC("Phá băng");
                        fieldBE = false;
                        fieldBF = false;
                        return true;
                    }
                } else {
                    GameScr.fieldAC("Băng skill");
                    fieldBF = true;
                    return true;
                }
            } else {
                GameScr.fieldAC("Băng boss");
                fieldBE = true;
                return true;
            }
        } else {
            GameScr.fieldAC("Đóng băng");
            fieldBE = true;
            fieldBF = true;
            return true;
        }
    }

    public static void fieldAG(String var0) {
        for (int var1 = 0; var1 < fieldCQ.length; ++var1) {
            fieldAA(var0, fieldCQ[var1].trim());
        }

    }

    public static void fieldAQ() {
        if (System.currentTimeMillis() - fieldCU > 60000L) {
            fieldCU = System.currentTimeMillis();
            MyVector var0;
            (var0 = new MyVector()).addElement(Char.getMyChar());
            Service.gI().sendPlayerAttack((MyVector) (new MyVector()), (MyVector) var0, (int) 2);
        }

    }

    public final void fieldAH(String var1) {
        if (System.currentTimeMillis() - fieldCS >= 5000L) {
            fieldCS = System.currentTimeMillis();
            Calendar var2;
            (var2 = Calendar.getInstance()).setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            StringBuffer var10000 = (new StringBuffer()).append("@");
            boolean var3 = true;
            var3 = true;
            var1 = var10000.append(10 + fieldCT.nextInt(89)).append(" ").append(var1).append(" ").append(var2.get(11)).append(":").append(var2.get(12)).append(":").append(var2.get(13)).toString();
            Service.gI().chat(var1);
        }
    }

    public static void fieldAA(String var0, String var1) {
        ChatManager.gameAD().gameAA(var0, Char.getMyChar().cName, var1);
        Service.gI().chatPrivate(var0, var1);
        Auto.fieldAA(20L);
    }

    private static String fieldAK(String var0) {
        InputStream var3 = RMS.fieldAA("/" + var0);

        try {
            byte[] var1 = new byte[var3.available()];
            var3.read(var1);
            var0 = new String(var1, "UTF-8");
        } catch (Exception var2) {
            var0 = "";
        }

        return var0;
    }

    public final void fieldAB(String var1, String var2) {
        if (Char.fieldFI && fieldAH != null && var1.equals(fieldAH) && !Char.getMyChar().cName.equals(fieldAH)) {
            fieldAD(var1, var2);
        }

        ChatTab var3;
        boolean var17;
        if ((var3 = ChatManager.gameAD().gameAA(var1)) == null) {
            var17 = true;
        } else if (System.currentTimeMillis() - var3.fieldAD > 1000L) {
            var3.fieldAD = System.currentTimeMillis();
            var17 = true;
        } else {
            var17 = false;
        }

        if (var17) {
            Char var18 = Char.getMyChar();
            if (var2.toLowerCase().equals("yenxu")) {
                fieldAA(var1, "Yên: " + var18.yen + " Xu: " + var18.xu + " Lượng: " + var18.luong);
                if (fieldAB != null) {
                    int var6 = Char.getMyChar().yen - fieldAB.fieldAG;
                    int var7 = (int) ((System.currentTimeMillis() - fieldAB.fieldAI) / 1000L);
                    fieldAA(var1, "Up " + var6 + " trong " + NinjaUtil.gameAB(var7) + " perh=" + var6 / var7 * 3600);
                    return;
                }
            } else {
                if (var2.toLowerCase().equals("level")) {
                    long var20;
                    long var8 = (var20 = (Char.getMyChar().cExpDown > 0L ? Char.getMyChar().cExpDown : Char.getMyChar().gameBE) * 10000L / GameScr.exps[Char.getMyChar().clevel]) % 100L;
                    fieldAA(var1, "LV: " + var18.clevel + " + " + (Char.getMyChar().cExpDown > 0L ? "-" : "") + var20 / 100L + "." + (var8 < 10L ? "0" + var8 : String.valueOf(var8)) + "%");
                    if (fieldAB != null) {
                        long var10;
                        float var16 = (float) ((var10 = Char.getMyChar().cEXP - fieldAB.fieldAH) * 10000L / GameScr.exps[Char.getMyChar().clevel]) / 100.0F;
                        int var19 = (int) ((System.currentTimeMillis() - fieldAB.fieldAI) / 1000L);
                        long var14;
                        float var4 = (float) ((var14 = var10 * 3600L / (long) var19) * 10000L / GameScr.exps[Char.getMyChar().clevel]) / 100.0F;
                        fieldAA(var1, "Up " + var10 + " - " + var16 + "% trong " + NinjaUtil.gameAB(var19) + " perh=" + var14 + " - " + var4 + "%");
                        return;
                    }

                    return;
                }

            }
        }

    }

    public static String[] fieldAC(String var0, String var1) {
        int var2 = 0;
        int var3 = var1.length();

        int var4;
        for (var4 = var0.indexOf(var1, 0); var4 != -1; ++var2) {
            var4 += var3;
            var4 = var0.indexOf(var1, var4);
        }

        String[] var7 = new String[var2 + 1];
        var4 = var0.indexOf(var1);
        int var5 = 0;

        int var6;
        for (var6 = 0; var4 != -1; ++var6) {
            var7[var6] = var0.substring(var5, var4);
            var5 = var4 + var3;
            var4 = var0.indexOf(var1, var5);
        }

        var7[var6] = var0.substring(var5, var0.length());
        return var7;
    }

    public static void fieldAD(String var0, String var1) {
        if (Char.fieldFI && fieldAH != null && var0.equals(fieldAH) && !Char.getMyChar().cName.equals(fieldAH)) {
            String[] var5 = fieldAC(var1, " ");

            try {
                if (var5[0].equals("dcvt")) {
                    fieldAR = Integer.parseInt(var5[1]) == 1;
                    return;
                }

                if (var5[0].equals("avt")) {
                    GameScr.fieldAC("Thêm vị trí " + fieldAT.size());
                    fieldAT.addElement(Integer.valueOf(var5[1]));
                    fieldAU.addElement(Integer.valueOf(var5[2]));
                    return;
                }

                if (var5[0].equals("dvt")) {
                    GameScr.fieldAC("Xóa hết vị trí");
                    fieldAT.removeAllElements();
                    fieldAU.removeAllElements();
                    return;
                }

                int var6;
                if (var5[0].equals("dvtx")) {
                    var6 = Integer.parseInt(var5[1]);
                    GameScr.fieldAC("Xóa vị trí " + var6);
                    fieldAT.removeElementAt(var6);
                    fieldAU.removeElementAt(var6);
                    return;
                }

                if (var5[0].equals("pe")) {
                    GameScr.fieldAC("End Auto");
                    LockGame.fieldBK();
                    fieldAB = null;
                    return;
                }

                if (var5[0].equals("tsa")) {
                    if (fieldAB == fieldCE) {
                        fieldCE.fieldAB = Integer.parseInt(var5[1]);
                        fieldCE.fieldAC = Integer.parseInt(var5[2]);
                        return;
                    }

                    fieldCC.fieldAA(-1, Integer.parseInt(var5[1]), Integer.parseInt(var5[2]));
                    fieldCC.fieldAA = true;
                    fieldAA((Auto) fieldCC);
                    return;
                }

                if (var5[0].equals("ts")) {
                    if (fieldAB == fieldCE) {
                        fieldCE.fieldAB = Integer.parseInt(var5[1]);
                        fieldCE.fieldAC = Integer.parseInt(var5[2]);
                        return;
                    }

                    fieldCC.fieldAA(Integer.parseInt(var5[3]), Integer.parseInt(var5[1]), Integer.parseInt(var5[2]));
                    fieldCC.fieldAA = true;
                    fieldAA((Auto) fieldCC);
                    return;
                }

                if (var5[0].equals("att")) {
                    if (fieldAB == fieldCE) {
                        fieldCE.fieldAB = Integer.parseInt(var5[1]);
                        fieldCE.fieldAC = Integer.parseInt(var5[2]);
                        return;
                    }

                    var6 = Integer.parseInt(var5[1]);
                    int var8 = Integer.parseInt(var5[3]);
                    TaskOrder var3;
                    if ((var3 = Char.fieldAM(1)) != null && var3.mapId == var6) {
                        fieldAE.fieldAD();
                    } else {
                        fieldAE.fieldAC(var6, var8);
                    }

                    fieldAE.fieldAC = Integer.parseInt(var5[2]);
                    fieldAE.fieldAA = true;
                    fieldAA((Auto) fieldAE);
                    return;
                }

                if (var5[0].equals("sta")) {
                    if (fieldAB == fieldCE) {
                        fieldCE.fieldAB = Integer.parseInt(var5[1]);
                        fieldCE.fieldAC = Integer.parseInt(var5[2]);
                        return;
                    }

                    fieldAC.fieldAA(-1, Integer.parseInt(var5[1]), Integer.parseInt(var5[2]), false, false);
                    fieldAC.fieldAA = true;
                    fieldAA((Auto) fieldAC);
                    return;
                }

                if (var5[0].equals("st")) {
                    if (fieldAB == fieldCE) {
                        fieldCE.fieldAB = Integer.parseInt(var5[1]);
                        fieldCE.fieldAC = Integer.parseInt(var5[2]);
                        return;
                    }

                    fieldAC.fieldAA(Integer.parseInt(var5[3]), Integer.parseInt(var5[1]), Integer.parseInt(var5[2]), false, false);
                    fieldAC.fieldAA = true;
                    fieldAA((Auto) fieldAC);
                    return;
                }

                if (var5[0].equals("hd9x")) {
                    fieldAA((Auto) (new Hd9x()));
                    return;
                }

                if (var5[0].equals("pkms")) {
                    if (fieldAB instanceof PKBossS) {
                        PKBossS var2;
                        (var2 = (PKBossS) fieldAB).fieldAB = Integer.parseInt(var5[1]);
                        var2.fieldAW = Integer.parseInt(var5[2]);
                        var2.fieldAV = 3;
                        return;
                    }
                } else if (var5[0].equals("pkes")) {
                    if (fieldAB instanceof PKBossS) {
                        ((PKBossS) fieldAB).fieldAV = 4;
                        return;
                    }
                } else {
                    if (var5[0].equals("pkm")) {
                        if (fieldAB == fieldCE) {
                            fieldCE.fieldAB = Integer.parseInt(var5[1]);
                            return;
                        }

                        Auto var7 = fieldAB instanceof PkBoss ? fieldAB.fieldAJ : fieldAB;
                        fieldAA((Auto) (new PkBoss(Integer.parseInt(var5[1]))));
                        fieldAB.fieldAJ = var7;
                        return;
                    }

                    if (var5[0].equals("pkk")) {
                        if (fieldAB instanceof PkBoss || fieldAB == fieldCE) {
                            fieldAB.fieldAC = Integer.parseInt(var5[1]);
                            return;
                        }
                    } else if (var5[0].equals("pke")) {
                        if (fieldAB instanceof PkBoss) {
                            fieldAC();
                            return;
                        }
                    } else if (fieldAB != null) {
                        if (var5[0].equals("map")) {
                            fieldAB.fieldAB = Integer.parseInt(var5[1]);
                            return;
                        }

                        if (var5[0].equals("khu")) {
                            fieldAB.fieldAC = Integer.parseInt(var5[1]);
                            return;
                        }

                        if (fieldAB instanceof TaThu) {
                            if (var5[0].equals("waitGr")) {
                                TaThu.fieldAX = System.currentTimeMillis();
                                TaThu.fieldAW = true;
                                return;
                            }

                            if (var5[0].equals("notifyGr")) {
                                TaThu.fieldAW = false;
                                return;
                            }
                        } else if (fieldAB instanceof Stanima && var5[0].equals("sts")) {
                            fieldAC.fieldAM();
                            return;
                        }
                    }
                }

                return;
            } catch (Exception var4) {
                var4.printStackTrace();
            }
        }

    }

    static {
        fieldAR();
    }
}
