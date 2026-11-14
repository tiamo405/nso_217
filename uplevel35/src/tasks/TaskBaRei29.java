/**
 * Nhiệm vụ Bà Rei (Làng Chài, npc 18 map 32) — "NV Tìm lại cây rìu", mốc level 29.
 *
 *  bước 0: đạt level 29 -> đánh quái map 49
 *  bước 1: "Giết heo rừng và nhặt cây rìu": map 8 gặp npc Fujiwara (id 15, server gửi
 *          trong mapTasks/tasks) -> được đưa vào map ẩn -> đánh Thủ lĩnh heo rừng (chỉ 1 con)
 *          -> nhặt rìu rơi ra. Server tự chuyển sang bước 2 khi nhặt được.
 *  bước 2 (cuối): As35 tự về Bà Rei trả nhiệm vụ.
 */
final class TaskBaRei29 extends Uplevel35Task implements As35.NpcMessageListener {
    static final int TASK_ID = Integer.getInteger("nso.uplevel35.task.barei29", 20).intValue();
    static final int LEVEL_REQUIRED = 29;
    static final int LEVEL_MAP = 49;
    static final int NPC_MAP = 8;
    static final int NPC_FUJIWARA = 15;
    static final String AXE_NAME = "rìu";
    /**
     * Rìu Bạc (id 221, README). Trước đây tra id qua As35.itemIdByName("rìu"): duyệt Hashtable theo thứ tự
     * bất kỳ nên có thể trả về món khác có chữ "rìu" -> không bao giờ nhặt đúng rìu.
     */
    static final int AXE_ID = Integer.getInteger("nso.uplevel35.barei29.axe", 221).intValue();
    /** Hang Inoshishi — map ẩn Fujiwara đưa vào (đã thấy trong log). */
    static final int HIDDEN_MAP = Integer.getInteger("nso.uplevel35.barei29.hidden", 74).intValue();

    /** Map ẩn đã vào (học được khi rời map 8 ngay sau khi nói chuyện với Fujiwara). */
    private int hiddenMap = HIDDEN_MAP;
    private long talkedAt;
    private int talks;
    private int lastMap = -1;
    private long pickAt;
    private int pickTries;

    boolean canLeaveMap(int mapId) {
        return mapId != this.hiddenMap;
    }

    int taskId() {
        return TASK_ID;
    }

    String label() {
        return "BaRei29";
    }

    void reset() {
        this.hiddenMap = HIDDEN_MAP;
        this.talkedAt = 0L;
        this.talks = 0;
        this.lastMap = -1;
        this.pickAt = 0L;
        this.pickTries = 0;
    }

    void tick(As35 bot, Char me, Task task) {
        switch (task.index) {
            case 0:
                bot.levelUp(LEVEL_REQUIRED, LEVEL_MAP, -1);
                return;
            case 1:
                this.killBoar(bot, me, task);
                return;
            default:
                Uplevel35.logEvery("barei29-step", 10000L, "BaRei29 bước " + task.index
                        + " chưa có logic: " + As35.describeTask(me));
        }
    }

    private void killBoar(As35 bot, Char me, Task task) {
        int map = TileMap.mapID;
        if (map != this.lastMap) {
            Uplevel35.log("BOAR vào map " + map + " (" + TileMap.mapName + ") từ " + this.lastMap
                    + " pos=" + me.cx + "," + me.cy + " mobs=" + describeMobs());
            if (this.lastMap == NPC_MAP && this.talkedAt > 0L
                    && System.currentTimeMillis() - this.talkedAt < 30000L) {
                this.hiddenMap = map;
                Uplevel35.log("BOAR map ẩn = " + map);
            }
            this.lastMap = map;
        }

        if (map == this.hiddenMap) {
            // Chỉ có 1 Thủ lĩnh heo rừng: Auto.fieldAC chỉ nhặt trong 150px và thử 1 lần, rìu rơi xa
            // hoặc nhặt hụt là kẹt mãi -> tự đi tới rìu và nhặt lại tới khi nó biến khỏi mặt đất.
            ItemMap axe = findAxe();
            if (axe != null) {
                this.pickAxe(me, axe);
                return;
            }
            Uplevel35.logEvery("boar-fight", 5000L, "BOAR đánh trong map ẩn " + map + " axeId=" + AXE_ID
                    + " mobs=" + describeMobs() + " items=" + GameScr.vItemMap.size());
            bot.pickAndFight(AXE_ID, -1);
            return;
        }

        if (map != NPC_MAP) {
            if (bot.goMap(NPC_MAP)) {
                this.lastMap = NPC_MAP;
            }
            return;
        }

        Npc npc = Uplevel35Support.walkToNpc(NPC_FUJIWARA);
        if (npc == null) {
            Uplevel35.logEvery("boar-npc", 5000L, "BOAR chưa thấy npc " + NPC_FUJIWARA + " trên map " + map);
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.talkedAt < 6000L) {
            return;
        }
        this.talkedAt = now;
        ++this.talks;
        NpcTemplate template = npc.template;
        Uplevel35.log("BOAR nói chuyện Fujiwara lần " + this.talks + " npc=" + NPC_FUJIWARA
                + " name=" + (template == null ? "?" : template.name)
                + " menu=" + (template == null || template.menu == null ? "null"
                : java.util.Arrays.deepToString(template.menu)));
        GameScr.fieldAB(NPC_FUJIWARA, 0, 0);
        Service.gI().getTask(NPC_FUJIWARA, 0, -1);
        LockGame.fieldAO();
        if (GameCanvas.menu != null && GameCanvas.menu.showMenu) {
            Uplevel35.log("BOAR menu đang mở: " + As35.describeMenu());
        }
    }

    /** Rìu trên đất: đúng id, hoặc (phòng id khác bản server) tên chứa "rìu". */
    private static ItemMap findAxe() {
        for (int i = 0; i < GameScr.vItemMap.size(); i++) {
            ItemMap item = (ItemMap) GameScr.vItemMap.elementAt(i);
            if (item == null || item.template == null) {
                continue;
            }
            String name = item.template.name == null ? "" : item.template.name.toLowerCase();
            if (item.template.id == AXE_ID || name.indexOf(AXE_NAME) >= 0) {
                return item;
            }
        }
        return null;
    }

    private void pickAxe(Char me, ItemMap item) {
        if (Math.abs(me.cx - item.xEnd) > 24 || Math.abs(me.cy - item.yEnd) > 24) {
            Uplevel35.logEvery("boar-pick-walk", 5000L, "BOAR PICK đi tới " + item.template.name + " @"
                    + item.xEnd + "," + item.yEnd + " từ " + me.cx + "," + me.cy);
            Char.fieldAC(item.xEnd, item.yEnd);
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.pickAt < 1500L) {
            return;
        }
        this.pickAt = now;
        ++this.pickTries;
        Uplevel35.log("BOAR PICK nhặt " + item.template.name + " id=" + item.template.id + " itemMapID="
                + item.itemMapID + " lần=" + this.pickTries + " pos=" + me.cx + "," + me.cy);
        Service.gI().pickItem(item.itemMapID);
    }

    public void onNpcMessage(As35 bot, int npcId, String text) {
        Uplevel35.log("BOAR npc " + npcId + " nói: " + text);
    }

    private static String describeMobs() {
        StringBuffer sb = new StringBuffer("[");
        for (int i = 0; i < GameScr.vMob.size(); i++) {
            Mob mob = (Mob) GameScr.vMob.elementAt(i);
            if (i > 0) {
                sb.append(", ");
            }
            sb.append('#').append(mob.templateId).append(" hp=").append(mob.hp).append('/').append(mob.maxHp)
                    .append(" status=").append(mob.status)
                    .append(" boss=").append(mob.levelBoss).append(" @").append(mob.x).append(',').append(mob.y);
        }
        return sb.append(']').toString();
    }
}
