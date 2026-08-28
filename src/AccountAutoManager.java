import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Vector;

/** Sequential account/character runner for daily missions. */
public final class AccountAutoManager implements Runnable {
    private static final int MAX_CONNECT_RETRIES = 3;
    private static final long CONNECT_RETRY_DELAY = 10000L;
    private static final Vector usernames = new Vector();
    private static final Vector passwords = new Vector();
    private static int accountIndex;
    private static int characterIndex;
    private static String[] characterNames;
    private static boolean enabled;
    private static boolean started;
    private static boolean switching;
    private static boolean waitingForGame;
    private static boolean enteringCave;
    private static boolean postDailyProcessing;
    private static boolean reconnecting;
    private static int disconnectRetryCount;
    private static int connectRetryCount;

    private AccountAutoManager() {
    }

    public static synchronized void start() {
        if (started) {
            return;
        }
        started = true;
        loadAccounts();
        if (usernames.size() == 0) {
            System.out.println("AUTO NVHN: account.csv không có tài khoản, bỏ qua tự đăng nhập.");
            finishAll();
            return;
        }
        enabled = true;
        accountIndex = 0;
        characterIndex = 0;
        switching = true;
        reconnecting = false;
        disconnectRetryCount = 0;
        connectRetryCount = 0;
        (new Thread(new AccountAutoManager())).start();
    }

    private static void loadAccounts() {
        InputStream input = null;
        try {
            input = AccountAutoManager.class.getResourceAsStream("/account.csv");
            if (input == null) {
                System.out.println("AUTO NVHN: không tìm thấy account.csv trong JAR.");
                return;
            }
            byte[] bytes = new byte[input.available()];
            int offset = 0;
            while (offset < bytes.length) {
                int read = input.read(bytes, offset, bytes.length - offset);
                if (read < 0) {
                    break;
                }
                offset += read;
            }
            String content = new String(bytes, 0, offset, "UTF-8");
            int start = 0;
            while (start <= content.length()) {
                int end = content.indexOf('\n', start);
                if (end < 0) {
                    end = content.length();
                }
                addAccount(content.substring(start, end).trim());
                if (end == content.length()) {
                    break;
                }
                start = end + 1;
            }
        } catch (Exception ex) {
            System.out.println("AUTO NVHN: lỗi đọc account.csv: " + ex.toString());
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    private static void addAccount(String line) {
        if (line.length() == 0 || line.charAt(0) == '#') {
            return;
        }
        int comma = line.indexOf(',');
        if (comma <= 0 || comma == line.length() - 1) {
            System.out.println("AUTO NVHN: bỏ qua dòng account.csv sai định dạng.");
            return;
        }
        String username = line.substring(0, comma).trim();
        String password = line.substring(comma + 1).trim();
        if (username.equalsIgnoreCase("username") && password.equalsIgnoreCase("password")) {
            return;
        }
        if (username.length() > 0 && password.length() > 0) {
            usernames.addElement(username);
            passwords.addElement(password);
        }
    }

    public void run() {
        try {
            Thread.sleep(3000L);
            loginCurrentAccount();
        } catch (InterruptedException ignored) {
        }
    }

    private static void loginCurrentAccount() {
        if (!enabled || accountIndex >= usernames.size()) {
            finishAll();
            return;
        }
        String username = (String) usernames.elementAt(accountIndex);
        String password = (String) passwords.elementAt(accountIndex);
        SelectServerScr.uname = username;
        SelectServerScr.pass = password;
        SelectServerScr.unameChange = "";
        SelectServerScr.passChange = "";
        GameMidlet.IP = UpdateServer.listIP[0];
        GameMidlet.PORT = UpdateServer.listPort[0];
        GameMidlet.serverLogin = UpdateServer.serverLoginList[0];
        characterNames = null;
        waitingForGame = false;
        enteringCave = false;
        postDailyProcessing = false;

        System.out.println("AUTO NVHN: đăng nhập tài khoản " + username + " (" + (accountIndex + 1) + "/" + usernames.size() + ")");
        Session_ME session = Session_ME.gI();
        session.gameAC();
        session.gameAA11(GameMidlet.IP, GameMidlet.PORT);
        long deadline = System.currentTimeMillis() + 20000L;
        while ((!session.connected || !session.getKeyComplete) && System.currentTimeMillis() < deadline) {
            sleep(100L);
        }
        if (!session.connected || !session.getKeyComplete) {
            session.gameAC();
            int retryNumber = ++connectRetryCount;
            if (retryNumber <= MAX_CONNECT_RETRIES) {
                System.out.println("AUTO NVHN: kết nối thất bại, thử lại tài khoản " + username
                        + " sau " + (CONNECT_RETRY_DELAY / 1000L) + " giây (lần "
                        + retryNumber + "/" + MAX_CONNECT_RETRIES + ").");
                sleep(CONNECT_RETRY_DELAY);
                loginCurrentAccount();
                return;
            }
            System.out.println("AUTO NVHN: kết nối tài khoản " + username + " vẫn thất bại sau "
                    + MAX_CONNECT_RETRIES + " lần thử lại, chuyển tài khoản tiếp theo.");
            connectRetryCount = 0;
            nextAccount();
            return;
        }
        connectRetryCount = 0;
        Service.gI().login(username, password, "2.1.7");
    }

    public static synchronized void onCharacterList(String[] names) {
        if (!enabled) {
            return;
        }
        reconnecting = false;
        disconnectRetryCount = 0;
        characterNames = names;
        while (characterIndex < characterNames.length
                && (characterNames[characterIndex] == null || characterNames[characterIndex].length() == 0)) {
            characterIndex++;
        }
        if (characterIndex >= characterNames.length) {
            switching = true;
            (new Thread(new Runnable() {
                public void run() {
                    nextAccount();
                }
            })).start();
            return;
        }
        String name = characterNames[characterIndex];
        SelectCharScr.fieldAK = name;
        waitingForGame = true;
        switching = false;
        System.out.println("AUTO NVHN: chọn nhân vật " + name);
        Service.gI().selectCharToPlay(name);
    }

    public static synchronized void onServerMessage(String message) {
        if (!enabled || !switching) {
            return;
        }
        System.out.println("AUTO NVHN: đăng nhập không thành công, chuyển tài khoản. " + message);
        (new Thread(new Runnable() {
            public void run() {
                sleep(1000L);
                nextAccount();
            }
        })).start();
    }

    public static synchronized void onGameReady() {
        if (!enabled || !waitingForGame) {
            return;
        }
        reconnecting = false;
        disconnectRetryCount = 0;
        waitingForGame = false;
        Char me = Char.getMyChar();
        if (me.clevel < 30) {
            skipCurrentCharacter("nhân vật " + me.cName + " level=" + me.clevel + " < 30, bỏ qua");
            return;
        }
        System.out.println("AUTO NVHN: chuẩn bị nhân vật " + me.cName);
        AutoPrepareNvhn prepare = new AutoPrepareNvhn();
        prepare.fieldAD();
        Code.fieldAA((Auto) prepare);
    }

    /** Reconnects the current account when an established socket is closed before/during play. */
    public static synchronized boolean onDisconnected() {
        if (!enabled) {
            return false;
        }
        if (reconnecting) {
            return true;
        }

        reconnecting = true;
        waitingForGame = false;
        enteringCave = false;
        int retryNumber = ++disconnectRetryCount;
        final long delay = Math.min(30000L, 5000L * retryNumber);
        System.out.println("AUTO LOGIN: mất kết nối, đăng nhập lại tài khoản hiện tại sau "
                + (delay / 1000L) + " giây (lần " + retryNumber + ")");

        (new Thread(new Runnable() {
            public void run() {
                sleep(delay);
                synchronized (AccountAutoManager.class) {
                    if (!enabled) {
                        reconnecting = false;
                        return;
                    }
                    reconnecting = false;
                }
                loginCurrentAccount();
            }
        })).start();
        return true;
    }

    public static synchronized void onCharacterBelowLevel30(String message) {
        if (!enabled || switching) {
            return;
        }
        skipCurrentCharacter("NPC báo chưa đạt cấp 30, bỏ qua nhân vật. " + message);
    }

    public static synchronized void onDailyTasksFinished() {
        if (!enabled || switching) {
            return;
        }
        startPostDailyActions();
    }

    /** Called immediately when NPC 25 says today's daily-task limit is exhausted. */
    public static synchronized void onDailyLimitReached() {
        if (!enabled || switching) {
            return;
        }
        startPostDailyActions();
    }

    private static void startPostDailyActions() {
        if (postDailyProcessing) {
            return;
        }
        postDailyProcessing = true;
        if (!Code.fieldAD.didDailyWorkThisRun()) {
            System.out.println("AUTO NVHN LAT HINH: nhân vật không làm nhiệm vụ nào trong lượt chạy này, bỏ qua lật thẻ");
            startCaveEntry();
            return;
        }
        System.out.println("AUTO NVHN: đã hết nhiệm vụ, bắt đầu lật thẻ trước khi đi hang.");
        AutoFlipNvhn flip = new AutoFlipNvhn();
        flip.fieldAD();
        Code.fieldAA((Auto) flip);
    }

    public static synchronized void onPostDailyFlipFinished() {
        if (!enabled || switching) {
            return;
        }
        startCaveEntry();
    }

    private static void startCaveEntry() {
        if (enteringCave) {
            return;
        }
        enteringCave = true;
        System.out.println("AUTO NVHN: nhân vật đã hết nhiệm vụ, bắt đầu vào hang trước khi đổi nhân vật.");
        AutoEnterCave cave = new AutoEnterCave();
        cave.fieldAD();
        Code.fieldAA((Auto) cave);
    }

    public static synchronized void onCaveEntered() {
        if (!enabled || switching) {
            return;
        }
        skipCurrentCharacter("đã xử lý hang động, chuyển nhân vật");
    }

    private static void skipCurrentCharacter(String reason) {
        switching = true;
        waitingForGame = false;
        enteringCave = false;
        postDailyProcessing = false;
        Code.fieldAG();
        System.out.println("AUTO NVHN: " + reason);
        (new Thread(new Runnable() {
            public void run() {
                sleep(1500L);
                advanceCharacterOrAccount();
            }
        })).start();
    }

    private static void advanceCharacterOrAccount() {
        characterIndex++;
        while (characterNames != null && characterIndex < characterNames.length
                && (characterNames[characterIndex] == null || characterNames[characterIndex].length() == 0)) {
            characterIndex++;
        }
        if (characterNames != null && characterIndex < characterNames.length) {
            loginCurrentAccount();
        } else {
            nextAccount();
        }
    }

    public static synchronized String getCurrentUsername() {
        if (!enabled || accountIndex < 0 || accountIndex >= usernames.size()) {
            return "-";
        }
        return (String) usernames.elementAt(accountIndex);
    }

    private static void nextAccount() {
        accountIndex++;
        characterIndex = 0;
        connectRetryCount = 0;
        switching = true;
        loginCurrentAccount();
    }

    private static void finishAll() {
        enabled = false;
        switching = false;
        reconnecting = false;
        System.out.println("AUTO NVHN: đã xử lý hết toàn bộ tài khoản và nhân vật, dừng worker.");
        markWorkerCompleted();
        Session_ME.gI().gameAC();
        sleep(500L);
        if (GameMidlet.instance != null) {
            GameMidlet.instance.notifyDestroyed();
        }
        System.exit(0);
    }

    private static void markWorkerCompleted() {
        FileOutputStream output = null;
        try {
            String userHome = System.getProperty("user.home");
            if (userHome == null || userHome.length() == 0) {
                return;
            }
            File marker = new File(userHome, "worker.done");
            output = new FileOutputStream(marker);
            output.write("completed\n".getBytes("UTF-8"));
            output.flush();
            System.out.println("AUTO NVHN: đã tạo marker hoàn tất " + marker.getAbsolutePath());
        } catch (Exception ex) {
            System.out.println("AUTO NVHN: không thể tạo marker hoàn tất: " + ex.toString());
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }
}
