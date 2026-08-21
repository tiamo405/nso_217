
public final class SplashScr extends mScreen {

    public static int splashScrStat;

    public final void gameAD() {
        if (splashScrStat++ > 5) {
            if (RMS.gameAC("indLanguage") <= 0) {
                GameCanvas.gameAB();
                GameCanvas.selectsvScr.update();
                return;
            }

            GameCanvas.gameAB();
            GameCanvas.languageScr.update();
        }
    }

}
