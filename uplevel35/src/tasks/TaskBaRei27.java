/**
 * Nhiệm vụ Bà Rei (Làng Chài) — mốc level 27, múc nước.
 *
 *  bước 0: đạt level 27 -> đánh quái map 50
 *  bước 1: nhận nhiệm vụ xong có Bình nước (túi hoặc rương). Đến map 63, đứng
 *          ở mép phải (1885, 336), dùng bình -> chờ thanh tiến trình vài giây ->
 *          dùng tiếp tới khi hết bình.
 *  bước cuối: As35 tự về Bà Rei trả nhiệm vụ.
 *
 * Có thể chỉ định id bình bằng -Dnso.uplevel35.bottle.id=<id> nếu tìm theo tên sai.
 */
final class TaskBaRei27 extends Uplevel35Task {
    static final int TASK_ID = Integer.getInteger("nso.uplevel35.task.barei27", 19).intValue();
    static final int LEVEL_REQUIRED = 27;
    static final int LEVEL_MAP = 50;
    static final int WATER_MAP = 63;
    static final int WATER_X = 1885;
    static final int WATER_Y = 336;
    static final int WATER_TOLERANCE = 24;
    static final String BOTTLE_NAME = "bình";
    /** Thời gian chờ thanh "múc nước" chạy xong trước khi dùng bình kế tiếp. */
    static final long USE_WAIT_MS = Long.getLong("nso.uplevel35.bottle.wait", 6000L).longValue();
    static final int BOTTLE_ID_OVERRIDE = Integer.getInteger("nso.uplevel35.bottle.id", -1).intValue();

    private int bottleId = -1;
    private boolean boxChecked;
    private long useSentAt;
    private int quantityBefore;
    private int countBefore;
    private int uses;

    int taskId() {
        return TASK_ID;
    }

    String label() {
        return "BaRei27";
    }

    void reset() {
        this.bottleId = -1;
        this.boxChecked = false;
        this.useSentAt = 0L;
        this.uses = 0;
    }

    void tick(As35 bot, Char me, Task task) {
        switch (task.index) {
            case 0:
                bot.levelUp(LEVEL_REQUIRED, LEVEL_MAP, -1);
                return;
            case 1:
                this.fetchWater(bot, me, task);
                return;
            default:
                Uplevel35.logEvery("barei27-step", 10000L, "BaRei27 bước " + task.index
                        + " chưa có logic: " + As35.describeTask(me));
        }
    }

    private void fetchWater(As35 bot, Char me, Task task) {
        long now = System.currentTimeMillis();
        if (this.useSentAt > 0L) {
            Item bottle = this.findBottle(me);
            int quantity = bottle == null ? 0 : bottle.quantity;
            boolean progressed = task.count != this.countBefore || quantity < this.quantityBefore;
            if (!progressed && now - this.useSentAt < USE_WAIT_MS) {
                return;
            }
            Uplevel35.log("WATER lần " + this.uses + (progressed ? " xong" : " hết thời gian chờ")
                    + " count=" + task.count + " bìnhCòn=" + quantity);
            this.useSentAt = 0L;
            if (!progressed) {
                return; // chờ thêm 1 tick rồi dùng lại
            }
        }

        if (!bot.goMap(WATER_MAP)) {
            Uplevel35.logEvery("water-route", 10000L, "WATER đi map " + TileMap.mapID + " -> " + WATER_MAP);
            return;
        }
        if (!bot.standAt(WATER_X, WATER_Y, WATER_TOLERANCE)) {
            Uplevel35.logEvery("water-walk", 5000L, "WATER đi tới " + WATER_X + "," + WATER_Y
                    + " từ " + me.cx + "," + me.cy);
            return;
        }

        Item bottle = this.findBottle(me);
        if (bottle == null && !this.boxChecked) {
            this.boxChecked = true;
            int id = this.bottleId >= 0 ? this.bottleId : this.resolveBottleIdFromBox();
            if (id >= 0) {
                bottle = Uplevel35Support.takeFromBox(id);
            }
        }
        if (bottle == null) {
            Uplevel35.logEvery("water-nobottle", 10000L, "WATER không còn bình trong túi/rương: "
                    + As35.describeTask(me));
            return;
        }

        this.bottleId = bottle.template.id;
        this.quantityBefore = bottle.quantity;
        this.countBefore = task.count;
        this.useSentAt = now;
        ++this.uses;
        Uplevel35.log("WATER dùng bình id=" + bottle.template.id + " name=" + bottle.template.name
                + " bagIndex=" + bottle.indexUI + " sl=" + bottle.quantity + " lần=" + this.uses
                + " pos=" + me.cx + "," + me.cy);
        Service.gI().useItem(bottle.indexUI);
    }

    private Item findBottle(Char me) {
        int id = BOTTLE_ID_OVERRIDE >= 0 ? BOTTLE_ID_OVERRIDE : this.bottleId;
        if (id >= 0) {
            return Uplevel35Support.findItem(me.arrItemBag, id);
        }
        Item[] bag = me.arrItemBag;
        for (int i = 0; bag != null && i < bag.length; i++) {
            if (isBottle(bag[i])) {
                return bag[i];
            }
        }
        return null;
    }

    private int resolveBottleIdFromBox() {
        if (BOTTLE_ID_OVERRIDE >= 0) {
            return BOTTLE_ID_OVERRIDE;
        }
        if (!Uplevel35Support.refreshBox(5000L)) {
            return -1;
        }
        Item[] box = Char.getMyChar().arrItemBox;
        for (int i = 0; box != null && i < box.length; i++) {
            if (isBottle(box[i])) {
                Uplevel35.log("WATER thấy bình trong rương id=" + box[i].template.id + " name=" + box[i].template.name);
                return box[i].template.id;
            }
        }
        return -1;
    }

    /** Bình nước nhiệm vụ: tên chứa "bình", loại trừ bình HP/MP (type 16/17). */
    private static boolean isBottle(Item item) {
        if (item == null || item.template == null || item.template.name == null) {
            return false;
        }
        int type = item.template.type;
        return type != 16 && type != 17 && item.template.name.toLowerCase().indexOf(BOTTLE_NAME) >= 0;
    }
}
