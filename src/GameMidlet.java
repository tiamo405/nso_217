
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

public class GameMidlet extends MIDlet {

    public static byte CLIENT_TYPE = 1;
    public static byte indexClient = 0;
    public static int PORT = 14444;
    public static String IP = "";
    public static byte userProvider = 0;
    public static String clientAgent;
    public static GameMidlet instance;
    public static byte serverLogin = 0;

    public GameMidlet() {
        Session_ME.gI().setHandler(Controller.gI());
        instance = this;
        //UpdateServer.b1();// nhập ip
        UpdateServer.a();// nhận qua https
        clientAgent = this.readFileText("agent.txt");
        userProvider = Byte.parseByte(this.readFileText("provider.txt"));
        System.out.println("AGENT: " + clientAgent + ", PROVIDER: " + userProvider);
        SplashScr.splashScrStat = 0;
        GameCanvas.currentScreen = new SplashScr();
        MotherCanvas.gI().tCanvas = GameCanvas.gameAA();

    }

    protected void destroyApp(boolean var1) {
    }

    protected void pauseApp() {
    }

    protected void startApp() {
        if (!MotherCanvas.AC) {
            (new Thread(MotherCanvas.gI())).start();
        }
        Display.getDisplay(this).setCurrent(MotherCanvas.gI());
        AccountAutoManager.start();
    }

    public static void gameAA(String var0) {
        if (!var0.equals("")) {
            try {
                instance.platformRequest(var0);
                instance.notifyDestroyed();
            } catch (ConnectionNotFoundException var1) {
                var1.printStackTrace();
            }
        }
    }

    private String readFileText(String var1) {
        InputStream var5 = this.getClass().getResourceAsStream("/" + var1);

        String var3;
        try {
            byte[] var4 = new byte[var5.available()];
            var5.read(var4);
            var3 = new String(var4, "UTF-8");
        } catch (Exception var2) {
            var3 = "";
        }

        return var3;
    }

}
