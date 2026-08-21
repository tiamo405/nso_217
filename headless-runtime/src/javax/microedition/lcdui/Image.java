package javax.microedition.lcdui;

import java.io.IOException;
import java.io.InputStream;

public final class Image {
    private final int width;
    private final int height;

    private Image(int width, int height) {
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
    }

    public static Image createImage(int width, int height) {
        return new Image(width, height);
    }

    public static Image createImage(String path) throws IOException {
        return new Image(1, 1);
    }

    public static Image createImage(byte[] imageData, int imageOffset, int imageLength) {
        return new Image(1, 1);
    }

    public static Image createImage(Image source, int x, int y, int width, int height, int transform) {
        return new Image(width, height);
    }

    public static Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha) {
        return new Image(width, height);
    }

    public static Image createImage(InputStream stream) throws IOException {
        return new Image(1, 1);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Graphics getGraphics() {
        return new Graphics();
    }
}
