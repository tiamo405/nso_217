
import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class GameCanvas extends GameGraphics implements IActionListener {

    public static boolean lowGraphic = false;
    public static Image testCapcha = null;
    public static boolean gameAC = true;
    public static boolean isGPRS = true;
    public static boolean isBallEffect = false;
    public static boolean isLoading;
    public static boolean isTouch = false;
    public static boolean gameAH;
    public static boolean gameAI;
    public static boolean gameAJ;
    public static GameCanvas instance;
    public static Readmsg readMessenge;
    static boolean bRun;
    public static boolean[] keyPressedz = new boolean[14];
    private static boolean[] keyReleasedz = new boolean[14];
    public static boolean[] keyHold = new boolean[14];
    public static boolean isPointerDown;
    public static boolean isPointerClick;
    public static boolean isPointerJustRelease;
    public static int px;
    public static int py;
    public static int pxLast;
    public static int pyLast;
    public static Position[] arrPos = new Position[4];
    public static int gameTick;
    public static int taskTick;
    public static boolean isEff1;
    public static boolean isEff2;
    private static long timeTickEff1;
    private static long timeTickEff2;
    public static boolean gameCE;
    public static int w;
    public static int h;
    public static int hw;
    public static int hh;
    public static mScreen currentScreen;
    public static Menu menu = new Menu();
    public static SelectServerScr selectsvScr;
    public static LoginScr loginScr;
    public static LanguageScr languageScr;
    public static Dialog currentDialog;
    public static MsgDlg msgdlg;
    public static InputDlg inputDlg;
    public static Input2Dlg input2Dlg;
    public static Paint paintz;
    public static RegisterScr regScr;
    private static Image[] gameCF;
    private static int gameCG;
    private static int gameCH;
    public static int timeBallEffect;
    private static int[] gameCI;
    private mGraphics g = new mGraphics();
    private static boolean gameCK;
    private static int gameCL;
    private static int gameCM;
    private static int gameCN;
    private static int gameCO;
    private static int gameCP;
    private static int gameCQ;
    private static int gameCR;
    private static Image gameCS;
    private static Image gameCT;

    public static Image imgBGitem;
    public static Image imgBGitem0;
    public static Image imgBGitem1;
    public static Image imgBGitem2;
    public static Image imgBGitem3;
    public static Image imgBGitem4;

    public static Image bgitem1x1, bgitem1x2, bgitem1x3, bgitem1x4;
    public static Image bgitem2x1, bgitem2x2, bgitem2x3, bgitem2x4;
    public static Image bgitem3x1, bgitem3x2, bgitem3x3, bgitem3x4;
    public static Image bgitem4x1, bgitem4x2, bgitem4x3, bgitem4x4;
    public static Image bgitem5x1, bgitem5x2, bgitem5x3, bgitem5x4;
    public static Image bgitem6x1, bgitem6x2, bgitem6x3, bgitem6x4;
    public static Image bgitem7x1, bgitem7x2, bgitem7x3, bgitem7x4;

    public static Image[] gameBQ;
    public static int gameBR;
    public static int gameBS;
    private static int[] gameCU;
    private static int[] gameCV;
    private static int gameCW;
    private static int gameCX;
    public static int typeBg;
    private static long gameCY;
    public static int gameBU;
    private static int[] gameCZ;
    private int[] gameDA;
    private int[] gameDB;
    private int[] gameDC;
    private static int[] gameDD;
    private static int[] gameDE;
    private static int[] gameDF;
    private static Image[] gameDG;
    private static Image gameDH;
    private static Image[][] gameDI;
    private boolean gameDJ;
    public static boolean isKiemduyet;
    public static boolean isKiemduyet_info;
    public static Image gameBX;
    public static Image gameBY;
    public static Image[] gameBZ;
    private static int gameDK;
    private static int gameDL;
    private static int gameDM;
    public static int fieldBV;
    public static long fieldBW;
    public static long fieldBX;

    static {
        new MyVector();
        gameCH = 0;
        gameBQ = new Image[2];
        typeBg = -1;
        gameCY = 0L;
        new MyVector();
        isKiemduyet = false;
        isKiemduyet_info = false;
        gameDK = w;
        gameDL = 0;
    }

    public GameCanvas() {
        MotherCanvas.instance.setFullScreenMode(true);
        MotherCanvas.instance.gameAA(this);
        w = MotherCanvas.instance.gameAB();
        h = MotherCanvas.instance.gameAA();
        hw = w / 2;
        hh = h / 2;
        gameCE = System.getProperty("microedition.platform").indexOf("RIM") == 0;
        if (MotherCanvas.instance.hasPointerEvents()) {
            isTouch = true;
            if (w >= 240) {
                gameAH = true;
            }

            if (w < 320) {
                gameAI = true;
            }

            if (w >= 320) {
                gameAJ = true;
            }
        }
        int languagene;
        if ((languagene = RMS.gameAC("indLanguage")) < 0) {
            mResources.languageID = 0;
        } else {
            mResources.languageID = languagene;
        }
        mResources.gameAA();
        msgdlg = new MsgDlg();
        if (h <= 160) {
            Paint.hTab = 15;
            mScreen.cmdH = 17;
        }

        readMessenge = new Readmsg();
        instance = this;
        System.gc();
        paintz = new Paint();
        if (!lowGraphic) {
            if (gameDI == null) {
                gameDI = new Image[2][5];

                for (int var4 = 0; var4 < 2; ++var4) {
                    for (int var2 = 0; var2 < 5; ++var2) {
                        gameDI[var4][var2] = gameAC("/e/d" + var4 + var2 + ".png");
                    }
                }
            }

            this.gameDA = new int[2];
            this.gameDB = new int[2];
            this.gameDC = new int[2];
            this.gameDC[0] = this.gameDC[1] = -1;
        }

        gameAO();
        gameDH = gameAC("/u/f.png");
        if (isTouch) {
            for (int var3 = 0; var3 < 2; ++var3) {
                gameBQ[var3] = gameAC("/hd/bd" + var3 + ".png");
            }

            gameBR = mGraphics.gameAA(gameBQ[0]);
            gameBS = mGraphics.gameAB(gameBQ[0]);
            mGraphics.gameAA(gameBQ[1]);
            mGraphics.gameAB(gameBQ[1]);
        } else if (RMS.gameAC("lowGraphic") == 1) {
            lowGraphic = true;
        }
        TileMap.fieldAI();
        SmallImage.gameAC();
        if (MotherCanvas.instance.hasPointerEvents()) {
            new MyVector();
        }

        mScreen.gameAC();
        languageScr = new LanguageScr();
    }

    public static GameCanvas gameAA() {
        if (instance == null) {
            instance = new GameCanvas();
        }

        return instance;
    }

    public static void AD() {
        Session_ME.gI().gameAA11(GameMidlet.IP, GameMidlet.PORT);
    }

    public static void gameAB() {
        w = MotherCanvas.instance.gameAB();
        h = MotherCanvas.instance.gameAA();
        hw = w / 2;
        hh = h / 2;
        loginScr = new LoginScr();
        selectsvScr = new SelectServerScr();
        regScr = new RegisterScr();
        inputDlg = new InputDlg();
        input2Dlg = new Input2Dlg();
    }

    public final void AF() {

        if (fieldBV > 0) {
            if ((fieldBX = System.currentTimeMillis()) - fieldBW >= 1000L) {
                if (--fieldBV == 0) {
                    Session_ME.gI().fieldAD();
                }

                fieldBW = fieldBX;
            }
        }
//                else if (Session_ME.gI().connected && System.currentTimeMillis() - Session_ME.gI().gameAN > 300000L) {
//                    Session_ME.gI().fieldAD();
//                }

//        Char var37 = Char.getMyChar();
//        if (var37.charFocus != null && keyPressedz[5]) {
//            int distance = Res.gameAA(var37.cx, var37.cy, var37.charFocus.cx, var37.charFocus.cy);
//            if (distance >= 50) {
//                GameScr.fieldAC("MoveTo " + var37.charFocus.cName);
//                Char.fieldAC(var37.charFocus.cx, var37.charFocus.cy);
//                keyPressedz[5] = false;
//            }
//        } else if (var37.npcFocus != null && keyPressedz[5]) {
//            int distance = Res.gameAA(var37.cx, var37.cy, var37.npcFocus.cx, var37.npcFocus.cy);
//            if (distance >= 60) {
//                GameScr.fieldAC("MoveTo " + var37.npcFocus.cName);
//                Char.fieldAC(var37.npcFocus.cx, var37.npcFocus.cy);
//                keyPressedz[5] = false;
//            }
//        } else if (var37.mobFocus != null && keyPressedz[5]) {
//            int distance = Res.gameAA(var37.cx, var37.cy, var37.mobFocus.xFirst, var37.mobFocus.yFirst);
//            if (distance >= 60) {
//                GameScr.fieldAC("MoveTo " + var37.mobFocus.getTemplate().name);
//                Char.fieldAC(var37.mobFocus.xFirst, var37.mobFocus.yFirst);
//                keyPressedz[5] = false;
//            }
//        } else if (var37.itemFocus != null && keyPressedz[5]) {
//            int distance = Res.gameAA(var37.cx, var37.cy, var37.itemFocus.x, var37.itemFocus.y);
//            if (distance >= 60) {
//                GameScr.fieldAC("MoveTo " + var37.itemFocus.template.name);
//                Char.fieldAC(var37.itemFocus.x, var37.itemFocus.y);
//                keyPressedz[5] = false;
//            }
//        }
        long var1;
        if ((var1 = System.currentTimeMillis()) - timeTickEff1 >= 780L && !isEff1) {
            timeTickEff1 = var1;
            isEff1 = true;
        } else {
            isEff1 = false;
        }

        if (var1 - timeTickEff2 >= 7800L && !isEff2) {
            timeTickEff2 = var1;
            isEff2 = true;
        } else {
            isEff2 = false;
        }

        if (taskTick > 0) {
            --taskTick;
        }

        if (++gameTick > 10000) {
            if (System.currentTimeMillis() - gameCY > 20000L && currentScreen == loginScr) {
                GameMidlet.instance.notifyDestroyed();
            }

            gameTick = 0;
        }

        if (currentScreen != null) {

            if (currentDialog != null) {
                currentDialog.gameAB();
            } else if (menu.showMenu) {
                menu.gameAB();
                menu.update();
            }

            if (!isLoading) {
                currentScreen.gameAD();
            }

            currentScreen.setOffset();
        }

        long var5 = System.currentTimeMillis();
        if (Timer.isON && var5 > Timer.timeExecute) {
            Timer.isON = false;

            try {
                if (Timer.idAction > 0) {
                    GameScr.gI().perform(Timer.idAction, (Object) null);
                }
            } catch (Exception var10) {
                var10.printStackTrace();
            }
        }

        if (InfoDlg.delay > 0 && --InfoDlg.delay == 0) {
            InfoDlg.gameAB();
        }

        if (this.gameDJ) {
            this.fieldAE();
        }

        MotherCanvas.instance.repaint();
        MotherCanvas.instance.serviceRepaints();

    }

    public final void fieldAE() {
        this.gameDJ = false;
        selectsvScr.update();

        try {
            Char.gameAE();
            GameScr.gameAO();
            GameScr.setPasswordTest();
            gameAJ();
            InfoDlg.gameAB();
            GameScr.gameAA(true);
            GameScr.cmx = 100;
            setMaxTextLenght(TileMap.bgID);
            GameScr.vParty.removeAllElements();
            GameScr.vClan.removeAllElements();
            GameScr.vFriend.removeAllElements();
            GameScr.vEnemies.removeAllElements();
            Char.clan = null;
        } catch (Exception var9) {
            var9.printStackTrace();
        }
    }

    public static void gameAD() {
        if (isBallEffect && --timeBallEffect < 0) {
            isBallEffect = false;
        }

    }

    public static void setOffset() {
        if (!lowGraphic) {
            if (gameCS != null) {
                for (int var0 = 0; var0 < gameCU.length; ++var0) {
                    if (gameTick % (var0 + 2 << 3) == 0) {
                        int var10002 = gameCU[var0]++;
                        if (gameCU[var0] > GameScr.gW + (mGraphics.gameAA(gameCS) >> 1)) {
                            gameCU[var0] = -(mGraphics.gameAA(gameCS) >> 1);
                        }
                    }
                }

            }
        }
    }

    public static void paint(mGraphics var0) {
        if (!CodePhu.fieldAA(var0)) {
            if (isBallEffect) {
                if (gameTick % 10 > 7) {
                    var0.gameAA(16777215);
                } else {
                    var0.gameAA(0);
                }

                var0.gameAC(0, 0, GameScr.gW, GameScr.gH);
            } else {
                if (gameCK && !lowGraphic && gameCF != null) {
                    var0.gameAA(gameCG);
                    var0.gameAC(0, 0, GameScr.gW, gameCL);
                    int var1;
                    if (typeBg >= 0 && typeBg <= 1) {
                        if (gameCF[0] != null) {
                            for (var1 = -((GameScr.cmx >> 1) % 24); var1 < GameScr.gW; var1 += 24) {
                                var0.gameAA(gameCF[0], var1, gameCM, 0);
                            }
                        }

                        if (gameCF[1] != null) {
                            for (var1 = -((GameScr.cmx >> 2) % 24); var1 < GameScr.gW; var1 += 24) {
                                var0.gameAA(gameCF[1], var1, gameCN, 0);
                            }
                        }

                        if (gameCF[3] != null) {
                            for (var1 = -((GameScr.cmx >> 4) % 64); var1 < GameScr.gW; var1 += 64) {
                                var0.gameAA(gameCF[3], var1, gameCP, 0);
                            }
                        }

                        if (gameCT != null) {
                            var0.gameAA(gameCT, gameCW, gameCX, 3);
                        }

                        if (gameCS != null) {
                            for (var1 = 0; var1 < 2; ++var1) {
                                var0.gameAA(gameCS, gameCU[var1], gameCV[var1], 3);
                            }
                        }

                        if (gameCF[2] != null) {
                            for (var1 = -((GameScr.cmx >> 3) % 192); var1 < GameScr.gW; var1 += 192) {
                                var0.gameAA(gameCF[2], var1, gameCO, 0);
                            }

                            return;
                        }
                    } else if (typeBg >= 2 && typeBg <= 6) {
                        if (gameCT != null) {
                            var0.gameAA(gameCT, gameCW, gameCX, 3);
                        }

                        if (gameCS != null) {
                            for (var1 = 0; var1 < gameCU.length; ++var1) {
                                var0.gameAA(gameCS, gameCU[var1], gameCV[var1], 3);
                            }
                        }

                        if (typeBg != 2) {
                            if (gameCF[3] != null) {
                                for (var1 = -((GameScr.cmx >> gameCZ[3]) % gameCI[3]); var1 < GameScr.gW; var1 += gameCI[3]) {
                                    var0.gameAA(gameCF[3], var1, gameCP, 0);
                                }
                            }

                            if (gameCF[2] != null) {
                                for (var1 = -((GameScr.cmx >> gameCZ[2]) % gameCI[2]); var1 < GameScr.gW; var1 += gameCI[2]) {
                                    var0.gameAA(gameCF[2], var1, gameCO, 0);
                                }
                            }

                            if (gameCF[1] != null) {
                                for (var1 = -((GameScr.cmx >> gameCZ[1]) % gameCI[1]); var1 < GameScr.gW; var1 += gameCI[1]) {
                                    var0.gameAA(gameCF[1], var1, gameCR, 0);
                                }
                            }

                            if (gameCF[0] != null) {
                                for (var1 = -((GameScr.cmx >> gameCZ[0]) % gameCI[0]); var1 < GameScr.gW; var1 += gameCI[0]) {
                                    var0.gameAA(gameCF[0], var1, gameCQ, 0);
                                }

                                return;
                            }
                        }
                    } else if (typeBg >= 7 && typeBg <= 16) {
                        var0.gameAA(gameCG);
                        var0.gameAC(0, 0, GameScr.gW, GameScr.gH);
                        if (typeBg != 8 && gameCF[3] != null) {
                            for (var1 = -((GameScr.cmx >> gameCZ[3]) % gameCI[3]); var1 < GameScr.gW; var1 += gameCI[3]) {
                                if (typeBg != 11 && typeBg != 12) {
                                    var0.gameAA(gameCF[3], var1, gameCP, 0);
                                } else {
                                    var0.gameAA(gameCF[3], var1, GameScr.gH - mGraphics.gameAB(gameCF[3]), 0);
                                }
                            }
                        }

                        if (typeBg != 8 && typeBg != 11 && typeBg != 12 && gameCF[2] != null) {
                            if (TileMap.mapID == 45) {
                                var0.gameAA(gameCF[2], GameScr.gW, gameCO, 0);
                            } else {
                                for (var1 = -((GameScr.cmx >> gameCZ[2]) % gameCI[2]); var1 < GameScr.gW; var1 += gameCI[2]) {
                                    if (typeBg == 14) {
                                        var0.gameAA(gameCF[2], var1, gameCO + 12, 0);
                                    } else {
                                        var0.gameAA(gameCF[2], var1, gameCO, 0);
                                    }
                                }
                            }
                        }

                        if (typeBg != 11 && typeBg != 12 && gameCF[1] != null && TileMap.mapID != 52) {
                            for (var1 = -((GameScr.cmx >> gameCZ[1]) % gameCI[1]); var1 < GameScr.gW; var1 += gameCI[1]) {
                                var0.gameAA(gameCF[1], var1, gameCR, 0);
                            }
                        }

                        if (TileMap.mapID == 45 || TileMap.mapID == 55) {
                            var0.gameAA(1114112);
                            var0.gameAC(0, gameCQ + 20, GameScr.gW, GameScr.gH);
                        }

                        if (gameCF[0] != null) {
                            for (var1 = -((GameScr.cmx >> gameCZ[0]) % gameCI[0]); var1 < GameScr.gW; var1 += gameCI[0]) {
                                var0.gameAA(gameCF[0], var1, gameCQ, 0);
                            }
                        }

                        if (gameCS != null) {
                            if (typeBg != 13 && typeBg != 15) {
                                for (var1 = 0; var1 < 2; ++var1) {
                                    var0.gameAA(gameCS, gameCU[var1], gameCV[var1], 3);
                                }

                                return;
                            }

                            for (var1 = 0; var1 < 2; ++var1) {
                                var0.gameAA(gameCS, gameCU[var1], gameCV[var1] - 130, 3);
                            }

                            return;
                        }
                    }
                } else {
                    var0.gameAA(gameCG);
                    var0.gameAC(0, 0, GameScr.gW, GameScr.gH);
                }

            }
        }
    }

    public static void setPasswordTest() {
        gameCF = null;
        gameCS = null;
        gameCT = null;
    }

    public static void setMaxTextLenght(int var0) {
        byte var1;
        byte var2;
        byte var3;
        label217:
        {
            var1 = 0;
            var2 = 0;
            var3 = 0;
            typeBg = var0;
            switch (var0) {
                case 0:
                case 1:
                default:
                    break label217;
                case 2:
                    gameCZ = new int[]{1, 2, 3, 4};
                    break label217;
                case 3:
                    gameCZ = new int[]{1, 2, 3, 4};
                    break label217;
                case 4:
                    var1 = 9;
                    var2 = 6;
                    break;
                case 5:
                    gameCZ = new int[]{1, 1, 1, 1};
                    break label217;
                case 6:
                    var1 = 12;
                    break;
                case 7:
                    gameCZ = new int[]{1, 2, 3, 4};
                    break label217;
                case 8:
                    gameCZ = new int[]{1, 2, 3, 4};
                    break label217;
                case 9:
                    var1 = 16;
                    var2 = 10;
                    var3 = 6;
                    break;
                case 10:
                    gameCZ = new int[]{1, 1, 1, 1};
                    break label217;
                case 11:
                    gameCZ = new int[]{1, 2, 3, 4};
                    break label217;
                case 12:
                    gameCZ = new int[]{1, 2, 3, 4};
                    break label217;
                case 13:
                    var1 = 60;
                    break;
                case 14:
                    gameCZ = new int[]{1, 2, 3, 4};
                    break label217;
                case 15:
                    gameCZ = new int[]{1, 2, 3, 4};
                    break label217;
                case 16:
            }

            gameCZ = new int[]{1, 2, 3, 4};
        }

        gameCG = StaticObj.SKYCOLOR[typeBg];

        int var4;
        try {
            if (!lowGraphic) {
                gameCF = new Image[4];
                gameCI = new int[4];

                for (var4 = 0; var4 < 4; ++var4) {
                    try {
                        if (StaticObj.TYPEBG[typeBg][var4] != -1) {
                            gameCF[var4] = gameAC("/bg/bg" + var4 + StaticObj.TYPEBG[typeBg][var4] + ".png");
                        }
                    } catch (Exception var6) {
                        var6.printStackTrace();
                    }

                    if (gameCF[var4] != null) {
                        gameCI[var4] = mGraphics.gameAA(gameCF[var4]);
                    }
                }

                if (typeBg == 10) {
                    gameCF[1] = gameAC("/bg/bg09.png");
                    gameCF[2] = gameAC("/bg/bg09.png");
                    gameCI[1] = mGraphics.gameAA(gameCF[1]);
                    gameCI[2] = mGraphics.gameAA(gameCF[2]);
                }

                if (typeBg == 12) {
                    gameCF[3] = gameAC("/bg/bg39.png");
                    gameCI[3] = mGraphics.gameAA(gameCF[3]);
                }

                if (typeBg == 14) {
                    if (isTouch) {
                        gameCR = (gameCQ = h - mGraphics.gameAB(gameCF[0])) - mGraphics.gameAB(gameCF[1]);
                    } else {
                        gameCR = (gameCQ = h - mGraphics.gameAB(gameCF[0]) - 45) - mGraphics.gameAB(gameCF[1]);
                    }
                }

                if (typeBg == 15 && isTouch) {
                    gameCR = (gameCQ = h - mGraphics.gameAB(gameCF[0])) - mGraphics.gameAB(gameCF[1]) + 100;
                }

                if (typeBg == 16) {
                    if (isTouch) {
                        gameCR = (gameCQ = h - mGraphics.gameAB(gameCF[0])) - mGraphics.gameAB(gameCF[1]) + 100;
                    } else {
                        gameCR = (gameCQ = h - mGraphics.gameAB(gameCF[0]) - 40) - mGraphics.gameAB(gameCF[1]) + 100;
                    }
                }
            }

            if (typeBg >= 0 && typeBg <= 1) {
                gameCS = gameAC("/bg/cl0.png");
                gameCT = gameAC("/bg/sun0.png");
            } else {
                gameCS = null;
                gameCT = null;
            }

            if (typeBg == 2) {
                gameCS = gameAC("/bg/cl1.png");
                gameCT = gameAC("/bg/sun1.png");
            }

            if (typeBg == 7 || typeBg == 11 || typeBg == 12) {
                if (TileMap.mapID == 20) {
                    gameCS = null;
                } else {
                    gameCS = gameAC("/bg/cl0.png");
                }
            }

            if (var0 == 13 || var0 == 15) {
                gameCS = gameAC("/bg/cl2.png");
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        gameCK = false;
        if (!lowGraphic) {
            gameCK = true;
            if (gameCF[0] != null && gameCF[1] != null && gameCF[2] != null) {
                gameCL = GameScr.gH - (mGraphics.gameAB(gameCF[0]) + mGraphics.gameAB(gameCF[1]) + mGraphics.gameAB(gameCF[2])) + 11;
            }

            if (gameCF[0] != null) {
                gameCM = GameScr.gH - mGraphics.gameAB(gameCF[0]);
            }

            if (gameCF[1] != null) {
                gameCN = gameCM - mGraphics.gameAB(gameCF[1]);
            }

            if (gameCF[2] != null) {
                gameCO = gameCN - mGraphics.gameAB(gameCF[2]);
            }

            if (gameCF[3] != null) {
                gameCP = gameCN - mGraphics.gameAB(gameCF[3]) - 10;
            }

            if (typeBg >= 2 && typeBg <= 13) {
                gameCQ = var4 = GameScr.gH - mGraphics.gameAB(gameCF[0]);
                if (gameCF[1] != null) {
                    var4 = var4 - mGraphics.gameAB(gameCF[1]) + var1;
                }

                gameCR = var4;
                if (gameCF[3] != null) {
                    var4 = var4 - mGraphics.gameAB(gameCF[3]) + var3;
                }

                gameCP = var4;
                gameCL = var4;
                if (gameCF[2] != null) {
                    gameCO = gameCR - mGraphics.gameAB(gameCF[2]) + var2;
                }

                if (typeBg == 2) {
                    gameCL = h;
                }
            }
        }

        boolean var8 = false;
        if (typeBg >= 2 && typeBg <= 12) {
            var4 = 2 * GameScr.gH / 3 - gameCR;
        } else {
            var4 = 2 * GameScr.gH / 3 - gameCN;
        }

        if (var4 < 0) {
            var4 = 0;
        }

        if (TileMap.mapID == 48 && TileMap.mapID == 51) {
            gameCQ += var4;
        }

        if (typeBg >= 2 && typeBg <= 6) {
            gameCR += var4;
        }

        gameCL += var4;
        gameCM += var4;
        gameCN += var4;
        gameCO += var4;
        gameCP += var4;
        gameCW = 3 * GameScr.gW / 4;
        gameCX = gameCL / 3;
        gameCU = new int[2];
        gameCV = new int[2];
        gameCU[0] = GameScr.gW / 3;
        gameCV[0] = gameCL / 2 - 8;
        gameCU[1] = 2 * GameScr.gW / 3;
        gameCV[1] = gameCL / 2 + 8;
        if (typeBg == 2) {
            gameCX = gameCL / 5;
            gameCU = new int[5];
            gameCV = new int[5];
            gameCU[0] = GameScr.gW / 3;
            gameCV[0] = gameCL / 3 - 35;
            gameCU[1] = 3 * GameScr.gW / 4;
            gameCV[1] = gameCL / 3 + 12;
            gameCU[2] = GameScr.gW / 3 - 15;
            gameCV[2] = gameCL / 3 + 12;
            gameCU[3] = GameScr.gW / 15;
            gameCV[3] = gameCL / 2 + 12;
            gameCU[4] = 2 * GameScr.gW / 3 + 25;
            gameCV[4] = gameCL / 3 + 12;
        }

        if (!lowGraphic) {
            if (typeBg == 8) {
                gameCQ = gameCR = GameScr.gH2 - 50;
            }

            if (typeBg == 10 && gameCF[3] != null) {
                gameCP = gameCO - mGraphics.gameAB(gameCF[3]);
            }

            if (typeBg == 11 || typeBg == 12) {
                gameCP = 0;
            }
        }

    }

    protected final void setIputType(int var1) {
        gameCY = System.currentTimeMillis();
        if (var1 >= 48 && var1 <= 57 || var1 >= 65 && var1 <= 122 || var1 == 10 || var1 == 8 || var1 == 13 || var1 == 32) {
            gameBU = var1;
        }

        if (currentDialog != null) {
            currentDialog.gameAA(var1);
            gameBU = 0;
        } else {
            currentScreen.gameAA(var1);
            switch (var1) {
                case -39:
                case -2:

                    keyHold[8] = true;
                    keyPressedz[8] = true;
                    return;
                case -38:
                case -1:

                    keyHold[2] = true;
                    keyPressedz[2] = true;
                    return;
                case -22:
                case -7:
                    keyHold[13] = true;
                    keyPressedz[13] = true;
                    return;
                case -21:
                case -6:
                    keyHold[12] = true;
                    keyPressedz[12] = true;
                    return;
                case -5:
                case 10:

                    keyHold[5] = true;
                    keyPressedz[5] = true;
                    return;
                case -4:

                    keyHold[6] = true;
                    keyPressedz[6] = true;
                    return;
                case -3:

                    keyHold[4] = true;
                    keyPressedz[4] = true;
                    return;
                case 35:
                    keyHold[11] = true;
                    keyPressedz[11] = true;
                    return;
                case 42:
                    keyHold[10] = true;
                    keyPressedz[10] = true;
                    return;
                case 48:
                    keyHold[0] = true;
                    keyPressedz[0] = true;
                    return;
                case 49:
                    if (currentScreen == GameScr.instance && gameAC && !ChatTextField.gameAA().isShow) {
                        keyHold[1] = true;
                        keyPressedz[1] = true;
                    }

                    return;
                case 50:
                    if (currentScreen == GameScr.instance && gameAC && !ChatTextField.gameAA().isShow && !GameScr.gameDN) {
                        keyHold[2] = true;
                        keyPressedz[2] = true;
                    }

                    return;
                case 51:
                    if (currentScreen == GameScr.instance && gameAC && !ChatTextField.gameAA().isShow) {
                        keyHold[3] = true;
                        keyPressedz[3] = true;
                    }

                    return;
                case 52:
                    if (currentScreen == GameScr.instance && gameAC && !ChatTextField.gameAA().isShow && !GameScr.gameDN) {
                        keyHold[4] = true;
                        keyPressedz[4] = true;
                    }

                    return;
                case 53:
                    if (currentScreen == GameScr.instance && gameAC && !ChatTextField.gameAA().isShow && !GameScr.gameDN) {
                        keyHold[5] = true;
                        keyPressedz[5] = true;
                    }
                default:
                    return;
                case 54:
                    if (currentScreen == GameScr.instance && gameAC && !ChatTextField.gameAA().isShow && !GameScr.gameDN) {
                        keyHold[6] = true;
                        keyPressedz[6] = true;
                    }

                    return;
                case 55:
                    keyHold[7] = true;
                    keyPressedz[7] = true;
                    return;
                case 56:
                    if (currentScreen == GameScr.instance && gameAC && !ChatTextField.gameAA().isShow && !GameScr.gameDN) {
                        keyHold[8] = true;
                        keyPressedz[8] = true;
                    }

                    return;
                case 57:
                    keyHold[9] = true;
                    keyPressedz[9] = true;
            }
        }
    }

    protected final void gameAD(int var1) {
        gameBU = 0;
        switch (var1) {
            case -39:
            case -2:
                keyHold[8] = false;
                return;
            case -38:
            case -1:
                keyHold[2] = false;
                return;
            case -22:
            case -7:
                keyHold[13] = false;
                keyReleasedz[13] = true;
                return;
            case -21:
            case -6:
                keyHold[12] = false;
                keyReleasedz[12] = true;
                return;
            case -5:
            case 10:
                keyHold[5] = false;
                keyReleasedz[5] = true;
                return;
            case -4:
                keyHold[6] = false;
                return;
            case -3:
                keyHold[4] = false;
                return;
            case 35:
                keyHold[11] = false;
                keyReleasedz[11] = true;
                return;
            case 42:
                keyHold[10] = false;
                keyReleasedz[10] = true;
                return;
            case 48:
                keyHold[0] = false;
                keyReleasedz[0] = true;
                return;
            case 49:
                if (currentScreen == GameScr.instance && gameAC && !ChatTextField.gameAA().isShow) {
                    keyHold[1] = false;
                    keyReleasedz[1] = true;
                }

                return;
            case 50:
                if (currentScreen == GameScr.instance && gameAC && !ChatTextField.gameAA().isShow) {
                    keyHold[2] = false;
                    keyReleasedz[2] = true;
                }

                return;
            case 51:
                if (currentScreen == GameScr.instance && gameAC && !ChatTextField.gameAA().isShow) {
                    keyHold[3] = false;
                    keyReleasedz[3] = true;
                }

                return;
            case 52:
                if (currentScreen == GameScr.instance && gameAC && !ChatTextField.gameAA().isShow) {
                    keyHold[4] = false;
                    keyReleasedz[4] = true;
                }

                return;
            case 53:
                if (currentScreen == GameScr.instance && gameAC && !ChatTextField.gameAA().isShow) {
                    keyHold[5] = false;
                    keyReleasedz[5] = true;
                }
            default:
                return;
            case 54:
                if (currentScreen == GameScr.instance && gameAC && !ChatTextField.gameAA().isShow) {
                    keyHold[6] = false;
                    keyReleasedz[6] = true;
                }

                return;
            case 55:
                keyHold[7] = false;
                keyReleasedz[7] = true;
                return;
            case 56:
                if (currentScreen == GameScr.instance && gameAC && !ChatTextField.gameAA().isShow) {
                    keyHold[8] = false;
                    keyReleasedz[8] = true;
                }

                return;
            case 57:
                keyHold[9] = false;
                keyReleasedz[9] = true;
        }
    }

    protected final void gameAA(int var1, int var2) {
        if (Res.abs(var1 - pxLast) >= 10 || Res.abs(var2 - pyLast) >= 10) {
            isPointerClick = false;
        }

        px = var1;
        py = var2;
        if (++gameCH > 3) {
            gameCH = 0;
        }

        arrPos[gameCH] = new Position(var1, var2);
    }

    public static boolean gameAG() {
        return System.currentTimeMillis() - gameCY >= 800L;
    }

    protected final void gameAB(int var1, int var2) {
        isPointerDown = true;
        isPointerClick = true;
        gameCY = System.currentTimeMillis();
        pxLast = var1;
        pyLast = var2;
        px = var1;
        py = var2;
    }

    protected final void gameAC(int var1, int var2) {
        isPointerDown = false;
        isPointerJustRelease = true;
        mScreen.keyTouch = -1;
        px = var1;
        py = var2;
    }

    public static boolean gameAA(int var0, int var1, int var2, int var3) {
        int var4 = px + GameScr.cmx;
        int var5 = GameScr.cmy + py;
        if (!isPointerDown && !isPointerJustRelease) {
            return false;
        } else {
            return var4 >= var0 && var4 <= var0 + var2 && var5 >= var1 && var5 <= var1 + var3;
        }
    }

    public static boolean gameAA(int var0, int var1, int var2, int var3, Scroll var4) {
        int var5 = px + var4.cmx;
        int var6 = var4.cmy + py;
        if (!isPointerDown && !isPointerJustRelease) {
            return false;
        } else {
            return var5 >= var0 && var5 <= var0 + var2 && var6 >= var1 && var6 <= var1 + var3;
        }
    }

    public static boolean gameAB(int var0, int var1, int var2, int var3) {
        return (isPointerDown || isPointerJustRelease) && px >= var0 && px <= var0 + var2 && py >= var1 && py <= var1 + var3;

    }

    public static void gameAH() {
        for (int var0 = 0; var0 < 14; ++var0) {
            keyPressedz[var0] = false;
        }

        isPointerJustRelease = false;
    }

    public static void gameAI() {
        for (int var0 = 0; var0 < 14; ++var0) {
            keyHold[var0] = false;
        }

    }

    protected final void gameAA(Graphics var1) {
        this.g.gameAA = var1;

        try {
            if (currentScreen != null && !isLoading) {
                currentScreen.paint(this.g);
                this.g.gameAD(0, 0, w, h);
            }

            this.g.gameAA(-this.g.gameAA(), -this.g.gameAB());
            this.g.gameAD(0, 0, w, h);
            InfoDlg.gameAA(this.g);
            if (currentDialog != null) {
                currentDialog.paint(this.g);
            } else if (menu.showMenu) {
                menu.paint(this.g);
            }

            GameScr.gameAB(this.g);

            if (fieldBV > 0) {
                Paint.gameAA(30, h - 118, w - 60, 80, this.g);
                gameAA(hw, h - 98, this.g, true);
                mFont.tahoma_8b.gameAA(this.g, "Xin chờ " + fieldBV + "s...", hw, h - 78, 2);
                return;
            }

            mGraphics var3 = this.g;
            if (isKiemduyet) {
                if ((gameDM = mFont.tahoma_7b_yellow.gameAA(mResources.gameXH)) > w && --gameDK < -440) {
                    gameDK = w;
                }

                var3.gameAA(0);
                var3.gameAC(0, 0, w, 16);
                mFont.tahoma_7b_yellow.gameAA(var3, mResources.gameXH, gameDK + 20, 3, 0);
                var3.gameAA(gameBX, 0, 0, 0);
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }
    }

    public static void gameAJ() {
        inputDlg.tfInput.setMaxTextLenght(500);
        input2Dlg.tfInput.setMaxTextLenght(500);
        input2Dlg.tfInput2.setMaxTextLenght(500);
        currentDialog = null;
    }

    public static void setText(String var0) {
        msgdlg.gameAA(var0, (Command) null, new Command(mResources.gameCX, instance, 8882, (Object) null), (Command) null);
        currentDialog = msgdlg;
    }

    public static void gameAB(String var0) {
        msgdlg.gameAA(var0, (Command) null, new Command(mResources.gameED, instance, 8882, (Object) null), (Command) null);
        currentDialog = msgdlg;
        msgdlg.isWait = true;
    }

    public static void gameAK() {
        gameAB(mResources.PLEASEWAIT);
    }

    public static void gameAL() {
        msgdlg.timeShow = 500;
        msgdlg.gameAA(mResources.PLEASEWAIT, (Command) null, (Command) null, (Command) null);
        currentDialog = msgdlg;
        msgdlg.isWait = true;
    }

    public final void gameAA(String var1, String var2, String var3, String var4) {
        msgdlg.gameAA(var4, new Command(var1, this, 8881, var3), (Command) null, new Command(var2, this, 8882, (Object) null));
        currentDialog = msgdlg;
    }

    public final void gameAA(String var1, String var2, short var3, String var4, String var5) {
        MyVector var6;
        (var6 = new MyVector()).addElement(new Short(var3));
        var6.addElement(var4);
        msgdlg.gameAA(var5, new Command(var1, this, 8883, var6), (Command) null, new Command(var2, this, 8882, (Object) null));
        currentDialog = msgdlg;
    }

    public static void gameAA(String var0, int var1, Object var2) {
        msgdlg.gameAA(var0, (Command) null, new Command(mResources.gameCX, instance, var1, (Object) null, w / 2 - 35, h - 50), (Command) null);
        currentDialog = msgdlg;
    }

    public static void gameAA(String var0, int var1, Object var2, int var3, Object var4) {
        (new StringBuffer("yeso no dilog ")).append(var3).toString();
        msgdlg.gameAA(var0, new Command(mResources.gameCH, instance, var1, var2), new Command("", instance, var1, var2), new Command(mResources.gameCU, instance, var3, var4));
        currentDialog = msgdlg;
    }

    public static void gameAA(String var0, Command var1, Command var2) {
        msgdlg.gameAA(var0, var1, (Command) null, var2);
        currentDialog = msgdlg;
    }

    public static Image gameAC(String var0) {
        var0 = "/x" + mGraphics.zoomLevel + var0;
        Image var1 = null;

        try {
            var1 = Image.createImage(var0);
        } catch (IOException var2) {
        }

        return var1;
    }

    public final boolean gameAA(int var1, int var2, int var3) {
        if (lowGraphic) {
            return false;
        } else {
            var1 = var1 == 1 ? 0 : 1;
            if (this.gameDC[var1] != -1) {
                return false;
            } else {
                this.gameDC[var1] = 0;
                this.gameDA[var1] = var2;
                this.gameDB[var1] = var3;
                return true;
            }
        }
    }

    private static void gameAO() {
        if (!lowGraphic) {
            gameDG = new Image[3];

            for (int var0 = 0; var0 < 3; ++var0) {
                gameDG[var0] = gameAC("/e/w" + var0 + ".png");
            }

            gameDD = new int[2];
            gameDE = new int[2];
            (gameDF = new int[2])[0] = gameDF[1] = -1;
        }
    }

    public static boolean gameAD(int var0, int var1) {
        if (lowGraphic) {
            return false;
        } else {
            int var2 = gameDF[0] == -1 ? 0 : 1;
            if (gameDF[var2] != -1) {
                return false;
            } else {
                gameDF[var2] = 0;
                gameDD[var2] = var0;
                gameDE[var2] = var1;
                return true;
            }
        }
    }

    public final void gameAM() {
        if (!lowGraphic) {
            for (int var1 = 0; var1 < 2; ++var1) {
                if (this.gameDC[var1] != -1) {
                    int var10002 = this.gameDC[var1]++;
                    if (this.gameDC[var1] >= 5) {
                        this.gameDC[var1] = -1;
                    }

                    if (var1 == 0) {
                        var10002 = this.gameDA[var1]--;
                    } else {
                        var10002 = this.gameDA[var1]++;
                    }

                    var10002 = this.gameDB[var1]--;
                }
            }

        }
    }

    public static boolean gameAE(int var0, int var1) {
        if (var0 < GameScr.cmx) {
            return false;
        } else if (var0 > GameScr.cmx + GameScr.gW) {
            return false;
        } else if (var1 < GameScr.cmy) {
            return false;
        } else {
            return var1 <= GameScr.cmy + GameScr.gH + 30;
        }
    }

    public final void gameAB(mGraphics var1) {
        if (!lowGraphic) {
            for (int var2 = 0; var2 < 2; ++var2) {
                if (this.gameDC[var2] != -1 && gameAE(this.gameDA[var2], this.gameDB[var2])) {
                    var1.gameAA(gameDI[var2][this.gameDC[var2]], this.gameDA[var2], this.gameDB[var2], 3);
                }
            }

        }
    }

    public static void gameAA(int var0, int var1, mGraphics var2, boolean var3) {
        int var4 = gameTick % 3;
        var2.gameAA(gameDH, 0, var4 << 4, 16, 16, 0, var0, var1, 3);
    }

    public final void gameAN() {
        isLoading = false;
        this.gameDJ = true;
    }

    public static boolean gameAC(int var0, int var1, int var2, int var3) {
        if (!isPointerDown && !isPointerJustRelease) {
            return false;
        } else {
            return px >= var0 && px <= var0 + var2 && py >= var1 && py <= var1 + var3;
        }
    }

    public final void perform(int var1, Object var2) {
        String var11;
        short var12;
        Char var13;
        String var14;
        int var16;
        Item[] var21;
        switch (var1) {
            case 1608:
                Service.gI().ChucTet(input2Dlg.tfInput.getText(), input2Dlg.tfInput2.getText(), (byte) 0);
                gameAJ();
                return;
            case 1700:
                Service.gI().luckyDraw((short) 100, inputDlg.tfInput.getText(), GameScr.gameGP);
                gameAJ();
                break;
            case 8880:
                gameAJ();
                languageScr.update();
                return;
            case 8881:
                NinjaUtil.gameAB(var11 = (String) var2);
                currentDialog = null;
                return;
            case 8882:
                currentDialog = null;
                return;
            case 8883:
                var12 = ((Short) ((MyVector) var2).elementAt(0)).shortValue();
                NinjaUtil.gameAA((String) ((MyVector) var2).elementAt(0), var12);
                currentDialog = null;
                return;
            case 8884:
                gameAJ();
                selectsvScr.update();
                return;
            case 8885:
                GameMidlet.instance.notifyDestroyed();
                return;
            case 8886:
                gameAA(mResources.gameXB, new Command(mResources.gameXG, instance, 8880, (Object) null), new Command(mResources.gameCU, instance, 8882, (Object) null));
                return;
            case 8887:
                gameAJ();
                var16 = ((Integer) var2).intValue();
                Service.gI().addPartyAccept(var16);
                return;
            case 8888:
                var16 = ((Integer) var2).intValue();
                Service.gI().addPartyCancel(var16);
                gameAJ();
                return;
            case 8889:
                var11 = (String) var2;
                gameAJ();
                Service.gI().acceptPleaseParty(var11);
                return;
            case 8890:
                gameAJ();
                Service.gI().sendUIConfirmID(((Integer) var2).intValue());
                return;
            case 8891:
                Service.gI().sendUIConfirmID(0);
                currentDialog = null;
                return;
            case 16081:
                Service.gI().ChucTet(input2Dlg.tfInput.getText(), input2Dlg.tfInput2.getText(), (byte) 1);
                gameAJ();
                return;
            case 88810:
                var16 = ((Integer) var2).intValue();
                gameAJ();
                Service.gI().acceptInviteTrade(var16);
                return;
            case 88811:
                gameAJ();
                Service.gI().cancelInviteTrade();
                return;
            case 88812:
                var13 = (Char) var2;
                gameAJ();
                Service.gI().acceptInviteTest(var13.charID);
                return;
            case 88813:
                gameAJ();
                var21 = (Item[]) var2;
                Service.gI().crystalCollect(var21);
                return;
            case 88814:
                var21 = (Item[]) var2;
                gameAJ();
                Service.gI().crystalCollectLock(var21);
                return;
            case 88815:
                GameScr.gI();
                GameScr.gameBG();
                return;
            case 88816:
                Service.gI().sendCardInfo(input2Dlg.tfInput.getText(), input2Dlg.tfInput2.getText());
                gameAJ();
                return;
            case 88817:
                if (Char.getMyChar().npcFocus != null) {
                    Service.gI().menu((byte) 0, Char.getMyChar().npcFocus.template.npcTemplateId, menu.menuSelectedItem, 0);
                    return;
                }

                Service.gI().menu((byte) 0, 0, menu.menuSelectedItem, 0);
                return;
            case 88818:
                var12 = ((Short) var2).shortValue();
                Service.gI().textBoxId(var12, inputDlg.tfInput.getText());
                gameAJ();
                return;
            case 88819:
                var12 = ((Short) var2).shortValue();
                Service.gI().menuId(var12);
                GameScr.gI().gameBH();
                return;
            case 88820:
                String[] var20 = (String[]) var2;
                if (Char.getMyChar().npcFocus == null) {
                    return;
                }

                Integer var18 = new Integer(menu.menuSelectedItem);
                if (var20.length <= 1) {
                    ChatPopup.addChatPopup("", 1, Char.getMyChar().npcFocus);
                    Service.gI().menu((byte) 0, Char.getMyChar().npcFocus.template.npcTemplateId, var18.intValue(), 0);
                    return;
                }

                MyVector var17 = new MyVector();

                for (int var19 = 0; var19 < var20.length - 1; ++var19) {
                    var17.addElement(new Command(var20[var19 + 1], instance, 88821, var18));
                }

                menu.gameAA(var17);
                return;
            case 88821:
                int var15 = ((Integer) var2).intValue();
                ChatPopup.addChatPopup("", 1, Char.getMyChar().npcFocus);
                Service.gI().menu((byte) 0, Char.getMyChar().npcFocus.template.npcTemplateId, var15, menu.menuSelectedItem);
                return;
            case 88822:
                ChatPopup.addChatPopup("", 1, Char.getMyChar().npcFocus);
                Service.gI().menu((byte) 0, Char.getMyChar().npcFocus.template.npcTemplateId, menu.menuSelectedItem, 0);
                return;
            case 88823:
                setText(mResources.gameFS);
                return;
            case 88824:
                setText(mResources.gameFT);
                return;
            case 88825:
                setText(mResources.gameUT);
                return;
            case 88826:
                setText(mResources.gameUV);
                return;
            case 88827:
                setText(mResources.gameUU);
                return;
            case 88828:
                setText(mResources.gameUW);
                return;
            case 88829:
                String var3;
                if ((var3 = inputDlg.tfInput.getText()).equals("")) {
                    return;
                }

                Service.gI().changeName(var3, ((Integer) var2).intValue());
                gameAB(mResources.PLEASEWAIT);
                return;
            case 88830:
                var16 = ((Integer) var2).intValue();
                gameAJ();
                Service.gI().acceptInviteClan(var16);
                return;
            case 88831:
                var16 = ((Integer) var2).intValue();
                gameAJ();
                Service.gI().acceptPleaseClan(var16);
                return;
            case 88832:
                var11 = inputDlg.tfInput.getText();
                gameAJ();
                if (!var11.equals("")) {
                    Service.gI().changeClanAlert(var11);
                    return;
                }
                break;
            case 88833:
                var11 = inputDlg.tfInput.getText();
                gameAJ();
                if (!var11.equals("")) {
                    try {
                        var16 = Integer.parseInt(var11);
                        if (Char.getMyChar().xu >= var16 && var16 >= 0) {
                            Service.gI().inputCoinClan(var16);
                            return;
                        }

                        InfoMe.gameAA(mResources.gameUX, 20, mFont.tahoma_7_yellow);
                        return;
                    } catch (Exception var10) {
                        return;
                    }
                }
                break;
            case 88834:
                var11 = inputDlg.tfInput.getText();
                gameAJ();
                if (!var11.equals("")) {
                    try {
                        if ((var16 = Integer.parseInt(var11)) <= 0) {
                            return;
                        }

                        Service.gI().outputCoinClan(var16);
                        return;
                    } catch (Exception var9) {
                        return;
                    }
                }
                break;
            case 88835:
                var16 = Integer.parseInt((String) var2);
                var1 = Integer.parseInt(inputDlg.tfInput.getText());
                currentDialog = null;
                if (var1 >= 1 && var1 < Char.getMyChar().arrItemBag[var16].quantity) {
                    Service.gI().inputNumSplit(var16, var1);
                    return;
                }

                setText(mResources.gameUY);
                return;
            case 88836:
                inputDlg.tfInput.setMaxTextLenght(6);
                inputDlg.gameAA(mResources.gameTX, new Command(mResources.gameEC, instance, 888361, (Object) null), 1);
                return;
            case 88837:
                var11 = inputDlg.tfInput.getText();
                gameAJ();

                try {
                    Service.gI().openLockAccProtect(Integer.parseInt(var11.trim()));
                    return;
                } catch (Exception var8) {
                    return;
                }
            case 88838:
                var11 = input2Dlg.tfInput.getText().trim();
                var14 = input2Dlg.tfInput2.getText().trim();
                gameAJ();
                if (var11.length() >= 6 && var14.length() >= 6) {
                    try {
                        var16 = Integer.parseInt(var11);
                        var1 = Integer.parseInt(var14);
                        if (var16 >= 99999 && var1 >= 99999) {
                            Service.gI().updateActive(var16, var1);
                            return;
                        }

                        setText(mResources.gameTT);
                        return;
                    } catch (Exception var7) {
                        setText(mResources.gameTS);
                        return;
                    }
                }

                setText(mResources.gameTR);
                return;
            case 88839:
                var11 = inputDlg.tfInput.getText();
                gameAJ();

                try {
                    Integer.parseInt(var11);
                    gameAA(mResources.gameVA, 888391, var11, 8882, (Object) null);
                    return;
                } catch (Exception var6) {
                    InfoMe.gameAA(mResources.gameTU, 20, mFont.tahoma_7_yellow);
                    return;
                }
            case 88840:
                var13 = (Char) var2;
                gameAJ();
                Service.gI().acceptInviteTestDun(var13.charID);
                return;
            case 88841:
                var13 = (Char) var2;
                gameAJ();
                Service.gI().acceptInviteTestGT(var13.charID);
                return;
            case 88842:
                Service.gI().acceptClanBattlefield();
                return;
            case 88843:
                var14 = inputDlg.tfInput.getText();
                gameAJ();
                if (var14.equals("")) {
                    setText(mResources.gameUZ);
                    return;
                }

                Service.gI().sendClanItem(var14);
                return;
            case 888181:
                var12 = ((Short) var2).shortValue();
                Service.gI().SendCapcha(var12, inputDlg.tfInput.getText());
                testCapcha = null;
                gameAJ();
                return;
            case 888361:
                var11 = inputDlg.tfInput.getText();
                gameAJ();
                if (var11.length() >= 6 && !var11.equals("")) {
                    try {
                        Service.gI().activeAccProtect(Integer.parseInt(var11));
                        return;
                    } catch (Exception var5) {
                        setText(mResources.gameTS);
                        return;
                    }
                }

                setText(mResources.gameTR);
                return;
            case 888391:
                try {
                    gameAJ();
                    var1 = Integer.parseInt((String) var2);
                    Service.gI().clearAccProtect(var1);
                    return;
                } catch (Exception var4) {
                    return;
                }
        }

    }
}
