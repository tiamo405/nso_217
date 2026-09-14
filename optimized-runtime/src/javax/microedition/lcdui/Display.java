package javax.microedition.lcdui;

import javax.microedition.midlet.MIDlet;

public final class Display {
    private static final Display INSTANCE = new Display();
    private Displayable current;

    private Display() {
    }

    public static Display getDisplay(MIDlet midlet) {
        return INSTANCE;
    }

    public void setCurrent(Displayable displayable) {
        this.current = displayable;
    }

    public Displayable getCurrent() {
        return current;
    }
}
