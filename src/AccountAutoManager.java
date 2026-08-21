import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Vector;

/** Sequential account/character runner for daily missions. */
public final class AccountAutoManager implements Runnable {
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

        System.out.println("AUTO NVHN: đăng nhập tài khoản " + username + " (" + (accountIndex + 1) + "/" + usernames.size() + ")");
        Session_ME session = Session_ME.gI();
        session.gameAC();
        session.gameAA11(GameMidlet.IP, GameMidlet.PORT);
        long deadline = System.currentTimeMillis() + 20000L;
        while ((!session.connected || !session.getKeyComplete) && System.currentTimeMillis() < deadline) {
            sleep(100L);
        }
        if (!session.connected || !session.getKeyComplete) {
            System.out.println("AUTO NVHN: kết nối thất bại, chuyển tài khoản tiếp theo.");
            accountIndex++;
            characterIndex = 0;
            loginCurrentAccount();
            return;
        }
        Service.gI().login(username, password, "2.1.7");
    }

    public static synchronized void onCharacterList(String[] names) {
        if (!enabled) {
            return;
        }
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
        waitingForGame = false;
        System.out.println("AUTO NVHN: chuẩn bị nhân vật " + Char.getMyChar().cName);
        AutoPrepareNvhn prepare = new AutoPrepareNvhn();
        prepare.fieldAD();
        Code.fieldAA((Auto) prepare);
    }

    public static synchronized void onDailyTasksFinished() {
        if (!enabled || switching) {
            return;
        }
        startCaveEntry();
    }

    /** Called immediately when NPC 25 says today's daily-task limit is exhausted. */
    public static synchronized void onDailyLimitReached() {
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
        switching = true;
        enteringCave = false;
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
        switching = true;
        loginCurrentAccount();
    }

    private static void finishAll() {
        enabled = false;
        switching = false;
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
