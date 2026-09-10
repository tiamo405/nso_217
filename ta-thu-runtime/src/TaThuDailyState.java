import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Properties;

/** Durable per-character, per-game-day progress for the Ta Thu runtime. */
public final class TaThuDailyState {
    public int ordersUsed;
    public boolean ordersPurchaseSkipped;
    public int questsCompleted;
    public int mapId = -1;
    public int zoneId = -1;
    public int killId = -1;
    public int taskCount;
    public int taskMax;
    public int mobX = -1;
    public int mobY = -1;
    public boolean finished;
    public boolean flipDone;
    public boolean caveDone;
    public boolean completionPending;

    private final File file;

    private TaThuDailyState(File file) {
        this.file = file;
    }

    public static TaThuDailyState loadCurrent() {
        String username = TaThuAccountManager.getCurrentUsername();
        String character = Char.getMyChar() == null ? "unknown" : Char.getMyChar().cName;
        Calendar now = Res.fieldAB();
        String day = now.get(Calendar.YEAR) + "-" + two(now.get(Calendar.MONTH) + 1) + "-" + two(now.get(Calendar.DAY_OF_MONTH));
        File directory = getBaseStateDirectory(day);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, safe(username) + "__" + safe(character) + ".properties");
        TaThuDailyState state = new TaThuDailyState(file);
        state.read();
        return state;
    }

    private static File getBaseStateDirectory(String day) {
        String customPath = System.getProperty("ta.thu.state.dir");
        if (customPath == null || customPath.trim().length() == 0) {
            customPath = System.getenv("TA_THU_STATE_DIR");
        }
        File root;
        if (customPath != null && customPath.trim().length() > 0) {
            root = new File(customPath.trim());
        } else {
            String home = System.getProperty("user.home");
            if (home != null && home.contains("/workers/worker-")) {
                int index = home.indexOf("/workers/worker-");
                home = home.substring(0, index);
            }
            root = new File(home, "ta-thu-state");
        }
        return new File(root, day);
    }

    private void read() {
        if (!this.file.isFile()) {
            return;
        }
        Properties values = new Properties();
        FileInputStream input = null;
        try {
            input = new FileInputStream(this.file);
            values.load(input);
            this.ordersUsed = integer(values, "ordersUsed", 0);
            this.ordersPurchaseSkipped = "true".equals(values.getProperty("ordersPurchaseSkipped"));
            this.questsCompleted = integer(values, "questsCompleted", 0);
            this.mapId = integer(values, "mapId", -1);
            this.zoneId = integer(values, "zoneId", -1);
            this.killId = integer(values, "killId", -1);
            this.taskCount = integer(values, "taskCount", 0);
            this.taskMax = integer(values, "taskMax", 0);
            this.mobX = integer(values, "mobX", -1);
            this.mobY = integer(values, "mobY", -1);
            this.finished = "true".equals(values.getProperty("finished"));
            this.flipDone = "true".equals(values.getProperty("flipDone"));
            this.caveDone = "true".equals(values.getProperty("caveDone"));
            this.completionPending = "true".equals(values.getProperty("completionPending"));
        } catch (Exception ex) {
            System.out.println("AUTO TA THU STATE: không đọc được " + this.file + ": " + ex.toString());
        } finally {
            close(input);
        }
    }

    public synchronized void save() {
        Properties values = new Properties();
        values.setProperty("ordersUsed", String.valueOf(this.ordersUsed));
        values.setProperty("ordersPurchaseSkipped", String.valueOf(this.ordersPurchaseSkipped));
        values.setProperty("questsCompleted", String.valueOf(this.questsCompleted));
        values.setProperty("mapId", String.valueOf(this.mapId));
        values.setProperty("zoneId", String.valueOf(this.zoneId));
        values.setProperty("killId", String.valueOf(this.killId));
        values.setProperty("taskCount", String.valueOf(this.taskCount));
        values.setProperty("taskMax", String.valueOf(this.taskMax));
        values.setProperty("mobX", String.valueOf(this.mobX));
        values.setProperty("mobY", String.valueOf(this.mobY));
        values.setProperty("finished", String.valueOf(this.finished));
        values.setProperty("flipDone", String.valueOf(this.flipDone));
        values.setProperty("caveDone", String.valueOf(this.caveDone));
        values.setProperty("completionPending", String.valueOf(this.completionPending));
        File temporary = new File(this.file.getParentFile(), this.file.getName() + ".tmp");
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(temporary);
            values.store(output, "NSO Ta Thu runtime state");
            output.flush();
            close(output);
            output = null;
            if (this.file.exists() && !this.file.delete()) {
                throw new Exception("không thay thế được state cũ");
            }
            if (!temporary.renameTo(this.file)) {
                throw new Exception("không rename được state tạm");
            }
        } catch (Exception ex) {
            System.out.println("AUTO TA THU STATE: không ghi được " + this.file + ": " + ex.toString());
        } finally {
            close(output);
        }
    }

    public boolean matches(TaskOrder task) {
        return task != null && this.mapId == task.mapId && this.killId == task.killId;
    }

    public void rememberTask(TaskOrder task) {
        if (task == null) {
            return;
        }
        if (this.mapId != task.mapId || this.killId != task.killId) {
            this.mapId = task.mapId;
            this.zoneId = -1;
            this.killId = task.killId;
            this.mobX = -1;
            this.mobY = -1;
        }
        this.taskCount = task.count;
        this.taskMax = task.maxCount;
        this.save();
    }

    public void clearTaskLocation() {
        this.mapId = -1;
        this.zoneId = -1;
        this.killId = -1;
        this.taskCount = 0;
        this.taskMax = 0;
        this.mobX = -1;
        this.mobY = -1;
        this.save();
    }

    public void clearLockedZone() {
        this.zoneId = -1;
        this.mobX = -1;
        this.mobY = -1;
        this.save();
    }

    public File getFile() {
        return this.file;
    }

    private static int integer(Properties values, String key, int fallback) {
        try {
            return Integer.parseInt(values.getProperty(key, String.valueOf(fallback)));
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private static String safe(String value) {
        if (value == null || value.length() == 0) {
            return "unknown";
        }
        StringBuffer result = new StringBuffer();
        for (int index = 0; index < value.length(); ++index) {
            char character = value.charAt(index);
            if (Character.isLetterOrDigit(character) || character == '-' || character == '_') {
                result.append(character);
            } else {
                result.append('_');
            }
        }
        return result.toString();
    }

    private static String two(int value) {
        return value < 10 ? "0" + value : String.valueOf(value);
    }

    private static void close(java.io.Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception ignored) {
        }
    }
}
