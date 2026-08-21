package javax.microedition.lcdui;

public class Graphics {
    public static final int HCENTER = 1;
    public static final int VCENTER = 2;
    public static final int LEFT = 4;
    public static final int RIGHT = 8;
    public static final int TOP = 16;
    public static final int BOTTOM = 32;

    private int translateX;
    private int translateY;

    public void drawImage(Image image, int x, int y, int anchor) {
    }

    public void drawRegion(Image src, int x, int y, int width, int height, int transform, int destX, int destY, int anchor) {
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
    }

    public void drawRect(int x, int y, int width, int height) {
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    }

    public void fillRect(int x, int y, int width, int height) {
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    }

    public void setClip(int x, int y, int width, int height) {
    }

    public void setColor(int rgb) {
    }

    public void translate(int x, int y) {
        translateX += x;
        translateY += y;
    }

    public int getTranslateX() {
        return translateX;
    }

    public int getTranslateY() {
        return translateY;
    }
}
