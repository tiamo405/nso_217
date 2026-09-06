/**
 * Temporarily leaves a Ta Thu fight to replenish food, then resumes the exact
 * same TaThu auto instance (and therefore its saved mission map/zone lock).
 */
public final class AutoTaThuFoodSupply extends Auto {
    private static final int OKAZA_MAP = 72;
    private static final int FOOD_NPC = 4;
    private static final int TARGET_BAG_FOOD = 2;
    private final TaThu fighter;
    private long nextActionAt;
    private boolean buyRequested;

    public AutoTaThuFoodSupply(TaThu fighter) {
        this.fighter = fighter;
    }

    public void fieldAD() {
        this.nextActionAt = 0L;
        this.buyRequested = false;
        super.fieldAD();
    }

    public void fieldAA() {
        if (System.currentTimeMillis() < this.nextActionAt) {
            return;
        }
        if (hasFoodEffect()) {
            this.finishSupply("server đã xác nhận hiệu ứng thức ăn");
            return;
        }
        if (TileMap.mapID != OKAZA_MAP) {
            GameScr.fieldAC("AUTO TA THU: hết thức ăn, đang về Okaza tiếp tế");
            this.fieldAA(OKAZA_MAP, -2, -1, -1);
            return;
        }

        int foodLevel = configuredFoodLevel();
        Item food = findFood(foodLevel);
        if (food == null) {
            if (!AutoPrepareNvhn.ensureEmergencyBagSlot()) {
                System.out.println("AUTO TA THU FOOD: hành trang đầy và không dọn được ô trống; thử lại sau 10 giây");
                this.nextActionAt = System.currentTimeMillis() + 10000L;
                return;
            }
            int count = countFood(foodLevel);
            int missing = TARGET_BAG_FOOD - count;
            if (missing < 1) {
                missing = 1;
            }
            GameScr.fieldAB(FOOD_NPC, 0, 0);
            int shopIndex = foodLevel == 50 ? 7 : foodLevel / 10;
            System.out.println("AUTO TA THU FOOD: mua tiếp tế " + missing
                    + " thức ăn level=" + foodLevel + " shopIndex=" + shopIndex);
            Service.gI().buyItem1(9, shopIndex, missing);
            this.buyRequested = true;
            this.nextActionAt = System.currentTimeMillis() + 1500L;
            return;
        }

        int before = countFood(foodLevel);
        System.out.println("AUTO TA THU FOOD: dùng thức ăn tiếp tế level=" + foodLevel
                + " bagIndex=" + food.indexUI + " cònTrước=" + before);
        Service.gI().useItem(food.indexUI);
        long deadline = System.currentTimeMillis() + 4000L;
        while (!hasFoodEffect() && countFood(foodLevel) >= before
                && System.currentTimeMillis() < deadline) {
            Auto.fieldAA(100L);
        }
        if (hasFoodEffect() || countFood(foodLevel) < before) {
            this.finishSupply(hasFoodEffect() ? "đã có hiệu ứng thức ăn" : "server đã tiêu thụ thức ăn");
            return;
        }
        System.out.println("AUTO TA THU FOOD: chưa xác nhận dùng được thức ăn"
                + (this.buyRequested ? " sau khi mua" : "") + "; thử lại sau 5 giây");
        this.nextActionAt = System.currentTimeMillis() + 5000L;
    }

    private void finishSupply(String detail) {
        System.out.println("AUTO TA THU FOOD: tiếp tế xong (" + detail
                + "), quay lại map/khu Tà Thú đã khóa");
        this.fighter.onFoodSupplyFinished();
        Code.fieldAC();
    }

    public static boolean needsSupply() {
        return Char.isAFood && !hasFoodEffect()
                && countFood(configuredFoodLevel()) == 0;
    }

    public static boolean hasFoodEffect() {
        Char me = Char.getMyChar();
        if (me == null || me.vEff == null) {
            return false;
        }
        for (int index = 0; index < me.vEff.size(); ++index) {
            Effect effect = (Effect) me.vEff.elementAt(index);
            if (effect != null && effect.template != null && effect.template.type == 0) {
                return true;
            }
        }
        return false;
    }

    private static int configuredFoodLevel() {
        int level = Char.aFoodValue;
        if (level < 10 || level > 50) {
            int characterLevel = Char.getMyChar().clevel;
            level = characterLevel / 10 * 10;
            if (level < 10) {
                level = 10;
            } else if (level > 50) {
                level = 50;
            }
            Char.aFoodValue = level;
        }
        return level;
    }

    private static Item findFood(int level) {
        Item[] bag = Char.getMyChar().arrItemBag;
        if (bag == null) {
            return null;
        }
        for (int index = 0; index < bag.length; ++index) {
            Item item = bag[index];
            if (item != null && item.template != null
                    && item.template.type == 18 && item.template.level == level) {
                return item;
            }
        }
        return null;
    }

    private static int countFood(int level) {
        int count = 0;
        Item[] bag = Char.getMyChar().arrItemBag;
        if (bag == null) {
            return 0;
        }
        for (int index = 0; index < bag.length; ++index) {
            Item item = bag[index];
            if (item != null && item.template != null
                    && item.template.type == 18 && item.template.level == level) {
                count += item.quantity > 0 ? item.quantity : 1;
            }
        }
        return count;
    }

    public String toString() {
        return "Tiếp tế thức ăn Tà Thú";
    }
}
