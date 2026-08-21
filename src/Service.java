
import java.io.IOException;

public final class Service {

    private Session_ME session = Session_ME.gI();
    private static Service instance;
    private int xx;
    private int yy;

    public static Service gI() {
        if (instance == null) {
            instance = new Service();
        }

        return instance;
    }

    private static Message messageNotLogin(byte var0) {
        try {
            Message var1 = new Message((byte) -29);
            (new StringBuffer("CMD NOT_LOGIN ----> ")).append(var0).toString();
            var1.writer().writeByte(var0);
            return var1;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Message messageNotMap(byte var0) {
        try {
            Message var1 = new Message((byte) -28);
            (new StringBuffer("CMD NOT MAP ----> ")).append(var0).toString();
            var1.writer().writeByte(var0);
            return var1;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Message messageSubCommand(byte var0) {
        try {
            Message var1 = new Message((byte) -30);
            (new StringBuffer("CMD SUB_COMMAND ----> ")).append(var0).toString();
            var1.writer().writeByte(var0);
            return var1;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public final void setClientType() {
        try {
            Message var1;
            (var1 = messageNotLogin((byte) -125)).writer().writeByte(GameMidlet.CLIENT_TYPE);
            var1.writer().writeByte(mGraphics.zoomLevel);
            var1.writer().writeBoolean(GameCanvas.isGPRS);
            var1.writer().writeInt(GameCanvas.w);
            var1.writer().writeInt(GameCanvas.h);
            var1.writer().writeBoolean(TField.isQwerty);
            var1.writer().writeBoolean(GameCanvas.isTouch);
            var1.writer().writeUTF(System.getProperty("microedition.platform"));
            var1.writer().writeByte(0);
            var1.writer().writeInt(0);
            var1.writer().writeByte(mResources.languageID);
            var1.writer().writeInt(GameMidlet.userProvider);
            var1.writer().writeUTF(GameMidlet.clientAgent);
            this.session.sendMessage(var1);
            var1.cleanup();
        } catch (IOException var2) {
            var2.printStackTrace();
        }
    }

    public final void login(String var1, String var2, String var3) {
        gI().setClientType();

        try {
            Message var4;
            (var4 = messageNotLogin((byte) -127)).writer().writeUTF(var1);
            var4.writer().writeUTF(var2);
            var4.writer().writeUTF(var3);
            var4.writer().writeUTF("");
            var4.writer().writeUTF("");
            var4.writer().writeUTF(RMS.loadRMSString("random"));
            var4.writer().writeByte(GameMidlet.serverLogin);
            String clientAuthKey = "VALID_CLIENT_KEY"; // code giới hạn đăng nhập
            var4.writer().writeUTF(clientAuthKey);
            this.session.sendMessage(var4);
            var4.cleanup();
        } catch (IOException var5) {
            var5.printStackTrace();
        }
    }

    public final void requestRegisterNew(String var1, String var2, String var3) {
        try {
            Message var4;
            (var4 = new Message((byte) 118)).writer().writeUTF(var1);
            var4.writer().writeUTF(var2);
            var4.writer().writeUTF(var3);
            this.session.sendMessage(var4);
            var4.cleanup();
        } catch (Exception var5) {
            var5.printStackTrace();
        }
    }

    public final void requestForgetPass(String var1) {
        try {
            Message var2;
            (var2 = messageNotLogin((byte) -122)).writer().writeUTF(var1);
            this.session.sendMessage(var2);
            var2.cleanup();
        } catch (IOException var3) {
            var3.printStackTrace();
        }
    }

    public final void requestChangeMap() {
        Message var1 = new Message((byte) -17);
        this.session.sendMessage(var1);
        var1.cleanup();
    }

    public final void requestChangeZone(int var1, int var2) {
        Message var3 = new Message((byte) 28);

        try {
            var3.writer().writeByte(var1);
            var3.writer().writeByte(var2);
            this.session.sendMessage(var3);
            var3.cleanup();
        } catch (Exception var4) {
        }
    }

    public final void charMove(int var1, int var2) {
        try {
            if (var1 - this.xx != 0 || var2 - this.yy != 0) {
                Message var3;
                (var3 = new Message((byte) 1)).writer().writeShort(var1);
                var3.writer().writeShort(var2);
                this.xx = var1;
                this.yy = var2;
                this.session.sendMessage(var3);
                var3.cleanup();
                return;
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public final void selectCharToPlay(String var1) {
        Message var2 = new Message((byte) -28);

        try {
            var2.writer().writeByte(-126);
            var2.writer().writeUTF(var1);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        this.session.sendMessage(var2);
    }

    public final void createChar(String var1, int var2, int var3) {
        Message var4 = new Message((byte) -28);

        try {
            var4.writer().writeByte(-125);
            var4.writer().writeUTF(var1);
            var4.writer().writeByte(var2);
            var4.writer().writeByte(var3);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        this.session.sendMessage(var4);
    }

    public final void requestModTemplate(int var1) {
        Message var2 = null;

        try {
            (var2 = messageNotMap((byte) -108)).writer().writeShort(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void requestItemInfo(int var1, int var2) {
        Message var3 = null;

        try {
            (var3 = new Message((byte) 42)).writer().writeByte(var1);
            var3.writer().writeByte(var2);
            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void requestItemPlayer(int var1, int var2) {
        Message var3 = null;

        try {
            (var3 = new Message((byte) 94)).writer().writeInt(var1);
            var3.writer().writeByte(var2);
            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void upPotential(int var1, int var2) {
        Message var3 = null;

        try {
            (var3 = messageSubCommand((byte) -109)).writer().writeByte(var1);
            var3.writer().writeShort(var2);
            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void upSkill(int var1, int var2) {
        Message var3 = null;

        try {
            (var3 = messageSubCommand((byte) -108)).writer().writeShort(var1);
            var3.writer().writeByte(var2);
            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void itemBodyToBag(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 15)).writer().writeByte(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void itemMonToBag(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 108)).writer().writeByte(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void itemBoxToBag(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 16)).writer().writeByte(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void itemBagToBox(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 17)).writer().writeByte(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void useItem(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 11)).writer().writeByte(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

        if (Char.getMyChar().arrItemBag[var1] != null && Char.getMyChar().arrItemBag[var1].template.type == 24) {
            GameScr.gI().resetButton();
            InfoDlg.showWait("");
        }

    }

    public final void saleItem(int var1, int var2) {
        GameCanvas.msgdlg.update();
        Message var3 = null;

        try {
            (var3 = new Message((byte) 14)).writer().writeByte(var1);
            if (var2 > 1) {
                var3.writer().writeInt(var2);
            }

            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void saleItem1(int var1, int var2) {
        Message var3 = null;

        try {
            (var3 = new Message((byte) 14)).writer().writeByte(var1);
            if (var2 > 1) {
                var3.writer().writeInt(var2);
            }

            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void buyItem(int var1, int var2, int var3) {
        GameCanvas.msgdlg.update();
        Message var4 = null;

        try {
            (var4 = new Message((byte) 13)).writer().writeByte(var1);
            var4.writer().writeByte(var2);
            if (var3 > 1) {
                var4.writer().writeShort(var3);
            }

            this.session.sendMessage(var4);
        } catch (Exception var7) {
            var7.printStackTrace();
        } finally {
            var4.cleanup();
        }

    }

    public final void buyItem1(int var1, int var2, int var3) {
        Message var4 = null;

        try {
            (var4 = new Message((byte) 13)).writer().writeByte(var1);
            var4.writer().writeByte(var2);
            if (var3 > 1) {
                var4.writer().writeShort(var3);
            }

            this.session.sendMessage(var4);
        } catch (Exception var7) {
            var7.printStackTrace();
        } finally {
            var4.cleanup();
        }

    }

    public final void selectSkill(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 41)).writer().writeShort(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void openFindParty() {
        Message var1 = null;

        try {
            var1 = messageSubCommand((byte) -77);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void openUIZone() {
        Message var1 = null;

        try {
            var1 = new Message((byte) 36);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void useItemChangeMap(int var1, int var2) {
        Message var3 = null;

        try {
            (var3 = new Message((byte) 12)).writer().writeByte(var1);
            var3.writer().writeByte(var2);
            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void openMenu(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 40)).writer().writeShort(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void menu(byte var1, int var2, int var3, int var4) {
        Message var5 = null;

        try {
            var5 = new Message((byte) 29);
            var5.writer().writeByte(var1);
            var5.writer().writeByte(var2);
            var5.writer().writeByte(var3);
            var5.writer().writeByte(var4);
            this.session.sendMessage(var5);
        } catch (Exception var8) {
            var8.printStackTrace();
        } finally {
            var5.cleanup();
        }

    }

    public final void menuId(short var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 34)).writer().writeShort(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void textBoxId(short var1, String var2) {
        Message var3 = null;

        try {
            (var3 = new Message((byte) 92)).writer().writeShort(var1);
            var3.writer().writeUTF(var2);
            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void requestItem(int var1) {
        Message var2 = null;

        try {
            (var2 = messageSubCommand((byte) -103)).writer().writeByte(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void bagSort() {
        Message var1 = null;

        try {
            var1 = messageSubCommand((byte) -107);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void boxSort() {
        Message var1 = null;

        try {
            var1 = messageSubCommand((byte) -106);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void boxCoinIn(int var1) {
        Message var2 = null;

        try {
            (var2 = messageSubCommand((byte) -105)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void boxCoinOut(int var1) {
        Message var2 = null;

        try {
            (var2 = messageSubCommand((byte) -104)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void crystalCollect(Item[] var1) {
        GameCanvas.msgdlg.update();
        Message var2 = null;

        try {
            var2 = new Message((byte) 19);

            for (int var3 = 0; var3 < var1.length; ++var3) {
                if (var1[var3] != null) {
                    var2.writer().writeByte(var1[var3].indexUI);
                }
            }

            this.session.sendMessage(var2);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void upgradeItem(Item var1, Item[] var2, boolean var3) {
        GameCanvas.msgdlg.update();
        Message var4 = null;

        try {
            (var4 = new Message((byte) 21)).writer().writeBoolean(var3);
            var4.writer().writeByte(var1.indexUI);

            for (int var9 = 0; var9 < var2.length; ++var9) {
                if (var2[var9] != null) {
                    var4.writer().writeByte(var2[var9].indexUI);
                }
            }

            this.session.sendMessage(var4);
        } catch (Exception var7) {
            var7.printStackTrace();
        } finally {
            var4.cleanup();
        }

    }

    public final void crystalCollectLock(Item[] var1) {
        GameCanvas.msgdlg.update();
        Message var2 = null;

        try {
            var2 = new Message((byte) 20);

            for (int var3 = 0; var3 < var1.length; ++var3) {
                if (var1[var3] != null) {
                    var2.writer().writeByte(var1[var3].indexUI);
                }
            }

            this.session.sendMessage(var2);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void crystalCollectLock1(Item[] var1) {
        GameCanvas.msgdlg.update();
        Message var2 = null;

        try {
            var2 = new Message((byte) 20);

            for (int var3 = 0; var3 < var1.length; ++var3) {
                if (var1[var3] != null) {
                    var2.writer().writeByte(var1[var3].indexUI);
                }
            }

            this.session.sendMessage(var2);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void splitItem(Item var1) {
        GameCanvas.msgdlg.update();
        Message var2 = null;

        try {
            (var2 = new Message((byte) 22)).writer().writeByte(var1.indexUI);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void acceptInviteTrade(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 44)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void acceptInviteTestDun(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 99)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void acceptInviteTestGT(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 106)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void sendUIConfirmID(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 107)).writer().writeByte(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void acceptInviteTest(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 66)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void cancelInviteTrade() {
        Message var1 = null;

        try {
            var1 = new Message((byte) 56);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void cancelTrade() {
        Message var1 = null;

        try {
            var1 = new Message((byte) 57);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void tradeAccept() {
        Message var1 = null;

        try {
            var1 = new Message((byte) 46);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void tradeItemLock(int var1, Item[] var2) {
        Message var3 = null;

        try {
            (var3 = new Message((byte) 45)).writer().writeInt(var1);
            var1 = 0;

            int var4;
            for (var4 = 0; var4 < var2.length; ++var4) {
                if (var2[var4] != null) {
                    ++var1;
                }
            }

            var3.writer().writeByte(var1);

            for (var4 = 0; var4 < var2.length; ++var4) {
                if (var2[var4] != null) {
                    var3.writer().writeByte(var2[var4].indexUI);
                }
            }

            this.session.sendMessage(var3);
        } catch (Exception var7) {
            var7.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void sendPlayerAttack(MyVector var1, MyVector var2, int var3) {
        Message var4 = null;
        if (var3 != 0) {
            try {
                Mob var5;
                Char var7;
                if (var1.size() > 0 && var2.size() > 0) {
                    if (var3 == 1) {
                        var4 = new Message((byte) 4);
                    } else if (var3 == 2) {
                        var4 = new Message((byte) 73);
                    }

                    var4.writer().writeByte(var1.size());

                    for (var3 = 0; var3 < var1.size(); ++var3) {
                        var5 = (Mob) var1.elementAt(var3);
                        var4.writer().writeByte(var5.mobId);
                    }

                    for (var3 = 0; var3 < var2.size(); ++var3) {
                        if ((var7 = (Char) var2.elementAt(var3)) != null) {
                            var4.writer().writeInt(var7.charID);
                        } else {
                            var4.writer().writeInt(-1);
                        }
                    }
                } else if (var1.size() > 0) {
                    var4 = new Message((byte) 60);

                    for (var3 = 0; var3 < var1.size(); ++var3) {
                        var5 = (Mob) var1.elementAt(var3);
                        var4.writer().writeByte(var5.mobId);
                    }
                } else if (var2.size() > 0) {
                    var4 = new Message((byte) 61);

                    for (var3 = 0; var3 < var2.size(); ++var3) {
                        var7 = (Char) var2.elementAt(var3);
                        var4.writer().writeInt(var7.charID);
                    }
                }

                if (var4 != null) {
                    this.session.sendMessage(var4);
                }
            } catch (Exception var6) {
            }
        }
    }

    public final void pickItem(int var1) {
        Message var2 = null;

        for (int var3 = 0; var3 < GameScr.vItemMap.size(); ++var3) {
            GameScr.vItemMap.elementAt(var3);
        }

        try {
            (var2 = new Message((byte) -14)).writer().writeShort(var1);
            this.session.sendMessage(var2);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void throwItem(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) -12)).writer().writeByte(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void returnTownFromDead() {
        Message var1 = null;

        try {
            var1 = new Message((byte) -9);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void wakeUpFromDead() {
        Message var1 = null;

        try {
            var1 = new Message((byte) -10);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void getTask(int var1, int var2, int var3) {
        Message var8 = null;

        try {
            (var8 = new Message((byte) 47)).writer().writeByte(var1);
            var8.writer().writeByte(var2);
            this.session.sendMessage(var8);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var8.cleanup();
        }

    }

    public final void chat(String var1) {
        if (CodePhu.fieldAC(var1)) {
            return;
        }
        Message var2 = null;

        try {
            (var2 = new Message((byte) -23)).writer().writeUTF(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void adminChat(String var1) {
        Message var2 = null;

        try {
            (var2 = messageNotMap((byte) -78)).writer().writeUTF(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void updateData() {
        Message var1 = null;

        try {
            var1 = messageNotMap((byte) -122);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void updateMap() {
        Message var1 = null;

        try {
            var1 = messageNotMap((byte) -121);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void updateSkill() {
        Message var1 = null;

        try {
            var1 = messageNotMap((byte) -120);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void updateItem() {
        Message var1 = null;

        try {
            var1 = messageNotMap((byte) -119);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void clientOk() {
        Message var1 = null;

        try {
            System.out.println("AUTO LOGIN TRACE: Service.clientOk command=-101");
            var1 = messageNotMap((byte) -101);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void tradeInvite(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 43)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void addFriend(String var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 59)).writer().writeUTF(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void addPartyAccept(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 80)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void addPartyCancel(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 81)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void testInvite(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 65)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void addCuuSat(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 68)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void buffLive(int var1) {
        Message var2 = null;

        try {
            (var2 = messageSubCommand((byte) -79)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void addParty(String var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 79)).writer().writeUTF(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void requestMaptemplate(int var1) {
        Message var2 = null;

        try {
            (var2 = messageNotMap((byte) -109)).writer().writeByte(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void changePk(int var1) {
        Message var2 = null;

        try {
            (var2 = messageSubCommand((byte) -93)).writer().writeByte(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void changeTeamLeader(int var1) {
        Message var2 = null;

        try {
            (var2 = messageSubCommand((byte) -87)).writer().writeByte(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void moveMember(int var1) {
        Message var2 = null;

        try {
            (var2 = messageSubCommand((byte) -86)).writer().writeByte(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void sendUseSkillMyBuff() {
        Message var1 = null;

        try {
            (var1 = new Message((byte) 74)).writer().writeByte(Char.getMyChar().cdir);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void createParty() {
        Message var1 = null;

        try {
            var1 = messageSubCommand((byte) -88);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void lockParty(boolean var1) {
        Message var2 = null;

        try {
            (var2 = messageSubCommand((byte) -76)).writer().writeBoolean(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void outParty() {
        Message var1 = null;

        try {
            var1 = new Message((byte) 83);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void requestFriend() {
        Message var1 = null;

        try {
            var1 = messageSubCommand((byte) -85);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void requestMatchInfo(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 100)).writer().writeByte(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void requestPlayerInfo(MyVector var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 25)).writer().writeByte(var1.size());

            for (int var3 = 0; var3 < var1.size(); ++var3) {
                Char var4 = (Char) var1.elementAt(var3);
                var2.writer().writeInt(var4.charID);
            }

            this.session.sendMessage(var2);
        } catch (Exception var7) {
            var7.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void requestEnemies() {
        Message var1 = null;

        try {
            var1 = messageSubCommand((byte) -84);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void removeFriend(String var1) {
        Message var2 = null;

        try {
            (var2 = messageSubCommand((byte) -83)).writer().writeUTF(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void pleaseInputParty(String var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 23)).writer().writeUTF(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void acceptPleaseParty(String var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 24)).writer().writeUTF(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void viewInfo(String var1, int var2) {
        Message var7 = null;

        try {
            (var7 = new Message((byte) 93)).writer().writeUTF(var1);
            var7.writer().writeByte(0);
            this.session.sendMessage(var7);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var7.cleanup();
        }

    }

    public final void chatParty(String var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) -20)).writer().writeUTF(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void chatGlobal(String var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) -21)).writer().writeUTF(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void chatPrivate(String var1, String var2) {
        Message var3 = null;

        try {
            (var3 = new Message((byte) -22)).writer().writeUTF(var1);
            var3.writer().writeUTF(var2);
            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void chatClan(String var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) -19)).writer().writeUTF(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void sendCardInfo(String var1, String var2) {
        Message var3 = null;

        try {
            (var3 = messageNotMap((byte) -99)).writer().writeUTF(var1);
            var3.writer().writeUTF(var2);
            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void saveRms(String var1, byte[] var2, byte var3) {
        Message var4 = null;

        try {
            (var4 = messageSubCommand((byte) -67)).writer().writeUTF(var1);
            var4.writer().writeInt(var2.length);
            var4.writer().write(var2);
            var4.writer().write(var3);
            this.session.sendMessage(var4);
        } catch (Exception var7) {
            var7.printStackTrace();
        } finally {
            var4.cleanup();
        }

    }

    public final void loadRMS(String var1) {
        Message var2 = null;

        try {
            (var2 = messageSubCommand((byte) -65)).writer().writeUTF(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void adminMove(int var1) {
        Message var2 = null;

        try {
            (var2 = messageSubCommand((byte) -70)).writer().writeByte(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void changeName(String var1, int var2) {
        Message var3 = null;

        try {
            (var3 = messageNotMap((byte) -97)).writer().writeInt(var2);
            var3.writer().writeUTF(var1);
            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void requestIcon(int var1) {
        Message var2 = null;

        try {
            (var2 = messageNotMap((byte) -115)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void requestClanInfo() {
        Message var1 = null;

        try {
            var1 = messageNotMap((byte) -113);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void requestClanMember() {
        Message var1 = null;

        try {
            var1 = messageNotMap((byte) -112);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void requestClanItem() {
        Message var1 = null;

        try {
            var1 = messageNotMap((byte) -111);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void requestClanLog() {
        Message var1 = null;

        try {
            var1 = messageNotMap((byte) -114);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void clanInvite(int var1) {
        Message var2 = null;

        try {
            (var2 = messageSubCommand((byte) -63)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void clanPlease(int var1) {
        Message var2 = null;

        try {
            (var2 = messageSubCommand((byte) -61)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void acceptInviteClan(int var1) {
        Message var2 = null;

        try {
            (var2 = messageSubCommand((byte) -62)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void acceptPleaseClan(int var1) {
        Message var2 = null;

        try {
            (var2 = messageSubCommand((byte) -60)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void changeClanAlert(String var1) {
        Message var2 = null;

        try {
            (var2 = messageNotMap((byte) -95)).writer().writeUTF(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void changeClanType(String var1, int var2) {
        Message var3 = null;

        try {
            (var3 = messageNotMap((byte) -94)).writer().writeUTF(var1);
            var3.writer().writeByte(var2);
            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void moveOutClan(String var1) {
        Message var2 = null;

        try {
            (var2 = messageNotMap((byte) -93)).writer().writeUTF(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void outClan() {
        Message var1 = null;

        try {
            var1 = messageNotMap((byte) -92);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void inputCoinClan(int var1) {
        Message var2 = null;

        try {
            (var2 = messageNotMap((byte) -90)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void outputCoinClan(int var1) {
        Message var2 = null;

        try {
            (var2 = messageNotMap((byte) -89)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void clanUpLevel() {
        Message var1 = null;

        try {
            var1 = messageNotMap((byte) -91);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void doConvertUpgrade(int var1, int var2, int var3) {
        Message var4 = null;

        try {
            (var4 = messageNotMap((byte) -88)).writer().writeByte(var1);
            var4.writer().writeByte(var2);
            var4.writer().writeByte(var3);
            this.session.sendMessage(var4);
        } catch (Exception var7) {
            var7.printStackTrace();
        } finally {
            var4.cleanup();
        }

    }

    public final void inviteClanDun(String var1) {
        Message var2 = null;

        try {
            (var2 = messageNotMap((byte) -87)).writer().writeUTF(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void inviteClanBattlefield(String var1) {
        Message var2 = null;

        try {
            (var2 = messageNotMap((byte) -70)).writer().writeUTF(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void acceptClanBattlefield() {
        Message var1 = null;

        try {
            var1 = messageNotMap((byte) -68);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void inviteClanBattlefieldAll() {
        Message var1 = null;

        try {
            var1 = messageNotMap((byte) -69);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void unlockClanItem() {
        Message var1 = null;

        try {
            var1 = messageNotMap((byte) -62);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void inputNumSplit(int var1, int var2) {
        Message var3 = null;

        try {
            (var3 = messageNotMap((byte) -85)).writer().writeByte(var1);
            var3.writer().writeInt(var2);
            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void activeAccProtect(int var1) {
        Message var2 = null;

        try {
            (var2 = messageNotMap((byte) -105)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void clearAccProtect(int var1) {
        Message var2 = null;

        try {
            (var2 = messageNotMap((byte) -102)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void updateActive(int var1, int var2) {
        Message var3 = null;

        try {
            (var3 = messageNotMap((byte) -104)).writer().writeInt(var1);
            var3.writer().writeInt(var2);
            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void openLockAccProtect(int var1) {
        Message var2 = null;

        try {
            (var2 = messageNotMap((byte) -103)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void rewardPB() {
        Message var1 = null;

        try {
            var1 = messageNotMap((byte) -82);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void rewardCT() {
        Message var1 = null;

        try {
            var1 = messageNotMap((byte) -79);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void sendToSaleItem(Item var1, int var2) {
        Message var3 = null;

        try {
            (var3 = new Message((byte) 102)).writer().writeByte(var1.indexUI);
            var3.writer().writeInt(var2);
            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void requestItemAuction(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 104)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void buyItemAuction(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 105)).writer().writeInt(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void selectCard() {
        Message var1 = null;

        try {
            (var1 = messageNotMap((byte) -72)).writer().writeByte(GameScr.indexSelect);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void sendClanItem(String var1) {
        Message var2 = null;

        try {
            (var2 = messageNotMap((byte) -61)).writer().writeByte(GameScr.indexSelect);
            var2.writer().writeUTF(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void useClanItem() {
        Message var1 = null;

        try {
            (var1 = messageNotMap((byte) -60)).writer().writeByte(GameScr.indexSelect);
            this.session.sendMessage(var1);
        } catch (Exception var4) {
            var4.printStackTrace();
        } finally {
            var1.cleanup();
        }

    }

    public final void luyenthach(Item[] var1) {
        GameCanvas.msgdlg.update();
        Message var2 = null;

        try {
            var2 = new Message((byte) 110);

            int var3;
            for (var3 = 0; var3 < var1.length; ++var3) {
                if (var1[var3] != null && (var1[var3].template.id == 10 || var1[var3].template.id == 11)) {
                    var2.writer().writeByte(var1[var3].indexUI);
                    break;
                }
            }

            for (var3 = 0; var3 < var1.length; ++var3) {
                if (var1[var3] != null && (var1[var3].template.id == 455 || var1[var3].template.id == 456)) {
                    var2.writer().writeByte(var1[var3].indexUI);
                }
            }

            this.session.sendMessage(var2);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void luyenthach1(Item[] var1) {
        Message var2 = null;

        try {
            var2 = new Message((byte) 110);

            int var3;
            for (var3 = 0; var3 < var1.length; ++var3) {
                if (var1[var3] != null && (var1[var3].template.id == 10 || var1[var3].template.id == 11)) {
                    var2.writer().writeByte(var1[var3].indexUI);
                    break;
                }
            }

            for (var3 = 0; var3 < var1.length; ++var3) {
                if (var1[var3] != null && (var1[var3].template.id == 455 || var1[var3].template.id == 456)) {
                    var2.writer().writeByte(var1[var3].indexUI);
                }
            }

            this.session.sendMessage(var2);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void tinhluyen(Item var1, Item[] var2) {
        GameCanvas.msgdlg.update();
        Message var3 = null;

        try {
            (var3 = new Message((byte) 111)).writer().writeByte(var1.indexUI);

            for (int var8 = 0; var8 < var2.length; ++var8) {
                if (var2[var8] != null && (var2[var8].template.id == 455 || var2[var8].template.id == 456 || var2[var8].template.id == 457)) {
                    var3.writer().writeByte(var2[var8].indexUI);
                }
            }

            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void tinhluyen1(Item var1, Item[] var2) {
        Message var3 = null;

        try {
            (var3 = new Message((byte) 111)).writer().writeByte(var1.indexUI);

            for (int var8 = 0; var8 < var2.length; ++var8) {
                if (var2[var8] != null && (var2[var8].template.id == 455 || var2[var8].template.id == 456 || var2[var8].template.id == 457)) {
                    var3.writer().writeByte(var2[var8].indexUI);
                }
            }

            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void dichchuyen(Item var1, Item[] var2) {
        GameCanvas.msgdlg.update();
        Message var3 = null;

        try {
            (var3 = new Message((byte) 112)).writer().writeByte(var1.indexUI);
            int var9 = 0;

            for (int var4 = 0; var4 < var2.length; ++var4) {
                if (var2[var4] != null && var2[var4].template.id == 454) {
                    var3.writer().writeByte(var2[var4].indexUI);
                    ++var9;
                }
            }

            if (var9 >= 20) {
                this.session.sendMessage(var3);
                return;
            }

            GameCanvas.setText(mResources.gameWS);
        } catch (Exception var7) {
            var7.printStackTrace();
            return;
        } finally {
            var3.cleanup();
        }

    }

    public final void sendCatkeo(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 113)).writer().writeShort(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void ChucTet(String var1, String var2, byte var3) {
        Message var4 = null;
        var1 = var1 + " " + var2;

        try {
            (var4 = new Message((byte) -21)).writer().writeUTF(var1);
            var4.writer().writeByte(var3);
            this.session.sendMessage(var4);
        } catch (Exception var7) {
        } finally {
            var4.cleanup();
        }

    }

    public final void SendCapcha(short var1, String var2) {
        Message var3 = null;

        try {
            (var3 = new Message((byte) 118)).writer().writeUTF(var2);
            var3.writer().writeShort(var1);
            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void sendAttackMobFast(short var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 119)).writer().writeByte(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void requestRanked(byte var1, String var2) {
        Message var3 = null;

        try {
            (var3 = new Message((byte) 121)).writer().writeByte(var1);
            var3.writer().writeUTF(var2);
            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void request_imgEffAuto(byte var1, short var2) {
        Message var3 = null;

        try {
            (var3 = messageNotMap((byte) 122)).writer().writeByte(var1);
            var3.writer().writeByte(var2);
            this.session.sendMessage(var3);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var3.cleanup();
        }

    }

    public final void luckyDraw(short var1, String var2, byte var3) {
        Message var4 = null;

        try {
            (var4 = new Message((byte) 92)).writer().writeShort(var1);
            var4.writer().writeUTF(var2);
            var4.writer().writeByte(var3);
            this.session.sendMessage(var4);
        } catch (Exception var7) {
            var7.printStackTrace();
        } finally {
            var4.cleanup();
        }

    }

    public final void info_Kiemduyet(String var1, String var2, String var3, String var4, String var5, String var6, String var7) {
        try {
            Message var8;
            (var8 = new Message((byte) 123)).writer().writeUTF(var1);
            var8.writer().writeUTF(var2);
            var8.writer().writeUTF(var3);
            var8.writer().writeUTF(var4);
            var8.writer().writeUTF(var5);
            var8.writer().writeUTF(var6);
            var8.writer().writeUTF(var7);
            this.session.sendMessage(var8);
            var8.cleanup();
        } catch (Exception var9) {
            var9.printStackTrace();
        }
    }

    public final void send_Captcha(byte var1) {
        Message var2 = null;

        try {
            (var2 = messageNotMap((byte) 122)).writer().writeByte(4);
            var2.writer().writeByte(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void ngockham(byte var1, Item var2, Item var3, Item[] var4) {
        GameCanvas.msgdlg.update();
        Message var5 = null;

        try {
            (var5 = new Message((byte) 124)).writer().writeByte(var1);
            int var10;
            if (var1 == 0) {
                var5.writer().writeByte(var2.indexUI);
                var5.writer().writeByte(var3.indexUI);

                for (var10 = 0; var10 < var4.length; ++var10) {
                    if (var4[var10] != null) {
                        var5.writer().writeByte(var4[var10].indexUI);
                    }
                }
            } else if (var1 == 1) {
                var5.writer().writeByte(var3.indexUI);

                for (var10 = 0; var10 < var4.length; ++var10) {
                    if (var4[var10] != null) {
                        var5.writer().writeByte(var4[var10].indexUI);
                    }
                }
            } else if (var1 == 2 || var1 == 3) {
                var5.writer().writeByte(var3.indexUI);
            }

            this.session.sendMessage(var5);
        } catch (Exception var8) {
            var8.printStackTrace();
        } finally {
            var5.cleanup();
        }

    }

    public final void giaodo(Item[] var1) {
        GameCanvas.msgdlg.update();
        Message var2 = null;

        try {
            var2 = new Message((byte) 126);

            for (int var3 = 0; var3 < var1.length; ++var3) {
                if (var1[var3] != null) {
                    var2.writer().writeByte(var1[var3].indexUI);
                }
            }

            this.session.sendMessage(var2);
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void doGetImgIcon(short var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 125)).writer().writeByte(1);
            var2.writer().writeShort(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void doGetByteData(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 125)).writer().writeByte(2);
            var2.writer().writeShort(var1);
            this.session.sendMessage(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            var2.cleanup();
        }

    }

    public final void doRemoveVithu(int var1) {
        Message var2 = null;

        try {
            (var2 = new Message((byte) 117)).writer().writeByte(0);
            var2.writer().writeByte(var1);
            this.session.sendMessage(var2);
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }
}
