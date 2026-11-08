/**
 * Nhiệm vụ Matsurugi (làng Oshin, npc 23 map 48) — "NV truy tìm địa đồ", mốc level 34.
 *
 *  bước 0: đạt level 34 ("NV truy tìm địa đồ") -> đánh quái map 35 (từ Oshin dịch chuyển thẳng tới 35 lỗi,
 *          As35.goMap tự đi vòng qua Làng chài 32)
 *  bước 1: map 35 đứng tại (1861,432), dùng chìa khóa (NPC đưa khi nhận nhiệm vụ) -> bị đưa
 *          vào map ẩn -> đánh hết quái -> nhặt vật phẩm rơi ra (server tự sang bước cuối).
 *          Chết (mất chìa khóa) -> về Matsurugi xin chìa khóa mới.
 *  bước cuối: As35 tự về Matsurugi trả nhiệm vụ.
 *
 * Chưa biết trước: id chìa khóa, id map ẩn, id vật phẩm cần nhặt -> tự học từ dữ liệu
 * thật và in log (KEY/HIDDEN/LOOT) để cố định lại sau.
 */
final class TaskMatsurugi35 extends Uplevel35Task {
    static final int TASK_ID = Integer.getInteger("nso.uplevel35.task.matsurugi35", 23).intValue();
    static final int LEVEL_REQUIRED = 34;
    static final int LEVEL_MAP = 35;
    static final int DOOR_MAP = 35;
    static final int DOOR_X = 1861;
    static final int DOOR_Y = 432;
    static final int NPC_MAP = 48;
    static final int NPC_MATSURUGI = 23;
    /** Ghi đè id chìa khóa / map ẩn / vật phẩm nếu tự học sai. */
    static final int KEY_ID = Integer.getInteger("nso.uplevel35.m35.key", -1).intValue();
    static final int HIDDEN_MAP = Integer.getInteger("nso.uplevel35.m35.hidden", -1).intValue();
    static final int LOOT_ID = Integer.getInteger("nso.uplevel35.m35.loot", -1).intValue();
    /** Chìa khóa thu thập ở nhiệm vụ 22 / chìa khóa may mắn: không phải chìa mở cửa. */
    private static final int KEY_COLLECT_ID = 230;
    private static final int KEY_LUCKY_ID = 846;

    private int hiddenMap = HIDDEN_MAP;
    private int lootId = LOOT_ID;
    private int lastMap = -1;
    private long keyUsedAt;
    private int keyUses;
    private long npcTalkAt;
    private boolean bagLogged;

    int taskId() {
        return TASK_ID;
    }

    String label() {
        return "Matsurugi35";
    }

    void reset() {
        this.hiddenMap = HIDDEN_MAP;
        this.lootId = LOOT_ID;
        this.lastMap = -1;
        this.keyUsedAt = 0L;
        this.keyUses = 0;
        this.npcTalkAt = 0L;
        this.bagLogged = false;
    }

    void tick(As35 bot, Char me, Task task) {
        switch (task.index) {
            case 0:
                bot.levelUp(LEVEL_REQUIRED, LEVEL_MAP, -1);
                return;
            case 1:
                this.openDoor(bot, me);
                return;
            default:
                Uplevel35.logEvery("matsurugi35-step", 10000L, "Matsurugi35 bước " + task.index
                        + " chưa có logic: " + As35.describeTask(me));
        }
    }

    // ------------------------------------------------------------------ bước 1

    private void openDoor(As35 bot, Char me) {
        if (!this.bagLogged) {
            this.bagLogged = true;
            Uplevel35.log("M35 BAG " + describeBag(me));
        }
        int map = TileMap.mapID;
        long now = System.currentTimeMillis();
        if (map != this.lastMap) {
            Uplevel35.log("M35 vào map " + map + " (" + TileMap.mapName + ") từ " + this.lastMap
                    + " pos=" + me.cx + "," + me.cy + " mobs=" + describeMobs());
            if (this.lastMap == DOOR_MAP && this.keyUsedAt > 0L && now - this.keyUsedAt < 30000L
                    && map != DOOR_MAP) {
                this.hiddenMap = map;
                Uplevel35.log("M35 HIDDEN map ẩn = " + map);
            }
            this.lastMap = map;
        }

        if (map == this.hiddenMap) {
            this.fightHidden(bot, me);
            return;
        }

        Item key = findKey(me);
        if (key == null) {
            this.askNewKey(bot, me, now);
            return;
        }
        if (map != DOOR_MAP) {
            bot.goMap(DOOR_MAP);
            return;
        }
        if (!bot.standAt(DOOR_X, DOOR_Y, 12)) {
            Uplevel35.logEvery("m35-walk", 5000L, "M35 đi tới cửa " + DOOR_X + "," + DOOR_Y
                    + " pos=" + me.cx + "," + me.cy);
            return;
        }
        if (now - this.keyUsedAt < 8000L) {
            return;
        }
        this.keyUsedAt = now;
        ++this.keyUses;
        Uplevel35.log("M35 KEY dùng " + key.template.name + " id=" + key.template.id + " bagIndex="
                + key.indexUI + " lần=" + this.keyUses + " pos=" + me.cx + "," + me.cy);
        Service.gI().useItem(key.indexUI);
    }

    /** Map ẩn: đánh mọi quái, rồi nhặt vật phẩm nhiệm vụ rơi ra. */
    private void fightHidden(As35 bot, Char me) {
        if (this.lootId < 0) {
            ItemMap loot = findLoot();
            if (loot != null) {
                this.lootId = loot.template.id;
                Uplevel35.log("M35 LOOT id=" + this.lootId + " name=" + loot.template.name
                        + " type=" + loot.template.type + " @" + loot.x + "," + loot.y);
            }
        }
        Uplevel35.logEvery("m35-fight", 5000L, "M35 đánh map ẩn " + TileMap.mapID + " lootId=" + this.lootId
                + " mobs=" + describeMobs() + " ground=" + describeGround());
        bot.pickAndFight(this.lootId, -1);
    }

    /** Không còn chìa khóa (chết trong map ẩn) -> về Matsurugi xin chìa mới. */
    private void askNewKey(As35 bot, Char me, long now) {
        if (TileMap.mapID != NPC_MAP) {
            Uplevel35.logEvery("m35-nokey", 10000L, "M35 không có chìa khóa -> về Matsurugi map " + NPC_MAP);
            bot.goMap(NPC_MAP);
            return;
        }
        Npc npc = Uplevel35Support.walkToNpc(NPC_MATSURUGI);
        if (npc == null || now - this.npcTalkAt < 6000L) {
            return;
        }
        this.npcTalkAt = now;
        NpcTemplate template = npc.template;
        Uplevel35.log("M35 xin chìa khóa Matsurugi menu="
                + (template == null || template.menu == null ? "null" : java.util.Arrays.deepToString(template.menu))
                + " BAG " + describeBag(me));
        GameScr.fieldAB(NPC_MATSURUGI, 0, 0);
        Service.gI().getTask(NPC_MATSURUGI, 0, -1);
        LockGame.fieldAO();
        if (GameCanvas.menu != null && GameCanvas.menu.showMenu) {
            Uplevel35.log("M35 menu đang mở: " + As35.describeMenu());
        }
    }

    // ------------------------------------------------------------------ helper

    private static Item findKey(Char me) {
        Item[] bag = me.arrItemBag;
        for (int i = 0; bag != null && i < bag.length; i++) {
            Item item = bag[i];
            if (item == null || item.template == null) {
                continue;
            }
            int id = item.template.id;
            if (KEY_ID >= 0) {
                if (id == KEY_ID) {
                    return item;
                }
                continue;
            }
            String name = item.template.name == null ? "" : item.template.name.toLowerCase();
            if (id != KEY_COLLECT_ID && id != KEY_LUCKY_ID
                    && (name.indexOf("chìa khóa") >= 0 || name.indexOf("chìa khoá") >= 0)) {
                return item;
            }
        }
        return null;
    }

    /** Vật phẩm nhiệm vụ trên đất (type 25 như chìa khóa/rìu), bỏ qua yên/thuốc. */
    private static ItemMap findLoot() {
        for (int i = 0; i < GameScr.vItemMap.size(); i++) {
            ItemMap item = (ItemMap) GameScr.vItemMap.elementAt(i);
            if (item != null && item.template != null && item.template.type == 25) {
                return item;
            }
        }
        return null;
    }

    private static String describeBag(Char me) {
        StringBuffer out = new StringBuffer("[");
        Item[] bag = me.arrItemBag;
        for (int i = 0; bag != null && i < bag.length; i++) {
            Item item = bag[i];
            if (item == null || item.template == null) {
                continue;
            }
            out.append(item.template.id).append(':').append(item.template.name).append('/')
                    .append(item.template.type).append('x').append(item.quantity).append(' ');
        }
        return out.append(']').toString();
    }

    private static String describeGround() {
        StringBuffer out = new StringBuffer("[");
        for (int i = 0; i < GameScr.vItemMap.size(); i++) {
            ItemMap item = (ItemMap) GameScr.vItemMap.elementAt(i);
            if (item != null && item.template != null) {
                out.append(item.template.id).append(':').append(item.template.name).append('/')
                        .append(item.template.type).append(' ');
            }
        }
        return out.append(']').toString();
    }

    private static String describeMobs() {
        StringBuffer sb = new StringBuffer("[");
        for (int i = 0; i < GameScr.vMob.size(); i++) {
            Mob mob = (Mob) GameScr.vMob.elementAt(i);
            if (i > 0) {
                sb.append(", ");
            }
            sb.append('#').append(mob.templateId).append(" hp=").append(mob.hp).append('/').append(mob.maxHp)
                    .append(" boss=").append(mob.levelBoss);
        }
        return sb.append(']').toString();
    }
}
