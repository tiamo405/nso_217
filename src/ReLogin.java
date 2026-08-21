
import java.util.Calendar;

public final class ReLogin implements Runnable {

    private Session_ME fieldAA;

    ReLogin(Session_ME var1) {
        this.fieldAA = var1;
    }

    public final void run() {
        do {
            try {
                GameCanvas.gameAL();
                this.fieldAA.gameAC();
                Thread.sleep(5000L);
                this.fieldAA.gameAA11(Session_ME.fieldAM, Session_ME.fieldAN);
                GameMidlet.serverLogin = Session_ME.fieldAO;
                Service.gI().login(SelectServerScr.uname, SelectServerScr.pass, "2.1.7");
                Session_ME.fieldAA(10000L);
                Service.gI().selectCharToPlay(SelectCharScr.fieldAK);
                Session_ME.fieldAA(5000L);

            } catch (InterruptedException var4) {
                var4.printStackTrace();
            }
        } while (Session_ME.gameAP && GameCanvas.currentScreen != GameScr.instance);

    }
}
