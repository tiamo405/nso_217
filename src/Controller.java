
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import javax.microedition.lcdui.Image;

public final class Controller {

    private static Controller me;
    private Message messWait;
    private static String[] fieldAC;
    private static String fieldAD;
    private static String fieldAE;
    private static String fieldAF;
    private static String fieldAG;
    private static String fieldAH;
    private static String fieldAI;

    static {
        Controller.fieldAC = new String[]{"Bạn chỉ có thể vào lại game sau ", " giây nữa"};
        Controller.fieldAD = "Bạn chưa thể đi đến khu vực này.Hãy hoàn thành nhiệm vụ trước.";
        Controller.fieldAE = "Cửa này vẫn chưa được mở.";
        Controller.fieldAF = "Cửa này chỉ chứa được tối đa 2 người.";
        Controller.fieldAG = "Số nhóm của khu vực này";
        Controller.fieldAH = "Khu vực này đã đầy.";
        Controller.fieldAI = "Thao tác quá nhanh.";
    }

    public final void fieldAB() {
        System.out.println("Connect ok");
    }

    public static Controller gI() {
        if (me == null) {
            me = new Controller();
        }

        return me;
    }

    private static boolean isCaveOutOfTurnsMessage(String message) {
        if (message == null) {
            return false;
        }
        String lower = message.toLowerCase();
        return lower.indexOf("hang") >= 0
                && (lower.indexOf("hết lượt") >= 0
                || lower.indexOf("het luot") >= 0
                || lower.indexOf("số lần") >= 0
                || lower.indexOf("so lan") >= 0);
    }

    public final void gameAB() {
        if (Char.ReConnect && Code.fieldAB != null) {
            Session_ME.gI().fieldAD();
            return;
        }
        GameCanvas.gameAA(mResources.gameEY, 8884, (Object) null);
    }

    public final void fieldAD() {
        System.out.println("Disconnected");
        Code.fieldAB();
        if (AccountAutoManager.onDisconnected()) {
            return;
        }
        if (Code.fieldAB instanceof Stanima && Res.fieldAB().get(11) == 3) {
            Stanima.fieldAY = true;
        }
        if (Char.ReConnect && Code.fieldAB != null) {
            Session_ME.gI().fieldAD();
            return;
        }
        GameCanvas.instance.gameAN();
    }

    private static void gameAB(Message var0) {
        try {
            int var1 = var0.reader().readUnsignedByte();
            Item var4;
            (var4 = GameScr.currentCharViewInfo.arrItemBody[var1]).expires = var0.reader().readLong();
            var4.saleCoinLock = var0.reader().readInt();
            var4.sys = var0.reader().readByte();
            var4.options = new MyVector();

            try {
                while (true) {
                    var4.options.addElement(new ItemOption(var0.reader().readUnsignedByte(), var0.reader().readInt()));
                }
            } catch (Exception var2) {
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }

    private static void gameAC(Message var0) {
        try {
            Item var1 = null;
            int var2 = var0.reader().readInt();

            for (int var3 = 0; var3 < GameScr.arrItemStands.length; ++var3) {
                if (GameScr.arrItemStands[var3].item.itemId == var2) {
                    var1 = GameScr.arrItemStands[var3].item;
                    break;
                }
            }

            var1.typeUI = 37;
            var1.expires = -1L;
            var1.saleCoinLock = var0.reader().readInt();
            if (var1.isTypeBody() || var1.isTypeNgocKham()) {
                var1.options = new MyVector();

                try {
                    var1.upgrade = var0.reader().readByte();
                    var1.sys = var0.reader().readByte();

                    while (true) {
                        var1.options.addElement(new ItemOption(var0.reader().readUnsignedByte(), var0.reader().readInt()));
                    }
                } catch (Exception var4) {
                    return;
                }
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    public final void gameAA(Message fieldAB) {
        try {
            GameScr var256;
            Ranked var2;
            int var3;
            short var4;
            String var5;
            int var7;
            Mob var8;
            Char var9;
            Char var10000;
            BuNhin var73;
            short var76;
            int var77;
            Npc var78;
            Char[] var80;
            int var81;
            byte var85;
            int var86;
            int var91;
            Mob var92;
            byte var185;
            int var186;
            Char var187;
            String var188;
            short var190;
            String var191;
            Item var193;
            byte var194;
            MyVector var195;
            short var196;
            int var197;
            int var200;
            int var201;
            byte var202;
            int var204;
            String var206;
            byte var210;
            String var220;
            String var221;
            short var224;
            boolean var226;
            short var232;
            int var234;
            ItemMap var236;
            Item var240;
            int var242;
            long var247;
            Char var248;
            int var249;
            int var250;
            int var252;
            switch (fieldAB.command) {
                case -30:
                    this.gameAG(fieldAB);
                    break;
                case -29:
                    gameAF(fieldAB);
                    break;
                case -28:
                    this.gameAE(fieldAB);
                case -27:
                case -17:
                case -9:
                case 12:
                case 24:
                case 28:
                case 29:
                case 32:
                case 35:
                case 41:
                case 44:
                case 56:
                case 73:
                case 74:
                case 80:
                case 81:
                case 105:
                case 110:
                case 111:
                case 113:
                case 115:
                case 120:
                default:
                    break;
                case -26:
                    final String utf;
                    GameCanvas.setText(utf = fieldAB.reader().readUTF());
                    AccountAutoManager.onServerMessage(utf);
                    boolean equals = false;
                    boolean equals2 = false;
                    if (utf.startsWith(Controller.fieldAC[0])) {
                        int int1 = 0;
                        try {
                            int1 = Integer.parseInt(utf.substring(Controller.fieldAC[0].length(), utf.indexOf(Controller.fieldAC[1])).trim());
                        } catch (final Exception ex) {
                            ex.printStackTrace();
                        }
                        Session_ME.gI().gameAC();
                        Session_ME.gameAP = false;
                        Session_ME.fieldAE();
                        GameCanvas.fieldBV = int1;
                        GameCanvas.fieldBX = (GameCanvas.fieldBW = System.currentTimeMillis());
                        break;
                    }
                    if (utf.equals(Controller.fieldAI)) {
                        LockGame.fieldAR();
                        break;
                    }
                    if (TileMap.fieldBF && (utf.equals(Controller.fieldAD) || (equals = utf.equals(Controller.fieldAE)) || (equals2 = utf.equals(Controller.fieldAF)) || utf.equals(Controller.fieldAH) || utf.startsWith(Controller.fieldAG))) {
                        if (Code.fieldAB != null && TileMap.isHang(TileMap.mapID)) {
                            if (equals) {
                                final int fieldAI;
                                if ((fieldAI = TileMap.fieldAI(Code.fieldAB.fieldAB)) > 0) {
                                    Code.fieldAB.fieldAB = fieldAI;
                                }
                                Code.fieldAB.fieldAE = -1;
                            } else if (equals2) {
                                final int fieldAH;
                                if ((fieldAH = TileMap.fieldAH(Code.fieldAB.fieldAB)) > 0) {
                                    Code.fieldAB.fieldAB = fieldAH;
                                }
                                Code.fieldAB.fieldAE = -1;
                            }
                        }
                        if (TileMap.fieldBE) {
                            TileMap.fieldBE = false;
                        } else {
                            GameCanvas.gameAJ();
                        }
                        TileMap.fieldAG();
                        break;
                    }
                    break;
                case -25:
                    Info.gameAA(var188 = fieldAB.reader().readUTF(), 150, mFont.tahoma_7b_yellow);
                    ChatManager.gameAD().gameAA(mResources.gameTK[0], mResources.gameVO, var188);
                    if (Code.fieldAB instanceof Stanima) {
                        Code.fieldAC.fieldAA(var188);
                    }
                    if (Code.fieldAB instanceof ChoBoss) {
                        ((ChoBoss) Code.fieldAB).fieldAA(var188);
                        break;
                    }
                    break;
                case -24:
                    final String utf3;
                    if ((utf3 = fieldAB.reader().readUTF()).indexOf("đang đứng nhìn bạn") > 0) {
                        Code.fieldAG(utf3.substring(0, utf3.indexOf("đang đứng nhìn bạn")).trim());
                    } else if (Char.fieldEZ && Code.fieldAB != null && utf3.equals("Không đủ MP để sử dụng")) {
                        Auto.fieldAM = true;
                    } else if (LockGame.fieldAB && utf3.equals("Vật phẩm của người khác")) {
                        LockGame.fieldAD();
                        final ItemMap fieldDQ;
                        if ((fieldDQ = Char.getMyChar().itemFocus) != null) {
                            fieldDQ.fieldAK = true;
                        }
                    } else if (Code.fieldAB instanceof Stanima) {
                        if (Code.fieldAC.fieldAV == 2 && utf3.equals("Cửa hang động đã được khép lại.")) {
                            Stanima.fieldAY = true;
                        } else if (Code.fieldAC.fieldAV == 4 && utf3.equals("Chiến trường đã khép lại, xem kết quả tại Npc Rikudou.")) {
                            TileMap.fieldAG();
                        }
                    } else if (Code.fieldAH != null && !Code.fieldAH.equals(Char.getMyChar().cName) && utf3.equals("Đối phương đang ở trong nhóm khác.")) {
                        Service.gI().outParty();
                    }

                    InfoMe.gameAA(utf3, 50, mFont.tahoma_7_yellow);
                    break;
                case -23:
                    final int int2 = fieldAB.reader().readInt();
                    final String utf4 = fieldAB.reader().readUTF();
                    Char char1;
                    if (Char.getMyChar().charID == int2) {
                        char1 = Char.getMyChar();
                    } else {
                        char1 = GameScr.gameAE(int2);
                    }
                    if (char1 == null) {
                        return;
                    }
                    ChatPopup.addChatPopup(utf4, 100, char1);
                    ChatManager.gameAD().gameAA(mResources.gameTI[0], char1.cName, utf4);

                    break;
                case -22:
                    var221 = fieldAB.reader().readUTF();
                    var188 = fieldAB.reader().readUTF();
                    ChatManager.gameAD().gameAA(var221, var221, var188);
                    if ((!GameScr.isPaintMessage || !ChatManager.gameAD().gameAE().ownerName.equals(var221)) && !ChatManager.blockPrivateChat) {
                        ChatManager.gameAD().gameAC(var221);
                    }
                    Code.fieldAA.fieldAB(var221, var188);

                    break;
                case -21:
                    var220 = fieldAB.reader().readUTF();
                    var206 = fieldAB.reader().readUTF();
                    ChatManager.gameAD().gameAA(mResources.gameTK[0], var220, var206);
                    if (!ChatManager.blockGlobalChat) {
                        Info.gameAA(var220 + ": " + var206, 80, mFont.tahoma_7b_yellow);
                    }
                    break;
                case -20:
                    var5 = fieldAB.reader().readUTF();
                    var221 = fieldAB.reader().readUTF();
                    ChatManager.gameAD().gameAA(mResources.gameTJ[0], var5, var221);
                    if (!GameScr.isPaintMessage || ChatManager.gameAD().gameAE().type != 1) {
                        ChatManager.gameAD();
                        ChatManager.isMessagePt = true;
                    }
                    Code.fieldAD(var5, var221);

                    break;
                case -19:
                    var188 = fieldAB.reader().readUTF();
                    var191 = fieldAB.reader().readUTF();
                    ChatManager.gameAD().gameAA(mResources.gameTL[0], var188, var191);
                    if (!GameScr.isPaintMessage || ChatManager.gameAD().gameAE().type != 4) {
                        ChatManager.isMessageClan = true;
                    }
                    break;
                case -18:
                    GameCanvas.isLoading = true;
                    GameScr.setPasswordTest();
                    TileMap.vGo.removeAllElements();
                    System.gc();
                    TileMap.mapID = (short) fieldAB.reader().readUnsignedByte();
                    TileMap.tileID = fieldAB.reader().readByte();
                    TileMap.bgID = fieldAB.reader().readByte();
                    TileMap.typeMap = fieldAB.reader().readByte();
                    TileMap.mapName = fieldAB.reader().readUTF();
                    TileMap.zoneID = fieldAB.reader().readByte();
                    TileMap.gameAF();

                    try {
                        TileMap.gameAE();
                    } catch (Exception var180) {
                        Service.gI().requestMaptemplate(TileMap.mapID);
                        this.messWait = fieldAB;
                        return;
                    }

                    this.gameAD(fieldAB);
                    if (Char.getMyChar().mobMe != null) {
                        Char.getMyChar().mobMe.x = Char.getMyChar().cx;
                        Char.getMyChar().mobMe.y = Char.getMyChar().cy - 40;
                    }
                    break;
                case -16:
                    Char.isLockKey = true;
                    Char.ischangingMap = true;
                    Mob.vEggMonter.removeAllElements();
                    GameScr.gI().timeStartMap = 0;
                    GameScr.gI().timeLengthMap = 0;
                    Char.getMyChar().mobFocus = null;
                    Char.getMyChar().npcFocus = null;
                    Char.getMyChar().charFocus = null;
                    Char.getMyChar().itemFocus = null;
                    Char.getMyChar().focus.removeAllElements();
                    Char.getMyChar().testCharId = -9999;
                    Char.getMyChar().killCharId = -9999;
                    GameScr.setPasswordTest();
                    GameCanvas.setPasswordTest();
                    if (GameScr.vParty.size() <= 1) {
                        GameScr.vParty.removeAllElements();
                    }

                    GameScr.gI().resetButton();
                    GameScr.gI().center = null;
                    break;
                case -15:
                    var232 = fieldAB.reader().readShort();

                    for (var234 = 0; var234 < GameScr.vItemMap.size(); ++var234) {
                        if (((ItemMap) GameScr.vItemMap.elementAt(var234)).itemMapID == var232) {
                            GameScr.vItemMap.removeElementAt(var234);
                            return;
                        }
                    }

                    return;
                case -14:
                    Char.getMyChar().itemFocus = null;
                    var232 = fieldAB.reader().readShort();

                    for (var234 = 0; var234 < GameScr.vItemMap.size(); ++var234) {
                        if ((var236 = (ItemMap) GameScr.vItemMap.elementAt(var234)).itemMapID == var232) {
                            var236.gameAA(Char.getMyChar().cx, Char.getMyChar().cy - 10);
                            var236.fieldAK = true;
                            if (var236.template.type == 19) {
                                int var237 = fieldAB.reader().readUnsignedShort();
                                var10000 = Char.getMyChar();
                                var10000.yen += var237;
                                if (var236.template.id != 238) {
                                    InfoMe.gameAA(mResources.gamePM + " " + var237 + " " + mResources.gamePB);
                                    return;
                                }
                            } else if (var236.template.type == 25 && var236.template.id != 238) {
                                InfoMe.gameAA(mResources.gamePM + " " + var236.template.name, 15, mFont.tahoma_7_yellow);
                                return;
                            }

                            return;
                        }
                    }
                    LockGame.fieldAD();

                    return;
                case -13:
                    var232 = fieldAB.reader().readShort();

                    for (var234 = 0; var234 < GameScr.vItemMap.size(); ++var234) {
                        if ((var236 = (ItemMap) GameScr.vItemMap.elementAt(var234)).itemMapID == var232) {
                            if ((var9 = GameScr.gameAE(fieldAB.reader().readInt())) == null) {
                                return;
                            }

                            var236.gameAA(var9.cx, var9.cy - 10);
                            if (var236.x < var9.cx) {
                                var9.cdir = -1;
                            } else if (var236.x > var9.cx) {
                                var9.cdir = 1;
                                return;
                            }
                            if (var236 == Char.getMyChar().itemFocus) {
                                var236.fieldAK = true;
                                LockGame.fieldAD();
                            }
                            return;
                        }
                    }

                    return;
                case -12:
                    var210 = fieldAB.reader().readByte();
                    GameScr.vItemMap.addElement(new ItemMap(fieldAB.reader().readShort(), Char.getMyChar().arrItemBag[var210].template.id, Char.getMyChar().cx, Char.getMyChar().cy, fieldAB.reader().readShort(), fieldAB.reader().readShort()));
                    Char.getMyChar().arrItemBag[var210] = null;
                    break;
                case -11:
                    Char.getMyChar().cPk = fieldAB.reader().readByte();
                    Char.getMyChar().gameAA(fieldAB.reader().readShort(), fieldAB.reader().readShort());

                    try {
                        Char.getMyChar().cEXP = fieldAB.reader().readLong();
                        GameScr.gameAA(Char.getMyChar().cEXP, true);
                    } catch (Exception var144) {
                    }

                    Char.getMyChar().countKill = 0;
                    break;
                case -10:
                    if (Char.getMyChar().wdx != 0 || Char.getMyChar().wdy != 0) {
                        Char.getMyChar().cx = Char.getMyChar().wdx;
                        Char.getMyChar().cy = Char.getMyChar().wdy;
                        Char.getMyChar().wdx = Char.getMyChar().wdy = 0;
                    }

                    Char.getMyChar().gameAX();
                    Char.isLockKey = false;
                    break;
                case -8:
                    int var245 = fieldAB.reader().readInt();
                    var10000 = Char.getMyChar();
                    var10000.yen += var245;
                    GameScr.gI().yenTemp = var245;
                    GameScr.gameAA(var245 > 0 ? "+" + var245 : "" + var245, Char.getMyChar().cx, Char.getMyChar().cy - Char.getMyChar().ch - 10, 0, -2, 1);
                    break;
                case -7:
                    var86 = fieldAB.reader().readInt();
                    var10000 = Char.getMyChar();
                    var10000.xu += var86;
                    var10000 = Char.getMyChar();
                    var10000.yen -= var86;
                    GameScr.gameAA("+" + var86, Char.getMyChar().cx, Char.getMyChar().cy - Char.getMyChar().ch - 10, 0, -2, 1);
                    break;
                case -6:
                    if ((var9 = GameScr.gameAE(fieldAB.reader().readInt())) == null) {
                        return;
                    }

                    GameScr.vItemMap.addElement(new ItemMap(fieldAB.reader().readShort(), fieldAB.reader().readShort(), var9.cx, var9.cy, fieldAB.reader().readShort(), fieldAB.reader().readShort()));
                    break;
                case -5:
                    try {
                        (var92 = Mob.gameAA(fieldAB.reader().readUnsignedByte())).sys = fieldAB.reader().readByte();
                        var92.levelBoss = fieldAB.reader().readByte();
                        var92.x = var92.xFirst;
                        var92.y = var92.yFirst;
                        var92.status = 5;
                        var92.injureThenDie = false;
                        var92.hp = fieldAB.reader().readInt();
                        var92.maxHp = var92.hp;
                        if (var92.getTemplate().mobTemplateId == 202) {
                            ServerEffect.gameAA(148, var92.x, var92.y, 0);
                        } else {
                            ServerEffect.gameAA(60, var92.x, var92.y, 1);
                        }
                        Auto.fieldAA(var92);

                    } catch (Exception var155) {
                        var155.printStackTrace();
                    }
                    break;
                case -4:
                    var92 = null;

                    try {
                        var92 = Mob.gameAA(fieldAB.reader().readUnsignedByte());
                    } catch (Exception var151) {
                    }

                    if (var92 != null && var92.status != 0 && var92.status != 0) {
                        var92.gameAF();

                        try {
                            if ((var252 = fieldAB.reader().readInt()) < 0) {
                                var252 = Res.abs(var252) + 32767;
                            }

                            if (fieldAB.reader().readBoolean()) {
                                GameScr.gameAA("-" + var252, var92.x, var92.y - var92.h, 0, -2, 3);
                            } else {
                                GameScr.gameAA("-" + var252, var92.x, var92.y - var92.h, 0, -2, 5);
                            }

                            ItemMap var253 = new ItemMap(fieldAB.reader().readShort(), fieldAB.reader().readShort(), var92.x, var92.y, fieldAB.reader().readShort(), fieldAB.reader().readShort());
                            GameScr.vItemMap.addElement(var253);
                            if (Res.abs(var253.y - Char.getMyChar().cy) < 24 && Res.abs(var253.x - Char.getMyChar().cx) < 24) {
                                Char.getMyChar().charFocus = null;
                            }
                        } catch (Exception var150) {
                        }
                    }
                    break;
                case -3:
                    var92 = null;

                    try {
                        var92 = Mob.gameAA(fieldAB.reader().readUnsignedByte());
                    } catch (Exception var148) {
                        System.out.println("----err null:NPC_ATTACK_ME");
                    }

                    if (var92 != null) {
                        var252 = fieldAB.reader().readInt();

                        try {
                            var250 = fieldAB.reader().readInt();
                        } catch (Exception var147) {
                            var250 = 0;
                        }

                        if (var92.isBusyAttackSomeOne) {
                            Char.getMyChar().gameAA(var252, var250, false, -1);
                            var92.gameAG();
                        } else {
                            var92.dame = var252;
                            var92.dameMp = var250;
                            var92.gameAA(Char.getMyChar());
                        }

                        var190 = fieldAB.reader().readShort();
                        var194 = fieldAB.reader().readByte();
                        var185 = fieldAB.reader().readByte();
                        var92.gameAA(var190, var194, var185);
                        if (Char.getMyChar().cHP >= Char.getMyChar().cMaxHP / 2) {
                            break;
                        }
                        if (var92.isBoss) {
                            System.out.println("Bi Quai TG Danh");
                            break;
                        }
                        if (var92.levelBoss == 1) {
                            System.out.println("Bi TA Danh");
                            break;
                        }
                        if (var92.levelBoss == 2) {
                            System.out.println("Bi TL Danh");
                            break;
                        }
                    }
                    break;
                case -2:
                    var92 = null;

                    try {
                        var92 = Mob.gameAA(fieldAB.reader().readUnsignedByte());
                    } catch (Exception var146) {
                    }

                    if (var92 != null) {
                        if ((var9 = GameScr.gameAE(fieldAB.reader().readInt())) == null) {
                            return;
                        }

                        var250 = fieldAB.reader().readInt();
                        var92.dame = var9.cHP - var250;
                        var9.cHpNew = var250;

                        try {
                            var9.cMP = fieldAB.reader().readInt();
                        } catch (Exception var145) {
                        }

                        if (var92.isBusyAttackSomeOne) {
                            var9.gameAA(var92.dame, 0, false, -1);
                            var92.gameAG();
                        } else {
                            var92.gameAA(var9);
                        }

                        var190 = fieldAB.reader().readShort();
                        var194 = fieldAB.reader().readByte();
                        var185 = fieldAB.reader().readByte();
                        var92.gameAA(var190, var194, var185);
                    }
                    break;
                case -1:
                    var92 = null;

                    try {
                        var92 = Mob.gameAA(fieldAB.reader().readUnsignedByte());
                    } catch (Exception var154) {
                    }

                    if (var92 != null) {
                        var92.hp = fieldAB.reader().readInt();
                        if ((var252 = fieldAB.reader().readInt()) < 0) {
                            var252 = Res.abs(var252) + 32767;
                        }

                        boolean var94 = fieldAB.reader().readBoolean();

                        try {
                            var92.levelBoss = fieldAB.reader().readByte();
                            var92.maxHp = fieldAB.reader().readInt();
                        } catch (Exception var153) {
                        }

                        if (var94) {
                            GameScr.gameAA("-" + var252, var92.x, var92.y - var92.h, 0, -2, 3);
                        } else {
                            GameScr.gameAA("-" + var252, var92.x, var92.y - var92.h, 0, -2, 5);
                        }
                    }
                    break;
                case 0:
                    if ((var9 = GameScr.gameAE(fieldAB.reader().readInt())) == null) {
                        return;
                    }

                    var9.cPk = fieldAB.reader().readByte();
                    if (var9.charID == Char.aCID) {
                        Char.isAFocusDie = true;
                    }

                    var9.gameAA(fieldAB.reader().readShort(), fieldAB.reader().readShort());
                    if (Char.getMyChar().charFocus == var9) {
                        Char.getMyChar().charFocus = null;
                    }
                    break;
                case 1:
                    var91 = fieldAB.reader().readInt();

                    for (var249 = 0; var249 < GameScr.vCharInMap.size(); ++var249) {
                        var248 = null;

                        try {
                            var248 = (Char) GameScr.vCharInMap.elementAt(var249);
                        } catch (Exception var156) {
                            System.out.println("Char null");

                        }

                        if (var248 == null) {
                            return;
                        }

                        if (var248.charID == var91) {
                            var248.cxMoveLast = fieldAB.reader().readShort();
                            var248.cyMoveLast = fieldAB.reader().readShort();
                            var248.gameAA(var248.cxMoveLast, var248.cyMoveLast);
                            var248.lastUpdateTime = System.currentTimeMillis();
                            return;
                        }
                    }

                    return;
                case 2:
                    var91 = fieldAB.reader().readInt();

                    for (var249 = 0; var249 < GameScr.vCharInMap.size(); ++var249) {
                        if ((var248 = (Char) GameScr.vCharInMap.elementAt(var249)) != null && var248.charID == var91) {
                            if (!var248.isInvisible && var248.isHuman && !var248.isNhanban) {
                                ServerEffect.gameAA(60, var248.cx, var248.cy, 1);
                            } else if (!var248.isInvisible && var248.gameBB() && !var248.isHuman) {
                                ServerEffect.gameAA(141, var248.cx, var248.cy, 0);
                            }

                            GameScr.vCharInMap.removeElementAt(var249);
                            Party.gameAA(var91);
                            if (!var248.gameBA() && var248.cName.equals(Char.getMyChar().cName)) {
                                Char.getMyChar().fieldAA = null;
                            }
                            return;
                        }
                    }

                    return;
                case 3:
                    (var9 = new Char()).charID = fieldAB.reader().readInt();
                    if (gameAA(var9, fieldAB)) {
                        GameScr.vCharInMap.addElement(var9);
                        Auto.fieldAB(var9);
                        if (!var9.gameBA() && var9.cName.equals(Char.getMyChar().cName)) {
                            Char.getMyChar().fieldAA = var9;
                            LockGame.fieldBD();
                        }
                        if (var9.charID == -Char.getMyChar().charID) {
                            Char.getMyChar().fieldAB = var9;
                        }
                        if (Code.fieldAB(var9.cName) && !Code.fieldAC.fieldAN()) {
                            Service.gI().addParty(var9.cName);
                            break;
                        }
                    }
                    break;
                case 4:
                    if ((var9 = GameScr.gameAE(fieldAB.reader().readInt())) == null) {
                        return;
                    }

                    if ((TileMap.gameAA(var9.cx, var9.cy) & 2) == 2) {
                        var9.gameAA((SkillPaint) GameScr.sks[fieldAB.reader().readByte()], 0);
                    } else {
                        var9.gameAA((SkillPaint) GameScr.sks[fieldAB.reader().readByte()], 1);
                    }

                    if (var9.isWolf) {
                        var9.isWolf = false;
                        var9.timeSummon = System.currentTimeMillis();
                        ServerEffect.gameAA(60, var9, 1);
                    }

                    if (var9.isMoto) {
                        var9.isMoto = false;
                        var9.isMotoBehind = true;
                    }

                    var185 = fieldAB.reader().readByte();
                    var9.attMobs = new Mob[var185];

                    for (var242 = 0; var242 < var9.attMobs.length; ++var242) {
                        Mob var246 = Mob.gameAA(fieldAB.reader().readUnsignedByte());
                        var9.attMobs[var242] = var246;
                        if (var242 == 0) {
                            if (var9.cx <= var246.x) {
                                var9.cdir = 1;
                            } else {
                                var9.cdir = -1;
                            }
                        }
                    }

                    var9.mobFocus = var9.attMobs[0];
                    var80 = new Char[10];
                    var81 = 0;

                    try {
                        for (var81 = 0; var81 < var80.length; ++var81) {
                            Char var243;
                            if ((var186 = fieldAB.reader().readInt()) == Char.getMyChar().charID) {
                                var243 = Char.getMyChar();
                            } else {
                                var243 = GameScr.gameAE(var186);
                            }

                            var80[var81] = var243;
                            if (var81 == 0) {
                                if (var9.cx <= var243.cx) {
                                    var9.cdir = 1;
                                } else {
                                    var9.cdir = -1;
                                }
                            }
                        }
                    } catch (Exception var174) {
                    }

                    if (var81 > 0) {
                        var9.attChars = new Char[var81];

                        for (var81 = 0; var81 < var9.attChars.length; ++var81) {
                            var9.attChars[var81] = var80[var81];
                        }

                        var9.charFocus = var9.attChars[0];
                    }
                    break;
                case 5:
                    var247 = fieldAB.reader().readLong();
                    Char.getMyChar().cExpDown = 0L;
                    var10000 = Char.getMyChar();
                    var10000.cEXP += var247;
                    int var89 = Char.getMyChar().clevel;
                    GameScr.gameAA(Char.getMyChar().cEXP, true);
                    if (var89 != Char.getMyChar().clevel) {
                        ServerEffect.gameAA(58, Char.getMyChar(), 1);
                    }

                    GameScr.gameAA("+" + var247, Char.getMyChar().cx, Char.getMyChar().cy - Char.getMyChar().ch, 0, -2, 2);
                    if (var247 >= 1000000L) {
                        InfoMe.gameAA(mResources.gamePM + " " + var247 + " " + mResources.gameKA, 20, mFont.tahoma_7_yellow);
                    }
                    break;
                case 6:
                    ItemMap var233 = new ItemMap(fieldAB.reader().readShort(), fieldAB.reader().readShort(), fieldAB.reader().readShort(), fieldAB.reader().readShort());
                    byte[] var235;
                    if ((var235 = NinjaUtil.gameAA(fieldAB)) != null && var235.length > 0) {
                        var233.imgCaptcha = new MyImage();
                        var233.imgCaptcha.img = gameAA(var235);
                    }

                    GameScr.vItemMap.addElement(var233);
                    break;
                case 7:
                    Char.getMyChar().arrItemBag[fieldAB.reader().readByte()].quantity = fieldAB.reader().readShort();
                    break;
                case 8:
                    var210 = fieldAB.reader().readByte();
                    Char.getMyChar().arrItemBag[var210] = new Item();
                    Char.getMyChar().arrItemBag[var210].typeUI = 3;
                    Char.getMyChar().arrItemBag[var210].indexUI = var210;
                    Char.getMyChar().arrItemBag[var210].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                    Char.getMyChar().arrItemBag[var210].isLock = fieldAB.reader().readBoolean();
                    if (Char.getMyChar().arrItemBag[var210].isTypeBody() || Char.getMyChar().arrItemBag[var210].isTypeNgocKham()) {
                        Char.getMyChar().arrItemBag[var210].upgrade = fieldAB.reader().readByte();
                    }

                    Char.getMyChar().arrItemBag[var210].isExpires = fieldAB.reader().readBoolean();

                    try {
                        Char.getMyChar().arrItemBag[var210].quantity = fieldAB.reader().readUnsignedShort();
                    } catch (Exception var166) {
                        Char.getMyChar().arrItemBag[var210].quantity = 1;
                    }

                    if (Char.getMyChar().arrItemBag[var210].template.type == 16) {
                        GameScr.hpPotion += Char.getMyChar().arrItemBag[var210].quantity;
                    }

                    if (Char.getMyChar().arrItemBag[var210].template.type == 17) {
                        GameScr.mpPotion += Char.getMyChar().arrItemBag[var210].quantity;
                    }

                    if (Char.getMyChar().arrItemBag[var210].template.id == 340) {
                        var256 = GameScr.gI();
                        var256.numSprinLeft += Char.getMyChar().arrItemBag[var210].quantity;
                    }

                    if (GameScr.isPaintTrade) {
                        if (GameScr.gI().tradeItemName.equals("")) {
                            var256 = GameScr.gI();
                            var256.tradeItemName = var256.tradeItemName + Char.getMyChar().arrItemBag[var210].template.name;
                        } else {
                            var256 = GameScr.gI();
                            var256.tradeItemName = var256.tradeItemName + ", " + Char.getMyChar().arrItemBag[var210].template.name;
                        }
                    } else if (Char.getMyChar().arrItemBag[var210].template.type != 20) {
                        InfoMe.gameAA(mResources.gamePM + " " + Char.getMyChar().arrItemBag[var210].template.name);
                        LockGame.fieldAT();
                    }
                    break;
                case 9:
                    var193 = Char.getMyChar().arrItemBag[fieldAB.reader().readUnsignedByte()];
                    var226 = false;

                    try {
                        var224 = fieldAB.reader().readShort();
                    } catch (Exception var164) {
                        var224 = 1;
                    }

                    var193.quantity += var224;
                    if (var193.template.type == 16) {
                        GameScr.hpPotion += var224;
                    }

                    if (var193.template.type == 17) {
                        GameScr.mpPotion += var224;
                    }

                    if (var193.template.id == 340) {
                        var256 = GameScr.gI();
                        var256.numSprinLeft += var224;
                    }

                    GameCanvas.gameAJ();
                    if (GameScr.isPaintTrade) {
                        if (GameScr.gI().tradeItemName.equals("")) {
                            var256 = GameScr.gI();
                            var256.tradeItemName = var256.tradeItemName + var193.template.name;
                        } else {
                            var256 = GameScr.gI();
                            var256.tradeItemName = var256.tradeItemName + ", " + var193.template.name;
                        }
                    } else if (var193.template.type != 20) {
                        InfoMe.gameAA(mResources.gamePM + " " + var193.template.name);
                    }
                    break;
                case 10:
                    final byte byte3 = fieldAB.reader().readByte();
                    if (Char.getMyChar().arrItemBag[byte3].template.type == 16) {
                        GameScr.hpPotion -= Char.getMyChar().arrItemBag[byte3].quantity;
                    }
                    if (Char.getMyChar().arrItemBag[byte3].template.type == 17) {
                        GameScr.mpPotion -= Char.getMyChar().arrItemBag[byte3].quantity;
                    }
                    Char.getMyChar().arrItemBag[byte3] = null;
                    if (GameScr.gameBA()) {
                        GameScr.gI().center = null;
                        GameScr.gI().left = null;
                        break;
                    }
                    GameScr.gI().resetButton();

                    break;
                case 11:
                    var210 = fieldAB.reader().readByte();
                    if (Char.getMyChar().arrItemBag[var210].template.type == 24) {
                        InfoDlg.gameAB();
                    }

                    Char.getMyChar().gameAA(var210);
                    Char.getMyChar().gameAA(fieldAB);
                    Char.getMyChar().eff5BuffHp = fieldAB.reader().readShort();
                    Char.getMyChar().eff5BuffMp = fieldAB.reader().readShort();
                    GameScr.gI().gameBJ();
                    LockGame.fieldAR();

                    break;
                case 13:
                    Char.getMyChar().xu = fieldAB.reader().readInt();
                    Char.getMyChar().yen = fieldAB.reader().readInt();
                    Char.getMyChar().luong = fieldAB.reader().readInt();
                    LockGame.fieldAH();
                    GameCanvas.gameAJ();
                    break;
                case 14:
                    int i10;
                    Item item3 = (Char.getMyChar()).arrItemBag[fieldAB.reader().readByte()];
                    (Char.getMyChar()).yen = fieldAB.reader().readInt();
                    try {
                        i10 = fieldAB.reader().readShort();
                    } catch (Exception exception) {
                        i10 = 1;
                    }
                    item3.quantity -= i10;
                    if (item3.template.type == 16) {
                        GameScr.hpPotion -= i10;
                    }
                    if (item3.template.type == 17) {
                        GameScr.mpPotion -= i10;
                    }
                    if (item3.quantity <= 0) {
                        (Char.getMyChar()).arrItemBag[item3.indexUI] = null;
                    }
                    if (GameScr.gameBA()) {
                        (GameScr.gI()).left = (GameScr.gI()).center = null;
                        GameScr.gI().gameBB();
                    }
                    GameCanvas.gameAJ();
                    LockGame.fieldAT();
                    return;

                case 15:
                    Char.getMyChar().gameAC(fieldAB);
                    LockGame.fieldAR();

                    break;
                case 16:
                    Char.getMyChar().gameAE(fieldAB);
                    break;
                case 17:
                    Char.getMyChar().gameAD(fieldAB);
                    break;
                case 18:
                    var210 = fieldAB.reader().readByte();
                    var224 = 1;

                    try {
                        var224 = fieldAB.reader().readShort();
                    } catch (Exception var165) {
                    }

                    if (Char.getMyChar().arrItemBag[var210].template.type == 24) {
                        InfoDlg.gameAB();
                    }

                    if (Char.getMyChar().arrItemBag[var210].template.type == 16) {
                        --GameScr.hpPotion;
                    }

                    if (Char.getMyChar().arrItemBag[var210].template.type == 17) {
                        --GameScr.mpPotion;
                    }

                    if (Char.getMyChar().arrItemBag[var210].quantity > var224) {
                        Item var257 = Char.getMyChar().arrItemBag[var210];
                        var257.quantity -= var224;
                    } else {
                        Char.getMyChar().arrItemBag[var210] = null;
                    }

                    if (GameScr.isPaintInfoMe) {
                        GameScr.gI().gameBJ();
                    }
                    break;
                case 19:
                    Char.getMyChar();
                    Char.gameAA(fieldAB, true);
                    break;
                case 20:
                    Char.getMyChar();
                    Char.gameAA(fieldAB, false);
                    break;
                case 21:
                    var185 = fieldAB.reader().readByte();
                    Char.getMyChar().luong = fieldAB.reader().readInt();
                    Char.getMyChar().xu = fieldAB.reader().readInt();
                    Char.getMyChar().yen = fieldAB.reader().readInt();
                    if (GameScr.itemUpGrade != null) {
                        GameScr.itemUpGrade.upgrade = fieldAB.reader().readByte();
                        GameScr.itemUpGrade.isLock = true;
                        GameScr.itemUpGrade.clearExpire();
                        if (var185 == 1) {
                            GameScr.effUpok = GameScr.efs[53];
                            GameScr.indexEff = 0;
                        }
                    }

                    if (GameScr.arrItemUpGrade != null) {
                        for (var201 = 0; var201 < GameScr.arrItemUpGrade.length; ++var201) {
                            GameScr.arrItemUpGrade[var201] = null;
                        }
                    }

                    if (var185 == 5 || var185 == 6) {
                        if (GameScr.itemSplit != null) {
                            GameScr.itemSplit = null;
                        }

                        if (GameScr.arrItemSplit != null) {
                            for (var201 = 0; var201 < GameScr.arrItemSplit.length; ++var201) {
                                GameScr.arrItemSplit[var201] = null;
                            }
                        }
                    }

                    GameScr.gI().left = GameScr.gI().center = null;
                    GameScr.gI().gameBB();
                    GameCanvas.gameAJ();
                    if (var185 == 5) {
                        InfoMe.gameAA(mResources.gamePT[0] + GameScr.itemUpGrade.upgrade, 20, mFont.tahoma_7_white);
                    } else if (var185 == 6) {
                        InfoMe.gameAA(mResources.gamePT[1] + GameScr.itemUpGrade.upgrade, 20, mFont.tahoma_7_red);
                    } else if (var185 == 1) {
                        InfoMe.gameAA(mResources.gamePS[0] + GameScr.itemUpGrade.upgrade, 20, mFont.tahoma_7_white);
                    } else {
                        InfoMe.gameAA(mResources.gamePS[1] + GameScr.itemUpGrade.upgrade, 20, mFont.tahoma_7_red);
                    }
                    break;
                case 22:
                    var185 = fieldAB.reader().readByte();
                    var191 = mResources.gameCG;

                    for (var3 = 0; var3 < GameScr.arrItemSplit.length; ++var3) {
                        GameScr.arrItemSplit[var3] = null;
                    }

                    for (var3 = 0; var3 < var185; ++var3) {
                        Item var218;
                        (var218 = new Item()).typeUI = 3;
                        var218.indexUI = fieldAB.reader().readByte();
                        var218.template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        var218.expires = -1L;
                        var218.quantity = 1;
                        var218.isLock = GameScr.itemSplit.isLock;
                        Char.getMyChar().arrItemBag[var218.indexUI] = var218;
                        var191 = var191 + var218.template.name;
                        if (var3 < var185 - 1) {
                            var191 = var191 + ", ";
                        }
                    }

                    GameScr.itemSplit.upgrade = 0;
                    GameScr.itemSplit.clearExpire();
                    GameScr.gI().left = GameScr.gI().center = null;
                    GameScr.gI().gameBC();
                    GameCanvas.gameAJ();
                    InfoMe.gameAA(var191);
                    GameScr.effUpok = GameScr.efs[66];
                    GameScr.indexEff = 0;
                    LockGame.fieldAR();

                    break;
                case 23:
                    final String utf11;

                    if (Code.fieldAC(utf11 = fieldAB.reader().readUTF())) {
                        Service.gI().acceptPleaseParty(utf11);
                        break;
                    }
                    GameCanvas.gameAA(String.valueOf(utf11) + " " + mResources.gameQZ, 8889, utf11, 8882, (Object) null);
                    break;
                case 25:
                    byte var95 = fieldAB.reader().readByte();

                    for (int var255 = 0; var255 < var95; ++var255) {
                        int var97 = fieldAB.reader().readInt();
                        short var98 = fieldAB.reader().readShort();
                        short var99 = fieldAB.reader().readShort();
                        int var100 = fieldAB.reader().readInt();
                        Char var101;
                        if ((var101 = GameScr.gameAE(var97)) != null) {
                            var101.cx = var98;
                            var101.cy = var99;
                            var101.cHP = var100;
                            var101.lastUpdateTime = System.currentTimeMillis();
                        }
                    }

                    return;
                case 26:
                    Char.getMyChar().countKill = fieldAB.reader().readUnsignedShort();
                    Char.getMyChar().countKillMax = fieldAB.reader().readUnsignedShort();
                    break;
                case 27:
                    Mob var222 = Mob.gameAA(fieldAB.reader().readUnsignedByte());
                    if ((var186 = fieldAB.reader().readInt()) == Char.getMyChar().charID) {
                        var9 = Char.getMyChar();
                    } else {
                        var9 = GameScr.gameAE(var186);
                    }

                    var9.moveFast = new short[3];
                    var9.moveFast[0] = 0;
                    var9.moveFast[1] = (short) var222.x;
                    var9.moveFast[2] = (short) var222.y;
                    var9.isBlinking = false;
                    break;
                case 30:
                    byte var75 = fieldAB.reader().readByte();

                    try {
                        GameScr.svTitle = fieldAB.reader().readUTF();
                        GameScr.svAction = fieldAB.reader().readUTF();
                    } catch (Exception var158) {
                    }

                    GameScr.gI().gameAD((int) var75);
                    LockGame.fieldAR();

                    break;
                case 31:
                    Char.getMyChar().xuInBox = fieldAB.reader().readInt();
                    Char.getMyChar().arrItemBox = new Item[fieldAB.reader().readUnsignedByte()];
                    if (Code.fieldAB instanceof AutoPrepareNvhn) {
                        System.out.println("AUTO NVHN BOX CLEAN: server trả dữ liệu rương slots="
                                + Char.getMyChar().arrItemBox.length);
                    }

                    for (var77 = 0; var77 < Char.getMyChar().arrItemBox.length; ++var77) {
                        short var251;
                        if ((var251 = fieldAB.reader().readShort()) != -1) {
                            Char.getMyChar().arrItemBox[var77] = new Item();
                            Char.getMyChar().arrItemBox[var77].typeUI = 4;
                            Char.getMyChar().arrItemBox[var77].indexUI = var77;
                            Char.getMyChar().arrItemBox[var77].template = ItemTemplates.gameAA(var251);
                            Char.getMyChar().arrItemBox[var77].isLock = fieldAB.reader().readBoolean();
                            if (Char.getMyChar().arrItemBox[var77].isTypeBody() || Char.getMyChar().arrItemBox[var77].isTypeNgocKham()) {
                                Char.getMyChar().arrItemBox[var77].upgrade = fieldAB.reader().readByte();
                            }

                            Char.getMyChar().arrItemBox[var77].isExpires = fieldAB.reader().readBoolean();
                            Char.getMyChar().arrItemBox[var77].quantity = fieldAB.reader().readShort();
                        }
                    }
                    LockGame.fieldAT();

                    return;
                case 33:
                    byte var238 = fieldAB.reader().readByte();
                    int var239;
                    System.out.println("TypeI=" + var238);

                    if ((var238) == 14) {
                        GameScr.arrItemStore = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemStore.length; ++var239) {
                            GameScr.arrItemStore[var239] = new Item();
                            GameScr.arrItemStore[var239].typeUI = 14;
                            GameScr.arrItemStore[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemStore[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 15) {
                        GameScr.arrItemBook = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemBook.length; ++var239) {
                            GameScr.arrItemBook[var239] = new Item();
                            GameScr.arrItemBook[var239].typeUI = 15;
                            GameScr.arrItemBook[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemBook[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 32) {
                        GameScr.arrItemFashion = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemFashion.length; ++var239) {
                            GameScr.arrItemFashion[var239] = new Item();
                            GameScr.arrItemFashion[var239].typeUI = 32;
                            GameScr.arrItemFashion[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemFashion[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 34) {
                        GameScr.arrItemClanShop = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemClanShop.length; ++var239) {
                            GameScr.arrItemClanShop[var239] = new Item();
                            GameScr.arrItemClanShop[var239].typeUI = 34;
                            GameScr.arrItemClanShop[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemClanShop[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 35) {
                        GameScr.arrItemElites = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemElites.length; ++var239) {
                            GameScr.arrItemElites[var239] = new Item();
                            GameScr.arrItemElites[var239].typeUI = 35;
                            GameScr.arrItemElites[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemElites[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 20) {
                        GameScr.arrItemNonNam = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemNonNam.length; ++var239) {
                            GameScr.arrItemNonNam[var239] = new Item();
                            GameScr.arrItemNonNam[var239].typeUI = var238;
                            GameScr.arrItemNonNam[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemNonNam[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 21) {
                        GameScr.arrItemNonNu = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemNonNu.length; ++var239) {
                            GameScr.arrItemNonNu[var239] = new Item();
                            GameScr.arrItemNonNu[var239].typeUI = var238;
                            GameScr.arrItemNonNu[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemNonNu[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 22) {
                        GameScr.arrItemAoNam = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemAoNam.length; ++var239) {
                            GameScr.arrItemAoNam[var239] = new Item();
                            GameScr.arrItemAoNam[var239].typeUI = var238;
                            GameScr.arrItemAoNam[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemAoNam[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 23) {
                        GameScr.arrItemAoNu = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemAoNu.length; ++var239) {
                            GameScr.arrItemAoNu[var239] = new Item();
                            GameScr.arrItemAoNu[var239].typeUI = var238;
                            GameScr.arrItemAoNu[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemAoNu[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 24) {
                        GameScr.arrItemGangTayNam = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemGangTayNam.length; ++var239) {
                            GameScr.arrItemGangTayNam[var239] = new Item();
                            GameScr.arrItemGangTayNam[var239].typeUI = var238;
                            GameScr.arrItemGangTayNam[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemGangTayNam[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 25) {
                        GameScr.arrItemGangTayNu = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemGangTayNu.length; ++var239) {
                            GameScr.arrItemGangTayNu[var239] = new Item();
                            GameScr.arrItemGangTayNu[var239].typeUI = var238;
                            GameScr.arrItemGangTayNu[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemGangTayNu[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 26) {
                        GameScr.arrItemQuanNam = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemQuanNam.length; ++var239) {
                            GameScr.arrItemQuanNam[var239] = new Item();
                            GameScr.arrItemQuanNam[var239].typeUI = var238;
                            GameScr.arrItemQuanNam[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemQuanNam[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 27) {
                        GameScr.arrItemQuanNu = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemQuanNu.length; ++var239) {
                            GameScr.arrItemQuanNu[var239] = new Item();
                            GameScr.arrItemQuanNu[var239].typeUI = var238;
                            GameScr.arrItemQuanNu[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemQuanNu[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 28) {
                        GameScr.arrItemGiayNam = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemGiayNam.length; ++var239) {
                            GameScr.arrItemGiayNam[var239] = new Item();
                            GameScr.arrItemGiayNam[var239].typeUI = var238;
                            GameScr.arrItemGiayNam[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemGiayNam[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 29) {
                        GameScr.arrItemGiayNu = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemGiayNu.length; ++var239) {
                            GameScr.arrItemGiayNu[var239] = new Item();
                            GameScr.arrItemGiayNu[var239].typeUI = var238;
                            GameScr.arrItemGiayNu[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemGiayNu[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 16) {
                        GameScr.arrItemLien = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemLien.length; ++var239) {
                            GameScr.arrItemLien[var239] = new Item();
                            GameScr.arrItemLien[var239].typeUI = var238;
                            GameScr.arrItemLien[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemLien[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 17) {
                        GameScr.arrItemNhan = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemNhan.length; ++var239) {
                            GameScr.arrItemNhan[var239] = new Item();
                            GameScr.arrItemNhan[var239].typeUI = var238;
                            GameScr.arrItemNhan[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemNhan[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 18) {
                        GameScr.arrItemNgocBoi = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemNgocBoi.length; ++var239) {
                            GameScr.arrItemNgocBoi[var239] = new Item();
                            GameScr.arrItemNgocBoi[var239].typeUI = var238;
                            GameScr.arrItemNgocBoi[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemNgocBoi[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 19) {
                        GameScr.arrItemPhu = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemPhu.length; ++var239) {
                            GameScr.arrItemPhu[var239] = new Item();
                            GameScr.arrItemPhu[var239].typeUI = var238;
                            GameScr.arrItemPhu[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemPhu[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 2) {
                        GameScr.arrItemWeapon = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemWeapon.length; ++var239) {
                            GameScr.arrItemWeapon[var239] = new Item();
                            GameScr.arrItemWeapon[var239].typeUI = var238;
                            GameScr.arrItemWeapon[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemWeapon[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 6) {
                        GameScr.arrItemStack = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemStack.length; ++var239) {
                            GameScr.arrItemStack[var239] = new Item();
                            GameScr.arrItemStack[var239].typeUI = var238;
                            GameScr.arrItemStack[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemStack[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 7) {
                        GameScr.arrItemStackLock = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemStackLock.length; ++var239) {
                            GameScr.arrItemStackLock[var239] = new Item();
                            GameScr.arrItemStackLock[var239].typeUI = var238;
                            GameScr.arrItemStackLock[var239].isLock = true;
                            GameScr.arrItemStackLock[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemStackLock[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else if (var238 == 8) {
                        GameScr.arrItemGrocery = new Item[fieldAB.reader().readByte()];

                        for (var239 = 0; var239 < GameScr.arrItemGrocery.length; ++var239) {
                            GameScr.arrItemGrocery[var239] = new Item();
                            GameScr.arrItemGrocery[var239].typeUI = var238;
                            GameScr.arrItemGrocery[var239].indexUI = fieldAB.reader().readUnsignedByte();
                            GameScr.arrItemGrocery[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        }

                        return;
                    } else {
                        if (var238 == 9) {
                            GameScr.arrItemGroceryLock = new Item[fieldAB.reader().readByte()];

                            for (var239 = 0; var239 < GameScr.arrItemGroceryLock.length; ++var239) {
                                GameScr.arrItemGroceryLock[var239] = new Item();
                                GameScr.arrItemGroceryLock[var239].typeUI = var238;
                                GameScr.arrItemGroceryLock[var239].isLock = true;
                                GameScr.arrItemGroceryLock[var239].indexUI = fieldAB.reader().readUnsignedByte();
                                GameScr.arrItemGroceryLock[var239].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                            }
                        }
                        LockGame.fieldBF();

                        break;
                    }
                case 34:
                    var195 = new MyVector();
                    if (!(var188 = fieldAB.reader().readUTF()).equals("")) {
                        GameScr.gI().gameAA((String) null, (String) var188, true);
                    }

                    var185 = fieldAB.reader().readByte();

                    for (var3 = 0; var3 < var185; ++var3) {
                        var206 = fieldAB.reader().readUTF();
                        Short var228 = new Short(fieldAB.reader().readShort());
                        var195.addElement(new Command(var206, GameCanvas.instance, 88819, var228));
                    }

                    GameCanvas.menu.gameAA(var195);
                    break;
                case 36:
                    GameScr.gI().gameAA(fieldAB);
                    LockGame.fieldAF();

                    break;
                case 37:
                    GameScr.gI().tradeName = fieldAB.reader().readUTF();
                    GameScr.gI().gameAU();
                    LockGame.fieldBB();

                    break;
                case 38:
                    var76 = fieldAB.reader().readShort();

                    for (var77 = 0; var77 < GameScr.vNpc.size(); ++var77) {
                        if ((var78 = (Npc) GameScr.vNpc.elementAt(var77)).template.npcTemplateId == var76 && var78.equals(Char.getMyChar().npcFocus)) {
                            final String utf13;
                            ChatPopup.gameAA(utf13 = fieldAB.reader().readUTF(), var78);
                            if (var78.template.npcTemplateId == 0) {
                                if (Code.fieldAB instanceof AutoEnterCave) {
                                    System.out.println("AUTO NVHN HANG NPC0: [" + utf13 + "]");
                                }
                                boolean autoNvhnCaveOutOfTurns = isCaveOutOfTurnsMessage(utf13);
                                if (utf13.equals("Số lần vào trong hang hôm nay của con đã hết.")
                                        || autoNvhnCaveOutOfTurns) {
                                    Stanima.fieldAZ = true;
                                    if (Code.fieldAB instanceof Stanima) {
                                        Stanima.fieldAY = true;
                                    }
                                    if (Code.fieldAB instanceof Hd9x) {
                                        Code.fieldAC();
                                    }
                                    if (Code.fieldAB instanceof AutoEnterCave) {
                                        System.out.println("AUTO NVHN HANG: hôm nay đã hết lượt vào hang, chuyển nhân vật: " + utf13);
                                        Code.fieldAG();
                                        AccountAutoManager.onCaveEntered();
                                    }
                                    TileMap.fieldAG();
                                }
                            } else if (var78.template.npcTemplateId == 5) {
                                if (utf13.equals("Tốt lắm, ngươi đã chọn nơi này làm nơi trở về khi bị trọng thương")) {
                                    LockGame.fieldAZ();
                                }
                            } else if (var78.template.npcTemplateId == 25) {
                                System.out.println("AUTO NVHN NPC25: [" + utf13 + "]");
                                if (AutoNvhn.isBelowLevel30Message(utf13)) {
                                    AccountAutoManager.onCharacterBelowLevel30(utf13);
                                }
                                if (AutoNvhn.isDailyLimitMessage(utf13)) {
                                    AccountAutoManager.onDailyLimitReached();
                                }
                                if (Code.fieldAB == Code.fieldAD) {
                                    AutoNvhn.fieldAA(utf13);
                                } else if (AutoNvhn.isDailyLimitMessage(utf13)) {
                                    Stanima.fieldAX = true;
                                }
                            }
                            return;
                        }
                    }

                    return;
                case 39:
                    var76 = fieldAB.reader().readShort();

                    for (var77 = 0; var77 < GameScr.vNpc.size(); ++var77) {
                        if ((var78 = (Npc) GameScr.vNpc.elementAt(var77)).template.npcTemplateId == var76 && var78.equals(Char.getMyChar().npcFocus)) {
                            ChatPopup.addChatPopup(fieldAB.reader().readUTF(), 1000, var78);
                            String[] var241 = new String[fieldAB.reader().readByte()];

                            for (var242 = 0; var242 < var241.length; ++var242) {
                                var241[var242] = fieldAB.reader().readUTF();
                            }

                            GameScr.gI();
                            GameScr.gameAA(var241, var78);
                            return;
                        }
                    }

                    return;
                case 40:
                    InfoDlg.gameAB();
                    GameCanvas.gameAI();
                    GameCanvas.gameAH();
                    var195 = new MyVector();

                    try {
                        while (true) {
                            String serverMenu = fieldAB.reader().readUTF();
                            if (Code.fieldAB instanceof AutoEnterCave) {
                                System.out.println("AUTO NVHN HANG SERVER MENU[" + var195.size() + "]: " + serverMenu);
                            }
                            var195.addElement(new Command(serverMenu, GameCanvas.instance, 88822, (Object) null));
                        }
                    } catch (Exception var182) {
                        if (Char.getMyChar().npcFocus == null) {
                            return;
                        }
                        if (Char.getMyChar().npcFocus.charID == 25) {
                            GameScr.fieldGH = var195.size();
                        }

                        for (var3 = 0; var3 < Char.getMyChar().npcFocus.template.menu.length; ++var3) {
                            String[] var216 = Char.getMyChar().npcFocus.template.menu[var3];
                            var195.addElement(new Command(var216[0], GameCanvas.instance, 88820, var216));
                        }

                        GameCanvas.menu.gameAA(var195);
                        // GameCanvas.menu.showbyServer = true;
                        break;
                    }
                case 42:
                    gameAH(fieldAB);
                    break;
                case 43:
                    Integer var212 = new Integer(fieldAB.reader().readInt());
                    if ((var187 = GameScr.gameAE(var212.intValue())) != null) {

                        GameCanvas.gameAA(var187.cName + " " + mResources.gameQT, 88810, var212, 88811, (Object) null);
                    }
                    break;
                case 45:
                    GameScr.gI().typeTradeOrder = 1;
                    GameScr.gI().coinTradeOrder = fieldAB.reader().readInt();
                    GameScr.arrItemTradeOrder = new Item[12];
                    var202 = fieldAB.reader().readByte();

                    for (var201 = 0; var201 < var202; ++var201) {
                        GameScr.arrItemTradeOrder[var201] = new Item();
                        GameScr.arrItemTradeOrder[var201].typeUI = 3;
                        GameScr.arrItemTradeOrder[var201].indexUI = var201;
                        GameScr.arrItemTradeOrder[var201].template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                        GameScr.arrItemTradeOrder[var201].isLock = false;
                        if (GameScr.arrItemTradeOrder[var201].isTypeBody() || GameScr.arrItemTradeOrder[var201].isTypeNgocKham()) {
                            GameScr.arrItemTradeOrder[var201].upgrade = fieldAB.reader().readByte();
                        }

                        GameScr.arrItemTradeOrder[var201].isExpires = fieldAB.reader().readBoolean();
                        GameScr.arrItemTradeOrder[var201].quantity = fieldAB.reader().readShort();
                    }

                    if (GameScr.gI().typeTrade == 1 && GameScr.gI().typeTradeOrder == 1) {
                        GameScr.gI().timeTrade = (int) (System.currentTimeMillis() / 1000L + 5L);
                    }
                    break;
                case 46:
                    GameScr.gI().typeTradeOrder = 2;
                    if (GameScr.gI().typeTrade >= 2 && GameScr.gI().typeTradeOrder >= 2) {
                        InfoDlg.gameAA();
                        LockGame.fieldBB();

                    }
                    break;
                case 47:
                    GameCanvas.taskTick = 150;
                    var196 = fieldAB.reader().readShort();
                    var202 = fieldAB.reader().readByte();
                    String var227 = fieldAB.reader().readUTF();
                    String var229 = fieldAB.reader().readUTF();
                    String[] var66;
                    short[] var67 = new short[(var66 = new String[fieldAB.reader().readByte()]).length];
                    short var68 = -1;

                    int var230;
                    for (var230 = 0; var230 < var66.length; ++var230) {
                        String var231 = fieldAB.reader().readUTF();
                        var67[var230] = -1;
                        if (!var231.equals("")) {
                            var66[var230] = var231;
                        }
                    }

                    try {
                        var68 = fieldAB.reader().readShort();

                        for (var230 = 0; var230 < var66.length; ++var230) {
                            var67[var230] = fieldAB.reader().readShort();
                        }
                    } catch (Exception var171) {
                    }

                    Char.getMyChar().taskMaint = new Task(var196, var202, var227, var229, var66, var67, var68);
                    Char.getMyChar().gameAC(21);
                    if (Char.getMyChar().npcFocus != null) {
                        Npc.npcBE();
                    }
                    LockGame.fieldAP();

                    break;
                case 48:
                    if (Char.getMyChar().taskMaint != null) {
                        GameCanvas.taskTick = 100;
                        ++Char.getMyChar().taskMaint.index;
                        Char.getMyChar().taskMaint.count = 0;
                        if (Char.getMyChar().npcFocus != null && Char.getMyChar().npcFocus.chatPopup != null && Char.getMyChar().taskMaint.index >= 2) {
                            Char.getMyChar().npcFocus.chatPopup = null;
                        }

                        if (Char.getMyChar().taskMaint.index >= Char.getMyChar().taskMaint.subNames.length - 1) {
                            Char.getMyChar().gameAC(61);
                        } else {
                            Char.getMyChar().gameAC(21);
                        }

                        Npc.npcBE();
                    }
                    LockGame.fieldAP();

                    break;
                case 49:
                    ++Char.getMyChar().ctaskId;
                    Char.getMyChar();
                    Char.gameAY();
                    break;
                case 50:
                    GameCanvas.taskTick = 50;
                    Char.getMyChar().taskMaint.count = fieldAB.reader().readShort();
                    if (Char.getMyChar().npcFocus != null) {
                        Npc.npcBE();
                    }
                    break;
                case 51:
                    var92 = null;

                    try {
                        var92 = Mob.gameAA(fieldAB.reader().readUnsignedByte());
                    } catch (Exception var152) {
                    }

                    if (var92 != null) {
                        var92.hp = fieldAB.reader().readInt();
                        GameScr.gameAA("", var92.x, var92.y - var92.h, 0, -2, 4);
                    }
                    break;
                case 52:
                    Char.ischangingMap = false;
                    Char.isLockKey = false;
                    Char.getMyChar().cx = fieldAB.reader().readShort();
                    Char.getMyChar().cy = fieldAB.reader().readShort();
                    Char.getMyChar().cxSend = Char.getMyChar().cx;
                    Char.getMyChar().cySend = Char.getMyChar().cy;
                    break;
                case 53:
                    GameScr.gI().resetButton();
                    if (!(var188 = fieldAB.reader().readUTF()).equals("typemoi")) {
                        var191 = fieldAB.reader().readUTF();
                        GameScr.gI().gameAA(var188, var191, false);
                    } else {
                        var191 = fieldAB.reader().readUTF();
                        var224 = fieldAB.reader().readShort();
                        var220 = fieldAB.reader().readUTF();
                        var4 = fieldAB.reader().readShort();
                        var5 = fieldAB.reader().readUTF();
                        short var219 = fieldAB.reader().readShort();
                        String var209 = fieldAB.reader().readUTF();
                        byte var215 = fieldAB.reader().readByte();
                        String var225 = fieldAB.reader().readUTF();
                        GameScr.gI().gameAA(var191, var224, var220, var4, var5, var219, var209, var225, var215);
                    }
                    break;
                case 54:
                    GameCanvas.gameAA().gameAA(fieldAB.reader().readUTF(), fieldAB.reader().readUTF(), fieldAB.reader().readUTF(), fieldAB.reader().readUTF());
                    break;
                case 55:
                    GameCanvas.gameAA().gameAA(fieldAB.reader().readUTF(), fieldAB.reader().readUTF(), fieldAB.reader().readShort(), fieldAB.reader().readUTF(), fieldAB.reader().readUTF());
                    break;
                case 57:
                    GameCanvas.gameAJ();
                    GameScr.gI().resetButton();
                    LockGame.fieldBB();

                    break;
                case 58:
                    GameScr.arrItemTradeMe = null;
                    GameScr.arrItemTradeOrder = null;
                    if (GameScr.gI().coinTradeOrder > 0) {
                        var256 = GameScr.gI();
                        var256.tradeItemName = var256.tradeItemName + ", " + GameScr.gI().coinTradeOrder + " " + mResources.gamePA;
                        GameScr.gameAA("+" + GameScr.gI().coinTradeOrder, Char.getMyChar().cx, Char.getMyChar().cy - Char.getMyChar().ch - 10, 0, -2, 6);
                    }

                    GameScr.gI().coinTrade = GameScr.gI().coinTradeOrder = 0;
                    GameScr.gI().resetButton();
                    Char.getMyChar().xu = fieldAB.reader().readInt();
                    InfoDlg.gameAB();
                    if (!GameScr.gI().tradeItemName.equals("")) {
                        InfoMe.gameAA(mResources.gamePM + " " + GameScr.gI().tradeItemName);
                    }
                    LockGame.fieldBB();

                    break;
                case 59:
                    var191 = fieldAB.reader().readUTF();
                    Friend var223 = new Friend(var191, (byte) 4);
                    GameScr.vFriendWait.addElement(var223);
                    InfoMe.gameAA(var191 + " " + mResources.gameMJ, 20, mFont.tahoma_7_white);
                    if (GameScr.isPaintFriend) {
                        boolean var217 = false;

                        for (var197 = 0; var197 < GameScr.vFriend.size(); ++var197) {
                            if (((Friend) GameScr.vFriend.elementAt(var197)).friendName.equals(var191)) {
                                var217 = true;
                                break;
                            }
                        }

                        if (!var217) {
                            GameScr.vFriend.addElement(var223);
                            GameScr.gI();
                            GameScr.sortList(0);
                            GameScr.indexRow = 0;
                            GameScr.scrMain.gameAA();
                        }
                    }
                    break;
                case 60:
                    if ((var9 = GameScr.gameAE(fieldAB.reader().readInt())) != null) {
                        Mob.interestChar = var9;
                    }

                    if (var9 == null) {
                        return;
                    }

                    if ((TileMap.gameAA(var9.cx, var9.cy) & 2) == 2) {
                        var9.gameAA((SkillPaint) GameScr.sks[fieldAB.reader().readByte()], 0);
                    } else {
                        var9.gameAA((SkillPaint) GameScr.sks[fieldAB.reader().readByte()], 1);
                    }

                    if (var9.isWolf && var9.vitaWolf >= 500) {
                        var9.isWolf = false;
                        var9.timeSummon = System.currentTimeMillis();
                        ServerEffect.gameAA(60, var9, 1);
                    }

                    if (var9.isMoto) {
                        var9.isMoto = false;
                        var9.isMotoBehind = true;
                        if (var9.vitaWolf > 500) {
                            ServerEffect.gameAA(60, var9, 1);
                        }
                    }

                    Mob[] var82 = new Mob[10];
                    var81 = 0;

                    try {
                        for (var81 = 0; var81 < var82.length; ++var81) {
                            Mob var244 = Mob.gameAA(fieldAB.reader().readUnsignedByte());
                            var82[var81] = var244;
                            if (var81 == 0) {
                                if (var9.cx <= var244.x) {
                                    var9.cdir = 1;
                                } else {
                                    var9.cdir = -1;
                                }
                            }
                        }
                    } catch (Exception var173) {
                    }

                    if (var81 > 0) {
                        var9.attMobs = new Mob[var81];

                        for (var81 = 0; var81 < var9.attMobs.length; ++var81) {
                            var9.attMobs[var81] = var82[var81];
                        }

                        var9.mobFocus = var9.attMobs[0];
                    }
                    break;
                case 61:
                    if ((var9 = GameScr.gameAE(fieldAB.reader().readInt())) == null) {
                        return;
                    }

                    if ((TileMap.gameAA(var9.cx, var9.cy) & 2) == 2) {
                        var9.gameAA((SkillPaint) GameScr.sks[fieldAB.reader().readByte()], 0);
                    } else {
                        var9.gameAA((SkillPaint) GameScr.sks[fieldAB.reader().readByte()], 1);
                    }

                    if (var9.isWolf) {
                        var9.isWolf = false;
                        var9.timeSummon = System.currentTimeMillis();
                        if (var9.vitaWolf >= 500) {
                            ServerEffect.gameAA(60, var9, 1);
                        }
                    }

                    if (var9.isMoto) {
                        var9.isMoto = false;
                        var9.isMotoBehind = true;
                        ServerEffect.gameAA(60, var9, 1);
                    }

                    var80 = new Char[10];
                    var81 = 0;

                    try {
                        for (var81 = 0; var81 < var80.length; ++var81) {
                            Char var83;
                            if ((var186 = fieldAB.reader().readInt()) == Char.getMyChar().charID) {
                                var83 = Char.getMyChar();
                            } else {
                                var83 = GameScr.gameAE(var186);
                            }

                            var80[var81] = var83;
                            if (var81 == 0) {
                                if (var9.cx <= var83.cx) {
                                    var9.cdir = 1;
                                } else {
                                    var9.cdir = -1;
                                }
                            }
                        }
                    } catch (Exception var172) {
                    }

                    if (var81 > 0) {
                        var9.attChars = new Char[var81];

                        for (var81 = 0; var81 < var9.attChars.length; ++var81) {
                            var9.attChars[var81] = var80[var81];
                        }

                        var9.charFocus = var9.attChars[0];
                    }
                    break;
                case 62:
                    if ((var186 = fieldAB.reader().readInt()) == Char.getMyChar().charID) {
                        (var9 = Char.getMyChar()).cHP = fieldAB.reader().readInt();
                        var186 = fieldAB.reader().readInt();
                        var201 = 0;

                        try {
                            var9.cMP = fieldAB.reader().readInt();
                            var201 = fieldAB.reader().readInt();
                        } catch (Exception var169) {
                        }

                        if ((var186 += var201) == 0) {
                            GameScr.gameAA("", var9.cx, var9.cy - var9.ch, 0, -2, 7);
                        } else if (var186 < 0) {
                            var186 = -var186;
                            GameScr.gameAA("-" + var186, var9.cx, var9.cy - var9.ch, 0, -2, 8);
                        } else {
                            GameScr.gameAA("-" + var186, var9.cx, var9.cy - var9.ch, 0, -2, 0);
                        }
                        if (Char.getMyChar().cHP < Char.getMyChar().cMaxHP / 2) {
                            System.out.println("Bi PK: " + var186);
                            break;
                        }
                    } else {
                        if ((var9 = GameScr.gameAE(var186)) == null) {
                            return;
                        }

                        var9.cHP = fieldAB.reader().readInt();
                        var186 = fieldAB.reader().readInt();
                        var201 = 0;

                        try {
                            var9.cMP = fieldAB.reader().readInt();
                            var201 = fieldAB.reader().readInt();
                        } catch (Exception var168) {
                        }

                        if ((var186 += var201) == 0) {
                            GameScr.gameAA("", var9.cx, var9.cy - var9.ch, 0, -2, 4);
                        } else if (var186 < 0) {
                            var186 = -var186;
                            GameScr.gameAA("-" + var186, var9.cx, var9.cy - var9.ch, 0, -2, 3);
                        } else {
                            GameScr.gameAA("-" + var186, var9.cx, var9.cy - var9.ch, 0, -2, 5);
                        }
                    }
                    break;
                case 63:
                    var195 = new MyVector();

                    while (true) {
                        try {
                            var195.addElement(new Command(fieldAB.reader().readUTF(), GameCanvas.instance, 88817, (Object) null));
                        } catch (Exception var179) {
                            GameCanvas.menu.gameAA(var195);
                            return;
                        }
                    }
                case 64:
                    if ((var186 = fieldAB.reader().readInt()) == Char.getMyChar().charID) {
                        var187 = Char.getMyChar();
                    } else {
                        var187 = GameScr.gameAE(var186);
                    }

                    var187.moveFast = new short[3];
                    var187.moveFast[0] = 0;
                    var190 = fieldAB.reader().readShort();
                    var196 = fieldAB.reader().readShort();
                    var187.moveFast[1] = var190;
                    var187.moveFast[2] = var196;
                    var187.isBlinking = false;

                    try {
                        if ((var186 = fieldAB.reader().readInt()) == Char.getMyChar().charID) {
                            var187 = Char.getMyChar();
                        } else {
                            var187 = GameScr.gameAE(var186);
                        }

                        var187.cx = var190;
                        var187.cy = var196;
                    } catch (Exception var160) {
                        var160.printStackTrace();
                    }
                    break;
                case 65:
                    if ((var187 = GameScr.gameAE(fieldAB.reader().readInt())) != null) {
                        GameCanvas.gameAA(var187.cName + " " + mResources.gameQW, 88812, var187, 8882, (Object) null);
                    }
                    break;
                case 66:
                    var3 = fieldAB.reader().readInt();
                    var201 = fieldAB.reader().readInt();
                    if (var3 != Char.getMyChar().charID && var201 != Char.getMyChar().charID) {
                        GameScr.gameAE(var3).testCharId = var201;
                        GameScr.gameAE(var201).testCharId = var3;
                    } else if (var3 == Char.getMyChar().charID) {
                        Char.getMyChar().testCharId = var201;
                        Char.getMyChar().npcFocus = null;
                        Char.getMyChar().mobFocus = null;
                        Char.getMyChar().itemFocus = null;
                        Char.getMyChar().charFocus = GameScr.gameAE(Char.getMyChar().testCharId);
                        Char.getMyChar().charFocus.testCharId = Char.getMyChar().charID;
                        GameScr.gI().cPreFocusID = GameScr.gI().cLastFocusID;
                        GameScr.gI().cLastFocusID = var201;
                        Char.isManualFocus = true;
                    } else if (var201 == Char.getMyChar().charID) {
                        Char.getMyChar().testCharId = var3;
                        Char.getMyChar().npcFocus = null;
                        Char.getMyChar().mobFocus = null;
                        Char.getMyChar().itemFocus = null;
                        Char.getMyChar().charFocus = GameScr.gameAE(Char.getMyChar().testCharId);
                        Char.getMyChar().charFocus.testCharId = Char.getMyChar().charID;
                        GameScr.gI().cPreFocusID = GameScr.gI().cLastFocusID;
                        GameScr.gI().cLastFocusID = var3;
                        Char.isManualFocus = true;
                    }
                    break;
                case 67:
                    var3 = fieldAB.reader().readInt();
                    var201 = fieldAB.reader().readInt();
                    var186 = 0;

                    try {
                        var186 = fieldAB.reader().readInt();
                    } catch (Exception var162) {
                    }

                    if (var3 == Char.getMyChar().charID) {
                        var9 = GameScr.gameAE(var201);
                        if (var186 > 0) {
                            InfoMe.gameAA(mResources.gameAA(mResources.gameMY, var9.cName));
                            Char.getMyChar().cHP = var186;
                            Char.getMyChar().resultTest = 29;
                            if (var9 != null) {
                                var9.resultTest = 89;
                            }
                        } else {
                            if (var9 != null) {
                                var9.resultTest = 59;
                            }

                            Char.getMyChar().resultTest = 59;
                            InfoMe.gameAA(mResources.gameAA(mResources.gameMZ, var9.cName));
                        }

                        Char.getMyChar().testCharId = -9999;
                        Char.getMyChar().charFocus = null;
                        if (GameScr.gI().cPreFocusID >= 0) {
                            GameScr.gI().cLastFocusID = GameScr.gI().cPreFocusID;
                            GameScr.gI().cPreFocusID = -1;
                        } else {
                            GameScr.gI().cLastFocusID = -1;
                        }

                        if (var9 != null) {
                            var9.testCharId = -9999;
                        }
                    } else if (var201 == Char.getMyChar().charID) {
                        var9 = GameScr.gameAE(var3);
                        if (var186 > 0) {
                            if (var9 != null) {
                                var9.cHP = var186;
                            }

                            if (var9 != null) {
                                var9.resultTest = 29;
                            }

                            Char.getMyChar().resultTest = 89;
                            InfoMe.gameAA(mResources.gameAA(mResources.gameMX, var9.cName));
                        } else {
                            if (var9 != null) {
                                var9.resultTest = 59;
                            }

                            Char.getMyChar().resultTest = 59;
                            InfoMe.gameAA(mResources.gameAA(mResources.gameMZ, var9.cName));
                        }

                        if (var9 != null) {
                            var9.testCharId = -9999;
                        }

                        Char.getMyChar().testCharId = -9999;
                        Char.getMyChar().charFocus = null;
                        if (GameScr.gI().cPreFocusID >= 0) {
                            GameScr.gI().cLastFocusID = GameScr.gI().cPreFocusID;
                            GameScr.gI().cPreFocusID = -1;
                        } else {
                            GameScr.gI().cLastFocusID = -1;
                        }
                    } else {
                        Char var208 = GameScr.gameAE(var3);
                        Char var203 = GameScr.gameAE(var201);
                        if (var186 > 0) {
                            if (var208 != null) {
                                var208.cHP = var186;
                            }

                            if (var208 != null) {
                                var208.resultTest = 29;
                            }

                            if (var203 != null) {
                                var203.resultTest = 89;
                            }
                        } else {
                            if (var208 != null) {
                                var208.resultTest = 59;
                            }

                            if (var203 != null) {
                                var203.resultTest = 59;
                            }
                        }

                        if (var208 != null) {
                            var208.testCharId = -9999;
                        }

                        if (var203 != null) {
                            var203.testCharId = -9999;
                        }
                    }
                    break;
                case 68:
                    if ((var9 = GameScr.gameAE(fieldAB.reader().readInt())) != null) {
                        var9.killCharId = Char.getMyChar().charID;
                        Char.getMyChar().npcFocus = null;
                        Char.getMyChar().mobFocus = null;
                        Char.getMyChar().itemFocus = null;
                        Char.getMyChar().charFocus = var9;
                        Char.isManualFocus = true;
                        InfoMe.gameAA(var9.cName + mResources.gameNB, 20, mFont.tahoma_7_red);
                    }
                    break;
                case 69:
                    Char.getMyChar().killCharId = fieldAB.reader().readInt();
                    Char.getMyChar().npcFocus = null;
                    Char.getMyChar().mobFocus = null;
                    Char.getMyChar().itemFocus = null;
                    Char.getMyChar().charFocus = GameScr.gameAE(Char.getMyChar().killCharId);
                    Char.isManualFocus = true;
                    break;
                case 70:
                    var9 = Char.getMyChar();

                    try {
                        var9 = GameScr.gameAE(fieldAB.reader().readInt());
                    } catch (Exception var161) {
                    }

                    var9.killCharId = -9999;
                    break;
                case 71:
                    var247 = fieldAB.reader().readLong();
                    var10000 = Char.getMyChar();
                    var10000.cExpDown -= var247;
                    GameScr.gameAA("+" + var247, Char.getMyChar().cx, Char.getMyChar().cy - Char.getMyChar().ch, 0, -2, 2);
                    break;
                case 72:
                    Char.getMyChar().cPk = fieldAB.reader().readByte();
                    Char.getMyChar().gameAA(fieldAB.reader().readShort(), fieldAB.reader().readShort());
                    Char.getMyChar().cEXP = GameScr.gameAB(Char.getMyChar().clevel - 1);
                    Char.getMyChar().cExpDown = fieldAB.reader().readLong();
                    GameScr.gameAA(Char.getMyChar().cEXP, true);
                    break;
                case 75:
                    var73 = new BuNhin(fieldAB.reader().readUTF(), fieldAB.reader().readShort(), fieldAB.reader().readShort());
                    GameScr.vBuNhin.addElement(var73);
                    ServerEffect.gameAA(60, var73.x, var73.y, 1);
                    break;
                case 76:
                    Mob var74;
                    if ((var74 = Mob.gameAA(fieldAB.reader().readUnsignedByte())) != null) {
                        if ((var73 = GameScr.gameAF(fieldAB.reader().readShort())) == null) {
                            return;
                        }

                        var190 = fieldAB.reader().readShort();
                        var194 = fieldAB.reader().readByte();
                        var185 = fieldAB.reader().readByte();
                        var74.gameAA(var73);
                        var74.gameAA(var190, var194, var185);
                    }
                    break;
                case 77:
                    var73 = (BuNhin) GameScr.vBuNhin.elementAt(fieldAB.reader().readShort());
                    GameScr.vBuNhin.removeElement(var73);
                    ServerEffect.gameAA(60, var73.x, var73.y, 1);
                    break;
                case 78:
                    var92 = null;

                    try {
                        var92 = Mob.gameAA(fieldAB.reader().readUnsignedByte());
                    } catch (Exception var149) {
                    }

                    if (var92 != null && var92.status != 0 && var92.status != 0) {
                        var92.status = 0;
                        ServerEffect.gameAA(60, var92.x, var92.y, 1);
                        ItemMap var93 = new ItemMap(fieldAB.reader().readShort(), fieldAB.reader().readShort(), var92.x, var92.y, fieldAB.reader().readShort(), fieldAB.reader().readShort());
                        GameScr.vItemMap.addElement(var93);
                        if (Res.abs(var93.y - Char.getMyChar().cy) < 24 && Res.abs(var93.x - Char.getMyChar().cx) < 24) {
                            Char.getMyChar().charFocus = null;
                        }
                    }
                    break;
                case 79:
                    var3 = fieldAB.reader().readInt();
                    var206 = fieldAB.reader().readUTF();
                    GameCanvas.gameAA(var206 + " " + mResources.gameQY, 8887, new Integer(var3), 8888, new Integer(var3));
                    break;
                case 82:
                    GameScr.vParty.removeAllElements();
                    boolean var207 = fieldAB.reader().readBoolean();

                    try {
                        for (var204 = 0; var204 < 6; ++var204) {
                            GameScr.vParty.addElement(new Party(fieldAB.reader().readInt(), fieldAB.reader().readByte(), fieldAB.reader().readUTF(), var207));
                        }
                    } catch (Exception var177) {
                    }

                    GameScr.gI().gameAT();
                    final String fieldAD3 = ((Party) GameScr.vParty.firstElement()).name;
                    if (Code.fieldAH == null) {
                        Code.fieldAH = fieldAD3;
                        break;
                    }
                    if (!fieldAD3.equals(Code.fieldAH)) {
                        Service.gI().outParty();
                        break;
                    }
                    break;
                case 83:
                    GameScr.vParty.removeAllElements();
                    GameScr.gI().gameAT();
                    break;
                case 84:
                    Friend var214 = new Friend(fieldAB.reader().readUTF(), fieldAB.reader().readByte());
                    GameScr.gI();
                    GameScr.setText(var214.friendName);
                    if (var214.type == 0) {
                        InfoMe.gameAA(mResources.gameMK + " " + var214.friendName + " " + mResources.gameML);
                        GameScr.vFriend.addElement(var214);
                    } else if (var214.type == 1) {
                        for (var7 = 0; var7 < GameScr.vFriend.size(); ++var7) {
                            if (((Friend) GameScr.vFriend.elementAt(var7)).friendName.equals(var214.friendName)) {
                                GameScr.vFriend.removeElementAt(var7);
                                break;
                            }
                        }

                        InfoMe.gameAA(mResources.gameMM + " " + var214.friendName + " " + mResources.gameFC);
                        var214.type = 3;
                        GameScr.vFriend.insertElementAt(var214, 0);
                    }

                    if (GameScr.isPaintFriend) {
                        GameScr.gI();
                        GameScr.sortList(0);
                        GameScr.indexRow = 0;
                        GameScr.scrMain.gameAA();
                    }
                    break;
                case 85:
                    if ((var8 = Mob.gameAA(fieldAB.reader().readUnsignedByte())) != null) {
                        var8.isDisable = fieldAB.reader().readBoolean();
                    }
                    break;
                case 86:
                    if ((var8 = Mob.gameAA(fieldAB.reader().readUnsignedByte())) != null) {
                        var8.isDontMove = fieldAB.reader().readBoolean();
                    }
                    break;
                case 87:
                    if ((var186 = fieldAB.reader().readInt()) == Char.getMyChar().charID) {
                        var9 = Char.getMyChar();
                    } else {
                        var9 = GameScr.gameAE(var186);
                    }

                    if (var9 == null) {
                        return;
                    }

                    var7 = fieldAB.reader().readUnsignedByte();
                    var190 = fieldAB.reader().readShort();
                    var194 = fieldAB.reader().readByte();
                    var185 = fieldAB.reader().readByte();
                    var202 = 0;
                    var200 = -1;

                    try {
                        if ((var202 = fieldAB.reader().readByte()) == 1) {
                            var200 = fieldAB.reader().readInt();
                        }
                    } catch (Exception var167) {
                        var167.printStackTrace();
                    }

                    if (var9.mobMe != null) {
                        if (var202 == 0) {
                            Mob var211 = Mob.gameAA(var7);
                            var9.mobMe.gameAA(var211);
                        } else {
                            Char var213 = GameScr.gameAE(var200);
                            var9.mobMe.gameAB(var213);
                        }
                    }

                    var9.mobMe.gameAA(var190, var194, var185);
                    break;
                case 88:
                    if ((var186 = fieldAB.reader().readInt()) == Char.getMyChar().charID) {
                        var9 = Char.getMyChar();
                    } else if ((var9 = GameScr.gameAE(var186)) == null) {
                        return;
                    }

                    var9.cHP = var9.cMaxHP;
                    var9.cMP = var9.cMaxMP;
                    var9.cx = fieldAB.reader().readShort();
                    var9.cy = fieldAB.reader().readShort();
                    var9.gameAX();
                    break;
                case 89:
                    if ((var8 = Mob.gameAA(fieldAB.reader().readUnsignedByte())) != null) {
                        var8.isFire = fieldAB.reader().readBoolean();
                    }
                    break;
                case 90:
                    if ((var8 = Mob.gameAA(fieldAB.reader().readUnsignedByte())) != null) {
                        var8.isIce = fieldAB.reader().readBoolean();
                        if (!var8.isIce) {
                            ServerEffect.gameAA(77, var8.x, var8.y - 9, 1);
                        }
                    }
                    break;
                case 91:
                    if ((var8 = Mob.gameAA(fieldAB.reader().readUnsignedByte())) != null) {
                        var8.isWind = fieldAB.reader().readBoolean();
                    }
                    break;
                case 92:
                    var188 = fieldAB.reader().readUTF();
                    Short var199 = new Short(fieldAB.reader().readShort());
                    GameCanvas.inputDlg.gameAA(var188, new Command(mResources.gameEC, GameCanvas.instance, 88818, var199), 0);
                    break;
                case 93:
                    var186 = fieldAB.reader().readInt();
                    GameScr.currentCharViewInfo = new Char();
                    if (Char.getMyChar().charID == var186) {
                        GameScr.currentCharViewInfo = Char.getMyChar();
                    } else {
                        if ((var9 = GameScr.gameAE(var186)) == null) {
                            GameScr.currentCharViewInfo = new Char();
                        } else {
                            GameScr.currentCharViewInfo = var9;
                        }

                        GameScr.currentCharViewInfo.charID = var186;
                        GameScr.currentCharViewInfo.statusMe = 1;
                        GameScr.gI().gameBL();
                    }

                    GameScr.currentCharViewInfo.cName = fieldAB.reader().readUTF();
                    GameScr.currentCharViewInfo.head = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.cgender = fieldAB.reader().readByte();
                    byte var69 = fieldAB.reader().readByte();
                    GameScr.currentCharViewInfo.nClass = GameScr.nClasss[var69];
                    GameScr.currentCharViewInfo.cPk = fieldAB.reader().readByte();
                    GameScr.currentCharViewInfo.cHP = fieldAB.reader().readInt();
                    GameScr.currentCharViewInfo.cMaxHP = fieldAB.reader().readInt();
                    GameScr.currentCharViewInfo.cMP = fieldAB.reader().readInt();
                    GameScr.currentCharViewInfo.cMaxMP = fieldAB.reader().readInt();
                    GameScr.currentCharViewInfo.cspeed = fieldAB.reader().readByte();
                    GameScr.currentCharViewInfo.cResFire = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.cResIce = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.cResWind = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.cdame = fieldAB.reader().readInt();
                    GameScr.currentCharViewInfo.cdameDown = fieldAB.reader().readInt();
                    GameScr.currentCharViewInfo.cExactly = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.cMiss = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.cFatal = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.cReactDame = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.sysUp = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.sysDown = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.clevel = fieldAB.reader().readUnsignedByte();
                    GameScr.currentCharViewInfo.pointUydanh = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.cClanName = fieldAB.reader().readUTF();
                    if (!GameScr.currentCharViewInfo.cClanName.equals("")) {
                        GameScr.currentCharViewInfo.ctypeClan = fieldAB.reader().readByte();
                    }

                    GameScr.currentCharViewInfo.pointUydanh = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.pointNon = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.pointAo = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.pointGangtay = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.pointQuan = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.pointGiay = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.pointVukhi = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.pointLien = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.pointNhan = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.pointNgocboi = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.pointPhu = fieldAB.reader().readShort();
                    GameScr.currentCharViewInfo.countFinishDay = fieldAB.reader().readByte();
                    GameScr.currentCharViewInfo.countLoopBoos = fieldAB.reader().readByte();
                    GameScr.currentCharViewInfo.countPB = fieldAB.reader().readByte();
                    GameScr.currentCharViewInfo.limitTiemnangso = fieldAB.reader().readByte();
                    GameScr.currentCharViewInfo.limitKynangso = fieldAB.reader().readByte();
                    GameScr.currentCharViewInfo.arrItemBody = new Item[32];

                    int var70;
                    short var71;
                    ItemTemplate var72;
                    try {
                        GameScr.currentCharViewInfo.gameAJ();

                        for (var70 = 0; var70 < 16; ++var70) {
                            if ((var71 = fieldAB.reader().readShort()) > -1) {
                                var210 = (var72 = ItemTemplates.gameAA(var71)).type;
                                GameScr.currentCharViewInfo.arrItemBody[var210] = new Item();
                                GameScr.currentCharViewInfo.arrItemBody[var210].indexUI = var210;
                                GameScr.currentCharViewInfo.arrItemBody[var210].typeUI = 5;
                                GameScr.currentCharViewInfo.arrItemBody[var210].template = var72;
                                GameScr.currentCharViewInfo.arrItemBody[var210].isLock = true;
                                GameScr.currentCharViewInfo.arrItemBody[var210].upgrade = fieldAB.reader().readByte();
                                GameScr.currentCharViewInfo.arrItemBody[var210].sys = fieldAB.reader().readByte();
                                if (var210 == 1) {
                                    GameScr.currentCharViewInfo.wp = GameScr.currentCharViewInfo.arrItemBody[var210].template.part;
                                } else if (var210 == 2) {
                                    GameScr.currentCharViewInfo.body = GameScr.currentCharViewInfo.arrItemBody[var210].template.part;
                                } else if (var210 == 6) {
                                    GameScr.currentCharViewInfo.leg = GameScr.currentCharViewInfo.arrItemBody[var210].template.part;
                                }
                            }
                        }
                    } catch (Exception var176) {
                    }

                    try {
                        for (var70 = 0; var70 < 16; ++var70) {
                            if ((var71 = fieldAB.reader().readShort()) > -1) {
                                var204 = (var72 = ItemTemplates.gameAA(var71)).type + 16;
                                GameScr.currentCharViewInfo.arrItemBody[var204] = new Item();
                                GameScr.currentCharViewInfo.arrItemBody[var204].indexUI = var204;
                                GameScr.currentCharViewInfo.arrItemBody[var204].typeUI = 5;
                                GameScr.currentCharViewInfo.arrItemBody[var204].template = var72;
                                GameScr.currentCharViewInfo.arrItemBody[var204].isLock = true;
                                GameScr.currentCharViewInfo.arrItemBody[var204].upgrade = fieldAB.reader().readByte();
                                GameScr.currentCharViewInfo.arrItemBody[var204].sys = fieldAB.reader().readByte();
                                if (var204 == 1) {
                                    GameScr.currentCharViewInfo.wp = GameScr.currentCharViewInfo.arrItemBody[var204].template.part;
                                } else if (var204 == 2) {
                                    GameScr.currentCharViewInfo.body = GameScr.currentCharViewInfo.arrItemBody[var204].template.part;
                                } else if (var204 == 6) {
                                    GameScr.currentCharViewInfo.leg = GameScr.currentCharViewInfo.arrItemBody[var204].template.part;
                                }
                            }
                        }

                        return;
                    } catch (Exception var175) {
                        break;
                    }
                case 94:
                    gameAB(fieldAB);
                    break;
                case 95:
                    int var84 = fieldAB.reader().readInt();
                    var10000 = Char.getMyChar();
                    var10000.xu += var84;
                    GameScr.gameAA(var84 > 0 ? "+" + var84 : "" + var84, Char.getMyChar().cx, Char.getMyChar().cy - Char.getMyChar().ch - 10, 0, -2, 1);
                    break;
                case 96:
                    Char.getMyChar().taskOrders.addElement(new TaskOrder(fieldAB.reader().readByte(), fieldAB.reader().readInt(), fieldAB.reader().readInt(), fieldAB.reader().readUTF(), fieldAB.reader().readUTF(), fieldAB.reader().readUnsignedByte(), fieldAB.reader().readUnsignedByte()));
                    Char.getMyChar().gameAC(21);
                    LockGame.fieldAL();

                    break;
                case 97:
                    var85 = fieldAB.reader().readByte();

                    for (var86 = 0; var86 < Char.getMyChar().taskOrders.size(); ++var86) {
                        TaskOrder var87;
                        if ((var87 = (TaskOrder) Char.getMyChar().taskOrders.elementAt(var86)).taskId == var85) {
                            var87.count = fieldAB.reader().readInt();
                            if (var87.count == var87.maxCount) {
                                Char.getMyChar().gameAC(61);
                            }
                            if (var87.taskId == 0) {
                                AutoNvhn.fieldAV = true;
                            }
                            return;
                        }
                    }

                    return;
                case 98:
                    var85 = fieldAB.reader().readByte();

                    for (var86 = 0; var86 < Char.getMyChar().taskOrders.size(); ++var86) {
                        if (((TaskOrder) Char.getMyChar().taskOrders.elementAt(var86)).taskId == var85) {
                            Char.getMyChar().taskOrders.removeElementAt(var86);
                            break;
                        }
                    }

                    Char.getMyChar().gameAC(21);
                    LockGame.fieldAN();

                    break;
                case 99:
                    if ((var187 = GameScr.gameAE(fieldAB.reader().readInt())) != null) {
                        GameCanvas.gameAA(var187.cName + " " + mResources.gameQX, 88840, var187, 8882, (Object) null);
                    }
                    break;
                case 100:
                    GameScr.vList.removeAllElements();
                    var185 = fieldAB.reader().readByte();
                    var2 = null;

                    for (var3 = 0; var3 < var185; ++var3) {
                        try {
                            DunItem var198;
                            (var198 = new DunItem()).id = fieldAB.reader().readByte();
                            var198.name1 = fieldAB.reader().readUTF();
                            var198.name2 = fieldAB.reader().readUTF();
                            GameScr.vList.addElement(var198);
                        } catch (Exception var163) {
                        }
                    }

                    GameScr.gI().gameAR();
                    break;
                case 101:
                    try {
                        GameScr.currentCharViewInfo.pointTinhTu = fieldAB.reader().readInt();
                        GameScr.currentCharViewInfo.limitPhongLoi = fieldAB.reader().readByte();
                        GameScr.currentCharViewInfo.limitBangHoa = fieldAB.reader().readByte();
                    } catch (Exception var159) {
                        var159.printStackTrace();
                    }
                    break;
                case 102:
                    if ((var240 = Char.getMyChar().arrItemBag[fieldAB.reader().readByte()]) != null) {
                        GameScr.itemSell = var240;
                    }

                    Char.getMyChar().xu = fieldAB.reader().readInt();
                    if (GameScr.itemSell != null) {
                        if (GameScr.itemSell.template.type == 16) {
                            GameScr.hpPotion -= GameScr.itemSell.quantity;
                        }

                        if (GameScr.itemSell.template.type == 17) {
                            GameScr.mpPotion -= GameScr.itemSell.quantity;
                        }

                        Char.getMyChar().arrItemBag[GameScr.itemSell.indexUI] = null;
                        GameScr.itemSell = null;
                        GameScr.gI().resetButton();
                        InfoMe.gameAA(mResources.gameVE);
                    }

                    GameCanvas.gameAJ();
                    break;
                case 103:
                    GameScr.indexMenu = fieldAB.reader().readByte();
                    GameScr.arrItemStands = new ItemStands[fieldAB.reader().readInt()];

                    for (int var79 = 0; var79 < GameScr.arrItemStands.length; ++var79) {
                        GameScr.arrItemStands[var79] = new ItemStands();
                        GameScr.arrItemStands[var79].item = new Item();
                        GameScr.arrItemStands[var79].item.itemId = fieldAB.reader().readInt();
                        GameScr.arrItemStands[var79].timeStart = (int) (System.currentTimeMillis() / 1000L);
                        GameScr.arrItemStands[var79].timeEnd = fieldAB.reader().readInt();
                        GameScr.arrItemStands[var79].item.quantity = fieldAB.reader().readUnsignedShort();
                        GameScr.arrItemStands[var79].seller = fieldAB.reader().readUTF();
                        GameScr.arrItemStands[var79].price = fieldAB.reader().readInt();
                        GameScr.arrItemStands[var79].item.template = ItemTemplates.gameAA(fieldAB.reader().readShort());
                    }

                    GameScr.gI().gameAD((int) 37);
                    break;
                case 104:
                    gameAC(fieldAB);
                    break;
                case 106:
                    if ((var187 = GameScr.gameAE(fieldAB.reader().readInt())) != null) {
                        GameCanvas.gameAA(var187.cName + " " + mResources.gameVS, 88841, var187, 8882, (Object) null);
                    }
                    break;
                case 107:
                    var194 = fieldAB.reader().readByte();
                    GameCanvas.gameAA(fieldAB.reader().readUTF(), 8890, new Integer(var194), 8891, (Object) null);
                    break;
                case 108:
                    Char.getMyChar().gameAB(fieldAB);
                    break;
                case 109:
                    InfoDlg.gameAB();
                    GameCanvas.gameAI();
                    GameCanvas.gameAH();
                    var195 = new MyVector();

                    try {
                        var194 = fieldAB.reader().readByte();

                        for (var197 = 0; var197 < var194; ++var197) {
                            String[] var64 = new String[fieldAB.reader().readByte()];

                            for (int var65 = 0; var65 < var64.length; ++var65) {
                                var64[var65] = fieldAB.reader().readUTF();
                            }

                            var195.addElement(new Command(var64[0], GameCanvas.instance, 88820, var64));
                        }
                    } catch (Exception var181) {
                    }

                    if (Char.getMyChar().npcFocus == null) {
                        return;
                    }

                    GameCanvas.menu.gameAA(var195);
                    break;
                case 112:
                    (var193 = Char.getMyChar().arrItemBag[fieldAB.reader().readByte()]).upgrade = fieldAB.reader().readByte();
                    var193.expires = 0L;
                    break;
                case 114:
                    GameScr.gI().typeba = fieldAB.reader().readByte();
                    break;
                case 116:
                    if ((var9 = GameScr.gameAE(fieldAB.reader().readInt())) != null) {
                        gameAA(var9, fieldAB);
                    }
                    break;
                case 117:
                    byte var192;
                    if ((var192 = fieldAB.reader().readByte()) == -1) {
                        GameCanvas.readMessenge.gameAA(fieldAB);
                        return;
                    }

                    if (GameCanvas.lowGraphic) {
                        return;
                    }

                    try {
                        Mob.vEggMonter.removeAllElements();
                        TileMap.itemMap.clear();
                        GameScr.vItemTreeBehind.removeAllElements();
                        GameScr.vItemTreeBetwen.removeAllElements();
                        GameScr.vItemTreeFront.removeAllElements();

                        for (var3 = 0; var3 < var192; ++var3) {
                            var4 = fieldAB.reader().readShort();
                            var5 = String.valueOf(var4);
                            byte[] var6 = new byte[fieldAB.reader().readInt()];
                            fieldAB.reader().read(var6);
                            Image var189 = gameAA(var6);
                            TileMap.itemMap.gameAA(var5, var189);
                        }

                        var186 = fieldAB.reader().readUnsignedByte();

                        ItemTree var205;
                        for (var3 = 0; var3 < var186; ++var3) {
                            var197 = fieldAB.reader().readUnsignedByte();
                            var200 = fieldAB.reader().readUnsignedByte();
                            var204 = fieldAB.reader().readUnsignedByte();
                            (var205 = new ItemTree(var200, var204)).idTree = var197;
                            GameScr.vItemTreeBehind.addElement(var205);
                        }

                        var186 = fieldAB.reader().readUnsignedByte();

                        for (var3 = 0; var3 < var186; ++var3) {
                            var197 = fieldAB.reader().readUnsignedByte();
                            var200 = fieldAB.reader().readUnsignedByte();
                            var204 = fieldAB.reader().readUnsignedByte();
                            (var205 = new ItemTree(var200, var204)).idTree = var197;
                            GameScr.vItemTreeBetwen.addElement(var205);
                        }

                        var186 = fieldAB.reader().readUnsignedByte();

                        for (var3 = 0; var3 < var186; ++var3) {
                            var197 = fieldAB.reader().readUnsignedByte();
                            var200 = fieldAB.reader().readUnsignedByte();
                            var204 = fieldAB.reader().readUnsignedByte();
                            (var205 = new ItemTree(var200, var204)).idTree = var197;
                            GameScr.vItemTreeFront.addElement(var205);
                        }

                        return;
                    } catch (Exception var178) {
                        var178.printStackTrace();
                        break;
                    }
                case 118:
                    var191 = fieldAB.reader().readUTF();
                    RMS.gameAA("acc", var191);
                    var188 = fieldAB.reader().readUTF();
                    RMS.gameAA("pass", var188);
                    SelectServerScr.uname = var191;
                    SelectServerScr.pass = var188;
                    SelectServerScr.unameChange = "";
                    SelectServerScr.passChange = "";
                    if (!var191.startsWith("tmpusr")) {
                        GameScr.gI().update();
                    }
                    Session_ME.fieldAE();

                    break;
                case 119:
                    if ((var194 = fieldAB.reader().readByte()) == -1) {
                        GameScr.isUseitemAuto = true;
                        GameScr.gameAA(true);
                        if (fieldAB.reader().available() < 4) {
                            break;
                        }
                        if ((GameScr.rangeSearch = fieldAB.reader().readInt()) > 360) {
                            GameScr.isAllmap = true;
                        } else {
                            GameScr.isAllmap = false;
                            GameScr.pointCenterX = Char.getMyChar().cx;
                            GameScr.pointCenterY = Char.getMyChar().cy;
                        }
                    } else if (var194 == 0) {
                        if (fieldAB.reader().available() < 8) {
                            break;
                        }
                        if ((var187 = GameScr.gameAE(fieldAB.reader().readInt())) != null) {
                            ServerEffect.gameAA(141, var187.cx, var187.cy, 2);
                            var190 = fieldAB.reader().readShort();
                            var187.cxMoveLast = var190;
                            var196 = fieldAB.reader().readShort();
                            var187.cyMoveLast = var196;
                            ServerEffect.gameAA(141, var187.cx, var187.cy, 2);
                        }
                    } else {
                        GameScr.isUseitemAuto = false;
                        GameScr.auto = 0;
                    }
                    break;
                case 121:
                    GameScr.vList.removeAllElements();
                    var186 = fieldAB.reader().readUnsignedByte();
                    var2 = null;

                    for (var3 = 0; var3 < var186; ++var3) {
                        try {
                            (var2 = new Ranked()).name = fieldAB.reader().readUTF();
                            var2.id = fieldAB.reader().readInt();
                            var2.stt = fieldAB.reader().readUTF();
                            GameScr.vList.addElement(var2);
                        } catch (Exception var170) {
                        }
                    }

                    GameScr.gI().gameAQ();
                    break;
                case 122:
                    if ((var185 = fieldAB.reader().readByte()) == 0) {
                        gameAI(fieldAB);
                    } else if (var185 == 1) {
                        gameAJ(fieldAB);
                    } else if (var185 == 2) {
                        gameAL(fieldAB);
                    } else if (var185 == 3) {
                        gameAK(fieldAB);
                    }
                    break;
                case 123:
                    if ((var185 = fieldAB.reader().readByte()) == 0) {
                        GameCanvas.isKiemduyet_info = true;
                    } else if (var185 == 1) {
                        GameCanvas.isKiemduyet_info = false;
                    } else if (var185 == 2) {
                        GameCanvas.isKiemduyet = true;
                        RMS.gameAA("isKiemduyet", 0);
                    } else {
                        GameCanvas.isKiemduyet = false;
                        RMS.gameAA("isKiemduyet", 1);
                    }
                    break;
                case 124:
                    gameAM(fieldAB);
                    break;
                case 125:
                    if ((var185 = fieldAB.reader().readByte()) == 0) {
                        gameAN(fieldAB);
                    } else if (var185 == 1) {
                        gameAO(fieldAB);
                    } else if (var185 == 2) {
                        gameAP(fieldAB);
                    }
                    break;
                case 126:
                    byte var96 = fieldAB.reader().readByte();
                    GameCanvas.gameAJ();
                    if (var96 == 0) {
                        GameScr.instance.resetButton();
                    }
            }
        } catch (Exception var183) {
            System.out.println("ERROR COMAND: " + fieldAB.command);
            var183.printStackTrace();
        } finally {
            if (fieldAB != null) {
                fieldAB.cleanup();
            }

        }

    }

    private static void gameAA(DataInputStream var0) {
        try {
            GameScr.vcItem = var0.readByte();
            GameScr.iOptionTemplates = new ItemOptionTemplate[var0.readUnsignedByte()];

            for (int var1 = 0; var1 < GameScr.iOptionTemplates.length; ++var1) {
                GameScr.iOptionTemplates[var1] = new ItemOptionTemplate();
                GameScr.iOptionTemplates[var1].id = var1;
                GameScr.iOptionTemplates[var1].name = var0.readUTF();
                GameScr.iOptionTemplates[var1].type = var0.readByte();
            }

            short var4 = var0.readShort();

            for (int var2 = 0; var2 < var4; ++var2) {
                ItemTemplates.gameAA(new ItemTemplate((short) var2, var0.readByte(), var0.readByte(), var0.readUTF(), var0.readUTF(), var0.readByte(), var0.readShort(), var0.readShort(), var0.readBoolean()));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private static void gameAB(DataInputStream var0) {
        try {
            GameScr.vcSkill = var0.readByte();
            GameScr.sOptionTemplates = new SkillOptionTemplate[var0.readByte()];

            int var1;
            for (var1 = 0; var1 < GameScr.sOptionTemplates.length; ++var1) {
                GameScr.sOptionTemplates[var1] = new SkillOptionTemplate();
                GameScr.sOptionTemplates[var1].id = var1;
                GameScr.sOptionTemplates[var1].name = var0.readUTF();
            }

            GameScr.nClasss = new NClass[var0.readUnsignedByte()];

            for (var1 = 0; var1 < GameScr.nClasss.length; ++var1) {
                GameScr.nClasss[var1] = new NClass();
                GameScr.nClasss[var1].classId = var1;
                GameScr.nClasss[var1].name = var0.readUTF();
                GameScr.nClasss[var1].skillTemplates = new SkillTemplate[var0.readByte()];

                for (int var2 = 0; var2 < GameScr.nClasss[var1].skillTemplates.length; ++var2) {
                    GameScr.nClasss[var1].skillTemplates[var2] = new SkillTemplate();
                    GameScr.nClasss[var1].skillTemplates[var2].id = var0.readByte();
                    GameScr.nClasss[var1].skillTemplates[var2].name = var0.readUTF();
                    GameScr.nClasss[var1].skillTemplates[var2].maxPoint = var0.readByte();
                    GameScr.nClasss[var1].skillTemplates[var2].type = var0.readByte();
                    GameScr.nClasss[var1].skillTemplates[var2].iconId = var0.readShort();
                    short var3 = 150;
                    if (GameCanvas.w == 128 || GameCanvas.h <= 208) {
                        var3 = 100;
                    }

                    GameScr.nClasss[var1].skillTemplates[var2].description = mFont.tahoma_7_white.gameAB(var0.readUTF(), var3);
                    GameScr.nClasss[var1].skillTemplates[var2].skills = new Skill[var0.readByte()];

                    for (int var5 = 0; var5 < GameScr.nClasss[var1].skillTemplates[var2].skills.length; ++var5) {
                        GameScr.nClasss[var1].skillTemplates[var2].skills[var5] = new Skill();
                        GameScr.nClasss[var1].skillTemplates[var2].skills[var5].skillId = var0.readShort();
                        GameScr.nClasss[var1].skillTemplates[var2].skills[var5].template = GameScr.nClasss[var1].skillTemplates[var2];
                        GameScr.nClasss[var1].skillTemplates[var2].skills[var5].point = var0.readByte();
                        GameScr.nClasss[var1].skillTemplates[var2].skills[var5].level = var0.readByte();
                        GameScr.nClasss[var1].skillTemplates[var2].skills[var5].manaUse = var0.readShort();
                        GameScr.nClasss[var1].skillTemplates[var2].skills[var5].coolDown = var0.readInt();
                        GameScr.nClasss[var1].skillTemplates[var2].skills[var5].dx = var0.readShort();
                        GameScr.nClasss[var1].skillTemplates[var2].skills[var5].dy = var0.readShort();
                        GameScr.nClasss[var1].skillTemplates[var2].skills[var5].maxFight = var0.readByte();
                        GameScr.nClasss[var1].skillTemplates[var2].skills[var5].options = new SkillOption[var0.readByte()];

                        for (int var4 = 0; var4 < GameScr.nClasss[var1].skillTemplates[var2].skills[var5].options.length; ++var4) {
                            GameScr.nClasss[var1].skillTemplates[var2].skills[var5].options[var4] = new SkillOption();
                            GameScr.nClasss[var1].skillTemplates[var2].skills[var5].options[var4].param = var0.readShort();
                            GameScr.nClasss[var1].skillTemplates[var2].skills[var5].options[var4].optionTemplate = GameScr.sOptionTemplates[var0.readByte()];
                        }

                        Skills.gameAA(GameScr.nClasss[var1].skillTemplates[var2].skills[var5]);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private static void gameAC(DataInputStream var0) {
        try {
            GameScr.vcMap = var0.readByte();
            TileMap.mapNames = new String[var0.readUnsignedByte()];

            for (int var1 = 0; var1 < TileMap.mapNames.length; ++var1) {
                TileMap.mapNames[var1] = var0.readUTF();
            }

            Npc.arrNpcTemplate = new NpcTemplate[var0.readByte()];

            int var2;
            for (byte var4 = 0; var4 < Npc.arrNpcTemplate.length; ++var4) {
                Npc.arrNpcTemplate[var4] = new NpcTemplate();
                Npc.arrNpcTemplate[var4].npcTemplateId = var4;
                Npc.arrNpcTemplate[var4].name = var0.readUTF();
                Npc.arrNpcTemplate[var4].headId = var0.readShort();
                Npc.arrNpcTemplate[var4].bodyId = var0.readShort();
                Npc.arrNpcTemplate[var4].legId = var0.readShort();
                Npc.arrNpcTemplate[var4].menu = new String[var0.readByte()][];

                for (var2 = 0; var2 < Npc.arrNpcTemplate[var4].menu.length; ++var2) {
                    Npc.arrNpcTemplate[var4].menu[var2] = new String[var0.readByte()];

                    for (int var3 = 0; var3 < Npc.arrNpcTemplate[var4].menu[var2].length; ++var3) {
                        Npc.arrNpcTemplate[var4].menu[var2][var3] = var0.readUTF();
                    }
                }
            }

            short var5;
            Mob.arrMobTemplate = new MobTemplate[var5 = var0.readShort()];

            for (var2 = 0; var2 < var5; ++var2) {
                Mob.arrMobTemplate[var2] = new MobTemplate();
                Mob.arrMobTemplate[var2].mobTemplateId = (short) var2;
                Mob.arrMobTemplate[var2].type = var0.readByte();
                Mob.arrMobTemplate[var2].name = var0.readUTF();
                Mob.arrMobTemplate[var2].hp = var0.readInt();
                Mob.arrMobTemplate[var2].rangeMove = var0.readByte();
                Mob.arrMobTemplate[var2].speed = var0.readByte();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private static void gameAD(DataInputStream var0) {
        try {
            GameScr.vcData = var0.readByte();
            RMS.gameAA("nj_arrow", NinjaUtil.gameAA(var0));
            RMS.gameAA("nj_effect", NinjaUtil.gameAA(var0));
            RMS.gameAA("nj_image", NinjaUtil.gameAA(var0));
            RMS.gameAA("nj_part", NinjaUtil.gameAA(var0));
            RMS.gameAA("nj_skill", NinjaUtil.gameAA(var0));
            GameScr.mapTasks = new byte[(GameScr.tasks = new byte[var0.readByte()][]).length][];

            int var1;
            for (var1 = 0; var1 < GameScr.tasks.length; ++var1) {
                GameScr.tasks[var1] = new byte[var0.readByte()];
                GameScr.mapTasks[var1] = new byte[GameScr.tasks[var1].length];

                for (int var2 = 0; var2 < GameScr.tasks[var1].length; ++var2) {
                    GameScr.tasks[var1][var2] = var0.readByte();
                    GameScr.mapTasks[var1][var2] = var0.readByte();
                }
            }

            GameScr.exps = new long[var0.readUnsignedByte()];

            for (var1 = 0; var1 < GameScr.exps.length; ++var1) {
                GameScr.exps[var1] = var0.readLong();
            }

            GameScr.crystals = new int[var0.readByte()];

            for (var1 = 0; var1 < GameScr.crystals.length; ++var1) {
                GameScr.crystals[var1] = var0.readInt();
            }

            GameScr.upClothe = new int[var0.readByte()];

            for (var1 = 0; var1 < GameScr.upClothe.length; ++var1) {
                GameScr.upClothe[var1] = var0.readInt();
            }

            GameScr.upAdorn = new int[var0.readByte()];

            for (var1 = 0; var1 < GameScr.upAdorn.length; ++var1) {
                GameScr.upAdorn[var1] = var0.readInt();
            }

            GameScr.upWeapon = new int[var0.readByte()];

            for (var1 = 0; var1 < GameScr.upWeapon.length; ++var1) {
                GameScr.upWeapon[var1] = var0.readInt();
            }

            GameScr.coinUpCrystals = new int[var0.readByte()];

            for (var1 = 0; var1 < GameScr.coinUpCrystals.length; ++var1) {
                GameScr.coinUpCrystals[var1] = var0.readInt();
            }

            GameScr.coinUpClothes = new int[var0.readByte()];

            for (var1 = 0; var1 < GameScr.coinUpClothes.length; ++var1) {
                GameScr.coinUpClothes[var1] = var0.readInt();
            }

            GameScr.coinUpAdorns = new int[var0.readByte()];

            for (var1 = 0; var1 < GameScr.coinUpAdorns.length; ++var1) {
                GameScr.coinUpAdorns[var1] = var0.readInt();
            }

            GameScr.coinUpWeapons = new int[var0.readByte()];

            for (var1 = 0; var1 < GameScr.coinUpWeapons.length; ++var1) {
                GameScr.coinUpWeapons[var1] = var0.readInt();
            }

            GameScr.goldUps = new int[var0.readByte()];

            for (var1 = 0; var1 < GameScr.goldUps.length; ++var1) {
                GameScr.goldUps[var1] = var0.readInt();
            }

            GameScr.maxPercents = new int[var0.readByte()];

            for (var1 = 0; var1 < GameScr.maxPercents.length; ++var1) {
                GameScr.maxPercents[var1] = var0.readInt();
            }

            Effect.effTemplates = new EffectTemplate[var0.readByte()];

            for (var1 = 0; var1 < Effect.effTemplates.length; ++var1) {
                Effect.effTemplates[var1] = new EffectTemplate();
                Effect.effTemplates[var1].id = var0.readByte();
                Effect.effTemplates[var1].type = var0.readByte();
                var0.readUTF();
                Effect.effTemplates[var1].iconId = var0.readShort();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static Image gameAA(byte[] var0) {
        try {
            return Image.createImage(var0, 0, var0.length);
        } catch (Exception var1) {
            return null;
        }
    }

    private void gameAD(Message var1) {
        try {
            Auto.fieldAB();
            Auto.fieldAC();
            Auto.fieldAM = false;
            final Char fieldAG;
            (fieldAG = Char.getMyChar()).fieldAB = null;
            Char var10000 = Char.getMyChar();
            Char var10001 = fieldAG;
            Char.getMyChar();
            var10000.cx = var10001.cxSend = var1.reader().readShort();
            var10000 = fieldAG;
            var10001 = fieldAG;
            Char.getMyChar();
            var10000.cy = var10001.cySend = var1.reader().readShort();
            byte var10 = var1.reader().readByte();

            int var2;
            for (var2 = 0; var2 < var10; ++var2) {
                TileMap.vGo.addElement(new Waypoint(var1.reader().readShort(), var1.reader().readShort(), var1.reader().readShort(), var1.reader().readShort()));
            }
            Auto.fieldAB();
//            for (byte byte2 = var1.reader().readByte(), b2 = 0; b2 < byte2; ++b2) {
//                GameScr.fieldAA(new Mob(b2, var1.reader().readBoolean(), var1.reader().readBoolean(), var1.reader().readBoolean(), var1.reader().readBoolean(), var1.reader().readBoolean(), var1.reader().readUnsignedByte(), var1.reader().readByte(), var1.reader().readInt(), var1.reader().readUnsignedByte(), var1.reader().readInt(), var1.reader().readShort(), var1.reader().readShort(), var1.reader().readByte(), var1.reader().readByte(), var1.reader().readBoolean(), false), b2);
//            }
            var10 = var1.reader().readByte();
            for (byte byte2 = 0; byte2 < var10; ++byte2) {
                GameScr.fieldAA(new Mob((short) byte2, var1.reader().readBoolean(), var1.reader().readBoolean(), var1.reader().readBoolean(), var1.reader().readBoolean(), var1.reader().readBoolean(), var1.reader().readShort(), var1.reader().readByte(), var1.reader().readInt(), var1.reader().readUnsignedByte(), var1.reader().readInt(), var1.reader().readShort(), var1.reader().readShort(), var1.reader().readByte(), var1.reader().readByte(), var1.reader().readBoolean(), false), byte2);
            }
//            byte var11;
//            for (var11 = 0; var11 < var10; ++var11) {
//                Mob var3 = new Mob(var11, var1.reader().readBoolean(), var1.reader().readBoolean(), var1.reader().readBoolean(), var1.reader().readBoolean(), var1.reader().readBoolean(), var1.reader().readShort(), var1.reader().readByte(), var1.reader().readInt(), var1.reader().readUnsignedByte(), var1.reader().readInt(), var1.reader().readShort(), var1.reader().readShort(), var1.reader().readByte(), var1.reader().readByte(), var1.reader().readBoolean(), false);
//                if (Mob.arrMobTemplate[var3.templateId].type != 0) {
//                    if (var11 % 3 == 0) {
//                        var3.dir = -1;
//                    } else {
//                        var3.dir = 1;
//                    }
//
//                    var3.x += 10 - var11 % 20;
//                }
//
//                GameScr.vMob.addElement(var3);
//            }

            var10 = var1.reader().readByte();

            for (int var11 = 0; var11 < var10; ++var11) {
                GameScr.vBuNhin.addElement(new BuNhin(var1.reader().readUTF(), var1.reader().readShort(), var1.reader().readShort()));
            }

            var10 = var1.reader().readByte();

            for (var2 = 0; var2 < var10; ++var2) {
                GameScr.vNpc.addElement(new Npc(var2, var1.reader().readByte(), var1.reader().readShort(), var1.reader().readShort(), var1.reader().readByte()));
            }

            var10 = var1.reader().readByte();

            int var5;
            for (var2 = 0; var2 < var10; ++var2) {
                ItemMap var12 = new ItemMap(var1.reader().readShort(), var1.reader().readShort(), var1.reader().readShort(), var1.reader().readShort());
                boolean var4 = false;

                for (var5 = 0; var5 < GameScr.vItemMap.size(); ++var5) {
                    if (((ItemMap) GameScr.vItemMap.elementAt(var5)).itemMapID == var12.itemMapID) {
                        var4 = true;
                        break;
                    }
                }

                if (!var4) {
                    GameScr.vItemMap.addElement(var12);
                }
            }

            GameScr.gameAA(false);

            try {
                TileMap.mapName1 = null;
                TileMap.mapName = TileMap.mapName1 = var1.reader().readUTF();
            } catch (Exception var7) {
            }

            try {
                TileMap.locationStand.clear();
                var2 = var1.reader().readUnsignedByte();

                for (int var13 = 0; var13 < var2; ++var13) {
                    int var14 = var1.reader().readUnsignedByte();
                    var5 = var1.reader().readUnsignedByte();
                    String var6 = String.valueOf((short) (var5 * TileMap.tmw + var14));
                    TileMap.locationStand.gameAA(var6, "location");
                }
            } catch (Exception var8) {
                var8.printStackTrace();
            }

            TileMap.gameAA(TileMap.tileID);
            Char.getMyChar().cvx = 0;
            Char.getMyChar().statusMe = 4;
            GameScr.gI().gameAP();
            GameCanvas.setMaxTextLenght(TileMap.bgID);
            Char.isLockKey = false;
            Char.ischangingMap = false;
            GameCanvas.gameAI();
            GameCanvas.gameAH();

            if (!TileMap.fieldBE || TileMap.fieldBD == TileMap.mapID) {
                GameScr.gI().update();
                AccountAutoManager.onGameReady();
                InfoDlg.gameAB();
                InfoDlg.gameAA(TileMap.mapName, mResources.gameRC + " " + TileMap.zoneID, 30);
                Party.gameAA();
                GameCanvas.gameAJ();
            }
            Session_ME.fieldAE();

            GameCanvas.isLoading = false;
        } catch (Exception var9) {
        }
        TileMap.fieldAG();

    }

    private void gameAE(Message var1) {
        try {
            try {
                Char var2;
                String var7;
                int var33;
                String var34;
                byte var36;
                int var40;
                int var41;
                short var42;
                short var43;
                byte[] var56;
                switch (var1.reader().readByte()) {
                    case -127:
                    case -125:
                    case -124:
                    case -118:
                    case -110:
                    case -107:
                    case -105:
                    case -104:
                    case -103:
                    case -102:
                    case -101:
                    case -100:
                    case -94:
                    case -92:
                    case -91:
                    case -89:
                    case -87:
                    case -85:
                    case -82:
                    case -79:
                    case -78:
                    case -76:
                    case -75:
                    case -74:
                    case -73:
                    case -71:
                    case -69:
                    case -68:
                    case -65:
                    case -64:
                    case -63:
                    case -61:
                    case -60:
                    default:
                        return;
                    case -126:
                        var36 = var1.reader().readByte();
                        LoginScr.isLoggingIn = false;
                        SelectCharScr var49;
                        (var49 = SelectCharScr.gameAF()).name = new String[3];
                        var49.parthead = new int[3];
                        var49.partleg = new int[3];
                        var49.partbody = new int[3];
                        var49.partWp = new int[3];
                        var49.level = new int[3];
                        var49.phai = new String[3];
                        var49.gender = new byte[3];
                        if (GameCanvas.isTouch) {
                            var49.indexSelect = -1;
                        } else {
                            var49.indexSelect = 0;
                        }

                        GameScr.gI();
                        GameScr.gameAJ();
                        SmallImage.gameAD();

                        for (byte var57 = 0; var57 < var36; ++var57) {
                            SelectCharScr.gameAF().gender[var57] = var1.reader().readByte();
                            SelectCharScr.gameAF().name[var57] = var1.reader().readUTF();
                            SelectCharScr.gameAF().phai[var57] = var1.reader().readUTF();
                            SelectCharScr.gameAF().level[var57] = var1.reader().readUnsignedByte();
                            SelectCharScr.gameAF().parthead[var57] = var1.reader().readShort();
                            SelectCharScr.gameAF().partWp[var57] = var1.reader().readShort();
                            SelectCharScr.gameAF().partbody[var57] = var1.reader().readShort();
                            SelectCharScr.gameAF().partleg[var57] = var1.reader().readShort();
                            if (SelectCharScr.gameAF().partWp[var57] == -1) {
                                SelectCharScr.gameAF().partWp[var57] = 15;
                            }

                            if (SelectCharScr.gameAF().partbody[var57] == -1) {
                                if (SelectCharScr.gameAF().gender[var57] == 0) {
                                    SelectCharScr.gameAF().partbody[var57] = 10;
                                } else {
                                    SelectCharScr.gameAF().partbody[var57] = 1;
                                }
                            }

                            if (SelectCharScr.gameAF().partleg[var57] == -1) {
                                if (SelectCharScr.gameAF().gender[var57] == 0) {
                                    SelectCharScr.gameAF().partleg[var57] = 9;
                                } else {
                                    SelectCharScr.gameAF().partleg[var57] = 0;
                                }
                            }
                        }

                        AccountAutoManager.onCharacterList(var49.name);
                        SelectCharScr.gameAF().update();
                        GameCanvas.gameAJ();
                        Session_ME.fieldAE();

                        return;
                    case -123:
                        System.out.println("AUTO LOGIN TRACE: nhận response -123, bắt đầu đồng bộ dữ liệu");
                        GameScr.vsData = var1.reader().readByte();
                        GameScr.vsMap = var1.reader().readByte();
                        GameScr.vsSkill = var1.reader().readByte();
                        GameScr.vsItem = var1.reader().readByte();
                        System.out.println("****** DATA VERSION: Server " + GameScr.vsData + " Client " + GameScr.vcData);
                        System.out.println("****** MAP VERSION: Server " + GameScr.vsMap + " Client " + GameScr.vcMap);
                        System.out.println("****** SKILL VERSION: Server " + GameScr.vsSkill + " Client " + GameScr.vcSkill);
                        System.out.println("****** ITEM VERSION: Server " + GameScr.vsItem + " Client " + GameScr.vcItem);
                        if (GameScr.vsData != GameScr.vcData) {
                            System.out.println("AUTO LOGIN TRACE: yêu cầu update DATA");
                            Service.gI().updateData();
                        } else {
                            try {
                                gameAD(new DataInputStream(new ByteArrayInputStream(RMS.gameAA("data"))));
                                System.out.println("AUTO LOGIN TRACE: load DATA RMS xong");
                                if (GameScr.vsData != GameScr.vcData) {
                                    System.out.println("AUTO LOGIN TRACE: DATA RMS stale, yêu cầu update "
                                            + GameScr.vcData + " -> " + GameScr.vsData);
                                    Service.gI().updateData();
                                }
                            } catch (Exception var28) {
                                System.out.println("AUTO LOGIN TRACE: DATA RMS lỗi: " + var28.toString());
                                GameScr.vcData = -1;
                                Service.gI().updateData();
                            }
                        }

                        if (GameScr.vsMap != GameScr.vcMap) {
                            Service.gI().updateMap();
                        } else {
                            try {
                                gameAC(new DataInputStream(new ByteArrayInputStream(RMS.gameAA("map"))));
                                System.out.println("AUTO LOGIN TRACE: load MAP RMS xong");
                                if (GameScr.vsMap != GameScr.vcMap) {
                                    System.out.println("AUTO LOGIN TRACE: MAP RMS stale, yêu cầu update "
                                            + GameScr.vcMap + " -> " + GameScr.vsMap);
                                    Service.gI().updateMap();
                                }
                            } catch (Exception var27) {
                                System.out.println("AUTO LOGIN TRACE: MAP RMS lỗi: " + var27.toString());
                                GameScr.vcMap = -1;
                                Service.gI().updateMap();
                            }
                        }

                        if (GameScr.vsSkill != GameScr.vcSkill) {
                            Service.gI().updateSkill();
                        } else {
                            try {
                                gameAB(new DataInputStream(new ByteArrayInputStream(RMS.gameAA("skill"))));
                                System.out.println("AUTO LOGIN TRACE: load SKILL RMS xong");
                                if (GameScr.vsSkill != GameScr.vcSkill) {
                                    System.out.println("AUTO LOGIN TRACE: SKILL RMS stale, yêu cầu update "
                                            + GameScr.vcSkill + " -> " + GameScr.vsSkill);
                                    Service.gI().updateSkill();
                                }
                            } catch (Exception var26) {
                                System.out.println("AUTO LOGIN TRACE: SKILL RMS lỗi: " + var26.toString());
                                GameScr.vcSkill = -1;
                                Service.gI().updateSkill();
                            }
                        }

                        if (GameScr.vsItem != GameScr.vcItem) {
                            Service.gI().updateItem();
                        } else {
                            try {
                                gameAA(new DataInputStream(new ByteArrayInputStream(RMS.gameAA("item"))));
                                System.out.println("AUTO LOGIN TRACE: load ITEM RMS xong");
                                if (GameScr.vsItem != GameScr.vcItem) {
                                    System.out.println("AUTO LOGIN TRACE: ITEM RMS stale, yêu cầu update "
                                            + GameScr.vcItem + " -> " + GameScr.vsItem);
                                    Service.gI().updateItem();
                                }
                            } catch (Exception var25) {
                                System.out.println("AUTO LOGIN TRACE: ITEM RMS lỗi: " + var25.toString());
                                GameScr.vcItem = -1;
                                Service.gI().updateItem();
                            }
                        }

                        System.out.println("AUTO LOGIN TRACE: version sau parse server="
                                + GameScr.vsData + "/" + GameScr.vsMap + "/" + GameScr.vsSkill + "/" + GameScr.vsItem
                                + " client=" + GameScr.vcData + "/" + GameScr.vcMap + "/" + GameScr.vcSkill + "/" + GameScr.vcItem);
                        if (GameScr.vsData == GameScr.vcData && GameScr.vsMap == GameScr.vcMap && GameScr.vsSkill == GameScr.vcSkill && GameScr.vsItem == GameScr.vcItem) {
                            System.out.println("AUTO LOGIN TRACE: version khớp, tạo paint data");
                            GameScr.gI();
                            GameScr.gameAK();
                            GameScr.gI();
                            GameScr.gameAL();
                            GameScr.gI();
                            GameScr.gameAM();
                            System.out.println("AUTO LOGIN TRACE: gửi clientOk");
                            Service.gI().clientOk();
                            System.out.println("AUTO LOGIN TRACE: đã enqueue clientOk");
                        }

                        System.out.println("AUTO LOGIN TRACE: đọc CharPartInfo, bytes còn lại=" + var1.reader().available());
                        CharPartInfo.gameAA(var1);
                        System.out.println("AUTO LOGIN TRACE: xử lý -123 hoàn tất");
                        return;
                    case -122:
                        System.out.println("GET UPDATE_DATA " + var1.reader().available() + " bytes");
                        var1.reader().mark(100000);
                        gameAD(var1.reader());
                        var1.reader().reset();
                        byte[] var55 = new byte[var1.reader().available()];
                        var1.reader().readFully(var55);
                        RMS.gameAA("data", var55);
                        byte[] var53 = new byte[]{GameScr.vcData};
                        RMS.gameAA("dataVersion", var53);
                        if (GameScr.vsData != GameScr.vcData || GameScr.vsMap != GameScr.vcMap || GameScr.vsSkill != GameScr.vcSkill || GameScr.vsItem != GameScr.vcItem) {
                            return;
                        }

                        GameScr.gI();
                        GameScr.gameAK();
                        GameScr.gI();
                        GameScr.gameAL();
                        GameScr.gI();
                        GameScr.gameAM();
                        Service.gI().clientOk();
                        return;
                    case -121:
                        System.out.println("GET UPDATE_MAP " + var1.reader().available() + " bytes");
                        var1.reader().mark(100000);
                        gameAC(var1.reader());
                        var1.reader().reset();
                        var56 = new byte[var1.reader().available()];
                        var1.reader().readFully(var56);
                        RMS.gameAA("map", var56);
                        byte[] var58 = new byte[]{GameScr.vcMap};
                        RMS.gameAA("mapVersion", var58);
                        if (GameScr.vsData != GameScr.vcData || GameScr.vsMap != GameScr.vcMap || GameScr.vsSkill != GameScr.vcSkill || GameScr.vsItem != GameScr.vcItem) {
                            return;
                        }

                        GameScr.gI();
                        GameScr.gameAK();
                        GameScr.gI();
                        GameScr.gameAL();
                        GameScr.gI();
                        GameScr.gameAM();
                        Service.gI().clientOk();
                        return;
                    case -120:
                        System.out.println("GET UPDATE_SKILL " + var1.reader().available() + " bytes");
                        var1.reader().mark(100000);
                        gameAB(var1.reader());
                        var1.reader().reset();
                        byte[] var59 = new byte[var1.reader().available()];
                        var1.reader().readFully(var59);
                        if (Char.getMyChar().gameBA()) {
                            RMS.gameAA("skill", var59);
                        } else {
                            RMS.gameAA("skillnhanban", var59);
                        }

                        byte[] var61 = new byte[]{GameScr.vcSkill};
                        RMS.gameAA("skillVersion", var61);
                        if (GameScr.vsData != GameScr.vcData || GameScr.vsMap != GameScr.vcMap || GameScr.vsSkill != GameScr.vcSkill || GameScr.vsItem != GameScr.vcItem) {
                            return;
                        }

                        GameScr.gI();
                        GameScr.gameAK();
                        GameScr.gI();
                        GameScr.gameAL();
                        GameScr.gI();
                        GameScr.gameAM();
                        Service.gI().clientOk();
                        return;
                    case -119:
                        System.out.println("GET UPDATE_ITEM " + var1.reader().available() + " bytes");
                        var1.reader().mark(100000);
                        gameAA(var1.reader());
                        var1.reader().reset();
                        var56 = new byte[var1.reader().available()];
                        var1.reader().readFully(var56);
                        RMS.gameAA("item", var56);
                        byte[] var47 = new byte[]{GameScr.vcItem};
                        RMS.gameAA("itemVersion", var47);
                        if (GameScr.vsData != GameScr.vcData || GameScr.vsMap != GameScr.vcMap || GameScr.vsSkill != GameScr.vcSkill || GameScr.vsItem != GameScr.vcItem) {
                            return;
                        }

                        GameScr.gI();
                        GameScr.gameAK();
                        GameScr.gI();
                        GameScr.gameAL();
                        GameScr.gI();
                        GameScr.gameAM();
                        Service.gI().clientOk();
                        return;
                    case -117:
                        Char.getMyChar().cPk = var1.reader().readByte();
                        Info.gameAA(mResources.gameMF + " " + Char.getMyChar().cPk, 15, mFont.tahoma_7_yellow);
                        Char.getMyChar().gameAC(21);
                        return;
                    case -116:
                        Char.getMyChar().xu = var1.reader().readInt();
                        Char.clan.coin = var1.reader().readInt();
                        return;
                    case -115:
                        var33 = var1.reader().readInt();
                        byte[] var52 = NinjaUtil.gameAB(var1);
                        SmallImage.gameAA(var33, var52);
                        return;
                    case -114:
                        if (Char.clan == null) {
                            Char.clan = new Clan();
                        }

                        Char.clan.gameAA(var1.reader().readUTF());
                        return;
                    case -113:
                        if (Char.clan == null) {
                            Char.clan = new Clan();
                        }

                        Char.clan.name = var1.reader().readUTF();
                        Char.clan.main_name = var1.reader().readUTF();
                        var1.reader().readUTF();
                        Char.clan.total = var1.reader().readShort();
                        Char.clan.openDun = var1.reader().readByte();
                        Char.clan.level = var1.reader().readByte();
                        Char.clan.exp = var1.reader().readInt();
                        Char.clan.expNext = var1.reader().readInt();
                        Char.clan.coin = var1.reader().readInt();
                        Char.clan.freeCoin = var1.reader().readInt();
                        Char.clan.coinUp = var1.reader().readInt();
                        Char.clan.reg_date = var1.reader().readUTF();
                        Char.clan.alert = var1.reader().readUTF();
                        Char.clan.use_card = var1.reader().readInt();
                        Char.clan.itemLevel = var1.reader().readByte();
                        return;
                    case -112:
                        GameScr.vClan.removeAllElements();
                        var42 = var1.reader().readShort();

                        for (var41 = 0; var41 < var42; ++var41) {
                            GameScr.vClan.addElement(new Member(var1.reader().readByte(), var1.reader().readByte(), var1.reader().readByte(), var1.reader().readUTF(), var1.reader().readInt(), var1.reader().readBoolean()));
                        }

                        try {
                            for (var41 = 0; var41 < var42; ++var41) {
                                ((Member) GameScr.vClan.elementAt(var41)).pointClanWeek = var1.reader().readInt();
                            }
                        } catch (Exception var30) {
                        }

                        GameScr.gI();
                        GameScr.gameAY();
                        return;
                    case -111:
                        Char.clan.items = new Item[30];
                        var36 = var1.reader().readByte();

                        for (var33 = 0; var33 < var36; ++var33) {
                            Char.clan.items[var33] = new Item();
                            Char.clan.items[var33].typeUI = 39;
                            Char.clan.items[var33].indexUI = var33;
                            Char.clan.items[var33].quantity = var1.reader().readShort();
                            Char.clan.items[var33].template = ItemTemplates.gameAA(var1.reader().readShort());
                        }

                        GameScr.gI().gameBO();
                        byte var44 = var1.reader().readByte();

                        for (var41 = 0; var41 < var44; ++var41) {
                            String var50 = var1.reader().readUTF();
                            var43 = var1.reader().readShort();
                            short var45 = var1.reader().readShort();
                            int var51 = var1.reader().readInt();
                            var7 = "";
                            MyVector var8 = new MyVector();
                            int var9 = -1;
                            int var10 = -1;
                            byte var11 = var1.reader().readByte();
                            if (var51 >= 0) {
                                var7 = var1.reader().readUTF();
                            } else {
                                for (int var12 = 0; var12 < var11; ++var12) {
                                    String var54 = var1.reader().readUTF();
                                    var8.addElement(var54);
                                }

                                var9 = var1.reader().readInt();
                                var10 = var1.reader().readInt();
                            }

                            byte var60 = var1.reader().readByte();
                            GameScr.gI().gameAA(new Clan_ThanThu(var50, var60, var43, var45, var51, var7, var8, var9, var10));
                        }

                        return;
                    case -109:
                        try {
                            GameCanvas.isLoading = true;
                            TileMap.maps = null;
                            TileMap.types = null;
                            System.gc();
                            TileMap.fieldAA(TileMap.mapID, var1.reader());
                            TileMap.gameAE();
                            gameAD(this.messWait);
                        } catch (final Exception ex6) {
                            ex6.printStackTrace();
                        }
//                        try {
//                            boolean var48 = false;
//                            GameCanvas.isLoading = true;
//                            TileMap.maps = null;
//                            TileMap.types = null;
//                            System.gc();
//                            TileMap.tmw = var1.reader().readByte();
//                            TileMap.tmh = var1.reader().readByte();
//                            TileMap.maps = new char[TileMap.tmw * TileMap.tmh];
//
//                            for (var41 = 0; var41 < TileMap.maps.length; ++var41) {
//                                if ((var40 = var1.reader().readByte()) < 0) {
//                                    var40 += 256;
//                                }
//
//                                TileMap.maps[var41] = (char) var40;
//                            }
//
//                            TileMap.types = new int[TileMap.maps.length];
//                            var1 = this.messWait;
//                            this.gameAD(var1);
//                        } catch (Exception var29) {
//                            var29.printStackTrace();
//                        }

                        var1.cleanup();
                        this.messWait.cleanup();
                        var1 = this.messWait = null;
                        return;
                    case -108:
                        var42 = var1.reader().readShort();

                        try {
                            var36 = var1.reader().readByte();
                            Mob.arrMobTemplate[var42].typeFly = var36;
                        } catch (Exception var24) {
                        }

                        var36 = var1.reader().readByte();
                        Mob.arrMobTemplate[var42].imgs = new Image[var36];
                        if (var42 != 98 && var42 != 99) {
                            for (var41 = 0; var41 < Mob.arrMobTemplate[var42].imgs.length; ++var41) {
                                Mob.arrMobTemplate[var42].imgs[var41] = gameAA(NinjaUtil.gameAB(var1));
                            }
                        } else {
                            Mob.arrMobTemplate[var42].imgs = new Image[3];
                            Image var39 = gameAA(NinjaUtil.gameAB(var1));

                            for (var40 = 0; var40 < Mob.arrMobTemplate[var42].imgs.length; ++var40) {
                                Mob.arrMobTemplate[var42].imgs[var40] = var39;
                            }
                        }

                        if (var1.reader().readBoolean()) {
                            var36 = var1.reader().readByte();
                            Mob.arrMobTemplate[var42].frameBossMove = new byte[var36];

                            for (var40 = 0; var40 < var36; ++var40) {
                                Mob.arrMobTemplate[var42].frameBossMove[var40] = var1.reader().readByte();
                            }

                            var36 = var1.reader().readByte();
                            Mob.arrMobTemplate[var42].frameBossAttack = new byte[var36][];

                            for (var40 = 0; var40 < var36; ++var40) {
                                Mob.arrMobTemplate[var42].frameBossAttack[var40] = new byte[var1.reader().readByte()];

                                for (int var46 = 0; var46 < Mob.arrMobTemplate[var42].frameBossAttack[var40].length; ++var46) {
                                    Mob.arrMobTemplate[var42].frameBossAttack[var40][var46] = var1.reader().readByte();
                                }
                            }
                        }

                        if (var1.reader().readInt() > 0) {
                            if (var42 < 236) {
                                gameAA(var1, var42);
                            } else {
                                gameAB(var1, var42);
                            }

                            return;
                        }

                        return;
                    case -106:
                        GameScr.typeActive = var1.reader().readByte();
                        return;
                    case -99:
                        GameCanvas.input2Dlg.gameAA(mResources.gameBO, mResources.gameBP);
                        var34 = var1.reader().readUTF();
                        GameCanvas.input2Dlg.gameAA(var34, new Command(mResources.gameBH, GameCanvas.instance, 8882, (Object) null), new Command(mResources.gameBT, GameCanvas.instance, 88816, (Object) null), 0, 1);
                        return;
                    case -98:
                        Char.getMyChar();
                        Char.gameAY();
                        return;
                    case -97:
                        GameCanvas.isLoading = false;
                        GameCanvas.gameAJ();
                        Integer var38 = new Integer(var1.reader().readInt());
                        GameCanvas.inputDlg.gameAA(mResources.gameEX, new Command(mResources.gameCX, GameCanvas.instance, 88829, var38), 0);
                        return;
                    case -96:
                        Char.getMyChar().cClanName = var1.reader().readUTF();
                        Char.getMyChar().ctypeClan = 4;
                        Char.getMyChar().luong = var1.reader().readInt();
                        Char.getMyChar().gameAC(21);
                        return;
                    case -95:
                        if (Char.clan != null) {
                            Char.clan.alert = var1.reader().readUTF();
                        }

                        return;
                    case -93:
                        if ((var33 = var1.reader().readInt()) == Char.getMyChar().charID) {
                            GameScr.vClan.removeAllElements();
                            Char.getMyChar().cClanName = "";
                            Char.getMyChar().ctypeClan = -1;
                            Char.clan = null;
                        } else {
                            GameScr.vClan.removeAllElements();
                            Char var37;
                            (var37 = GameScr.gameAE(var33)).cClanName = "";
                            var37.ctypeClan = -1;
                        }

                        return;
                    case -90:
                        Char.getMyChar().xu = var1.reader().readInt();
                        GameScr.gI().resetButton();
                        return;
                    case -88:
                        GameScr.gI().resetButton();
                        Item var35;
                        (var35 = Char.getMyChar().arrItemBag[var1.reader().readByte()]).clearExpire();
                        var35.isLock = true;
                        var35.upgrade = var1.reader().readByte();
                        (var35 = Char.getMyChar().arrItemBag[var1.reader().readByte()]).clearExpire();
                        var35.isLock = true;
                        var35.upgrade = var1.reader().readByte();
                        Info.gameAA(mResources.gameRA, 20, mFont.tahoma_7b_yellow);
                        return;
                    case -86:
                        GameCanvas.gameAJ();
                        GameScr.gI().resetButton();
                        InfoMe.gameAA(var1.reader().readUTF(), 20, mFont.tahoma_7_yellow);
                        return;
                    case -84:
                        Char.pointPB = var1.reader().readShort();
                        return;
                    case -83:
                        short var3 = var1.reader().readShort();
                        var43 = var1.reader().readShort();
                        byte var5 = var1.reader().readByte();
                        short var6 = var1.reader().readShort();
                        if (var3 == 0) {
                            GameScr.gI().gameAA(mResources.gameET, "          " + mResources.gameSO, false);
                        } else {
                            var7 = mResources.gameEI + ": " + var3 + "\n\n";
                            if (var43 == 0) {
                                var7 = var7 + mResources.gameEL + "\n\n";
                            } else {
                                var7 = var7 + mResources.gameEM + ": " + NinjaUtil.gameAB(var43) + "\n\n";
                            }

                            var7 = var7 + mResources.gameEN + ": " + var5 + "\n\n";
                            var7 = var7 + mResources.gameFF + ": " + var6 + " " + mResources.gameFG + "\n\n";
                            GameScr.gI().gameAA(mResources.gameET, var7, false);
                            if (var6 > 0) {
                                GameScr.gI().left = new Command(mResources.gameFF, 1000);
                                return;
                            }
                        }

                        return;
                    case -81:
                        Char.pointChienTruong = var1.reader().readShort();
                        return;
                    case -80:
                        GameScr.gI().gameAA(mResources.gameES, var1.reader().readUTF(), false);
                        if (var1.reader().readBoolean()) {
                            GameScr.gI().left = new Command(mResources.gameFF, 2000);
                            LockGame.fieldAZ();

                        }

                        return;
                    case -77:
                        GameCanvas.setMaxTextLenght(TileMap.bgID = var1.reader().readByte());
                        return;
                    case -72:
                        GameScr.gI().yenValue = new String[9];
                        GameScr.arrItemSprin = new short[9];
                        if (GameScr.indexSelect < 0 || GameScr.indexSelect > 8) {
                            GameScr.indexCard = 0;
                            GameScr.indexSelect = 0;
                        }

                        for (var33 = 0; var33 < 9; ++var33) {
                            GameScr.arrItemSprin[var33] = var1.reader().readShort();
                            GameScr.gI().yenValue[var33] = GameScr.gI().YenCards[NinjaUtil.gameAA(9)];
                        }

                        GameScr.gI().left = new Command(mResources.gameBY, (IActionListener) null, 1506, (Object) null);
                        GameScr.gI().timePoint = System.currentTimeMillis();
                        --GameScr.gI().numSprinLeft;
                        GameCanvas.gameAJ();
                        return;
                    case -70:
                        var34 = var1.reader().readUTF();
                        GameCanvas.gameAA(NinjaUtil.replace(mResources.gameVW, "#", var34), new Command(mResources.gameCH, GameCanvas.instance, 88842, (Object) null), new Command(mResources.gameCU, GameCanvas.instance, 8882, (Object) null));
                        return;
                    case -67:
                        Mob var4 = null;

                        try {
                            var4 = Mob.gameAA(var1.reader().readUnsignedByte());
                        } catch (Exception var23) {
                        }

                        if (var4 != null) {
                            if ((var33 = var1.reader().readInt()) == Char.getMyChar().charID) {
                                GameScr.vMobSoul.addElement(new MobSoul(var4.x, var4.y, Char.getMyChar()));
                            } else if ((var2 = GameScr.gameAE(var33)) != null) {
                                GameScr.vMobSoul.addElement(new MobSoul(var4.x, var4.y, var2));
                                return;
                            }

                            return;
                        }

                        return;
                    case -66:
                        var33 = var1.reader().readInt();
                        if (Char.getMyChar().charID == var33) {
                            GameScr.vMobSoul.addElement(new MobSoul(Char.getMyChar().cx, Char.getMyChar().cy));
                        } else if ((var2 = GameScr.gameAE(var33)) != null) {
                            GameScr.vMobSoul.addElement(new MobSoul(var2.cx, var2.cy));
                            return;
                        }

                        return;
                    case -62:
                        Char.clan.itemLevel = var1.reader().readByte();
                        return;

                }
            } catch (Exception var31) {
            }

        } finally {
            if (var1 != null) {
                var1.cleanup();
            }

        }
    }

    private static void gameAF(Message var0) {
        try {
            switch (var0.reader().readByte()) {
                case -124:
                    System.out.println("SEND SMS");
                    String var1 = var0.reader().readUTF();
                    SendSMS.gameAA(var0.reader().readUTF(), "sms://" + var1, new Command("", GameCanvas.gameAA(), 88825, (Object) null), new Command("", GameCanvas.gameAA(), 88826, (Object) null));
                    break;
                case 2:
                    RMS.gameAA();
            }
        } catch (Exception var5) {
        } finally {
            if (var0 != null) {
                var0.cleanup();
            }

        }

    }

    private void gameAG(Message var1) {
        try {
            byte var42 = var1.reader().readByte();
            boolean var2 = false;
            short var64;
            String var65;
            Skill var3;
            EffectTemplate var67;
            byte var4;
            Service var73;
            GameScr var74;
            Char var75;
            byte var10000;
            int var43;
            byte var44;
            short var45;
            short[] var46;
            ItemTemplate var47;
            Char var50;
            int var51;
            short var52;
            int var53;
            int var54;
            short var56;
            byte var58;
            Integer var59;
            Effect var60;
            Effect var63;
            switch (var42) {
                case -128:
                    if ((var50 = GameScr.gameAE(var1.reader().readInt())) == null) {
                        return;
                    }

                    var50.cHP = var1.reader().readInt();
                    var50.cMaxHP = var1.reader().readInt();
                    var50.clevel = var1.reader().readUnsignedByte();
                    break;
                case -127:
                    GameScr.vCharInMap.removeAllElements();
                    GameScr.vItemMap.removeAllElements();
                    GameScr.gameBD();
                    GameScr.currentCharViewInfo = Char.getMyChar();
                    Char.getMyChar().charID = var1.reader().readInt();
                    Char.getMyChar().cClanName = var1.reader().readUTF();
                    if (!Char.getMyChar().cClanName.equals("")) {
                        Char.getMyChar().ctypeClan = var1.reader().readByte();
                    }

                    Char.getMyChar().ctaskId = var1.reader().readByte();
                    Char.getMyChar().cgender = var1.reader().readByte();
                    Char.getMyChar().head = var1.reader().readShort();
                    Char.getMyChar().cspeed = var1.reader().readByte();
                    Char.getMyChar().cName = var1.reader().readUTF();
                    Char.getMyChar().cPk = var1.reader().readByte();
                    Char.getMyChar().cTypePk = var1.reader().readByte();
                    Char.getMyChar().cMaxHP = var1.reader().readInt();
                    Char.getMyChar().cHP = var1.reader().readInt();
                    Char.getMyChar().cMaxMP = var1.reader().readInt();
                    Char.getMyChar().cMP = var1.reader().readInt();
                    Char.getMyChar().cEXP = var1.reader().readLong();
                    Char.getMyChar().cExpDown = var1.reader().readLong();
                    GameScr.gameAA(Char.getMyChar().cEXP, true);
                    Char.getMyChar().eff5BuffHp = var1.reader().readShort();
                    Char.getMyChar().eff5BuffMp = var1.reader().readShort();
                    Char.getMyChar().nClass = GameScr.nClasss[var1.reader().readByte()];
                    Char.getMyChar().pPoint = var1.reader().readShort();
                    Char.getMyChar().potential[0] = var1.reader().readShort();
                    Char.getMyChar().potential[1] = var1.reader().readShort();
                    Char.getMyChar().potential[2] = var1.reader().readInt();
                    Char.getMyChar().potential[3] = var1.reader().readInt();
                    Char.getMyChar().sPoint = var1.reader().readShort();
                    Char.getMyChar().vSkill.removeAllElements();
                    Char.getMyChar().vSkillFight.removeAllElements();
                    var44 = var1.reader().readByte();

                    for (var4 = 0; var4 < var44; ++var4) {
                        var3 = Skills.gameAA(var1.reader().readShort());
                        if (Char.getMyChar().myskill == null) {
                            Char.getMyChar().myskill = var3;
                        }
                        if (Code.fieldAB != null && Auto.fieldAL != null && var3.template.id == Auto.fieldAL.template.id) {
                            Auto.fieldAL = var3;
                        }
                        Char.getMyChar().vSkill.addElement(var3);
                        if ((var3.template.type == 1 || var3.template.type == 4 || var3.template.type == 2 || var3.template.type == 3) && (var3.template.maxPoint == 0 || var3.template.maxPoint > 0 && var3.point > 0)) {
                            var10000 = var3.template.id;
                            Char.getMyChar();
                            if (var10000 == 0) {
                                var73 = Service.gI();
                                Char.getMyChar();
                                var73.selectSkill(0);
                            }

                            Char.getMyChar().vSkillFight.addElement(var3);
                        }
                    }

                    GameScr.gI();
                    GameScr.gameAZ();
                    Char.getMyChar().xu = var1.reader().readInt();
                    Char.getMyChar().yen = var1.reader().readInt();
                    Char.getMyChar().luong = var1.reader().readInt();
                    Char.getMyChar().arrItemBag = new Item[var1.reader().readUnsignedByte()];
                    GameScr.mpPotion = 0;
                    GameScr.hpPotion = 0;

                    for (var54 = 0; var54 < Char.getMyChar().arrItemBag.length; ++var54) {
                        if ((var64 = var1.reader().readShort()) != -1) {
                            Char.getMyChar().arrItemBag[var54] = new Item();
                            Char.getMyChar().arrItemBag[var54].typeUI = 3;
                            Char.getMyChar().arrItemBag[var54].indexUI = var54;
                            Char.getMyChar().arrItemBag[var54].template = ItemTemplates.gameAA(var64);
                            Char.getMyChar().arrItemBag[var54].isLock = var1.reader().readBoolean();
                            if (Char.getMyChar().arrItemBag[var54].isTypeBody() || Char.getMyChar().arrItemBag[var54].isTypeMounts() || Char.getMyChar().arrItemBag[var54].isTypeNgocKham()) {
                                Char.getMyChar().arrItemBag[var54].upgrade = var1.reader().readByte();
                            }

                            Char.getMyChar().arrItemBag[var54].isExpires = var1.reader().readBoolean();
                            Char.getMyChar().arrItemBag[var54].quantity = var1.reader().readUnsignedShort();
                            if (Char.getMyChar().arrItemBag[var54].template.type == 16) {
                                GameScr.hpPotion += Char.getMyChar().arrItemBag[var54].quantity;
                            }

                            if (Char.getMyChar().arrItemBag[var54].template.type == 17) {
                                GameScr.mpPotion += Char.getMyChar().arrItemBag[var54].quantity;
                            }

                            if (Char.getMyChar().arrItemBag[var54].template.id == 340) {
                                var74 = GameScr.gI();
                                var74.numSprinLeft += Char.getMyChar().arrItemBag[var54].quantity;
                            }
                        }
                    }
                    Code.fieldAL();

                    Char.getMyChar().arrItemBody = new Item[32];

                    try {
                        Char.getMyChar().gameAJ();

                        for (var54 = 0; var54 < 16; ++var54) {
                            if ((var64 = var1.reader().readShort()) != -1) {
                                ItemTemplate var71;
                                var42 = (var71 = ItemTemplates.gameAA(var64)).type;
                                Char.getMyChar().arrItemBody[var42] = new Item();
                                Char.getMyChar().arrItemBody[var42].indexUI = var42;
                                Char.getMyChar().arrItemBody[var42].typeUI = 5;
                                Char.getMyChar().arrItemBody[var42].template = var71;
                                Char.getMyChar().arrItemBody[var42].isLock = true;
                                Char.getMyChar().arrItemBody[var42].upgrade = var1.reader().readByte();
                                Char.getMyChar().arrItemBody[var42].sys = var1.reader().readByte();
                                if (var42 == 1) {
                                    Char.getMyChar().wp = Char.getMyChar().arrItemBody[var42].template.part;
                                } else if (var42 == 2) {
                                    Char.getMyChar().body = Char.getMyChar().arrItemBody[var42].template.part;
                                } else if (var42 == 6) {
                                    Char.getMyChar().leg = Char.getMyChar().arrItemBody[var42].template.part;
                                }
                            }
                        }
                    } catch (Exception var30) {
                        var30.printStackTrace();
                    }

                    Char.getMyChar().isHuman = var1.reader().readBoolean();
                    Char.getMyChar().isNhanban = var1.reader().readBoolean();
                    short[] var70;
                    if ((var70 = new short[]{var1.reader().readShort(), var1.reader().readShort(), var1.reader().readShort(), var1.reader().readShort()})[0] > -1) {
                        Char.getMyChar().head = var70[0];
                    }

                    if (var70[1] > -1) {
                        Char.getMyChar().wp = var70[1];
                    }

                    if (var70[2] > -1) {
                        Char.getMyChar().body = var70[2];
                    }

                    if (var70[3] > -1) {
                        Char.getMyChar().leg = var70[3];
                    }

                    var46 = new short[10];

                    try {
                        for (var53 = 0; var53 < 10; ++var53) {
                            var46[var53] = var1.reader().readShort();
                        }
                    } catch (Exception var29) {
                        var46 = null;
                    }

                    if (var46 != null) {
                        Char.getMyChar().gameAA(var46);
                    }

                    try {
                        for (var53 = 0; var53 < 16; ++var53) {
                            if ((var64 = var1.reader().readShort()) != -1) {
                                var51 = (var47 = ItemTemplates.gameAA(var64)).type + 16;
                                Char.getMyChar().arrItemBody[var51] = new Item();
                                Char.getMyChar().arrItemBody[var51].indexUI = var51;
                                Char.getMyChar().arrItemBody[var51].typeUI = 5;
                                Char.getMyChar().arrItemBody[var51].template = var47;
                                Char.getMyChar().arrItemBody[var51].isLock = true;
                                Char.getMyChar().arrItemBody[var51].upgrade = var1.reader().readByte();
                                Char.getMyChar().arrItemBody[var51].sys = var1.reader().readByte();
                                if (var51 == 1) {
                                    Char.getMyChar().wp = Char.getMyChar().arrItemBody[var51].template.part;
                                } else if (var51 == 2) {
                                    Char.getMyChar().body = Char.getMyChar().arrItemBody[var51].template.part;
                                } else if (var51 == 6) {
                                    Char.getMyChar().leg = Char.getMyChar().arrItemBody[var51].template.part;
                                }
                            }
                        }
                    } catch (Exception var28) {
                        var28.printStackTrace();
                    }

                    var2 = false;

                    try {
                        var45 = var1.reader().readShort();
                    } catch (Exception var26) {
                        var45 = -1;
                    }

                    Char.getMyChar().ID_SUSANO = var45;
                    if (Char.getMyChar().isHuman) {
                        GameScr.gI();
                        GameScr.gameAG();
                    } else if (Char.getMyChar().isNhanban) {
                        GameScr.gI();
                        GameScr.gameAH();
                    }

                    Char.getMyChar().statusMe = 4;
                    GameScr.isViewClanInvite = RMS.gameAC(Char.getMyChar().cName + "vci") >= 1;
                    if (Char.getMyChar().gameBA()) {
                        gameAB(new DataInputStream(new ByteArrayInputStream(RMS.gameAA("skill"))));
                    } else {
                        gameAB(new DataInputStream(new ByteArrayInputStream(RMS.gameAA("skillnhanban"))));
                    }

                    Service.gI().loadRMS("KSkill");
                    Service.gI().loadRMS("OSkill");
                    Service.gI().loadRMS("CSkill");
                    break;
                case -126:
                    Char.getMyChar().gameAA(var1);
                    Char.getMyChar().potential[0] = var1.reader().readShort();
                    Char.getMyChar().potential[1] = var1.reader().readShort();
                    Char.getMyChar().potential[2] = var1.reader().readInt();
                    Char.getMyChar().potential[3] = var1.reader().readInt();
                    Char.getMyChar().gameAC(61);
                    Char.getMyChar().nClass = GameScr.nClasss[var1.reader().readByte()];
                    Char.getMyChar().sPoint = var1.reader().readShort();
                    Char.getMyChar().pPoint = var1.reader().readShort();
                    Char.getMyChar().vSkill.removeAllElements();
                    Char.getMyChar().vSkillFight.removeAllElements();
                    Char.getMyChar().myskill = null;
                    break;
                case -125:
                    Char.getMyChar().gameAA(var1);
                    if (Char.getMyChar().statusMe != 14 && Char.getMyChar().statusMe != 5) {
                        Char.getMyChar().cHP = Char.getMyChar().cMaxHP;
                        Char.getMyChar().cMP = Char.getMyChar().cMaxMP;
                    }

                    try {
                        Char.getMyChar().sPoint = var1.reader().readShort();
                        Char.getMyChar().vSkill.removeAllElements();
                        Char.getMyChar().vSkillFight.removeAllElements();
                        var44 = var1.reader().readByte();

                        for (var42 = 0; var42 < var44; ++var42) {
                            var3 = Skills.gameAA(var1.reader().readShort());
                            if (Char.getMyChar().myskill == null) {
                                Char.getMyChar().myskill = var3;
                            } else if (var3.template.equals(Char.getMyChar().myskill.template)) {
                                Char.getMyChar().myskill = var3;
                            }
                            if (Code.fieldAB != null && Auto.fieldAL != null && var3.template.id == Auto.fieldAL.template.id) {
                                Auto.fieldAL = var3;
                            }
                            Char.getMyChar().vSkill.addElement(var3);
                            if ((var3.template.type == 1 || var3.template.type == 4 || var3.template.type == 2 || var3.template.type == 3) && (var3.template.maxPoint == 0 || var3.template.maxPoint > 0 && var3.point > 0)) {
                                var10000 = var3.template.id;
                                Char.getMyChar();
                                if (var10000 == 0) {
                                    var73 = Service.gI();
                                    Char.getMyChar();
                                    var73.selectSkill(0);
                                }

                                Char.getMyChar().vSkillFight.addElement(var3);
                            }
                        }

                        GameScr.gI();
                        GameScr.gameAZ();
                        if (GameScr.isPaintInfoMe) {
                            GameScr.indexRow = -1;
                            GameScr.gI().gameBJ();
                        }
                        System.out.println("LOAD XONG ME LOAD SKILL " + Char.getMyChar().vSkill.size());

                    } catch (Exception var34) {
                        var34.printStackTrace();
                    }
                    LockGame.fieldAV();

                    break;
                case -124:
                    Char.getMyChar().gameAA(var1);
                    Char.getMyChar().cEXP = var1.reader().readLong();
                    GameScr.gameAA(Char.getMyChar().cEXP, true);
                    Char.getMyChar().sPoint = var1.reader().readShort();
                    Char.getMyChar().pPoint = var1.reader().readShort();
                    Char.getMyChar().potential[0] = var1.reader().readShort();
                    Char.getMyChar().potential[1] = var1.reader().readShort();
                    Char.getMyChar().potential[2] = var1.reader().readInt();
                    Char.getMyChar().potential[3] = var1.reader().readInt();
                    break;
                case -123:
                    Char.getMyChar().xu = var1.reader().readInt();
                    Char.getMyChar().yen = var1.reader().readInt();
                    Char.getMyChar().luong = var1.reader().readInt();
                    Char.getMyChar().cHP = var1.reader().readInt();
                    Char.getMyChar().cMP = var1.reader().readInt();
                    if (var1.reader().readByte() == 1) {
                        GameScr.gI().gameBN();
                        Char.getMyChar().isCaptcha = true;
                    } else {
                        Char.getMyChar().isCaptcha = false;
                    }
                    LockGame.fieldAJ();

                    break;
                case -122:
                    Char.getMyChar().cHP = var1.reader().readInt();
                    break;
                case -121:
                    Char.getMyChar().cMP = var1.reader().readInt();
                    break;
                case -120:
                    System.out.println("PLAYER LOAD ALL");

                    if ((var50 = GameScr.gameAE(var1.reader().readInt())) != null) {
                        gameAA(var50, var1);
                    }
                    break;
                case -119:
                    if ((var50 = GameScr.gameAE(var1.reader().readInt())) == null) {
                        return;
                    }

                    var50.cHP = var1.reader().readInt();
                    var50.cMaxHP = var1.reader().readInt();
                    break;
                case -117:
                    if ((var50 = GameScr.gameAE(var1.reader().readInt())) == null) {
                        return;
                    }

                    var50.cHP = var1.reader().readInt();
                    var50.cMaxHP = var1.reader().readInt();
                    var50.eff5BuffHp = var1.reader().readShort();
                    var50.eff5BuffMp = var1.reader().readShort();
                    var50.wp = var1.reader().readShort();
                    if (var50.wp == -1) {
                        var50.gameAK();
                    }
                    break;
                case -116:
                    if ((var50 = GameScr.gameAE(var1.reader().readInt())) == null) {
                        return;
                    }

                    var50.cHP = var1.reader().readInt();
                    var50.cMaxHP = var1.reader().readInt();
                    var50.eff5BuffHp = var1.reader().readShort();
                    var50.eff5BuffMp = var1.reader().readShort();
                    var50.body = var1.reader().readShort();
                    if (var50.body == -1) {
                        var50.gameAL();
                    }
                    break;
                case -113:
                    if ((var50 = GameScr.gameAE(var1.reader().readInt())) == null) {
                        return;
                    }

                    var50.cHP = var1.reader().readInt();
                    var50.cMaxHP = var1.reader().readInt();
                    var50.eff5BuffHp = var1.reader().readShort();
                    var50.eff5BuffMp = var1.reader().readShort();
                    var50.leg = var1.reader().readShort();
                    if (var50.leg == -1) {
                        var50.gameAM();
                    }
                    break;
                case -112:
                    if ((var50 = GameScr.gameAE(var1.reader().readInt())) == null) {
                        return;
                    }

                    var50.cHP = var1.reader().readInt();
                    var50.cMaxHP = var1.reader().readInt();
                    var50.eff5BuffHp = var1.reader().readShort();
                    var50.eff5BuffMp = var1.reader().readShort();
                    break;
                case -111:
                    if ((var50 = GameScr.gameAE(var1.reader().readInt())) == null) {
                        return;
                    }

                    var50.cHP = var1.reader().readInt();
                    break;
                case -110:
                    if ((var50 = GameScr.gameAE(var1.reader().readInt())) == null) {
                        return;
                    }

                    var50.cHP = var1.reader().readInt();
                    var50.cMaxHP = var1.reader().readInt();
                    var50.cx = var50.cxMoveLast = var1.reader().readShort();
                    var50.cy = var50.cyMoveLast = var1.reader().readShort();
                    var50.statusMe = 1;
                    ServerEffect.gameAA(20, var50, 2);
                    break;
                case -109:
                    Char.getMyChar().gameAA(var1);
                    if (Char.getMyChar().statusMe != 14 && Char.getMyChar().statusMe != 5) {
                        Char.getMyChar().cHP = Char.getMyChar().cMaxHP;
                        Char.getMyChar().cMP = Char.getMyChar().cMaxMP;
                    }

                    Char.getMyChar().pPoint = var1.reader().readShort();
                    Char.getMyChar().potential[0] = var1.reader().readShort();
                    Char.getMyChar().potential[1] = var1.reader().readShort();
                    Char.getMyChar().potential[2] = var1.reader().readInt();
                    Char.getMyChar().potential[3] = var1.reader().readInt();
                    LockGame.fieldAX();

                    break;
                case -107:
                    Char.getMyChar().gameAF();
                    break;
                case -106:
                    Char.getMyChar().gameAG();
                    break;
                case -105:
                    var43 = var1.reader().readInt();
                    var75 = Char.getMyChar();
                    var75.xu -= var43;
                    var75 = Char.getMyChar();
                    var75.xuInBox += var43;
                    break;
                case -104:
                    var51 = var1.reader().readInt();
                    var75 = Char.getMyChar();
                    var75.xuInBox -= var51;
                    var75 = Char.getMyChar();
                    var75.xu += var51;
                    break;
                case -102:
                    Char.getMyChar().arrItemBag[var1.reader().readByte()] = null;
                    var3 = Skills.gameAA(var1.reader().readShort());
                    Char.getMyChar().vSkill.addElement(var3);
                    if ((var3.template.type == 1 || var3.template.type == 4 || var3.template.type == 2 || var3.template.type == 3) && (var3.template.maxPoint == 0 || var3.template.maxPoint > 0 && var3.point > 0)) {
                        var10000 = var3.template.id;
                        Char.getMyChar();
                        if (var10000 == 0) {
                            var73 = Service.gI();
                            Char.getMyChar();
                            var73.selectSkill(0);
                        }

                        Char.getMyChar().vSkillFight.addElement(var3);
                    }

                    GameScr.gI();
                    GameScr.gameAZ();
                    GameScr.gI().gameAA(var3);
                    GameScr.gI().gameBJ();
                    InfoMe.gameAA(mResources.gameUF + " " + var3.template.name);
                    break;
                case -101:
                    var63 = new Effect(var1.reader().readByte(), (int) (System.currentTimeMillis() / 1000L) - var1.reader().readInt(), var1.reader().readInt(), var1.reader().readShort());
                    Char.getMyChar().vEff.addElement(var63);
                    if (var63.template.type == 7) {
                        var75 = Char.getMyChar();
                        var75.cMiss += var63.param;
                    } else if (var63.template.type != 12 && var63.template.type != 11) {
                        if (var63.template.type == 14) {
                            GameCanvas.gameAH();
                            GameCanvas.gameAI();
                            Char.getMyChar().cx = var1.reader().readShort();
                            Char.getMyChar().cy = var1.reader().readShort();
                            Char.getMyChar().statusMe = 1;
                            Char.getMyChar().isLockMove = true;
                            ServerEffect.gameAB(76, Char.getMyChar(), var63.timeLenght);
                        } else if (var63.template.type == 1) {
                            ServerEffect.gameAB(48, Char.getMyChar(), var63.timeLenght);
                        } else if (var63.template.type == 2) {
                            GameCanvas.gameAH();
                            GameCanvas.gameAI();
                            Char.getMyChar().cx = var1.reader().readShort();
                            Char.getMyChar().cy = var1.reader().readShort();
                            Char.getMyChar().statusMe = 1;
                            Char.getMyChar().isLockMove = true;
                            Char.getMyChar().isLockAttack = true;
                        } else if (var63.template.type == 3) {
                            GameCanvas.gameAH();
                            GameCanvas.gameAI();
                            Char.getMyChar().cx = var1.reader().readShort();
                            Char.getMyChar().cy = var1.reader().readShort();
                            Char.getMyChar().statusMe = 1;
                            Char.isLockKey = true;
                            ServerEffect.gameAB(43, Char.getMyChar(), var63.timeLenght);
                        }
                    } else {
                        Char.getMyChar().isInvisible = true;
                        ServerEffect.gameAA(60, Char.getMyChar().cx, Char.getMyChar().cy, 1);
                    }
                    break;
                case -100:
                    var67 = Effect.effTemplates[var1.reader().readByte()];

                    for (var51 = 0; var51 < Char.getMyChar().vEff.size(); ++var51) {
                        if ((var60 = (Effect) Char.getMyChar().vEff.elementAt(var51)).template.type == var67.type) {
                            if (var60.template.type == 7) {
                                var75 = Char.getMyChar();
                                var75.cMiss -= var60.param;
                            }

                            var60.template = var67;
                            var60.timeStart = (int) (System.currentTimeMillis() / 1000L) - var1.reader().readInt();
                            var60.timeLenght = var1.reader().readInt() / 1000;
                            var60.param = var1.reader().readShort();
                            if (var60.template.type == 7) {
                                var75 = Char.getMyChar();
                                var75.cMiss += var60.param;
                            }
                            break;
                        }
                    }

                    if (!GameScr.isPaintInfoMe) {
                        GameScr.gI().resetButton();
                    }
                    break;
                case -99:
                    var58 = var1.reader().readByte();
                    var60 = null;

                    for (var53 = 0; var53 < Char.getMyChar().vEff.size(); ++var53) {
                        if ((var60 = (Effect) Char.getMyChar().vEff.elementAt(var53)).template.id == var58) {
                            if (var60.template.type == 7) {
                                var75 = Char.getMyChar();
                                var75.cMiss -= var60.param;
                            }

                            Char.getMyChar().vEff.removeElementAt(var53);
                            break;
                        }
                    }

                    if (var60.template.type != 0 && var60.template.type != 12) {
                        if (var60.template.type != 4 && var60.template.type != 13 && var60.template.type != 17) {
                            if (var60.template.type == 23) {
                                Char.getMyChar().cHP = var1.reader().readInt();
                                Char.getMyChar().cMaxHP = var1.reader().readInt();
                            } else if (var60.template.type == 11) {
                                Char.getMyChar().isInvisible = false;
                                ServerEffect.gameAA(60, Char.getMyChar().cx, Char.getMyChar().cy, 1);
                            } else if (var60.template.type == 14) {
                                Char.getMyChar().isLockMove = false;
                            } else if (var60.template.type == 2) {
                                Char.getMyChar().isLockMove = false;
                                Char.getMyChar().isLockAttack = false;
                                ServerEffect.gameAA(77, Char.getMyChar().cx, Char.getMyChar().cy - 9, 1);
                            } else if (var60.template.type == 3) {
                                Char.isLockKey = false;
                            }
                        } else {
                            Char.getMyChar().cHP = var1.reader().readInt();
                        }
                    } else {
                        Char.getMyChar().cHP = var1.reader().readInt();
                        Char.getMyChar().cMP = var1.reader().readInt();
                        if (var60.template.type == 0) {
                            InfoMe.gameAA(mResources.gameRX);
                        } else if (var60.template.type == 12) {
                            Char.getMyChar().isInvisible = false;
                            ServerEffect.gameAA(60, Char.getMyChar().cx, Char.getMyChar().cy, 1);
                        }
                    }
                    break;
                case -98:
                    try {
                        if ((var50 = GameScr.gameAE(var1.reader().readInt())) == null) {
                            return;
                        }

                        var63 = new Effect(var1.reader().readByte(), (int) (System.currentTimeMillis() / 1000L) - var1.reader().readInt(), var1.reader().readInt(), var1.reader().readShort());
                        var50.vEff.addElement(var63);
                        if (var63.template.type != 12 && var63.template.type != 11) {
                            if (var63.template.type == 14) {
                                var50.cx = var50.cxMoveLast = var1.reader().readShort();
                                var50.cy = var50.cyMoveLast = var1.reader().readShort();
                                var50.statusMe = 1;
                                ServerEffect.gameAB(76, var50, var63.timeLenght);
                            } else if (var63.template.type == 1) {
                                ServerEffect.gameAB(48, var50, var63.timeLenght);
                            } else if (var63.template.type == 2) {
                                var50.cx = var50.cxMoveLast = var1.reader().readShort();
                                var50.cy = var50.cyMoveLast = var1.reader().readShort();
                                var50.statusMe = 1;
                                var50.isLockAttack = true;
                            } else if (var63.template.type == 3) {
                                var50.cx = var50.cxMoveLast = var1.reader().readShort();
                                var50.cy = var50.cyMoveLast = var1.reader().readShort();
                                var50.statusMe = 1;
                                ServerEffect.gameAB(43, var50, var63.timeLenght);
                            }
                        } else {
                            var50.isInvisible = true;
                            ServerEffect.gameAA(60, var50.cx, var50.cy, 1);
                        }
                    } catch (Exception var39) {
                    }
                    break;
                case -97:
                    try {
                        if ((var50 = GameScr.gameAE(var1.reader().readInt())) == null) {
                            return;
                        }

                        var67 = Effect.effTemplates[var1.reader().readByte()];

                        for (var51 = 0; var51 < var50.vEff.size(); ++var51) {
                            var60 = (Effect) var50.vEff.elementAt(var51);
                            if (var67.type == var67.type) {
                                var60.template = var67;
                                var60.timeStart = (int) (System.currentTimeMillis() / 1000L) - var1.reader().readInt();
                                var60.timeLenght = var1.reader().readInt() / 1000;
                                var60.param = var1.reader().readShort();
                                return;
                            }
                        }

                        return;
                    } catch (Exception var38) {
                        break;
                    }
                case -96:
                    if ((var50 = GameScr.gameAE(var1.reader().readInt())) == null) {
                        return;
                    }

                    var58 = var1.reader().readByte();
                    var63 = null;

                    for (var54 = 0; var54 < var50.vEff.size(); ++var54) {
                        if ((var63 = (Effect) var50.vEff.elementAt(var54)).template.id == var58) {
                            var50.vEff.removeElementAt(var54);
                            break;
                        }
                    }

                    if (var63 != null) {
                        if (var63.template.type == 0) {
                            var50.cHP = var1.reader().readInt();
                            var50.cMP = var1.reader().readInt();
                        } else if (var63.template.type == 11) {
                            var50.cx = var50.cxMoveLast = var1.reader().readUnsignedShort();
                            var50.cy = var50.cyMoveLast = var1.reader().readUnsignedShort();
                            var50.isInvisible = false;
                            ServerEffect.gameAA(60, var50.cx, var50.cy, 1);
                        } else if (var63.template.type == 12) {
                            var50.cHP = var1.reader().readInt();
                            var50.cMP = var1.reader().readInt();
                            var50.isInvisible = false;
                            ServerEffect.gameAA(60, var50.cx, var50.cy, 1);
                        } else if (var63.template.type != 4 && var63.template.type != 13 && var63.template.type != 17) {
                            if (var63.template.type == 23) {
                                Char.getMyChar().cHP = var1.reader().readInt();
                                Char.getMyChar().cMaxHP = var1.reader().readInt();
                            } else if (var63.template.type == 2) {
                                var50.isLockAttack = false;
                                ServerEffect.gameAA(77, var50.cx, var50.cy - 9, 1);
                            }
                        } else {
                            var50.cHP = var1.reader().readInt();
                        }
                    }
                    break;
                case -95:
                    GameScr.gI().timeLengthMap = var1.reader().readInt();
                    GameScr.gI().timeStartMap = (int) (System.currentTimeMillis() / 1000L);
                    break;
                case -94:
                    var4 = var1.reader().readByte();
                    Npc var72;
                    (var72 = (Npc) GameScr.vNpc.elementAt(var4)).statusMe = var1.reader().readByte();
                    if (var72.template.npcTemplateId == 31 && var72.statusMe == 15) {
                        GameScr.gameAA(var72.cx, var72.cy);
                    }
                    break;
                case -92:
                    if ((var43 = var1.reader().readInt()) == Char.getMyChar().charID) {
                        var50 = Char.getMyChar();
                    } else {
                        var50 = GameScr.gameAE(var43);
                    }

                    if (var50 != null) {
                        var50.cTypePk = var1.reader().readByte();
                        Auto.fieldAB(var50);
                        if (var50 != Char.getMyChar()) {
                            break;
                        }
                        if (var50.cTypePk == 4) {
                            GameScr.fieldGI = true;
                            break;
                        }
                        if (var50.cTypePk == 5) {
                            GameScr.fieldGI = false;
                            break;
                        }
                    }
                    break;
                case -91:
                    Item[] var68 = new Item[var1.reader().readUnsignedByte()];

                    for (var43 = 0; var43 < Char.getMyChar().arrItemBag.length; ++var43) {
                        var68[var43] = Char.getMyChar().arrItemBag[var43];
                    }

                    Char.getMyChar().arrItemBag = var68;
                    Char.getMyChar().arrItemBag[var1.reader().readUnsignedByte()] = null;
                    InfoMe.gameAA(mResources.gameFH + " " + Char.getMyChar().arrItemBag.length + " " + mResources.gameGB);
                    break;
                case -90:
                    for (var43 = 0; var43 < GameScr.vNpc.size(); ++var43) {
                        Npc var62;
                        if ((var62 = (Npc) GameScr.vNpc.elementAt(var43)).statusMe == 15) {
                            var62.statusMe = 1;
                            break;
                        }
                    }

                    if ((var42 = var1.reader().readByte()) == 1) {
                        InfoMe.gameAA(mResources.gameFI, 20, mFont.tahoma_7_yellow);
                    } else if (var42 == 2) {
                        InfoMe.gameAA(mResources.gameFJ, 20, mFont.tahoma_7_yellow);
                    }
                    break;
                case -89:
                    GameCanvas.isLoading = false;

                    try {
                        InfoMe.gameAA(var1.reader().readUTF(), 20, mFont.tahoma_7_yellow);
                    } catch (Exception var24) {
                    }

                    InfoDlg.gameAB();
                    GameCanvas.gameAJ();
                    break;
                case -87:
                    var44 = var1.reader().readByte();
                    Party var69 = (Party) GameScr.vParty.elementAt(var44);
                    GameScr.vParty.setElementAt(GameScr.vParty.elementAt(0), var44);
                    GameScr.vParty.setElementAt(var69, 0);
                    GameScr.gI().gameAT();
                    InfoMe.gameAA(var69.name + mResources.gameSY, 20, mFont.tahoma_7_yellow);
                    break;
                case -86:
                    GameScr.vParty.removeAllElements();
                    GameScr.gI().gameAT();
                    InfoMe.gameAA(mResources.gameSZ, 20, mFont.tahoma_7_yellow);
                    Code.fieldAH = null;

                    break;
                case -85:
                    GameScr.vFriend.removeAllElements();

                    try {
                        while (true) {
                            GameScr.vFriend.addElement(new Friend(var1.reader().readUTF(), var1.reader().readByte()));
                        }
                    } catch (Exception var35) {
                        for (var43 = 0; var43 < GameScr.vFriendWait.size(); ++var43) {
                            GameScr.vFriend.addElement(GameScr.vFriendWait.elementAt(var43));
                        }

                        GameScr.gI();
                        GameScr.sortList(0);
                        break;
                    }
                case -84:
                    GameScr.vEnemies.removeAllElements();

                    try {
                        while (true) {
                            GameScr.vEnemies.addElement(new Friend(var1.reader().readUTF(), var1.reader().readByte()));
                        }
                    } catch (Exception var36) {
                        GameScr.gI();
                        GameScr.sortList(1);
                        break;
                    }
                case -83:
                    var65 = var1.reader().readUTF();

                    for (var53 = 0; var53 < GameScr.vFriend.size(); ++var53) {
                        if (((Friend) GameScr.vFriend.elementAt(var53)).friendName.equals(var65)) {
                            GameScr.indexRow = 0;
                            GameScr.vFriend.removeElementAt(var53);
                            GameScr.gI();
                            GameScr.setText(var65);
                            break;
                        }
                    }

                    if (GameScr.isPaintFriend) {
                        GameScr.gI();
                        GameScr.sortList(0);
                        GameScr.indexRow = 0;
                        GameScr.scrMain.gameAA();
                    }
                    break;
                case -82:
                    var65 = var1.reader().readUTF();

                    for (var53 = 0; var53 < GameScr.vEnemies.size(); ++var53) {
                        if (((Friend) GameScr.vEnemies.elementAt(var53)).friendName.equals(var65)) {
                            GameScr.indexRow = 0;
                            GameScr.vEnemies.removeElementAt(var53);
                            break;
                        }
                    }

                    GameScr.gI();
                    GameScr.sortList(1);
                    break;
                case -81:
                    Char.getMyChar().cPk = var1.reader().readByte();
                    Char.getMyChar().charFocus = null;
                    break;
                case -80:
                    Char.getMyChar().arrItemBody[var1.reader().readByte()] = null;
                    break;
                case -78:
                    ServerEffect.gameAA(var1.reader().readShort(), Char.getMyChar().cx, Char.getMyChar().cy, 1);
                    break;
                case -77:
                    try {
                        GameScr.vPtMap.removeAllElements();

                        while (true) {
                            GameScr.vPtMap.addElement(new Party(var1.reader().readByte(), var1.reader().readUnsignedByte(), var1.reader().readUTF(), var1.reader().readByte()));
                        }
                    } catch (Exception var37) {
                        GameScr.gI().gameAS();
                        break;
                    }
                case -76:
                    ((Party) GameScr.vParty.firstElement()).isLock = var1.reader().readBoolean();
                    break;
                case -75:
                    Char.getMyChar().arrItemBox[var1.reader().readByte()] = null;
                    break;
                case -74:
                    InfoDlg.showWait(var1.reader().readUTF());
                    break;
                case -73:
                    Mob var66 = Mob.gameAA(var1.reader().readUnsignedByte());
                    ServerEffect.gameAA(67, var66.x, var66.y, 1);
                    break;
                case -72:
                    Char.getMyChar().luong = var1.reader().readInt();
                    break;
                case -71:
                    var51 = var1.reader().readInt();
                    var75 = Char.getMyChar();
                    var75.luong += var51;
                    GameScr.gameAA("+" + var51, Char.getMyChar().cx, Char.getMyChar().cy - Char.getMyChar().ch - 10, 0, -2, 6);
                    InfoMe.gameAA(mResources.gamePM + " " + var51 + " " + mResources.gameKO, 20, mFont.tahoma_7_yellow);
                    break;
                case -69:
                    var52 = var1.reader().readShort();
                    var44 = var1.reader().readByte();
                    if (var52 > 0) {
                        var64 = (short) Char.getMyChar().cx;
                        var56 = (short) (Char.getMyChar().cy - 40);
                        Char.getMyChar().mobMe = new Mob((short) -1, false, false, false, false, false, var52, 1, 0, 0, 0, var64, var56, (byte) 4, (byte) 0, var44 != 0, false);
                        Char.getMyChar().mobMe.status = 5;
                    } else {
                        Char.getMyChar().mobMe = null;
                    }
                    break;
                case -68:
                    if ((var50 = GameScr.gameAE(var1.reader().readInt())) == null) {
                        return;
                    }

                    var52 = var1.reader().readShort();
                    var44 = var1.reader().readByte();
                    if (var52 > 0) {
                        var56 = (short) var50.cx;
                        short var57 = (short) (var50.cy - 40);
                        var50.mobMe = new Mob((short) -1, false, false, false, false, false, var52, 1, 0, 0, 0, var56, var57, (byte) 4, (byte) 0, var44 != 0, false);
                        var50.mobMe.status = 5;
                    } else {
                        var50.mobMe = null;
                    }
                    break;
                case -65:
                    String var61 = var1.reader().readUTF();
                    byte[] var5 = new byte[var1.reader().readInt()];
                    var1.reader().read(var5);
                    if (var5.length == 0) {
                        var5 = null;
                    }

                    try {
                        var1.reader().readByte();
                    } catch (Exception var25) {
                        var25.printStackTrace();
                    }

                    if (var61.equals("KSkill")) {
                        GameScr.gI().gameAB(var5);
                    } else if (var61.equals("OSkill")) {
                        GameScr.gI().gameAA(var5);
                    } else if (var61.equals("CSkill")) {
                        GameScr.gI().gameAC(var5);
                    }
                    break;
                case -64:
                    if ((var50 = GameScr.gameAE(var1.reader().readInt())) == null) {
                        return;
                    }

                    var50.cHP = var1.reader().readInt();
                    var50.cMaxHP = var1.reader().readInt();
                    var50.eff5BuffHp = var1.reader().readShort();
                    var50.eff5BuffMp = var1.reader().readShort();
                    var50.head = var1.reader().readShort();
                    break;
                case -63:
                    var59 = new Integer(var1.reader().readInt());
                    if ((var50 = GameScr.gameAE(var59.intValue())) != null) {
                        GameCanvas.gameAA(var50.cName + " " + mResources.gameAA(mResources.gameQU, var1.reader().readUTF()), 88830, var59, 88811, (Object) null);
                    }
                    break;
                case -62:
                    var43 = var1.reader().readInt();
                    if (Char.getMyChar().charID == var43) {
                        Char.getMyChar().cClanName = var1.reader().readUTF();
                        Char.getMyChar().ctypeClan = var1.reader().readByte();
                        Char.getMyChar().gameAC(21);
                    } else {
                        (var50 = GameScr.gameAE(var43)).cClanName = var1.reader().readUTF();
                        var50.ctypeClan = var1.reader().readByte();
                    }
                    break;
                case -61:
                    var59 = new Integer(var1.reader().readInt());
                    if (GameScr.isViewClanInvite && (var50 = GameScr.gameAE(var59.intValue())) != null) {
                        GameCanvas.gameAA(var50.cName + " " + mResources.gameQV, 88831, var59, 88811, (Object) null);
                    }
                    break;
                case -59:
                    if ((var43 = var1.reader().readInt()) == Char.getMyChar().charID) {
                        var50 = Char.getMyChar();
                    } else {
                        var50 = GameScr.gameAE(var43);
                    }

                    var50.cHP = var1.reader().readInt();
                    var50.cMaxHP = var1.reader().readInt();
                    break;
                case -58:
                    GameScr.gI().resetButton();
                    GameCanvas.timeBallEffect = 70;
                    GameCanvas.isBallEffect = true;
                    ServerEffect.gameAA(119, GameScr.gW2 + GameScr.cmx, GameScr.gH2 + GameScr.cmy, 1);
                    break;
                case -57:
                    GameCanvas.timeBallEffect = 40;
                    GameCanvas.isBallEffect = true;
                    break;
                case -56:
                    if ((var50 = GameScr.gameAE(var1.reader().readInt())) != null) {
                        var50.cHP = var1.reader().readInt();
                        var50.cMaxHP = var1.reader().readInt();
                        var50.coat = (short) var1.reader().readUnsignedShort();
                    }
                    break;
                case -55:
                    if ((var50 = GameScr.gameAE(var1.reader().readInt())) != null) {
                        var50.cHP = var1.reader().readInt();
                        var50.cMaxHP = var1.reader().readInt();
                        var50.glove = (short) var1.reader().readUnsignedShort();
                    }
                    break;
                case -54:
                    var43 = var1.reader().readInt();
                    if (Char.getMyChar().charID == var43) {
                        var50 = Char.getMyChar();
                    } else {
                        var50 = GameScr.gameAE(var43);
                    }

                    if (var50 != null) {
                        var50.arrItemMounts = new Item[5];
                        var50.isNewMount = var50.isWolf = var50.isMoto = var50.isMotoBehind = false;

                        for (var53 = 0; var53 < var50.arrItemMounts.length; ++var53) {
                            if ((var56 = var1.reader().readShort()) != -1) {
                                var50.arrItemMounts[var53] = new Item();
                                var50.arrItemMounts[var53].typeUI = 41;
                                var50.arrItemMounts[var53].indexUI = var53;
                                var50.arrItemMounts[var53].template = ItemTemplates.gameAA(var56);
                                var50.arrItemMounts[var53].upgrade = var1.reader().readByte();
                                var50.arrItemMounts[var53].expires = var1.reader().readLong();
                                var50.arrItemMounts[var53].sys = var1.reader().readByte();
                                var50.arrItemMounts[var53].isLock = true;
                                if (var53 == 4) {
                                    if (var50.arrItemMounts[var53].template.id != 485 && var50.arrItemMounts[var53].template.id != 524) {
                                        if (var50.arrItemMounts[var53].template.id != 443 && var50.arrItemMounts[var53].template.id != 523) {
                                            var50.isNewMount = true;
                                            var50.gameBC();
                                        } else {
                                            var50.isWolf = true;
                                        }
                                    } else {
                                        var50.isMoto = true;
                                    }
                                }

                                var58 = var1.reader().readByte();
                                var50.arrItemMounts[var53].options = new MyVector();

                                for (var54 = 0; var54 < var58; ++var54) {
                                    var50.arrItemMounts[var53].options.addElement(new ItemOption(var1.reader().readUnsignedByte(), var1.reader().readInt()));
                                }
                            }
                        }
                    }
                    break;
                case 115:
                    System.out.println("UPDATE INFO ME");
                    GameScr.currentCharViewInfo = Char.getMyChar();
                    Char.getMyChar().gameGK = null;
                    Char.getMyChar().charID = var1.reader().readInt();
                    Char.getMyChar().cClanName = var1.reader().readUTF();
                    if (!Char.getMyChar().cClanName.equals("")) {
                        Char.getMyChar().ctypeClan = var1.reader().readByte();
                    }

                    Char.getMyChar().ctaskId = var1.reader().readByte();
                    Char.getMyChar().cgender = var1.reader().readByte();
                    Char.getMyChar().head = var1.reader().readShort();
                    Char.getMyChar().cspeed = var1.reader().readByte();
                    Char.getMyChar().cName = var1.reader().readUTF();
                    Char.getMyChar().cPk = var1.reader().readByte();
                    Char.getMyChar().cTypePk = var1.reader().readByte();
                    Char.getMyChar().cMaxHP = var1.reader().readInt();
                    Char.getMyChar().cHP = var1.reader().readInt();
                    Char.getMyChar().cMaxMP = var1.reader().readInt();
                    Char.getMyChar().cMP = var1.reader().readInt();
                    Char.getMyChar().cEXP = var1.reader().readLong();
                    Char.getMyChar().cExpDown = var1.reader().readLong();
                    GameScr.gameAA(Char.getMyChar().cEXP, true);
                    Char.getMyChar().eff5BuffHp = var1.reader().readShort();
                    Char.getMyChar().eff5BuffMp = var1.reader().readShort();
                    Char.getMyChar().nClass = GameScr.nClasss[var1.reader().readByte()];
                    Char.getMyChar().pPoint = var1.reader().readShort();
                    Char.getMyChar().potential[0] = var1.reader().readShort();
                    Char.getMyChar().potential[1] = var1.reader().readShort();
                    Char.getMyChar().potential[2] = var1.reader().readInt();
                    Char.getMyChar().potential[3] = var1.reader().readInt();
                    Char.getMyChar().sPoint = var1.reader().readShort();
                    Char.getMyChar().vSkill.removeAllElements();
                    Char.getMyChar().vSkillFight.removeAllElements();
                    var44 = var1.reader().readByte();

                    for (var42 = 0; var42 < var44; ++var42) {
                        var3 = Skills.gameAA(var1.reader().readShort());
                        if (Char.getMyChar().myskill == null) {
                            Char.getMyChar().myskill = var3;
                        }
                        if (Code.fieldAB != null && Auto.fieldAL != null && var3.template.id == Auto.fieldAL.template.id) {
                            Auto.fieldAL = var3;
                        }
                        Char.getMyChar().vSkill.addElement(var3);
                        if ((var3.template.type == 1 || var3.template.type == 4 || var3.template.type == 2 || var3.template.type == 3) && (var3.template.maxPoint == 0 || var3.template.maxPoint > 0 && var3.point > 0)) {
                            var10000 = var3.template.id;
                            Char.getMyChar();
                            if (var10000 == 0) {
                                var73 = Service.gI();
                                Char.getMyChar();
                                var73.selectSkill(0);
                            }

                            Char.getMyChar().vSkillFight.addElement(var3);
                        }
                    }

                    GameScr.gI();
                    GameScr.gameAZ();
                    Char.getMyChar().xu = var1.reader().readInt();
                    Char.getMyChar().yen = var1.reader().readInt();
                    Char.getMyChar().luong = var1.reader().readInt();
                    Char.getMyChar().arrItemBag = new Item[var1.reader().readUnsignedByte()];
                    GameScr.mpPotion = 0;
                    GameScr.hpPotion = 0;

                    for (var43 = 0; var43 < Char.getMyChar().arrItemBag.length; ++var43) {
                        if ((var45 = var1.reader().readShort()) != -1) {
                            Char.getMyChar().arrItemBag[var43] = new Item();
                            Char.getMyChar().arrItemBag[var43].typeUI = 3;
                            Char.getMyChar().arrItemBag[var43].indexUI = var43;
                            Char.getMyChar().arrItemBag[var43].template = ItemTemplates.gameAA(var45);
                            Char.getMyChar().arrItemBag[var43].isLock = var1.reader().readBoolean();
                            if (Char.getMyChar().arrItemBag[var43].isTypeBody() || Char.getMyChar().arrItemBag[var43].isTypeMounts() || Char.getMyChar().arrItemBag[var43].isTypeNgocKham()) {
                                Char.getMyChar().arrItemBag[var43].upgrade = var1.reader().readByte();
                            }

                            Char.getMyChar().arrItemBag[var43].isExpires = var1.reader().readBoolean();
                            Char.getMyChar().arrItemBag[var43].quantity = var1.reader().readUnsignedShort();
                            if (Char.getMyChar().arrItemBag[var43].template.type == 16) {
                                GameScr.hpPotion += Char.getMyChar().arrItemBag[var43].quantity;
                            }

                            if (Char.getMyChar().arrItemBag[var43].template.type == 17) {
                                GameScr.mpPotion += Char.getMyChar().arrItemBag[var43].quantity;
                            }

                            if (Char.getMyChar().arrItemBag[var43].template.id == 340) {
                                var74 = GameScr.gI();
                                var74.numSprinLeft += Char.getMyChar().arrItemBag[var43].quantity;
                            }
                        }
                    }
                    Code.fieldAL();

                    Char.getMyChar().arrItemBody = new Item[32];

                    try {
                        Char.getMyChar().gameAJ();

                        for (var43 = 0; var43 < 16; ++var43) {
                            if ((var45 = var1.reader().readShort()) != -1) {
                                ItemTemplate var48;
                                var4 = (var48 = ItemTemplates.gameAA(var45)).type;
                                Char.getMyChar().arrItemBody[var4] = new Item();
                                Char.getMyChar().arrItemBody[var4].indexUI = var4;
                                Char.getMyChar().arrItemBody[var4].typeUI = 5;
                                Char.getMyChar().arrItemBody[var4].template = var48;
                                Char.getMyChar().arrItemBody[var4].isLock = true;
                                Char.getMyChar().arrItemBody[var4].upgrade = var1.reader().readByte();
                                Char.getMyChar().arrItemBody[var4].sys = var1.reader().readByte();
                                if (var4 == 1) {
                                    Char.getMyChar().wp = Char.getMyChar().arrItemBody[var4].template.part;
                                } else if (var4 == 2) {
                                    Char.getMyChar().body = Char.getMyChar().arrItemBody[var4].template.part;
                                } else if (var4 == 6) {
                                    Char.getMyChar().leg = Char.getMyChar().arrItemBody[var4].template.part;
                                }
                            }
                        }
                    } catch (Exception var33) {
                        var33.printStackTrace();
                    }

                    Char.getMyChar().isHuman = var1.reader().readBoolean();
                    Char.getMyChar().isNhanban = var1.reader().readBoolean();
                    if ((var46 = new short[]{var1.reader().readShort(), var1.reader().readShort(), var1.reader().readShort(), var1.reader().readShort()})[0] > -1) {
                        Char.getMyChar().head = var46[0];
                    }

                    if (var46[1] > -1) {
                        Char.getMyChar().wp = var46[1];
                    }

                    if (var46[2] > -1) {
                        Char.getMyChar().body = var46[2];
                    }

                    if (var46[3] > -1) {
                        Char.getMyChar().leg = var46[3];
                    }

                    short[] var49 = new short[10];

                    try {
                        for (var51 = 0; var51 < 10; ++var51) {
                            var49[var51] = var1.reader().readShort();
                        }
                    } catch (Exception var32) {
                        var49 = null;
                    }

                    if (var49 != null) {
                        Char.getMyChar().gameAA(var49);
                    }

                    GameScr.gI();
                    GameScr.gameAZ();
                    if (Char.getMyChar().isHuman) {
                        GameScr.gI();
                        GameScr.gameAG();
                    } else if (Char.getMyChar().isNhanban) {
                        GameScr.gI();
                        GameScr.gameAH();
                    }

                    Char.getMyChar().statusMe = 4;
                    GameScr.isViewClanInvite = RMS.gameAC(Char.getMyChar().cName + "vci") >= 1;
                    Service.gI().loadRMS("KSkill");
                    Service.gI().loadRMS("OSkill");
                    Service.gI().loadRMS("CSkill");

                    try {
                        for (var51 = 0; var51 < 16; ++var51) {
                            if ((var52 = var1.reader().readShort()) != -1) {
                                var53 = (var47 = ItemTemplates.gameAA(var52)).type + 16;
                                Char.getMyChar().arrItemBody[var53] = new Item();
                                Char.getMyChar().arrItemBody[var53].indexUI = var53;
                                Char.getMyChar().arrItemBody[var53].typeUI = 5;
                                Char.getMyChar().arrItemBody[var53].template = var47;
                                Char.getMyChar().arrItemBody[var53].isLock = true;
                                Char.getMyChar().arrItemBody[var53].upgrade = var1.reader().readByte();
                                Char.getMyChar().arrItemBody[var53].sys = var1.reader().readByte();
                                if (var53 == 1) {
                                    Char.getMyChar().wp = Char.getMyChar().arrItemBody[var53].template.part;
                                } else if (var53 == 2) {
                                    Char.getMyChar().body = Char.getMyChar().arrItemBody[var53].template.part;
                                } else if (var53 == 6) {
                                    Char.getMyChar().leg = Char.getMyChar().arrItemBody[var53].template.part;
                                }
                            }
                        }
                    } catch (Exception var31) {
                        var31.printStackTrace();
                    }

                    boolean var55 = false;

                    try {
                        var56 = var1.reader().readShort();
                    } catch (Exception var27) {
                        var56 = -1;
                    }

                    Char.getMyChar().ID_SUSANO = var56;
            }
        } catch (Exception var40) {
            System.out.println("AUTO LOGIN/CONTROLLER ERROR: " + var40.toString());
            var40.printStackTrace();
        } finally {
            if (var1 != null) {
                var1.cleanup();
            }

        }

    }

    private static boolean gameAA(Char var0, Message var1) {
        try {
            var0.cClanName = var1.reader().readUTF();
            if (!var0.cClanName.equals("")) {
                var0.ctypeClan = var1.reader().readByte();
            }

            var0.isInvisible = var1.reader().readBoolean();
            var0.cTypePk = var1.reader().readByte();
            var0.nClass = GameScr.nClasss[var1.reader().readByte()];
            var0.cgender = var1.reader().readByte();
            var0.head = var1.reader().readShort();
            var0.cName = var1.reader().readUTF();
            var0.cHP = var1.reader().readInt();
            var0.cMaxHP = var1.reader().readInt();
            var0.clevel = var1.reader().readUnsignedByte();
            var0.wp = var1.reader().readShort();
            var0.body = var1.reader().readShort();
            var0.leg = var1.reader().readShort();
            byte var2 = var1.reader().readByte();
            if (var0.wp == -1) {
                var0.gameAK();
            }

            if (var0.body == -1) {
                var0.gameAL();
            }

            if (var0.leg == -1) {
                var0.gameAM();
            }

            short var3;
            if (var2 == -1) {
                var0.mobMe = null;
            } else {
                var3 = (short) var0.cx;
                short var4 = (short) (var0.cy - 40);
                var0.mobMe = new Mob((short) -1, false, false, false, false, false, var2, 1, 0, 0, 0, var3, var4, (byte) 4, (byte) 0, false, false);
                var0.mobMe.status = 5;
            }

            var0.cx = var0.cxMoveLast = var1.reader().readShort();
            var0.cy = var0.cyMoveLast = var1.reader().readShort();
            var0.eff5BuffHp = var1.reader().readShort();
            var0.eff5BuffMp = var1.reader().readShort();
            byte var13 = var1.reader().readByte();

            int var16;
            for (var16 = 0; var16 < var13; ++var16) {
                Effect var10 = new Effect(var1.reader().readByte(), var1.reader().readInt(), var1.reader().readInt(), var1.reader().readShort());
                var0.vEff.addElement(var10);
                if (var10.template.type == 12 || var10.template.type == 11) {
                    var0.isInvisible = true;
                }
            }

            if (!var0.isInvisible) {
                ServerEffect.gameAA(60, var0, 1);
            }

            if (var0.cHP == 0) {
                var0.statusMe = 14;
                if (Char.getMyChar().charID == var0.charID) {
                    GameScr.gI().resetButton();
                }
            }

            if (var0.charID == -Char.getMyChar().charID) {
                for (var16 = 0; var16 < GameScr.vNpc.size(); ++var16) {
                    Npc var11;
                    if ((var11 = (Npc) GameScr.vNpc.elementAt(var16)).template.name.equals(var0.cName)) {
                        var11.statusMe = 15;
                        var11.chatPopup = null;
                        break;
                    }
                }
            }

            var0.isHuman = var1.reader().readBoolean();
            var0.isNhanban = var1.reader().readBoolean();
            if (var0.gameBB()) {
                ServerEffect.gameAA(141, var0.cx, var0.cy, 0);
            }

            short[] var18;
            if ((var18 = new short[]{var1.reader().readShort(), var1.reader().readShort(), var1.reader().readShort(), var1.reader().readShort()})[0] > -1) {
                var0.head = var18[0];
            }

            if (var18[1] > -1) {
                var0.wp = var18[1];
            }

            if (var18[2] > -1) {
                var0.body = var18[2];
            }

            if (var18[3] > -1) {
                var0.leg = var18[3];
            }

            short[] var12 = new short[10];

            try {
                for (int var15 = 0; var15 < 10; ++var15) {
                    var12[var15] = var1.reader().readShort();
                }
            } catch (Exception var6) {
            }

            var0.gameAA(var12);
            boolean var17 = false;

            try {
                var3 = var1.reader().readShort();
            } catch (Exception var5) {
                var3 = -1;
            }

            var0.ID_SUSANO = var3;
            Char var14 = var0;

            for (int var8 = 0; var8 < GameScr.vParty.size(); ++var8) {
                Party var9;
                if ((var9 = (Party) GameScr.vParty.elementAt(var8)).charId == var14.charID) {
                    var9.c = var14;
                    break;
                }
            }

            return true;
        } catch (Exception var7) {
            return false;
        }
    }

    private static void gameAH(Message var0) {
        try {
            byte var1;
            Item var3;
            var1 = var0.reader().readByte();
            int var2 = var0.reader().readUnsignedByte();
            var3 = null;
            label186:
            switch (var1) {
                case 2:
                    var3 = GameScr.arrItemWeapon[var2];
                    break;
                case 3:
                    if ((var3 = Char.getMyChar().arrItemBag[var2]) != null) {
                        break;
                    }

                    if (GameScr.itemSplit != null && GameScr.itemSplit.indexUI == var2) {
                        var3 = GameScr.itemSplit;
                    }

                    if (GameScr.itemUpGrade != null && GameScr.itemUpGrade.indexUI == var2) {
                        var3 = GameScr.itemUpGrade;
                    }

                    if (GameScr.itemSell != null && GameScr.itemSell.indexUI == var2) {
                        var3 = GameScr.itemSell;
                    }

                    int var4;
                    if (var3 == null && GameScr.arrItemUpGrade != null) {
                        for (var4 = 0; var4 < GameScr.arrItemUpGrade.length; ++var4) {
                            if (GameScr.arrItemUpGrade[var4] != null && GameScr.arrItemUpGrade[var4].indexUI == var2) {
                                var3 = GameScr.arrItemUpGrade[var4];
                                break;
                            }
                        }
                    }

                    if (var3 == null && GameScr.arrItemConvert != null) {
                        for (var4 = 0; var4 < GameScr.arrItemConvert.length; ++var4) {
                            if (GameScr.arrItemConvert[var4] != null && GameScr.arrItemConvert[var4].indexUI == var2) {
                                var3 = GameScr.arrItemConvert[var4];
                                break;
                            }
                        }
                    }

                    if (var3 == null && GameScr.arrItemUpPeal != null) {
                        for (var4 = 0; var4 < GameScr.arrItemUpPeal.length; ++var4) {
                            if (GameScr.arrItemUpPeal[var4] != null && GameScr.arrItemUpPeal[var4].indexUI == var2) {
                                var3 = GameScr.arrItemUpPeal[var4];
                                break;
                            }
                        }
                    }

                    if (var3 == null && GameScr.arrItemTradeMe != null) {
                        for (var4 = 0; var4 < GameScr.arrItemTradeMe.length; ++var4) {
                            if (GameScr.arrItemTradeMe[var4] != null && GameScr.arrItemTradeMe[var4].indexUI == var2) {
                                var3 = GameScr.arrItemTradeMe[var4];
                                break;
                            }
                        }
                    }

                    if (var3 != null || GameScr.arrItemSplit == null) {
                        break;
                    }

                    var4 = 0;

                    while (true) {
                        if (var4 >= GameScr.arrItemSplit.length) {
                            break label186;
                        }

                        if (GameScr.arrItemSplit[var4] != null && GameScr.arrItemSplit[var4].indexUI == var2) {
                            var3 = GameScr.arrItemSplit[var4];
                            break label186;
                        }

                        ++var4;
                    }
                case 4:
                    var3 = Char.getMyChar().arrItemBox[var2];
                    break;
                case 5:
                    var3 = Char.getMyChar().arrItemBody[var2];
                    break;
                case 6:
                    var3 = GameScr.arrItemStack[var2];
                    break;
                case 7:
                    var3 = GameScr.arrItemStackLock[var2];
                    break;
                case 8:
                    var3 = GameScr.arrItemGrocery[var2];
                    break;
                case 9:
                    var3 = GameScr.arrItemGroceryLock[var2];
                case 10:
                case 11:
                case 12:
                case 13:
                case 31:
                case 33:
                case 36:
                case 37:
                case 38:
                default:
                    break;
                case 14:
                    var3 = GameScr.arrItemStore[var2];
                    break;
                case 15:
                    var3 = GameScr.arrItemBook[var2];
                    break;
                case 16:
                    var3 = GameScr.arrItemLien[var2];
                    break;
                case 17:
                    var3 = GameScr.arrItemNhan[var2];
                    break;
                case 18:
                    var3 = GameScr.arrItemNgocBoi[var2];
                    break;
                case 19:
                    var3 = GameScr.arrItemPhu[var2];
                    break;
                case 20:
                    var3 = GameScr.arrItemNonNam[var2];
                    break;
                case 21:
                    var3 = GameScr.arrItemNonNu[var2];
                    break;
                case 22:
                    var3 = GameScr.arrItemAoNam[var2];
                    break;
                case 23:
                    var3 = GameScr.arrItemAoNu[var2];
                    break;
                case 24:
                    var3 = GameScr.arrItemGangTayNam[var2];
                    break;
                case 25:
                    var3 = GameScr.arrItemGangTayNu[var2];
                    break;
                case 26:
                    var3 = GameScr.arrItemQuanNam[var2];
                    break;
                case 27:
                    var3 = GameScr.arrItemQuanNu[var2];
                    break;
                case 28:
                    var3 = GameScr.arrItemGiayNam[var2];
                    break;
                case 29:
                    var3 = GameScr.arrItemGiayNu[var2];
                    break;
                case 30:
                    var3 = GameScr.arrItemTradeOrder[var2];
                    break;
                case 32:
                    var3 = GameScr.arrItemFashion[var2];
                    break;
                case 34:
                    var3 = GameScr.arrItemClanShop[var2];
                    break;
                case 35:
                    var3 = GameScr.arrItemElites[var2];
                    break;
                case 39:
                    var3 = Char.clan.items[GameScr.indexSelect];
            }

            var3.expires = var0.reader().readLong();
            if (var3.isTypeUIMe()) {
                var3.saleCoinLock = var0.reader().readInt();
            } else if (var3.isTypeUIShop() || var3.isTypeUIShopLock() || var3.isTypeUIStore() || var3.isTypeUIBook() || var3.isTypeUIFashion() || var3.isTypeUIClanShop()) {
                var3.buyCoin = var0.reader().readInt();
                var3.buyCoinLock = var0.reader().readInt();
                var3.buyGold = var0.reader().readInt();
            }

            if (!var3.isTypeBody() && !var3.isTypeMounts() && !var3.isTypeNgocKham()) {
                if (var3.template.id == 233) {
                    var3.img = gameAA(NinjaUtil.gameAB(var0));
                } else if (var3.template.id == 234) {
                    var3.img = gameAA(NinjaUtil.gameAB(var0));
                } else if (var3.template.id == 235) {
                    var3.img = gameAA(NinjaUtil.gameAB(var0));
                }
            } else {
                var3.sys = var0.reader().readByte();
                var3.options = new MyVector();

                try {
                    while (true) {
                        var3.options.addElement(new ItemOption(var0.reader().readUnsignedByte(), var0.reader().readInt()));
                    }
                } catch (Exception var5) {
                }
            }

            if (var1 == 5) {
                Char.getMyChar().gameAW();
                return;
            }
        } catch (Exception var6) {
            var6.printStackTrace();
            System.out.println("Controller.requestItemInfo()");
        }

    }

    private static void gameAI(Message message) {
        try {
//            byte var1 = var0.reader().readByte();
//
//            for (byte var2 = 0; var2 < var1; ++var2) {
//                short var3 = (short) var0.reader().readUnsignedByte();
//                boolean var4 = var0.reader().readBoolean();
//                boolean var5 = var0.reader().readBoolean();
//                boolean var6 = var0.reader().readBoolean();
//                boolean var7 = var0.reader().readBoolean();
//                boolean var8 = var0.reader().readBoolean();
//                short var9 = var0.reader().readShort();
//                byte var10 = var0.reader().readByte();
//                int var11 = var0.reader().readInt();
//                int var12 = var0.reader().readUnsignedByte();
//                int var13 = var0.reader().readInt();
//                short var14 = var0.reader().readShort();
//                short var15 = var0.reader().readShort();
//                byte var16 = var0.reader().readByte();
//                byte var17 = var0.reader().readByte();
//                boolean var18 = var0.reader().readBoolean();
//                Mob var20 = new Mob(var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, true);
//                if (Mob.arrMobTemplate[var20.templateId].type != 0) {
//                    if (var2 % 3 == 0) {
//                        var20.dir = -1;
//                    } else {
//                        var20.dir = 1;
//                    }
//
//                    var20.x += 10 - var2 % 20;
//                }
//
//                GameScr.vMob.addElement(var20);
            for (byte byte1 = message.reader().readByte(), b = 0; b < byte1; ++b) {
                GameScr.fieldAA(new Mob((short) message.reader().readUnsignedByte(), message.reader().readBoolean(), message.reader().readBoolean(), message.reader().readBoolean(), message.reader().readBoolean(), message.reader().readBoolean(), message.reader().readUnsignedByte(), message.reader().readByte(), message.reader().readInt(), message.reader().readUnsignedByte(), message.reader().readInt(), message.reader().readShort(), message.reader().readShort(), message.reader().readByte(), message.reader().readByte(), message.reader().readBoolean(), true), b);
            }

        } catch (Exception var19) {
            var19.printStackTrace();
            System.out.println("err addMob");
        }
    }

    private static void gameAJ(Message var0) {
        try {
            short var1 = (short) var0.reader().readUnsignedByte();
            short var2 = var0.reader().readShort();
            short var3 = var0.reader().readShort();
            byte var4 = var0.reader().readByte();
            short var6 = var0.reader().readShort();
            EffectAuto.gameAA(var1, var2, var3, var4, var6, 1);
        } catch (Exception var5) {
            var5.printStackTrace();
            System.out.println("err add effAuto");
        }
    }

    private static void gameAK(Message var0) {
        try {
            short var1 = (short) var0.reader().readUnsignedByte();
            short var2 = var0.reader().readShort();
            byte[] var3 = null;
            if (var2 > 0) {
                var3 = new byte[var2];
                var0.reader().read(var3);
            }

            EffectAuto.gameAA(var1, var3);
        } catch (Exception var4) {
            var4.printStackTrace();
            System.out.println("err add effAuto");
        }
    }

    private static void gameAL(Message var0) {
        try {
            short var1 = (short) var0.reader().readUnsignedByte();
            byte[] var3 = NinjaUtil.gameAA(var0);
            EffectAuto.gameAB(var1, var3);
        } catch (Exception var2) {
            var2.printStackTrace();
            System.out.println("err getImgEffAuto");
        }
    }

    private static void gameAM(Message var0) {
        try {
            byte var1 = var0.reader().readByte();
            Char.getMyChar().luong = var0.reader().readInt();
            Char.getMyChar().xu = var0.reader().readInt();
            Char.getMyChar().yen = var0.reader().readInt();
            int var3;
            if (var1 == 0) {
                if (GameScr.itemSplit != null) {
                    GameScr.itemSplit = null;
                }

                if (GameScr.arrItemSplit != null) {
                    for (var3 = 0; var3 < GameScr.arrItemSplit.length; ++var3) {
                        GameScr.arrItemSplit[var3] = null;
                    }
                }
            } else if (var1 == 1) {
                if (GameScr.itemSplit != null) {
                    GameScr.itemSplit.isLock = true;
                    GameScr.itemSplit.upgrade = var0.reader().readByte();
                    GameScr.effUpok = GameScr.efs[53];
                    GameScr.indexEff = 0;
                }

                if (GameScr.arrItemSplit != null) {
                    for (var3 = 0; var3 < GameScr.arrItemSplit.length; ++var3) {
                        GameScr.arrItemSplit[var3] = null;
                    }
                }
            } else if ((var1 == 2 || var1 == 3) && GameScr.arrItemSplit != null) {
                for (var3 = 0; var3 < GameScr.arrItemSplit.length; ++var3) {
                    GameScr.arrItemSplit[var3] = null;
                }
            }

            GameScr.gI().left = GameScr.gI().center = null;
            GameScr.gI().gameBB();
            GameCanvas.gameAJ();
        } catch (Exception var2) {
            var2.printStackTrace();
            System.out.println("err getImgEffAuto");
        }
    }

    private static void gameAN(Message var0) {
        try {
            Object var1;
            int var2;
            if (var0.reader().readByte() == 1) {
                var1 = Mob.gameAA(var0.reader().readUnsignedByte());
            } else if ((var2 = var0.reader().readInt()) == Char.getMyChar().charID) {
                var1 = Char.getMyChar();
            } else {
                var1 = GameScr.gameAE(var2);
            }

            if (var1 == null) {
                return;
            }

            short var15 = var0.reader().readShort();
            int var3 = var0.reader().readInt();
            byte var4 = var0.reader().readByte();
            boolean var13 = var0.reader().readByte() != 0;
            long var10002 = (long) var3;
            var2 = var4 * 1000;
            long var10 = var10002;
            short var16 = var15;
            Object var14 = var1;
            int var18 = 0;

            while (true) {
                if (var18 >= ((MainObject) var14).vecEFfect.size()) {
                    DataSkillEff var19 = new DataSkillEff(var16, var10, var2, var13);
                    ((MainObject) var14).vecEFfect.addElement(var19);
                    return;
                }

                DataSkillEff var5;
                if ((var5 = (DataSkillEff) ((MainObject) var14).vecEFfect.elementAt(var18)) != null && var5.gameAE == var16) {
                    var5.gameAQ = var10 + System.currentTimeMillis();
                    var5.gameAR = var2;
                    var5.gameAA(var10);
                    break;
                }

                ++var18;
            }
        } catch (Exception var12) {
            var12.printStackTrace();
        }

    }

    private static void gameAO(Message var0) {
        try {
            short var1 = (short) var0.reader().readUnsignedByte();
            byte[] var4 = NinjaUtil.gameAA(var0);
            GameData.gameAA(var1, var4);
            ImageIcon var2;
            if ((var2 = (ImageIcon) GameData.listImgIcon.gameAA("" + var1)) == null) {
                var2 = new ImageIcon();
                GameData.listImgIcon.gameAA(String.valueOf(var1), var2);
            }

            var2.img = gameAA(var4);
            if (GameMidlet.CLIENT_TYPE != 1) {
                RMS.gameAB("ImgEffect " + var1, var4);
                return;
            }
        } catch (Exception var3) {
            var3.printStackTrace();
            System.out.println("err getImgEffect");
        }

    }

    private static void gameAP(Message var0) {
        try {
            short var1 = (short) var0.reader().readUnsignedByte();
            short var2 = var0.reader().readShort();
            byte[] var3 = null;
            if (var2 > 0) {
                var3 = new byte[var2];
                var0.reader().read(var3);
            }

            EffectData var5;
            if ((var5 = (EffectData) GameData.listbyteData.gameAA("" + var1)) != null) {
                if (var3 != null) {
                    var5.data = var3;
                }

                return;
            }
        } catch (Exception var4) {
            var4.printStackTrace();
            System.out.println("err getDataEffect ");
        }

    }

    private static void gameAA(Message var0, int var1) {
        try {
            Mob.arrMobTemplate[var1].imginfo = new ImageInfo[var0.reader().readByte()];

            int var2;
            for (var2 = 0; var2 < Mob.arrMobTemplate[var1].imginfo.length; ++var2) {
                Mob.arrMobTemplate[var1].imginfo[var2] = new ImageInfo();
                var0.reader().readByte();
                Mob.arrMobTemplate[var1].imginfo[var2].x0 = var0.reader().readUnsignedByte();
                Mob.arrMobTemplate[var1].imginfo[var2].y0 = var0.reader().readUnsignedByte();
                Mob.arrMobTemplate[var1].imginfo[var2].w = var0.reader().readUnsignedByte();
                Mob.arrMobTemplate[var1].imginfo[var2].h = var0.reader().readUnsignedByte();
            }

            Mob.arrMobTemplate[var1].frameBoss = new Frame[var0.reader().readShort()];

            for (var2 = 0; var2 < Mob.arrMobTemplate[var1].frameBoss.length; ++var2) {
                Mob.arrMobTemplate[var1].frameBoss[var2] = new Frame();
                byte var3 = var0.reader().readByte();
                Mob.arrMobTemplate[var1].frameBoss[var2].dx = new short[var3];
                Mob.arrMobTemplate[var1].frameBoss[var2].dy = new short[var3];
                Mob.arrMobTemplate[var1].frameBoss[var2].idImg = new byte[var3];

                for (int var4 = 0; var4 < var3; ++var4) {
                    Mob.arrMobTemplate[var1].frameBoss[var2].dx[var4] = var0.reader().readShort();
                    Mob.arrMobTemplate[var1].frameBoss[var2].dy[var4] = var0.reader().readShort();
                    Mob.arrMobTemplate[var1].frameBoss[var2].idImg[var4] = var0.reader().readByte();
                }
            }

            short var6 = var0.reader().readShort();

            for (int var7 = 0; var7 < var6; ++var7) {
                var0.reader().readShort();
            }

        } catch (Exception var5) {
        }
    }

    private static void gameAB(Message var0, int var1) {
        try {
            boolean var2 = false;
            Mob.arrMobTemplate[var1].imginfo = new ImageInfo[var0.reader().readByte()];

            int var7;
            for (var7 = 0; var7 < Mob.arrMobTemplate[var1].imginfo.length; ++var7) {
                Mob.arrMobTemplate[var1].imginfo[var7] = new ImageInfo();
                var0.reader().readByte();
                Mob.arrMobTemplate[var1].imginfo[var7].x0 = var0.reader().readUnsignedByte();
                Mob.arrMobTemplate[var1].imginfo[var7].y0 = var0.reader().readUnsignedByte();
                Mob.arrMobTemplate[var1].imginfo[var7].w = var0.reader().readUnsignedByte();
                Mob.arrMobTemplate[var1].imginfo[var7].h = var0.reader().readUnsignedByte();
            }

            Mob.arrMobTemplate[var1].frameBoss = new Frame[var0.reader().readShort()];

            int var4;
            for (var7 = 0; var7 < Mob.arrMobTemplate[var1].frameBoss.length; ++var7) {
                Mob.arrMobTemplate[var1].frameBoss[var7] = new Frame();
                byte var3 = var0.reader().readByte();
                Mob.arrMobTemplate[var1].frameBoss[var7].dx = new short[var3];
                Mob.arrMobTemplate[var1].frameBoss[var7].dy = new short[var3];
                Mob.arrMobTemplate[var1].frameBoss[var7].idImg = new byte[var3];
                Mob.arrMobTemplate[var1].frameBoss[var7].flip = new byte[var3];
                Mob.arrMobTemplate[var1].frameBoss[var7].onTop = new byte[var3];

                for (var4 = 0; var4 < var3; ++var4) {
                    Mob.arrMobTemplate[var1].frameBoss[var7].dx[var4] = var0.reader().readShort();
                    Mob.arrMobTemplate[var1].frameBoss[var7].dy[var4] = var0.reader().readShort();
                    Mob.arrMobTemplate[var1].frameBoss[var7].idImg[var4] = var0.reader().readByte();
                    Mob.arrMobTemplate[var1].frameBoss[var7].flip[var4] = var0.reader().readByte();
                    Mob.arrMobTemplate[var1].frameBoss[var7].onTop[var4] = var0.reader().readByte();
                }
            }

            short var9 = (short) var0.reader().readUnsignedByte();
            Mob.arrMobTemplate[var1].sequence = new byte[var9];

            int var8;
            for (var8 = 0; var8 < var9; ++var8) {
                Mob.arrMobTemplate[var1].sequence[var8] = (byte) var0.reader().readShort();
            }

            var0.reader().readByte();

            for (var8 = 0; var8 < 4; ++var8) {
                if (var8 != 2) {
                    byte var10 = var0.reader().readByte();
                    Mob.arrMobTemplate[var1].frameChar[var8] = new byte[var10];

                    for (var4 = 0; var4 < var10; ++var4) {
                        Mob.arrMobTemplate[var1].frameChar[var8][var4] = var0.reader().readByte();
                    }
                }
            }

            try {
                Mob.arrMobTemplate[var1].indexSplash[0] = (byte) (Mob.arrMobTemplate[var1].frameChar[0].length - 7);
                Mob.arrMobTemplate[var1].indexSplash[1] = (byte) (Mob.arrMobTemplate[var1].frameChar[1].length - 7);
                Mob.arrMobTemplate[var1].indexSplash[2] = (byte) (Mob.arrMobTemplate[var1].frameChar[3].length - 7);
                Mob.arrMobTemplate[var1].indexSplash[3] = (byte) (Mob.arrMobTemplate[var1].frameChar[3].length - 7);
            } catch (Exception var5) {
                (new StringBuffer("loi read data mod ")).append(var5.toString()).toString();
            }

            for (var8 = 0; var8 < 3; ++var8) {
                Mob.arrMobTemplate[var1].indexSplash[var8] = var0.reader().readByte();
            }

            Mob.arrMobTemplate[var1].indexSplash[3] = Mob.arrMobTemplate[var1].indexSplash[2];
        } catch (Exception var6) {
            (new StringBuffer("loi ham read data new mob ")).append(var1).append(" ").append(var6.toString()).toString();
        }
    }
}
