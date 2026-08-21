
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Enumeration;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Image;

public final class GameScr extends mScreen implements IActionListener, IChatable {

    public static GameScr instance;
    public static int gW;
    public static int gH;
    public static int gW2;
    private static int gameGQ;
    private static int gameGR;
    public static int gH23;
    public static int gH2;
    private static int gameGS;
    public static int gameAN;
    public static int gW6;
    public static int cmx;
    public static int cmy;
    private static int gameGT;
    private static int gameGU;
    private static int gameGV;
    private static int gameGW;
    public static int cmtoX;
    public static int cmtoY;
    private static int gameGX;
    private static int gameGY;
    public static int gssx;
    public static int gssy;
    public static int gssxe;
    public static int gssye;
    public static MyVector gameAX = new MyVector();
    private Command gameGZ;
    private Command gameHA;
    private Command gameHB;
    private Command gameHC;
    private Command gameHD;
    private Command gameHE;
    private Command gameHF;
    private Command gameHG;
    public static int mpPotion;
    public static int hpPotion;
    public static SkillPaint[] sks;
    public static Arrowpaint[] gameBB;
    public static Part[] parts;
    public static EffectCharPaint[] efs;
    private int gameHH = 0;
    private boolean gameHI = false;
    public static MyVector vMobSoul = new MyVector();
    public static MyVector vClan = new MyVector();
    public static MyVector vParty = new MyVector();
    public static MyVector vPtMap = new MyVector();
    public static MyVector vFriend = new MyVector();
    public static MyVector vList = new MyVector();
    public static MyVector vFriendWait = new MyVector();
    public static MyVector vEnemies = new MyVector();
    public static MyVector vCharInMap = new MyVector();
    public static MyVector vItemMap = new MyVector();
    public static MyVector vMobAttack = new MyVector();
    public static MyVector gameBP = new MyVector();
    public static MyVector vMob = new MyVector();
    public static MyVector vNpc = new MyVector();
    public static MyVector vBuNhin = new MyVector();
    private static MyVector gameHJ = new MyVector();
    public static NClass[] nClasss;
    private static int gameHK = 28;
    private static int gameHL = 0;
    public static int indexSelect = 0;
    public static int indexRow = -1;
    private static int gameHM;
    public static int indexMenu = 0;
    public static int indexCard = -1;
    private Item gameHN;
    public static ItemOptionTemplate[] iOptionTemplates;
    public static SkillOptionTemplate[] sOptionTemplates;
    private static Scroll gameHO = new Scroll();
    public static Scroll scrMain = new Scroll();
    public static Item[] arrItemNonNam;
    public static Item[] arrItemNonNu;
    public static Item[] arrItemAoNam;
    public static Item[] arrItemAoNu;
    public static Item[] arrItemGangTayNam;
    public static Item[] arrItemGangTayNu;
    public static Item[] arrItemQuanNam;
    public static Item[] arrItemQuanNu;
    public static Item[] arrItemGiayNam;
    public static Item[] arrItemGiayNu;
    public static Item[] arrItemLien;
    public static Item[] arrItemNhan;
    public static Item[] arrItemNgocBoi;
    public static Item[] arrItemPhu;
    public static Item[] arrItemWeapon;
    public static Item[] arrItemStack;
    public static Item[] arrItemStackLock;
    public static Item[] arrItemGrocery;
    public static Item[] arrItemGroceryLock;
    public static Item[] arrItemStore;
    public static Item[] arrItemElites;
    public static Item[] arrItemClanShop;
    public static Item[] arrItemBook;
    public static Item[] arrItemFashion;
    public static Item[] arrItemUpPeal;
    public static Item[] arrItemUpGrade;
    public static Item[] arrItemSplit;
    public static Item[] arrItemTradeMe;
    public static Item[] arrItemTradeOrder;
    public static Item[] arrItemConvert;
    public static ItemStands[] arrItemStands;
    public static short[] arrItemSprin;
    public int numSprinLeft;
    public static Item itemUpGrade;
    public static Item itemSplit;
    public static Item itemSell;
    private static boolean gameHP;
    private static boolean gameHQ;
    private static boolean gameHR;
    private static boolean gameHS;
    public static boolean isViewClanInvite;
    private static boolean gameHT;
    public static boolean gameDM;
    private static boolean gameHU;
    public static boolean gameDN;
    public static boolean isPaintAlert;
    private static boolean gameHV;
    private static boolean gameHW;
    private static boolean gameHX;
    public static boolean isPaintFriend;
    private static boolean gameHY;
    private static boolean gameHZ;
    public static boolean gameDQ;
    private static boolean gameKA;
    public static boolean isPaintInfoMe;
    private static boolean gameKB;
    private static boolean gameKC;
    private static boolean gameKD;
    private static boolean gameKE;
    private static boolean gameKF;
    private static boolean gameKG;
    private static boolean gameKH;
    private static boolean gameKI;
    private static boolean gameKJ;
    private static boolean gameKK;
    private static boolean gameKL;
    private static boolean gameKM;
    private static boolean gameKN;
    private static boolean gameKO;
    private static boolean gameKP;
    private static boolean gameKQ;
    private static boolean gameKR;
    private static boolean gameKS;
    private static boolean gameKT;
    private static boolean gameKU;
    private static boolean gameKV;
    private static boolean gameKW;
    private static boolean gameKX;
    private static boolean gameKY;
    private static boolean gameKZ;
    private static boolean gameMA;
    private static boolean gameMB;
    private static boolean gameMC;
    public static boolean isPaintTrade;
    private static boolean gameMD;
    public static boolean isPaintAuto;
    public static boolean isPaintMessage;
    private static boolean gameME;
    private static boolean gameMF;
    private static boolean gameMG;
    private static boolean gameMH;
    private static boolean gameMI;
    private static boolean gameMJ;
    private static boolean gameMK;
    private static boolean gameML;
    private static boolean gameMM;
    private static boolean gameMN;
    private static boolean gameMO;
    private static boolean gameMP;
    private static boolean gameMQ;
    private static boolean gameMR;
    public static Char currentCharViewInfo;
    public static long[] exps;
    public static int[] crystals;
    public static int[] upClothe;
    public static int[] upAdorn;
    public static int[] upWeapon;
    public static int[] coinUpCrystals;
    public static int[] coinUpClothes;
    public static int[] coinUpAdorns;
    public static int[] coinUpWeapons;
    public static int[] maxPercents;
    public static int[] goldUps;
    private static int[] gameMS;
    private int gameMT = 6;
    public int[] zones;
    private int[] gameMV;
    public int typeTrade = 0;
    public int typeTradeOrder = 0;
    public int coinTrade = 0;
    public int coinTradeOrder = 0;
    public int timeTrade = 0;
    private int gameMW = 0;
    private int gameMX = 0;
    private int gameMY = 0;
    private int gameMZ = -1;
    public int cLastFocusID = -1;
    public int cPreFocusID = -1;
    private boolean gameNA;
    public static byte[][] tasks;
    public static byte[][] mapTasks;
    private MyVector gameNB;
    private String gameNC;
    private TField gameND = null;
    public static byte vcData;
    public static byte vcMap;
    public static byte vcSkill;
    public static byte vcItem;
    public static byte vsData;
    public static byte vsMap;
    public static byte vsSkill;
    public static byte vsItem;
    private static Image gameNE;
    private static Image gameNF;
    private static Image gameNG;
    private static Image gameNH;
    private static Image gameNI;
    private static Image gameNJ;
    private static Image gameNK;
    private static Image gameNL;
    private static Image gameNM;
    private static Image gameNN;
    private static Image gameNO;
    private static Image gameNP;
    private static Image gameNQ;
    private static Image gameNR;
    public static Image gameEY;
    public static Image gameEZ;
    public static Image imgLbtn;
    public static Image imgLbtnFocus;
    private static Image gameNS;
    public static Image imgMatcho;
    public static Image imgFiremoto;
    public String tradeName = "";
    public String tradeItemName = "";
    public int timeLengthMap;
    public int timeStartMap;
    private static byte gameNT;
    public static byte typeActive;
    private int[] gameNU = new int[2];
    private int[] gameNV = new int[2];
    private int[] gameNW;
    private int[] gameNX;
    public long timePoint;
    public String[] YenCards = new String[]{"10000", "20000", "30000", "50000", "100000", "200000", "500000", "1000000", "5000000"};
    public int yenTemp;
    public int typeba;
    public String[] yenValue;
    public static MyVector vItemTreeFront;
    public static MyVector vItemTreeBehind;
    public static MyVector vItemTreeBetwen;
    public static Image gameFR;
    public static Image gameFS;
    public static Image imgEffMob1;
    public static Image imgEffMob2;
    public static Image imgEffMob3;
    public static Image imgsmokeFollow;
    public static boolean isUseitemAuto;
    public static boolean isAllmap;
    private static Skill[] gameNY;
    private static Skill[] gameNZ;
    private static byte[] fieldLW;
    private static byte[] fieldLX;
    private Command gamePA;
    private Command gamePB;
    private Command gamePC;
    private Command gamePD;
    private Command gamePE;
    private Command gamePF;
    private static byte gamePG;
    static int shaking;
    static int count;
    private long gamePH;
    public static int auto;
    public boolean gameGC = false;
    private int gamePI = 0;
    private int gamePJ = -1;
    private long gamePK;
    private int gamePL = 0;
    private static int gamePM;
    private static int gamePN;
    private static int gamePO;
    private static int gamePP;
    private static int gamePQ;
    private static int gamePR;
    private static int gamePS;
    private static int gamePT;
    private static int gamePU;
    private static int gamePV;
    private static int gamePW;
    private static int gamePX;
    private static int gamePY;
    private static int gamePZ;
    private static int gameQA;
    private static int gameQB;
    private static int gameQC;
    private static int gameQD;
    private static int[] gameQE;
    private static int[] gameQF;
    private static int gameQG;
    private static int gameQH;
    private static int gameQI;
    private static String[] gameQJ;
    private static int[] gameQK;
    private static int[] gameQL;
    private static int[] gameQM;
    private static int[] gameQN;
    private static int[] gameQO;
    private static int[] gameQP;
    private static int[] gameQQ;
    private static int[] gameQR;
    private static int[] gameQS;
    private static int[] gameQT;
    private static int[] gameQU;
    private static Image[] gameQV;
    private static int gameQW;
    private static int gameQX;
    private static int gameQY;
    private static int gameQZ;
    private static int gameRA;
    private static int gameRB;
    private static int gameRC;
    private static int gameRD;
    private static int gameRE;
    private static int gameRF;
    private static int gameRG;
    private static int gameRH;
    private static Image[] gameRI;
    public static int popupY;
    public static int popupX;
    private static int gameRJ;
    private static boolean gameRK;
    private int gameRL;
    private String[] gameRM;
    private String[] gameRN;
    private int gameRO;
    private Command gameRP;
    private Command gameRQ;
    private Command gameRR;
    private Command gameRS;
    private Command gameRT;
    private Command gameRU;
    private Command gameRV;
    private Command gameRW;
    private Command gameRX;
    private Command gameRY;
    private Command gameRZ;
    private Command gameSA;
    private Command gameSB;
    private Command gameSC;
    private Command gameSD;
    private Command gameSE;
    private Command gameSF;
    private Command gameSG;
    private Command gameSH;
    private Command gameSI;
    private Command gameSJ;
    private Command gameSK;
    private Command gameSL;
    private Command gameSM;
    private Command gameSN;
    private Command gameSO;
    private Command gameSP;
    private Command gameSQ;
    private Command gameSR;
    private Command gameSS;
    private Command gameST;
    private Command gameSU;
    private Command gameSV;
    private Command gameSW;
    private Command gameSX;
    private Command gameSY;
    private Command gameSZ;
    private Command gameTA;
    private String gameTB;
    private String gameTC;
    private static int gameTD;
    private static int gameTE;
    public static int popupW;
    public static int popupH;
    private static int gameTF;
    private static int gameTG;
    private static int gameTH;
    private static int gameTI;
    private static int gameTJ;
    private static int gameTK;
    private int gameTL;
    private int[] gameTM;
    private int[][] gameTN;
    private int[] gameTO;
    private static String gameTP;
    public static int indexEff;
    public static EffectCharPaint effUpok;
    private static int gameTQ;
    private static int gameTR;
    private static int gameTS;
    private static int gameTT;
    public Command cmdDead;
    private Command gameTU;
    private Command gameTV;
    private Command gameTW;
    private Command gameTX;
    private Command gameTY;
    private Command gameTZ;
    private Command gameUA;
    public static String svTitle;
    public static String svAction;
    private int gameUB;
    private int gameUC;
    private String gameUD;
    private long gameUE;
    private static long gameUF;
    private static int gameUG;
    private static int[] gameUH;
    private static int[] gameUI;
    private static boolean gameUJ;
    public static int pointCenterX;
    public static int pointCenterY;
    public static int rangeSearch;
    private short gameUK;
    private short gameUL;
    private short gameUM;
    private String gameUN;
    private String gameUO;
    private String gameUP;
    private String gameUQ;
    private String gameUR;
    private long gameUS;
    private boolean gameUT;
    public static byte gameGP;
    private mFont gameUU;
    private byte[] gameUV;
    private byte gameUW;
    private MyVector gameUX;
    private int gameUY;
    private int gameUZ;
    private int gameVA;
    private int gameVB;
    private int gameVC;
    private int gameVD;
    private int gameVE;
    private int gameVF;
    private int gameVG;
    private int gameVH;
    private int gameVI;
    private int gameVJ;
    private int gameVK;
    private int gameVL;
    private int gameVM;
    private int gameVN;
    private String gameVO;
    private int[] gameVP;
    private int[] gameVQ;
    private int gameVR;
    private int[][] gameVS;
    private static int gameVT;
    public static boolean fieldGE;
    private static InfoDlg fieldTE;
    private static MyVector fieldTH;
    public static int fieldGH;
    public static boolean fieldGI;
    private static boolean fieldTF;
    private static boolean fieldTG;
    private static boolean ItemThrow;
    public String[] fieldGF = new String[]{"LEFT", "UP", "RIGHT"};

    static {
        fieldGE = true;
        fieldTE = new InfoDlg();

        fieldTH = new MyVector();
        fieldGH = 1;
        fieldTF = false;
        fieldTG = false;
        ItemThrow = false;
        fieldGI = true;
        new MyVector();
        gameHR = false;
        gameHS = false;
        isViewClanInvite = true;
        gameDM = false;
        gameHU = false;
        gameDN = false;
        isPaintAlert = false;
        gameHV = false;
        gameHW = false;
        gameHX = false;
        isPaintFriend = false;
        gameHY = false;
        gameHZ = false;
        gameDQ = false;
        gameKA = false;
        isPaintInfoMe = false;
        gameKB = false;
        gameKC = false;
        gameKD = false;
        gameKE = false;
        gameKF = false;
        gameKG = false;
        gameKH = false;
        gameKI = false;
        gameKJ = false;
        gameKK = false;
        gameKL = false;
        gameKM = false;
        gameKN = false;
        gameKO = false;
        gameKP = false;
        gameKQ = false;
        gameKR = false;
        gameKS = false;
        gameKT = false;
        gameKU = false;
        gameKV = false;
        gameKW = false;
        gameKX = false;
        gameKY = false;
        gameKZ = false;
        gameMA = false;
        gameMB = false;
        gameMC = false;
        isPaintTrade = false;
        gameMD = false;
        isPaintAuto = false;
        isPaintMessage = false;
        gameME = false;
        gameMF = false;
        gameMG = false;
        gameMH = false;
        gameMP = false;
        gameMQ = false;
        gameMR = false;
        gameMS = new int[]{0, 5000, 40000, 135000, 320000, 625000, 1080000, 1715000, 2560000, 3645000, 5000000};
        gameNT = 0;
        typeActive = 0;
        vItemTreeFront = new MyVector();
        vItemTreeBehind = new MyVector();
        vItemTreeBetwen = new MyVector();
        isUseitemAuto = false;
        isAllmap = false;
        (GameCanvas.gameBZ = new Image[2])[0] = GameCanvas.gameAC("/m1.png");
        GameCanvas.gameBZ[1] = GameCanvas.gameAC("/m2.png");
        GameCanvas.gameBX = GameCanvas.gameAC("/plus12.png");
        GameCanvas.imgBGitem = GameCanvas.gameAC("/bgitem.png");
        GameCanvas.imgBGitem0 = GameCanvas.gameAC("/bgitem0.png");
        GameCanvas.imgBGitem1 = GameCanvas.gameAC("/bgitem1.png");
        GameCanvas.imgBGitem2 = GameCanvas.gameAC("/bgitem2.png");
        GameCanvas.imgBGitem3 = GameCanvas.gameAC("/bgitem3.png");
        GameCanvas.imgBGitem4 = GameCanvas.gameAC("/bgitem4.png");
        GameCanvas.bgitem1x1 = GameCanvas.gameAC("/bgitem4x1.png");
        GameCanvas.bgitem1x2 = GameCanvas.gameAC("/bgitem4x2.png");
        GameCanvas.bgitem1x3 = GameCanvas.gameAC("/bgitem4x3.png");
        GameCanvas.bgitem1x4 = GameCanvas.gameAC("/bbgitem4x4.png");

        GameCanvas.bgitem2x1 = GameCanvas.gameAC("/bgitem8x1.png");
        GameCanvas.bgitem2x2 = GameCanvas.gameAC("/bgitem8x2.png");
        GameCanvas.bgitem2x3 = GameCanvas.gameAC("/bgitem8x3.png");
        GameCanvas.bgitem2x4 = GameCanvas.gameAC("/bgitem8x4.png");

        GameCanvas.bgitem3x1 = GameCanvas.gameAC("/bgitem11x1.png");
        GameCanvas.bgitem3x2 = GameCanvas.gameAC("/bgitem11x2.png");
        GameCanvas.bgitem3x3 = GameCanvas.gameAC("/bgitem11x3.png");
        GameCanvas.bgitem3x4 = GameCanvas.gameAC("/bgitem11x4.png");

        GameCanvas.bgitem4x1 = GameCanvas.gameAC("/bgitem14x1.png");
        GameCanvas.bgitem4x2 = GameCanvas.gameAC("/bgitem14x2.png");
        GameCanvas.bgitem4x3 = GameCanvas.gameAC("/bgitem14x3.png");
        GameCanvas.bgitem4x4 = GameCanvas.gameAC("/bgitem14x4.png");

        GameCanvas.bgitem5x1 = GameCanvas.gameAC("/bgitem16x1.png");
        GameCanvas.bgitem5x2 = GameCanvas.gameAC("/bgitem16x2.png");
        GameCanvas.bgitem5x3 = GameCanvas.gameAC("/bgitem16x3.png");
        GameCanvas.bgitem5x4 = GameCanvas.gameAC("/bgitem16x4.png");

        GameCanvas.bgitem6x1 = GameCanvas.gameAC("/bgitem18x1.png");
        GameCanvas.bgitem6x2 = GameCanvas.gameAC("/bgitem18x2.png");
        GameCanvas.bgitem6x3 = GameCanvas.gameAC("/bgitem18x3.png");
        GameCanvas.bgitem6x4 = GameCanvas.gameAC("/bgitem18x4.png");

        GameCanvas.bgitem7x1 = GameCanvas.gameAC("/bgitem20x1.png");
        GameCanvas.bgitem7x2 = GameCanvas.gameAC("/bgitem20x2.png");
        GameCanvas.bgitem7x3 = GameCanvas.gameAC("/bgitem20x3.png");
        GameCanvas.bgitem7x4 = GameCanvas.gameAC("/bgitem20x4.png");

        GameCanvas.gameBY = GameCanvas.gameAC("/Big4.png");
        gameFR = GameCanvas.gameAC("/trung1.png");
        gameNS = GameCanvas.gameAC("/u/select.png");
        gameEY = GameCanvas.gameAC("/hd/tf.png");
        gameFS = GameCanvas.gameAC("/eff/g132.png");
        imgEffMob1 = GameCanvas.gameAC("/eff/g10.png");
        imgEffMob2 = GameCanvas.gameAC("/eff/g6.png");
        imgEffMob3 = GameCanvas.gameAC("/eff/g99.png");
        imgsmokeFollow = GameCanvas.gameAC("/eff/g9.png");
        if (GameCanvas.isTouch) {
            gameNL = GameCanvas.gameAC("/hd/button.png");
            gameNM = GameCanvas.gameAC("/hd/button2.png");
            gameNN = GameCanvas.gameAC("/hd/hpp.png");
            gameNO = GameCanvas.gameAC("/hd/mpp.png");
            gameNP = GameCanvas.gameAC("/hd/right.png");
            gameNQ = GameCanvas.gameAC("/hd/right2.png");
            gameNR = GameCanvas.gameAC("/hd/skill.png");
            imgLbtnFocus = GameCanvas.gameAC("/hd/btnlf.png");
            gameNG = GameCanvas.gameAC("/hd/arrow.png");
            gameNH = GameCanvas.gameAC("/hd/arrow2.png");
            gameNI = GameCanvas.gameAC("/hd/chat.png");
            gameNK = GameCanvas.gameAC("/hd/focus.png");
            gameNJ = GameCanvas.gameAC("/hd/menu.png");
            gameNE = GameCanvas.gameAC("/hd/topbar.png");
            gameNF = GameCanvas.gameAC("/hd/transparent.png");
            gameEZ = GameCanvas.gameAC("/hd/mapborder.png");
            imgLbtn = GameCanvas.gameAC("/hd/btnl.png");
        }

        imgMatcho = GameCanvas.gameAC("/hd/mat.png");
        imgFiremoto = GameCanvas.gameAC("/hd/lua.png");
        byte[] var0 = RMS.gameAA("dataVersion");
        byte[] var1 = RMS.gameAA("mapVersion");
        byte[] var2 = RMS.gameAA("skillVersion");
        byte[] var3 = RMS.gameAA("itemVersion");
        if (var0 != null) {
            vcData = var0[0];
        }

        if (var1 != null) {
            vcMap = var1[0];
        }

        if (var2 != null) {
            vcSkill = var2[0];
        }

        if (var3 != null) {
            vcItem = var3[0];
        }

        gameNY = new Skill[3];
        gameNZ = new Skill[5];
        count = 0;
        gameQK = new int[5];
        gameQL = new int[5];
        gameQM = new int[5];
        gameQN = new int[5];
        gameQO = new int[5];
        gameQJ = new String[5];
        gameQP = new int[8];

        for (int var4 = 0; var4 < 5; ++var4) {
            gameQO[var4] = -1;
        }

        gameRK = false;
        popupW = 140;
        popupH = 160;
        gameTJ = 6;
        gameTP = "Shop Online";
        new MyVector();
        indexEff = 0;
        svTitle = "";
        svAction = "";
        gameUJ = false;
        gameGP = 0;
        gameVT = 0;
    }

    public static long gameAB(int var0) {
        long var1 = 0L;

        for (int var3 = 0; var3 <= var0; ++var3) {
            var1 += exps[var3];
        }

        return var1;
    }

    public static void setPasswordTest() {
        vCharInMap.removeAllElements();
        vItemMap.removeAllElements();
        vMobSoul.removeAllElements();
        Effect2.vEffect2.removeAllElements();
        Effect2.vAnimateEffect.removeAllElements();
        Effect2.vEffect2Outside.removeAllElements();
        vMobAttack.removeAllElements();
        gameBP.removeAllElements();
        vMob.removeAllElements();
        vNpc.removeAllElements();
        vBuNhin.removeAllElements();
        Char.getMyChar().vMovePoints.removeAllElements();
    }

    public static void gameAG() {
        Service.gI().loadRMS("KSkill");
        Service.gI().loadRMS("OSkill");
        Service.gI().loadRMS("CSkill");
    }

    public static void gameAH() {
        Service.gI().loadRMS("KSkill");
        Service.gI().loadRMS("OSkill");
        Service.gI().loadRMS("CSkill");
    }

    public final void gameAA(byte[] var0) {
        gameNZ = new Skill[5];
        int var1;
        int var2;
        Skill var3;
        if (var0 == null) {
            if (fieldLW != null) {
                for (var1 = 0; var1 < fieldLW.length; ++var1) {
                    for (var2 = 0; var2 < Char.getMyChar().vSkillFight.size(); ++var2) {
                        if ((var3 = (Skill) Char.getMyChar().vSkillFight.elementAt(var2)).template.id == fieldLW[var1]) {
                            gameNZ[var1] = var3;
                            break;
                        }
                    }
                }
            } else {
                for (var1 = 0; var1 < gameNZ.length && var1 < Char.getMyChar().vSkillFight.size(); ++var1) {
                    Skill var4 = (Skill) Char.getMyChar().vSkillFight.elementAt(var1);
                    gameNZ[var1] = var4;
                }
            }

            gameBP();
        } else {
            for (var1 = 0; var1 < var0.length; ++var1) {
                for (var2 = 0; var2 < Char.getMyChar().vSkillFight.size(); ++var2) {
                    if ((var3 = (Skill) Char.getMyChar().vSkillFight.elementAt(var2)).template.id == var0[var1]) {
                        gameNZ[var1] = var3;
                        break;
                    }
                }
            }

        }
    }

    public final void gameAB(byte[] var0) {
        gameNY = new Skill[3];
        int var1;
        int var2;
        Skill var3;
        if (var0 == null) {
            if (fieldLX != null) {
                for (var1 = 0; var1 < fieldLX.length; ++var1) {
                    for (var2 = 0; var2 < Char.getMyChar().vSkillFight.size(); ++var2) {
                        if ((var3 = (Skill) Char.getMyChar().vSkillFight.elementAt(var2)).template.id == fieldLX[var1]) {
                            gameNY[var1] = var3;
                            break;
                        }
                    }
                }
            } else {
                for (var1 = 0; var1 < gameNY.length && var1 < Char.getMyChar().vSkillFight.size(); ++var1) {
                    Skill var4 = (Skill) Char.getMyChar().vSkillFight.elementAt(var1);
                    gameNY[var1] = var4;
                }
            }

            gameBQ();
        } else {
            for (var1 = 0; var1 < var0.length; ++var1) {
                for (var2 = 0; var2 < Char.getMyChar().vSkillFight.size(); ++var2) {
                    if ((var3 = (Skill) Char.getMyChar().vSkillFight.elementAt(var2)).template.id == var0[var1]) {
                        gameNY[var1] = var3;
                        break;
                    }
                }
            }

        }
    }

    public final void gameAC(byte[] var1) {
        if (var1 != null && var1.length != 0) {
            for (int var3 = 0; var3 < Char.getMyChar().vSkillFight.size(); ++var3) {
                Skill var2;
                if ((var2 = (Skill) Char.getMyChar().vSkillFight.elementAt(var3)).template.id == var1[0]) {
                    Char.getMyChar().myskill = var2;
                    Char.getMyChar().gameGK = var2;
                    break;
                }
            }
        } else if (Char.getMyChar().vSkillFight.size() > 0) {
            Char.getMyChar().myskill = (Skill) Char.getMyChar().vSkillFight.elementAt(0);
        }

        if (Char.getMyChar().myskill != null) {
            Service.gI().selectSkill(Char.getMyChar().myskill.template.id);
            Char.getMyChar();
        }

    }

    private static void gameAA(SkillTemplate var0) {
        Skill var5 = Char.getMyChar().gameAA(var0);
        MyVector var1 = new MyVector();

        for (int var2 = 0; var2 < 5; ++var2) {
            boolean var3 = false;
            if (gameNZ[var2] == null) {
                var3 = true;
            }

            Object[] var4;
            (var4 = new Object[2])[0] = var5;
            var4[1] = String.valueOf(var2);
            var1.addElement(new Command(mResources.gameGC + " " + (var2 + 1), 11120, var4));
            if (var3) {
                break;
            }
        }

        GameCanvas.menu.gameAA(var1);
    }

    private static void gameAB(SkillTemplate var0) {
        Skill var5 = Char.getMyChar().gameAA(var0);
        String[] var1 = TField.isQwerty ? mResources.gameGD : mResources.gameGE;
        MyVector var2 = new MyVector();

        for (int var3 = 0; var3 < 3; ++var3) {
            Object[] var4;
            (var4 = new Object[2])[0] = var5;
            var4[1] = String.valueOf(var3);
            var2.addElement(new Command(var1[var3], 11121, var4));
        }

        GameCanvas.menu.gameAA(var2);
    }

    private static void gameBP() {
        byte[] var0 = new byte[gameNZ.length];

        for (int var1 = 0; var1 < gameNZ.length; ++var1) {
            if (gameNZ[var1] == null) {
                var0[var1] = -1;
            } else {
                var0[var1] = gameNZ[var1].template.id;
            }
        }

        if (Char.getMyChar().gameBA()) {
            Service.gI().saveRms("OSkill", var0, (byte) 0);
        } else {
            Service.gI().saveRms("OSkill", var0, (byte) 1);
        }
    }

    private static void gameBQ() {
        byte[] var0 = new byte[gameNY.length];

        for (int var1 = 0; var1 < gameNY.length; ++var1) {
            if (gameNY[var1] == null) {
                var0[var1] = -1;
            } else {
                var0[var1] = gameNY[var1].template.id;
            }
        }

        if (Char.getMyChar().gameBA()) {
            Service.gI().saveRms("KSkill", var0, (byte) 0);
        } else {
            Service.gI().saveRms("KSkill", var0, (byte) 1);
        }
    }

    public final void gameAA(Skill var1) {
        if (var1.template.type != 0) {
            int var2;
            for (var2 = 0; var2 < gameNZ.length; ++var2) {
                if (gameNZ[var2] == null) {
                    gameNZ[var2] = var1;
                    break;
                }
            }

            for (var2 = 0; var2 < gameNY.length; ++var2) {
                if (gameNY[var2] == null) {
                    gameNY[var2] = var1;
                    break;
                }
            }

            if (Char.getMyChar().myskill == null) {
                Char.getMyChar().myskill = var1;
                if (Code.fieldAB instanceof As20) {
                    Auto.fieldAL = var1;
                }
            }

            gameBQ();
            gameBP();
        }
    }

    public static boolean gameAI() {
        for (int var0 = Char.getMyChar().arrItemBag.length - 1; var0 >= 0; --var0) {
            if (Char.getMyChar().arrItemBag[var0] == null) {
                return false;
            }
        }

        return true;
    }

    public static void gameAA(String[] var0, Npc var1) {
        MyVector var2 = new MyVector();

        for (int var3 = 0; var3 < var0.length; ++var3) {
            var2.addElement(new Command(var0[var3], 11057, var1));
        }

        GameCanvas.menu.gameAA(var2);
    }

    public void gameBR() {
        currentCharViewInfo = Char.getMyChar();
        indexMenu = 0;
        this.gameCE();
    }

    private void gameBS() {
        currentCharViewInfo = Char.getMyChar();
        indexMenu = 1;
        this.gameCE();
    }

    private void gameBT() {
        currentCharViewInfo = Char.getMyChar();
        indexMenu = 2;
        this.gameCE();
    }

    private void gameBU() {
        currentCharViewInfo = Char.getMyChar();
        indexMenu = 3;
        this.gameCE();
    }

    private void gameBV() {
        currentCharViewInfo = Char.getMyChar();
        indexMenu = 4;
        this.gameCE();
    }

    private void gameBW() {
        currentCharViewInfo = Char.getMyChar();
        indexMenu = 5;
        this.gameCE();
    }

    private void gameBX() {
        currentCharViewInfo = Char.getMyChar();
        indexMenu = 6;
        gameVT = 0;
        this.gameCE();
    }

    private void gameBY() {
        currentCharViewInfo = Char.getMyChar();
        indexMenu = 6;
        gameVT = 1;
        this.gameCE();
    }

    private void gameBZ() {
        currentCharViewInfo = Char.getMyChar();
        indexMenu = 6;
        gameVT = 2;
        this.gameCE();
    }

    public static void gameAJ() {
        DataInputStream var0 = null;

        try {
            short var1;
            parts = new Part[var1 = (var0 = new DataInputStream(new ByteArrayInputStream(RMS.gameAA("nj_part")))).readShort()];

            for (int var2 = 0; var2 < var1; ++var2) {
                byte var3 = var0.readByte();
                parts[var2] = new Part(var3);

                for (int var11 = 0; var11 < parts[var2].pi.length; ++var11) {
                    parts[var2].pi[var11] = new PartImage();
                    parts[var2].pi[var11].id = var0.readShort();
                    parts[var2].pi[var11].dx = var0.readByte();
                    parts[var2].pi[var11].dy = var0.readByte();
                }
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        } finally {
            try {
                var0.close();
            } catch (IOException var8) {
                var8.printStackTrace();
            }

        }

    }

    public static void gameAK() {
        DataInputStream var0 = null;

        try {
            short var1;
            efs = new EffectCharPaint[var1 = (var0 = new DataInputStream(new ByteArrayInputStream(RMS.gameAA("nj_effect")))).readShort()];

            for (int var2 = 0; var2 < var1; ++var2) {
                efs[var2] = new EffectCharPaint();
                efs[var2].idEf = var0.readShort();
                efs[var2].arrEfInfo = new EffectInfoPaint[var0.readByte()];

                for (int var3 = 0; var3 < efs[var2].arrEfInfo.length; ++var3) {
                    efs[var2].arrEfInfo[var3] = new EffectInfoPaint();
                    efs[var2].arrEfInfo[var3].idImg = var0.readShort();
                    efs[var2].arrEfInfo[var3].dx = var0.readByte();
                    efs[var2].arrEfInfo[var3].dy = var0.readByte();
                }
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        } finally {
            try {
                var0.close();
            } catch (IOException var8) {
                var8.printStackTrace();
            }

        }

    }

    public static void gameAL() {
        DataInputStream var0 = null;

        try {
            short var1;
            gameBB = new Arrowpaint[var1 = (var0 = new DataInputStream(new ByteArrayInputStream(RMS.gameAA("nj_arrow")))).readShort()];

            for (int var2 = 0; var2 < var1; ++var2) {
                gameBB[var2] = new Arrowpaint();
                var0.readShort();
                gameBB[var2].imgId[0] = var0.readShort();
                gameBB[var2].imgId[1] = var0.readShort();
                gameBB[var2].imgId[2] = var0.readShort();
            }
        } catch (Exception var8) {
            var8.printStackTrace();
        } finally {
            try {
                var0.close();
            } catch (IOException var7) {
                var7.printStackTrace();
            }

        }

    }

    public static void gameAM() {
        DataInputStream var0 = null;

        try {
            short var1 = (var0 = new DataInputStream(new ByteArrayInputStream(RMS.gameAA("nj_skill")))).readShort();
            int var2 = 0;

            int var3;
            for (var3 = 0; var3 < nClasss.length; ++var3) {
                var2 += nClasss[var3].skillTemplates.length;
            }

            sks = new SkillPaint[var2];

            for (var3 = 0; var3 < var1; ++var3) {
                short var12 = var0.readShort();
                sks[var12] = new SkillPaint();
                sks[var12].id = var0.readShort();
                var0.readByte();
                sks[var12].skillStand = new SkillInfoPaint[var0.readByte()];

                int var4;
                for (var4 = 0; var4 < sks[var12].skillStand.length; ++var4) {
                    sks[var12].skillStand[var4] = new SkillInfoPaint();
                    sks[var12].skillStand[var4].status = var0.readByte();
                    sks[var12].skillStand[var4].effS0Id = var0.readShort();
                    sks[var12].skillStand[var4].e0dx = var0.readShort();
                    sks[var12].skillStand[var4].e0dy = var0.readShort();
                    sks[var12].skillStand[var4].effS1Id = var0.readShort();
                    sks[var12].skillStand[var4].e1dx = var0.readShort();
                    sks[var12].skillStand[var4].e1dy = var0.readShort();
                    sks[var12].skillStand[var4].effS2Id = var0.readShort();
                    sks[var12].skillStand[var4].e2dx = var0.readShort();
                    sks[var12].skillStand[var4].e2dy = var0.readShort();
                    sks[var12].skillStand[var4].arrowId = var0.readShort();
                    sks[var12].skillStand[var4].adx = var0.readShort();
                    sks[var12].skillStand[var4].ady = var0.readShort();
                }

                sks[var12].skillfly = new SkillInfoPaint[var0.readByte()];

                for (var4 = 0; var4 < sks[var12].skillfly.length; ++var4) {
                    sks[var12].skillfly[var4] = new SkillInfoPaint();
                    sks[var12].skillfly[var4].status = var0.readByte();
                    sks[var12].skillfly[var4].effS0Id = var0.readShort();
                    sks[var12].skillfly[var4].e0dx = var0.readShort();
                    sks[var12].skillfly[var4].e0dy = var0.readShort();
                    sks[var12].skillfly[var4].effS1Id = var0.readShort();
                    sks[var12].skillfly[var4].e1dx = var0.readShort();
                    sks[var12].skillfly[var4].e1dy = var0.readShort();
                    sks[var12].skillfly[var4].effS2Id = var0.readShort();
                    sks[var12].skillfly[var4].e2dx = var0.readShort();
                    sks[var12].skillfly[var4].e2dy = var0.readShort();
                    sks[var12].skillfly[var4].arrowId = var0.readShort();
                    sks[var12].skillfly[var4].adx = var0.readShort();
                    sks[var12].skillfly[var4].ady = var0.readShort();
                }
            }
        } catch (Exception var10) {
            var10.printStackTrace();
        } finally {
            try {
                var0.close();
            } catch (IOException var9) {
                var9.printStackTrace();
            }

        }

    }

    public static void gameAA(long var0, boolean var2) {
        long var6 = var0;
        boolean var8 = false;

        int var9;
        for (var9 = 0; var9 < exps.length && var6 >= exps[var9]; ++var9) {
            var6 -= exps[var9];
        }

        long[] var10 = new long[]{(long) var9, var6};
        Char.getMyChar().clevel = (int) var10[0];
        Char.getMyChar().gameBE = var10[1];
    }

    public static GameScr gI() {
        if (instance == null) {
            instance = new GameScr();
        }

        return instance;
    }

    public static void gameAO() {
        instance = null;
        arrItemTradeOrder = null;
        arrItemTradeMe = null;
        arrItemSplit = null;
        arrItemUpGrade = null;
        arrItemUpPeal = null;
        itemSplit = null;
        itemUpGrade = null;
    }

    public final void gameAP() {
        if (gameQV == null) {
            gameQV = new Image[3];

            for (int var1 = 0; var1 < 3; ++var1) {
                gameQV[var1] = GameCanvas.gameAC("/e/sp" + var1 + ".png");
            }
        }

        gameQQ = new int[2];
        gameQR = new int[2];
        gameQS = new int[2];
        gameQT = new int[2];
        gameQU = new int[2];
        gameQS[0] = gameQS[1] = -1;
        this.gameCT();
        Res.gameAA();
    }

    public GameScr() {
        int[] var10000 = new int[]{0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1};
        this.gameRL = 0;
        this.gameRM = null;
        this.gameRN = null;
        this.gameRO = 0;
        this.gameTL = 0;
        this.gameTM = new int[]{0, 0, 0, 0, 600841, 600841, 667658, 667658, 3346944, 3346688, 4199680, 5052928, 3276851, 3932211, 4587571, 5046280, 6684682, 3359744, 1234567, 16776960, 16753920, 16711935, 8421504};
        this.gameTN = new int[][]{{18687, 16869, 15052, 13235, 11161, 9344},
        {45824, 39168, 32768, 26112, 19712, 13056},
        {16744192, 15037184, 13395456, 11753728, 10046464, 8404992},
        {13500671, 12058853, 10682572, 9371827, 7995545, 6684800},
        {16711705, 15007767, 13369364, 11730962, 10027023, 8388621},
        {0xFF6347, 0x4682B4, 0x32CD32, 0xFFD700, 0x8A2BE2, 0xD2691E}
        };
        this.gameTO = new int[]{2, 1, 1, 1, 1, 1};
        this.cmdDead = new Command(mResources.gameSE[0], 11038);
        this.gameUB = 30;
        this.gameUC = 0;
        this.gameUD = "";
        this.gameUU = mFont.tahoma_7b_yellow;
        this.gameUV = new byte[]{-1, -1, -1, -1, -1, -1};
        this.gameUW = 0;
        this.gameUX = new MyVector();
        this.gameVO = "";
        this.gameVS = new int[][]{new int[2], {200, 10}, {500, 20}, {1000, 50}, {2000, 100}, {5000, 200}, {10000, 500}, {20000, 1000}, {50000, 2000}, {100000, 5000}, {100000, 10000}};
        if (GameCanvas.w == 128 || GameCanvas.h <= 208) {
            gameHK = 20;
        }

        this.gamePE = new Command(mResources.gameHZ, 5043);
        this.gamePF = new Command(mResources.gameHZ, 5053);
        this.gamePD = new Command(mResources.gameEC, 11002);
        this.gamePC = new Command(mResources.gameHZ, 11003);
        this.gameTA = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11004);
        this.gameSZ = new Command(mResources.gameCS, 11005);
        this.gamePB = new Command(mResources.gameHZ, 11006);
        this.gameSY = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11007);
        this.gameSX = new Command(mResources.gameCS, 11008);
        this.gameSW = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11009);
        this.gameSV = new Command(mResources.gameCS, 11010);
        this.gameSU = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11011);
        this.gameST = new Command(mResources.gameCS, 11012);
        this.gameSS = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11013);
        this.gameSR = new Command(mResources.gameCS, 11014);
        this.gameSQ = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11015);
        this.gameSP = new Command(mResources.gameCS, 11016);
        this.gameSM = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11017);
        this.gameSO = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 13001);
        this.gameSL = new Command(mResources.gameCS, 11018);
        this.gameSN = new Command(mResources.gameCS, 13002);
        this.gameSI = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11019);
        this.gameSH = new Command(mResources.gameCS, 11020);
        this.gameSF = new Command(mResources.gameCS, 14022);
        this.gameSG = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 14023);
        this.gameSK = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 14018);
        this.gameSJ = new Command(mResources.gameCS, 14019);
        this.gameSE = new Command(mResources.gameBH, 11021);
        this.gameUA = new Command(mResources.gameEO, 11022);
        this.gameTZ = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11023);
        this.gameTX = new Command(mResources.gameCE, 11024);
        this.gameTY = new Command(mResources.gameCF, 110244);
        this.gameTW = new Command(mResources.gameBH, 11025);
        this.gameTU = new Command(mResources.gameCC, 11026);
        this.gameTV = new Command(mResources.gameCD, 110221);
        this.gameSD = new Command(mResources.gameEO, 11027);
        this.gameSC = new Command(mResources.gameEO, 11028);
        this.gameSB = new Command(mResources.gameEO, 11029);
        this.gameSA = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11030);
        new Command(mResources.gameFE, 11021);
        this.gamePA = new Command(mResources.gameBA, 11000);
        this.gameHE = new Command("Focus", 11001);
        this.gameRQ = new Command(mResources.gameHR, 11032);
        this.gameRR = new Command(mResources.gameEC, 11033);
        this.gameRV = new Command(mResources.gameCJ, 11034);
        this.gameRW = new Command(mResources.gameCJ, 14014);
        this.gameRX = new Command(mResources.gameCJ, 11035);
        this.gameRY = new Command(mResources.gameCM, 11036);
        this.gameRZ = new Command(mResources.gameCJ, 11037);
        this.gameRS = new Command(mResources.gameCJ, 339);
        this.gameRT = new Command(mResources.gameCJ, 340);
        this.gameRU = new Command(mResources.gameCJ, 343);
        this.gameRP = new Command(mResources.gameCJ, 402);
        new Command("240", 110381);
        new Command("360", 1103911);
        new Command("Toàn Map", 110401);
        this.gameHG = new Command(mResources.gameXJ, 2003);
        Command var5 = this.gameHG;
        int var10001 = GameCanvas.w / 2 - mScreen.cmdW / 2;
        int var4 = GameCanvas.h - 26;
        int var3 = var10001;
        Command var2 = var5;
        var5.x = var3;
        var2.y = var4;
        if (GameCanvas.isTouch && GameCanvas.gameAH) {
            this.gamePA.x = gW - 135;
            this.gamePA.y = 6;
            this.gamePA.img = gameNJ;
            this.gameHE.x = gW;
            this.gameHE.y = gH;
            if (GameCanvas.gameAI) {
                this.gamePA.x = gW / 2 - 38;
                this.gamePA.y = gH - 34;
            }
        }

        this.gameHE.img = GameCanvas.gameAC("/u/fc.png");
        super.left = this.gamePA;
        super.right = this.gameHE;
        if ((gamePM = GameCanvas.h / 5) > 100) {
            gamePM = 100;
        }

    }

    private void gameCA() {
        if (!GameCanvas.isTouch || GameCanvas.isTouch && GameCanvas.w < 320 || isPaintInfoMe && indexMenu > 0 || gameME && indexMenu == 0) {
            gameDQ = false;
        }

        gameHQ = false;
        if (gameBA()) {
            this.gameBC();
            super.right = this.gameSE;
        } else {
            this.gameBJ();
        }
    }

    private void gameCB() {
        if (gameHL > 0 && gameHL <= 4 || GameCanvas.isTouch) {
            GameCanvas.inputDlg.gameAA(mResources.gameNW, this.gamePB, 1);
        }
    }

    private void gameCC() {
        if (gameHL > 0 && gameHL <= 4) {
            GameCanvas.inputDlg.gameAA(mResources.gameNW, this.gamePC, 1);
        }
    }

    private void gameCD() {
        if (isPaintFriend) {
            GameCanvas.inputDlg.gameAA(mResources.gameFB, this.gamePD, 0);
        }
    }

    private void gameCE() {
        isPaintInfoMe = true;
        gameAB(175, 200);
        this.gameBJ();
        if (indexMenu == 3 && currentCharViewInfo.charID == Char.getMyChar().charID) {
            Service.gI().viewInfo(currentCharViewInfo.cName, 0);
        }

        if (indexMenu == 5) {
            this.gameNW = new int[5];
            this.gameNX = new int[5];
            gameTD = popupX + 5;
            gameTE = popupY + 35;
            this.gameNW[0] = gameTD + 5;
            this.gameNX[0] = gameTE + 35;
            this.gameNW[1] = gameTD + 5;
            this.gameNX[1] = gameTE + 70;
            this.gameNW[2] = gameTD + 131;
            this.gameNX[2] = gameTE + 35;
            this.gameNW[3] = gameTD + 131;
            this.gameNX[3] = gameTE + 70;
            this.gameNW[4] = this.gameNW[0] + gameHK + 7;
            this.gameNX[4] = this.gameNX[0] - 5;
        }

        if (indexMenu == 6) {
            this.gameNW = new int[5];
            this.gameNX = new int[5];
            gameTD = popupX + 5;
            gameTE = popupY + 35;
            this.gameNW[0] = gameTD + 5;
            this.gameNX[0] = gameTE + 35;
            this.gameNW[1] = gameTD + 5;
            this.gameNX[1] = gameTE + 70;
            this.gameNW[2] = gameTD + 131;
            this.gameNX[2] = gameTE + 35;
            this.gameNW[3] = gameTD + 131;
            this.gameNX[3] = gameTE + 70;
            this.gameNW[4] = this.gameNW[0] + gameHK + 7;
            this.gameNX[4] = this.gameNX[0] - 5;
        }

        super.right = new Command(mResources.gameFE, 11060);
    }

    private void gameCF() {
        scrMain.gameAA();
        gameHO.gameAA();
        gameHQ = false;
        this.gameGZ = new Command(mResources.gameGG[0], 1100011);
        this.gameHA = new Command(mResources.gameGG[1], 1100012);
        this.gameHB = new Command(mResources.gameGG[2], 1100013);
        this.gameHD = new Command(mResources.gameGG[3], 1100014);
        this.gameHC = new Command(mResources.gameGG[4], 1100015);
        this.gameHF = new Command(mResources.gameGG[6], 1100017);
        MyVector var1;
        (var1 = new MyVector()).addElement(this.gameGZ);
        var1.addElement(this.gameHA);
        var1.addElement(this.gameHB);
        var1.addElement(this.gameHD);
        var1.addElement(this.gameHC);
        var1.addElement(new Command(mResources.gameGG[5], 1100016));
        var1.addElement(this.gameHF);
        GameCanvas.menu.gameAA(var1);
    }

    private static void gameCG() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameSS[0], 110002));
        var0.addElement(new Command(mResources.gameSS[1], 1100032));
        var0.addElement(new Command(mResources.gameSS[2], 1100033));
        var0.addElement(new Command(mResources.gameAS, 1100034));
        var0.addElement(new Command(mResources.gameBV, LoginScr.gameAF(), 1004, (Object) null));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameCH() {
        if (gamePG <= 0) {
            Command var0 = new Command(mResources.gameMN[1], 110001);
            Command var1 = new Command(mResources.gameMN[3], 110003);
            Command var2 = new Command(mResources.gameMN[4], 110004);
            new Command(mResources.gameMN[0], 110005);
            Command var3 = new Command(mResources.gameMN[6], 110006);
            new Command(mResources.gameMN[7], 110007);
            new Command(mResources.gameMN[8], 110008);
            new Command(mResources.gameMN[9], 110009);
            new Command(mResources.gameMN[10], 110010);
            new Command(mResources.gameMN[11], 110011);
            new Command(mResources.gameMN[12], 110012);
            new Command(mResources.gameMN[13], 110013);
            new Command(mResources.gameMN[14], 110014);
            new Command(mResources.gameMN[15], 110015);
            new Command(mResources.gameMN[16], 110016);
            new Command(mResources.gameMN[17], 110017);
            Command var4 = new Command(mResources.gameMN[18], 110018);
            Command var5 = new Command("Xác thực tài khoản", 1100181);
            MyVector var6 = new MyVector();
            if (Char.getMyChar().clevel >= 3 && SelectServerScr.gameAG()) {
                var6.addElement(var5);
            }

            var6.addElement(var0);
            var6.addElement(new Command("MenuAuto", 110021));
            var6.addElement(new Command("Fake", 110007930));

            var6.addElement(new Command("Lật Hình", 11000804));

            var6.addElement(var1);
            var6.addElement(var3);
            var6.addElement(var4);
            var6.addElement(new Command("Thủ khố", 110014));
            var6.addElement(new Command("Tự sát", 110020));
            var6.addElement(new Command("Khu Vực", 110016));

            var6.addElement(var2);
            GameCanvas.menu.gameAA(var6);
        }
    }

    private static void fieldCE() {
        MyVector var0 = new MyVector();
        if (Code.fieldAB != null) {
            var0.addElement(new Command("Tắt Auto", 1100073));
        } else {
            if (TileMap.mapID == 1 || TileMap.mapID == 27 || TileMap.mapID == 72) {
                var0.addElement(new Command("Auto NV", 1100074));
            }

            var0.addElement(new Command("Tàn sát", 1100069));
            var0.addElement(new Command("Auto Tà Thú", 1100075));
        }

        var0.addElement(new Command("NPC", 1100071));
        var0.addElement(new Command("DS PK", 1100093));
        var0.addElement(new Command("Item Nhặt", 1100076));
        var0.addElement(new Command("Item Del", 11000761));
        var0.addElement(new Command("Item Throw", 110007611));
        var0.addElement(new Command(Code.fieldAQ ? "Hút VP" : "Nhặt Xa", 1100080));
        var0.addElement(new Command(Code.fieldAM > 0 ? "KC Nhặt: " + Code.fieldAM : "Nhặt Full", 1100081));
        var0.addElement(new Command(Code.fieldAN > 0 ? "KCTS: " + Code.fieldAN : "TS Full", 1100082));
        var0.addElement(new Command("Đánh CVT: " + (Code.fieldAR ? "Bật" : "Tắt"), 1100083));
        String var1 = "Màu nền\nBackGround: " + CodePhu.fieldAK;
        var0.addElement(new Command(var1, nameDD.fieldAA(), 3, (Object) null));
        var0.addElement(new Command("SPGame: " + Code.speedGame, 1100087));
        var1 = "SPNextMap: " + CodePhu.fieldAJ;
        var0.addElement(new Command(var1, nameDD.fieldAA(), 1, (Object) null));
        var0.addElement(new Command("Tự Động", 1100068));
        GameCanvas.menu.gameAA(var0);
    }

    private void gameCI() {
        this.resetButton();
        gameHV = true;
        indexMenu = this.gameHH;
        this.gameNA = true;
        gameAB(175, 200);
        super.right = this.gameSE;
        super.left = new Command(mResources.gameMN[2], 110002);
        super.center = new Command(mResources.gameGA, 110019);
    }

    private void gameCJ() {
        this.resetButton();
        gameHX = true;
        this.gameNA = true;
        gameAB(175, 200);
        super.right = this.gameSE;
        Service.gI().openFindParty();
        this.gameAS();
    }

    private void gameCK() {
        this.resetButton();
        if (this.cLastFocusID > 0) {
            indexRow = Char.gameAD(this.cLastFocusID);
        } else {
            indexRow = 0;
            this.cLastFocusID = -1;
        }

        gameMC = true;
        this.gameNA = true;
        gameAB(175, 200);
        super.right = this.gameSE;
    }

    private void gameCL() {
        this.resetButton();
        gameHW = true;
        this.gameNA = true;
        gameAB(175, 200);
        super.right = this.gameSE;
        this.gameAT();
    }

    public final void gameAQ() {
        this.resetButton();
        gameHU = true;
        this.gameNA = true;
        gameAB(175, 200);
        super.right = this.gameSE;
        super.left = super.center = null;
        indexRow = 0;
    }

    public final void gameAR() {
        this.resetButton();
        gameHY = true;
        this.gameNA = true;
        gameAB(175, 200);
        super.right = this.gameSE;
        super.left = super.center = null;
        indexRow = 0;
    }

    private void gameCM() {
        this.resetButton();
        isPaintFriend = true;
        this.gameNA = true;
        gameAB(175, 200);
        super.right = this.gameSE;
        super.left = new Command(mResources.gameEE, 11044);
        super.center = null;
        indexRow = 0;
        Service.gI().requestFriend();
    }

    private void gameCN() {
        this.resetButton();
        gameHZ = true;
        this.gameNA = true;
        gameAB(175, 200);
        super.right = this.gameSE;
        super.left = new Command(mResources.gameEE, 14017);
        super.center = null;
        indexRow = 0;
        Service.gI().requestEnemies();
    }

    public final void gameAS() {
        if (gameHX) {
            super.left = super.center = null;
            super.left = new Command(mResources.gameBA, 11045);
            Party var1;
            if (vPtMap.size() > 0 && indexRow >= 0 && indexRow < vPtMap.size() && (var1 = (Party) vPtMap.elementAt(indexRow)) != null && !Char.getMyChar().cName.equals(var1.name)) {
                super.center = new Command(mResources.gameEO, 11046);
            }
        }

    }

    public final void gameAT() {
        if (gameHW) {
            super.left = super.center = null;
            indexRow = 0;
            if (vParty.size() == 0) {
                super.center = null;
                super.left = new Command(mResources.gameBA, 11047);
                return;
            }

            Party var1;
            if ((var1 = (Party) vParty.firstElement()).charId == Char.getMyChar().charID) {
                super.left = new Command(mResources.gameSI, 11070, var1);
                return;
            }

            super.left = new Command(mResources.gameSH, 11071);
        }

    }

    private static void gameCO() {
        if (TileMap.typeMap != 1) {
            MapScr.gameAF().update();
        }

    }

    public final void gameAA(Message var1) {
        InfoDlg.gameAB();

        try {
            byte var2;
            if ((var2 = var1.reader().readByte()) > 0) {
                this.zones = new int[var2];
                this.gameMV = new int[this.zones.length];

                for (int var4 = 0; var4 < this.zones.length; ++var4) {
                    this.zones[var4] = var1.reader().readByte();
                    this.gameMV[var4] = var1.reader().readByte();
                }

                gameMD = true;
                indexSelect = TileMap.zoneID;
                gameAB(175, 200);
                super.left = new Command(mResources.gameEO, 11067);
                super.center = new Command("", 11067);
                super.right = this.gameSE;
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }

    public final void gameAU() {
        try {
            this.resetButton();
            this.tradeItemName = "";
            this.typeTrade = 0;
            this.typeTrade = this.typeTradeOrder = this.coinTrade = this.coinTradeOrder = 0;
            isPaintTrade = true;
            arrItemTradeMe = new Item[12];
            arrItemTradeOrder = new Item[12];
            indexMenu = 0;
            gameAB(175, 200);
            super.right = this.gameSE;
        } catch (Exception var1) {
        }
    }

    public static final void gameAA(boolean var0) {
        gW = GameCanvas.w;
        if (GameCanvas.isTouch && (!GameCanvas.isTouch || GameCanvas.gameAH)) {
            gameAN = 8;
        } else {
            gameAN = 36;
            if (GameCanvas.isTouch) {
                gameAN += 3;
            }
        }

        gH = GameCanvas.h - gameAN - 20;
        if (var0) {
            gH = GameCanvas.h;
        }

        if (GameCanvas.isTouch && GameCanvas.gameAH) {
            gH = GameCanvas.h;
        }

        if (GameCanvas.h == 160) {
            gH = 150;
        }

        gameQY = gW;
        if (GameCanvas.w > 176) {
            gameQY -= 50;
        }

        gameQW = 0;
        gameQX = GameCanvas.h - Paint.hTab - gameAN;
        if ((gameGS = GameCanvas.h / 6) < 48) {
            gameGS = 48;
        }

        gW2 = gW >> 1;
        gH2 = gH >> 1;
        gH23 = 2 * gH / 3;
        gW6 = gW / 6;
        gameGQ = gW / TileMap.size + 2;
        gameGR = gH / TileMap.size + 2;
        if (gW % 24 != 0) {
            ++gameGQ;
        }

        gameGX = (TileMap.tmw - 1) * TileMap.size - gW;
        gameGY = (TileMap.tmh - 1) * TileMap.size - gH;
        if (GameCanvas.isTouch && GameCanvas.gameAH) {
            gameGY += 60;
        }

        cmx = cmtoX = Char.getMyChar().cx - gW2 + gW6 * Char.getMyChar().cdir;
        cmy = cmtoY = Char.getMyChar().cy - gH23;
        if (cmx < 24) {
            cmx = 24;
        }

        if (cmx > gameGX) {
            cmx = gameGX;
        }

        if (cmy < 0) {
            cmy = 0;
        }

        if (cmy > gameGY) {
            cmy = gameGY;
        }

        if ((gssx = cmx / TileMap.size - 1) < 0) {
            gssx = 0;
        }

        gssy = cmy / TileMap.size;
        gssxe = gssx + gameGQ;
        gssye = gssy + gameGR;
        if (gssy < 0) {
            gssy = 0;
        }

        if (gssye > TileMap.tmh - 1) {
            gssye = TileMap.tmh - 1;
        }

        if ((TileMap.countx = gssxe - gssx << 2) > TileMap.tmw) {
            TileMap.countx = TileMap.tmw;
        }

        if ((TileMap.county = gssye - gssy << 2) > TileMap.tmh) {
            TileMap.county = TileMap.tmh;
        }

        if ((TileMap.gssx = (Char.getMyChar().cx - 2 * gW) / TileMap.size) < 0) {
            TileMap.gssx = 0;
        }

        if ((TileMap.gssxe = TileMap.gssx + TileMap.countx) > TileMap.tmw) {
            TileMap.gssxe = TileMap.tmw;
        }

        if ((TileMap.gssy = (Char.getMyChar().cy - 2 * gH) / TileMap.size) < 0) {
            TileMap.gssy = 0;
        }

        if ((TileMap.gssye = TileMap.gssy + TileMap.county) > TileMap.tmh) {
            TileMap.gssye = TileMap.tmh;
        }

        ChatTextField.gameAA().gameAC = instance;
        ChatTextField.gameAA().tfChat.y = GameCanvas.h - 35 - ChatTextField.gameAA().tfChat.height;
        if (GameCanvas.isTouch && (!GameCanvas.isTouch || GameCanvas.gameAH)) {
            TileMap.gameAA(GameCanvas.w - 60, 0, 60, 42);
        } else {
            TileMap.gameAA(GameCanvas.w - 51, gameQX - 4, 50, 40);
        }

        if (GameCanvas.isTouch) {
            gamePN = gH - 88;
            gamePQ = gW - 100;
            gamePR = 2;
            if (GameCanvas.gameAI) {
                gamePQ = gW / 2 - 2;
                gamePR = gamePN + 50;
            }

            gamePO = 1;
            gamePP = gamePN + 50;
            gamePS = 42;
            gamePT = gamePN + 50;
            gamePU = gW - 50;
            gamePV = gamePN + 35;
            gamePW = 22;
            gamePX = gamePN + 19;
            gamePY = gW - 74;
            gamePZ = gamePN + 13;
            gameQA = gW - 85;
            gameQB = gamePN + 50;
            gameQC = gW - 37;
            gameQD = gamePN - 1;
            if (GameCanvas.w >= 450) {
                gamePX -= 15;
                gamePW += 28;
                gamePS += 45;
                gamePO += 10;
                gameQD -= 12;
                gamePZ -= 7;
                gamePU -= 18;
                gameQC -= 10;
                gamePY -= 17;
                gameQA -= 24;
            } else if (GameCanvas.w >= 360) {
                gamePX -= 5;
                gamePW += 6;
                gamePS += 12;
                gameQD -= 2;
                gamePZ -= 2;
                gamePY -= 2;
                gameQA -= 2;
            }
        }

        gameQE = new int[gameNZ.length];
        gameQF = new int[gameNZ.length];
        int var1;
        if (GameCanvas.isTouch) {
            if (GameCanvas.gameAI) {
                gameQG = 2;
                gameQH = 55;
                gameQI = 5;

                for (var1 = 0; var1 < gameQE.length; ++var1) {
                    gameQE[var1] = var1 * (25 + gameQI);
                    gameQF[var1] = gameQH;
                }

            } else {
                if (GameCanvas.w <= 320) {
                    gameQG = gW2 - gameNZ.length * 25 / 2 - 15;
                } else {
                    gameQG = gW2 - gameNZ.length * 25 / 2;
                }

                gameQH = gamePN + 58;
                gameQI = 5;

                for (var1 = 0; var1 < gameQE.length; ++var1) {
                    gameQE[var1] = var1 * (25 + gameQI);
                    gameQF[var1] = gameQH;
                }

            }
        } else {
            gameQG = 0;

            for (var1 = 0; var1 < gameQF.length; ++var1) {
                gameQE[var1] = 2;
                gameQF[var1] = 2 + var1 * 25;
            }

        }
    }

    private static boolean gameCP() {
        if (Char.getMyChar().myskill != null && Char.getMyChar().cMP < Char.getMyChar().myskill.manaUse) {
            InfoMe.gameAA(mResources.gameNE);
            return false;
        } else if (Char.getMyChar().myskill != null && (Char.getMyChar().myskill.template.maxPoint <= 0 || Char.getMyChar().myskill.point != 0)) {
            if (Char.getMyChar().arrItemBody[1] == null) {
                GameCanvas.setText(mResources.gameRS);
                return false;
            } else {
                return true;
            }
        } else {
            GameCanvas.setText(mResources.gameRR);
            return false;
        }
    }

    public final void resetButton() {
        if (Char.getMyChar().arrItemBag != null) {
            int var10001;
            int var1;
            if ((gameKZ || gameMI) && arrItemUpPeal != null) {
                for (var1 = 0; var1 < arrItemUpPeal.length; ++var1) {
                    if (arrItemUpPeal[var1] != null) {
                        var10001 = arrItemUpPeal[var1].indexUI;
                        Char.getMyChar().arrItemBag[var10001] = arrItemUpPeal[var1];
                        arrItemUpPeal[var1] = null;
                    }
                }
            }

            if (gameKW) {
                if (itemUpGrade != null) {
                    Char.getMyChar().arrItemBag[itemUpGrade.indexUI] = itemUpGrade;
                    itemUpGrade = null;
                }

                if (arrItemUpGrade != null) {
                    for (var1 = 0; var1 < arrItemUpGrade.length; ++var1) {
                        if (arrItemUpGrade[var1] != null) {
                            var10001 = arrItemUpGrade[var1].indexUI;
                            Char.getMyChar().arrItemBag[var10001] = arrItemUpGrade[var1];
                            arrItemUpGrade[var1] = null;
                        }
                    }
                }
            }

            if (gameMM) {
                if (itemUpGrade != null) {
                    Char.getMyChar().arrItemBag[itemUpGrade.indexUI] = itemUpGrade;
                    itemUpGrade = null;
                }

                if (itemSplit != null) {
                    Char.getMyChar().arrItemBag[itemSplit.indexUI] = itemSplit;
                    itemSplit = null;
                }

                if (arrItemUpGrade != null) {
                    for (var1 = 0; var1 < arrItemUpGrade.length; ++var1) {
                        if (arrItemUpGrade[var1] != null) {
                            var10001 = arrItemUpGrade[var1].indexUI;
                            Char.getMyChar().arrItemBag[var10001] = arrItemUpGrade[var1];
                            arrItemUpGrade[var1] = null;
                        }
                    }
                }
            }

            if (gameMQ && arrItemUpGrade != null) {
                for (var1 = 0; var1 < arrItemUpGrade.length; ++var1) {
                    if (arrItemUpGrade[var1] != null) {
                        var10001 = arrItemUpGrade[var1].indexUI;
                        Char.getMyChar().arrItemBag[var10001] = arrItemUpGrade[var1];
                        arrItemUpGrade[var1] = null;
                    }
                }
            }

            if (gameDN && itemSell != null) {
                Char.getMyChar().arrItemBag[itemSell.indexUI] = itemSell;
                itemSell = null;
            }

            if (gameKX && arrItemConvert != null) {
                for (var1 = 0; var1 < arrItemConvert.length; ++var1) {
                    if (arrItemConvert[var1] != null) {
                        var10001 = arrItemConvert[var1].indexUI;
                        Char.getMyChar().arrItemBag[var10001] = arrItemConvert[var1];
                        arrItemConvert[var1] = null;
                    }
                }
            }

            if (gameMB || gameMK || gameMJ || gameML || gameMR) {
                if (itemSplit != null) {
                    Char.getMyChar().arrItemBag[itemSplit.indexUI] = itemSplit;
                    itemSplit = null;
                }

                if (arrItemSplit != null) {
                    for (var1 = 0; var1 < arrItemSplit.length; ++var1) {
                        if (arrItemSplit[var1] != null) {
                            if (gameMJ || gameMK || gameML || gameMR) {
                                var10001 = arrItemSplit[var1].indexUI;
                                Char.getMyChar().arrItemBag[var10001] = arrItemSplit[var1];
                            }

                            arrItemSplit[var1] = null;
                        }
                    }
                }
            }

            if (gameMQ && arrItemSplit != null) {
                for (var1 = 0; var1 < arrItemSplit.length; ++var1) {
                    if (arrItemSplit[var1] != null) {
                        var10001 = arrItemSplit[var1].indexUI;
                        Char.getMyChar().arrItemBag[var10001] = arrItemSplit[var1];
                        arrItemSplit[var1] = null;
                    }
                }
            }

            if (isPaintTrade) {
                InfoDlg.gameAB();
                if (this.coinTrade > 0) {
                    Char var10000 = Char.getMyChar();
                    var10000.xu += this.coinTrade;
                }

                if (arrItemTradeMe != null) {
                    for (var1 = 0; var1 < arrItemTradeMe.length; ++var1) {
                        if (arrItemTradeMe[var1] != null) {
                            var10001 = arrItemTradeMe[var1].indexUI;
                            Char.getMyChar().arrItemBag[var10001] = arrItemTradeMe[var1];
                            arrItemTradeMe[var1] = null;
                        }
                    }
                }

                if (arrItemTradeOrder != null) {
                    for (var1 = 0; var1 < arrItemTradeOrder.length; ++var1) {
                        arrItemTradeOrder[var1] = null;
                    }
                }
            }
            if (isPaintAuto || fieldTF || fieldTG || ItemThrow) {
                Char.fieldAA();
            }
            if ((gameMN || gameMO) && itemSplit != null) {
                Char.getMyChar().arrItemBag[itemSplit.indexUI] = itemSplit;
                itemSplit = null;
            }
        }

        if (isPaintTrade) {
            Service.gI().cancelTrade();
        }

        GameCanvas.menu.showMenu = false;
        ChatTextField var2;
        (var2 = ChatTextField.gameAA()).tfChat.setText("");
        var2.isShow = false;
        ChatTextField.gameAA().center = null;
        if (!GameCanvas.isTouch) {
            gameKA = false;
        }

        gameDM = false;
        gameKY = false;
        this.gameNA = false;
        gameMD = false;
        isPaintAuto = false;
        isPaintInfoMe = false;
        gameDQ = false;
        gameHV = false;
        gameHW = false;
        isPaintMessage = false;
        gameME = false;
        gameMG = false;
        gameMF = false;
        gameMC = false;
        gameHX = false;
        isPaintFriend = false;
        gameMH = false;
        gameHY = false;
        gameHU = false;
        gameHZ = false;
        isPaintAlert = false;
        gameMP = false;
        this.typeTrade = 0;
        gameKB = false;
        gameKC = false;
        gameKD = false;
        gameKE = false;
        gameKF = false;
        gameKG = false;
        gameKH = false;
        gameKI = false;
        gameKJ = false;
        gameKK = false;
        gameKL = false;
        gameKM = false;
        gameKN = false;
        gameKO = false;
        gameKP = false;
        gameKQ = false;
        gameKR = false;
        gameKS = false;
        gameKT = false;
        gameKU = false;
        gameKV = false;
        gameKW = false;
        gameDN = false;
        gameKX = false;
        gameMK = false;
        gameMJ = false;
        gameMB = false;
        isPaintTrade = false;
        fieldTF = false;
        fieldTG = false;
        ItemThrow = false;
        gameMI = false;
        gameKZ = false;
        gameMA = false;
        gameMM = false;
        gameML = false;
        gameMR = false;
        gameMQ = false;
        gameMN = false;
        gameMO = false;
        indexMenu = 0;
        indexSelect = 0;
        this.gameMZ = -1;
        indexRow = -1;
        gameHM = 0;
        gameHL = 0;
        this.typeTrade = this.typeTradeOrder = 0;
        super.left = this.gamePA;
        super.right = this.gameHE;
        this.gameNW = this.gameNX = null;
        super.center = null;
        if (Char.getMyChar().cHP <= 0 || Char.getMyChar().statusMe == 14 || Char.getMyChar().statusMe == 5) {
            if (GameCanvas.gameAI) {
                this.cmdDead.caption = "";
            }

            super.center = this.cmdDead;
        }

        scrMain.gameAA();
    }

    public final void gameAA(int var1) {
        if (this.gameND != null && this.gameND.isFocus) {
            this.gameND.gameAA(var1);
        }

        super.gameAA(var1);
    }

    public final void setOffset() {
        if (!GameCanvas.menu.showMenu && !InfoDlg.isLock) {
            int var3;
            boolean var12;
            if (GameCanvas.isTouch && !ChatTextField.gameAA().isShow && !GameCanvas.menu.showMenu) {
                GameScr var1 = this;
                int var2 = -1;
                if (GameCanvas.isPointerClick) {
                    for (var3 = 0; var3 < var1.gameNU.length; ++var3) {
                        if (GameCanvas.gameAB(var1.gameNU[var3], var1.gameNV[var3], 100, 12) && GameCanvas.isPointerJustRelease) {
                            var2 = var3;
                            break;
                        }
                    }
                }

                if (var2 != -1 && !gameDG() && !gameBA() && !gameDI()) {
                    if (var2 == 0) {
                        if (ChatManager.gameAD().waitList.size() > 0) {
                            ChatManager var4 = ChatManager.gameAD();
                            var3 = 3;

                            int var8;
                            label1491:
                            while (true) {
                                if (var3 >= var4.chatTabs.size()) {
                                    var8 = -1;
                                    break;
                                }

                                ChatTab var7 = (ChatTab) var4.chatTabs.elementAt(var3);

                                for (var2 = 0; var2 < var4.waitList.size(); ++var2) {
                                    if (var7.ownerName.equals(var4.waitList.elementAt(var2).toString())) {
                                        var8 = var3;
                                        break label1491;
                                    }
                                }

                                ++var3;
                            }

                            ChatManager.gameAD().gameAA(var8);
                            this.gameGK();
                            this.gameNU[0] = this.gameNV[0] = -1;
                        }
                    } else {
                        if (ChatManager.isMessagePt) {
                            ChatManager.gameAD().gameAA(1);
                        } else if (ChatManager.isMessageClan) {
                            ChatManager.gameAD().gameAA(3);
                        }

                        this.gameGK();
                        this.gameNU[1] = this.gameNV[1] = -1;
                    }
                }

                var12 = false;
                mScreen.keyTouch = -1;
                if (GameCanvas.gameAB(TileMap.posMiniMapX, TileMap.posMiniMapY, TileMap.wMiniMap, TileMap.hMiniMap) && GameCanvas.isPointerClick && GameCanvas.isPointerJustRelease) {
                    gameCO();
                    var12 = true;
                }

                if (GameCanvas.isTouch && (!GameCanvas.menu.showMenu || !GameCanvas.gameAI) && GameCanvas.currentDialog == null && ChatPopup.currentMultilineChatPopup == null && !GameCanvas.menu.showMenu && !gameDG()) {
                    if (GameCanvas.gameAB(gamePQ, gamePR, 34, 34)) {
                        mScreen.keyTouch = 15;
                        if (GameCanvas.isPointerClick && GameCanvas.isPointerJustRelease) {
                            ChatTextField.gameAA().setText(mResources.gameTI[0]);
                            var12 = true;
                            GameCanvas.isPointerJustRelease = false;
                            GameCanvas.isPointerClick = false;
                        }
                    }

                    if (!this.gameDH()) {
                        if (!Char.getMyChar().isCaptcha) {
                            if (fieldGE) {
                                if (GameCanvas.gameAB(gamePW, gamePX, 34, 34)) {
                                    mScreen.keyTouch = 3;
                                    GameCanvas.keyHold[2] = true;
                                    this.gameCQ();
                                    var12 = true;
                                } else if (GameCanvas.isPointerDown) {
                                    GameCanvas.keyHold[2] = false;
                                }

                                if (GameCanvas.gameAB(gamePW - 30, gamePX, 30, 34)) {
                                    GameCanvas.keyHold[1] = true;
                                    this.gameCQ();
                                    var12 = true;
                                } else if (GameCanvas.isPointerDown) {
                                    GameCanvas.keyHold[1] = false;
                                }

                                if (GameCanvas.gameAB(gamePW + 34, gamePX, 30, 34)) {
                                    GameCanvas.keyHold[3] = true;
                                    this.gameCQ();
                                    var12 = true;
                                } else if (GameCanvas.isPointerDown) {
                                    GameCanvas.keyHold[3] = false;
                                }

                                if (GameCanvas.gameAB(gamePO, gamePP, 34, 34)) {
                                    mScreen.keyTouch = 4;
                                    GameCanvas.keyHold[4] = true;
                                    this.gameCQ();
                                    var12 = true;
                                } else if (GameCanvas.isPointerDown) {
                                    GameCanvas.keyHold[4] = false;
                                }

                                if (GameCanvas.gameAB(gamePS - 5, gamePT, 40, 34)) {
                                    mScreen.keyTouch = 6;
                                    GameCanvas.keyHold[6] = true;
                                    this.gameCQ();
                                    var12 = true;
                                } else if (GameCanvas.isPointerDown) {
                                    GameCanvas.keyHold[6] = false;
                                }
                            } else {
                                fieldTE.fieldAA();
                            }

                            if (GameCanvas.gameAB(gamePU, gamePV, 54, 54)) {
                                GameCanvas.keyHold[5] = true;
                                mScreen.keyTouch = 5;
                                if (GameCanvas.isPointerJustRelease) {
                                    GameCanvas.keyPressedz[5] = true;
                                    var12 = true;
                                }
                            }
                        } else {
                            if (GameCanvas.gameAC(gamePO, gamePP, 34, 34) && GameCanvas.isPointerJustRelease) {
                                this.gameAE((byte) 0);
                                GameCanvas.gameAI();
                            }

                            if (GameCanvas.gameAC(gamePW, gamePX, 34, 34) && GameCanvas.isPointerJustRelease) {
                                this.gameAE((byte) 1);
                                GameCanvas.gameAI();
                            }

                            if (GameCanvas.gameAC(gamePS - 5, gamePT, 40, 34) && GameCanvas.isPointerJustRelease) {
                                this.gameAE((byte) 2);
                                GameCanvas.gameAI();
                            }
                        }

                        if (Char.getMyChar().ctaskId > 1) {
                            if (GameCanvas.gameAB(gameQA, gameQB, 34, 34)) {
                                mScreen.keyTouch = 11;
                                if (GameCanvas.isPointerClick && GameCanvas.isPointerJustRelease) {
                                    GameCanvas.keyPressedz[11] = true;
                                    var12 = true;
                                }
                            }

                            if (GameCanvas.gameAB(gamePY, gamePZ, 34, 34)) {
                                mScreen.keyTouch = 10;
                                if (GameCanvas.isPointerClick && GameCanvas.isPointerJustRelease) {
                                    GameCanvas.keyPressedz[10] = true;
                                    var12 = true;
                                }
                            }

                            if (GameCanvas.gameAB(gameQC, gameQD, 34, 34)) {
                                mScreen.keyTouch = 13;
                                if (GameCanvas.isPointerClick && GameCanvas.isPointerJustRelease) {
                                    Char.getMyChar().gameAU();
                                    var12 = true;
                                }
                            }
                        }

                        if (Char.getMyChar().vSkill.size() >= 2 && (GameCanvas.gameAB(gameQG + gameQE[0], gameQF[0], gameNZ.length * 30, 30)
                                || !GameCanvas.gameAH && GameCanvas.gameAB(gameQG + gameQE[0], gameQF[0], 30, gameNZ.length * 25)) && GameCanvas.isPointerClick && GameCanvas.isPointerJustRelease) {
                            if (!GameCanvas.gameAH) {
                                var2 = (GameCanvas.pyLast - (gameQH + gameQF[0])) / 25;
                            } else {
                                var2 = (GameCanvas.pxLast - (gameQG + gameQE[0])) / 30;
                            }

                            this.gamePJ = var2;
                            if (indexSelect < 0) {
                                indexSelect = 0;
                            }

                            if (this.gamePJ > gameNZ.length - 1) {
                                this.gamePJ = gameNZ.length - 1;
                            }

                            var12 = true;
                            Skill var13 = gameNZ[this.gamePJ];
                            this.gameAA(var13, false, true);
                            this.gameHI = true;
                        }

                        if (GameCanvas.isPointerJustRelease) {
                            GameCanvas.keyHold[1] = false;
                            GameCanvas.keyHold[2] = false;
                            GameCanvas.keyHold[3] = false;
                            GameCanvas.keyHold[4] = false;
                            GameCanvas.keyHold[6] = false;
                        }

                        if (!var12 && !gameDG() && !gameBA() && !gameDI() && GameCanvas.isPointerClick) {
                            var3 = 0;

                            label1436:
                            while (true) {
                                if (var3 >= vMob.size()) {
                                    for (var3 = 0; var3 < vNpc.size(); ++var3) {
                                        Npc var16;
                                        if ((var16 = (Npc) vNpc.elementAt(var3)).gameAQ() && GameCanvas.gameAA(var16.cx - var16.cw / 2, var16.cy - var16.ch, var16.cw, var16.ch) && GameCanvas.isPointerJustRelease) {
                                            Char.getMyChar().mobFocus = null;
                                            Char.getMyChar().gameAV();
                                            Char.getMyChar().npcFocus = var16;
                                            Char.getMyChar().charFocus = null;
                                            Char.getMyChar().itemFocus = null;
                                            Char.isManualFocus = true;
                                            break label1436;
                                        }
                                    }

                                    for (var3 = 0; var3 < vCharInMap.size(); ++var3) {
                                        Char var17;
                                        if ((var17 = (Char) vCharInMap.elementAt(var3)).gameAQ() && !var17.gameBB() && GameCanvas.gameAA(var17.cx - var17.cw / 2, var17.cy - var17.ch, var17.cw, var17.ch) && GameCanvas.isPointerJustRelease) {
                                            Char.getMyChar().mobFocus = null;
                                            Char.getMyChar().gameAV();
                                            Char.getMyChar().charFocus = var17;
                                            Char.getMyChar().itemFocus = null;
                                            Char.isManualFocus = true;
                                            break label1436;
                                        }
                                    }

                                    var3 = 0;

                                    while (true) {
                                        if (var3 >= vItemMap.size()) {
                                            break label1436;
                                        }

                                        ItemMap var18;
                                        if (GameCanvas.gameAA((var18 = (ItemMap) vItemMap.elementAt(var3)).x - 12, var18.y - 24, 24, 24) && GameCanvas.isPointerJustRelease) {
                                            Char.getMyChar().mobFocus = null;
                                            Char.getMyChar().gameAV();
                                            Char.getMyChar().charFocus = null;
                                            Char.getMyChar().itemFocus = var18;
                                            Char.isManualFocus = true;
                                            break label1436;
                                        }

                                        ++var3;
                                    }
                                }

                                Mob var15;
                                if ((var15 = (Mob) vMob.elementAt(var3)).gameAD() && GameCanvas.gameAA(var15.x - var15.w / 2, var15.y - var15.h, var15.w, var15.h) && GameCanvas.isPointerJustRelease) {
                                    Char.getMyChar().mobFocus = var15;
                                    Char.getMyChar().gameAV();
                                    Char.getMyChar().charFocus = null;
                                    Char.getMyChar().itemFocus = null;
                                    Char.isManualFocus = true;
                                    break;
                                }

                                ++var3;
                            }
                        }
                    }
                }
            }

            if (TileMap.mapID != 130 && !gameCR()) {
                label1608:
                {
                    long var5 = System.currentTimeMillis();
                    if (GameCanvas.keyPressedz[2] || GameCanvas.keyPressedz[4] || GameCanvas.keyPressedz[6] || GameCanvas.keyPressedz[1] || GameCanvas.keyPressedz[3]) {
                        auto = 0;
                        if (this.gameGC) {
                            Char.getMyChar().isLockMove = false;
                            this.gameGC = false;
                        }
                    }

                    if (GameCanvas.keyPressedz[5] && !gameDG()) {
                        if (auto == 0) {
                            if (var5 - this.gamePH < 800L && (Char.getMyChar().myskill == null || Char.getMyChar().cMP >= Char.getMyChar().myskill.manaUse) && Char.getMyChar().myskill != null && (Char.getMyChar().myskill.template.maxPoint <= 0 || Char.getMyChar().myskill.point != 0) && Char.getMyChar().arrItemBody[1] != null && Char.getMyChar().mobFocus != null) {
                                auto = 10;
                                GameCanvas.keyPressedz[5] = false;
                            }
                        } else {
                            if (!this.gameGC && Char.getMyChar().statusMe != 14) {
                                this.gameGC = !this.gameGC;
                                Char.getMyChar().isLockMove = !Char.getMyChar().isLockMove;
                                this.gamePH = var5;
                                break label1608;
                            }

                            auto = 0;
                            if (this.gameGC) {
                                Char.getMyChar().isLockMove = false;
                                this.gameGC = false;
                            }

                            GameCanvas.keyPressedz[4] = GameCanvas.keyPressedz[6] = false;
                        }

                        this.gamePH = var5;
                    }

                    if (GameCanvas.gameTick % 10 == 0 && auto > 0 && (Char.getMyChar().mobFocus != null || Char.getMyChar().itemFocus != null)) {
                        this.gameAB(true);
                    }

                    if (auto > 1) {
                        --auto;
                    }
                }
            }

            if (GameCanvas.isTouch) {
                if (GameCanvas.isPointerDown && !GameCanvas.isPointerJustRelease && GameCanvas.gameAB(gameQC, gameQD, 34, 34) && !gameMC && GameCanvas.isPointerClick && GameCanvas.gameAG()) {
                    this.gameCK();
                }
            } else if (GameCanvas.keyHold[13] && !gameMC && GameCanvas.gameAG()) {
                this.gameCK();
            }

            if (ChatPopup.currentMultilineChatPopup != null) {
                Command var9 = ChatPopup.currentMultilineChatPopup.cmdNextLine;
                if ((GameCanvas.keyPressedz[5] || mScreen.gameAA(var9)) && var9 != null) {
                    GameCanvas.isPointerJustRelease = false;
                    GameCanvas.keyPressedz[5] = false;
                    mScreen.keyTouch = -1;
                    if (var9 != null) {
                        var9.gameAA();
                    }
                }
            } else if (!ChatTextField.gameAA().isShow) {
                if ((GameCanvas.keyPressedz[12] || mScreen.gameAA(GameCanvas.currentScreen.left)) && super.left != null) {
                    GameCanvas.isPointerJustRelease = false;
                    GameCanvas.isPointerClick = false;
                    GameCanvas.keyPressedz[12] = false;
                    mScreen.keyTouch = -1;
                    if (super.left != null) {
                        super.left.gameAA();
                    }
                }

                if ((GameCanvas.keyPressedz[13] || mScreen.gameAA(GameCanvas.currentScreen.right)) && super.right != null) {
                    GameCanvas.isPointerJustRelease = false;
                    GameCanvas.isPointerClick = false;
                    GameCanvas.keyPressedz[13] = false;
                    mScreen.keyTouch = -1;
                    if (super.right != null) {
                        super.right.gameAA();
                    }
                }

                if ((GameCanvas.keyPressedz[5] || mScreen.gameAA(GameCanvas.currentScreen.center)) && super.center != null) {
                    GameCanvas.isPointerJustRelease = false;
                    GameCanvas.keyPressedz[5] = false;
                    mScreen.keyTouch = -1;
                    if (super.center != null) {
                        super.center.gameAA();
                    }
                }
            } else {
                if (ChatTextField.gameAA().left != null && (GameCanvas.keyPressedz[12] || mScreen.gameAA(ChatTextField.gameAA().left)) && ChatTextField.gameAA().left != null) {
                    ChatTextField.gameAA().left.gameAA();
                }

                if (ChatTextField.gameAA().right != null && (GameCanvas.keyPressedz[13] || mScreen.gameAA(ChatTextField.gameAA().right)) && ChatTextField.gameAA().right != null) {
                    ChatTextField.gameAA().right.gameAA();
                }

                if (ChatTextField.gameAA().center != null && (GameCanvas.keyPressedz[5] || mScreen.gameAA(ChatTextField.gameAA().center)) && ChatTextField.gameAA().center != null) {
                    ChatTextField.gameAA().center.gameAA();
                }
            }

            ScrollResult var10;
            if (gameMD && GameCanvas.currentDialog == null) {
                var12 = false;
                if (GameCanvas.keyPressedz[4]) {
                    if (--indexSelect < 0) {
                        indexSelect = this.zones.length - 1;
                    }

                    var12 = true;
                } else if (GameCanvas.keyPressedz[6]) {
                    if (++indexSelect >= this.zones.length) {
                        indexSelect = 0;
                    }

                    var12 = true;
                } else if (GameCanvas.keyPressedz[8]) {
                    if (indexSelect + this.gameMT <= this.zones.length - 1) {
                        indexSelect += this.gameMT;
                    }

                    var12 = true;
                } else if (GameCanvas.keyPressedz[2]) {
                    if (indexSelect - this.gameMT >= 0) {
                        indexSelect -= this.gameMT;
                    }

                    var12 = true;
                }

                if (var12) {
                    scrMain.gameAA(indexSelect / gameTJ * scrMain.ITEM_SIZE);
                    GameCanvas.gameAI();
                    GameCanvas.gameAH();
                }

                if (GameCanvas.isTouch && ((var10 = scrMain.gameAB()).isDowning || var10.isFinish)) {
                    indexSelect = var10.selected;
                }
            }

            ScrollResult var14;
            if (gameHU || gameHW || gameHX || isPaintFriend || gameHZ || gameMC || gameHY || gameMH) {
                if (gameMH) {
                    if (gameMH && ((var14 = scrMain.gameAB()).isDowning || var14.isFinish)) {
                        indexSelect = var14.selected;
                        if (var14.selected >= arrItemStands.length) {
                            indexSelect = -1;
                        }

                        if (indexSelect >= 0) {
                            gameHL = 1;
                        }

                        this.gameBC();
                    }
                } else {
                    label1587:
                    {
                        if (gameHW) {
                            if (vParty.size() == 0) {
                                break label1587;
                            }

                            if (GameCanvas.keyPressedz[8]) {
                                if (++indexRow >= vParty.size()) {
                                    indexRow = vParty.size() - 1;
                                }

                                scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                            } else if (GameCanvas.keyPressedz[2]) {
                                if (--indexRow < 0) {
                                    indexRow = 0;
                                }

                                scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                            }

                            this.gameDF();
                        } else if (gameHX) {
                            if (GameCanvas.keyPressedz[8]) {
                                if (++indexRow >= vPtMap.size()) {
                                    indexRow = vPtMap.size() - 1;
                                }

                                scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                            } else if (GameCanvas.keyPressedz[2]) {
                                if (--indexRow < 0) {
                                    indexRow = 0;
                                }

                                scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                            }

                            this.gameAS();
                        } else if (isPaintFriend) {
                            if (GameCanvas.keyPressedz[8]) {
                                if (++indexRow >= gameHM) {
                                    indexRow = 0;
                                }

                                scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                            } else if (GameCanvas.keyPressedz[2]) {
                                if (--indexRow < 0) {
                                    indexRow = gameHM - 1;
                                }

                                scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                            }

                            this.gameDE();
                        } else if (gameHZ) {
                            if (GameCanvas.keyPressedz[8]) {
                                if (++indexRow >= gameHM) {
                                    indexRow = 0;
                                }

                                scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                            } else if (GameCanvas.keyPressedz[2]) {
                                if (--indexRow < 0) {
                                    indexRow = gameHM - 1;
                                }

                                scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                            }

                            this.gameDD();
                        } else if (gameMC) {
                            if (GameCanvas.keyPressedz[8]) {
                                if (++indexRow >= vCharInMap.size()) {
                                    indexRow = vCharInMap.size() - 1;
                                }

                                scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                            } else if (GameCanvas.keyPressedz[2]) {
                                if (--indexRow < 0) {
                                    indexRow = 0;
                                }

                                scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                            }

                            if (this.cLastFocusID > 0 && !GameCanvas.isTouch) {
                                scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                            }

                            this.gameDA();
                        } else if (gameHY) {
                            if (GameCanvas.keyPressedz[8]) {
                                if (++indexRow >= vList.size()) {
                                    indexRow = vList.size() - 1;
                                }

                                scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                            } else if (GameCanvas.keyPressedz[2]) {
                                if (--indexRow < 0) {
                                    indexRow = 0;
                                }

                                scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                            }

                            this.gameDC();
                        } else if (gameHU) {
                            if (GameCanvas.keyPressedz[8]) {
                                if (++indexRow >= vList.size()) {
                                    indexRow = vList.size() - 1;
                                }

                                scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                            } else if (GameCanvas.keyPressedz[2]) {
                                if (--indexRow < 0) {
                                    indexRow = 0;
                                }

                                scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                            }

                            this.gameDB();
                        }

                        if (GameCanvas.isTouch && GameCanvas.currentDialog == null && !GameCanvas.menu.showMenu) {
                            MyVector var19 = null;
                            if (gameHW) {
                                var19 = vParty;
                            } else if (isPaintFriend) {
                                var19 = vFriend;
                            } else if (gameHZ) {
                                var19 = vEnemies;
                            } else if (gameHX) {
                                var19 = vPtMap;
                            } else if (gameMC) {
                                var19 = vCharInMap;
                            } else if (gameHY) {
                                var19 = vList;
                            } else if (gameHU) {
                                var19 = vList;
                            }

                            if ((var10 = scrMain.gameAB()).isDowning || var10.isFinish) {
                                indexRow = var10.selected;
                                if (var10.selected >= var19.size()) {
                                    indexRow = -1;
                                }

                                if (gameHW) {
                                    this.gameDF();
                                } else if (isPaintFriend) {
                                    this.gameDE();
                                } else if (gameHZ) {
                                    this.gameDD();
                                } else if (gameMC) {
                                    this.gameDA();
                                } else if (gameHY) {
                                    this.gameDC();
                                } else if (gameHU) {
                                    this.gameDB();
                                }
                            }
                        }

                        GameCanvas.gameAI();
                        GameCanvas.gameAH();
                    }
                }
            }

            this.gameBB();
            gameCV();
            this.fieldCV();
            if (gameHV) {
                if (gameHL == 0) {
                    if (gameHL == 0 && GameCanvas.keyPressedz[8]) {
                        gameHL = 1;
                        indexRow = -1;
                        scrMain.gameAA();
                        gameHO.gameAA();
                    }
                } else {
                    if (indexRow < 0) {
                        indexRow = 0;
                    }

                    if (GameCanvas.keyPressedz[2]) {
                        if (indexRow == 0) {
                            --gameHL;
                            indexRow = -1;
                        } else {
                            --indexRow;
                        }

                        scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                    } else if (GameCanvas.keyPressedz[8]) {
                        if (++indexRow >= gameHM) {
                            indexRow = 0;
                        }

                        scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                    }
                }

                if (GameCanvas.isTouch && ((var14 = scrMain.gameAB()).isDowning || var14.isFinish)) {
                    indexRow = var14.selected;
                    gameHL = 1;
                }
            }

            this.gameCY();
            this.gameCZ();
            int var20;
            if (Char.getMyChar().currentMovePoint != null) {
                for (var20 = 0; var20 < GameCanvas.keyPressedz.length; ++var20) {
                    if (GameCanvas.keyPressedz[var20]) {
                        Char.getMyChar().currentMovePoint = null;
                        break;
                    }
                }
            }

            if (ChatTextField.gameAA().isShow && GameCanvas.gameBU != 0) {
                ChatTextField var21 = ChatTextField.gameAA();
                var3 = GameCanvas.gameBU;
                if (var21.isShow) {
                    var21.tfChat.gameAA(var3);
                }

                if (var21.tfChat.getText().equals("")) {
                    var21.right.caption = mResources.gameBH;
                } else {
                    var21.right.caption = mResources.gameBW;
                }

                GameCanvas.gameBU = 0;
            }

            if (this.gameNA) {
                GameCanvas.gameAI();
            } else {
                if (GameCanvas.menu.showMenu || gameDI() || Char.isLockKey) {
                    return;
                }

                if (GameCanvas.keyPressedz[10]) {
                    GameCanvas.keyPressedz[10] = false;
                    gameAX();
                    GameCanvas.gameAH();
                }

                if (GameCanvas.keyPressedz[11]) {
                    GameCanvas.keyPressedz[11] = false;
                    gameAW();
                    GameCanvas.gameAH();
                }

                if (GameCanvas.gameBU != 0 && TField.isQwerty) {
                    if (GameCanvas.gameBU == 32) {
                        gameAX();
                        GameCanvas.gameBU = 0;
                        GameCanvas.gameAH();
                    } else if (GameCanvas.gameBU == 64) {
                        gameAW();
                        GameCanvas.gameBU = 0;
                        GameCanvas.gameAH();
                    } else if (GameCanvas.gameBU == 48) {
                        gameAW();
                        GameCanvas.gameBU = 0;
                        GameCanvas.gameAH();
                    } else if (GameCanvas.gameBU == 63) {
                        gameAW();
                        GameCanvas.gameBU = 0;
                        GameCanvas.gameAH();
                    }
                }

                if (Char.getMyChar().gameFX != null) {
                    return;
                }

                if (Char.getMyChar().isCaptcha) {
                    if (GameCanvas.keyHold[4]) {
                        this.gameAE((byte) 0);
                        GameCanvas.gameAI();
                    } else if (GameCanvas.keyHold[2]) {
                        this.gameAE((byte) 1);
                        GameCanvas.gameAI();
                    } else if (GameCanvas.keyHold[6]) {
                        this.gameAE((byte) 2);
                        GameCanvas.gameAI();
                    }
                } else {
                    if (Char.getMyChar().statusMe == 1) {
                        if (GameCanvas.keyPressedz[5]) {
                            GameCanvas.keyPressedz[5] = false;
                            this.gameAB(false);
                        } else if (GameCanvas.keyHold[2]) {
                            if (!Char.getMyChar().isLockMove && !Char.getMyChar().isBlinking) {
                                gameAH(0);
                            }
                        } else if (GameCanvas.keyHold[1]) {
                            Char.getMyChar().cdir = -1;
                            if (!Char.getMyChar().isLockMove && !Char.getMyChar().isBlinking) {
                                gameAH(-4);
                            }
                        } else if (GameCanvas.keyHold[3]) {
                            Char.getMyChar().cdir = 1;
                            if (!Char.getMyChar().isLockMove && !Char.getMyChar().isBlinking) {
                                gameAH(4);
                            }
                        } else if (GameCanvas.keyHold[4]) {
                            Char.getMyChar();
                            if (Char.getMyChar().cdir == 1) {
                                Char.getMyChar().cdir = -1;
                            } else if (!Char.getMyChar().isLockMove && !Char.getMyChar().isBlinking) {
                                Char.getMyChar().statusMe = 2;
                                Char.getMyChar().cvx = -Char.getMyChar().gameAB();
                            }
                        } else if (GameCanvas.keyHold[6]) {
                            Char.getMyChar();
                            if (Char.getMyChar().cdir == -1) {
                                Char.getMyChar().cdir = 1;
                            } else if (!Char.getMyChar().isLockMove && !Char.getMyChar().isBlinking) {
                                Char.getMyChar().statusMe = 2;
                                Char.getMyChar().cvx = Char.getMyChar().gameAB();
                            }
                        }
                    } else if (Char.getMyChar().statusMe == 2) {
                        if (GameCanvas.keyPressedz[5]) {
                            GameCanvas.keyPressedz[5] = false;
                            this.gameAB(false);
                        } else if (GameCanvas.keyHold[2]) {
                            Char.getMyChar().cvy = Char.getMyChar().canJumpHigh ? -10 : -8;
                            Char.getMyChar().statusMe = 3;
                            Char.getMyChar().cp1 = 0;
                        } else if (GameCanvas.keyHold[1]) {
                            Char.getMyChar().cdir = -1;
                            Char.getMyChar().cvy = Char.getMyChar().canJumpHigh ? -10 : -8;
                            Char.getMyChar().cvx = -4;
                            Char.getMyChar().statusMe = 3;
                            Char.getMyChar().cp1 = 0;
                        } else if (GameCanvas.keyHold[3]) {
                            Char.getMyChar().cdir = 1;
                            Char.getMyChar().cvy = Char.getMyChar().canJumpHigh ? -10 : -8;
                            Char.getMyChar().cvx = 4;
                            Char.getMyChar().statusMe = 3;
                            Char.getMyChar().cp1 = 0;
                        } else {
                            Char var11;
                            if (GameCanvas.keyHold[4]) {
                                if (Char.getMyChar().cdir == 1) {
                                    Char.getMyChar().cdir = -1;
                                } else {
                                    var11 = Char.getMyChar();
                                    var20 = -Char.getMyChar().gameAB();
                                    Char.getMyChar();
                                    var11.cvx = var20;
                                }
                            } else if (GameCanvas.keyHold[6]) {
                                if (Char.getMyChar().cdir == -1) {
                                    Char.getMyChar().cdir = 1;
                                } else {
                                    var11 = Char.getMyChar();
                                    var20 = Char.getMyChar().gameAB();
                                    Char.getMyChar();
                                    var11.cvx = var20;
                                }
                            }
                        }
                    } else if (Char.getMyChar().statusMe == 3) {
                        if (GameCanvas.keyPressedz[5]) {
                            GameCanvas.keyPressedz[5] = false;
                            this.gameAB(false);
                        }

                        if (!GameCanvas.keyHold[4] && !GameCanvas.keyHold[1]) {
                            if (GameCanvas.keyHold[6] || GameCanvas.keyHold[3]) {
                                if (Char.getMyChar().cdir == -1) {
                                    Char.getMyChar().cdir = 1;
                                } else {
                                    Char.getMyChar().cvx = Char.getMyChar().gameAB();
                                }
                            }
                        } else if (Char.getMyChar().cdir == 1) {
                            Char.getMyChar().cdir = -1;
                        } else {
                            Char.getMyChar().cvx = -Char.getMyChar().gameAB();
                        }

                        if ((GameCanvas.keyHold[2] || GameCanvas.keyHold[1] || GameCanvas.keyHold[3]) && Char.getMyChar().canJumpHigh && Char.getMyChar().cp1 == 0 && Char.getMyChar().cvy > -4) {
                            ++Char.getMyChar().cp1;
                            Char.getMyChar().cvy = -7;
                        }
                    } else if (Char.getMyChar().statusMe == 4) {
                        if (GameCanvas.keyPressedz[5]) {
                            GameCanvas.keyPressedz[5] = false;
                            this.gameAB(false);
                        }

                        if (GameCanvas.keyPressedz[2]) {
                            GameCanvas.gameAH();
                        }

                        if (GameCanvas.keyHold[4]) {
                            if (Char.getMyChar().cdir == 1) {
                                Char.getMyChar().cdir = -1;
                            } else {
                                Char.getMyChar().cvx = -Char.getMyChar().gameAB();
                            }
                        } else if (GameCanvas.keyHold[6]) {
                            if (Char.getMyChar().cdir == -1) {
                                Char.getMyChar().cdir = 1;
                            } else {
                                Char.getMyChar().cvx = Char.getMyChar().gameAB();
                            }
                        }
                    } else if (Char.getMyChar().statusMe == 10) {
                        if (GameCanvas.keyPressedz[5]) {
                            GameCanvas.keyPressedz[5] = false;
                            this.gameAB(false);
                        }

                        if (GameCanvas.keyHold[2]) {
                            Char.getMyChar().cvy = -10;
                            Char.getMyChar().statusMe = 3;
                            Char.getMyChar().cp1 = 0;
                        } else if (GameCanvas.keyHold[4]) {
                            if (Char.getMyChar().cdir == 1) {
                                Char.getMyChar().cdir = -1;
                            } else {
                                Char.getMyChar().cvx = -5;
                            }
                        } else if (GameCanvas.keyHold[6]) {
                            if (Char.getMyChar().cdir == -1) {
                                Char.getMyChar().cdir = 1;
                            } else {
                                Char.getMyChar().cvx = 5;
                            }
                        }
                    } else if (Char.getMyChar().statusMe == 7) {
                        if (GameCanvas.keyPressedz[5]) {
                            GameCanvas.keyPressedz[5] = false;
                        }

                        if (GameCanvas.keyHold[4]) {
                            if (Char.getMyChar().cdir == 1) {
                                Char.getMyChar().cdir = -1;
                            } else {
                                Char.getMyChar().cvx = -Char.getMyChar().gameAB() + 2;
                            }
                        } else if (GameCanvas.keyHold[6]) {
                            if (Char.getMyChar().cdir == -1) {
                                Char.getMyChar().cdir = 1;
                            } else {
                                Char.getMyChar().cvx = Char.getMyChar().gameAB() - 2;
                            }
                        }
                    } else if (Char.getMyChar().statusMe == 11) {
                        if (GameCanvas.keyPressedz[5]) {
                            GameCanvas.keyPressedz[5] = false;
                            this.gameAB(false);
                        }

                        if (GameCanvas.keyHold[2]) {
                            Char.getMyChar().cvy = -10;
                            Char.getMyChar().statusMe = 3;
                            Char.getMyChar().cp1 = 0;
                        }
                    }

                    if (GameCanvas.keyPressedz[8] && GameCanvas.gameBU != 56) {
                        GameCanvas.keyPressedz[8] = false;
                        this.gameCS();
                    }
                }

                if (GameCanvas.gameBU != 0) {
                    if (TField.isQwerty) {
                        if (GameCanvas.gameBU == 113) {
                            this.gameHI = true;
                            if (gameNY[0] != null) {
                                this.gameAA(gameNY[0], true, false);
                            }
                        } else if (GameCanvas.gameBU == 119) {
                            this.gameHI = true;
                            if (gameNY[1] != null) {
                                this.gameAA(gameNY[1], true, false);
                            }
                        } else if (GameCanvas.gameBU == 101) {
                            this.gameHI = true;
                            if (gameNY[2] != null) {
                                this.gameAA(gameNY[2], true, false);
                            }
                        } else {
                            ChatTextField.gameAA().gameAA(GameCanvas.gameBU, this, mResources.gameTI[0]);
                        }
                    } else if (!GameCanvas.gameAC) {
                        ChatTextField.gameAA().gameAA(GameCanvas.gameBU, this, mResources.gameTI[0]);
                    } else if (GameCanvas.gameBU == 55) {
                        this.gameHI = true;
                        if (gameNY[0] != null) {
                            this.gameAA(gameNY[0], true, false);
                        }
                    } else if (GameCanvas.gameBU == 56) {
                        this.gameHI = true;
                        if (gameNY[1] != null) {
                            this.gameAA(gameNY[1], true, false);
                        }
                    } else if (GameCanvas.gameBU == 57) {
                        this.gameHI = true;
                        if (gameNY[2] != null) {
                            this.gameAA(gameNY[2], true, false);
                        }
                    } else if (GameCanvas.gameBU == 48) {
                        ChatTextField.gameAA().setText(mResources.gameTI[0]);
                    }

                    GameCanvas.gameBU = 0;
                }
            }

            GameCanvas.gameAH();
        }

    }

    private void gameCQ() {
        auto = 0;
        this.gameGC = Char.getMyChar().isLockMove = false;
    }

    public static void gameAW() {
        if (!Char.getMyChar().gameAE(17)) {
            for (int var0 = 0; var0 < Char.getMyChar().arrItemBag.length; ++var0) {
                if (Char.getMyChar().arrItemBag[var0] != null && Char.getMyChar().arrItemBag[var0].template.type == 17) {
                    InfoMe.gameAA(mResources.gameRY);
                    return;
                }
            }

            if (auto != 1) {
                InfoMe.gameAA(mResources.gameRZ);
            }
        }

    }

    public static void gameAX() {
        int var0 = (int) (System.currentTimeMillis() / 1000L);

        int var1;
        for (var1 = 0; var1 < Char.getMyChar().vEff.size(); ++var1) {
            Effect var2;
            if ((var2 = (Effect) Char.getMyChar().vEff.elementAt(var1)).template.id == 21 && var2.timeLenght - (var0 - var2.timeStart) >= 2) {
                return;
            }
        }

        if (!Char.getMyChar().gameAE(16)) {
            for (var1 = 0; var1 < Char.getMyChar().arrItemBag.length; ++var1) {
                if (Char.getMyChar().arrItemBag[var1] != null && Char.getMyChar().arrItemBag[var1].template.type == 16) {
                    InfoMe.gameAA(mResources.gameRY);
                    return;
                }
            }

            if (auto != 1) {
                InfoMe.gameAA(mResources.gameSA);
            }
        }

    }

    private static boolean gameCR() {
        if (Char.getMyChar().mobFocus == null) {
            return false;
        } else {
            return Char.getMyChar().mobFocus.getTemplate().mobTemplateId == 142 && Char.getMyChar().cTypePk == 4 || Char.getMyChar().mobFocus.getTemplate().mobTemplateId == 143 && Char.getMyChar().cTypePk == 5 || Char.getMyChar().mobFocus.getTemplate().mobTemplateId == 143 && Char.getMyChar().cTypePk == 6;
        }
    }

    private void gameAB(boolean var1) {
        if (Char.getMyChar().statusMe != 14) {
            boolean var10000;
            if (!InfoDlg.isLock && !Char.getMyChar().isLockAttack && !Char.isLockKey && !Char.getMyChar().isBlinking) {
                if (Char.getMyChar().mobFocus == null || (Char.getMyChar().mobFocus.templateId != 97 || Char.getMyChar().cTypePk != 4) && (Char.getMyChar().mobFocus.templateId != 98 || Char.getMyChar().cTypePk != 4) && (Char.getMyChar().mobFocus.templateId != 96 || Char.getMyChar().cTypePk != 5) && (Char.getMyChar().mobFocus.templateId != 99 || Char.getMyChar().cTypePk != 5) && (Char.getMyChar().mobFocus.templateId != 200 || Char.getMyChar().cTypePk != 4) && (Char.getMyChar().mobFocus.templateId != 199 || Char.getMyChar().cTypePk != 5) && (Char.getMyChar().mobFocus.templateId != 198 || Char.getMyChar().cTypePk != 6)) {
                    if (Char.getMyChar().myskill != null && Char.getMyChar().myskill.template.type == 2 && Char.getMyChar().npcFocus == null) {
                        var10000 = gameCP();
                    } else if (Char.getMyChar().gameFX == null && (Char.getMyChar().charFocus == null || !Char.getMyChar().charFocus.gameBB()) && (Char.getMyChar().mobFocus != null || Char.getMyChar().npcFocus != null || Char.getMyChar().charFocus != null || Char.getMyChar().itemFocus != null)) {
                        label448:
                        {
                            int var2;
                            int var3;
                            if (Char.getMyChar().mobFocus != null) {
                                if (Char.getMyChar().myskill == null) {
                                    var10000 = false;
                                    break label448;
                                }

                                if (Char.getMyChar().arrItemBody[1] == null) {
                                    InfoMe.gameAA(mResources.gameTC);
                                    var10000 = false;
                                    break label448;
                                }

                                if (Char.getMyChar().mobFocus.status == 1 || Char.getMyChar().mobFocus.status == 0 || Char.getMyChar().myskill.template.type == 4) {
                                    var10000 = false;
                                    break label448;
                                }

                                if (!gameCP()) {
                                    var10000 = false;
                                    break label448;
                                }

                                if (Char.getMyChar().cx < Char.getMyChar().mobFocus.x) {
                                    Char.getMyChar().cdir = 1;
                                } else {
                                    Char.getMyChar().cdir = -1;
                                }

                                var2 = Math.abs(Char.getMyChar().cx - Char.getMyChar().mobFocus.x);
                                var3 = Math.abs(Char.getMyChar().cy - Char.getMyChar().mobFocus.y);
                                Char.getMyChar().cvx = 0;
                                if (Char.getMyChar().gameAC()) {
                                    if (var2 > Char.getMyChar().myskill.fieldAB() || var3 > Char.getMyChar().myskill.fieldAC()) {
                                        Char.getMyChar().currentMovePoint = new MovePoint(Char.getMyChar().mobFocus.x, Char.getMyChar().cy);
                                        GameCanvas.gameAI();
                                        GameCanvas.gameAH();
                                        var10000 = false;
                                        break label448;
                                    }

                                    GameCanvas.gameAI();
                                    GameCanvas.gameAH();
                                } else if ((Char.getMyChar().myskill.template.id == 24 || Char.getMyChar().myskill.template.id == 40 || Char.getMyChar().myskill.template.id == 42) && var2 <= Char.getMyChar().myskill.fieldAB() && var3 <= Char.getMyChar().myskill.fieldAC()) {
                                    GameCanvas.gameAI();
                                    GameCanvas.gameAH();
                                    Char.getMyChar().cvx = 0;
                                } else {
                                    if (var2 > Char.getMyChar().myskill.fieldAB() || var3 > Char.getMyChar().myskill.fieldAC() || Char.getMyChar().cy < Char.getMyChar().mobFocus.y - 10) {
                                        Char.getMyChar().currentMovePoint = new MovePoint(Char.getMyChar().mobFocus.x + Char.getMyChar().mobFocus.dir * 12, Char.getMyChar().cy);
                                        GameCanvas.gameAI();
                                        GameCanvas.gameAH();
                                        var10000 = false;
                                        break label448;
                                    }

                                    GameCanvas.gameAI();
                                    GameCanvas.gameAH();
                                    Char.getMyChar().cvx = 0;
                                }
                            } else {
                                if (Char.getMyChar().npcFocus != null) {
                                    if (Char.getMyChar().cx < Char.getMyChar().npcFocus.cx) {
                                        Char.getMyChar().cdir = 1;
                                    } else {
                                        Char.getMyChar().cdir = -1;
                                    }

                                    if (Char.getMyChar().cx < Char.getMyChar().npcFocus.cx) {
                                        Char.getMyChar().npcFocus.cdir = -1;
                                    } else {
                                        Char.getMyChar().npcFocus.cdir = 1;
                                    }

                                    var2 = Math.abs(Char.getMyChar().cx - Char.getMyChar().npcFocus.cx);
                                    var3 = Math.abs(Char.getMyChar().cy - Char.getMyChar().npcFocus.cy);
                                    if (var2 < 60 && var3 < 40) {
                                        GameCanvas.gameAI();
                                        GameCanvas.gameAH();
                                        if (Char.getMyChar().npcFocus.template.npcTemplateId == 13) {
                                            InfoDlg.gameAA();
                                            Service.gI().openUIZone();
                                        } else {
                                            Service.gI().openMenu(Char.getMyChar().npcFocus.template.npcTemplateId);
                                            InfoDlg.gameAA();
                                        }
                                    } else {
                                        Char.getMyChar().currentMovePoint = new MovePoint(Char.getMyChar().npcFocus.cx, Char.getMyChar().cy);
                                        GameCanvas.gameAI();
                                        GameCanvas.gameAH();
                                    }

                                    var10000 = false;
                                    break label448;
                                }

                                if (Char.getMyChar().charFocus != null) {
                                    if (Char.getMyChar().cx < Char.getMyChar().charFocus.cx) {
                                        Char.getMyChar().cdir = 1;
                                    } else {
                                        Char.getMyChar().cdir = -1;
                                    }

                                    var2 = Math.abs(Char.getMyChar().cx - Char.getMyChar().charFocus.cx);
                                    var3 = Math.abs(Char.getMyChar().cy - Char.getMyChar().charFocus.cy);
                                    if (!Char.getMyChar().gameAB(Char.getMyChar().charFocus)) {
                                        if (var2 < 60 && var3 < 40 && Char.getMyChar().charFocus.charID >= 0) {
                                            GameCanvas.gameAI();
                                            if (Char.getMyChar().charFocus.statusMe != 14 && Char.getMyChar().charFocus.statusMe != 5 && TileMap.typeMap == 1) {
                                                var10000 = false;
                                                break label448;
                                            }

                                            if (!this.gameHI) {
                                                MyVector var5;
                                                (var5 = new MyVector()).addElement(new Command(mResources.gameSB[6], 110397));
                                                var5.addElement(new Command(mResources.gameSB[4], 110391));
                                                if ((Char.getMyChar().ctypeClan == 4 || Char.getMyChar().ctypeClan == 3 || Char.getMyChar().ctypeClan == 2) && Char.getMyChar().charFocus.cClanName.equals("")) {
                                                    var5.addElement(new Command(mResources.gameSB[8], 110398));
                                                }

                                                if ((Char.getMyChar().charFocus.ctypeClan == 4 || Char.getMyChar().charFocus.ctypeClan == 3 || Char.getMyChar().charFocus.ctypeClan == 2) && Char.getMyChar().cClanName.equals("")) {
                                                    var5.addElement(new Command(mResources.gameSB[9], 110399));
                                                }

                                                var5.addElement(new Command(mResources.gameSB[7], 12004, Char.getMyChar().charFocus.cName));
                                                if (Char.getMyChar().nClass.classId == 6) {
                                                    var5.addElement(new Command(mResources.gameSB[11] + ": " + (!Char.isAResuscitate ? mResources.gameBQ : mResources.gameBR), 1103991));
                                                }

                                                if (Char.getMyChar().charFocus.statusMe != 14 && Char.getMyChar().charFocus.statusMe != 5) {
                                                    var5.addElement(new Command(mResources.gameSB[0], 110392));
                                                    var5.addElement(new Command(mResources.gameSB[1], 110393));
                                                    var5.addElement(new Command(mResources.gameSB[2], 110394));
                                                } else if (Char.getMyChar().myskill.template.type == 4) {
                                                    var5.addElement(new Command(mResources.gameSB[5], 110395));
                                                }

                                                var5.addElement(new Command(mResources.gameSB[3], 110396));
                                                GameCanvas.menu.gameAA(var5);
                                                GameCanvas.menu.gameAE = 5;
                                            }

                                            this.gameHI = false;
                                        } else {
                                            Char.getMyChar().currentMovePoint = new MovePoint(Char.getMyChar().charFocus.cx, Char.getMyChar().cy);
                                            GameCanvas.gameAI();
                                            GameCanvas.gameAH();
                                        }

                                        var10000 = false;
                                        break label448;
                                    }

                                    if (Char.getMyChar().myskill == null) {
                                        var10000 = false;
                                        break label448;
                                    }

                                    if (Char.getMyChar().arrItemBody[1] == null) {
                                        InfoMe.gameAA(mResources.gameTC);
                                        var10000 = false;
                                        break label448;
                                    }

                                    if (!gameCP()) {
                                        var10000 = false;
                                        break label448;
                                    }

                                    if (Char.getMyChar().cx < Char.getMyChar().charFocus.cx) {
                                        Char.getMyChar().cdir = 1;
                                    } else {
                                        Char.getMyChar().cdir = -1;
                                    }

                                    Char.getMyChar().cvx = 0;
                                    if (Char.getMyChar().gameAC()) {
                                        if (var2 > Char.getMyChar().myskill.fieldAB() || var3 > Char.getMyChar().myskill.fieldAC()) {
                                            Char.getMyChar().currentMovePoint = new MovePoint(Char.getMyChar().charFocus.cx, Char.getMyChar().cy);
                                            GameCanvas.gameAI();
                                            GameCanvas.gameAH();
                                            var10000 = false;
                                            break label448;
                                        }

                                        GameCanvas.gameAI();
                                        GameCanvas.gameAH();
                                    } else if ((Char.getMyChar().myskill.template.id == 24 || Char.getMyChar().myskill.template.id == 40 || Char.getMyChar().myskill.template.id == 42) && var2 <= Char.getMyChar().myskill.fieldAB() && var3 <= Char.getMyChar().myskill.fieldAC()) {
                                        GameCanvas.gameAI();
                                        GameCanvas.gameAH();
                                        Char.getMyChar().cvx = 0;
                                    } else {
                                        if (var2 > Char.getMyChar().myskill.fieldAB() || var3 > Char.getMyChar().myskill.fieldAC() || Char.getMyChar().cy < Char.getMyChar().charFocus.cy) {
                                            Char.getMyChar().currentMovePoint = new MovePoint(Char.getMyChar().charFocus.cx + Char.getMyChar().charFocus.cdir * 12, Char.getMyChar().cy);
                                            GameCanvas.gameAI();
                                            GameCanvas.gameAH();
                                            var10000 = false;
                                            break label448;
                                        }

                                        GameCanvas.gameAI();
                                        GameCanvas.gameAH();
                                        Char.getMyChar().cvx = 0;
                                    }
                                } else if (Char.getMyChar().itemFocus != null) {
                                    if (Char.getMyChar().statusMe != 1) {
                                        var10000 = false;
                                        break label448;
                                    }

                                    if (Char.getMyChar().cx < Char.getMyChar().itemFocus.x) {
                                        Char.getMyChar().cdir = 1;
                                    } else {
                                        Char.getMyChar().cdir = -1;
                                    }

                                    var2 = Math.abs(Char.getMyChar().cx - Char.getMyChar().itemFocus.x);
                                    var3 = Math.abs(Char.getMyChar().cy - Char.getMyChar().itemFocus.y);
                                    if ((var2 > 35 || var3 >= 35) && (auto == 0 || var2 > 48 || var3 > 48)) {
                                        Char.getMyChar().currentMovePoint = new MovePoint(Char.getMyChar().itemFocus.x, Char.getMyChar().cy);
                                        GameCanvas.gameAI();
                                        GameCanvas.gameAH();
                                    } else {
                                        GameCanvas.gameAI();
                                        GameCanvas.gameAH();
                                        Service.gI().pickItem(Char.getMyChar().itemFocus.itemMapID);
                                    }

                                    var10000 = false;
                                    break label448;
                                }
                            }

                            var10000 = true;
                        }
                    } else {
                        var10000 = false;
                    }
                } else {
                    var10000 = false;
                }
            } else {
                var10000 = false;
            }

            if (var10000) {
                MyVector var4;
                if (gameCR()) {
                    (var4 = new MyVector()).addElement(new Command(mResources.gameWU, 151301));
                    GameCanvas.menu.gameAA(var4);
                    return;
                }

                if (Char.getMyChar().mobFocus != null && Char.getMyChar().mobFocus.getTemplate().mobTemplateId == 144 && TileMap.mapID == 130) {
                    (var4 = new MyVector()).addElement(new Command(mResources.gameWY, 151301));
                    GameCanvas.menu.gameAA(var4);
                    return;
                }

                Char.getMyChar().gameAA((SkillPaint) sks[Char.getMyChar().myskill.template.id], 0);
                Char.getMyChar().gameGL = var1;
                if (Char.getMyChar().isWolf) {
                    Char.getMyChar().isWolf = false;
                    Char.getMyChar().timeSummon = System.currentTimeMillis();
                    if (Char.getMyChar().vitaWolf >= 500) {
                        ServerEffect.gameAA(60, Char.getMyChar(), 1);
                    }
                }

                if (Char.getMyChar().gameAP() && !Char.getMyChar().isMotoBehind) {
                    Char.getMyChar().isMoto = false;
                    Char.getMyChar().isMotoBehind = true;
                    ServerEffect.gameAA(60, Char.getMyChar(), 1);
                }

                if (Char.getMyChar().gameBD() && !Char.getMyChar().isMotoBehind) {
                    Char.getMyChar().isNewMount = false;
                    Char.getMyChar().isMotoBehind = true;
                    ServerEffect.gameAA(60, Char.getMyChar(), 1);
                }
            }
        }

        if (!var1) {
            Char.getMyChar().gameGK = Char.getMyChar().myskill;
        }

    }

    private void gameCS() {
        this.gamePI = 0;

        int var1;
        for (var1 = 0; var1 < gameNZ.length; ++var1) {
            if (gameNZ[var1] != null) {
                ++this.gamePI;
            }
        }

        if (this.gamePI <= 1) {
            InfoMe.gameAB();
        } else {
            if (!gameKA || this.gamePJ == -1) {
                gameKA = true;

                for (var1 = 0; var1 < gameNZ.length; ++var1) {
                    if (gameNZ[var1] == Char.getMyChar().myskill) {
                        this.gamePJ = var1;
                        break;
                    }
                }
            }

            ++this.gamePJ;
            if (this.gamePJ >= gameNZ.length) {
                this.gamePJ = 0;
            }

            if (gameNZ[this.gamePJ] == null) {
                this.gamePJ = 0;
            }

            super.center = new Command("", 11059);
        }
    }

    public final void gameAA(Skill var1, boolean var2, boolean var3) {
        this.gamePJ = -1;
        if (var1 != null) {
            if (var1.template.type == 4 && Char.getMyChar().charFocus != null) {
                if (Char.getMyChar().charFocus.gameBB()) {
                    return;
                }

                if (Char.getMyChar().charFocus.statusMe == 14 || Char.getMyChar().charFocus.statusMe == 5) {
                    Service.gI().buffLive(Char.getMyChar().charFocus.charID);
                    if ((TileMap.gameAA(Char.getMyChar().cx, Char.getMyChar().cy) & 2) == 2) {
                        Char.getMyChar().gameAA((SkillPaint) sks[49], 0);
                    } else {
                        Char.getMyChar().gameAA((SkillPaint) sks[49], 1);
                    }
                }
            }

            if (var3) {
                Service.gI().selectSkill(var1.template.id);
            }
        }

        if (var1.template.type != 2) {
            this.resetButton();
        }

        if (var1 != null) {
            Char.getMyChar().myskill = var1;
            if (var1.template.type == 1 && Code.fieldAB != null) {
                Auto.fieldAL = var1;
            }
            if (Char.getMyChar().npcFocus == null && var1.template.type != 4) {
                this.gameAB(var2);
            }
        }

    }

    public static void sortList(int var0) {
        MyVector var6 = var0 == 0 ? vFriend : vEnemies;

        for (int var1 = 0; var1 < var6.size() - 1; ++var1) {
            Friend var2 = (Friend) var6.elementAt(var1);

            for (int var3 = var1 + 1; var3 < var6.size(); ++var3) {
                Friend var4;
                Friend var5;
                if ((var4 = (Friend) var6.elementAt(var3)).type > var2.type) {
                    var5 = var4;
                    var4 = var2;
                    var2 = var5;
                    var6.setElementAt(var5, var1);
                    var6.setElementAt(var4, var3);
                } else if (var4.type == var2.type && var2.friendName.compareTo(var4.friendName) > 0) {
                    var5 = var4;
                    var4 = var2;
                    var2 = var5;
                    var6.setElementAt(var5, var1);
                    var6.setElementAt(var4, var3);
                }
            }
        }

    }

    public static void gameAY() {
        for (int var0 = 0; var0 < vClan.size() - 1; ++var0) {
            Member var1 = (Member) vClan.elementAt(var0);

            for (int var2 = var0 + 1; var2 < vClan.size(); ++var2) {
                Member var3 = (Member) vClan.elementAt(var2);
                Member var4;
                if (gameHR && !gameHS) {
                    if (var3.isOnline && !var1.isOnline) {
                        var4 = var3;
                        var3 = var1;
                        var1 = var4;
                        vClan.setElementAt(var4, var0);
                        vClan.setElementAt(var3, var2);
                    } else if (var3.isOnline && var1.isOnline) {
                        if (var3.type > var1.type) {
                            var4 = var3;
                            var3 = var1;
                            var1 = var4;
                            vClan.setElementAt(var4, var0);
                            vClan.setElementAt(var3, var2);
                        } else if (var3.type == var1.type) {
                            if (var3.pointClan > var1.pointClan) {
                                var4 = var3;
                                var3 = var1;
                                var1 = var4;
                                vClan.setElementAt(var4, var0);
                                vClan.setElementAt(var3, var2);
                            } else if (var1.pointClan == var3.pointClan) {
                                if (var3.pointClanWeek > var1.pointClanWeek) {
                                    var4 = var3;
                                    var3 = var1;
                                    var1 = var4;
                                    vClan.setElementAt(var4, var0);
                                    vClan.setElementAt(var3, var2);
                                } else if (var1.pointClanWeek == var3.pointClanWeek) {
                                    if (var3.level > var1.level) {
                                        var4 = var3;
                                        var3 = var1;
                                        var1 = var4;
                                        vClan.setElementAt(var4, var0);
                                        vClan.setElementAt(var3, var2);
                                    } else if (var1.level == var3.level && var1.name.compareTo(var3.name) > 0) {
                                        var4 = var3;
                                        var3 = var1;
                                        var1 = var4;
                                        vClan.setElementAt(var4, var0);
                                        vClan.setElementAt(var3, var2);
                                    }
                                }
                            }
                        }
                    }
                } else if (gameHS) {
                    if (gameHR) {
                        if (var3.isOnline && !var1.isOnline) {
                            var4 = var3;
                            var3 = var1;
                            var1 = var4;
                            vClan.setElementAt(var4, var0);
                            vClan.setElementAt(var3, var2);
                        } else if (var3.isOnline && var1.isOnline) {
                            if (var3.pointClanWeek > var1.pointClanWeek) {
                                var4 = var3;
                                var3 = var1;
                                var1 = var4;
                                vClan.setElementAt(var4, var0);
                                vClan.setElementAt(var3, var2);
                            } else if (var1.pointClanWeek == var3.pointClanWeek) {
                                if (var3.pointClan > var1.pointClan) {
                                    var4 = var3;
                                    var3 = var1;
                                    var1 = var4;
                                    vClan.setElementAt(var4, var0);
                                    vClan.setElementAt(var3, var2);
                                } else if (var1.pointClan == var3.pointClan) {
                                    if (var3.type > var1.type) {
                                        var4 = var3;
                                        var3 = var1;
                                        var1 = var4;
                                        vClan.setElementAt(var4, var0);
                                        vClan.setElementAt(var3, var2);
                                    } else if (var3.type == var1.type && var1.level == var3.level && var1.name.compareTo(var3.name) > 0) {
                                        var4 = var3;
                                        var3 = var1;
                                        var1 = var4;
                                        vClan.setElementAt(var4, var0);
                                        vClan.setElementAt(var3, var2);
                                    }
                                }
                            }
                        }
                    } else if (var3.pointClanWeek > var1.pointClanWeek) {
                        var4 = var3;
                        var3 = var1;
                        var1 = var4;
                        vClan.setElementAt(var4, var0);
                        vClan.setElementAt(var3, var2);
                    } else if (var1.pointClanWeek == var3.pointClanWeek) {
                        if (var3.pointClan > var1.pointClan) {
                            var4 = var3;
                            var3 = var1;
                            var1 = var4;
                            vClan.setElementAt(var4, var0);
                            vClan.setElementAt(var3, var2);
                        } else if (var1.pointClan == var3.pointClan) {
                            if (var3.type > var1.type) {
                                var4 = var3;
                                var3 = var1;
                                var1 = var4;
                                vClan.setElementAt(var4, var0);
                                vClan.setElementAt(var3, var2);
                            } else if (var3.type == var1.type && var1.level == var3.level && var1.name.compareTo(var3.name) > 0) {
                                var4 = var3;
                                var3 = var1;
                                var1 = var4;
                                vClan.setElementAt(var4, var0);
                                vClan.setElementAt(var3, var2);
                            }
                        }
                    }
                } else if (var3.type > var1.type) {
                    var4 = var3;
                    var3 = var1;
                    var1 = var4;
                    vClan.setElementAt(var4, var0);
                    vClan.setElementAt(var3, var2);
                } else if (var3.type == var1.type) {
                    if (var3.pointClan > var1.pointClan) {
                        var4 = var3;
                        var3 = var1;
                        var1 = var4;
                        vClan.setElementAt(var4, var0);
                        vClan.setElementAt(var3, var2);
                    } else if (var1.pointClan == var3.pointClan) {
                        if (var3.pointClanWeek > var1.pointClanWeek) {
                            var4 = var3;
                            var3 = var1;
                            var1 = var4;
                            vClan.setElementAt(var4, var0);
                            vClan.setElementAt(var3, var2);
                        } else if (var1.pointClanWeek == var3.pointClanWeek) {
                            if (var3.level > var1.level) {
                                var4 = var3;
                                var3 = var1;
                                var1 = var4;
                                vClan.setElementAt(var4, var0);
                                vClan.setElementAt(var3, var2);
                            } else if (var1.level == var3.level && var1.name.compareTo(var3.name) > 0) {
                                var4 = var3;
                                var3 = var1;
                                var1 = var4;
                                vClan.setElementAt(var4, var0);
                                vClan.setElementAt(var3, var2);
                            }
                        }
                    }
                }
            }
        }

    }

    public static void gameAZ() {
        for (int var0 = 0; var0 < Char.getMyChar().vSkillFight.size() - 1; ++var0) {
            Skill var1 = (Skill) Char.getMyChar().vSkillFight.elementAt(var0);

            for (int var2 = var0 + 1; var2 < Char.getMyChar().vSkillFight.size(); ++var2) {
                Skill var3;
                if ((var3 = (Skill) Char.getMyChar().vSkillFight.elementAt(var2)).template.id < var1.template.id) {
                    Skill var4 = var3;
                    var3 = var1;
                    var1 = var4;
                    Char.getMyChar().vSkillFight.setElementAt(var4, var0);
                    Char.getMyChar().vSkillFight.setElementAt(var3, var2);
                }
            }
        }

    }

    private static void gameAH(int var0) {
        Char.getMyChar().cvy = Char.getMyChar().canJumpHigh ? -10 : -8;
        Char.getMyChar().cvx = var0;
        Char.getMyChar().statusMe = 3;
        Char.getMyChar().cp1 = 0;
    }

    public final void gameAD() {
        int var3;
        CodePhu.fieldAE();

        for (var3 = 0; var3 < gameAX.size(); ++var3) {
            CharCountDown var2;
            if ((var2 = (CharCountDown) gameAX.elementAt(var3)) != null) {
                var2.gameAA();
                if (var2.WantDestroy) {
                    gameAX.removeElement(var2);
                }
            }
        }

        if (indexMenu == 4 && GameCanvas.isTouch && mScreen.gameAA(this.gameHG) && this.gameHG != null) {
            GameCanvas.isPointerJustRelease = false;
            GameCanvas.keyPressedz[5] = false;
            mScreen.keyTouch = -1;
            this.gameHG.gameAA();
        }

        if (GameCanvas.gameTick % 200 == 0) {
            Char.gameAR();
        }

        if (shaking != 0 && !GameCanvas.lowGraphic) {
            cmx += NinjaUtil.gameAA(-7, 7);
            if (++count > 20) {
                shaking = 0;
                count = 0;
            }
        } else if (cmx != cmtoX || cmy != cmtoY) {
            if (!isUseitemAuto) {
                gameGV = cmtoX - cmx << 2;
                gameGW = cmtoY - cmy << 2;
            } else {
                gameGV = cmtoX - cmx << 1;
                gameGW = cmtoY - cmy << 2;
            }

            gameGT += gameGV;
            cmx += gameGT >> 4;
            gameGT &= 15;
            gameGU += gameGW;
            cmy += gameGU >> 4;
            gameGU &= 15;
            if (cmx < 24) {
                cmx = 24;
            }

            if (cmx > gameGX) {
                cmx = gameGX;
            }

            if (cmy < 0) {
                cmy = 0;
            }

            if (cmy > gameGY) {
                cmy = gameGY;
            }
        }

        if ((gssx = cmx / TileMap.size - 1) < 0) {
            gssx = 0;
        }

        gssy = cmy / TileMap.size;
        gssxe = gssx + gameGQ;
        gssye = gssy + gameGR;
        if (gssy < 0) {
            gssy = 0;
        }

        if (gssye > TileMap.tmh - 1) {
            gssye = TileMap.tmh - 1;
        }

        if ((TileMap.gssx = (Char.getMyChar().cx - 2 * gW) / TileMap.size) < 0) {
            TileMap.gssx = 0;
        }

        if ((TileMap.gssxe = TileMap.gssx + TileMap.countx) > TileMap.tmw) {
            TileMap.gssx = (TileMap.gssxe = TileMap.tmw) - TileMap.countx;
        }

        if ((TileMap.gssy = (Char.getMyChar().cy - 2 * gH) / TileMap.size) < 0) {
            TileMap.gssy = 0;
        }

        if ((TileMap.gssye = TileMap.gssy + TileMap.county) > TileMap.tmh) {
            TileMap.gssy = (TileMap.gssye = TileMap.tmh) - TileMap.county;
        }

        scrMain.gameAC();
        gameHO.gameAC();
        ChatTextField var10;
        if ((var10 = ChatTextField.gameAA()).isShow) {
            var10.tfChat.gameAC();
            var10.tfChat.getClass();
        }
        if ((var10 = ChatTextField.gameAA()).isShow) {
            var10.tfChat.gameAC();
            if (var10.tfChat.fieldAI) {
                var10.tfChat.fieldAI = false;
                var10.gameAC.gameAA(var10.tfChat.getText(), var10.to);
                var10.tfChat.setText("");
                var10.right.caption = "Đóng";
            }
        }

        if (gamePG >= 0) {
            --gamePG;
        }

        TileMap.gameAB();
        TileMap.fieldAE();

        GameCanvas.gameAA();
        GameCanvas.gameAD();
        int var1;
        if (GameCanvas.isGPRS) {
            MyVector var6 = new MyVector();
            long var12 = System.currentTimeMillis();

            for (var1 = 0; var1 < vCharInMap.size(); ++var1) {
                Char var5;
                (var5 = (Char) vCharInMap.elementAt(var1)).pxw();
                if (var5.gameAQ()) {
                    if (var5.gameGJ && var12 - var5.lastUpdateTime > 10000L && var12 - this.gamePK > 10000L) {
                        var5.gameGJ = false;
                        var5.lastUpdateTime = var12;
                        var6.addElement(var5);
                    }
                } else {
                    var5.lastUpdateTime = var12;
                    var5.gameGJ = true;
                }
            }

            if (var6.size() > 0) {
                Service.gI().requestPlayerInfo(var6);
                this.gamePK = var12;
            }
        } else {
            for (var1 = 0; var1 < vCharInMap.size(); ++var1) {
                ((Char) vCharInMap.elementAt(var1)).pxw();
            }
        }

        Char.getMyChar().pxw();
        if (Char.getMyChar().cHP <= 0 && TileMap.fieldBF && !LockGame.fieldAA) {
            TileMap.fieldAG();
        }

        if (Char.getMyChar().statusMe == 1 && GameCanvas.gameTick % 100 == 0) {
            System.gc();
        }

        for (var1 = 0; var1 < vMob.size(); ++var1) {
            ((Mob) vMob.elementAt(var1)).gameAA();
        }

        for (var1 = 0; var1 < vNpc.size(); ++var1) {
            ((Npc) vNpc.elementAt(var1)).pxw();
        }

        GameCanvas.gameAA().gameAM();

        int[] var10000;
        for (var3 = 0; var3 < 5; ++var3) {
            if (gameQO[var3] != -1) {
                var10000 = gameQO;
                var10000[var3] += Res.abs(gameQN[var3]);
                if (gameQO[var3] > 30) {
                    gameQO[var3] = -1;
                }

                var10000 = gameQK;
                var10000[var3] += gameQM[var3];
                var10000 = gameQL;
                var10000[var3] += gameQN[var3];
            }
        }

        for (var3 = 0; var3 < gameHJ.size(); ++var3) {
            Lanterns var7;
            Lanterns var16 = var7 = (Lanterns) gameHJ.elementAt(var3);
            var16.y -= var7.dy;
            if (var7.yStart - var7.y > 150) {
                var7.isEnd = true;
            }

            if (((Lanterns) gameHJ.elementAt(var3)).isEnd) {
                gameHJ.removeElementAt(var3);
            }
        }

        for (var3 = 0; var3 < 2; ++var3) {
            if (gameQS[var3] != -1) {
                int var10002 = gameQS[var3]++;
                var10000 = gameQQ;
                var10000[var3] += gameQU[var3] << 2;
                var10002 = gameQR[var3]--;
                if (gameQS[var3] >= 6) {
                    gameQS[var3] = -1;
                } else {
                    gameQT[var3] = (gameQS[var3] >> 1) % 3;
                }
            }
        }

        if (indexMenu != -1) {
            if (gameTF != gameTG) {
                gameTI = gameTG - gameTF << 2;
                gameTH += gameTI;
                gameTF += gameTH >> 4;
                gameTH &= 15;
            }

            if (Math.abs(gameTG - gameTF) < 15 && gameTF < 0) {
                gameTG = 0;
            }

            if (Math.abs(gameTG - gameTF) < 15 && gameTF > 0) {
                gameTG = 0;
            }
        }

        GameCanvas.setOffset();

        for (var1 = 0; var1 < vMobAttack.size(); ++var1) {
            ((MobAttack) vMobAttack.elementAt(var1)).gameAA();
        }

        for (var1 = 0; var1 < vItemMap.size(); ++var1) {
            ItemMap var13;
            if ((var13 = (ItemMap) vItemMap.elementAt(var1)).status == 2 && var13.x == var13.xEnd && var13.y == var13.yEnd) {
                vItemMap.removeElement(var13);
                if (Char.getMyChar().itemFocus != null && Char.getMyChar().itemFocus.equals(var13)) {
                    Char.getMyChar().itemFocus = null;
                }
            } else if (var13.status <= 0) {
                var13.status = (byte) (var13.status - 4);
                if (var13.status < -12) {
                    var13.y -= 12;
                    var13.status = 1;
                }
            } else {
                if (var13.vx == 0) {
                    var13.x = var13.xEnd;
                }

                if (var13.vy == 0) {
                    var13.y = var13.yEnd;
                }

                if (var13.x != var13.xEnd) {
                    var13.x += var13.vx;
                    if (var13.vx > 0 && var13.x > var13.xEnd || var13.vx < 0 && var13.x < var13.xEnd) {
                        var13.x = var13.xEnd;
                    }
                }

                if (var13.y != var13.yEnd) {
                    var13.y += var13.vy;
                    if (var13.vy > 0 && var13.y > var13.yEnd || var13.vy < 0 && var13.y < var13.yEnd) {
                        var13.y = var13.yEnd;
                    }
                }
                if (var13.fieldAK && System.currentTimeMillis() - var13.fieldAL >= 10000L) {
                    var13.fieldAK = false;
                }
            }
        }

        for (var1 = 0; var1 < vMobSoul.size(); ++var1) {
            ((MobSoul) vMobSoul.elementAt(var1)).gameAA();
        }

        for (var1 = 0; var1 < gameBP.size(); ++var1) {
            Set var14;
            ++(var14 = (Set) gameBP.elementAt(var1)).gameAA;
            if (var14.gameAA == 5) {
                var14.gameAA = 0;
                gameBP.removeElement(var14);
            }

            Mob var8 = Mob.gameAA(0);
            var14.gameAB = var8.x;
            var14.gameAC = var8.y - var8.h / 2;
            var14.gameAD = ((Char) vCharInMap.elementAt(0)).cx;
            var14.gameAE = ((Char) vCharInMap.elementAt(0)).cy - Char.getMyChar().chh;

            int[] arr = new int[1];

            if (Res.abs(arr[0] - var14.gameAB) > 5 || Res.abs(arr[0] - var14.gameAC) > 5) {
                arr[0] = var14.gameAB;
                arr[0] = var14.gameAC;
            }

            if (Res.abs(arr[0] - var14.gameAD) > 5 || Res.abs(arr[0] - var14.gameAE) > 5) {
                arr[0] = var14.gameAD;
                arr[0] = var14.gameAE;
            }
        }

        if ((TileMap.tmw * TileMap.sizeMiniMap >= TileMap.wMiniMap || TileMap.tmh * TileMap.sizeMiniMap >= TileMap.hMiniMap) && System.currentTimeMillis() / 100L > 20L) {
            TileMap.gameAA();
        }

        for (var1 = Effect2.vRemoveEffect2.size() - 1; var1 >= 0; --var1) {
            Effect2.vEffect2.removeElement(Effect2.vRemoveEffect2.elementAt(var1));
            Effect2.vRemoveEffect2.removeElementAt(var1);
        }

        for (var1 = 0; var1 < Effect2.vEffect2.size(); ++var1) {
            ((Effect2) Effect2.vEffect2.elementAt(var1)).update();
        }

        for (var1 = 0; var1 < Effect2.vEffect2Outside.size(); ++var1) {
            ((Effect2) Effect2.vEffect2Outside.elementAt(var1)).update();
        }

        for (var1 = 0; var1 < Effect2.vAnimateEffect.size(); ++var1) {
            ((Effect2) Effect2.vAnimateEffect.elementAt(var1)).update();
        }

        for (var1 = 0; var1 < Mob.vEggMonter.size(); ++var1) {
            EggMonters var9;
            if ((var9 = (EggMonters) Mob.vEggMonter.elementAt(var1)) != null) {
                if (var9.gameAA()) {
                    if (var9.status == 0) {
                        ++var9.vy;
                        var9.y += var9.vy;
                        ++var9.frame;
                        if (var9.frame > 3) {
                            var9.frame = 0;
                        }

                        if ((TileMap.gameAA(var9.x, var9.y) & 2) == 2) {
                            var9.status = 1;
                            var9.vy = 0;
                        }
                    } else if (var9.status == 1) {
                        ++var9.frame;
                        if (var9.frame > 6) {
                            var9.frame = 6;
                            EggMonters.ownerEgg.status = 5;
                        }
                    }
                }

                if (var9.frame == 6) {
                    if (EggMonters.ownerEgg != null) {
                        EggMonters.ownerEgg.status = 5;
                    }

                    Mob.vEggMonter.removeElementAt(var1);
                }
            }
        }

        SmallImage.gameAA();
        if (this.cLastFocusID >= 0 && vCharInMap.size() > 0) {
            int var11;
            if ((var11 = Char.gameAD(this.cLastFocusID)) >= 0 && var11 < vCharInMap.size()) {
                Char var15;
                if ((var15 = (Char) vCharInMap.elementAt(var11)) != null && Char.gameAA(var15) && !var15.gameBB()) {
                    Char.getMyChar().mobFocus = null;
                    Char.getMyChar().gameAV();
                    Char.getMyChar().itemFocus = null;
                    Char.getMyChar();
                    Char.isManualFocus = true;
                    Char.getMyChar().charFocus = var15;
                }
            } else {
                this.cLastFocusID = -1;
                Char.getMyChar().charFocus = null;
            }
        } else {
            this.cLastFocusID = -1;
        }

        Info.gameAA();
        InfoMe.gameAA();
        if (currentCharViewInfo != null && currentCharViewInfo.charID != Char.getMyChar().charID) {
            currentCharViewInfo.pxw();
        }

        ++this.gamePL;
        if (this.gamePL > 3) {
            this.gamePL = 0;
        }

        if (gameHU) {
            gameHK = 40;
        } else {
            gameHK = 28;
        }

        EffectAuto.gameAB();
        EffectAuto.gameAC();
        if (GameCanvas.isKiemduyet_info) {
            GameCanvas.regScr.update();
        }

    }

    private static void gameAC(mGraphics var0) {
        for (int var1 = 0; var1 < gameAX.size(); ++var1) {
            CharCountDown var2;
            if ((var2 = (CharCountDown) gameAX.elementAt(var1)) != null) {
                var2.gameAA(var0, GameCanvas.w, gamePM + var1 * 18 + 15);
            }
        }

    }

    public final void paint(mGraphics var1) {
        if (Char.ischangingMap) {
            var1.gameAA(0);
            var1.gameAC(0, 0, GameCanvas.w, GameCanvas.h);
            mFont.tahoma_7b_yellow.gameAA(var1, mResources.gameFD, GameCanvas.hw, GameCanvas.hh + 20, 2);
            GameCanvas.gameAA(GameCanvas.hw, GameCanvas.hh, var1, false);
        } else {
            GameCanvas.paint(var1);
            var1.gameAA(-cmx, -cmy);

            int var2;
            for (var2 = 0; var2 < vItemTreeBehind.size(); ++var2) {
                ((ItemTree) vItemTreeBehind.elementAt(var2)).gameAA(var1);
            }

            TileMap.gameAA(var1);

            for (var2 = 0; var2 < vItemTreeBetwen.size(); ++var2) {
                ((ItemTree) vItemTreeBetwen.elementAt(var2)).gameAA(var1);
            }

            for (var2 = 0; var2 < vMob.size(); ++var2) {
                ((Mob) vMob.elementAt(var2)).gameAA(var1);
            }

            for (var2 = 0; var2 < Mob.vEggMonter.size(); ++var2) {
                EggMonters var3;
                if ((var3 = (EggMonters) Mob.vEggMonter.elementAt(var2)).gameAA()) {
                    var1.gameAA(gameFR, 0, var3.frame << 5, 32, 32, 0, var3.x, var3.y, 33);
                }
            }

            for (var2 = 0; var2 < vBuNhin.size(); ++var2) {
                BuNhin var5;
                BuNhin var13;
                if ((var5 = var13 = (BuNhin) vBuNhin.elementAt(var2)).x < cmx ? false : (var5.x > cmx + gW ? false : (var5.y < cmy ? false : var5.y <= cmy + gH + 30))) {
                    mFont.tahoma_7_yellow.gameAA(var1, var13.name, var13.x, var13.y - 32, 2, mFont.tahoma_7_grey);
                    SmallImage.gameAA(var1, 1180, var13.x, var13.y, 0, 33);
                    if (var13.isInjure) {
                        SmallImage.gameAA(var1, 288, var13.x, var13.y, 0, 33);
                        var13.isInjure = false;
                    }
                }
            }

            for (var2 = 0; var2 < vNpc.size(); ++var2) {
                ((Npc) vNpc.elementAt(var2)).gameAA(var1);
            }

            mGraphics var4 = var1;
            GameScr var14 = this;

            int var7;
            int var9;
            int var18;
            for (var9 = 0; var9 < TileMap.vGo.size(); ++var9) {
                Waypoint var10;
                if ((var10 = (Waypoint) TileMap.vGo.elementAt(var9)).minY != 0 && var10.maxY < TileMap.pxh - 24) {
                    if (var10.maxX <= TileMap.pxw / 2) {
                        if (!GameCanvas.isTouch) {
                            SmallImage.gameAA(var4, 1213, var10.maxX + 12 + var14.gamePL, var10.maxY - 12, 2, StaticObj.VCENTER_HCENTER);
                        } else {
                            SmallImage.gameAA(var4, 1213, var10.maxX + 12 + var14.gamePL, var10.maxY - 32, 2, StaticObj.VCENTER_HCENTER);
                        }
                    } else if (var10.minX >= TileMap.pxw / 2) {
                        if (!GameCanvas.isTouch) {
                            SmallImage.gameAA(var4, 1213, var10.minX - 12 - var14.gamePL, var10.maxY - 12, 0, StaticObj.VCENTER_HCENTER);
                        } else {
                            SmallImage.gameAA(var4, 1213, var10.minX - 12 - var14.gamePL, var10.maxY - 32, 0, StaticObj.VCENTER_HCENTER);
                        }
                    }
                } else if (var10.maxY <= TileMap.pxh / 2) {
                    var18 = var10.minX + (var10.maxX - var10.minX) / 2;
                    var7 = var10.minY + (var10.maxY - var10.minY) / 2 + var14.gamePL;
                    if (GameCanvas.isTouch) {
                        var7 = var10.maxY + (var10.maxY - var10.minY) + var14.gamePL + 10;
                    }

                    SmallImage.gameAA(var4, 1213, var18, var7, 6, StaticObj.VCENTER_HCENTER);
                } else if (var10.minY >= TileMap.pxh / 2) {
                    SmallImage.gameAA(var4, 1213, var10.minX + (var10.maxX - var10.minX) / 2, var10.minY - 12 - var14.gamePL, 4, StaticObj.VCENTER_HCENTER);
                }
            }

            var1.gameAD(0, -200, GameCanvas.w - var1.gameAA(), 200 + GameCanvas.h - var1.gameAB());
            GameCanvas.gameAA().gameAB(var1);

            for (var2 = 0; var2 < vCharInMap.size(); ++var2) {
                Char var15 = null;

                try {
                    var15 = (Char) vCharInMap.elementAt(var2);
                } catch (Exception var11) {
                }

                if (var15 != null) {
                    if (TileMap.mapID == 111 && var2 > 19) {
                        var15.gameAC(var1);
                    } else {
                        var15.gameAA(var1);
                    }
                }
            }

            for (var2 = 0; var2 < vParty.size(); ++var2) {
                Party var17;
                if ((var17 = (Party) vParty.elementAt(var2)).c != null && var17.c != Char.getMyChar()) {
                    var17.c.gameAB(var1);
                }
            }

            mGraphics var19 = var1;

            int var16;
            for (var16 = 0; var16 < 5; ++var16) {
                if (gameQO[var16] != -1 && GameCanvas.gameAE(gameQK[var16], gameQL[var16])) {
                    if (gameQP[var16] == 0) {
                        mFont.number_red.gameAA(var19, gameQJ[var16], gameQK[var16], gameQL[var16], 2);
                    } else if (gameQP[var16] == 1) {
                        mFont.number_yellow.gameAA(var19, gameQJ[var16], gameQK[var16], gameQL[var16], 2);
                    } else if (gameQP[var16] == 2) {
                        mFont.number_green.gameAA(var19, gameQJ[var16], gameQK[var16], gameQL[var16], 2);
                    } else if (gameQP[var16] == 3) {
                        mFont.tahoma_7b_yellow.gameAA(var19, gameQJ[var16], gameQK[var16], gameQL[var16], 2, mFont.tahoma_7b_blue);
                    } else if (gameQP[var16] == 8) {
                        mFont.tahoma_7b_white.gameAA(var19, gameQJ[var16], gameQK[var16], gameQL[var16], 2, mFont.tahoma_7b_blue);
                    } else if (gameQP[var16] == 4) {
                        SmallImage.gameAA(var19, 1062, gameQK[var16], gameQL[var16], 0, 3);
                    } else if (gameQP[var16] == 5) {
                        mFont.number_orange.gameAA(var19, gameQJ[var16], gameQK[var16], gameQL[var16], 2);
                    } else if (gameQP[var16] == 6) {
                        mFont.tahoma_7_yellow.gameAA(var19, gameQJ[var16], gameQK[var16], gameQL[var16], 2, mFont.tahoma_7_red);
                    } else if (gameQP[var16] == 7) {
                        SmallImage.gameAA(var19, 655, gameQK[var16], gameQL[var16], 0, 3);
                    }
                }
            }

            var19 = var1;

            for (var16 = 0; var16 < gameHJ.size(); ++var16) {
                Lanterns var20 = (Lanterns) gameHJ.elementAt(var16);
                if (GameCanvas.gameTick % 10 < 8) {
                    SmallImage.gameAA(var19, 1292, var20.x, var20.y, 0, 3);
                } else {
                    SmallImage.gameAA(var19, 1291, var20.x, var20.y, 0, 3);
                }
            }

            var19 = var1;

            for (var16 = 0; var16 < 2; ++var16) {
                if (gameQS[var16] != -1) {
                    if (gameQU[var16] == 1) {
                        var19.gameAA(gameQV[gameQT[var16]], gameQQ[var16], gameQR[var16], 3);
                    } else {
                        var19.gameAA(gameQV[gameQT[var16]], 0, 0, mGraphics.gameAA(gameQV[gameQT[var16]]), mGraphics.gameAB(gameQV[gameQT[var16]]), 2, gameQQ[var16], gameQR[var16], 3);
                    }
                }
            }

            Char.getMyChar().gameAA(var1);
            var4 = var1;
            var14 = this;
            if (Char.getMyChar().isCaptcha) {
                for (var18 = 0; var18 < var14.gameUV.length; ++var18) {
                    if (var14.gameUV[var18] != -1) {
                        byte var24 = 0;
                        if (var14.gameUV[var18] == 0) {
                            var24 = 2;
                        } else if (var14.gameUV[var18] == 1) {
                            var24 = 6;
                        } else if (var14.gameUV[var18] == 2) {
                            var24 = 0;
                        }

                        SmallImage.gameAA(var4, 989, Char.getMyChar().cx + var18 * 10 - (var14.gameUV.length - 1) * 10 / 2, Char.getMyChar().cy - 40, var24, 3);
                    }
                }
            }

            for (var2 = 0; var2 < gameBP.size(); ++var2) {
                Set var10000 = (Set) gameBP.elementAt(var2);
            }

            byte var22;
            for (var2 = 0; var2 < vItemMap.size(); ++var2) {
                ItemMap var21;
                if ((var21 = (ItemMap) vItemMap.elementAt(var2)).imgCaptcha != null && var21.imgCaptcha.img != null) {
                    var22 = 0;
                    if (var21.status <= 0) {
                        var22 = var21.status;
                    }

                    var1.gameAA(var21.imgCaptcha.img, var21.x, var21.y + var22, 33);
                } else {
                    var22 = 0;
                    if (var21.status <= 0) {
                        var22 = var21.status;
                    }
                    SmallImage.gameAAA(var1, var21.template.iconID, var21.x, var21.y + var22, 0, 33);
                    if (Char.getMyChar().itemFocus != null && Char.getMyChar().itemFocus.equals(var21) && var21.status != 2) {
                        SmallImage.gameAAA(var1, 988, var21.x, var21.y - 20, 0, 33);
                    }
                }
            }

            for (var2 = 0; var2 < vMobSoul.size(); ++var2) {
                ((MobSoul) vMobSoul.elementAt(var2)).gameAA(var1);
            }

            TileMap.gameAC(var1);
            if (Code.fieldAN > 0) {
                var1.gameAA(16711680);
                var1.fieldAE(Code.fieldAO - Code.fieldAN, Code.fieldAP - Code.fieldAN, Code.fieldAN << 1, Code.fieldAN << 1);
            }

            if (Code.fieldAM > 0) {
                var1.gameAA(65280);
                var1.fieldAE(Char.getMyChar().cx - Code.fieldAM, Char.getMyChar().cy - Code.fieldAM, Code.fieldAM << 1, Code.fieldAM << 1);
            }

            for (var2 = 0; var2 < Effect2.vEffect2.size(); ++var2) {
                ((Effect2) Effect2.vEffect2.elementAt(var2)).paint(var1);
            }

            for (var2 = 0; var2 < vItemTreeFront.size(); ++var2) {
                ((ItemTree) vItemTreeFront.elementAt(var2)).gameAA(var1);
            }

            if (!GameCanvas.lowGraphic) {
                for (var2 = 0; var2 < Effect2.vAnimateEffect.size(); ++var2) {
                    ((Effect2) Effect2.vAnimateEffect.elementAt(var2)).paint(var1);
                }
            }

            for (var2 = 0; var2 < vMobAttack.size(); ++var2) {
                vMobAttack.elementAt(var2);
            }

            var4 = var1;

            int var8;
            try {
                if ((var22 = gameBF()) != -1) {
                    Npc var25 = null;

                    for (var8 = 0; var8 < vNpc.size(); ++var8) {
                        Npc var26;
                        if ((var26 = (Npc) vNpc.elementAt(var8)).template.npcTemplateId == var22) {
                            if (var25 == null) {
                                var25 = var26;
                            } else if (Res.abs(var26.cx - Char.getMyChar().cx) < Res.abs(var25.cx - Char.getMyChar().cx)) {
                                var25 = var26;
                            }
                        }
                    }

                    if (var25 != null && var25.statusMe != 15 && (var25.cx <= cmx || var25.cx >= cmx + gW || var25.cy <= cmy || var25.cy >= cmy + gH) && GameCanvas.gameTick % 10 >= 5) {
                        var8 = var25.cx - Char.getMyChar().cx;
                        var9 = var25.cy - Char.getMyChar().cy;
                        int var27 = 0;
                        var2 = 0;
                        byte var23 = 0;
                        if (var8 > 0 && var9 >= 0) {
                            if (Res.abs(var8) >= Res.abs(var9)) {
                                var27 = gW - 10;
                                var2 = gH / 2 + 30;
                                if (GameCanvas.isTouch) {
                                    var2 = gH / 2 + 10;
                                }

                                var23 = 0;
                            } else {
                                var27 = gW / 2;
                                var2 = gH - 10;
                                var23 = 5;
                            }
                        } else if (var8 >= 0 && var9 < 0) {
                            if (Res.abs(var8) >= Res.abs(var9)) {
                                var27 = gW - 10;
                                var2 = gH / 2 + 30;
                                if (GameCanvas.isTouch) {
                                    var2 = gH / 2 + 10;
                                }

                                var23 = 0;
                            } else {
                                var27 = gW / 2;
                                var2 = 10;
                                var23 = 6;
                            }
                        }

                        if (var8 < 0 && var9 >= 0) {
                            if (Res.abs(var8) >= Res.abs(var9)) {
                                var27 = 10;
                                var2 = gH / 2 + 30;
                                if (GameCanvas.isTouch) {
                                    var2 = gH / 2 + 10;
                                }

                                var23 = 3;
                            } else {
                                var27 = gW / 2;
                                var2 = gH - 10;
                                var23 = 5;
                            }
                        } else if (var8 <= 0 && var9 < 0) {
                            if (Res.abs(var8) >= Res.abs(var9)) {
                                var27 = 10;
                                var2 = gH / 2 + 30;
                                if (GameCanvas.isTouch) {
                                    var2 = gH / 2 + 10;
                                }

                                var23 = 3;
                            } else {
                                var27 = gW / 2;
                                var2 = 10;
                                var23 = 6;
                            }
                        }

                        gameAB(var4);
                        SmallImage.gameAA(var4, 992, var27, var2, var23, StaticObj.VCENTER_HCENTER);
                    }
                }
            } catch (Exception var12) {
            }

            var4 = var1;
            if (GameCanvas.isTouch) {
                gameAB(var1);
                this.gameCU();
                var18 = Char.getMyChar().cHP * gameRE / Char.getMyChar().cMaxHP;
                var7 = Char.getMyChar().cMP * gameRF / Char.getMyChar().cMaxMP;
                var8 = (int) (Char.getMyChar().gameBE * (long) gameRG / exps[Char.getMyChar().clevel]);
                if (var18 > gameRE) {
                    var18 = 0;
                }

                var1.gameAA(-10585344);
                var1.gameAC(0, gameRD - 10, gameRG, 3);
                var1.gameAA(-10427136);
                var1.gameAC(0, gameRD - 10, var8, 3);
                var1.gameAA(-9756672);
                var1.gameAC(0, gameRD - 10, gameRG, 1);
                var1.gameAC(0, gameRD - 7, gameRG, 1);

                for (var9 = 0; var9 < 10; ++var9) {
                    var4.gameAC(var9 * gameRG / 10 - 1, gameRD - 10, 1, 3);
                }

                var4.gameAA(-1769452);
                var4.gameAA(gameNF, gameRC - 1, gameRD, 0);
                var4.gameAC(gameRC, gameRD, var18, 9);
                var4.gameAA(-16755227);
                var4.gameAA(gameNF, gameRC - 28, gameRD + 13, 0);
                var4.gameAC(gameRC, gameRD + 16, var7, 7);
                var4.gameAA(gameNE, 0, gameRD - 7, 0);
                mFont.number_white.gameAA(var4, "" + Char.getMyChar().cHP, gameRC + gameRE / 2 - 30, gameRD + 1, 0);
                mFont.number_white.gameAA(var4, "" + Char.getMyChar().cMP, gameRC + gameRE / 2 - 30, gameRD + 15, 0);
                mFont.tahoma_8b.gameAA(var4, "" + Char.getMyChar().clevel, gameRC - 27, gameRD + 1, 2);
                long var28 = 0L;
                if (Char.getMyChar().cExpDown > 0L) {
                    var28 = Char.getMyChar().cExpDown * 10000L / exps[Char.getMyChar().clevel];
                } else {
                    var28 = Char.getMyChar().gameBE * 10000L / exps[Char.getMyChar().clevel];
                }
                mFont.tahoma_7_yellow.gameAA(var4, Res.fieldAB().get(11) + ":" + Res.fieldAB().get(12) + ":" + Res.fieldAB().get(13), gameRC + 20, gameRD + 27, 0, mFont.tahoma_7_red);
                var2 = (int) (var28 % 100L);
                mFont.tahoma_7_white.gameAA(var4, (Char.getMyChar().cExpDown > 0L ? "-" + var28 / 100L : "" + var28 / 100L) + "." + (var2 < 10 ? "0" + var2 : "" + var2) + "%", gameRC - 27, gameRD + 13, 2);
            }

            if (!Char.getMyChar().isCaptcha) {
                this.gameAH(var1);
                gameAB(var1);
                this.gameAG(var1);
                gameAB(var1);
                TileMap.gameAB(var1);
                var1.gameAA(-var1.gameAA(), -var1.gameAB());
                if (!GameCanvas.isTouch || GameCanvas.isTouch && !GameCanvas.gameAH) {
                    var1.gameAD(0, gameQX - 4, GameCanvas.w, 100);
                    var1.gameAA(3612190);
                    var1.gameAC(gameRC - 44, gameRD, 19, 19);
                    var1.gameAA(265220);
                    var1.gameAC(gameQZ, gameQX + 35, gameRB, 1);
                    var1.gameAC(gameQZ, gameQX + 33, gameRB, 1);
                    var1.gameAC(gameQZ, gameQX + 30, gameRB, 1);
                    var1.gameAC(gameQZ, gameQX + 28, gameRB, 1);
                    var1.gameAC(gameQZ, gameQX + 26, gameRB, 1);
                    var1.gameAC(gameQZ, gameQX + 12, gameRB, 1);
                    var1.gameAC(gameQZ, gameQX + 24, gameRB, 1);
                    var1.gameAC(gameQZ, gameQX + 18, gameRB, 1);
                    var1.gameAC(gameQZ, gameQX + 16, gameRB, 1);
                    var1.gameAC(gameQZ, gameQX + 2, gameRB, 1);
                    var1.gameAC(gameQZ, gameQX + 4, gameRB, 1);
                    var1.gameAC(gameQZ, gameQX + 6, gameRB, 1);
                    var1.gameAC(gameQZ, gameQX + 14, gameRB, 1);
                    var1.gameAA(12562018);
                    var1.gameAC(gameQZ, gameQX + 5, gameRB, 1);
                    var1.gameAC(gameQZ, gameQX + 17, gameRB, 1);
                    var1.gameAC(gameQZ, gameQX + 34, gameRB, 1);
                    var1.gameAC(gameQZ, gameQX + 29, gameRB, 1);
                    var1.gameAA(14667167);
                    var1.gameAC(gameQZ, gameQX + 3, gameRB, 1);
                    var1.gameAC(gameQZ, gameQX + 15, gameRB, 1);
                    var1.gameAC(gameQZ, gameQX + 27, gameRB, 1);
                    var1.gameAA(3355443);
                    var1.gameAC(gameQZ, gameQX + 7, gameRB, 5);
                    var1.gameAC(gameQZ, gameQX + 19, gameRB, 5);
                    var1.gameAC(gameQZ, gameQX + 31, gameRB, 2);
                    var1.gameAA(12281361);
                    var1.gameAC(gameQZ, gameQX + 25, gameRB, 1);
                    var1.gameAC(gameQZ, gameQX + 13, gameRB, 1);
                    var1.gameAA(gameRI[0], 0, gameQX + 2, 0);
                    var1.gameAA(gameRI[1], 0 + gameQY, gameQX - 4, 24);
                    if ((var16 = Char.getMyChar().cHP * gameRE / Char.getMyChar().cMaxHP) > gameRE) {
                        var16 = 0;
                    }

                    var1.gameAA(7798784);
                    var1.gameAC(gameRC, gameRD, var16, 2);
                    var1.gameAA(13369344);
                    var1.gameAC(gameRC, gameRD + 1, var16, 4);
                    if ((var16 = Char.getMyChar().cMP * gameRE / Char.getMyChar().cMaxMP) > gameRE) {
                        var16 = 0;
                    }

                    var1.gameAA(4488);
                    var1.gameAC(gameRC, gameRD + 12, var16, 2);
                    var1.gameAA(4573);
                    var1.gameAC(gameRC, gameRD + 14, var16, gameRH - 2);
                    var18 = (int) (Char.getMyChar().gameBE * (long) gameRG / exps[Char.getMyChar().clevel]);
                    var1.gameAA(94373);
                    var1.gameAC(46, gameQX + 31, var18, 1);
                    var1.gameAA(65535);
                    var1.gameAC(46, gameQX + 32, var18, 1);
                    mFont.number_yellow.gameAA(var1, "" + Char.getMyChar().clevel, 28, gameQX + 9, 2);
                    long var29 = 0L;
                    if (Char.getMyChar().cExpDown > 0L) {
                        var29 = Char.getMyChar().cExpDown * 10000L / exps[Char.getMyChar().clevel];
                    } else {
                        var29 = Char.getMyChar().gameBE * 10000L / exps[Char.getMyChar().clevel];
                    }

                    var9 = (int) (var29 % 100L);
                    mFont.tahoma_7_white.gameAA(var1, (Char.getMyChar().cExpDown > 0L ? "-" + var29 / 100L : "" + var29 / 100L) + "." + (var9 < 10 ? "0" + var9 : "" + var9) + "%", 24, gameQX + 23, 2);
                    mFont.number_green.gameAA(var1, "" + hpPotion, gameQY - 11, gameQX + 6, 2);
                    mFont.number_green.gameAA(var1, "" + mpPotion, gameQY - 11, gameQX + 18, 2);
                    mFont.number_white.gameAA(var1, "" + Char.getMyChar().cHP, gameRC + gameRE / 2, gameQX + 6, 2);
                    mFont.number_white.gameAA(var1, "" + Char.getMyChar().cMP, gameRC + gameRE / 2, gameQX + 18, 2);
                    if (Char.getMyChar().vSkillFight.size() > 0 && Char.getMyChar().myskill != null) {
                        int var10001 = gameQZ - 28;
                        int var10002 = gameRD + 7;
                        Char.getMyChar().myskill.gameAA(var10001, var10002, var1);
                    }

                    var1.gameAA(9463099);
                    var1.gameAC(0, gameQX + 35, GameCanvas.w, 1);
                } else {
                    Paint.gameAA(var1);
                }

                if (GameCanvas.isTouch) {
                    gameKA = true;
                }

                gameAB(var1);
                this.gameAJ(var1);
                gameAB(var1);
                var1.gameAD(0, 0, GameCanvas.w, GameCanvas.h);

                for (var2 = 0; var2 < Effect2.vEffect2Outside.size(); ++var2) {
                    ((Effect2) Effect2.vEffect2Outside.elementAt(var2)).paint(var1);
                }

                gameAB(var1);
                if (isPaintInfoMe) {
                    if (indexMenu == 0) {
                        this.gameAA(var1, mResources.gameGG);
                    }

                    this.gameAO(var1);
                    this.gameBE(var1);
                    if (indexMenu == 3) {
                        label796:
                        {
                            gameAB(var1);
                            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
                            if (gameHL == 1) {
                                var1.gameAA(Paint.COLORDARK);
                                var1.gameAC(popupX + 7, popupY + 32, popupW - 14, popupH - 40);
                                var1.gameAA(16777215);
                            } else {
                                var1.gameAA(10249521);
                            }

                            var1.gameAB(popupX + 7, popupY + 32, popupW - 14, popupH - 40);
                            gameAA(var1, mResources.gameGG[indexMenu], true);
                            gameTD = popupX + 17;
                            gameTE = popupY + 34;
                            scrMain.gameAA(gameHM, 12, popupX, popupY + 35, popupW, popupH - 44, true, 1);
                            scrMain.gameAA(var1);
                            mFont var30;
                            String var31;
                            if (gameNT == 0) {
                                gameHM = 19;
                                var18 = gameTE;
                                if (currentCharViewInfo == null) {
                                    break label796;
                                }

                                mFont.tahoma_7b_white.gameAA(var1, mResources.gameMP[0] + currentCharViewInfo.cName, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMP[1] + currentCharViewInfo.cPk;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMP[2] + currentCharViewInfo.clevel;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_blue1;
                                var31 = mResources.gameMP[3] + currentCharViewInfo.nClass.name;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_blue1;
                                var31 = mResources.gameMP[4] + mResources.gamePK[currentCharViewInfo.gameAA()];
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMP[5] + currentCharViewInfo.cHP + "/" + currentCharViewInfo.cMaxHP;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMP[6] + currentCharViewInfo.cMP + "/" + currentCharViewInfo.cMaxMP;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMP[7] + currentCharViewInfo.gameAB();
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMP[8] + (currentCharViewInfo.cdame - currentCharViewInfo.cdame / 10) + "-" + currentCharViewInfo.cdame;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMP[9] + currentCharViewInfo.cResFire;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMP[10] + currentCharViewInfo.cResIce;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMP[11] + currentCharViewInfo.cResWind;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMP[12] + currentCharViewInfo.cdameDown;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMP[13] + currentCharViewInfo.cExactly;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMP[14] + currentCharViewInfo.cMiss;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMP[15] + currentCharViewInfo.cFatal;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMP[16] + currentCharViewInfo.cReactDame;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMP[17] + currentCharViewInfo.sysUp;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMP[18] + currentCharViewInfo.sysDown;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                            } else if (gameNT == 1) {
                                gameHM = 20;
                                var18 = gameTE;
                                if (currentCharViewInfo == null) {
                                    break label796;
                                }

                                mFont.tahoma_7b_white.gameAA(var1, mResources.gameMS[15] + (currentCharViewInfo.cClanName.equals("") ? mResources.gameKP : currentCharViewInfo.cClanName), gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMS[0] + currentCharViewInfo.pointUydanh;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_blue1;
                                var31 = mResources.gameMS[11] + currentCharViewInfo.countFinishDay + "/20";
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_blue1;
                                var31 = mResources.gameMS[12] + currentCharViewInfo.countLoopBoos + mResources.gameMQ;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_blue1;
                                var31 = mResources.gameMS[16] + currentCharViewInfo.countPB + mResources.gameMQ;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMS[13] + currentCharViewInfo.limitTiemnangso;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMS[14] + currentCharViewInfo.limitKynangso;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMS[18] + currentCharViewInfo.limitPhongLoi + mResources.gameMQ;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMS[19] + currentCharViewInfo.limitBangHoa + mResources.gameMQ;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMS[17] + currentCharViewInfo.pointTinhTu;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMS[1] + currentCharViewInfo.pointVukhi;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMS[2] + currentCharViewInfo.pointLien;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMS[3] + currentCharViewInfo.pointNhan;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMS[4] + currentCharViewInfo.pointNgocboi;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMS[5] + currentCharViewInfo.pointPhu;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMS[6] + currentCharViewInfo.pointNon;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMS[7] + currentCharViewInfo.pointAo;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMS[8] + currentCharViewInfo.pointGangtay;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMS[9] + currentCharViewInfo.pointQuan;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                                var30 = mFont.tahoma_7_white;
                                var31 = mResources.gameMS[10] + currentCharViewInfo.pointGiay;
                                var18 += 12;
                                var30.gameAA(var1, var31, gameTD, var18, 0);
                            }

                            if (gameHL == 1 && indexRow >= 0) {
                                SmallImage.gameAA(var1, 942, gameTD - 8, gameTE + 2 + indexRow * 12, 0, StaticObj.TOP_LEFT);
                            }
                        }
                    }

                    this.gameAK(var1);
                    this.gameAL(var1);
                    this.gameBX(var1);
                } else if (gameBA()) {
                    if (gameKB) {
                        if (indexMenu == 0) {
                            this.gameAA(var1, mResources.gameHB, arrItemStore);
                        } else if (indexMenu == 1) {
                            this.gameAA(var1, mResources.gameHB, arrItemBook);
                        } else if (indexMenu == 2) {
                            this.gameAA(var1, mResources.gameHB, arrItemFashion);
                        } else if (indexMenu == 3) {
                            this.gameAA(var1, mResources.gameHB, arrItemClanShop);
                        } else if (indexMenu == 52) {
                            this.gameAB(var1, arrItemFashion);
                        }
                    }

                    if (gameKD) {
                        if (indexMenu == 0) {
                            this.gameAA(var1, mResources.gameGH, arrItemNonNam);
                        } else if (indexMenu == 1) {
                            this.gameAA(var1, mResources.gameGI);
                        }
                    }

                    if (gameKE) {
                        if (indexMenu == 0) {
                            this.gameAA(var1, mResources.gameGH, arrItemNonNu);
                        } else if (indexMenu == 1) {
                            this.gameAA(var1, mResources.gameGI);
                        }
                    }

                    if (gameKF) {
                        if (indexMenu == 0) {
                            this.gameAA(var1, mResources.gameGH, arrItemAoNam);
                        } else if (indexMenu == 1) {
                            this.gameAA(var1, mResources.gameGJ);
                        }
                    }

                    if (gameKG) {
                        if (indexMenu == 0) {
                            this.gameAA(var1, mResources.gameGH, arrItemAoNu);
                        } else if (indexMenu == 1) {
                            this.gameAA(var1, mResources.gameGJ);
                        }
                    }

                    if (gameKH) {
                        if (indexMenu == 0) {
                            this.gameAA(var1, mResources.gameGH, arrItemGangTayNam);
                        } else if (indexMenu == 1) {
                            this.gameAA(var1, mResources.gameGK);
                        }
                    }

                    if (gameKI) {
                        if (indexMenu == 0) {
                            this.gameAA(var1, mResources.gameGH, arrItemGangTayNu);
                        } else if (indexMenu == 1) {
                            this.gameAA(var1, mResources.gameGK);
                        }
                    }

                    if (gameKJ) {
                        if (indexMenu == 0) {
                            this.gameAA(var1, mResources.gameGH, arrItemQuanNam);
                        } else if (indexMenu == 1) {
                            this.gameAA(var1, mResources.gameGL);
                        }
                    }

                    if (gameKK) {
                        if (indexMenu == 0) {
                            this.gameAA(var1, mResources.gameGL, arrItemQuanNu);
                        } else if (indexMenu == 1) {
                            this.gameAA(var1, mResources.gameGL);
                        }
                    }

                    if (gameKL) {
                        if (indexMenu == 0) {
                            this.gameAA(var1, mResources.gameGH, arrItemGiayNam);
                        } else if (indexMenu == 1) {
                            this.gameAA(var1, mResources.gameGM);
                        }
                    }

                    if (gameKM) {
                        if (indexMenu == 0) {
                            this.gameAA(var1, mResources.gameGH, arrItemGiayNu);
                        } else if (indexMenu == 1) {
                            this.gameAA(var1, mResources.gameGM);
                        }
                    }

                    if (gameKN) {
                        if (indexMenu == 0) {
                            this.gameAA(var1, mResources.gameGN, arrItemLien);
                        } else if (indexMenu == 1) {
                            this.gameAA(var1, mResources.gameGN);
                        }
                    }

                    if (gameKO) {
                        if (indexMenu == 0) {
                            this.gameAA(var1, mResources.gameGO, arrItemNhan);
                        } else if (indexMenu == 1) {
                            this.gameAA(var1, mResources.gameGO);
                        }
                    }

                    if (gameKP) {
                        if (indexMenu == 0) {
                            this.gameAA(var1, mResources.gameGP, arrItemNgocBoi);
                        } else if (indexMenu == 1) {
                            this.gameAA(var1, mResources.gameGP);
                        }
                    }

                    if (gameKQ) {
                        if (indexMenu == 0) {
                            this.gameAA(var1, mResources.gameGQ, arrItemPhu);
                        } else if (indexMenu == 1) {
                            this.gameAA(var1, mResources.gameGQ);
                        }
                    }

                    if (gameKR) {
                        if (indexMenu == 0) {
                            this.gameAA(var1, mResources.gameGR, arrItemWeapon);
                        } else if (indexMenu == 1) {
                            this.gameAA(var1, mResources.gameGR);
                        }
                    }

                    if (gameKC) {
                        this.gameAA(var1, mResources.gameHC, arrItemElites);
                    }

                    this.gameAP(var1);
                    this.gameAQ(var1);
                    this.gameAR(var1);
                    this.gameAS(var1);
                    this.gameAT(var1);
                    this.gameAU(var1);
                    this.gameAV(var1);
                    this.gameBJ(var1);
                    this.gameAW(var1);
                    this.gameAX(var1);
                    this.gameAY(var1);
                    this.gameAZ(var1);
                    this.gameBB(var1);
                    this.gameBC(var1);
                    this.gameBD(var1);
                    this.gameBA(var1);
                    this.gameBR(var1);
                    this.gameBS(var1);
                    this.gameBT(var1);
                    this.gameBW(var1);
                    this.gameBU(var1);
                    this.gameBV(var1);
                    this.fieldBC(var1);
                    this.fieldBD(var1);
                    this.fieldThrow(var1);

                } else if (gameMD) {
                    this.gameBF(var1);
                }

                this.gameAD(var1);
                this.gameBL(var1);
                this.gameAF(var1);
                this.gameBI(var1);
                this.gameBH(var1);
                this.gameBK(var1);
                this.gameAE(var1);
                this.gameAM(var1);
                this.gameBM(var1);
                this.gameBO(var1);
                gameAB(var1);
                if (GameCanvas.isTouch && GameCanvas.w >= 320) {
                    if (super.left != null && super.left != this.gamePA) {
                        super.left.x = GameCanvas.w / 2 - 160;
                        super.left.y = GameCanvas.h - 26;
                    }

                    if (super.center != null) {
                        super.center.x = GameCanvas.w / 2 - 35;
                        super.center.y = GameCanvas.h - 26;
                    }

                    if (super.right != null && super.right != this.gameHE) {
                        super.right.x = GameCanvas.w / 2 + 88;
                        super.right.y = GameCanvas.h - 26;
                    }
                }
            }

            super.paint(var1);
            if (GameCanvas.isTouch && GameCanvas.gameAH) {
                this.gameAI(var1);
                gameKA = true;
            }

            gameAC(var1);
            gameAB(var1);
            this.gameBG(var1);
            gameAB(var1);
            this.gameBP(var1);
            gameAB(var1);
            Info.gameAA(var1);
            gameAB(var1);
            ChatTextField.gameAA().paint(var1);
            gameAB(var1);
            InfoMe.gameAA(var1);
        }
    }

    private void gameAD(mGraphics graphics) {
        if (isPaintAuto) {
            gameAB(graphics);
            Paint.gameAA(popupX, popupY, popupW, popupH, graphics);
            if (gameHL == 1) {
                graphics.gameAA(Paint.COLORDARK);
                graphics.gameAC(popupX + 7, popupY + 32, popupW - 14, popupH - 55);
                graphics.gameAA(16777215);
            } else {
                graphics.gameAA(10249521);
            }

            graphics.gameAB(popupX + 7, popupY + 32, popupW - 14, popupH - 55);
            gameAA(graphics, mResources.gameSR[7], false);
            gameTD = popupX + 17;
            gameTE = popupY + 45;
            gameHM = 36;
            scrMain.gameAA(gameHM, 30, popupX, popupY + 39, popupW, popupH - 63, true, 1);
            scrMain.gameAA(graphics);
            int int1 = gameTE;
            gameAA(graphics, mResources.gameVZ[0], Char.isAHP, Char.aHpValue + "%", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[1], Char.isAMP, Char.aMpValue + "%", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[2], Char.isAFood, String.valueOf(Char.aFoodValue), gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[3], Char.isABuff, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[4], Char.fieldEH, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[5], Char.fieldEI, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[6], Char.fieldEG, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[7], Char.isAPickYen, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[8], Char.isAPickYHM, "LV: " + Char.fieldFR, gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[9], Char.isAPickYHMS, "LV: " + Char.fieldFS, gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[10], Char.fieldEM, "LV: " + Char.fieldFT, gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[11], Char.fieldEN, "LV: " + Char.fieldFU, gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[12], Char.fieldEO, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[13], Char.fieldEP, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[14], Char.fieldEQ, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[15], Char.fieldER, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[16], Char.fieldES, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[17], Char.isANoPick, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[18], Char.fieldEU, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[19], Char.fieldEV, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[20], Char.fieldEW, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[21], Char.fieldEX, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[22], Char.fieldEY, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[23], Char.fieldEZ, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[24], Char.ReConnect, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[25], Char.fieldFB, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[26], Char.fieldFC, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[27], Char.fieldFD, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[28], Char.fieldFE, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[29], Char.fieldFF, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[30], Char.fieldFG, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[31], Char.fieldFH, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[32], Char.fieldFI, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[33], Char.fieldFJ, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[34], Char.isUseCL, "", gameTD, int1);
            int1 += 30;
            gameAA(graphics, mResources.gameVZ[35], Char.isBuyCL, "", gameTD, int1);
            if (gameHL == 1 && indexRow >= 0 && !GameCanvas.isTouch) {
                SmallImage.gameAA(graphics, 942, gameTD - 8, gameTE + 2 + indexRow * 30, 0, StaticObj.TOP_LEFT);
            }

            gameAB(graphics);
            mFont.tahoma_7_green.gameAA(graphics, GameCanvas.isTouch ? mResources.gameWB : mResources.gameWA, popupX + popupW / 2, popupY + popupH - 17, 2);
        }
    }

    private static void gameAA(mGraphics var0, String var1, boolean var2, String var3, int var4, int var5) {
        var0.gameAA(16777215);
        var0.gameAC(var4, var5, 12, 12);
        if (var2) {
            var0.gameAA(9650442);
            var0.gameAA(var4 + 2, var5 + 2, var4 + 2 + 7, var5 + 2 + 7);
            var0.gameAA(var4 + 2, var5 + 2 + 7, var4 + 2 + 7, var5 + 2);
        }

        mFont var6;
        (var6 = var2 ? mFont.tahoma_7_white : mFont.tahoma_7_grey).gameAA(var0, var1, var4 + 18, var5, 0);
        if (!var3.equals("")) {
            var0.gameAA(Paint.COLORLIGHT);
            var0.gameAC(var4 + 115, var5 - 3, 30, 20);
            var0.gameAA(var2 ? 16777215 : 0);
            var0.gameAB(var4 + 115, var5 - 3, 30, 20);
            var6.gameAA(var0, var3, var4 + 133, var5 + 2, 2);
        }
    }

    private void gameAE(mGraphics var1) {
        if (gameHY) {
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            gameAA(var1, mResources.gameEE, false);
            gameTD = popupX + 5;
            gameTE = popupY + 40;
            if (vList.size() == 0) {
                mFont.tahoma_7_white.gameAA(var1, mResources.gameSO, popupX + popupW / 2, popupY + 40, 2);
                return;
            }

            var1.gameAA(-16770791);
            var1.gameAC(gameTD - 2, gameTE - 2, popupW - 6, gameHK * 5 + 8);
            gameAB(var1);
            scrMain.gameAA(vList.size(), gameHK, gameTD, gameTE, popupW - 3, gameHK * 5 + 4, true, 1);
            scrMain.gameAA(var1, gameTD, gameTE, popupW - 3, gameHK * 5 + 6);
            gameHM = vList.size();

            for (int var4 = 0; var4 < vList.size(); ++var4) {
                DunItem var2 = null;

                try {
                    var2 = (DunItem) vList.elementAt(var4);
                } catch (Exception var3) {
                }

                if (var2 != null) {
                    if (indexRow == var4) {
                        var1.gameAA(Paint.COLORLIGHT);
                        var1.gameAC(gameTD + 2, gameTE + indexRow * gameHK + 2, popupW - 15, gameHK - 4);
                        var1.gameAA(16777215);
                        var1.gameAB(gameTD + 2, gameTE + indexRow * gameHK + 2, popupW - 15, gameHK - 4);
                    } else {
                        var1.gameAA(Paint.COLORBACKGROUND);
                        var1.gameAC(gameTD + 2, gameTE + var4 * gameHK + 2, popupW - 15, gameHK - 4);
                        var1.gameAA(13932896);
                        var1.gameAB(gameTD + 2, gameTE + var4 * gameHK + 2, popupW - 15, gameHK - 4);
                    }

                    mFont.tahoma_7_yellow.gameAA(var1, var2.name1, gameTD + (popupW - 10) / 2 - popupW / 4, gameTE + var4 * gameHK + gameHK / 2 - 6, 2);
                    mFont.tahoma_7b_red.gameAA(var1, " vs ", gameTD + (popupW - 10) / 2, gameTE + var4 * gameHK + gameHK / 2 - 6, 2);
                    mFont.tahoma_7_yellow.gameAA(var1, var2.name2, gameTD + (popupW - 10) / 2 + popupW / 4, gameTE + var4 * gameHK + gameHK / 2 - 6, 2);
                }
            }

            gameAN(var1);
        }

    }

    private void gameAF(mGraphics var1) {
        if (gameMC) {
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            gameAA(var1, mResources.gameEE, false);
            gameTD = popupX + 5;
            gameTE = popupY + 40;
            if (vCharInMap.size() == 0) {
                mFont.tahoma_7_white.gameAA(var1, mResources.gameQG, popupX + popupW / 2, popupY + 40, 2);
                return;
            }

            var1.gameAA(-16770791);
            var1.gameAC(gameTD - 2, gameTE - 2, popupW - 6, gameHK * 5 + 8);
            gameAB(var1);
            scrMain.gameAA(vCharInMap.size(), gameHK, gameTD, gameTE, popupW - 3, gameHK * 5 + 4, true, 1);
            scrMain.gameAA(var1, gameTD, gameTE, popupW - 3, gameHK * 5 + 6);
            gameHM = vCharInMap.size();

            for (int var2 = 0; var2 < vCharInMap.size(); ++var2) {
                Char var3 = null;

                try {
                    if ((var3 = (Char) vCharInMap.elementAt(var2)).gameBB()) {
                        continue;
                    }
                } catch (Exception var4) {
                }

                if (var3 != null) {
                    if (indexRow == var2) {
                        var1.gameAA(Paint.COLORLIGHT);
                        var1.gameAC(gameTD + 2, gameTE + indexRow * gameHK + 2, popupW - 15, gameHK - 4);
                        var1.gameAA(16777215);
                        var1.gameAB(gameTD + 2, gameTE + indexRow * gameHK + 2, popupW - 15, gameHK - 4);
                    } else {
                        var1.gameAA(Paint.COLORBACKGROUND);
                        var1.gameAC(gameTD + 2, gameTE + var2 * gameHK + 2, popupW - 15, gameHK - 4);
                        var1.gameAA(13932896);
                        var1.gameAB(gameTD + 2, gameTE + var2 * gameHK + 2, popupW - 15, gameHK - 4);
                    }

                    SmallImage.gameAA(var1, 647, gameTD + 12, gameTE + var2 * gameHK + gameHK / 2, 0, 3);
                    if (this.cLastFocusID > 0 && this.cLastFocusID == var3.charID) {
                        mFont.tahoma_7_yellow.gameAA(var1, var3.cName + "cc - " + mResources.gameEV + ": " + var3.clevel, gameTD + 22, gameTE + var2 * gameHK + gameHK / 2 - 6, 0);
                    } else if (var3.statusMe == 14) {
                        mFont.tahoma_7_grey.gameAA(var1, var3.cName + "cc - " + mResources.gameEV + ": " + var3.clevel, gameTD + 22, gameTE + var2 * gameHK + gameHK / 2 - 6, 0);
                    } else {
                        mFont.tahoma_7_green.gameAA(var1, var3.cName + "cc - " + mResources.gameEV + ": " + var3.clevel, gameTD + 22, gameTE + var2 * gameHK + gameHK / 2 - 6, 0);
                    }
                }
            }

            gameAN(var1);
        }

    }

    private void gameAG(mGraphics var1) {
        try {
            int var2 = (int) (System.currentTimeMillis() / 1000L);
            int var4 = 5;
            if (GameCanvas.isTouch && GameCanvas.gameAH) {
                var4 = 45 + Info.hI;
            }

            if (GameCanvas.gameAI && Char.getMyChar().vSkillFight.size() > 4) {
                var4 += 25;
            }

            gameAB(var1);
            int var8;
            if (!GameCanvas.gameAJ) {
                boolean var3 = false;

                for (int var5 = 0; var5 < Char.getMyChar().vEff.size(); ++var5) {
                    Effect var7 = (Effect) Char.getMyChar().vEff.elementAt(var5);
                    SmallImage.gameAA(var1, var7.template.iconId, GameCanvas.w - 13 - (var5 * 13 << 1), var4 + 14, 0, 33);
                    if ((var8 = var7.timeLenght - (var2 - var7.timeStart)) >= 0) {
                        mFont.tahoma_7_white.gameAA(var1, NinjaUtil.gameAB(var8), GameCanvas.w - 13 - (var5 * 13 << 1), var4 + 15, 2, mFont.tahoma_7_grey);
                    }
                }

                var8 = this.timeLengthMap - (var2 - this.timeStartMap);
                if (Char.getMyChar().vEff.size() > 0) {
                    var4 += 27;
                }

                if (var8 > 0) {
                    mFont.tahoma_7_white.gameAA(var1, mResources.gameEF + ": " + NinjaUtil.gameAB(var8), GameCanvas.w - 2, var4, 1, mFont.tahoma_7_grey);
                    var4 += 12;
                }

                if (TileMap.typeMap == 1) {
                    mFont.tahoma_7_white.gameAA(var1, mResources.gameEG + ": " + Char.getMyChar().countKill, GameCanvas.w - 2, var4, 1, mFont.tahoma_7_grey);
                    var4 += 12;
                    mFont.tahoma_7_white.gameAA(var1, mResources.gameEH + ": " + Char.getMyChar().countKillMax, GameCanvas.w - 2, var4, 1, mFont.tahoma_7_grey);
                    var4 += 12;
                } else if (TileMap.typeMap != 2 && TileMap.mapID != 114 && TileMap.mapID != 115 && TileMap.mapID != 116) {
                    if (TileMap.typeMap == 3) {
                        mFont.tahoma_7_white.gameAA(var1, mResources.gameEI + ": " + Char.pointChienTruong, GameCanvas.w - 2, var4, 1, mFont.tahoma_7_grey);
                        var4 += 12;
                        mFont.tahoma_7_white.gameAA(var1, mResources.gameMR[Char.gameAZ()], GameCanvas.w - 2, var4, 1, mFont.tahoma_7_grey);
                        var4 += 12;
                    }
                } else {
                    mFont.tahoma_7_white.gameAA(var1, mResources.gameEI + ": " + Char.pointPB, GameCanvas.w - 2, var4, 1, mFont.tahoma_7_grey);
                    var4 += 12;
                }
            }

            if (vParty.size() > 0 && GameCanvas.w > 128 && !gameBA()) {
                var4 -= 18;

                for (var8 = 0; var8 < vParty.size(); ++var8) {
                    Party var9;
                    if ((var9 = (Party) vParty.elementAt(var8)).c != null) {
                        mFont var10000 = mFont.tahoma_7_white;
                        String var10002 = var9.name + "(" + var9.c.clevel + ")";
                        int var10003 = GameCanvas.w - 14;
                        var4 += 18;
                        var10000.gameAA(var1, var10002, var10003, var4, 1, mFont.tahoma_7_grey);
                        var9.c.gameAA(var1, GameCanvas.w - 41, var4 + 12);
                        SmallImage.gameAA(var1, var9.iconId, GameCanvas.w - 7, var4 + 9, 0, 3);
                    } else {
                        var4 += 16;
                        mFont.tahoma_7_green.gameAA(var1, var9.name, GameCanvas.w - 14, var4 + 5, 1, mFont.tahoma_7_grey);
                        SmallImage.gameAA(var1, var9.iconId, GameCanvas.w - 7, var4 + 11, 0, 3);
                    }
                }

                return;
            }
        } catch (Exception var6) {
        }

    }

    public static void gameAB(mGraphics var0) {
        var0.gameAA(-var0.gameAA(), -var0.gameAB());
        var0.gameAD(0, -200, GameCanvas.w, 200 + GameCanvas.h);
    }

    public static void ShowMob(final mGraphics var1, final int x, final int y) {
        int normalMobs = 0;
        int eliteMobs = 0;
        int bossMobs = 0;
        for (int i = 0; i < vMob.size(); i++) {
            Mob mob = (Mob) vMob.elementAt(i);
            if (mob.levelBoss == 0) {
                normalMobs++;
            } else if (mob.levelBoss == 1) {
                eliteMobs++;
            } else if (mob.levelBoss == 2) {
                bossMobs++;
            }
        }

        String displayText = "Số quái,TA,TL: " + normalMobs
                + " - " + eliteMobs
                + " - " + bossMobs;
        mFont.tahoma_7_yellow.gameAA(var1, displayText, x, y + 66, 0, mFont.tahoma_7);
    }

    private void gameAH(mGraphics var1) {
        try {
            if (!GameCanvas.menu.showMenu) {
                if (!InfoDlg.isShow) {
                    if (!gameDG()) {
                        int var3 = -7;
                        int var2 = 3;
                        if ((!GameCanvas.isTouch || GameCanvas.isTouch && !GameCanvas.gameAH) && gameKA) {
                            var2 += 30;
                        }

                        if (GameCanvas.isTouch) {
                            var3 = -7 + 45 + Info.hI;
                            if (GameCanvas.gameAI) {
                                var3 += 35;
                            }
                        }

                        var1.gameAA(-var1.gameAA(), -var1.gameAB());
                        int var9;
                        mFont var10000;
                        String var10002;
                        if (GameCanvas.gameAJ) {
                            int var4 = (int) (System.currentTimeMillis() / 1000L);

                            for (int var6 = 0; var6 < Char.getMyChar().vEff.size(); ++var6) {
                                Effect var5 = (Effect) Char.getMyChar().vEff.elementAt(var6);
                                SmallImage.gameAA(var1, var5.template.iconId, var2 + 13 + (var6 * 13 << 1), var3 + 27, 0, 33);
                                if ((var9 = var5.timeLenght - (var4 - var5.timeStart)) >= 0) {
                                    mFont.tahoma_7_white.gameAA(var1, NinjaUtil.gameAB(var9), var2 + 13 + (var6 * 13 << 1), var3 + 28, 2, mFont.tahoma_7_grey);
                                }
                            }

                            var9 = this.timeLengthMap - (var4 - this.timeStartMap);
                            if (Char.getMyChar().vEff.size() > 0) {
                                var3 += 27;
                            }

                            if (var9 > 0) {
                                var10000 = mFont.tahoma_7_white;
                                var10002 = mResources.gameEF + ": " + NinjaUtil.gameAB(var9);
                                var3 += 12;
                                var10000.gameAA(var1, var10002, var2, var3, 0, mFont.tahoma_7_grey);
                            }
                        }

                        String var8;
                        if (Char.getMyChar().clevel <= 20) {
                            if (Char.getMyChar().pPoint > 0) {
                                var8 = "+" + Char.getMyChar().pPoint + " " + mResources.gameEA;
                                var3 += 12;
                                mFont.tahoma_7_yellow.gameAA(var1, var8, var2, var3, 0, mFont.tahoma_7_grey);
                            }

                            if (Char.getMyChar().sPoint > 0) {
                                var8 = "+" + Char.getMyChar().sPoint + " " + mResources.gameEB;
                                var3 += 12;
                                mFont.tahoma_7_yellow.gameAA(var1, var8, var2, var3, 0, mFont.tahoma_7_grey);
                            }
                        }
                        if (Code.fieldAB != null) {
                            var3 += 12;
                            (GameCanvas.gameTick % 10 > 7 ? mFont.tahoma_7_red : mFont.tahoma_7_yellow).gameAA(var1, Code.fieldAB.toString(), var2, var3, 0, mFont.tahoma_7_grey);
                        }
                        ShowMob(var1, var2, var3);
                        int playersType1 = 0;
                        int playersType2 = 0;
                        int playersType3 = 0;
                        int players = vCharInMap.size();

                        for (int i = 0; i < vCharInMap.size(); i++) {
                            Char player = (Char) vCharInMap.elementAt(i);
                            if (player.cTypePk == 1) {
                                playersType1++;
                            } else if (player.cTypePk == 2) {
                                playersType2++;
                            } else if (player.cTypePk == 3) {
                                playersType3++;
                            }
                        }

                        String player = "Số player pk,player: " + (playersType1 + playersType2 + playersType3 + " - " + players);
                        mFont.tahoma_7_yellow.gameAA(var1, player, var2, var3 + 78, 0, mFont.tahoma_7);
                        if (Code.fieldAR) {
                            var3 += 12;
                            mFont.tahoma_7_yellow.gameAA(var1, "Vị trí: " + (Code.fieldAS + 1) + "/" + Code.fieldAT.size(), var2, var3, 0, mFont.tahoma_7_grey);
                        }

//                        int index = 1;
//                        for (Enumeration e = GameScr.vCharInMap.elements(); e.hasMoreElements();) {
//                            Char character = (Char) e.nextElement();
//                            String charName = character.cName;
//                            int currentHP = character.cHP;
//                            int maxHP = character.cMaxHP;
//                            var3 += 12;
//                            mFont.tahoma_7_yellow.gameAA(var1, index + ". " + charName + " - HP: " + currentHP + "/" + maxHP, var2, var3, 0, mFont.tahoma_7_grey);
//                            index++;
//                        }
                        if (ChatManager.gameAD().waitList.size() > 0) {
                            this.gameNU[0] = var2;
                            this.gameNV[0] = var3 + 12;
                            var8 = "+" + ChatManager.gameAD().waitList.size() + " " + mResources.gameDV;
                            if (GameCanvas.gameTick % 10 > 4) {
                                var3 += 12;
                                mFont.tahoma_7_red.gameAA(var1, var8, var2, var3, 0, mFont.tahoma_7_grey);
                            } else {
                                var3 += 12;
                                mFont.tahoma_7_yellow.gameAA(var1, var8, var2, var3, 0, mFont.tahoma_7_grey);
                            }
                        }

                        if (ChatManager.isMessageClan || ChatManager.isMessagePt) {
                            var8 = "";
                            this.gameNU[1] = var2;
                            this.gameNV[1] = var3 + 12;
                            if (ChatManager.isMessageClan && ChatManager.isMessagePt) {
                                var8 = mResources.gameDW[0];
                            } else if (ChatManager.isMessageClan) {
                                var8 = mResources.gameDW[1];
                            } else if (ChatManager.isMessagePt) {
                                var8 = mResources.gameDW[2];
                            }

                            if (GameCanvas.gameTick % 10 > 7) {
                                var3 += 12;
                                mFont.tahoma_7_red.gameAA(var1, var8, var2, var3, 0, mFont.tahoma_7_grey);
                            } else {
                                var3 += 12;
                                mFont.tahoma_7_yellow.gameAA(var1, var8, var2, var3, 0, mFont.tahoma_7_grey);
                            }
                        }

                        if (TileMap.typeMap != 3) {
                            if (Char.getMyChar().gameBA()) {
                                if (Char.getMyChar().taskMaint != null) {
                                    var8 = Char.getMyChar().taskMaint.subNames[Char.getMyChar().taskMaint.index];

                                    for (var9 = 0; var8 == null; var8 = Char.getMyChar().taskMaint.subNames[Char.getMyChar().taskMaint.index - var9]) {
                                        ++var9;
                                    }

                                    if (Char.getMyChar().taskMaint.counts[Char.getMyChar().taskMaint.index] != -1) {
                                        var8 = var8 + " " + Char.getMyChar().taskMaint.count + "/" + Char.getMyChar().taskMaint.counts[Char.getMyChar().taskMaint.index];
                                    }

                                    if (GameCanvas.taskTick > 0 && GameCanvas.gameTick % 10 > 4) {
                                        var3 += 12;
                                        mFont.tahoma_7_yellow.gameAA(var1, var8, var2, var3, 0, mFont.tahoma_7_grey);
                                    } else {
                                        var3 += 12;
                                        mFont.tahoma_7_white.gameAA(var1, var8, var2, var3, 0, mFont.tahoma_7_grey);
                                    }
                                } else {
                                    byte var11;
                                    if ((var11 = gameBE()) >= 0) {
                                        var10000 = mFont.tahoma_7_white;
                                        var10002 = mResources.gameKY + " " + TileMap.mapNames[var11];
                                        var3 += 12;
                                        var10000.gameAA(var1, var10002, var2, var3, 0, mFont.tahoma_7_grey);
                                    }
                                }
                            }
                        } else if (Char.getMyChar().charFocus != null) {
                            if (Char.getMyChar().charFocus.cTypePk == 4) {
                                var3 += 12;
                                mFont.tahoma_7_white.gameAA(var1, mResources.gameKZ, var2, var3, 0, mFont.tahoma_7_grey);
                            } else if (Char.getMyChar().charFocus.cTypePk == 5) {
                                var3 += 12;
                                mFont.tahoma_7_white.gameAA(var1, mResources.gameMA, var2, var3, 0, mFont.tahoma_7_grey);
                            } else if (Char.getMyChar().charFocus.cTypePk == 6) {
                                var3 += 12;
                                mFont.tahoma_7_white.gameAA(var1, mResources.gameMB, var2, var3, 0, mFont.tahoma_7_grey);
                            }
                        } else if (Char.getMyChar().mobFocus != null) {
                            if (Char.getMyChar().mobFocus.templateId == 96) {
                                var3 += 12;
                                mFont.tahoma_7_white.gameAA(var1, mResources.gameMA, var2, var3, 0, mFont.tahoma_7_grey);
                            } else if (Char.getMyChar().mobFocus.templateId == 97) {
                                var3 += 12;
                                mFont.tahoma_7_white.gameAA(var1, mResources.gameKZ, var2, var3, 0, mFont.tahoma_7_grey);
                            } else if (Char.getMyChar().mobFocus.templateId == 93) {
                                var3 += 12;
                                mFont.tahoma_7_white.gameAA(var1, mResources.gameMB, var2, var3, 0, mFont.tahoma_7_grey);
                            } else {
                                var3 += 12;
                                mFont.tahoma_7_white.gameAA(var1, mResources.gameMC, var2, var3, 0, mFont.tahoma_7_grey);
                            }
                        }

                        if (Char.getMyChar().mobFocus != null) {
                            MobTemplate var12 = Char.getMyChar().mobFocus.getTemplate();
                            String var10 = var12.name + " lv" + Char.getMyChar().mobFocus.level;
                            if (Char.getMyChar().mobFocus.templateId != 0 && Char.getMyChar().mobFocus.templateId != 142 && Char.getMyChar().mobFocus.templateId != 143) {
                                var10 = var10 + ": " + Char.getMyChar().mobFocus.hp + "/" + Char.getMyChar().mobFocus.maxHp;
                            }

                            var1.gameAA(Char.getMyChar().mobFocus.gameAE());
                            var3 += 12;
                            var1.gameAC(var2, var3 + 3, 5, 5);
                            var1.gameAA(0);
                            var1.gameAB(var2, var3 + 3, 5, 5);
                            mFont.tahoma_7_white.gameAA(var1, var10, var2 + 12, var3, 0, mFont.tahoma_7_grey);
                        } else if (Char.getMyChar().npcFocus != null) {
                            var3 += 12;
                            mFont.tahoma_7_yellow.gameAA(var1, Char.getMyChar().npcFocus.template.name, var2, var3, 0, mFont.tahoma_7_grey);
                        } else if (Char.getMyChar().charFocus != null) {
                            var1.gameAA(Char.getMyChar().charFocus.gameAT());
                            var3 += 12;
                            var1.gameAC(var2, var3 + 3, 5, 5);
                            var1.gameAA(0);
                            var1.gameAB(var2, var3 + 3, 5, 5);
                            mFont.tahoma_7_white.gameAA(var1, Char.getMyChar().charFocus.cName + " lv" + Char.getMyChar().charFocus.clevel + ": " + Char.getMyChar().charFocus.cHP + "/" + Char.getMyChar().charFocus.cMaxHP, var2 + 12, var3, 0, mFont.tahoma_7_grey);
                        }

                        if (GameCanvas.gameAJ) {
                            if (TileMap.typeMap == 1) {
                                var10000 = mFont.tahoma_7_white;
                                var10002 = mResources.gameEG + ": " + Char.getMyChar().countKill;
                                var3 += 12;
                                var10000.gameAA(var1, var10002, var2, var3, 0, mFont.tahoma_7_grey);
                                var10000 = mFont.tahoma_7_white;
                                var10002 = mResources.gameEH + ": " + Char.getMyChar().countKillMax;
                                var3 += 12;
                                var10000.gameAA(var1, var10002, var2, var3, 0, mFont.tahoma_7_grey);
                            } else if (TileMap.typeMap != 2 && TileMap.mapID != 114 && TileMap.mapID != 115 && TileMap.mapID != 116) {
                                if (TileMap.typeMap == 3) {
                                    var10000 = mFont.tahoma_7_white;
                                    var10002 = mResources.gameEI + ": " + Char.pointChienTruong;
                                    var3 += 12;
                                    var10000.gameAA(var1, var10002, var2, var3, 0, mFont.tahoma_7_grey);
                                    var10000 = mFont.tahoma_7_white;
                                    var10002 = mResources.gameMR[Char.gameAZ()];
                                    var3 += 12;
                                    var10000.gameAA(var1, var10002, var2, var3, 0, mFont.tahoma_7_grey);
                                }
                            } else {
                                var10000 = mFont.tahoma_7_white;
                                var10002 = mResources.gameEI + ": " + Char.pointPB;
                                var3 += 12;
                                var10000.gameAA(var1, var10002, var2, var3, 0, mFont.tahoma_7_grey);
                            }
                        }

                        var1.gameAA(-var1.gameAA(), -var1.gameAB());
                    }
                }
            }
        } catch (Exception var7) {
        }
    }

    private void gameAI(mGraphics var1) {
        if (GameCanvas.isTouch && (!GameCanvas.menu.showMenu || !GameCanvas.gameAI)) {
            if (GameCanvas.currentDialog == null && ChatPopup.currentMultilineChatPopup == null && !GameCanvas.menu.showMenu && !gameDG()) {
                gameAB(var1);
                if (!ChatTextField.gameAA().isShow) {
                    var1.gameAA(gameNI, gamePQ + 17, gamePR + 17, 3);
                }

                if (!this.gameDH()) {
                    if (!fieldGE) {
                        InfoDlg var2 = fieldTE;
                        var1.gameAA((Image) null, var2.fieldAA, var2.fieldAB, 3);
                        var1.gameAA((Image) null, var2.fieldAC, var2.fieldAD, 3);
                    } else {
                        var1.gameAA(gameNL, gamePO, gamePP, 0);
                        var1.gameAA(gameNG, 0, 0, mGraphics.gameAA(gameNG), mGraphics.gameAB(gameNG), 3, gamePO + 15, gamePP + 16, 3);
                        if (mScreen.keyTouch == 4) {
                            var1.gameAA(gameNM, gamePO, gamePP, 0);
                            var1.gameAA(gameNH, 0, 0, mGraphics.gameAA(gameNG), mGraphics.gameAB(gameNG), 3, gamePO + 15, gamePP + 16, 3);
                        }

                        var1.gameAA(gameNL, gamePS, gamePT, 0);
                        var1.gameAA(gameNG, 0, 0, mGraphics.gameAA(gameNG), mGraphics.gameAB(gameNG), 0, gamePS + 17, gamePT + 16, 3);
                        if (mScreen.keyTouch == 6) {
                            var1.gameAA(gameNM, gamePS, gamePT, 0);
                            var1.gameAA(gameNH, 0, 0, mGraphics.gameAA(gameNG), mGraphics.gameAB(gameNG), 0, gamePS + 17, gamePT + 16, 3);
                        }

                        var1.gameAA(gameNL, gamePW, gamePX, 0);
                        var1.gameAA(gameNG, 0, 0, mGraphics.gameAA(gameNG), mGraphics.gameAB(gameNG), 7, gamePW + 17, gamePX + 14, 3);
                        if (mScreen.keyTouch == 3) {
                            var1.gameAA(gameNM, gamePW, gamePX, 0);
                            var1.gameAA(gameNH, 0, 0, mGraphics.gameAA(gameNG), mGraphics.gameAB(gameNG), 7, gamePW + 17, gamePX + 14, 3);
                        }
                    }
                    if (Char.getMyChar().ctaskId > 1) {
                        var1.gameAA(gameNL, gamePY, gamePZ, 0);
                        if (mScreen.keyTouch == 10) {
                            var1.gameAA(gameNM, gamePY, gamePZ, 0);
                        }

                        var1.gameAA(gameNN, gamePY + 16, gamePZ + 15, 3);
                        mFont.number_white.gameAA(var1, "" + hpPotion, gamePY + 22, gamePZ + 20, 1);
                        var1.gameAA(gameNL, gameQA, gameQB, 0);
                        if (mScreen.keyTouch == 11) {
                            var1.gameAA(gameNM, gameQA, gameQB, 0);
                        }

                        var1.gameAA(gameNO, gameQA + 16, gameQB + 15, 3);
                        mFont.number_white.gameAA(var1, "" + mpPotion, gameQA + 22, gameQB + 20, 1);
                        var1.gameAA(gameNL, gameQC, gameQD, 0);
                        if (mScreen.keyTouch == 13) {
                            var1.gameAA(gameNM, gameQC, gameQD, 0);
                        }

                        var1.gameAA(gameNK, gameQC + 16, gameQD + 16, 3);
                    }

                    var1.gameAA(gameNP, gamePU, gamePV, 0);
                    if (mScreen.keyTouch == 5) {
                        var1.gameAA(gameNQ, gamePU, gamePV, 0);
                    }

                }
            }
        }
    }

    private void gameAJ(mGraphics var1) {
        if (GameCanvas.currentDialog == null && ChatPopup.currentMultilineChatPopup == null && !GameCanvas.menu.showMenu && !gameDG() && super.center != this.cmdDead) {
            if (!GameCanvas.isTouch || Char.getMyChar().vSkill.size() >= 2) {
                if (gameKA) {
                    for (int var2 = 0; var2 < gameNZ.length; ++var2) {
                        if (GameCanvas.gameAI) {
                            if (Info.hI > 0) {
                                gameQF[var2] = 55 + Info.hI;
                            } else {
                                gameQF[var2] = 55;
                            }
                        }

                        if (GameCanvas.isTouch && GameCanvas.gameAH) {
                            var1.gameAA(gameNR, gameQG + gameQE[var2] - 1, gameQF[var2] - 1, 0);
                        } else {
                            var1.gameAA(16764040);
                            var1.gameAB(gameQG + gameQE[var2] - 1, gameQF[var2] - 1, 25, 25);
                        }

                        Skill var3 = gameNZ[var2];
                        if (var2 == this.gamePJ && !gameBA() && GameCanvas.gameTick % 10 > 5) {
                            var1.gameAA(16777215);
                            var1.gameAC(gameQG + gameQE[var2] + 1, gameQF[var2] + 1, 22, 22);
                        } else if (!GameCanvas.isTouch) {
                            var1.gameAA(0);
                            var1.gameAC(gameQG + gameQE[var2], gameQF[var2], 24, 24);
                        }

                        if (var3 != null) {
                            if (var3 == Char.getMyChar().myskill) {
                                var1.gameAA(16711680);
                                var1.gameAB(gameQG + gameQE[var2] - 1, gameQF[var2] - 1, 25, 25);
                            }

                            var3.gameAA(gameQG + gameQE[var2] + 12, gameQF[var2] + 12, var1);
                        }
                    }
                }

            }
        }
    }

    public static final void gameAA(String var0, int var1, int var2, int var3, int var4, int var5) {
        var3 = -1;

        for (var4 = 0; var4 < 5; ++var4) {
            if (gameQO[var4] == -1) {
                var3 = var4;
                break;
            }
        }

        if (var3 != -1) {
            gameQP[var3] = var5;
            gameQJ[var3] = var0;
            gameQK[var3] = var1;
            gameQL[var3] = var2;
            gameQM[var3] = 0;
            gameQN[var3] = -2;
            gameQO[var3] = 0;
        }
    }

    public static final void gameAA(int var0, int var1) {
        gameHJ.addElement(new Lanterns(var0, var1));
    }

    public static final boolean gameAA(int var0, int var1, int var2) {
        int var3 = gameQS[0] == -1 ? 0 : 1;
        if (gameQS[var3] != -1) {
            return false;
        } else {
            gameQS[var3] = 0;
            gameQU[var3] = var2;
            gameQQ[var3] = var0;
            gameQR[var3] = var1;
            return true;
        }
    }

    private void gameCT() {
        if (gameRI == null) {
            gameRI = new Image[2];

            for (int var1 = 0; var1 < 2; ++var1) {
                gameRI[var1] = GameCanvas.gameAC("/u/c" + var1 + ".png");
            }
        }

        gameQZ = mGraphics.gameAA(gameRI[0]);
        gameRA = mGraphics.gameAA(gameRI[1]);
        gameRB = gW - gameQZ - gameRA + 1;
        gameRC = 63;
        gameRD = gameQX + 7;
        gameRE = gW - 84 - 30 + 15;
        gameRG = gW - 44 - 4;
        gameRH = 5;
        if (GameCanvas.w > 176) {
            gameRB -= 50;
            gameRE -= 50;
            gameRG -= 50;
            gameRC += 15;
            gameRE -= 15;
        }

        this.gameCU();
    }

    private void gameCU() {
        if (GameCanvas.isTouch) {
            gameRE = 82;
            gameRF = 57;
            gameRC = 52;
            gameRD = GameCanvas.isKiemduyet ? 25 : 10 + Info.hI;
            gameRG = gW - 61;
            if (GameCanvas.gameAI) {
                gamePQ = gW / 2 - 2;
                gamePR = gamePN + 50;
            } else {
                this.gamePA.y = 6 + Info.hI;
                gamePQ = gW - 100;
                gamePR = 2 + Info.hI;
            }

            TileMap.gameAA(GameCanvas.w - 60, GameCanvas.isKiemduyet ? 16 : Info.hI, 60, 42);
        }
    }

    private void gameAK(mGraphics var1) {
        if (indexMenu == 4) {
            var1.gameAA(-var1.gameAA(), -var1.gameAB());
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            var1.gameAA(Paint.COLORBACKGROUND);
            gameAA(var1, mResources.gameGG[indexMenu], true);
            if (currentCharViewInfo.arrItemBody == null) {
                GameCanvas.gameAA(popupX + 90, popupY + 75, var1, false);
                mFont.tahoma_7b_white.gameAA(var1, mResources.PLEASEWAIT, popupX + popupW / 2, popupY + 90, 2);
            } else {
                var1.gameAA(13606712);
                var1.gameAB(popupX + 33, popupY + (GameCanvas.gameAJ ? 87 : 34), popupW - 67, GameCanvas.gameAJ ? 76 : 128);
                int var2 = gameHK - 2;
                int var3 = 0;

                int var4;
                int var5;
                int var6;
                for (var4 = 0; var4 < 16; ++var4) {
                    if (var4 != 0 && var4 != 2 && var4 != 4 && var4 != 6 && var4 != 8) {
                        if (var4 != 1 && var4 != 3 && var4 != 5 && var4 != 7 && var4 != 9) {
                            if (var4 == 9 || var4 == 10 || var4 == 11 || var4 == 12 || var4 == 13 || var4 == 14 || var4 == 15) {
                                var5 = popupX + 4 + 1 + var3 * (var2 + 2);
                                var6 = popupY + 35 + var2 * 5 + 1;
                                var1.gameAA(0);
                                var1.gameAC(var5, popupY + 35 + var2 * 5 + 1, var2 - 1, var2 - 1);
                                if (gameRJ == 0) {
                                    if (mResources.gamePJ[var4].length > 1) {
                                        mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[var4][0], var5 + var2 / 2, var6 + 2, 2);
                                        mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[var4][1], var5 + var2 / 2, var6 + 2 + 9, 2);
                                    } else {
                                        mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[var4][0], var5 + var2 / 2, var6 + 2 + 5, 2);
                                    }
                                }

                                ++var3;
                            }
                        } else {
                            var1.gameAA(0);
                            var1.gameAC(popupX + popupW - var2 - 4, popupY + 35 + var4 / 2 * var2 + 1, var2 - 1, var2 - 1);
                            if (gameRJ == 0) {
                                if (mResources.gamePJ[var4].length > 1) {
                                    mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[var4][0], popupX + popupW - var2 / 2 - 4, popupY + 36 + var4 / 2 * var2 + 2, 2);
                                    mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[var4][1], popupX + popupW - var2 / 2 - 4, popupY + 36 + var4 / 2 * var2 + 2 + 9, 2);
                                } else {
                                    mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[var4][0], popupX + popupW - var2 / 2 - 4, popupY + 36 + var4 / 2 * var2 + 2 + 5, 2);
                                }
                            }
                        }
                    } else {
                        var1.gameAA(0);
                        var1.gameAC(popupX + 4 + 1, popupY + 35 + var4 / 2 * var2 + 1, var2 - 1, var2 - 1);
                        if (gameRJ == 0) {
                            if (mResources.gamePJ[var4].length > 1) {
                                mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[var4][0], popupX + 7 + 11, popupY + 36 + var4 / 2 * var2 + 2, 2);
                                mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[var4][1], popupX + 7 + 11, popupY + 36 + var4 / 2 * var2 + 2 + 9, 2);
                            } else {
                                mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[var4][0], popupX + 7 + 11, popupY + 36 + var4 / 2 * var2 + 2 + 5, 2);
                            }
                        }
                    }
                }

                int var7;
                Item var10;
                if (gameRJ == 0) {
                    for (var4 = 0; var4 < 16; ++var4) {
                        if ((var10 = currentCharViewInfo.arrItemBody[var4]) != null) {
                            if (var10.eff == null) {
                                var10.eff = efs[56];
                            }

                            if (var10.indexUI != 0 && var10.indexUI != 2 && var10.indexUI != 4 && var10.indexUI != 6 && var10.indexUI != 8 && var10.indexUI != 10) {
                                if (var10.indexUI != 1 && var10.indexUI != 3 && var10.indexUI != 5 && var10.indexUI != 7 && var10.indexUI != 9) {
                                    if (var10.indexUI == 11 || var10.indexUI == 12 || var10.indexUI == 13 || var10.indexUI == 14 || var10.indexUI == 15) {
                                        if (var10.indexUI == 10) {
                                            var3 = 1;
                                        } else if (var10.indexUI == 11) {
                                            var3 = 2;
                                        } else if (var10.indexUI == 12) {
                                            var3 = 3;
                                        } else if (var10.indexUI == 13) {
                                            var3 = 4;
                                        } else if (var10.indexUI == 14) {
                                            var3 = 5;
                                        } else if (var10.indexUI == 15) {
                                            var3 = 6;
                                        }

                                        var6 = popupX + 2 + 1 + var3 * (var2 + 2) - var2;
                                        var7 = popupY + 35 + var2 * 5;
                                        this.gameAA(var1, var10, var6 - 2, var7 - 1, 0, 1);
//                                        if (var10.upgrade >= 0) {
//                                            String upgradeText = "+" + var10.upgrade + ",TL" + var10.checkne();
//                                            mFont.tahoma_7b_white.gameAA(var1, upgradeText, var6 + var2 / 2, var7 + 27, 2);
//                                        }
                                    }
                                } else {
                                    var6 = popupX + popupW - var2 - 5;
                                    var7 = popupY + 35 + var10.indexUI / 2 * var2;
                                    this.gameAA(var1, var10, var6 - 1, var7 - 1, 0, 1);
//                                    if (var10.upgrade >= 0) {
//                                        String upgradeText = "+" + var10.upgrade + ",TL" + var10.checkne();
//                                        mFont.tahoma_7b_white.gameAA(var1, upgradeText, var6 + var2 / 2 - 27, var7 + 8, 2);
//                                    }
                                }
                            } else {
                                var6 = popupX + 4;
                                var7 = popupY + 34 + var10.indexUI / 2 * var2;
                                this.gameAA(var1, var10, var6 - 1, var7, 0, 1);
//                                if (var10.upgrade >= 0) {
//                                    String upgradeText = "+" + var10.upgrade + ",TL" + var10.checkne();
//                                    mFont.tahoma_7b_white.gameAA(var1, upgradeText, var6 + var2 / 2 + 27, var7 + 8, 2);
//                                }
                            }

                            if (GameCanvas.gameTick % 4 == 0) {
                                ++var10.indexEff;
                                if (var10.indexEff >= var10.eff.arrEfInfo.length) {
                                    var10.indexEff = 0;
                                }
                            }
                        }
                    }
                }

                if (gameRJ > 0) {
                    for (var4 = 0; var4 < 16; ++var4) {
                        if ((var10 = currentCharViewInfo.arrItemBody[var4 + gameRJ]) != null) {
                            var6 = var10.indexUI - 16;
                            if (var10.eff == null) {
                                var10.eff = efs[56];
                            }

                            int var8;
                            if (var6 != 0 && var6 != 2 && var6 != 4 && var6 != 6 && var6 != 8 && var6 != 10) {
                                if (var6 != 1 && var6 != 3 && var6 != 5 && var6 != 7 && var6 != 9) {
                                    if (var6 == 11 || var6 == 12 || var6 == 13 || var6 == 14 || var6 == 15) {
                                        if (var6 == 10) {
                                            var3 = 1;
                                        } else if (var6 == 11) {
                                            var3 = 2;
                                        } else if (var6 == 12) {
                                            var3 = 3;
                                        } else if (var6 == 13) {
                                            var3 = 4;
                                        } else if (var6 == 14) {
                                            var3 = 5;
                                        } else if (var6 == 15) {
                                            var3 = 6;
                                        }

                                        var7 = popupX + 2 + 1 + var3 * (var2 + 2) - var2;
                                        var8 = popupY + 35 + var2 * 5;
                                        this.gameAA(var1, var10, var7 - 2, var8 - 1, 0, 1);
                                    }
                                } else {
                                    var7 = popupX + popupW - var2 - 5;
                                    var8 = popupY + 35 + var6 / 2 * var2;
                                    this.gameAA(var1, var10, var7 - 1, var8 - 1, 0, 1);
                                }
                            } else {
                                var7 = popupX + 4;
                                var8 = popupY + 34 + var6 / 2 * var2;
                                this.gameAA(var1, var10, var7 - 1, var8, 0, 1);
                            }

                            if (GameCanvas.gameTick % 4 == 0) {
                                ++var10.indexEff;
                                if (var10.indexEff >= var10.eff.arrEfInfo.length) {
                                    var10.indexEff = 0;
                                }
                            }
                        }
                    }
                }

                for (var4 = 0; var4 < 16; ++var4) {
                    if (gameHL == 1 && var4 == indexSelect) {
                        if (var4 != 0 && var4 != 2 && var4 != 4 && var4 != 6 && var4 != 8) {
                            if (var4 != 1 && var4 != 3 && var4 != 5 && var4 != 7 && var4 != 9) {
                                if (var4 == 9 || var4 == 10 || var4 == 11 || var4 == 12 || var4 == 13 || var4 == 14 || var4 == 15) {
                                    if (var4 == 9) {
                                        var3 = 0;
                                    } else if (var4 == 10) {
                                        var3 = 1;
                                    } else if (var4 == 11) {
                                        var3 = 2;
                                    } else if (var4 == 12) {
                                        var3 = 3;
                                    } else if (var4 == 13) {
                                        var3 = 4;
                                    } else if (var4 == 14) {
                                        var3 = 5;
                                    } else if (var4 == 15) {
                                        var3 = 6;
                                    }

                                    var5 = popupX + 2 + 1 + var3 * (var2 + 2) - var2;
                                    var6 = popupY + 35 + var2 * 5;
                                    var1.gameAA(16777215);
                                    var1.gameAB(var5 - 1, var6, var2, var2);
                                    gameAA(var5 - 2, var6 - 1, var1);
                                }
                            } else {
                                var1.gameAA(16777215);
                                var1.gameAB(popupX + popupW - var2 - 4 - 1, popupY + 35 + var4 / 2 * var2, var2, var2);
                                gameAA(popupX + popupW - var2 - 4 - 2, popupY + 35 + var4 / 2 * var2 - 1, var1);
                            }
                        } else {
                            var1.gameAA(16777215);
                            var1.gameAB(popupX + 4, popupY + 35 + var4 / 2 * var2, var2, var2);
                            gameAA(popupX + 5 - 2, popupY + 35 + var4 / 2 * var2 - 1, var1);
                        }
                    }
                }

                var4 = GameCanvas.gameAJ ? -25 : 16;
                Part var11 = parts[currentCharViewInfo.head];
                Part var12 = parts[currentCharViewInfo.leg];
                Part var13 = parts[currentCharViewInfo.body];
                Part var14 = parts[currentCharViewInfo.wp];
                if (currentCharViewInfo.arrItemBody != null && currentCharViewInfo.arrItemBody[11] != null) {
                    var11 = parts[currentCharViewInfo.arrItemBody[11].template.part];
                }

                if (var11.pi != null && var11.pi.length >= 8) {
                    for (var2 = 0; var2 < var11.pi.length; ++var2) {
                        if (var11.pi[var2] == null || !SmallImage.gameAA(var11.pi[var2].id)) {
                            Char.getMyChar();
                            var11 = Char.gameAB(Char.getMyChar().cgender);
                            break;
                        }
                    }
                } else {
                    Char.getMyChar();
                    var11 = Char.gameAB(Char.getMyChar().cgender);
                }

                var2 = currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1;
                int[] var15 = currentCharViewInfo.gameAS();
                if (currentCharViewInfo.ID_WEA_PONE > -1) {
                    currentCharViewInfo.gameAG(var1, gW2, gH2 - 24, var2);
                }

                if (currentCharViewInfo.ID_PP > -1) {
                    currentCharViewInfo.gameAC(var1, gW2, gH2 - 24, var2);
                }

                if (var15 != null && currentCharViewInfo.ID_PP == -1) {
                    if (Char.getMyChar().tickCoat == 0) {
                        SmallImage.gameAA(var1, var15[Char.getMyChar().tickCoat], gW2 + Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][1] + var11.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][0]].dx - 2, gH2 + var4 - Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][2] + var11.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][0]].dy + 16, 0, 0);
                    } else if (Char.getMyChar().tickCoat == 1) {
                        SmallImage.gameAA(var1, var15[Char.getMyChar().tickCoat], gW2 + Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][1] + var11.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][0]].dx - 9, gH2 + var4 - Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][2] + var11.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][0]].dy + 16, 0, 0);
                    } else if (Char.getMyChar().tickCoat == 2) {
                        SmallImage.gameAA(var1, var15[Char.getMyChar().tickCoat], gW2 + Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][1] + var11.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][0]].dx - 12, gH2 + var4 - Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][2] + var11.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][0]].dy + 16, 0, 0);
                    } else {
                        SmallImage.gameAA(var1, var15[Char.getMyChar().tickCoat], gW2 + Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][1] + var11.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][0]].dx - 9, gH2 + var4 - Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][2] + var11.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][0]].dy + 16, 0, 0);
                    }
                }

                currentCharViewInfo.gameAC(var1, gW2 + Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][2][1] + var13.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][2][0]].dx + 18, gH2 + var4 - Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][1][2] + var12.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][1][0]].dy + 5);
                if (currentCharViewInfo.ID_WEA_PONE == -1) {
                    SmallImage.gameAA(var1, var14.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][3][0]].id, gW2 + Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][3][1] + var14.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][3][0]].dx, gH2 + var4 - Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][3][2] + var14.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][3][0]].dy, 0, 0);
                }

                if (currentCharViewInfo.ID_LEG > -1) {
                    currentCharViewInfo.gameAI(var1, gW2, gH2 - 24, var2);
                } else {
                    SmallImage.gameAA(var1, var12.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][1][0]].id, gW2 + Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][1][1] + var12.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][1][0]].dx, gH2 + var4 - Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][1][2] + var12.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][1][0]].dy, 0, 0);
                }

                if (currentCharViewInfo.ID_Body > -1) {
                    currentCharViewInfo.gameAJ(var1, gW2, gH2 - 24, var2);
                } else {
                    SmallImage.gameAA(var1, var13.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][2][0]].id, gW2 + Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][2][1] + var13.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][2][0]].dx, gH2 + var4 - Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][2][2] + var13.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][2][0]].dy, 0, 0);
                }

                if (currentCharViewInfo.ID_HAIR > -1) {
                    currentCharViewInfo.gameAH(var1, gW2, gH2 - 24, var2);
                } else {
                    SmallImage.gameAA(var1, var11.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][0]].id, gW2 + Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][1] + var11.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][0]].dx, gH2 + var4 - Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][2] + var11.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][0][0]].dy, 0, 0);
                }

                if (currentCharViewInfo.ID_MAT_NA > -1) {
                    currentCharViewInfo.gameAE(var1, gW2, gH2 - 24, var2);
                }

                currentCharViewInfo.gameAC(var1, gW2 + Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][2][1] + var13.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][2][0]].dx + 5, gH2 + var4 - Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][1][2] + var12.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][1][0]].dy + 5);
                currentCharViewInfo.gameAD(var1, gW2 + Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][2][1] + var13.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][2][0]].dx + 22, gH2 + var4 - Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][1][2] + var12.pi[Char.CharInfo[currentCharViewInfo.cp1 % 15 < 5 ? 0 : 1][1][0]].dy + 5);
                if (currentCharViewInfo.ID_PP > -1) {
                    currentCharViewInfo.gameAD(var1, gW2, gH2 - 24, var2);
                }

                if (currentCharViewInfo.ID_WEA_PONE > -1) {
                    currentCharViewInfo.gameAF(var1, gW2, gH2 - 24, var2);
                }

                if (this.gameHG != null && GameCanvas.isTouch) {
                    Command var9;
                    if (!(var9 = this.gameHG).isFocus) {
                        var1.gameAA(imgLbtn, var9.x, var9.y, 0);
                    } else {
                        var1.gameAA(imgLbtnFocus, var9.x, var9.y, 0);
                    }

                    mFont.tahoma_7b_yellow.gameAA(var1, var9.caption, var9.x + 36, var9.y + 6, 2);
                }

            }
        }
    }

    private void gameAL(mGraphics var1) {
        if (indexMenu == 5) {
            var1.gameAA(-var1.gameAA(), -var1.gameAB());
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            var1.gameAA(Paint.COLORBACKGROUND);
            gameAA(var1, mResources.gameGG[indexMenu], true);
            gameAB(var1);
            var1.gameAA(0);
            var1.gameAC(popupX + 2, popupY + 31, 171, popupH - 34);
            var1.gameAA(13606712);
            var1.gameAB(popupX + 3, popupY + 32, 168, popupH - 37);
            var1.gameAA(Paint.COLORBACKGROUND);
            var1.gameAC(popupX + 4, popupY + 34, 166, popupH - 39);
            int var2;
            int var3;
            if (currentCharViewInfo.arrItemMounts[4] != null) {
                mFont.tahoma_7b_white.gameAA(var1, currentCharViewInfo.arrItemMounts[4].template.name, popupX + 90, gameTE + 2, 2);
                var2 = currentCharViewInfo.arrItemMounts[4].sys + 1;

                for (var3 = 0; var3 < var2; ++var3) {
                    SmallImage.gameAA(var1, 628, popupX + 90 + var3 * 12 - var2 * 6, gameTE + 20, 0, 3);
                }
            } else {
                mFont.tahoma_7b_white.gameAA(var1, mResources.gameWH, popupX + 90, gameTE + 2, 2);
            }

            for (var2 = 0; var2 < currentCharViewInfo.arrItemMounts.length - 1; ++var2) {
                if (currentCharViewInfo.arrItemMounts[var2] != null) {
                    this.gameAA(var1, currentCharViewInfo.arrItemMounts[var2], this.gameNW[var2], this.gameNX[var2]);
                } else {
                    var1.gameAA(6425);
                    var1.gameAC(this.gameNW[var2] - 1, this.gameNX[var2] - 1, gameHK + 3, gameHK + 3);
                    if (var2 == 0) {
                        if (currentCharViewInfo.gameAP()) {
                            mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[22][0], this.gameNW[var2] + gameHK / 2, this.gameNX[var2] + gameHK / 2 - 10, 2);
                            mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[22][1], this.gameNW[var2] + gameHK / 2, this.gameNX[var2] + gameHK / 2 + 2, 2);
                        } else {
                            mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[19][0], this.gameNW[var2] + gameHK / 2, this.gameNX[var2] + gameHK / 2 - 10, 2);
                            mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[19][1], this.gameNW[var2] + gameHK / 2, this.gameNX[var2] + gameHK / 2 + 2, 2);
                        }
                    } else if (var2 == 1) {
                        if (currentCharViewInfo.gameAP()) {
                            mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[20][0], this.gameNW[var2] + gameHK / 2, this.gameNX[var2] + gameHK / 2 - 10, 2);
                            mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[20][1], this.gameNW[var2] + gameHK / 2, this.gameNX[var2] + gameHK / 2 + 2, 2);
                        } else {
                            mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[16][0], this.gameNW[var2] + gameHK / 2, this.gameNX[var2] + gameHK / 2 - 10, 2);
                            mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[16][1], this.gameNW[var2] + gameHK / 2, this.gameNX[var2] + gameHK / 2 + 2, 2);
                        }
                    } else if (var2 == 2) {
                        if (currentCharViewInfo.gameAP()) {
                            mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[21][0], this.gameNW[var2] + gameHK / 2, this.gameNX[var2] + gameHK / 2 - 10, 2);
                            mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[21][1], this.gameNW[var2] + gameHK / 2, this.gameNX[var2] + gameHK / 2 + 2, 2);
                        } else {
                            mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[17][0], this.gameNW[var2] + gameHK / 2, this.gameNX[var2] + gameHK / 2 - 10, 2);
                            mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[17][1], this.gameNW[var2] + gameHK / 2, this.gameNX[var2] + gameHK / 2 + 2, 2);
                        }
                    } else if (var2 == 3) {
                        if (currentCharViewInfo.gameAP()) {
                            mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[23][0], this.gameNW[var2] + gameHK / 2, this.gameNX[var2] + gameHK / 2 - 10, 2);
                            mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[23][1], this.gameNW[var2] + gameHK / 2, this.gameNX[var2] + gameHK / 2 + 2, 2);
                        } else {
                            mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[18][0], this.gameNW[var2] + gameHK / 2, this.gameNX[var2] + gameHK / 2 - 10, 2);
                            mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[18][1], this.gameNW[var2] + gameHK / 2, this.gameNX[var2] + gameHK / 2 + 2, 2);
                        }
                    }
                }

                if (indexSelect == var2 && gameHL == 1 && indexSelect < 4) {
                    var1.gameAA(16777215);
                } else {
                    var1.gameAA(12281361);
                }

                var1.gameAB(this.gameNW[var2], this.gameNX[var2], gameHK, gameHK);
            }

            var2 = this.gameNW[0] + gameHK + 7;
            var3 = this.gameNX[0] - 5;
            var1.gameAA(6425);
            var1.gameAC(var2, var3, 84, 75);
            if (indexSelect == 4) {
                var1.gameAA(16777215);
            } else {
                var1.gameAA(12281361);
            }

            var1.gameAB(var2, var3, 84, 75);
            int var9 = 0;
            int var4 = 0;
            int var5 = 0;
            int var6 = 0;
            int var7 = 0;
            if (currentCharViewInfo.ID_HORSE > -1) {
                currentCharViewInfo.gameAB(var1, var2 + 35, var3 + 55);
            }

            int var11;
            if (currentCharViewInfo.arrItemMounts[4] != null) {
                if (currentCharViewInfo.gameBD()) {
                    int[][] var8 = (int[][]) CharPartInfo.gameAH.gameAA(String.valueOf(currentCharViewInfo.arrItemMounts[4].template.id));
                    if (GameCanvas.gameTick % 20 > 15) {
                        SmallImage.gameAA(var1, var8[0][0], var2 + 45 - 10, var3 + 35, 0, 3);
                    } else {
                        SmallImage.gameAA(var1, var8[0][1], var2 + 45 - 10, var3 + 35, 0, 3);
                    }
                } else if (currentCharViewInfo.gameAP()) {
                    if (currentCharViewInfo.arrItemMounts[4].template.id == 485) {
                        if (currentCharViewInfo.arrItemMounts[4].sys < 2) {
                            SmallImage.gameAA(var1, 1800, var2 + 45, var3 + 35, 0, 3);
                        } else {
                            SmallImage.gameAA(var1, 2063, var2 + 45, var3 + 35, 0, 3);
                        }
                    } else if (currentCharViewInfo.arrItemMounts[4].template.id == 524) {
                        if (currentCharViewInfo.arrItemMounts[4].sys < 2) {
                            SmallImage.gameAA(var1, 2067, var2 + 45, var3 + 35, 0, 3);
                        } else {
                            SmallImage.gameAA(var1, 2071, var2 + 45, var3 + 35, 0, 3);
                        }
                    }
                } else if (currentCharViewInfo.gameAO()) {
                    if (currentCharViewInfo.arrItemMounts[4].template.id == 443) {
                        if (currentCharViewInfo.arrItemMounts[4].sys < 2) {
                            if (GameCanvas.gameTick % 20 > 15) {
                                SmallImage.gameAA(var1, 1801, var2 + 45, var3 + 35, 0, 3);
                            } else {
                                SmallImage.gameAA(var1, 1802, var2 + 45, var3 + 35, 0, 3);
                            }
                        } else if (GameCanvas.gameTick % 20 > 15) {
                            SmallImage.gameAA(var1, 2080, var2 + 45, var3 + 35, 0, 3);
                        } else {
                            SmallImage.gameAA(var1, 2081, var2 + 45, var3 + 35, 0, 3);
                        }
                    } else if (currentCharViewInfo.arrItemMounts[4].template.id == 523) {
                        if (GameCanvas.gameTick % 20 > 15) {
                            SmallImage.gameAA(var1, 2062, var2 + 45, var3 + 35, 0, 3);
                        } else {
                            SmallImage.gameAA(var1, 2061, var2 + 45, var3 + 35, 0, 3);
                        }
                    }
                }

                if (currentCharViewInfo.arrItemMounts[4].options != null) {
                    for (var11 = 0; var11 < currentCharViewInfo.arrItemMounts[4].options.size(); ++var11) {
                        ItemOption var10;
                        if ((var10 = (ItemOption) currentCharViewInfo.arrItemMounts[4].options.elementAt(var11)).optionTemplate.id == 65) {
                            var9 = var10.param;
                        } else if (var10.optionTemplate.id == 66) {
                            var4 = var10.param;
                        }
                    }
                }

                var5 = var9 * 85 / 1000;
                var6 = var4 * 85 / 1000;
                var7 = currentCharViewInfo.arrItemMounts[4].upgrade + 1;
            }

            var11 = gameTD + 5;
            var2 = gameTE + 112;
            mFont.tahoma_7b_white.gameAA(var1, mResources.gameEU + ": ", var11, var2, 0);
            mFont.tahoma_7b_white.gameAA(var1, String.valueOf(var7), var11 + 70, var2, 0);
            mFont var10000;
            String var10002;
            if (currentCharViewInfo.gameAP()) {
                var10000 = mFont.tahoma_7b_white;
                var10002 = mResources.gameWW + ": ";
                var2 += 15;
                var10000.gameAA(var1, var10002, var11, var2, 0);
            } else {
                var10000 = mFont.tahoma_7b_white;
                var10002 = mResources.gameKA + ": ";
                var2 += 15;
                var10000.gameAA(var1, var10002, var11, var2, 0);
            }

            var1.gameAA(6425);
            var1.gameAC(var11 + 70, var2, 85, 14);
            var1.gameAA(371981);
            var1.gameAC(var11 + 70, var2, var5, 14);
            var1.gameAA(5131338);
            var1.gameAB(var11 + 70, var2, 85, 14);
            mFont.tahoma_7_white.gameAA(var1, var9 + "/" + 1000, var11 + 113, var2 + 2, 2);
            if (currentCharViewInfo.gameAP()) {
                var10000 = mFont.tahoma_7b_white;
                var10002 = mResources.gameWX + ": ";
                var2 += 17;
                var10000.gameAA(var1, var10002, var11, var2, 0);
            } else {
                var10000 = mFont.tahoma_7b_white;
                var10002 = mResources.gameWG + ": ";
                var2 += 17;
                var10000.gameAA(var1, var10002, var11, var2, 0);
            }

            var1.gameAA(6425);
            var1.gameAC(var11 + 70, var2, 85, 14);
            var1.gameAA(16711680);
            var1.gameAC(var11 + 70, var2, var6, 14);
            var1.gameAA(5131338);
            var1.gameAB(var11 + 70, var2, 85, 14);
            mFont.tahoma_7_white.gameAA(var1, var4 + "/" + 1000, var11 + 113, var2 + 2, 2);
        }
    }

    private void gameAM(mGraphics var1) {
        if (gameME) {
            gameAB(var1);
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            gameAA(var1, mResources.gameRH[indexMenu], true);
            if (indexMenu == 0) {
                if (Char.clan != null && Char.clan.name != null && !Char.clan.name.equals("")) {
                    int[] var6 = new int[]{1692, 1693, 1694, 1695, 1696};

                    for (int var4 = 0; var4 < var6.length; ++var4) {
                        var1.gameAA(6425);
                        var1.gameAC(popupX + var4 * gameHK + 18, popupY + 32, gameHK - 2, gameHK - 2);
                        if (gameHL == 1 && var4 == indexSelect) {
                            var1.gameAA(16777215);
                        } else {
                            var1.gameAA(12281361);
                        }

                        var1.gameAB(popupX + var4 * gameHK + 18, popupY + 32, gameHK - 2, gameHK - 2);
                        if (var4 > Char.clan.itemLevel - 1) {
                            SmallImage.gameAA(var1, 1697, popupX + var4 * gameHK + 18 + gameHK / 2, popupY + 32 + gameHK / 2, 0, 3);
                        } else {
                            SmallImage.gameAA(var1, var6[var4], popupX + var4 * gameHK + 18 + gameHK / 2, popupY + 32 + gameHK / 2, 0, 3);
                        }
                    }

                    if (gameHL == 2) {
                        var1.gameAA(Paint.COLORDARK);
                        var1.gameAC(popupX + 7, popupY + 60, popupW - 14, popupH - 68);
                        var1.gameAA(16777215);
                    } else {
                        var1.gameAA(10249521);
                    }

                    var1.gameAB(popupX + 7, popupY + 60, popupW - 14, popupH - 68);
                    gameTD = popupX + 17;
                    gameTE = popupY + 62;
                    gameHM = 12;
                    scrMain.gameAA(gameHM, 12, popupX, popupY + 62, popupW, popupH - 72, true, 1);
                    scrMain.gameAA(var1);
                    mFont.tahoma_7b_yellow.gameAA(var1, mResources.gameRI[0] + Char.clan.name, gameTD, gameTE, 0);
                    mFont.tahoma_7_blue1.gameAA(var1, mResources.gameRI[1] + Char.clan.main_name, gameTD, gameTE += 12, 0);
                    mFont.tahoma_7_white.gameAA(var1, mResources.gameRI[2] + Char.clan.total + "/" + (Char.clan.level * 5 + 45), gameTD, gameTE += 12, 0);
                    mFont.tahoma_7_white.gameAA(var1, mResources.gameRI[3] + Char.clan.level, gameTD, gameTE += 12, 0);
                    mFont.tahoma_7_white.gameAA(var1, mResources.gameRI[4] + Char.clan.exp + "/" + Char.clan.expNext, gameTD, gameTE += 12, 0);
                    mFont.tahoma_7_white.gameAA(var1, mResources.gameRI[5] + NinjaUtil.gameAA(String.valueOf(Char.clan.coin)) + " " + mResources.gamePA, gameTD, gameTE += 12, 0);
                    mFont.tahoma_7_white.gameAA(var1, mResources.gameRI[8] + NinjaUtil.gameAA(String.valueOf(Char.clan.freeCoin)) + " " + mResources.gamePA, gameTD, gameTE += 12, 0);
                    mFont.tahoma_7_white.gameAA(var1, mResources.gameRI[9] + NinjaUtil.gameAA(String.valueOf(Char.clan.coinUp)) + " " + mResources.gamePA, gameTD, gameTE += 12, 0);
                    if (mFont.tahoma_7_white.gameAA(mResources.gameRI[10] + Char.clan.openDun + " " + mResources.gameRG) > gameTS - 10) {
                        this.gameAA(var1, mFont.tahoma_7_white, mResources.gameRI[10] + Char.clan.openDun + " " + mResources.gameRG, gameTD, gameTE += 12, 0, popupW - 20);
                    } else {
                        mFont.tahoma_7_white.gameAA(var1, mResources.gameRI[10] + Char.clan.openDun + " " + mResources.gameRG, gameTD, gameTE += 12, 0);
                    }

                    if (mFont.tahoma_7_white.gameAA(mResources.gameRI[12] + Char.clan.use_card + " " + mResources.gameRG) > gameTS - 10) {
                        this.gameAA(var1, mFont.tahoma_7_white, mResources.gameRI[12] + Char.clan.use_card + " " + mResources.gameRG, gameTD, gameTE += 12, 0, popupW - 20);
                    } else {
                        mFont.tahoma_7_white.gameAA(var1, mResources.gameRI[12] + Char.clan.use_card + " " + mResources.gameRG, gameTD, gameTE += 12, 0);
                    }

                    mFont.tahoma_7_white.gameAA(var1, mResources.gameRI[6] + Char.clan.reg_date, gameTD, gameTE += 12, 0);
                    if (this.gameRM == null) {
                        this.gameRM = gameAA(mFont.tahoma_7_yellow, Char.clan.alert);
                    }

                    this.gameAA(var1, mFont.tahoma_7_yellow, (String[]) this.gameRM, gameTD, gameTE += 12, 0);
                    if (gameHL == 2 && indexRow >= 0) {
                        SmallImage.gameAA(var1, 942, gameTD - 8, popupY + 62 + 2 + indexRow * 12, 0, StaticObj.TOP_LEFT);
                    }

                    scrMain.gameAA(gameHM, 12, popupX, popupY + 62, popupW, popupH - 72, true, 1);
                } else {
                    gameHM = 1;
                    mFont.tahoma_7_white.gameAA(var1, mResources.gameSO, popupX + popupW / 2, popupY + 40, 2);
                }
            } else if (indexMenu == 1) {
                gameTD = popupX + 5;
                gameTE = popupY + 32;
                if (vClan.size() == 0) {
                    mFont.tahoma_7_white.gameAA(var1, mResources.gameSO, popupX + popupW / 2, popupY + 40, 2);
                } else {
                    var1.gameAA(6425);
                    var1.gameAC(gameTD - 2, gameTE - 2, popupW - 6, gameHK * 5 + 8);
                    gameAB(var1);
                    scrMain.gameAA(var1, gameTD, gameTE, popupW - 3, gameHK * 5 + 6);
                    this.gameRL = 0;

                    for (int var5 = 0; var5 < vClan.size(); ++var5) {
                        Member var3 = (Member) vClan.elementAt(var5);
                        if (!gameHR || var3.isOnline) {
                            if (var5 * (gameHK + gameHK / 2) >= scrMain.cmy - (gameHK + gameHK / 2) && var5 * (gameHK + gameHK / 2) < scrMain.cmy + gameHK * 5 + 8) {
                                if (indexRow == this.gameRL) {
                                    var1.gameAA(Paint.COLORLIGHT);
                                    var1.gameAC(gameTD + 2, gameTE + indexRow * (gameHK + gameHK / 2) + 2, popupW - 15, gameHK + gameHK / 2 - 4);
                                    var1.gameAA(16777215);
                                    var1.gameAB(gameTD + 2, gameTE + indexRow * (gameHK + gameHK / 2) + 2, popupW - 15, gameHK + gameHK / 2 - 4);
                                } else {
                                    var1.gameAA(Paint.COLORBACKGROUND);
                                    var1.gameAC(gameTD + 2, gameTE + this.gameRL * (gameHK + gameHK / 2) + 2, popupW - 15, gameHK + gameHK / 2 - 4);
                                    var1.gameAA(13932896);
                                    var1.gameAB(gameTD + 2, gameTE + this.gameRL * (gameHK + gameHK / 2) + 2, popupW - 15, gameHK + gameHK / 2 - 4);
                                }

                                SmallImage.gameAA(var1, var3.iconId, gameTD + 12, gameTE + this.gameRL * (gameHK + gameHK / 2) + 13, 0, 3);
                                if (var3.type == 4) {
                                    SmallImage.gameAA(var1, 1216, gameTD + 12, gameTE + this.gameRL * (gameHK + gameHK / 2) + 30, 0, 3);
                                    if (var3.isOnline) {
                                        mFont.tahoma_7_yellow.gameAA(var1, mResources.gameSU[0] + " ", gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 5, 0, mFont.tahoma_7_grey);
                                        mFont.tahoma_7_white.gameAA(var1, var3.name + " - " + mResources.gameEV + ": " + var3.level, gameTD + 45, gameTE + this.gameRL * (gameHK + gameHK / 2) + 5, 0);
                                        mFont.tahoma_7_green.gameAA(var1, mResources.gameRI[7] + var3.pointClan, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 16, 0);
                                        mFont.tahoma_7_blue1.gameAA(var1, mResources.gameRI[11] + var3.pointClanWeek, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 26, 0);
                                    } else {
                                        mFont.tahoma_7_grey.gameAA(var1, mResources.gameSU[0] + " ", gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 5, 0);
                                        mFont.tahoma_7_grey.gameAA(var1, var3.name + " - " + mResources.gameEV + ": " + var3.level, gameTD + 45, gameTE + this.gameRL * (gameHK + gameHK / 2) + 5, 0);
                                        mFont.tahoma_7_grey.gameAA(var1, mResources.gameRI[7] + var3.pointClan, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 16, 0);
                                        mFont.tahoma_7_grey.gameAA(var1, mResources.gameRI[11] + var3.pointClanWeek, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 26, 0);
                                    }
                                } else if (var3.type == 3) {
                                    SmallImage.gameAA(var1, 1215, gameTD + 12, gameTE + this.gameRL * (gameHK + gameHK / 2) + 30, 0, 3);
                                    if (var3.isOnline) {
                                        mFont.tahoma_7_yellow.gameAA(var1, mResources.gameSU[1] + " ", gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 5, 0, mFont.tahoma_7_grey);
                                        mFont.tahoma_7_white.gameAA(var1, var3.name + " - " + mResources.gameEV + ": " + var3.level, gameTD + 45, gameTE + this.gameRL * (gameHK + gameHK / 2) + 5, 0);
                                        mFont.tahoma_7_green.gameAA(var1, mResources.gameRI[7] + var3.pointClan, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 16, 0);
                                        mFont.tahoma_7_blue1.gameAA(var1, mResources.gameRI[11] + var3.pointClanWeek, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 26, 0);
                                    } else {
                                        mFont.tahoma_7_grey.gameAA(var1, mResources.gameSU[1] + " ", gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 5, 0);
                                        mFont.tahoma_7_grey.gameAA(var1, var3.name + " - " + mResources.gameEV + ": " + var3.level, gameTD + 45, gameTE + this.gameRL * (gameHK + gameHK / 2) + 5, 0);
                                        mFont.tahoma_7_grey.gameAA(var1, mResources.gameRI[7] + var3.pointClan, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 16, 0);
                                        mFont.tahoma_7_grey.gameAA(var1, mResources.gameRI[11] + var3.pointClanWeek, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 26, 0);
                                    }
                                } else if (var3.type == 2) {
                                    SmallImage.gameAA(var1, 1217, gameTD + 12, gameTE + this.gameRL * (gameHK + gameHK / 2) + 30, 0, 3);
                                    if (var3.isOnline) {
                                        mFont.tahoma_7_yellow.gameAA(var1, mResources.gameSU[2] + " ", gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 5, 0, mFont.tahoma_7_grey);
                                        mFont.tahoma_7_white.gameAA(var1, var3.name + " - " + mResources.gameEV + ": " + var3.level, gameTD + 45, gameTE + this.gameRL * (gameHK + gameHK / 2) + 5, 0);
                                        mFont.tahoma_7_green.gameAA(var1, mResources.gameRI[7] + var3.pointClan, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 16, 0);
                                        mFont.tahoma_7_blue1.gameAA(var1, mResources.gameRI[11] + var3.pointClanWeek, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 26, 0);
                                    } else {
                                        mFont.tahoma_7_grey.gameAA(var1, mResources.gameSU[2] + " ", gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 5, 0);
                                        mFont.tahoma_7_grey.gameAA(var1, var3.name + " - " + mResources.gameEV + ": " + var3.level, gameTD + 45, gameTE + this.gameRL * (gameHK + gameHK / 2) + 5, 0);
                                        mFont.tahoma_7_grey.gameAA(var1, mResources.gameRI[7] + var3.pointClan, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 16, 0);
                                        mFont.tahoma_7_grey.gameAA(var1, mResources.gameRI[11] + var3.pointClanWeek, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 26, 0);
                                    }
                                } else if (var3.type == 1) {
                                    SmallImage.gameAA(var1, 1214, gameTD + 12, gameTE + this.gameRL * (gameHK + gameHK / 2) + 30, 0, 3);
                                    if (var3.isOnline) {
                                        mFont.tahoma_7_yellow.gameAA(var1, mResources.gameSU[3] + " ", gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 5, 0, mFont.tahoma_7_grey);
                                        mFont.tahoma_7_white.gameAA(var1, var3.name + " - " + mResources.gameEV + ": " + var3.level, gameTD + 45, gameTE + this.gameRL * (gameHK + gameHK / 2) + 5, 0);
                                        mFont.tahoma_7_green.gameAA(var1, mResources.gameRI[7] + var3.pointClan, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 16, 0);
                                        mFont.tahoma_7_blue1.gameAA(var1, mResources.gameRI[11] + var3.pointClanWeek, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 26, 0);
                                    } else {
                                        mFont.tahoma_7_grey.gameAA(var1, mResources.gameSU[3] + " ", gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 5, 0);
                                        mFont.tahoma_7_grey.gameAA(var1, var3.name + " - " + mResources.gameEV + ": " + var3.level, gameTD + 45, gameTE + this.gameRL * (gameHK + gameHK / 2) + 5, 0);
                                        mFont.tahoma_7_grey.gameAA(var1, mResources.gameRI[7] + var3.pointClan, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 16, 0);
                                        mFont.tahoma_7_grey.gameAA(var1, mResources.gameRI[11] + var3.pointClanWeek, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 26, 0);
                                    }
                                } else if (var3.isOnline) {
                                    mFont.tahoma_7_white.gameAA(var1, var3.name + " - " + mResources.gameEV + ": " + var3.level, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 5, 0);
                                    mFont.tahoma_7_green.gameAA(var1, mResources.gameRI[7] + var3.pointClan, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 16, 0);
                                    mFont.tahoma_7_blue1.gameAA(var1, mResources.gameRI[11] + var3.pointClanWeek, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 26, 0);
                                } else {
                                    mFont.tahoma_7_grey.gameAA(var1, var3.name + " - " + mResources.gameEV + ": " + var3.level, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 5, 0);
                                    mFont.tahoma_7_grey.gameAA(var1, mResources.gameRI[7] + var3.pointClan, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 16, 0);
                                    mFont.tahoma_7_grey.gameAA(var1, mResources.gameRI[11] + var3.pointClanWeek, gameTD + 22, gameTE + this.gameRL * (gameHK + gameHK / 2) + 26, 0);
                                }
                            }

                            ++this.gameRL;
                        }
                    }

                    scrMain.gameAA(this.gameRL, gameHK + gameHK / 2, gameTD, gameTE, popupW - 3, gameHK * 5 + 4, true, 1);
                    gameHM = this.gameRL;
                    gameAN(var1);
                }
            } else if (indexMenu != 2) {
                if (indexMenu == 3) {
                    if (Char.clan != null && Char.clan.name != null && !Char.clan.name.equals("") && !Char.clan.log.equals("")) {
                        gameHM = 1;
                        gameTD = popupX + 17;
                        gameTE = popupY + 34;
                        gameTS = popupW - 30;
                        scrMain.gameAA(var1);
                        if (this.gameRN == null) {
                            this.gameRN = gameAA(mFont.tahoma_7_white, Char.clan.log);
                        }

                        this.gameAA(var1, mFont.tahoma_7_white, (String[]) this.gameRN, gameTD, gameTE, 0);
                        if (gameHL == 1 && indexRow >= 0) {
                            SmallImage.gameAA(var1, 942, gameTD - 8, popupY + 34 + 2 + indexRow * 12, 0, StaticObj.TOP_LEFT);
                        }

                        scrMain.gameAA(gameHM, 12, popupX, popupY + 35, popupW, popupH - 44, true, 1);
                    } else {
                        gameHM = 1;
                        mFont.tahoma_7_white.gameAA(var1, mResources.gameSO, popupX + popupW / 2, popupY + 40, 2);
                    }
                } else {
                    if (indexMenu == 4) {
                        this.gameBQ(var1);
                    }

                }
            } else {
                gameTD = popupX + 3;
                gameTE = popupY + 32;
                var1.gameAA(6425);
                var1.gameAC(gameTD - 1, gameTE - 1, gameTJ * gameHK + 3, 5 * gameHK + 3);
                Item[] var2 = null;
                if (Char.clan != null && Char.clan.items != null) {
                    var2 = Char.clan.items;
                } else {
                    var2 = new Item[30];
                }

                this.gameAA(var1, var2);
            }
        }
    }

    private static void gameAN(mGraphics var0) {
        gameAB(var0);
        int var1 = indexRow;
        if (gameMH) {
            var1 = indexSelect;
        }

        if (var1 >= 0 && gameHM > 0) {
            var1 = var1 + 1 < gameHM ? var1 + 1 : gameHM;
            mFont.tahoma_7_yellow.gameAA(var0, var1 + "/" + gameHM, popupX + popupW / 2, popupY + popupH - 12, 2, mFont.tahoma_7_grey);
        }

    }

    private void gameCV() {
        if (isPaintInfoMe && indexMenu != -1 && GameCanvas.currentDialog == null) {
            int var1;
            if (gameHL == 0) {
                super.left = super.center = null;
                if (indexMenu == 0) {
                    super.left = new Command(mResources.gameCD, 110221);
                }

                if (GameCanvas.keyPressedz[8]) {
                    gameHL = 1;
                    indexSelect = 0;
                    indexRow = 0;
                    scrMain.gameAA();
                    gameHO.gameAA();
                }

                if (GameCanvas.keyPressedz[4]) {
                    indexSelect = 0;
                    indexRow = -1;
                    --indexMenu;
                    scrMain.gameAA();
                    gameHO.gameAA();
                    if (currentCharViewInfo.charID != Char.getMyChar().charID) {
                        if (indexMenu < 3) {
                            indexMenu = 5;
                        }
                    } else if (indexMenu < 0) {
                        indexMenu = mResources.gameGG.length - 1;
                    }

                    this.gameCE();
                }

                if (GameCanvas.keyPressedz[6]) {
                    indexSelect = 0;
                    indexRow = -1;
                    ++indexMenu;
                    scrMain.gameAA();
                    gameHO.gameAA();
                    if (currentCharViewInfo.charID != Char.getMyChar().charID) {
                        if (indexMenu > 5) {
                            indexMenu = 3;
                        }
                    } else if (indexMenu > mResources.gameGG.length - 1) {
                        indexMenu = 0;
                    }

                    this.gameCE();
                }

                this.gameBJ();
            } else if (gameDQ) {
                if (GameCanvas.keyPressedz[2]) {
                    if (--indexRow < 0) {
                        indexRow = gameHM - 1;
                    }

                    gameHO.gameAA(indexRow * gameHO.ITEM_SIZE);
                } else if (GameCanvas.keyPressedz[8]) {
                    if (++indexRow >= gameHM) {
                        indexRow = 0;
                    }

                    gameHO.gameAA(indexRow * gameHO.ITEM_SIZE);
                }
            } else if (indexMenu == 0) {
                if (GameCanvas.keyPressedz[4]) {
                    if (--indexSelect < 0) {
                        indexSelect = Char.getMyChar().arrItemBag.length - 1;
                    }

                    super.left = super.center = null;
                    this.gameBJ();
                    scrMain.gameAA(indexSelect / gameTJ * scrMain.ITEM_SIZE);
                } else if (GameCanvas.keyPressedz[6]) {
                    if (++indexSelect >= Char.getMyChar().arrItemBag.length) {
                        indexSelect = 0;
                    }

                    super.left = super.center = null;
                    this.gameBJ();
                    scrMain.gameAA(indexSelect / gameTJ * scrMain.ITEM_SIZE);
                } else if (GameCanvas.keyPressedz[8]) {
                    if (indexSelect + gameTJ <= Char.getMyChar().arrItemBag.length - 1) {
                        indexSelect += gameTJ;
                    }

                    super.left = super.center = null;
                    this.gameBJ();
                    scrMain.gameAA(indexSelect / gameTJ * scrMain.ITEM_SIZE);
                } else if (GameCanvas.keyPressedz[2]) {
                    if (indexSelect >= 0 && indexSelect < gameTJ) {
                        gameHL = 0;
                        indexSelect = 0;
                    } else if (indexSelect - gameTJ >= 0) {
                        indexSelect -= gameTJ;
                    }

                    super.left = super.center = null;
                    this.gameBJ();
                    scrMain.gameAA(indexSelect / gameTJ * scrMain.ITEM_SIZE);
                }
            } else if (indexMenu == 1) {
                if (GameCanvas.keyPressedz[2]) {
                    if (gameHL == 1 && indexRow == -1) {
                        --gameHL;
                    } else if (gameHL == 1 && indexRow >= 0) {
                        --indexRow;
                    }

                    gameHO.gameAA(indexRow * gameHO.ITEM_SIZE);
                } else if (GameCanvas.keyPressedz[8]) {
                    if (gameHL == 0) {
                        ++gameHL;
                    } else if (gameHL == 1) {
                        if (++indexRow >= gameHM) {
                            indexRow = 0;
                        }

                        gameHO.gameAA(indexRow * gameHO.ITEM_SIZE);
                    }

                    super.left = super.center = null;
                    this.gameBJ();
                } else if (GameCanvas.keyPressedz[4]) {
                    indexRow = -1;
                    if (gameHL == 1 && --indexSelect < 0) {
                        indexSelect = Char.getMyChar().nClass.skillTemplates.length - 1;
                    }

                    super.left = super.center = null;
                    this.gameBJ();
                    scrMain.gameAA(indexSelect * scrMain.ITEM_SIZE);
                    gameHO.gameAA();
                    indexRow = 0;
                } else if (GameCanvas.keyPressedz[6]) {
                    indexRow = -1;
                    if (gameHL == 1 && ++indexSelect >= Char.getMyChar().nClass.skillTemplates.length) {
                        indexSelect = 0;
                    }

                    super.left = super.center = null;
                    this.gameBJ();
                    scrMain.gameAA(indexSelect * scrMain.ITEM_SIZE);
                    gameHO.gameAA();
                    indexRow = 0;
                }
            } else if (indexMenu == 2) {
                if (GameCanvas.keyPressedz[2]) {
                    --gameHL;
                } else if (GameCanvas.keyPressedz[8]) {
                    if (++gameHL >= 5) {
                        gameHL = 1;
                    }

                    super.left = super.center = null;
                    this.gameBJ();
                }
            } else if (indexMenu == 3) {
                if (indexRow < 0) {
                    indexRow = 0;
                }

                if (GameCanvas.keyPressedz[2]) {
                    if (indexRow == 0) {
                        --gameHL;
                        indexRow = -1;
                    } else {
                        --indexRow;
                    }

                    scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                } else if (GameCanvas.keyPressedz[8]) {
                    if (++indexRow >= gameHM) {
                        indexRow = 0;
                    }

                    scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                }
            } else if (indexMenu == 4) {
                var1 = indexSelect;
                if (indexSelect != 11 && indexSelect != 12 && indexSelect != 13 && indexSelect != 14) {
                    if (indexSelect == 9) {
                        if (GameCanvas.keyPressedz[2]) {
                            indexSelect -= 2;
                        } else if (GameCanvas.keyPressedz[8]) {
                            indexSelect = 15;
                        } else if (GameCanvas.keyPressedz[4]) {
                            --indexSelect;
                        } else if (GameCanvas.keyPressedz[6]) {
                            ++indexSelect;
                        }
                    } else if (indexSelect == 10) {
                        if (GameCanvas.keyPressedz[2]) {
                            indexSelect -= 2;
                        } else if (GameCanvas.keyPressedz[4]) {
                            --indexSelect;
                        } else if (GameCanvas.keyPressedz[6] || GameCanvas.keyPressedz[8]) {
                            ++indexSelect;
                        }
                    } else if (indexSelect == 15) {
                        if (GameCanvas.keyPressedz[2]) {
                            indexSelect = 9;
                        } else if (GameCanvas.keyPressedz[4]) {
                            --indexSelect;
                        } else if (GameCanvas.keyPressedz[8] || GameCanvas.keyPressedz[6]) {
                            indexSelect = 0;
                        }
                    } else if (GameCanvas.keyPressedz[2]) {
                        if (indexSelect <= 1) {
                            indexSelect = 0;
                            gameHL = 0;
                        } else {
                            indexSelect -= 2;
                        }
                    } else if (GameCanvas.keyPressedz[8]) {
                        if ((indexSelect += 2) > 15) {
                            indexSelect = 0;
                        }
                    } else if (GameCanvas.keyPressedz[4]) {
                        if (--indexSelect < 0) {
                            indexSelect = 15;
                        }
                    } else if (GameCanvas.keyPressedz[6] && ++indexSelect > 11) {
                        indexSelect = 0;
                    }
                } else if (!GameCanvas.keyPressedz[2] && !GameCanvas.keyPressedz[4]) {
                    if (GameCanvas.keyPressedz[6] || GameCanvas.keyPressedz[8]) {
                        ++indexSelect;
                    }
                } else {
                    --indexSelect;
                }

                if (var1 != indexSelect) {
                    super.left = super.center = null;
                    this.gameBJ();
                }
            } else if (indexMenu == 5) {
                if (GameCanvas.keyPressedz[2]) {
                    if (indexSelect == 4) {
                        indexSelect = 0;
                        --gameHL;
                    } else if (--indexSelect < 0) {
                        indexSelect = 0;
                        --gameHL;
                    }

                    this.gameBJ();
                } else if (GameCanvas.keyPressedz[4]) {
                    if (indexSelect >= 2 && indexSelect != 4) {
                        indexSelect = 4;
                    } else {
                        indexSelect = 0;
                    }

                    this.gameBJ();
                } else if (GameCanvas.keyPressedz[6]) {
                    if (indexSelect < 2) {
                        indexSelect = 4;
                    } else {
                        indexSelect = 2;
                    }

                    this.gameBJ();
                } else if (GameCanvas.keyPressedz[8]) {
                    if (++indexSelect >= 4) {
                        indexSelect = 0;
                    }

                    this.gameBJ();
                }
            } else if (indexMenu == 6) {
                if (gameVT == 0) {
                    if (GameCanvas.keyPressedz[2]) {
                        if (indexSelect == 4) {
                            indexSelect = 0;
                            --gameHL;
                        } else if (--indexSelect < 0) {
                            indexSelect = 0;
                            --gameHL;
                        }

                        this.gameBJ();
                    } else if (GameCanvas.keyPressedz[4]) {
                        if (indexSelect >= 2 && indexSelect != 4) {
                            indexSelect = 4;
                        } else {
                            indexSelect = 0;
                        }

                        this.gameBJ();
                    } else if (GameCanvas.keyPressedz[6]) {
                        if (indexSelect < 2) {
                            indexSelect = 4;
                        } else {
                            indexSelect = 2;
                        }

                        this.gameBJ();
                    } else if (GameCanvas.keyPressedz[8]) {
                        if (++indexSelect >= 4) {
                            indexSelect = 0;
                        }

                        this.gameBJ();
                    }
                } else if (gameVT == 1) {
                    if (GameCanvas.keyPressedz[2]) {
                        --gameHL;
                    } else if (GameCanvas.keyPressedz[8]) {
                        if (++gameHL >= 5) {
                            gameHL = 1;
                        }

                        super.left = super.center = null;
                        this.gameBJ();
                    }
                } else if (GameCanvas.keyPressedz[2]) {
                    if (gameHL == 1 && indexRow == -1) {
                        --gameHL;
                    } else if (gameHL == 1 && indexRow >= 0) {
                        --indexRow;
                    }

                    gameHO.gameAA(indexRow * gameHO.ITEM_SIZE);
                } else if (GameCanvas.keyPressedz[8]) {
                    if (gameHL == 0) {
                        ++gameHL;
                    } else if (gameHL == 1) {
                        if (++indexRow >= gameHM) {
                            indexRow = 0;
                        }

                        gameHO.gameAA(indexRow * gameHO.ITEM_SIZE);
                    }

                    super.left = super.center = null;
                    this.gameBJ();
                } else if (GameCanvas.keyPressedz[4]) {
                    indexRow = -1;
                    if (gameHL == 1 && --indexSelect < 0) {
                        indexSelect = Char.getMyChar().nClass.skillTemplates.length - 1;
                    }

                    super.left = super.center = null;
                    this.gameBJ();
                    scrMain.gameAA(indexSelect * scrMain.ITEM_SIZE);
                    gameHO.gameAA();
                    indexRow = 0;
                } else if (GameCanvas.keyPressedz[6]) {
                    indexRow = -1;
                    if (gameHL == 1 && ++indexSelect >= Char.getMyChar().nClass.skillTemplates.length) {
                        indexSelect = 0;
                    }

                    super.left = super.center = null;
                    this.gameBJ();
                    scrMain.gameAA(indexSelect * scrMain.ITEM_SIZE);
                    gameHO.gameAA();
                    indexRow = 0;
                }
            }

            if (GameCanvas.isTouch) {
                GameScr var3 = this;
                if (!GameCanvas.menu.showMenu && GameCanvas.currentDialog == null) {
                    label696:
                    {
                        if (GameCanvas.isPointerJustRelease && GameCanvas.gameAB(popupX, popupY, popupW, this.gameUB) && (!gameDQ || GameCanvas.w >= 320) && GameCanvas.isPointerClick) {
                            if (GameCanvas.gameAB(gW2 - 90, popupY + 5, 60, 40)) {
                                indexSelect = 0;
                                --indexMenu;
                            }

                            if (GameCanvas.gameAB(gW2 + 20, popupY + 5, 60, 40)) {
                                indexSelect = 0;
                                ++indexMenu;
                            }

                            gameDQ = false;
                            scrMain.gameAA();
                            gameHO.gameAA();
                            if (currentCharViewInfo.charID != Char.getMyChar().charID) {
                                if (indexMenu < 3) {
                                    indexMenu = mResources.gameGG.length - 1;
                                }

                                if (indexMenu > mResources.gameGG.length - 1) {
                                    indexMenu = 3;
                                }
                            } else {
                                if (indexMenu < 0) {
                                    indexMenu = mResources.gameGG.length - 1;
                                }

                                if (indexMenu > mResources.gameGG.length - 1) {
                                    indexMenu = 0;
                                }
                            }

                            gameHL = 1;
                            indexSelect = -1;
                            this.gameCE();
                        }

                        ScrollResult var4;
                        if (gameDQ) {
                            if ((var4 = gameHO.gameAB()).isDowning || var4.isFinish) {
                                indexRow = var4.selected;
                                gameHL = 1;
                            }

                            if (GameCanvas.gameAI) {
                                break label696;
                            }
                        }

                        if (indexMenu == 0) {
                            if ((var4 = scrMain.gameAB()).isDowning || var4.isFinish) {
                                if (indexSelect != var4.selected) {
                                    indexSelect = var4.selected;
                                    super.left = super.center = null;
                                    if (GameCanvas.gameAI) {
                                        this.gameBJ();
                                    } else if (gameAK(3) != null) {
                                        this.gameHQ();
                                    } else {
                                        gameDQ = false;
                                        super.left = this.gameTV;
                                    }
                                }

                                gameHL = 1;
                            }
                        } else if (indexMenu == 1) {
                            if (!(var4 = scrMain.gameAB()).isDowning && !var4.isFinish) {
                                if (((var4 = gameHO.gameAB()).isDowning || var4.isFinish) && indexRow != var4.selected) {
                                    indexRow = var4.selected;
                                }
                            } else {
                                if (indexSelect != var4.selected) {
                                    if ((indexSelect = var4.selected) >= Char.getMyChar().nClass.skillTemplates.length) {
                                        indexSelect = -1;
                                    }

                                    super.left = super.center = null;
                                    this.gameBJ();
                                    gameHO.gameAA();
                                    indexRow = 0;
                                }

                                gameHL = 1;
                            }
                        } else if (indexMenu == 2) {
                            if (GameCanvas.isPointerJustRelease && GameCanvas.gameAB(popupX + 5, popupY + 52, popupW - 10, 130) && GameCanvas.isPointerClick) {
                                var1 = (GameCanvas.py - (popupY + 52)) / 32;
                                ++var1;
                                if (var1 == this.gameRO) {
                                    MyVector var2;
                                    (var2 = new MyVector()).addElement(new Command(mResources.gameDY, 11064));
                                    var2.addElement(new Command(mResources.gameDZ, 11065));
                                    GameCanvas.menu.gameAA(var2);
                                }

                                gameHL = var1;
                                this.gameRO = var1;
                                this.gameBJ();
                            }
                        } else if (indexMenu == 3) {
                            if ((var4 = scrMain.gameAB()).isDowning || var4.isFinish) {
                                indexRow = var4.selected;
                                gameHL = 1;
                            }
                        } else if (indexMenu == 4) {
                            if (GameCanvas.isPointerJustRelease) {
                                gameHL = 1;
                                if (GameCanvas.gameAB(popupX + 4, popupY + 35, gameHK, 130)) {
                                    indexSelect = (GameCanvas.py - (popupY + 35)) / gameHK << 1;
                                    super.left = super.center = null;
                                    this.gameBJ();
                                }

                                if (GameCanvas.gameAB(popupX + popupW - 30, popupY + 35, gameHK, 130)) {
                                    indexSelect = ((GameCanvas.pyLast - (popupY + 35)) / gameHK << 1) + 1;
                                    super.left = super.center = null;
                                    this.gameBJ();
                                }

                                if (GameCanvas.gameAB(popupX + 4, popupY + 165, popupW - 8, gameHK)) {
                                    var1 = (GameCanvas.pxLast - (popupX + 4)) / gameHK;
                                    var1 += 10;
                                    indexSelect = var1;
                                    super.left = super.center = null;
                                    this.gameBJ();
                                }
                            }
                        } else if (indexMenu == 5) {
                            if (GameCanvas.isPointerJustRelease) {
                                for (var1 = 0; var1 < var3.gameNW.length; ++var1) {
                                    if (var1 == 4) {
                                        if (GameCanvas.gameAB(var3.gameNW[var1], var3.gameNX[var1], 84, 75) && GameCanvas.isPointerClick) {
                                            gameHL = 1;
                                            indexSelect = 4;
                                            var3.gameBJ();
                                            if (!GameCanvas.gameAI && var3.center != null) {
                                                var3.gameAB(var3.center.idAction, var3.center.p);
                                            }
                                        }
                                    } else if (GameCanvas.gameAB(var3.gameNW[var1], var3.gameNX[var1], gameHK, gameHK) && GameCanvas.isPointerClick) {
                                        gameHL = 1;
                                        indexSelect = var1;
                                        var3.gameBJ();
                                        if (!GameCanvas.gameAI) {
                                            if (currentCharViewInfo.arrItemMounts[indexSelect] != null) {
                                                var3.gameAB(var3.center.idAction, var3.center.p);
                                            } else {
                                                gameDQ = false;
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (indexMenu == 6) {
                            if (gameVT == 0) {
                                if (GameCanvas.isPointerJustRelease) {
                                    for (var1 = 0; var1 < var3.gameNW.length; ++var1) {
                                        if (var1 == 4) {
                                            if (GameCanvas.gameAB(var3.gameNW[var1], var3.gameNX[var1], 84, 75) && GameCanvas.isPointerClick) {
                                                gameHL = 1;
                                                indexSelect = 4;
                                                var3.gameBJ();
                                                if (!GameCanvas.gameAI && var3.center != null) {
                                                    var3.gameAB(var3.center.idAction, var3.center.p);
                                                }
                                            }
                                        } else if (GameCanvas.gameAB(var3.gameNW[var1], var3.gameNX[var1], gameHK, gameHK) && GameCanvas.isPointerClick) {
                                            gameHL = 1;
                                            indexSelect = var1;
                                            var3.gameBJ();
                                            if (!GameCanvas.gameAI) {
                                                if (currentCharViewInfo.arrItemViThu[indexSelect] != null) {
                                                    var3.gameAB(var3.center.idAction, var3.center.p);
                                                } else {
                                                    gameDQ = false;
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (gameVT == 1) {
                                if (GameCanvas.isPointerJustRelease && GameCanvas.gameAB(popupX + 5, popupY + 52, popupW - 10, 130) && GameCanvas.isPointerClick) {
                                    var1 = (GameCanvas.py - (popupY + 52)) / 32;
                                    ++var1;
                                    if (var1 == this.gameRO) {
                                        gameKU();
                                    }

                                    gameHL = var1;
                                    this.gameRO = var1;
                                    this.gameBJ();
                                }
                                return;
                            } else if (!(var4 = scrMain.gameAB()).isDowning && !(var4 = scrMain.gameAB()).isFinish) {
                                if (((var4 = gameHO.gameAB()).isDowning || (var4 = scrMain.gameAB()).isFinish) && indexRow != (var4 = scrMain.gameAB()).selected) {
                                    indexRow = (var4 = scrMain.gameAB()).selected;
                                }
                            } else {
                                if (indexSelect != (var4 = scrMain.gameAB()).selected) {
                                    if ((indexSelect = (var4 = scrMain.gameAB()).selected) >= Char.getMyChar().nClass.skillTemplates.length) {
                                        indexSelect = -1;
                                    }

                                    super.left = super.center = null;
                                    this.gameBJ();
                                    gameHO.gameAA();
                                    indexRow = 0;
                                }

                                gameHL = 1;
                            }
                        }
                    }
                }
            }

            GameCanvas.gameAI();
            GameCanvas.gameAH();
        }
    }

    private static Member gameCW() {
        return (Member) vClan.elementAt(indexRow);
    }

    private void gameCX() {
        if (!gameDQ) {
            super.left = super.center = null;
            if (indexMenu == 0) {
                if (Char.getMyChar().ctypeClan == 4) {
                    super.left = new Command(mResources.gameST[0], 14004);
                }

                if (Char.getMyChar().ctypeClan == 3) {
                    super.left = new Command(mResources.gameST[1], 14004);
                }

                if (Char.getMyChar().ctypeClan == 2) {
                    super.left = new Command(mResources.gameST[2], 14004);
                }

                if (!Char.getMyChar().cClanName.equals("")) {
                    if (gameHL == 1) {
                        super.center = new Command(mResources.gameBX, 140101);
                        return;
                    }

                    super.center = new Command(mResources.gameSV[3], 14010);
                    return;
                }
            } else if (indexMenu == 1) {
                Member var1;
                if (vClan.size() > 0 && indexRow >= 0 && indexRow < vClan.size() && (var1 = gameCW()) != null) {
                    if (Char.getMyChar().ctypeClan == 4) {
                        super.left = new Command(mResources.gameST[0], 14005);
                    }

                    if (Char.getMyChar().ctypeClan == 3) {
                        super.left = new Command(mResources.gameST[1], 14005);
                    }

                    if (Char.getMyChar().ctypeClan != 4 && Char.getMyChar().ctypeClan != 3) {
                        super.left = new Command(mResources.gameST[4], 14005);
                    }

                    if (!var1.name.equals(Char.getMyChar().cName) && (!gameHR || this.gameRL != 0)) {
                        super.center = new Command(mResources.gameEO, 14006, var1.name);
                        return;
                    }
                }
            } else if (indexMenu == 2 && gameHL == 1) {
                if (Char.clan == null || Char.clan.items == null) {
                    return;
                }

                super.left = new Command(mResources.gameEO, 1508);
                if (!GameCanvas.gameAJ) {
                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 1509);
                }
            }

        }
    }

    private void gameCY() {
        if (isPaintAlert && GameCanvas.currentDialog == null) {
            boolean var1 = false;
            if (GameCanvas.keyPressedz[8]) {
                if (++indexRow >= this.gameNB.size()) {
                    indexRow = 0;
                }

                var1 = true;
            } else if (GameCanvas.keyPressedz[2]) {
                if (--indexRow < 0) {
                    indexRow = this.gameNB.size() - 1;
                }

                var1 = true;
            }

            if (var1) {
                scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                GameCanvas.gameAI();
                GameCanvas.gameAH();
            }

            ScrollResult var2;
            if (GameCanvas.isTouch && ((var2 = scrMain.gameAB()).isDowning || var2.isFinish)) {
                indexRow = var2.selected;
                var1 = true;
            }

            ChatTab var5;
            if (isPaintMessage && !GameCanvas.isTouch && (var5 = ChatManager.gameAD().gameAE()).type == 2 && indexRow == 0) {
                ChatTextField.gameAA().center = new Command(mResources.gameBI, 120051, var5);
            }

            if (var1 && indexRow >= 0 && indexRow < this.gameNB.size()) {
                String var6 = (String) this.gameNB.elementAt(indexRow);
                var1 = false;
                this.gameTC = null;
                this.gameTB = null;
                super.center = null;
                ChatTextField.gameAA().center = null;
                int var7;
                if ((var7 = var6.indexOf("http://")) >= 0) {
                    this.gameTB = var6.substring(var7);
                    super.center = new Command(mResources.gameBS, 12000);
                    if (!GameCanvas.isTouch) {
                        ChatTextField.gameAA().center = new Command(mResources.gameBS, (IActionListener) null, 12000, (Object) null);
                        return;
                    }
                } else if (var6.indexOf("@") >= 0) {
                    var7 = (var6 = var6.substring(2).trim()).indexOf("@");
                    String var3 = var6.substring(var7);
                    boolean var4 = false;
                    int var8;
                    if ((var8 = var3.indexOf(" ")) <= 0) {
                        var8 = var7 + var3.length();
                    } else {
                        var8 += var7;
                    }

                    this.gameTC = var6.substring(var7 + 1, var8);
                    if (!this.gameTC.equals("") && !this.gameTC.equals(Char.getMyChar().cName) && !this.gameTC.equals(mResources.gameVO.substring(0, 5)) && !this.gameTC.equals(mResources.gameVO)) {
                        super.center = new Command(mResources.gameEO, 12009, this.gameTC);
                        if (!GameCanvas.isTouch) {
                            ChatTextField.gameAA().center = new Command(mResources.gameEO, (IActionListener) null, 12009, this.gameTC);
                            return;
                        }
                    } else {
                        this.gameTC = null;
                        super.center = null;
                    }
                }
            }

        }
    }

    private void gameCZ() {
        if (isPaintMessage) {
            boolean var1 = false;
            if (GameCanvas.keyPressedz[4]) {
                var1 = true;
                ChatManager.gameAD().gameAB();
            } else if (GameCanvas.keyPressedz[6]) {
                var1 = true;
                ChatManager.gameAD().gameAA();
            }

            if (var1) {
                this.gameGK();
            }

            if (GameCanvas.isTouch && GameCanvas.isTouch && GameCanvas.gameAB(popupX, popupY, popupW, this.gameUB) && (!gameDQ || GameCanvas.w >= 320) && GameCanvas.isPointerClick && GameCanvas.isPointerJustRelease) {
                if (GameCanvas.gameAB(gW2 - 90, popupY + 5, 60, 40)) {
                    ChatManager.gameAD().gameAB();
                    this.gameGK();
                }

                if (GameCanvas.gameAB(gW2 + 20, popupY + 5, 60, 40)) {
                    ChatManager.gameAD().gameAA();
                    this.gameGK();
                }
            }

        }
    }

    private void gameDA() {
        if (indexRow >= 0 && vCharInMap.size() > 0) {
            if (Char.gameAD(this.cLastFocusID) == indexRow) {
                super.left = new Command(mResources.gameEP, 14002);
                super.center = new Command("Dịch chuyển", 14026);

            } else {
                super.left = new Command(mResources.gameEO, 14003);
                super.center = new Command("Dịch chuyển", 14026);
            }
        } else {
            super.left = super.center = null;
        }
    }

    private void handleMoveAction() {
        if (indexRow >= 0 && indexRow < vCharInMap.size()) {
            Char selectedChar = (Char) vCharInMap.elementAt(indexRow);

            if (selectedChar != null) {
                String targetName = selectedChar.cName;

                Char targetChar = getCharacterByName(targetName);
                if (targetChar != null) {
                    Char.fieldAC(targetChar.cx, targetChar.cy);
                } else {
                    System.out.println("Character with name " + targetName + " not found.");
                }
            }
        }
    }

    private Char getCharacterByName(String name) {
        for (int i = 0; i < vCharInMap.size(); i++) {
            Char c = (Char) vCharInMap.elementAt(i);
            if (c.cName.equals(name)) {
                return c;
            }
        }
        return null;  // Return null if character is not found
    }

    private void gameDB() {
        if (vList.size() > 0 && indexRow >= 0 && gameHM > 0) {
            super.center = new Command(mResources.gameAV, 14024);
            super.left = new Command(mResources.gameAW, 14025);
        } else {
            super.center = null;
            super.left = null;
        }
    }

    private void gameDC() {
        if (vList.size() > 0 && indexRow >= 0 && gameHM > 0) {
            super.center = new Command(mResources.gameEO, 14021);
        } else {
            super.center = null;
        }
    }

    private void gameDD() {
        if (vEnemies.size() > 0 && indexRow >= 0 && gameHM > 0) {
            super.center = new Command(mResources.gameEO, 11078);
        } else {
            super.center = null;
        }
    }

    private void gameDE() {
        if (vFriend.size() > 0 && indexRow >= 0 && gameHM > 0 && indexRow < vFriend.size()) {
            vFriend.elementAt(indexRow);
            super.center = new Command(mResources.gameEO, 11079);
        } else {
            super.center = null;
        }
    }

    private void gameDF() {
        super.center = null;
        if (indexRow != -1) {
            Party var1;
            if (((Party) vParty.elementAt(0)).charId == Char.getMyChar().charID) {
                if ((var1 = (Party) vParty.elementAt(indexRow)).charId != Char.getMyChar().charID) {
                    super.center = new Command(mResources.gameEO, 11080, var1.name);
                    return;
                }
            } else if ((var1 = (Party) vParty.elementAt(indexRow)).charId != Char.getMyChar().charID) {
                super.center = new Command(mResources.gameEO, 12009, var1.name);
            }

        }
    }

    private static boolean gameDG() {
        return gameHU || gameMG || gameDQ || isPaintInfoMe || gameKB
                || gameKC || gameMH || gameKR || gameKD || gameKE || gameKF
                || gameKG || gameKH || gameKI || gameKJ || gameKK || gameKL
                || gameKM || gameKN || gameKO || gameKP || gameKQ || gameKS
                || gameKT || gameKU || gameKV || gameKW || gameDN || gameKX
                || gameMB || gameMJ || gameMK || gameKZ || gameMI || gameMA
                || fieldTF || fieldTG || ItemThrow || isPaintTrade || isPaintAlert
                || gameMD || isPaintAuto || gameHW || gameME || gameHX
                || gameHV || isPaintFriend || gameHY || gameHZ || gameMC
                || isPaintMessage || gameMP || gameML || gameMR || gameMM
                || gameMQ || gameMN || gameMO;
    }

    private boolean gameDH() {
        if (GameCanvas.isTouch && !Char.ischangingMap && !gameMD && !isPaintAuto) {
            if (ChatTextField.gameAA().isShow) {
                return true;
            } else if (super.center == this.cmdDead && GameCanvas.gameAJ) {
                return true;
            } else {
                return GameCanvas.currentDialog != null || ChatPopup.currentMultilineChatPopup != null || GameCanvas.menu.showMenu || gameDG();
            }
        } else {
            return true;
        }
    }

    public static boolean gameBA() {
        return isPaintAuto || gameKB || gameMG || gameKC || gameKR || gameKD || gameKE || gameKF || gameKG || gameKH || gameKI || gameKJ || gameKK || gameKL || gameKM || gameKN || gameKO || gameKP || gameKQ || gameKS || gameKT || gameKU || gameKV || gameKW || gameMH || gameDN || gameKX || gameMB || gameMJ || gameMK || gameKZ || gameMI || gameMA || fieldTF || fieldTG || ItemThrow || isPaintTrade || gameML || gameMR || gameMM || gameMQ || gameMN || gameMO;
    }

    private static boolean gameDI() {
        return isPaintAuto || gameDQ || gameMG || isPaintInfoMe || gameKB || gameKC || gameKR || gameKD || gameKE || gameKF || gameKG || gameKH || gameKI || gameKJ || gameKK || gameKL || gameKM || gameKN || gameKO || gameKP || gameKQ || gameKS || gameKT || gameKU || gameKV || gameKW || gameMH || gameDN || gameKX || gameMB || gameMJ || gameMK || gameKZ || gameMI || gameMA || fieldTF || fieldTG || ItemThrow || isPaintTrade || gameML || gameMR || gameMM || gameMQ || gameMN || gameMO;
    }

    private static boolean gameDJ() {
        return gameHU || isPaintInfoMe && indexMenu == 6 && gameVT != 0 || isPaintInfoMe && indexMenu > 0 && indexMenu < 5 || isPaintAuto || gameMD || gameME && (indexMenu == 0 || indexMenu == 1 || indexMenu == 3 || indexMenu == 4) || gameMC || gameHW || gameHX || isPaintFriend || gameHY || gameHZ || gameHV || isPaintMessage || isPaintAlert || gameMP;
    }

    private static void gameAA(Item var0) {
        Command var1 = new Command(mResources.gameEC, 11055, var0);
        GameCanvas.inputDlg.gameAA(mResources.gameNY, var1, 1);
    }

    private static void gameAB(Item var0) {
        if (var0.quantity > 1) {
            Command var1 = new Command(mResources.gameEC, 110562, var0);
            GameCanvas.inputDlg.gameAA(mResources.gameNY, var1, 1);
        } else {
            GameCanvas.gameAA(mResources.gamePI, new Command(mResources.gameCH, 11061, var0), new Command(mResources.gameCU, 1));
        }
    }

    private static void gameDK() {
        Command var0 = new Command(mResources.gameEC, 11042);
        GameCanvas.inputDlg.gameAA(mResources.gameNX, var0, 1);
    }

    private static void gameDL() {
        Command var0 = new Command(mResources.gameEC, 110361);
        GameCanvas.inputDlg.gameAA(mResources.gameNX, var0, 1);
    }

    private static void gameDM() {
        Command var0 = new Command(mResources.gameEC, 11043);
        GameCanvas.inputDlg.gameAA(mResources.gameNX, var0, 1);
    }

    public final void gameBB() {
        if (GameCanvas.currentDialog == null && gameBA()) {
            GameScr var1;
            int var3;
            int var4;
            int var6;
            int var11;
            if (gameHL == 0) {
                var1 = this;
                super.left = super.center = null;
                if (!gameMG) {
                    if (gameMA) {
                        if (svTitle.equals("")) {
                            if (indexMenu == 0) {
                                super.left = new Command(mResources.gameBA, 11115);
                            } else if (indexMenu == 1) {
                                super.left = new Command(mResources.gameBA, 11116);
                            }
                        } else {
                            super.left = null;
                        }
                    } else if (indexMenu == 1 && isPaintTrade && this.typeTrade == 0) {
                        super.left = this.gameRY;
                    } else if (indexMenu == 1 && !gameKB && !gameKC && !gameKZ && !gameMI && !gameKW && !gameKX && !gameMB && !gameMJ && !gameMK && !isPaintTrade && !gameMH && !gameDN && !gameML && !gameMR && !gameMM && !gameMQ && !gameMN && !gameMO) {
                        super.left = new Command(mResources.gameCD, 110221);
                    }

                    if (gameMI && indexMenu == 0) {
                        var11 = 0;
                        var3 = 0;
                        var4 = 0;
                        short var5 = 0;

                        for (var6 = 0; var6 < arrItemUpPeal.length; ++var6) {
                            Item var7;
                            if ((var7 = arrItemUpPeal[var6]) != null) {
                                if (var7.template.id == 455) {
                                    ++var11;
                                } else if (var7.template.id == 456) {
                                    ++var3;
                                } else if (var7.template.type == 26) {
                                    ++var4;
                                    var5 = var7.template.id;
                                }
                            }
                        }

                        if (var11 >= 9 || var3 >= 9 || var5 >= 10 && var11 >= 3 && var4 == 1 || var5 >= 11 && var3 >= 3 && var4 == 1) {
                            super.left = new Command(mResources.gameFV, 1600);
                        }
                    } else {
                        Item var2;
                        if (gameMJ) {
                            try {
                                if (indexMenu == 0) {
                                    if (gameHL == 1) {
                                        if (itemSplit != null) {
                                            var1.left = new Command(mResources.gameEO, 11103);
                                        } else {
                                            gameDQ = false;
                                        }

                                        if (GameCanvas.gameAJ) {
                                            var1.gameAA((int) 3, (Item) itemSplit);
                                        } else {
                                            var1.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11104, itemSplit);
                                        }
                                    } else if (gameHL == 2) {
                                        if ((var2 = arrItemSplit[indexSelect]) != null) {
                                            if (GameCanvas.gameAJ) {
                                                var1.gameAA((int) 3, (Item) var2);
                                            } else {
                                                var1.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11104, var2);
                                            }

                                            var1.left = new Command(mResources.gameCJ, 1605);
                                        } else {
                                            gameDQ = false;
                                        }

                                        if (itemSplit != null) {
                                            var1.left = new Command(mResources.gameEO, 1604);
                                        }
                                    }
                                } else if (indexMenu == 1) {
                                    if (Char.getMyChar().arrItemBag[indexSelect] == null) {
                                        var1.left = null;
                                        gameDQ = false;
                                    } else {
                                        var1.left = new Command(mResources.gameEO, 11106);
                                    }
                                }
                            } catch (Exception var8) {
                            }
                        } else if (gameMK) {
                            if (indexMenu == 0) {
                                if (gameHL != 1) {
                                    if (gameHL == 2) {
                                        if ((var2 = arrItemSplit[indexSelect]) != null) {
                                            if (GameCanvas.gameAJ) {
                                                this.gameAA((int) 3, (Item) var2);
                                            } else {
                                                super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11104, var2);
                                            }
                                        } else {
                                            gameDQ = false;
                                        }

                                        if (itemSplit != null && itemSplit.upgrade > 13) {
                                            super.left = new Command(mResources.gameEO, 1604);
                                        }
                                    }
                                } else {
                                    if (itemSplit != null && itemSplit.upgrade > 13) {
                                        super.left = new Command(mResources.gameEO, 11103);
                                    } else if (itemSplit != null) {
                                        super.left = this.gameRX;
                                    } else {
                                        gameDQ = false;
                                    }

                                    if (GameCanvas.gameAJ) {
                                        this.gameAA((int) 3, (Item) itemSplit);
                                    } else {
                                        super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11104, itemSplit);
                                    }
                                }
                            } else if (indexMenu == 1) {
                                if (Char.getMyChar().arrItemBag[indexSelect] == null) {
                                    super.left = null;
                                    gameDQ = false;
                                } else {
                                    super.left = new Command(mResources.gameEO, 1606);
                                }
                            }
                        }
                    }

                    if (gameKZ && indexMenu == 0) {
                        var11 = 0;

                        for (var3 = 0; var3 < arrItemUpPeal.length; ++var3) {
                            if (arrItemUpPeal[var3] != null) {
                                ++var11;
                                if (var11 >= 2) {
                                    super.left = new Command(mResources.gameFV, 11062);
                                    break;
                                }
                            }
                        }
                    }

                    if (gameKW && indexMenu == 0 && itemUpGrade != null) {
                        for (var11 = 0; var11 < arrItemUpGrade.length; ++var11) {
                            if (arrItemUpGrade[var11] != null) {
                                super.center = new Command("", 110981);
                                super.left = new Command(mResources.gameFV, 110981);
                                break;
                            }
                        }
                    }

                    if (gameMM && indexMenu == 0 && itemUpGrade != null && itemSplit != null) {
                        for (var11 = 0; var11 < arrItemUpGrade.length; ++var11) {
                            if (arrItemUpGrade[var11] != null) {
                                super.center = new Command("", 341);
                                super.left = new Command(mResources.gameFV, 341);
                                break;
                            }
                        }
                    }

                    if (gameDN && indexMenu == 0 && itemSell != null && this.gameND != null && !this.gameND.getText().equals("") && Char.getMyChar().xu >= 5000) {
                        super.left = new Command(mResources.gameCO, 15002);
                    }

                    if (gameKX && indexMenu == 0 && gameHL == 0) {
                        for (var11 = 0; var11 < arrItemConvert.length; ++var11) {
                            if (arrItemConvert[var11] == null) {
                                var1.left = null;
                                break;
                            }

                            if (var11 == arrItemConvert.length - 1) {
                                var1.left = new Command(mResources.gameFV, 140131);
                            }
                        }
                    }

                    if (gameMB && indexMenu == 0 && itemSplit != null && itemSplit.upgrade > 0) {
                        var1.left = new Command(mResources.gameFV, 11105);
                    }

                    if (isPaintTrade && indexMenu == 0) {
                        if (var1.typeTrade == 0) {
                            var1.left = var1.gameRQ;
                        } else if (var1.typeTrade == 1 && var1.typeTradeOrder >= 1 && (long) var1.timeTrade - System.currentTimeMillis() / 1000L <= 0L) {
                            var1.left = var1.gameRR;
                        }
                    }

                    if (GameCanvas.keyPressedz[8]) {
                        gameHL = 1;
                        indexSelect = 0;
                        indexRow = -1;
                        scrMain.gameAA();
                        gameHO.gameAA();
                        var1.gameBC();
                    }

                    if (GameCanvas.keyPressedz[4]) {
                        indexSelect = 0;
                        indexRow = -1;
                        --indexMenu;
                        scrMain.gameAA();
                        gameHO.gameAA();
                        if (gameMH) {
                            if (indexMenu < 0) {
                                indexMenu = mResources.gameVN.length - 1;
                            }

                            Service.gI().menu((byte) 0, 28, 0, indexMenu);
                            arrItemStands = null;
                            indexSelect = -1;
                        } else if (!gameKC && !isPaintAuto) {
                            if (indexMenu < 0) {
                                if (gameKB) {
                                    indexMenu = mResources.gameHB.length - 1;
                                } else {
                                    indexMenu = 1;
                                }
                            }
                        } else {
                            indexMenu = 0;
                        }

                        var1.left = var1.center = null;
                        if (gameKB) {
                            gameDO();
                        }

                        if (gameKC && arrItemElites == null && indexMenu == 0) {
                            Service.gI().requestItem(35);
                        }
                    }

                    if (GameCanvas.keyPressedz[6]) {
                        indexSelect = 0;
                        indexRow = -1;
                        ++indexMenu;
                        scrMain.gameAA();
                        gameHO.gameAA();
                        if (gameMH) {
                            if (indexMenu > mResources.gameVN.length - 1) {
                                indexMenu = 0;
                            }

                            Service.gI().menu((byte) 0, 28, 0, indexMenu);
                            arrItemStands = null;
                            indexSelect = -1;
                        } else if (!gameKC && !isPaintAuto) {
                            if (gameKB) {
                                if (indexMenu > mResources.gameHB.length - 1) {
                                    indexMenu = 0;
                                }
                            } else if (indexMenu > 1) {
                                indexMenu = 0;
                            }
                        } else {
                            indexMenu = 0;
                        }

                        var1.left = var1.center = null;
                        if (gameKB) {
                            gameDO();
                        }

                        if (gameKC && arrItemElites == null && indexMenu == 0) {
                            Service.gI().requestItem(35);
                        }
                    }
                }
            } else if (gameHL >= 1) {
                if (gameDQ) {
                    if (GameCanvas.keyPressedz[2]) {
                        if (--indexRow < 0) {
                            indexRow = gameHM - 1;
                        }

                        gameHO.gameAA(indexRow * gameHO.ITEM_SIZE);
                    } else if (GameCanvas.keyPressedz[8]) {
                        if (++indexRow >= gameHM) {
                            indexRow = 0;
                        }

                        gameHO.gameAA(indexRow * gameHO.ITEM_SIZE);
                    }
                } else {
                    if (isPaintAuto) {
                        if (GameCanvas.keyPressedz[2]) {
                            if (--indexRow < 0) {
                                indexRow = gameHM;
                            }

                            scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                        } else if (GameCanvas.keyPressedz[4]) {
                            if (indexRow == 0) {
                                if ((Char.aHpValue -= 10) < 10) {
                                    Char.aHpValue = 10;
                                }
                            } else if (indexRow == 1) {
                                if ((Char.aMpValue -= 10) < 10) {
                                    Char.aMpValue = 10;
                                }
                            } else if (indexRow == 2 && (Char.aFoodValue -= 10) <= 0) {
                                Char.aFoodValue = 1;
                            }
                        } else if (GameCanvas.keyPressedz[6]) {
                            if (indexRow == 0) {
                                if ((Char.aHpValue += 10) > 90) {
                                    Char.aHpValue = 90;
                                }
                            } else if (indexRow == 1) {
                                if ((Char.aMpValue += 10) > 90) {
                                    Char.aMpValue = 90;
                                }
                            } else if (indexRow == 2) {
                                if (Char.aFoodValue == 1) {
                                    Char.aFoodValue = 10;
                                } else if ((Char.aFoodValue += 10) > 70) {
                                    Char.aFoodValue = 70;
                                }
                            }
                        } else if (GameCanvas.keyPressedz[8]) {
                            if (++indexRow > gameHM) {
                                indexRow = 0;
                            }

                            scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                        }

                        if (!GameCanvas.isTouch) {
                            GameCanvas.gameAI();
                            GameCanvas.gameAH();
                        }
                    } else if (isPaintTrade && indexMenu == 0) {
                        boolean var9 = false;
                        if (gameHL == 1) {
                            if (GameCanvas.keyPressedz[4]) {
                                if (--indexSelect < 0) {
                                    indexSelect = 11;
                                }

                                super.left = super.center = null;
                                this.gameBC();
                            } else if (GameCanvas.keyPressedz[6]) {
                                if (indexSelect == 2) {
                                    gameHL = 2;
                                    indexSelect = 0;
                                } else if (indexSelect == 5) {
                                    gameHL = 2;
                                    indexSelect = 3;
                                } else if (indexSelect == 8) {
                                    gameHL = 2;
                                    indexSelect = 6;
                                } else if (indexSelect == 11) {
                                    gameHL = 2;
                                    indexSelect = 9;
                                } else if (indexSelect == 14) {
                                    gameHL = 2;
                                    indexSelect = 12;
                                } else if (++indexSelect >= 12) {
                                    indexSelect = 0;
                                }

                                super.left = super.center = null;
                                this.gameBC();
                            } else if (GameCanvas.keyPressedz[8]) {
                                if (indexSelect + 3 <= 11) {
                                    indexSelect += 3;
                                }

                                super.left = super.center = null;
                                this.gameBC();
                            } else if (GameCanvas.keyPressedz[2]) {
                                if (indexSelect >= 0 && indexSelect < 3) {
                                    gameHL = 0;
                                    indexSelect = 0;
                                } else if (indexSelect - 3 >= 0) {
                                    indexSelect -= 3;
                                }

                                super.left = super.center = null;
                                this.gameBC();
                            }
                        } else if (gameHL == 2) {
                            if (GameCanvas.keyPressedz[4]) {
                                if (indexSelect == 0) {
                                    gameHL = 1;
                                    indexSelect = 2;
                                } else if (indexSelect == 3) {
                                    gameHL = 1;
                                    indexSelect = 5;
                                } else if (indexSelect == 6) {
                                    gameHL = 1;
                                    indexSelect = 8;
                                } else if (indexSelect == 9) {
                                    gameHL = 1;
                                    indexSelect = 11;
                                } else if (indexSelect == 12) {
                                    gameHL = 1;
                                    indexSelect = 14;
                                } else if (--indexSelect < 0) {
                                    indexSelect = 11;
                                }

                                super.left = super.center = null;
                                this.gameBC();
                            } else if (GameCanvas.keyPressedz[6]) {
                                if (++indexSelect >= 12) {
                                    indexSelect = 0;
                                }

                                super.left = super.center = null;
                                this.gameBC();
                            } else if (GameCanvas.keyPressedz[8]) {
                                if (indexSelect + 3 <= 11) {
                                    indexSelect += 3;
                                }

                                super.left = super.center = null;
                                this.gameBC();
                            } else if (GameCanvas.keyPressedz[2]) {
                                if (indexSelect >= 0 && indexSelect < 3) {
                                    gameHL = 0;
                                    indexSelect = 0;
                                } else if (indexSelect - 3 >= 0) {
                                    indexSelect -= 3;
                                }

                                super.left = super.center = null;
                                this.gameBC();
                            }
                        }

                        if (!GameCanvas.isTouch) {
                            GameCanvas.gameAI();
                            GameCanvas.gameAH();
                        }
                    } else if (gameMG) {
                        if (GameCanvas.keyPressedz[4]) {
                            if (--indexSelect < 0) {
                                indexSelect = 8;
                            }
                        } else if (GameCanvas.keyPressedz[6]) {
                            if (++indexSelect > 8) {
                                indexSelect = 0;
                            }
                        } else if (GameCanvas.keyPressedz[8]) {
                            if (indexSelect + 3 < 9) {
                                indexSelect += 3;
                            }
                        } else if (GameCanvas.keyPressedz[2] && indexSelect - 3 >= 0) {
                            indexSelect -= 3;
                        }

                        if (!GameCanvas.isTouch) {
                            GameCanvas.gameAI();
                            GameCanvas.gameAH();
                        }
                    } else if (gameDN && indexMenu == 0) {
                        if (GameCanvas.keyPressedz[2]) {
                            if (--gameHL < 0) {
                                gameHL = 0;
                            }
                        } else if (GameCanvas.keyPressedz[8] && ++gameHL > 2) {
                            gameHL = 2;
                        }

                        this.gameBC();
                    } else if (gameMH) {
                        if (GameCanvas.keyPressedz[4]) {
                            if (arrItemStands != null) {
                                if ((indexSelect -= 5) < 0) {
                                    indexSelect = arrItemStands.length - 1;
                                }

                                scrMain.gameAA(indexSelect * scrMain.ITEM_SIZE);
                                this.gameBC();
                            }
                        } else if (GameCanvas.keyPressedz[6]) {
                            if (arrItemStands != null) {
                                if ((indexSelect += 5) >= arrItemStands.length) {
                                    indexSelect = 0;
                                }

                                scrMain.gameAA(indexSelect * scrMain.ITEM_SIZE);
                                this.gameBC();
                            }
                        } else if (GameCanvas.keyPressedz[8]) {
                            if (arrItemStands != null) {
                                if (++indexSelect >= arrItemStands.length) {
                                    indexSelect = 0;
                                }

                                scrMain.gameAA(indexSelect * scrMain.ITEM_SIZE);
                                this.gameBC();
                            }
                        } else if (GameCanvas.keyPressedz[2] && arrItemStands != null) {
                            if (--indexSelect < 0) {
                                gameHL = 0;
                            }

                            scrMain.gameAA(indexSelect * scrMain.ITEM_SIZE);
                            this.gameBC();
                        }

                        GameCanvas.gameAI();
                        GameCanvas.gameAH();
                    } else if ((gameMQ || gameMM || gameKW || gameKX) && gameHL == 1 && indexMenu == 0) {
                        if (GameCanvas.keyPressedz[4]) {
                            if (--indexSelect < 0) {
                                indexSelect = 1;
                            }

                            super.left = super.center = null;
                            this.gameBC();
                        } else if (GameCanvas.keyPressedz[6]) {
                            if (++indexSelect > 1) {
                                indexSelect = 0;
                            }

                            super.left = super.center = null;
                            this.gameBC();
                        } else if (GameCanvas.keyPressedz[8]) {
                            gameHL = 2;
                            indexSelect = 0;
                            super.left = super.center = null;
                            this.gameBC();
                        } else if (GameCanvas.keyPressedz[2]) {
                            gameHL = 0;
                            indexSelect = 0;
                            super.left = super.center = null;
                            this.gameBC();
                        }

                        if (!GameCanvas.isTouch) {
                            GameCanvas.gameAI();
                            GameCanvas.gameAH();
                        }
                    } else if ((gameMB || gameMJ || gameMK || gameML || gameMR) && gameHL == 1 && indexMenu == 0) {
                        if (GameCanvas.keyPressedz[8]) {
                            gameHL = 2;
                            indexSelect = 0;
                            super.left = super.center = null;
                            this.gameBC();
                        } else if (GameCanvas.keyPressedz[2]) {
                            gameHL = 0;
                            indexSelect = 0;
                            super.left = super.center = null;
                            this.gameBC();
                        }

                        GameCanvas.gameAI();
                        GameCanvas.gameAH();
                    } else if ((gameMN || gameMO) && indexMenu == 0) {
                        if (GameCanvas.keyPressedz[2]) {
                            if (--gameHL < 0) {
                                gameHL = 0;
                            }
                        } else if (GameCanvas.keyPressedz[8] && ++gameHL > 1) {
                            gameHL = 1;
                        }

                        this.gameBC();
                    }

                    int var10 = gameDN();
                    if (GameCanvas.keyPressedz[4]) {
                        if (--indexSelect < 0) {
                            indexSelect = var10 - 1;
                        }

                        super.left = super.center = null;
                        this.gameBC();
                        scrMain.gameAA(indexSelect / gameTJ * scrMain.ITEM_SIZE);
                    } else if (GameCanvas.keyPressedz[6]) {
                        if (++indexSelect >= var10) {
                            indexSelect = 0;
                        }

                        super.left = super.center = null;
                        this.gameBC();
                        scrMain.gameAA(indexSelect / gameTJ * scrMain.ITEM_SIZE);
                    } else if (GameCanvas.keyPressedz[8]) {
                        if (indexSelect + gameTJ <= var10 - 1) {
                            indexSelect += gameTJ;
                        }

                        super.left = super.center = null;
                        this.gameBC();
                        scrMain.gameAA(indexSelect / gameTJ * scrMain.ITEM_SIZE);
                    } else if (GameCanvas.keyPressedz[2]) {
                        if (indexSelect >= 0 && indexSelect < gameTJ) {
                            gameHL = 0;
                            indexSelect = 0;
                            if ((gameMM || gameKW || gameMB || gameMJ || gameMK || gameKX || gameML || gameMR) && indexMenu == 0) {
                                gameHL = 1;
                            }
                        } else if (indexSelect - gameTJ >= 0) {
                            indexSelect -= gameTJ;
                        }

                        super.left = super.center = null;
                        this.gameBC();
                        scrMain.gameAA(indexSelect / gameTJ * scrMain.ITEM_SIZE);
                    }
                }
            }

            if (GameCanvas.isTouch) {
                var1 = this;
                if (!GameCanvas.menu.showMenu && GameCanvas.currentDialog == null) {
                    label1492:
                    {
                        if (GameCanvas.isPointerJustRelease && GameCanvas.gameAB(popupX, popupY, popupW, this.gameUB) && (!gameDQ || GameCanvas.w >= 320) && GameCanvas.isPointerClick) {
                            if (GameCanvas.gameAB(gW2 - 80, popupY + 5, 60, 40)) {
                                indexSelect = 0;
                                --indexMenu;
                                this.gameBC();
                            }

                            if (GameCanvas.gameAB(gW2 + 10, popupY + 5, 60, 40)) {
                                indexSelect = 0;
                                ++indexMenu;
                                this.gameBC();
                            }

                            gameHL = 0;
                            if (!gameBA()) {
                                if (indexMenu < 0) {
                                    indexMenu = mResources.gameGG.length - 1;
                                }

                                if (indexMenu > mResources.gameGG.length - 1) {
                                    indexMenu = 0;
                                }
                            } else {
                                if (gameKB) {
                                    if (indexMenu < 0) {
                                        indexMenu = mResources.gameHB.length - 1;
                                    } else if (indexMenu > mResources.gameHB.length - 1) {
                                        indexMenu = 0;
                                    }

                                    gameDO();
                                } else if (gameMH) {
                                    if (indexMenu < 0) {
                                        indexMenu = mResources.gameVN.length - 1;
                                    } else if (indexMenu > mResources.gameVN.length - 1) {
                                        indexMenu = 0;
                                    }

                                    Service.gI().menu((byte) 0, 28, 0, indexMenu);
                                    indexSelect = -1;
                                } else if (!gameKC && !isPaintAuto) {
                                    if (indexMenu < 0) {
                                        indexMenu = 1;
                                    } else if (indexMenu > 1) {
                                        indexMenu = 0;
                                    }
                                } else {
                                    indexMenu = 0;
                                }

                                if (gameKC) {
                                    indexMenu = 0;
                                }
                            }

                            gameDQ = false;
                            scrMain.gameAA();
                        }

                        ScrollResult var14;
                        if (gameDQ) {
                            if ((var14 = gameHO.gameAB()).isDowning || var14.isFinish) {
                                indexRow = var14.selected;
                                gameHL = 1;
                            }

                            if (!GameCanvas.gameAJ) {
                                break label1492;
                            }
                        }

                        if (isPaintAuto) {
                            if (((var14 = scrMain.gameAB()).isFinish || var14.isDowning) && indexSelect != var14.selected) {
                                gameHL = 1;
                                indexRow = var14.selected;
                                this.gameBC();
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 5, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.isAHP = !Char.isAHP;
                                if (typeActive == 1) {
                                    Char.isAHP = false;
                                    InfoMe.gameAA(mResources.gameWF, 20, mFont.tahoma_7_yellow);
                                }

                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 35, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.isAMP = !Char.isAMP;
                                if (typeActive == 1) {
                                    Char.isAMP = false;
                                    InfoMe.gameAA(mResources.gameWF, 20, mFont.tahoma_7_yellow);
                                }

                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 65, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.isAFood = !Char.isAFood;
                                if (typeActive == 1) {
                                    Char.isAFood = false;
                                    InfoMe.gameAA(mResources.gameWF, 20, mFont.tahoma_7_yellow);
                                }

                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 95, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.isABuff = !Char.isABuff;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 125, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldEH = !Char.fieldEH;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 155, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldEI = !Char.fieldEI;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 185, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldEG = !Char.fieldEG;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 215, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.isAPickYen = !Char.isAPickYen;
                                GameCanvas.isPointerDown = false;
                                if (Char.isAPickYen) {
                                    Char.isANoPick = false;
                                }
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 245, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.isAPickYHM = !Char.isAPickYHM;
                                GameCanvas.isPointerDown = false;
                                if (Char.isAPickYHM) {
                                    Char.isANoPick = false;
                                }
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 275, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.isAPickYHMS = !Char.isAPickYHMS;
                                GameCanvas.isPointerDown = false;
                                if (Char.isAPickYHMS) {
                                    Char.isANoPick = false;
                                }
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 305, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldEM = !Char.fieldEM;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 335, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldEN = !Char.fieldEN;
                                GameCanvas.isPointerDown = false;
                                if (Char.fieldEN) {
                                    Char.isANoPick = false;
                                }
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 365, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldEO = !Char.fieldEO;
                                GameCanvas.isPointerDown = false;
                                if (Char.fieldEO) {
                                    Char.fieldEN = true;
                                    Char.isANoPick = false;
                                }
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 395, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldEP = !Char.fieldEP;
                                GameCanvas.isPointerDown = false;
                                if (Char.fieldEP) {
                                    Char.isANoPick = false;
                                }
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 425, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldEQ = !Char.fieldEQ;
                                GameCanvas.isPointerDown = false;
                                if (Char.fieldEQ) {
                                    Char.isANoPick = false;
                                }
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 455, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldER = !Char.fieldER;
                                GameCanvas.isPointerDown = false;
                                if (Char.fieldER) {
                                    Char.isANoPick = false;
                                }
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 485, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldES = !Char.fieldES;
                                GameCanvas.isPointerDown = false;
                                if (Char.fieldES) {
                                    Char.isANoPick = false;
                                }
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 515, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.isANoPick = !Char.isANoPick;
                                GameCanvas.isPointerDown = false;
                                if (Char.isANoPick) {
                                    Char.isAPickYHMS = false;
                                    Char.isAPickYHM = false;
                                    Char.isAPickYen = false;
                                    Char.fieldEN = false;
                                    Char.fieldEO = false;
                                    Char.fieldEP = false;
                                    Char.fieldEQ = false;
                                    Char.fieldER = false;
                                    Char.fieldES = false;
                                }
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 545, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldEU = !Char.fieldEU;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 575, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldEV = !Char.fieldEV;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 605, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldEW = !Char.fieldEW;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 635, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldEX = !Char.fieldEX;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 665, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldEY = !Char.fieldEY;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 695, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldEZ = !Char.fieldEZ;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 725, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.ReConnect = !Char.ReConnect;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 755, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldFB = !Char.fieldFB;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 785, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldFC = !Char.fieldFC;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 815, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldFD = !Char.fieldFD;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 845, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldFE = !Char.fieldFE;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 875, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldFF = !Char.fieldFF;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 905, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldFG = !Char.fieldFG;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 935, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldFH = !Char.fieldFH;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 965, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldFI = !Char.fieldFI;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 995, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.fieldFJ = !Char.fieldFJ;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 1025, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.isUseCL = !Char.isUseCL;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 16, scrMain.yPos + 1055, 16, 16, scrMain) && GameCanvas.isPointerClick) {
                                Char.isBuyCL = !Char.isBuyCL;
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 131, scrMain.yPos + 2, 30, 20, scrMain) && GameCanvas.isPointerClick && Char.isAHP) {
                                GameCanvas.inputDlg.gameAA(mResources.gameWD, new Command(mResources.gameCX, 1511), 1);
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 131, scrMain.yPos + 32, 30, 20, scrMain) && GameCanvas.isPointerClick && Char.isAMP) {
                                GameCanvas.inputDlg.gameAA(mResources.gameWD, new Command(mResources.gameCX, 1512), 1);
                                GameCanvas.isPointerDown = false;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 131, scrMain.yPos + 62, 30, 20, scrMain) && GameCanvas.isPointerClick && Char.isAFood) {
                                MyVector var12;
                                (var12 = new MyVector()).addElement(new Command(mResources.gameWE[0], 15130));
                                var12.addElement(new Command(mResources.gameWE[1], 15131));
                                var12.addElement(new Command(mResources.gameWE[2], 15132));
                                var12.addElement(new Command(mResources.gameWE[3], 15133));
                                var12.addElement(new Command(mResources.gameWE[4], 15134));
                                var12.addElement(new Command(mResources.gameWE[5], 15135));
                                var12.addElement(new Command(mResources.gameWE[6], 15136));
                                var12.addElement(new Command(mResources.gameWE[7], 15137));
                                GameCanvas.menu.gameAA(var12);
                                GameCanvas.menu.gameAC = true;
                            }
                            if (GameCanvas.gameAA(scrMain.xPos + 131, scrMain.yPos + 242, 30, 20, scrMain) && GameCanvas.isPointerClick && Char.isAPickYHM) {
                                MyVector var12;
                                (var12 = new MyVector()).addElement(new Command(mResources.fieldUH[0], 15150));
                                var12.addElement(new Command(mResources.fieldUH[1], 15151));
                                var12.addElement(new Command(mResources.fieldUH[3], 15153));
                                var12.addElement(new Command(mResources.fieldUH[5], 15155));
                                var12.addElement(new Command(mResources.fieldUH[7], 15157));
                                GameCanvas.menu.gameAA(var12);
                                GameCanvas.menu.gameAC = true;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 131, scrMain.yPos + 272, 30, 20, scrMain) && GameCanvas.isPointerClick && Char.isAPickYHMS) {
                                MyVector var12;
                                (var12 = new MyVector()).addElement(new Command(mResources.fieldUI[0], 15161));
                                var12.addElement(new Command(mResources.fieldUI[1], 15162));
                                var12.addElement(new Command(mResources.fieldUI[2], 15163));
                                var12.addElement(new Command(mResources.fieldUI[3], 15164));
                                var12.addElement(new Command(mResources.fieldUI[4], 15165));
                                var12.addElement(new Command(mResources.fieldUI[5], 15166));
                                var12.addElement(new Command(mResources.fieldUI[6], 15167));
                                GameCanvas.menu.gameAA(var12);
                                GameCanvas.menu.gameAC = true;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 131, scrMain.yPos + 302, 30, 20, scrMain) && GameCanvas.isPointerClick && Char.fieldEM) {
                                MyVector var12;
                                (var12 = new MyVector()).addElement(new Command(mResources.fieldUI[3], 15174));
                                var12.addElement(new Command(mResources.fieldUI[4], 15175));
                                var12.addElement(new Command(mResources.fieldUI[5], 15176));
                                var12.addElement(new Command(mResources.fieldUI[6], 15177));
                                var12.addElement(new Command(mResources.fieldUI[7], 15178));
                                var12.addElement(new Command(mResources.fieldUI[8], 15179));
                                var12.addElement(new Command(mResources.fieldUI[9], 151710));
                                var12.addElement(new Command(mResources.fieldUI[10], 151711));
                                var12.addElement(new Command(mResources.fieldUI[11], 151712));
                                GameCanvas.menu.gameAA(var12);
                                GameCanvas.menu.gameAC = true;
                            }

                            if (GameCanvas.gameAA(scrMain.xPos + 131, scrMain.yPos + 332, 30, 20, scrMain) && GameCanvas.isPointerClick && Char.fieldEN) {
                                MyVector var12;
                                (var12 = new MyVector()).addElement(new Command(mResources.fieldUH[0], 15140));
                                var12.addElement(new Command(mResources.fieldUH[1], 15141));
                                var12.addElement(new Command(mResources.fieldUH[2], 15142));
                                var12.addElement(new Command(mResources.fieldUH[3], 15143));
                                var12.addElement(new Command(mResources.fieldUH[4], 15144));
                                var12.addElement(new Command(mResources.fieldUH[5], 15145));
                                var12.addElement(new Command(mResources.fieldUH[6], 15146));
                                var12.addElement(new Command(mResources.fieldUH[7], 15147));
                                GameCanvas.menu.gameAA(var12);
                                GameCanvas.menu.gameAC = true;
                            }
                        } else if (!gameKC && !fieldTF && !fieldTG && !ItemThrow && !gameKB && !gameMA && !gameKU && !gameKV && !gameKS && !gameKT && !gameKD && !gameKE && !gameKF && !gameKG && !gameKH && !gameKI && !gameKJ && !gameKK && !gameKL && !gameKM && !gameKR && !gameKN && !gameKO && !gameKP && !gameKQ) {
                            if (isPaintTrade) {
                                if (indexMenu == 0) {
                                    this.gameAA(popupX + 4, popupY + this.gameUB + 15, 3, 4, 1);
                                    this.gameAA(popupX + popupW - 3 - 3 * gameHK, popupY + this.gameUB + 15, 3, 4, 2);
                                } else if (indexMenu == 1 && ((var14 = scrMain.gameAB()).isFinish || var14.isDowning) && indexSelect != var14.selected) {
                                    gameHL = 1;
                                    indexSelect = var14.selected;
                                    this.gameBC();
                                }
                            } else if (!gameKZ && !gameMI) {
                                if (!gameMB && !gameMJ && !gameMK && !gameML) {
                                    if (!gameKW && !gameKX && !gameMM) {
                                        if (gameMQ) {
                                            if (indexMenu == 0) {
                                                if (((var14 = scrMain.gameAB()).isFinish || var14.isDowning) && indexSelect != var14.selected) {
                                                    gameHL = 1;
                                                    indexSelect = var14.selected;
                                                    super.left = super.center = null;
                                                    gameDQ = false;
                                                    this.gameBC();
                                                }
                                            } else if (indexMenu == 1 && ((var14 = scrMain.gameAB()).isFinish || var14.isDowning) && indexSelect != var14.selected) {
                                                gameHL = 1;
                                                indexSelect = var14.selected;
                                                this.gameBC();
                                            }
                                        } else if (gameDN) {
                                            if (indexMenu == 0) {
                                                if (GameCanvas.gameAB(popupX + 75, popupY + 69, gameHK, gameHK)) {
                                                    if (GameCanvas.isPointerClick && GameCanvas.isPointerJustRelease) {
                                                        gameHL = 1;
                                                        this.gameBC();
                                                    }
                                                } else if (GameCanvas.gameAB(this.gameND.x, this.gameND.y, this.gameND.width, this.gameND.height) && GameCanvas.isPointerClick && GameCanvas.isPointerJustRelease) {
                                                    gameHL = 2;
                                                    this.gameND.update();
                                                    this.gameBC();
                                                }
                                            } else if (indexMenu == 1 && ((var14 = scrMain.gameAB()).isFinish || var14.isDowning) && indexSelect != var14.selected) {
                                                gameHL = 1;
                                                indexSelect = var14.selected;
                                                this.gameBC();
                                            }
                                        } else if (gameMG) {
                                            if (GameCanvas.gameAB(gameTD, gameTE, 120, 120) && GameCanvas.isPointerJustRelease && GameCanvas.isPointerClick) {
                                                indexSelect = (GameCanvas.pxLast - gameTD) / 40 + (GameCanvas.pyLast - gameTE) / 40 * 3;
                                                gameHL = 1;
                                                this.gameBC();
                                                this.gameDY();
                                            }
                                        } else if (!gameMN && !gameMO) {
                                            if (gameMR) {
                                                if (indexMenu == 1 && ((var14 = scrMain.gameAB()).isFinish || var14.isDowning) && indexSelect != var14.selected) {
                                                    gameHL = 1;
                                                    indexSelect = var14.selected;
                                                    this.gameBC();
                                                }

                                                if (indexMenu == 0) {
                                                    for (var11 = 0; var11 < var1.gameVP.length; ++var11) {
                                                        if (GameCanvas.gameAB(var1.gameVP[var11], var1.gameVQ[var11], gameHK, gameHK) && GameCanvas.isPointerClick && GameCanvas.isPointerJustRelease) {
                                                            var1.gameVR = var11;
                                                        }
                                                    }
                                                }
                                            }
                                        } else if (indexMenu == 0) {
                                            if (GameCanvas.gameAB(popupX + 75, popupY + 69, gameHK, gameHK) && GameCanvas.isPointerClick && GameCanvas.isPointerJustRelease) {
                                                gameHL = 1;
                                                this.gameBC();
                                            }
                                        } else if (indexMenu == 1 && ((var14 = scrMain.gameAB()).isFinish || var14.isDowning) && indexSelect != var14.selected) {
                                            gameHL = 1;
                                            indexSelect = var14.selected;
                                            this.gameBC();
                                        }
                                    } else if (indexMenu == 0) {
                                        var3 = popupX + 45;
                                        var4 = popupY + 32;
                                        int var13 = popupX + 100;
                                        if (GameCanvas.isPointerJustRelease) {
                                            if (GameCanvas.gameAB(var3, var4, 29, 29)) {
                                                gameHL = 1;
                                                indexSelect = 0;
                                                this.gameBC();
                                            }

                                            if (GameCanvas.gameAB(var13, var4, 29, 29)) {
                                                gameHL = 1;
                                                indexSelect = 1;
                                                this.gameBC();
                                                if (gameKZ && indexMenu == 0) {
                                                    for (var6 = 0; var6 < arrItemUpPeal.length; ++var6) {
                                                        if (arrItemUpPeal[var6] != null) {
                                                            super.center = new Command(mResources.gameEC, 11062);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }

                                            if (GameCanvas.gameAB(popupX, popupY + 2 * this.gameUB + 5, popupW, popupH - this.gameUB * 3)) {
                                                this.gameAA(popupX, popupY + 2 * this.gameUB + 5, 6, 3, 2);
                                            }
                                        }
                                    } else if (indexMenu == 1 && ((var14 = scrMain.gameAB()).isFinish || var14.isDowning) && indexSelect != var14.selected) {
                                        gameHL = 1;
                                        indexSelect = var14.selected;
                                        this.gameBC();
                                    }
                                } else if (indexMenu == 0) {
                                    if (GameCanvas.gameAB(popupX + 74, gameTE - gameHK - 3, gameHK, gameHK)) {
                                        gameHL = 1;
                                        indexSelect = 0;
                                        this.gameBC();
                                    }

                                    this.gameAA(popupX + 4, popupY + 2 * this.gameUB + 5, 6, 4, 2);
                                } else if (indexMenu == 1 && ((var14 = scrMain.gameAB()).isFinish || var14.isDowning) && indexSelect != var14.selected) {
                                    gameHL = 1;
                                    indexSelect = var14.selected;
                                    this.gameBC();
                                }
                            } else if (indexMenu == 0) {
                                this.gameAA(popupX + 4, popupY + this.gameUB + 3, 6, 4, 1);
                            } else if (indexMenu == 1 && ((var14 = scrMain.gameAB()).isFinish || var14.isDowning) && indexSelect != var14.selected) {
                                gameHL = 1;
                                indexSelect = var14.selected;
                                this.gameBC();
                            }
                        } else if (((var14 = scrMain.gameAB()).isFinish || var14.isDowning) && indexSelect != var14.selected) {
                            gameHL = 1;
                            indexSelect = var14.selected;
                            super.left = super.center = null;
                            gameDQ = false;
                            this.gameBC();
                        }
                    }
                }
            }

            GameCanvas.gameAI();
            GameCanvas.gameAH();
        }
    }

    private static int gameDN() {
        int var0 = 0;

        try {
            if (gameKC) {
                if (arrItemElites.length % gameTJ == 0) {
                    var0 = arrItemElites.length;
                } else {
                    var0 = (arrItemElites.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKB) {
                if (indexMenu == 0) {
                    if (arrItemStore.length % gameTJ == 0) {
                        var0 = arrItemStore.length;
                    } else {
                        var0 = (arrItemStore.length / gameTJ + 1) * gameTJ;
                    }
                } else if (indexMenu == 1) {
                    if (arrItemBook.length % gameTJ == 0) {
                        var0 = arrItemBook.length;
                    } else {
                        var0 = (arrItemBook.length / gameTJ + 1) * gameTJ;
                    }
                } else if (indexMenu == 2) {
                    if (arrItemFashion.length % gameTJ == 0) {
                        var0 = arrItemFashion.length;
                    } else {
                        var0 = (arrItemFashion.length / gameTJ + 1) * gameTJ;
                    }
                } else if (indexMenu == 52) {
                    if (arrItemFashion.length % gameTJ == 0) {
                        var0 = arrItemFashion.length;
                    } else {
                        var0 = (arrItemFashion.length / gameTJ + 1) * gameTJ;
                    }
                }
            } else if (gameKD) {
                if (arrItemNonNam.length % gameTJ == 0) {
                    var0 = arrItemNonNam.length;
                } else {
                    var0 = (arrItemNonNam.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKE) {
                if (arrItemNonNu.length % gameTJ == 0) {
                    var0 = arrItemNonNu.length;
                } else {
                    var0 = (arrItemNonNu.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKF) {
                if (arrItemAoNam.length % gameTJ == 0) {
                    var0 = arrItemAoNam.length;
                } else {
                    var0 = (arrItemAoNam.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKG) {
                if (arrItemAoNu.length % gameTJ == 0) {
                    var0 = arrItemAoNu.length;
                } else {
                    var0 = (arrItemAoNu.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKH) {
                if (arrItemGangTayNam.length % gameTJ == 0) {
                    var0 = arrItemGangTayNam.length;
                } else {
                    var0 = (arrItemGangTayNam.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKI) {
                if (arrItemGangTayNu.length % gameTJ == 0) {
                    var0 = arrItemGangTayNu.length;
                } else {
                    var0 = (arrItemGangTayNu.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKJ) {
                if (arrItemQuanNam.length % gameTJ == 0) {
                    var0 = arrItemQuanNam.length;
                } else {
                    var0 = (arrItemQuanNam.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKK) {
                if (arrItemQuanNu.length % gameTJ == 0) {
                    var0 = arrItemQuanNu.length;
                } else {
                    var0 = (arrItemQuanNu.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKL) {
                if (arrItemGiayNam.length % gameTJ == 0) {
                    var0 = arrItemGiayNam.length;
                } else {
                    var0 = (arrItemGiayNam.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKM) {
                if (arrItemGiayNu.length % gameTJ == 0) {
                    var0 = arrItemGiayNu.length;
                } else {
                    var0 = (arrItemGiayNu.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKN) {
                if (arrItemLien.length % gameTJ == 0) {
                    var0 = arrItemLien.length;
                } else {
                    var0 = (arrItemLien.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKO) {
                if (arrItemNhan.length % gameTJ == 0) {
                    var0 = arrItemNhan.length;
                } else {
                    var0 = (arrItemNhan.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKP) {
                if (arrItemNgocBoi.length % gameTJ == 0) {
                    var0 = arrItemNgocBoi.length;
                } else {
                    var0 = (arrItemNgocBoi.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKQ) {
                if (arrItemPhu.length % gameTJ == 0) {
                    var0 = arrItemPhu.length;
                } else {
                    var0 = (arrItemPhu.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKR) {
                if (arrItemWeapon.length % gameTJ == 0) {
                    var0 = arrItemWeapon.length;
                } else {
                    var0 = (arrItemWeapon.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKS) {
                if (arrItemStack.length % gameTJ == 0) {
                    var0 = arrItemStack.length;
                } else {
                    var0 = (arrItemStack.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKT) {
                if (arrItemStackLock.length % gameTJ == 0) {
                    var0 = arrItemStackLock.length;
                } else {
                    var0 = (arrItemStackLock.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKU) {
                if (arrItemGrocery.length % gameTJ == 0) {
                    var0 = arrItemGrocery.length;
                } else {
                    var0 = (arrItemGrocery.length / gameTJ + 1) * gameTJ;
                }
            } else if (gameKV) {
                if (arrItemGroceryLock.length % gameTJ == 0) {
                    var0 = arrItemGroceryLock.length;
                } else {
                    var0 = (arrItemGroceryLock.length / gameTJ + 1) * gameTJ;
                }
            }

            if (gameMA) {
                var0 = Char.getMyChar().arrItemBox.length;
            }

            if (indexMenu == 1 && !gameKB) {
                var0 = Char.getMyChar().arrItemBag.length;
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        if ((gameKZ || gameMI || gameMB || gameMJ || gameMK || gameML || gameMR) && indexMenu == 0) {
            var0 = 24;
        } else if ((gameMM || gameKW || gameKX) && indexMenu == 0) {
            var0 = 18;
        } else if (var0 < 30) {
            var0 = 30;
        }

        return var0;
    }

    private static void gameDO() {
        if (indexMenu == 0) {
            Service.gI().requestItem(14);
        } else if (indexMenu == 1) {
            Service.gI().requestItem(15);
        } else if (indexMenu == 2) {
            Service.gI().requestItem(32);
        } else if (indexMenu == 3) {
            Service.gI().requestItem(34);
        } else {
            if (indexMenu == 52) {
                Service.gI().requestItem(32);
            }

        }
    }

    private void gameDP() {
        if (Char.getMyChar().arrItemBag[indexSelect].quantity > 1) {
            super.left = new Command(mResources.gameCO, 11072);
        } else {
            super.left = new Command(mResources.gameCO, 11073);
        }
    }

    public final void gameBC() {
        super.left = super.center = null;
        if (indexSelect >= 0) {
            if (isPaintAuto) {
                if (gameHL == 1 && !GameCanvas.isTouch) {
                    super.left = new Command(mResources.gameEO, 1510);
                }

            } else if (gameMG) {
                if (gameHL == 1) {
                    super.left = new Command(mResources.gameEO, 1506);
                    super.center = new Command("", 1507);
                }

            } else if (gameKC) {
                if (indexMenu == 0 && gameAK(35) != null) {
                    super.left = this.gameSF;
                    if (!GameCanvas.gameAJ) {
                        super.center = this.gameSG;
                        return;
                    }

                    this.gameAD((byte) 35);
                }

            } else if (gameKB) {
                if (indexMenu == 52) {
                    if (gameAK(32) != null) {
                        super.left = this.gameSN;
                        if (!GameCanvas.gameAJ) {
                            super.center = this.gameSO;
                            return;
                        }

                        this.gameAD((byte) 32);
                        return;
                    }
                } else if (indexMenu == 0) {
                    if (gameAK(14) != null) {
                        super.left = this.gameSH;
                        if (!GameCanvas.gameAJ) {
                            super.center = this.gameSI;
                            return;
                        }

                        this.gameAD((byte) 14);
                        return;
                    }
                } else if (indexMenu == 1) {
                    if (gameAK(15) != null) {
                        super.left = this.gameSL;
                        if (!GameCanvas.gameAJ) {
                            super.center = this.gameSM;
                            return;
                        }

                        this.gameAD((byte) 15);
                        return;
                    }
                } else if (indexMenu == 2) {
                    if (gameAK(32) != null) {
                        super.left = this.gameSN;
                        if (!GameCanvas.gameAJ) {
                            super.center = this.gameSO;
                            return;
                        }

                        this.gameAD((byte) 32);
                        return;
                    }
                } else if (indexMenu == 3 && gameAK(34) != null) {
                    super.left = this.gameSJ;
                    if (!GameCanvas.gameAJ) {
                        super.center = this.gameSK;
                        return;
                    }

                    this.gameAD((byte) 34);
                }

            } else {
                if (gameKD) {
                    if (indexMenu == 0) {
                        if (gameAK(20) != null) {
                            super.left = this.gameSP;
                            if (!GameCanvas.gameAJ) {
                                super.center = this.gameSQ;
                            } else {
                                this.gameAD((byte) 20);
                            }
                        }
                    } else if (indexMenu == 1 && Char.getMyChar().arrItemBag[indexSelect] != null) {
                        this.gameDP();
                    }
                } else if (gameKE) {
                    if (indexMenu == 0) {
                        if (gameAK(21) != null) {
                            super.left = this.gameSR;
                            if (!GameCanvas.gameAJ) {
                                super.center = this.gameSS;
                            } else {
                                this.gameAD((byte) 21);
                            }
                        }
                    } else if (indexMenu == 1 && Char.getMyChar().arrItemBag[indexSelect] != null) {
                        this.gameDP();
                    }
                } else if (gameKF) {
                    if (indexMenu == 0) {
                        if (gameAK(22) != null) {
                            super.left = this.gameST;
                            if (!GameCanvas.gameAJ) {
                                super.center = this.gameSU;
                            } else {
                                this.gameAD((byte) 22);
                            }
                        }
                    } else if (indexMenu == 1 && Char.getMyChar().arrItemBag[indexSelect] != null) {
                        this.gameDP();
                    }
                } else if (gameKG) {
                    if (indexMenu == 0) {
                        if (gameAK(23) != null) {
                            super.left = this.gameSV;
                            if (!GameCanvas.gameAJ) {
                                super.center = this.gameSW;
                            } else {
                                this.gameAD((byte) 23);
                            }
                        }
                    } else if (indexMenu == 1 && Char.getMyChar().arrItemBag[indexSelect] != null) {
                        this.gameDP();
                    }
                } else if (gameKH) {
                    if (indexMenu == 0) {
                        if (gameAK(24) != null) {
                            super.left = this.gameSX;
                            if (!GameCanvas.gameAJ) {
                                super.center = this.gameSY;
                            } else {
                                this.gameAD((byte) 24);
                            }
                        }
                    } else if (indexMenu == 1 && Char.getMyChar().arrItemBag[indexSelect] != null) {
                        this.gameDP();
                    }
                } else if (gameKI) {
                    if (indexMenu == 0) {
                        if (gameAK(25) != null) {
                            super.left = this.gameSZ;
                            if (!GameCanvas.gameAJ) {
                                super.center = this.gameTA;
                            } else {
                                this.gameAD((byte) 25);
                            }
                        }
                    } else if (indexMenu == 1 && Char.getMyChar().arrItemBag[indexSelect] != null) {
                        this.gameDP();
                    }
                } else {
                    Item var1;
                    if (gameKJ) {
                        if (indexMenu == 0) {
                            if ((var1 = gameAK(26)) != null) {
                                super.left = new Command(mResources.gameCS, 11092, var1);
                                if (!GameCanvas.gameAJ) {
                                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11088, var1);
                                } else {
                                    this.gameAA((int) 26, (Item) var1);
                                }
                            }
                        } else if (indexMenu == 1 && Char.getMyChar().arrItemBag[indexSelect] != null) {
                            this.gameDP();
                        }
                    } else if (gameKK) {
                        if (indexMenu == 0) {
                            if ((var1 = gameAK(27)) != null) {
                                super.left = new Command(mResources.gameCS, 11092, var1);
                                if (!GameCanvas.gameAJ) {
                                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11089);
                                } else {
                                    this.gameAA((int) 27, (Item) var1);
                                }
                            }
                        } else if (indexMenu == 1 && Char.getMyChar().arrItemBag[indexSelect] != null) {
                            this.gameDP();
                        }
                    } else if (gameKL) {
                        if (indexMenu == 0) {
                            if ((var1 = gameAK(28)) != null) {
                                super.left = new Command(mResources.gameCS, 11092, var1);
                                if (!GameCanvas.gameAJ) {
                                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11090);
                                } else {
                                    this.gameAA((int) 28, (Item) var1);
                                }
                            }
                        } else if (indexMenu == 1 && Char.getMyChar().arrItemBag[indexSelect] != null) {
                            this.gameDP();
                        }
                    } else if (gameKM) {
                        if (indexMenu == 0) {
                            if ((var1 = gameAK(29)) != null) {
                                super.left = new Command(mResources.gameCS, 11092, var1);
                                if (!GameCanvas.gameAJ) {
                                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11091);
                                } else {
                                    this.gameAA((int) 29, (Item) var1);
                                }
                            }
                        } else if (indexMenu == 1 && Char.getMyChar().arrItemBag[indexSelect] != null) {
                            this.gameDP();
                        }
                    } else if (gameKN) {
                        if (indexMenu == 0) {
                            if ((var1 = gameAK(16)) != null) {
                                super.left = new Command(mResources.gameCS, 11092, var1);
                                if (GameCanvas.gameAJ) {
                                    this.gameAA((int) 16, (Item) var1);
                                } else {
                                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 110923);
                                }
                            } else {
                                gameDQ = false;
                            }
                        } else if (indexMenu == 1 && Char.getMyChar().arrItemBag[indexSelect] != null) {
                            this.gameDP();
                        }
                    } else if (gameKO) {
                        if (indexMenu == 0) {
                            if ((var1 = gameAK(17)) != null) {
                                super.left = new Command(mResources.gameCS, 11092, var1);
                                if (GameCanvas.gameAJ) {
                                    this.gameAA((int) 17, (Item) var1);
                                } else {
                                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 110924);
                                }
                            } else {
                                gameDQ = false;
                            }
                        } else if (indexMenu == 1 && Char.getMyChar().arrItemBag[indexSelect] != null) {
                            this.gameDP();
                        }
                    } else if (gameKP) {
                        if (indexMenu == 0) {
                            if ((var1 = gameAK(18)) != null) {
                                super.left = new Command(mResources.gameCS, 11092, var1);
                                if (GameCanvas.gameAJ) {
                                    this.gameAA((int) 18, (Item) var1);
                                } else {
                                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 110925);
                                }
                            } else {
                                gameDQ = false;
                            }
                        } else if (indexMenu == 1 && Char.getMyChar().arrItemBag[indexSelect] != null) {
                            this.gameDP();
                        }
                    } else if (gameKQ) {
                        if (indexMenu == 0) {
                            if ((var1 = gameAK(19)) != null) {
                                super.left = new Command(mResources.gameCS, 11092, var1);
                                if (GameCanvas.gameAJ) {
                                    this.gameAA((int) 19, (Item) var1);
                                } else {
                                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 110926);
                                }
                            } else {
                                gameDQ = false;
                            }
                        } else if (indexMenu == 1 && Char.getMyChar().arrItemBag[indexSelect] != null) {
                            this.gameDP();
                        }
                    } else if (gameKR) {
                        if (indexMenu == 0) {
                            if ((var1 = gameAK(2)) != null) {
                                super.left = new Command(mResources.gameCS, 11092, var1);
                                if (GameCanvas.gameAJ) {
                                    this.gameAA((int) 2, (Item) var1);
                                } else {
                                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11093);
                                }
                            } else {
                                gameDQ = false;
                            }
                        } else if (indexMenu == 1 && Char.getMyChar().arrItemBag[indexSelect] != null) {
                            this.gameDP();
                        }
                    } else if (gameKS) {
                        if (indexMenu == 0) {
                            if ((var1 = gameAK(6)) != null) {
                                super.left = new Command(mResources.gameCS, 11092, var1);
                                if (GameCanvas.gameAJ) {
                                    this.gameAA((int) 6, (Item) var1);
                                } else {
                                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11094);
                                }
                            }
                        } else if (indexMenu == 1) {
                            if (Char.getMyChar().arrItemBag[indexSelect] != null) {
                                this.gameDP();
                            } else {
                                super.left = this.gameTV;
                            }
                        }
                    } else if (gameKT) {
                        if (indexMenu == 0) {
                            if ((var1 = gameAK(7)) != null) {
                                super.left = new Command(mResources.gameCS, 11092, var1);
                                if (GameCanvas.gameAJ) {
                                    this.gameAA((int) 7, (Item) var1);
                                } else {
                                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11095);
                                }
                            }
                        } else if (indexMenu == 1) {
                            if (Char.getMyChar().arrItemBag[indexSelect] != null) {
                                this.gameDP();
                            } else {
                                super.left = this.gameTV;
                            }
                        }
                    } else if (gameKU) {
                        if (indexMenu == 0) {
                            if ((var1 = gameAK(8)) != null) {
                                super.left = new Command(mResources.gameCS, 11092, var1);
                                if (GameCanvas.gameAJ) {
                                    this.gameAA((int) 8, (Item) var1);
                                } else {
                                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11096);
                                }
                            }
                        } else if (indexMenu == 1) {
                            if (Char.getMyChar().arrItemBag[indexSelect] != null) {
                                this.gameDP();
                            } else {
                                super.left = this.gameTV;
                            }
                        }
                    } else if (gameKV) {
                        if (indexMenu == 0) {
                            if ((var1 = gameAK(9)) != null) {
                                super.left = new Command(mResources.gameCS, 11092, var1);
                                if (GameCanvas.gameAJ) {
                                    this.gameAA((int) 9, (Item) var1);
                                } else {
                                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11097);
                                }
                            }
                        } else if (indexMenu == 1) {
                            if (Char.getMyChar().arrItemBag[indexSelect] != null) {
                                this.gameDP();
                            } else {
                                super.left = this.gameTV;
                            }
                        }
                    } else {
                        int var2;
                        if (gameKW) {
                            if (indexMenu == 0) {
                                if (gameHL == 1) {
                                    if (itemUpGrade != null) {
                                        if (indexSelect == 0) {
                                            super.left = new Command(mResources.gameEO, 11098);
                                            if (GameCanvas.gameAJ) {
                                                gameHQ = false;
                                                this.gameAA((int) 3, (Item) itemUpGrade);
                                            } else {
                                                super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11099);
                                            }
                                        } else if (indexSelect == 1 && !itemUpGrade.isUpMax()) {
                                            if (GameCanvas.gameAJ) {
                                                gameHQ = true;
                                                this.gameAA((int) 3, (Item) itemUpGrade);
                                            } else {
                                                super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 110991);
                                            }
                                        }
                                    } else {
                                        gameDQ = false;
                                    }
                                } else if (gameHL == 2) {
                                    var1 = gameAK(10);
                                    gameHQ = false;
                                    if (var1 != null) {
                                        super.left = new Command(mResources.gameEO, 11100);
                                        if (GameCanvas.gameAJ) {
                                            this.gameAA((int) 3, (Item) var1);
                                        } else {
                                            super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11101);
                                        }
                                    } else {
                                        super.left = null;
                                        gameDQ = false;
                                        if (itemUpGrade != null) {
                                            for (var2 = 0; var2 < arrItemUpGrade.length; ++var2) {
                                                if (arrItemUpGrade[var2] != null) {
                                                    super.left = new Command(mResources.gameFV, 110981);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (indexMenu == 1) {
                                if (Char.getMyChar().arrItemBag[indexSelect] != null) {
                                    super.left = new Command(mResources.gameEO, 11102);
                                } else {
                                    super.left = null;
                                }
                            }
                        } else {
                            int var3;
                            if (gameKX) {
                                if (indexMenu == 0) {
                                    if (gameHL == 1) {
                                        if (indexSelect == 0) {
                                            if (arrItemConvert[0] != null) {
                                                super.left = new Command(mResources.gameEO, 14013);
                                                if (GameCanvas.gameAJ) {
                                                    this.gameAA((int) 3, (Item) arrItemConvert[indexSelect]);
                                                } else {
                                                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 14016);
                                                }
                                            }
                                        } else if (indexSelect == 1) {
                                            if (arrItemConvert[1] != null) {
                                                super.left = new Command(mResources.gameEO, 14013);
                                                if (GameCanvas.gameAJ) {
                                                    this.gameAA((int) 3, (Item) arrItemConvert[indexSelect]);
                                                } else {
                                                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 14016);
                                                }
                                            }
                                        } else {
                                            gameDQ = false;
                                        }
                                    } else if (gameHL == 2) {
                                        var1 = null;
                                        if ((var2 = indexSelect + 2) <= arrItemConvert.length - 1) {
                                            var1 = arrItemConvert[var2];
                                        }

                                        if (var1 != null) {
                                            super.left = new Command(mResources.gameCJ, 140151);
                                            if (GameCanvas.gameAJ) {
                                                this.gameAA((int) 3, (Item) var1);
                                            } else {
                                                super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 140161);
                                            }
                                        } else {
                                            super.left = new Command(mResources.gameFV, 140131);

                                            for (var3 = 0; var3 < arrItemConvert.length; ++var3) {
                                                if (arrItemConvert[var3] == null) {
                                                    super.left = null;
                                                    break;
                                                }
                                            }

                                            gameDQ = false;
                                        }
                                    }
                                } else if (indexMenu == 1) {
                                    if (Char.getMyChar().arrItemBag[indexSelect] != null) {
                                        super.left = new Command(mResources.gameEO, 14012);
                                    } else {
                                        super.left = null;
                                    }
                                }
                            } else {
                                int var13;
                                if (gameDN) {
                                    if (indexMenu == 0) {
                                        if (gameHL == 2) {
                                            this.gameND.isFocus = true;
                                            super.right = this.gameND.cmdClear;
                                        } else {
                                            this.gameND.isFocus = false;
                                            super.right = this.gameSE;
                                        }

                                        var13 = 0;

                                        try {
                                            var13 = Integer.parseInt(this.gameND.getText());
                                        } catch (Exception var9) {
                                        }

                                        if (itemSell != null && var13 > 0 && Char.getMyChar().xu >= 5000) {
                                            super.left = new Command(mResources.gameCO, 15002);
                                        }

                                        if (gameHL == 1 && itemSell != null) {
                                            super.left = new Command(mResources.gameEO, 1500);
                                            if (GameCanvas.gameAJ) {
                                                this.gameAA((int) 3, (Item) itemSell);
                                            } else {
                                                super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 1501);
                                            }
                                        }
                                    } else if (indexMenu == 1) {
                                        super.right = this.gameSE;
                                        if (Char.getMyChar().arrItemBag[indexSelect] != null) {
                                            super.left = new Command(mResources.gameEO, 1503);
                                        } else {
                                            super.left = null;
                                            gameDQ = false;
                                        }
                                    }
                                } else {
                                    if (gameMH) {
                                        if (gameHL == 1 && arrItemStands != null && indexSelect >= 0 && indexSelect < arrItemStands.length && arrItemStands[indexSelect] != null) {
                                            super.left = new Command(mResources.gameEO, 1504);
                                            if (GameCanvas.gameAJ) {
                                                this.gameAB(1505, (Object) null);
                                                return;
                                            }

                                            super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 1505);
                                        }

                                        return;
                                    }

                                    if (gameMB) {
                                        if (indexMenu == 0) {
                                            if (gameHL == 1) {
                                                if (itemSplit != null && itemSplit.upgrade > 0) {
                                                    super.left = new Command(mResources.gameEO, 11103);
                                                } else if (itemSplit != null) {
                                                    super.left = this.gameRX;
                                                } else {
                                                    gameDQ = false;
                                                }

                                                if (GameCanvas.gameAJ) {
                                                    this.gameAA((int) 3, (Item) itemSplit);
                                                } else {
                                                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11104, itemSplit);
                                                }
                                            } else if (gameHL == 2) {
                                                if ((var1 = arrItemSplit[indexSelect]) != null) {
                                                    if (GameCanvas.gameAJ) {
                                                        this.gameAA((int) 3, (Item) var1);
                                                    } else {
                                                        super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11104, var1);
                                                    }
                                                } else {
                                                    gameDQ = false;
                                                }

                                                if (itemSplit != null && itemSplit.upgrade > 0) {
                                                    super.left = new Command(mResources.gameFV, 11105);
                                                }
                                            }
                                        } else if (indexMenu == 1) {
                                            if (Char.getMyChar().arrItemBag[indexSelect] == null) {
                                                super.left = null;
                                                gameDQ = false;
                                            } else {
                                                super.left = new Command(mResources.gameEO, 11106);
                                            }
                                        }
                                    } else if (gameMJ) {
                                        try {
                                            if (indexMenu == 0) {
                                                if (gameHL == 1) {
                                                    if (itemSplit != null) {
                                                        super.left = new Command(mResources.gameEO, 11103);
                                                    } else {
                                                        gameDQ = false;
                                                    }

                                                    if (GameCanvas.gameAJ) {
                                                        this.gameAA((int) 3, (Item) itemSplit);
                                                    } else {
                                                        super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11104, itemSplit);
                                                    }
                                                } else if (gameHL == 2) {
                                                    if ((var1 = arrItemSplit[indexSelect]) != null) {
                                                        if (GameCanvas.gameAJ) {
                                                            this.gameAA((int) 3, (Item) var1);
                                                        } else {
                                                            super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11104, var1);
                                                        }

                                                        super.left = new Command(mResources.gameCJ, 1605);
                                                    } else {
                                                        gameDQ = false;
                                                    }

                                                    if (itemSplit != null) {
                                                        super.left = new Command(mResources.gameEO, 1604);
                                                    }
                                                }
                                            } else if (indexMenu == 1) {
                                                if (Char.getMyChar().arrItemBag[indexSelect] == null) {
                                                    super.left = null;
                                                    gameDQ = false;
                                                } else {
                                                    super.left = new Command(mResources.gameEO, 11106);
                                                }
                                            }
                                        } catch (Exception var8) {
                                        }
                                    } else if (gameMK) {
                                        if (indexMenu == 0) {
                                            if (gameHL == 1) {
                                                if (itemSplit != null && itemSplit.upgrade > 11) {
                                                    super.left = new Command(mResources.gameEO, 11103);
                                                } else if (itemSplit != null) {
                                                    super.left = this.gameRX;
                                                } else {
                                                    gameDQ = false;
                                                }

                                                if (GameCanvas.gameAJ) {
                                                    this.gameAA((int) 3, (Item) itemSplit);
                                                } else {
                                                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11104, itemSplit);
                                                }
                                            } else if (gameHL == 2) {
                                                if ((var1 = arrItemSplit[indexSelect]) != null) {
                                                    if (GameCanvas.gameAJ) {
                                                        this.gameAA((int) 3, (Item) var1);
                                                    } else {
                                                        super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11104, var1);
                                                    }
                                                } else {
                                                    gameDQ = false;
                                                }

                                                if (itemSplit != null && itemSplit.upgrade > 11) {
                                                    super.left = new Command(mResources.gameEO, 1604);
                                                }
                                            }
                                        } else if (indexMenu == 1) {
                                            if (Char.getMyChar().arrItemBag[indexSelect] == null) {
                                                super.left = null;
                                                gameDQ = false;
                                            } else {
                                                super.left = new Command(mResources.gameEO, 1606);
                                            }
                                        }
                                    } else {
                                        Item var10;
                                        if (gameKZ) {
                                            if (indexMenu == 0) {
                                                var13 = 0;

                                                for (var2 = 0; var2 < arrItemUpPeal.length; ++var2) {
                                                    if (arrItemUpPeal[var2] != null) {
                                                        ++var13;
                                                        if (var13 >= 2) {
                                                            break;
                                                        }
                                                    }
                                                }

                                                if ((var10 = gameAK(11)) != null) {
                                                    if (var13 >= 2) {
                                                        super.left = new Command(mResources.gameEO, 11107);
                                                    } else {
                                                        super.left = new Command(mResources.gameCJ, 111071);
                                                    }

                                                    if (GameCanvas.gameAJ) {
                                                        this.gameAA((int) 3, (Item) var10);
                                                    } else {
                                                        super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11108);
                                                    }
                                                } else {
                                                    gameDQ = false;
                                                    if (var13 >= 2) {
                                                        super.left = new Command(mResources.gameFV, 11062);
                                                    }
                                                }
                                            } else if (indexMenu == 1) {
                                                if (Char.getMyChar().arrItemBag[indexSelect] != null) {
                                                    super.left = new Command(mResources.gameEO, 11109);
                                                } else {
                                                    gameDQ = false;
                                                    super.left = null;
                                                }
                                            }
                                        } else if (gameMI) {
                                            if (indexMenu == 0) {
                                                var13 = 0;
                                                var2 = 0;
                                                var3 = 0;
                                                short var4 = 0;

                                                for (int var5 = 0; var5 < arrItemUpPeal.length; ++var5) {
                                                    Item var6;
                                                    if ((var6 = arrItemUpPeal[var5]) != null) {
                                                        if (var6.template.id == 455) {
                                                            ++var13;
                                                        } else if (var6.template.id == 456) {
                                                            ++var2;
                                                        } else if (var6.template.type == 26) {
                                                            var4 = var6.template.id;
                                                            ++var3;
                                                        }
                                                    }

                                                    if (var13 >= 9 || var2 >= 9 || var4 == 10 && var13 >= 3 || var4 == 11 && var2 >= 3) {
                                                        break;
                                                    }
                                                }

                                                Item var11;
                                                if ((var11 = gameAK(43)) != null) {
                                                    if (var13 != 9 && var2 != 9 && (var4 != 10 || var13 != 3 || var3 != 1) && (var4 != 11 || var2 != 3 || var3 != 1)) {
                                                        super.left = new Command(mResources.gameCJ, 111071);
                                                    } else {
                                                        super.left = new Command(mResources.gameEO, 1601);
                                                    }

                                                    if (GameCanvas.gameAJ) {
                                                        this.gameAA((int) 43, (Item) var11);
                                                    } else {
                                                        super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 1602);
                                                    }
                                                } else {
                                                    gameDQ = false;
                                                    if (var13 >= 9 || var2 >= 9 || var4 >= 10 && (var13 >= 3 || var2 >= 3)) {
                                                        super.left = new Command(mResources.gameFV, 1600);
                                                    }
                                                }
                                            } else if (indexMenu == 1) {
                                                if (Char.getMyChar().arrItemBag[indexSelect] != null) {
                                                    super.left = new Command(mResources.gameEO, 1603);
                                                } else {
                                                    gameDQ = false;
                                                    super.left = null;
                                                }
                                            }
                                        } else if (isPaintTrade) {
                                            if (indexMenu == 0) {
                                                if (gameHL == 1) {
                                                    if (arrItemTradeMe[indexSelect] != null) {
                                                        if (this.typeTrade == 0) {
                                                            super.left = this.gameSD;
                                                        } else if (this.typeTrade == 1 && this.typeTradeOrder >= 1 && (long) this.timeTrade - System.currentTimeMillis() / 1000L <= 0L) {
                                                            super.left = this.gameRR;
                                                        }

                                                        if (GameCanvas.gameAJ) {
                                                            var10 = arrItemTradeMe[indexSelect];
                                                            this.gameAA((int) 3, (Item) var10);
                                                        } else {
                                                            super.center = this.gameSA;
                                                        }
                                                    } else {
                                                        gameDQ = false;
                                                        if (this.typeTrade == 0) {
                                                            super.left = this.gameRQ;
                                                        } else if (this.typeTrade == 1 && this.typeTradeOrder >= 1 && (long) this.timeTrade - System.currentTimeMillis() / 1000L <= 0L) {
                                                            super.left = this.gameRR;
                                                        }
                                                    }
                                                }

                                                if (gameHL == 2) {
                                                    if (arrItemTradeOrder[indexSelect] != null) {
                                                        if (GameCanvas.gameAJ) {
                                                            var10 = arrItemTradeOrder[indexSelect];
                                                            this.gameAA((int) 30, (Item) var10);
                                                        } else {
                                                            super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11110);
                                                        }
                                                    } else {
                                                        gameDQ = false;
                                                    }
                                                }
                                            } else if (indexMenu == 1 && this.typeTrade == 0) {
                                                if (Char.getMyChar().arrItemBag[indexSelect] != null) {
                                                    super.left = this.gameSC;
                                                } else {
                                                    super.left = this.gameRY;
                                                }
                                            }
                                        } else if (gameMA) {
                                            if (indexMenu == 0) {
                                                if ((var1 = gameAK(4)) != null) {
                                                    String var12 = mResources.gameCI;
                                                    if (!svTitle.equals("")) {
                                                        var12 = svAction;
                                                    }

                                                    super.left = new Command(var12, 111101);
                                                    if (GameCanvas.gameAJ) {
                                                        this.gameAA((int) 4, (Item) var1);
                                                    } else {
                                                        super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11111);
                                                    }
                                                } else if (svTitle.equals("")) {
                                                    super.left = new Command(mResources.gameCD, 11112);
                                                }
                                            } else if (indexMenu == 1) {
                                                if (Char.getMyChar().arrItemBag[indexSelect] != null) {
                                                    super.left = new Command(mResources.gameCN, 11113);
                                                } else {
                                                    super.left = this.gameTV;
                                                }
                                            }
                                        } else if (fieldTF) {
                                            if (indexMenu == 0) {
                                                if (indexSelect >= 0 && indexSelect <= Code.fieldAK.length && Code.fieldAK[indexSelect] > 0) {
                                                    super.left = new Command("Xóa", 1100077);

                                                } else {

                                                    super.left = new Command("Sắp xếp", 1100078);
                                                }
                                            } else if (indexMenu == 1) {
                                                if (Char.getMyChar().arrItemBag[indexSelect] != null) {
                                                    super.left = new Command("Thêm", 1100079);
                                                } else {
                                                    super.left = this.gameTV;
                                                }
                                            }
                                        } else if (fieldTG) {
                                            if (indexMenu == 0) {
                                                if (indexSelect >= 0 && indexSelect <= Code.dell.length && Code.dell[indexSelect] > 0) {
                                                    super.left = new Command("Xóa", 11000771);
                                                } else {
                                                    super.left = new Command("Sắp xếp", 11000781);
                                                }
                                            } else if (indexMenu == 1) {
                                                if (Char.getMyChar().arrItemBag[indexSelect] != null) {
                                                    super.left = new Command("Thêm", 11000791);
                                                } else {
                                                    super.left = this.gameTV;
                                                }
                                            }
                                        } else if (ItemThrow) {
                                            if (indexMenu == 0) {
                                                if (indexSelect >= 0 && indexSelect <= Code.throwne.length && Code.throwne[indexSelect] > 0) {
                                                    super.left = new Command("Xóa", 110007711);
                                                } else {
                                                    super.left = new Command("Sắp xếp", 110007811);
                                                }
                                            } else if (indexMenu == 1) {
                                                if (Char.getMyChar().arrItemBag[indexSelect] != null) {
                                                    super.left = new Command("Thêm", 110007911);
                                                } else {
                                                    super.left = this.gameTV;
                                                }
                                            }
                                        } else if (!gameMR) {
                                            if (gameML) {
                                                GameScr var14 = this;

                                                try {
                                                    if (indexMenu == 0) {
                                                        if (gameHL == 1) {
                                                            if (itemSplit != null) {
                                                                var14.left = new Command(mResources.gameEO, 11103);
                                                            } else {
                                                                gameDQ = false;
                                                            }

                                                            if (GameCanvas.gameAJ) {
                                                                var14.gameAA((int) 3, (Item) itemSplit);
                                                            } else {
                                                                var14.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11104, itemSplit);
                                                            }
                                                        } else if (gameHL == 2) {
                                                            if ((var10 = arrItemSplit[indexSelect]) != null) {
                                                                if (GameCanvas.gameAJ) {
                                                                    var14.gameAA((int) 3, (Item) var10);
                                                                } else {
                                                                    var14.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11104, var10);
                                                                }

                                                                var14.left = new Command(mResources.gameCJ, 1605);
                                                            } else {
                                                                gameDQ = false;
                                                            }

                                                            if (itemSplit != null) {
                                                                var14.left = new Command(mResources.gameEO, 1604);
                                                            }
                                                        }
                                                    } else if (indexMenu == 1) {
                                                        if (Char.getMyChar().arrItemBag[indexSelect] == null) {
                                                            var14.left = null;
                                                            gameDQ = false;
                                                        } else {
                                                            var14.left = new Command(mResources.gameEO, 222);
                                                        }
                                                    }
                                                } catch (Exception var7) {
                                                }
                                            } else if (gameMM) {
                                                if (indexMenu == 0) {
                                                    if (gameHL == 1) {
                                                        if (itemSplit != null && indexSelect == 0) {
                                                            super.left = new Command(mResources.gameEO, 338);
                                                            if (GameCanvas.gameAJ) {
                                                                gameHQ = false;
                                                                this.gameAA((int) 3, (Item) itemSplit);
                                                            } else {
                                                                super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 335);
                                                            }
                                                        }

                                                        if (itemUpGrade != null && indexSelect == 1) {
                                                            super.left = new Command(mResources.gameEO, 344);
                                                            if (GameCanvas.gameAJ) {
                                                                gameHQ = false;
                                                                this.gameAA((int) 3, (Item) itemUpGrade);
                                                            } else {
                                                                super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 336);
                                                            }
                                                        }

                                                        if (itemSplit == null && itemUpGrade == null) {
                                                            gameDQ = false;
                                                        }
                                                    } else if (gameHL == 2) {
                                                        var10 = gameAK(47);
                                                        gameHQ = false;
                                                        if (var10 != null) {
                                                            super.left = new Command(mResources.gameEO, 345);
                                                            if (GameCanvas.gameAJ) {
                                                                this.gameAA((int) 3, (Item) var10);
                                                            } else {
                                                                super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11101);
                                                            }
                                                        } else {
                                                            super.left = null;
                                                            gameDQ = false;
                                                            if (itemUpGrade != null && itemSplit != null) {
                                                                for (var2 = 0; var2 < arrItemUpGrade.length; ++var2) {
                                                                    if (arrItemUpGrade[var2] != null) {
                                                                        super.left = new Command(mResources.gameFV, 341);
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else if (indexMenu == 1) {
                                                    if (Char.getMyChar().arrItemBag[indexSelect] != null) {
                                                        super.left = new Command(mResources.gameEO, 337);
                                                    } else {
                                                        super.left = null;
                                                    }
                                                }
                                            } else if (gameMQ) {
                                                if (indexMenu == 0) {
                                                    var10 = gameAK(48);
                                                    gameHQ = false;
                                                    if (var10 != null) {
                                                        super.left = new Command(mResources.gameEO, 401);
                                                        if (GameCanvas.gameAJ) {
                                                            this.gameAA((int) 3, (Item) var10);
                                                        } else {
                                                            super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11101);
                                                        }
                                                    } else {
                                                        for (var2 = 0; var2 < arrItemSplit.length; ++var2) {
                                                            if (arrItemSplit[var2] != null) {
                                                                super.left = new Command(mResources.gameHL[0], 403);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                } else if (indexMenu == 1) {
                                                    if (Char.getMyChar().arrItemBag[indexSelect] != null) {
                                                        super.left = new Command(mResources.gameEO, 400);
                                                    } else {
                                                        super.left = null;
                                                    }
                                                }
                                            } else if (gameMN || gameMO) {
                                                if (indexMenu == 0) {
                                                    if (itemSplit != null) {
                                                        super.left = new Command(mResources.gameEO, 11103);
                                                    } else {
                                                        gameDQ = false;
                                                    }

                                                    if (GameCanvas.gameAJ) {
                                                        this.gameAA((int) 3, (Item) itemSplit);
                                                    } else {
                                                        super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11104, itemSplit);
                                                    }
                                                } else if (indexMenu == 1) {
                                                    if (Char.getMyChar().arrItemBag[indexSelect] == null) {
                                                        super.left = null;
                                                        gameDQ = false;
                                                    } else {
                                                        super.left = new Command(mResources.gameEO, 405);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (gameBA() && indexMenu == 1 && Char.getMyChar().arrItemBag[indexSelect] != null) {
                    if (GameCanvas.gameAJ) {
                        this.gameAA((int) 3, (Item) Char.getMyChar().arrItemBag[indexSelect]);
                        return;
                    }

                    super.center = new Command(GameCanvas.isTouch ? mResources.gameBX : "", 11114);
                }

            }
        }
    }

    private void fieldBC(mGraphics var1) {
        if (fieldTF) {
            if (indexMenu == 0) {
                gameAB(var1);
                gameAA(var1, mResources.nhatdo, false);
                fieldAA(var1, Code.fieldAK);
                return;
            }

            if (indexMenu == 1) {
                this.gameAA(var1, mResources.nhatdo);
            }
        }

    }

    private void fieldBD(mGraphics var1) {
        if (fieldTG) {
            if (indexMenu == 0) {
                gameAB(var1);
                gameAA(var1, mResources.deldo, false);
                fieldAA(var1, Code.dell);
                return;
            }

            if (indexMenu == 1) {
                this.gameAA(var1, mResources.deldo);
            }
        }

    }

    private void fieldThrow(mGraphics var1) {
        if (ItemThrow) {
            if (indexMenu == 0) {
                gameAB(var1);
                gameAA(var1, mResources.vutdo, false);
                fieldAA(var1, Code.throwne);
                return;
            }

            if (indexMenu == 1) {
                this.gameAA(var1, mResources.vutdo);
            }
        }

    }

    public static void gameAB(int var0, int var1) {
        if (GameCanvas.w == 128 || GameCanvas.h <= 208) {
            var0 = 126;
            var1 = 160;
        }

        popupW = var0;
        popupH = var1;
        popupX = gW2 - var0 / 2;
        popupY = gH2 - var1 / 2;
        if (GameCanvas.h <= 250) {
            popupY -= 10;
        }

        if (GameCanvas.gameAJ && !gameDJ() && GameCanvas.currentScreen instanceof GameScr) {
            popupW = 310;
            popupX = gW / 2 - popupW / 2;
        }

        if (popupY < -10) {
            popupY = -10;
        }

        if (GameCanvas.h > 208 && popupY < 0) {
            popupY = 0;
        }

        if (GameCanvas.h == 208 && popupY < 10) {
            popupY = 10;
        }

    }

    private void ThongTinSkill(mGraphics var1, Skill var2) {
        if (Char.getMyChar().clevel >= var2.level) {
            mFont.tahoma_7_white.gameAA(var1, mResources.gameKU + " " + var2.level, gameTD + 5, this.gameTL += 12, 0);
        } else {
            mFont.tahoma_7_red.gameAA(var1, mResources.gameKU + " " + var2.level, gameTD + 5, this.gameTL += 12, 0);
        }

        if (var2.template.type != 0) {
            gameHM += 4;
            mFont.tahoma_7_white.gameAA(var1, mResources.gameQA + ": " + var2.maxFight, gameTD + 5, this.gameTL += 12, 0);
            mFont.tahoma_7_white.gameAA(var1, mResources.gameQC + ": " + var2.manaUse, gameTD + 5, this.gameTL += 12, 0);
            mFont.tahoma_7_white.gameAA(var1, mResources.gameQB + ": " + var2.dx, gameTD + 5, this.gameTL += 12, 0);
            mFont var10000 = mFont.tahoma_7_white;
            mGraphics var10001 = var1;
            StringBuffer var10002 = (new StringBuffer(String.valueOf(mResources.gameQD))).append(": ");
            Skill var3 = var2;
            String var10003;
            if (var2.coolDown % 1000 == 0) {
                var10003 = String.valueOf(var2.coolDown / 1000);
            } else {
                int var4 = var2.coolDown % 1000;
                var10003 = var3.coolDown / 1000 + "." + (var4 % 100 == 0 ? var4 / 100 : var4 / 10);
            }

            var10000.gameAA(var10001, var10002.append(var10003).append(" ").append(mResources.gamePY).toString(), gameTD + 5, this.gameTL += 12, 0);
        }

        ++gameHM;
    }

    private void gameAB(mGraphics var1, Skill var2) {
        SkillOption[] var6 = var2.options;

        for (int var3 = 0; var3 < var6.length; ++var3) {
            SkillOption var4 = var6[var3];
            if (mFont.tahoma_7_white.gameAA(var4.gameAA()) > 145) {
                MyVector var7 = mFont.tahoma_7_white.gameAA(var4.gameAA(), 145);

                for (int var5 = 0; var5 < var7.size(); ++var5) {
                    mFont.tahoma_7_white.gameAA(var1, var7.elementAt(var5).toString(), gameTD + 5, this.gameTL += 12, 0);
                    ++gameHM;
                }
            } else {
                mFont.tahoma_7_white.gameAA(var1, var4.gameAA(), gameTD + 5, this.gameTL += 12, 0);
                ++gameHM;
            }
        }

    }

    private void gameAO(mGraphics var1) {
        if (indexMenu == 1) {
            gameAB(var1);
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            gameAA(var1, mResources.gameGG[indexMenu], true);
            mFont.tahoma_7b_white.gameAA(var1, mResources.gameND, popupX + 10, popupY + 32, 0);
            mFont.tahoma_7b_white.gameAA(var1, "" + Char.getMyChar().sPoint, popupX + popupW - 10, popupY + 32, 1);
            var1.gameAA(0);
            var1.gameAC(popupX + 4, popupY + 44, popupW - 7, gameHK + 3);
            var1.gameAA(12281361);
            var1.gameAB(popupX + 5, popupY + 45, popupW - 10, gameHK);
            if (gameHL >= 1) {
                var1.gameAA(Paint.COLORBORDER);
                var1.gameAB(popupX + 5, popupY + 48 + gameHK, popupW - 10, popupH - 64 - gameHK);
            }

            int var2 = Char.getMyChar().nClass.skillTemplates.length;
            gameTD = popupX + 5;
            gameTE = popupY + 45;
            scrMain.gameAA(var2, gameHK + 2, gameTD + 1, gameTE, popupW - 12, gameHK + 2, false, 1);
            scrMain.gameAA(var1);

            for (int var3 = 0; var3 < var2; ++var3) {
                int var10002 = gameTD + var3 * (gameHK + 2) + gameHK / 2;
                SmallImage.gameAA(var1, Char.getMyChar().nClass.skillTemplates[var3].iconId, var10002, gameTE + gameHK / 2, 0, 3);
                if (gameHL == 1 && var3 == indexSelect) {
                    var1.gameAA(16777215);
                    var1.gameAB(gameTD + var3 * (gameHK + 2) + 2, gameTE + 2, gameHK - 4, gameHK - 4);
                    gameAA(gameTD + var3 * (gameHK + 2), gameTE, var1);
                }
            }

            gameTD += 8;
            gameTE += 6;
            if (gameHL == 1 && indexSelect >= 0) {
                gameAB(var1);
                SkillTemplate var10 = Char.getMyChar().nClass.skillTemplates[indexSelect];
                gameHM = 4 + var10.description.length;
                Skill var9 = Char.getMyChar().gameAA(var10);
                int var4 = popupX;
                int var5 = gameTE + gameHK + 2;
                int var6 = popupW - 6;
                int var7 = popupH - 70 - gameHK;
                gameHO.gameAA(var1, var4, var5, var6, var7);
                this.gameTL = gameTE + 18;
                int var8;
                if (var9 == null) {
                    var9 = var10.skills.length > 1 ? var10.skills[1] : var10.skills[0];
                    mFont.tahoma_7b_red.gameAA(var1, "[" + var9.template.id + "]" + var10.name, gameTD + 5, this.gameTL += 12, 0);

                    for (var8 = 0; var8 < var10.description.length; ++var8) {
                        mFont.tahoma_7_white.gameAA(var1, var10.description[var8], gameTD + 5, this.gameTL += 12, 0);
                    }

                    mFont.tahoma_7_white.gameAA(var1, mResources.gameMO[var10.type], gameTD + 5, this.gameTL += 12, 0);
                    mFont.tahoma_7_white.gameAA(var1, mResources.gameKS + ": " + var10.maxPoint, gameTD + 5, this.gameTL += 12, 0);
                    mFont.tahoma_7_red.gameAA(var1, mResources.gameAA(mResources.gameKQ, String.valueOf(var9.point)), gameTD + 5, this.gameTL += 12, 0);
                    this.ThongTinSkill(var1, var9);
                    this.gameAB(var1, var9);
                } else {
                    mFont.tahoma_7b_white.gameAA(var1, "[" + var9.template.id + "]" + var10.name, gameTD + 5, this.gameTL += 12, 0);

                    for (var8 = 0; var8 < var10.description.length; ++var8) {
                        mFont.tahoma_7_white.gameAA(var1, var10.description[var8], gameTD + 5, this.gameTL += 12, 0);
                    }

                    mFont.tahoma_7_white.gameAA(var1, mResources.gameMO[var10.type], gameTD + 5, this.gameTL += 12, 0);
                    mFont.tahoma_7_white.gameAA(var1, mResources.gameKS + ": " + var10.maxPoint, gameTD + 5, this.gameTL += 12, 0);
                    if (var9.point == var10.maxPoint) {
                        mFont.tahoma_7_blue.gameAA(var1, mResources.gameKT, gameTD + 5, this.gameTL += 12, 0);
                        this.ThongTinSkill(var1, var9);
                        this.gameAB(var1, var9);
                    } else {
                        mFont.tahoma_7_blue.gameAA(var1, mResources.gameAA(mResources.gameKR, String.valueOf(var9.point)), gameTD + 5, this.gameTL += 12, 0);
                        this.ThongTinSkill(var1, var9);
                        this.gameAB(var1, var9);

                        for (var8 = 0; var8 < var10.skills.length; ++var8) {
                            if (var10.skills[var8].equals(var9)) {
                                ++var8;
                                break;
                            }
                        }

                        mFont.tahoma_7_red.gameAA(var1, mResources.gameAA(mResources.gameKQ, String.valueOf(var10.skills[var8].point)), gameTD + 5, this.gameTL += 12, 0);
                        this.ThongTinSkill(var1, var10.skills[var8]);
                        ++gameHM;
                        this.gameAB(var1, var10.skills[var8]);
                    }
                }

                gameHO.gameAA(gameHM, 12, var4, var5, var6, var7, true, 1);
                if (indexRow >= 0) {
                    SmallImage.gameAA(var1, 942, gameTD + 2, gameTE + 32 + indexRow * 12, 0, StaticObj.TOP_RIGHT);
                }
            }

        }
    }

    private void gameAA(mGraphics var1, String[] var2) {
        try {
            gameAB(var1);
            this.gameAA(var1, var2, true);
            this.gameAA(var1, Char.getMyChar().arrItemBag);
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }

    private void gameAA(mGraphics var1, Item[] var2) {
        gameTK = var2.length / gameTJ;
        scrMain.gameAA(gameTK, gameHK, gameTD, gameTE, gameTJ * gameHK, 5 * gameHK, true, 6);
        scrMain.gameAA(var1, gameTD, gameTE, scrMain.width + 2, scrMain.height + 2);

        int var3;
        int var4;
        for (var3 = 0; var3 < gameTK; ++var3) {
            for (var4 = 0; var4 < gameTJ; ++var4) {
                SmallImage.gameAA(var1, 154, gameTD + var4 * gameHK + gameHK / 2, gameTE + var3 * gameHK + gameHK / 2, 0, 3);
                var1.gameAA(12281361);
                var1.gameAB(gameTD + var4 * gameHK, gameTE + var3 * gameHK, gameHK, gameHK);
            }
        }

        for (var3 = 0; var3 < var2.length; ++var3) {
            Item var7;
            if ((var7 = var2[var3]) != null) {
                int var5 = var7.indexUI / gameTJ;
                int var6 = var7.indexUI - var5 * gameTJ;
                this.gameAA(var1, var7, gameTD + var6 * gameHK, gameTE + var5 * gameHK);
                if (var7.quantity > 1) {
                    mFont.number_yellow.gameAA(var1, "" + var7.quantity, gameTD + var6 * gameHK + gameHK, gameTE + var5 * gameHK + gameHK - mFont.number_yellow.gameAC(), 1);
                }
            }
        }

        if (gameHL > 0 && indexSelect >= 0) {
            var3 = indexSelect / gameTJ;
            var4 = indexSelect - var3 * gameTJ;
            var1.gameAA(16777215);
            var1.gameAB(gameTD + var4 * gameHK, gameTE + var3 * gameHK, gameHK, gameHK);
            gameAA(gameTD + var4 * gameHK, gameTE + var3 * gameHK, var1);
        }

    }

    private static void fieldAA(mGraphics var0, short[] var1) {
        gameTK = var1.length / gameTJ;
        scrMain.gameAA(gameTK, gameHK, gameTD, gameTE, gameTJ * gameHK, 5 * gameHK, true, 6);
        scrMain.gameAA(var0, gameTD, gameTE, scrMain.width + 2, scrMain.height + 2);

        int var2;
        int var3;
        for (var2 = 0; var2 < gameTK; ++var2) {
            for (var3 = 0; var3 < gameTJ; ++var3) {
                SmallImage.gameAAA(var0, 154, gameTD + var3 * gameHK + gameHK / 2, gameTE + var2 * gameHK + gameHK / 2, 0, 3);
                var0.gameAA(12281361);
                var0.gameAB(gameTD + var3 * gameHK, gameTE + var2 * gameHK, gameHK, gameHK);
            }
        }

        for (var2 = 0; var2 < var1.length; ++var2) {
            short var6;
            if ((var6 = var1[var2]) > 0) {
                int var4 = var2 / gameTJ;
                int var5 = var2 - var4 * gameTJ;
                ItemTemplate var7;
                if ((var7 = ItemTemplates.gameAA(var6)) != null) {
                    SmallImage.gameAAA(var0, var7.iconID, gameTD + var5 * gameHK + gameHK / 2, gameTE + var4 * gameHK + gameHK / 2, 0, 3);
                }
            }
        }

        if (gameHL > 0 && indexSelect >= 0) {
            var2 = indexSelect / gameTJ;
            var3 = indexSelect - var2 * gameTJ;
            var0.gameAA(16777215);
            var0.gameAB(gameTD + var3 * gameHK, gameTE + var2 * gameHK, gameHK, gameHK);
            gameAA(gameTD + var3 * gameHK, gameTE + var2 * gameHK, var0);
        }

    }

    private static void gameAA(int var0, int var1, mGraphics var2) {
        var2.gameAA(gameNS, var0 - 5, var1 - 5, 0);
    }

    private static int gameAI(int var0) {
        int var1 = gameHK - 2;
        if ((var0 %= var1 * 4) >= 0 && var0 < var1) {
            return 0;
        } else if (var1 <= var0 && var0 < var1 << 1) {
            return var0 % var1;
        } else {
            return var1 << 1 <= var0 && var0 < var1 * 3 ? var1 : var1 - var0 % var1;
        }
    }

    private static int gameAJ(int var0) {
        int var1 = gameHK - 2;
        if ((var0 %= var1 * 4) >= 0 && var0 < var1) {
            return var0 % var1;
        } else if (var1 <= var0 && var0 < var1 << 1) {
            return var1;
        } else {
            return var1 << 1 <= var0 && var0 < var1 * 3 ? var1 - var0 % var1 : 0;
        }
    }

    private void gameAB(mGraphics var1, Item[] var2) {
        try {
            gameAB(var1);
            boolean var7 = true;
            String var4 = gameTP;
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            mFont.tahoma_7_white.gameAA(var1, mResources.gamePA + ": " + NinjaUtil.gameAA(String.valueOf(Char.getMyChar().xu)), popupX + 6, popupY + popupH - 26, 0);
            mFont.tahoma_7_white.gameAA(var1, mResources.gamePB + ": " + NinjaUtil.gameAA(String.valueOf(Char.getMyChar().yen)), popupX + popupW - 6, popupY + popupH - 26, 1);
            if (isPaintTrade) {
                if (GameCanvas.gameTick % 10 > 4) {
                    mFont.tahoma_7_yellow.gameAA(var1, mResources.gameFY, popupX + popupW / 2, popupY + popupH - 14, 2);
                }
            } else if (gameKZ) {
                if (GameCanvas.gameTick % 10 > 4) {
                    mFont.tahoma_7_yellow.gameAA(var1, mResources.gameFW, popupX + popupW / 2, popupY + popupH - 14, 2);
                }
            } else if (gameMB) {
                if (GameCanvas.gameTick % 10 > 4) {
                    mFont.tahoma_7_yellow.gameAA(var1, mResources.gameFX, popupX + popupW / 2, popupY + popupH - 14, 2);
                }
            } else {
                mFont.tahoma_7_yellow.gameAA(var1, mResources.gamePC + ": " + NinjaUtil.gameAA(String.valueOf(Char.getMyChar().luong)), popupX + popupW / 2, popupY + popupH - 14, 2);
            }

            gameAA(var1, var4, false);
            gameTD = popupX + 3;
            gameTE = popupY + 32;
            var1.gameAA(6425);
            var1.gameAC(gameTD - 1, gameTE - 1, gameTJ * gameHK + 3, 5 * gameHK + 3);
            if (var2 == null) {
                GameCanvas.gameAA(popupX + 90, popupY + 75, var1, false);
                mFont.tahoma_7b_white.gameAA(var1, mResources.PLEASEWAIT, popupX + 90, popupY + 90, 2);
                return;
            }

            if (var2.length <= 30) {
                gameTK = 5;
            } else if (var2.length % gameTJ == 0) {
                gameTK = var2.length / gameTJ;
            } else {
                gameTK = var2.length / gameTJ + 1;
            }

            scrMain.gameAA(gameTK, gameHK, gameTD, gameTE, gameTJ * gameHK, 5 * gameHK, true, 6);
            scrMain.gameAA(var1, gameTD, gameTE, scrMain.width + 2, scrMain.height + 2);

            int var3;
            int var8;
            for (var8 = 0; var8 < gameTK; ++var8) {
                for (var3 = 0; var3 < gameTJ; ++var3) {
                    SmallImage.gameAA(var1, 154, gameTD + var3 * gameHK + gameHK / 2, gameTE + var8 * gameHK + gameHK / 2, 0, 3);
                    var1.gameAA(12281361);
                    var1.gameAB(gameTD + var3 * gameHK, gameTE + var8 * gameHK, gameHK, gameHK);
                }
            }

            for (var8 = 0; var8 < var2.length; ++var8) {
                Item var9;
                if ((var9 = var2[var8]) != null) {
                    int var10 = var9.indexUI / gameTJ;
                    int var5 = var9.indexUI - var10 * gameTJ;
                    if (!var9.isLock) {
                        var1.gameAA(12083);
                        var1.gameAC(gameTD + var5 * gameHK + 1, gameTE + var10 * gameHK + 1, gameHK - 1, gameHK - 1);
                        SmallImage.gameAA(var1, 154, gameTD + var5 * gameHK + gameHK / 2, gameTE + var10 * gameHK + gameHK / 2, 0, 3);
                    }

                    SmallImage.gameAA(var1, var9.template.iconID, gameTD + var5 * gameHK + gameHK / 2, gameTE + var10 * gameHK + gameHK / 2, 0, 3);
                }
            }

            if (gameHL > 0 && indexSelect >= 0) {
                var8 = indexSelect / gameTJ;
                var3 = indexSelect - var8 * gameTJ;
                var1.gameAA(16777215);
                var1.gameAB(gameTD + var3 * gameHK, gameTE + var8 * gameHK, gameHK, gameHK);
                gameAA(gameTD + var3 * gameHK, gameTE + var8 * gameHK, var1);
                return;
            }
        } catch (Exception var6) {
        }

    }

    private void gameAA(mGraphics var1, String[] var2, Item[] var3) {
        try {
            gameAB(var1);
            this.gameAA(var1, var2, true);
            if (var3 == null) {
                GameCanvas.gameAA(popupX + 90, popupY + 75, var1, false);
                mFont.tahoma_7b_white.gameAA(var1, mResources.PLEASEWAIT, popupX + 90, popupY + 90, 2);
                return;
            }

            if (var3.length <= 30) {
                gameTK = 5;
            } else if (var3.length % gameTJ == 0) {
                gameTK = var3.length / gameTJ;
            } else {
                gameTK = var3.length / gameTJ + 1;
            }

            scrMain.gameAA(gameTK, gameHK, gameTD, gameTE, gameTJ * gameHK, 5 * gameHK, true, 6);
            scrMain.gameAA(var1, gameTD, gameTE, scrMain.width + 2, scrMain.height + 2);

            int var7;
            int var8;
            for (var7 = 0; var7 < gameTK; ++var7) {
                for (var8 = 0; var8 < gameTJ; ++var8) {
                    SmallImage.gameAA(var1, 154, gameTD + var8 * gameHK + gameHK / 2, gameTE + var7 * gameHK + gameHK / 2, 0, 3);
                    var1.gameAA(12281361);
                    var1.gameAB(gameTD + var8 * gameHK, gameTE + var7 * gameHK, gameHK, gameHK);
                }
            }

            for (var7 = 0; var7 < var3.length; ++var7) {
                Item var9;
                if ((var9 = var3[var7]) != null) {
                    int var4 = var9.indexUI / gameTJ;
                    int var5 = var9.indexUI - var4 * gameTJ;
                    if (!var9.isLock) {
                        var1.gameAA(12083);
                        var1.gameAC(gameTD + var5 * gameHK + 1, gameTE + var4 * gameHK + 1, gameHK - 1, gameHK - 1);
                        SmallImage.gameAA(var1, 154, gameTD + var5 * gameHK + gameHK / 2, gameTE + var4 * gameHK + gameHK / 2, 0, 3);
                    }

                    if (GameCanvas.gameTick % 6 == 0) {
                        var9.indexFrame = (var9.indexFrame + 1) % 3;
                    }

                    if (var9.isNewitem()) {
                        var1.gameAA(GameCanvas.gameBY, gameTD + var5 * gameHK + gameHK / 2, gameTE + var4 * gameHK + gameHK / 2, 3);
                    }

                    SmallImage.gameAA(var1, var9.template.iconID, gameTD + var5 * gameHK + gameHK / 2, gameTE + var4 * gameHK + gameHK / 2, 0, 3, var9.indexFrame);
                }
            }

            if (gameHL > 0 && indexSelect >= 0) {
                var7 = indexSelect / gameTJ;
                var8 = indexSelect - var7 * gameTJ;
                var1.gameAA(16777215);
                var1.gameAB(gameTD + var8 * gameHK, gameTE + var7 * gameHK, gameHK, gameHK);
                gameAA(gameTD + var8 * gameHK, gameTE + var7 * gameHK, var1);
                return;
            }
        } catch (Exception var6) {
        }

    }

    private void gameAA(mGraphics var1, String[] var2, boolean var3) {
        Paint.gameAA(popupX, popupY, popupW, popupH, var1);
        if (var3) {
            mFont.tahoma_7_white.gameAA(var1, mResources.gamePA + ": " + NinjaUtil.gameAA(String.valueOf(Char.getMyChar().xu)), popupX + 6, popupY + popupH - 26, 0);
            mFont.tahoma_7_white.gameAA(var1, mResources.gamePB + ": " + NinjaUtil.gameAA(String.valueOf(Char.getMyChar().yen)), popupX + popupW - 6, popupY + popupH - 26, 1);
            if (isPaintTrade) {
                if (GameCanvas.gameTick % 10 > 4) {
                    mFont.tahoma_7_yellow.gameAA(var1, mResources.gameFY, popupX + popupW / 2, popupY + popupH - 14, 2);
                }
            } else if (gameKZ) {
                if (GameCanvas.gameTick % 10 > 4) {
                    mFont.tahoma_7_yellow.gameAA(var1, mResources.gameFW, popupX + popupW / 2, popupY + popupH - 14, 2);
                }
            } else if (gameMB) {
                if (GameCanvas.gameTick % 10 > 4) {
                    mFont.tahoma_7_yellow.gameAA(var1, mResources.gameFX, popupX + popupW / 2, popupY + popupH - 14, 2);
                }
            } else {
                mFont.tahoma_7_yellow.gameAA(var1, mResources.gamePC + ": " + NinjaUtil.gameAA(String.valueOf(Char.getMyChar().luong)) + " - Bag: " + Char.countUseSlots() + "/" + Char.countTotalSlots(), popupX + popupW / 2, popupY + popupH - 14, 2);
            }
        }

        gameAA(var1, var2[indexMenu], var2.length > 1);
        gameTD = popupX + 3;
        gameTE = popupY + 32;
        var1.gameAA(6425);
        var1.gameAC(gameTD - 1, gameTE - 1, gameTJ * gameHK + 3, 5 * gameHK + 3);
    }

    private void gameAA(mGraphics var1, Item var2, int var3, int var4) {
        this.gameAA(var1, var2, var3, var4, 0, 0);
    }

    private void gameAA(mGraphics var1, Item var2, int var3, int var4, int var5, int var6) {
        if (!var2.isTypeMounts()) {
            if ((var5 += var2.upgrade) > 0) {
                if (var5 >= 1) {
                    var1.gameAA(this.gameTM[var5 <= 16 ? var5 : 16]);
                    var1.gameAC(var3 + 1 + var6, var4 + 1 + var6, gameHK - 1 - 2 * var6, gameHK - 1 - 2 * var6);
                    SmallImage.gameAA(var1, 154, var3 + gameHK / 2, var4 + gameHK / 2, 0, 3, var2.indexFrame);
                    int var10001 = var3 + gameHK / 2;
                    int var10002 = var4 + gameHK / 2;
                    mGraphics var8 = var1;
                    int var7 = var5;
                    var6 = var10002;
                    var5 = var10001;
                    GameScr var14 = this;
                    int var9 = gameHK - 2;
                    int var10 = var7 < 4 ? 0 : (var7 < 8 ? 1 : (var7 < 12 ? 2 : (var7 <= 14 ? 3 : 4)));

                    int var11;
                    int var12;
                    int var13;
                    for (var11 = 0; var11 < var14.gameTO.length; ++var11) {
                        var12 = var5 - var9 / 2 + gameAJ(GameCanvas.gameTick - (var11 << 2));
                        var13 = var6 - var9 / 2 + gameAI(GameCanvas.gameTick - (var11 << 2));
                        var8.gameAA(var14.gameTN[var10][var11]);
                        var8.gameAC(var12 - var14.gameTO[var11] / 2, var13 - var14.gameTO[var11] / 2, var14.gameTO[var11], var14.gameTO[var11]);
                    }

                    if (var7 == 4 || var7 == 8) {
                        for (var11 = 0; var11 < var14.gameTO.length; ++var11) {
                            var12 = var5 - var9 / 2 + gameAJ(GameCanvas.gameTick - (var9 << 1) - (var11 << 2));
                            var13 = var6 - var9 / 2 + gameAI(GameCanvas.gameTick - (var9 << 1) - (var11 << 2));
                            var8.gameAA(var14.gameTN[var10 - 1][var11]);
                            var8.gameAC(var12 - var14.gameTO[var11] / 2, var13 - var14.gameTO[var11] / 2, var14.gameTO[var11], var14.gameTO[var11]);
                        }
                    }

                    if (var7 != 1 && var7 != 4 && var7 != 8) {
                        for (var11 = 0; var11 < var14.gameTO.length; ++var11) {
                            var12 = var5 - var9 / 2 + gameAJ(GameCanvas.gameTick - (var9 << 1) - (var11 << 2));
                            var13 = var6 - var9 / 2 + gameAI(GameCanvas.gameTick - (var9 << 1) - (var11 << 2));
                            var8.gameAA(var14.gameTN[var10][var11]);
                            var8.gameAC(var12 - var14.gameTO[var11] / 2, var13 - var14.gameTO[var11] / 2, var14.gameTO[var11], var14.gameTO[var11]);
                        }
                    }

                    if (var7 != 1 && var7 != 4 && var7 != 8 && var7 != 12 && var7 != 2 && var7 != 5 && var7 != 9) {
                        for (var11 = 0; var11 < var14.gameTO.length; ++var11) {
                            var12 = var5 - var9 / 2 + gameAJ(GameCanvas.gameTick - var9 - (var11 << 2));
                            var13 = var6 - var9 / 2 + gameAI(GameCanvas.gameTick - var9 - (var11 << 2));
                            var8.gameAA(var14.gameTN[var10][var11]);
                            var8.gameAC(var12 - var14.gameTO[var11] / 2, var13 - var14.gameTO[var11] / 2, var14.gameTO[var11], var14.gameTO[var11]);
                        }
                    }

                    if (var7 != 1 && var7 != 4 && var7 != 8 && var7 != 12 && var7 != 2 && var7 != 5 && var7 != 9 && var7 != 13 && var7 != 3 && var7 != 6 && var7 != 10 && var7 != 15) {
                        for (var11 = 0; var11 < var14.gameTO.length; ++var11) {
                            var12 = var5 - var9 / 2 + gameAJ(GameCanvas.gameTick - var9 * 3 - (var11 << 2));
                            var13 = var6 - var9 / 2 + gameAI(GameCanvas.gameTick - var9 * 3 - (var11 << 2));
                            var8.gameAA(var14.gameTN[var10][var11]);
                            var8.gameAC(var12 - var14.gameTO[var11] / 2, var13 - var14.gameTO[var11] / 2, var14.gameTO[var11], var14.gameTO[var11]);
                        }
                    }
                }
            } else {
                gameAA(var1, var2, var3, var4, var6);
            }
        }

        if (GameCanvas.gameTick % 6 == 0) {
            var2.indexFrame = (var2.indexFrame + 1) % 3;
        }

        if (var2.isNewitem()) {
            var1.gameAA(GameCanvas.gameBY, var3 + gameHK / 2, var4 + gameHK / 2, 3);
        }

        SmallImage.gameAA(var1, var2.template.iconID, var3 + gameHK / 2, var4 + gameHK / 2, 0, 3, var2.indexFrame);
    }

    private static void gameAA(mGraphics var0, Item var1, int var2, int var3, int var4) {
        if (!var1.isLock) {
            var0.gameAA(12083);
        } else {
            var0.gameAA(6425);
        }

        var0.gameAC(var2 + 1 + var4, var3 + 1 + var4, gameHK - 2 - var4 * 2, gameHK - 2 - var4 * 2);
        SmallImage.gameAA(var0, 154, var2 + gameHK / 2, var3 + gameHK / 2, 0, 3);
    }

    private void gameAB(mGraphics var1, String[] var2) {
        try {
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            gameAA(var1, var2[indexMenu], var2.length > 1);
            gameTD = popupX + 3;
            gameTE = popupY + 34 + gameHK;
            int var10 = popupX + 74;
            int var3 = gameTE - gameHK - 3;
            gameTK = 4;
            if (itemSplit != null) {
                this.gameAA(var1, itemSplit, var10, var3);
            } else {
                var1.gameAA(6425);
                var1.gameAC(var10 - 1, var3 - 1, gameHK + 3, gameHK + 3);
                SmallImage.gameAA(var1, 154, var10 + gameHK / 2, var3 + gameHK / 2, 0, 3);
            }

            var1.gameAA(12281361);
            var1.gameAB(var10, var3, gameHK, gameHK);
            var1.gameAA(6425);
            var1.gameAC(gameTD - 1, gameTE - 1, gameHK * gameTJ + 3, gameHK * gameTK + 3);

            int var4;
            int var8;
            for (var8 = 0; var8 < gameTK; ++var8) {
                for (var4 = 0; var4 < gameTJ; ++var4) {
                    SmallImage.gameAA(var1, 154, gameTD + var4 * gameHK + gameHK / 2, gameTE + var8 * gameHK + gameHK / 2, 0, 3);
                    var1.gameAA(12281361);
                    var1.gameAB(gameTD + var4 * gameHK, gameTE + var8 * gameHK, gameHK, gameHK);
                }
            }

            for (var8 = 0; var8 < arrItemSplit.length; ++var8) {
                Item var11;
                if ((var11 = arrItemSplit[var8]) != null) {
                    int var5 = var8 / gameTJ;
                    int var6 = var8 - var5 * gameTJ;
                    if (!var11.isLock) {
                        var1.gameAA(12083);
                        var1.gameAC(gameTD + var6 * gameHK + 1, gameTE + var5 * gameHK + 1, gameHK - 1, gameHK - 1);
                    }

                    SmallImage.gameAA(var1, var11.template.iconID, gameTD + var6 * gameHK + gameHK / 2, gameTE + var5 * gameHK + gameHK / 2, 0, 3);
                }
            }

            if (gameHL == 1) {
                var1.gameAA(16777215);
                var1.gameAB(var10, var3, gameHK, gameHK);
            } else if (gameHL == 2) {
                var8 = indexSelect / gameTJ;
                var4 = indexSelect - var8 * gameTJ;
                var1.gameAA(16777215);
                var1.gameAB(gameTD + var4 * gameHK, gameTE + var8 * gameHK, gameHK, gameHK);
            }

            if (effUpok != null) {
                SmallImage.gameAA(var1, effUpok.arrEfInfo[indexEff].idImg, var10 + gameHK / 2 + effUpok.arrEfInfo[indexEff].dx, var3 + gameHK / 2 + effUpok.arrEfInfo[indexEff].dy, 0, 3);
                if (GameCanvas.gameTick % 2 == 0 && ++indexEff >= effUpok.arrEfInfo.length) {
                    indexEff = 0;
                    effUpok = null;
                }
            }

            if (gameMJ && itemSplit != null) {
                ItemOption var9 = null;

                for (var4 = 0; var4 < itemSplit.options.size() && (var9 = (ItemOption) itemSplit.options.elementAt(var4)).optionTemplate.id != 85; ++var4) {
                    var9 = null;
                }

                if (var9 != null) {
                    int[] var13 = new int[]{60, 45, 34, 26, 20, 15, 11, 8, 6};
                    int[] var12 = new int[]{150000, 247500, 408375, 673819, 1111801, 2056832, 4010822, 7420021, 12243035};
                    byte[] var14 = new byte[]{3, 5, 9, 4, 7, 10, 5, 7, 9};
                    mFont.tahoma_7_yellow.gameAA(var1, mResources.gameWV + ": " + NinjaUtil.gameAA(String.valueOf(var9.param + 1)), gameTD + 1, gameTE + 114, 0);
                    mFont.tahoma_7_yellow.gameAA(var1, mResources.gameHX + var13[var9.param] + "%)", gameTD + 70, gameTE + 114, 0);
                    mFont.tahoma_7_yellow.gameAA(var1, mResources.gameBB + ": " + NinjaUtil.gameAA(String.valueOf(var12[var9.param])) + " " + mResources.gamePB + ", " + var14[var9.param] + " " + (var9.param < 3 ? mResources.gameWZ[1] : (var9.param < 6 ? mResources.gameWZ[2] : mResources.gameWZ[3])), gameTD + 1, gameTE + 126, 0);
                    return;
                }

                mFont.tahoma_7_red.gameAA(var1, mResources.gameWR, gameTD + 1, gameTE + 120, 0);
                return;
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }

    private void gameAC(mGraphics var1, String[] var2) {
        try {
            gameAB(var1);
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            gameAA(var1, var2[indexMenu], var2.length > 1);
            gameTD = popupX + 3;
            gameTE = popupY + 32;
            var1.gameAA(6425);
            gameTK = 4;
            int var3;
            int var7;
            int var8;
            if (!gameKZ) {
                mFont.tahoma_7_white.gameAA(var1, mResources.gameWI[0], gameTD + 3, gameTE + gameTK * gameHK + 9, 0);
                mFont.tahoma_7_white.gameAA(var1, mResources.gameWI[1], gameTD + 3, gameTE + gameTK * gameHK + 21, 0);
                mFont.tahoma_7_white.gameAA(var1, mResources.gameWI[2], gameTD + 3, gameTE + gameTK * gameHK + 33, 0);
            } else {
                var7 = 0;
                var8 = 0;
                var3 = 0;
                boolean var4 = false;

                int var5;
                for (var5 = 0; var5 < arrItemUpPeal.length; ++var5) {
                    if (arrItemUpPeal[var5] != null) {
                        if (arrItemUpPeal[var5].isLock) {
                            var4 = true;
                        }

                        var7 += crystals[arrItemUpPeal[var5].template.id];
                        ++var8;
                    }
                }

                if (var7 > 0) {
                    for (var3 = crystals.length - 1; var3 >= 0 && var7 <= crystals[var3]; --var3) {
                    }
                }

                if (var3 >= crystals.length - 1) {
                    var3 = crystals.length - 2;
                }

                if (gameHP) {
                    if (var8 > 1) {
                        mFont.tahoma_7_yellow.gameAA(var1, mResources.gameHS + " " + (var3 + 2) + " " + (var4 ? mResources.gameBM : ""), gameTD + 3, gameTE + gameTK * gameHK + 9, 0);
                        if (coinUpCrystals[var3 + 1] > Char.getMyChar().xu) {
                            mFont.tahoma_7_red.gameAA(var1, mResources.gameAA(mResources.gameHV, NinjaUtil.gameAA(String.valueOf(coinUpCrystals[var3 + 1]))), gameTD + 3, gameTE + gameTK * gameHK + 21, 0);
                        } else {
                            mFont.tahoma_7_yellow.gameAA(var1, mResources.gameAA(mResources.gameHV, NinjaUtil.gameAA(String.valueOf(coinUpCrystals[var3 + 1]))), gameTD + 3, gameTE + gameTK * gameHK + 21, 0);
                        }

                        mFont.tahoma_7_yellow.gameAA(var1, mResources.gameHW + ": " + var7 * 100 / crystals[var3 + 1] + "%", gameTD + 3, gameTE + gameTK * gameHK + 33, 0);
                    } else {
                        for (var5 = 1; var5 <= 2; ++var5) {
                            mFont.tahoma_7_white.gameAA(var1, mResources.gameHO[var5], gameTD + 3, gameTE + gameTK * gameHK + 5 + (var5 - 1) * 12, 0);
                        }
                    }
                } else if (var8 > 1) {
                    mFont.tahoma_7_yellow.gameAA(var1, mResources.gameHS + " " + (var3 + 2) + " " + mResources.gameBM, gameTD + 3, gameTE + gameTK * gameHK + 9, 0);
                    if (coinUpCrystals[var3 + 1] > Char.getMyChar().xu + Char.getMyChar().yen) {
                        mFont.tahoma_7_red.gameAA(var1, mResources.gameAA(mResources.gameHT, NinjaUtil.gameAA(String.valueOf(coinUpCrystals[var3 + 1]))), gameTD + 3, gameTE + gameTK * gameHK + 21, 0);
                    } else {
                        mFont.tahoma_7_yellow.gameAA(var1, mResources.gameAA(mResources.gameHT, NinjaUtil.gameAA(String.valueOf(coinUpCrystals[var3 + 1]))), gameTD + 3, gameTE + gameTK * gameHK + 21, 0);
                    }

                    mFont.tahoma_7_yellow.gameAA(var1, mResources.gameHW + ": " + var7 * 100 / crystals[var3 + 1] + "%", gameTD + 3, gameTE + gameTK * gameHK + 33, 0);
                } else {
                    for (var5 = 0; var5 < 3; ++var5) {
                        mFont.tahoma_7_white.gameAA(var1, mResources.gameHO[var5], gameTD + 3, gameTE + gameTK * gameHK + 5 + var5 * 12, 0);
                    }
                }
            }

            var1.gameAA(0);
            var1.gameAC(gameTD, gameTE, gameTJ * gameHK + 1, gameTK * gameHK + 1);

            for (var7 = 0; var7 < gameTK; ++var7) {
                for (var8 = 0; var8 < gameTJ; ++var8) {
                    SmallImage.gameAA(var1, 154, gameTD + var8 * gameHK + gameHK / 2, gameTE + var7 * gameHK + gameHK / 2, 0, 3);
                    var1.gameAA(12281361);
                    var1.gameAB(gameTD + var8 * gameHK, gameTE + var7 * gameHK, gameHK, gameHK);
                }
            }

            for (var7 = 0; var7 < arrItemUpPeal.length; ++var7) {
                Item var9;
                if ((var9 = arrItemUpPeal[var7]) != null) {
                    var3 = var7 / gameTJ;
                    int var10 = var7 - var3 * gameTJ;
                    if (!var9.isLock) {
                        var1.gameAA(4543829);
                        var1.gameAC(gameTD + var10 * gameHK + 1, gameTE + var3 * gameHK + 1, gameHK - 1, gameHK - 1);
                    }

                    SmallImage.gameAA(var1, var9.template.iconID, gameTD + var10 * gameHK + gameHK / 2, gameTE + var3 * gameHK + gameHK / 2, 0, 3);
                }
            }

            if (gameHL > 0) {
                var7 = indexSelect / gameTJ;
                var8 = indexSelect - var7 * gameTJ;
                var1.gameAA(16777215);
                var1.gameAB(gameTD + var8 * gameHK, gameTE + var7 * gameHK, gameHK, gameHK);
            }

            if (effUpok != null) {
                SmallImage.gameAA(var1, effUpok.arrEfInfo[indexEff].idImg, gameTD + gameHK / 2 + effUpok.arrEfInfo[indexEff].dx + 1, gameTE + gameHK / 2 + 9 + effUpok.arrEfInfo[indexEff].dy, 0, 3);
                if (GameCanvas.gameTick % 2 == 0 && ++indexEff >= effUpok.arrEfInfo.length) {
                    indexEff = 0;
                    effUpok = null;
                    return;
                }
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    private void gameAP(mGraphics var1) {
        if (gameMG) {
            gameAB(var1);
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            int var2 = popupW;
            if (GameCanvas.gameAJ) {
                var2 = popupW / 2 + 20;
            }

            var1.gameAA(0);
            var1.gameAC(popupX + 7, popupY + 31, var2 - 14, popupH - 58);
            var1.gameAA(-3170504);
            var1.gameAB(popupX + 8, popupY + 32, var2 - 16, popupH - 60);
            var1.gameAA(Paint.COLORBACKGROUND);
            var1.gameAC(popupX + 9, popupY + 33, var2 - 18, popupH - 62);
            gameAA(var1, mResources.gameVQ, false);
            gameTD = popupX + 33;
            gameTE = popupY + 40;

            int var3;
            for (var2 = 0; var2 < 3; ++var2) {
                for (var3 = 0; var3 < 3; ++var3) {
                    var1.gameAA(Paint.COLORDARK);
                    var1.gameAC(gameTD + var3 * 40, gameTE + 10 + var2 * 40, 29, 29);
                    var1.gameAA(-6527695);
                    var1.gameAB(gameTD + var3 * 40, gameTE + 10 + var2 * 40, 29, 29);
                    var1.gameAA(-6737152);
                    var1.gameAC(gameTD + var3 * 40 + 2, gameTE + 12 + var2 * 40, 26, 26);
                    var1.gameAA(Paint.COLORDARK);
                    var1.gameAC(gameTD + var3 * 40 + 4, gameTE + 14 + var2 * 40, 22, 22);
                    SmallImage.gameAA(var1, 1414, gameTD + var3 * 40 + 20 - 5, gameTE + var2 * 40 + 20 + 4, 0, StaticObj.VCENTER_HCENTER);
                }
            }

            for (var2 = 0; var2 < 9; ++var2) {
                mGraphics var10000;
                int var10001;
                int var4;
                label75:
                {
                    var3 = var2 / 3;
                    var4 = var2 - var3 * 3;
                    if (arrItemSprin != null) {
                        var1.gameAA(-16770791);
                        var1.gameAC(gameTD + var4 * 40 + 4, gameTE + 14 + var3 * 40, 22, 22);
                        var1.gameAA(var2 == indexSelect ? -1 : -6527695);
                        var1.gameAB(gameTD + var4 * 40 + 4, gameTE + 14 + var3 * 40, 21, 21);
                        SmallImage.gameAA(var1, 154, gameTD + var4 * 40 + 17 - 3, gameTE + 7 + var3 * 40 + 17, 0, 3);
                        if (System.currentTimeMillis() - this.timePoint < 1000L) {
                            if (var2 == indexCard) {
                                SmallImage.gameAA(var1, ItemTemplates.gameAB(arrItemSprin[indexCard]), gameTD + var4 * 40 + 17 - 3, gameTE + 7 + var3 * 40 + 17, 0, 3);
                            } else {
                                SmallImage.gameAA(var1, 1414, gameTD + var4 * 40 + 17 - 2, gameTE + 7 + var3 * 40 + 17, 0, StaticObj.VCENTER_HCENTER);
                            }
                        } else if (arrItemSprin[var2] >= 0 && arrItemSprin[var2] < ItemTemplates.itemTemplates.gameAA.size()) {
                            SmallImage.gameAA(var1, ItemTemplates.gameAB(arrItemSprin[var2]), gameTD + var4 * 40 + 17 - 3, gameTE + 7 + var3 * 40 + 17, 0, 3);
                        } else {
                            SmallImage.gameAA(var1, ItemTemplates.gameAB((short) 242), gameTD + var4 * 40 + 17 - 3, gameTE + 7 + var3 * 40 + 17, 0, 3);
                        }

                        if (indexCard == var2 && this.yenTemp > 0) {
                            this.yenValue[var2] = String.valueOf(this.yenTemp);
                        }

                        if (gameHL != 1) {
                            continue;
                        }

                        if (indexCard == var2 && GameCanvas.gameTick % 10 > 4) {
                            var10000 = var1;
                            var10001 = -3368653;
                            break label75;
                        }

                        if (var2 == indexSelect) {
                            var10000 = var1;
                            var10001 = -1;
                            break label75;
                        }

                        var10000 = var1;
                    } else {
                        if (gameHL != 1) {
                            continue;
                        }

                        var10000 = var1;
                        if (var2 == indexSelect) {
                            var10001 = -1;
                            break label75;
                        }
                    }

                    var10001 = Paint.COLORLIGHT;
                }

                var10000.gameAA(var10001);
                var1.gameAB(gameTD + var4 * 40, gameTE + 10 + var3 * 40, 29, 29);
            }

            mFont.tahoma_7_yellow.gameAA(var1, mResources.gameVR + this.numSprinLeft, popupX + popupW / 2, popupY + popupH - 20, 2);
        }

    }

    private void gameAQ(mGraphics var1) {
        if (gameKS) {
            if (indexMenu == 0) {
                this.gameAA(var1, mResources.gameGS, arrItemStack);
                return;
            }

            if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameGS);
            }
        }

    }

    private void gameAR(mGraphics var1) {
        if (gameKT) {
            if (indexMenu == 0) {
                this.gameAA(var1, mResources.gameGT, arrItemStackLock);
                return;
            }

            if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameGT);
            }
        }

    }

    private void gameAS(mGraphics var1) {
        if (gameKU) {
            if (indexMenu == 0) {
                this.gameAA(var1, mResources.gameGU, arrItemGrocery);
                return;
            }

            if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameGU);
            }
        }

    }

    private void gameAT(mGraphics var1) {
        if (gameKV) {
            if (indexMenu == 0) {
                this.gameAA(var1, mResources.gameGV, arrItemGroceryLock);
                return;
            }

            if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameGV);
            }
        }

    }

    private void gameAU(mGraphics var1) {
        if (gameKW) {
            if (indexMenu == 0) {
                String[] var2 = mResources.gameGW;
                var1 = var1;
                GameScr var8 = this;

                try {
                    gameTK = 3;
                    Paint.gameAA(popupX, popupY, popupW, popupH, var1);
                    gameAA(var1, var2[indexMenu], var2.length > 1);
                    gameTD = popupX + 3;
                    gameTE = popupY + 34 + gameHK;
                    int var10 = popupX + 45;
                    int var3 = popupX + 100;
                    int var4 = gameTE - gameHK - 3;
                    if (itemUpGrade != null) {
                        var8.gameAA(var1, itemUpGrade, var10, var4);
                        var1.gameAA(12281361);
                        var1.gameAB(var10, var4, gameHK, gameHK);
                        mFont.tahoma_7_yellow.gameAA(var1, "(+" + itemUpGrade.upgrade + ")", var10 - 5, var4 + gameHK / 2 - 5, 1);
                    } else {
                        var1.gameAA(6425);
                        var1.gameAC(var10 - 1, var4 - 1, gameHK + 3, gameHK + 3);
                        SmallImage.gameAA(var1, 154, var10 + gameHK / 2, var4 + gameHK / 2, 0, 3);
                        var1.gameAA(12281361);
                        var1.gameAB(var10, var4, gameHK, gameHK);
                    }

                    SmallImage.gameAA(var1, 942, var10 + 43, gameTE - 15, 0, StaticObj.VCENTER_HCENTER);
                    if (itemUpGrade != null && !itemUpGrade.isUpMax()) {
                        var8.gameAA(var1, itemUpGrade, var3, var4, 1, 0);
                        var1.gameAA(12281361);
                        var1.gameAB(var3, var4, gameHK, gameHK);
                        mFont.tahoma_7_yellow.gameAA(var1, "(+" + (itemUpGrade.upgrade + 1) + ")", var3 + gameHK + 10, var4 + gameHK / 2 - 5, 0);
                    } else {
                        var1.gameAA(6425);
                        var1.gameAC(var3 - 1, var4 - 1, gameHK + 3, gameHK + 3);
                        SmallImage.gameAA(var1, 154, var3 + gameHK / 2, var4 + gameHK / 2, 0, 3);
                        var1.gameAA(12281361);
                        var1.gameAB(var3, var4, gameHK, gameHK);
                    }

                    if (gameHL == 1) {
                        if (indexSelect == 0) {
                            var1.gameAA(16777215);
                            var1.gameAB(var10, var4, gameHK, gameHK);
                        }

                        if (indexSelect == 1) {
                            var1.gameAA(16777215);
                            var1.gameAB(var3, var4, gameHK, gameHK);
                        }
                    }

                    int var5;
                    int var9;
                    if (itemUpGrade == null) {
                        for (var9 = 0; var9 < 3; ++var9) {
                            mFont.tahoma_7_white.gameAA(var1, mResources.gameHP[var9], gameTD, gameTE + gameTK * gameHK + 5 + var9 * 12, 0);
                        }
                    } else if (itemUpGrade.isUpMax()) {
                        if (!GameCanvas.gameAJ) {
                            mFont.tahoma_7_yellow.gameAA(var1, mResources.gameHP[3], popupX + popupW / 2, gameTE + gameTK * gameHK + 5, 2);
                        } else {
                            mFont.tahoma_7_yellow.gameAA(var1, mResources.gameHP[3], popupX + 7, gameTE + gameTK * gameHK + 5, 0);
                        }
                    } else {
                        var9 = 0;

                        for (var3 = 0; var3 < arrItemUpGrade.length; ++var3) {
                            if (arrItemUpGrade[var3] != null && arrItemUpGrade[var3].template.type == 26) {
                                var9 += crystals[arrItemUpGrade[var3].template.id];
                            }
                        }

                        var5 = 0;
                        if (itemUpGrade.isTypeClothe()) {
                            if ((var3 = var9 * 100 / upClothe[itemUpGrade.upgrade]) > maxPercents[itemUpGrade.upgrade]) {
                                var3 = maxPercents[itemUpGrade.upgrade];
                            }

                            if (gameKY) {
                                var3 = (int) ((double) var3 * 1.5D);
                                var5 = goldUps[itemUpGrade.upgrade];
                            }

                            if (coinUpClothes[itemUpGrade.upgrade] > Char.getMyChar().xu + Char.getMyChar().yen) {
                                mFont.tahoma_7_red.gameAA(var1, mResources.gameAA(mResources.gameHT, NinjaUtil.gameAA(String.valueOf(coinUpClothes[itemUpGrade.upgrade]))), gameTD, gameTE + gameTK * gameHK + 5, 0);
                            } else {
                                mFont.tahoma_7_yellow.gameAA(var1, mResources.gameAA(mResources.gameHT, NinjaUtil.gameAA(String.valueOf(coinUpClothes[itemUpGrade.upgrade]))), gameTD, gameTE + gameTK * gameHK + 5, 0);
                            }

                            if (var5 > Char.getMyChar().luong) {
                                mFont.tahoma_7_red.gameAA(var1, mResources.gameAA(mResources.gameHU, String.valueOf(var5)), gameTD, gameTE + gameTK * gameHK + 17, 0);
                            } else {
                                mFont.tahoma_7_yellow.gameAA(var1, mResources.gameAA(mResources.gameHU, String.valueOf(var5)), gameTD, gameTE + gameTK * gameHK + 17, 0);
                            }

                            mFont.tahoma_7_yellow.gameAA(var1, mResources.gameHW + ": " + var3 + "%", gameTD, gameTE + gameTK * gameHK + 29, 0);
                        } else if (itemUpGrade.isTypeAdorn()) {
                            if ((var3 = var9 * 100 / upAdorn[itemUpGrade.upgrade]) > maxPercents[itemUpGrade.upgrade]) {
                                var3 = maxPercents[itemUpGrade.upgrade];
                            }

                            if (gameKY) {
                                var3 = (int) ((double) var3 * 1.5D);
                                var5 = goldUps[itemUpGrade.upgrade];
                            }

                            if (coinUpAdorns[itemUpGrade.upgrade] > Char.getMyChar().xu + Char.getMyChar().yen) {
                                mFont.tahoma_7_red.gameAA(var1, mResources.gameAA(mResources.gameHT, NinjaUtil.gameAA(String.valueOf(coinUpAdorns[itemUpGrade.upgrade]))), gameTD, gameTE + gameTK * gameHK + 5, 0);
                            } else {
                                mFont.tahoma_7_yellow.gameAA(var1, mResources.gameAA(mResources.gameHT, NinjaUtil.gameAA(String.valueOf(coinUpAdorns[itemUpGrade.upgrade]))), gameTD, gameTE + gameTK * gameHK + 5, 0);
                            }

                            if (var5 > Char.getMyChar().luong) {
                                mFont.tahoma_7_red.gameAA(var1, mResources.gameAA(mResources.gameHU, String.valueOf(var5)), gameTD, gameTE + gameTK * gameHK + 17, 0);
                            } else {
                                mFont.tahoma_7_yellow.gameAA(var1, mResources.gameAA(mResources.gameHU, String.valueOf(var5)), gameTD, gameTE + gameTK * gameHK + 17, 0);
                            }

                            mFont.tahoma_7_yellow.gameAA(var1, mResources.gameHW + ": " + var3 + "%", gameTD, gameTE + gameTK * gameHK + 29, 0);
                        } else if (itemUpGrade.isTypeWeapon()) {
                            if ((var3 = var9 * 100 / upWeapon[itemUpGrade.upgrade]) > maxPercents[itemUpGrade.upgrade]) {
                                var3 = maxPercents[itemUpGrade.upgrade];
                            }

                            if (gameKY) {
                                var3 = (int) ((double) var3 * 1.5D);
                                var5 = goldUps[itemUpGrade.upgrade];
                            }

                            if (coinUpWeapons[itemUpGrade.upgrade] > Char.getMyChar().xu + Char.getMyChar().yen) {
                                mFont.tahoma_7_red.gameAA(var1, mResources.gameAA(mResources.gameHT, NinjaUtil.gameAA(String.valueOf(coinUpWeapons[itemUpGrade.upgrade]))), gameTD, gameTE + gameTK * gameHK + 5, 0);
                            } else {
                                mFont.tahoma_7_yellow.gameAA(var1, mResources.gameAA(mResources.gameHT, NinjaUtil.gameAA(String.valueOf(coinUpWeapons[itemUpGrade.upgrade]))), gameTD, gameTE + gameTK * gameHK + 5, 0);
                            }

                            if (var5 > Char.getMyChar().luong) {
                                mFont.tahoma_7_red.gameAA(var1, mResources.gameAA(mResources.gameHU, String.valueOf(var5)), gameTD, gameTE + gameTK * gameHK + 17, 0);
                            } else {
                                mFont.tahoma_7_yellow.gameAA(var1, mResources.gameAA(mResources.gameHU, String.valueOf(var5)), gameTD, gameTE + gameTK * gameHK + 17, 0);
                            }

                            mFont.tahoma_7_yellow.gameAA(var1, mResources.gameHW + ": " + var3 + "%", gameTD, gameTE + gameTK * gameHK + 29, 0);
                        }
                    }

                    var1.gameAA(0);
                    var1.gameAC(gameTD - 1, gameTE - 1, gameTJ * gameHK + 3, gameTK * gameHK + 3);

                    for (var9 = 0; var9 < gameTK; ++var9) {
                        for (var3 = 0; var3 < gameTJ; ++var3) {
                            SmallImage.gameAA(var1, 154, gameTD + var3 * gameHK + gameHK / 2, gameTE + var9 * gameHK + gameHK / 2, 0, 3);
                            var1.gameAA(12281361);
                            var1.gameAB(gameTD + var3 * gameHK, gameTE + var9 * gameHK, gameHK, gameHK);
                        }
                    }

                    if (gameHL == 2) {
                        var9 = indexSelect / gameTJ;
                        var3 = indexSelect - var9 * gameTJ;
                        var1.gameAA(16777215);
                        var1.gameAB(gameTD + var3 * gameHK, gameTE + var9 * gameHK, gameHK, gameHK);
                    }

                    for (var9 = 0; var9 < arrItemUpGrade.length; ++var9) {
                        Item var11;
                        if ((var11 = arrItemUpGrade[var9]) != null) {
                            var5 = var9 / gameTJ;
                            int var6 = var9 - var5 * gameTJ;
                            if (!var11.isLock) {
                                var1.gameAA(12083);
                                var1.gameAC(gameTD + var6 * gameHK + 1, gameTE + var5 * gameHK + 1, gameHK - 1, gameHK - 1);
                            }

                            SmallImage.gameAA(var1, var11.template.iconID, gameTD + var6 * gameHK + gameHK / 2, gameTE + var5 * gameHK + gameHK / 2, 0, 3);
                        }
                    }

                    if (effUpok == null) {
                        return;
                    }

                    SmallImage.gameAA(var1, effUpok.arrEfInfo[indexEff].idImg, var10 + gameHK / 2 + effUpok.arrEfInfo[indexEff].dx + 1, var4 + gameHK / 2 + 9 + effUpok.arrEfInfo[indexEff].dy, 0, 3);
                    if (GameCanvas.gameTick % 2 != 0 || ++indexEff < effUpok.arrEfInfo.length) {
                        return;
                    }

                    indexEff = 0;
                    effUpok = null;
                } catch (Exception var7) {
                    var7.printStackTrace();
                    return;
                }
            } else if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameGW);
            }
        }

    }

    private void gameAV(mGraphics var1) {
        if (gameDN) {
            if (indexMenu == 0) {
                String[] var2 = mResources.gameGX;
                var1 = var1;
                GameScr var5 = this;

                try {
                    gameTK = 5;
                    Paint.gameAA(popupX, popupY, popupW, popupH, var1);
                    var5.gameAA(var1, var2, false);
                    var1.gameAA(6693376);
                    var1.gameAC(popupX + 3, popupY + 32, 168, 140);
                    var1.gameAA(13408563);
                    var1.gameAB(popupX + 3, popupY + 32, 168, 140);
                    int var6 = popupX + 74;
                    int var3 = popupY + 40 + gameHK;
                    mFont.tahoma_7_white.gameAA(var1, mResources.gameRU, var6 + gameHK / 2, var3 - gameHK / 2 - 4, 2);
                    if (itemSell != null) {
                        var1.gameAA(6425);
                        var1.gameAC(var6 - 1, var3 - 1, gameHK + 3, gameHK + 3);
                        SmallImage.gameAA(var1, 154, var6 + gameHK / 2, var3 + gameHK / 2, 0, 3);
                        var5.gameAA(var1, itemSell, var6, var3);
                        if (itemSell.quantity > 1) {
                            mFont.number_yellow.gameAA(var1, String.valueOf(itemSell.quantity), var6 + gameHK, var3 + gameHK / 2 + 6, 1);
                        }

                        var1.gameAA(gameHL == 1 ? 16777215 : 12281361);
                        var1.gameAB(var6, var3, gameHK, gameHK);
                    } else {
                        var1.gameAA(6425);
                        var1.gameAC(var6 - 1, var3 - 1, gameHK + 3, gameHK + 3);
                        SmallImage.gameAA(var1, 154, var6 + gameHK / 2, var3 + gameHK / 2, 0, 3);
                        var1.gameAA(12281361);
                        var1.gameAB(var6, var3, gameHK, gameHK);
                    }

                    mFont.tahoma_7_white.gameAA(var1, mResources.gameRV, var6 + gameHK / 2, var3 + 3 * gameHK / 2 + 2, 2);
                    if (Char.getMyChar().xu < 5000) {
                        mFont.tahoma_7_red.gameAA(var1, mResources.gameVF, var6 + gameHK / 2, popupY + popupH - 25, 2);
                        mFont.tahoma_7_red.gameAA(var1, mResources.gameVG, var6 + gameHK / 2, popupY + popupH - 13, 2);
                    } else {
                        mFont.tahoma_7_white.gameAA(var1, mResources.gameVF, var6 + gameHK / 2, popupY + popupH - 25, 2);
                        mFont.tahoma_7_white.gameAA(var1, mResources.gameVG, var6 + gameHK / 2, popupY + popupH - 13, 2);
                    }

                    var5.gameND.x = popupX + 40;
                    var5.gameND.y = popupY + 130;
                    var5.gameND.paint(var1);
                } catch (Exception var4) {
                    var4.printStackTrace();
                    return;
                }
            } else if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameGX);
            }
        }

    }

    private void gameAW(mGraphics var1) {
        if (gameKX) {
            if (indexMenu == 0) {
                String[] var2 = mResources.gameGY;
                var1 = var1;
                GameScr var7 = this;

                try {
                    gameTK = 3;
                    Paint.gameAA(popupX, popupY, popupW, popupH, var1);
                    gameAA(var1, var2[indexMenu], var2.length > 1);
                    gameTD = popupX + 3;
                    gameTE = popupY + 34 + gameHK;
                    int var9 = popupX + 45;
                    int var3 = popupX + 100;
                    int var4 = gameTE - gameHK - 3;
                    if (arrItemConvert[0] != null) {
                        var7.gameAA(var1, arrItemConvert[0], var9, var4);
                        var1.gameAA(12281361);
                        var1.gameAB(var9, var4, gameHK, gameHK);
                        mFont.tahoma_7_yellow.gameAA(var1, "(+" + arrItemConvert[0].upgrade + ")", var9 - 5, var4 + gameHK / 2 - 5, 1);
                    } else {
                        var1.gameAA(6425);
                        var1.gameAC(var9 - 1, var4 - 1, gameHK + 3, gameHK + 3);
                        SmallImage.gameAA(var1, 154, var9 + gameHK / 2, var4 + gameHK / 2, 0, 3);
                        var1.gameAA(12281361);
                        var1.gameAB(var9, var4, gameHK, gameHK);
                    }

                    SmallImage.gameAA(var1, 942, var9 + 43, gameTE - 15, 0, StaticObj.VCENTER_HCENTER);
                    Item var5;
                    if (arrItemConvert[1] != null) {
                        var5 = arrItemConvert[1].clone();
                        if (arrItemConvert[0] != null && arrItemConvert[0].template.type == var5.template.type && arrItemConvert[1].template.level >= arrItemConvert[0].template.level) {
                            var5.upgrade = arrItemConvert[0].upgrade;
                        }

                        var7.gameAA(var1, var5, var3, var4);
                        var1.gameAA(12281361);
                        var1.gameAB(var3, var4, gameHK, gameHK);
                        mFont.tahoma_7_yellow.gameAA(var1, "(+" + var5.upgrade + ")", var3 + gameHK + 10, var4 + gameHK / 2 - 5, 0);
                    } else {
                        var1.gameAA(6425);
                        var1.gameAC(var3 - 1, var4 - 1, gameHK + 3, gameHK + 3);
                        SmallImage.gameAA(var1, 154, var3 + gameHK / 2, var4 + gameHK / 2, 0, 3);
                        var1.gameAA(12281361);
                        var1.gameAB(var3, var4, gameHK, gameHK);
                    }

                    if (gameHL == 1) {
                        if (indexSelect == 0) {
                            var1.gameAA(16777215);
                            var1.gameAB(var9, var4, gameHK, gameHK);
                        }

                        if (indexSelect == 1) {
                            var1.gameAA(16777215);
                            var1.gameAB(var3, var4, gameHK, gameHK);
                        }
                    }

                    var1.gameAA(0);
                    var1.gameAC(gameTD - 1, gameTE - 1, gameTJ * gameHK + 3, gameTK * gameHK + 3);
                    int var10 = 0;

                    while (true) {
                        int var8;
                        if (var10 >= gameTK) {
                            if ((var5 = arrItemConvert[2]) != null) {
                                var8 = 0 / gameTJ;
                                var9 = 0 - var8 * gameTJ;
                                if (!var5.isLock) {
                                    var1.gameAA(12083);
                                    var1.gameAC(gameTD + var9 * gameHK + 1, gameTE + var8 * gameHK + 1, gameHK - 1, gameHK - 1);
                                }

                                SmallImage.gameAA(var1, var5.template.iconID, gameTD + var9 * gameHK + gameHK / 2, gameTE + var8 * gameHK + gameHK / 2, 0, 3);
                            }

                            mFont.tahoma_7_white.gameAA(var1, "- " + mResources.gameFQ[0], gameTD, gameTE + gameTK * gameHK + 10, 0);
                            mFont.tahoma_7_white.gameAA(var1, "  " + mResources.gameFQ[1], gameTD, gameTE + gameTK * gameHK + 22, 0);
                            mFont.tahoma_7_white.gameAA(var1, "- " + mResources.gameFQ[2], gameTD, gameTE + gameTK * gameHK + 34, 0);
                            if (gameHL != 2) {
                                return;
                            }

                            var8 = indexSelect / gameTJ;
                            var9 = indexSelect - var8 * gameTJ;
                            var1.gameAA(16777215);
                            var1.gameAB(gameTD + var9 * gameHK, gameTE + var8 * gameHK, gameHK, gameHK);
                            break;
                        }

                        for (var8 = 0; var8 < gameTJ; ++var8) {
                            SmallImage.gameAA(var1, 154, gameTD + var8 * gameHK + gameHK / 2, gameTE + var10 * gameHK + gameHK / 2, 0, 3);
                            var1.gameAA(12281361);
                            var1.gameAB(gameTD + var8 * gameHK, gameTE + var10 * gameHK, gameHK, gameHK);
                        }

                        ++var10;
                    }
                } catch (Exception var6) {
                    var6.printStackTrace();
                    return;
                }
            } else if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameGW);
            }
        }

    }

    private void gameAX(mGraphics var1) {
        if (gameMB) {
            if (indexMenu == 0) {
                this.gameAB(var1, mResources.gameHD);
                return;
            }

            if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameHD);
            }
        }

    }

    private void gameAY(mGraphics var1) {
        if (gameMK) {
            if (indexMenu == 0) {
                this.gameAB(var1, mResources.gameHG);
                return;
            }

            if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameHG);
            }
        }

    }

    private void gameAZ(mGraphics var1) {
        if (gameMJ) {
            if (indexMenu == 0) {
                this.gameAB(var1, mResources.gameHH);
                return;
            }

            if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameHH);
            }
        }

    }

    private void gameBA(mGraphics var1) {
        if (isPaintTrade) {
            if (indexMenu == 0) {
                String[] var2 = mResources.gameHE;
                var1 = var1;
                GameScr var7 = this;

                try {
                    Paint.gameAA(popupX, popupY, popupW, popupH, var1);
                    gameAA(var1, var2[indexMenu], var2.length > 1);
                    gameTD = popupX + 3;
                    gameTE = popupY + 45;
                    gameTK = 4;
                    int var10003 = gameTD + 1;
                    int var10004 = gameTE - 12;
                    mFont.tahoma_7_yellow.gameAA(var1, Char.getMyChar().cName, var10003, var10004, 0);
                    int var8 = gameTD;

                    int var3;
                    for (var3 = 0; var3 < 3; ++var3) {
                        if (var3 == var7.typeTrade) {
                            mFont.tahoma_7_blue1.gameAA(var1, String.valueOf(var3 + 1), var8 + 2 + var3 * 20, gameTE + gameTK * (gameHK + 3) + 8, 0);
                        } else {
                            mFont.tahoma_7_grey.gameAA(var1, String.valueOf(var3 + 1), var8 + 2 + var3 * 20, gameTE + gameTK * (gameHK + 3) + 8, 0);
                        }

                        if (var3 < 2) {
                            SmallImage.gameAA(var1, 942, var8 + 14 + var3 * 20, gameTE + gameTK * (gameHK + 3) + 13, 0, StaticObj.VCENTER_HCENTER);
                        }
                    }

                    mFont.tahoma_7_white.gameAA(var1, NinjaUtil.gameAA(String.valueOf(var7.coinTrade)) + " " + mResources.gamePA, gameTD, gameTE + gameTK * gameHK + 4, 0);
                    if (var7.typeTrade == 0) {
                        var1.gameAA(0);
                    }

                    if (var7.typeTrade == 1) {
                        var1.gameAA(210986);
                    }

                    if (var7.typeTrade == 2) {
                        var1.gameAA(805690);
                    }

                    var1.gameAC(gameTD - 1, gameTE - 1, gameHK * 3 + 3, (gameHK << 2) + 3);

                    for (var3 = 0; var3 < gameTK; ++var3) {
                        for (var8 = 0; var8 < 3; ++var8) {
                            SmallImage.gameAA(var1, 154, gameTD + var8 * gameHK + gameHK / 2, gameTE + var3 * gameHK + gameHK / 2, 0, 3);
                            var1.gameAA(12281361);
                            var1.gameAB(gameTD + var8 * gameHK, gameTE + var3 * gameHK, gameHK, gameHK);
                        }
                    }

                    if (gameHL == 1) {
                        var3 = indexSelect / 3;
                        var8 = indexSelect - var3 * 3;
                        var1.gameAA(16777215);
                        var1.gameAB(gameTD + var8 * gameHK, gameTE + var3 * gameHK, gameHK, gameHK);
                    }

                    int var4;
                    int var5;
                    Item var9;
                    if (arrItemTradeMe != null) {
                        for (var3 = 0; var3 < arrItemTradeMe.length; ++var3) {
                            if ((var9 = arrItemTradeMe[var3]) != null) {
                                var4 = var3 / 3;
                                var5 = var3 - var4 * 3;
                                if (!var9.isLock) {
                                    var1.gameAA(12083);
                                    var1.gameAC(gameTD + var5 * gameHK + 1, gameTE + var4 * gameHK + 1, gameHK - 1, gameHK - 1);
                                }

                                if (GameCanvas.gameTick % 6 == 0) {
                                    var9.indexFrame = (var9.indexFrame + 1) % 3;
                                }

                                if (var9.isNewitem()) {
                                    var1.gameAA(GameCanvas.gameBY, gameTD + var5 * gameHK + gameHK / 2, gameTE + var4 * gameHK + gameHK / 2, 3);
                                }

                                SmallImage.gameAA(var1, var9.template.iconID, gameTD + var5 * gameHK + gameHK / 2, gameTE + var4 * gameHK + gameHK / 2, 0, 3, var9.indexFrame);
                                if (var9.quantity > 1) {
                                    mFont.number_yellow.gameAA(var1, String.valueOf(var9.quantity), gameTD + var5 * gameHK + gameHK, gameTE + var4 * gameHK + gameHK - mFont.number_yellow.gameAC(), 1);
                                }

                                if (var9.quantity > 1) {
                                    mFont.number_yellow.gameAA(var1, String.valueOf(var9.quantity), gameTD + var5 * gameHK + gameHK, gameTE + var4 * gameHK + gameHK - mFont.number_yellow.gameAC(), 1);
                                }
                            }
                        }
                    }

                    gameTD = popupX + popupW - 2 - gameHK * 3;
                    gameTK = 4;
                    mFont.tahoma_7_yellow.gameAA(var1, var7.tradeName, popupX + popupW - 2, gameTE - 12, 1);
                    var8 = popupX + popupW - 3 - 60;

                    for (var3 = 0; var3 < 3; ++var3) {
                        if (var3 == var7.typeTradeOrder) {
                            mFont.tahoma_7_blue1.gameAA(var1, String.valueOf(var3 + 1), var8 + 2 + var3 * 20, gameTE + gameTK * (gameHK + 3) + 8, 0);
                        } else {
                            mFont.tahoma_7_grey.gameAA(var1, String.valueOf(var3 + 1), var8 + 2 + var3 * 20, gameTE + gameTK * (gameHK + 3) + 8, 0);
                        }

                        if (var3 < 2) {
                            SmallImage.gameAA(var1, 942, var8 + 14 + var3 * 20, gameTE + gameTK * (gameHK + 3) + 13, 0, StaticObj.VCENTER_HCENTER);
                        }
                    }

                    mFont.tahoma_7_white.gameAA(var1, NinjaUtil.gameAA(String.valueOf(var7.coinTradeOrder)) + " " + mResources.gamePA, popupX + popupW - 2, gameTE + gameTK * gameHK + 4, 1);
                    if (var7.typeTradeOrder == 0) {
                        var1.gameAA(0);
                    }

                    if (var7.typeTradeOrder == 1) {
                        var1.gameAA(210986);
                    }

                    if (var7.typeTradeOrder == 2) {
                        var1.gameAA(805690);
                    }

                    var1.gameAC(gameTD - 1, gameTE - 1, gameHK * 3 + 3, (gameHK << 2) + 3);

                    for (var3 = 0; var3 < gameTK; ++var3) {
                        for (var8 = 0; var8 < 3; ++var8) {
                            SmallImage.gameAA(var1, 154, gameTD + var8 * gameHK + gameHK / 2, gameTE + var3 * gameHK + gameHK / 2, 0, 3);
                            var1.gameAA(12281361);
                            var1.gameAB(gameTD + var8 * gameHK, gameTE + var3 * gameHK, gameHK, gameHK);
                        }
                    }

                    if (gameHL == 2) {
                        var3 = indexSelect / 3;
                        var8 = indexSelect - var3 * 3;
                        var1.gameAA(16777215);
                        var1.gameAB(gameTD + var8 * gameHK, gameTE + var3 * gameHK, gameHK, gameHK);
                    }

                    if (arrItemTradeOrder != null) {
                        for (var3 = 0; var3 < arrItemTradeOrder.length; ++var3) {
                            if ((var9 = arrItemTradeOrder[var3]) != null) {
                                var4 = var3 / 3;
                                var5 = var3 - var4 * 3;
                                if (!var9.isLock) {
                                    var1.gameAA(12083);
                                    var1.gameAC(gameTD + var5 * gameHK + 1, gameTE + var4 * gameHK + 1, gameHK - 1, gameHK - 1);
                                }

                                if (GameCanvas.gameTick % 6 == 0) {
                                    var9.indexFrame = (var9.indexFrame + 1) % 3;
                                }

                                if (var9.isNewitem()) {
                                    var1.gameAA(GameCanvas.gameBY, gameTD + var5 * gameHK + gameHK / 2, gameTE + var4 * gameHK + gameHK / 2, 3);
                                }

                                SmallImage.gameAA(var1, var9.template.iconID, gameTD + var5 * gameHK + gameHK / 2, gameTE + var4 * gameHK + gameHK / 2, 0, 3, var9.indexFrame);
                                if (var9.quantity > 1) {
                                    mFont.number_yellow.gameAA(var1, String.valueOf(var9.quantity), gameTD + var5 * gameHK + gameHK, gameTE + var4 * gameHK + gameHK - mFont.number_yellow.gameAC(), 1);
                                }

                                if (var9.quantity > 1) {
                                    mFont.number_yellow.gameAA(var1, String.valueOf(var9.quantity), gameTD + var5 * gameHK + gameHK, gameTE + var4 * gameHK + gameHK - mFont.number_yellow.gameAC(), 1);
                                }
                            }
                        }
                    }

                    var3 = (int) (System.currentTimeMillis() / 1000L);
                    if (var7.timeTrade - var3 > 0 && var7.typeTrade == 1 && var7.typeTradeOrder == 1) {
                        mFont.tahoma_7_white.gameAA(var1, mResources.gameAZ + " " + (var7.timeTrade - var3) + " " + mResources.gamePY, popupX + popupW / 2, popupY + popupH - 13, 2);
                    } else {
                        if (var7.typeTrade != 0) {
                            return;
                        }

                        mFont.tahoma_7_white.gameAA(var1, mResources.gameFZ, popupX + popupW / 2, popupY + popupH - 13, 2);
                    }
                } catch (Exception var6) {
                    var6.printStackTrace();
                    return;
                }
            } else if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameHE);
            }
        }

    }

    private void gameBB(mGraphics var1) {
        if (gameKZ) {
            if (indexMenu == 0) {
                this.gameAC(var1, mResources.gameGZ);
                return;
            }

            if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameGZ);
            }
        }

    }

    private void gameBC(mGraphics var1) {
        if (gameMI) {
            if (indexMenu == 0) {
                this.gameAC(var1, mResources.gameHF);
                return;
            }

            if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameHF);
            }
        }

    }

    private void gameBD(mGraphics var1) {
        if (gameMA) {
            if (indexMenu == 0) {
                String[] var2 = mResources.gameHA;
                var1 = var1;
                GameScr var4 = this;

                try {
                    gameAB(var1);
                    var4.gameAA(var1, var2, false);
                    if (Char.getMyChar().arrItemBox == null) {
                        GameCanvas.gameAA(popupX + 90, popupY + 75, var1, false);
                        mFont.tahoma_7b_white.gameAA(var1, mResources.PLEASEWAIT, popupX + 90, popupY + 90, 2);
                    } else {
                        mFont.tahoma_7_white.gameAA(var1, mResources.gameFU + ": " + NinjaUtil.gameAA(String.valueOf(Char.getMyChar().xuInBox)), popupX + popupW / 2, popupY + popupH - 18, 2);
                        var4.gameAA(var1, Char.getMyChar().arrItemBox);
                    }
                } catch (Exception var3) {
                    var3.printStackTrace();
                    return;
                }
            } else if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameHA);
            }
        }

    }

    private void gameBE(mGraphics var1) {
        if (indexMenu == 2) {
            var1.gameAA(-var1.gameAA(), -var1.gameAB());
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            gameAA(var1, mResources.gameGG[indexMenu], true);
            mFont.tahoma_8b.gameAA(var1, mResources.gameNC, popupX + 10, popupY + 33, 0);
            mFont.tahoma_8b.gameAA(var1, "" + Char.getMyChar().pPoint, popupX + popupW - 10, popupY + 33, 1);
            int var3 = (popupH - 80) / 5;

            for (int var2 = 0; var2 < Char.getMyChar().potential.length; ++var2) {
                var1.gameAA(Paint.COLORBORDER);
                if (gameHL > 0 && gameHL - 1 == var2) {
                    var1.gameAA(Paint.COLORDARK);
                    var1.gameAC(popupX + 5, popupY + 52 + var2 * (var3 + 4), popupW - 10, var3);
                    var1.gameAA(Paint.COLORFOCUS);
                }

                var1.gameAB(popupX + 5, popupY + 52 + var2 * (var3 + 4), popupW - 10, var3);
                mFont.tahoma_7b_white.gameAA(var1, "" + Char.getMyChar().potential[var2], popupX + popupW - 10, popupY + 52 + (var3 - 10) / 2 + var2 * (var3 + 4), 1);
                mFont.tahoma_7b_white.gameAA(var1, mResources.gameMV[var2], popupX + 10, popupY + 52 + (var3 - 10) / 2 + var2 * (var3 + 4), 0);
            }

            if (gameHL > 0) {
                switch (Char.getMyChar().nClass.classId) {
                    case 0:
                        mFont.tahoma_7_green.gameAA(var1, mResources.gameTE[0], popupX + 10, popupY + 52 + (var3 - 10) / 2 + 4 * (var3 + 4), 0);
                        return;
                    case 1:
                    case 3:
                    case 5:
                        mFont.tahoma_7_green.gameAA(var1, mResources.gameTF[gameHL - 1], popupX + 10, popupY + 52 + (var3 - 10) / 2 + 4 * (var3 + 4), 0);
                        return;
                    case 2:
                    case 4:
                    case 6:
                        mFont.tahoma_7_green.gameAA(var1, mResources.gameTG[gameHL - 1], popupX + 10, popupY + 52 + (var3 - 10) / 2 + 4 * (var3 + 4), 0);
                }
            }

        }
    }

    private static Item gameAK(int var0) {
        try {
            if (indexSelect < 0) {
                return null;
            }

            switch (var0) {
                case 2:
                    if (arrItemWeapon.length > indexSelect) {
                        return arrItemWeapon[indexSelect];
                    }

                    return null;
                case 3:
                    return Char.getMyChar().arrItemBag[indexSelect];
                case 4:
                    return Char.getMyChar().arrItemBox[indexSelect];
                case 5:
                    return currentCharViewInfo.arrItemBody[indexSelect + gameRJ];
                case 6:
                    if (arrItemStack.length > indexSelect) {
                        return arrItemStack[indexSelect];
                    }

                    return null;
                case 7:
                    if (arrItemStackLock.length > indexSelect) {
                        return arrItemStackLock[indexSelect];
                    }

                    return null;
                case 8:
                    if (arrItemGrocery.length > indexSelect) {
                        return arrItemGrocery[indexSelect];
                    }

                    return null;
                case 9:
                    if (arrItemGroceryLock.length > indexSelect) {
                        return arrItemGroceryLock[indexSelect];
                    }

                    return null;
                case 10:
                    return arrItemUpGrade[indexSelect];
                case 11:
                    return arrItemUpPeal[indexSelect];
                case 12:
                case 13:
                case 30:
                case 31:
                case 33:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 49:
                case 50:
                case 51:
                default:
                    break;
                case 14:
                    if (arrItemStore.length > indexSelect) {
                        return arrItemStore[indexSelect];
                    }

                    return null;
                case 15:
                    if (arrItemBook.length > indexSelect) {
                        return arrItemBook[indexSelect];
                    }

                    return null;
                case 16:
                    if (arrItemLien.length > indexSelect) {
                        return arrItemLien[indexSelect];
                    }

                    return null;
                case 17:
                    if (arrItemNhan.length > indexSelect) {
                        return arrItemNhan[indexSelect];
                    }

                    return null;
                case 18:
                    if (arrItemNgocBoi.length > indexSelect) {
                        return arrItemNgocBoi[indexSelect];
                    }

                    return null;
                case 19:
                    if (arrItemPhu.length > indexSelect) {
                        return arrItemPhu[indexSelect];
                    }

                    return null;
                case 20:
                    if (arrItemNonNam.length > indexSelect) {
                        return arrItemNonNam[indexSelect];
                    }

                    return null;
                case 21:
                    if (arrItemNonNu.length > indexSelect) {
                        return arrItemNonNu[indexSelect];
                    }

                    return null;
                case 22:
                    if (arrItemAoNam.length > indexSelect) {
                        return arrItemAoNam[indexSelect];
                    }

                    return null;
                case 23:
                    if (arrItemAoNu.length > indexSelect) {
                        return arrItemAoNu[indexSelect];
                    }

                    return null;
                case 24:
                    if (arrItemGangTayNam.length > indexSelect) {
                        return arrItemGangTayNam[indexSelect];
                    }

                    return null;
                case 25:
                    if (arrItemGangTayNu.length > indexSelect) {
                        return arrItemGangTayNu[indexSelect];
                    }

                    return null;
                case 26:
                    if (arrItemQuanNam.length > indexSelect) {
                        return arrItemQuanNam[indexSelect];
                    }

                    return null;
                case 27:
                    if (arrItemQuanNu.length > indexSelect) {
                        return arrItemQuanNu[indexSelect];
                    }

                    return null;
                case 28:
                    if (arrItemGiayNam.length > indexSelect) {
                        return arrItemGiayNam[indexSelect];
                    }

                    return null;
                case 29:
                    if (arrItemGiayNu.length > indexSelect) {
                        return arrItemGiayNu[indexSelect];
                    }

                    return null;
                case 32:
                    if (arrItemFashion.length > indexSelect) {
                        return arrItemFashion[indexSelect];
                    }

                    return null;
                case 34:
                    if (arrItemClanShop.length > indexSelect) {
                        return arrItemClanShop[indexSelect];
                    }

                    return null;
                case 35:
                    if (arrItemElites.length > indexSelect) {
                        return arrItemElites[indexSelect];
                    }

                    return null;
                case 43:
                    return arrItemUpPeal[indexSelect];
                case 44:
                    return arrItemSplit[indexSelect];
                case 45:
                    return arrItemSplit[indexSelect];
                case 46:
                    return arrItemSplit[indexSelect];
                case 47:
                    return arrItemUpGrade[indexSelect];
                case 48:
                    return arrItemSplit[indexSelect];
                case 52:
                    if (arrItemFashion.length > indexSelect) {
                        return arrItemFashion[indexSelect];
                    }

                    return null;
            }
        } catch (Exception var1) {
        }

        return null;
    }

    public static void gameBD() {
        TileMap.gameAD();
    }

    private static void gameAA(mGraphics var0, String var1, boolean var2) {
        boolean var3 = false;
        if (!svTitle.equals("")) {
            var1 = svTitle;
        }

        int var4 = gW / 2;
        var0.gameAA(Paint.COLORDARK);
        var0.gameAB(var4 - mFont.tahoma_8b.gameAA(var1) / 2 - 12, popupY + 4, mFont.tahoma_8b.gameAA(var1) + 22, 24, 6, 6);
        if ((gameHL == 0 || GameCanvas.isTouch) && var2) {
            SmallImage.gameAA(var0, 989, var4 - mFont.tahoma_8b.gameAA(var1) / 2 - 15 - 7 - (GameCanvas.gameTick % 8 <= 3 ? 2 : 0), popupY + 16, 2, StaticObj.VCENTER_HCENTER);
            SmallImage.gameAA(var0, 989, var4 + mFont.tahoma_8b.gameAA(var1) / 2 + 15 + 5 + (GameCanvas.gameTick % 8 <= 3 ? 2 : 0), popupY + 16, 0, StaticObj.VCENTER_HCENTER);
        }

        if (gameHL == 0) {
            var0.gameAA(Paint.COLORFOCUS);
        } else {
            var0.gameAA(Paint.COLORBORDER);
        }

        var0.gameAA(var4 - mFont.tahoma_8b.gameAA(var1) / 2 - 12, popupY + 4, mFont.tahoma_8b.gameAA(var1) + 22, 24, 6, 6);
        mFont.tahoma_8b.gameAA(var0, var1, var4, popupY + 9, 2);
    }

    private void gameBF(mGraphics var1) {
        if (gameMD) {
            gameAB(var1);
            this.gameAA(var1, new String[]{mResources.gameRC}, false);
            mFont.tahoma_7_yellow.gameAA(var1, TileMap.mapName, popupX + popupW / 2, popupY + popupH - 25, 2);
            if (indexSelect >= 0 && indexSelect < this.zones.length) {
                mFont.tahoma_7_white.gameAA(var1, mResources.gameRD + ": " + this.zones[indexSelect] + ", " + mResources.gameRE + ": " + this.gameMV[indexSelect], popupX + popupW / 2, popupY + popupH - 13, 2);
            }

            int var2 = indexSelect / this.gameMT;
            int var3 = indexSelect % this.gameMT;
            gameTK = this.zones.length / this.gameMT;
            if (this.zones.length % this.gameMT > 0) {
                ++gameTK;
            }

            if (gameTK < 5) {
                gameTK = 5;
            }

            scrMain.gameAA(gameTK, gameHK, gameTD, gameTE, gameTJ * gameHK + 2, 5 * gameHK + 2, true, 6);
            scrMain.gameAA(var1);
            int var4 = 0;

            for (int var5 = 0; var5 < gameTK; ++var5) {
                for (int var6 = 0; var6 < this.gameMT; ++var6) {
                    var1.gameAA(12281361);
                    var1.gameAB(gameTD + var6 * gameHK, gameTE + var5 * gameHK, gameHK, gameHK);
                    if (var4 < this.zones.length) {
                        SmallImage.gameAA(var1, 154, gameTD + var6 * gameHK + gameHK / 2, gameTE + var5 * gameHK + gameHK / 2, 0, 3);
                        if (this.zones[var4] >= 20) {
                            mFont.tahoma_7b_red.gameAA(var1, "" + var4, gameTD + var6 * gameHK + gameHK / 2, gameTE + var5 * gameHK + gameHK / 2 - 4, 2);
                        } else if (this.zones[var4] >= 15) {
                            mFont.tahoma_7b_yellow.gameAA(var1, "" + var4, gameTD + var6 * gameHK + gameHK / 2, gameTE + var5 * gameHK + gameHK / 2 - 4, 2);
                        } else {
                            mFont.tahoma_7b_white.gameAA(var1, "" + var4, gameTD + var6 * gameHK + gameHK / 2, gameTE + var5 * gameHK + gameHK / 2 - 4, 2);
                        }

                        ++var4;
                    }
                }
            }

            if (indexSelect >= 0) {
                var1.gameAA(16777215);
                var1.gameAB(gameTD + var3 * gameHK, gameTE + var2 * gameHK, gameHK, gameHK);
            }

        }
    }

    private static void gameDQ() {
        int var0 = 0;
        int var1 = 0;
        boolean var2 = false;
        boolean var3 = false;

        int var4;
        for (var4 = 0; var4 < arrItemUpPeal.length; ++var4) {
            if (arrItemUpPeal[var4] != null) {
                ++var0;
                var1 += crystals[arrItemUpPeal[var4].template.id];
                if (arrItemUpPeal[var4].template.id == 11) {
                    GameCanvas.msgdlg.gameAA(mResources.gameQI, (Command) null, new Command(mResources.gameBH, 1), (Command) null);
                    GameCanvas.currentDialog = GameCanvas.msgdlg;
                    return;
                }
            }

            if (arrItemUpPeal[var4] != null && arrItemUpPeal[var4].isLock) {
                var2 = true;
            }

            if (arrItemUpPeal[var4] != null && !arrItemUpPeal[var4].isLock) {
                var3 = true;
            }
        }

        if (var0 <= 1) {
            GameCanvas.msgdlg.gameAA(mResources.gameQE, (Command) null, new Command(mResources.gameBH, 1), (Command) null);
            GameCanvas.currentDialog = GameCanvas.msgdlg;
        } else {
            for (var4 = crystals.length - 1; var4 >= 0 && var1 <= crystals[var4]; --var4) {
            }

            if (var4 >= crystals.length - 1) {
                var4 = crystals.length - 2;
            }

            if (gameHP) {
                if (coinUpCrystals[var4 + 1] > Char.getMyChar().xu) {
                    GameCanvas.msgdlg.gameAA(mResources.gameQK, (Command) null, new Command(mResources.gameBH, 1), (Command) null);
                    GameCanvas.currentDialog = GameCanvas.msgdlg;
                } else if (var2) {
                    GameCanvas.gameAA(mResources.gameQJ, 88813, arrItemUpPeal, 8882, (Object) null);
                } else {
                    Service.gI().crystalCollect(arrItemUpPeal);
                }
            } else if (coinUpCrystals[var4 + 1] > Char.getMyChar().xu + Char.getMyChar().yen) {
                GameCanvas.msgdlg.gameAA(mResources.gameQK, (Command) null, new Command(mResources.gameBH, 1), (Command) null);
                GameCanvas.currentDialog = GameCanvas.msgdlg;
            } else if (var3) {
                GameCanvas.gameAA(mResources.gameQJ, 88814, arrItemUpPeal, 8882, (Object) null);
            } else {
                Service.gI().crystalCollectLock(arrItemUpPeal);
            }
        }
    }

    private static void gameDR() {
        int var0 = 0;
        int var1 = 0;
        int var2 = 0;
        short var3 = 0;

        for (int var4 = 0; var4 < arrItemUpPeal.length; ++var4) {
            Item var5;
            if ((var5 = arrItemUpPeal[var4]) != null) {
                if (var5.template.id == 455) {
                    ++var0;
                } else if (var5.template.id == 456) {
                    ++var1;
                } else if (var5.template.type == 26) {
                    ++var2;
                    var3 = var5.template.id;
                }
            }
        }

        if (var2 > 1) {
            GameCanvas.setText(mResources.gameWL);
        } else if (var0 <= 9 && var1 <= 9 && (var3 < 10 || var0 <= 3 && var1 <= 3)) {
            if (var0 + var1 < 3) {
                GameCanvas.setText(mResources.gameWP);
            } else if ((var3 != 10 || var1 != 3) && (var3 != 11 || var0 != 3)) {
                Service.gI().luyenthach(arrItemUpPeal);
            } else {
                GameCanvas.setText(mResources.gameWM);
            }
        } else {
            GameCanvas.setText(mResources.gameWQ);
        }
    }

    public static byte gameBE() {
        if (Char.getMyChar().ctaskId >= tasks.length) {
            return -3;
        } else {
            boolean var0 = false;
            byte var1;
            if (Char.getMyChar().taskMaint == null) {
                var1 = mapTasks[Char.getMyChar().ctaskId][0];
            } else {
                var1 = mapTasks[Char.getMyChar().ctaskId][Char.getMyChar().taskMaint.index + 1];
            }

            if (var1 == -1) {
                if (Char.getMyChar().nClass.classId == 0 && Char.getMyChar().ctaskId == 9) {
                    var1 = -2;
                } else if (Char.getMyChar().nClass.classId != 0 && Char.getMyChar().nClass.classId != 1 && Char.getMyChar().nClass.classId != 2) {
                    if (Char.getMyChar().nClass.classId != 3 && Char.getMyChar().nClass.classId != 4) {
                        if (Char.getMyChar().nClass.classId == 5 || Char.getMyChar().nClass.classId == 6) {
                            var1 = 27;
                        }
                    } else {
                        var1 = 72;
                    }
                } else {
                    var1 = 1;
                }
            }

            return var1;
        }
    }

    public static byte gameBF() {
        try {
            if (Char.getMyChar().ctaskId >= tasks.length) {
                return -3;
            } else {
                boolean var0 = false;
                byte var2;
                if (Char.getMyChar().taskMaint == null) {
                    var2 = tasks[Char.getMyChar().ctaskId][0];
                } else {
                    var2 = tasks[Char.getMyChar().ctaskId][Char.getMyChar().taskMaint.index + 1];
                }

                if (var2 == -1) {
                    if (Char.getMyChar().nClass.classId == 0 && Char.getMyChar().ctaskId == 9) {
                        var2 = -2;
                    } else if (Char.getMyChar().nClass.classId != 0 && Char.getMyChar().nClass.classId != 1 && Char.getMyChar().nClass.classId != 2) {
                        if (Char.getMyChar().nClass.classId != 3 && Char.getMyChar().nClass.classId != 4) {
                            if (Char.getMyChar().nClass.classId == 5 || Char.getMyChar().nClass.classId == 6) {
                                var2 = 11;
                            }
                        } else {
                            var2 = 10;
                        }
                    } else {
                        var2 = 9;
                    }
                }

                return var2;
            }
        } catch (Exception var1) {
            return -1;
        }
    }

    private void gameDS() {
        int var4 = 0;

        for (int var1 = 0; var1 < arrItemUpGrade.length; ++var1) {
            if (arrItemUpGrade[var1] != null && arrItemUpGrade[var1].template.type == 26) {
                var4 += crystals[arrItemUpGrade[var1].template.id];
            }
        }

        boolean var5 = false;
        boolean var2 = false;
        int var3 = 0;
        if (itemUpGrade.isTypeClothe()) {
            if (coinUpClothes[itemUpGrade.upgrade] > Char.getMyChar().xu + Char.getMyChar().yen) {
                var5 = true;
            }

            var3 = var4 * 100 / upClothe[itemUpGrade.upgrade];
        } else if (itemUpGrade.isTypeAdorn()) {
            if (coinUpAdorns[itemUpGrade.upgrade] > Char.getMyChar().xu + Char.getMyChar().yen) {
                var5 = true;
            }

            var3 = var4 * 100 / upAdorn[itemUpGrade.upgrade];
        } else if (itemUpGrade.isTypeWeapon()) {
            if (coinUpWeapons[itemUpGrade.upgrade] > Char.getMyChar().xu + Char.getMyChar().yen) {
                var5 = true;
            }

            var3 = var4 * 100 / upWeapon[itemUpGrade.upgrade];
        }

        if (gameKY && goldUps[itemUpGrade.upgrade] > Char.getMyChar().luong) {
            var2 = true;
        }

        if (var5) {
            InfoMe.gameAA(mResources.gameQM, 15, mFont.tahoma_7_red);
        } else if (var2) {
            InfoMe.gameAA(mResources.gameQL, 15, mFont.tahoma_7_red);
        } else if (var3 > 250) {
            GameCanvas.gameAA(mResources.gameQN, 88815, (Object) null, 8882, (Object) null);
        } else {
            gameBG();
        }
    }

    public static void gameBG() {
        if (!itemUpGrade.isLock) {
            GameCanvas.gameAA(mResources.gameQO, new Command(mResources.gameCH, 11063), new Command(mResources.gameCU, 1));
        } else {
            Service.gI().upgradeItem(itemUpGrade, arrItemUpGrade, gameKY);
        }
    }

    private static void gameDT() {
        if (!gameMR) {
            if (gameML) {
                Service.gI().ngockham((byte) 1, (Item) null, itemSplit, arrItemSplit);
                return;
            }

            if (gameMJ) {
                Service.gI().tinhluyen(itemSplit, arrItemSplit);
                return;
            }

            if (gameMK) {
                Service.gI().dichchuyen(itemSplit, arrItemSplit);
                return;
            }

            if (itemSplit.upgrade == 0) {
                GameCanvas.msgdlg.gameAA(mResources.gameQP, (Command) null, new Command(mResources.gameBH, 1), (Command) null);
                GameCanvas.currentDialog = GameCanvas.msgdlg;
                return;
            }

            int var0 = -1;
            int var1 = 0;

            int var2;
            for (var2 = 0; var2 < Char.getMyChar().arrItemBag.length; ++var2) {
                if (Char.getMyChar().arrItemBag[var2] == null) {
                    ++var0;
                }
            }

            for (var2 = 0; var2 < arrItemSplit.length; ++var2) {
                if (arrItemSplit[var2] != null) {
                    ++var1;
                }
            }

            if (var1 > var0) {
                GameCanvas.msgdlg.gameAA(mResources.gameQQ, (Command) null, new Command(mResources.gameBH, 1), (Command) null);
                GameCanvas.currentDialog = GameCanvas.msgdlg;
                return;
            }

            GameCanvas.gameAA(mResources.gameQR, new Command(mResources.gameCH, 11087, itemSplit), new Command(mResources.gameCU, 1));
        }

    }

    private void gameAA(int var1, Item var2, Command var3, Command var4) {
        this.gameAA((int) 3, (Item) var2);
        if (var3 != null) {
            super.left = new Command(var3.caption, 11040);
        }

    }

    private void gameAA(int var1, Item var2) {
        if (var2 != null) {
            this.gameHN = var2;
            gameTS = 120;
            gameTT = 120;
            if (GameCanvas.isTouch && !GameCanvas.gameAI) {
                gameTT += 18;
            }

            gameDQ = true;
            gameHO.gameAA();
            indexRow = 0;
            if (var2.expires == 0L) {
                if (gameMJ || gameMK || gameML || gameMR || gameMM || gameMO || gameMN) {
                    Service.gI().requestItemInfo(var2.typeUI, var2.indexUI);
                }

                if (gameMH) {
                    Service.gI().requestItemAuction(var2.itemId);
                } else if (currentCharViewInfo.charID == Char.getMyChar().charID) {
                    Service.gI().requestItemInfo(var1, var2.indexUI);
                } else {
                    Service.gI().requestItemPlayer(currentCharViewInfo.charID, var2.indexUI);
                }
            }

            if (var1 == 5) {
                Char.getMyChar().gameAW();
            }

            if (!GameCanvas.isTouch || GameCanvas.isTouch && GameCanvas.gameAI || isPaintInfoMe && indexMenu > 0 && indexMenu < 4 || gameME && indexMenu == 0) {
                super.center = this.gameTW;
                super.right = null;
                super.left = null;
            }

            GameCanvas.gameAI();
            GameCanvas.gameAH();
        }
    }

    public final void gameAA(String var1, String var2, boolean var3) {
        InfoDlg.gameAB();
        isPaintAlert = true;
        this.gameNA = true;
        indexRow = 0;
        gameAB(175, 200);
        if (var3) {
            popupH -= 60;
        }

        super.right = new Command(mResources.gameBH, 3);
        super.left = super.center = null;
        this.gameNC = var1;
        this.gameNB = mFont.tahoma_7.gameAA(var2, popupW - 30);
    }

    public final void gameBH() {
        isPaintAlert = false;
        this.gameNC = null;
        this.gameNB = null;
        super.center = null;
        this.resetButton();
    }

    public final void gameBI() {
        gameHM = this.gameNB.size();
        scrMain.gameAA(gameHM, 12, popupX, gameTE + 12, popupW, popupH - 42 - (this.gameNC != null ? 10 : 0), true, 1);
        indexRow = this.gameNB.size() - 1;
        scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
    }

    private void gameBG(mGraphics var1) {
        if (this.gameNB != null && isPaintAlert) {
            gameAB(var1);
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            if (this.gameNC != null) {
                gameAA(var1, this.gameNC, isPaintMessage);
            }

            gameTD = popupX + 15;
            gameTE = popupY + 15;
            if (this.gameNC != null) {
                gameTE += 10;
            }

            gameHM = this.gameNB.size();
            scrMain.gameAA(gameHM, 12, popupX, gameTE + 12, popupW, popupH - 42 - (this.gameNC != null ? 10 : 0), true, 1);
            scrMain.gameAA(var1);
            this.gameTL = gameTE;
            mFont var2 = mFont.tahoma_7_white;

            String var4;
            for (int var3 = 0; var3 < this.gameNB.size() && (var4 = (String) this.gameNB.elementAt(var3)) != null && this.gameNB != null && var2 != null; ++var3) {
                if (var4.startsWith("c")) {
                    if (var4.startsWith("c0")) {
                        var4 = var4.substring(2);
                        var2 = mFont.tahoma_7_white;
                    } else if (var4.startsWith("c1")) {
                        var4 = var4.substring(2);
                        var2 = mFont.tahoma_7b_yellow;
                    } else if (var4.startsWith("c2")) {
                        var4 = var4.substring(2);
                        var2 = mFont.tahoma_7b_white;
                    } else if (var4.startsWith("c3")) {
                        var4 = var4.substring(2);
                        var2 = mFont.tahoma_7_yellow;
                    } else if (var4.startsWith("c4")) {
                        var4 = var4.substring(2);
                        var2 = mFont.tahoma_7b_red;
                    } else if (var4.startsWith("c5")) {
                        var4 = var4.substring(2);
                        var2 = mFont.tahoma_7_red;
                    } else if (var4.startsWith("c6")) {
                        var4 = var4.substring(2);
                        var2 = mFont.tahoma_7_grey;
                    } else if (var4.startsWith("c7")) {
                        var4 = var4.substring(2);
                        var2 = mFont.tahoma_7b_blue;
                    } else if (var4.startsWith("c8")) {
                        var4 = var4.substring(2);
                        var2 = mFont.tahoma_7_blue;
                    } else if (var4.startsWith("c9")) {
                        var4 = var4.substring(2);
                        var2 = mFont.tahoma_7_green;
                    }
                }

                var2.gameAA(var1, var4, gameTD + 5, this.gameTL += 12, 0);
            }

            if (indexRow >= 0) {
                SmallImage.gameAA(var1, 942, gameTD - 5, gameTE + 12 + 1 + indexRow * 12, 0, StaticObj.TOP_LEFT);
            }

        }
    }

    private void gameBH(mGraphics var1) {
        if (gameHX) {
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            gameAA(var1, mResources.gameME, false);
            gameTD = popupX + 5;
            gameTE = popupY + 40;
            if (vPtMap.size() == 0) {
                mFont.tahoma_7_white.gameAA(var1, mResources.gameSP, popupX + popupW / 2, popupY + 40, 2);
            } else {
                var1.gameAA(6425);
                var1.gameAC(gameTD - 2, gameTE - 2, popupW - 6, gameHK * 5 + 8);
                gameAB(var1);
                scrMain.gameAA(vPtMap.size(), gameHK, gameTD, gameTE, popupW - 3, gameHK * 5 + 4, true, 1);
                scrMain.gameAA(var1, gameTD, gameTE, popupW - 3, gameHK * 5 + 6);
                gameHM = vPtMap.size();

                for (int var3 = 0; var3 < vPtMap.size(); ++var3) {
                    Party var2 = (Party) vPtMap.elementAt(var3);
                    if (indexRow == var3) {
                        var1.gameAA(Paint.COLORLIGHT);
                        var1.gameAC(gameTD + 2, gameTE + indexRow * gameHK + 2, popupW - 15, gameHK - 4);
                        var1.gameAA(16777215);
                        var1.gameAB(gameTD + 2, gameTE + indexRow * gameHK + 2, popupW - 15, gameHK - 4);
                    } else {
                        var1.gameAA(Paint.COLORBACKGROUND);
                        var1.gameAC(gameTD + 2, gameTE + var3 * gameHK + 2, popupW - 15, gameHK - 4);
                        var1.gameAA(13932896);
                        var1.gameAB(gameTD + 2, gameTE + var3 * gameHK + 2, popupW - 15, gameHK - 4);
                    }

                    SmallImage.gameAA(var1, 647, gameTD + 12, gameTE + var3 * gameHK + gameHK / 2, 0, 3);
                    mFont.tahoma_7_white.gameAA(var1, var2.name + " - " + mResources.gameEV + ": " + var2.level + " (" + var2.size + ")", gameTD + 22, gameTE + var3 * gameHK + gameHK / 2 - 6, 0);
                }
            }

            gameAN(var1);
        }

    }

    private void gameBI(mGraphics var1) {
        if (gameHW) {
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            gameAA(var1, mResources.gameMD, false);
            gameTD = popupX + 5;
            gameTE = popupY + 40;
            if (vParty.size() == 0) {
                mFont.tahoma_7_white.gameAA(var1, mResources.gameSN, popupX + popupW / 2, popupY + 40, 2);
            } else {
                var1.gameAA(6425);
                var1.gameAC(gameTD - 2, gameTE - 2, popupW - 6, gameHK * 5 + 8);
                gameAB(var1);
                scrMain.gameAA(vParty.size(), gameHK, gameTD, gameTE, popupW - 3, gameHK * 5 + 4, true, 1);
                scrMain.gameAA(var1, gameTD, gameTE, popupW - 3, gameHK * 5 + 6);
                gameHM = vParty.size();

                for (int var3 = 0; var3 < vParty.size(); ++var3) {
                    Party var2 = (Party) vParty.elementAt(var3);
                    if (indexRow == var3) {
                        var1.gameAA(Paint.COLORLIGHT);
                        var1.gameAC(gameTD + 2, gameTE + indexRow * gameHK + 2, popupW - 15, gameHK - 4);
                        var1.gameAA(16777215);
                        var1.gameAB(gameTD + 2, gameTE + indexRow * gameHK + 2, popupW - 15, gameHK - 4);
                    } else {
                        var1.gameAA(Paint.COLORBACKGROUND);
                        var1.gameAC(gameTD + 2, gameTE + var3 * gameHK + 2, popupW - 15, gameHK - 4);
                        var1.gameAA(13932896);
                        var1.gameAB(gameTD + 2, gameTE + var3 * gameHK + 2, popupW - 15, gameHK - 4);
                    }

                    SmallImage.gameAA(var1, var2.iconId, gameTD + 12, gameTE + var3 * gameHK + gameHK / 2, 0, 3);
                    if (var2.c == null) {
                        mFont.tahoma_7_green.gameAA(var1, var2.name, gameTD + 22, gameTE + var3 * gameHK + gameHK / 2 - 6, 0);
                    } else if (var3 == 0) {
                        mFont.tahoma_7_yellow.gameAA(var1, var2.name + " - " + mResources.gameEV + ": " + var2.c.clevel, gameTD + 22, gameTE + var3 * gameHK + gameHK / 2 - 6, 0);
                    } else {
                        mFont.tahoma_7_white.gameAA(var1, var2.name + " - " + mResources.gameEV + ": " + var2.c.clevel, gameTD + 22, gameTE + var3 * gameHK + gameHK / 2 - 6, 0);
                    }
                }
            }

            gameAN(var1);
        }

    }

    private void gameBJ(mGraphics var1) {
        if (gameMH) {
            int var6 = popupW;
            if (GameCanvas.gameAJ) {
                var6 = popupW / 2 + 20;
            }

            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            gameAA(var1, mResources.gameVN[indexMenu], GameCanvas.isTouch ? true : gameHL == 0);
            var1.gameAA(6425);
            var1.gameAC(gameTD - 2, gameTE - 2, var6 - 6, gameHK * 5 + 4);
            if (arrItemStands == null) {
                GameCanvas.gameAA(popupX + 90, popupY + 75, var1, false);
                mFont.tahoma_7b_white.gameAA(var1, mResources.PLEASEWAIT, popupX + 90, popupY + 90, 2);
                return;
            }

            ItemStands[] var2 = arrItemStands;
            gameTD = popupX + 5;
            gameTE = popupY + 33;
            if (var2.length > 0) {
                gameHM = var2.length;
                gameAB(var1);
                scrMain.gameAA(gameHM, gameHK, gameTD, gameTE, var6 - 3, gameHK * 5, true, 1);
                scrMain.gameAA(var1, gameTD, gameTE, var6 - 3, gameHK * 5 + 2);

                for (int var4 = 0; var4 < var2.length; ++var4) {
                    ItemStands var5;
                    if ((var5 = var2[var4]) != null && var5.item != null && var5.item.template != null) {
                        int var3 = (int) (System.currentTimeMillis() / 1000L);
                        if (var4 * gameHK >= scrMain.cmy - gameHK && var4 * gameHK < scrMain.cmy + gameHK * 5 + 4) {
                            if (indexSelect == var4) {
                                var1.gameAA(Paint.COLORLIGHT);
                                var1.gameAC(gameTD + 2, gameTE + indexSelect * gameHK + 2, var6 - 15, gameHK - 4);
                                var1.gameAA(16777215);
                                var1.gameAB(gameTD + 2, gameTE + indexSelect * gameHK + 2, var6 - 15, gameHK - 4);
                            } else {
                                var1.gameAA(Paint.COLORBACKGROUND);
                                var1.gameAC(gameTD + 2, gameTE + var4 * gameHK + 2, var6 - 15, gameHK - 4);
                                var1.gameAA(13932896);
                                var1.gameAB(gameTD + 2, gameTE + var4 * gameHK + 2, var6 - 15, gameHK - 4);
                            }

                            var1.gameAA(0);
                            var1.gameAC(gameTD + 4, gameTE + var4 * gameHK + 4, gameHK - 1, gameHK - 8);
                            var1.gameAA(indexSelect == var4 ? 16777215 : 12281361);
                            var1.gameAB(gameTD + 4, gameTE + var4 * gameHK + 4, gameHK - 1, gameHK - 8);
                            if (GameCanvas.gameTick % 6 == 0) {
                                var5.item.indexFrame = (var5.item.indexFrame + 1) % 3;
                            }

                            var1.gameAA(11403519);
                            if (var5.item.isNewitem()) {
                                var1.gameAB(gameTD + 5, gameTE + var4 * gameHK + 5, gameHK - 3, gameHK - 10);
                            }

                            SmallImage.gameAA(var1, var5.item.template.iconID, gameTD + gameHK / 2, gameTE + var4 * gameHK + gameHK / 2, 0, 3, var5.item.indexFrame);
                            if (var5.item.quantity > 1) {
                                mFont.number_yellow.gameAA(var1, String.valueOf(var5.item.quantity), gameTD + gameHK, gameTE + var4 * gameHK + gameHK / 2 + 2, 1);
                            }

                            var3 = var5.timeEnd - (var3 - var5.timeStart);
                            mFont.tahoma_7_white.gameAA(var1, mResources.gameVJ + ": " + var5.seller, gameTD + gameHK + 7, gameTE + var4 * gameHK + gameHK / 2 - mFont.number_yellow.gameAC() - 2, 0);
                            mFont.tahoma_7_white.gameAA(var1, mResources.gameCR + ": " + NinjaUtil.gameAA(String.valueOf(var5.price)) + " " + mResources.gamePA, gameTD + gameHK + 7, gameTE + var4 * gameHK + gameHK / 2 - mFont.number_yellow.gameAC() + 9, 0);
                            if (var3 < 60) {
                                mFont.tahoma_7_blue.gameAA(var1, mResources.gameVL, gameTD + var6 - 30, gameTE + var4 * gameHK + gameHK / 2 - mFont.number_yellow.gameAC() - 2, 2);
                                mFont.tahoma_7_blue.gameAA(var1, mResources.gameVM, gameTD + var6 - 30, gameTE + var4 * gameHK + gameHK / 2 - mFont.number_yellow.gameAC() + 9, 2);
                            } else {
                                mFont.tahoma_7_green.gameAA(var1, mResources.gameVK, gameTD + var6 - 30, gameTE + var4 * gameHK + gameHK / 2 - mFont.number_yellow.gameAC() - 2, 2);
                                mFont.tahoma_7_green.gameAA(var1, NinjaUtil.gameAB(var3), gameTD + var6 - 30, gameTE + var4 * gameHK + gameHK / 2 - mFont.number_yellow.gameAC() + 9, 2);
                            }
                        }
                    }
                }
            } else {
                gameHM = var2.length;
                mFont.tahoma_7_white.gameAA(var1, mResources.gameSO, popupX + var6 / 2, popupY + 40, 2);
            }

            gameAN(var1);
        }

    }

    private void gameBK(mGraphics var1) {
        if (isPaintFriend || gameHZ) {
            String var6 = isPaintFriend ? mResources.gameMG[0] : mResources.gameMG[1];
            MyVector var2 = isPaintFriend ? vFriend : vEnemies;
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            gameAA(var1, var6, false);
            if (var2.size() <= 0) {
                mFont.tahoma_7_white.gameAA(var1, isPaintFriend ? mResources.gameMH : mResources.gameMW, popupX + popupW / 2, popupY + 40, 2);
            } else {
                gameTD = popupX + 5;
                gameTE = popupY + 40;
                var1.gameAA(6425);
                var1.gameAC(gameTD - 2, gameTE - 2, popupW - 6, gameHK * 5 + 8);
                gameAB(var1);
                scrMain.gameAA(var2.size(), gameHK, gameTD, gameTE, popupW - 3, gameHK * 5 + 4, true, 1);
                scrMain.gameAA(var1, gameTD, gameTE, popupW - 3, gameHK * 5 + 6);
                gameHM = var2.size();
                int var7 = 0;
                int var3 = 0;

                while (true) {
                    if (var3 >= var2.size()) {
                        gameHM = var7;
                        scrMain.gameAA(var7, gameHK, gameTD, gameTE, popupW - 3, gameHK * 5 + 4, true, 1);
                        break;
                    }

                    Friend var4 = (Friend) var2.elementAt(var3);
                    if (!gameHR || var4.type == 3) {
                        mFont var5 = mFont.tahoma_7_grey;
                        if (var4.type != 1 && var4.type != 2) {
                            if (var4.type == 3) {
                                var5 = mFont.tahoma_7_white;
                            } else if (var4.type == 4) {
                                var5 = mFont.tahoma_7_red;
                            }
                        } else {
                            var5 = mFont.tahoma_7_green;
                        }

                        if (var3 * gameHK >= scrMain.cmy - gameHK && var3 * gameHK < scrMain.cmy + gameHK * 5 + 8) {
                            if (indexRow == var3) {
                                var1.gameAA(Paint.COLORLIGHT);
                                var1.gameAC(gameTD + 2, gameTE + indexRow * gameHK + 2, popupW - 15, gameHK - 4);
                                var1.gameAA(16777215);
                                var1.gameAB(gameTD + 2, gameTE + indexRow * gameHK + 2, popupW - 15, gameHK - 4);
                            } else {
                                var1.gameAA(Paint.COLORBACKGROUND);
                                var1.gameAC(gameTD + 2, gameTE + var3 * gameHK + 2, popupW - 15, gameHK - 4);
                                var1.gameAA(13932896);
                                var1.gameAB(gameTD + 2, gameTE + var3 * gameHK + 2, popupW - 15, gameHK - 4);
                            }

                            if (var4.type == 4) {
                                if (GameCanvas.gameTick % 10 > 7) {
                                    var5.gameAA(var1, var4.friendName, gameTD + 8, gameTE + var3 * gameHK + gameHK / 2 - 6, 0);
                                } else {
                                    mFont.tahoma_7_yellow.gameAA(var1, var4.friendName, gameTD + 8, gameTE + var3 * gameHK + gameHK / 2 - 6, 0);
                                }

                                mFont.tahoma_7_blue.gameAA(var1, mResources.gameMI, gameTD + popupW - 15, gameTE + var3 * gameHK + gameHK / 2 - 6, 1);
                            } else {
                                var5.gameAA(var1, var4.friendName, gameTD + 8, gameTE + var3 * gameHK + gameHK / 2 - 6, 0);
                            }
                        }

                        ++var7;
                    }

                    ++var3;
                }
            }

            gameAN(var1);
        }

    }

    private void gameBL(mGraphics var1) {
        if (gameHV) {
            gameHM = 0;
            gameAB(var1);
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            gameAA(var1, mResources.gameKW[indexMenu], false);
            gameTD = popupX + 10;
            gameTE = popupY + 32;
            int var2;
            String var4;
            if (indexMenu == 0) {
                boolean var7 = false;
                scrMain.gameAA(gameHM, 12, popupX, popupY + 32, popupW, popupH - 40, true, 1);
                scrMain.gameAA(var1);
                if (Char.getMyChar().taskMaint != null) {
                    int var9;
                    for (var9 = 0; var9 < Char.getMyChar().taskMaint.names.length; ++var9) {
                        mFont.tahoma_7b_white.gameAA(var1, Char.getMyChar().taskMaint.names[var9], gameTD, this.gameTL = gameTE, 0);
                        ++gameHM;
                    }

                    var9 = 0;
                    var4 = null;

                    int var11;
                    for (var11 = 0; var11 < Char.getMyChar().taskMaint.subNames.length; ++var11) {
                        mFont var10;
                        if (Char.getMyChar().taskMaint.subNames[var11] != null) {
                            var9 = var11;
                            var4 = "- " + Char.getMyChar().taskMaint.subNames[var11];
                            if (Char.getMyChar().taskMaint.counts[var11] != -1) {
                                if (Char.getMyChar().taskMaint.index == var11) {
                                    var4 = var4 + " " + Char.getMyChar().taskMaint.count + "/" + Char.getMyChar().taskMaint.counts[var11];
                                    if (Char.getMyChar().taskMaint.count == Char.getMyChar().taskMaint.counts[var11]) {
                                        mFont.tahoma_7_white.gameAA(var1, var4, gameTD + 5, this.gameTL += 12, 0);
                                    } else {
                                        var10 = mFont.tahoma_7_grey;
                                        if (!var7) {
                                            var7 = true;
                                            var10 = mFont.tahoma_7_yellow;
                                        }

                                        var10.gameAA(var1, var4, gameTD + 5, this.gameTL += 12, 0);
                                    }
                                } else if (Char.getMyChar().taskMaint.index > var11) {
                                    var4 = var4 + " " + Char.getMyChar().taskMaint.counts[var11] + "/" + Char.getMyChar().taskMaint.counts[var11];
                                    mFont.tahoma_7_white.gameAA(var1, var4, gameTD + 5, this.gameTL += 12, 0);
                                } else {
                                    var4 = var4 + " 0/" + Char.getMyChar().taskMaint.counts[var11];
                                    var10 = mFont.tahoma_7_grey;
                                    if (!var7) {
                                        var7 = true;
                                        var10 = mFont.tahoma_7_yellow;
                                    }

                                    var10.gameAA(var1, var4, gameTD + 5, this.gameTL += 12, 0);
                                }
                            } else if (Char.getMyChar().taskMaint.index > var11) {
                                mFont.tahoma_7_white.gameAA(var1, var4, gameTD + 5, this.gameTL += 12, 0);
                            } else {
                                var10 = mFont.tahoma_7_grey;
                                if (!var7) {
                                    var7 = true;
                                    var10 = mFont.tahoma_7_yellow;
                                }

                                var10.gameAA(var1, var4, gameTD + 5, this.gameTL += 12, 0);
                            }

                            ++gameHM;
                        } else if (Char.getMyChar().taskMaint.index <= var11) {
                            var4 = "- " + Char.getMyChar().taskMaint.subNames[var9];
                            var10 = mFont.tahoma_7_grey;
                            if (!var7) {
                                var7 = true;
                                var10 = mFont.tahoma_7_yellow;
                            }

                            var10.gameAA(var1, var4, gameTD + 5, this.gameTL, 0);
                        }
                    }

                    this.gameTL += 5;

                    for (var11 = 0; var11 < Char.getMyChar().taskMaint.details.length; ++var11) {
                        mFont.tahoma_7_white.gameAA(var1, Char.getMyChar().taskMaint.details[var11], gameTD, this.gameTL += 12, 0);
                        ++gameHM;
                    }
                } else {
                    byte var8 = gameBE();
                    byte var12 = gameBF();
                    String var5 = null;
                    if (var8 != -3 && var12 != -3) {
                        if (Char.getMyChar().taskMaint == null && Char.getMyChar().ctaskId == 9 && Char.getMyChar().nClass.classId == 0) {
                            var5 = mResources.gameSF;
                        } else {
                            if (var12 < 0 || var8 < 0) {
                                return;
                            }

                            var5 = mResources.gameSD[0] + Npc.arrNpcTemplate[var12].name + mResources.gameSD[1] + TileMap.mapNames[var8] + mResources.gameSD[2];
                        }
                    } else {
                        var5 = mResources.gameSD[3];
                    }

                    String[] var6 = mFont.tahoma_7_white.gameAB(var5, 150);

                    for (var2 = 0; var2 < var6.length; ++var2) {
                        if (var2 == 0) {
                            mFont.tahoma_7_white.gameAA(var1, var6[var2], gameTD + 5, this.gameTL = gameTE, 0);
                        } else {
                            mFont.tahoma_7_white.gameAA(var1, var6[var2], gameTD + 5, this.gameTL += 12, 0);
                        }

                        ++gameHM;
                    }
                }

                if (gameHL == 1 && indexRow >= 0 && gameHM > 0) {
                    SmallImage.gameAA(var1, 942, gameTD - 8, gameTE + 2 + indexRow * 12, 0, StaticObj.TOP_LEFT);
                }

                scrMain.gameAA(gameHM, 12, popupX, popupY + 32, popupW, popupH - 44, true, 1);
                return;
            }

            if (indexMenu == 1) {
                this.gameTL = gameTE - 12;
                scrMain.gameAA(Char.getMyChar().taskOrders.size(), 12, popupX, popupY + 32, popupW, popupH - 44, true, 1);
                scrMain.gameAA(var1);
                gameHM = 0;

                for (var2 = 0; var2 < Char.getMyChar().taskOrders.size(); ++var2) {
                    TaskOrder var3 = (TaskOrder) Char.getMyChar().taskOrders.elementAt(var2);
                    mFont.tahoma_7b_white.gameAA(var1, var3.name, gameTD + 5, this.gameTL += 12, 0);
                    var4 = "";
                    if (var3.taskId != 0 && var3.taskId != 3) {
                        if (var3.taskId == 1) {
                            var4 = mResources.gameKD + " " + Mob.arrMobTemplate[var3.killId].name;
                        } else if (var3.taskId == 2) {
                            var4 = mResources.gameVP;
                        } else if (var3.taskId == 4) {
                            var4 = mResources.gameKE + " " + Mob.arrMobTemplate[var3.killId].name;
                        } else if (var3.taskId == 5) {
                            var4 = mResources.gameKF + " " + Mob.arrMobTemplate[var3.killId].name;
                        } else if (var3.taskId == 6) {
                            var4 = mResources.gameKG;
                        }
                    } else {
                        var4 = mResources.gameKC + " " + Mob.arrMobTemplate[var3.killId].name;
                    }

                    if (var3.taskId == 6) {
                        if (var3.count == var3.maxCount) {
                            mFont.tahoma_7_white.gameAA(var1, var4, gameTD + 5, this.gameTL += 12, 0);
                        } else {
                            mFont.tahoma_7_yellow.gameAA(var1, var4, gameTD + 5, this.gameTL += 12, 0);
                        }
                    } else if (var3.count == var3.maxCount) {
                        mFont.tahoma_7_white.gameAA(var1, var4 + " " + var3.count + "/" + var3.maxCount, gameTD + 5, this.gameTL += 12, 0);
                    } else {
                        mFont.tahoma_7_yellow.gameAA(var1, var4 + " " + var3.count + "/" + var3.maxCount, gameTD + 5, this.gameTL += 12, 0);
                    }

                    gameHM += 3;
                    gameTS = popupW - 25;
                    this.gameAA(var1, mFont.tahoma_7_white, (String) var3.description, gameTD + 5, this.gameTL += 12, 0);
                    this.gameTL += 12;
                }

                if (this.typeba > 0) {
                    mFont.tahoma_7_white.gameAA(var1, mResources.gameAE, gameTD + 5, this.gameTL += 12, 0);
                    switch (this.typeba) {
                        case 1:
                            mFont.tahoma_7_white.gameAA(var1, mResources.gameAF, gameTD + 5, this.gameTL += 12, 0);
                            break;
                        case 2:
                            mFont.tahoma_7_white.gameAA(var1, mResources.gameAG, gameTD + 5, this.gameTL += 12, 0);
                            break;
                        case 3:
                            mFont.tahoma_7_white.gameAA(var1, mResources.gameAH, gameTD + 5, this.gameTL += 12, 0);
                            break;
                        case 4:
                            mFont.tahoma_7_white.gameAA(var1, mResources.gameAI, gameTD + 5, this.gameTL += 12, 0);
                            break;
                        case 5:
                            mFont.tahoma_7_white.gameAA(var1, mResources.gameAJ, gameTD + 5, this.gameTL += 12, 0);
                            break;
                        case 6:
                            mFont.tahoma_7_white.gameAA(var1, mResources.gameAK, gameTD + 5, this.gameTL += 12, 0);
                            break;
                        case 7:
                            mFont.tahoma_7_white.gameAA(var1, mResources.gameAL, gameTD + 5, this.gameTL += 12, 0);
                            break;
                        case 8:
                            mFont.tahoma_7_white.gameAA(var1, mResources.gameAM, gameTD + 5, this.gameTL += 12, 0);
                    }
                }

                if (gameHL == 1 && indexRow >= 0 && gameHM > 0) {
                    SmallImage.gameAA(var1, 942, gameTD - 8, gameTE + 2 + indexRow * 12, 0, StaticObj.TOP_LEFT);
                }

                ++gameHM;
                scrMain.gameAA(gameHM, 12, popupX, popupY + 32, popupW, popupH - 44, true, 1);
            }
        }

    }

    private static String[] gameAA(mFont var0, String var1) {
        return var0.gameAB(var1, popupW - 20);
    }

    private void gameAA(mGraphics var1, mFont var2, String[] var3, int var4, int var5, int var6) {
        var6 = var5;

        for (int var7 = 0; var7 < var3.length; ++var7) {
            String var8;
            if ((var8 = var3[var7]).startsWith("c")) {
                if (var8.startsWith("c0")) {
                    var8 = var8.substring(2);
                    var2 = mFont.tahoma_7_white;
                } else if (var8.startsWith("c1")) {
                    var8 = var8.substring(2);
                    var2 = mFont.tahoma_7_yellow;
                } else if (var8.startsWith("c2")) {
                    var8 = var8.substring(2);
                    var2 = mFont.tahoma_7_green;
                }
            }

            if (var7 == 0) {
                var2.gameAA(var1, var8, var4, var5, 0);
            } else {
                if (var7 * scrMain.ITEM_SIZE + var6 >= scrMain.cmy - 12 && var7 * scrMain.ITEM_SIZE < scrMain.cmy + popupH - 44) {
                    var5 += 12;
                    var2.gameAA(var1, var8, var4, var5, 0);
                } else {
                    var5 += 12;
                }

                this.gameTL += 12;
                ++gameHM;
            }
        }

    }

    private void gameAA(mGraphics var1, mFont var2, String var3, int var4, int var5, int var6) {
        try {
            int var7 = GameCanvas.isTouch && GameCanvas.w >= 320 ? 20 : 10;
            int var8 = var5;
            String[] var10 = var2.gameAB(var3, gameTS - var7);

            for (var7 = 0; var7 < var10.length; ++var7) {
                if (var7 == 0) {
                    var2.gameAA(var1, var10[var7], var4, var5, var6);
                } else {
                    if (var7 * scrMain.ITEM_SIZE + var8 >= scrMain.cmy - 12 && var7 * scrMain.ITEM_SIZE < scrMain.cmy + popupH - 44) {
                        String var10002 = var10[var7];
                        var5 += 12;
                        var2.gameAA(var1, var10002, var4, var5, var6);
                        this.gameTL += 12;
                    } else {
                        var5 += 12;
                    }

                    ++gameHM;
                }
            }

        } catch (Exception var9) {
            System.out.println("loi  " + var9.toString());
        }
    }

    private void gameAA(mGraphics var1, mFont var2, String var3, int var4, int var5, int var6, int var7) {
        var6 = var5;
        String[] var8 = var2.gameAB(var3, var7);

        for (var7 = 0; var7 < var8.length; ++var7) {
            if (var7 == 0) {
                var2.gameAA(var1, var8[var7], var4, var5, 0);
            } else {
                if (var7 * scrMain.ITEM_SIZE + var6 >= scrMain.cmy - 12 && var7 * scrMain.ITEM_SIZE < scrMain.cmy + popupH - 44) {
                    String var10002 = var8[var7];
                    var5 += 12;
                    var2.gameAA(var1, var10002, var4, var5, 0);
                    this.gameTL += 12;
                } else {
                    var5 += 12;
                }

                ++gameHM;
            }
        }

    }

    private void gameBM(mGraphics var1) {
        if (GameCanvas.gameAJ && !gameDJ() && (gameDI() || gameDG() || gameBA())) {
            gameDU();
            gameBN(var1);
            gameAB(var1);
            this.gameAA(var1, mFont.tahoma_7_white, (String) mResources.gameEQ, gameTQ + gameTS / 2, gameTR + gameTT / 2 - 20, 2);
        }

        if (gameDQ && this.gameHN != null && this.gameHN.template != null) {
            Item var2 = this.gameHN;
            if (gameHQ && !this.gameHN.isUpMax() && indexMenu == 0) {
                var2 = this.gameHN.viewNext(this.gameHN.upgrade + 1);
            }

            if (gameKX && indexMenu == 0 && gameHL == 1 && var2.isTypeBody() && var2.upgrade == 0 && arrItemConvert[0] != null && arrItemConvert[0].template.type == arrItemConvert[1].template.type && arrItemConvert[1].template.level >= arrItemConvert[0].template.level) {
                var2 = this.gameHN.viewNext(arrItemConvert[0].upgrade);
            }

            gameAB(var1);
            int var3;
            int var4;
            if (var2.expires != 0L && var2.options != null && var2.options.size() > 0) {
                for (var4 = 0; var4 < var2.options.size(); ++var4) {
                    if ((var3 = ((ItemOption) var2.options.elementAt(var4)).getOptionString().length() * 5) > gameTS && !GameCanvas.gameAJ) {
                        gameTS = var3;
                    }
                }
            }

            if ((var3 = mFont.tahoma_7b_white.gameAA(var2.template.name) + 10) > gameTS && !GameCanvas.gameAJ) {
                gameTS = var3;
            }

            if (gameTS > GameCanvas.w - 4) {
                gameTS = GameCanvas.w - 4;
            }

            if (gameTT > GameCanvas.h - 4) {
                gameTS = GameCanvas.h - 4;
            }

            gameTQ = gW / 2 - gameTS / 2;
            gameTR = gH / 2 - gameTT / 2;
            gameDU();
            if (gameTQ < 2) {
                gameTQ = 2;
            }

            if (gameTR < 2) {
                gameTR = 2;
            }

            gameBN(var1);
            if (gameME && indexMenu == 0) {
                if (Char.clan != null) {
                    this.gameTL = gameTR - 9;
                    gameHM = 2;
                    gameHO.gameAA(var1, gameTQ, gameTR + 2, gameTS, gameTT - 2);
                    gameTS = mFont.tahoma_7_white.gameAA(mResources.gameVY[Char.clan.itemLevel][1]) + 10;

                    for (var4 = 0; var4 < 2; ++var4) {
                        mFont.tahoma_7_white.gameAA(var1, mResources.gameVY[Char.clan.itemLevel][var4], gameTQ + 8, this.gameTL += 12, 0);
                    }

                    if (indexRow >= 0 && (!GameCanvas.isTouch || GameCanvas.isTouch && GameCanvas.w < 320)) {
                        SmallImage.gameAA(var1, 942, gameTQ + 1, gameTR + 5 + indexRow * 12, 0, StaticObj.TOP_LEFT);
                    }

                    gameHO.gameAA(gameHM, 12, gameTQ, gameTR + 2, gameTS, gameTT - 4, true, 1);
                }
            } else {
                gameHO.gameAA(var1, gameTQ, gameTR + 2, gameTS, gameTT - 2);
                gameHM = 3;
                this.gameTL = gameTR + 3;
                mFont var9 = mFont.tahoma_7b_white;
                if (var2.isTypeMounts()) {
                    if (var2.sys >= 1 && var2.sys < 4) {
                        var9 = mFont.tahoma_7b_blue;
                    } else if (var2.sys >= 4 && var2.sys < 8) {
                        var9 = mFont.tahoma_7b_green;
                    } else if (var2.sys >= 8 && var2.sys < 12) {
                        var9 = mFont.tahoma_7b_yellow;
                    } else if (var2.sys >= 12 && var2.sys < 15) {
                        var9 = mFont.tahoma_7b_purple;
                    } else if (var2.sys >= 15) {
                        var9 = mFont.tahoma_7b_red;
                    }
                } else if (var2.upgrade >= 1 && var2.upgrade < 4) {
                    var9 = mFont.tahoma_7b_blue;
                } else if (var2.upgrade >= 4 && var2.upgrade < 8) {
                    var9 = mFont.tahoma_7b_green;
                } else if (var2.upgrade >= 8 && var2.upgrade < 12) {
                    var9 = mFont.tahoma_7b_yellow;
                } else if (var2.upgrade >= 12 && var2.upgrade < 15) {
                    var9 = mFont.tahoma_7b_purple;
                } else if (var2.upgrade >= 15) {
                    var9 = mFont.tahoma_7b_red;
                }

                if (var2.img != null) {
                    var1.gameAA(var2.img, 0, 0, mGraphics.gameAA(var2.img), mGraphics.gameAB(var2.img), 0, gameTQ + gameTS / 2, this.gameTL + gameTT - 10, 33);
                }

                if (var2.isTypeMounts()) {
                    this.gameAA(var1, var9, (String) (var2.template.name + " + " + (var2.sys + 1)), gameTQ + 8, this.gameTL, 0);
                } else {
                    this.gameAA(var1, var9, (String) (var2.template.name + (var2.upgrade > 0 ? " +" + var2.upgrade : "")), gameTQ + 8, this.gameTL, 0);
                }

                if (var2.upgrade >= 15 && !gameHT && !var2.isTypeMounts()) {
                    if (var9.gameAB(var2.template.name + (var2.upgrade > 0 ? " +" + var2.upgrade : ""), gameTS - (GameCanvas.isTouch && GameCanvas.w >= 320 ? 20 : 10)).length > 1) {
                        this.gameTL -= 12;
                    }

                    if (var2.isTypeMounts()) {
                        this.gameAA(var1, mFont.tahoma_7b_white, (String) var2.template.name, gameTQ + 8, this.gameTL, 0);
                    } else {
                        this.gameAA(var1, mFont.tahoma_7b_white, (String) (var2.template.name + (var2.upgrade > 0 ? " +" + var2.upgrade : "")), gameTQ + 8, this.gameTL, 0);
                    }
                }
                String result = "indexUI: " + var2.indexUI + " - ID: [" + var2.template.id + "]";

                mFont.tahoma_7_blue1.gameAA(var1, result, gameTQ + 8, this.gameTL += 12, 0, mFont.tahoma_7);

                int var5;
                if (var2.isTypeBody()) {
                    this.gameTL += 12;
                    ++gameHM;
                    if (gameHT && GameCanvas.gameTick % 5 == 0) {
                        gameHT = !gameHT;
                    } else if (!gameHT && GameCanvas.gameTick % 5 == 0) {
                        gameHT = !gameHT;
                    }

                    var5 = var2.upgrade / 2 + 1;
                    if (var2.upgrade == 0) {
                        for (var4 = 0; var4 < var5; ++var4) {
                            SmallImage.gameAA(var1, 633, gameTQ + 12 + var4 * 10, this.gameTL + 5, 0, StaticObj.VCENTER_HCENTER);
                        }
                    } else if (var2.upgrade >= 1 && var2.upgrade < 4) {
                        for (var4 = 0; var4 < var5; ++var4) {
                            SmallImage.gameAA(var1, 625, gameTQ + 12 + var4 * 10, this.gameTL + 5, 0, StaticObj.VCENTER_HCENTER);
                        }

                        if (var2.upgrade == 3) {
                            SmallImage.gameAA(var1, 635, gameTQ + 12 + var5 * 10, this.gameTL + 5, 0, StaticObj.VCENTER_HCENTER);
                        }
                    } else if (var2.upgrade >= 4 && var2.upgrade < 8) {
                        for (var4 = 0; var4 < var5; ++var4) {
                            SmallImage.gameAA(var1, 626, gameTQ + 12 + var4 * 10, this.gameTL + 5, 0, StaticObj.VCENTER_HCENTER);
                        }

                        if (var2.upgrade % 2 != 0) {
                            SmallImage.gameAA(var1, 636, gameTQ + 12 + var5 * 10, this.gameTL + 5, 0, StaticObj.VCENTER_HCENTER);
                        }
                    } else if (var2.upgrade >= 8 && var2.upgrade < 12) {
                        for (var4 = 0; var4 < var5; ++var4) {
                            if (gameHT) {
                                SmallImage.gameAA(var1, 627, gameTQ + 12 + var4 * 10, this.gameTL + 5, 0, StaticObj.VCENTER_HCENTER);
                            } else {
                                SmallImage.gameAA(var1, 628, gameTQ + 12 + var4 * 10, this.gameTL + 5, 0, StaticObj.VCENTER_HCENTER);
                            }
                        }

                        if (var2.upgrade % 2 != 0) {
                            if (gameHT) {
                                SmallImage.gameAA(var1, 637, gameTQ + 12 + var5 * 10, this.gameTL + 5, 0, StaticObj.VCENTER_HCENTER);
                            } else {
                                SmallImage.gameAA(var1, 638, gameTQ + 12 + var5 * 10, this.gameTL + 5, 0, StaticObj.VCENTER_HCENTER);
                            }
                        }
                    } else if (var2.upgrade >= 12 && var2.upgrade < 15) {
                        for (var4 = 0; var4 < var5; ++var4) {
                            if (gameHT) {
                                SmallImage.gameAA(var1, 629, gameTQ + 12 + var4 * 10, this.gameTL + 5, 0, StaticObj.VCENTER_HCENTER);
                            } else {
                                SmallImage.gameAA(var1, 630, gameTQ + 12 + var4 * 10, this.gameTL + 5, 0, StaticObj.VCENTER_HCENTER);
                            }
                        }

                        if (var2.upgrade % 2 != 0) {
                            if (gameHT) {
                                SmallImage.gameAA(var1, 639, gameTQ + 12 + var5 * 10, this.gameTL + 5, 0, StaticObj.VCENTER_HCENTER);
                            } else {
                                SmallImage.gameAA(var1, 640, gameTQ + 12 + var5 * 10, this.gameTL + 5, 0, StaticObj.VCENTER_HCENTER);
                            }
                        }
                    } else {
                        for (var4 = 0; var4 < var5; ++var4) {
                            if (gameHT) {
                                SmallImage.gameAA(var1, 631, gameTQ + 12 + var4 * 10, this.gameTL + 5, 0, StaticObj.VCENTER_HCENTER);
                            } else {
                                SmallImage.gameAA(var1, 632, gameTQ + 12 + var4 * 10, this.gameTL + 5, 0, StaticObj.VCENTER_HCENTER);
                            }
                        }

                        if (var2.upgrade % 2 != 0) {
                            if (gameHT) {
                                SmallImage.gameAA(var1, 641, gameTQ + 12 + var5 * 10, this.gameTL + 5, 0, StaticObj.VCENTER_HCENTER);
                            } else {
                                SmallImage.gameAA(var1, 642, gameTQ + 12 + var5 * 10, this.gameTL + 5, 0, StaticObj.VCENTER_HCENTER);
                            }
                        }
                    }
                } else if (var2.isTypeMounts()) {
                    this.gameTL += 12;
                    var5 = var2.sys + 1;

                    for (var4 = 0; var4 < var5; ++var4) {
                        SmallImage.gameAA(var1, 633, gameTQ + 12 + var4 * 10, this.gameTL + 5, 0, StaticObj.VCENTER_HCENTER);
                    }
                }

                mFont.tahoma_7_white.gameAA(var1, var2.isLock ? mResources.gameKB : mResources.gameKH, gameTQ + 8, this.gameTL += 12, 0);
                String var10;
                if ((var2.isTypeBody() || var2.isTypeMounts()) && (var10 = var2.template.type == 12 ? mResources.gameFN : (var2.template.level >= 10 && var2.template.type < 10 ? (var2.upgrade == 0 ? mResources.gameFL : null) : mResources.gameFM)) != null) {
                    this.gameAA(var1, mFont.tahoma_7_white, (String) var10, gameTQ + 8, this.gameTL += 12, 0);
                    ++gameHM;
                }

                if (var2.template.gender == 0 || var2.template.gender == 1) {
                    if (var2.template.gender == Char.getMyChar().cgender) {
                        mFont.tahoma_7_white.gameAA(var1, mResources.gameRB[var2.template.gender], gameTQ + 8, this.gameTL += 12, 0);
                        ++gameHM;
                    } else {
                        mFont.tahoma_7_red.gameAA(var1, mResources.gameRB[var2.template.gender], gameTQ + 8, this.gameTL += 12, 0);
                        ++gameHM;
                    }
                }

                if (Char.getMyChar().clevel != -1) {
                    if (Char.getMyChar().clevel >= var2.template.level) {
                        this.gameAA(var1, mFont.tahoma_7_white, (String) (mResources.gameKU + " " + var2.template.level), gameTQ + 8, this.gameTL += 12, 0);
                    } else {
                        this.gameAA(var1, mFont.tahoma_7_red, (String) (mResources.gameKU + " " + var2.template.level), gameTQ + 8, this.gameTL += 12, 0);
                    }
                }

                boolean var11;
                if ((var2.template.id < 40 || var2.template.id > 48) && var2.template.id != 311 && var2.template.id != 375 && var2.template.id != 397 && var2.template.id != 552 && var2.template.id != 558) {
                    if ((var2.template.id < 49 || var2.template.id > 57) && var2.template.id != 312 && var2.template.id != 376 && var2.template.id != 398 && var2.template.id != 553 && var2.template.id != 559) {
                        if ((var2.template.id < 58 || var2.template.id > 66) && var2.template.id != 313 && var2.template.id != 377 && var2.template.id != 399 && var2.template.id != 554 && var2.template.id != 560) {
                            if ((var2.template.id < 67 || var2.template.id > 75) && var2.template.id != 314 && var2.template.id != 378 && var2.template.id != 400 && var2.template.id != 555 && var2.template.id != 561) {
                                if ((var2.template.id < 76 || var2.template.id > 84) && var2.template.id != 315 && var2.template.id != 379 && var2.template.id != 401 && var2.template.id != 556 && var2.template.id != 562) {
                                    if (var2.template.id >= 85 && var2.template.id <= 93 || var2.template.id == 316 || var2.template.id == 380 || var2.template.id == 402 || var2.template.id == 557 || var2.template.id == 563) {
                                        var11 = false;
                                        if (Char.getMyChar().nClass.classId == 6) {
                                            mFont.tahoma_7_white.gameAA(var1, mResources.gameKV + " " + nClasss[6].name, gameTQ + 8, this.gameTL += 12, 0);
                                        } else {
                                            mFont.tahoma_7_red.gameAA(var1, mResources.gameKV + " " + nClasss[6].name, gameTQ + 8, this.gameTL += 12, 0);
                                        }

                                        ++gameHM;
                                    }
                                } else {
                                    var11 = false;
                                    if (Char.getMyChar().nClass.classId == 5) {
                                        mFont.tahoma_7_white.gameAA(var1, mResources.gameKV + " " + nClasss[5].name, gameTQ + 8, this.gameTL += 12, 0);
                                    } else {
                                        mFont.tahoma_7_red.gameAA(var1, mResources.gameKV + " " + nClasss[5].name, gameTQ + 8, this.gameTL += 12, 0);
                                    }

                                    ++gameHM;
                                }
                            } else {
                                var11 = false;
                                if (Char.getMyChar().nClass.classId == 4) {
                                    mFont.tahoma_7_white.gameAA(var1, mResources.gameKV + " " + nClasss[4].name, gameTQ + 8, this.gameTL += 12, 0);
                                } else {
                                    mFont.tahoma_7_red.gameAA(var1, mResources.gameKV + " " + nClasss[4].name, gameTQ + 8, this.gameTL += 12, 0);
                                }

                                ++gameHM;
                            }
                        } else {
                            var11 = false;
                            if (Char.getMyChar().nClass.classId == 3) {
                                mFont.tahoma_7_white.gameAA(var1, mResources.gameKV + " " + nClasss[3].name, gameTQ + 8, this.gameTL += 12, 0);
                            } else {
                                mFont.tahoma_7_red.gameAA(var1, mResources.gameKV + " " + nClasss[3].name, gameTQ + 8, this.gameTL += 12, 0);
                            }

                            ++gameHM;
                        }
                    } else {
                        var11 = false;
                        if (Char.getMyChar().nClass.classId == 2) {
                            mFont.tahoma_7_white.gameAA(var1, mResources.gameKV + " " + nClasss[2].name, gameTQ + 8, this.gameTL += 12, 0);
                        } else {
                            mFont.tahoma_7_red.gameAA(var1, mResources.gameKV + " " + nClasss[2].name, gameTQ + 8, this.gameTL += 12, 0);
                        }

                        ++gameHM;
                    }
                } else {
                    var11 = false;
                    if (Char.getMyChar().nClass.classId == 1) {
                        mFont.tahoma_7_white.gameAA(var1, mResources.gameKV + " " + nClasss[1].name, gameTQ + 8, this.gameTL += 12, 0);
                    } else {
                        mFont.tahoma_7_red.gameAA(var1, mResources.gameKV + " " + nClasss[1].name, gameTQ + 8, this.gameTL += 12, 0);
                    }

                    ++gameHM;
                }

                if (!var2.isTypeMounts()) {
                    if (var2.template.id == 420) {
                        if (Char.getMyChar().nClass.classId != 1 && Char.getMyChar().nClass.classId != 2) {
                            mFont.tahoma_7_red.gameAA(var1, mResources.gamePL[1], gameTQ + 8, this.gameTL += 12, 0);
                        } else {
                            mFont.tahoma_7_white.gameAA(var1, mResources.gamePL[1], gameTQ + 8, this.gameTL += 12, 0);
                        }

                        ++gameHM;
                    } else if (var2.template.id == 421) {
                        if (Char.getMyChar().nClass.classId != 3 && Char.getMyChar().nClass.classId != 4) {
                            mFont.tahoma_7_red.gameAA(var1, mResources.gamePL[2], gameTQ + 8, this.gameTL += 12, 0);
                        } else {
                            mFont.tahoma_7_white.gameAA(var1, mResources.gamePL[2], gameTQ + 8, this.gameTL += 12, 0);
                        }

                        ++gameHM;
                    } else if (var2.template.id == 422) {
                        if (Char.getMyChar().nClass.classId != 5 && Char.getMyChar().nClass.classId != 6) {
                            mFont.tahoma_7_red.gameAA(var1, mResources.gamePL[3], gameTQ + 8, this.gameTL += 12, 0);
                        } else {
                            mFont.tahoma_7_white.gameAA(var1, mResources.gamePL[3], gameTQ + 8, this.gameTL += 12, 0);
                        }

                        ++gameHM;
                    }
                }

                if (var2.expires > 0L) {
                    if (!var2.isTypeUIShop() && !var2.isTypeUIShopLock() && !var2.isTypeUIStore() && !var2.isTypeUIBook() && !var2.isTypeUIFashion() && !var2.isTypeUIClanShop() && var2.typeUI != 39) {
                        if ((var3 = mFont.tahoma_7.gameAA(mResources.gameKI + ": " + var2.getExpiresString()) + 10) > gameTS && !GameCanvas.gameAJ) {
                            gameTS = var3;
                        }

                        this.gameAA(var1, mFont.tahoma_7_yellow, (String) (mResources.gameKI + ": " + var2.getExpiresString()), gameTQ + 8, this.gameTL += 12, 0);
                    } else {
                        if ((var3 = mFont.tahoma_7.gameAA(mResources.gameKI + ": " + var2.getExpiresShopString()) + 10) > gameTS && !GameCanvas.gameAJ) {
                            gameTS = var3;
                        }

                        this.gameAA(var1, mFont.tahoma_7_yellow, (String) (mResources.gameKI + ": " + var2.getExpiresShopString()), gameTQ + 8, this.gameTL += 12, 0);
                    }

                    ++gameHM;
                }

                if (!var2.template.description.equals("")) {
                    this.gameAA(var1, mFont.tahoma_7_white, (String) var2.template.description, gameTQ + 8, this.gameTL += 12, 0);
                    ++gameHM;
                }

                if (!var2.isTypeUIMe() && var2.typeUI != 37) {
                    if (var2.isTypeUIShop() || var2.isTypeUIShopLock() || var2.isTypeUIStore() || var2.isTypeUIBook() || var2.isTypeUIFashion() || var2.isTypeUIClanShop()) {
                        if (var2.buyCoin > 0) {
                            if (var2.isTypeUIClanShop()) {
                                if ((var3 = mFont.tahoma_7.gameAA(mResources.gameAA(mResources.gameKL, NinjaUtil.gameAA(String.valueOf(var2.buyCoin)))) + 10) > gameTS && !GameCanvas.gameAJ) {
                                    gameTS = var3;
                                }

                                mFont.tahoma_7_yellow.gameAA(var1, mResources.gameAA(mResources.gameKL, NinjaUtil.gameAA(String.valueOf(var2.buyCoin))), gameTQ + 8, this.gameTL += 12, 0);
                            } else {
                                mFont.tahoma_7_yellow.gameAA(var1, mResources.gameAA(mResources.gameKK, NinjaUtil.gameAA(String.valueOf(var2.buyCoin))), gameTQ + 8, this.gameTL += 12, 0);
                            }

                            ++gameHM;
                        } else if (var2.buyCoinLock > 0) {
                            mFont.tahoma_7_yellow.gameAA(var1, mResources.gameAA(mResources.gameKM, NinjaUtil.gameAA(String.valueOf(var2.buyCoinLock))), gameTQ + 8, this.gameTL += 12, 0);
                            ++gameHM;
                        } else if (var2.buyGold > 0) {
                            mFont.tahoma_7_yellow.gameAA(var1, mResources.gameAA(mResources.gameKN, NinjaUtil.gameAA(String.valueOf(var2.buyGold))), gameTQ + 8, this.gameTL += 12, 0);
                            ++gameHM;
                        }
                    }
                } else {
                    mFont.tahoma_7_yellow.gameAA(var1, mResources.gameAA(mResources.gameKJ, NinjaUtil.gameAA(String.valueOf(var2.saleCoinLock))), gameTQ + 8, this.gameTL += 12, 0);
                    ++gameHM;
                }

                if (var2.template.type == 33) {
                    mFont.tahoma_7_yellow.gameAA(var1, mResources.gameEU + ": " + (var2.upgrade + 1), gameTQ + 8, this.gameTL += 12, 0);
                    ++gameHM;
                }

                if (var2.isTypeBody() && var2.sys != 0) {
                    mFont.tahoma_7_blue1.gameAA(var1, mResources.gamePL[var2.sys], gameTQ + 8, this.gameTL += 12, 0);
                    ++gameHM;
                }

                if (var2.expires != 0L && var2.options != null && var2.options.size() > 0) {
                    var11 = false;
                    boolean var12 = false;

                    for (int var6 = 0; var6 < var2.options.size(); ++var6) {
                        ItemOption var7 = (ItemOption) var2.options.elementAt(var6);
                        if (!var11 && var7.optionTemplate.type == 2) {
                            var11 = true;
                            String var8 = mResources.gameRO[0] + ": ";
                            if (var2.template.type == 1) {
                                var8 = var8 + mResources.gameRN[var2.template.type] + "(" + mResources.gameRP[this.gameHN.sys] + ")";
                            } else if (var2.template.type == 0) {
                                var8 = var8 + mResources.gameRN[6] + "(" + mResources.gameRP[this.gameHN.sys] + "), " + mResources.gameRN[5] + "(" + mResources.gameRP[this.gameHN.sys] + ")";
                            } else if (var2.template.type == 6) {
                                var8 = var8 + mResources.gameRN[0] + "(" + mResources.gameRP[this.gameHN.sys] + "), " + mResources.gameRN[5] + "(" + mResources.gameRP[this.gameHN.sys] + ")";
                            } else if (var2.template.type == 5) {
                                var8 = var8 + mResources.gameRN[0] + "(" + mResources.gameRP[this.gameHN.sys] + "), " + mResources.gameRN[6] + "(" + mResources.gameRP[this.gameHN.sys] + ")";
                            } else if (var2.template.type == 2) {
                                var8 = var8 + mResources.gameRN[8] + "(" + mResources.gameRP[this.gameHN.sys] + "), " + mResources.gameRN[7] + "(" + mResources.gameRP[this.gameHN.sys] + ")";
                            } else if (var2.template.type == 8) {
                                var8 = var8 + mResources.gameRN[2] + "(" + mResources.gameRP[this.gameHN.sys] + "), " + mResources.gameRN[7] + "(" + mResources.gameRP[this.gameHN.sys] + ")";
                            } else if (var2.template.type == 7) {
                                var8 = var8 + mResources.gameRN[2] + "(" + mResources.gameRP[this.gameHN.sys] + "), " + mResources.gameRN[8] + "(" + mResources.gameRP[this.gameHN.sys] + ")";
                            } else if (var2.template.type == 4) {
                                var8 = var8 + mResources.gameRN[3] + "(" + mResources.gameRP[this.gameHN.sys] + "), " + mResources.gameRN[9] + "(" + mResources.gameRP[this.gameHN.sys] + ")";
                            } else if (var2.template.type == 3) {
                                var8 = var8 + mResources.gameRN[4] + "(" + mResources.gameRP[this.gameHN.sys] + "), " + mResources.gameRN[9] + "(" + mResources.gameRP[this.gameHN.sys] + ")";
                            } else if (var2.template.type == 9) {
                                var8 = var8 + mResources.gameRN[4] + "(" + mResources.gameRP[this.gameHN.sys] + "), " + mResources.gameRN[3] + "(" + mResources.gameRP[this.gameHN.sys] + ")";
                            }

                            if ((var3 = mFont.tahoma_7_white.gameAA(var8) + 15) > gameTS && !GameCanvas.gameAJ) {
                                gameTS = var3;
                            }

                            this.gameAA(var1, mFont.tahoma_7_white, (String) var8, gameTQ + 8, this.gameTL += 12, 0);
                            ++gameHM;
                        }

                        if (!var12 && var7.optionTemplate.type > 2 && var7.optionTemplate.type < 8) {
                            var12 = true;
                            mFont.tahoma_7_white.gameAA(var1, mResources.gameRO[1], gameTQ + 8, this.gameTL += 12, 0);
                            ++gameHM;
                        }

                        if (var7.optionTemplate.id == 65) {
                            this.gameAA(var1, mFont.tahoma_7_blue, (String) (var2.template.id == 485 ? NinjaUtil.replace(var7.getOptionString(), mResources.gameKA, mResources.gameWW) : var7.getOptionString()), gameTQ + 8, this.gameTL += 12, 0);
                        } else if (var7.optionTemplate.id == 66) {
                            this.gameAA(var1, mFont.tahoma_7_blue1, (String) (var2.template.id == 485 ? NinjaUtil.replace(var7.getOptionString(), mResources.gameWG, mResources.gameWX) : var7.getOptionString()), gameTQ + 8, this.gameTL += 12, 0);
                        } else if (var7.optionTemplate.type == 0) {
                            this.gameAA(var1, mFont.tahoma_7_blue1, (String) (var2.isTypeUIShopView() ? var7.getOptionShopString() : var7.getOptionString()), gameTQ + 8, this.gameTL += 12, 0);
                        } else if (var7.optionTemplate.type == 1) {
                            this.gameAA(var1, mFont.tahoma_7_green, (String) (var2.isTypeUIShopView() ? var7.getOptionShopString() : var7.getOptionString()), gameTQ + 8, this.gameTL += 12, 0);
                        } else if (var7.optionTemplate.type == 8) {
                            if (var7.optionTemplate.id == 85) {
                                this.gameAA(var1, mFont.tahoma_7_yellow, (String) (var2.isTypeUIShopView() ? var7.getOptionShopString() : NinjaUtil.replace(var7.optionTemplate.name, "#", String.valueOf(var7.param))), gameTQ + 8, this.gameTL += 12, 0);
                            } else {
                                mFont var10002 = mFont.tahoma_7b_blue;
                                var2.isTypeUIShopView();
                                this.gameAA(var1, var10002, (String) var7.getOptionShopString(), gameTQ + 8, this.gameTL += 12, 0);
                            }
                        } else if (var7.optionTemplate.type == 2 && var2.typeUI == 5 && var7.active == 1 || var7.optionTemplate.type == 3 && var2.upgrade >= 4 || var7.optionTemplate.type == 4 && var2.upgrade >= 8 || var7.optionTemplate.type == 5 && var2.upgrade >= 12 || var7.optionTemplate.type == 6 && var2.upgrade >= 14 || var7.optionTemplate.type == 7 && var2.upgrade >= 16) {  // cũ 16
                            this.gameAA(var1, mFont.tahoma_7_green, (String) (var2.isTypeUIShopView() ? var7.getOptionShopString() : var7.getOptionString()), gameTQ + 8, this.gameTL += 12, 0);
                        } else {
                            this.gameAA(var1, mFont.tahoma_7_grey, (String) (var2.isTypeUIShopView() ? var7.getOptionShopString() : var7.getOptionString()), gameTQ + 8, this.gameTL += 12, 0);
                        }

                        ++gameHM;
                    }
                }

                if (gameMG) {
                    if (var2.template.id == 12) {
                        mFont.tahoma_7_red.gameAA(var1, NinjaUtil.gameAA(this.yenValue[indexSelect]) + " " + mResources.gamePB, gameTQ + 8, this.gameTL += 12, 0);
                    }

                    if (var2.template.type >= 0 && var2.template.type <= 9) {
                        mFont.tahoma_7_yellow.gameAA(var1, mResources.gameVT, gameTQ + 8, this.gameTL += 12, 0);
                    }

                    ++gameHM;
                }

                if (indexRow >= 0 && (!GameCanvas.isTouch || GameCanvas.isTouch && GameCanvas.w < 320)) {
                    SmallImage.gameAA(var1, 942, gameTQ + 1, gameTR + 5 + indexRow * 12, 0, StaticObj.TOP_LEFT);
                }

                gameHO.gameAA(gameHM, 12, gameTQ, gameTR + 2, gameTS, gameTT - 4, true, 1);
            }
        }
    }

    private static void gameBN(mGraphics var0) {
        gameAB(var0);
        var0.gameAA(0);
        var0.gameAC(gameTQ - 2, gameTR - 2, gameTS + 5, gameTT + 5);
        var0.gameAA(13606712);
        var0.gameAB(gameTQ - 1, gameTR - 1, gameTS + 2, gameTT + 2);
        var0.gameAA(Paint.COLORBACKGROUND);
        var0.gameAC(gameTQ, gameTR, gameTS, gameTT);
    }

    private static void gameDU() {
        if (GameCanvas.gameAJ && (!gameME || indexMenu != 0)) {
            gameTQ = popupX + 175;
            gameTS = popupW - 179;
            gameTR = popupY + 33;
            gameTT = 138;
            if (isPaintTrade && indexMenu == 0) {
                gameTQ = popupX + 6 + 3 * gameHK;
                gameTS = popupW - (11 + 6 * gameHK);
            }

            if (isPaintInfoMe) {
                if (indexMenu == 4) {
                    gameTQ = popupX + 33;
                    gameTR = popupY + 87;
                    gameTS = popupW - 67;
                    gameTT = 75;
                    return;
                }

                if (indexMenu == 5) {
                    gameTT = 161;
                }
            }

        }
    }

    public final void gameBJ() {
        super.center = null;
        if (gameHL != 0 || indexMenu != 1 && indexMenu != 3 && indexMenu != 4) {
            SkillTemplate var1;
            Skill var2;
            switch (indexMenu) {
                case 0:
                    if (gameHL == 1) {
                        if (gameAK(3) != null) {
                            super.left = this.gameUA;
                            if (GameCanvas.isTouch && GameCanvas.w < 320 || !GameCanvas.isTouch) {
                                super.center = this.gameTZ;
                            }
                        } else {
                            gameDQ = false;
                            super.left = this.gameTV;
                        }
                    }
                    break;
                case 1:
                    if (gameHL == 1) {
                        super.left = null;
                        if (indexSelect >= 0) {
                            var1 = Char.getMyChar().nClass.skillTemplates[indexSelect];
                            if ((var2 = Char.getMyChar().gameAA(var1)) != null) {
                                if (var2.point < var1.maxPoint) {
                                    super.left = new Command(mResources.gameDY, 14001);
                                }

                                if (var2.template.type != 1 && var2.template.type != 4 && var2.template.type != 2 && var2.template.type != 3) {
                                    super.center = null;
                                } else {
                                    super.center = new Command(mResources.gameER, 11081);
                                }
                            }
                        }
                    }
                    break;
                case 2:
                    if (gameHL >= 1) {
                        super.left = new Command(mResources.gameDY, 11084);
                        super.center = new Command("", 11084);
                    }
                    break;
                case 3:
                    super.left = null;
                    super.center = new Command(mResources.gameGA, 110854);
                    break;
                case 4:
                    if (gameHL == 1) {
                        super.left = null;
                        Item var3;
                        if ((var3 = gameAK(5)) != null) {
                            if (currentCharViewInfo.charID == Char.getMyChar().charID) {
                                super.left = new Command(mResources.gameEO, 11082);
                                if (GameCanvas.gameAJ) {
                                    this.gameAA((int) 5, (Item) var3);
                                } else {
                                    super.center = new Command(mResources.gameBX, 11083);
                                }
                            } else if (GameCanvas.gameAJ) {
                                this.gameAA((int) 5, (Item) var3);
                            } else {
                                super.center = new Command(mResources.gameBX, 11083);
                            }
                        } else {
                            gameDQ = false;
                            if (!GameCanvas.isTouch && gameRJ > 0) {
                                super.left = new Command(mResources.gameXI, 2003);
                            }
                        }
                    }
                    break;
                case 5:
                    super.left = null;
                    if (gameHL == 1 && indexSelect >= 0 && currentCharViewInfo.arrItemMounts[indexSelect] != null) {
                        if (Char.getMyChar().charID == currentCharViewInfo.charID) {
                            super.left = new Command(mResources.gameCJ, 1516);
                        }

                        super.center = new Command(GameCanvas.gameAJ ? "" : mResources.gameBX, 1515);
                    }
                    break;
                case 6:
                    if (gameVT == 0) {
                        super.left = null;
                        if (gameHL == 1 && indexSelect >= 0 && currentCharViewInfo.arrItemViThu[indexSelect] != null) {
                            if (Char.getMyChar().charID == currentCharViewInfo.charID) {
                                super.left = new Command(mResources.gameCJ, 508);
                            }

                            super.center = new Command(GameCanvas.gameAJ ? "" : mResources.gameBX, 503);
                        }
                    } else if (gameVT == 1) {
                        if (gameHL >= 1) {
                            super.left = new Command(mResources.gameDY, 504);
                        }
                        break;
                    } else {
                        if (gameHL == 1) {
                            super.left = null;
                            if (indexSelect >= 0) {
                                var1 = Char.getMyChar().nClass.skillTemplates[indexSelect];
                                if ((var2 = Char.getMyChar().gameAA(var1)) != null) {
                                    if (var2.point < var1.maxPoint) {
                                        super.left = new Command(mResources.gameDY, 14001);
                                    }

                                    if (var2.template.type != 1 && var2.template.type != 4 && var2.template.type != 2 && var2.template.type != 3) {
                                        super.center = null;
                                    } else {
                                        super.center = new Command(mResources.gameER, 11081);
                                    }
                                }
                            }
                        }

                        if (gameHL == 1) {
                            super.left = null;
                            if (indexSelect >= 0) {
                                var1 = Char.getMyChar().nClass.skillTemplates[indexSelect];
                                if ((var2 = Char.getMyChar().gameAA(var1)) != null) {
                                    if (var2.point < var1.maxPoint) {
                                        super.left = new Command(mResources.gameDY, 505);
                                    }

                                    super.center = null;
                                }
                            }
                        }
                    }
            }

            if (indexMenu != 6 && currentCharViewInfo.charID == Char.getMyChar().charID) {
                super.right = new Command(mResources.gameFE, 11086);
            } else {
                super.right = this.gameSE;
            }
        } else {
            super.left = null;
        }
    }

    public final void gameAD(int var1) {
        gameHL = 0;
        super.right = this.gameSE;
        switch (var1) {
            case 2:
                indexMenu = 0;
                gameKR = true;
                if (arrItemWeapon == null) {
                    Service.gI().requestItem(2);
                }
            case 3:
            case 5:
            case 30:
            case 39:
            case 41:
            case 42:
            case 51:
            default:
                break;
            case 4:
                indexMenu = 0;
                gameMA = true;
                if (Char.getMyChar().arrItemBox == null) {
                    Service.gI().requestItem(4);
                }
                break;
            case 6:
                indexMenu = 0;
                gameKS = true;
                if (arrItemStack == null) {
                    Service.gI().requestItem(6);
                }
                break;
            case 7:
                indexMenu = 0;
                gameKT = true;
                if (arrItemStackLock == null) {
                    Service.gI().requestItem(7);
                }
                break;
            case 8:
                indexMenu = 0;
                gameKU = true;
                if (arrItemGrocery == null) {
                    Service.gI().requestItem(8);
                }
                break;
            case 9:
                indexMenu = 0;
                gameKV = true;
                if (arrItemGroceryLock == null) {
                    Service.gI().requestItem(9);
                }
                break;
            case 10:
                indexMenu = 0;
                gameKW = true;
                arrItemUpGrade = new Item[18];
                break;
            case 11:
                indexMenu = 0;
                gameKZ = true;
                gameHP = true;
                arrItemUpPeal = new Item[24];
                break;
            case 12:
                indexMenu = 0;
                gameKZ = true;
                gameHP = false;
                arrItemUpPeal = new Item[24];
                break;
            case 13:
                indexMenu = 0;
                gameMB = true;
                arrItemSplit = new Item[24];
                break;
            case 14:
                gameKB = true;
                indexMenu = 0;
                if (arrItemStore == null) {
                    Service.gI().requestItem(14);
                }
                break;
            case 15:
                gameKB = true;
                indexMenu = 1;
                if (arrItemBook == null) {
                    Service.gI().requestItem(15);
                }
                break;
            case 16:
                indexMenu = 0;
                gameKN = true;
                if (arrItemLien == null) {
                    Service.gI().requestItem(16);
                }
                break;
            case 17:
                indexMenu = 0;
                gameKO = true;
                if (arrItemNhan == null) {
                    Service.gI().requestItem(17);
                }
                break;
            case 18:
                indexMenu = 0;
                gameKP = true;
                if (arrItemNgocBoi == null) {
                    Service.gI().requestItem(18);
                }
                break;
            case 19:
                indexMenu = 0;
                gameKQ = true;
                if (arrItemPhu == null) {
                    Service.gI().requestItem(19);
                }
                break;
            case 20:
                indexMenu = 0;
                gameKD = true;
                if (arrItemNonNam == null) {
                    Service.gI().requestItem(20);
                }
                break;
            case 21:
                indexMenu = 0;
                gameKE = true;
                if (arrItemNonNu == null) {
                    Service.gI().requestItem(21);
                }
                break;
            case 22:
                indexMenu = 0;
                gameKF = true;
                if (arrItemAoNam == null) {
                    Service.gI().requestItem(22);
                }
                break;
            case 23:
                indexMenu = 0;
                gameKG = true;
                if (arrItemAoNu == null) {
                    Service.gI().requestItem(23);
                }
                break;
            case 24:
                indexMenu = 0;
                gameKH = true;
                if (arrItemGangTayNam == null) {
                    Service.gI().requestItem(24);
                }
                break;
            case 25:
                indexMenu = 0;
                gameKI = true;
                if (arrItemGangTayNu == null) {
                    Service.gI().requestItem(25);
                }
                break;
            case 26:
                indexMenu = 0;
                gameKJ = true;
                if (arrItemQuanNam == null) {
                    Service.gI().requestItem(26);
                }
                break;
            case 27:
                indexMenu = 0;
                gameKK = true;
                if (arrItemQuanNu == null) {
                    Service.gI().requestItem(27);
                }
                break;
            case 28:
                indexMenu = 0;
                gameKL = true;
                if (arrItemGiayNam == null) {
                    Service.gI().requestItem(28);
                }
                break;
            case 29:
                indexMenu = 0;
                gameKM = true;
                if (arrItemGiayNu == null) {
                    Service.gI().requestItem(29);
                }
                break;
            case 31:
                indexMenu = 0;
                gameKW = true;
                gameKY = true;
                arrItemUpGrade = new Item[18];
                break;
            case 32:
                gameKB = true;
                indexMenu = 2;
                if (arrItemFashion == null) {
                    Service.gI().requestItem(32);
                }
                break;
            case 33:
                indexMenu = 0;
                gameKX = true;
                arrItemConvert = new Item[3];
                break;
            case 34:
                gameKB = true;
                indexMenu = 3;
                if (arrItemClanShop == null) {
                    Service.gI().requestItem(34);
                }
                break;
            case 35:
                gameKC = true;
                indexMenu = 0;
                if (arrItemElites == null) {
                    Service.gI().requestItem(35);
                }
                break;
            case 36:
                indexMenu = 0;
                gameDN = true;
                itemSell = null;
                this.gameND = new TField();
                this.gameND.setMaxTextLenght(9);
                this.gameND.setIputType(1);
                this.gameND.width = 100;
                this.gameND.height = mScreen.ITEM_HEIGHT + 2;
                break;
            case 37:
                gameMH = true;
                this.gameBC();
                break;
            case 38:
                arrItemSprin = null;
                gameMG = true;
                gameHL = 1;
                this.gameBC();
                break;
            case 40:
                isPaintAuto = true;
                gameHL = 1;
                indexRow = 0;
                this.gameBC();
                break;
            case 43:
                indexMenu = 0;
                gameMI = true;
                arrItemUpPeal = new Item[24];
                break;
            case 44:
                indexMenu = 0;
                gameMJ = true;
                arrItemSplit = new Item[24];
                break;
            case 45:
                indexMenu = 0;
                gameMK = true;
                arrItemSplit = new Item[24];
                break;
            case 46:
                indexMenu = 0;
                gameML = true;
                arrItemSplit = new Item[24];
                break;
            case 47:
                indexMenu = 0;
                gameMM = true;
                arrItemUpGrade = new Item[18];
                break;
            case 48:
                indexMenu = 0;
                gameMQ = true;
                arrItemSplit = new Item[18];
                break;
            case 49:
                indexMenu = 0;
                gameMN = true;
                itemSplit = null;
                break;
            case 50:
                indexMenu = 0;
                gameMO = true;
                itemSplit = null;
                break;
            case 52:
                gameKB = true;
                indexMenu = 52;
                Service.gI().requestItem(52);
                break;
            case 53:
                indexMenu = 0;
                gameMR = true;
                arrItemSplit = new Item[24];
        }

        gameAB(175, 200);
        GameScr var4 = this;
        this.gameVP = new int[9];
        this.gameVQ = new int[9];
        int var3 = popupX + 3;
        int var2 = popupY + 34 + gameHK;
        var4.gameVP[0] = popupX + 74;
        var4.gameVQ[0] = var2 + 4;
        var4.gameVP[1] = var3 + 1;
        var4.gameVQ[1] = var2 - gameHK;
        var4.gameVP[2] = var3 + popupW / 2 - gameHK / 2 - 2;
        var4.gameVQ[2] = var2 - gameHK;
        var4.gameVP[3] = var4.gameVP[1];
        var4.gameVP[4] = var4.gameVP[0];
        var4.gameVP[5] = var4.gameVP[2];
        var4.gameVQ[3] = var4.gameVQ[1] + gameHK * 3 - 2;
        var4.gameVQ[4] = var4.gameVQ[1] + gameHK * 3 - 2;
        var4.gameVQ[5] = var4.gameVQ[1] + gameHK * 3 - 2;
        var4.gameVP[6] = var4.gameVP[1];
        var4.gameVP[7] = var4.gameVP[0];
        var4.gameVP[8] = var4.gameVP[2];
        var4.gameVQ[6] = var4.gameVQ[1] + (gameHK << 2) + 10;
        var4.gameVQ[7] = var4.gameVQ[1] + (gameHK << 2) + 10;
        var4.gameVQ[8] = var4.gameVQ[1] + (gameHK << 2) + 10;
    }

    public static Char gameAE(int var0) {
        for (int var1 = 0; var1 < vCharInMap.size(); ++var1) {
            Char var2;
            if ((var2 = (Char) vCharInMap.elementAt(var1)).charID == var0) {
                return var2;
            }
        }

        return null;
    }

    public static BuNhin gameAF(int var0) {
        return vBuNhin.size() > 0 ? (BuNhin) vBuNhin.elementAt(var0) : null;
    }

    public final void gameAA(String var1, String var2) {
        if (!isPaintMessage || GameCanvas.isTouch) {
            ChatTextField.gameAA().isShow = false;
        }

        gamePG = 5;
        if (!var1.equals("")) {
            if (var2.equals(mResources.gameTI[0])) {
                if (!Code.fieldAA.fieldAF(var1)) {
                    Service.gI().chat(var1);
                    return;
                }
            } else if (var2.equals(mResources.gameTJ[0])) {
                if (vParty.size() == 0) {
                    ChatManager.gameAD().gameAE().gameAA(mResources.gameRF);
                } else {
                    Service.gI().chatParty(var1);
                }
            } else if (var2.equals(mResources.gameTK[0])) {
                Service.gI().chatGlobal(var1);
            } else if (var2.equals(mResources.gameTL[0])) {
                if (Char.getMyChar().cClanName.equals("")) {
                    ChatManager.gameAD().gameAE().gameAA(mResources.gameRK);
                } else {
                    Service.gI().chatClan(var1);
                }
            } else {
                ChatManager.gameAD().gameAA(var2, Char.getMyChar().cName, var1);
                Service.gI().chatPrivate(var2, var1);
            }
        }
    }

    public final void gameBK() {
        if (isPaintMessage) {
            this.gameBH();
            isPaintMessage = false;
            ChatTextField.gameAA().center = null;
        }

    }

    private void gameAA(int var1, int var2, int var3, int var4, int var5) {
        if (gameKZ || gameMI || gameMJ || gameMK || gameMB || isPaintTrade || gameKW || gameKX || gameDN || gameML || gameMR || gameMM || gameMN || gameMO) {
            int var6 = var3 * gameHK;
            var4 *= gameHK;
            scrMain.gameAA();
            if (GameCanvas.gameAB(var1, var2, var6, var4)) {
                gameHL = var5;
                if (GameCanvas.isPointerClick) {
                    if ((var1 = (GameCanvas.pxLast - var1) / gameHK + (GameCanvas.pyLast - var2) / gameHK * var3) / gameTJ < gameTK) {
                        indexSelect = var1;
                    }

                    super.left = super.center = null;
                    if (isPaintTrade) {
                        if (indexSelect < 0) {
                            indexSelect = 11;
                        }

                        if (indexSelect > 11) {
                            indexSelect = 11;
                        }
                    }

                    this.gameBC();
                }
            }
        }

    }

    public final void gameAB(int var1, Object var2) {
        String var3 = null;
        var3 = null;
        MyVector var4 = null;
        Member var9;
        Item var10;
        Npc var12;
        ChatTab var14;
        Object[] var15;
        int var16;
        Skill var17;
        Item var20;
        switch (var1) {
            case 1:
                GameCanvas.gameAJ();
                return;
            case 2:
                GameCanvas.gameAJ();
                super.left = super.center = null;
                this.gameBC();
                return;
            case 3:
                this.gameBH();
                return;
            case 222:
                this.gameKK();
                return;
            case 333:
                this.gameAD((int) 47);
                return;
            case 334:
                this.gameFJ();
                return;
            case 335:
                gameHQ = false;
                this.gameAA((int) 3, (Item) itemSplit);
                return;
            case 336:
                gameHQ = false;
                this.gameAA((int) 3, (Item) itemUpGrade);
                return;
            case 337:
                this.gameKL();
                return;
            case 338:
                this.gameAO(0);
                return;
            case 339:
                this.gameAP(0);
                return;
            case 340:
                this.gameAP(1);
                return;
            case 341:
                this.gameKM();
                return;
            case 342:
                gameKN();
                return;
            case 343:
                this.gameAP(2);
                return;
            case 344:
                this.gameAO(1);
                return;
            case 345:
                this.gameAO(2);
                return;
            case 400:
                this.gameKP();
                return;
            case 401:
                this.gameKQ();
                return;
            case 402:
                this.gameKR();
                return;
            case 403:
                gameKS();
                return;
            case 405:
                gameKO();
                return;
            case 500:
                this.gameKT();
                return;
            case 501:
                Service.gI().itemMonToBag(indexSelect);
                return;
            case 502:
                this.gameBX();
                return;
            case 503:
                this.gameAA((int) 51, (Item) currentCharViewInfo.arrItemViThu[indexSelect]);
                return;
            case 504:
                gameKU();
                return;
            case 505:
                gameKX();
                return;
            case 508:
                Service.gI().doRemoveVithu(indexSelect);
                return;
            case 999:
                this.gameAD((int) 35);
                return;
            case 1000:
                Service.gI().rewardPB();
                this.resetButton();
                return;
            case 1500:
                (var4 = new MyVector()).addElement(new Command(mResources.gameCJ, 15001));
                if (Char.getMyChar().xu >= 5000) {
                    var4.addElement(new Command(mResources.gameCO, 15002));
                }

                GameCanvas.menu.gameAA(var4);
                return;
            case 1501:
                this.gameAA((int) 3, (Item) itemSell);
                return;
            case 1502:
                this.gameND.update();
                return;
            case 1503:
                gameED();
                return;
            case 1504:
                gameEB();
                return;
            case 1505:
                this.gameAA((int) 3, (Item) arrItemStands[indexSelect].item);
                return;
            case 1506:
                if (arrItemSprin != null) {
                    this.yenTemp = 0;
                    gameDQ = false;
                    indexCard = -1;
                    arrItemSprin = null;
                    gI().left = new Command(mResources.gameEO, 1506);
                    return;
                }

                indexCard = indexSelect;
                Service.gI().selectCard();
                GameCanvas.gameAL();
                return;
            case 1507:
                this.gameDY();
                return;
            case 1508:
                var4 = new MyVector();
                if ((var20 = Char.clan.items[indexSelect]) != null) {
                    if (var20.template.id == 281) {
                        var4.addElement(new Command(mResources.gameCC, 15081));
                    } else {
                        var4.addElement(new Command(mResources.gameRL, 15082));
                    }

                    GameCanvas.menu.gameAA(var4);
                    return;
                }
                break;
            case 1509:
                if (indexSelect >= 0 && Char.clan != null) {
                    this.gameAA((int) 39, (Item) Char.clan.items[indexSelect]);
                    return;
                }

                gameDQ = false;
                return;
            case 1510:
                gameDX();
                return;
            case 1511:
                var3 = GameCanvas.inputDlg.tfInput.getText();
                GameCanvas.gameAJ();

                try {
                    if (var3.equals("")) {
                        GameCanvas.setText(mResources.gameUZ);
                        return;
                    }

                    if ((var16 = Integer.valueOf(var3).intValue()) >= 10 && var16 <= 90) {
                        Char.aHpValue = var16;
                        return;
                    }

                    GameCanvas.setText(mResources.gameUZ);
                    return;
                } catch (Exception var8) {
                    GameCanvas.setText(mResources.gameUZ);
                    return;
                }
            case 1512:
                var3 = GameCanvas.inputDlg.tfInput.getText();
                GameCanvas.gameAJ();

                try {
                    if (var3.equals("")) {
                        GameCanvas.setText(mResources.gameUZ);
                        return;
                    }

                    if ((var16 = Integer.valueOf(var3).intValue()) >= 10 && var16 <= 90) {
                        Char.aMpValue = var16;
                        return;
                    }

                    GameCanvas.setText(mResources.gameUZ);
                    return;
                } catch (Exception var7) {
                    GameCanvas.setText(mResources.gameUZ);
                    return;
                }
            case 1515:
                this.gameAA((int) 41, (Item) currentCharViewInfo.arrItemMounts[indexSelect]);
                return;
            case 1516:
                Service.gI().itemMonToBag(indexSelect);
                return;
            case 1600:
                gameDR();
                return;
            case 1601:
                gameFF();
                return;
            case 1602:
                var20 = gameAK(43);
                this.gameAA((int) 3, (Item) var20);
                return;
            case 1603:
                this.gameFC();
                return;
            case 1604:
                var4 = new MyVector();
                if (arrItemSplit[indexSelect] != null) {
                    var4.addElement(new Command(mResources.gameCJ, 1605));
                }

                var4.addElement(new Command(mResources.gameFV, 11105));
                GameCanvas.menu.gameAA(var4);
                return;
            case 1605:
                this.gameDW();
                return;
            case 1606:
                this.gameDV();
                return;
            case 1700:
                this.gameKG();
                return;
            case 1701:
                gameKH();
                return;
            case 1702:
                this.gameKI();
                return;
            case 2000:
                Service.gI().rewardCT();
                this.resetButton();
                return;
            case 2001:
                Service.gI().throwItem(indexSelect);
                return;
            case 2002:
                GameCanvas.gameAJ();
                return;
            case 2003:
                if (gameRJ == 0) {
                    gameRJ = 16;
                    this.gameHG.caption = mResources.gameXI;
                    return;
                }

                gameRJ = 0;
                this.gameHG.caption = mResources.gameXJ;
                if (!GameCanvas.isTouch) {
                    super.left = new Command(mResources.gameEO, 11082);
                    return;
                }
                break;
            case 2004:
                return;
            case 5021:
                this.gameBY();
                return;
            case 5022:
                this.gameBZ();
                return;
            case 5041:
                Service.gI().upPotential(gameHL - 1, 1);
                this.gameBJ();
                return;
            case 5042:
                this.gameKV();
                return;
            case 5043:
                gameKW();
                return;
            case 5051:
                Service.gI().upSkill(Char.getMyChar().nClass.skillTemplates[indexSelect].id, 1);
                this.gameBJ();
                return;
            case 5052:
                this.gameKY();
                return;
            case 5053:
                gameKZ();
                break;
            case 9999:
                this.gameAD((int) 37);
                return;
            case 11000:
                gameCH();
                return;
            case 11001:
                Char.getMyChar().gameAU();
                return;
            case 11002:
                gameKE();
                return;
            case 11003:
                gameKD();
                return;
            case 11004:
                this.gameAD((byte) 25);
                return;
            case 11005:
                gameKB();
                return;
            case 11006:
                gameKC();
                return;
            case 11007:
                this.gameAD((byte) 24);
                return;
            case 11008:
                gameKA();
                return;
            case 11009:
                this.gameAD((byte) 23);
                return;
            case 11010:
                gameHZ();
                return;
            case 11011:
                this.gameAD((byte) 22);
                return;
            case 11012:
                gameHY();
                return;
            case 11013:
                this.gameAD((byte) 21);
                return;
            case 11014:
                gameHX();
                return;
            case 11015:
                this.gameAD((byte) 20);
                return;
            case 11016:
                gameHW();
                return;
            case 11017:
                this.gameAD((byte) 15);
                return;
            case 11018:
                gameHU();
                return;
            case 11019:
                this.gameAD((byte) 14);
                return;
            case 11020:
                gameHT();
                return;
            case 11021:
                svTitle = "";
                svAction = "";
                this.resetButton();
                return;
            case 11022:
                this.gameHR();
                return;
            case 11023:
                this.gameHQ();
                return;
            case 11024:
                gameHP();
                return;
            case 11025:
                this.gameCA();
                return;
            case 11026:
                gameHN();
                return;
            case 11027:
                this.gameHM();
                return;
            case 11028:
                this.gameHL();
                return;
            case 11029:
                this.gameHK();
                return;
            case 11030:
                this.gameHJ();
                return;
            case 11032:
                this.gameHI();
                return;
            case 11033:
                this.gameHH();
                return;
            case 11034:
                this.gameHF();
                return;
            case 11035:
                this.gameHE();
                return;
            case 11036:
                gameDL();
                return;
            case 11037:
                this.gameHD();
                return;
            case 11038:
                gameHB();
                return;
            case 11040:
                this.gameGY();
                return;
            case 11041:
                this.gameGX();
                return;
            case 11042:
                gameGU();
                return;
            case 11043:
                gameGS();
                return;
            case 11044:
                gameEJ();
                return;
            case 11045:
                gameEP();
                return;
            case 11046:
                gameEO();
                return;
            case 11047:
                gameEL();
                return;
            case 11048:
                gameGO();
                return;
            case 11049:
                gameDK();
                return;
            case 11050:
                gameDM();
                return;
            case 11051:
                gameGN();
                return;
            case 11052:
                var10 = (Item) var2;
                Service.gI().useItemChangeMap(var10.indexUI, GameCanvas.menu.menuSelectedItem);
                return;
            case 11053:
                gameAG(var10 = (Item) var2);
                return;
            case 11054:
                this.gameGH();
                return;
            case 11055:
                gameAF(var10 = (Item) var2);
                return;
            case 11057:
                Npc var22 = (Npc) var2;
                Service.gI().getTask(var22.template.npcTemplateId, GameCanvas.menu.menuSelectedItem, -1);
                return;
            case 11058:
                var10 = (Item) var2;
                GameCanvas.gameAJ();
                Service.gI().saleItem(var10.indexUI, Integer.parseInt(GameCanvas.inputDlg.tfInput.getText()));
                return;
            case 11059:
                this.gameGG();
                return;
            case 11060:
                this.gameGF();
                return;
            case 11061:
                gameAD(var10 = (Item) var2);
                return;
            case 11062:
                gameDQ();
                return;
            case 11063:
                gameGE();
                return;
            case 11064:
                Service.gI().upPotential(gameHL - 1, 1);
                this.gameBJ();
                return;
            case 11065:
                this.gameCB();
                return;
            case 11066:
                this.gameBH();
                isPaintMessage = false;
                gameDM = false;
                ChatTextField.gameAA().center = null;
                return;
            case 11067:
                if (TileMap.zoneID != indexSelect) {
                    Service.gI().requestChangeZone(indexSelect, this.gameMZ);
                    return;
                }

                InfoMe.gameAA(mResources.gameSC);
                return;
            case 11068:
                var3 = (String) var2;
                this.gameAF(var3);
                return;
            case 11069:
                this.gameGD();
                return;
            case 11070:
                Party var21;
                gameAA(var21 = (Party) var2);
                return;
            case 11071:
                Service.gI().outParty();
                return;
            case 11072:
                gameGC();
                return;
            case 11073:
                gameAB(Char.getMyChar().arrItemBag[indexSelect]);
                return;
            case 11074:
                MyVector var18;
                short var11 = Short.parseShort(String.valueOf((var18 = (MyVector) var2).elementAt(0)));
                String var19 = String.valueOf(var18.elementAt(1));
                this.gameAA(var11, var19);
                return;
            case 11075:
                this.gameGB();
                return;
            case 11076:
                var3 = (String) var2;
                Service.gI().addParty(var3);
                return;
            case 11077:
                gameAE(var3 = (String) var2);
                return;
            case 11078:
                gameGA();
                return;
            case 11079:
                gameFY();
                return;
            case 11080:
                gameAC(var3 = (String) var2);
                return;
            case 11081:
                gameFW();
                return;
            case 11082:
                gameFV();
                return;
            case 11083:
                this.gameAD((byte) 5);
                return;
            case 11084:
                gameFU();
                return;
            case 11085:
                gameFT();
                return;
            case 11086:
                this.gameFS();
                return;
            case 11087:
                var10 = (Item) var2;
                GameCanvas.gameAJ();
                Service.gI().splitItem(var10);
                return;
            case 11088:
                this.gameAD((byte) 26);
                return;
            case 11089:
                this.gameAD((byte) 27);
                return;
            case 11090:
                this.gameAD((byte) 28);
                return;
            case 11091:
                this.gameAD((byte) 29);
                return;
            case 11092:
                gameAC(var10 = (Item) var2);
                return;
            case 11093:
                this.gameAD((byte) 2);
                return;
            case 11094:
                this.gameAD((byte) 6);
                return;
            case 11095:
                this.gameAD((byte) 7);
                return;
            case 11096:
                this.gameAD((byte) 8);
                return;
            case 11097:
                this.gameAD((byte) 9);
                return;
            case 11098:
                this.gameFQ();
                return;
            case 11099:
                gameHQ = false;
                this.gameAA((int) 3, (Item) itemUpGrade);
                return;
            case 11100:
                gameFO();
                return;
            case 11101:
                var20 = gameAK(10);
                this.gameAA((int) 3, (Item) var20);
                return;
            case 11102:
                this.gameFJ();
                return;
            case 11103:
                this.gameFI();
                return;
            case 11104:
                this.gameAA((int) 3, (Item) ((Item) var2));
                return;
            case 11105:
                gameDT();
                return;
            case 11106:
                this.gameFH();
                return;
            case 11107:
                gameFG();
                return;
            case 11108:
                this.gameAD((byte) 3);
                return;
            case 11109:
                this.gameFD();
                return;
            case 11110:
                var20 = arrItemTradeOrder[indexSelect];
                this.gameAA((int) 30, (Item) var20);
                return;
            case 11111:
                var20 = gameAK(4);
                this.gameAA((int) 4, (Item) var20);
                return;
            case 11112:
                Service.gI().boxSort();
                return;
            case 11113:
                Service.gI().itemBagToBox(Char.getMyChar().arrItemBag[indexSelect].indexUI);
                return;
            case 11114:
                this.gameAA((int) 3, (Item) Char.getMyChar().arrItemBag[indexSelect]);
                return;
            case 11115:
                gameFB();
                return;
            case 11116:
                gameFA();
                return;
            case 11120:
                var17 = (Skill) (var15 = (Object[]) var2)[0];
                var16 = Integer.parseInt((String) var15[1]);
                gameNZ[var16] = var17;
                gameBP();
                return;
            case 11121:
                var17 = (Skill) (var15 = (Object[]) var2)[0];
                var16 = Integer.parseInt((String) var15[1]);
                gameNY[var16] = var17;
                gameBQ();
                return;
            case 12000:
                this.gameGM();
                return;
            case 12001:
                ChatManager.gameAD().gameAA(((Integer) var2).intValue());
                this.gameGK();
                return;
            case 12002:
            case 12004:
                var3 = (String) var2;
                if ((var14 = ChatManager.gameAD().gameAA(var3)) == null) {
                    ChatManager.gameAD().gameAB(var3);
                    ChatManager.gameAD().gameAC();
                } else {
                    ChatManager.gameAD().gameAA(var14);
                }

                this.gameGK();
                gameHX = false;
                gameME = false;
                gameHZ = false;
                isPaintFriend = false;
                gameHW = false;
                ChatTextField.gameAA().center = null;
                return;
            case 12003:
                this.gameCM();
                return;
            case 12005:
                this.gameGL();
                return;
            case 12006:
                gameGI();
                return;
            case 12007:
                this.gameUC = 1;
                this.gameUD = "";
                this.gameEG();
                return;
            case 12008:
                gameEF();
                return;
            case 12009:
                var3 = (String) var2;
                MyVector var13;
                (var13 = new MyVector()).addElement(new Command(mResources.gameSB[7], 12002, var3));
                var13.addElement(new Command(mResources.gameSX[2], 110803, var3));
                if (gameHW) {
                    var13.addElement(new Command(mResources.gameSB[6], 110804));
                }

                if (isPaintMessage) {
                    var13.addElement(new Command(mResources.gameBC, 14020, var3));
                    var13.addElement(new Command(mResources.gameSB[6], 1108041, var3));
                }

                GameCanvas.menu.gameAA(var13);
                return;
            case 13001:
                this.gameAD((byte) 32);
                return;
            case 13002:
                gameHV();
                return;
            case 14001:
                gameFX();
                return;
            case 14002:
                this.gameEW();
                return;
            case 14003:
                this.gameEX();
                return;
            case 14004:
                gameEN();
                return;
            case 14005:
                this.gameES();
                return;
            case 14006:
                gameAB(var3 = (String) var2);
                return;
            case 14007:
                gameEH();
                return;
            case 14008:
                GameCanvas.gameAA(mResources.gamePG, new Command(mResources.gameCH, 140081), new Command(mResources.gameCU, 1));
                return;
            case 14009:
                gameEQ();
                return;
            case 14010:
                GameCanvas.inputDlg.gameAA(mResources.gameUI, new Command(mResources.gameEC, GameCanvas.instance, 88833, (Object) null), 1);
                return;
            case 14011:
                gameDM = false;
                return;
            case 14012:
                this.gameFK();
                return;
            case 14013:
                this.gameFP();
                return;
            case 14014:
                this.gameHG();
                return;
            case 14015:
                this.gameFM();
                return;
            case 14016:
                this.gameAA((int) 3, (Item) arrItemConvert[indexSelect]);
                return;
            case 14017:
                gameEI();
                return;
            case 14018:
                this.gameAD((byte) 34);
                return;
            case 14019:
                gameEE();
                return;
            case 14020:
                if (gameET()) {
                    var3 = (String) var2;
                    Service.gI().textBoxId((short) 1, var3);
                    return;
                }

                GameCanvas.setText(mResources.gameTV);
                return;
            case 14021:
                gameFZ();
                return;
            case 14022:
                gameHS();
                return;
            case 14023:
                this.gameAD((byte) 35);
                return;
            case 14024:
                this.gameAA((byte) 1);
                return;
            case 14025:
                this.gameAA((byte) 0);
                return;
            case 14026:
                handleMoveAction();
                return;
            case 15001:
                this.gameEC();
                return;
            case 15002:
                try {
                    if ((var1 = Integer.parseInt(this.gameND.getText())) <= 0) {
                        GameCanvas.setText(mResources.gameUZ);
                    }

                    GameCanvas.gameAA(mResources.gameAA(mResources.gameVH, NinjaUtil.gameAA(String.valueOf(var1))), new Command(mResources.gameCH, 150021), new Command(mResources.gameCU, 1));
                    return;
                } catch (Exception var6) {
                    GameCanvas.setText(mResources.gameUZ);
                    return;
                }
            case 15041:
                gameEA();
                return;
            case 15042:
                GameCanvas.gameAA(mResources.gameAA(mResources.gameVI, NinjaUtil.gameAA(String.valueOf(arrItemStands[indexSelect].price))), new Command(mResources.gameCH, 150421), new Command(mResources.gameCU, 1));
                return;
            case 15081:
                Service.gI().useClanItem();
                return;
            case 15082:
                GameCanvas.inputDlg.gameAA(mResources.gameVX, new Command(mResources.gameCX, GameCanvas.instance, 88843, new Integer(indexSelect)), 0);
                return;
            case 15130:
                Char.aFoodValue = 1;
                return;
            case 15131:
                Char.aFoodValue = 10;
                return;
            case 15132:
                Char.aFoodValue = 20;
                return;
            case 15133:
                Char.aFoodValue = 30;
                return;
            case 15134:
                Char.aFoodValue = 40;
                return;
            case 15135:
                Char.aFoodValue = 50;
                return;
            case 15136:
                Char.aFoodValue = 60;
                return;
            case 15137:
                Char.aFoodValue = 70;
                return;
            case 15140:
                Char.fieldFU = 1;
                return;
            case 15141:
                Char.fieldFU = 10;
                return;
            case 15142:
                Char.fieldFU = 20;
                return;
            case 15143:
                Char.fieldFU = 30;
                return;
            case 15144:
                Char.fieldFU = 40;
                return;
            case 15145:
                Char.fieldFU = 50;
                return;
            case 15146:
                Char.fieldFU = 60;
                return;
            case 15147:
                Char.fieldFU = 70;
                return;
            case 15150:
                Char.fieldFR = 1;
                return;
            case 15151:
                Char.fieldFR = 10;
                return;
            case 15153:
                Char.fieldFR = 30;
                return;
            case 15155:
                Char.fieldFR = 50;
                return;
            case 15157:
                Char.fieldFR = 70;
                return;
            case 15161:
                Char.fieldFS = 1;
                return;
            case 15162:
                Char.fieldFS = 2;
                return;
            case 15163:
                Char.fieldFS = 3;
                return;
            case 15164:
                Char.fieldFS = 4;
                return;
            case 15165:
                Char.fieldFS = 5;
                return;
            case 15166:
                Char.fieldFS = 6;
                return;
            case 15167:
                Char.fieldFS = 7;
                return;
            case 15174:
                Char.fieldFT = 4;
                return;
            case 15175:
                Char.fieldFT = 5;
                return;
            case 15176:
                Char.fieldFT = 6;
                return;
            case 15177:
                Char.fieldFT = 7;
                return;
            case 15178:
                Char.fieldFT = 8;
                return;
            case 15179:
                Char.fieldFT = 9;
                return;
            case 110001:
                this.gameCF();
                return;
            case 110002:
                gameCO();
                return;
            case 110003:
                gameCG();
                return;
            case 110004:
                gameHA();
                return;
            case 110005:
                this.gameAD((int) 14);
                return;
            case 110006:
                gameGW();
                return;
            case 110007:
                this.gameAD((int) 7);
                return;
            case 110008:
                this.gameAD((int) 6);
                return;
            case 110009:
                this.gameAD((int) 9);
                return;
            case 110010:
                this.gameAD((int) 8);
                return;
            case 110011:
                this.gameAD((int) 10);
                return;
            case 110012:
                this.gameAD((int) 11);
                return;
            case 110013:
                this.gameAD((int) 12);
                return;
            case 110014:
                Npc npc1;
                if ((npc1 = fieldAI(5)) != null && (Math.abs(npc1.cx - Char.getMyChar().cx) > 22 || Math.abs(npc1.cy - Char.getMyChar().cy) > 22)) {
                    Char.fieldAC(npc1.cx, npc1.cy);
                }
                this.gameAD((int) 4);
                return;
            case 110015:
                this.gameAD((int) 13);
                return;
            case 110016:
                Npc npc5;
                if ((npc5 = fieldAI(13)) != null && npc5.statusMe != 15) {
                    if (Math.abs(npc5.cx - Char.getMyChar().cx) > 22 || Math.abs(npc5.cy - Char.getMyChar().cy) > 22) {
                        Char.fieldAC(npc5.cx, npc5.cy);
                    }

                    Service.gI().openUIZone();
                    return;
                }

                if ((var1 = Char.fieldAI(37)) < 0) {
                    var1 = Char.fieldAI(35);
                }

                if (var1 >= 0) {
                    this.gameMZ = var1;
                    Service.gI().openUIZone();
                }
                return;
            case 110017:
                this.gameAU();
                return;
            case 110018:
                gameGJ();
                return;
            case 110019:
                this.gameEY();
                return;
            case 110020:
                Code.fieldAP();
                return;
            case 110021:
                fieldCE();
                return;
            case 110051:
                gameAB((byte) 25);
                return;
            case 110052:
                this.gameAC((byte) 25);
                return;
            case 110081:
                gameAB((byte) 24);
                return;
            case 110082:
                this.gameAC((byte) 24);
                return;
            case 110101:
                gameAB((byte) 23);
                return;
            case 110102:
                this.gameAC((byte) 23);
                return;
            case 110121:
                gameAB((byte) 22);
                return;
            case 110122:
                this.gameAC((byte) 22);
                return;
            case 110141:
                gameAB((byte) 21);
                return;
            case 110142:
                this.gameAC((byte) 21);
                return;
            case 110161:
                gameAB((byte) 20);
                return;
            case 110162:
                this.gameAC((byte) 20);
                return;
            case 110181:
                gameAB((byte) 15);
                return;
            case 110182:
                this.gameAC((byte) 15);
                return;
            case 110201:
                gameAB((byte) 14);
                return;
            case 110202:
                this.gameAC((byte) 14);
                return;
            case 110221:
                gameHC();
                return;
            case 110244:
                gameHO();
                return;
            case 110261:
                fieldHO();
                return;
            case 110262:
                Item item1;

                if ((item1 = gameAK(3)) != null) {
                    Code.fieldAB((int) item1.template.id);
                }

                return;
            case 110263:
                if ((item1 = gameAK(3)) != null) {
                    Code.fieldAC((int) item1.template.id);
                }

                return;
            case 110264:
                if ((item1 = gameAK(3)) != null) {
                    Code.fieldAB(item1);
                }

                return;
            case 110265:
                if ((item1 = gameAK(3)) != null) {
                    Code.fieldAC(item1);
                }

                return;
            case 110361:
                this.gameGT();
                return;
            case 110382:
                Service.gI().returnTownFromDead();
                return;
            case 110383:
                Service.gI().wakeUpFromDead();
                return;
            case 110391:
                this.gameAN(1);
                return;
            case 110392:
                this.gameAN(2);
                return;
            case 110393:
                this.gameAN(3);
                return;
            case 110394:
                this.gameAN(4);
                return;
            case 110395:
                this.gameAN(5);
                return;
            case 110396:
                this.gameAN(6);
                return;
            case 110397:
                this.gameAN(7);
                return;
            case 110398:
                this.gameAN(8);
                return;
            case 110399:
                this.gameAN(9);
                return;
            case 110441:
                this.gameCD();
                return;
            case 110451:
                gameGR();
                return;
            case 110452:
                gameGQ();
                return;
            case 110471:
                gameGP();
                return;
            case 110531:
                var10 = (Item) var2;
                Service.gI().useItemChangeMap(var10.indexUI, GameCanvas.menu.menuSelectedItem + 3);
                return;
            case 110561:
                GameCanvas.gameAJ();
                this.gameBC();
                return;
            case 110562:
                gameAE(var10 = (Item) var2);
                return;
            case 110701:
                Service.gI().outParty();
                return;
            case 110702:
                Service.gI().lockParty(true);
                return;
            case 110703:
                Service.gI().lockParty(false);
                return;
            case 110721:
                Service.gI().saleItem(indexSelect, 1);
                return;
            case 110722:
                gameAB(Char.getMyChar().arrItemBag[indexSelect]);
                return;
            case 110723:
                Service.gI().saleItem(indexSelect, Char.getMyChar().arrItemBag[indexSelect].quantity);
                return;
            case 110771:
                var3 = (String) var2;
                GameCanvas.gameAJ();
                Service.gI().removeFriend(var3);
                return;
            case 110791:
                var3 = (String) var2;
                Service.gI().addParty(var3);
                return;
            case 110792:
                gameAD(var3 = (String) var2);
                return;
            case 110801:
                Service.gI().moveMember(indexRow);
                return;
            case 110802:
                Service.gI().changeTeamLeader(indexRow);
                return;
            case 110803:
                var3 = (String) var2;
                Service.gI().addFriend(var3);
                return;
            case 110804:
                gameEZ();
                return;
            case 110805:
                this.gameER();
                return;
            case 110811:
                gameAB(Char.getMyChar().nClass.skillTemplates[indexSelect]);
                return;
            case 110812:
                gameAA(Char.getMyChar().nClass.skillTemplates[indexSelect]);
                return;
            case 110821:
                Service.gI().itemBodyToBag(indexSelect + gameRJ);
                return;
            case 110841:
                Service.gI().upPotential(gameHL - 1, 1);
                this.gameBJ();
                return;
            case 110842:
                this.gameCB();
                return;
            case 110851:
                var10 = (Item) var2;
                Service.gI().buyItem(var10.typeUI, var10.indexUI, 1);
                return;
            case 110852:
                gameAA(var10 = (Item) var2);
                return;
            case 110854:
                gameFR();
                return;
            case 110921:
                var10 = (Item) var2;
                Service.gI().buyItem(var10.typeUI, var10.indexUI, 1);
                return;
            case 110922:
                gameAA(var10 = (Item) var2);
                return;
            case 110923:
                this.gameAD((byte) 16);
                return;
            case 110924:
                this.gameAD((byte) 17);
                return;
            case 110925:
                this.gameAD((byte) 18);
                return;
            case 110926:
                this.gameAD((byte) 19);
                return;
            case 110981:
                this.gameDS();
                return;
            case 110991:
                gameHQ = true;
                this.gameAA((int) 3, (Item) itemUpGrade);
                return;
            case 111001:
                this.gameFN();
                return;
            case 111031:
                if (gameMJ) {
                    Service.gI().tinhluyen(itemSplit, arrItemSplit);
                    return;
                }

                if (gameMK) {
                    Service.gI().dichchuyen(itemSplit, arrItemSplit);
                    return;
                }

                if (gameML) {
                    Service.gI().ngockham((byte) 1, (Item) null, itemSplit, arrItemSplit);
                    return;
                }

                if (gameMN) {
                    Service.gI().ngockham((byte) 2, (Item) null, itemSplit, (Item[]) null);
                    return;
                }

                if (gameMO) {
                    Service.gI().ngockham((byte) 3, (Item) null, itemSplit, (Item[]) null);
                    return;
                }
                break;
            case 111071:
                this.gameFE();
                return;
            case 111101:
                var20 = gameAK(4);
                Service.gI().itemBoxToBag(var20.indexUI);
                return;
            case 120051:
                var14 = (ChatTab) var2;
                ChatManager.gameAD().chatTabs.removeElement(var14);
                if (ChatManager.gameAD().currentTabIndex > ChatManager.gameAD().chatTabs.size() - 1) {
                    ChatManager.gameAD().gameAB();
                }

                if (ChatManager.gameAD().gameAE() != null) {
                    this.gameGK();
                    return;
                }

                ChatTextField.gameAA().isShow = false;
                this.resetButton();
                return;
            case 120061:
                ChatManager.blockGlobalChat = !ChatManager.blockGlobalChat;
                GameCanvas.setText(mResources.gameTM + (ChatManager.blockGlobalChat ? mResources.gameBQ : mResources.gameBR));
                return;
            case 120062:
                ChatManager.blockPrivateChat = !ChatManager.blockPrivateChat;
                GameCanvas.setText(mResources.gameTN + (ChatManager.blockPrivateChat ? mResources.gameBQ : mResources.gameBR));
                return;
            case 120071:
                this.gameUC = 2;
                if (GameCanvas.input2Dlg.tfInput.getText().equals("")) {
                    GameCanvas.setText(mResources.gameNR);
                    return;
                }

                if (GameCanvas.input2Dlg.tfInput2.getText().equals("")) {
                    GameCanvas.setText(mResources.gameNS);
                    return;
                }

                this.gameUD = "Loại thẻ: " + GameCanvas.input2Dlg.tfInput.getText();
                this.gameUD = this.gameUD + ", Mệnh giá: " + GameCanvas.input2Dlg.tfInput2.getText();
                GameCanvas.gameAJ();
                this.gameEG();
                return;
            case 120072:
                if (GameCanvas.input2Dlg.tfInput.getText().equals("")) {
                    GameCanvas.setText(mResources.gameNT);
                    return;
                }

                if (GameCanvas.input2Dlg.tfInput2.getText().equals("")) {
                    GameCanvas.setText(mResources.gameNU);
                    return;
                }

                this.gameUD = this.gameUD + ", Số seri: " + GameCanvas.input2Dlg.tfInput.getText();
                this.gameUD = this.gameUD + ", Khoảng thời gian nạp: " + GameCanvas.input2Dlg.tfInput2.getText();
                Service.gI().adminChat(this.gameUD);
                GameCanvas.gameAJ();
                return;
            case 120081:
                GameCanvas.inputDlg.tfInput.setMaxTextLenght(11);
                GameCanvas.inputDlg.gameAA(mResources.gameTY, new Command("OK", (IActionListener) null, 120082, (Object) null), 1);
                return;
            case 120082:
                if ((var3 = GameCanvas.inputDlg.tfInput.getText()).equals("")) {
                    GameCanvas.setText(mResources.gameTZ);
                    return;
                }

                Service.gI().adminChat("Số điện thoại đăng ký: " + var3);
                GameCanvas.gameAJ();
                return;
            case 130011:
                var12 = (Npc) var2;
                Service.gI().getTask(var12.template.npcTemplateId, 0, -1);
                var12.chatPopup = null;
                this.resetButton();
                return;
            case 130012:
                (var12 = (Npc) var2).chatPopup = null;
                this.resetButton();
                return;
            case 130021:
                gameAB((byte) 32);
                return;
            case 130022:
                this.gameAC((byte) 32);
                return;
            case 140011:
                Service.gI().upSkill(Char.getMyChar().nClass.skillTemplates[indexSelect].id, 1);
                this.gameBJ();
                return;
            case 140012:
                this.gameCC();
                return;
            case 140041:
                GameCanvas.inputDlg.tfInput.setMaxTextLenght(180);
                GameCanvas.inputDlg.gameAA(mResources.gameUG, new Command(mResources.gameEC, GameCanvas.instance, 88832, (Object) null), 0);
                return;
            case 140042:
                Service.gI().clanUpLevel();
                return;
            case 140043:
                GameCanvas.inputDlg.gameAA(mResources.gameUH, new Command(mResources.gameEC, GameCanvas.instance, 88834, (Object) null), 0);
                return;
            case 140044:
                Service.gI().unlockClanItem();
                return;
            case 140071:
                indexRow = 0;
                indexSelect = 0;
                scrMain.gameAA();
                gameHR = !gameHR;
                gameAY();
                return;
            case 140072:
                indexRow = 0;
                indexSelect = 0;
                scrMain.gameAA();
                gameHS = !gameHS;
                gameAY();
                return;
            case 140081:
                Service.gI().outClan();
                GameCanvas.gameAJ();
                return;
            case 140091:
                Service.gI().changeClanType(((Member) vClan.elementAt(indexRow)).name, 3);
                return;
            case 140092:
                Service.gI().changeClanType(((Member) vClan.elementAt(indexRow)).name, 2);
                return;
            case 140093:
                GameCanvas.gameAA(mResources.gamePE, new Command(mResources.gameCH, 1400931), new Command(mResources.gameCU, 1));
                return;
            case 140094:
                GameCanvas.gameAA(mResources.gamePF, new Command(mResources.gameCH, 1400941), new Command(mResources.gameCU, 1));
                return;
            case 140095:
                var9 = (Member) vClan.elementAt(indexRow);
                Service.gI().inviteClanDun(var9.name);
                return;
            case 140096:
                (var4 = new MyVector()).addElement(new Command(mResources.gameVU, 1400961));
                var4.addElement(new Command(mResources.gameVV, 1400962));
                GameCanvas.menu.gameAA(var4);
                return;
            case 140101:
                (var20 = new Item()).template = ItemTemplates.gameAA((short) 0);
                var20.expires = -1L;
                this.gameAA((int) 39, (Item) var20);
                return;
            case 140131:
                GameCanvas.gameAA(mResources.gameQS, new Command(mResources.gameCH, 140132), new Command(mResources.gameCU, 1));
                return;
            case 140132:
                gameEM();
                return;
            case 140151:
                this.gameFL();
                return;
            case 140161:
                this.gameAA((int) 3, (Item) arrItemConvert[2]);
                return;
            case 140191:
                gameAB((byte) 34);
                return;
            case 140192:
                this.gameAC((byte) 34);
                return;
            case 140221:
                gameAB((byte) 35);
                return;
            case 140222:
                this.gameAC((byte) 35);
                return;
            case 150021:
                GameCanvas.gameAK();
                var1 = 0;

                try {
                    var1 = Integer.parseInt(this.gameND.getText());
                } catch (Exception var5) {
                }

                Service.gI().sendToSaleItem(itemSell, var1);
                return;
            case 150411:
                this.gameMX = 0;
                this.gameMY = 0;
                if (this.gameMW == 0) {
                    this.gameMW = 1;
                } else if (this.gameMW == 1) {
                    this.gameMW = 2;
                } else if (this.gameMW == 2) {
                    this.gameMW = 1;
                }

                this.gameDZ();
                return;
            case 150412:
                this.gameMX = 0;
                this.gameMW = 0;
                if (this.gameMY == 0) {
                    this.gameMY = 1;
                } else if (this.gameMY == 1) {
                    this.gameMY = 2;
                } else if (this.gameMY == 2) {
                    this.gameMY = 1;
                }

                this.gameDZ();
                return;
            case 150413:
                this.gameMW = 0;
                this.gameMY = 0;
                if (this.gameMX == 0) {
                    this.gameMX = 1;
                } else if (this.gameMX == 1) {
                    this.gameMX = 2;
                } else if (this.gameMX == 2) {
                    this.gameMX = 1;
                }

                this.gameDZ();
                return;
            case 150421:
                GameCanvas.gameAJ();
                Service.gI().buyItemAuction(arrItemStands[indexSelect].item.itemId);
                return;
            case 151301:
                Service.gI().sendCatkeo(Char.getMyChar().mobFocus.getTemplate().mobTemplateId);
                return;
            case 151710:
                Char.fieldFT = 10;
                return;
            case 151711:
                Char.fieldFT = 11;
                return;
            case 151712:
                Char.fieldFT = 12;
                return;
            case 909090:
                this.gameAD((int) 38);
                return;
            case 1100011:
                this.gameBR();
                return;
            case 1100012:
                this.gameBS();
                return;
            case 1100013:
                this.gameBT();
                return;
            case 1100014:
                this.gameBU();
                return;
            case 1100015:
                this.gameBV();
                return;
            case 1100016:
                this.gameBW();
                return;
            case 1100017:
                this.gameBX();
                return;
            case 1100032:
                this.gameCI();
                return;
            case 1100033:
                gameEV();
                return;
            case 1100034:
                gameKF();
                return;
            case 1100041:
                gameGZ();
                return;
            case 1100061:
                this.gameCJ();
                return;
            case 1100062:
                this.gameCL();
                return;
            case 1100063:
                this.gameCM();
                return;
            case 1100064:
                this.gameCN();
                return;
            case 1100065:
                gameGV();
                return;
            case 1100067:
                gameEK();
                return;
            case 1100068:
                this.gameAD((int) 40);
                return;

            case 1100069:
                MyVector myVector;

                (myVector = new MyVector()).addElement(new Command("Tàn sát all", 1100070, (Object) null));

                for (int ivar18 = 0; ivar18 < fieldTH.size(); ++ivar18) {
                    MobTemplate mobtem = (MobTemplate) fieldTH.elementAt(ivar18);
                    myVector.addElement(new Command(mobtem.name, 1100070, mobtem));
                }

                GameCanvas.menu.gameAA(myVector);
                return;
            case 1100070:
                MobTemplate mobtem;
                Code.fieldAA((mobtem = (MobTemplate) var2) != null ? mobtem.mobTemplateId : -1, TileMap.mapID);
                return;
            case 1100071:
                final MyVector bh = new MyVector();
                final MyVector ep10 = new MyVector();
                for (int m = 0; m < vNpc.size(); ++m) {
                    final Npc ev4 = (Npc) vNpc.elementAt(m);
                    if (!ep10.contains(ev4.template)) {
                        ep10.addElement(ev4.template);

                        bh.addElement(String.valueOf(m) + ". " + ev4.template.name);
                    }
                }
                InfoDlg.gameAB();
                isPaintAlert = true;
                this.gameNA = true;
                indexRow = 0;
                gameAB(175, 200);
                final Command t8 = null;
                super.left = t8;
                super.center = t8;
                super.right = new Command("Đóng", 3);
                super.left = new Command("Dịch chuyển", 1100072);
                this.gameNC = "Danh sách NPC";
                this.gameNB = bh;

                return;
            case 1100072:
                final int int2 = Integer.parseInt(((String) this.gameNB.elementAt(indexRow)).substring(0, ((String) this.gameNB.elementAt(indexRow)).indexOf(". ")));
                this.gameBH();
                final Npc ev5;
                if ((ev5 = (Npc) vNpc.elementAt(int2)) != null) {
                    if (Math.abs(ev5.cx - Char.getMyChar().cx) > 22) {
                        Char.fieldAC(ev5.cx, ev5.cy);
                    }
                    Service.gI().openMenu(ev5.template.npcTemplateId);
                }

                return;
            case 1100073:
                Code.fieldAG();
                return;
            case 1100074:
                Code.fieldAD();
                return;
            case 1100075:
                Code.fieldAE();
                return;
            case 1100076:
                super.right = this.gameSE;
                indexMenu = 0;
                fieldTF = true;
                gameAB(175, 200);
                return;
            case 1100077:
                Code.fieldAB(Code.fieldAK[indexSelect]);
                return;
            case 1100078:
                Code.fieldAN();
                return;
            case 1100079:
                Code.fieldAA(Char.getMyChar().arrItemBag[indexSelect].template.id);
                return;
            case 1100080:
                Code.fieldAQ = !Code.fieldAQ;
                return;
            case 1100081:
                GameCanvas.inputDlg.gameAA("KC Nhặt", new Command("Đặt", 1100085), 1);
                GameCanvas.inputDlg.tfInput.setText(String.valueOf(Code.fieldAM));
                return;
            case 1100082:
                GameCanvas.inputDlg.gameAA("KC Tàn sát", new Command("Đặt", 1100086), 1);
                GameCanvas.inputDlg.tfInput.setText(String.valueOf(Code.fieldAN));
                return;
            case 1100083:
                Code.fieldAR = !Code.fieldAR;
                return;
            case 1100084:
                Code.fieldAY = !Code.fieldAY;
                return;
            case 1100085:
                try {
                    Code.fieldAM = Integer.parseInt(GameCanvas.inputDlg.tfInput.getText());
                } catch (Exception var8) {
                }

                GameCanvas.gameAJ();
                return;
            case 1100086:
                try {
                    Code.fieldAN = Integer.parseInt(GameCanvas.inputDlg.tfInput.getText());
                } catch (Exception var7) {
                }

                GameCanvas.gameAJ();
                return;
            case 1100087:
                GameCanvas.inputDlg.gameAA("Độ Trễ", new Command("Đặt", 1100088), 1);
                GameCanvas.inputDlg.tfInput.setText(String.valueOf(Code.speedGame));
                return;
            case 1100088:
                int ivarspeed = Code.speedGame;

                try {
                    ivarspeed = Integer.parseInt(GameCanvas.inputDlg.tfInput.getText());
                } catch (Exception var6) {
                }

                if (ivarspeed >= 0 && ivarspeed < 100) {
                    Code.speedGame = ivarspeed;
                } else {
                    fieldAC("Tốc độ game từ 0 đến 99");
                }

                GameCanvas.gameAJ();
                return;
            case 1100089:
                if (Code.fieldAV = !Code.fieldAV) {
                    GameCanvas.inputDlg.gameAA("Khu-Mỗi khu cách nhau bằng dấu cách", new Command("Đặt", 1100090), 0);
                    GameCanvas.inputDlg.tfInput.setText(Code.fieldAM());
                }

                return;
            case 1100090:
                Code.fieldAE(GameCanvas.inputDlg.tfInput.getText());
                GameCanvas.gameAJ();
                return;

            case 1100093:
                InfoDlg.gameAB();
                isPaintAlert = true;
                this.gameNA = true;
                indexRow = 0;
                gameAB(175, 200);
                super.right = new Command("Đóng", 3);
                super.left = super.center = null;
                this.gameNC = "Danh sách PK";
                this.gameNB = SavePk.fieldAA();
                return;
            case 1100181:
                GameCanvas.gameAA(mResources.gameAD, new Command(mResources.gameXG, 1100182), new Command(mResources.gameCU, GameCanvas.instance, 8882, (Object) null));
                return;
            case 1100182:
                GameCanvas.currentDialog = null;
                GameCanvas.loginScr.gameAA(true);
                return;
            case 1103991:
                this.gameAN(10);
                return;
            case 1107921:
                var3 = (String) var2;
                GameCanvas.gameAJ();
                Service.gI().removeFriend(var3);
                setText(var3);
                return;
            case 1107931:
                var3 = (String) var2;
                Service.gI().addFriend(var3);
                return;
            case 1107932:
                setText(var3 = (String) var2);
                return;
            case 1108041:
                var3 = (String) var2;
                Service.gI().viewInfo(var3, 0);
                gI().resetButton();
                return;
            case 1400931:
                Service.gI().changeClanType(((Member) vClan.elementAt(indexRow)).name, 0);
                GameCanvas.gameAJ();
                return;
            case 1400941:
                Service.gI().moveOutClan(((Member) vClan.elementAt(indexRow)).name);
                GameCanvas.gameAJ();
                return;
            case 1400961:
                var9 = (Member) vClan.elementAt(indexRow);
                Service.gI().inviteClanBattlefield(var9.name);
                return;
            case 1400962:
                Service.gI().inviteClanBattlefieldAll();
                return;
            case 11000651:
                gameAM(1);
                return;
            case 11000652:
                gameAM(2);
                return;
            case 11000653:
                gameAM(3);
                return;
            case 11000661:
                indexMenu = 0;
                this.gameEU();
                return;
            case 11000662:
                indexMenu = 1;
                this.gameEU();
                Service.gI().requestClanMember();
                return;
            case 11000663:
                indexMenu = 2;
                this.gameEU();
                Service.gI().requestClanItem();
                return;
            case 11000664:
                indexMenu = 3;
                this.gameEU();
                Service.gI().requestClanLog();
                return;
            case 11000665:
                if (isViewClanInvite = !isViewClanInvite) {
                    RMS.gameAA(Char.getMyChar().cName + "vci", 1);
                    return;
                }

                RMS.gameAA(Char.getMyChar().cName + "vci", 0);
                return;
            case 11000666:
                indexMenu = 4;
                this.gameEU();
                Service.gI().requestClanItem();
                return;
            case 11000671:
                GameCanvas.gameAA(mResources.gameTW, 88836, (Object) null, 8882, (Object) null);
                return;
            case 11000672:
                GameCanvas.inputDlg.tfInput.setMaxTextLenght(6);
                GameCanvas.inputDlg.gameAA(mResources.gameTX, new Command(mResources.gameEC, GameCanvas.instance, 88837, (Object) null), 1);
                return;
            case 11000673:
                GameCanvas.input2Dlg.gameAA(mResources.gameUD, mResources.gameUE);
                GameCanvas.input2Dlg.tfInput.setMaxTextLenght(6);
                GameCanvas.input2Dlg.tfInput2.setMaxTextLenght(6);
                GameCanvas.input2Dlg.gameAA(mResources.gameTX, new Command(mResources.gameBH, GameCanvas.instance, 8882, (Object) null), new Command(mResources.gameEC, GameCanvas.instance, 88838, (Object) null), 1, 1);
                return;
            case 11000674:
                GameCanvas.inputDlg.tfInput.setMaxTextLenght(6);
                GameCanvas.inputDlg.gameAA(mResources.gameTX, new Command(mResources.gameEC, GameCanvas.instance, 88839, (Object) null), 1);
                return;
            case 11000761:
                super.right = this.gameSE;
                indexMenu = 0;
                fieldTG = true;
                gameAB(175, 200);
                return;
            case 11000771:
                Code.fieldAD(Code.dell[indexSelect]);
                return;
            case 11000781:
                Code.fieldAO();
                return;
            case 11000791:
                Code.fieldAC(Char.getMyChar().arrItemBag[indexSelect].template.id);
                return;

            case 11000800:
                GameCanvas.inputDlg.gameAA("Nhập số phiếu cần lật", new Command("OK", 11000801), 1);
                return;
            case 11000801:
                LatHinh.lap = Integer.parseInt(GameCanvas.inputDlg.tfInput.getText());
                (new Thread(new LatHinh(LatHinh.lap))).start();
                gameAC("Auto Lật " + LatHinh.lap + " Phiếu");
                GameCanvas.gameAJ();
                return;
            case 11000802:
                GameCanvas.inputDlg.gameAA("Tốc Độ Lật Hình", new Command("OK", 11000803), 1);
                return;
            case 11000803:
                LatHinh.time = (long) Integer.parseInt(GameCanvas.inputDlg.tfInput.getText());
                gameAC("Tốc độ lật hình của bạn là:" + LatHinh.time);
                GameCanvas.gameAJ();
                return;
            case 11000804:
                lathinh();
                return;
            case 11000812:
                Item item3 = gameAK(3);
                if (item3 != null) {
                    Code.gameAG(item3);
                }
                return;

            case 11000813:
                Item item2 = gameAK(3);
                if (item2 != null) {
                    Code.gameAH(item2);
                }
                return;
            case 110007611:
                super.right = this.gameSE;
                indexMenu = 0;
                ItemThrow = true;
                gameAB(175, 200);
                return;
            case 110007711:
                Code.ThrowAD(Code.dell[indexSelect]);
                return;
            case 110007811:
                Code.fieldAO();
                return;
            case 110007911:
                Code.ThrowAC(Char.getMyChar().arrItemBag[indexSelect].template.id);
                return;
            case 110007912:
                Char.getMyChar().myskill.template.id = 62;
                return;
            case 110007913:
                Char.getMyChar().myskill.template.id = 78;
                return;
            case 110007914:
                Char.getMyChar().myskill.template.id = 83;
                return;
            case 110007915:
                Char.getMyChar().myskill.template.id = 61;
                return;
            case 110007916:
                Char.getMyChar().myskill.template.id = 73;
                return;
            case 110007917:
                Char.getMyChar().myskill.template.id = 79;
                return;
            case 110007918:
                Char.getMyChar().myskill.template.id = 66;
                return;
            case 110007919:
                Char.getMyChar().myskill.template.id = 77;
                return;
            case 110007920:
                Char.getMyChar().myskill.template.id = 84;
                return;
            case 110007921:
                Char.getMyChar().myskill.template.id = 65;
                return;
            case 110007922:
                Char.getMyChar().myskill.template.id = 74;
                return;
            case 110007923:
                Char.getMyChar().myskill.template.id = 80;
                return;
            case 110007924:
                Char.getMyChar().myskill.template.id = 63;
                return;
            case 110007925:
                Char.getMyChar().myskill.template.id = 85;
                return;
            case 110007926:
                Char.getMyChar().myskill.template.id = 81;
                return;
            case 110007927:
                Char.getMyChar().myskill.template.id = 64;
                return;
            case 110007928:
                Char.getMyChar().myskill.template.id = 76;
                return;
            case 110007929:
                Char.getMyChar().myskill.template.id = 82;
                return;
            case 110007930:
                FakeSkill();
                return;
            case 110007931:
                ClassTieu();
                return;
            case 110007932:
                ClassKiem();
                return;
            case 110007933:
                ClassQuat();
                return;
            case 110007934:
                ClassDao();
                return;
            case 110007935:
                ClassKunai();
                return;
            case 110007936:
                ClassCung();
                return;
            case 110007937:

                return;

        }

    }

    public static void FakeSkill() {
        MyVector myVector = new MyVector();
        myVector.addElement(new Command("Tiêu", 110007931));
        myVector.addElement(new Command("Kiếm", 110007932));
        myVector.addElement(new Command("Quạt", 110007933));
        myVector.addElement(new Command("Đao", 110007934));
        myVector.addElement(new Command("Kunai", 110007935));
        myVector.addElement(new Command("Cung", 110007936));
        GameCanvas.menu.gameAA(myVector);
    }

    public static void ClassTieu() {
        MyVector myVector = new MyVector();
        myVector.addElement(new Command("Tiêu 7x", 110007912));
        myVector.addElement(new Command("Tiêu 8x", 110007913));
        myVector.addElement(new Command("Tiêu 10x", 110007914));
        GameCanvas.menu.gameAA(myVector);
    }

    public static void ClassKiem() {
        MyVector myVector = new MyVector();
        myVector.addElement(new Command("Kiếm 7x", 110007915));
        myVector.addElement(new Command("Kiếm 8x", 110007916));
        myVector.addElement(new Command("Kiếm 10x", 110007917));
        GameCanvas.menu.gameAA(myVector);
    }

    public static void ClassQuat() {
        MyVector myVector = new MyVector();
        myVector.addElement(new Command("Quạt 7x", 110007918));
        myVector.addElement(new Command("Quạt 8x", 110007919));
        myVector.addElement(new Command("Quạt 10x", 110007920));
        GameCanvas.menu.gameAA(myVector);
    }

    public static void ClassDao() {
        MyVector myVector = new MyVector();
        myVector.addElement(new Command("Đao 7x", 110007921));
        myVector.addElement(new Command("Đao 8x", 110007922));
        myVector.addElement(new Command("Đao 10x", 110007923));
        GameCanvas.menu.gameAA(myVector);
    }

    public static void ClassKunai() {
        MyVector myVector = new MyVector();
        myVector.addElement(new Command("Kunai 7x", 110007924));
        myVector.addElement(new Command("Kunai 8x", 110007925));
        myVector.addElement(new Command("Kunai 10x", 110007926));
        GameCanvas.menu.gameAA(myVector);
    }

    public static void ClassCung() {
        MyVector myVector = new MyVector();
        myVector.addElement(new Command("Cung 7x", 110007927));
        myVector.addElement(new Command("Cung 8x", 110007928));
        myVector.addElement(new Command("Cung 10x", 110007929));
        GameCanvas.menu.gameAA(myVector);
    }

    public static void lathinh() {
        MyVector var0 = new MyVector();
        var0.addElement(new Command("Lật Hình", 11000800));
        var0.addElement(new Command("Set Time Lật", 11000802));
        GameCanvas.menu.gameAA(var0);
    }

    private void gameDV() {
        try {
            if (Char.getMyChar().arrItemBag[indexSelect].template.id == 454) {
                if (arrItemSplit == null) {
                    arrItemSplit = new Item[24];
                }

                for (int var3 = 0; var3 < arrItemSplit.length; ++var3) {
                    if (var3 == 20) {
                        GameCanvas.setText(mResources.gameNO);
                        return;
                    }

                    if (arrItemSplit[var3] == null) {
                        arrItemSplit[var3] = Char.getMyChar().arrItemBag[indexSelect];
                        Char.getMyChar().arrItemBag[indexSelect] = null;
                        super.left = super.center = null;
                        this.gameBC();
                        return;
                    }
                }

            } else if (Char.getMyChar().arrItemBag[indexSelect].upgrade > 11) {
                if (itemSplit == null) {
                    itemSplit = Char.getMyChar().arrItemBag[indexSelect];
                    Char.getMyChar().arrItemBag[indexSelect] = null;
                } else {
                    Item var1 = Char.getMyChar().arrItemBag[indexSelect];
                    Char.getMyChar().arrItemBag[indexSelect] = null;
                    Char.getMyChar().arrItemBag[itemSplit.indexUI] = itemSplit;
                    itemSplit = var1;
                }

                Service.gI().requestItemInfo(itemSplit.typeUI, itemSplit.indexUI);
            } else {
                GameCanvas.setText(mResources.gameWT);
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }
    }

    private void gameDW() {
        Item var1 = gameAK(44);
        arrItemSplit[indexSelect] = null;
        Char.getMyChar().arrItemBag[var1.indexUI] = var1;
        super.left = super.center = null;
        this.gameBC();
    }

    private static void gameDX() {
        switch (indexRow) {
            case 0:
                Char.isAHP = !Char.isAHP;
                if (typeActive == 1) {
                    Char.isAHP = false;
                    InfoMe.gameAA(mResources.gameWF, 20, mFont.tahoma_7_yellow);
                }

                return;
            case 1:
                Char.isAMP = !Char.isAMP;
                if (typeActive == 1) {
                    Char.isAMP = false;
                    InfoMe.gameAA(mResources.gameWF, 20, mFont.tahoma_7_yellow);
                }

                return;
            case 2:
                Char.isAFood = !Char.isAFood;
                if (typeActive == 1) {
                    Char.isAFood = false;
                    InfoMe.gameAA(mResources.gameWF, 20, mFont.tahoma_7_yellow);
                }

                return;
            case 3:
                Char.isABuff = !Char.isABuff;
                return;
            case 4:
                Char.fieldEH = !Char.fieldEH;
                return;
            case 5:
                Char.fieldEI = !Char.fieldEI;
                return;
            case 6:
                Char.fieldEG = !Char.fieldEG;
                return;
            case 7:
                if (Char.isAPickYen = !Char.isAPickYen) {
                    Char.isANoPick = false;
                }

                return;
            case 8:
                if (Char.isAPickYHM = !Char.isAPickYHM) {
                    Char.isANoPick = false;
                }

                return;
            case 9:
                if (Char.isAPickYHMS = !Char.isAPickYHMS) {
                    Char.isANoPick = false;
                }

                return;
            case 10:
                Char.fieldEM = !Char.fieldEM;
                return;
            case 11:
                if (Char.fieldEN = !Char.fieldEN) {
                    Char.isANoPick = false;
                }

                return;
            case 12:
                if (Char.fieldEO = !Char.fieldEO) {
                    Char.fieldEN = true;
                    Char.isANoPick = false;
                }

                return;
            case 13:
                if (Char.fieldEP = !Char.fieldEP) {
                    Char.isANoPick = false;
                }

                return;
            case 14:
                if (Char.fieldEQ = !Char.fieldEQ) {
                    Char.isANoPick = false;
                }

                return;
            case 15:
                if (Char.fieldER = !Char.fieldER) {
                    Char.isANoPick = false;
                }

                return;
            case 16:
                if (Char.fieldES = !Char.fieldES) {
                    Char.isANoPick = false;
                }

                return;
            case 17:
                if (Char.isANoPick = !Char.isANoPick) {
                    Char.isAPickYHMS = false;
                    Char.isAPickYHM = false;
                    Char.isAPickYen = false;
                    Char.fieldEN = false;
                    Char.fieldEO = false;
                    Char.fieldEP = false;
                    Char.fieldEQ = false;
                    Char.fieldER = false;
                    Char.fieldES = false;
                }

                return;
            case 18:
                Char.fieldEU = !Char.fieldEU;
                return;
            case 19:
                Char.fieldEV = !Char.fieldEV;
                return;
            case 20:
                Char.fieldEW = !Char.fieldEW;
                return;
            case 21:
                Char.fieldEX = !Char.fieldEX;
                return;
            case 22:
                Char.fieldEY = !Char.fieldEY;
                return;
            case 23:
                Char.fieldEZ = !Char.fieldEZ;
                return;
            case 24:
                Char.ReConnect = !Char.ReConnect;
                return;
            case 25:
                Char.fieldFB = !Char.fieldFB;
                return;
            case 26:
                Char.fieldFC = !Char.fieldFC;
                return;
            case 27:
                Char.fieldFD = !Char.fieldFD;
                return;
            case 28:
                Char.fieldFE = !Char.fieldFE;
                return;
            case 29:
                Char.fieldFF = !Char.fieldFF;
                return;
            case 30:
                Char.fieldFG = !Char.fieldFG;
                return;
            case 31:
                Char.fieldFH = !Char.fieldFH;
                return;
            case 32:
                Char.fieldFI = !Char.fieldFI;
                return;
            case 33:
                Char.fieldFJ = !Char.fieldFJ;
                return;
            case 34:
                Char.isUseCL = !Char.isUseCL;
                return;
            case 35:
                Char.isBuyCL = !Char.isBuyCL;
                return;
            default:
        }
    }

    private void gameDY() {
        if (arrItemSprin != null) {
            Item var1;
            (var1 = new Item()).template = ItemTemplates.gameAA(arrItemSprin[indexSelect]);
            this.gameAA((int) 38, (Item) var1);
        }

    }

    private void gameDZ() {
        if (this.gameMX != 0 || this.gameMW != 0 || this.gameMY != 0) {
            for (int var1 = 0; var1 < arrItemStands.length - 1; ++var1) {
                for (int var2 = var1 + 1; var2 < arrItemStands.length; ++var2) {
                    ItemStands var3;
                    if (this.gameMW == 1) {
                        if (arrItemStands[var1].price < arrItemStands[var2].price) {
                            var3 = arrItemStands[var1];
                            arrItemStands[var1] = arrItemStands[var2];
                            arrItemStands[var2] = var3;
                        }
                    } else if (this.gameMW == 2 && arrItemStands[var1].price > arrItemStands[var2].price) {
                        var3 = arrItemStands[var1];
                        arrItemStands[var1] = arrItemStands[var2];
                        arrItemStands[var2] = var3;
                    }

                    if (this.gameMY == 1) {
                        if (!arrItemStands[var1].item.template.name.equals(arrItemStands[var2].item.template.name) && arrItemStands[var1].item.template.name.compareTo(arrItemStands[var2].item.template.name) > 0) {
                            var3 = arrItemStands[var1];
                            arrItemStands[var1] = arrItemStands[var2];
                            arrItemStands[var2] = var3;
                        }
                    } else if (this.gameMY == 2 && !arrItemStands[var1].item.template.name.equals(arrItemStands[var2].item.template.name) && arrItemStands[var1].item.template.name.compareTo(arrItemStands[var2].item.template.name) < 0) {
                        var3 = arrItemStands[var1];
                        arrItemStands[var1] = arrItemStands[var2];
                        arrItemStands[var2] = var3;
                    }

                    if (this.gameMX == 1) {
                        if (arrItemStands[var1].item.template.level < arrItemStands[var2].item.template.level) {
                            var3 = arrItemStands[var1];
                            arrItemStands[var1] = arrItemStands[var2];
                            arrItemStands[var2] = var3;
                        }
                    } else if (this.gameMX == 2 && arrItemStands[var1].item.template.level > arrItemStands[var2].item.template.level) {
                        var3 = arrItemStands[var1];
                        arrItemStands[var1] = arrItemStands[var2];
                        arrItemStands[var2] = var3;
                    }
                }
            }

        }
    }

    private static void gameEA() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameVB, 150411));
        var0.addElement(new Command(mResources.gameVD, 150412));
        var0.addElement(new Command(mResources.gameVC, 150413));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameEB() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCD, 15041));
        var0.addElement(new Command(mResources.gameCS, 15042));
        GameCanvas.menu.gameAA(var0);
    }

    private void gameEC() {
        Char.getMyChar().arrItemBag[itemSell.indexUI] = itemSell;
        itemSell = null;
        super.left = super.center = null;
    }

    private static void gameED() {
        Item var0;
        if ((var0 = Char.getMyChar().arrItemBag[indexSelect]) != null) {
            if (!var0.isLock && !var0.isExpires) {
                if (itemSell == null) {
                    itemSell = Char.getMyChar().arrItemBag[indexSelect];
                    Char.getMyChar().arrItemBag[indexSelect] = null;
                } else {
                    var0 = Char.getMyChar().arrItemBag[indexSelect];
                    Char.getMyChar().arrItemBag[indexSelect] = null;
                    Char.getMyChar().arrItemBag[itemSell.indexUI] = itemSell;
                    itemSell = var0;
                }
            } else {
                GameCanvas.setText(mResources.gameNG);
            }
        }
    }

    private static void gameEE() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCS, 140191));
        var0.addElement(new Command(mResources.gameCT, 140192));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameEF() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameBZ, (IActionListener) null, 120081, (Object) null));
        var0.addElement(new Command(mResources.gameCA, (IActionListener) null, 12007, (Object) null));
        GameCanvas.menu.gameAA(var0);
    }

    private void gameEG() {
        if (this.gameUC == 1) {
            GameCanvas.input2Dlg.gameAA(mResources.gameUN, mResources.gameUM);
            GameCanvas.input2Dlg.gameAA(mResources.gameUJ, new Command(mResources.gameBH, GameCanvas.gameAA(), 8882, (Object) null), new Command(mResources.gameCB, (IActionListener) null, 120071, (Object) null), 0, 0);
        } else {
            GameCanvas.input2Dlg.gameAA(mResources.gameUO, mResources.gameUP);
            GameCanvas.input2Dlg.gameAA(mResources.gameUK, new Command(mResources.gameBH, GameCanvas.gameAA(), 8882, (Object) null), new Command(mResources.gameCX, (IActionListener) null, 120072, (Object) null), 0, 0);
        }
    }

    public static void setText(String var0) {
        int var1;
        for (var1 = 0; var1 < vFriend.size(); ++var1) {
            Friend var2;
            if ((var2 = (Friend) vFriend.elementAt(var1)).friendName.equals(var0) && var2.type == 4) {
                vFriend.removeElementAt(var1);
                break;
            }
        }

        for (var1 = 0; var1 < vFriendWait.size(); ++var1) {
            if (((Friend) vFriendWait.elementAt(var1)).friendName.equals(var0)) {
                vFriendWait.removeElementAt(var1);
                return;
            }
        }

    }

    private static void gameEH() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameSV[0] + ": " + (gameHR ? mResources.gameBR : mResources.gameBQ), 140071));
        var0.addElement(new Command(mResources.gameSW, 140072));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameEI() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameSV[0] + ": " + (gameHR ? mResources.gameBR : mResources.gameBQ), 140071));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameEJ() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameTB, 110441));
        var0.addElement(new Command(mResources.gameSV[0] + ": " + (gameHR ? mResources.gameBR : mResources.gameBQ), 140071));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameEK() {
        MyVector var0 = new MyVector();
        if (typeActive == 0) {
            var0.addElement(new Command(mResources.gameBU, 11000671));
        } else if (typeActive == 1) {
            var0.addElement(new Command(mResources.gameBN, 11000672));
        }

        if (typeActive == 1 || typeActive == 2) {
            var0.addElement(new Command(mResources.gameUC, 11000674));
            var0.addElement(new Command(mResources.gameUB, 11000673));
        }

        GameCanvas.menu.gameAA(var0);
    }

    private static void gameEL() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameSG, 110471));
        var0.addElement(new Command(mResources.gameSR[4], 1100061));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameEM() {
        GameCanvas.gameAJ();
        Service.gI().doConvertUpgrade(arrItemConvert[0].indexUI, arrItemConvert[1].indexUI, arrItemConvert[2].indexUI);
    }

    private static void gameEN() {
        MyVector var0 = new MyVector();
        if (Char.getMyChar().ctypeClan == 3 || Char.getMyChar().ctypeClan == 4) {
            if (gameHL == 1) {
                var0.addElement(new Command(mResources.gameSV[8], 140044));
            }

            var0.addElement(new Command(mResources.gameSV[2], 140041));
            var0.addElement(new Command(mResources.gameSV[5], 140042));
            if (Char.getMyChar().ctypeClan == 4) {
                var0.addElement(new Command(mResources.gameSV[4], 140043));
            }

            var0.addElement(new Command(mResources.gameSB[10] + ": " + (isViewClanInvite ? mResources.gameBR : mResources.gameBQ), 11000665));
        }

        GameCanvas.menu.gameAA(var0);
    }

    private static void gameEO() {
        Party var0;
        if ((var0 = (Party) vPtMap.elementAt(indexRow)) != null && !Char.getMyChar().cName.equals(var0.name)) {
            MyVector var1;
            (var1 = new MyVector()).addElement(new Command(mResources.gameSB[6], 1108041, var0.name));
            var1.addElement(new Command(mResources.gameCW, 12002, var0.name));
            var1.addElement(new Command(mResources.gameSX[2], 110803, var0.name));
            GameCanvas.menu.gameAA(var1);
        }

    }

    private static void gameEP() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameSM, 110452));
        if (vPtMap.size() > 0) {
            var0.addElement(new Command(mResources.gameSL, 110451));
        }

        GameCanvas.menu.gameAA(var0);
    }

    private static void gameEQ() {
        Member var0 = (Member) vClan.elementAt(indexRow);
        MyVector var1;
        (var1 = new MyVector()).addElement(new Command(mResources.gameST[1], 140091, var0.name));
        var1.addElement(new Command(mResources.gameST[2], 140092, var0.name));
        GameCanvas.menu.gameAA(var1);
    }

    private void gameER() {
        Member var1;
        if (indexRow >= 0 && indexRow < vClan.size() && (var1 = gameCW()) != null && !var1.name.equals("")) {
            Service.gI().viewInfo(var1.name, 0);
            gameME = false;
            gI().resetButton();
        }

    }

    private void gameES() {
        Member var2 = gameCW();
        MyVector var1 = new MyVector();
        if (Char.getMyChar().ctypeClan == 4) {
            if (var2.type != 4) {
                if (var2.type != 3 && var2.type != 2) {
                    var1.addElement(new Command(mResources.gameTO, 14009, var2.name));
                } else {
                    var1.addElement(new Command(mResources.gameTP, 140093, var2.name));
                }

                if (TileMap.mapID != 98 && TileMap.mapID != 104) {
                    var1.addElement(new Command(mResources.gameSV[6], 140095, var2.name));
                } else {
                    var1.addElement(new Command(mResources.gameSV[7], 140096, var2.name));
                }

                var1.addElement(new Command(mResources.gameTQ, 140094, var2.name));
            }

            var1.addElement(new Command(mResources.gameBV, 14007));
        } else if (Char.getMyChar().ctypeClan == 3) {
            if (var2.type != 4 && !var2.name.equals(Char.getMyChar().cName)) {
                var1.addElement(new Command(mResources.gameTQ, 140094, var2.name));
                if (TileMap.mapID != 98 && TileMap.mapID != 104) {
                    var1.addElement(new Command(mResources.gameSV[6], 140095, var2.name));
                } else {
                    var1.addElement(new Command(mResources.gameSV[7], 140096, var2.name));
                }
            }

            var1.addElement(new Command(mResources.gameBV, 14007));
            var1.addElement(new Command(mResources.gameSV[1], 14008, var2.name));
        } else {
            var1.addElement(new Command(mResources.gameBV, 14007));
            var1.addElement(new Command(mResources.gameSV[1], 14008, var2.name));
        }

        GameCanvas.menu.gameAA(var1);
    }

    private static boolean gameET() {
        for (int var0 = 0; var0 < Char.getMyChar().arrItemBag.length; ++var0) {
            Item var1;
            if ((var1 = Char.getMyChar().arrItemBag[var0]) != null && var1.template.id == 279 && var1.template.level <= Char.getMyChar().clevel) {
                return true;
            }
        }

        return false;
    }

    private static void gameAB(String var0) {
        MyVector var1;
        (var1 = new MyVector()).addElement(new Command(mResources.gameSB[6], 110805));
        var1.addElement(new Command(mResources.gameCW, 12002, var0));
        var1.addElement(new Command(mResources.gameTA, 110791, var0));
        var1.addElement(new Command(mResources.gameBC, 14020, var0));
        var1.addElement(new Command(mResources.gameSX[2], 110803, var0));
        GameCanvas.menu.gameAA(var1);
    }

    private void gameEU() {
        gameME = true;
        gameMF = true;
        gameHS = false;
        this.gameRM = this.gameRN = null;
        gameAB(175, 200);
        this.gameBC();
        super.right = this.gameSE;
        Service.gI().requestClanInfo();
    }

    private static void gameEV() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameRH[0], 11000661));
        var0.addElement(new Command(mResources.gameRH[1], 11000662));
        var0.addElement(new Command(mResources.gameRH[2], 11000663));
        var0.addElement(new Command(mResources.gameRH[3], 11000664));
        var0.addElement(new Command(mResources.gameRH[4], 11000666));
        GameCanvas.menu.gameAA(var0);
    }

    private void gameEW() {
        Char.getMyChar().charFocus = null;
        Char.isManualFocus = false;
        this.cLastFocusID = -1;
        gameMC = false;
        this.resetButton();
    }

    private void gameEX() {
        Char var1;
        if (!(var1 = (Char) vCharInMap.elementAt(indexRow)).gameBB()) {
            this.cLastFocusID = var1.charID;
            Char.getMyChar().mobFocus = null;
            Char.getMyChar().gameAV();
            Char.getMyChar().itemFocus = null;
            Char.getMyChar();
            Char.isManualFocus = true;
            gameMC = false;
            Char.getMyChar().charFocus = var1;
        }

        this.resetButton();
    }

    private void gameEY() {
        if (indexMenu == 0) {
            indexMenu = 1;
        } else {
            indexMenu = 0;
        }

        indexRow = 0;
        this.gameHH = indexMenu;
    }

    private static void gameEZ() {
        Party var0;
        if ((var0 = (Party) vParty.elementAt(indexRow)).c != null && var0.c != Char.getMyChar()) {
            Service.gI().viewInfo(var0.c.cName, 0);
            gameHW = false;
            gI().resetButton();
        }

    }

    private static void gameFA() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCD, 110221));
        var0.addElement(new Command(mResources.gameCL, 11050));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameFB() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCD, 11048));
        var0.addElement(new Command(mResources.gameCK, 11049));
        GameCanvas.menu.gameAA(var0);
    }

    private void gameFC() {
        if (Char.getMyChar().arrItemBag[indexSelect].template.type != 26 && Char.getMyChar().arrItemBag[indexSelect].template.id != 455 && Char.getMyChar().arrItemBag[indexSelect].template.id != 456) {
            if (Char.getMyChar().arrItemBag[indexSelect].template.id == 457) {
                GameCanvas.msgdlg.gameAA(mResources.gameWJ, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
            } else {
                GameCanvas.msgdlg.gameAA(mResources.gameWK, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
            }
        } else {
            if (Char.getMyChar().arrItemBag[indexSelect].template.type == 26 && Char.getMyChar().arrItemBag[indexSelect].template.id < 10) {
                GameCanvas.setText(mResources.gameWN);
                return;
            }

            short var1 = 0;
            int var2 = 0;
            boolean var3 = arrItemUpPeal[12] != null;

            int var4;
            for (var4 = 0; var4 < arrItemUpPeal.length; ++var4) {
                if (arrItemUpPeal[var4] != null && arrItemUpPeal[var4].template.type != 26) {
                    var1 = arrItemUpPeal[var4].template.id;
                    ++var2;
                }
            }

            for (var4 = 0; var4 < arrItemUpPeal.length; ++var4) {
                if (arrItemUpPeal[var4] == null) {
                    if (Char.getMyChar().arrItemBag[indexSelect].template.type == 26) {
                        if (arrItemUpPeal[12] == null) {
                            arrItemUpPeal[12] = Char.getMyChar().arrItemBag[indexSelect];
                            Char.getMyChar().arrItemBag[indexSelect] = null;
                        } else {
                            Item var5 = Char.getMyChar().arrItemBag[indexSelect];
                            Char.getMyChar().arrItemBag[indexSelect] = null;
                            Char.getMyChar().arrItemBag[arrItemUpPeal[12].indexUI] = arrItemUpPeal[12];
                            arrItemUpPeal[12] = var5;
                        }
                    } else if (var1 > 0 && Char.getMyChar().arrItemBag[indexSelect].template.id != var1) {
                        GameCanvas.setText(mResources.gameWO);
                    } else if ((!var3 || var2 < 3) && var2 < 9) {
                        arrItemUpPeal[var4] = Char.getMyChar().arrItemBag[indexSelect];
                        Char.getMyChar().arrItemBag[indexSelect] = null;
                    } else {
                        GameCanvas.setText(mResources.gameWQ);
                    }

                    super.left = super.center = null;
                    this.gameBC();
                    return;
                }
            }

            GameCanvas.msgdlg.gameAA(mResources.gameNO, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
        }

        GameCanvas.currentDialog = GameCanvas.msgdlg;
    }

    private void gameFD() {
        if (Char.getMyChar().arrItemBag[indexSelect].template.type != 26) {
            GameCanvas.msgdlg.gameAA(mResources.gameNH, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
            GameCanvas.currentDialog = GameCanvas.msgdlg;
        } else {
            for (int var1 = 0; var1 < arrItemUpPeal.length; ++var1) {
                if (arrItemUpPeal[var1] == null) {
                    arrItemUpPeal[var1] = Char.getMyChar().arrItemBag[indexSelect];
                    Char.getMyChar().arrItemBag[indexSelect] = null;
                    super.left = super.center = null;
                    this.gameBC();
                    return;
                }
            }

            GameCanvas.msgdlg.gameAA(mResources.gameNO, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
            GameCanvas.currentDialog = GameCanvas.msgdlg;
        }
    }

    private void gameFE() {
        Item var1 = gameAK(11);
        arrItemUpPeal[indexSelect] = null;
        Char.getMyChar().arrItemBag[var1.indexUI] = var1;
        super.left = super.center = null;
        this.gameBC();
    }

    private static void gameFF() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCJ, 111071));

        for (int var1 = 0; var1 < arrItemUpPeal.length; ++var1) {
            if (arrItemUpPeal[var1] != null) {
                var0.addElement(new Command(mResources.gameFV, 1600));
                break;
            }
        }

        GameCanvas.menu.gameAA(var0);
    }

    private static void gameFG() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCJ, 111071));

        for (int var1 = 0; var1 < arrItemUpPeal.length; ++var1) {
            if (arrItemUpPeal[var1] != null) {
                var0.addElement(new Command(mResources.gameFV, 11062));
                break;
            }
        }

        GameCanvas.menu.gameAA(var0);
    }

    private void gameFH() {
        try {
            int var2;
            int var5;
            if (gameMJ) {
                if (!Char.getMyChar().arrItemBag[indexSelect].isTypeBody() && !Char.getMyChar().arrItemBag[indexSelect].isTypeMounts() && Char.getMyChar().arrItemBag[indexSelect].template.id != 455 && Char.getMyChar().arrItemBag[indexSelect].template.id != 456 && Char.getMyChar().arrItemBag[indexSelect].template.id != 457) {
                    GameCanvas.msgdlg.gameAA(mResources.gameWR, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
                    GameCanvas.currentDialog = GameCanvas.msgdlg;
                } else if (Char.getMyChar().arrItemBag[indexSelect].template.id != 455 && Char.getMyChar().arrItemBag[indexSelect].template.id != 456 && Char.getMyChar().arrItemBag[indexSelect].template.id != 457) {
                    ItemOption var6 = null;

                    for (var2 = 0; var2 < Char.getMyChar().arrItemBag[indexSelect].options.size() && (var6 = (ItemOption) Char.getMyChar().arrItemBag[indexSelect].options.elementAt(var2)).optionTemplate.id != 85; ++var2) {
                        var6 = null;
                    }

                    if (var6 != null && var6.param >= 9) {
                        GameCanvas.setText(mResources.gameXA);
                    } else {
                        if (itemSplit == null) {
                            itemSplit = Char.getMyChar().arrItemBag[indexSelect];
                            Char.getMyChar().arrItemBag[indexSelect] = null;
                        } else {
                            Item var7 = Char.getMyChar().arrItemBag[indexSelect];
                            Char.getMyChar().arrItemBag[indexSelect] = null;
                            Char.getMyChar().arrItemBag[itemSplit.indexUI] = itemSplit;
                            itemSplit = var7;
                        }

                        Service.gI().requestItemInfo(itemSplit.typeUI, itemSplit.indexUI);
                    }
                } else {
                    if (arrItemSplit == null) {
                        arrItemSplit = new Item[24];
                    }

                    for (var5 = 0; var5 < arrItemSplit.length; ++var5) {
                        if (arrItemSplit[var5] == null) {
                            arrItemSplit[var5] = Char.getMyChar().arrItemBag[indexSelect];
                            Char.getMyChar().arrItemBag[indexSelect] = null;
                            super.left = super.center = null;
                            this.gameBC();
                            return;
                        }

                        if (var5 == arrItemSplit.length - 1) {
                            GameCanvas.setText(mResources.gameNO);
                        }
                    }

                }
            } else if (Char.getMyChar().arrItemBag[indexSelect].isTypeBody() && Char.getMyChar().arrItemBag[indexSelect].upgrade > 0) {
                if (itemSplit == null) {
                    itemSplit = Char.getMyChar().arrItemBag[indexSelect];
                    Char.getMyChar().arrItemBag[indexSelect] = null;
                } else {
                    Item var1 = Char.getMyChar().arrItemBag[indexSelect];
                    Char.getMyChar().arrItemBag[indexSelect] = null;
                    Char.getMyChar().arrItemBag[itemSplit.indexUI] = itemSplit;
                    itemSplit = var1;
                }

                if (itemSplit != null) {
                    var5 = 0;
                    if (itemSplit.isTypeClothe()) {
                        for (var2 = 0; var2 < itemSplit.upgrade; ++var2) {
                            var5 += upClothe[var2];
                        }
                    } else if (itemSplit.isTypeAdorn()) {
                        for (var2 = 0; var2 < itemSplit.upgrade; ++var2) {
                            var5 += upAdorn[var2];
                        }
                    } else if (itemSplit.isTypeWeapon()) {
                        for (var2 = 0; var2 < itemSplit.upgrade; ++var2) {
                            var5 += upWeapon[var2];
                        }
                    }

                    var5 /= 2;
                    var2 = 0;
                    arrItemSplit = new Item[24];

                    for (int var3 = crystals.length - 1; var3 >= 0; --var3) {
                        if (var5 >= crystals[var3]) {
                            arrItemSplit[var2] = new Item();
                            arrItemSplit[var2].typeUI = 3;
                            arrItemSplit[var2].template = ItemTemplates.gameAA((short) var3);
                            arrItemSplit[var2].isLock = itemSplit.isLock;
                            arrItemSplit[var2].expires = -1L;
                            var5 -= crystals[var3];
                            ++var3;
                            ++var2;
                        }
                    }
                }

                super.left = super.center = null;
                this.gameBC();
            } else {
                GameCanvas.msgdlg.gameAA(mResources.gameNM, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
                GameCanvas.currentDialog = GameCanvas.msgdlg;
            }
        } catch (Exception var4) {
            GameCanvas.msgdlg.gameAA(mResources.gameWR, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
            GameCanvas.currentDialog = GameCanvas.msgdlg;
        }
    }

    private void gameFI() {
        MyVector var1;
        (var1 = new MyVector()).addElement(this.gameRX);
        if (itemSplit != null) {
            var1.addElement(new Command(mResources.gameFV, 111031));
        }

        GameCanvas.menu.gameAA(var1);
    }

    private void gameFJ() {
        if (Char.getMyChar().arrItemBag[indexSelect].isTypeBody()) {
            if (Char.getMyChar().arrItemBag[indexSelect].template.level >= 10 && Char.getMyChar().arrItemBag[indexSelect].template.type < 10) {
                if (Char.getMyChar().arrItemBag[indexSelect].upgrade >= Char.getMyChar().arrItemBag[indexSelect].getUpMax()) {
                    GameCanvas.msgdlg.gameAA(mResources.gameNL, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
                    GameCanvas.currentDialog = GameCanvas.msgdlg;
                } else {
                    if (itemUpGrade == null) {
                        itemUpGrade = Char.getMyChar().arrItemBag[indexSelect];
                        Char.getMyChar().arrItemBag[indexSelect] = null;
                    } else {
                        Item var3 = Char.getMyChar().arrItemBag[indexSelect];
                        Char.getMyChar().arrItemBag[indexSelect] = null;
                        Char.getMyChar().arrItemBag[itemUpGrade.indexUI] = itemUpGrade;
                        itemUpGrade = var3;
                    }

                    super.left = super.center = null;
                    this.gameBC();
                }
            } else {
                GameCanvas.msgdlg.gameAA(mResources.gameNK, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
                GameCanvas.currentDialog = GameCanvas.msgdlg;
            }
        } else if (Char.getMyChar().arrItemBag[indexSelect].template.type != 26 && Char.getMyChar().arrItemBag[indexSelect].template.type != 28) {
            GameCanvas.msgdlg.gameAA(mResources.gameNI, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
            GameCanvas.currentDialog = GameCanvas.msgdlg;
        } else {
            int var1;
            if (Char.getMyChar().arrItemBag[indexSelect].template.type == 28) {
                for (var1 = 0; var1 < arrItemUpGrade.length; ++var1) {
                    if (arrItemUpGrade[var1] != null && arrItemUpGrade[var1].template.type == 28) {
                        Item var2 = Char.getMyChar().arrItemBag[indexSelect];
                        Char.getMyChar().arrItemBag[indexSelect] = null;
                        int var10001 = arrItemUpGrade[var1].indexUI;
                        Char.getMyChar().arrItemBag[var10001] = arrItemUpGrade[var1];
                        arrItemUpGrade[var1] = var2;
                        return;
                    }
                }
            }

            for (var1 = 0; var1 < arrItemUpGrade.length; ++var1) {
                if (arrItemUpGrade[var1] == null) {
                    arrItemUpGrade[var1] = Char.getMyChar().arrItemBag[indexSelect];
                    Char.getMyChar().arrItemBag[indexSelect] = null;
                    super.left = super.center = null;
                    this.gameBC();
                    return;
                }
            }

            GameCanvas.msgdlg.gameAA(mResources.gameNO, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
            GameCanvas.currentDialog = GameCanvas.msgdlg;
        }
    }

    private void gameAL(int var1) {
        if (arrItemConvert[var1] == null) {
            arrItemConvert[var1] = Char.getMyChar().arrItemBag[indexSelect];
            Char.getMyChar().arrItemBag[indexSelect] = null;
        } else {
            Item var2 = Char.getMyChar().arrItemBag[indexSelect];
            Char.getMyChar().arrItemBag[indexSelect] = null;
            int var10001 = arrItemConvert[var1].indexUI;
            Char.getMyChar().arrItemBag[var10001] = arrItemConvert[var1];
            arrItemConvert[var1] = var2;
        }

        super.left = super.center = null;
        this.gameBC();
    }

    private void gameFK() {
        if (Char.getMyChar().arrItemBag[indexSelect].isTypeBody()) {
            if (Char.getMyChar().arrItemBag[indexSelect].upgrade > 0) {
                this.gameAL(0);
            } else {
                this.gameAL(1);
            }
        } else if (Char.getMyChar().arrItemBag[indexSelect].template.id != 269 && Char.getMyChar().arrItemBag[indexSelect].template.id != 270 && Char.getMyChar().arrItemBag[indexSelect].template.id != 271) {
            GameCanvas.msgdlg.gameAA(mResources.gameNJ, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
            GameCanvas.currentDialog = GameCanvas.msgdlg;
        } else {
            this.gameAL(2);
        }
    }

    private void gameFL() {
        Item var1 = null;
        var1 = arrItemConvert[2];
        arrItemConvert[2] = null;
        Char.getMyChar().arrItemBag[var1.indexUI] = var1;
        super.left = super.center = null;
        this.gameBC();
    }

    private void gameFM() {
        Item var1 = null;
        var1 = arrItemConvert[indexSelect];
        arrItemConvert[indexSelect] = null;
        Char.getMyChar().arrItemBag[var1.indexUI] = var1;
        super.left = super.center = null;
        this.gameBC();
    }

    private void gameFN() {
        Item var1 = gameAK(10);
        arrItemUpGrade[indexSelect] = null;
        Char.getMyChar().arrItemBag[var1.indexUI] = var1;
        super.left = super.center = null;
        this.gameBC();
    }

    private static void gameFO() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCJ, 111001));
        if (itemUpGrade != null) {
            for (int var1 = 0; var1 < arrItemUpGrade.length; ++var1) {
                if (arrItemUpGrade[var1] != null) {
                    var0.addElement(new Command(mResources.gameFV, 110981));
                    break;
                }
            }
        }

        GameCanvas.menu.gameAA(var0);
    }

    private void gameFP() {
        MyVector var1;
        (var1 = new MyVector()).addElement(this.gameRW);

        for (int var2 = 0; var2 < arrItemConvert.length; ++var2) {
            if (arrItemConvert[var2] == null) {
                super.left = null;
                break;
            }

            if (var2 == arrItemConvert.length - 1) {
                var1.addElement(new Command(mResources.gameFV, 140131));
            }
        }

        GameCanvas.menu.gameAA(var1);
    }

    private void gameFQ() {
        MyVector var1;
        (var1 = new MyVector()).addElement(this.gameRV);

        for (int var2 = 0; var2 < arrItemUpGrade.length; ++var2) {
            if (arrItemUpGrade[var2] != null) {
                var1.addElement(new Command(mResources.gameFV, 110981));
                break;
            }
        }

        GameCanvas.menu.gameAA(var1);
    }

    private static void gameAC(Item var0) {
        MyVector var1;
        (var1 = new MyVector()).addElement(new Command(mResources.gameCS, 110921, var0));
        var1.addElement(new Command(mResources.gameCT, 110922, var0));
        GameCanvas.menu.gameAA(var1);
    }

    private static void gameFR() {
        indexRow = 0;
        scrMain.gameAA();
        if (gameNT == 0) {
            gameNT = 1;
        } else {
            gameNT = 0;
        }
    }

    private void gameFS() {
        indexMenu = 0;
        isPaintInfoMe = false;
        super.left = this.gamePA;
        super.right = this.gameHE;
        super.center = null;
        System.gc();
        this.resetButton();
        this.gameCF();
    }

    private static void gameFT() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCS, 110851));
        var0.addElement(new Command(mResources.gameCT, 110852));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameFU() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameDY, 110841));
        var0.addElement(new Command(mResources.gameDZ, 110842));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameFV() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gamePD, 110821));
        if (!GameCanvas.isTouch) {
            if (gameRJ == 0) {
                var0.addElement(new Command(mResources.gameXJ, 2003));
            } else {
                var0.addElement(new Command(mResources.gameXI, 2003));
            }
        }

        GameCanvas.menu.gameAA(var0);
    }

    private static void gameFW() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameUS[0], 110811));
        var0.addElement(new Command(mResources.gameUS[1], 110812));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameAC(String var0) {
        MyVector var1;
        (var1 = new MyVector()).addElement(new Command(mResources.gameSX[0], 110801));
        var1.addElement(new Command(mResources.gameSX[1], 110802));
        var1.addElement(new Command(mResources.gameSX[2], 110803, var0));
        var1.addElement(new Command(mResources.gameSB[7], 12002, var0));
        var1.addElement(new Command(mResources.gameSB[6], 110804));
        GameCanvas.menu.gameAA(var1);
    }

    private static void gameFX() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameDY, 140011));
        var0.addElement(new Command(mResources.gameDZ, 140012));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameAD(String var0) {
        GameCanvas.gameAA(mResources.gameTD, new Command(mResources.gameCH, 1107921, var0), new Command(mResources.gameCU, 1));
    }

    private static void gameFY() {
        Friend var0 = (Friend) vFriend.elementAt(indexRow);
        MyVector var1;
        (var1 = new MyVector()).addElement(new Command(mResources.gameSB[6], 1108041, var0.friendName));
        var1.addElement(new Command(mResources.gameCW, 12002, var0.friendName));
        if (var0.type == 4) {
            var1.addElement(new Command(mResources.gameEC, 1107931, var0.friendName));
            var1.addElement(new Command(mResources.gameED, 1107932, var0.friendName));
        } else {
            var1.addElement(new Command(mResources.gameTA, 110791, var0.friendName));
            var1.addElement(new Command(mResources.gameBC, 14020, var0.friendName));
            var1.addElement(new Command(mResources.gameBW, 110792, var0.friendName));
        }

        GameCanvas.menu.gameAA(var1);
    }

    private void gameAA(byte var1) {
        if (indexRow >= 0 && indexRow < vList.size()) {
            Ranked var2 = null;

            try {
                var2 = (Ranked) vList.elementAt(indexRow);
                String var3 = null;
                if (var2 != null) {
                    var3 = var2.name;
                } else {
                    var3 = "raned=null";
                }

                Service.gI().requestRanked(var1, var3);
                this.resetButton();
            } catch (Exception var4) {
            }
        }
    }

    private static void gameFZ() {
        if (indexRow >= 0 && indexRow < vList.size()) {
            int var0 = ((DunItem) vList.elementAt(indexRow)).id;
            Service.gI().requestMatchInfo(var0);
        }
    }

    private static void gameGA() {
        Friend var0 = (Friend) vEnemies.elementAt(indexRow);
        MyVector var1;
        (var1 = new MyVector()).addElement(new Command(mResources.gameSB[6], 1108041, var0.friendName));
        var1.addElement(new Command(mResources.gameCW, 12002, var0.friendName));
        var1.addElement(new Command(mResources.gameTA, 11076, var0.friendName));
        var1.addElement(new Command(mResources.gameBC, 14020, var0.friendName));
        var1.addElement(new Command(mResources.gameBW, 11077, var0.friendName));
        GameCanvas.menu.gameAA(var1);
    }

    private static void gameAE(String var0) {
        GameCanvas.gameAA(mResources.gameTD, new Command(mResources.gameCH, 110771, var0), new Command(mResources.gameCU, 1));
    }

    private void gameGB() {
        isPaintAlert = false;
        this.gameNC = null;
        this.gameNB = null;
        super.center = null;
        this.resetButton();
    }

    private void gameAA(short var1, String var2) {
        NinjaUtil.gameAA(var2, var1);
        isPaintAlert = false;
        this.gameNC = null;
        this.gameNB = null;
        super.center = null;
        this.resetButton();
    }

    private static void gameGC() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCO, 110721));
        var0.addElement(new Command(mResources.gameCP, 110722));
        var0.addElement(new Command(mResources.gameCQ, 110723));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameAA(Party var0) {
        MyVector var1;
        (var1 = new MyVector()).addElement(new Command(mResources.gameSH, 110701));
        if (!var0.isLock) {
            var1.addElement(new Command(mResources.gameSJ, 110702));
        } else {
            var1.addElement(new Command(mResources.gameSK, 110703));
        }

        GameCanvas.menu.gameAA(var1);
    }

    private void gameGD() {
        isPaintAlert = false;
        this.gameNC = null;
        this.gameNB = null;
        super.center = null;
        this.resetButton();
    }

    private void gameAF(String var1) {
        NinjaUtil.gameAB(var1);
        isPaintAlert = false;
        this.gameNC = null;
        this.gameNB = null;
        super.center = null;
        this.resetButton();
    }

    private static void gameGE() {
        GameCanvas.gameAJ();
        Service.gI().upgradeItem(itemUpGrade, arrItemUpGrade, gameKY);
    }

    private static void gameAD(Item var0) {
        GameCanvas.gameAJ();
        Service.gI().saleItem(var0.indexUI, 1);
    }

    private void gameGF() {
        indexMenu = 0;
        isPaintInfoMe = false;
        this.resetButton();
        if (currentCharViewInfo.charID == Char.getMyChar().charID) {
            this.gameCF();
        }

    }

    private void gameGG() {
        if (this.gamePJ >= 0 && this.gamePJ < gameNZ.length) {
            Skill var1 = gameNZ[this.gamePJ];
            this.gameAA(var1, false, true);
        }
    }

    private static void gameAE(Item var0) {
        String var1;
        if (!(var1 = GameCanvas.inputDlg.tfInput.getText()).trim().equals("")) {
            boolean var2 = false;

            int var4;
            try {
                var4 = Integer.parseInt(var1);
            } catch (Exception var3) {
                GameCanvas.gameAJ();
                return;
            }

            if (var4 <= 0) {
                GameCanvas.gameAJ();
            } else if (var4 > var0.quantity) {
                GameCanvas.setText(mResources.gamePR);
            } else {
                GameCanvas.gameAJ();
                GameCanvas.gameAA(mResources.gamePI, new Command(mResources.gameCH, 11058, var0), new Command(mResources.gameCU, 1));
            }
        }
    }

    private static void gameAF(Item var0) {
        String var1;
        if ((var1 = GameCanvas.inputDlg.tfInput.getText()).trim().equals("")) {
            GameCanvas.msgdlg.gameAA(mResources.gamePN, (Command) null, new Command(mResources.gameBH, 1), (Command) null);
            GameCanvas.currentDialog = GameCanvas.msgdlg;
        } else {
            boolean var2 = false;

            int var4;
            try {
                var4 = Integer.parseInt(var1);
            } catch (Exception var3) {
                GameCanvas.gameAJ();
                return;
            }

            if (var4 <= 0) {
                GameCanvas.gameAJ();
            } else {
                Service.gI().buyItem(var0.typeUI, var0.indexUI, var4);
                GameCanvas.gameAJ();
            }
        }
    }

    private void gameGH() {
        int var1 = indexSelect;
        this.resetButton();
        this.gameMZ = var1;
        Service.gI().openUIZone();
    }

    private static void gameAG(Item var0) {
        MyVector var1 = new MyVector();

        for (int var2 = 1; var2 < mResources.gameRM[3].length; ++var2) {
            var1.addElement(new Command(mResources.gameRM[3][var2], 110531, var0));
        }

        GameCanvas.menu.gameAA(var1);
    }

    private static void gameGI() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameTM + (ChatManager.blockGlobalChat ? mResources.gameBQ : mResources.gameBR), 120061));
        var0.addElement(new Command(mResources.gameTN + (ChatManager.blockPrivateChat ? mResources.gameBQ : mResources.gameBR), 120062));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameGJ() {
        MyVector var0 = new MyVector();

        for (int var1 = 0; var1 < ChatManager.gameAD().chatTabs.size(); ++var1) {
            ChatTab var2 = (ChatTab) ChatManager.gameAD().chatTabs.elementAt(var1);
            var0.addElement(new Command(var2.ownerName, 12001, new Integer(var1)));
        }

        var0.addElement(new Command(mResources.gameUQ, 12006));
        var0.addElement(new Command(mResources.gameUR, 12008));
        GameCanvas.menu.gameAA(var0);
        gameDM = true;
    }

    private void gameGK() {
        ChatTab var1 = ChatManager.gameAD().gameAE();
        isPaintAlert = true;
        isPaintMessage = true;
        this.gameNA = true;
        gameAB(175, 200);
        if (GameCanvas.h - popupH < 40 && !GameCanvas.isTouch) {
            popupH -= 52;
        }

        super.right = new Command(mResources.gameBH, 11066);
        super.left = super.center = null;
        if (!GameCanvas.isTouch) {
            this.gameGL();
        } else {
            super.left = new Command(mResources.gameCW, 12005);
        }

        if (var1.type == 2) {
            super.center = new Command(mResources.gameBI, 120051, var1);
        }

        ChatTextField.gameAA().center = null;
        this.gameNC = var1.ownerName;
        this.gameNB = var1.contents;
        ChatManager.gameAD().gameAE(var1.ownerName);
        if (var1.type == 1) {
            ChatManager.isMessagePt = false;
        }

        if (var1.type == 4) {
            ChatManager.isMessageClan = false;
        }

        this.gameBI();
    }

    private void gameGL() {
        ChatTab var1;
        if ((var1 = ChatManager.gameAD().gameAE()).type == 0) {
            ChatTextField.gameAA().setText(mResources.gameTI[0]);
        }

        if (var1.type == 1) {
            ChatTextField.gameAA().setText(mResources.gameTJ[0]);
        }

        if (var1.type == 2) {
            ChatTextField.gameAA().setText(var1.ownerName);
        }

        if (var1.type == 3) {
            ChatTextField.gameAA().setText(mResources.gameTK[0]);
        }

        if (var1.type == 4) {
            ChatTextField.gameAA().setText(mResources.gameTL[0]);
        }

    }

    private void gameGM() {
        try {
            GameMidlet.instance.platformRequest(this.gameTB);
        } catch (ConnectionNotFoundException var1) {
            var1.printStackTrace();
        }
    }

    private static void gameGN() {
        GameCanvas.gameAJ();
        Service.gI().useItem(indexSelect);
    }

    private static void gameGO() {
        Service.gI().boxSort();
    }

    private static void gameGP() {
        Service.gI().createParty();
    }

    private static void gameGQ() {
        indexRow = 0;
        Service.gI().openFindParty();
    }

    private static void gameGR() {
        Party var0;
        if (indexRow >= 0 && indexRow < vPtMap.size() && (var0 = (Party) vPtMap.elementAt(indexRow)) != null) {
            Service.gI().pleaseInputParty(var0.name);
        }

    }

    private static void gameAM(int var0) {
        switch (var0) {
            case 1:
                Service.gI().changePk(0);
                return;
            case 2:
                Service.gI().changePk(1);
                return;
            case 3:
                Service.gI().changePk(3);
            default:
        }
    }

    private static void gameGS() {
        String var0;
        if ((var0 = GameCanvas.inputDlg.tfInput.getText()).trim().equals("")) {
            GameCanvas.gameAJ();
        } else {
            boolean var1 = false;

            int var3;
            try {
                var3 = Integer.parseInt(var0);
            } catch (Exception var2) {
                GameCanvas.gameAJ();
                return;
            }

            if (var3 <= 0) {
                GameCanvas.gameAJ();
            } else if (Char.getMyChar().xu != 0 && var3 <= Char.getMyChar().xu) {
                Service.gI().boxCoinIn(var3);
                GameCanvas.gameAJ();
            } else {
                GameCanvas.setText(mResources.gamePP);
            }
        }
    }

    private void gameGT() {
        String var1;
        if ((var1 = GameCanvas.inputDlg.tfInput.getText()).trim().equals("")) {
            GameCanvas.gameAJ();
        } else {
            boolean var2 = false;

            int var4;
            try {
                var4 = Integer.parseInt(var1);
            } catch (Exception var3) {
                GameCanvas.gameAJ();
                return;
            }

            if (var4 <= 0) {
                GameCanvas.gameAJ();
            } else if (Char.getMyChar().xu != 0 && var4 <= Char.getMyChar().xu) {
                this.coinTrade += var4;
                Char var10000 = Char.getMyChar();
                var10000.xu -= var4;
                GameCanvas.gameAJ();
            } else {
                GameCanvas.setText(mResources.gamePO);
            }
        }
    }

    private static void gameGU() {
        String var0;
        if ((var0 = GameCanvas.inputDlg.tfInput.getText()).trim().equals("")) {
            GameCanvas.gameAJ();
        } else {
            boolean var1 = false;

            int var3;
            try {
                var3 = Integer.parseInt(var0);
            } catch (Exception var2) {
                GameCanvas.gameAJ();
                return;
            }

            if (var3 <= 0) {
                GameCanvas.gameAJ();
            } else if (Char.getMyChar().xuInBox != 0 && var3 <= Char.getMyChar().xuInBox) {
                Service.gI().boxCoinOut(var3);
                GameCanvas.gameAJ();
            } else {
                GameCanvas.setText(mResources.gamePQ);
            }
        }
    }

    private static void gameGV() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameSQ[0], 11000651));
        var0.addElement(new Command(mResources.gameSQ[1], 11000652));
        var0.addElement(new Command(mResources.gameSQ[3], 11000653));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameGW() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameSR[6], 1100067));
        var0.addElement(new Command(mResources.gameSR[3], 1100062));
        var0.addElement(new Command(mResources.gameSR[1], 1100063));
        var0.addElement(new Command(mResources.gameSR[2], 1100064));
        var0.addElement(new Command(mResources.gameSR[0], 1100065));
        var0.addElement(new Command(mResources.gameSR[7], 1100068));
        GameCanvas.menu.gameAA(var0);
    }

    private void gameGX() {
        this.gameCA();
        if (super.right != null) {
            super.right.gameAA();
        }

    }

    private void gameGY() {
        this.gameCA();
        if (super.left != null) {
            super.left.gameAA();
        }

    }

    private void gameAN(int var1) {
        if (Char.getMyChar().charFocus != null && !Char.getMyChar().charFocus.gameBB()) {
            switch (var1) {
                case 1:
                    Service.gI().addParty(Char.getMyChar().charFocus.cName);
                    return;
                case 2:
                    Service.gI().tradeInvite(Char.getMyChar().charFocus.charID);
                    return;
                case 3:
                    Service.gI().testInvite(Char.getMyChar().charFocus.charID);
                    return;
                case 4:
                    Service.gI().addCuuSat(Char.getMyChar().charFocus.charID);
                    return;
                case 5:
                    this.gameAG(Char.getMyChar().charFocus.charID);
                    return;
                case 6:
                    Service.gI().addFriend(Char.getMyChar().charFocus.cName);
                    return;
                case 7:
                    Service.gI().viewInfo(Char.getMyChar().charFocus.cName, 0);
                    gI().resetButton();
                    return;
                case 8:
                    Service.gI().clanInvite(Char.getMyChar().charFocus.charID);
                    return;
                case 9:
                    Service.gI().clanPlease(Char.getMyChar().charFocus.charID);
                    return;
                case 10:
                    Char.isAResuscitate = !Char.isAResuscitate;
                    Char.aCID = Char.getMyChar().charFocus.charID;
            }
        }

    }

    public final void gameAG(int var1) {
        if (System.currentTimeMillis() - this.gameUE > 500L) {
            Service.gI().buffLive(var1);
            this.gameUE = System.currentTimeMillis();
        }

        if ((TileMap.gameAA(Char.getMyChar().cx, Char.getMyChar().cy) & 2) == 2) {
            Char.getMyChar().gameAA((SkillPaint) sks[49], 0);
        } else {
            Char.getMyChar().gameAA((SkillPaint) sks[49], 1);
        }
    }

    private static void gameGZ() {
        GameCanvas.gameAK();
        ChatManager.gameAD();
        ChatManager.gameAF();
        Session_ME.gI().gameAC();
        long var2 = 1000L;
        boolean var0 = true;
        Object var4 = null;
        Timer.idAction = 9999;
        Timer.timeExecute = System.currentTimeMillis() + var2;
        Timer.isON = true;
    }

    private static void gameHA() {
        GameCanvas.gameAA(mResources.gameEW, new Command(mResources.gameCH, 1100041), new Command(mResources.gameCU, 1));
    }

    private static void gameHB() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameSE[1], 110381));
        var0.addElement(new Command(mResources.gameSE[2], 110382));
        var0.addElement(new Command(mResources.gameSE[3], 110383));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameHC() {
        Service.gI().bagSort();
    }

    private void gameHD() {
        Item var1 = arrItemTradeMe[indexSelect];
        arrItemTradeMe[indexSelect] = null;
        Char.getMyChar().arrItemBag[var1.indexUI] = var1;
        super.left = super.center = null;
        this.gameBC();
    }

    private void gameHE() {
        Item var1 = itemSplit;
        Char.getMyChar().arrItemBag[var1.indexUI] = var1;
        itemSplit = null;
        if (!gameMJ && !gameMK && !gameML && !gameMR && arrItemSplit != null) {
            for (int var2 = 0; var2 < arrItemSplit.length; ++var2) {
                arrItemSplit[var2] = null;
            }
        }

        super.left = super.center = null;
        this.gameBC();
    }

    private void gameHF() {
        Item var1 = itemUpGrade;
        Char.getMyChar().arrItemBag[var1.indexUI] = var1;
        itemUpGrade = null;
        super.left = super.center = null;
        this.gameBC();
    }

    private void gameHG() {
        Item var1 = arrItemConvert[indexSelect];
        Char.getMyChar().arrItemBag[var1.indexUI] = var1;
        arrItemConvert[indexSelect] = null;
        super.left = super.center = null;
        this.gameBC();
    }

    private void gameHH() {
        Service.gI().tradeAccept();
        this.typeTrade = 2;
        if (gI().typeTrade >= 2 && gI().typeTradeOrder >= 2) {
            InfoDlg.gameAA();
        }

    }

    private void gameHI() {
        Service.gI().tradeItemLock(this.coinTrade, arrItemTradeMe);
        this.typeTrade = 1;
        if (gI().typeTrade == 1 && gI().typeTradeOrder == 1) {
            gI().timeTrade = (int) (System.currentTimeMillis() / 1000L + 5L);
        }

        super.left = this.gameRR;
    }

    private void gameHJ() {
        Item var1 = arrItemTradeMe[indexSelect];
        this.gameAA((int) 3, (Item) var1);
    }

    private void gameHK() {
        if (Char.getMyChar().arrItemBag[indexSelect].isLock) {
            GameCanvas.msgdlg.gameAA(mResources.gameNF, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
            GameCanvas.currentDialog = GameCanvas.msgdlg;
        } else {
            for (int var1 = 0; var1 < arrItemTradeMe.length; ++var1) {
                if (arrItemTradeMe[var1] == null) {
                    arrItemTradeMe[var1] = Char.getMyChar().arrItemBag[indexSelect];
                    Char.getMyChar().arrItemBag[indexSelect] = null;
                    super.left = super.center = null;
                    this.gameBC();
                    return;
                }
            }

            GameCanvas.msgdlg.gameAA(mResources.gameNN, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
            GameCanvas.currentDialog = GameCanvas.msgdlg;
        }
    }

    private void gameHL() {
        MyVector var1;
        (var1 = new MyVector()).addElement(this.gameSB);
        var1.addElement(this.gameRY);
        GameCanvas.menu.gameAA(var1);
    }

    private void gameHM() {
        MyVector var1;
        (var1 = new MyVector()).addElement(this.gameRZ);
        if (this.typeTrade == 0) {
            var1.addElement(this.gameRQ);
        } else if (this.typeTrade == 1 && this.typeTradeOrder >= 1 && (long) this.timeTrade - System.currentTimeMillis() / 1000L <= 0L) {
            var1.addElement(this.gameRR);
        }

        GameCanvas.menu.gameAA(var1);
    }

    private static void gameHN() {
        try {
            Item var0;
            if ((var0 = gameAK(3)).template.gender != 2 && var0.template.gender != Char.getMyChar().cgender) {
                GameCanvas.msgdlg.gameAA(mResources.gameQF, (Command) null, new Command(mResources.gameBH, 1), (Command) null);
                GameCanvas.currentDialog = GameCanvas.msgdlg;
            } else if (var0.template.level > Char.getMyChar().clevel) {
                GameCanvas.msgdlg.gameAA(mResources.gameQH, (Command) null, new Command(mResources.gameBH, 1), (Command) null);
                GameCanvas.currentDialog = GameCanvas.msgdlg;
            } else if ((var0.isTypeBody() || var0.isTypeMounts()) && !var0.isLock) {
                GameCanvas.gameAA(mResources.gamePH, new Command(mResources.gameCH, 11051, (Object) null), new Command(mResources.gameCU, 1));
            } else if (var0.template.id != 35 && var0.template.id != 37) {
                if (var0.template.id == 514) {
                    GameCanvas.input2Dlg.gameAA("Đến: ", "Lời chúc: ");
                    GameCanvas.input2Dlg.gameAA("Chúc tết", new Command(mResources.gameBH, GameCanvas.instance, 8882, (Object) null), new Command("Gửi", GameCanvas.instance, 1608, (Object) null), 0, 0);
                } else if (var0.template.id == 515) {
                    GameCanvas.input2Dlg.gameAA("Đến: ", "Lời chúc: ");
                    GameCanvas.input2Dlg.gameAA("Chúc tết", new Command(mResources.gameBH, GameCanvas.instance, 8882, (Object) null), new Command("Gửi", GameCanvas.instance, 16081, (Object) null), 0, 0);
                } else {
                    Service.gI().useItem(indexSelect);
                }
            } else {
                MyVector var1 = new MyVector();

                for (int var2 = 0; var2 < 3; ++var2) {
                    var1.addElement(new Command(mResources.gameRM[var2][0], 11052, var0));
                }

                var1.addElement(new Command(mResources.gameRM[3][0], 11053, var0));
                var1.addElement(new Command(mResources.gameRM[4][0], 11054));
                GameCanvas.menu.gameAA(var1);
            }
        } catch (Exception var3) {
        }
    }

    private static void gameHO() {
        if (Char.getMyChar().arrItemBag[indexSelect] != null && Char.getMyChar().arrItemBag[indexSelect].quantity > 1) {
            GameCanvas.inputDlg.gameAA(mResources.gameUL, new Command(mResources.gameCX, GameCanvas.instance, 88835, String.valueOf(indexSelect)), 1);
        }
    }

    private static void gameHP() {
        if (Char.getMyChar().arrItemBag[indexSelect] != null) {
            if (Char.getMyChar().arrItemBag[indexSelect].isLock) {
                InfoMe.gameAA(mResources.gameRT);
            }
            Service.gI().throwItem(indexSelect);

        }
    }

    private void gameHQ() {
        if (gameHL == 1) {
            Item var1 = gameAK(3);
            if (GameCanvas.gameAJ) {
                this.gameAA(3, var1, this.gameTU, (Command) null);
                return;
            }

            this.gameAA(3, var1, (Command) null, (Command) null);
        }

    }

    private static void fieldHO() {
        Item var0;
        if ((var0 = gameAK(3)) != null) {
            if (var0.template.level > Char.getMyChar().clevel) {
                GameCanvas.msgdlg.gameAA("Không thể bán trang bị đã nâng cấp", (Command) null, new Command("Quy định", 1), (Command) null);
                GameCanvas.currentDialog = GameCanvas.msgdlg;
                return;
            }

            (new Thread(new MoAll(var0))).start();
        }

    }

    private void gameHR() {
        MyVector var1;
        (var1 = new MyVector()).addElement(this.gameTU);

        if (Char.getMyChar().arrItemBag[indexSelect] != null) {
            Item var2;
            if (Code.fieldAA((int) (var2 = Char.getMyChar().arrItemBag[indexSelect]).template.id)) {
                var1.addElement(new Command("Tắt Tự Dùng", 110263));
            } else {
                var1.addElement(new Command("Tự Dùng", 110262));
            }
            if (Code.gameAF(var2)) {
                var1.addElement(new Command("Tắt Luyện Ngọc", 11000813));
            } else if ((var2.template.id == 652
                    || var2.template.id == 653
                    || var2.template.id == 654
                    || var2.template.id == 655) && var2.upgrade >= 2) {
                var1.addElement(new Command("Luyện Ngọc", 11000812));
            }
            if (Code.fieldAA(var2)) {
                var1.addElement(new Command("Tắt Tự TL", 110265));
            } else if (var2.fieldAU() >= 0 && var2.fieldAU() < 9) {
                var1.addElement(new Command("Tự TL", 110264));
            }

        }

        if (Char.getMyChar().arrItemBag[indexSelect] != null && Char.getMyChar().arrItemBag[indexSelect].quantity > 1) {
            var1.addElement(new Command("Mở all", 110261));

            var1.addElement(this.gameTY);
        }
        var1.addElement(this.gameTX);
        var1.addElement(new Command("Bán", Char.getMyChar().arrItemBag[indexSelect].quantity > 1 ? 11072 : 11073));

        //   var1.addElement(new Command(mResources.gameCD, 110221));
        GameCanvas.menu.gameAA(var1);
    }

    private static void gameHS() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCS, 140221));
        var0.addElement(new Command(mResources.gameCT, 140222));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameHT() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCS, 110201));
        var0.addElement(new Command(mResources.gameCT, 110202));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameHU() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCS, 110181));
        var0.addElement(new Command(mResources.gameCT, 110182));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameHV() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCS, 130021));
        var0.addElement(new Command(mResources.gameCT, 130022));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameHW() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCS, 110161));
        var0.addElement(new Command(mResources.gameCT, 110162));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameHX() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCS, 110141));
        var0.addElement(new Command(mResources.gameCT, 110142));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameHY() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCS, 110121));
        var0.addElement(new Command(mResources.gameCT, 110122));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameHZ() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCS, 110101));
        var0.addElement(new Command(mResources.gameCT, 110102));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameKA() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCS, 110081));
        var0.addElement(new Command(mResources.gameCT, 110082));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameKB() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameCS, 110051));
        var0.addElement(new Command(mResources.gameCT, 110052));
        GameCanvas.menu.gameAA(var0);
    }

    private static void gameAB(byte var0) {
        Item var1 = gameAK(var0);
        Service.gI().buyItem(var1.typeUI, var1.indexUI, 1);
    }

    private void gameAC(byte var1) {
        gameAA(gameAK(var1));
    }

    private void gameAD(byte var1) {
        Item var2 = gameAK(var1);
        this.gameAA(var2.typeUI, var2);
    }

    private static void gameKC() {
        String var0;
        if ((var0 = GameCanvas.inputDlg.tfInput.getText()).trim().equals("")) {
            GameCanvas.setText(mResources.gameNQ);
        } else {
            boolean var1 = false;

            int var3;
            try {
                var3 = Integer.parseInt(var0);
            } catch (Exception var2) {
                GameCanvas.gameAJ();
                return;
            }

            if (var3 <= 0) {
                GameCanvas.gameAJ();
            } else if (Char.getMyChar().pPoint != 0 && var3 <= Char.getMyChar().pPoint) {
                Service.gI().upPotential(gameHL - 1, var3);
                GameCanvas.gameAJ();
            } else {
                GameCanvas.setText(mResources.gameNP);
            }
        }
    }

    private static void gameKD() {
        String var0;
        if ((var0 = GameCanvas.inputDlg.tfInput.getText()).trim().equals("")) {
            GameCanvas.setText(mResources.gameNQ);
        } else {
            boolean var1 = false;

            int var3;
            try {
                var3 = Integer.parseInt(var0);
            } catch (Exception var2) {
                GameCanvas.gameAJ();
                return;
            }

            if (Char.getMyChar().sPoint != 0 && var3 <= Char.getMyChar().sPoint) {
                Service.gI().upSkill(Char.getMyChar().nClass.skillTemplates[indexSelect].id, var3);
                GameCanvas.gameAJ();
            } else {
                GameCanvas.setText(mResources.gameNQ);
                GameCanvas.currentDialog = GameCanvas.msgdlg;
            }
        }
    }

    private static void gameKE() {
        String var0;
        if ((var0 = GameCanvas.inputDlg.tfInput.getText()).trim().equals("")) {
            GameCanvas.setText(mResources.gameNV);
        } else {
            Service.gI().addFriend(var0);
            GameCanvas.gameAJ();
        }
    }

    public final void perform(int var1, Object var2) {
        if (var1 == 9999) {
            GameCanvas.instance.gameAN();
        }

    }

    public final void gameBL() {
        this.gameBJ();
        indexMenu = 3;
        isPaintInfoMe = true;
        gameAB(175, 200);
        super.right = this.gameSE;
    }

    public static void gameBM() {
        gameUH = new int[vMob.size()];
        gameUI = new int[vMob.size()];

        int var0;
        Mob var1;
        for (var0 = 0; var0 < vMob.size(); ++var0) {
            if (var0 != gameUG) {
                var1 = (Mob) vMob.elementAt(var0);
                int var2;
                int var3;
                if (!isAllmap) {
                    var2 = Math.abs(var1.xFirst - pointCenterX);
                    gameUH[var0] = var2;
                    var3 = Math.abs(var1.yFirst - pointCenterY);
                    gameUI[var0] = var3;
                } else {
                    var2 = Math.abs(var1.xFirst - Char.getMyChar().cx);
                    gameUH[var0] = var2;
                    var3 = Math.abs(var1.yFirst - Char.getMyChar().cy);
                    gameUI[var0] = var3;
                    rangeSearch = 700;
                }
            }
        }

        if (auto == 1 && Char.getMyChar().mobFocus == null && Char.getMyChar().npcFocus == null && Char.getMyChar().mobFocus == null && Char.getMyChar().statusMe != 14 && Char.getMyChar().cMP > 0 && Char.getMyChar().itemFocus == null) {
            if (System.currentTimeMillis() - gameUF + 2000L >= 0L) {
                if (!isAllmap && Char.getMyChar().mobFocus != null && (Char.getMyChar().cx < pointCenterX - rangeSearch || Char.getMyChar().cy > pointCenterX + rangeSearch || Char.getMyChar().cy < pointCenterY - rangeSearch || Char.getMyChar().cy > pointCenterY + rangeSearch)) {
                    Char.getMyChar().cx = pointCenterX;
                    Char.getMyChar().cy = pointCenterY;
                }

                for (var0 = 0; var0 < vMob.size(); ++var0) {
                    if (var0 != gameUG && gameUH[var0] < rangeSearch && gameUI[var0] < rangeSearch && Char.getMyChar().mobFocus == null && (var1 = (Mob) vMob.elementAt(var0)).status != 0 && var1.status != 1 && var1.levelBoss != 3) {
                        ServerEffect.gameAA(141, Char.getMyChar().cx, Char.getMyChar().cy, 2);
                        Char.getMyChar().cx = var1.xFirst;
                        Char.getMyChar().cy = var1.yFirst;
                        Char.getMyChar().statusMe = 4;
                        Char.getMyChar().mobFocus = var1;
                        ServerEffect.gameAA(141, Char.getMyChar().cx, Char.getMyChar().cy, 2);
                        Char.getMyChar().cxSend = var1.xFirst;
                        Char.getMyChar().cySend = var1.yFirst;
                        Service.gI().sendAttackMobFast(var1.mobId);
                        gameUG = var0;
                        gameUF = System.currentTimeMillis();
                    }
                }
            }

        }
    }

    private void gameBO(mGraphics var1) {
        if (gameHU) {
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            gameAA(var1, mResources.gameAV, false);
            gameTD = popupX + 5;
            gameTE = popupY + 40;
            if (vList.size() == 0) {
                mFont.tahoma_7_white.gameAA(var1, mResources.gameSO, popupX + popupW / 2, popupY + 40, 2);
                return;
            }

            var1.gameAA(-16770791);
            var1.gameAC(gameTD - 2, gameTE - 2, popupW - 6, gameHK * 3 + 8);
            gameAB(var1);
            scrMain.gameAA(vList.size(), gameHK, gameTD, gameTE, popupW - 3, gameHK * 3 + 4, true, 1);
            scrMain.gameAA(var1, gameTD, gameTE, popupW - 3, gameHK * 3 + 6);
            gameHM = vList.size();

            for (int var4 = 0; var4 < vList.size(); ++var4) {
                Ranked var2 = null;

                try {
                    var2 = (Ranked) vList.elementAt(var4);
                } catch (Exception var3) {
                }

                if (var2 != null) {
                    if (indexRow == var4) {
                        var1.gameAA(Paint.COLORLIGHT);
                        var1.gameAC(gameTD + 2, gameTE + indexRow * gameHK + 2, popupW - 15, gameHK - 4);
                        var1.gameAA(16777215);
                        var1.gameAB(gameTD + 2, gameTE + indexRow * gameHK + 2, popupW - 15, gameHK - 4);
                    } else {
                        var1.gameAA(Paint.COLORBACKGROUND);
                        var1.gameAC(gameTD + 2, gameTE + var4 * gameHK + 2, popupW - 15, gameHK - 4);
                        var1.gameAA(13932896);
                        var1.gameAB(gameTD + 2, gameTE + var4 * gameHK + 2, popupW - 15, gameHK - 4);
                    }

                    mFont.tahoma_7_yellow.gameAA(var1, var2.name, gameTD + (popupW - 10) / 2 - popupW / 4, gameTE + var4 * gameHK + gameHK / 2 - 10, 2);
                    mFont.tahoma_7_yellow.gameAA(var1, "-", gameTD + (popupW - 10) / 2, gameTE + var4 * gameHK + gameHK / 2 - 10, 2);
                    mFont.tahoma_7_yellow.gameAA(var1, mResources.gameAU + ": " + var2.id, gameTD + (popupW - 10) / 2 + popupW / 4, gameTE + var4 * gameHK + gameHK / 2 - 10, 2);
                    mFont.tahoma_7_white.gameAA(var1, var2.stt, gameTD + popupW / 2, gameTE + var4 * gameHK + gameHK / 2 + 5, 2);
                }
            }
        }

    }

    private static void gameKF() {
        Service.gI().luckyDraw((short) 102, "", gameGP);
    }

    private void gameKG() {
        this.gameUT = true;
        Service.gI().luckyDraw((short) 101, "", gameGP);
    }

    private static void gameKH() {
        Short var0 = new Short((short) 1);
        GameCanvas.inputDlg.gameAA(mResources.gameAS, new Command(mResources.gameAT, GameCanvas.instance, 1700, var0), 1);
    }

    public final void gameAA(String var1, short var2, String var3, short var4, String var5, short var6, String var7, String var8, byte var9) {
        InfoDlg.gameAB();
        gameMP = true;
        this.gameNA = true;
        indexRow = 0;
        gameAB(175, 200);
        super.left = null;
        super.center = new Command(mResources.gameAT, 1701);
        super.right = new Command(mResources.gameBH, 1702);
        gameGP = var9;
        this.gameNC = var1;
        this.gameUK = var2;
        this.gameUO = var3;
        this.gameUL = var4;
        this.gameUP = var5;
        this.gameUM = var6;
        this.gameUQ = var7;
        this.gameUR = var8;
        var1 = this.gameUR == "" ? "" : mResources.gameAQ + this.gameUR + mResources.gamePA;
        var1 = "c3" + this.gameUO + "\n" + mResources.gameAR + var6 + "\n" + var1 + "\n\n" + "c0" + this.gameUQ;
        this.gameNB = mFont.tahoma_7.gameAA(var1, popupW - 30);
        this.gameUS = System.currentTimeMillis();
        this.gameUN = gameAA(this.gameUS, this.gameUK);
        this.gameUT = false;
    }

    private static String gameAA(long var0, int var2) {
        Object var3 = null;
        long var4;
        if ((var4 = (var0 + (long) (var2 * 1000) - System.currentTimeMillis()) / 1000L) <= 0L) {
            return "";
        } else {
            long var6 = var4 / 60L;
            return var6 > 0L ? (var6 < 10L ? (var4 % 60L >= 0L && var4 % 60L < 10L ? "0" + var6 + ":" + "0" + var4 % 60L : "0" + var6 + ":" + var4 % 60L) : (var4 % 60L >= 0L && var4 % 60L < 10L ? var6 + ":" + "0" + var4 % 60L : var6 + ":" + var4 % 60L)) : (var4 < 10L ? "0" + var4 + "s" : var4 + "s");
        }
    }

    private void gameKI() {
        gameMP = false;
        this.gameNC = null;
        this.gameNB = null;
        super.center = null;
        super.left = null;
        super.right = null;
        this.resetButton();
    }

    private void gameBP(mGraphics var1) {
        if (this.gameNB != null && gameMP) {
            if (!this.gameUT) {
                if (this.gameUM > 1) {
                    this.gameUN = gameAA(this.gameUS, this.gameUK);
                }

                if (this.gameUN == "") {
                    this.gameKG();
                }

                long var6;
                if ((var6 = (this.gameUS + (long) (this.gameUK * 1000) - System.currentTimeMillis()) / 1000L) > 8L && var6 % 5L == 0L) {
                    this.gameKG();
                }

                if (var6 <= 10L) {
                    super.center = null;
                }

                if (var6 <= 20L) {
                    this.gameUU = mFont.tahoma_7b_red;
                } else {
                    this.gameUU = mFont.tahoma_7b_yellow;
                }
            }

            gameAB(var1);
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            if (this.gameNC != null) {
                gameAA(var1, this.gameNC, isPaintMessage);
            }

            gameTD = popupX + 15;
            gameTE = popupY + 15;
            if (this.gameNC != null) {
                gameTE += 10;
            }

            this.gameUU.gameAA(var1, this.gameUN, popupX + popupW / 2, gameTE + 12, 2);
            mFont.tahoma_7_yellow.gameAA(var1, mResources.gameAP, popupX + popupW / 2, gameTE + 30, 2);
            short var10001 = this.gameUL;
            int var10002 = popupX + popupW / 2;
            int var10003 = gameTE + 42;
            int var10004 = popupW / 2;
            String var5 = this.gameUL + "." + this.gameUP + "%";
            boolean var2 = true;
            int var4 = var10004;
            int var3 = var10003;
            int var7 = var10002;
            short var10 = var10001;
            var1.gameAA(0);
            var1.gameAC(var7 - var4 / 2, var3, var4, 12);
            int var11;
            if ((var11 = var10 * var4 / 100) < 1) {
                var11 = 1;
            }

            var1.gameAD(var7 - var4 / 2, var3, var11, 12);
            var1.gameAA(16711680);
            var1.gameAC(var7 - var4 / 2, var3, var4, 12);
            gameAB(var1);
            mFont.tahoma_7_yellow.gameAA(var1, var5, popupX + popupW / 2, var3, 2);
            gameHM = this.gameNB.size();
            scrMain.gameAA(gameHM, 12, popupX, gameTE + 48, popupW, popupH - 42 - (this.gameNC != null ? 10 : 0), true, 1);
            scrMain.gameAA(var1);
            this.gameTL = gameTE + 48;
            mFont var8 = mFont.tahoma_7_white;

            String var9;
            for (var3 = 0; var3 < this.gameNB.size() && (var9 = (String) this.gameNB.elementAt(var3)) != null && this.gameNB != null && var8 != null; ++var3) {
                if (var9.startsWith("c")) {
                    if (var9.startsWith("c0")) {
                        var9 = var9.substring(2);
                        var8 = mFont.tahoma_7_white;
                    } else if (var9.startsWith("c1")) {
                        var9 = var9.substring(2);
                        var8 = mFont.tahoma_7b_yellow;
                    } else if (var9.startsWith("c2")) {
                        var9 = var9.substring(2);
                        var8 = mFont.tahoma_7b_white;
                    } else if (var9.startsWith("c3")) {
                        var9 = var9.substring(2);
                        var8 = mFont.tahoma_7_yellow;
                    } else if (var9.startsWith("c4")) {
                        var9 = var9.substring(2);
                        var8 = mFont.tahoma_7b_red;
                    } else if (var9.startsWith("c5")) {
                        var9 = var9.substring(2);
                        var8 = mFont.tahoma_7_red;
                    } else if (var9.startsWith("c6")) {
                        var9 = var9.substring(2);
                        var8 = mFont.tahoma_7_grey;
                    } else if (var9.startsWith("c7")) {
                        var9 = var9.substring(2);
                        var8 = mFont.tahoma_7b_blue;
                    } else if (var9.startsWith("c8")) {
                        var9 = var9.substring(2);
                        var8 = mFont.tahoma_7_blue;
                    } else if (var9.startsWith("c9")) {
                        var9 = var9.substring(2);
                        var8 = mFont.tahoma_7_green;
                    }
                }

                var8.gameAA(var1, var9, popupX + popupW / 2, this.gameTL += 12, 2);
            }

        }
    }

    public final void update() {
        Session_ME.gameAP = false;
        Session_ME.fieldAE();
        TileMap.fieldBE = false;
        Code.fieldAA.fieldAA();
        super.update();
    }

    public final void gameBN() {
        this.gameUV = new byte[]{-1, -1, -1, -1, -1, -1};
    }

    private void gameKJ() {
        if (GameCanvas.keyHold[4]) {
            this.gameAE((byte) 0);
            GameCanvas.gameAI();
        } else if (GameCanvas.keyHold[2]) {
            this.gameAE((byte) 1);
            GameCanvas.gameAI();
        } else {
            if (GameCanvas.keyHold[6]) {
                this.gameAE((byte) 2);
                GameCanvas.gameAI();
            }

        }
    }

    private void gameAE(byte var1) {
        for (int var2 = 0; var2 < this.gameUV.length; ++var2) {
            if (var2 != this.gameUV.length - 1) {
                this.gameUV[var2] = this.gameUV[var2 + 1];
            } else {
                this.gameUV[var2] = var1;
                Service.gI().send_Captcha(var1);
            }
        }

    }

    private void gameBQ(mGraphics var1) {
        try {
            gameHM = 1;
            this.gameVA = popupW;
            this.gameVB = popupH;
            this.gameUY = popupX;
            this.gameUZ = popupY;
            this.gameVJ = this.gameUY + 25;
            this.gameVK = this.gameUZ + 60;
            this.gameVL = this.gameVA - 50;
            this.gameVM = 70;
            this.gameVE = this.gameVA - 49;
            this.gameVF = 10;
            this.gameVC = GameCanvas.hw - this.gameVE / 2;
            this.gameVD = this.gameVK + this.gameVM - this.gameVF;
            this.gameVI = 18;
            this.gameVG = GameCanvas.hw - (this.gameUX.size() - 1) * ((this.gameVI + 5) / 2);
            this.gameVH = this.gameUZ + this.gameVB - this.gameVI / 2 - 5;
            Clan_ThanThu var2;
            if (this.gameUX.size() > 0 && this.gameUW <= this.gameUX.size()) {
                var2 = (Clan_ThanThu) this.gameUX.elementAt(this.gameUW);
            } else {
                var2 = null;
            }

            if (var2 == null) {
                var1.gameAA(13606712);
                var1.gameAB(this.gameVJ - 1, this.gameVK - 1, this.gameVL + 1, this.gameVM + 1);
                var1.gameAD(this.gameVJ, this.gameVK, this.gameVL, this.gameVM);
                var1.gameAA(6425);
                var1.gameAC(this.gameVJ, this.gameVK, this.gameVL, this.gameVM);
            } else {
                mFont.tahoma_7b_white.gameAA(var1, var2.name, GameCanvas.hw, this.gameUZ + 35, 2);
                var1.gameAA(13606712);
                var1.gameAB(this.gameVJ - 1, this.gameVK - 1, this.gameVL + 1, this.gameVM + 1);
                var1.gameAD(this.gameVJ, this.gameVK, this.gameVL, this.gameVM);
                var1.gameAA(6425);
                var1.gameAC(this.gameVJ, this.gameVK, this.gameVL, this.gameVM);
                SmallImage.gameAA(var1, var2.idThanThu, this.gameVJ + this.gameVL / 2, this.gameVK + this.gameVM / 2 - 10, 0, 3);
                gameAB(var1);
                int var3;
                if (var2.time_aptrung >= 0) {
                    this.gameVO = Res.gameAA(var2.timeStartThanThu, var2.time_aptrung);
                    if (!this.gameVO.equals("")) {
                        mFont.tahoma_7_yellow.gameAA(var1, var2.str_trungno + " " + this.gameVO, this.gameVJ, this.gameVD + 15, 0);
                    } else {
                        --this.gameVN;
                        if (this.gameVN <= 0) {
                            Service.gI().requestClanItem();
                            this.gameVN = 100;
                        }
                    }
                } else {
                    for (var3 = 0; var3 < var2.stars; ++var3) {
                        SmallImage.gameAA(var1, 628, this.gameUY + 95 + var3 * 12 - var2.stars * 6, this.gameUZ + 50, 0, 3);
                    }

                    var3 = var2.curExp * this.gameVE / var2.maxExp;
                    var1.gameAA(2506246);
                    var1.gameAC(this.gameVC, this.gameVD, this.gameVE, this.gameVF);
                    var1.gameAA(371981);
                    var1.gameAC(this.gameVC, this.gameVD, var3, this.gameVF);
                    var1.gameAA(13606712);
                    var1.gameAB(this.gameVC, this.gameVD, this.gameVE, this.gameVF);
                    mFont.tahoma_7_white.gameAA(var1, var2.curExp + "/" + var2.maxExp, this.gameVC + this.gameVE / 2, this.gameVD, 2);

                    for (int var4 = 0; var4 < var2.vecInfo.size(); ++var4) {
                        String var6 = (String) var2.vecInfo.elementAt(var4);
                        mFont.tahoma_7_yellow.gameAA(var1, var6, this.gameVJ + this.gameVL / 2, this.gameVD + 15 + var4 * 10, 2);
                    }
                }

                for (var3 = 0; var3 < this.gameUX.size(); ++var3) {
                    Clan_ThanThu var7;
                    if (this.gameUX.size() > 0 && var3 <= this.gameUX.size()) {
                        var7 = (Clan_ThanThu) this.gameUX.elementAt(var3);
                    } else {
                        var7 = null;
                    }

                    if (var7 != null) {
                        var1.gameAA(0);
                        var1.gameAC(this.gameVG + var3 * (this.gameVI + 5) - this.gameVI / 2, this.gameVH - this.gameVI / 2, this.gameVI, this.gameVI);
                        SmallImage.gameAA(var1, 154, this.gameVG + var3 * (this.gameVI + 5), this.gameVH, 0, 3);
                        var1.gameAA(12281361);
                        var1.gameAB(this.gameVG + var3 * (this.gameVI + 5) - this.gameVI / 2, this.gameVH - this.gameVI / 2, this.gameVI, this.gameVI);
                        SmallImage.gameAA(var1, var7.idIconItem, this.gameVG + var3 * (this.gameVI + 5), this.gameVH, 0, 3);
                    }
                }

                var1.gameAA(16777215);
                var1.gameAB(this.gameVG + this.gameUW * (this.gameVI + 5) - this.gameVI / 2, this.gameVH - this.gameVI / 2, this.gameVI, this.gameVI);
            }
        } catch (Exception var5) {
            System.out.println("e:" + var5.toString());
        }
    }

    public final void gameAA(Clan_ThanThu var1) {
        this.gameUX.addElement(var1);
    }

    public final void gameBO() {
        this.gameUX.removeAllElements();
    }

    private void gameKK() {
        try {
            if (gameML && Char.getMyChar().arrItemBag[indexSelect].isTypeNgocKham()) {
                if (itemSplit == null) {
                    if (Char.getMyChar().arrItemBag[indexSelect].upgrade < 10) {
                        itemSplit = Char.getMyChar().arrItemBag[indexSelect];
                        Char.getMyChar().arrItemBag[indexSelect] = null;
                        return;
                    }

                    GameCanvas.msgdlg.gameAA(mResources.gameAB, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
                    GameCanvas.currentDialog = GameCanvas.msgdlg;
                    return;
                }

                if (arrItemSplit == null) {
                    arrItemSplit = new Item[24];
                }

                for (int var1 = 0; var1 < arrItemSplit.length; ++var1) {
                    if (arrItemSplit[var1] == null) {
                        arrItemSplit[var1] = Char.getMyChar().arrItemBag[indexSelect];
                        Char.getMyChar().arrItemBag[indexSelect] = null;
                        super.left = super.center = null;
                        this.gameBC();
                        return;
                    }

                    if (var1 == arrItemSplit.length - 1) {
                        GameCanvas.setText(mResources.gameNO);
                    }
                }

                return;
            }
        } catch (Exception var2) {
            GameCanvas.msgdlg.gameAA(mResources.gameAB, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
            GameCanvas.currentDialog = GameCanvas.msgdlg;
            var2.printStackTrace();
        }

    }

    private void gameKL() {
        Item var3;
        if (Char.getMyChar().arrItemBag[indexSelect].isTypeNgocKham()) {
            if (itemSplit == null) {
                itemSplit = Char.getMyChar().arrItemBag[indexSelect];
                Char.getMyChar().arrItemBag[indexSelect] = null;
            } else {
                var3 = Char.getMyChar().arrItemBag[indexSelect];
                Char.getMyChar().arrItemBag[indexSelect] = null;
                Char.getMyChar().arrItemBag[itemSplit.indexUI] = itemSplit;
                itemSplit = var3;
            }

            super.left = super.center = null;
            this.gameBC();
        } else if (Char.getMyChar().arrItemBag[indexSelect].isTypeBody()) {
            if (itemUpGrade == null) {
                itemUpGrade = Char.getMyChar().arrItemBag[indexSelect];
                Char.getMyChar().arrItemBag[indexSelect] = null;
            } else {
                var3 = Char.getMyChar().arrItemBag[indexSelect];
                Char.getMyChar().arrItemBag[indexSelect] = null;
                Char.getMyChar().arrItemBag[itemUpGrade.indexUI] = itemUpGrade;
                itemUpGrade = var3;
            }

            super.left = super.center = null;
            this.gameBC();
        } else if (Char.getMyChar().arrItemBag[indexSelect].template.type != 26 && Char.getMyChar().arrItemBag[indexSelect].template.type != 28) {
            GameCanvas.msgdlg.gameAA(mResources.gameNI, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
            GameCanvas.currentDialog = GameCanvas.msgdlg;
        } else {
            int var1;
            if (Char.getMyChar().arrItemBag[indexSelect].template.type == 28) {
                for (var1 = 0; var1 < arrItemUpGrade.length; ++var1) {
                    if (arrItemUpGrade[var1] != null && arrItemUpGrade[var1].template.type == 28) {
                        Item var2 = Char.getMyChar().arrItemBag[indexSelect];
                        Char.getMyChar().arrItemBag[indexSelect] = null;
                        int var10001 = arrItemUpGrade[var1].indexUI;
                        Char.getMyChar().arrItemBag[var10001] = arrItemUpGrade[var1];
                        arrItemUpGrade[var1] = var2;
                        return;
                    }
                }
            }

            for (var1 = 0; var1 < arrItemUpGrade.length; ++var1) {
                if (arrItemUpGrade[var1] == null) {
                    arrItemUpGrade[var1] = Char.getMyChar().arrItemBag[indexSelect];
                    Char.getMyChar().arrItemBag[indexSelect] = null;
                    super.left = super.center = null;
                    this.gameBC();
                    return;
                }
            }

            GameCanvas.msgdlg.gameAA(mResources.gameNO, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
            GameCanvas.currentDialog = GameCanvas.msgdlg;
        }
    }

    private void gameAO(int var1) {
        MyVector var2 = new MyVector();
        if (var1 == 0) {
            var2.addElement(this.gameRS);
        } else if (var1 == 1) {
            var2.addElement(this.gameRT);
        } else if (var1 == 2) {
            var2.addElement(this.gameRU);
        }

        if (itemUpGrade != null && itemSplit != null) {
            for (int var3 = 0; var3 < arrItemUpGrade.length; ++var3) {
                if (arrItemUpGrade[var3] != null) {
                    var2.addElement(new Command(mResources.gameFV, 341));
                    break;
                }
            }
        }

        GameCanvas.menu.gameAA(var2);
    }

    private void gameAP(int var1) {
        Item var2 = itemSplit;
        if (var1 == 1) {
            var2 = itemUpGrade;
            itemUpGrade = null;
        } else if (var1 == 2) {
            var2 = gameAK(47);
            arrItemUpGrade[indexSelect] = null;
        } else {
            itemSplit = null;
        }

        Char.getMyChar().arrItemBag[var2.indexUI] = var2;
        super.left = super.center = null;
        this.gameBC();
    }

    private void gameKM() {
        int var3 = 0;

        for (int var1 = 0; var1 < arrItemUpGrade.length; ++var1) {
            if (arrItemUpGrade[var1] != null && arrItemUpGrade[var1].template.type == 26) {
                var3 += crystals[arrItemUpGrade[var1].template.id];
            }
        }

        boolean var4 = false;
        int var2 = 0;
        if (itemSplit != null) {
            if (coinUpWeapons[itemSplit.upgrade] > Char.getMyChar().xu + Char.getMyChar().yen) {
                var4 = true;
            }

            var2 = var3 * 100 / upWeapon[itemSplit.upgrade];
        }

        if (var4) {
            InfoMe.gameAA(mResources.gameQM, 15, mFont.tahoma_7_red);
        } else if (var2 > 250) {
            GameCanvas.gameAA(mResources.gameQN, new Command(mResources.gameCH, 342), new Command(mResources.gameCU, 1));
        } else {
            if (itemSplit != null && itemUpGrade != null && arrItemUpGrade.length > 0) {
                if (!itemUpGrade.isLock) {
                    GameCanvas.gameAA(mResources.gameQO, new Command(mResources.gameCH, 342), new Command(mResources.gameCU, 1));
                    return;
                }

                Service.gI().ngockham((byte) 0, itemUpGrade, itemSplit, arrItemUpGrade);
            }

        }
    }

    private static void gameKN() {
        GameCanvas.gameAJ();
        if (itemSplit != null && itemUpGrade != null && arrItemUpGrade.length > 0) {
            Service.gI().ngockham((byte) 0, itemUpGrade, itemSplit, arrItemUpGrade);
        }

    }

    private void gameBR(mGraphics var1) {
        if (gameML) {
            if (indexMenu == 0) {
                String[] var2 = mResources.gameHJ;
                var1 = var1;
                GameScr var14 = this;

                try {
                    Paint.gameAA(popupX, popupY, popupW, popupH, var1);
                    gameAA(var1, var2[indexMenu], var2.length > 1);
                    gameTD = popupX + 3;
                    gameTE = popupY + 34 + gameHK;
                    int var15 = popupX + 74;
                    int var3 = gameTE - gameHK - 3;
                    gameTK = 4;
                    int var6;
                    int var7;
                    if (itemSplit != null) {
                        var14.gameAA(var1, itemSplit, var15, var3);
                        int var10002 = var15 + 35;
                        var7 = var3 + 25;
                        var6 = var10002;
                        GameScr var4 = var14;
                        int var8 = var14.gameVS[itemSplit.upgrade][0];
                        int var9 = 0;
                        ItemOption var10 = null;
                        int var11;
                        if (itemSplit.options != null) {
                            for (var11 = 0; var11 < itemSplit.options.size(); ++var11) {
                                if ((var10 = (ItemOption) itemSplit.options.elementAt(var11)).optionTemplate.id == 104) {
                                    var9 = var10.param;
                                }
                            }
                        }

                        var11 = 0;

                        int var18;
                        for (var18 = 0; var18 < arrItemSplit.length; ++var18) {
                            Item var12;
                            if ((var12 = arrItemSplit[var18]) != null) {
                                var11 += var4.gameVS[var12.upgrade][1];
                            }
                        }

                        boolean var19 = false;
                        var1.gameAA(0);
                        var1.gameAC(var6, var7 - 5, 60, 5);
                        if ((var18 = var9 * 60 / var8) <= 0) {
                            var18 = 0;
                        } else if (var18 > 60) {
                            var18 = 60;
                        }

                        var1.gameAA(-16711936);
                        var1.gameAC(var6, var7 - 5, var18, 5);
                        int var20;
                        if ((var20 = var11 * 60 / var8) >= 60 - var18) {
                            var20 = 60 - var18;
                        }

                        var1.gameAA(-16346586);
                        var1.gameAC(var6 + var18, var7 - 5, var20, 5);
                        mFont.tahoma_7_yellow.gameAA(var1, var11 + var9 + "/" + var8, var6 + 30, var7 - 5 - 15, 2);
                    } else {
                        var1.gameAA(6425);
                        var1.gameAC(var15 - 1, var3 - 1, gameHK + 3, gameHK + 3);
                        SmallImage.gameAA(var1, 154, var15 + gameHK / 2, var3 + gameHK / 2, 0, 3);
                    }

                    var1.gameAA(12281361);
                    var1.gameAB(var15, var3, gameHK, gameHK);
                    var1.gameAA(6425);
                    var1.gameAC(gameTD - 1, gameTE - 1, gameHK * gameTJ + 3, gameHK * gameTK + 3);
                    int var16 = 0;

                    while (true) {
                        int var5;
                        if (var16 >= gameTK) {
                            for (var16 = 0; var16 < arrItemSplit.length; ++var16) {
                                Item var17;
                                if ((var17 = arrItemSplit[var16]) != null) {
                                    var6 = var16 / gameTJ;
                                    var7 = var16 - var6 * gameTJ;
                                    if (!var17.isLock) {
                                        var1.gameAA(12083);
                                        var1.gameAC(gameTD + var7 * gameHK + 1, gameTE + var6 * gameHK + 1, gameHK - 1, gameHK - 1);
                                    }

                                    var14.gameAA(var1, var17, gameTD + var7 * gameHK, gameTE + var6 * gameHK);
                                    if (var17.quantity > 1) {
                                        mFont.number_yellow.gameAA(var1, String.valueOf(var17.quantity), gameTD + var7 * gameHK + gameHK, gameTE + var6 * gameHK + gameHK - mFont.number_yellow.gameAC(), 1);
                                    }
                                }
                            }

                            if (gameHL == 1) {
                                var1.gameAA(16777215);
                                var1.gameAB(var15, var3, gameHK, gameHK);
                                return;
                            }

                            if (gameHL == 2) {
                                var16 = indexSelect / gameTJ;
                                var5 = indexSelect - var16 * gameTJ;
                                var1.gameAA(16777215);
                                var1.gameAB(gameTD + var5 * gameHK, gameTE + var16 * gameHK, gameHK, gameHK);
                                return;
                            }
                            break;
                        }

                        for (var5 = 0; var5 < gameTJ; ++var5) {
                            SmallImage.gameAA(var1, 154, gameTD + var5 * gameHK + gameHK / 2, gameTE + var16 * gameHK + gameHK / 2, 0, 3);
                            var1.gameAA(12281361);
                            var1.gameAB(gameTD + var5 * gameHK, gameTE + var16 * gameHK, gameHK, gameHK);
                        }

                        ++var16;
                    }
                } catch (Exception var13) {
                    var13.printStackTrace();
                }

                return;
            } else if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameHJ);
            }
        }

    }

    private void gameBS(mGraphics var1) {
        if (gameMR) {
            if (indexMenu == 0) {
                String[] var2 = mResources.gameHK;
                var1 = var1;
                GameScr var9 = this;

                try {
                    Paint.gameAA(popupX, popupY, popupW, popupH, var1);
                    gameAA(var1, var2[indexMenu], var2.length > 1);
                    gameTD = popupX + 3;
                    gameTE = popupY + 34 + gameHK;
                    int var10 = popupX + 74;
                    int var3 = gameTE - gameHK - 3;
                    gameTK = 4;

                    int var4;
                    for (var4 = 0; var4 < var9.gameVP.length; ++var4) {
                        var1.gameAA(6425);
                        var1.gameAC(var9.gameVP[var4] - 1, var9.gameVQ[var4] - 1, gameHK + 3, gameHK + 3);
                        var1.gameAA(12281361);
                        var1.gameAB(var9.gameVP[var4], var9.gameVQ[var4], gameHK, gameHK);
                        SmallImage.gameAA(var1, 154, var9.gameVP[var4] + gameHK / 2, var9.gameVQ[var4] + gameHK / 2, 0, 3);
                    }

                    for (var4 = 0; var4 < arrItemSplit.length; ++var4) {
                        Item var5;
                        if ((var5 = arrItemSplit[var4]) != null) {
                            int var6 = var4 / gameTJ;
                            int var7 = var4 - var6 * gameTJ;
                            if (!var5.isLock) {
                                var1.gameAA(12083);
                                var1.gameAC(gameTD + var7 * gameHK + 1, gameTE + var6 * gameHK + 1, gameHK - 1, gameHK - 1);
                            }

                            var9.gameAA(var1, var5, gameTD + var7 * gameHK, gameTE + var6 * gameHK);
                            if (var5.quantity > 1) {
                                mFont.number_yellow.gameAA(var1, String.valueOf(var5.quantity), gameTD + var7 * gameHK + gameHK, gameTE + var6 * gameHK + gameHK - mFont.number_yellow.gameAC(), 1);
                            }
                        }
                    }

                    if (indexMenu == 0 && var9.gameVR >= 0) {
                        var1.gameAA(16777215);
                        var1.gameAB(var9.gameVP[var9.gameVR], var9.gameVQ[var9.gameVR], gameHK, gameHK);
                    }

                    if (gameHL != 1) {
                        return;
                    }

                    var1.gameAA(16777215);
                    var1.gameAB(var10, var3, gameHK, gameHK);
                } catch (Exception var8) {
                    var8.printStackTrace();
                    return;
                }
            } else if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameHK);
            }
        }

    }

    private void gameBT(mGraphics var1) {
        if (gameMM) {
            if (indexMenu == 0) {
                String[] var2 = mResources.gameHI;
                var1 = var1;
                GameScr var9 = this;

                try {
                    gameTK = 3;
                    Paint.gameAA(popupX, popupY, popupW, popupH, var1);
                    gameAA(var1, var2[indexMenu], var2.length > 1);
                    gameTD = popupX + 3;
                    gameTE = popupY + 34 + gameHK;
                    int var11 = popupX + 45;
                    int var3 = popupX + 100;
                    int var4 = gameTE - gameHK - 3;
                    if (itemSplit != null) {
                        var9.gameAA(var1, itemSplit, var11, var4);
                    } else {
                        var1.gameAA(6425);
                        var1.gameAC(var11 - 1, var4 - 1, gameHK + 3, gameHK + 3);
                        SmallImage.gameAA(var1, 154, var11 + gameHK / 2, var4 + gameHK / 2, 0, 3);
                    }

                    var1.gameAA(12281361);
                    var1.gameAB(var11, var4, gameHK, gameHK);
                    if (itemUpGrade != null) {
                        var9.gameAA(var1, itemUpGrade, var3, var4);
                    } else {
                        var1.gameAA(6425);
                        var1.gameAC(var3 - 1, var4 - 1, gameHK + 3, gameHK + 3);
                        SmallImage.gameAA(var1, 154, var3 + gameHK / 2, var4 + gameHK / 2, 0, 3);
                    }

                    var1.gameAA(12281361);
                    var1.gameAB(var11, var4, gameHK, gameHK);
                    var1.gameAB(var3, var4, gameHK, gameHK);
                    mFont.tahoma_7b_yellow.gameAA(var1, "+", var11 + gameHK + 15, var4 + gameHK / 2 - 5, 2);
                    if (gameHL == 1) {
                        if (indexSelect == 0) {
                            var1.gameAA(16777215);
                            var1.gameAB(var11, var4, gameHK, gameHK);
                        }

                        if (indexSelect == 1) {
                            var1.gameAA(16777215);
                            var1.gameAB(var3, var4, gameHK, gameHK);
                        }
                    }

                    int var10;
                    for (var10 = 0; var10 < gameTK; ++var10) {
                        for (var11 = 0; var11 < gameTJ; ++var11) {
                            var1.gameAA(6425);
                            var1.gameAC(gameTD + var11 * gameHK, gameTE + var10 * gameHK, gameHK + 3, gameHK + 3);
                            SmallImage.gameAA(var1, 154, gameTD + var11 * gameHK + gameHK / 2, gameTE + var10 * gameHK + gameHK / 2, 0, 3);
                            var1.gameAA(12281361);
                            var1.gameAB(gameTD + var11 * gameHK, gameTE + var10 * gameHK, gameHK, gameHK);
                        }
                    }

                    if (gameHL == 2) {
                        var10 = indexSelect / gameTJ;
                        var11 = indexSelect - var10 * gameTJ;
                        var1.gameAA(16777215);
                        var1.gameAB(gameTD + var11 * gameHK, gameTE + var10 * gameHK, gameHK, gameHK);
                    }

                    int var6;
                    for (var10 = 0; var10 < arrItemUpGrade.length; ++var10) {
                        Item var12;
                        if ((var12 = arrItemUpGrade[var10]) != null) {
                            int var5 = var10 / gameTJ;
                            var6 = var10 - var5 * gameTJ;
                            if (!var12.isLock) {
                                var1.gameAA(12083);
                                var1.gameAC(gameTD + var6 * gameHK + 1, gameTE + var5 * gameHK + 1, gameHK - 1, gameHK - 1);
                            }

                            SmallImage.gameAA(var1, var12.template.iconID, gameTD + var6 * gameHK + gameHK / 2, gameTE + var5 * gameHK + gameHK / 2, 0, 3);
                        }
                    }

                    if (itemUpGrade != null && itemSplit != null) {
                        var10 = 0;

                        for (var11 = 0; var11 < arrItemUpGrade.length; ++var11) {
                            if (arrItemUpGrade[var11] != null && arrItemUpGrade[var11].template.type == 26) {
                                var10 += crystals[arrItemUpGrade[var11].template.id];
                            }
                        }

                        if ((var11 = var10 * 100 / upWeapon[itemSplit.upgrade]) > maxPercents[itemSplit.upgrade]) {
                            var11 = maxPercents[itemSplit.upgrade];
                        }

                        if (gameKY) {
                            var11 = (int) ((double) var11 * 1.5D);
                        }

                        mFont var14 = mFont.tahoma_7_yellow;
                        var6 = 0;
                        var9 = null;
                        if (itemSplit.options != null) {
                            for (int var7 = 0; var7 < itemSplit.options.size(); ++var7) {
                                ItemOption var13;
                                if ((var13 = (ItemOption) itemSplit.options.elementAt(var7)).optionTemplate.id == 123) {
                                    var6 = var13.param;
                                }
                            }
                        } else {
                            Service.gI().requestItemInfo(itemSplit.typeUI, itemSplit.indexUI);
                        }

                        if (var6 > Char.getMyChar().xu + Char.getMyChar().yen) {
                            var14 = mFont.tahoma_7_red;
                        }

                        var14.gameAA(var1, mResources.gameAA(mResources.gameHT, NinjaUtil.gameAA(String.valueOf(var6))), gameTD, gameTE + gameTK * gameHK + 5, 0);
                        mFont.tahoma_7_yellow.gameAA(var1, mResources.gameHW + ": " + var11 + "%", gameTD, gameTE + gameTK * gameHK + 17, 0);
                    } else {
                        for (var10 = 0; var10 < mResources.gameHQ.length; ++var10) {
                            mFont.tahoma_7_white.gameAA(var1, mResources.gameHQ[var10], gameTD, gameTE + gameTK * gameHK + 5 + var10 * 12, 0);
                        }
                    }

                    if (effUpok == null) {
                        return;
                    }

                    SmallImage.gameAA(var1, effUpok.arrEfInfo[indexEff].idImg, var3 + gameHK / 2 + effUpok.arrEfInfo[indexEff].dx + 1, var4 + gameHK / 2 + 9 + effUpok.arrEfInfo[indexEff].dy, 0, 3);
                    if (GameCanvas.gameTick % 2 != 0 || ++indexEff < effUpok.arrEfInfo.length) {
                        return;
                    }

                    indexEff = 0;
                    effUpok = null;
                } catch (Exception var8) {
                    var8.printStackTrace();
                    return;
                }
            } else if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameHI);
            }
        }

    }

    private void gameBU(mGraphics var1) {
        if (gameMN) {
            if (indexMenu == 0) {
                this.gameAA(var1, mResources.gameHM, (byte) 0);
                return;
            }

            if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameHM);
            }
        }

    }

    private void gameBV(mGraphics var1) {
        if (gameMO) {
            if (indexMenu == 0) {
                this.gameAA(var1, mResources.gameHN, (byte) 1);
                return;
            }

            if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameHN);
            }
        }

    }

    private void gameAA(mGraphics var1, String[] var2, byte var3) {
        try {
            gameTK = 5;
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            this.gameAA(var1, var2, false);
            var1.gameAA(6693376);
            var1.gameAC(popupX + 3, popupY + 32, 168, 140);
            var1.gameAA(13408563);
            var1.gameAB(popupX + 3, popupY + 32, 168, 140);
            int var11 = popupX + 74;
            int var4 = popupY + 40 + gameHK;
            if (itemSplit != null) {
                var1.gameAA(6425);
                var1.gameAC(var11 - 1, var4 - 1, gameHK + 3, gameHK + 3);
                SmallImage.gameAA(var1, 154, var11 + gameHK / 2, var4 + gameHK / 2, 0, 3);
                this.gameAA(var1, itemSplit, var11, var4);
                if (itemSplit.quantity > 1) {
                    mFont.number_yellow.gameAA(var1, "" + itemSplit.quantity, var11 + gameHK, var4 + gameHK / 2 + 6, 1);
                }

                var1.gameAA(gameHL == 1 ? 16777215 : 12281361);
                var1.gameAB(var11, var4, gameHK, gameHK);
            } else {
                var1.gameAA(6425);
                var1.gameAC(var11 - 1, var4 - 1, gameHK + 3, gameHK + 3);
                SmallImage.gameAA(var1, 154, var11 + gameHK / 2, var4 + gameHK / 2, 0, 3);
                var1.gameAA(12281361);
                var1.gameAB(var11, var4, gameHK, gameHK);
            }

            if (itemSplit != null) {
                int var8 = 0;
                ItemOption var5 = null;
                if (itemSplit.options != null) {
                    for (int var6 = 0; var6 < itemSplit.options.size(); ++var6) {
                        if ((var5 = (ItemOption) itemSplit.options.elementAt(var6)).optionTemplate.id == 122) {
                            var8 = var5.param;
                        }
                    }
                } else {
                    Service.gI().requestItemInfo(itemSplit.typeUI, itemSplit.indexUI);
                }

                String var14 = mResources.gameRW;
                String var9 = var8 + mResources.gamePB;
                String var13 = mResources.gameAO;
                if (var3 == 0) {
                    var14 = mResources.gameRV;
                    var9 = gameMS[itemSplit.upgrade] + mResources.gamePA;
                    var13 = mResources.gameAN;
                }

                mFont.tahoma_7_white.gameAA(var1, var14, var11 + gameHK / 2, var4 + 3 * gameHK / 2 + 2, 2);
                mFont.tahoma_7_yellow.gameAA(var1, var9, var11 + gameHK / 2, var4 + 3 * gameHK / 2 + 14, 2);
                String[] var10 = mFont.tahoma_7_white.gameAB(var13, 130);

                for (int var12 = 0; var12 < var10.length; ++var12) {
                    mFont.tahoma_7_white.gameAA(var1, var10[var12], var11 + gameHK / 2, popupY + popupH - 25 + var12 * 12 - 2, 2);
                }

                return;
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }

    private static void gameKO() {
        try {
            if (gameMN) {
                if (Char.getMyChar().arrItemBag[indexSelect].isTypeNgocKham()) {
                    itemSplit = Char.getMyChar().arrItemBag[indexSelect];
                    Char.getMyChar().arrItemBag[indexSelect] = null;
                    return;
                }
            } else if (gameMO && Char.getMyChar().arrItemBag[indexSelect].isTypeBody()) {
                itemSplit = Char.getMyChar().arrItemBag[indexSelect];
                Char.getMyChar().arrItemBag[indexSelect] = null;
                return;
            }
        } catch (Exception var1) {
            GameCanvas.msgdlg.gameAA(mResources.gameAC, (Command) null, new Command(mResources.gameBH, 2), (Command) null);
            GameCanvas.currentDialog = GameCanvas.msgdlg;
        }

    }

    private void gameBW(mGraphics var1) {
        if (gameMQ) {
            if (indexMenu == 0) {
                Item[] var3 = arrItemSplit;
                String[] var2 = mResources.gameHL;
                var1 = var1;
                GameScr var7 = this;

                try {
                    gameAB(var1);
                    var7.gameAA(var1, var2, true);
                    if (var3 == null) {
                        GameCanvas.gameAA(popupX + 90, popupY + 75, var1, false);
                        mFont.tahoma_7b_white.gameAA(var1, mResources.PLEASEWAIT, popupX + 90, popupY + 90, 2);
                        return;
                    }

                    if (var3.length <= 30) {
                        gameTK = 5;
                    } else if (var3.length % gameTJ == 0) {
                        gameTK = var3.length / gameTJ;
                    } else {
                        gameTK = var3.length / gameTJ + 1;
                    }

                    scrMain.gameAA(gameTK, gameHK, gameTD, gameTE, gameTJ * gameHK, 5 * gameHK, true, 6);
                    scrMain.gameAA(var1, gameTD, gameTE, scrMain.width + 2, scrMain.height + 2);

                    int var8;
                    int var9;
                    for (var8 = 0; var8 < gameTK; ++var8) {
                        for (var9 = 0; var9 < gameTJ; ++var9) {
                            SmallImage.gameAA(var1, 154, gameTD + var9 * gameHK + gameHK / 2, gameTE + var8 * gameHK + gameHK / 2, 0, 3);
                            var1.gameAA(12281361);
                            var1.gameAB(gameTD + var9 * gameHK, gameTE + var8 * gameHK, gameHK, gameHK);
                        }
                    }

                    for (var8 = 0; var8 < var3.length; ++var8) {
                        Item var10;
                        if ((var10 = var3[var8]) != null) {
                            int var4 = var8 / gameTJ;
                            int var5 = var8 - var4 * gameTJ;
                            if (!var10.isLock) {
                                var1.gameAA(12083);
                                var1.gameAC(gameTD + var5 * gameHK + 1, gameTE + var4 * gameHK + 1, gameHK - 1, gameHK - 1);
                                SmallImage.gameAA(var1, 154, gameTD + var5 * gameHK + gameHK / 2, gameTE + var4 * gameHK + gameHK / 2, 0, 3);
                            }

                            SmallImage.gameAA(var1, var10.template.iconID, gameTD + var5 * gameHK + gameHK / 2, gameTE + var4 * gameHK + gameHK / 2, 0, 3);
                        }
                    }

                    if (gameHL > 0 && indexSelect >= 0) {
                        var8 = indexSelect / gameTJ;
                        var9 = indexSelect - var8 * gameTJ;
                        var1.gameAA(16777215);
                        var1.gameAB(gameTD + var9 * gameHK, gameTE + var8 * gameHK, gameHK, gameHK);
                        gameAA(gameTD + var9 * gameHK, gameTE + var8 * gameHK, var1);
                        return;
                    }
                } catch (Exception var6) {
                }

                return;
            } else if (indexMenu == 1) {
                this.gameAA(var1, mResources.gameHL);
            }
        }

    }

    private void gameKP() {
        for (int var1 = 0; var1 < arrItemSplit.length; ++var1) {
            if (arrItemSplit[var1] == null) {
                arrItemSplit[var1] = Char.getMyChar().arrItemBag[indexSelect];
                Char.getMyChar().arrItemBag[indexSelect] = null;
                super.left = super.center = null;
                this.gameBC();
                return;
            }
        }

    }

    private void gameKQ() {
        MyVector var1 = new MyVector();

        for (int var2 = 0; var2 < arrItemSplit.length; ++var2) {
            if (arrItemSplit[var2] != null) {
                var1.addElement(this.gameRP);
                break;
            }
        }

        if (arrItemSplit.length > 0) {
            var1.addElement(new Command(mResources.gameHL[0], 403));
        }

        GameCanvas.menu.gameAA(var1);
    }

    private void gameKR() {
        Item var1 = gameAK(48);
        arrItemSplit[indexSelect] = null;
        Char.getMyChar().arrItemBag[var1.indexUI] = var1;
        super.left = super.center = null;
        this.gameBC();
    }

    private static void gameKS() {
        Service.gI().giaodo(arrItemSplit);
    }

    private void gameBX(mGraphics var1) {
        if (indexMenu == 6) {
            var1.gameAA(-var1.gameAA(), -var1.gameAB());
            Paint.gameAA(popupX, popupY, popupW, popupH, var1);
            var1.gameAA(Paint.COLORBACKGROUND);
            gameAA(var1, mResources.gameGG[indexMenu], true);
            gameAB(var1);
            var1.gameAA(0);
            var1.gameAC(popupX + 2, popupY + 31, 171, popupH - 34);
            var1.gameAA(13606712);
            var1.gameAB(popupX + 3, popupY + 32, 168, popupH - 37);
            var1.gameAA(Paint.COLORBACKGROUND);
            var1.gameAC(popupX + 4, popupY + 34, 166, popupH - 39);
            int var2;
            int var3;
            if (currentCharViewInfo.arrItemViThu[4] != null) {
                mFont.tahoma_7b_white.gameAA(var1, currentCharViewInfo.arrItemViThu[4].template.name, popupX + 90, gameTE + 2, 2);
                var2 = currentCharViewInfo.arrItemViThu[4].sys + 1;

                for (var3 = 0; var3 < var2; ++var3) {
                    SmallImage.gameAA(var1, 628, popupX + 90 + var3 * 12 - var2 * 6, gameTE + 20, 0, 3);
                }
            }

            for (var2 = 0; var2 < currentCharViewInfo.arrItemViThu.length - 1; ++var2) {
                if (currentCharViewInfo.arrItemViThu[var2] != null) {
                    this.gameAA(var1, currentCharViewInfo.arrItemViThu[var2], this.gameNW[var2], this.gameNX[var2]);
                } else {
                    var1.gameAA(6425);
                    var1.gameAC(this.gameNW[var2] - 1, this.gameNX[var2] - 1, gameHK + 3, gameHK + 3);
                    mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[var2 + 24][0], this.gameNW[var2] + gameHK / 2, this.gameNX[var2] + gameHK / 2 - 10, 2);
                    mFont.tahoma_7_grey.gameAA(var1, mResources.gamePJ[var2 + 24][1], this.gameNW[var2] + gameHK / 2, this.gameNX[var2] + gameHK / 2 + 2, 2);
                }

                if (indexSelect == var2 && gameHL == 1 && indexSelect < 4) {
                    var1.gameAA(16777215);
                } else {
                    var1.gameAA(12281361);
                }

                var1.gameAB(this.gameNW[var2], this.gameNX[var2], gameHK, gameHK);
            }

            var2 = this.gameNW[0] + gameHK + 7;
            var3 = this.gameNX[0] - 5;
            var1.gameAA(6425);
            var1.gameAC(var2, var3, 84, 75);
            if (indexSelect == 4) {
                var1.gameAA(16777215);
            } else {
                var1.gameAA(12281361);
            }

            var1.gameAB(var2, var3, 84, 75);
            if (currentCharViewInfo.mobVithu != null) {
                currentCharViewInfo.mobVithu.gameAB(var1, var2 + 35, var3 + 55);
                currentCharViewInfo.mobVithu.gameAA(var1, var2 + 35, var3 + 55);
            }

            int var6 = 0;
            var2 = 0;
            var3 = 0;
            int var4;
            if (currentCharViewInfo.arrItemViThu[4] != null) {
                if (currentCharViewInfo.arrItemViThu[4].options != null) {
                    for (var4 = 0; var4 < currentCharViewInfo.arrItemViThu[4].options.size(); ++var4) {
                        ItemOption var5;
                        if ((var5 = (ItemOption) currentCharViewInfo.arrItemViThu[4].options.elementAt(var4)).optionTemplate.id == 151) {
                            var6 = var5.param;
                        } else if (var5.optionTemplate.id == 150) {
                            var2 = var5.param;
                        }
                    }
                }

                var3 = currentCharViewInfo.arrItemViThu[4].upgrade;
            }

            var4 = gameTD + 5;
            int var7 = gameTE + 112;
            mFont.tahoma_7b_white.gameAA(var1, mResources.gameEU + ": ", var4, var7, 0);
            mFont.tahoma_7b_white.gameAA(var1, String.valueOf(var3), var4 + 70, var7, 0);
            mFont var10000 = mFont.tahoma_7b_white;
            String var10002 = mResources.gameMV[2] + ": ";
            var7 += 15;
            var10000.gameAA(var1, var10002, var4, var7, 0);
            mFont.tahoma_7b_white.gameAA(var1, String.valueOf(var2), var4 + 70, var7, 0);
            var10000 = mFont.tahoma_7b_white;
            var10002 = mResources.gameKA + ": ";
            var7 += 17;
            var10000.gameAA(var1, var10002, var4, var7, 0);
            mFont.tahoma_7b_white.gameAA(var1, String.valueOf(var6), var4 + 70, var7, 0);
        }
    }

    private void gameKT() {
        MyVector var1 = new MyVector();
        if (gameVT == 0) {
            var1.addElement(new Command(mResources.gameNC, 5021));
            var1.addElement(new Command(mResources.gameGG[1], 5022));
            var1.addElement(this.gameSE);
        } else if (gameVT == 1) {
            var1.addElement(new Command(mResources.gameGG[indexMenu], 502));
            var1.addElement(new Command(mResources.gameGG[1], 5022));
            var1.addElement(this.gameSE);
        } else if (gameVT == 2) {
            var1.addElement(new Command(mResources.gameGG[indexMenu], 502));
            var1.addElement(new Command(mResources.gameNC, 5021));
            var1.addElement(this.gameSE);
        }

        GameCanvas.menu.gameAAz(var1, 3);
    }

    private static void gameKU() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameDY, 5041));
        var0.addElement(new Command(mResources.gameDZ, 5042));
        GameCanvas.menu.gameAA(var0);
    }

    private void gameKV() {
        if (gameHL > 0 && gameHL <= 4 || GameCanvas.isTouch) {
            GameCanvas.inputDlg.gameAA(mResources.gameNW, this.gamePE, 1);
        }
    }

    private static void gameKW() {
        String var0;
        if ((var0 = GameCanvas.inputDlg.tfInput.getText()).trim().equals("")) {
            GameCanvas.setText(mResources.gameNQ);
        } else {
            boolean var1 = false;

            int var3;
            try {
                var3 = Integer.parseInt(var0);
            } catch (Exception var2) {
                GameCanvas.gameAJ();
                return;
            }

            if (var3 <= 0) {
                GameCanvas.gameAJ();
            } else if (Char.getMyChar().pPoint != 0 && var3 <= Char.getMyChar().pPoint) {
                Service.gI().upPotential(gameHL - 1, var3);
                GameCanvas.gameAJ();
            } else {
                GameCanvas.setText(mResources.gameNP);
            }
        }
    }

    private static void gameKX() {
        MyVector var0;
        (var0 = new MyVector()).addElement(new Command(mResources.gameDY, 5051));
        var0.addElement(new Command(mResources.gameDZ, 5052));
        GameCanvas.menu.gameAA(var0);
    }

    private void gameKY() {
        if (gameHL > 0 && gameHL <= 4) {
            GameCanvas.inputDlg.gameAA(mResources.gameNW, this.gamePF, 1);
        }
    }

    private static void gameKZ() {
        String var0;
        if ((var0 = GameCanvas.inputDlg.tfInput.getText()).trim().equals("")) {
            GameCanvas.setText(mResources.gameNQ);
        } else {
            boolean var1 = false;

            int var3;
            try {
                var3 = Integer.parseInt(var0);
            } catch (Exception var2) {
                GameCanvas.gameAJ();
                return;
            }

            if (Char.getMyChar().sPoint != 0 && var3 <= Char.getMyChar().sPoint) {
                Service.gI().upSkill(Char.getMyChar().nClass.skillTemplates[indexSelect].id, var3);
                GameCanvas.gameAJ();
            } else {
                GameCanvas.setText(mResources.gameNQ);
                GameCanvas.currentDialog = GameCanvas.msgdlg;
            }
        }
    }

    public static void fieldAA(Mob var0, int var1) {
        MobTemplate var2;
        if ((var2 = Mob.arrMobTemplate[var0.templateId]).type != 0) {
            var0.dir = var1 % 3 == 0 ? -1 : 1;
            var0.x += 10 - var1 % 20;
        }

        Auto.fieldAA(var0);
        if (!fieldTH.contains(var2) && !var0.isBoss && (var0.templateId != 179 && var0.templateId != 175 && var0.templateId != 202 || var0.status != 8)) {
            fieldTH.addElement(var2);
        }

        vMob.addElement(var0);
    }

    public static void fieldAH(int var0) {
        Npc var1;
        if ((var1 = fieldAI(var0)) != null) {
            Char.fieldAC(var1.cx, var1.cy);
            Char.getMyChar().npcFocus = var1;
            Service.gI().openMenu(var1.template.npcTemplateId);
        }

    }

    public static Char fieldAA(String var0) {
        for (int var1 = 0; var1 < vCharInMap.size(); ++var1) {
            Char var2;
            if ((var2 = (Char) vCharInMap.elementAt(var1)).cName.equals(var0)) {
                return var2;
            }
        }

        return null;
    }

    public static void fieldAB(int var0, int var1, int var2) {
        if (System.currentTimeMillis() < 500L) {
            cuong.sleep(500L - System.currentTimeMillis());
        }

        Npc var3;
        if ((var3 = fieldAI(var0)) != null) {
            Char.fieldAC(var3.cx, var3.cy);
            Char.getMyChar().npcFocus = var3;
            Service.gI().menu((byte) 0, var0, var1, var2);
        }

    }

    public static Npc fieldAI(int var0) {
        Char var1 = Char.getMyChar();
        MyVector var2 = vNpc;
        int var3 = -1;
        Npc var4 = null;

        for (int var5 = 0; var5 < var2.size(); ++var5) {
            Npc var6;
            if ((var6 = (Npc) var2.elementAt(var5)) != null && var0 == var6.template.npcTemplateId) {
                int var7 = Res.gameAA(var1.cx, var1.cy, var6.cx, var6.cy);
                if (var3 == -1 || var7 < var3) {
                    var3 = var7;
                    var4 = var6;
                }
            }
        }

        return var4;
    }

    public static void fieldAC(String var0) {
        ChatPopup.gameAA("[THUAN_DEN] " + var0, Char.getMyChar());
    }

    public final void fieldAJ(int var1) {
        (new Thread(new DoiKhu(var1))).start();
    }

    static void fieldAA(GameScr var0, byte var1) {
        var0.gameAE(var1);
    }

    private void fieldCU() {
        if (!GameCanvas.menu.showMenu && GameCanvas.currentDialog == null) {
            if (GameCanvas.isPointerJustRelease && GameCanvas.gameAB(popupX, popupY, popupW, this.gameUB) && (!gameDQ || GameCanvas.w >= 320) && GameCanvas.isPointerClick) {
                if (GameCanvas.gameAB(gW2 - 90, popupY + 5, 60, 40)) {
                    indexSelect = 0;
                    --indexMenu;
                }

                if (GameCanvas.gameAB(gW2 + 20, popupY + 5, 60, 40)) {
                    indexSelect = 0;
                    ++indexMenu;
                }

                gameDQ = false;
                scrMain.gameAA();
                gameHO.gameAA();
                if (currentCharViewInfo.charID != Char.getMyChar().charID) {
                    if (indexMenu < 3) {
                        indexMenu = mResources.gameGG.length - 1;
                    }

                    if (indexMenu > mResources.gameGG.length - 1) {
                        indexMenu = 3;
                    }
                } else {
                    if (indexMenu < 0) {
                        indexMenu = mResources.gameGG.length - 1;
                    }

                    if (indexMenu > mResources.gameGG.length - 1) {
                        indexMenu = 0;
                    }
                }

                gameHL = 1;
                indexSelect = -1;
                this.gameCE();
            }

            ScrollResult var1;
            if (gameDQ) {
                if ((var1 = gameHO.gameAB()).isDowning || var1.isFinish) {
                    indexRow = var1.selected;
                    gameHL = 1;
                }

                if (GameCanvas.gameAI) {
                    return;
                }
            }

            if (indexMenu == 0) {
                if ((var1 = scrMain.gameAB()).isDowning || var1.isFinish) {
                    if (indexSelect != var1.selected) {
                        indexSelect = var1.selected;
                        super.left = super.center = null;
                        if (GameCanvas.gameAI) {
                            this.gameBJ();
                        } else if (gameAK(3) != null) {
                            this.gameHQ();
                        } else {
                            gameDQ = false;
                            super.left = this.gameTV;
                        }
                    }

                    gameHL = 1;
                    return;
                }
            } else if (indexMenu == 1) {
                if ((var1 = scrMain.gameAB()).isDowning || var1.isFinish) {
                    if (indexSelect != var1.selected) {
                        indexSelect = var1.selected;
                        if (var1.selected >= Char.getMyChar().nClass.skillTemplates.length) {
                            indexSelect = -1;
                        }

                        super.left = super.center = null;
                        this.gameBJ();
                        gameHO.gameAA();
                        indexRow = 0;
                    }

                    gameHL = 1;
                    return;
                }

                if (((var1 = gameHO.gameAB()).isDowning || var1.isFinish) && indexRow != var1.selected) {
                    indexRow = var1.selected;
                    return;
                }
            } else {
                int var3;
                if (indexMenu == 2) {
                    if (GameCanvas.isPointerJustRelease && GameCanvas.gameAB(popupX + 5, popupY + 52, popupW - 10, 130) && GameCanvas.isPointerClick) {
                        var3 = (GameCanvas.py - (popupY + 52)) / 32;
                        ++var3;
                        if (var3 == this.gameRO) {
                            MyVector var2;
                            (var2 = new MyVector()).addElement(new Command(mResources.gameDY, 11064));
                            var2.addElement(new Command(mResources.gameDZ, 11065));
                            GameCanvas.menu.gameAA(var2);
                        }

                        gameHL = var3;
                        this.gameRO = var3;
                        this.gameBJ();
                        return;
                    }
                } else if (indexMenu == 3) {
                    if ((var1 = scrMain.gameAB()).isDowning || var1.isFinish) {
                        indexRow = var1.selected;
                        gameHL = 1;
                        return;
                    }
                } else if (indexMenu == 4) {
                    if (GameCanvas.isPointerJustRelease) {
                        gameHL = 1;
                        if (GameCanvas.gameAB(popupX + 4, popupY + 35, gameHK, 130)) {
                            indexSelect = (GameCanvas.py - (popupY + 35)) / gameHK << 1;
                            super.left = super.center = null;
                            this.gameBJ();
                        }

                        if (GameCanvas.gameAB(popupX + popupW - 30, popupY + 35, gameHK, 130)) {
                            indexSelect = ((GameCanvas.pyLast - (popupY + 35)) / gameHK << 1) + 1;
                            super.left = super.center = null;
                            this.gameBJ();
                        }

                        if (GameCanvas.gameAB(popupX + 4, popupY + 165, popupW - 8, gameHK)) {
                            var3 = (GameCanvas.pxLast - (popupX + 4)) / gameHK;
                            var3 += 10;
                            indexSelect = var3;
                            super.left = super.center = null;
                            this.gameBJ();
                            return;
                        }
                    }
                } else if (indexMenu == 5 && GameCanvas.isPointerJustRelease) {
                    for (var3 = 0; var3 < this.gameNW.length; ++var3) {
                        if (var3 == 4) {
                            if (GameCanvas.gameAB(this.gameNW[var3], this.gameNX[var3], 84, 75) && GameCanvas.isPointerClick) {
                                gameHL = 1;
                                indexSelect = 4;
                                this.gameBJ();
                                if (!GameCanvas.gameAI && super.center != null) {
                                    this.gameAB(super.center.idAction, super.center.p);
                                }
                            }
                        } else if (GameCanvas.gameAB(this.gameNW[var3], this.gameNX[var3], gameHK, gameHK) && GameCanvas.isPointerClick) {
                            gameHL = 1;
                            indexSelect = var3;
                            this.gameBJ();
                            if (!GameCanvas.gameAI) {
                                if (currentCharViewInfo.arrItemMounts[indexSelect] != null) {
                                    this.gameAB(super.center.idAction, super.center.p);
                                } else {
                                    gameDQ = false;
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private void fieldCV() {
        if (gameME) {
            if (gameHL == 0) {
                if (GameCanvas.keyPressedz[8]) {
                    if (Char.clan == null) {
                        gameHL = 0;
                    } else {
                        gameHL = 1;
                    }

                    indexSelect = 0;
                    indexRow = -1;
                    if (indexMenu == 0) {
                        indexSelect = Char.clan.itemLevel;
                    }

                    scrMain.gameAA();
                    gameHO.gameAA();
                }

                if (GameCanvas.keyPressedz[4]) {
                    indexSelect = 0;
                    indexRow = -1;
                    --indexMenu;
                    scrMain.gameAA();
                    gameHO.gameAA();
                    if (indexMenu < 0) {
                        indexMenu = mResources.gameRH.length - 1;
                    }

                    if (indexMenu >= mResources.gameRH.length) {
                        indexMenu = 0;
                    }

                    if (indexMenu == 1 && gameMF) {
                        Service.gI().requestClanMember();
                        gameMF = false;
                    } else if (indexMenu == 2) {
                        Service.gI().requestClanItem();
                    } else if (indexMenu == 3) {
                        Service.gI().requestClanLog();
                    } else if (indexMenu == 4) {
                        Service.gI().requestClanItem();
                    }

                    gameAB(175, 200);
                }

                if (GameCanvas.keyPressedz[6]) {
                    indexSelect = 0;
                    indexRow = -1;
                    ++indexMenu;
                    scrMain.gameAA();
                    gameHO.gameAA();
                    if (indexMenu < 0) {
                        indexMenu = mResources.gameRH.length - 1;
                    }

                    if (indexMenu >= mResources.gameRH.length) {
                        indexMenu = 0;
                    }

                    if (indexMenu == 1 && gameMF) {
                        Service.gI().requestClanMember();
                        gameMF = false;
                    } else if (indexMenu == 2) {
                        Service.gI().requestClanItem();
                    } else if (indexMenu == 3) {
                        Service.gI().requestClanLog();
                    } else if (indexMenu == 4) {
                        Service.gI().requestClanItem();
                    }

                    gameAB(175, 200);
                }

                this.gameCX();
            } else if (gameDQ) {
                if (GameCanvas.keyPressedz[2]) {
                    if (--indexRow < 0) {
                        indexRow = gameHM - 1;
                    }

                    gameHO.gameAA(indexRow * gameHO.ITEM_SIZE);
                } else if (GameCanvas.keyPressedz[8]) {
                    if (++indexRow >= gameHM) {
                        indexRow = 0;
                    }

                    gameHO.gameAA(indexRow * gameHO.ITEM_SIZE);
                }
            } else {
                if (indexRow < 0) {
                    indexRow = 0;
                }

                if (indexMenu == 2) {
                    if (Char.clan != null && Char.clan.items != null) {
                        if (GameCanvas.keyPressedz[4]) {
                            if (--indexSelect < 0) {
                                indexSelect = Char.clan.items.length - 1;
                            }
                        } else if (GameCanvas.keyPressedz[6]) {
                            if (++indexSelect >= Char.clan.items.length) {
                                indexSelect = 0;
                            }
                        } else if (GameCanvas.keyPressedz[8]) {
                            if (indexSelect + gameTJ <= Char.clan.items.length - 1) {
                                indexSelect += gameTJ;
                            }
                        } else if (GameCanvas.keyPressedz[2]) {
                            if (indexSelect >= 0 && indexSelect < gameTJ) {
                                gameHL = 0;
                                indexSelect = 0;
                            } else if (indexSelect - gameTJ >= 0) {
                                indexSelect -= gameTJ;
                            }
                        }

                        scrMain.gameAA(indexSelect / gameTJ * scrMain.ITEM_SIZE);
                    }
                } else if (indexMenu == 0 && gameHL == 1) {
                    if (GameCanvas.keyPressedz[8]) {
                        ++gameHL;
                    } else if (GameCanvas.keyPressedz[2]) {
                        --gameHL;
                    }
                } else if (indexMenu == 4) {
                    if (GameCanvas.keyPressedz[2]) {
                        if (indexRow == 0) {
                            --gameHL;
                            indexRow = -1;
                        } else {
                            --indexRow;
                        }

                        scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                    } else if (GameCanvas.keyPressedz[8]) {
                        if (++indexRow >= gameHM) {
                            indexRow = 0;
                        }

                        scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                    } else if (GameCanvas.keyPressedz[4]) {
                        --this.gameUW;
                        if (this.gameUW < 0) {
                            this.gameUW = 0;
                        }
                    } else if (GameCanvas.keyPressedz[6]) {
                        ++this.gameUW;
                        if (this.gameUW > this.gameUX.size() - 1) {
                            this.gameUW = (byte) (this.gameUX.size() - 1);
                        }
                    }
                } else if (GameCanvas.keyPressedz[2]) {
                    if (indexRow == 0) {
                        --gameHL;
                        indexRow = -1;
                    } else {
                        --indexRow;
                    }

                    scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                    if (indexMenu == 1 && gameMF) {
                        Service.gI().requestClanMember();
                        gameMF = false;
                    }
                } else if (GameCanvas.keyPressedz[8]) {
                    if (++indexRow >= gameHM) {
                        indexRow = 0;
                    }

                    scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                }

                this.gameCX();
            }

            if (GameCanvas.isTouch && GameCanvas.currentDialog == null && !GameCanvas.menu.showMenu) {
                label367:
                {
                    if (GameCanvas.isPointerJustRelease) {
                        if (GameCanvas.gameAB(popupX, popupY, popupW, this.gameUB) && (!gameDQ || GameCanvas.w >= 320) && GameCanvas.isPointerClick) {
                            if (GameCanvas.gameAB(gW2 - 90, popupY + 5, 60, 40)) {
                                indexSelect = 0;
                                --indexMenu;
                                indexRow = 0;
                            }

                            if (GameCanvas.gameAB(gW2 + 20, popupY + 5, 60, 40)) {
                                indexSelect = 0;
                                ++indexMenu;
                                indexRow = 0;
                            }

                            gameDQ = false;
                            scrMain.gameAA();
                            gameHO.gameAA();
                            if (indexMenu < 0) {
                                indexMenu = mResources.gameRH.length - 1;
                            }

                            if (indexMenu > mResources.gameRH.length - 1) {
                                indexMenu = 0;
                            }

                            gameHL = 1;
                            if (indexMenu == 1 && gameMF) {
                                Service.gI().requestClanMember();
                                gameMF = false;
                            } else if (indexMenu == 2 && Char.clan != null && Char.clan.items == null) {
                                Service.gI().requestClanItem();
                            }

                            if (indexMenu == 3) {
                                Service.gI().requestClanLog();
                            }

                            if (indexMenu == 4) {
                                Service.gI().requestClanItem();
                            }

                            gameAB(175, 200);
                            this.gameCX();
                        }

                        if (indexMenu == 4) {
                            int var1 = this.gameVG - this.gameVI / 2;
                            int var2 = this.gameVH - this.gameVI / 2;
                            int var3 = (this.gameVI + 5) * this.gameUX.size();
                            int var4 = this.gameVI;
                            if (GameCanvas.gameAB(var1, var2, var3, var4) && (var1 = (GameCanvas.px - var1) / (this.gameVI + 5)) >= 0 && var1 < this.gameUX.size()) {
                                this.gameUW = (byte) var1;
                            }
                        }
                    }

                    ScrollResult var5;
                    if (gameDQ) {
                        if ((var5 = gameHO.gameAB()).isDowning || var5.isFinish) {
                            indexRow = var5.selected;
                            gameHL = 1;
                        }

                        if (GameCanvas.gameAI) {
                            break label367;
                        }
                    }

                    if (indexMenu == 2) {
                        if ((var5 = scrMain.gameAB()).isDowning || var5.isFinish) {
                            indexSelect = var5.selected;
                            gameHL = 1;
                            this.gameAB(1509, (Object) null);
                        }
                    } else if (indexMenu == 0 && GameCanvas.gameAB(popupX + 18, popupY + 32, 5 * gameHK, gameHK) && GameCanvas.isPointerJustRelease && GameCanvas.isPointerClick) {
                        if (Char.clan != null) {
                            indexSelect = Char.clan.itemLevel;
                            gameHL = 1;
                        }
                    } else if (indexMenu != 0 && indexMenu != 3) {
                        if (indexMenu == 1 && vClan.size() != 0 && ((var5 = scrMain.gameAB()).isDowning || var5.isFinish)) {
                            indexRow = var5.selected;
                            this.gameBC();
                        }
                    } else if (!gameDQ && ((var5 = scrMain.gameAB()).isDowning || var5.isFinish)) {
                        indexRow = var5.selected;
                        gameHL = indexMenu == 0 ? 2 : 1;
                        if (var5.isFinish) {
                            scrMain.gameAA(indexRow * scrMain.ITEM_SIZE);
                        }
                    }
                }
            }

            GameCanvas.gameAI();
            GameCanvas.gameAH();
        }

    }

}
