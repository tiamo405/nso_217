/** Buys and flips lucky cards after daily missions, then continues to the cave. */
public final class AutoFlipNvhn extends Auto {
    private static final int OKAZA_MAP = 72;
    private static final int GOOSHO_NPC = 30;
    private static final int LUCKY_TICKET_ID = 340;
    private static final int REQUIRED_TICKETS = 2;

    private boolean finished;
    private long lastMoveLogAt;

    public final void fieldAD() {
        this.finished = false;
        this.lastMoveLogAt = 0L;
        super.fieldAD();
    }

    public final void fieldAA() {
        if (this.finished) {
            return;
        }
        if (TileMap.mapID != OKAZA_MAP) {
            long now = System.currentTimeMillis();
            if (now - this.lastMoveLogAt >= 30000L) {
                this.lastMoveLogAt = now;
                System.out.println("AUTO NVHN LAT HINH: đã hết NVHN, đang tới Okaza"
                        + " map=" + TileMap.mapID + "(" + TileMap.mapName + ")");
            }
            this.fieldAA(OKAZA_MAP, -2, -1, -1);
            return;
        }

        this.finished = true;
        try {
            this.buyAndFlipLuckyTickets();
        } catch (Exception ex) {
            System.out.println("AUTO NVHN LAT HINH: lỗi xử lý lật thẻ, vẫn tiếp tục đi hang: "
                    + ex.toString());
        } finally {
            AccountAutoManager.onPostDailyFlipFinished();
        }
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
}
