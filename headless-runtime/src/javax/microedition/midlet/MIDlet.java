package javax.microedition.midlet;

import javax.microedition.io.ConnectionNotFoundException;

public abstract class MIDlet {
    protected abstract void startApp();

    protected abstract void pauseApp();

    protected abstract void destroyApp(boolean unconditional);

    public final void notifyDestroyed() {
        try {
            destroyApp(true);
        } catch (Throwable ignored) {
        }
        System.exit(0);
    }

    public boolean platformRequest(String url) throws ConnectionNotFoundException {
        return false;
    }
}
