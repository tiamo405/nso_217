
public final class InfoDlg {

    int fieldAA;
    int fieldAB;
    int fieldAC;
    int fieldAD;
    private int fieldAG;
    private int fieldAH;
    private int fieldAI;
    private int fieldAJ;
    private int fieldAK = 28;
    private int fieldAL;
    private int fieldAM;
    private int fieldAN;
    private int fieldAO;
    private int fieldAP;
    private boolean fieldAQ = false;
    static boolean isShow;
    private static String title;
    private static String subtitke;
    public static int delay;
    public static boolean isLock;

    private static boolean fieldAE() {
        if (Char.getMyChar().statusMe != 3) {
            for (int var0 = 2; var0 > 0; --var0) {
                int var1;
                try {
                    if (Res.abs(GameCanvas.arrPos[var0].x - GameCanvas.arrPos[var0 - 1].x) <= 2) {
                        continue;
                    }

                    var1 = Res.abs(GameCanvas.arrPos[var0].y - GameCanvas.arrPos[var0 - 1].y);
                } catch (Exception var2) {
                    var2.printStackTrace();
                    return true;
                }

                if (var1 > 2) {
                    return false;
                }
            }
        }

        return true;
    }

    private static void fieldAF() {
        GameCanvas.keyHold[1] = false;
        GameCanvas.keyHold[2] = false;
        GameCanvas.keyHold[3] = false;
        GameCanvas.keyHold[4] = false;
        GameCanvas.keyHold[6] = false;
    }

    public final void fieldAA() {
        if (GameCanvas.isPointerDown && !GameCanvas.isPointerJustRelease) {
            this.fieldAG = GameCanvas.pxLast;
            this.fieldAH = GameCanvas.pyLast;
            if (this.fieldAG <= (GameCanvas.w >> 1) - 100 && this.fieldAH >= GameCanvas.h >> 1) {
                if (!this.fieldAQ) {
                    this.fieldAA = this.fieldAC = this.fieldAG;
                    this.fieldAB = this.fieldAD = this.fieldAH;
                }

                this.fieldAQ = true;
                this.fieldAM = GameCanvas.px - this.fieldAA;
                this.fieldAN = GameCanvas.py - this.fieldAB;
                this.fieldAO = this.fieldAM * this.fieldAM + this.fieldAN * this.fieldAN;
                this.fieldAL = Res.fieldAF(this.fieldAO);
                if (Math.abs(this.fieldAM) > 4 || Math.abs(this.fieldAN) > 4) {
                    this.fieldAP = Res.fieldAA(this.fieldAM, this.fieldAN);
                    if (!GameCanvas.gameAB(this.fieldAA - this.fieldAK, this.fieldAB - this.fieldAK, this.fieldAK << 1, this.fieldAK << 1)) {
                        if (this.fieldAL != 0) {
                            this.fieldAD = this.fieldAN * this.fieldAK / this.fieldAL;
                            this.fieldAC = this.fieldAM * this.fieldAK / this.fieldAL;
                            this.fieldAC += this.fieldAA;
                            this.fieldAD += this.fieldAB;
                            if (!Res.fieldAA(this.fieldAA - this.fieldAK, this.fieldAB - this.fieldAK, this.fieldAK << 1, this.fieldAK << 1, this.fieldAC, this.fieldAD)) {
                                this.fieldAC = this.fieldAI;
                                this.fieldAD = this.fieldAJ;
                            } else {
                                this.fieldAI = this.fieldAC;
                                this.fieldAJ = this.fieldAD;
                            }
                        } else {
                            this.fieldAC = this.fieldAI;
                            this.fieldAD = this.fieldAJ;
                        }
                    } else {
                        this.fieldAC = GameCanvas.px;
                        this.fieldAD = GameCanvas.py;
                    }

                    fieldAF();
                    if (!fieldAE()) {
                        fieldAF();
                        return;
                    }

                    if (this.fieldAP <= 360 && this.fieldAP > 340 || this.fieldAP > 0 && this.fieldAP <= 90) {
                        GameScr.auto = 0;
                        GameCanvas.keyHold[6] = true;
                        GameCanvas.keyPressedz[6] = true;
                        return;
                    }

                    if (this.fieldAP > 290 && this.fieldAP <= 340) {
                        GameScr.auto = 0;
                        GameCanvas.keyHold[3] = true;
                        GameCanvas.keyPressedz[3] = true;
                        return;
                    }

                    if (this.fieldAP > 250 && this.fieldAP <= 290) {
                        GameScr.auto = 0;
                        GameCanvas.keyHold[2] = true;
                        GameCanvas.keyPressedz[2] = true;
                        return;
                    }

                    if (this.fieldAP > 200 && this.fieldAP <= 250) {
                        GameScr.auto = 0;
                        GameCanvas.keyHold[1] = true;
                        GameCanvas.keyPressedz[1] = true;
                        return;
                    }

                    if (this.fieldAP > 90 && this.fieldAP <= 200) {
                        GameScr.auto = 0;
                        GameCanvas.keyHold[4] = true;
                        GameCanvas.keyPressedz[4] = true;
                        return;
                    }
                }
            }
        } else {
            this.fieldAA = this.fieldAC = 50;
            this.fieldAB = this.fieldAD = GameCanvas.h - 50;
            this.fieldAQ = false;
            fieldAF();
        }

    }

    public static void gameAA(String var0, String var1, int var2) {
        if (var0 != null) {
            isShow = true;
            title = var0;
            subtitke = var1;
            delay = var2;
        }
    }

    public static void gameAA() {
        gameAA(mResources.PLEASEWAIT, (String) null, 5000);
        isLock = true;
    }

    public static void showWait(String var0) {
        gameAA(var0, (String) null, 5000);
        isLock = true;
    }

    public static void gameAA(mGraphics var0) {
        String var1 = title;
        if (TileMap.mapName1 != null) {
            var1 = TileMap.mapName1;
        }

        if (isShow) {
            if (!isLock || delay <= 4990) {
                if (!GameScr.isPaintAlert) {
                    Paint.gameAA(GameCanvas.hw - 64, 10, 128, 40, var0);
                    if (isLock) {
                        GameCanvas.gameAA(GameCanvas.hw - mFont.tahoma_8b.gameAA(var1) / 2 - 10, 30, var0, false);
                        mFont.tahoma_8b.gameAA(var0, var1, GameCanvas.hw + 5, 23, 2);
                    } else if (subtitke != null) {
                        mFont.tahoma_8b.gameAA(var0, var1, GameCanvas.hw, 18, 2);
                        mFont.tahoma_7_white.gameAA(var0, subtitke, GameCanvas.hw, 32, 2);
                    } else {
                        mFont.tahoma_8b.gameAA(var0, var1, GameCanvas.hw, 23, 2);
                    }
                }
            }
        }
    }

    public static void gameAB() {
        title = "";
        subtitke = null;
        isLock = false;
        delay = 0;
        isShow = false;
    }
}
