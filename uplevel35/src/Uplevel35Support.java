/**
 * Các bước "giữ buff" chạy trước nhiệm vụ: Nấm linh chi (x2 exp), Mặt nạ/Mũ Noel,
 * thức ăn theo level. Mỗi hàm ensureXxx() trả về true khi đã sẵn sàng (cho phép
 * làm nhiệm vụ tiếp), false khi còn đang bận (đi map, chờ server...).
 *
 * Logic port từ uplevel/overrides/As20.java nhưng tách riêng để sửa độc lập.
 */
final class Uplevel35Support {
    static final int OKAZA_MAP = 72;
    static final int GOOSHO_NPC = 30;
    static final int FOOD_NPC = 4;
    static final int FOOD_TYPE = 18;
    static final int FOOD_STOCK = 2;
    static final int MUSHROOM_ITEM_ID = 248;
    static final int MUSHROOM_PRICE = 10;
    static final int MUSHROOM_SHOP = 14;
    /** Effect id của Nấm linh chi (đo trên acctest2 ngày 2026-11-07). */
    static final int MUSHROOM_EFFECT_ID = 22;
    static final int NOEL_HAT_MALE_ID = 351;
    static final int NOEL_HAT_FEMALE_ID = 352;
    static final int FASHION_SHOP = 32;
    static final int BOX_REQUEST = 4;
    private static final long RETRY_DELAY = 10L * 60L * 1000L;

    private final As35 bot;
    /** Task đang tự uống bình HP (M35HpPotions trong map ẩn): tắt tự uống gốc của game. */
    static volatile boolean suppressNativeHp;

    private long mushroomUseSentAt;
    private int mushroomUseAttempts;
    private int mushroomQuantityBefore;
    private long mushroomRetryAt;

    private boolean noelReady;
    private int noelUseSlot = -1;
    private long noelUseSentAt;
    private int noelUseAttempts;

    private int foodLevel = -1;
    private boolean foodBoxChecked;
    private boolean foodBuyPending;
    private long foodBuySentAt;
    private long foodLastBuyAt;
    private long foodUseSentAt;

    private final java.util.Hashtable bookTried = new java.util.Hashtable();
    private int bookUseSlot = -1;
    private int bookUseQty;
    private long bookUseSentAt;

    Uplevel35Support(As35 bot) {
        this.bot = bot;
    }

    // ------------------------------------------------------------------ chung

    static Item findItem(Item[] items, int templateId) {
        for (int i = 0; items != null && i < items.length; i++) {
            Item item = items[i];
            if (item != null && item.template != null && item.template.id == templateId) {
                return item;
            }
        }
        return null;
    }

    static int countItem(Item[] items, int templateId) {
        int count = 0;
        for (int i = 0; items != null && i < items.length; i++) {
            Item item = items[i];
            if (item != null && item.template != null && item.template.id == templateId) {
                count += item.quantity;
            }
        }
        return count;
    }

    static Item waitBagItem(int templateId, long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        Item item;
        while ((item = findItem(Char.getMyChar().arrItemBag, templateId)) == null
                && System.currentTimeMillis() < deadline) {
            Auto.fieldAA(100L);
        }
        return item;
    }

    /** Yêu cầu server gửi dữ liệu rương, chờ tối đa timeoutMs. */
    static boolean refreshBox(long timeoutMs) {
        Char me = Char.getMyChar();
        me.arrItemBox = null;
        Service.gI().requestItem(BOX_REQUEST);
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (Char.getMyChar().arrItemBox == null && System.currentTimeMillis() < deadline) {
            Auto.fieldAA(100L);
        }
        return Char.getMyChar().arrItemBox != null;
    }

    /** Lấy item từ rương sang hành trang nếu hành trang chưa có. */
    static Item takeFromBox(int templateId) {
        Item bagItem = findItem(Char.getMyChar().arrItemBag, templateId);
        if (bagItem != null) {
            return bagItem;
        }
        if (!refreshBox(5000L)) {
            Uplevel35.log("BOX không tải được rương khi tìm item id=" + templateId);
            return null;
        }
        Item boxItem = findItem(Char.getMyChar().arrItemBox, templateId);
        if (boxItem == null) {
            return null;
        }
        Uplevel35.log("BOX -> bag id=" + templateId + " boxIndex=" + boxItem.indexUI);
        Service.gI().itemBoxToBag(boxItem.indexUI);
        return waitBagItem(templateId, 5000L);
    }

    /** Đi tới NPC trên map hiện tại; trả về NPC khi đã đứng cạnh. */
    static Npc walkToNpc(int npcId) {
        Npc npc = GameScr.fieldAI(npcId);
        if (npc == null) {
            return null;
        }
        Char me = Char.getMyChar();
        if (Math.abs(npc.cx - me.cx) > 22 || Math.abs(npc.cy - me.cy) > 22) {
            Char.fieldAC(npc.cx, npc.cy);
            return null;
        }
        return npc;
    }

    static int schoolMap(Char me) {
        int classId = me.nClass == null ? 0 : me.nClass.classId;
        return classId <= 2 ? 1 : (classId <= 4 ? 27 : 72);
    }

    static String effectsLog(Char me) {
        StringBuilder out = new StringBuilder("[");
        int nowSec = (int) (System.currentTimeMillis() / 1000L);
        for (int i = 0; me.vEff != null && i < me.vEff.size(); i++) {
            Effect effect = (Effect) me.vEff.elementAt(i);
            if (effect == null || effect.template == null) {
                continue;
            }
            if (out.length() > 1) {
                out.append(',');
            }
            out.append(effect.template.id).append("/t").append(effect.template.type)
                    .append("/left").append(effect.timeLenght - (nowSec - effect.timeStart)).append('s');
        }
        return out.append(']').toString();
    }

    /**
     * Hiệu ứng còn thời gian. Client headless không xóa hiệu ứng hết hạn khỏi me.vEff
     * (log worker-23 ngày 2026-11-10: thức ăn "left-42190s", Nấm "left-25990s" vẫn nằm trong danh sách)
     * -> trước đây bot tưởng còn no / còn Nấm nên không bao giờ ăn hay mua lại.
     */
    static boolean active(Effect effect) {
        if (effect.timeLenght <= 0) {
            return true; // hiệu ứng không thời hạn
        }
        int nowSec = (int) (System.currentTimeMillis() / 1000L);
        return effect.timeLenght - (nowSec - effect.timeStart) > 0;
    }

    private static boolean hasEffectId(Char me, int effectId) {
        for (int i = 0; me.vEff != null && i < me.vEff.size(); i++) {
            Effect effect = (Effect) me.vEff.elementAt(i);
            if (effect != null && effect.template != null && effect.template.id == effectId && active(effect)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasEffectType(Char me, int type) {
        for (int i = 0; me.vEff != null && i < me.vEff.size(); i++) {
            Effect effect = (Effect) me.vEff.elementAt(i);
            if (effect != null && effect.template != null && effect.template.type == type && active(effect)) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------- Nấm linh chi

    /** Giữ hiệu ứng x2 kinh nghiệm; hết thì mua ở Goosho (10 lượng) và dùng. */
    boolean ensureMushroom(Char me) {
        long now = System.currentTimeMillis();
        if (this.mushroomUseSentAt > 0L) {
            int quantity = countItem(me.arrItemBag, MUSHROOM_ITEM_ID);
            if (hasEffectId(me, MUSHROOM_EFFECT_ID) || quantity < this.mushroomQuantityBefore) {
                Uplevel35.log("MUSHROOM đã dùng luong=" + me.luong + " effects=" + effectsLog(me));
                this.mushroomUseSentAt = 0L;
                this.mushroomUseAttempts = 0;
                return true;
            }
            if (now - this.mushroomUseSentAt < 8000L) {
                return false;
            }
            this.mushroomUseSentAt = 0L;
            Uplevel35.log("MUSHROOM dùng bị timeout lần=" + this.mushroomUseAttempts);
            if (this.mushroomUseAttempts >= 3) {
                this.mushroomUseAttempts = 0;
                this.mushroomRetryAt = now + RETRY_DELAY;
                Uplevel35.log("MUSHROOM dùng thất bại, thử lại sau 10 phút");
                return true;
            }
        }
        if (hasEffectId(me, MUSHROOM_EFFECT_ID) || now < this.mushroomRetryAt) {
            return true;
        }

        Item mushroom = findItem(me.arrItemBag, MUSHROOM_ITEM_ID);
        if (mushroom == null) {
            if (me.luong < MUSHROOM_PRICE) {
                Uplevel35.log("MUSHROOM không đủ lượng=" + me.luong + ", thử lại sau 10 phút");
                this.mushroomRetryAt = now + RETRY_DELAY;
                return true;
            }
            if (GameScr.fieldAI(GOOSHO_NPC) == null && TileMap.mapID != OKAZA_MAP) {
                if (!this.bot.canTravelForSupplies()) {
                    Uplevel35.logEvery("mushroom-hold", 60000L, "MUSHROOM hết nhưng đang ở map ẩn " + TileMap.mapID + ", mua sau");
                    return true;
                }
                Uplevel35.logEvery("mushroom-route", 5000L, "MUSHROOM đi map " + TileMap.mapID + " -> " + OKAZA_MAP);
                this.bot.goMap(OKAZA_MAP);
                return false;
            }
            GameScr.arrItemStore = null;
            GameScr.fieldAB(GOOSHO_NPC, 0, 0);
            Service.gI().requestItem(MUSHROOM_SHOP);
            long deadline = System.currentTimeMillis() + 5000L;
            while (GameScr.arrItemStore == null && System.currentTimeMillis() < deadline) {
                Auto.fieldAA(100L);
            }
            Item offer = findItem(GameScr.arrItemStore, MUSHROOM_ITEM_ID);
            if (offer == null) {
                Uplevel35.log("MUSHROOM không thấy trong shop " + MUSHROOM_SHOP + ", thử lại sau 10 phút");
                this.mushroomRetryAt = now + RETRY_DELAY;
                return true;
            }
            int price = offer.buyGold > 0 ? offer.buyGold : MUSHROOM_PRICE;
            if (me.luong < price) {
                Uplevel35.log("MUSHROOM không đủ lượng=" + me.luong + " giá=" + price);
                this.mushroomRetryAt = now + RETRY_DELAY;
                return true;
            }
            Uplevel35.log("MUSHROOM mua shopIndex=" + offer.indexUI + " giá=" + price + " lượng=" + me.luong);
            Service.gI().buyItem(offer.typeUI, offer.indexUI, 1);
            mushroom = waitBagItem(MUSHROOM_ITEM_ID, 5000L);
            if (mushroom == null) {
                Uplevel35.log("MUSHROOM mua xong nhưng chưa thấy trong hành trang, thử lại sau 1 phút");
                this.mushroomRetryAt = now + 60000L;
                return true;
            }
        }

        this.mushroomQuantityBefore = countItem(me.arrItemBag, MUSHROOM_ITEM_ID);
        this.mushroomUseSentAt = now;
        ++this.mushroomUseAttempts;
        Uplevel35.log("MUSHROOM dùng bagIndex=" + mushroom.indexUI + " sl=" + mushroom.quantity);
        Service.gI().useItem(mushroom.indexUI);
        return false;
    }

    // ------------------------------------------------------------ Mũ Noel

    private static int noelHatId(int gender) {
        ItemTemplate male = ItemTemplates.gameAA((short) NOEL_HAT_MALE_ID);
        ItemTemplate female = ItemTemplates.gameAA((short) NOEL_HAT_FEMALE_ID);
        if (male != null && male.gender == gender) {
            return NOEL_HAT_MALE_ID;
        }
        if (female != null && female.gender == gender) {
            return NOEL_HAT_FEMALE_ID;
        }
        return gender == 0 ? NOEL_HAT_MALE_ID : NOEL_HAT_FEMALE_ID;
    }

    private static boolean noelEquipped(Char me, int hatId) {
        return findItem(me.arrItemBody, hatId) != null;
    }

    /** Đeo Mặt nạ/Mũ Noel: tìm trong túi -> rương -> mua ở Goosho (shop thời trang). */
    boolean ensureNoelHat(Char me) {
        if (this.noelReady) {
            return true;
        }
        int hatId = noelHatId(me.cgender);
        if (noelEquipped(me, hatId)) {
            Uplevel35.log("NOEL đang đeo id=" + hatId);
            this.noelReady = true;
            return true;
        }
        if (this.noelUseSlot >= 0) {
            if (me.arrItemBag == null || this.noelUseSlot >= me.arrItemBag.length
                    || me.arrItemBag[this.noelUseSlot] == null) {
                Uplevel35.log("NOEL đã dùng id=" + hatId);
                this.noelUseSlot = -1;
                this.noelReady = true;
                return true;
            }
            if (System.currentTimeMillis() - this.noelUseSentAt < 8000L) {
                return false;
            }
            this.noelUseSlot = -1;
            if (this.noelUseAttempts >= 3) {
                Uplevel35.log("NOEL dùng timeout id=" + hatId + ", bỏ qua");
                this.noelReady = true;
                return true;
            }
        }

        Item hat = findItem(me.arrItemBag, hatId);
        if (hat == null) {
            hat = takeFromBox(hatId);
        }
        if (hat == null) {
            if (TileMap.mapID != OKAZA_MAP) {
                if (!this.bot.canTravelForSupplies()) {
                    return true;
                }
                Uplevel35.logEvery("noel-route", 5000L, "NOEL đi map " + TileMap.mapID + " -> " + OKAZA_MAP);
                this.bot.goMap(OKAZA_MAP);
                return false;
            }
            GameScr.arrItemFashion = null;
            GameScr.fieldAB(GOOSHO_NPC, 0, 0);
            Service.gI().requestItem(FASHION_SHOP);
            long deadline = System.currentTimeMillis() + 5000L;
            while (GameScr.arrItemFashion == null && System.currentTimeMillis() < deadline) {
                Auto.fieldAA(100L);
            }
            Item shopHat = findItem(GameScr.arrItemFashion, hatId);
            if (shopHat == null) {
                Uplevel35.log("NOEL không có trong shop thời trang id=" + hatId + ", bỏ qua");
                this.noelReady = true;
                return true;
            }
            Uplevel35.log("NOEL mua id=" + hatId + " shopIndex=" + shopHat.indexUI);
            Service.gI().buyItem(shopHat.typeUI, shopHat.indexUI, 1);
            hat = waitBagItem(hatId, 5000L);
            if (hat == null) {
                hat = takeFromBox(hatId);
            }
            if (hat == null) {
                Uplevel35.log("NOEL mua xong nhưng không lấy được vào túi id=" + hatId + ", bỏ qua");
                this.noelReady = true;
                return true;
            }
        }
        Uplevel35.log("NOEL dùng id=" + hatId + " bagIndex=" + hat.indexUI);
        this.noelUseSlot = hat.indexUI;
        this.noelUseSentAt = System.currentTimeMillis();
        ++this.noelUseAttempts;
        Service.gI().useItem(hat.indexUI);
        return false;
    }

    // ------------------------------------------------------------ Thức ăn

    static int foodLevelFor(Char me) {
        int level = me.clevel / 10 * 10;
        if (level < 10) {
            return 1;
        }
        return level > 50 ? 50 : level;
    }

    private static Item findFood(Item[] items, int level) {
        for (int i = 0; items != null && i < items.length; i++) {
            Item item = items[i];
            if (item != null && item.template != null && item.template.type == FOOD_TYPE
                    && item.template.level == level && item.quantity > 0) {
                return item;
            }
        }
        return null;
    }

    private static int countFood(Item[] items, int level) {
        int count = 0;
        for (int i = 0; items != null && i < items.length; i++) {
            Item item = items[i];
            if (item != null && item.template != null && item.template.type == FOOD_TYPE
                    && item.template.level == level) {
                count += item.quantity;
            }
        }
        return count;
    }

    /** Luôn có hiệu ứng thức ăn đúng level; thiếu thì lấy từ rương hoặc mua ở trường. */
    boolean ensureFood(Char me) {
        int level = foodLevelFor(me);
        Char.aHpValue = level;
        Char.aMpValue = level;
        Char.aFoodValue = level;
        Char.isAHP = !suppressNativeHp;
        Char.isAMP = true;
        Char.isAFood = true;
        if (this.foodLevel != level) {
            this.foodLevel = level;
            this.foodBoxChecked = false;
            Uplevel35.log("FOOD bật tự dùng thức ăn level=" + level);
        }

        long now = System.currentTimeMillis();
        Item food = findFood(me.arrItemBag, level);
        if (food != null) {
            this.foodBuyPending = false;
            if (!hasEffectType(me, 0) && now - this.foodUseSentAt >= 5000L) {
                this.foodUseSentAt = now;
                Uplevel35.log("FOOD dùng level=" + level + " bagIndex=" + food.indexUI);
                Service.gI().useItem(food.indexUI);
                return false;
            }
            return true;
        }

        if (!this.foodBoxChecked) {
            this.foodBoxChecked = true;
            if (refreshBox(5000L)) {
                Item boxFood = findFood(me.arrItemBox, level);
                if (boxFood != null) {
                    Uplevel35.log("FOOD rương -> túi level=" + level + " boxIndex=" + boxFood.indexUI);
                    Service.gI().itemBoxToBag(boxFood.indexUI);
                    Auto.fieldAA(1500L);
                    return false;
                }
            }
        }

        if (this.foodBuyPending) {
            if (now - this.foodBuySentAt < 10000L) {
                return false;
            }
            Uplevel35.log("FOOD mua timeout level=" + level + ", tiếp tục nhiệm vụ");
            this.foodBuyPending = false;
        }
        if (now - this.foodLastBuyAt < 30000L) {
            return true;
        }

        int school = schoolMap(me);
        if (TileMap.mapID != school) {
            if (!this.bot.canTravelForSupplies()) {
                Uplevel35.logEvery("food-hold", 60000L, "FOOD hết thức ăn level=" + level
                        + " nhưng đang ở map ẩn " + TileMap.mapID + ", mua sau khi ra");
                return true;
            }
            Uplevel35.logEvery("food-route", 5000L, "FOOD về trường map " + TileMap.mapID + " -> " + school);
            this.bot.goMap(school);
            return false;
        }
        if (walkToNpc(FOOD_NPC) == null) {
            return false;
        }
        int missing = FOOD_STOCK - countFood(me.arrItemBag, level);
        if (missing < 1) {
            return true;
        }
        int shopIndex = level == 50 ? 7 : level / 10;
        GameScr.fieldAB(FOOD_NPC, 0, 0);
        Service.gI().buyItem1(9, shopIndex, missing);
        this.foodBuyPending = true;
        this.foodBuySentAt = now;
        this.foodLastBuyAt = now;
        Uplevel35.log("FOOD mua level=" + level + " sl=" + missing + " shopIndex=" + shopIndex);
        return false;
    }

    // ---------------------------------------------------------- Sách kỹ năng

    /** Skill của lớp mình mà tên sách chứa tên skill (vd. "... X Zangeki" -> skill "X Zangeki"). */
    private static SkillTemplate skillForBook(Char me, ItemTemplate book) {
        if (me.nClass == null || me.nClass.skillTemplates == null || book == null || book.name == null) {
            return null;
        }
        String name = book.name.toLowerCase();
        SkillTemplate best = null;
        int bestLen = 0;
        for (int i = 0; i < me.nClass.skillTemplates.length; i++) {
            SkillTemplate skill = me.nClass.skillTemplates[i];
            String skillName = skillCoreName(skill);
            if (skillName.length() > 2 && name.indexOf(skillName) >= 0 && skillName.length() > bestLen) {
                best = skill;
                bestLen = skillName.length();
            }
        }
        return best;
    }

    /** Tên skill bỏ tiền tố "Chiêu " (skill "Chiêu X Zangeki" <-> sách "Sách võ công X Zangeki"). */
    private static String skillCoreName(SkillTemplate skill) {
        if (skill == null || skill.name == null) {
            return "";
        }
        String name = skill.name.toLowerCase().trim();
        return name.startsWith("chiêu ") ? name.substring(6).trim() : name;
    }

    /**
     * Học sách kỹ năng đang nằm trong túi (vd. X Zangeki id 44, cần lv30) khi đủ level và
     * chưa học skill đó. Học xong As35 tự chọn lại skill đánh -> điểm kỹ năng cộng vào skill mới.
     */
    boolean ensureSkillBooks(Char me) {
        long now = System.currentTimeMillis();
        if (this.bookUseSlot >= 0) {
            Item item = me.arrItemBag == null || this.bookUseSlot >= me.arrItemBag.length
                    ? null : me.arrItemBag[this.bookUseSlot];
            boolean used = item == null || item.quantity < this.bookUseQty;
            if (!used && now - this.bookUseSentAt < 5000L) {
                return false;
            }
            Uplevel35.log("BOOK " + (used ? "đã học" : "dùng không phản hồi") + " slot=" + this.bookUseSlot);
            this.bookUseSlot = -1;
            if (used) {
                this.bot.selectAttackSkill(me);
            }
            return true;
        }
        for (int i = 0; me.arrItemBag != null && i < me.arrItemBag.length; i++) {
            Item item = me.arrItemBag[i];
            if (item == null || item.template == null || item.template.level > me.clevel) {
                continue;
            }
            SkillTemplate skill = skillForBook(me, item.template);
            if (skill == null) {
                continue;
            }
            Skill learned = As35.learnedSkill(me, skill.id);
            if (learned != null && learned.point > 0) {
                Uplevel35.logEvery("book-known-" + item.template.id, 600000L, "BOOK đã có skill " + skill.name
                        + " point=" + learned.point + ", sách id=" + item.template.id + " x" + item.quantity
                        + " không cần dùng");
                continue;
            }
            Integer key = new Integer(item.template.id);
            Integer tries = (Integer) this.bookTried.get(key);
            if (tries != null && tries.intValue() >= 3) {
                continue;
            }
            this.bookTried.put(key, new Integer(tries == null ? 1 : tries.intValue() + 1));
            this.bookUseSlot = item.indexUI;
            this.bookUseQty = item.quantity;
            this.bookUseSentAt = now;
            Uplevel35.log("BOOK học " + item.template.name + " id=" + item.template.id + " -> skill "
                    + skill.name + " (lv" + item.template.level + ") bagIndex=" + item.indexUI);
            Service.gI().useItem(item.indexUI);
            return false;
        }
        return true;
    }
}
