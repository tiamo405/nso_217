/**
 * Bình HP lv30 cho riêng nhiệm vụ 23 (TaskMatsurugi35, bước "Nhặt tấm địa đồ").
 *
 *  - ensureStock(): trước khi dùng chìa, túi phải có >= MIN bình; thiếu thì ghé Làng chài
 *    (map 32, NPC 3, shop 7 — nằm sẵn trên đường Oshin -> 32 -> 35) mua bù đủ STOCK.
 *  - drink(): trong map ẩn, HP <= 90% thì uống bình (đợi hiệu ứng hồi máu id 21 hết).
 *    Trong lúc đó tắt tự uống gốc của game (Uplevel35Support.suppressNativeHp) để không uống trùng
 *    hoặc uống nhầm bình cấp thấp.
 */
final class M35HpPotions {
    static final int STOCK = Integer.getInteger("nso.uplevel35.m35.hpStock", 100).intValue();
    static final int MIN = Integer.getInteger("nso.uplevel35.m35.hpMin", 50).intValue();
    static final int DRINK_PERCENT = Integer.getInteger("nso.uplevel35.m35.hpPercent", 90).intValue();
    static final int POTION_LEVEL = 30;
    static final int HP_TYPE = 16;
    static final int SHOP_MAP = 32;
    static final int SHOP_NPC = 3;
    static final int SHOP_ID = 7;
    static final int HP_REGEN_EFFECT = 21;
    private static final long BUY_RETRY_MS = 30000L;
    private static final int MAX_BUY_FAILS = 3;

    /** id template bình đã mua (học từ shop); -1 = nhận theo type 16 + level 30. */
    private int potionId = -1;
    private int buyFails;
    private long retryAt;
    private long lastDrinkAt;
    private int drinks;

    void reset() {
        this.buyFails = 0;
        this.retryAt = 0L;
        this.lastDrinkAt = 0L;
        this.drinks = 0;
        Uplevel35Support.suppressNativeHp = false;
    }

    private boolean isPotion(Item item) {
        if (item == null || item.template == null || item.quantity <= 0) {
            return false;
        }
        if (this.potionId >= 0) {
            return item.template.id == this.potionId;
        }
        return item.template.type == HP_TYPE && item.template.level == POTION_LEVEL;
    }

    int count(Char me) {
        int total = 0;
        for (int i = 0; me.arrItemBag != null && i < me.arrItemBag.length; i++) {
            if (this.isPotion(me.arrItemBag[i])) {
                total += me.arrItemBag[i].quantity;
            }
        }
        return total;
    }

    private Item find(Char me) {
        for (int i = 0; me.arrItemBag != null && i < me.arrItemBag.length; i++) {
            if (this.isPotion(me.arrItemBag[i])) {
                return me.arrItemBag[i];
            }
        }
        return null;
    }

    // ------------------------------------------------------------------ mua

    /** true = đủ bình (hoặc mua lỗi quá nhiều lần, cho đi đánh luôn); false = đang đi mua. */
    boolean ensureStock(As35 bot, Char me) {
        int have = this.count(me);
        if (have >= MIN) {
            return true;
        }
        long now = System.currentTimeMillis();
        if (now < this.retryAt) {
            if (this.buyFails >= MAX_BUY_FAILS) {
                return true;
            }
            return false;
        }
        if (this.buyFails >= MAX_BUY_FAILS) {
            // Hết thời gian chờ sau 3 lần lỗi: thử lại từ đầu.
            this.buyFails = 0;
        }
        Uplevel35.logEvery("hppot-stock", 10000L, "HPPOT kho=" + have + "/" + MIN + " -> mua đủ " + STOCK
                + " ở map " + SHOP_MAP + " npc " + SHOP_NPC);
        if (TileMap.mapID != SHOP_MAP) {
            bot.goMap(SHOP_MAP);
            return false;
        }
        if (Uplevel35Support.walkToNpc(SHOP_NPC) == null) {
            if (GameScr.fieldAI(SHOP_NPC) == null) {
                this.fail(now, "không thấy npc " + SHOP_NPC + " trên map " + TileMap.mapID);
            }
            return false;
        }

        GameScr.arrItemStackLock = null;
        GameScr.fieldAB(SHOP_NPC, 0, 0);
        Service.gI().requestItem(SHOP_ID);
        long deadline = System.currentTimeMillis() + 5000L;
        while (GameScr.arrItemStackLock == null && System.currentTimeMillis() < deadline) {
            Auto.fieldAA(100L);
        }
        Item[] shop = GameScr.arrItemStackLock;
        if (shop == null) {
            this.fail(now, "shop " + SHOP_ID + " không phản hồi");
            return false;
        }
        Item offer = bestOffer(shop, me.clevel);
        if (offer == null) {
            this.fail(now, "shop không có bình HP: " + describe(shop));
            return false;
        }
        if (offer.template.level != POTION_LEVEL) {
            Uplevel35.logEvery("hppot-level", 60000L, "HPPOT shop không có bình lv" + POTION_LEVEL
                    + ", dùng " + offer.template.name + " lv" + offer.template.level + " shop=" + describe(shop));
        }
        this.potionId = offer.template.id;
        int before = this.count(me);
        int need = STOCK - before;
        Uplevel35.log("HPPOT mua " + offer.template.name + " id=" + offer.template.id + " lv" + offer.template.level
                + " sl=" + need + " giá=" + offer.buyCoin + " yên=" + me.yen + " shopIndex=" + offer.indexUI);
        Service.gI().buyItem(offer.typeUI, offer.indexUI, need);
        deadline = System.currentTimeMillis() + 5000L;
        while (this.count(me) <= before && System.currentTimeMillis() < deadline) {
            Auto.fieldAA(100L);
        }
        int after = this.count(me);
        if (after <= before) {
            this.fail(now, "mua xong nhưng túi chưa tăng (" + before + ") yên=" + me.yen);
            return false;
        }
        this.buyFails = 0;
        Uplevel35.log("HPPOT đã mua: kho " + before + " -> " + after + " yên=" + me.yen);
        return after >= MIN;
    }

    private void fail(long now, String reason) {
        ++this.buyFails;
        this.retryAt = now + BUY_RETRY_MS;
        Uplevel35.log("HPPOT mua lỗi lần " + this.buyFails + "/" + MAX_BUY_FAILS + ": " + reason
                + (this.buyFails >= MAX_BUY_FAILS ? " -> đi đánh luôn, thử lại sau" : " -> thử lại sau 30s"));
    }

    /** Bình HP (type 16) level cao nhất không vượt level nhân vật, ưu tiên đúng lv30. */
    private static Item bestOffer(Item[] shop, int charLevel) {
        Item best = null;
        for (int i = 0; i < shop.length; i++) {
            Item item = shop[i];
            if (item == null || item.template == null || item.template.type != HP_TYPE
                    || item.template.level > charLevel) {
                continue;
            }
            if (item.template.level == POTION_LEVEL) {
                return item;
            }
            if (best == null || item.template.level > best.template.level) {
                best = item;
            }
        }
        return best;
    }

    private static String describe(Item[] shop) {
        StringBuffer out = new StringBuffer("[");
        for (int i = 0; i < shop.length; i++) {
            Item it = shop[i];
            if (it != null && it.template != null) {
                out.append(it.template.id).append(':').append(it.template.name).append("/t")
                        .append(it.template.type).append("/lv").append(it.template.level).append(' ');
            }
        }
        return out.append(']').toString();
    }

    // ------------------------------------------------------------------ uống

    /** Gọi mỗi tick khi đang ở map ẩn. */
    void drink(Char me) {
        Uplevel35Support.suppressNativeHp = true;
        if (me.cHP <= 0 || me.cMaxHP <= 0 || (long) me.cHP * 100L > (long) me.cMaxHP * DRINK_PERCENT) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastDrinkAt < 1000L || regenActive(me)) {
            return;
        }
        Item potion = this.find(me);
        if (potion == null) {
            Uplevel35.logEvery("hppot-empty", 30000L, "HPPOT hết bình trong map ẩn hp=" + me.cHP + "/" + me.cMaxHP
                    + ", đánh tiếp (không rời map)");
            return;
        }
        this.lastDrinkAt = now;
        ++this.drinks;
        Uplevel35.logEvery("hppot-drink", 5000L, "HPPOT uống hp=" + (me.cHP * 100 / me.cMaxHP) + "% còn="
                + (this.count(me) - 1) + " đã uống lượt này=" + this.drinks);
        Service.gI().useItem(potion.indexUI);
    }

    /** Rời map ẩn (chết hoặc xong): bật lại tự uống gốc, in tổng số bình đã uống. */
    void leaveHidden(Char me, String why) {
        Uplevel35Support.suppressNativeHp = false;
        if (this.drinks > 0) {
            Uplevel35.log("HPPOT lượt map ẩn kết thúc (" + why + "): đã uống " + this.drinks + " bình, còn "
                    + this.count(me));
        }
        this.drinks = 0;
    }

    private static boolean regenActive(Char me) {
        for (int i = 0; me.vEff != null && i < me.vEff.size(); i++) {
            Effect effect = (Effect) me.vEff.elementAt(i);
            if (effect != null && effect.template != null && effect.template.id == HP_REGEN_EFFECT
                    && Uplevel35Support.active(effect)) {
                return true;
            }
        }
        return false;
    }
}
