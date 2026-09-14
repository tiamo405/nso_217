package javax.microedition.lcdui;

public abstract class Canvas extends Displayable {
    public static final int UP = 1;
    public static final int DOWN = 6;
    public static final int LEFT = 2;
    public static final int RIGHT = 5;
    public static final int FIRE = 8;
    public static final int GAME_A = 9;
    public static final int GAME_B = 10;
    public static final int GAME_C = 11;
    public static final int GAME_D = 12;

    public void setFullScreenMode(boolean mode) {
    }

    public int getWidth() {
        return 240;
    }

    public int getHeight() {
        return 320;
    }

    public int getGameAction(int keyCode) {
        return keyCode;
    }

    public void repaint() {
    }

    public void serviceRepaints() {
    }

    public boolean hasPointerEvents() {
        return false;
    }

    protected abstract void paint(Graphics graphics);

    protected void keyPressed(int keyCode) {
    }

    protected void keyReleased(int keyCode) {
    }

    protected void pointerDragged(int x, int y) {
    }

    protected void pointerPressed(int x, int y) {
    }

    protected void pointerReleased(int x, int y) {
    }
}
