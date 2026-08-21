
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class mGraphics {

    public Graphics gameAA;
    public static int zoomLevel = 1;
    public static int HCENTER = 1;

    public static int VCENTER = 2;

    public static int LEFT = 4;

    public static int RIGHT = 8;

    public static int TOP = 16;

    public static int BOTTOM = 32;

    public mGraphics(Graphics var1) {
        this.gameAA = var1;
    }

    public mGraphics() {
    }

    public final void gameAA(Image var1, int var2, int var3, int var4) {
        var2 *= zoomLevel;
        var3 *= zoomLevel;
        this.gameAA.drawImage(var1, var2, var3, var4);
    }

    public final void fieldAE(int var1, int var2, int var3, int var4) {
        var1 *= zoomLevel;
        var2 *= zoomLevel;
        var3 *= zoomLevel;
        var4 *= zoomLevel;
        this.gameAA.drawArc(var1, var2, var3, var4, 0, 360);
    }

    public final void gameAA(int var1, int var2, int var3, int var4) {
        var1 *= zoomLevel;
        var2 *= zoomLevel;
        var3 *= zoomLevel;
        var4 *= zoomLevel;
        this.gameAA.drawLine(var1, var2, var3, var4);
    }

    public final void gameAB(int var1, int var2, int var3, int var4) {
        var1 *= zoomLevel;
        var2 *= zoomLevel;
        var3 *= zoomLevel;
        var4 *= zoomLevel;
        this.gameAA.drawRect(var1, var2, var3, var4);
    }

    public final void gameAA(Image var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
        var7 *= zoomLevel;
        var8 *= zoomLevel;
        var2 *= zoomLevel;
        var3 *= zoomLevel;
        var4 *= zoomLevel;
        var5 *= zoomLevel;
        this.gameAA.drawRegion(var1, var2, var3, var4, var5, var6, var7, var8, var9);
    }

    public final void gameAA(int var1, int var2, int var3, int var4, int var5, int var6) {
        var1 *= zoomLevel;
        var2 *= zoomLevel;
        var3 *= zoomLevel;
        var4 *= zoomLevel;
        var5 *= zoomLevel;
        var6 *= zoomLevel;
        this.gameAA.drawRoundRect(var1, var2, var3, var4, var5, var6);
    }

    public final void gameAC(int var1, int var2, int var3, int var4) {
        var1 *= zoomLevel;
        var2 *= zoomLevel;
        var3 *= zoomLevel;
        var4 *= zoomLevel;
        this.gameAA.fillRect(var1, var2, var3, var4);
    }

    public void drawRect(int x, int y, int w, int h) {
        int num = 1;
        this.gameAA.fillRect(x, y, w, num);
        this.gameAA.fillRect(x, y, num, h);
        this.gameAA.fillRect(x + w, y, num, h + 1);
        this.gameAA.fillRect(x, y + h, w + 1, num);
    }

    public final void gameAB(int var1, int var2, int var3, int var4, int var5, int var6) {
        var1 *= zoomLevel;
        var2 *= zoomLevel;
        var3 *= zoomLevel;
        var4 *= zoomLevel;
        var5 *= zoomLevel;
        var6 *= zoomLevel;
        this.gameAA.fillRoundRect(var1, var2, var3, var4, var5, var6);
    }

    public final int gameAA() {
        return this.gameAA.getTranslateX() / zoomLevel;
    }

    public final int gameAB() {
        return this.gameAA.getTranslateY() / zoomLevel;
    }

    public final void gameAD(int var1, int var2, int var3, int var4) {
        var1 *= zoomLevel;
        var2 *= zoomLevel;
        var3 *= zoomLevel;
        var4 *= zoomLevel;
        this.gameAA.setClip(var1, var2, var3, var4);
    }

    public final void gameAA(int var1) {
        this.gameAA.setColor(var1);
    }

    public final void gameAA(int var1, int var2) {
        var1 *= zoomLevel;
        var2 *= zoomLevel;
        this.gameAA.translate(var1, var2);
    }

    public static int gameAA(Image var0) {
        return var0.getWidth() / zoomLevel;
    }

    public static int gameAB(Image var0) {
        return var0.getHeight() / zoomLevel;
    }
}
