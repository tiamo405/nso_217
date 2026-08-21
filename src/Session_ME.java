
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;

public final class Session_ME {

    protected static Session_ME instance = new Session_ME();
    public boolean gameAB = false;
    public DataOutputStream dos;
    public DataInputStream dis;
    public Controller messageHandler;
    public SocketConnection fieldAE;
    public boolean connected;
    public boolean connecting;
    private final Sender sender = new Sender(this);
    public Thread fieldAS;
    public Thread gameAI;
    public int sendByteCount;
    public int recvByteCount;
    boolean getKeyComplete;
    public byte[] key = null;
    private byte curR;
    private byte curW;
    long gameAN;
    public String gameAO = "";
    public static boolean gameAP;
    private MyVector recieveMsg;
    public static String fieldAM;
    public static int fieldAN;
    public static byte fieldAO;
    private static Object fieldBA = new Object();

    public static Session_ME gI() {
        return instance;
    }

    public final boolean gameAB() {
        return this.connected;
    }

    public final void setHandler(Controller var1) {
        this.messageHandler = var1;
    }

    public final void gameAA(String var1) {
        if (!this.connected && !this.connecting) {
            this.getKeyComplete = false;
            this.fieldAE = null;
            fieldAM = var1;

            fieldAO = GameMidlet.serverLogin;

            this.fieldAS = new Thread(new NetworkConnector(this, var1));
            SendSMS.gameAA();
        }
    }

    public final void gameAA1(String var1) {
        if (!this.connected && !this.connecting) {
            this.getKeyComplete = false;
            this.fieldAE = null;
            fieldAM = var1;

            fieldAO = GameMidlet.serverLogin;

            this.fieldAS = new Thread(new NetworkConnector(this, var1));
            SendSMS.gameAA();
        }
    }

    public final void gameAA2(String var1) {
        if (!this.connected && !this.connecting) {
            this.getKeyComplete = false;
            this.fieldAE = null;
            fieldAM = var1;

            fieldAO = GameMidlet.serverLogin;

            this.fieldAS = new Thread(new NetworkConnector(this, var1));
            SendSMS.gameAA();
        }
    }

    public final void gameAA3(String var1) {
        if (!this.connected && !this.connecting) {
            this.getKeyComplete = false;
            this.fieldAE = null;
            fieldAM = var1;

            fieldAO = GameMidlet.serverLogin;

            this.fieldAS = new Thread(new NetworkConnector(this, var1));
            SendSMS.gameAA();
        }
    }

    public final void gameAA4(String var1) {
        if (!this.connected && !this.connecting) {
            this.getKeyComplete = false;
            this.fieldAE = null;
            fieldAM = var1;

            fieldAO = GameMidlet.serverLogin;

            this.fieldAS = new Thread(new NetworkConnector(this, var1));
            SendSMS.gameAA();
        }
    }

    public final void gameAA5(String var1) {
        if (!this.connected && !this.connecting) {
            this.getKeyComplete = false;
            this.fieldAE = null;
            fieldAM = var1;

            fieldAO = GameMidlet.serverLogin;

            this.fieldAS = new Thread(new NetworkConnector(this, var1));
            SendSMS.gameAA();
        }
    }

    public final void gameAA6(String var1) {
        if (!this.connected && !this.connecting) {
            this.getKeyComplete = false;
            this.fieldAE = null;
            fieldAM = var1;

            fieldAO = GameMidlet.serverLogin;

            this.fieldAS = new Thread(new NetworkConnector(this, var1));
            SendSMS.gameAA();
        }
    }

    public final void gameAA8(String var1) {
        if (!this.connected && !this.connecting) {
            this.getKeyComplete = false;
            this.fieldAE = null;
            fieldAM = var1;

            fieldAO = GameMidlet.serverLogin;

            this.fieldAS = new Thread(new NetworkConnector(this, var1));
            SendSMS.gameAA();
        }
    }

    public final void gameAA9(String var1) {
        if (!this.connected && !this.connecting) {
            this.getKeyComplete = false;
            this.fieldAE = null;
            fieldAM = var1;

            fieldAO = GameMidlet.serverLogin;

            this.fieldAS = new Thread(new NetworkConnector(this, var1));
            SendSMS.gameAA();
        }
    }

    public final void gameAA11(String var1, int var2) {
        String var3 = new String(new char[]{'s', 'o', 'c', 'k', 'e', 't', ':', '/', '/'}) + var1 + new String(new char[]{':'}) + var2;
        if (GameCanvas.gameCE) {
            if (!GameCanvas.isGPRS) {
                var3 = var3 + new String(new char[]{';', 'i', 'n', 't', 'e', 'r', 'f', 'a', 'c', 'e', '=', 'w', 'i', 'f', 'i'});
            } else {
                var3 = var3 + new String(new char[]{';', 'd', 'e', 'v', 'i', 'c', 'e', 's', 'i', 'd', 'e', '=', 't', 'r', 'u', 'e'});
            }
        }
        //System.out.println(new String(new char[]{'c', 'o', 'n', 'n', 'e', 'c', 't', ' ', 't', 'o', ' ', ' '}) + var3 + new String(new char[]{' ', ':', ' ', ' '}) + +GameMidlet.serverLogin);
        if (!this.connected && !this.connecting) {
            this.getKeyComplete = false;
            this.fieldAE = null;
            fieldAM = var1;
            fieldAN = var2;
            fieldAO = GameMidlet.serverLogin;
            this.fieldAS = new Thread(new NetworkConnector1(this, var3));
            SendSMS.gameAA();
        }

    }

    public final void gameAA10(String var1) {
        if (!this.connected && !this.connecting) {
            this.getKeyComplete = false;
            this.fieldAE = null;
            fieldAM = var1;

            fieldAO = GameMidlet.serverLogin;

            this.fieldAS = new Thread(new NetworkConnector(this, var1));
            SendSMS.gameAA();
        }
    }

    public final void gameAA7(String var1) {
        if (!this.connected && !this.connecting) {
            this.getKeyComplete = false;
            this.fieldAE = null;
            fieldAM = var1;

            fieldAO = GameMidlet.serverLogin;

            this.fieldAS = new Thread(new NetworkConnector(this, var1));
            SendSMS.gameAA();
        }
    }

    public final void sendMessage(Message var1) {
        this.sender.gameAA.recieveMsg.addElement(var1);
    }

    private synchronized void gameAB(Message var1) {
        byte[] var2 = var1.getData();

        try {
            if (this.getKeyComplete) {
                byte var3 = this.gameAA(var1.command);
                this.dos.writeByte(var3);
            } else {
                this.dos.writeByte(var1.command);
            }

            (new StringBuffer("cmd send ---> ")).append(var1.command).toString();
            if (var2 != null) {
                int var7 = var2.length;
                if (var1.command != -31 && this.getKeyComplete) {
                    byte var5 = this.gameAA((byte) (var7 >> 8));
                    this.dos.writeByte(var5);
                    var5 = this.gameAA((byte) var7);
                    this.dos.writeByte(var5);
                } else {
                    this.dos.writeShort(var7);
                }

                if (this.getKeyComplete) {
                    for (int var6 = 0; var6 < var2.length; ++var6) {
                        var2[var6] = this.gameAA(var2[var6]);
                    }
                }

                this.dos.write(var2);
                this.sendByteCount += 5 + var2.length;
            } else {
                this.dos.writeShort(0);
                this.sendByteCount += 5;
            }

            this.dos.flush();
        } catch (IOException var4) {
            var4.printStackTrace();
        }
    }

    final class ConnectionMonitor1 implements Runnable {

        public final void run() {
            try {
                Thread.sleep(20000L);
            } catch (InterruptedException var2) {
            }

            if (Session_ME.instance.connecting) {
                try {
                    Session_ME.instance.fieldAE.close();
                } catch (Exception var1) {
                }

                Session_ME.gameAP = true;
                Session_ME.instance.connecting = false;
                Session_ME.instance.connected = false;
                Session_ME.instance.messageHandler.gameAB();
            }

        }
    }

    final class NetworkConnector1 implements Runnable {

        private final String gameAA;
        private Session_ME gameAB;

        NetworkConnector1(Session_ME var1, String var2) {
            this.gameAB = var1;
            this.gameAA = var2;
        }

        public final void run() {

            Session_ME.gameAP = false;
            (new Thread(new ConnectionMonitor1())).start();
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

    private byte gameAA(byte var1) {
        byte[] var10000 = this.key;
        byte var10003 = this.curW;
        this.curW = (byte) (var10003 + 1);
        var1 = (byte) (var10000[var10003] & 255 ^ var1 & 255);
        if (this.curW >= this.key.length) {
            this.curW = (byte) (this.curW % this.key.length);
        }

        return var1;
    }

    public final void gameAC() {
        Code.fieldAB();

        this.gameAD();
    }

    public final void gameAD() {
        this.key = null;
        this.curR = 0;
        this.curW = 0;

        try {
            this.connected = false;
            this.connecting = false;
            if (this.fieldAE != null) {
                this.fieldAE.close();
                this.fieldAE = null;
            }

            if (this.dos != null) {
                this.dos.close();
                this.dos = null;
            }

            if (this.dis != null) {
                this.dis.close();
                this.dis = null;
            }

            this.gameAI = null;
            System.gc();
        } catch (Exception var1) {
            var1.printStackTrace();
        }
    }

    static void gameAA(Session_ME var0, DataOutputStream var1) {
        var0.dos = var1;
    }

    static Sender gameAA(Session_ME var0) {
        return var0.sender;
    }

    static void gameAA(Session_ME var0, Message var1) {
        var0.gameAB(var1);
    }

    static void gameAA(Session_ME var0, MyVector var1) {
        var0.recieveMsg = var1;
    }

    static MyVector gameAB(Session_ME var0) {
        return var0.recieveMsg;
    }

    static void gameAC(Session_ME var0) {
        var0.gameAD();
    }

    static byte gameAA(Session_ME var0, byte var1) {
        byte[] var10000 = (var0 = var0).key;
        byte var10003 = var0.curR;
        var0.curR = (byte) (var10003 + 1);
        var1 = (byte) (var10000[var10003] & 255 ^ var1 & 255);
        if (var0.curR >= var0.key.length) {
            var0.curR = (byte) (var0.curR % var0.key.length);
        }

        return var1;
    }

    public final void fieldAD() {
        if (GameCanvas.currentScreen != GameCanvas.selectsvScr) {
            GameCanvas.instance.fieldAE();
        }

        if (gameAP) {
            fieldAE();
        } else {
            gameAP = true;
            (new Thread(new ReLogin(this))).start();
        }
    }

    public static void fieldAE() {
        synchronized (fieldBA) {
            fieldBA.notifyAll();
        }
    }

    static void fieldAA(long var0) {
        long var2 = var0;
        synchronized (fieldBA) {
            try {
                fieldBA.wait(var2);
            } catch (Exception var4) {
            }

        }
    }
}
