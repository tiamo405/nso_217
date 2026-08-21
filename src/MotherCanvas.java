
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public final class MotherCanvas extends Canvas implements Runnable {

    public static MotherCanvas instance;
    public GameGraphics tCanvas;
    private int zoomLevel = 1;
    private int newWidth;
    private int newHeight;
    private static boolean isPC = true;
    public static boolean AC;

    public static MotherCanvas gI() {
        if (instance == null) {
            instance = new MotherCanvas();
        }

        return instance;
    }

    public final void run() {
        try {
            Thread.sleep(10L);
        } catch (InterruptedException var9) {
        }

        AC = true;

        while (AC) {
            try {
                long var1 = System.currentTimeMillis();
                this.tCanvas.AF();
                long var3 = System.currentTimeMillis() - var1;

                try {
                    Thread.sleep(var3 < (long) Code.speedGame ? (long) Code.speedGame - var3 : 1L);
                } catch (InterruptedException var6) {
                }
            } catch (Exception var12) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException var7) {
                    var7.printStackTrace();
                }

                var12.printStackTrace();
            }
        }
    }

    public MotherCanvas() {
        this.setFullScreenMode(true);
        mGraphics.zoomLevel = 1;
        if (!isPC) {
            if (this.getWidth() * this.getHeight() > 2073600) {
                mGraphics.zoomLevel = this.zoomLevel = 4;
                this.newWidth = this.getWidth() / this.zoomLevel + 2;
                this.newHeight = this.getHeight() / this.zoomLevel + 2;
                return;
            }

            if (this.getWidth() * this.getHeight() > 153600) {
                mGraphics.zoomLevel = this.zoomLevel = 3;
                this.newWidth = this.getWidth() / this.zoomLevel + 2;
                this.newHeight = this.getHeight() / this.zoomLevel + 2;
                return;
            }

            mGraphics.zoomLevel = this.zoomLevel = 1;
        }

    }

    public final void gameAA(GameGraphics var1) {
        this.tCanvas = var1;
    }

    protected final void paint(Graphics var1) {
        this.tCanvas.gameAA(var1);
    }

    protected final void keyPressed(int var1) {
        this.tCanvas.setIputType(var1);
    }

    protected final void keyReleased(int var1) {
        this.tCanvas.gameAD(var1);
    }

    protected final void pointerDragged(int var1, int var2) {
        var1 /= this.zoomLevel;
        var2 /= this.zoomLevel;
        this.tCanvas.gameAA(var1, var2);
    }

    protected final void pointerPressed(int var1, int var2) {
        var1 /= this.zoomLevel;
        var2 /= this.zoomLevel;
        this.tCanvas.gameAB(var1, var2);
    }

    protected final void pointerReleased(int var1, int var2) {
        var1 /= this.zoomLevel;
        var2 /= this.zoomLevel;
        this.tCanvas.gameAC(var1, var2);
    }

    public final int gameAA() {
        return this.zoomLevel == 1 ? this.getHeight() : this.newHeight;
    }

    public final int gameAB() {
        return this.zoomLevel == 1 ? this.getWidth() : this.newWidth;
    }
}
