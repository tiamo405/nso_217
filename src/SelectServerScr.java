
public final class SelectServerScr extends mScreen implements IActionListener {

    private int popupW;
    private int popupH;
    private int popupX;
    private int popupY;
    private int indexRow = -1;
    private static String[] menu;
    public static String uname;
    public static String pass;
    public static String unameChange = "";
    public static String passChange = "";
    private static Command cmdChoiMoi = null;
    private static Command cmdDoiTaiKhoan = null;
    private static Command cmdChoiTiep = null;
    private static Command cmdChonServer = null;
    private static Command cmdUpdLinkSv = null;
    private static Command[][] cmd = null;

    static {
        uname = RMS.loadRMSString("acc");
        pass = RMS.loadRMSString("pass");
        if (uname == null) {
            uname = "";
        }
        if (pass == null) {
            pass = "";
        }
    }

    public SelectServerScr() {
        GameCanvas.menu.menuSelectedItem = UpdateServer.serverST[0];
        GameMidlet.IP = UpdateServer.listIP[GameCanvas.menu.menuSelectedItem];
        int var1 = RMS.gameAC("indServer");

        for (int var2 = 0; var2 < UpdateServer.serverST.length; ++var2) {
            if (var1 == UpdateServer.serverST[var2]) {
                GameCanvas.menu.menuSelectedItem = UpdateServer.serverST[var2];
                GameMidlet.IP = UpdateServer.listIP[var2];
                return;
            }
        }

    }

    public final void update() {
        if (RMS.gameAC("isKiemduyet") == 0) {
            GameCanvas.isKiemduyet = true;
        } else {
            GameCanvas.isKiemduyet = false;
        }

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

        super.right = new Command(mResources.gameCV, GameCanvas.instance, 8885, (Object) null);
        super.left = new Command(mResources.gameBD, GameCanvas.instance, 8886, (Object) null);
        this.indexRow = -1;
        if (!GameCanvas.isTouch) {
            this.indexRow = 0;
        }

        if (GameCanvas.isTouch && GameCanvas.w >= 320) {
            super.right.x = GameCanvas.w / 2 + 88;
        }

        if (cmdChoiMoi == null) {
            cmdChoiMoi = new Command(GameCanvas.isTouch ? "" : mResources.gameCX, this, 1000, (Object) null);
            cmdDoiTaiKhoan = new Command(GameCanvas.isTouch ? "" : mResources.gameCX, this, 1001, (Object) null);
            cmdChonServer = new Command(GameCanvas.isTouch ? "" : mResources.gameCX, this, 1002, (Object) null);
            cmdChoiTiep = new Command(GameCanvas.isTouch ? "" : mResources.gameCX, this, 1003, (Object) null);
            cmdUpdLinkSv = new Command(GameCanvas.isTouch ? "" : mResources.gameCX, this, 1004, null);
            cmd = new Command[][]{{cmdChoiMoi, cmdDoiTaiKhoan, cmdChonServer, cmdUpdLinkSv}, {cmdChoiTiep, cmdChoiMoi, cmdDoiTaiKhoan, cmdChonServer, cmdUpdLinkSv}};
        }

        if ((uname == null || uname.equals("")) && unameChange.equals("")) {
            menu = new String[]{mResources.gameXC, mResources.gameXE, mResources.gameXF, mResources.gameXK};
        } else {
            menu = new String[]{mResources.gameXD, mResources.gameXC, mResources.gameXE, mResources.gameXF, mResources.gameXK};
        }

        GameCanvas.menu.menuSelectedItem = UpdateServer.serverST[0];
        GameMidlet.IP = UpdateServer.listIP[GameCanvas.menu.menuSelectedItem];
        int var1 = RMS.gameAC("indServer");

        for (int var2 = 0; var2 < UpdateServer.serverST.length; ++var2) {
            if (var1 == UpdateServer.serverST[var2]) {
                GameCanvas.menu.menuSelectedItem = UpdateServer.serverST[var2];
                GameMidlet.IP = UpdateServer.listIP[var2];
                return;
            }
        }

        if (RMS.loadRMSString("random") == null) {
            RMS.gameAA("random", gameAH());
        }

    }

    public final void paint(mGraphics var1) {
        var1.gameAA(0);
        var1.gameAC(0, 0, GameCanvas.w, GameCanvas.h);
        GameCanvas.paint(var1);
        var1.gameAA(LoginScr.imgTitle, GameCanvas.hw - LoginScr.imgTitle.getWidth() / 2, this.popupY + 10 - LoginScr.imgTitle.getHeight() / 2, 0);
        if (GameCanvas.menu.menuSelectedItem == -1) {
            GameCanvas.menu.menuSelectedItem = 0;
        }

        int var2 = this.popupY + 35;

        for (int var3 = 0; var3 < menu.length; ++var3) {
            var1.gameAA(Paint.COLORDARK);
            var1.gameAC(this.popupX + 10, var2 + var3 * 35, this.popupW - 20, 28);
            Paint.gameAB(this.popupX + 10, var2 + var3 * 35, this.popupW - 20, 28, var1);
            if (var3 == this.indexRow) {
                var1.gameAA(Paint.COLORLIGHT);
                var1.gameAC(this.popupX + 10, var2 + var3 * 35, this.popupW - 20, 28);
                Paint.gameAB(this.popupX + 10, var2 + var3 * 35, this.popupW - 20, 28, var1);
            }

            if (var3 < menu.length) {
                if (uname.equals("") && unameChange.equals("")) {
                    if (var3 == 2) {
                        mFont.tahoma_7b_white.gameAA(var1, menu[var3] + UpdateServer.listName[GameCanvas.menu.menuSelectedItem], this.popupX + this.popupW / 2, var2 + var3 * 35 + 8, 2);
                    } else {
                        mFont.tahoma_7b_white.gameAA(var1, menu[var3], this.popupX + this.popupW / 2, var2 + var3 * 35 + 8, 2);
                    }
                } else if (var3 == 0) {
                    mFont.tahoma_7b_white.gameAA(var1, menu[var3] + (!unameChange.equals("") ? ": " + unameChange : (uname.startsWith("tmpusr") ? "" : ": " + uname)), this.popupX + this.popupW / 2, var2 + var3 * 35 + 8, 2);
                } else if (var3 == 3) {
                    mFont.tahoma_7b_white.gameAA(var1, menu[var3] + UpdateServer.listName[GameCanvas.menu.menuSelectedItem], this.popupX + this.popupW / 2, var2 + var3 * 35 + 8, 2);
                } else {
                    mFont.tahoma_7b_white.gameAA(var1, menu[var3], this.popupX + this.popupW / 2, var2 + var3 * 35 + 8, 2);
                }
            }
        }

        if (GameCanvas.currentDialog == null) {
            Paint.gameAA(var1, super.left, super.center, super.right);
        }

        super.paint(var1);
    }

    public final void gameAD() {
        if (uname.equals("") && unameChange.equals("")) {
            if (this.indexRow > -1 && this.indexRow < cmd[0].length) {
                super.center = cmd[0][this.indexRow];
            }
        } else if (this.indexRow > -1 && this.indexRow < cmd[1].length) {
            super.center = cmd[1][this.indexRow];
        }

        if (++GameScr.cmx > GameCanvas.w * 3 + 100) {
            GameScr.cmx = 100;
        }

        super.gameAD();
    }

    public final void setOffset() {
        if (!GameCanvas.keyPressedz[2] && !GameCanvas.keyPressedz[4]) {
            if (GameCanvas.keyPressedz[8] || GameCanvas.keyPressedz[6]) {
                ++this.indexRow;
                if (this.indexRow > menu.length - 1) {
                    this.indexRow = 0;
                }
            }
        } else {
            --this.indexRow;
            if (this.indexRow < 0) {
                this.indexRow = menu.length - 1;
            }
        }

        if (GameCanvas.isPointerJustRelease && GameCanvas.gameAB(this.popupX + 10, this.popupY + 35, this.popupW - 10, 175)) {
            if (GameCanvas.isPointerClick) {
                this.indexRow = (GameCanvas.py - (this.popupY + 35)) / 35;
            }

            if (uname.equals("") && unameChange.equals("")) {
                if (this.indexRow > -1 && this.indexRow < cmd[0].length) {
                    cmd[0][this.indexRow].gameAA();
                }
            } else if (this.indexRow > -1 && this.indexRow < cmd[1].length) {
                cmd[1][this.indexRow].gameAA();
            }
        }

        super.setOffset();
        GameCanvas.gameAH();
    }

    private void gameAI() {
        if (!Session_ME.gI().connected) {
            GameCanvas.AD();
        }

        GameCanvas.gameAK();
    }

    public static boolean gameAG() {
        return uname != null && (uname.startsWith("tmpusr") || uname.equals(""));
    }

    public static String gameAH() {
        String var0 = "";

        for (int var1 = 0; var1 < 12; ++var1) {
            String var2 = Integer.toString(Res.gameAD(0, 9));
            var0 = var0 + var2;
        }

        return var0;
    }

    public final void perform(int var1, Object var2) {
        int i1;
        switch (var1) {
            case 1000:
                if (gameAG() && !uname.equals("")) {
                    GameCanvas.gameAA(mResources.gameXB, new Command(mResources.gameXD, this, 10001, (Object) null), new Command(mResources.gameCU, GameCanvas.instance, 8882, (Object) null));
                    return;
                }

                this.gameAI();
                Service.gI().login("-1", "12345", "2.1.7");
                return;
            case 1001:
                if (gameAG() && !uname.equals("") && unameChange.equals("")) {
                    GameCanvas.gameAA(mResources.gameXB, new Command(mResources.gameXG, this, 10004, (Object) null), new Command(mResources.gameCU, GameCanvas.instance, 8882, (Object) null));
                    return;
                }

                GameCanvas.loginScr.update();
                return;
            case 1002:
                MyVector var5 = new MyVector();
                for (i1 = 0; i1 < UpdateServer.serverST.length; ++i1) {
                    var5.addElement(new Command(UpdateServer.listName[UpdateServer.serverST[i1]], this, i1 + 20000, null));
                }
                GameCanvas.menu.gameAA(var5);
                if (RMS.gameAC("indServer") != -1 && !GameCanvas.isTouch) {
                    GameCanvas.menu.menuSelectedItem = RMS.gameAC("indServer");
                }
                return;
            case 1003:
                this.gameAI();
                if (!unameChange.equals("")) {
                    uname = unameChange;
                    pass = passChange;
                    unameChange = "";
                    passChange = "";
                    RMS.gameAA("acc", uname);
                    RMS.gameAA("pass", pass);
                }

                Service.gI().login(uname, pass, "2.1.7");
                return;
            case 1004:// nhận qua https
                boolean bl2 = UpdateServer.a();
                Session_ME.gI().gameAC();
                GameCanvas.gameAB();
                GameCanvas.gameAJ();
                GameCanvas.gameAA(bl2 ? new String(new char[]{'U', 'p', 'd', 'a', 't', 'e', ' ', 'O', 'K'})
                        : new String(new char[]{'e', 'r', 'r', 'o', 'r', ' ', '!', '!', ' ', 'V', 'u', 'i', ' ', 'L', 'ò', 'n', 'g', ' ', 'K', 'i', 'ể', 'm', ' ',
                    'T', 'r', 'a', ' ', 'L', 'ạ', 'i', ' ', 'K', 'ế', 't', ' ', 'N', 'ố', 'i'}), 8882, (Object) null);
                return;
//            case 1004:// nhận ip  
//                boolean bl2 = UpdateServer.b1();
//                Session_ME.gI().gameAC();
//                GameCanvas.gameAB();
//                GameCanvas.gameAJ();
//                GameCanvas.gameAA(bl2 ? new String(new char[]{'U', 'p', 'd', 'a', 't', 'e', ' ', 'O', 'K'})
//                        : new String(new char[]{'e', 'r', 'r', 'o', 'r', ' ', '!', '!', ' ', 'V', 'u', 'i', ' ', 'L', 'ò', 'n', 'g', ' ', 'K', 'i', 'ể', 'm', ' ',
//                    'T', 'r', 'a', ' ', 'L', 'ạ', 'i', ' ', 'K', 'ế', 't', ' ', 'N', 'ố', 'i'}), 8882, (Object) null);
//                return;
            case 10001:
                this.gameAI();
                Service.gI().login("-1", "12345", "2.1.7");
                if (!unameChange.equals("")) {
                    uname = unameChange;
                    pass = passChange;
                    unameChange = "";
                    passChange = "";
                    RMS.gameAA("acc", uname);
                    RMS.gameAA("pass", pass);
                    return;
                }
                break;
            case 10004:
                GameCanvas.currentDialog = null;
                GameCanvas.loginScr.update();
                return;

        }
        if (var1 >= 20000 && var1 < 20000 + UpdateServer.serverST.length) {
            i1 = UpdateServer.serverST[var1 - 20000];
            GameCanvas.menu.showMenu = false;
            GameMidlet.IP = UpdateServer.listIP[i1];
            GameMidlet.PORT = UpdateServer.listPort[i1];
            GameMidlet.serverLogin = UpdateServer.serverLoginList[i1];
            RMS.gameAA("indServer", UpdateServer.serverST[i1]);
        }

    }
}
