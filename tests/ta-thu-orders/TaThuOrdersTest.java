import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.TimeZone;

/** Offline state-machine tests with scripted server replies; never opens a socket. */
public final class TaThuOrdersTest {
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }

    private static void reset(String name) {
        Char.me = new Char();
        Char.me.cName = name;
        Char.task = null;
        Service.buys = Service.uses = GameScr.receives = 0;
        Service.remaining = 2;
        Service.reply = "reject";
        Code.current = null;
        Auto.onWait = null;
        TileMap.mapID = 72;
    }

    private static void orders() {
        AutoTaThuOrders orders = new AutoTaThuOrders(false);
        orders.fieldAD();
        orders.fieldAA();
    }

    private static void set(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    public static void main(String[] args) throws Exception {
        long old = TaThuAccountManager.getMessageSequence();
        TaThuAccountManager.onServerMessage("Không đủ tiền!");
        TaThuAccountManager.onNpcMessage(30, "Chào bạn");
        check(TaThuAccountManager.hasInsufficientFundsSince(old), "rejection survives later NPC message");
        long current = TaThuAccountManager.getMessageSequence();
        check(!TaThuAccountManager.hasInsufficientFundsSince(current), "old rejection must not affect a new purchase");
        TaThuAccountManager.onServerMessage("KHONG DU TIEN!");
        check(TaThuAccountManager.hasInsufficientFundsSince(current), "unaccented rejection");

        reset("insufficient");
        orders();
        TaThuDailyState state = TaThuDailyState.loadCurrent();
        check(Service.buys == 1 && Service.uses == 0, "one rejected purchase, no use");
        check(state.ordersPurchaseSkipped && state.ordersUsed == 0 && !state.finished,
                "skip persisted without inventing order uses or task completion");
        orders();
        check(Service.buys == 1, "recreated orders automation must not buy again");

        // Completing a free run must continue to the next free run, not return to orders.
        state.questsCompleted = 1;
        state.save();
        TileMap.mapID = 1;
        AutoTaThuDaily daily = new AutoTaThuDaily();
        daily.fieldAD();
        daily.fieldAA();
        check(GameScr.receives == 1 && !(Code.current instanceof AutoTaThuOrders),
                "receive remaining free task after restart");
        daily = new AutoTaThuDaily();
        daily.fieldAD();
        set(daily, "returningTask", Boolean.TRUE);
        daily.fieldAA();
        check(!(Code.current instanceof AutoTaThuOrders), "turn-in branch must not return to buying");
        set(daily, "nextActionAt", Long.valueOf(0));
        Service.remaining = 0;
        daily.fieldAA();
        check(TaThuDailyState.loadCurrent().finished, "finish when server confirms zero runs after skip");

        reset("timeout-count");
        state = TaThuDailyState.loadCurrent();
        state.ordersPurchaseSkipped = true;
        state.save();
        Service.remaining = -128;
        daily = new AutoTaThuDaily();
        daily.fieldAD();
        set(daily, "returningTask", Boolean.TRUE);
        daily.fieldAA();
        check(!TaThuDailyState.loadCurrent().finished, "unknown run count must not mark daily complete");

        reset("delayed-rejection");
        Service.reply = "delayed";
        orders();
        check(TaThuDailyState.loadCurrent().ordersPurchaseSkipped, "rejection arriving while waiting is terminal");

        reset("successful");
        Service.reply = "success";
        orders();
        state = TaThuDailyState.loadCurrent();
        check(Service.buys == 1 && Service.uses == 2 && state.ordersUsed == 2 && !state.ordersPurchaseSkipped,
                "successful purchase still uses two orders despite stale money rejection");

        reset("partial");
        Char.me.arrItemBag = new Item[]{Service.item(1)};
        orders();
        state = TaThuDailyState.loadCurrent();
        check(Service.uses == 1 && state.ordersUsed == 1 && state.ordersPurchaseSkipped,
                "preserve one actual use when purchase of missing order fails");

        reset("no-reply");
        Service.reply = "timeout";
        AutoTaThuOrders pending = new AutoTaThuOrders(false);
        pending.fieldAD();
        pending.fieldAA();
        pending.fieldAA();
        check(Service.buys == 1 && !TaThuDailyState.loadCurrent().ordersPurchaseSkipped,
                "timeout remains retryable with backoff, not mistaken for insufficient funds");
        TaThuAccountManager.onServerMessage("Không đủ tiền!");
        set(pending, "nextAttemptAt", Long.valueOf(0));
        pending.fieldAA();
        check(Service.buys == 1 && TaThuDailyState.loadCurrent().ordersPurchaseSkipped,
                "late rejection during backoff must prevent the next purchase");

        reset("insufficient");
        check(TaThuDailyState.loadCurrent().ordersPurchaseSkipped, "skip is isolated from other characters");
        Res.day.add(Calendar.DAY_OF_MONTH, 1);
        check(!TaThuDailyState.loadCurrent().ordersPurchaseSkipped, "new game day allows purchase again");
        System.out.println("PASS: purchase rejection, message ordering, restart, daily transitions, success and timeout");
    }
}

// Minimal game boundary doubles. The four Ta Thu production classes remain real.
class Auto {
    static Runnable onWait;
    public void fieldAD() {}
    public void fieldAA() {}
    public void fieldAA(int map, int zone, int x, int y) { TileMap.mapID = map; }
    public static void fieldAA(long millis) {
        if (onWait != null) {
            Runnable action = onWait;
            onWait = null;
            action.run();
        }
        Thread.yield();
    }
    public static void fieldAA(boolean value) {}
    public static boolean fieldAF() { return false; }
}
class Char {
    static Char me = new Char();
    static TaskOrder task;
    public String cName;
    public int countLoopBoos;
    public Item[] arrItemBag = new Item[0], arrItemBox = new Item[0];
    public static Char getMyChar() { return me; }
    public static TaskOrder fieldAM(int id) { return task; }
}
class TileMap { public static int mapID = 72, zoneID; }
class GameScr {
    public static Item[] arrItemStore;
    public static int fieldGH, receives;
    public static void fieldAC(String text) {}
    public static void fieldAB(int npc, int menu, int action) {
        if (npc == 25 && action == 0) ++receives;
    }
}
class Service {
    private static final Service instance = new Service();
    static int buys, uses, remaining;
    static String reply;
    public static Service gI() { return instance; }
    static Item item(int quantity) {
        Item item = new Item();
        item.template = new ItemTemplate();
        item.template.id = 268;
        item.quantity = quantity;
        item.typeUI = 14;
        item.indexUI = 17;
        return item;
    }
    public void viewInfo(String name, int type) { Char.me.countLoopBoos = remaining; }
    public void requestItem(int type) {
        if (type == 4) Char.me.arrItemBox = new Item[0];
        if (type == 14) GameScr.arrItemStore = new Item[]{item(1)};
    }
    public void itemBoxToBag(int index) { throw new AssertionError("unexpected box withdrawal"); }
    public void buyItem(int type, int index, int quantity) {
        ++buys;
        if ("success".equals(reply)) Char.me.arrItemBag = new Item[]{item(quantity)};
        if ("reject".equals(reply)) reject();
        if ("delayed".equals(reply)) Auto.onWait = new Runnable() {
            public void run() { reject(); }
        };
    }
    private static void reject() {
        TaThuAccountManager.onServerMessage("Không đủ tiền!");
        TaThuAccountManager.onServerMessage("Thông tin khác");
    }
    public void useItem(int index) {
        ++uses;
        ++remaining;
        if (--Char.me.arrItemBag[0].quantity == 0) Char.me.arrItemBag = new Item[0];
    }
}
class Item { public ItemTemplate template; public int quantity, typeUI, indexUI, buyCoin, buyGold; }
class ItemTemplate { public int id; }
class TaskOrder { public int taskId, mapId, killId, count, maxCount; }
class Code {
    public static TaThu fieldAE;
    static Auto current;
    public static void fieldAA(Auto auto) { current = auto; }
    public static void fieldAC() {}
}
class TaThu extends Auto {}
class LockGame { public static void fieldAK() {} public static void fieldAM() {} }
class Res {
    static Calendar day = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
    public static Calendar fieldAB() { return (Calendar) day.clone(); }
}
