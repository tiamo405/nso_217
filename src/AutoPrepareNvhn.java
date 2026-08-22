import java.io.InputStream;
import java.util.Vector;

/** Character preparation: skill/food, Noel hat, box cleanup, lucky cards, then NVHN. */
public final class AutoPrepareNvhn extends Auto {
    private static final int OKAZA_MAP = 72;
    private static final int GOOSHO_NPC = 30;
    private static final int FOOD_NPC = 4;
    private static final int LUCKY_TICKET_ID = 340;
    private static final int REQUIRED_TICKETS = 2;
    private static final int NOEL_HAT_MALE_ID = 351;
    private static final int NOEL_HAT_FEMALE_ID = 352;
    private static final int COMBAT_SKILL_INDEX = 4;

    private boolean saving;
    private boolean preparedAtOkaza;
    private boolean basicConfigured;
    private boolean boxCleaned;
    private long nextBoxCleanupAt;
    private long lastOkazaLogAt;

    public final void fieldAD() {
        this.saving = false;
        this.preparedAtOkaza = false;
        this.basicConfigured = false;
        this.boxCleaned = false;
        this.nextBoxCleanupAt = 0L;
        this.lastOkazaLogAt = 0L;
        super.fieldAD();
    }

    public final void fieldAA() {
        if (!this.preparedAtOkaza) {
            if (!this.basicConfigured) {
                this.basicConfigured = true;
                this.selectLevelSkill();
                this.configureFood();
            }
            if (!this.boxCleaned) {
                if (System.currentTimeMillis() < this.nextBoxCleanupAt) {
                    return;
                }
                System.out.println("AUTO NVHN BOX CLEAN: dọn rương ngay sau khi vào nhân vật, trước khi mua đồ");
                if (!this.deleteConfiguredBoxItems()) {
                    this.nextBoxCleanupAt = System.currentTimeMillis() + 5000L;
                    System.out.println("AUTO NVHN BOX CLEAN: chưa dọn được rương, thử lại sau 5 giây; chưa mua Mũ Noel/vé");
                    return;
                }
                this.boxCleaned = true;
            }
            if (TileMap.mapID != OKAZA_MAP) {
                GameScr.fieldAC("AUTO NVHN: đang tới trường Okaza");
                this.logOkazaMove();
                this.fieldAA(OKAZA_MAP, -2, -1, -1);
                return;
            }

            this.preparedAtOkaza = true;
            this.buyAndUseFood();
            this.ensureNoelHatEquipped();
            this.buyAndFlipLuckyTickets();
            return;
        }

        int schoolMap = this.getCharacterSchoolMap();
        if (TileMap.mapID != schoolMap) {
            GameScr.fieldAC("AUTO NVHN: đang về trường để lưu tọa độ");
            System.out.println("AUTO NVHN: đang về trường map " + schoolMap + " để lưu tọa độ");
            this.fieldAA(schoolMap, -2, -1, -1);
            return;
        }

        if (this.saving) {
            return;
        }
        this.saving = true;
        GameScr.fieldAC("AUTO NVHN: lưu tọa độ tại Kamakura");
        System.out.println("AUTO NVHN: đã tới trường, đang lưu tọa độ tại Kamakura");
        GameScr.fieldAH(5);
        Service.gI().openMenu(5);
        Service.gI().menu((byte) 0, 5, 1, 0);

        Auto.fieldAA(1500L);
        GameScr.fieldAC("AUTO NVHN: bắt đầu nhiệm vụ hàng ngày");
        System.out.println("AUTO NVHN: lưu tọa độ xong, bắt đầu nhiệm vụ hàng ngày");
        Code.fieldAD();
    }

    private void logOkazaMove() {
        long now = System.currentTimeMillis();
        if (now - this.lastOkazaLogAt < 30000L) {
            return;
        }
        this.lastOkazaLogAt = now;
        System.out.println("AUTO NVHN PREP: đang tới Okaza để mua thức ăn và lật hình"
                + " map=" + TileMap.mapID + "(" + TileMap.mapName + ")");
    }

    private int getCharacterSchoolMap() {
        int classId = Char.getMyChar().nClass.classId;
        return classId <= 2 ? 1 : (classId <= 4 ? 27 : 72);
    }

    private void selectLevelSkill() {
        Char me = Char.getMyChar();
        if (me.nClass == null || me.nClass.skillTemplates == null || me.nClass.skillTemplates.length == 0) {
            System.out.println("AUTO NVHN SKILL: không có bảng kỹ năng, dùng skill hiện tại nếu có"
                    + " charLv=" + me.clevel);
            return;
        }

        if (me.clevel >= 30 && me.nClass.skillTemplates.length > COMBAT_SKILL_INDEX) {
            SkillTemplate targetTemplate = me.nClass.skillTemplates[COMBAT_SKILL_INDEX];
            Skill selected = me.gameAA(targetTemplate);
            if (this.useCombatSkill(selected, "skill bảng index=4 (thứ 5)")) {
                return;
            }
            System.out.println("AUTO NVHN SKILL: skill thứ 5 chưa học/cộng điểm, fallback skill thứ 1"
                    + " templateId=" + targetTemplate.id + " name=" + targetTemplate.name
                    + " charLv=" + me.clevel);
        } else {
            System.out.println("AUTO NVHN SKILL: không đủ điều kiện dùng skill thứ 5, fallback skill thứ 1"
                    + " charLv=" + me.clevel + " templateCount=" + me.nClass.skillTemplates.length);
        }

        SkillTemplate targetTemplate = me.nClass.skillTemplates[0];
        Skill selected = me.gameAA(targetTemplate);
        if (!this.useCombatSkill(selected, "skill bảng index=0 (thứ 1)")) {
            System.out.println("AUTO NVHN SKILL: skill thứ 1 cũng chưa học/cộng điểm, giữ skill hiện tại"
                    + " templateId=" + targetTemplate.id + " name=" + targetTemplate.name
                    + " charLv=" + me.clevel);
        }
    }

    private boolean useCombatSkill(Skill selected, String label) {
        if (selected == null || selected.point <= 0) {
            return false;
        }
        Char me = Char.getMyChar();
        me.myskill = selected;
        Auto.fieldAL = selected;
        Service.gI().selectSkill(selected.template.id);
        System.out.println("AUTO NVHN SKILL: dùng " + label + " " + selected.template.name
                + " skillId=" + selected.template.id + " unlockLv=" + selected.level
                + " skillPoint=" + selected.point + " charLv=" + me.clevel);
        return true;
    }

    private void configureFood() {
        int foodLevel = Char.getMyChar().clevel / 10 * 10;
        if (foodLevel < 10) {
            foodLevel = 10;
        } else if (foodLevel > 50) {
            foodLevel = 50;
        }
        Char.aFoodValue = foodLevel;
        Char.isAFood = true;
        System.out.println("AUTO NVHN FOOD: bật tự dùng thức ăn level=" + foodLevel);
    }

    private void buyAndUseFood() {
        int foodLevel = Char.aFoodValue;
        int missing = 2 - this.countFood(foodLevel);
        if (missing > 0) {
            GameScr.fieldAB(FOOD_NPC, 0, 0);
            int shopIndex = foodLevel == 50 ? 7 : foodLevel / 10;
            System.out.println("AUTO NVHN FOOD: mua " + missing + " thức ăn level=" + foodLevel
                    + " shopIndex=" + shopIndex);
            Service.gI().buyItem1(9, shopIndex, missing);
            Auto.fieldAA(1200L);
        }
        Item food = this.findFood(foodLevel);
        if (food != null) {
            Service.gI().useItem(food.indexUI);
            System.out.println("AUTO NVHN FOOD: đã sử dụng thức ăn level=" + foodLevel
                    + " bagIndex=" + food.indexUI);
            Auto.fieldAA(700L);
        } else {
            System.out.println("AUTO NVHN FOOD: chưa tìm thấy thức ăn level=" + foodLevel + " sau khi mua");
        }
    }

    private int countFood(int level) {
        int count = 0;
        Item[] bag = Char.getMyChar().arrItemBag;
        for (int i = 0; i < bag.length; ++i) {
            Item item = bag[i];
            if (item != null && item.template.type == 18 && item.template.level == level) {
                count += item.quantity;
            }
        }
        return count;
    }

    private Item findFood(int level) {
        Item[] bag = Char.getMyChar().arrItemBag;
        for (int i = 0; i < bag.length; ++i) {
            Item item = bag[i];
            if (item != null && item.template.type == 18 && item.template.level == level) {
                return item;
            }
        }
        return null;
    }

    private void buyAndFlipLuckyTickets() {
        int current = this.countBagItem(LUCKY_TICKET_ID);
        int missing = REQUIRED_TICKETS - current;
        if (missing > 0) {
            GameScr.arrItemStore = null;
            GameScr.fieldAB(GOOSHO_NPC, 0, 0);
            Service.gI().requestItem(14);
            long deadline = System.currentTimeMillis() + 4000L;
            while (GameScr.arrItemStore == null && System.currentTimeMillis() < deadline) {
                Auto.fieldAA(100L);
            }
            Item ticket = this.findStoreItem(LUCKY_TICKET_ID);
            if (ticket != null) {
                System.out.println("AUTO NVHN LAT HINH: mua " + missing
                        + " Phiếu may mắn id=" + LUCKY_TICKET_ID
                        + " shopIndex=" + ticket.indexUI);
                Service.gI().buyItem(ticket.typeUI, ticket.indexUI, missing);
                Auto.fieldAA(1500L);
            } else {
                System.out.println("AUTO NVHN LAT HINH: không tìm thấy Phiếu may mắn id="
                        + LUCKY_TICKET_ID + " trong cửa hàng Goosho");
            }
        }

        int available = this.countBagItem(LUCKY_TICKET_ID);
        int flips = available < REQUIRED_TICKETS ? available : REQUIRED_TICKETS;
        System.out.println("AUTO NVHN LAT HINH: số phiếu hiện có=" + available + ", sẽ lật=" + flips);
        if (flips > 0) {
            LatHinh.time = 500L;
            (new LatHinh(flips)).run();
            Auto.fieldAA(1000L);
        }
    }

    private void ensureNoelHatEquipped() {
        Char me = Char.getMyChar();
        int hatId = this.getNoelHatId(me.cgender);
        ItemTemplate hatTemplate = ItemTemplates.gameAA((short) hatId);
        if (this.isNoelMaskActive(hatTemplate)) {
            System.out.println("AUTO NVHN NOEL: Mũ noel đang được sử dụng id=" + hatId
                    + " maskPart=" + me.ID_MAT_NA + ", không mua");
            return;
        }

        Item hat = this.findBagItem(hatId);
        this.refreshBox();
        if (hat == null) {
            Item boxHat = this.findBoxItem(hatId);
            if (boxHat != null) {
                System.out.println("AUTO NVHN NOEL: tìm thấy Mũ noel trong rương, boxIndex=" + boxHat.indexUI);
                Service.gI().itemBoxToBag(boxHat.indexUI);
                hat = this.waitForBagItem(hatId, 2000L);
            }
        }

        if (hat == null) {
            GameScr.arrItemFashion = null;
            GameScr.fieldAB(GOOSHO_NPC, 0, 0);
            Service.gI().requestItem(32);
            long deadline = System.currentTimeMillis() + 4000L;
            while (GameScr.arrItemFashion == null && System.currentTimeMillis() < deadline) {
                Auto.fieldAA(100L);
            }
            Item shopHat = this.findFashionItem(hatId);
            if (shopHat == null) {
                System.out.println("AUTO NVHN NOEL: không tìm thấy Mũ noel id=" + hatId
                        + " trong shop thời trang Goosho");
                return;
            }

            System.out.println("AUTO NVHN NOEL: mua Mũ noel id=" + hatId
                    + " shopIndex=" + shopHat.indexUI + " gender=" + me.cgender);
            Service.gI().buyItem(shopHat.typeUI, shopHat.indexUI, 1);
            Auto.fieldAA(1500L);
            hat = this.findBagItem(hatId);
            if (hat == null) {
                this.refreshBox();
                Item boxHat = this.findBoxItem(hatId);
                if (boxHat != null) {
                    System.out.println("AUTO NVHN NOEL: lấy Mũ noel mới mua từ rương, boxIndex="
                            + boxHat.indexUI);
                    Service.gI().itemBoxToBag(boxHat.indexUI);
                    hat = this.waitForBagItem(hatId, 2500L);
                }
            }
        }

        if (hat == null) {
            System.out.println("AUTO NVHN NOEL: chưa lấy được Mũ noel id=" + hatId
                    + " vào hành trang");
            return;
        }

        System.out.println("AUTO NVHN NOEL: sử dụng Mũ noel id=" + hatId
                + " bagIndex=" + hat.indexUI + " maskPart=" + hat.template.part
                + " type=" + hat.template.type + " itemGender=" + hat.template.gender
                + " charGender=" + me.cgender + " requiredLv=" + hat.template.level
                + " charLv=" + me.clevel + " locked=" + hat.isLock);
        int bagIndex = hat.indexUI;
        Service.gI().useItem(hat.indexUI);
        long deadline = System.currentTimeMillis() + 4000L;
        while (!this.isNoelMaskActive(hat.template)
                && Char.getMyChar().arrItemBag[bagIndex] != null
                && System.currentTimeMillis() < deadline) {
            Auto.fieldAA(100L);
        }
        boolean active = this.isNoelMaskActive(hat.template);
        boolean consumed = Char.getMyChar().arrItemBag[bagIndex] == null;
        System.out.println("AUTO NVHN NOEL: xác nhận sử dụng=" + (active || consumed)
                + " maskPart=" + Char.getMyChar().ID_MAT_NA
                + " expectedPart=" + hat.template.part
                + " itemRoiTui=" + consumed);
    }

    private int getNoelHatId(int characterGender) {
        ItemTemplate male = ItemTemplates.gameAA((short) NOEL_HAT_MALE_ID);
        ItemTemplate female = ItemTemplates.gameAA((short) NOEL_HAT_FEMALE_ID);
        if (male != null && male.gender == characterGender) {
            return NOEL_HAT_MALE_ID;
        }
        if (female != null && female.gender == characterGender) {
            return NOEL_HAT_FEMALE_ID;
        }
        System.out.println("AUTO NVHN NOEL: không khớp gender template, dùng fallback charGender="
                + characterGender);
        return characterGender == 0 ? NOEL_HAT_MALE_ID : NOEL_HAT_FEMALE_ID;
    }

    private boolean isNoelMaskActive(ItemTemplate template) {
        if (template == null) {
            return false;
        }
        Item[] body = Char.getMyChar().arrItemBody;
        if (body != null && template.type >= 0 && template.type < body.length
                && body[template.type] != null
                && body[template.type].template.id == template.id) {
            return true;
        }
        return template.part >= 0 && Char.getMyChar().ID_MAT_NA == template.part;
    }

    private Item findFashionItem(int templateId) {
        if (GameScr.arrItemFashion == null) {
            return null;
        }
        for (int i = 0; i < GameScr.arrItemFashion.length; ++i) {
            Item item = GameScr.arrItemFashion[i];
            if (item != null && item.template.id == templateId) {
                return item;
            }
        }
        return null;
    }

    private Item findBagItem(int templateId) {
        Item[] bag = Char.getMyChar().arrItemBag;
        for (int i = 0; i < bag.length; ++i) {
            if (bag[i] != null && bag[i].template.id == templateId) {
                return bag[i];
            }
        }
        return null;
    }

    private Item waitForBagItem(int templateId, long timeout) {
        long deadline = System.currentTimeMillis() + timeout;
        Item item;
        while ((item = this.findBagItem(templateId)) == null && System.currentTimeMillis() < deadline) {
            Auto.fieldAA(100L);
        }
        return item;
    }

    private boolean refreshBox() {
        for (int attempt = 1; attempt <= 3; ++attempt) {
            Char.getMyChar().arrItemBox = null;
            System.out.println("AUTO NVHN BOX CLEAN: yêu cầu dữ liệu rương lần=" + attempt);
            Service.gI().requestItem(4);
            long deadline = System.currentTimeMillis() + 5000L;
            while (Char.getMyChar().arrItemBox == null && System.currentTimeMillis() < deadline) {
                Auto.fieldAA(100L);
            }
            if (Char.getMyChar().arrItemBox != null) {
                return true;
            }
        }
        return false;
    }

    private Item findBoxItem(int templateId) {
        Item[] box = Char.getMyChar().arrItemBox;
        if (box == null) {
            return null;
        }
        for (int i = 0; i < box.length; ++i) {
            if (box[i] != null && box[i].template.id == templateId) {
                return box[i];
            }
        }
        return null;
    }

    private boolean deleteConfiguredBoxItems() {
        Vector deleteIds = this.loadDeleteIds();
        if (deleteIds.size() == 0) {
            System.out.println("AUTO NVHN BOX CLEAN: danh sách ID rỗng hoặc không đọc được");
            return true;
        }

        int bagDeleted = this.deleteConfiguredBagItems(deleteIds);
        int freeBagSlots = this.countFreeBagSlots();
        System.out.println("AUTO NVHN BAG CLEAN: hoàn tất, đã xóa=" + bagDeleted
                + " ôTrống=" + freeBagSlots + "/" + Char.getMyChar().arrItemBag.length);
        if (freeBagSlots < 3) {
            System.out.println("AUTO NVHN BAG CLEAN: cần ít nhất 3 ô trống để mua thức ăn/Mũ Noel/vé");
            return false;
        }

        // Kho arrItemBox không ảnh hưởng ô trống dùng để mua đồ. Server headless
        // có thể không trả UI kho, nên chỉ thử nhanh và không chặn bot.
        this.refreshBoxOnce();
        Item[] box = Char.getMyChar().arrItemBox;
        if (box == null) {
            System.out.println("AUTO NVHN BOX CLEAN: server không trả kho; hành trang đã đủ ô, tiếp tục chuẩn bị");
            return true;
        }

        int deleted = 0;
        int occupied = 0;
        for (int i = 0; i < box.length; ++i) {
            if (box[i] != null) {
                ++occupied;
            }
        }
        System.out.println("AUTO NVHN BOX CLEAN: đã mở rương, đang dùng=" + occupied + "/" + box.length);
        for (int i = box.length - 1; i >= 0; --i) {
            Item item = box[i];
            if (item == null || !this.containsId(deleteIds, item.template.id)) {
                continue;
            }
            int templateId = item.template.id;
            int boxIndex = item.indexUI;
            System.out.println("AUTO NVHN BOX CLEAN: lấy item id=" + templateId
                    + " boxIndex=" + boxIndex + " quantity=" + item.quantity);
            Service.gI().itemBoxToBag(boxIndex);
            Item bagItem = this.waitForBagItem(templateId, 1800L);
            if (bagItem == null) {
                System.out.println("AUTO NVHN BOX CLEAN: không chuyển được item id=" + templateId
                        + " sang hành trang (có thể hành trang đầy)");
                continue;
            }
            System.out.println("AUTO NVHN BOX CLEAN: Del đồ item id=" + templateId
                    + " bagIndex=" + bagItem.indexUI + " locked=" + bagItem.isLock);
            Service.gI().saleItem1(bagItem.indexUI, bagItem.quantity);
            Auto.fieldAA(500L);
            if (this.findBagItem(templateId) == null) {
                ++deleted;
            } else {
                System.out.println("AUTO NVHN BOX CLEAN: server chưa xóa item id=" + templateId
                        + " (item khóa hoặc còn stack khác)");
            }
        }
        System.out.println("AUTO NVHN BOX CLEAN: hoàn tất, đã xóa=" + deleted);
        return true;
    }

    private boolean refreshBoxOnce() {
        Char.getMyChar().arrItemBox = null;
        Service.gI().requestItem(4);
        long deadline = System.currentTimeMillis() + 2000L;
        while (Char.getMyChar().arrItemBox == null && System.currentTimeMillis() < deadline) {
            Auto.fieldAA(100L);
        }
        return Char.getMyChar().arrItemBox != null;
    }

    private int deleteConfiguredBagItems(Vector deleteIds) {
        int deleted = 0;
        Item[] bag = Char.getMyChar().arrItemBag;
        for (int i = bag.length - 1; i >= 0; --i) {
            Item item = bag[i];
            if (item == null || !this.containsId(deleteIds, item.template.id)) {
                continue;
            }
            int templateId = item.template.id;
            System.out.println("AUTO NVHN BAG CLEAN: Del đồ item id=" + templateId
                    + " bagIndex=" + item.indexUI + " quantity=" + item.quantity
                    + " locked=" + item.isLock);
            Service.gI().saleItem1(item.indexUI, item.quantity);
            long deadline = System.currentTimeMillis() + 1500L;
            while (bag[i] != null && System.currentTimeMillis() < deadline) {
                Auto.fieldAA(100L);
            }
            if (bag[i] == null) {
                ++deleted;
            } else {
                System.out.println("AUTO NVHN BAG CLEAN: server không cho xóa item id=" + templateId);
            }
        }
        return deleted;
    }

    private int countFreeBagSlots() {
        int free = 0;
        Item[] bag = Char.getMyChar().arrItemBag;
        for (int i = 0; i < bag.length; ++i) {
            if (bag[i] == null) {
                ++free;
            }
        }
        return free;
    }

    private Vector loadDeleteIds() {
        Vector ids = new Vector();
        InputStream input = null;
        try {
            input = AutoPrepareNvhn.class.getResourceAsStream("/delllllllllll.txt");
            if (input == null) {
                return ids;
            }
            byte[] bytes = new byte[input.available()];
            int offset = 0;
            while (offset < bytes.length) {
                int read = input.read(bytes, offset, bytes.length - offset);
                if (read < 0) {
                    break;
                }
                offset += read;
            }
            String content = new String(bytes, 0, offset, "UTF-8");
            int start = 0;
            while (start < content.length()) {
                int end = content.indexOf(';', start);
                if (end < 0) {
                    end = content.length();
                }
                String value = content.substring(start, end).trim();
                if (value.length() > 0) {
                    Integer id = Integer.valueOf(value);
                    if (!ids.contains(id)) {
                        ids.addElement(id);
                    }
                }
                start = end + 1;
            }
            System.out.println("AUTO NVHN BOX CLEAN: đã đọc " + ids.size() + " ID cần xóa");
        } catch (Exception ex) {
            System.out.println("AUTO NVHN BOX CLEAN: lỗi đọc danh sách: " + ex.toString());
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception ignored) {
            }
        }
        return ids;
    }

    private boolean containsId(Vector ids, int templateId) {
        return ids.contains(new Integer(templateId));
    }

    private Item findStoreItem(int templateId) {
        if (GameScr.arrItemStore == null) {
            return null;
        }
        for (int i = 0; i < GameScr.arrItemStore.length; ++i) {
            Item item = GameScr.arrItemStore[i];
            if (item != null && item.template.id == templateId) {
                return item;
            }
        }
        return null;
    }

    private int countBagItem(int templateId) {
        int count = 0;
        Item[] bag = Char.getMyChar().arrItemBag;
        for (int i = 0; i < bag.length; ++i) {
            Item item = bag[i];
            if (item != null && item.template.id == templateId) {
                count += item.quantity;
            }
        }
        return count;
    }
}
