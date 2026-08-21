
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;

final class NetworkConnector implements Runnable {

    private final String gameAA;
    private Session_ME gameAB;

    NetworkConnector(Session_ME var1, String var2) {
        this.gameAB = var1;
        this.gameAA = var2;
    }

    public final void run() {

        Session_ME.gameAP = false;
        (new Thread(new ConnectionMonitor())).start();
        this.gameAB.connecting = true;
        Thread.currentThread().setPriority(1);
        this.gameAB.connected = true;

        try {
            String var2 = this.gameAA;
            this.gameAB.fieldAE = (SocketConnection) Connector.open(var2);
            Session_ME.gameAA(this.gameAB, this.gameAB.fieldAE.openDataOutputStream());
            this.gameAB.dis = this.gameAB.fieldAE.openDataInputStream();
            (new Thread(Session_ME.gameAA(this.gameAB))).start();
            this.gameAB.gameAI = new Thread(new MessageCollector(this.gameAB));
            this.gameAB.gameAI.start();
            this.gameAB.gameAN = System.currentTimeMillis();
            Session_ME.gameAA(this.gameAB, new Message((byte) -27));
            this.gameAB.connecting = false;
            Controller var1 = this.gameAB.messageHandler;
        } catch (Exception var4) {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException var3) {
            }

            if (!Session_ME.gameAP) {
                if (this.gameAB.messageHandler != null) {
                    this.gameAB.gameAC();
                    this.gameAB.messageHandler.gameAB();
                }

            }
        }
    }
}
