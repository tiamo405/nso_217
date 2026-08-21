
public final class SelectCharScr extends mScreen implements IActionListener {

    private static SelectCharScr instance;
    private int w1char;
    private int h1char;
    private int padchar;
    private int x;
    private int y;
    public int indexSelect;
    public int[] parthead;
    public int[] partleg;
    public int[] partbody;
    public int[] partWp;
    public int[] level;
    public String[] name;
    public String[] phai;
    public byte[] gender;
    private Command cmdSelect;
    private int waitToPerform;
    public boolean isNullChar = true;
    private int moveUp;
    private int moveDow;
    public static String fieldAK = "";

    public static SelectCharScr gameAF() {
        if (instance == null) {
            instance = new SelectCharScr();
        }

        return instance;
    }

    public SelectCharScr() {
        this.moveUp = GameCanvas.h / 2 - 2;
        this.moveDow = GameCanvas.h / 2 + 2;
        this.w1char = 48;
        this.h1char = 85;
        if (GameCanvas.w < 160) {
            this.w1char = 32;
        }

        this.padchar = 7;
        this.x = (GameCanvas.w - 3 * this.w1char >> 1) - 5;
        this.y = GameCanvas.hh - (this.h1char >> 1) + 10;
        if (GameCanvas.isTouch && GameCanvas.w > 200) {
            this.w1char = 74;
            this.padchar = 25;
            this.h1char = 110;
            this.x = (GameCanvas.w - 3 * this.w1char >> 1) - 20;
            this.y = GameCanvas.hh - (this.h1char >> 1);
            if (GameCanvas.w < 320) {
                this.padchar = 6;
                this.x = (GameCanvas.w - 3 * this.w1char >> 1) - 6;
            }
        }

        super.left = null;
        this.cmdSelect = new Command(mResources.gameEO, this, 1000, (Object) null);
        super.center = new Command("", this, 1000, (Object) null);
        super.right = new Command(mResources.gameCV, this, 1001, (Object) null);
        super.left = this.cmdSelect;
        if (GameCanvas.isTouch) {
            super.center = null;
            super.left = null;
        }

        if (GameCanvas.isTouch && GameCanvas.w >= 320) {
            super.right.x = GameCanvas.w / 2 + 88;
            super.right.y = GameCanvas.h - 26;
        }

    }

    private void gameAG() {
        if (this.name[this.indexSelect] != null) {
            fieldAK = this.name[this.indexSelect];
            Service.gI().selectCharToPlay(this.name[this.indexSelect]);
            GameCanvas.gameAB(mResources.PLEASEWAIT);
            GameCanvas.isLoading = true;
        } else {
            CreateCharScr.gameAF().update();
        }
    }

    public final void setOffset() {
        super.setOffset();
        if (GameCanvas.keyPressedz[6]) {
            ++this.indexSelect;
            if (this.indexSelect >= 3) {
                this.indexSelect = 0;
            }
        }

        if (GameCanvas.keyPressedz[4]) {
            --this.indexSelect;
            if (this.indexSelect < 0) {
                this.indexSelect = 2;
            }
        }

        if (GameCanvas.isPointerDown && GameCanvas.gameAB(this.x, this.y, 3 * (this.w1char + this.padchar), this.h1char)) {
            int var1;
            if ((var1 = (GameCanvas.px - this.x) / (this.w1char + this.padchar)) > 2) {
                var1 = 2;
            }

            if (var1 < 0) {
                var1 = 0;
            }

            this.indexSelect = var1;
        }

        if (GameCanvas.isPointerJustRelease) {
            if (GameCanvas.gameAB(this.x, this.y, 3 * (this.w1char + this.padchar), this.h1char)) {
                this.waitToPerform = 5;
            } else {
                this.indexSelect = -1;
            }
        }

        GameCanvas.gameAI();
        GameCanvas.gameAH();
    }

    public final void gameAD() {
        if (++GameScr.cmx > GameCanvas.w * 3 + 100) {
            GameScr.cmx = 100;
        }

        if (this.waitToPerform > 0) {
            --this.waitToPerform;
            if (this.waitToPerform == 0 && this.indexSelect >= 0) {
                this.gameAG();
            }
        }

    }

    public final void update() {
        TileMap.gameAC();
        System.gc();
        super.update();

        for (int var1 = 0; var1 < this.name.length; ++var1) {
            if (this.name[var1] != null) {
                this.isNullChar = false;
                break;
            }
        }

        if (this.isNullChar) {
            CreateCharScr.gameAF().update();
        }

    }

    public final void paint(mGraphics var1) {
        GameCanvas.paint(var1);

        int var2;
        for (var2 = 0; var2 < 3; ++var2) {
            if (this.indexSelect == var2) {
                Paint.gameAD(this.x + var2 * (this.w1char + this.padchar), this.y, this.w1char, this.h1char, var1);
            } else {
                Paint.gameAC(this.x + var2 * (this.w1char + this.padchar), this.y, this.w1char, this.h1char, var1);
            }

            Paint.gameAB(this.x + var2 * (this.w1char + this.padchar), this.y, this.w1char, this.h1char, var1);
        }

        for (var2 = 0; var2 < 3; ++var2) {
            if (this.name[var2] != null) {
                Part var3 = GameScr.parts[this.parthead[var2]];
                Part var4 = GameScr.parts[this.partleg[var2]];
                Part var5 = GameScr.parts[this.partbody[var2]];
                Part var6 = GameScr.parts[this.partWp[var2]];
                int var7;
                if (var3.pi != null && var3.pi.length >= 8) {
                    for (var7 = 0; var7 < var3.pi.length; ++var7) {
                        if (var3.pi[var7] == null || !SmallImage.gameAA(var3.pi[var7].id)) {
                            Char.getMyChar();
                            var3 = Char.gameAB(this.gender[var2]);
                            break;
                        }
                    }
                } else {
                    Char.getMyChar();
                    var3 = Char.gameAB(this.gender[var2]);
                }

                var7 = this.x + var2 * (this.w1char + this.padchar) + this.w1char / 2;
                boolean var8 = false;
                int var9;
                if (!GameCanvas.isTouch) {
                    var9 = this.y + this.h1char / 2 + 16;
                    SmallImage.gameAA(var1, var6.pi[Char.CharInfo[0][3][0]].id, var7 + Char.CharInfo[0][3][1] + var6.pi[Char.CharInfo[0][3][0]].dx, var9 - Char.CharInfo[0][3][2] + var6.pi[Char.CharInfo[0][3][0]].dy, 0, 0);
                    SmallImage.gameAA(var1, var4.pi[Char.CharInfo[0][1][0]].id, var7 + Char.CharInfo[0][1][1] + var4.pi[Char.CharInfo[0][1][0]].dx, var9 - Char.CharInfo[0][1][2] + var4.pi[Char.CharInfo[0][1][0]].dy, 0, 0);
                    SmallImage.gameAA(var1, var5.pi[Char.CharInfo[0][2][0]].id, var7 + Char.CharInfo[0][2][1] + var5.pi[Char.CharInfo[0][2][0]].dx, var9 - Char.CharInfo[0][2][2] + var5.pi[Char.CharInfo[0][2][0]].dy, 0, 0);
                    SmallImage.gameAA(var1, var3.pi[Char.CharInfo[0][0][0]].id, var7 + Char.CharInfo[0][0][1] + var3.pi[Char.CharInfo[0][0][0]].dx, var9 - Char.CharInfo[0][0][2] + var3.pi[Char.CharInfo[0][0][0]].dy, 0, 0);
                    if (this.indexSelect == var2) {
                        mFont.tahoma_8b.gameAA(var1, mResources.gameRQ[0] + ": " + this.name[var2], GameCanvas.hw, this.y - 45, 2);
                        mFont.tahoma_7b_white.gameAA(var1, mResources.gameRQ[1] + ": " + this.level[var2], GameCanvas.hw, this.y - 28, 2, mFont.tahoma_7b_blue);
                        mFont.tahoma_7b_white.gameAA(var1, this.phai[var2], GameCanvas.hw, this.y - 16, 2, mFont.tahoma_7b_blue);
                    }
                } else {
                    var9 = this.y + this.h1char / 2 - 5;
                    SmallImage.gameAA(var1, var6.pi[Char.CharInfo[0][3][0]].id, var7 + Char.CharInfo[0][3][1] + var6.pi[Char.CharInfo[0][3][0]].dx, var9 - Char.CharInfo[0][3][2] + var6.pi[Char.CharInfo[0][3][0]].dy, 0, 0);
                    SmallImage.gameAA(var1, var4.pi[Char.CharInfo[0][1][0]].id, var7 + Char.CharInfo[0][1][1] + var4.pi[Char.CharInfo[0][1][0]].dx, var9 - Char.CharInfo[0][1][2] + var4.pi[Char.CharInfo[0][1][0]].dy, 0, 0);
                    SmallImage.gameAA(var1, var5.pi[Char.CharInfo[0][2][0]].id, var7 + Char.CharInfo[0][2][1] + var5.pi[Char.CharInfo[0][2][0]].dx, var9 - Char.CharInfo[0][2][2] + var5.pi[Char.CharInfo[0][2][0]].dy, 0, 0);
                    SmallImage.gameAA(var1, var3.pi[Char.CharInfo[0][0][0]].id, var7 + Char.CharInfo[0][0][1] + var3.pi[Char.CharInfo[0][0][0]].dx, var9 - Char.CharInfo[0][0][2] + var3.pi[Char.CharInfo[0][0][0]].dy, 0, 0);
                    mFont.tahoma_8b.gameAA(var1, this.name[var2], var7, this.y + this.h1char / 2 + 5, 2);
                    mFont.tahoma_7b_white.gameAA(var1, mResources.gameRQ[1] + ": " + this.level[var2], var7, this.y + this.h1char / 2 + 22, 2);
                    if (GameCanvas.w > 200) {
                        mFont.tahoma_7b_white.gameAA(var1, this.phai[var2], var7, this.y + this.h1char / 2 + 34, 2);
                    }
                }
            }
        }

        super.paint(var1);
    }

    public final void perform(int var1, Object var2) {
        switch (var1) {
            case 1000:
                this.gameAG();
                return;
            case 1001:
                Session_ME.gI().gameAC();
                GameCanvas.instance.gameAN();
            default:
        }
    }
}
