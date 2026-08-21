
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Image;

public final class LoginScr extends mScreen implements IActionListener {

    private TField tfUser;
    private TField tfPass;
    private TField tfRegPass;
    private TField tfEmail;
    private static LoginScr gI;
    private int focus;
    private int wC;
    private int yL;
    private int defYL;
    private boolean isCheck = false;
    private boolean isRes = false;
    private Command cmdLogin;
    private Command cmdCheck;
    private Command cmdRes;
    private Command cmdMenu;
    public static Image imgTitle = GameCanvas.gameAC("/tt.png");
    private int yy;
    private String[] currentTip;
    private int tipid = -1;
    private int v = 2;
    private int g = 0;
    private int ylogo = -40;
    private int dir = 1;
    public static boolean isLoggingIn;
    private String strNick = "";
    public static int AB = 0;

    public final void update() {
        if (RMS.loadRMSString("random") == null) {
            RMS.gameAA("random", SelectServerScr.gameAH());
        }

        this.yL = -50;
        this.isRes = false;
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

        if (GameCanvas.menu != null) {
            GameCanvas.menu = new Menu();
        }

        GameCanvas.isGPRS = false;
        int var1;
        if ((var1 = RMS.gameAC("isSoftKey")) <= 0) {
            RMS.gameAA("isSoftKey", 1);
            GameScr.fieldGE = true;
        } else if (var1 == 1) {
            GameScr.fieldGE = true;
        } else if (var1 == 2) {
            GameScr.fieldGE = false;
        }
        super.left = this.cmdMenu = new Command(mResources.gameDJ, this, 2005, (Object) null);
        GameScr.gameAX.removeAllElements();
    }

    public final void gameAA(boolean var1) {
        this.isRes = true;
        this.yL = -50;
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

        if (GameCanvas.menu != null) {
            GameCanvas.menu = new Menu();
        }

        GameCanvas.isGPRS = false;
        super.left = this.cmdMenu = new Command("Hủy", this, 20051, (Object) null);
    }

    public LoginScr() {
        gI = this;
        this.isRes = false;
        if ((TileMap.bgID = (byte) ((int) (System.currentTimeMillis() % 9L))) == 5 || TileMap.bgID == 6) {
            TileMap.bgID = 4;
        }

        GameScr.gameAA(true);
        GameScr.cmx = 100;
        if (GameCanvas.h > 200) {
            this.defYL = GameCanvas.hh - 80;
        } else {
            this.defYL = GameCanvas.hh - 65;
        }

        this.yL = -50;
        this.wC = GameCanvas.w - 30;
        if (this.wC < 135) {
            this.wC = 135;
        }

        if (this.wC > 155) {
            this.wC = 155;
        }

        this.yy = GameCanvas.hh - mScreen.ITEM_HEIGHT - 5;
        if (GameCanvas.h <= 160) {
            this.yy = 20;
        }

        this.tfUser = new TField();
        this.tfUser.name = mResources.gameCY;
        this.tfUser.x = GameCanvas.hw - 20 - 57;
        this.tfUser.y = this.yy;
        this.tfUser.width = this.wC;
        this.tfUser.height = mScreen.ITEM_HEIGHT + 2;
        this.tfUser.isFocus = true;
        this.tfUser.setIputType(3);
        TField.gameAJ = GameMidlet.instance;
        TField.gameAI = MotherCanvas.instance;
        this.tfPass = new TField();
        this.tfPass.name = mResources.gameCZ;
        this.tfPass.x = GameCanvas.hw - 20 - 57;
        this.tfPass.y = this.yy += 35;
        this.tfPass.width = this.wC;
        this.tfPass.height = mScreen.ITEM_HEIGHT + 2;
        this.tfPass.isFocus = false;
        this.tfPass.setIputType(2);
        this.tfRegPass = new TField();
        this.tfRegPass.name = mResources.gameDF;
        this.tfRegPass.x = GameCanvas.hw - 20 - 57;
        this.tfRegPass.y = this.yy += 35;
        this.tfRegPass.width = this.wC;
        this.tfRegPass.height = mScreen.ITEM_HEIGHT + 2;
        this.tfRegPass.isFocus = false;
        this.tfRegPass.setIputType(2);
        this.tfEmail = new TField();
        this.tfEmail.name = "Email/Số di động";
        this.tfEmail.x = GameCanvas.hw - 20 - 57;
        this.tfEmail.y = this.yy += 35;
        this.tfEmail.width = this.wC;
        this.tfEmail.height = mScreen.ITEM_HEIGHT + 2;
        this.tfEmail.isFocus = false;
        this.tfEmail.setIputType(3);
        this.isCheck = true;
        if (SelectServerScr.uname != null) {
            if (SelectServerScr.uname.startsWith("tmpusr")) {
                this.tfUser.setText("");
                this.tfPass.setText("");
            } else {
                this.tfUser.setText(SelectServerScr.uname);
                this.tfPass.setText(SelectServerScr.pass);
            }
        }

        this.focus = 0;
        this.cmdLogin = new Command(mResources.gameCX, this, 2000, (Object) null);
        this.cmdCheck = new Command(mResources.gameDG, this, 2001, (Object) null);
        this.cmdRes = new Command(mResources.gameDI, this, 2002, (Object) null);
        new Command(mResources.gameDJ, this, 2004, (Object) null);
        if (!this.isRes) {
            super.left = this.cmdMenu = new Command(mResources.gameDJ, this, 2005, (Object) null);
        } else {
            super.left = this.cmdMenu = new Command(mResources.gameED, this, 20051, (Object) null);
        }

        if (GameCanvas.isTouch && GameCanvas.w >= 320) {
            super.center = null;
            super.right = this.cmdLogin;
        } else {
            super.center = this.cmdLogin;
            super.right = this.tfUser.cmdClear;
        }
    }

    public static LoginScr gameAF() {
        return gI;
    }

    private static void gameAB(boolean var0) {
        GameCanvas.isGPRS = var0;
        RMS.gameAA("isGPRS", var0 ? 1 : 2);
    }

    private void setText(String var1) {
        GameMidlet.IP = UpdateServer.listIP[0];
        GameCanvas.gameAB(mResources.gameEJ);
        GameCanvas.AD();
        GameCanvas.gameAB(mResources.gameEK);
        Service.gI().setClientType();
        Service.gI().requestRegisterNew(var1, this.tfPass.getText(), this.tfEmail.getText());
    }

    private void AJ() {
        this.tipid = GameCanvas.gameTick % mResources.gameAX.length;
        this.currentTip = mFont.tahoma_7_white.gameAB(mResources.gameAX[this.tipid], GameCanvas.w - 40);
        String var1 = this.tfUser.getText().toLowerCase().trim();
        String var2 = this.tfPass.getText().toLowerCase().trim();
        if (var1.equals("a") && var2.equals("a")) {
            AB = 1;
        } else if (var1.equals("b") && var2.equals("b")) {
            AB = 2;
        }

        if (var1 != null && var2 != null && !var1.equals("")) {
            if (var2.equals("")) {
                this.focus = 1;
                this.tfUser.isFocus = false;
                this.tfPass.isFocus = true;
                super.right = this.tfPass.cmdClear;
                return;
            }

            GameCanvas.gameAB(mResources.gameEJ);
            GameCanvas.AD();
            GameCanvas.gameAB("Đang đăng nhập");
            Service.gI().login(var1, var2, "2.1.7");
            isLoggingIn = true;
            if (this.isCheck) {
                RMS.gameAA("check", 1);
                RMS.gameAA("acc", var1);
                RMS.gameAA("pass", var2);
            } else {
                RMS.gameAA("check", 2);
                RMS.gameAA("acc", "");
                RMS.gameAA("pass", "");
            }

            this.focus = 0;
        }

    }

    public final void gameAD() {
        if (++GameScr.cmx > GameCanvas.w * 3 + 100) {
            GameScr.cmx = 100;
        }

        this.tfUser.gameAC();
        this.tfPass.gameAC();
        if (this.isRes) {
            this.tfRegPass.gameAC();
            this.tfEmail.gameAC();
        }

        if (this.defYL != this.yL) {
            this.yL += this.defYL - this.yL >> 1;
        }

        if (GameCanvas.isTouch) {
            super.center = null;
            if (this.isRes) {
                super.right = this.cmdRes;
            } else {
                super.right = this.cmdLogin;
            }
        } else if (this.isRes) {
            super.center = this.cmdRes;
        } else if (this.focus == 2) {
            super.center = this.cmdCheck;
            if (this.isCheck) {
                super.center.caption = mResources.gameDH;
            } else {
                super.center.caption = mResources.gameDG;
            }
        } else {
            super.center = this.cmdLogin;
        }

        if (this.g >= 0) {
            this.ylogo += this.dir * this.g;
            this.g += this.dir * this.v;
            if (this.g <= 0) {
                this.dir = -this.dir;
            }

            if (this.ylogo > 0) {
                this.dir = -this.dir;
                this.g -= 2 * this.v;
            }
        }

        if (this.tipid >= 0 && GameCanvas.gameTick % 100 == 0) {
            ++this.tipid;
            if (this.tipid >= mResources.gameAX.length) {
                this.tipid = 0;
            }

            this.currentTip = mFont.tahoma_7_white.gameAB(mResources.gameAX[this.tipid], GameCanvas.w - 40);
        }

    }

    public final void gameAA(int var1) {
        if (this.tfUser.isFocus) {
            this.tfUser.gameAA(var1);
        } else if (this.tfPass.isFocus) {
            this.tfPass.gameAA(var1);
        } else if (this.isRes && this.tfRegPass.isFocus) {
            this.tfRegPass.gameAA(var1);
        } else if (this.isRes && this.tfEmail.isFocus) {
            this.tfEmail.gameAA(var1);
        }

        super.gameAA(var1);
    }

    public final void gameAB() {
        super.gameAB();
    }

    public final void paint(mGraphics var1) {
        var1.gameAA(0);
        var1.gameAC(0, 0, GameCanvas.w, GameCanvas.h);
        GameCanvas.paint(var1);
        int var2 = this.tfUser.y - 45;
        if (GameCanvas.h <= 220) {
            var2 += 5;
        }

        if (GameCanvas.currentDialog == null) {
            if (this.isRes) {
                Paint.gameAA(GameCanvas.hw - 85, this.tfUser.y - 15, 170, 150, var1);
            } else {
                Paint.gameAA(GameCanvas.hw - 85, this.tfUser.y - 15, 170, 90, var1);
            }

            if (GameCanvas.h > 160 && imgTitle != null) {
                var1.gameAA(imgTitle, GameCanvas.hw, var2 - 2, 3);
            }

            this.tfUser.paint(var1);
            this.tfPass.paint(var1);
            if (this.isRes) {
                this.tfRegPass.paint(var1);
                this.tfEmail.paint(var1);
            }

            var1.gameAD(0, 0, GameCanvas.w, GameCanvas.h);
            boolean var3 = false;
            if (GameCanvas.w > 200) {
                if (this.tfUser.getText().equals("")) {
                    if (!this.tfUser.isFocus) {
                        mFont.tahoma_7b_white.gameAA(var1, mResources.gameCY, this.tfUser.x + 5, this.tfUser.y + 7, 0);
                    } else {
                        mFont.tahoma_7_grey.gameAA(var1, mResources.gameCY, this.tfUser.x + 5, this.tfUser.y + 7, 0);
                    }
                }

                if (this.tfPass.getText().equals("")) {
                    if (!this.tfPass.isFocus) {
                        mFont.tahoma_7b_white.gameAA(var1, mResources.gameCZ, this.tfPass.x + 5, this.tfPass.y + 7, 0);
                    } else {
                        mFont.tahoma_7_grey.gameAA(var1, mResources.gameCZ, this.tfPass.x + 5, this.tfPass.y + 7, 0);
                    }
                }

                if (this.isRes) {
                    if (this.tfRegPass.getText().equals("")) {
                        if (!this.tfRegPass.isFocus) {
                            mFont.tahoma_7b_white.gameAA(var1, mResources.gameDA, this.tfRegPass.x + 5, this.tfRegPass.y + 7, 0);
                            mFont.tahoma_7b_white.gameAA(var1, mResources.gameCZ, this.tfRegPass.x + 50, this.tfRegPass.y + 7, 0);
                        } else {
                            mFont.tahoma_7_grey.gameAA(var1, mResources.gameDA, this.tfRegPass.x + 5, this.tfRegPass.y + 7, 0);
                            mFont.tahoma_7_grey.gameAA(var1, mResources.gameCZ, this.tfRegPass.x + 50, this.tfRegPass.y + 7, 0);
                        }
                    }

                    if (this.tfEmail.getText().equals("")) {
                        if (!this.tfEmail.isFocus) {
                            mFont.tahoma_7b_white.gameAA(var1, "Email/số di động", this.tfEmail.x + 5, this.tfEmail.y + 5, 0);
                        } else {
                            mFont.tahoma_7_grey.gameAA(var1, "Email/số di động", this.tfEmail.x + 5, this.tfEmail.y + 5, 0);
                        }
                    }
                }
            } else {
                if (this.tfUser.getText().equals("")) {
                    mFont.tahoma_7b_white.gameAA(var1, mResources.gameDB, this.tfUser.x - 35, this.tfUser.y + 7, 0);
                }

                if (this.tfPass.getText().equals("")) {
                    mFont.tahoma_7b_white.gameAA(var1, mResources.gameDC, this.tfPass.x - 35, this.tfPass.y + 7, 0);
                }

                if (this.isRes) {
                    mFont.tahoma_7b_white.gameAA(var1, mResources.gameDD, this.tfRegPass.x - 35, this.tfRegPass.y - 1, 0);
                    mFont.tahoma_7b_white.gameAA(var1, mResources.gameDC, this.tfRegPass.x - 35, this.tfRegPass.y + 13, 0);
                    mFont.tahoma_7b_white.gameAA(var1, "Email/số di động", this.tfEmail.x - 35, this.tfEmail.y + 5, 0);
                }
            }
        } else if (this.currentTip != null) {
            for (var2 = 0; var2 < this.currentTip.length; ++var2) {
                mFont.tahoma_7_white.gameAA(var1, this.currentTip[var2], GameCanvas.w / 2, this.tfUser.y - 15 + var2 * 10, 2, mFont.tahoma_7_grey);
            }
        }

        String var4 = "2.1.7";
        mFont.tahoma_7_grey.gameAA(var1, var4, GameCanvas.w - 5, 5, 1);
        super.paint(var1);
    }

    public final void setOffset() {
        if (GameCanvas.keyPressedz[2]) {
            --this.focus;
            if (this.focus < 0) {
                this.focus = 3;
            }
        } else if (GameCanvas.keyPressedz[8]) {
            ++this.focus;
            if (this.focus > 3) {
                this.focus = 0;
            }
        }

        if (GameCanvas.keyPressedz[2] || GameCanvas.keyPressedz[8]) {
            GameCanvas.gameAH();
            if (this.focus == 1) {
                this.tfUser.isFocus = false;
                this.tfPass.isFocus = true;
                this.tfRegPass.isFocus = false;
                this.tfEmail.isFocus = false;
                super.right = this.tfPass.cmdClear;
            } else if (this.focus == 0) {
                this.tfUser.isFocus = true;
                this.tfPass.isFocus = false;
                this.tfRegPass.isFocus = false;
                this.tfEmail.isFocus = false;
                super.right = this.tfUser.cmdClear;
            } else {
                this.tfUser.isFocus = false;
                this.tfPass.isFocus = false;
                if (this.isRes) {
                    if (this.focus == 2) {
                        this.tfRegPass.isFocus = true;
                        this.tfEmail.isFocus = false;
                        super.right = this.tfRegPass.cmdClear;
                    } else if (this.focus == 3) {
                        this.tfEmail.isFocus = true;
                        this.tfRegPass.isFocus = false;
                        super.right = this.tfEmail.cmdClear;
                    }
                }
            }
        }

        if (GameCanvas.isPointerJustRelease) {
            if (GameCanvas.gameAB(this.tfUser.x, this.tfUser.y, this.tfUser.width, this.tfUser.height)) {
                this.focus = 0;
            } else if (GameCanvas.gameAB(this.tfPass.x, this.tfPass.y, this.tfPass.width, this.tfPass.height)) {
                this.focus = 1;
            } else {
                if (this.isRes) {
                    if (GameCanvas.gameAB(this.tfRegPass.x, this.tfRegPass.y, this.tfRegPass.width, this.tfRegPass.height)) {
                        this.focus = 2;
                    } else if (GameCanvas.gameAB(this.tfEmail.x, this.tfEmail.y, this.tfEmail.width, this.tfEmail.height)) {
                        this.focus = 3;
                    }
                } else if (GameCanvas.gameAB(this.tfUser.x - 20, GameCanvas.hh + 40, 80, 20)) {
                    this.isCheck = !this.isCheck;
                }

                this.focus = 2;
            }
        }

        super.setOffset();
        GameCanvas.gameAH();
    }

    public final void perform(int var1, Object var2) {
        int var7;
        switch (var1) {
            case 1002:
                this.isRes = true;
                this.tfRegPass.isFocus = false;
                this.tfEmail.isFocus = false;
                this.tfPass.isFocus = false;
                this.tfUser.isFocus = true;
                super.right = this.tfUser.cmdClear;
                super.left = new Command(mResources.gameED, this, 10021, (Object) null);
                return;
            case 1003:
                try {
                    GameMidlet.instance.platformRequest("http://ninjaschool.vn");
                    return;
                } catch (ConnectionNotFoundException var3) {
                    var3.printStackTrace();
                    return;
                }
            case 1004:
                MyVector var6 = new MyVector();
                var7 = RMS.gameAC("lowGraphic");
                if (!GameCanvas.isTouch) {
                    if (var7 == 1) {
                        var6.addElement(new Command(mResources.gameDK, this, 10041, (Object) null));
                    } else {
                        var6.addElement(new Command(mResources.gameDL, this, 10042, (Object) null));
                    }
                }

                var6.addElement(new Command(mResources.gameBD, this, 1006, (Object) null));
                if (GameCanvas.currentScreen == this) {
                    var6.addElement(new Command(mResources.gameBE, this, 1009, (Object) null));
                }

                GameCanvas.menu.gameAA(var6);
                return;
            case 1005:
                GameCanvas.gameAA(mResources.gameDT, new Command("3G/Wifi", this, 3000, (Object) null), new Command("GPRS", this, 3001, (Object) null));
                return;
            case 1006:
                GameCanvas.gameAA(mResources.gameBG, new Command(mResources.gameEC, this, 10061, (Object) null), new Command(mResources.gameCU, GameCanvas.gameAA(), 8882, (Object) null));
                return;
            case 1009:
                RMS.gameAA();
                return;
            case 2000:
                if (!this.tfUser.getText().equals("") && !this.tfPass.getText().equals("")) {
                    SelectServerScr.unameChange = this.tfUser.getText();
                    SelectServerScr.passChange = this.tfPass.getText();
                }

                GameCanvas.selectsvScr.update();
                return;
            case 2001:
                if (this.isCheck) {
                    this.isCheck = false;
                    return;
                }

                this.isCheck = true;
                return;
            case 2002:
                if (this.tfUser.getText().equals("")) {
                    GameCanvas.setText(mResources.gameDM);
                } else {
                    char[] var5 = this.tfUser.getText().toCharArray();

                    for (var7 = 0; var7 < var5.length; ++var7) {
                        if (!TField.gameAA(var5[var7])) {
                            GameCanvas.setText(mResources.gameDN);
                            return;
                        }
                    }

                    if (this.tfPass.getText().equals("")) {
                        GameCanvas.setText(mResources.gameDO);
                    } else if (this.tfRegPass.getText().equals("")) {
                        GameCanvas.setText(mResources.gameDP);
                    } else {
                        this.tfRegPass.getText().equals("");
                        if (this.tfUser.getText().length() < 5) {
                            GameCanvas.setText(mResources.gameDQ);
                        } else if (!this.tfPass.getText().equals(this.tfRegPass.getText())) {
                            GameCanvas.setText(mResources.gameDR);
                        } else {
                            if (!this.tfEmail.getText().equals("")) {
                                GameCanvas.msgdlg.gameAA(mResources.gameDS[0] + " " + this.tfUser.getText() + ", " + mResources.gameDS[1], new Command(mResources.gameEC, this, 4000, (Object) null), (Command) null, new Command(mResources.gameCU, GameCanvas.instance, 8882, (Object) null));
                                GameCanvas.currentDialog = GameCanvas.msgdlg;
                                return;
                            }

                            GameCanvas.gameAA("Bạn chưa nhập Email/số di động, Email/số di động giúp bạn lấy lại mật khẩu khi mất mật khẩu", new Command(mResources.gameBY, this, 4001, (Object) null), new Command(mResources.gameCU, GameCanvas.instance, 8882, (Object) null));
                        }
                    }
                }
                break;
            case 2003:
                GameMidlet.gameAA("http://dd.ninjaschool.vn/app/index.php?for=event&do=resetpass");
                return;
            case 2004:
                GameCanvas.inputDlg.gameAA(mResources.gameNZ, new Command(mResources.gameCX, this, 20041, (Object) null), 0);
                return;
            case 2005:
                GameCanvas.gameAA("Bạn có muốn reset mật khẩu không?", new Command(mResources.gameCX, this, 20052, (Object) null), new Command(mResources.gameCU, GameCanvas.instance, 8882, (Object) null));
                return;
            case 3000:
                gameAB(false);
                GameCanvas.gameAJ();
                return;
            case 3001:
                gameAB(true);
                GameCanvas.gameAJ();
                return;
            case 4000:
                this.setText(this.tfUser.getText());
                return;
            case 4001:
                this.setText(this.tfUser.getText());
                break;
            case 10021:
                this.isRes = false;
                this.tfRegPass.isFocus = false;
                this.tfPass.isFocus = false;
                this.tfUser.isFocus = true;
                super.right = this.tfUser.cmdClear;
                super.left = this.cmdMenu;
                return;
            case 10041:
                RMS.gameAA("lowGraphic", 0);
                GameCanvas.gameAA(mResources.gameDE, 8885, (Object) null);
                return;
            case 10042:
                RMS.gameAA("lowGraphic", 1);
                GameCanvas.gameAA(mResources.gameDE, 8885, (Object) null);
                return;
            case 10051:
                RMS.gameAA("isSoftKey", 1);
                GameScr.fieldGE = true;
                return;
            case 10052:
                RMS.gameAA("isSoftKey", 2);
                GameScr.fieldGE = false;
                return;
            case 10061:
                GameCanvas.gameAJ();
                RMS.gameAA("indLanguage", -1);
                GameMidlet.instance.notifyDestroyed();
                return;
            case 20041:
                this.strNick = GameCanvas.inputDlg.tfInput.getText().toString();
                GameCanvas.gameAJ();
                if (this.strNick.equals("")) {
                    GameCanvas.setText(mResources.gameDM);
                    return;
                }

                GameCanvas.gameAA(mResources.gameDU, new Command(mResources.gameCH, this, 200421, (Object) null), new Command(mResources.gameCU, this, 200422, (Object) null));
                return;
            case 20051:
                GameScr.gI().update();
                return;
            case 20052:
                GameMidlet.gameAA("http://dd.ninjaschool.vn/app/index.php?for=event&do=resetpass");
                return;
            case 200421:
                GameCanvas.gameAJ();
                String var4 = this.strNick;
                GameMidlet.IP = UpdateServer.listIP[0];
                GameCanvas.gameAB(mResources.gameEJ);
                GameCanvas.AD();
                GameCanvas.gameAB(mResources.PLEASEWAIT);
                Service.gI().requestForgetPass(var4);
                return;
            case 200422:
                GameCanvas.setText(mResources.gameAA(mResources.gameUA, this.strNick));
                return;
        }
        if (var1 >= 20000 && var1 < 20000 + UpdateServer.serverST.length) {
            var1 = UpdateServer.serverST[var1 - 20000];
            GameCanvas.menu.showMenu = false;
            GameMidlet.IP = UpdateServer.listIP[var1];
            GameMidlet.PORT = UpdateServer.listPort[var1];
            GameMidlet.serverLogin = UpdateServer.serverLoginList[var1];
            RMS.gameAA("indServer", UpdateServer.serverST[var1]);
            this.AJ();
        }
    }
}
