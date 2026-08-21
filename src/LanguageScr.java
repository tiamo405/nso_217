
public final class LanguageScr extends mScreen implements IActionListener {

    private int popupW;
    private int popupH;
    private int popupX;
    private int popupY;
    private int indexRow = -1;

    public final void update() {
        GameScr.gH = GameCanvas.h;
        if (GameCanvas.typeBg == 2) {
            GameCanvas.setMaxTextLenght(0);
        } else {
            GameCanvas.setMaxTextLenght(TileMap.bgID);
        }

        super.update();
        if (GameScr.instance != null) {
            GameScr.instance = null;
        }

        if ((TileMap.bgID = (byte) ((int) (System.currentTimeMillis() % 9L))) == 5 || TileMap.bgID == 6) {
            TileMap.bgID = 4;
        }

        GameScr.gameAA(true);
        GameScr.cmx = 100;
        this.popupW = 170;
        this.popupH = 175;
        if (GameCanvas.w == 128 || GameCanvas.h <= 208) {
            this.popupW = 126;
            this.popupH = 160;
        }

        this.popupX = GameCanvas.w / 2 - this.popupW / 2;
        this.popupY = GameCanvas.h / 2 - this.popupH / 2;
        if (GameCanvas.h <= 250) {
            this.popupY -= 10;
        }

        super.center = new Command(GameCanvas.isTouch ? "" : mResources.gameCX, this, 1000, (Object) null);
        super.right = new Command(mResources.gameCV, GameCanvas.instance, 8885, (Object) null);
        this.indexRow = -1;
        if (!GameCanvas.isTouch) {
            this.indexRow = 0;
        }

        if (GameCanvas.isTouch && GameCanvas.w >= 320) {
            super.center.x = GameCanvas.w / 2 - 35;
            super.right.x = GameCanvas.w / 2 + 88;
            super.center.y = super.right.y = GameCanvas.h - 26;
        }

    }

    public final void paint(mGraphics var1) {
        var1.gameAA(0);
        var1.gameAC(0, 0, GameCanvas.w, GameCanvas.h);
        GameCanvas.paint(var1);
        Paint.gameAA(this.popupX, this.popupY, this.popupW, this.popupH, var1);
        var1.gameAA(Paint.COLORDARK);
        var1.gameAB(GameCanvas.hw - mFont.tahoma_8b.gameAA(mResources.gameBD) / 2 - 12, this.popupY + 7, mFont.tahoma_8b.gameAA(mResources.gameBD) + 22, 24, 6, 6);
        var1.gameAA(Paint.COLORLIGHT);
        var1.gameAA(GameCanvas.hw - mFont.tahoma_8b.gameAA(mResources.gameBD) / 2 - 12, this.popupY + 7, mFont.tahoma_8b.gameAA(mResources.gameBD) + 22, 24, 6, 6);
        mFont.tahoma_8b.gameAA(var1, mResources.gameBD, GameCanvas.hw, this.popupY + 12, 2);
        String[] var2 = mResources.gameBF;
        int var3 = this.popupY + 50;

        for (int var4 = 0; var4 < var2.length; ++var4) {
            var1.gameAA(Paint.COLORDARK);
            var1.gameAC(this.popupX + 10, var3 + var4 * 35, this.popupW - 20, 28);
            var1.gameAA(5720393);
            var1.gameAB(this.popupX + 10, var3 + var4 * 35, this.popupW - 20, 28);
            if (var4 == this.indexRow) {
                var1.gameAA(Paint.COLORLIGHT);
                var1.gameAC(this.popupX + 10, var3 + var4 * 35, this.popupW - 20, 28);
                var1.gameAA(11053224);
                var1.gameAB(this.popupX + 10, var3 + var4 * 35, this.popupW - 20, 28);
            }

            mFont.tahoma_7b_white.gameAA(var1, var2[var4], this.popupX + this.popupW / 2, var3 + var4 * 35 + 8, 2);
        }

        super.paint(var1);
        Paint.gameAA(var1, super.left, super.center, super.right);
    }

    public final void gameAD() {
        if (++GameScr.cmx > GameCanvas.w * 3 + 100) {
            GameScr.cmx = 100;
        }

        super.gameAD();
    }

    public final void setOffset() {
        if (GameCanvas.keyPressedz[2] || GameCanvas.keyPressedz[4] || GameCanvas.keyPressedz[6] || GameCanvas.keyPressedz[8]) {
            this.indexRow = this.indexRow == 0 ? 1 : 0;
        }

        if (GameCanvas.isPointerJustRelease && GameCanvas.gameAB(this.popupX + 10, this.popupY + 45, this.popupW - 10, 70)) {
            if (GameCanvas.isPointerClick) {
                this.indexRow = (GameCanvas.py - (this.popupY + 45)) / 35;
            }

            this.perform(1000, (Object) null);
        }

        super.setOffset();
        GameCanvas.gameAH();
    }

    public final void perform(int var1, Object var2) {
        switch (var1) {
            case 1000:
                GameCanvas.currentDialog = null;
                var1 = mResources.languageID = this.indexRow == 0 ? 0 : 1;
                RMS.gameAA("indLanguage", var1);
                mResources.gameAA();
                RMS.gameAA();
                GameCanvas.gameAB();
                GameCanvas.selectsvScr.update();
            default:
        }
    }
}
