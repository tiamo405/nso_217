/** Enter the school cave through Kanata, then hand control back to the account runner. */
public final class AutoEnterCave extends Auto {
    private int targetMap;
    private int caveLevel;
    private long lastRequestAt;
    private int requestCount;
    private static final int MAX_REQUEST_COUNT = 3;
    private static volatile int serverMenuSequence;
    private static volatile String[] serverMenuEntries;

    public final void fieldAD() {
        int level = Char.getMyChar().clevel;
        if (level < 40) {
            this.targetMap = 91;
            this.caveLevel = 35;
        } else if (level < 50) {
            this.targetMap = 94;
            this.caveLevel = 45;
        } else if (level < 60) {
            this.targetMap = 105;
            this.caveLevel = 55;
        } else if (level < 70) {
            this.targetMap = 114;
            this.caveLevel = 65;
        } else if (level < 90) {
            this.targetMap = 125;
            this.caveLevel = 75;
        } else {
            this.targetMap = 157;
            this.caveLevel = 95;
        }
        super.fieldAD();
        this.lastRequestAt = 0L;
        this.requestCount = 0;
        System.out.println("AUTO NVHN HANG: chuẩn bị vào hang qua Kanata, charLv=" + level
                + " caveLevel=" + this.caveLevel + " targetMap=" + this.targetMap);
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
        // Kanata trả menu cha qua command 63. Chọn "Hang động sau trường" sẽ làm
        // server trả một menu 63 mới gồm "Nhận thưởng sớm" và cấp hang phù hợp.
        // Phải đồng bộ theo từng response, không chỉ nhìn cờ showMenu phía client.
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
        System.out.println("AUTO NVHN HANG: mở Kanata -> Hang động sau trường -> Cấp "
                + this.caveLevel + " targetMap=" + this.targetMap + " lần=" + this.requestCount);
        GameCanvas.menu.showMenu = false;
        int menuSequenceBeforeOpen = serverMenuSequence;
        GameScr.fieldAH(0);
        if (Char.getMyChar().npcFocus == null
                || Char.getMyChar().npcFocus.template.npcTemplateId != 0) {
            System.out.println("AUTO NVHN HANG: chưa focus được NPC Kanata, sẽ thử lại");
            return;
        }
        int caveTopMenuIndex = waitForServerMenu(menuSequenceBeforeOpen,
                "Hang động sau trường", 3000L);
        if (caveTopMenuIndex < 0) {
            System.out.println("AUTO NVHN HANG: server chưa trả menu có nút Hang động sau trường");
            return;
        }
        // Đợi Controller hoàn tất xử lý menu trước khi gửi lựa chọn tiếp theo.
        // Gửi ngay trong lúc command 63 còn đang được parse khiến server bỏ packet.
        Auto.fieldAA(700L);

        GameCanvas.menu.showMenu = false;
        int menuSequenceBeforeCaveMenu = serverMenuSequence;
        System.out.println("AUTO NVHN HANG PACKET: mở menu hang npcId=0 menuIndex="
                + caveTopMenuIndex + " subMenuIndex=0");
        Service.gI().menu((byte) 0, 0, caveTopMenuIndex, 0);

        String caveButton = "Cấp " + this.caveLevel;
        int caveButtonIndex = waitForServerMenu(menuSequenceBeforeCaveMenu,
                caveButton, 3000L);
        if (caveButtonIndex < 0) {
            System.out.println("AUTO NVHN HANG: server chưa trả nút " + caveButton
                    + ", sẽ thử lại");
            return;
        }
        Auto.fieldAA(700L);

        GameCanvas.menu.showMenu = false;
        System.out.println("AUTO NVHN HANG PACKET: chọn " + caveButton
                + " npcId=0 menuIndex=" + caveButtonIndex + " subMenuIndex=0");
        Service.gI().menu((byte) 0, 0, caveButtonIndex, 0);
    }

    public static void onServerDynamicMenu(MyVector menuItems) {
        String[] entries = new String[menuItems.size()];
        for (int index = 0; index < entries.length; ++index) {
            Command command = (Command) menuItems.elementAt(index);
            entries[index] = command == null ? "" : command.caption;
        }
        serverMenuEntries = entries;
        ++serverMenuSequence;
    }

    private static int waitForServerMenu(int previousSequence, String expectedCaption,
            long timeoutMillis) {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            if (serverMenuSequence > previousSequence) {
                String[] entries = serverMenuEntries;
                for (int index = 0; entries != null && index < entries.length; ++index) {
                    if (expectedCaption.equals(entries[index])) {
                        return index;
                    }
                }
            }
            Auto.fieldAA(100L);
        }
        return -1;
    }
}
