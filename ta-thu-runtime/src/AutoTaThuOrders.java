/** Inspect/buy/use the two daily Ta Thu Lenh items before mission execution. */
public final class AutoTaThuOrders extends Auto {
    private static final int OKAZA_MAP = 72;
    private static final int GOOSHO_NPC = 30;
    private static final int ORDER_ITEM_ID = 268;
    private static final int REQUIRED_ORDERS = 2;
    private final boolean observeOnly;
    private boolean processing;
    private long nextAttemptAt;
    private int useFailures;
    private long pendingPurchaseSequence = -1L;

    public AutoTaThuOrders(boolean observeOnly) {
        this.observeOnly = observeOnly;
    }

    public void fieldAD() {
        this.processing = false;
        this.nextAttemptAt = 0L;
        this.useFailures = 0;
        this.pendingPurchaseSequence = -1L;
        super.fieldAD();
    }

    public void fieldAA() {
        if (this.processing || System.currentTimeMillis() < this.nextAttemptAt) {
            return;
        }
        if (!this.observeOnly && TileMap.mapID != OKAZA_MAP) {
            GameScr.fieldAC("AUTO TA THU: đang tới Okaza kiểm tra Tà Thú Lệnh");
            this.fieldAA(OKAZA_MAP, -2, -1, -1);
            return;
        }
        this.processing = true;
        boolean ready = false;
        try {
            ready = this.process();
        } catch (Exception ex) {
            System.out.println("AUTO TA THU ORDERS: lỗi " + ex.toString());
        }
        if (ready) {
            TaThuAccountManager.onOrdersReady();
        } else {
            this.processing = false;
            this.nextAttemptAt = System.currentTimeMillis() + 10000L;
            System.out.println("AUTO TA THU ORDERS: chưa sẵn sàng, giữ nguyên item và thử lại sau 10 giây");
        }
    }

    private boolean process() {
        Char me = Char.getMyChar();
        TaThuDailyState state = TaThuDailyState.loadCurrent();
        // A rejection can arrive after the bag wait timed out, during retry backoff.
        if (this.pendingPurchaseSequence >= 0L
                && TaThuAccountManager.hasInsufficientFundsSince(this.pendingPurchaseSequence)) {
            return this.skipPurchase(state);
        }
        int serverRemaining = this.refreshActivityCount();
        this.refreshBox();
        int bagCount = this.countItems(me.arrItemBag, ORDER_ITEM_ID);
        int boxCount = this.countItems(me.arrItemBox, ORDER_ITEM_ID);
        TaskOrder task = Char.fieldAM(1);
        System.out.println("AUTO TA THU OBSERVE: char=" + me.cName
                + " countLoopBoos=" + serverRemaining
                + " task=" + describe(task)
                + " bag268=" + bagCount + " box268=" + boxCount
                + " ordersUsedState=" + state.ordersUsed
                + " questsCompletedState=" + state.questsCompleted
                + " stateFile=" + state.getFile().getAbsolutePath());
        if (this.observeOnly) {
            return true;
        }

        // The server does not reliably accept Ta Thu Lenh while TaskOrder(1)
        // is active. Finish and turn in that task first, then come back here.
        if (task != null) {
            System.out.println("AUTO TA THU ORDERS: đang có TaskOrder id=1 map=" + task.mapId
                    + " killId=" + task.killId + ", hoãn dùng lệnh tới sau khi trả task");
            return true;
        }

        if (state.ordersPurchaseSkipped && !TaThuAccountManager.isShopStage()) {
            return true;
        }

        GameScr.arrItemStore = null;
        GameScr.fieldAB(GOOSHO_NPC, 0, 0);
        Service.gI().requestItem(14);
        long deadline = System.currentTimeMillis() + 5000L;
        while (GameScr.arrItemStore == null && System.currentTimeMillis() < deadline) {
            Auto.fieldAA(100L);
        }
        Item storeOrder = this.findItem(GameScr.arrItemStore, ORDER_ITEM_ID);
        System.out.println("AUTO TA THU SHOP: npc=" + GOOSHO_NPC + " itemId=" + ORDER_ITEM_ID
                + " found=" + (storeOrder != null)
                + (storeOrder == null ? "" : " typeUI=" + storeOrder.typeUI + " shopIndex=" + storeOrder.indexUI
                + " buyCoin=" + storeOrder.buyCoin + " buyGold=" + storeOrder.buyGold));
        if ("shop".equals(TaThuAccountManager.getStage())) {
            return true;
        }

        int usedFromLoop = serverRemaining > 0 ? Math.max(0, serverRemaining + state.questsCompleted - 2) : 0;
        if (usedFromLoop > state.ordersUsed) {
            state.ordersUsed = Math.min(REQUIRED_ORDERS, usedFromLoop);
            state.save();
            System.out.println("AUTO TA THU ORDERS: server còn " + serverRemaining
                    + " lượt (đã hoàn thành " + state.questsCompleted + "), cập nhật ordersUsed=" + state.ordersUsed);
        }
        while (state.ordersUsed < REQUIRED_ORDERS) {
            Item order = this.findItem(me.arrItemBag, ORDER_ITEM_ID);
            if (order == null) {
                Item boxOrder = this.findItem(me.arrItemBox, ORDER_ITEM_ID);
                if (boxOrder != null) {
                    System.out.println("AUTO TA THU ORDERS: lấy item 268 từ rương boxIndex=" + boxOrder.indexUI);
                    Service.gI().itemBoxToBag(boxOrder.indexUI);
                    order = this.waitForBagItem(ORDER_ITEM_ID, 3000L);
                    this.refreshBox();
                }
            }
            if (order == null) {
                int totalInBagAndBox = this.countItems(me.arrItemBag, ORDER_ITEM_ID)
                        + this.countItems(me.arrItemBox, ORDER_ITEM_ID);
                int missing = REQUIRED_ORDERS - state.ordersUsed - totalInBagAndBox;
                if (missing <= 0) {
                    System.out.println("AUTO TA THU ORDERS: đã đủ " + totalInBagAndBox
                            + " item 268 trong hành trang/rương cho "
                            + (REQUIRED_ORDERS - state.ordersUsed) + " lần dùng còn lại, không mua thêm");
                    Item boxOrder = this.findItem(me.arrItemBox, ORDER_ITEM_ID);
                    if (boxOrder != null) {
                        Service.gI().itemBoxToBag(boxOrder.indexUI);
                        order = this.waitForBagItem(ORDER_ITEM_ID, 3000L);
                    }
                    if (order == null) {
                        return false;
                    }
                } else {
                    if (storeOrder == null) {
                        System.out.println("AUTO TA THU ORDERS: không tìm thấy item 268 trong shop, không mua");
                        return false;
                    }
                    System.out.println("AUTO TA THU ORDERS: mua itemId=268 số lượng=" + missing
                            + " typeUI=" + storeOrder.typeUI + " shopIndex=" + storeOrder.indexUI);
                    long purchaseSequence = TaThuAccountManager.getMessageSequence();
                    this.pendingPurchaseSequence = purchaseSequence;
                    Service.gI().buyItem(storeOrder.typeUI, storeOrder.indexUI, missing);
                    deadline = System.currentTimeMillis() + 4000L;
                    while ((order = this.findItem(me.arrItemBag, ORDER_ITEM_ID)) == null
                            && !TaThuAccountManager.hasInsufficientFundsSince(purchaseSequence)
                            && System.currentTimeMillis() < deadline) {
                        Auto.fieldAA(100L);
                    }
                    if (TaThuAccountManager.hasInsufficientFundsSince(purchaseSequence)) {
                        return this.skipPurchase(state);
                    }
                    if (order == null) {
                        this.refreshBox();
                        return false;
                    }
                    this.pendingPurchaseSequence = -1L;
                    // Seeing the item in the local bag does not always mean the
                    // purchase transaction is ready for a use request yet.
                    Auto.fieldAA(1500L);
                }
            }

            int before = this.countItems(me.arrItemBag, ORDER_ITEM_ID);
            int beforeRemaining = serverRemaining;
            long messageBefore = TaThuAccountManager.getMessageSequence();
            System.out.println("AUTO TA THU ORDERS: dùng itemId=268 bagIndex=" + order.indexUI
                    + " lần=" + (state.ordersUsed + 1) + "/" + REQUIRED_ORDERS);
            Service.gI().useItem(order.indexUI);
            deadline = System.currentTimeMillis() + 10000L;
            boolean consumed = false;
            while (System.currentTimeMillis() < deadline) {
                if (this.countItems(me.arrItemBag, ORDER_ITEM_ID) < before) {
                    consumed = true;
                    break;
                }
                Auto.fieldAA(100L);
            }
            String response = TaThuAccountManager.getLastMessageSince(messageBefore);
            serverRemaining = this.refreshActivityCount();
            if (consumed || serverRemaining > beforeRemaining) {
                ++state.ordersUsed;
                state.save();
                System.out.println("AUTO TA THU ORDERS: xác nhận dùng thành công ordersUsed="
                        + state.ordersUsed + " countLoopBoos=" + serverRemaining);
            } else if (isDailyLimit(response) || state.questsCompleted >= 2 || ++useFailures >= 2) {
                state.ordersUsed = REQUIRED_ORDERS;
                state.save();
                System.out.println("AUTO TA THU ORDERS: server không nhận dùng thêm Tà Thú Lệnh (thử dùng "
                        + useFailures + " lần không mất item / đã đạt giới hạn), dừng dùng/mua. Response: " + response);
                return true;
            } else {
                System.out.println("AUTO TA THU ORDERS: chưa xác nhận được việc dùng item lần "
                        + useFailures + "; response=[" + response + "] countLoopBoos=" + serverRemaining);
                return false;
            }
        }
        return true;
    }

    private boolean skipPurchase(TaThuDailyState state) {
        state.ordersPurchaseSkipped = true;
        state.save();
        this.pendingPurchaseSequence = -1L;
        System.out.println("AUTO TA THU ORDERS: không đủ tiền, bỏ mua lệnh còn thiếu cho nhân vật hôm nay; tiếp tục lượt Tà Thú hiện có");
        return true;
    }

    private int refreshActivityCount() {
        Char me = Char.getMyChar();
        me.countLoopBoos = -128;
        Service.gI().viewInfo(me.cName, 0);
        long deadline = System.currentTimeMillis() + 5000L;
        while (me.countLoopBoos == -128 && System.currentTimeMillis() < deadline) {
            Auto.fieldAA(100L);
        }
        return me.countLoopBoos;
    }

    private boolean refreshBox() {
        Char.getMyChar().arrItemBox = null;
        Service.gI().requestItem(4);
        long deadline = System.currentTimeMillis() + 4000L;
        while (Char.getMyChar().arrItemBox == null && System.currentTimeMillis() < deadline) {
            Auto.fieldAA(100L);
        }
        return Char.getMyChar().arrItemBox != null;
    }

    private Item waitForBagItem(int templateId, long timeout) {
        long deadline = System.currentTimeMillis() + timeout;
        Item item;
        while ((item = this.findItem(Char.getMyChar().arrItemBag, templateId)) == null
                && System.currentTimeMillis() < deadline) {
            Auto.fieldAA(100L);
        }
        return item;
    }

    private Item findItem(Item[] items, int templateId) {
        if (items == null) {
            return null;
        }
        for (int index = 0; index < items.length; ++index) {
            if (items[index] != null && items[index].template != null && items[index].template.id == templateId) {
                return items[index];
            }
        }
        return null;
    }

    private int countItems(Item[] items, int templateId) {
        int count = 0;
        if (items == null) {
            return 0;
        }
        for (int index = 0; index < items.length; ++index) {
            Item item = items[index];
            if (item != null && item.template != null && item.template.id == templateId) {
                count += item.quantity > 0 ? item.quantity : 1;
            }
        }
        return count;
    }

    private static boolean isDailyLimit(String message) {
        if (message == null) {
            return false;
        }
        String lower = message.toLowerCase();
        return (lower.indexOf("tà thú") >= 0 || lower.indexOf("ta thu") >= 0 || lower.indexOf("dùng hết") >= 0)
                && (lower.indexOf("tối đa") >= 0 || lower.indexOf("hết") >= 0 || lower.indexOf("giới hạn") >= 0
                || lower.indexOf("ngày hôm sau") >= 0 || lower.indexOf("không thể") >= 0 || lower.indexOf("đã sử dụng") >= 0
                || lower.indexOf("2 tà thú") >= 0 || lower.indexOf("2 ta thu") >= 0);
    }

    private static String describe(TaskOrder task) {
        if (task == null) {
            return "none";
        }
        return "id=" + task.taskId + ",count=" + task.count + "/" + task.maxCount
                + ",map=" + task.mapId + ",kill=" + task.killId;
    }

    public String toString() {
        return this.observeOnly ? "Observe Tà Thú" : "Chuẩn bị Tà Thú Lệnh";
    }
}
