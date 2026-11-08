/**
 * Nhiệm vụ Matsurugi (làng Oshin, npc 23 map 48) — mốc level 32.
 *
 *  bước 0: đạt level 32 -> đánh quái map 35
 *  bước 1: đánh Xương khô (mob 33) ở map 35, nhặt "Chìa khóa cơ quan" tới đủ 200
 *  bước cuối: As35 tự về Matsurugi trả nhiệm vụ.
 *
 * Lưu ý: tra tên "chìa khóa" trơn sẽ khớp nhầm "Chìa khóa may mắn" (id 846) -> bot
 * không nhặt chìa khóa nhiệm vụ, count đứng 0. Phải tra đủ "chìa khóa cơ quan".
 *
 * Khu đầu thường đông người tranh quái: nếu count không tăng trong STALL_MS thì đổi khu.
 */
final class TaskMatsurugi32 extends Uplevel35Task {
    static final int TASK_ID = Integer.getInteger("nso.uplevel35.task.matsurugi32", 22).intValue();
    static final int LEVEL_REQUIRED = 32;
    static final int LEVEL_MAP = 35;
    static final int KEY_MAP = 35;
    static final int KEY_MOB = 33;
    /** Ghi đè id chìa khóa bằng -Dnso.uplevel35.key.id nếu tra tên sai. */
    static final int KEY_ID_OVERRIDE = Integer.getInteger("nso.uplevel35.key.id", -1).intValue();
    /** Count không tăng trong khoảng này (đang ở map 35) -> đổi khu. */
    static final long STALL_MS = Long.getLong("nso.uplevel35.key.stallMs", 180000L).longValue();
    static final long HOP_TIMEOUT_MS = 30000L;

    private int lastCount = Integer.MIN_VALUE;
    private long lastProgressAt;
    private int hopFromZone = -1;
    private long hopStartedAt;

    int taskId() {
        return TASK_ID;
    }

    String label() {
        return "Matsurugi32";
    }

    void reset() {
        lastCount = Integer.MIN_VALUE;
        lastProgressAt = 0L;
        hopFromZone = -1;
    }

    /**
     * Có HAI template trùng tên "Chìa khóa cơ quan" (230 và 231); đồ rơi từ quái map 35 là 230.
     * Tra theo tên sẽ ra id ngẫu nhiên theo thứ tự Hashtable, nên: ưu tiên id của đồ đang
     * thực sự nằm trên đất, mặc định 230.
     */
    static final int KEY_ID_DEFAULT = 230;
    private static int learnedKeyId = -1;

    private static int keyId() {
        if (KEY_ID_OVERRIDE >= 0) {
            return KEY_ID_OVERRIDE;
        }
        for (int i = 0; i < GameScr.vItemMap.size(); i++) {
            ItemMap item = (ItemMap) GameScr.vItemMap.elementAt(i);
            if (item != null && item.template != null && item.template.name != null
                    && item.template.id != learnedKeyId
                    && item.template.name.toLowerCase().indexOf("cơ quan") >= 0
                    && item.template.name.toLowerCase().indexOf("chìa kh") >= 0) {
                learnedKeyId = item.template.id;
                Uplevel35.log("Matsurugi32 nhận id chìa khóa từ đồ rơi: " + learnedKeyId);
            }
        }
        return learnedKeyId >= 0 ? learnedKeyId : KEY_ID_DEFAULT;
    }

    void tick(As35 bot, Char me, Task task) {
        switch (task.index) {
            case 0:
                bot.levelUp(LEVEL_REQUIRED, LEVEL_MAP, -1);
                return;
            case 1:
                collectKeys(bot, task);
                return;
            default:
                Uplevel35.logEvery("matsurugi32-step", 10000L, "Matsurugi32 bước " + task.index
                        + " chưa có logic: " + As35.describeTask(me));
        }
    }

    private void collectKeys(As35 bot, Task task) {
        long now = System.currentTimeMillis();
        int key = keyId();
        if (key < 0) {
            Uplevel35.logEvery("matsurugi32-nokey", 10000L,
                    "Matsurugi32 chưa tra được id 'Chìa khóa cơ quan' (template chưa tải?)");
        }
        if (task.count != lastCount) {
            if (lastCount != Integer.MIN_VALUE) {
                Uplevel35.logEvery("matsurugi32-count", 5000L, "Matsurugi32 chìa khóa " + task.count
                        + " (khu " + TileMap.zoneID + ")");
            }
            lastCount = task.count;
            lastProgressAt = now;
        }
        if (TileMap.mapID != KEY_MAP) {
            // Chết/về làng: đếm lại thời gian đứng yên khi quay lại map.
            lastProgressAt = now;
            hopFromZone = -1;
            bot.grind(KEY_MAP, KEY_MOB, key);
            return;
        }

        if (hopFromZone >= 0) {
            if (TileMap.zoneID != hopFromZone) {
                Uplevel35.log("Matsurugi32 đổi khu " + hopFromZone + " -> " + TileMap.zoneID
                        + " (count=" + task.count + ")");
                hopFromZone = -1;
                lastProgressAt = now;
            } else if (now - hopStartedAt > HOP_TIMEOUT_MS) {
                Uplevel35.log("Matsurugi32 đổi khu thất bại sau " + HOP_TIMEOUT_MS + "ms, đánh tiếp khu "
                        + TileMap.zoneID);
                hopFromZone = -1;
                lastProgressAt = now;
            } else {
                bot.stepChangeZone();
                return;
            }
        } else if (now - lastProgressAt > STALL_MS) {
            if (bot.stepChangeZone()) {
                Uplevel35.log("Matsurugi32 count=" + task.count + " đứng yên " + (now - lastProgressAt) / 1000
                        + "s ở khu " + TileMap.zoneID + " -> đổi khu");
                hopFromZone = TileMap.zoneID;
                hopStartedAt = now;
                return;
            }
            Uplevel35.logEvery("matsurugi32-nonpc", 60000L,
                    "Matsurugi32 map " + TileMap.mapID + " không có NPC đổi khu, đánh tiếp khu " + TileMap.zoneID);
            lastProgressAt = now;
        }
        Uplevel35.logEvery("matsurugi32-diag", 30000L, diag(key));
        bot.grind(KEY_MAP, KEY_MOB, key);
    }

    /** Chẩn đoán vì sao không nhặt: ô trống túi (Auto chỉ nhặt khi >2) và đồ trên đất. */
    private static String diag(int key) {
        StringBuffer out = new StringBuffer("Matsurugi32 DIAG key=").append(key)
                .append(" bagFree=").append(Char.fieldBF())
                .append(" bagKey=").append(As35.findBagItemByName("chìa khóa cơ quan") != null)
                .append(" ground=[");
        for (int i = 0; i < GameScr.vItemMap.size(); i++) {
            ItemMap item = (ItemMap) GameScr.vItemMap.elementAt(i);
            if (item == null || item.template == null) {
                continue;
            }
            out.append(item.template.id).append(item.template.id == key ? "*" : "")
                    .append(item.template.id > 20 ? ":" + item.template.name : "")
                    .append(item.fieldAK ? "(x)" : "").append(' ');
        }
        return out.append(']').toString();
    }
}
