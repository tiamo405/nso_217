
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.lcdui.Image;

public final class TileMap {

    public static int tmw;
    public static int tmh;
    public static int pxw;
    public static int pxh;
    public static int tileID;
    public static char[] maps;
    public static int[] types;
    private static Image gameBE;
    private static Image gameBF;
    public static Image imgMiniMap;
    private static Image imgWaterfall;
    private static Image imgTopWaterfall;
    private static Image imgWaterflow;
    private static Image imgLeaf;
    private static Image imgflowRiver;
    public static byte size = 24;
    private static int bx;
    private static int dbx;
    private static int fx;
    private static int dfx;
    public static String mapName1 = null;
    public static String mapName = "";
    public static byte zoneID;
    public static byte bgID;
    public static byte typeMap;
    public static short mapID;
    private static int cmtoYmini;
    private static int cmyMini;
    private static int cmdyMini;
    private static int cmvyMini;
    private static int cmtoXMini;
    private static int cmxMini;
    private static int cmdxMini;
    private static int cmvxMini;
    public static int wMiniMap;
    public static int hMiniMap;
    public static int posMiniMapX;
    public static int posMiniMapY;
    public static Vector vGo = new Vector();
    public static String[] mapNames;
    public static mHashtable1 locationStand = new mHashtable1();
    public static mHashtable1 itemMap = new mHashtable1();
    private static int defaultSolidTile;
    public static int sizeMiniMap = 2;
    public static int gssx;
    public static int gssxe;
    public static int gssy;
    public static int gssye;
    public static int countx;
    public static int county;
    private static int[] colorMini = new int[]{5257738, 8807192};
    public static short[][] fieldBZ = new short[160][];
    private static boolean[] fieldCA = new boolean[160];
    private static int[] fieldCB = new int[160];
    private static short[] fieldCC = new short[160];
    public static int fieldBD;
    public static boolean fieldBE;
    public static boolean fieldBF;
    private static Object fieldCD;
    private static byte[][] fieldCE;
    private static Image[] fieldCF;
    private static Image[] fieldCG;

    static {
        fieldBZ[0] = new short[]{27};
        fieldBZ[1] = new short[]{2, 3, 27, 72, 91, 94, 105, 114, 125, 157, 139, 113, 80};
        fieldBZ[2] = new short[]{6, 1};
        fieldBZ[3] = new short[]{1, 4};
        fieldBZ[4] = new short[]{3, 5};
        fieldBZ[5] = new short[]{7, 4};
        fieldBZ[6] = new short[]{7, 2, 20, 21};
        fieldBZ[7] = new short[]{6, 5, 8};
        fieldBZ[8] = new short[]{7, 9};
        fieldBZ[9] = new short[]{8, 10};
        fieldBZ[10] = new short[]{9, 11, 17, 22, 32, 38, 43, 48, 139};
        fieldBZ[11] = new short[]{12, 10};
        fieldBZ[12] = new short[]{11, 57};
        fieldBZ[13] = new short[]{57, 14};
        fieldBZ[14] = new short[]{13, 15};
        fieldBZ[15] = new short[]{14, 16};
        fieldBZ[16] = new short[]{15, 17};
        fieldBZ[17] = new short[]{16, 18, 10, 22, 32, 38, 43, 48, 139};
        fieldBZ[18] = new short[]{17, 19};
        fieldBZ[19] = new short[]{18, 58};
        fieldBZ[20] = new short[]{6};
        fieldBZ[21] = new short[]{22, 6};
        fieldBZ[22] = new short[]{23, 21, 10, 17, 32, 38, 43, 48, 139};
        fieldBZ[23] = new short[]{22, 69, 25};
        fieldBZ[24] = new short[]{59, 36};
        fieldBZ[25] = new short[]{23, 26};
        fieldBZ[26] = new short[]{27, 25};
        fieldBZ[27] = new short[]{26, 28, 1, 72, 91, 94, 105, 114, 125, 157, 139, 113, 80};
        fieldBZ[28] = new short[]{27, 60};
        fieldBZ[29] = new short[]{60, 30};
        fieldBZ[30] = new short[]{29, 31};
        fieldBZ[31] = new short[]{32, 30};
        fieldBZ[32] = new short[]{31, 61, 10, 17, 22, 38, 43, 48, 139};
        fieldBZ[33] = new short[]{61, 34};
        fieldBZ[34] = new short[]{35, 33};
        fieldBZ[35] = new short[]{34, 66};
        fieldBZ[36] = new short[]{37, 24};
        fieldBZ[37] = new short[]{36};
        fieldBZ[38] = new short[]{67, 68, 10, 17, 22, 32, 43, 48, 139};
        fieldBZ[39] = new short[]{72, 46, 40};
        fieldBZ[40] = new short[]{39, 65, 41};
        fieldBZ[41] = new short[]{42, 40, 43};
        fieldBZ[42] = new short[]{62, 41};
        fieldBZ[43] = new short[]{41, 44, 10, 17, 22, 32, 38, 48, 139};
        fieldBZ[44] = new short[]{43, 45};
        fieldBZ[45] = new short[]{44, 53};
        fieldBZ[46] = new short[]{63, 39, 47};
        fieldBZ[47] = new short[]{46, 48};
        fieldBZ[48] = new short[]{47, 50, 10, 17, 22, 32, 38, 43, 139};
        fieldBZ[49] = new short[]{50, 51};
        fieldBZ[50] = new short[]{48, 49};
        fieldBZ[51] = new short[]{52, 49};
        fieldBZ[52] = new short[]{51, 64};
        fieldBZ[53] = new short[]{54, 45};
        fieldBZ[54] = new short[]{55, 53};
        fieldBZ[55] = new short[]{54};
        fieldBZ[56] = new short[]{72};
        fieldBZ[57] = new short[]{12, 13};
        fieldBZ[58] = new short[]{19};
        fieldBZ[59] = new short[]{68, 24};
        fieldBZ[60] = new short[]{28, 29};
        fieldBZ[61] = new short[]{33, 32};
        fieldBZ[62] = new short[]{42};
        fieldBZ[63] = new short[]{46};
        fieldBZ[64] = new short[]{52};
        fieldBZ[65] = new short[]{40};
        fieldBZ[66] = new short[]{67, 35};
        fieldBZ[67] = new short[]{66, 38};
        fieldBZ[68] = new short[]{59, 38};
        fieldBZ[69] = new short[]{70, 23};
        fieldBZ[70] = new short[]{69, 71};
        fieldBZ[71] = new short[]{72, 70};
        fieldBZ[72] = new short[]{71, 39, 1, 27, 91, 94, 105, 114, 125, 157, 139, 113, 80};
        fieldBZ[73] = new short[]{1};
        fieldBZ[74] = new short[0];
        fieldBZ[75] = new short[0];
        fieldBZ[76] = new short[0];
        fieldBZ[77] = new short[0];
        fieldBZ[78] = new short[0];
        fieldBZ[79] = new short[0];
        fieldBZ[80] = new short[]{81, 82, 83};
        fieldBZ[81] = new short[]{80, 84};
        fieldBZ[82] = new short[]{80, 85};
        fieldBZ[83] = new short[]{80, 86};
        fieldBZ[84] = new short[]{81, 87};
        fieldBZ[85] = new short[]{82, 88};
        fieldBZ[86] = new short[]{83, 89};
        fieldBZ[87] = new short[]{84, 90};
        fieldBZ[88] = new short[]{85, 90};
        fieldBZ[89] = new short[]{86, 90};
        fieldBZ[90] = new short[0];
        fieldBZ[91] = new short[]{92};
        fieldBZ[92] = new short[]{91, 93};
        fieldBZ[93] = new short[]{92};
        fieldBZ[94] = new short[]{95};
        fieldBZ[95] = new short[]{94, 96};
        fieldBZ[96] = new short[]{95, 97};
        fieldBZ[97] = new short[]{96};
        fieldBZ[98] = new short[]{99};
        fieldBZ[99] = new short[]{98, 101, 100, 102};
        fieldBZ[100] = new short[]{99, 103};
        fieldBZ[101] = new short[]{99, 103};
        fieldBZ[102] = new short[]{99, 103};
        fieldBZ[103] = new short[]{101, 102, 104, 100};
        fieldBZ[104] = new short[]{103};
        fieldBZ[105] = new short[]{107, 106, 108};
        fieldBZ[106] = new short[]{105, 109};
        fieldBZ[107] = new short[]{105, 109};
        fieldBZ[108] = new short[]{105, 109};
        fieldBZ[109] = new short[]{106, 107, 108};
        fieldBZ[110] = new short[0];
        fieldBZ[111] = new short[0];
        fieldBZ[112] = new short[]{113};
        fieldBZ[113] = new short[]{112};
        fieldBZ[114] = new short[]{115};
        fieldBZ[115] = new short[]{114, 116};
        fieldBZ[116] = new short[]{115};
        fieldBZ[117] = new short[0];
        fieldBZ[118] = new short[0];
        fieldBZ[119] = new short[0];
        fieldBZ[120] = new short[0];
        fieldBZ[121] = new short[0];
        fieldBZ[122] = new short[0];
        fieldBZ[123] = new short[0];
        fieldBZ[124] = new short[0];
        fieldBZ[125] = new short[]{126};
        fieldBZ[126] = new short[]{125, 127};
        fieldBZ[127] = new short[]{126, 128};
        fieldBZ[128] = new short[]{127};
        fieldBZ[129] = new short[0];
        fieldBZ[130] = new short[0];
        fieldBZ[131] = new short[0];
        fieldBZ[132] = new short[0];
        fieldBZ[133] = new short[0];
        fieldBZ[134] = new short[]{138};
        fieldBZ[135] = new short[]{138};
        fieldBZ[136] = new short[]{138};
        fieldBZ[137] = new short[]{138};
        fieldBZ[138] = new short[]{134, 135, 136, 137};
        fieldBZ[139] = new short[]{140};
        fieldBZ[140] = new short[]{139, 141};
        fieldBZ[141] = new short[]{140, 142};
        fieldBZ[142] = new short[]{141, 143};
        fieldBZ[143] = new short[]{142, 144};
        fieldBZ[144] = new short[]{143, 145};
        fieldBZ[145] = new short[]{144, 146};
        fieldBZ[146] = new short[]{145, 147};
        fieldBZ[147] = new short[]{146, 148};
        fieldBZ[148] = new short[]{147};
        fieldBZ[149] = new short[0];
        fieldBZ[150] = new short[0];
        fieldBZ[151] = new short[0];
        fieldBZ[152] = new short[0];
        fieldBZ[153] = new short[0];
        fieldBZ[154] = new short[0];
        fieldBZ[155] = new short[0];
        fieldBZ[156] = new short[0];
        fieldBZ[157] = new short[]{158, 159};
        fieldBZ[158] = new short[]{157, 159};
        fieldBZ[159] = new short[]{158, 157};
        fieldBD = -1;
        fieldBE = false;
        fieldBF = false;
        fieldCD = new Object();
        fieldCE = new byte[160][];

        for (int var0 = 0; var0 < 160; ++var0) {
            int var1 = var0;
            InputStream var2 = null;

            try {
                var2 = "".getClass().getResourceAsStream("/map/" + var1);
                fieldCE[var1] = new byte[var2.available()];
                var2.read(fieldCE[var1]);
                var2.close();
            } catch (Exception var9) {
                var9.printStackTrace();
            } finally {
                try {
                    var2.close();
                } catch (Exception var8) {
                }

            }
        }

        fieldCF = new Image[5];
        fieldCG = new Image[5];
    }

    public static void gameAA(int var0, int var1, int var2, int var3) {
        wMiniMap = var2;
        hMiniMap = var3;
        posMiniMapX = var0;
        posMiniMapY = var1;
    }

    public static void gameAA() {
        cmtoXMini = Char.getMyChar().cx / 12;
        cmtoYmini = Char.getMyChar().cy / 12;
        if (cmtoXMini > tmw * sizeMiniMap - wMiniMap / 2) {
            cmtoXMini = tmw * sizeMiniMap - wMiniMap;
        } else if (cmtoXMini < wMiniMap / 2) {
            cmtoXMini = 0;
        } else {
            cmtoXMini -= wMiniMap / 2;
        }

        if (cmtoYmini < hMiniMap / 2) {
            cmtoYmini = 0;
        } else {
            cmtoYmini -= hMiniMap / 2;
        }

        if (cmtoYmini > tmh * sizeMiniMap - hMiniMap) {
            cmtoYmini = tmh * sizeMiniMap - hMiniMap;
        }

    }

    public static void gameAB() {
        if (tmw * sizeMiniMap >= wMiniMap || tmh * sizeMiniMap >= hMiniMap) {
            if (cmyMini != cmtoYmini) {
                cmvyMini = cmtoYmini - cmyMini << 2;
                cmdyMini += cmvyMini;
                cmyMini += cmdyMini >> 4;
                cmdyMini &= 15;
            }

            if (cmxMini != cmtoXMini) {
                cmvxMini = cmtoXMini - cmxMini << 2;
                cmdxMini += cmvxMini;
                cmxMini += cmdxMini >> 4;
                cmdxMini &= 15;
            }
        }

    }

    public static void gameAC() {
        gameBE = null;
        System.gc();
    }

    static final void gameAD() {
        if (imgLeaf == null) {
            imgLeaf = GameCanvas.gameAC("/t/uwt.png");
        }

        if (imgWaterfall == null) {
            imgWaterfall = GameCanvas.gameAC("/t/wtf.png");
        }

        if (imgTopWaterfall == null) {
            imgTopWaterfall = GameCanvas.gameAC("/t/twtf.png");
        }

        if (imgWaterflow == null) {
            imgWaterflow = GameCanvas.gameAC("/t/wts.png");
        }

        if (imgflowRiver == null) {
            imgflowRiver = GameCanvas.gameAC("/t/wts1.png");
        }

        System.gc();
    }

    public static void gameAA(int var0) {
        pxh = tmh * size;
        pxw = tmw * size;

        try {
            for (int var1 = 0; var1 < tmw * tmh; ++var1) {
                int[] var10000;
                if (locationStand != null ? locationStand.gameAA(String.valueOf(var1)) != null : false) {
                    var10000 = types;
                    var10000[var1] |= 2;
                }

                if (var0 == 4) {
                    if (maps[var1] == 1 || maps[var1] == 2 || maps[var1] == 3 || maps[var1] == 4 || maps[var1] == 5 || maps[var1] == 6 || maps[var1] == '\t' || maps[var1] == '\n' || maps[var1] == 'O' || maps[var1] == 'P' || maps[var1] == '\r' || maps[var1] == 14 || maps[var1] == '+' || maps[var1] == ',' || maps[var1] == '-' || maps[var1] == '2') {
                        var10000 = types;
                        var10000[var1] |= 2;
                    }

                    if (maps[var1] == '\t' || maps[var1] == 11) {
                        var10000 = types;
                        var10000[var1] |= 4;
                    }

                    if (maps[var1] == '\n' || maps[var1] == '\f') {
                        var10000 = types;
                        var10000[var1] |= 8;
                    }

                    if (maps[var1] == '\r' || maps[var1] == 14) {
                        var10000 = types;
                        var10000[var1] |= 1024;
                    }

                    if (maps[var1] == 'L' || maps[var1] == 'M') {
                        var10000 = types;
                        var10000[var1] |= 64;
                        if (maps[var1] == 'N') {
                            var10000 = types;
                            var10000[var1] |= 4096;
                        }
                    }
                }

                if (var0 == 1) {
                    if (maps[var1] == 22) {
                        defaultSolidTile = maps[var1] - 1;
                    }

                    if (maps[var1] == 1 || maps[var1] == 2 || maps[var1] == 3 || maps[var1] == 4 || maps[var1] == 5 || maps[var1] == 6 || maps[var1] == 7 || maps[var1] == '$' || maps[var1] == '%' || maps[var1] == '6' || maps[var1] == '[' || maps[var1] == '\\' || maps[var1] == ']' || maps[var1] == '^' || maps[var1] == 'I' || maps[var1] == 'J' || maps[var1] == 'a' || maps[var1] == 'b' || maps[var1] == 't' || maps[var1] == 'u' || maps[var1] == 'v' || maps[var1] == 'x' || maps[var1] == '=') {
                        var10000 = types;
                        var10000[var1] |= 2;
                    }

                    if (maps[var1] == 2 || maps[var1] == 3 || maps[var1] == 4 || maps[var1] == 5 || maps[var1] == 6 || maps[var1] == 20 || maps[var1] == 21 || maps[var1] == 22 || maps[var1] == 23 || maps[var1] == '$' || maps[var1] == '%' || maps[var1] == '&' || maps[var1] == '\'' || maps[var1] == '=') {
                        var10000 = types;
                        var10000[var1] |= 4096;
                    }

                    if (maps[var1] == '\b' || maps[var1] == '\t' || maps[var1] == '\n' || maps[var1] == '\f' || maps[var1] == '\r' || maps[var1] == 14 || maps[var1] == 30) {
                        var10000 = types;
                        var10000[var1] |= 16;
                    }

                    if (maps[var1] == 17) {
                        var10000 = types;
                        var10000[var1] |= 32;
                    }

                    if (maps[var1] == 18) {
                        var10000 = types;
                        var10000[var1] |= 128;
                    }

                    if (maps[var1] == '%' || maps[var1] == '&' || maps[var1] == '=') {
                        var10000 = types;
                        var10000[var1] |= 4;
                    }

                    if (maps[var1] == '$' || maps[var1] == '\'' || maps[var1] == '=') {
                        var10000 = types;
                        var10000[var1] |= 8;
                    }

                    if (maps[var1] == 19) {
                        var10000 = types;
                        var10000[var1] |= 64;
                        if ((types[var1 - tmw] & 4096) == 4096) {
                            var10000 = types;
                            var10000[var1] |= 4096;
                        }
                    }

                    if (maps[var1] == '#') {
                        var10000 = types;
                        var10000[var1] |= 2048;
                    }

                    if (maps[var1] == 7) {
                        var10000 = types;
                        var10000[var1] |= 1024;
                    }

                    if (maps[var1] == ' ' || maps[var1] == '!' || maps[var1] == '"') {
                        var10000 = types;
                        var10000[var1] |= 256;
                    }
                }

                if (var0 == 2) {
                    if (maps[var1] == 22 || maps[var1] == 'g' || maps[var1] == 'o') {
                        defaultSolidTile = maps[var1] - 1;
                    }

                    if (maps[var1] == 1 || maps[var1] == 2 || maps[var1] == 3 || maps[var1] == 4 || maps[var1] == 5 || maps[var1] == 6 || maps[var1] == 7 || maps[var1] == '$' || maps[var1] == '%' || maps[var1] == '6' || maps[var1] == '=' || maps[var1] == 'I' || maps[var1] == 'L' || maps[var1] == 'M' || maps[var1] == 'N' || maps[var1] == 'O' || maps[var1] == 'R' || maps[var1] == 'S' || maps[var1] == 'b' || maps[var1] == 'c' || maps[var1] == 'd' || maps[var1] == 'f' || maps[var1] == 'g' || maps[var1] == 'l' || maps[var1] == 'm' || maps[var1] == 'n' || maps[var1] == 'p' || maps[var1] == 'q' || maps[var1] == 't' || maps[var1] == 'u' || maps[var1] == '}' || maps[var1] == '~' || maps[var1] == 127 || maps[var1] == 129 || maps[var1] == 130) {
                        var10000 = types;
                        var10000[var1] |= 2;
                    }

                    if (maps[var1] == 1 || maps[var1] == 3 || maps[var1] == 4 || maps[var1] == 5 || maps[var1] == 6 || maps[var1] == 20 || maps[var1] == 21 || maps[var1] == 22 || maps[var1] == 23 || maps[var1] == '$' || maps[var1] == '%' || maps[var1] == '&' || maps[var1] == '\'' || maps[var1] == '7' || maps[var1] == 'm' || maps[var1] == 'o' || maps[var1] == 'p' || maps[var1] == 'q' || maps[var1] == 'r' || maps[var1] == 's' || maps[var1] == 't' || maps[var1] == 127 || maps[var1] == 129 || maps[var1] == 130) {
                        var10000 = types;
                        var10000[var1] |= 4096;
                    }

                    if (maps[var1] == '\b' || maps[var1] == '\t' || maps[var1] == '\n' || maps[var1] == '\f' || maps[var1] == '\r' || maps[var1] == 14 || maps[var1] == 30 || maps[var1] == 135) {
                        var10000 = types;
                        var10000[var1] |= 16;
                    }

                    if (maps[var1] == 17) {
                        var10000 = types;
                        var10000[var1] |= 32;
                    }

                    if (maps[var1] == 18) {
                        var10000 = types;
                        var10000[var1] |= 128;
                    }

                    if (maps[var1] == '=' || maps[var1] == '%' || maps[var1] == '&' || maps[var1] == 127 || maps[var1] == 130 || maps[var1] == 131) {
                        var10000 = types;
                        var10000[var1] |= 4;
                    }

                    if (maps[var1] == '=' || maps[var1] == '$' || maps[var1] == '\'' || maps[var1] == 127 || maps[var1] == 129 || maps[var1] == 132) {
                        var10000 = types;
                        var10000[var1] |= 8;
                    }

                    if (maps[var1] == 19) {
                        var10000 = types;
                        var10000[var1] |= 64;
                        if ((types[var1 - tmw] & 4096) == 4096) {
                            var10000 = types;
                            var10000[var1] |= 4096;
                        }
                    }

                    if (maps[var1] == 134) {
                        var10000 = types;
                        var10000[var1] |= 64;
                        if ((types[var1 - tmw] & 4096) == 4096) {
                            var10000 = types;
                            var10000[var1] |= 4096;
                        }
                    }

                    if (maps[var1] == '#') {
                        var10000 = types;
                        var10000[var1] |= 2048;
                    }

                    if (maps[var1] == 7) {
                        var10000 = types;
                        var10000[var1] |= 1024;
                    }

                    if (maps[var1] == ' ' || maps[var1] == '!' || maps[var1] == '"') {
                        var10000 = types;
                        var10000[var1] |= 256;
                    }

                    if (maps[var1] == '=' || maps[var1] == 127) {
                        var10000 = types;
                        var10000[var1] |= 8192;
                    }
                }

                if (var0 == 3) {
                    if (maps[var1] == '\f' || maps[var1] == '3' || maps[var1] == 'X' || maps[var1] == 't' || maps[var1] == 128) {
                        defaultSolidTile = maps[var1] - 1;
                    }

                    if (maps[var1] == 'm' || maps[var1] == 'n') {
                        defaultSolidTile = maps[var1];
                    }

                    if (maps[var1] == 1 || maps[var1] == 2 || maps[var1] == 3 || maps[var1] == 4 || maps[var1] == 5 || maps[var1] == 6 || maps[var1] == 7 || maps[var1] == 11 || maps[var1] == 14 || maps[var1] == 17 || maps[var1] == '+' || maps[var1] == '3' || maps[var1] == '?' || maps[var1] == 'A' || maps[var1] == 'C' || maps[var1] == 'D' || maps[var1] == 'G' || maps[var1] == 'H' || maps[var1] == 'S' || maps[var1] == 'T' || maps[var1] == 'U' || maps[var1] == 'W' || maps[var1] == '[' || maps[var1] == '^' || maps[var1] == 'a' || maps[var1] == 'b' || maps[var1] == 'j' || maps[var1] == 'k' || maps[var1] == 'o' || maps[var1] == 'q' || maps[var1] == 'u' || maps[var1] == 'v' || maps[var1] == 'w' || maps[var1] == '}' || maps[var1] == '~' || maps[var1] == 129 || maps[var1] == 130 || maps[var1] == 131 || maps[var1] == 133 || maps[var1] == 136 || maps[var1] == 138 || maps[var1] == 139 || maps[var1] == 142) {
                        var10000 = types;
                        var10000[var1] |= 2;
                    }

                    if (maps[var1] == '|' || maps[var1] == 't' || maps[var1] == '{' || maps[var1] == ',' || maps[var1] == '\f' || maps[var1] == 15 || maps[var1] == 16 || maps[var1] == '-' || maps[var1] == '\n' || maps[var1] == '\t') {
                        var10000 = types;
                        var10000[var1] |= 4096;
                    }

                    if (maps[var1] == 23) {
                        var10000 = types;
                        var10000[var1] |= 32;
                    }

                    if (maps[var1] == 24) {
                        var10000 = types;
                        var10000[var1] |= 128;
                    }

                    if (maps[var1] == 6 || maps[var1] == 15 || maps[var1] == '3' || maps[var1] == '_' || maps[var1] == 'a' || maps[var1] == 'j' || maps[var1] == 'o' || maps[var1] == '{' || maps[var1] == '}' || maps[var1] == 138 || maps[var1] == 140) {
                        var10000 = types;
                        var10000[var1] |= 4;
                    }

                    if (maps[var1] == 7 || maps[var1] == 16 || maps[var1] == '3' || maps[var1] == '`' || maps[var1] == 'b' || maps[var1] == 'k' || maps[var1] == 'o' || maps[var1] == '|' || maps[var1] == '~' || maps[var1] == 139 || maps[var1] == 141) {
                        var10000 = types;
                        var10000[var1] |= 8;
                    }

                    if (maps[var1] == 25) {
                        var10000 = types;
                        var10000[var1] |= 64;
                        if ((types[var1 - tmw] & 4096) == 4096) {
                            var10000 = types;
                            var10000[var1] |= 4096;
                        }
                    }

                    if (maps[var1] == '"') {
                        var10000 = types;
                        var10000[var1] |= 2048;
                    }

                    if (maps[var1] == 17) {
                        var10000 = types;
                        var10000[var1] |= 1024;
                    }

                    if (maps[var1] == '!' || maps[var1] == 'g' || maps[var1] == 'h' || maps[var1] == 'i' || maps[var1] == 26 || maps[var1] == '!') {
                        var10000 = types;
                        var10000[var1] |= 256;
                    }

                    if (maps[var1] == '3' || maps[var1] == 'o' || maps[var1] == 'D') {
                        var10000 = types;
                        var10000[var1] |= 8192;
                    }

                    if (maps[var1] == 'R' || maps[var1] == 'n' || maps[var1] == 143) {
                        var10000 = types;
                        var10000[var1] |= 16384;
                    }

                    if (maps[var1] == 'q') {
                        var10000 = types;
                        var10000[var1] |= 65536;
                    }

                    if (maps[var1] == 142) {
                        var10000 = types;
                        var10000[var1] |= 32768;
                    }

                    if (maps[var1] == '(' || maps[var1] == ')') {
                        var10000 = types;
                        var10000[var1] |= 131072;
                    }

                    if (maps[var1] == 'n') {
                        var10000 = types;
                        var10000[var1] |= 262144;
                    }

                    if (maps[var1] == 143) {
                        var10000 = types;
                        var10000[var1] |= 524288;
                    }
                }
            }

            imgMiniMap = Image.createImage(tmw * sizeMiniMap * mGraphics.zoomLevel, tmh * sizeMiniMap * mGraphics.zoomLevel);
            mGraphics var5;
            (var5 = new mGraphics(imgMiniMap.getGraphics())).gameAA(0);
            var5.gameAC(0, 0, tmw * sizeMiniMap, tmh * sizeMiniMap);

            for (var0 = 0; var0 < tmw; ++var0) {
                for (int var2 = 0; var2 < tmh; ++var2) {
                    int var3;
                    if ((var3 = maps[var2 * tmw + var0] - 1) != -1) {
                        var5.gameAA(gameBF, 0, var3 * sizeMiniMap, sizeMiniMap, sizeMiniMap, 0, var0 * sizeMiniMap, var2 * sizeMiniMap, 0);
                    }
                }
            }

            if (!GameCanvas.lowGraphic) {
                if (mapID == 0 || mapID <= 4 || mapID >= 16 && mapID <= 18 || mapID >= 24 && mapID <= 27 || mapID == 22 || mapID == 33 || mapID == 34 || mapID == 38 || mapID == 57 || mapID == 58 || mapID == 60 || mapID == 68 || mapID >= 70 && mapID <= 75 || mapID == 81) {
                    Effect2.vAnimateEffect.addElement(new AnimateEffect((byte) 1, true, 10));
                }

                if (mapID >= 39 && mapID <= 44 || mapID >= 46 && mapID <= 48 || mapID == 56 || mapID >= 62 && mapID <= 65) {
                    Effect2.vAnimateEffect.addElement(new AnimateEffect((byte) 3, true, Res.gameAD(150, 200)));
                    return;
                }
            }
        } catch (Exception var4) {
            System.out.println("Error Load Map");
            var4.printStackTrace();
            GameMidlet midlet = GameMidlet.instance;
            MotherCanvas.AC = false;
            System.gc();
            midlet.notifyDestroyed();
        }

    }

    public static final void gameAA(mGraphics var0) {
        for (int var2 = GameScr.gssx; var2 < GameScr.gssxe; ++var2) {
            for (int var3 = GameScr.gssy; var3 < GameScr.gssye; ++var3) {
                int var1 = maps[var3 * tmw + var2] - 1;
                if ((gameAC(var2, var3) & 256) != 256) {
                    if (tileID == 4 && (gameAC(var2, var3) & 64) == 64) {
                        var1 = var3 - 1;
                        if ((var1 = maps[var1 * tmw + var2] - 1) == 15) {
                            var0.gameAA(gameBE, 0, 17 * size, 24, 24, 0, var2 * size, var3 * size, 0);
                            continue;
                        }

                        if (var1 == 5) {
                            var0.gameAA(gameBE, 0, 7 * size, 24, 24, 0, var2 * size, var3 * size, 0);
                            continue;
                        }

                        if (var1 == 18 || var1 == 22 || var1 == 15) {
                            var0.gameAA(gameBE, 0, 17 * size, 24, 24, 0, var2 * size, var3 * size, 0);
                            continue;
                        }

                        if (var1 == 44 || var1 == 52 || var1 == 51) {
                            var0.gameAA(gameBE, 0, 56 * size, 24, 24, 0, var2 * size, var3 * size, 0);
                            continue;
                        }

                        if (var1 == 24 || var1 == 23 || var1 == 20 || var1 == 21 || var1 == 19 || var1 == 12 || var1 == 13) {
                            continue;
                        }

                        if (var1 != -1) {
                            var0.gameAA(gameBE, 0, var1 * size, 24, 24, 0, var2 * size, var3 * size, 0);
                        } else if (var1 == -1) {
                            continue;
                        }
                    }

                    if (tileID == 1) {
                        if ((gameAC(var2, var3) & 32) == 32) {
                            var0.gameAA(imgWaterfall, 0, 24 * (GameCanvas.gameTick % 4), 24, 24, 0, var2 * size, var3 * size, 0);
                            continue;
                        }

                        if ((gameAC(var2, var3) & 64) == 64 || (gameAC(var2, var3) & 2048) == 2048) {
                            if ((gameAC(var2, var3 - 1) & 32) == 32) {
                                var0.gameAA(imgWaterfall, 0, 24 * (GameCanvas.gameTick % 4), 24, 24, 0, var2 * size, var3 * size, 0);
                                continue;
                            }

                            if ((gameAC(var2, var3 - 1) & 4096) == 4096) {
                                var0.gameAA(gameBE, 0, 504, 24, 24, 0, var2 * size, var3 * size, 0);
                                continue;
                            }
                        }
                    }

                    if (tileID == 2) {
                        if ((gameAC(var2, var3) & 32) == 32) {
                            var0.gameAA(imgWaterfall, 0, 24 * (GameCanvas.gameTick % 8 >> 1), 24, 24, 0, var2 * size, var3 * size, 0);
                            continue;
                        }

                        if (var1 == 17) {
                            var0.gameAA(imgTopWaterfall, 0, 24 * (GameCanvas.gameTick % 8 >> 1), 24, 24, 0, var2 * size, var3 * size, 0);
                            continue;
                        }

                        if (var1 == 133) {
                            var0.gameAA(gameBE, 0, 132 * size, 24, 24, 0, var2 * size, var3 * size, 0);
                            continue;
                        }

                        if ((gameAC(var2, var3) & 64) == 64 || (gameAC(var2, var3) & 2048) == 2048) {
                            if ((gameAC(var2, var3 - 1) & 32) == 32) {
                                var0.gameAA(imgWaterfall, 0, 24 * (GameCanvas.gameTick % 4), 24, 24, 0, var2 * size, var3 * size, 0);
                                continue;
                            }

                            if ((gameAC(var2, var3 - 1) & 4096) == 4096) {
                                if ((var1 = gameAB(var2, var3 - 1)) == 55) {
                                    var1 = 54;
                                } else if (var1 != 19 && var1 != 35) {
                                    if (var1 < 40) {
                                        var1 = 21;
                                    } else {
                                        var1 = 110;
                                    }
                                } else if ((var1 = gameAB(var2, var3 - 2)) == 55) {
                                    var1 = 54;
                                } else if (var1 < 40) {
                                    var1 = 21;
                                }

                                var0.gameAA(gameBE, 0, var1 * 24, 24, 24, 0, var2 * size, var3 * size, 0);
                                continue;
                            }
                        }
                    }

                    if (tileID == 3) {
                        if ((gameAC(var2, var3) & 32) == 32) {
                            var0.gameAA(imgWaterfall, 0, 24 * (GameCanvas.gameTick % 8 >> 1), 24, 24, 0, var2 * size, var3 * size, 0);
                            continue;
                        }

                        if (var1 == 23) {
                            var0.gameAA(imgTopWaterfall, 0, 24 * (GameCanvas.gameTick % 8 >> 1), 24, 24, 0, var2 * size, var3 * size, 0);
                            continue;
                        }

                        if ((gameAC(var2, var3) & 64) == 64 || (gameAC(var2, var3) & 2048) == 2048) {
                            if ((gameAC(var2, var3 - 1) & 32) == 32) {
                                var0.gameAA(imgWaterfall, 0, 24 * (GameCanvas.gameTick % 4), 24, 24, 0, var2 * size, var3 * size, 0);
                                continue;
                            }

                            if ((gameAC(var2, var3 - 1) & 4096) == 4096) {
                                if ((var1 = gameAB(var2, var3 - 1)) == 25) {
                                    var1 = gameAB(var2, var3 - 2);
                                }

                                if (var1 == 45) {
                                    var1 = 44;
                                }

                                --var1;
                                var0.gameAA(gameBE, 0, var1 * 24, 24, 24, 0, var2 * size, var3 * size, 0);
                                continue;
                            }
                        }
                    }

                    if ((gameAC(var2, var3) & 16) == 16) {
                        dbx = (bx = var2 * size - GameScr.cmx) - GameScr.gW2;
                        fx = (dfx = (size - 2) * dbx / size) + GameScr.gW2;
                        var0.gameAA(gameBE, 0, var1 * size, 24, 24, 0, fx + GameScr.cmx, var3 * size, 0);
                    } else if ((gameAC(var2, var3) & 512) == 512) {
                        if (var1 != -1) {
                            var0.gameAA(gameBE, 0, var1 * size, 24, 1, 0, var2 * size, var3 * size, 0);
                            var0.gameAA(gameBE, 0, var1 * size, 24, 24, 0, var2 * size, var3 * size + 1, 0);
                        }
                    } else if (var1 != -1) {
                        var0.gameAA(gameBE, 0, var1 * size, 24, 24, 0, var2 * size, var3 * size, 0);
                    }
                }
            }
        }

    }

    public static final void gameAB(mGraphics var0) {
        if (GameCanvas.w > 176) {
            Res.gameAA(var0);
            var0.gameAA(posMiniMapX + 1, posMiniMapY + 2);
            int var1 = Char.getMyChar().cx / 12;
            int var2 = Char.getMyChar().cy / 12;
            var0.gameAA(0);
            var0.gameAC(-2, -2, wMiniMap + 2, hMiniMap);
            var0.gameAD(-2, -2, wMiniMap + 4, hMiniMap + 4);

            int var3;
            for (var3 = 0; var3 < 2; ++var3) {
                var0.gameAA(colorMini[var3]);
                var0.gameAB(var3 - 2, var3 - 2, wMiniMap + 2 - (var3 << 1), hMiniMap - (var3 << 1));
            }

            var0.gameAD(0, 0, wMiniMap - 2, hMiniMap - 3);
            if (mGraphics.gameAA(imgMiniMap) > wMiniMap || mGraphics.gameAB(imgMiniMap) > hMiniMap) {
                var0.gameAA(-cmxMini, -cmyMini);
            }

            var0.gameAA(imgMiniMap, 0, 0, 0);

            int int1;
            int int2;
            for (int1 = 0; int1 < Auto.fieldAN.size(); ++int1) {
                Mob mob;
                var3 = (mob = (Mob) Auto.fieldAN.elementAt(int1)).x / 12;
                int2 = mob.y / 12;
                if (var3 < cmvxMini) {
                    var3 = cmvxMini;
                }

                if (int2 < cmvyMini) {
                    int2 = cmvyMini;
                }

                if (var3 > cmvxMini + wMiniMap) {
                    var3 = cmvxMini + wMiniMap;
                }

                if (int2 > cmvyMini + hMiniMap) {
                    int2 = cmvyMini + hMiniMap;
                }

                if (GameCanvas.gameTick % 10 < 8) {
                    var0.gameAA(16777215);
                    var0.gameAC(var3 - 2, int2 - 2, 5, 5);
                    var0.gameAA(mob.levelBoss == 1 ? 255 : (mob.levelBoss == 2 ? 16711935 : '\uffff'));
                    var0.gameAC(var3 - 1, int2 - 1, 3, 3);
                }
            }

            var0.gameAA(16777215);
            var0.gameAC(var1 - 2, var2 - 2, 5, 5);
            var0.gameAA(16711680);
            var0.gameAC(var1 - 1, var2 - 1, 3, 3);
            if (Code.fieldAR) {
                for (int1 = 0; int1 < Code.fieldAT.size(); ++int1) {
                    var3 = ((Integer) Code.fieldAT.elementAt(int1)).intValue() / 12;
                    int2 = ((Integer) Code.fieldAU.elementAt(int1)).intValue() / 12;
                    if (Code.fieldAS == int1) {
                        var0.gameAA(16777215);
                        var0.gameAC(var3 - 2, int2 - 2, 5, 5);
                    }

                    var0.gameAA(16777215);
                    var0.gameAC(var3 - 1, int2 - 1, 3, 3);
                }
            }

            for (var3 = 0; var3 < GameScr.vParty.size(); ++var3) {
                Party var4;
                if ((var4 = (Party) GameScr.vParty.elementAt(var3)).c != null && var4.c != Char.getMyChar()) {
                    var2 = var4.c.cx / 12;
                    var1 = var4.c.cy / 12;
                    if (var2 < cmxMini) {
                        var2 = cmxMini;
                    }

                    if (var1 < cmyMini) {
                        var1 = cmyMini;
                    }

                    if (var2 > cmxMini + wMiniMap) {
                        var2 = cmxMini + wMiniMap;
                    }

                    if (var1 > cmyMini + hMiniMap) {
                        var1 = cmyMini + hMiniMap;
                    }

                    if (GameCanvas.gameTick % 10 < 8) {
                        var0.gameAA(16777215);
                        var0.gameAC(var2 - 2, var1 - 2, 5, 5);
                        var0.gameAA(65280);
                        var0.gameAC(var2 - 1, var1 - 1, 3, 3);
                    }
                }
            }

            Res.gameAA(var0);
            if (GameCanvas.isTouch) {
                var0.gameAA(GameScr.gameEZ, posMiniMapX - 1, posMiniMapY, 0);
                mFont.tahoma_7_yellow.gameAA(var0, "Map : " + mapID, posMiniMapX - 1, posMiniMapY, 0, mFont.tahoma_7);
                mFont.tahoma_7_yellow.gameAA(var0, "Khu : " + zoneID, posMiniMapX - 1, posMiniMapY += 12, 0, mFont.tahoma_7);
                mFont.tahoma_7_yellow.gameAA(var0, "XY: " + Char.getMyChar().cx + "-" + Char.getMyChar().cy, posMiniMapX - 1, posMiniMapY += 12, 0, mFont.tahoma_7);

            }

        }
    }

    public static final void gameAC(mGraphics var0) {
        if (!GameCanvas.lowGraphic) {
            int var1;
            int var2;
            for (var1 = GameScr.gssx; var1 < GameScr.gssxe; ++var1) {
                for (var2 = GameScr.gssy; var2 < GameScr.gssye; ++var2) {
                    Image var3 = null;
                    if (tileID == 4) {
                        var3 = imgflowRiver;
                    } else {
                        var3 = imgWaterflow;
                    }

                    if ((gameAC(var1, var2) & 2048) == 2048) {
                        var0.gameAA(imgLeaf, var1, var2, 0);
                    }

                    if ((gameAC(var1, var2) & 64) == 64) {
                        var0.gameAA(var3, 0, (GameCanvas.gameTick % 8 >> 2) * 24, 24, 24, 0, var1 * size, var2 * size, 0);
                    }

                    if ((gameAC(var1, var2) & 256) == 256) {
                        var0.gameAA(gameBE, 0, (maps[var2 * tmw + var1] - 1) * size, 24, 24, 0, var1 * size, var2 * size, 0);
                    }
                }
            }

            if (tileID != 4 && GameCanvas.isTouch && GameCanvas.gameAH && GameScr.gssye >= tmh - 2) {
                for (var1 = GameScr.gssx; var1 < GameScr.gssxe; ++var1) {
                    var2 = tmh - 2;
                    int var5 = maps[var2 * tmw + var1] - 1;
                    int var4;
                    if ((gameAC(var1, var2) & 32) == 32) {
                        for (var4 = 1; var4 <= 4; ++var4) {
                            var0.gameAA(imgWaterfall, 0, 24 * (GameCanvas.gameTick % 4), 24, 24, 0, var1 * size, (var2 + var4) * size, 0);
                        }
                    } else {
                        if (mapID == 64) {
                            defaultSolidTile = 115;
                        }

                        if ((gameAC(var1, var2) & 2) == 2 || (gameAC(var1, var2) & 64) == 64) {
                            var5 = defaultSolidTile;
                        }

                        if (var5 >= 0) {
                            for (var4 = 1; var4 <= 4; ++var4) {
                                var0.gameAA(gameBE, 0, var5 * size, 24, 24, 0, var1 * size, (var2 + var4) * size, 0);
                            }
                        }
                    }
                }
            }

        }
    }

    private static int gameAB(int var0, int var1) {
        try {
            return maps[var1 * tmw + var0];
        } catch (Exception var2) {
            return 1000;
        }
    }

    private static int gameAC(int var0, int var1) {
        try {
            return types[var1 * tmw + var0];
        } catch (Exception var2) {
            return 1000;
        }
    }

    public static final int gameAA(int var0, int var1) {
        try {
            return types[var1 / size * tmw + var0 / size];
        } catch (Exception var2) {
            return 1000;
        }
    }

    public static final boolean gameAA(int var0, int var1, int var2) {
        try {
            return (types[var1 / size * tmw + var0 / size] & var2) == var2;
        } catch (Exception var3) {
            return false;
        }
    }

    public static final void gameAB(int var0, int var1, int var2) {
        int[] var10000 = types;
        int var10001 = var1 / size * tmw + var0 / size;
        var10000[var10001] |= 512;
    }

    public static final void gameAC(int var0, int var1, int var2) {
        int[] var10000 = types;
        int var10001 = var1 / size * tmw + var0 / size;
        var10000[var10001] &= -513;
    }

    public static final int gameAB(int var0) {
        return var0 / size * size;
    }

    public static final int gameAC(int var0) {
        return var0 / size * size;
    }

    public static void gameAE() {
        try {
            ByteArrayInputStream var0 = new ByteArrayInputStream(fieldCE[mapID]);
            DataInputStream var2;
            tmw = (char) (var2 = new DataInputStream(var0)).readUnsignedByte();
            tmh = (char) var2.readUnsignedByte();
            maps = new char[var2.available()];

            for (int var1 = 0; var1 < tmw * tmh; ++var1) {
                maps[var1] = (char) var2.readUnsignedByte();
            }

            types = new int[maps.length];
        } catch (IOException ex) {
            ex.printStackTrace();
        }
//        try {
//            InputStream var0 = null;
//            var0 = null;
//            var0 = "".getClass().getResourceAsStream("/map/" + mapID);
//            DataInputStream var2;
//            tmw = (char) (var2 = new DataInputStream(var0)).read();
//            tmh = (char) var2.read();
//            maps = new char[var2.available()];
//
//            for (int var1 = 0; var1 < tmw * tmh; ++var1) {
//                maps[var1] = (char) var2.read();
//            }
//
//            types = new int[maps.length];
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }

    public static void fieldAI() {
        for (int var0 = 1; var0 < fieldCF.length; ++var0) {
            fieldCF[var0] = GameCanvas.gameAC("/t/" + var0 + ".png");
            fieldCG[var0] = GameCanvas.gameAC("/t/mini_" + var0 + ".png");
        }

    }

    public static void gameAF() {
        gameBE = null;
        System.gc();
        gameBE = fieldCF[tileID];
        gameBF = fieldCG[tileID];
    }

    public static void fieldAE() {
        if (GameCanvas.gameTick % 700 == 0 && mapID != 0 && mapID > 4 && (mapID < 16 || mapID > 18) && (mapID < 24 || mapID > 27) && mapID != 22 && mapID != 33 && mapID != 34 && mapID != 38 && mapID != 57 && mapID != 58 && mapID != 60 && mapID != 68 && (mapID < 70 || mapID > 75) && mapID != 81) {
            if (mapID >= 39 && mapID <= 44 || mapID >= 46 && mapID <= 48 || mapID == 56 || mapID >= 62 && mapID <= 65) {
                return;
            }

            if (mapID == 29 || mapID == 35) {
                return;
            }

            if (mapID == 50 || mapID == 51 || mapID == 52) {
                return;
            }

            if (mapID == 64) {
                if (Res.gameAD(0, 8) % 2 == 0) {
                    return;
                }

                return;
            }
        }

    }

    public static boolean isLang(int var0) {
        return var0 == 10 || var0 == 17 || var0 == 22 || var0 == 32 || var0 == 38 || var0 == 43 || var0 == 48 || var0 == 138;
    }

    public static boolean isLangCo(int var0) {
        return var0 >= 134 && var0 <= 138;
    }

    public static boolean isTruong(int var0) {
        return var0 == 1 || var0 == 27 || var0 == 72;
    }

    public static boolean isHang(int var0) {
        return var0 == 91 || var0 == 92 || var0 == 93 || var0 == 94 || var0 == 95 || var0 == 96 || var0 == 97 || var0 == 105 || var0 == 106 || var0 == 107 || var0 == 108 || var0 == 109 || var0 == 114 || var0 == 115 || var0 == 116 || var0 == 125 || var0 == 126 || var0 == 127 || var0 == 128 || var0 == 157 || var0 == 158 || var0 == 159;
    }

    public static int fieldAH(int var0) {
        if (isHang(var0)) {
            switch (var0) {
                case 91:
                    return 92;
                case 92:
                    return 93;
                case 94:
                    return 95;
                case 95:
                    return 96;
                case 96:
                    return 97;
                case 105:
                    return 106;
                case 106:
                    return 107;
                case 107:
                    return 108;
                case 108:
                    return 109;
                case 114:
                    return 115;
                case 115:
                    return 116;
                case 125:
                    return 126;
                case 126:
                    return 127;
                case 127:
                    return 128;
                case 157:
                    return 158;
                case 158:
                    return 159;
                case 159:
                    return 157;
            }
        }

        return -1;
    }

    public static int fieldAI(int var0) {
        if (isHang(var0)) {
            switch (var0) {
                case 92:
                    return 91;
                case 93:
                    return 92;
                case 95:
                    return 94;
                case 96:
                    return 95;
                case 97:
                    return 96;
                case 106:
                    return 105;
                case 107:
                    return 106;
                case 108:
                    return 107;
                case 109:
                    return 108;
                case 115:
                    return 114;
                case 116:
                    return 115;
                case 126:
                    return 125;
                case 127:
                    return 126;
                case 128:
                    return 127;
                case 158:
                    return 157;
                case 159:
                    return 158;
            }
        }

        return -1;
    }

    public static void fieldAJ(int var0) {
        try {
            Waypoint var4;
            int var1 = (var4 = (Waypoint) vGo.elementAt(var0)).minX;
            int var2 = var4.minY;
            if (var4.minY != 0 && var4.maxY < pxh - 24) {
                if (var4.maxX <= pxw / 2) {
                    var1 = var4.maxX + 12;
                    var2 = var4.maxY;
                } else if (var4.minX >= pxw / 2) {
                    var1 = var4.minX - 12;
                    var2 = var4.maxY;
                }
            } else if (var4.maxY <= pxh / 2) {
                var1 = (var4.maxX + var4.minX) / 2;
                var2 = var4.maxY + 24;
            } else if (var4.minY >= pxh / 2) {
                var1 = (var4.maxX + var4.minX) / 2 + 24;
                var2 = var4.maxY - 48;
            }

            if (mapID != 114 && mapID != 115 && mapID != 116) {
                Char.fieldAC(var1, var2);
            } else {
                Char.fieldAE(var1, var2);
            }

            Thread.sleep(10L);
            Service.gI().requestChangeMap();
        } catch (InterruptedException var3) {
            var3.printStackTrace();
        }
    }

    public static void fieldAF() {
        fieldBF = true;
        synchronized (fieldCD) {
            try {
                fieldCD.wait(10000L);
            } catch (InterruptedException var1) {
            }

        }
    }

    public static void fieldAG() {
        if (fieldBF) {
            fieldBF = false;
            synchronized (fieldCD) {
                fieldCD.notifyAll();
            }
        }
    }

    public static boolean fieldAK(int var0) {
        short var1 = mapID;
        fieldBD = var0;
        int var2;
        int var3;
        int var5;
        MyVector var10;
        int var13;
        if (var1 >= 0 && var1 < fieldBZ.length && var0 >= 0 && var0 < fieldBZ.length && fieldBZ[var1].length > 0) {
            TaskOrder var6 = Char.fieldAM(0);

            for (var2 = 0; var2 < fieldCA.length; ++var2) {
                fieldCA[var2] = true;
                fieldCB[var2] = -1;
                fieldCC[var2] = -1;
            }

            fieldCB[var1] = 0;

            label382:
            while (true) {
                short var7;
                do {
                    do {
                        if (!fieldCA[var0]) {
                            MyVector var16;
                            (var16 = new MyVector()).addElement(new Integer(var0));

                            for (var5 = var0; var5 != var1; var5 = fieldCC[var5]) {
                                byte var12;
                                int var18;
                                if (isLang(var18 = fieldCC[var5])) {
                                    if (isLang(var5) && var5 != 138 && var5 != 162) {
                                        var12 = 1;
                                        if (var5 == 10) {
                                            var12 = 1;
                                        } else if (var5 == 17) {
                                            var12 = 2;
                                        } else if (var5 == 22) {
                                            var12 = 3;
                                        } else if (var5 == 32) {
                                            var12 = 4;
                                        } else if (var5 == 38) {
                                            var12 = 5;
                                        } else if (var5 == 43) {
                                            var12 = 6;
                                        } else if (var5 == 48) {
                                            var12 = 7;
                                        }

                                        var18 = var18 | Integer.MIN_VALUE | 117440512 | var12 << 20 & 15728640;
                                    } else if (var5 == 139) {
                                        var18 = var18 | Integer.MIN_VALUE | 83886080 | 2097152;
                                    }
                                } else if (isTruong(var18)) {
                                    if (isTruong(var5)) {
                                        var12 = 0;
                                        if (var5 == 1) {
                                            var12 = 0;
                                        } else if (var5 == 27) {
                                            var12 = 1;
                                        } else if (var5 == 72) {
                                            var12 = 2;
                                        }

                                        var18 = var18 | Integer.MIN_VALUE | 134217728 | var12 << 20 & 15728640;
                                    } else if (var6 != null && var5 == var6.mapId) {
                                        var13 = GameScr.fieldGH;
                                        var18 = var18 | Integer.MIN_VALUE | 419430400 | var13 << 20 & 15728640 | 196608;
                                    } else {
                                        switch (var5) {
                                            case 80:
                                                var18 = var18 | Integer.MIN_VALUE | 1048576 | 65536;
                                                break;
                                            case 91:
                                                var18 = var18 | Integer.MIN_VALUE | 2097152 | 65536;
                                                break;
                                            case 94:
                                                var18 = var18 | Integer.MIN_VALUE | 2097152 | 131072;
                                                break;
                                            case 98:
                                                var13 = GameScr.fieldGH + 2;
                                                var18 = var18 | Integer.MIN_VALUE | 419430400 | var13 << 20 & 15728640;
                                                break;
                                            case 104:
                                                var13 = GameScr.fieldGH + 2;
                                                var18 = var18 | Integer.MIN_VALUE | 419430400 | var13 << 20 & 15728640 | 65536;
                                                break;
                                            case 105:
                                                var18 = var18 | Integer.MIN_VALUE | 2097152 | 196608;
                                                break;
                                            case 113:
                                                var13 = GameScr.fieldGH + 3;
                                                var18 = var18 | Integer.MIN_VALUE | 419430400 | var13 << 20 & 15728640;
                                                break;
                                            case 114:
                                                var18 = var18 | Integer.MIN_VALUE | 2097152 | 262144;
                                                break;
                                            case 125:
                                                var18 = var18 | Integer.MIN_VALUE | 2097152 | 327680;
                                                break;
                                            case 139:
                                                var18 = var18 | Integer.MIN_VALUE | 83886080 | 2097152;
                                                break;
                                            case 157:
                                                var18 = var18 | Integer.MIN_VALUE | 2097152 | 393216;
                                        }
                                    }
                                }

                                var16.addElement(new Integer(var18));
                            }

                            MyVector var19 = new MyVector();

                            for (var2 = var16.size() - 1; var2 >= 0; --var2) {
                                var19.addElement(var16.elementAt(var2));
                            }

                            var10 = var19;
                            break label382;
                        }

                        var3 = -1;
                        var7 = -1;

                        for (var2 = 0; var2 < fieldBZ.length; ++var2) {
                            if (fieldCA[var2] && fieldCB[var2] != -1 && (fieldCB[var2] < var3 || var3 == -1)) {
                                var3 = fieldCB[var2];
                                var7 = (short) var2;
                            }
                        }

                        if (var7 == -1) {
                            var10 = null;
                            break label382;
                        }

                        fieldCA[var7] = false;
                        boolean var11 = isTruong(var7);
                        short[] var8 = fieldBZ[var7];

                        short var4;
                        for (var2 = 0; var2 < var8.length; ++var2) {
                            var4 = var8[var2];
                            if (fieldCA[var4]) {
                                boolean var14;
                                label346:
                                {
                                    if (Char.getMyChar().isHuman) {
                                        var5 = Char.getMyChar().ctaskId;
                                        if ((var4 == 1 || var4 == 27 || var4 == 72) && var5 < 6) {
                                            var14 = false;
                                            break label346;
                                        }

                                        if ((var4 == 10 || var4 == 32 || var4 == 48) && var5 < 17) {
                                            var14 = false;
                                            break label346;
                                        }

                                        if (var4 == 38 && var5 < 28) {
                                            var14 = false;
                                            break label346;
                                        }

                                        if (var4 == 43 && var5 < 33) {
                                            var14 = false;
                                            break label346;
                                        }

                                        if (var4 == 17 && var5 < 38) {
                                            var14 = false;
                                            break label346;
                                        }

                                        if (var4 == 7 && var5 < 15) {
                                            var14 = false;
                                            break label346;
                                        }
                                    }

                                    var14 = true;
                                }

                                if (var14 && (!var11 || !isTruong(var4) || Char.getMyChar().ctaskId >= 9) && (fieldCB[var4] == -1 || fieldCB[var4] > fieldCB[var7] + 1)) {
                                    fieldCB[var4] = fieldCB[var7] + 1;
                                    fieldCC[var4] = var7;
                                }
                            }
                        }

                        if (var11 && var6 != null && fieldCA[var6.mapId] && (fieldCB[var6.mapId] == -1 || fieldCB[var6.mapId] > fieldCB[var7] + 1)) {
                            fieldCB[var6.mapId] = fieldCB[var7] + 1;
                            fieldCC[var6.mapId] = var7;
                        }
//                        if (!Char.DungTTL || TileMap.fieldCB[162] != -1 && TileMap.fieldCB[162] <= TileMap.fieldCB[var7] + 1) {
//                            continue;
//                        }
//                        TileMap.fieldCB[162] = TileMap.fieldCB[var7] + 1;
//                        TileMap.fieldCC[162] = var7;
                        if (var11) {
                            var4 = (short) (GameScr.fieldGI ? 98 : 104);
                            if (fieldCB[var4] == -1 || fieldCB[var4] > fieldCB[var7] + 1) {
                                fieldCB[var4] = fieldCB[var7] + 1;
                                fieldCC[var4] = var7;
                            }
                        }
                    } while (!Char.isUseCL);
                } while (fieldCB[138] != -1 && fieldCB[138] <= fieldCB[var7] + 1);

                fieldCB[138] = fieldCB[var7] + 1;
                fieldCC[138] = var7;
            }
        } else {
            var10 = null;
        }

        MyVector var15 = var10;
        if (var10 == null) {
            InfoMe.gameAA("Không thể chuyển map!", 50, mFont.tahoma_7_yellow);
            return false;
        } else {
            fieldBE = true;

            try {
                var5 = mapID;

                for (var13 = 1; var13 < var15.size() && fieldBE && var5 == mapID; ++var13) {
                    var3 = ((Integer) var15.elementAt(var13 - 1)).intValue();
                    var5 = ((Integer) var15.elementAt(var13)).intValue() & '\uffff';
                    int var17;
                    if (var3 < 0) {
                        var17 = var3 >> 24 & 127;
                        var2 = var3 >> 20 & 15;
                        var3 = var3 >> 16 & 15;
                        GameScr.fieldAB(var17, var2, var3);
                    } else if ((var3 < 134 || var3 > 138) && var5 == 138) {
                        if (Char.getMyChar().cPk > 0) {
                            InfoMe.gameAA("Hiếu chiến quá cao!", 50, mFont.tahoma_7_yellow);
                            return false;
                        }

                        Item var21;
                        if ((var21 = Char.fieldAF(490)) == null || var21.template.id != 490) {
                            if (Char.isBuyCL && Char.getMyChar().luong >= 10) {
                                Service.gI().buyItem1(14, 28, 2);
                                LockGame.fieldAG();
                                return false;
                            }

                            InfoMe.gameAA("Không đủ cổ lệnh!", 50, mFont.tahoma_7_yellow);
                            return false;
                        }

                        System.out.println("Dung co lenh");
                        Service.gI().useItem(var21.indexUI);
//                    }else if ((var3 < 166 || var3 > 161) && var5 == 162) {
//                        if (Char.getMyChar().cPk > 0) {
//                            InfoMe.gameAA("Hiếu chiến quá cao!", 50, mFont.tahoma_7_yellow);
//                            return false;
//                        }
//
//                        Item var21;
//                        if ((var21 = Char.fieldAF(833)) == null || var21.template.id != 833) {
//                            if (Char.MuaTTL && Char.getMyChar().luong >= 10) {
//                                Service.gI().buyItem1(14, 37, 2);
//                                LockGame.fieldAG();
//                                return false;
//                            }
//
//                            InfoMe.gameAA("Không đủ cổ lệnh!", 50, mFont.tahoma_7_yellow);
//                            return false;
//                        }
//
//                        System.out.println("Dung co lenh");
//                        Service.gI().useItem(var21.indexUI);
                    } else if (var3 != 0 && var3 != 56 && var3 != 73) {
                        var2 = -1;

                        for (var17 = 0; var17 < fieldBZ[var3].length; ++var17) {
                            if (fieldBZ[var3][var17] == var5) {
                                var2 = var17;
                                break;
                            }
                        }

                        if (var2 == -1) {
                            InfoMe.gameAA("Không thể chuyển map!", 50, mFont.tahoma_7_yellow);
                            return false;
                        }

                        fieldAJ(var2);
                    } else {
                        Npc var20;
                        if ((var20 = (Npc) GameScr.vNpc.elementAt(0)) != null && var20.statusMe != 15) {
                            Char.fieldAC(var20.cx, var20.cy);
                            Char.getMyChar().npcFocus = var20;
                            Service.gI().requestItem(var20.template.npcTemplateId);
                            Service.gI().menu((byte) 0, var20.template.npcTemplateId, 0, 0);
                            Service.gI().getTask(var20.template.npcTemplateId, 0, -1);
                        }
                    }

                    if (mapID != var5) {
                        fieldAF();
                        cuong.sleep(1000L * (long) CodePhu.fieldAJ / 10L);
                    } else {
                        Thread.sleep(100L);
                    }
                }
            } catch (Exception var9) {
                var9.printStackTrace();
                return false;
            }

            fieldBE = false;
            return mapID == var0;
        }
    }

    public static void fieldAL(int var0) {
        GameCanvas.gameAL();
        (new Thread(new GoMap(var0))).start();
    }

    public static void fieldAM(int var0) {
        GameCanvas.gameAL();
        (new Thread(new NextMap(var0))).start();
    }

    public static int fieldAD(int var0, int var1) {
        var1 = gameAB(var1);
        if (!gameAA(var0, var1, 2)) {
            for (int var2 = 0; var2 < 7; ++var2) {
                int var3;
                if ((var3 = var1 - 48 + var2 * 24) > 0 && var3 < pxh && gameAA(var0, var3, 2)) {
                    return var3;
                }
            }
        }

        return var1;
    }

    public static int fieldAE(int var0, int var1) {
        if ((gameAA(var0, var1 - 16) & 16386) != 0) {
            var1 = gameAB(var1);

            int var2;
            int var3;
            for (var2 = 24; var2 < 240; var2 += 24) {
                var3 = gameAA(var0, var1 - var2);
                if (var1 - var2 > 0 && (var3 & 16386) == 0) {
                    return var1 - var2 + 24;
                }
            }

            for (var2 = 24; var2 < 120; var2 += 24) {
                var3 = gameAA(var0, var1 + var2);
                if (var1 + var2 < pxh && (var3 & 16386) == 0) {
                    return var1 + var2;
                }
            }
        }

        return var1;
    }

    public static boolean fieldAA(int var0, int var1, int[] var2) {
        var1 = gameAB(var1);
        if (gameAA(var0, var1, 2)) {
            var2[0] = var0;
            var2[1] = var1;
            return true;
        } else {
            for (int var3 = 0; var3 < 5; ++var3) {
                int var4 = var1 + var3 * 24;

                for (int var5 = 0; var5 < 5; ++var5) {
                    int var6 = var0 - 48 + var5 * 24;
                    if (var4 < pxh && var6 > 24 && var6 < pxw - 24 && gameAA(var6, var4, 2)) {
                        var2[0] = var6;
                        var2[1] = var4;
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static void fieldAA(int var0, InputStream var1) {
        try {
            if (fieldCE.length <= var0) {
                byte[][] var2 = new byte[var0 + 10][];
                System.arraycopy(fieldCE, 0, var2, 0, fieldCE.length);
                fieldCE = var2;
            }

            fieldCE[var0] = new byte[var1.available()];
            var1.read(fieldCE[var0]);
        } catch (Exception var3) {
        }
    }
}
