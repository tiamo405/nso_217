public final class HeadlessMain {
    private HeadlessMain() {
    }

    public static void main(String[] args) throws Exception {
        if (System.getProperty("microedition.platform") == null) {
            System.setProperty("microedition.platform", "NSOHeadless");
        }

        GameMidlet midlet = new GameMidlet();
        midlet.startApp();

        while (true) {
            Thread.sleep(60000L);
        }
    }
}
