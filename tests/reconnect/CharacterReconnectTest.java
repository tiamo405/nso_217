import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Vector;

/** Exercises the real account manager with an offline login/selection transport. */
public final class CharacterReconnectTest {
    private static Class manager;

    private static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }

    private static Field field(String name) throws Exception {
        Field f = manager.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    private static void set(String name, Object value) throws Exception {
        field(name).set(null, value);
    }

    private static Object call(String name) throws Exception {
        Method method = manager.getDeclaredMethod(name);
        method.setAccessible(true);
        return method.invoke(null);
    }

    private static void list(String... names) throws Exception {
        manager.getMethod("onCharacterList", String[].class).invoke(null, (Object) names);
    }

    private static void ready() throws Exception {
        set("waitingForGame", false);
        set("reconnecting", false);
        set("disconnectRetryCount", 0);
    }

    private static void select(String expected, String... names) throws Exception {
        list(names);
        check(expected.equals(Service.selected), "expected " + expected + ", got " + Service.selected);
        ready();
    }

    public static void main(String[] args) throws Exception {
        manager = Class.forName(args[0]);
        set("enabled", true);
        Vector users = (Vector) field("usernames").get(null);
        Vector passwords = (Vector) field("passwords").get(null);
        users.addElement("offline-a"); users.addElement("offline-b");
        passwords.addElement("unused"); passwords.addElement("unused");

        call("loginCurrentAccount");
        String[] original = {"one", "two", "three"};
        select("one", original);
        // The initial packet must be copied, not retained as a mutable array.
        original[0] = "changed";
        check("one".equals(((String[]) field("characterNames").get(null))[0]), "snapshot is copied");

        call("advanceCharacterOrAccount");
        select("two", "one", "two", "three");
        call("loginCurrentAccount");
        select("two", "two", "one", "three");
        int beforeReconnect = Service.logins;
        check(Boolean.TRUE.equals(call("onDisconnected")), "disconnect belongs to runner");
        Thread.sleep(5500L);
        check(Service.logins == beforeReconnect + 1, "disconnect schedules exactly one login");
        select("two", "two", "one", "three");
        int selections = Service.selections;
        list("one", "three", "two");
        check(Service.selections == selections, "unsolicited/duplicate character list ignored");

        call("advanceCharacterOrAccount");
        select("three", "two", "one", "three");
        call("loginCurrentAccount");
        select("three", "three", "two", "one");

        // Explicit/legacy reconnect requests must be coalesced and retain the name.
        int closes = Session_ME.closes;
        check(Boolean.TRUE.equals(call("onReconnectRequested")), "manager owns reconnect");
        check(Boolean.TRUE.equals(call("onReconnectRequested")), "duplicate request handled");
        check(Session_ME.closes == closes + 1, "duplicate request must not close twice");
        check(Boolean.TRUE.equals(field("reconnecting").get(null)), "one reconnect scheduled");

        // Invalidate the pending delayed retry by intentionally moving to the next account.
        set("switching", true);
        check(Boolean.TRUE.equals(call("onDisconnected")), "intentional switch handled");
        call("advanceCharacterOrAccount");
        check(field("characterNames").get(null) == null, "new account clears old names");
        select("other", "other", "last", "");
        int logins = Service.logins;
        Thread.sleep(5500L);
        check(Service.logins == logins, "old reconnect must not log in over new account");

        call("advanceCharacterOrAccount");
        select("last", "other", "last", "");
        call("loginCurrentAccount");
        select("last", "last", "other", "");
        set("enabled", false);
        check(Boolean.FALSE.equals(call("onReconnectRequested")), "inactive runner allows legacy fallback");
        System.out.println("PASS " + args[0] + ": reordered lists, delayed reconnect, duplicate packets, account reset, stale retry");
    }
}

// These adapters never open a socket or start game automation.
class Service {
    private static final Service instance = new Service();
    static String selected;
    static int selections;
    static volatile int logins;
    public static Service gI() { return instance; }
    public void selectCharToPlay(String name) { selected = name; ++selections; }
    public void login(String user, String password, String version) { ++logins; }
}
class Session_ME {
    private static final Session_ME instance = new Session_ME();
    public boolean connected, getKeyComplete;
    static int closes;
    public static Session_ME gI() { return instance; }
    public void gameAC() { ++closes; connected = getKeyComplete = false; }
    public void gameAA11(String host, int port) { connected = getKeyComplete = true; }
}
class Code { public static void fieldAG() {} }
class SelectCharScr { public static String fieldAK; }
class SelectServerScr { public static String uname, pass, unameChange, passChange; }
class UpdateServer {
    public static String[] listIP = {"offline"};
    public static int[] listPort = {0};
    public static byte[] serverLoginList = {0};
}
class GameMidlet { public static String IP; public static int PORT; public static byte serverLogin; }
class BotMetrics {
    public static void begin(String a, String b) {}
    public static void event(String a, String b, int c) {}
    public static void finish(String a, String b) {}
}
