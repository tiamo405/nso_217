
import javax.microedition.lcdui.Image;

public final class MapScr extends mScreen implements IActionListener {

    private static MapScr instance;
    private boolean modeCurrentMap;
    private static Image imgMap = null;
    private static Image imgX;
    private static Image imgPointer;
    private static int mapW;
    private static int mapH;
    private static int mfx;
    private static int mfy;
    private static int mpoint;
    private static int tick3;
    private static int mcmtoX;
    private static int mcmtoY;
    private static int mcmvx;
    private static int mcmvy;
    private static int mcmdx;
    private static int mcmdy;
    private static int mcmx;
    private static int mcmy;
    private static int mcmxLim;
    private static int mcmyLim;
    private static int taskmapId;
    private static int dx = 0;
    private static int dy = 0;
    private static int[] x = new int[]{1, 156, 140, 174, 196, 195, 125, 148, 156, 173, 199, 203, 222, 264, 283, 277, 298, 307, 311, 315, 116, 90, 59, 31, 252, 55, 81, 111, 148, 187, 219, 253, 278, 304, 311, 310, 284, 309, 294, 62, 92, 117, 99, 134, 154, 175, 34, 52, 40, 78, 59, 82, 114, 179, 158, 142, 1, 215, 291, 242, 147, 301, 71, 23, 116, 126, 305, 286, 264, 20, 46, 70, 78, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    private static int[] y = new int[]{1, 68, 75, 88, 80, 107, 87, 114, 136, 160, 168, 196, 216, 219, 248, 265, 276, 260, 232, 204, 111, 82, 79, 59, 168, 33, 28, 34, 45, 20, 54, 44, 19, 40, 60, 100, 175, 165, 134, 181, 199, 208, 221, 220, 219, 221, 195, 217, 246, 244, 250, 263, 262, 241, 252, 244, 2, 240, 197, 139, 16, 18, 208, 223, 239, 186, 120, 119, 135, 107, 125, 126, 148, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    private int maxPX;
    private int maxPY;
    private int xM;
    private int yM;
    private boolean trans = false;
    private int lastX;
    private int lastY;
    private static int lastpoint;

    public MapScr() {
        super.right = new Command(mResources.gameBH, this, 1000, (Object) null);
        super.center = new Command(mResources.gameGA, this, 1001, (Object) null);
        super.left = new Command("Chuyển đến", this, 14004, (Object) null);
        lastpoint = -1;
    }

    private void gameAG() {
        if (this.modeCurrentMap) {
            super.left = null;
        } else {
            super.left = new Command("Chuyển đến", this, 14004, (Object) null);
        }
        int var1 = mGraphics.gameAA(TileMap.imgMiniMap);
        this.xM = (GameCanvas.w - var1) / 2;
        this.yM = (GameCanvas.h - 20 - mGraphics.gameAB(TileMap.imgMiniMap)) / 2;
        if (this.xM < 0) {
            this.xM = 0;
        }

        if (this.yM < 0) {
            this.yM = 0;
        }

        if (this.modeCurrentMap) {
            mcmxLim = var1 + 20 - GameCanvas.w;
            mcmyLim = mGraphics.gameAB(TileMap.imgMiniMap) + 40 - GameCanvas.h;
            this.maxPX = var1 + 20;
            this.maxPY = var1 + 40;
            if (this.maxPY < GameCanvas.h - 26) {
                this.maxPY = GameCanvas.h - 26;
            }

            if (this.maxPX < GameCanvas.w) {
                this.maxPX = GameCanvas.w;
            }

            mfx = this.xM + Char.getMyChar().cx / 12;
            mfy = this.yM + Char.getMyChar().cy / 12;
        } else {
            mcmxLim = 340 - GameCanvas.w;
            mcmyLim = 340 - GameCanvas.h;
            mfx = x[TileMap.mapID] + dx;
            mfy = y[TileMap.mapID] + dy;
            this.maxPX = 330 + dx;
            this.maxPY = 310 + dy;
        }

        this.maxPX -= 10;
        this.maxPY -= 10;
        if (mcmxLim < 0) {
            mcmxLim = 0;
        }

        if (mcmyLim < 0) {
            mcmyLim = 0;
        }

        mcmy = 0;
        mcmx = 0;
        mcmtoY = 0;
        mcmtoX = 0;
        mcmtoX = mfx - GameCanvas.hw;
        mcmtoY = mfy - GameCanvas.hh;
    }

    public static MapScr gameAF() {
        if (instance == null) {
            instance = new MapScr();
        }

        return instance;
    }

    public final void update() {
        super.update();
        SmallImage.gameAB();
        TileMap.gameAC();
        super.right = new Command(mResources.gameBH, this, 1000, (Object) null);
        super.center = new Command(mResources.gameGA, this, 1001, (Object) null);
        super.left = new Command("Chuyển đến", this, 14004, (Object) null);
        if (imgMap == null) {
            imgMap = GameCanvas.gameAC("/wm.png");
            imgX = GameCanvas.gameAC("/u/x.png");
            imgPointer = GameCanvas.gameAC("/u/wpt1.png");
            mapW = mGraphics.gameAB(imgMap);
            mapH = mGraphics.gameAB(imgMap);
        }

        if (GameCanvas.w > mapW) {
            dx = GameCanvas.hw - mapW / 2 - 12;
        }

        if (GameCanvas.h > mapH) {
            dy = GameCanvas.hh - mapH / 2;
        }

        this.gameAG();
        gameAH();
        mFont.number_green.gameAB();
        mFont.number_orange.gameAB();
        mFont.number_red.gameAB();
        mFont.number_white.gameAB();
        mFont.number_white.gameAB();
        mFont.number_yellow.gameAB();
        mFont.tahoma_7.gameAB();
        mFont.tahoma_7_blue.gameAB();
        mFont.tahoma_7_blue1.gameAB();
        mFont.tahoma_7_green.gameAB();
        mFont.tahoma_7_red.gameAB();
        mFont.tahoma_7b_blue.gameAB();
        mFont.tahoma_7b_purple.gameAB();
        mFont.tahoma_7b_red.gameAB();
        mFont.tahoma_7b_white.gameAB();
        System.gc();
        lastpoint = -1;
    }

    public final void gameAB() {
        super.gameAB();
        imgMap = null;
        imgX = null;
        System.gc();
        TileMap.gameAF();
        SmallImage.gameAC();
        mFont.number_green.gameAA();
        mFont.number_orange.gameAA();
        mFont.number_red.gameAA();
        mFont.number_white.gameAA();
        mFont.number_white.gameAA();
        mFont.number_yellow.gameAA();
        mFont.tahoma_7.gameAA();
        mFont.tahoma_7_blue.gameAA();
        mFont.tahoma_7_blue1.gameAA();
        mFont.tahoma_7_green.gameAA();
        mFont.tahoma_7_red.gameAA();
        mFont.tahoma_7b_blue.gameAA();
        mFont.tahoma_7b_purple.gameAA();
        mFont.tahoma_7b_red.gameAA();
        mFont.tahoma_7b_white.gameAA();
    }

    public final void paint(mGraphics var1) {
        boolean var2 = false;
        var1.gameAA(0);
        var1.gameAC(0, 0, GameCanvas.w, GameCanvas.h);
        var1.gameAA(10, 10);
        var1.gameAA(-mcmx, -mcmy);
        int var3;
        int var4;
        int var5;
        int var11;
        if (this.modeCurrentMap) {
            var1.gameAA(TileMap.imgMiniMap, this.xM, this.yM, 0);

            for (var5 = 0; var5 < GameScr.vMob.size(); ++var5) {
                Mob var6;
                var3 = (var6 = (Mob) GameScr.vMob.elementAt(var5)).x / 12;
                var4 = var6.y / 12;
                if (var6.level < Char.getMyChar().clevel - 2) {
                    var1.gameAA(11184810);
                } else if (var6.level > Char.getMyChar().clevel + 2) {
                    var1.gameAA(16711680);
                } else {
                    var1.gameAA(16776960);
                }

                var1.gameAC(this.xM + var3 - 1, this.yM + var4 - 1, 3, 3);
            }

            for (var5 = 0; var5 < GameScr.vParty.size(); ++var5) {
                Party var9;
                if ((var9 = (Party) GameScr.vParty.elementAt(var5)).c != null && var9.c != Char.getMyChar()) {
                    var3 = var9.c.cx / 12;
                    var4 = var9.c.cy / 12;
                    if (GameCanvas.gameTick % 10 < 8) {
                        var1.gameAA(16777215);
                        var1.gameAC(this.xM + var3 - 2, this.yM + var4 - 2, 5, 5);
                        var1.gameAA(65280);
                        var1.gameAC(this.xM + var3 - 1, this.yM + var4 - 1, 3, 3);
                    }
                }
            }

            GameScr.gI();
            GameScr.gameBF();

            for (var11 = 0; var11 < GameScr.vNpc.size(); ++var11) {
                Npc var7;
                var3 = (var7 = (Npc) GameScr.vNpc.elementAt(var11)).cx / 12;
                var4 = var7.cy / 12;
                var1.gameAA(16777215);
                var1.gameAC(this.xM + var3 - 2, this.yM + var4 - 2, 5, 5);
                var1.gameAA(65280);
                var1.gameAC(this.xM + var3 - 1, this.yM + var4 - 1, 3, 3);
                var1.gameAA(imgX, this.xM + var3, this.yM + var4, 3);
                var2 = true;
            }

            for (byte var12 = 0; var12 < TileMap.vGo.size(); ++var12) {
                Waypoint var8;
                var3 = ((var8 = (Waypoint) TileMap.vGo.elementAt(var12)).minX + var8.maxX) / 2 / 12;
                var4 = (var8.minY + var8.maxY) / 2 / 12;
                if (GameCanvas.gameTick % 10 < 8) {
                    var1.gameAA(0);
                    var1.gameAC(this.xM + var3 - 2, this.yM + var4 - 2, 5, 5);
                    var1.gameAA(16777215);
                    var1.gameAC(this.xM + var3 - 1, this.yM + var4 - 1, 3, 3);
                }
            }

            var3 = Char.getMyChar().cx / 12;
            var4 = Char.getMyChar().cy / 12;
            var1.gameAA(16777215);
            var1.gameAC(this.xM + var3 - 2, this.yM + var4 - 2, 5, 5);
            if (GameCanvas.gameTick % 10 > 5) {
                var1.gameAA(255);
                var1.gameAC(this.xM + var3 - 1, this.yM + var4 - 1, 3, 3);
            }

            var1.gameAA(imgPointer, mfx - 2, mfy, 0);
            super.paint(var1);
            if (!var2) {
                mFont.tahoma_7_white.gameAA(var1, TileMap.mapName, 10, GameCanvas.h - 17, 0);
            }
        } else {
            if (GameCanvas.w > mapW && GameCanvas.h > mapH) {
                var1.gameAA(imgMap, GameCanvas.hw, GameCanvas.hh, StaticObj.VCENTER_HCENTER);
            } else if (GameCanvas.w > mapW) {
                var1.gameAA(imgMap, GameCanvas.hw, 0, StaticObj.TOP_CENTER);
            } else if (GameCanvas.h > mapH) {
                var1.gameAA(imgMap, 0, GameCanvas.hh, StaticObj.VCENTER_LEFT);
            } else {
                var1.gameAA(imgMap, 0, 0, 0);
            }

            if (TileMap.mapID < TileMap.mapNames.length && TileMap.mapID >= 0) {
                var3 = 0;
                if (x[TileMap.mapID] != 1 || y[TileMap.mapID] != 1) {
                    var3 = x[TileMap.mapID] < 100 ? 0 : (x[TileMap.mapID] > 200 ? 1 : 2);
                    GameCanvas.gameAA(x[TileMap.mapID] + dx, y[TileMap.mapID] + dy, var1, false);
                }

                var5 = 0;
                if (taskmapId >= 0) {
                    var3 = x[taskmapId] < 100 ? 0 : (x[taskmapId] > 200 ? 1 : 2);
                    mFont.tahoma_7_white.gameAA(var1, TileMap.mapNames[taskmapId], x[taskmapId] + dx, y[taskmapId] + dy - 20, var3, mFont.tahoma_7_grey);
                    var1.gameAA(imgX, x[taskmapId] + dx, y[taskmapId] + dy, 3);
                    var2 = true;
                    var5 = y[taskmapId] - 20;
                } else if (x[TileMap.mapID] != 1 || y[TileMap.mapID] != 1) {
                    var5 = y[TileMap.mapID] - 20;
                    mFont.tahoma_7_yellow.gameAA(var1, TileMap.mapNames[TileMap.mapID], x[TileMap.mapID] + dx, y[TileMap.mapID] + dy - 20, var3, mFont.tahoma_7_grey);
                }

                for (var11 = 0; var11 < Char.getMyChar().taskOrders.size(); ++var11) {
                    TaskOrder var10;
                    if ((var10 = (TaskOrder) Char.getMyChar().taskOrders.elementAt(var11)).mapId >= 0 && var10.mapId < x.length) {
                        var1.gameAA(imgX, x[var10.mapId] + dx, y[var10.mapId] + dy, 3);
                    }
                }

                if (mpoint >= 0 && (taskmapId < 0 && TileMap.mapID != mpoint || taskmapId >= 0 && mpoint != taskmapId)) {
                    var3 = x[mpoint] < 100 ? 0 : (x[mpoint] > 200 ? 1 : 2);
                    var11 = x[mpoint];
                    if ((var4 = y[mpoint] - 20) > var5 && var4 - var5 < 30) {
                        var4 += 40;
                    }

                    if (var4 < var5 && var5 - var4 < 20) {
                        var4 -= 5;
                    }

                    mFont.tahoma_7_yellow.gameAA(var1, TileMap.mapNames[mpoint], var11 + dx, var4 + dy, var3, mFont.tahoma_7_grey);
                }
            }

            var1.gameAA(imgPointer, mfx - 2, mfy, 0);
            var1.gameAA(-var1.gameAA(), -var1.gameAB());
            super.paint(var1);
        }

        if (mpoint > 0) {
            int var13 = var2 ? 18 : 5;
            mFont.tahoma_7_yellow.gameAA(var1, "Map ID: " + mpoint, 5, var13, 0, mFont.tahoma_7_grey);
        }
        if (var2) {
            var1.gameAA(imgX, 10, 10, 3);
            mFont.tahoma_7_white.gameAA(var1, mResources.gameKX, 20, 5, 0);
        }

    }

    public final void setOffset() {
        super.setOffset();
        if (++tick3 > 10000) {
            tick3 = 0;
        }

        if (mcmx != mcmtoX || mcmy != mcmtoY) {
            mcmvx = mcmtoX - mcmx << 1;
            mcmvy = mcmtoY - mcmy << 1;
            mcmdx += mcmvx;
            mcmx += mcmdx >> 4;
            mcmdx &= 15;
            mcmdy += mcmvy;
            mcmy += mcmdy >> 4;
            mcmdy &= 15;
            if (mcmx < 0) {
                mcmx = 0;
            }

            if (mcmx > mcmxLim) {
                mcmx = mcmxLim;
            }

            if (mcmy < 0) {
                mcmy = 0;
            }

            if (mcmy > mcmyLim) {
                mcmy = mcmyLim;
            }
        }

        boolean var1 = false;
        if (GameCanvas.keyHold[2]) {
            if ((mfy -= 4) < dy - 10) {
                mfy = dy - 10;
            }

            var1 = true;
        }

        if (GameCanvas.keyHold[8]) {
            if ((mfy += 4) > this.maxPY) {
                mfy = this.maxPY;
            }

            var1 = true;
        }

        if (GameCanvas.keyHold[4]) {
            if ((mfx -= 4) < dx - 10) {
                mfx = dx - 10;
            }

            var1 = true;
        }

        if (GameCanvas.keyHold[6]) {
            if ((mfx += 4) > this.maxPX) {
                mfx = this.maxPX;
            }

            var1 = true;
        }

        if (var1) {
            mcmtoX = mfx - GameCanvas.hw;
            mcmtoY = mfy - GameCanvas.hh;
            gameAH();
        }

        GameScr.gI();
        taskmapId = GameScr.gameBE();
        if (GameCanvas.isPointerClick && GameCanvas.py < GameCanvas.h - mScreen.cmdH) {
            GameCanvas.isPointerClick = false;
            this.trans = true;
            this.lastX = GameCanvas.px;
            this.lastY = GameCanvas.py;
        } else if (GameCanvas.isPointerDown && this.trans) {
            mcmtoX -= GameCanvas.px - this.lastX;
            mcmtoY -= GameCanvas.py - this.lastY;
            if (mcmtoX < 0) {
                mcmtoX = 0;
            }

            if (mcmtoY < 0) {
                mcmtoY = 0;
            }

            if (mcmtoX > mcmxLim) {
                mcmtoX = mcmxLim;
            }

            if (mcmtoY > mcmyLim) {
                mcmtoY = mcmyLim;
            }

            mcmx = mcmtoX;
            mcmy = mcmtoY;
            this.lastX = GameCanvas.px;
            this.lastY = GameCanvas.py;
        }

        if (GameCanvas.isPointerJustRelease) {
            int var3 = GameCanvas.pxLast - GameCanvas.px;
            int var2 = GameCanvas.pyLast - GameCanvas.py;
            if (var3 < 10 && var2 < 10) {
                mfx = mcmx + GameCanvas.pxLast - 8;
                mfy = mcmy + GameCanvas.pyLast - 8;
                gameAH();
            }

            this.trans = false;
            GameCanvas.isPointerJustRelease = false;
        }

        if (GameCanvas.isTouch && GameCanvas.w >= 320) {
            super.left.x = GameCanvas.w / 2 - 160;
            super.center.x = GameCanvas.w / 2 - 35;
            super.right.x = GameCanvas.w / 2 + 88;
            super.left.y = super.center.y = super.right.y = GameCanvas.h - 26;
        }

    }

    private static void gameAH() {
        lastpoint = mpoint;
        mpoint = -1;

        for (int var0 = 0; var0 < x.length; ++var0) {
            if (Res.abs(mfx - (x[var0] + dx)) < 10 && Res.abs(mfy - (y[var0] + dy)) < 10) {
                mpoint = var0;
                if (lastpoint != -1 && lastpoint == mpoint) {
                    Service.gI().adminMove(mpoint);
                    lastpoint = -1;
                    mpoint = -1;
                    return;
                }
                break;
            }
        }

    }

    public final void perform(int var1, Object var2) {
        switch (var1) {
            case 1000:
                if (Char.getMyChar().cHP <= 0 || Char.getMyChar().statusMe == 14) {
                    super.center = GameScr.gI().cmdDead;
                    Char.getMyChar().cHP = 0;
                }

                GameScr.gI().update();
                return;
            case 1001:
                this.modeCurrentMap = !this.modeCurrentMap;
                this.gameAG();
                return;
            case 14004:
                TileMap.fieldAL(mpoint);
            default:
        }
    }
}
