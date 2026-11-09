import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Auto lên level 25 -> 35 bằng nhiệm vụ chính tuyến.
 *
 * Mỗi tick:
 *  1. Đủ level đích -> báo AccountAutoManager chuyển nhân vật.
 *  2. Chết -> hồi sinh.
 *  3. Giữ buff: Nấm linh chi, Mũ Noel, thức ăn (Uplevel35Support).
 *  4. Nhiệm vụ: nhận / làm bước giữa (lớp trong tasks/) / trả.
 */
public class As35 extends Auto {
    private static final Uplevel35Task[] TASKS = new Uplevel35Task[] {
        new TaskBaRei26(),
        new TaskBaRei27(),
        new TaskBaRei29(),
        new TaskMatsurugi30(),
        new TaskMatsurugi32(),
        new TaskMatsurugi35(),
    };

    private static final Hashtable itemIdCache = new Hashtable();

    private final Uplevel35Support support = new Uplevel35Support(this);
    private boolean configured;
    private boolean finished;
    private long npcRequestAt;
    private int lastTaskId = -2;
    private int lastTaskIndex = -2;
    private long stateLogAt;
    private String lastState = "";
    private int routeFails;
    private int skillLevel = -1;
    private long skillCheckAt;
    /** Kẹt chuyển map: số lần lỗi trước khi đăng nhập lại lần đầu, và số lần đăng nhập lại tối đa. */
    private static final int RELOGIN_AFTER_ROUTE_FAILS =
            Integer.getInteger("nso.uplevel35.route.relogin", 2).intValue();
    private static final int MAX_ROUTE_RELOGINS =
            Integer.getInteger("nso.uplevel35.route.maxRelogin", 3).intValue();
    /** Giữ qua lần đăng nhập lại (As35 bị tạo mới): tuyến đang kẹt và số lần đã đăng nhập lại. */
    private static String stuckRoute;
    private static int routeRelogins;

    public As35() {
        super.fieldAD();
        UplevelStatus.start("UPLEVEL35", "uplevel35");
    }

    public String toString() {
        return "Auto Lên Level 35";
    }

    // ================================================================ vòng chính

    public final void fieldAA() {
        Char me = Char.getMyChar();
        if (this.finished) {
            return;
        }
        this.logState(me);
        if (me.clevel >= Uplevel35.TARGET_LEVEL && !Uplevel35.hasTaskWork(me)) {
            // Nhiệm vụ mốc 35 (TaskMatsurugi35) vẫn làm tiếp sau khi đủ level.
            this.finish("đạt level " + me.clevel + " >= " + Uplevel35.TARGET_LEVEL);
            return;
        }
        if (me.cHP <= 0) {
            Auto.fieldAA(false);
            return;
        }
        if (!this.configured) {
            this.configured = true;
            this.configure(me);
        }
        if (!this.support.ensureMushroom(me)) {
            return;
        }
        if (!this.support.ensureNoelHat(me)) {
            return;
        }
        if (!this.support.ensureFood(me)) {
            return;
        }
        if (!this.support.ensureSkillBooks(me)) {
            return;
        }
        if (me.clevel != this.skillLevel || System.currentTimeMillis() - this.skillCheckAt > 5000L) {
            // Lên level / vừa học sách / server gửi lại danh sách skill (object Skill mới):
            // chọn lại skill đánh cao nhất để Code.java cộng điểm kỹ năng vào đúng skill đó.
            this.skillLevel = me.clevel;
            this.skillCheckAt = System.currentTimeMillis();
            this.selectAttackSkill(me);
        }

        if (Uplevel35.TEST_MAP >= 0) {
            if (this.goMap(Uplevel35.TEST_MAP)) {
                this.finish("TESTMAP tới map " + Uplevel35.TEST_MAP + " pos=" + me.cx + "," + me.cy);
            }
            return;
        }

        Uplevel35Task handler = findTask(me.ctaskId);
        if (handler == null) {
            this.finish("chưa có logic cho nhiệm vụ ctaskId=" + me.ctaskId + " " + describeTask(me));
            return;
        }
        Task task = me.taskMaint;
        if (me.ctaskId != this.lastTaskId || (task == null ? -1 : task.index) != this.lastTaskIndex) {
            if (me.ctaskId != this.lastTaskId) {
                handler.reset();
            }
            this.lastTaskId = me.ctaskId;
            this.lastTaskIndex = task == null ? -1 : task.index;
            Uplevel35.log("TASK " + handler.label() + " " + describeTask(me));
        }
        if (task == null) {
            this.talkToTaskNpc("nhận " + handler.label());
            return;
        }
        if (task.subNames != null && task.index >= task.subNames.length - 1) {
            this.talkToTaskNpc("trả " + handler.label());
            return;
        }
        handler.tick(this, me, task);
    }

    /** Được rời map hiện tại để đi mua đồ buff không (false khi đang ở map ẩn của nhiệm vụ). */
    boolean canTravelForSupplies() {
        Uplevel35Task handler = findTask(Char.getMyChar().ctaskId);
        return handler == null || handler.canLeaveMap(TileMap.mapID);
    }

    private static Uplevel35Task findTask(int taskId) {
        for (int i = 0; i < TASKS.length; i++) {
            if (TASKS[i].taskId() == taskId) {
                return TASKS[i];
            }
        }
        return null;
    }

    private void configure(Char me) {
        // Bật tự cộng tiềm năng và kỹ năng (Code.java xử lý khi fieldFG/fieldFH = true).
        Char.fieldFG = true;
        Char.fieldFH = true;
        this.selectAttackSkill(me);
        Uplevel35.log("START " + me.cName + " level=" + me.clevel + " class="
                + (me.nClass == null ? "?" : me.nClass.name) + " pPoint=" + me.pPoint
                + " sPoint=" + me.sPoint + " yen=" + me.yen + " luong=" + me.luong
                + " " + describeTask(me));
        // Dò nguồn sách võ công: cờ nhặt sách (Char.fieldES, lưu trong RMS) + sách trong túi/rương.
        Uplevel35Support.refreshBox(5000L);
        Uplevel35.log("BOOKS nhặtSách(fieldES)=" + Char.fieldES + " bag=" + books(me.arrItemBag)
                + " box=" + books(me.arrItemBox));
        StringBuffer skills = new StringBuffer();
        for (int i = 0; me.nClass != null && me.nClass.skillTemplates != null && i < me.nClass.skillTemplates.length; i++) {
            SkillTemplate t = me.nClass.skillTemplates[i];
            Skill s = t == null ? null : learnedSkill(me, t.id);
            if (t != null) {
                skills.append(t.id).append(':').append(t.name).append("/t").append(t.type)
                        .append("/p").append(s == null ? 0 : s.point).append(' ');
            }
        }
        Uplevel35.log("SKILLS " + skills);
    }

    private static String books(Item[] items) {
        StringBuffer out = new StringBuffer("[");
        for (int i = 0; items != null && i < items.length; i++) {
            Item item = items[i];
            if (item != null && item.template != null && item.template.name != null
                    && item.template.name.toLowerCase().indexOf("sách") >= 0) {
                out.append(item.template.id).append(':').append(item.template.name).append('x')
                        .append(item.quantity).append(item.isLock ? "(khóa)" : "").append(' ');
            }
        }
        return out.append(']').toString();
    }

    /**
     * Skill đã học theo id template. Không dùng Char.gameAA(template): nó so sánh tham chiếu,
     * sai sau khi server gửi lại danh sách skill (vd. vừa học sách -> "LOAD SKILL n").
     */
    static Skill learnedSkill(Char me, int templateId) {
        for (int i = 0; me.vSkill != null && i < me.vSkill.size(); i++) {
            Skill skill = (Skill) me.vSkill.elementAt(i);
            if (skill != null && skill.template != null && skill.template.id == templateId) {
                return skill;
            }
        }
        return null;
    }

    /** Chọn skill tấn công cao nhất đã học (template.type == 1). */
    void selectAttackSkill(Char me) {
        if (me.nClass == null || me.nClass.skillTemplates == null) {
            return;
        }
        Skill best = null;
        for (int i = 0; i < me.nClass.skillTemplates.length; i++) {
            SkillTemplate template = me.nClass.skillTemplates[i];
            Skill skill = template == null ? null : learnedSkill(me, template.id);
            if (skill != null && skill.point > 0 && template.type == 1) {
                best = skill;
            }
        }
        if (best == null) {
            Uplevel35.logEvery("skill-none", 60000L, "SKILL không tìm thấy skill tấn công, giữ skill hiện tại");
            return;
        }
        if (Auto.fieldAL == best && me.myskill == best) {
            return;
        }
        me.myskill = best;
        Auto.fieldAL = best;
        Service.gI().selectSkill(best.template.id);
        Uplevel35.log("SKILL dùng " + best.template.name + " id=" + best.template.id + " point=" + best.point);
    }

    void finish(String reason) {
        this.finished = true;
        Uplevel35.log("DONE " + Char.getMyChar().cName + ": " + reason);
        AccountAutoManager.onUplevel35Finished("UPLEVEL35 " + reason);
    }

    void onNpcMessage(int npcId, String text) {
        Uplevel35Task handler = findTask(Char.getMyChar().ctaskId);
        if (handler instanceof NpcMessageListener) {
            ((NpcMessageListener) handler).onNpcMessage(this, npcId, text);
        }
    }

    interface NpcMessageListener {
        void onNpcMessage(As35 bot, int npcId, String text);
    }

    // ============================================================ helper cho tasks

    /** Đi tới map; trả về true khi đã ở đúng map. */
    boolean goMap(int mapId) {
        if (TileMap.mapID == mapId) {
            this.routeFails = 0;
            return true;
        }
        int from = TileMap.mapID;
        int via = viaMap(from, mapId);
        if (via >= 0) {
            Uplevel35.logEvery("route-via", 10000L, "ROUTE " + from + " -> " + mapId + " đi vòng qua map " + via);
            this.goMap(via);
            return false;
        }
        String routeKey = Char.getMyChar().cName + ":" + from + "->" + mapId;
        if (!routeKey.equals(stuckRoute)) {
            stuckRoute = routeKey;
            routeRelogins = 0;
        }
        String ui = describeUi();
        if (ui.length() > 0) {
            // Hộp thoại NPC còn mở (vd. ngay sau khi nhận nhiệm vụ) có thể chặn dịch chuyển.
            Uplevel35.log("ROUTE đóng UI trước khi đi map " + from + " -> " + mapId + ":" + ui);
            closeUi();
        }
        long start = System.currentTimeMillis();
        // Đã thấy: ngay sau khi nói chuyện với Bà Rei (map 32) dịch chuyển làng (npc 7) bị server
        // bỏ qua suốt phiên, đăng nhập lại thì đi được ngay. Thứ tự xử lý khi kẹt:
        //   lỗi 2 lần -> đăng nhập lại; sau đó vẫn lỗi -> đi bộ bỏ dịch chuyển làng, rồi bỏ cả
        //   dịch chuyển trường; lỗi 6 lần -> đăng nhập lại (tối đa MAX_ROUTE_RELOGINS lần).
        boolean afterRelogin = routeRelogins > 0;
        boolean noVillageTp = afterRelogin && this.routeFails >= 1;
        boolean noSchoolTp = afterRelogin && this.routeFails >= 3;
        if (noVillageTp) {
            Uplevel35.logEvery("route-walk", 10000L, "ROUTE thử đi bộ " + from + " -> " + mapId
                    + " (bỏ dịch chuyển làng" + (noSchoolTp ? " + trường" : "") + ")");
        }
        boolean ok = routeWithoutTeleport(mapId, noVillageTp, noSchoolTp);
        if (TileMap.mapID == mapId) {
            this.routeFails = 0;
            stuckRoute = null;
            routeRelogins = 0;
            Uplevel35.log("ROUTE tới map " + mapId + " từ " + from + " sau "
                    + (System.currentTimeMillis() - start) + "ms");
            return true;
        }
        ++this.routeFails;
        Uplevel35.logEvery("route-fail", 10000L, "ROUTE lỗi " + from + " -> " + mapId
                + " fieldAK=" + ok + " đangỞ=" + TileMap.mapID + " lần=" + this.routeFails
                + " mất=" + (System.currentTimeMillis() - start) + "ms pos="
                + Char.getMyChar().cx + "," + Char.getMyChar().cy + describeUi());
        int reloginAt = afterRelogin ? 6 : RELOGIN_AFTER_ROUTE_FAILS;
        if (this.routeFails >= reloginAt && routeRelogins < MAX_ROUTE_RELOGINS) {
            ++routeRelogins;
            Uplevel35.log("ROUTE kẹt " + from + " -> " + mapId + " sau " + this.routeFails
                    + " lần, đăng nhập lại (lần " + routeRelogins + "/" + MAX_ROUTE_RELOGINS + ")");
            this.finished = true;
            AccountAutoManager.onReconnectRequested();
            return false;
        }
        Auto.fieldAA(500L);
        return false;
    }

    /**
     * Tuyến đi thẳng hay lỗi -> đi vòng qua map trung gian.
     * Làng Oshin (48) -> map 35: dịch chuyển nhanh lỗi, đi qua Làng chài (32) trước.
     */
    private static int viaMap(int from, int to) {
        if (from == 48 && to == 35) {
            return 32;
        }
        return -1;
    }

    /** Gọi TileMap.fieldAK với đồ thị tạm bỏ cạnh dịch chuyển, khôi phục ngay sau đó. */
    private static boolean routeWithoutTeleport(int mapId, boolean noVillageTp, boolean noSchoolTp) {
        if (!noVillageTp && !noSchoolTp) {
            return TileMap.fieldAK(mapId);
        }
        short[][] graph = TileMap.fieldBZ;
        short[][] saved = new short[graph.length][];
        System.arraycopy(graph, 0, saved, 0, graph.length);
        try {
            for (int from = 0; from < graph.length; from++) {
                short[] edges = graph[from];
                if (edges == null) {
                    continue;
                }
                boolean fromLang = TileMap.isLang(from);
                boolean fromTruong = TileMap.isTruong(from);
                short[] copy = null;
                for (int i = 0; i < edges.length; i++) {
                    int to = edges[i];
                    if ((noVillageTp && fromLang && TileMap.isLang(to))
                            || (noSchoolTp && fromTruong && TileMap.isTruong(to))) {
                        if (copy == null) {
                            copy = (short[]) edges.clone();
                        }
                        // Thay bằng cạnh tự trỏ (BFS bỏ qua vì đã duyệt) thay vì xóa: chỉ số cạnh
                        // là chỉ số cổng ra (TileMap.vGo) khi đi bộ, không được lệch.
                        copy[i] = (short) from;
                    }
                }
                if (copy != null) {
                    graph[from] = copy;
                }
            }
            return TileMap.fieldAK(mapId);
        } finally {
            System.arraycopy(saved, 0, graph, 0, graph.length);
        }
    }

    private static String describeUi() {
        StringBuffer sb = new StringBuffer();
        if (ChatPopup.currentMultilineChatPopup != null) {
            sb.append(" chatPopup");
        }
        if (GameCanvas.menu != null && GameCanvas.menu.showMenu) {
            sb.append(" menu");
        }
        if (GameCanvas.currentDialog != null) {
            sb.append(" dialog");
        }
        return sb.toString();
    }

    private static void closeUi() {
        ChatPopup.currentMultilineChatPopup = null;
        if (GameCanvas.menu != null) {
            GameCanvas.menu.showMenu = false;
        }
        GameCanvas.currentDialog = null;
    }

    /** Đi tới map (tự chọn khu) rồi đánh quái mobId (-1 = mọi quái), nhặt pickItemId. */
    void grind(int mapId, int mobId, int pickItemId) {
        if (TileMap.mapID != mapId) {
            Uplevel35.logEvery("grind-route", 10000L, "ROUTE map " + TileMap.mapID + " -> " + mapId
                    + " để đánh mob=" + mobId);
            if (this.goMap(mapId)) {
                this.fieldAA(mapId, -1, -1, -1); // vừa tới: chọn khu
            }
            return;
        }
        this.fieldAC(pickItemId);
        this.fieldAB(mobId, 1);
    }

    /**
     * Một bước đổi sang khu khác (logic Auto.fieldAB(int): đi tới NPC 13 rồi chọn khu kế/ít người).
     * Gọi lặp mỗi tick cho tới khi TileMap.zoneID đổi. Trả false khi map không có NPC đổi khu.
     */
    boolean stepChangeZone() {
        if (GameScr.fieldAI(13) == null) {
            return false;
        }
        this.fieldAB(TileMap.zoneID);
        return true;
    }

    /** Cờ loại quái cho Auto.fieldAB: 1 thường, 2 tinh anh, 4 thủ lĩnh, 8 boss. */
    static final int MOB_NORMAL = 1;
    static final int MOB_ANY = 1 | 2 | 4 | 8;

    /** Đánh quái mobId (-1 = mọi quái, mọi loại kể cả thủ lĩnh) và nhặt pickItemId trên map hiện tại. */
    void pickAndFight(int pickItemId, int mobId) {
        this.fieldAC(pickItemId);
        this.fieldAB(mobId, MOB_ANY);
    }

    /** Liệt kê caption menu đang mở (Menu.menuItems là private -> reflection, chỉ để log). */
    static String describeMenu() {
        try {
            java.lang.reflect.Field field = Menu.class.getDeclaredField("menuItems");
            field.setAccessible(true);
            MyVector items = (MyVector) field.get(GameCanvas.menu);
            StringBuffer sb = new StringBuffer("[");
            for (int i = 0; items != null && i < items.size(); i++) {
                Command cmd = (Command) items.elementAt(i);
                sb.append(i).append(':').append(cmd == null ? "null" : cmd.caption).append(i + 1 < items.size() ? " | " : "");
            }
            return sb.append(']').toString();
        } catch (Throwable e) {
            return "?" + e;
        }
    }

    /** Bước "yêu cầu đạt level": đánh quái ở map đến khi đủ level, sau đó gặp NPC. */
    void levelUp(int requiredLevel, int mapId, int mobId) {
        Char me = Char.getMyChar();
        if (me.clevel < requiredLevel) {
            this.grind(mapId, mobId, -1);
            return;
        }
        this.talkToTaskNpc("đủ level " + requiredLevel);
    }

    /** Đứng tại (x, y) trên map hiện tại; trả về true khi đã tới gần. */
    boolean standAt(int x, int y, int tolerance) {
        Char me = Char.getMyChar();
        if (Math.abs(me.cx - x) <= tolerance && Math.abs(me.cy - y) <= tolerance) {
            return true;
        }
        Char.fieldAC(x, y);
        return false;
    }

    /** Đi tới NPC của bước hiện tại (bảng mapTasks/tasks do server gửi) và gửi getTask. */
    void talkToTaskNpc(String reason) {
        int mapId = GameScr.gameBE();
        int npcId = GameScr.gameBF();
        int taskId = Char.getMyChar().ctaskId;
        if ((mapId < 0 || npcId < 0) && GameScr.tasks != null && taskId >= 0 && taskId < GameScr.tasks.length
                && GameScr.tasks[taskId] != null && GameScr.tasks[taskId].length > 0) {
            // Bước không gắn NPC (vd. "Trình độ đạt cấp 26"): dùng NPC giao nhiệm vụ.
            mapId = GameScr.mapTasks[taskId][0];
            npcId = GameScr.tasks[taskId][0];
        }
        if (mapId < 0 || npcId < 0) {
            Uplevel35.logEvery("npc-missing", 5000L, "NPC không xác định map=" + mapId + " npc=" + npcId
                    + " (" + reason + ")");
            return;
        }
        if (TileMap.mapID != mapId) {
            Uplevel35.logEvery("npc-route", 10000L, "ROUTE map " + TileMap.mapID + " -> " + mapId
                    + " gặp npc=" + npcId + " (" + reason + ")");
            this.goMap(mapId);
            return;
        }
        if (Uplevel35Support.walkToNpc(npcId) == null) {
            if (GameScr.fieldAI(npcId) == null) {
                Uplevel35.logEvery("npc-absent", 5000L, "NPC " + npcId + " không có trên map " + mapId);
            }
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.npcRequestAt < 5000L) {
            return;
        }
        this.npcRequestAt = now;
        Uplevel35.log("NPC gặp npc=" + npcId + " map=" + mapId + " (" + reason + ")");
        GameScr.fieldAB(npcId, 0, 0);
        Service.gI().getTask(npcId, 0, -1);
        LockGame.fieldAO();
    }

    /**
     * Tìm id vật phẩm theo một phần tên (không phân biệt hoa thường).
     * Trả về -1 khi chưa tìm được (template chưa tải). Kết quả được cache.
     */
    static int itemIdByName(String fragment) {
        Integer cached = (Integer) itemIdCache.get(fragment);
        if (cached != null) {
            return cached.intValue();
        }
        String needle = fragment.toLowerCase();
        Hashtable table = ItemTemplates.itemTemplates.gameAA;
        for (Enumeration e = table.elements(); e.hasMoreElements();) {
            ItemTemplate template = (ItemTemplate) e.nextElement();
            if (template != null && template.name != null && template.name.toLowerCase().indexOf(needle) >= 0) {
                itemIdCache.put(fragment, new Integer(template.id));
                Uplevel35.log("ITEM '" + fragment + "' -> id=" + template.id + " name=" + template.name
                        + " type=" + template.type);
                return template.id;
            }
        }
        return -1;
    }

    /** Tìm item trong túi theo một phần tên. */
    static Item findBagItemByName(String fragment) {
        Item[] bag = Char.getMyChar().arrItemBag;
        String needle = fragment.toLowerCase();
        for (int i = 0; bag != null && i < bag.length; i++) {
            Item item = bag[i];
            if (item != null && item.template != null && item.template.name != null
                    && item.template.name.toLowerCase().indexOf(needle) >= 0) {
                return item;
            }
        }
        return null;
    }

    // =================================================================== log

    static String describeTask(Char me) {
        StringBuilder out = new StringBuilder();
        out.append("ctaskId=").append(me.ctaskId);
        Task task = me.taskMaint;
        if (task == null) {
            out.append(" taskMaint=null");
        } else {
            out.append(" index=").append(task.index).append(" count=").append(task.count);
            if (task.names != null && task.names.length > 0) {
                out.append(" name=").append(join(task.names));
            }
            if (task.subNames != null) {
                out.append(" subs=[");
                for (int i = 0; i < task.subNames.length; i++) {
                    if (i > 0) {
                        out.append(" | ");
                    }
                    out.append(i).append(':').append(task.subNames[i]);
                    if (task.counts != null && i < task.counts.length) {
                        out.append('(').append(task.counts[i]).append(')');
                    }
                }
                out.append(']');
            }
        }
        out.append(" taskNpcMap=").append(GameScr.gameBE()).append(" taskNpc=").append(GameScr.gameBF());
        return out.toString();
    }

    private static String join(String[] parts) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                out.append(' ');
            }
            out.append(parts[i]);
        }
        return out.toString();
    }

    private void logState(Char me) {
        Task task = me.taskMaint;
        String state = "name=" + me.cName + " level=" + me.clevel + " map=" + TileMap.mapID
                + " zone=" + TileMap.zoneID + " task=" + me.ctaskId
                + (task == null ? ":-" : ":" + task.index + " count=" + task.count);
        long now = System.currentTimeMillis();
        if (state.equals(this.lastState) && now - this.stateLogAt < 30000L) {
            return;
        }
        this.lastState = state;
        this.stateLogAt = now;
        String need = GameScr.exps != null && me.clevel >= 0 && me.clevel < GameScr.exps.length
                ? String.valueOf(GameScr.exps[me.clevel]) : "?";
        Mob focus = me.mobFocus;
        String mob = focus == null ? "none" : ("#" + focus.templateId + " hp=" + focus.hp + "/" + focus.maxHp);
        Uplevel35.log("STATE " + state + " exp=" + me.gameBE + "/" + need + " hp=" + me.cHP + "/" + me.cMaxHP
                + " pos=" + me.cx + "," + me.cy + " mob=" + mob
                + " sp=" + me.sPoint + " skill=" + (me.myskill == null || me.myskill.template == null
                        ? "?" : me.myskill.template.id + ":" + me.myskill.point)
                + " eff=" + Uplevel35Support.effectsLog(me));
    }
}
