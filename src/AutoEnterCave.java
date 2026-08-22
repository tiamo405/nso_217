/** Enter the school cave through Kanata, then hand control back to the account runner. */
public final class AutoEnterCave extends Auto {
    private int targetMap;
    private int menuOption;
    private long lastRequestAt;
    private int requestCount;
    private static final int MAX_REQUEST_COUNT = 3;

    public final void fieldAD() {
        int level = Char.getMyChar().clevel;
        if (level < 40) {
            this.targetMap = 91;
            this.menuOption = 1;
        } else if (level < 50) {
            this.targetMap = 94;
            this.menuOption = 2;
        } else if (level < 60) {
            this.targetMap = 105;
            this.menuOption = 3;
        } else if (level < 70) {
            this.targetMap = 114;
            this.menuOption = 4;
        } else if (level < 90) {
            this.targetMap = 125;
            this.menuOption = 5;
        } else {
            this.targetMap = 157;
            this.menuOption = 6;
        }
        super.fieldAD();
        this.lastRequestAt = 0L;
        this.requestCount = 0;
        System.out.println("AUTO NVHN HANG: chuẩn bị vào hang qua Kanata, charLv=" + level
                + " menuOption=" + this.menuOption + " targetMap=" + this.targetMap);
    }

    public final void fieldAA() {
        if (TileMap.mapID == this.targetMap || TileMap.isHang(TileMap.mapID)) {
            System.out.println("AUTO NVHN HANG: đã vào hang map=" + TileMap.mapID
                    + "(" + TileMap.mapName + "), thoát nhân vật");
            Code.fieldAG();
            AccountAutoManager.onCaveEntered();
            return;
        }
        if (!TileMap.isTruong(TileMap.mapID)) {
            int classId = Char.getMyChar().nClass.classId;
            int schoolMap = classId <= 2 ? 1 : (classId <= 4 ? 27 : 72);
            this.fieldAA(schoolMap, -2, -1, -1);
            return;
        }
        // Phải mở NPC và menu cha trước khi gửi lựa chọn cấp hang. Router map cũ
        // chỉ gửi packet cuối nên Kanata hiển thị đúng nhưng không cho vào hang.
        long now = System.currentTimeMillis();
        if (now - this.lastRequestAt < 5000L) {
            return;
        }
        this.lastRequestAt = now;
        ++this.requestCount;
        if (this.requestCount > MAX_REQUEST_COUNT) {
            System.out.println("AUTO NVHN HANG: thử vào hang quá " + MAX_REQUEST_COUNT
                    + " lần nhưng chưa vào được, bỏ qua hang và chuyển nhân vật");
            Code.fieldAG();
            AccountAutoManager.onCaveEntered();
            return;
        }
        if (this.requestCount == 1) {
            this.logKanataMenus();
        }
        System.out.println("AUTO NVHN HANG: mở Kanata -> Hang động sau trường -> nút cấp hang thứ 2"
                + " targetMap=" + this.targetMap + " lần=" + this.requestCount);
        GameCanvas.menu.showMenu = false;
        GameScr.fieldAH(0);
        long menuDeadline = System.currentTimeMillis() + 3000L;
        while (!GameCanvas.menu.showMenu && System.currentTimeMillis() < menuDeadline) {
            Auto.fieldAA(100L);
        }
        System.out.println("AUTO NVHN HANG: menu Kanata đã mở=" + GameCanvas.menu.showMenu);
        // Bước 1: chọn "Hang động sau trường" để server dựng menu động gồm
        // "Nhận thưởng" và đúng một cấp phù hợp với level nhân vật.
        GameCanvas.menu.showMenu = false;
        Service.gI().menu((byte) 0, 0, 2, 0);
        long caveMenuDeadline = System.currentTimeMillis() + 3000L;
        while (!GameCanvas.menu.showMenu && System.currentTimeMillis() < caveMenuDeadline) {
            Auto.fieldAA(100L);
        }
        System.out.println("AUTO NVHN HANG: menu 2 nút đã mở=" + GameCanvas.menu.showMenu);
        // Bước 2: menu động là top-level; nút 0 nhận thưởng, nút 1 vào hang.
        Service.gI().menu((byte) 0, 0, 1, 0);
    }

    private void logKanataMenus() {
        Npc npc = GameScr.fieldAI(0);
        if (npc == null || npc.template == null || npc.template.menu == null) {
            System.out.println("AUTO NVHN HANG: không đọc được menu template Kanata");
            return;
        }
        for (int i = 0; i < npc.template.menu.length; ++i) {
            String[] menu = npc.template.menu[i];
            String text = "";
            for (int j = 0; menu != null && j < menu.length; ++j) {
                text += (j == 0 ? "" : " | ") + menu[j];
            }
            System.out.println("AUTO NVHN HANG MENU[" + i + "]: " + text);
        }
    }
}
