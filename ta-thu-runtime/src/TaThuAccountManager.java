import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Vector;

/** Sequential account/character runner dedicated to the four daily Ta Thu runs. */
public final class TaThuAccountManager implements Runnable {
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
    private static volatile boolean waitingForCharacters;
    private static boolean reconnecting;
    private static boolean postProcessing;
    private static int disconnectRetryCount;
    private static int connectRetryCount;
    private static volatile long messageSequence;
    private static volatile long insufficientFundsSequence;
    private static volatile String lastServerMessage = "";
    private static volatile String lastNpcMessage = "";
    private static volatile String lastMessage = "";
    private static volatile int lastNpcId = -1;

    private TaThuAccountManager() {
    }

    public static boolean isEnabledRuntime() {
        return "ta-thu".equalsIgnoreCase(System.getProperty("nso.runtime", ""));
    }

    public static String getStage() {
        return System.getProperty("ta.thu.stage", "full").toLowerCase();
    }

    public static boolean isObserveStage() {
        return "observe".equals(getStage());
    }

    public static boolean isShopStage() {
        return "shop".equals(getStage());
    }

    public static synchronized void start() {
        if (started) {
            return;
        }
        started = true;
        loadAccounts();
        if (usernames.size() == 0) {
            System.out.println("AUTO TA THU: account.csv không có tài khoản, dừng runtime.");
            finishAll();
            return;
        }
        enabled = true;
        accountIndex = 0;
        characterIndex = 0;
        characterNames = null;
        switching = true;
        System.out.println("AUTO TA THU: runtime stage=" + getStage() + " accounts=" + usernames.size());
        (new Thread(new TaThuAccountManager())).start();
    }

    private static void loadAccounts() {
        InputStream input = null;
        try {
            input = TaThuAccountManager.class.getResourceAsStream("/account.csv");
            if (input == null) {
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
            System.out.println("AUTO TA THU: lỗi đọc account.csv: " + ex.toString());
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
            return;
        }
        String username = line.substring(0, comma).trim();
        String password = line.substring(comma + 1).trim();
        if (username.equalsIgnoreCase("username") && password.equalsIgnoreCase("password")) {
            return;
        }
        usernames.addElement(username);
        passwords.addElement(password);
    }

    public void run() {
        sleep(3000L);
        loginCurrentAccount();
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
        // Keep the original name order across logins: the server moves the last
        // selected character to the front of each new list.
        waitingForCharacters = false;
        waitingForGame = false;
        postProcessing = false;
        System.out.println("AUTO TA THU: đăng nhập account " + (accountIndex + 1) + "/" + usernames.size());
        Code.fieldAG();
        Session_ME session = Session_ME.gI();
        session.gameAC();
        session.gameAA11(GameMidlet.IP, GameMidlet.PORT);
        long deadline = System.currentTimeMillis() + 20000L;
        while ((!session.connected || !session.getKeyComplete) && System.currentTimeMillis() < deadline) {
            sleep(100L);
        }
        if (!session.connected || !session.getKeyComplete) {
            session.gameAC();
            int retry = ++connectRetryCount;
            if (retry <= MAX_CONNECT_RETRIES) {
                System.out.println("AUTO TA THU: kết nối thất bại, thử lại " + retry + "/" + MAX_CONNECT_RETRIES);
                sleep(CONNECT_RETRY_DELAY);
                loginCurrentAccount();
            } else {
                connectRetryCount = 0;
                nextAccount();
            }
            return;
        }
        connectRetryCount = 0;
        waitingForCharacters = true;
        Service.gI().login(username, password, "2.1.7");
    }

    public static synchronized boolean isRunning() {
        return enabled;
    }

    /** Route legacy reconnect requests through the account runner as well. */
    public static synchronized boolean onReconnectRequested() {
        if (!enabled) {
            return false;
        }
        if (switching || reconnecting) {
            return true;
        }
        Code.fieldAG();
        Session_ME.gI().gameAC();
        return onDisconnected();
    }

    public static synchronized void onCharacterList(String[] names) {
        if (!enabled || !waitingForCharacters) {
            return;
        }
        waitingForCharacters = false;
        if (characterNames == null) {
            characterNames = new String[names.length];
            System.arraycopy(names, 0, characterNames, 0, names.length);
        }
        while (characterIndex < characterNames.length
                && (characterNames[characterIndex] == null || characterNames[characterIndex].length() == 0)) {
            ++characterIndex;
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
        boolean found = false;
        for (int i = 0; i < names.length; ++i) {
            if (name.equals(names[i])) {
                found = true;
                break;
            }
        }
        if (!found) {
            skipCurrentCharacter("nhân vật " + name + " không còn trong danh sách server");
            return;
        }
        SelectCharScr.fieldAK = name;
        waitingForGame = true;
        switching = false;
        System.out.println("AUTO TA THU: chọn nhân vật " + name);
        Service.gI().selectCharToPlay(name);
    }

    public static synchronized void onGameReady() {
        if (!enabled || !waitingForGame) {
            return;
        }
        Char me = Char.getMyChar();
        if (characterNames == null || characterIndex >= characterNames.length
                || !characterNames[characterIndex].equals(me.cName)) {
            System.out.println("AUTO LOGIN: nhân vật vào game không khớp tên đang chờ, đăng nhập lại");
            reconnecting = false;
            onReconnectRequested();
            return;
        }
        reconnecting = false;
        disconnectRetryCount = 0;
        waitingForGame = false;
        if (me.clevel < 30) {
            skipCurrentCharacter("level=" + me.clevel + " < 30");
            return;
        }
        System.out.println("AUTO TA THU: game ready char=" + me.cName + " level=" + me.clevel
                + " map=" + TileMap.mapID + " zone=" + TileMap.zoneID);
        TaThuDailyState savedState = TaThuDailyState.loadCurrent();
        if (savedState.finished) {
            resumePostProcessing(savedState);
            return;
        }
        if (isObserveStage() || isShopStage()) {
            AutoTaThuOrders observer = new AutoTaThuOrders(isObserveStage());
            observer.fieldAD();
            Code.fieldAA((Auto) observer);
            return;
        }
        if (!savedState.finished && savedState.mapId >= 0 && savedState.zoneId >= 0 && savedState.killId >= 0) {
            System.out.println("AUTO TA THU: có nhiệm vụ dở map="
                    + savedState.mapId + " zone=" + savedState.zoneId + " killId=" + savedState.killId
                    + "; vẫn chạy chuẩn bị trước khi quay lại đánh");
        }
        AutoPrepareNvhn prepare = new AutoPrepareNvhn();
        prepare.fieldAD();
        Code.fieldAA((Auto) prepare);
    }

    public static synchronized void onPreparationFinished() {
        if (!enabled || switching) {
            return;
        }
        TaskOrder task = Char.fieldAM(1);
        TaThuDailyState state = TaThuDailyState.loadCurrent();
        if (task != null || state.mapId >= 0 && state.zoneId >= 0 && state.killId >= 0) {
            System.out.println("AUTO TA THU: chuẩn bị xong; tiếp tục nhiệm vụ dở"
                    + (task == null ? " theo state đã lưu" : " TaskOrder map=" + task.mapId
                    + " killId=" + task.killId + " count=" + task.count + "/" + task.maxCount));
            AutoTaThuDaily daily = new AutoTaThuDaily();
            daily.fieldAD();
            Code.fieldAA((Auto) daily);
            return;
        }
        AutoTaThuOrders orders = new AutoTaThuOrders(false);
        orders.fieldAD();
        Code.fieldAA((Auto) orders);
    }

    public static synchronized void onOrdersReady() {
        if (!enabled || switching) {
            return;
        }
        if (!"full".equals(getStage()) && !"receive".equals(getStage()) && !"fight".equals(getStage())) {
            System.out.println("AUTO TA THU: test stage " + getStage() + " hoàn tất một nhân vật, dừng test");
            Code.fieldAG();
            finishAll();
            return;
        }
        AutoTaThuDaily daily = new AutoTaThuDaily();
        daily.fieldAD();
        Code.fieldAA((Auto) daily);
    }

    public static synchronized void onTestStageFinished(String detail) {
        if (!enabled) {
            return;
        }
        System.out.println("AUTO TA THU: test stage " + getStage() + " hoàn tất: " + detail);
        Code.fieldAG();
        finishAll();
    }

    public static synchronized void onTaThuDailyFinished() {
        if (!enabled || switching || postProcessing) {
            return;
        }
        postProcessing = true;
        System.out.println("AUTO TA THU: đã hết lượt, bắt đầu lật hình");
        AutoFlipNvhn flip = new AutoFlipNvhn();
        flip.fieldAD();
        Code.fieldAA((Auto) flip);
    }

    public static synchronized void onPostDailyFlipFinished() {
        if (!enabled || switching) {
            return;
        }
        TaThuDailyState state = TaThuDailyState.loadCurrent();
        state.flipDone = true;
        state.save();
        System.out.println("AUTO TA THU: đã ghi nhận hoàn tất lật hình, bắt đầu đi hang");
        AutoEnterCave cave = new AutoEnterCave();
        cave.fieldAD();
        Code.fieldAA((Auto) cave);
    }

    public static synchronized void onCaveEntered() {
        if (!enabled || switching) {
            return;
        }
        TaThuDailyState state = TaThuDailyState.loadCurrent();
        state.caveDone = true;
        state.save();
        skipCurrentCharacter("đã xử lý hang động");
    }

    public static synchronized void onCharacterBelowLevel30(String message) {
        if (enabled && !switching) {
            skipCurrentCharacter("NPC báo chưa đủ cấp 30: " + message);
        }
    }

    public static synchronized boolean onDisconnected() {
        if (!enabled) {
            return false;
        }
        if (switching) {
            System.out.println("AUTO TA THU: socket đóng do đang chuyển nhân vật/tài khoản, không reconnect");
            return true;
        }
        if (reconnecting) {
            return true;
        }
        reconnecting = true;
        Code.fieldAG();
        waitingForCharacters = false;
        final int retryAccount = accountIndex;
        final int retryCharacter = characterIndex;
        waitingForGame = false;
        int retry = ++disconnectRetryCount;
        final long delay = Math.min(30000L, 5000L * retry);
        System.out.println("AUTO TA THU: mất kết nối, đăng nhập lại sau " + delay / 1000L + " giây");
        (new Thread(new Runnable() {
            public void run() {
                sleep(delay);
                synchronized (TaThuAccountManager.class) {
                    if (!enabled || switching || !reconnecting
                            || accountIndex != retryAccount || characterIndex != retryCharacter) {
                        return;
                    }
                    reconnecting = false;
                }
                loginCurrentAccount();
            }
        })).start();
        return true;
    }

    public static synchronized void onServerMessage(String message) {
        lastServerMessage = message == null ? "" : message;
        lastMessage = lastServerMessage;
        ++messageSequence;
        String lowerMessage = lastServerMessage.toLowerCase();
        if (lowerMessage.indexOf("không đủ tiền") >= 0 || lowerMessage.indexOf("khong du tien") >= 0) {
            insufficientFundsSequence = messageSequence;
        }
        if (enabled) {
            System.out.println("AUTO TA THU SERVER: [" + lastServerMessage + "]");
        }
        if (enabled && switching) {
            String lower = lastServerMessage.toLowerCase();
            if (lower.indexOf("đã có người đăng nhập") >= 0
                    || lower.indexOf("dang nhap") >= 0 && lower.indexOf("roi") >= 0) {
                retryCurrentAccountAfterLoginConflict();
            } else if (lower.indexOf("mật khẩu") >= 0 || lower.indexOf("mat khau") >= 0
                    || lower.indexOf("tài khoản không") >= 0 || lower.indexOf("tai khoan khong") >= 0) {
                System.out.println("AUTO TA THU: server từ chối thông tin đăng nhập, chuyển account");
                (new Thread(new Runnable() {
                    public void run() {
                        sleep(1000L);
                        nextAccount();
                    }
                })).start();
            }
        }
    }

    private static void retryCurrentAccountAfterLoginConflict() {
        if (reconnecting) {
            return;
        }
        reconnecting = true;
        System.out.println("AUTO TA THU: account còn phiên đăng nhập cũ; chờ 15 giây rồi thử lại chính account");
        (new Thread(new Runnable() {
            public void run() {
                sleep(15000L);
                synchronized (TaThuAccountManager.class) {
                    if (!enabled) {
                        reconnecting = false;
                        return;
                    }
                    reconnecting = false;
                }
                loginCurrentAccount();
            }
        })).start();
    }

    public static synchronized void onNpcMessage(int npcId, String message) {
        lastNpcId = npcId;
        lastNpcMessage = message == null ? "" : message;
        lastMessage = lastNpcMessage;
        ++messageSequence;
        if (enabled) {
            System.out.println("AUTO TA THU NPC " + npcId + ": [" + lastNpcMessage + "]");
        }
    }

    public static long getMessageSequence() {
        return messageSequence;
    }

    // Keep this rejection even if a subsequent NPC/info message overwrites lastMessage.
    public static synchronized boolean hasInsufficientFundsSince(long previousSequence) {
        return insufficientFundsSequence > previousSequence;
    }

    public static String getLastMessageSince(long previousSequence) {
        if (messageSequence <= previousSequence) {
            return "";
        }
        return lastMessage;
    }

    private static void resumePostProcessing(TaThuDailyState state) {
        postProcessing = true;
        if (state.caveDone) {
            skipCurrentCharacter("hôm nay đã hoàn tất Tà Thú, lật hình và hang động");
            return;
        }
        if (state.flipDone) {
            System.out.println("AUTO TA THU: daily và lật hình đã xong, tiếp tục đi hang");
            AutoEnterCave cave = new AutoEnterCave();
            cave.fieldAD();
            Code.fieldAA((Auto) cave);
            return;
        }
        System.out.println("AUTO TA THU: daily đã xong, tiếp tục từ bước lật hình");
        AutoFlipNvhn flip = new AutoFlipNvhn();
        flip.fieldAD();
        Code.fieldAA((Auto) flip);
    }

    public static int getLastNpcId() {
        return lastNpcId;
    }

    public static synchronized void onTaskOrderProgress(TaskOrder task) {
        if (!enabled || task == null || task.taskId != 1 || task.count < task.maxCount) {
            return;
        }
        TaThuDailyState state = TaThuDailyState.loadCurrent();
        state.rememberTask(task);
        state.completionPending = true;
        state.save();
        System.out.println("AUTO TA THU PACKET: TaskOrder 1 đạt " + task.count + "/" + task.maxCount
                + ", chờ packet xóa/xác nhận trả task");
    }

    public static synchronized void onTaskOrderRemoved(int taskId) {
        if (!enabled || taskId != 1) {
            return;
        }
        TaThuDailyState state = TaThuDailyState.loadCurrent();
        if (!state.completionPending) {
            System.out.println("AUTO TA THU PACKET: TaskOrder 1 bị xóa nhưng chưa có mốc hoàn thành; không cộng state");
            state.clearTaskLocation();
            return;
        }
        state.completionPending = false;
        ++state.questsCompleted;
        state.clearTaskLocation();
        System.out.println("AUTO TA THU PACKET: server xóa TaskOrder 1, hoàn thành="
                + state.questsCompleted + "/4");
    }

    public static String getCurrentUsername() {
        if (accountIndex < 0 || accountIndex >= usernames.size()) {
            return "-";
        }
        return (String) usernames.elementAt(accountIndex);
    }

    private static void skipCurrentCharacter(String reason) {
        reconnecting = false;
        waitingForCharacters = false;
        switching = true;
        waitingForGame = false;
        postProcessing = false;
        Code.fieldAG();
        System.out.println("AUTO TA THU: " + reason + ", chuyển nhân vật");
        (new Thread(new Runnable() {
            public void run() {
                sleep(1500L);
                advanceCharacterOrAccount();
            }
        })).start();
    }

    private static void advanceCharacterOrAccount() {
        ++characterIndex;
        while (characterNames != null && characterIndex < characterNames.length
                && (characterNames[characterIndex] == null || characterNames[characterIndex].length() == 0)) {
            ++characterIndex;
        }
        if (characterNames != null && characterIndex < characterNames.length) {
            loginCurrentAccount();
        } else {
            nextAccount();
        }
    }

    private static void nextAccount() {
        reconnecting = false;
        waitingForCharacters = false;
        ++accountIndex;
        characterIndex = 0;
        characterNames = null;
        connectRetryCount = 0;
        switching = true;
        loginCurrentAccount();
    }

    private static void finishAll() {
        enabled = false;
        switching = false;
        reconnecting = false;
        FileOutputStream output = null;
        try {
            // FIX: Tạo marker ở đúng worker directory, không phải trong /home
            String home = System.getProperty("user.home");
            File homeDir = new File(home);
            // home thường là /path/workers/worker-XX/home
            // Ta cần tạo marker ở /path/workers/worker-XX/worker.done
            File workerDir = homeDir.getParentFile();
            if (workerDir == null || !workerDir.exists()) {
                workerDir = homeDir;
            }
            File marker = new File(workerDir, "worker.done");
            output = new FileOutputStream(marker);
            output.write("completed\n".getBytes("UTF-8"));
            output.flush();
            System.out.println("AUTO TA THU: hoàn tất toàn bộ tài khoản, marker=" + marker.getAbsolutePath());
        } catch (Exception ex) {
            System.out.println("AUTO TA THU: không tạo được marker: " + ex.toString());
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (Exception ignored) {
            }
        }
        Session_ME.gI().gameAC();
        sleep(500L);
        if (GameMidlet.instance != null) {
            GameMidlet.instance.notifyDestroyed();
        }
        System.exit(0);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }
}
