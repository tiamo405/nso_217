/** Receive, fight and turn in Ta Thu tasks until all four daily runs are done. */
public final class AutoTaThuDaily extends Auto {
    private static final int TASK_ID = 1;
    private static final int TASK_NPC = 25;
    private static final int TASK_MAP = 1;
    private static final int TASK_ZONE = 21;
    private static final int MAX_DAILY_TASKS = 4;
    private TaThuDailyState state;
    private boolean returningTask;
    private int receiveAttempts;
    private long nextActionAt;
    private boolean finished;
    private long startedAt;

    public void fieldAD() {
        super.fieldAD();
        this.state = TaThuDailyState.loadCurrent();
        this.returningTask = false;
        this.receiveAttempts = 0;
        this.nextActionAt = 0L;
        this.finished = false;
        this.startedAt = System.currentTimeMillis();
        System.out.println("AUTO TA THU DAILY: bắt đầu ordersUsed=" + this.state.ordersUsed
                + " completed=" + this.state.questsCompleted
                + " savedMap=" + this.state.mapId + " savedZone=" + this.state.zoneId
                + " savedKill=" + this.state.killId);
    }

    public void fieldAA() {
        if (this.finished || System.currentTimeMillis() < this.nextActionAt) {
            return;
        }
        if (Auto.fieldAF()) {
            Auto.fieldAA(true);
            this.nextActionAt = System.currentTimeMillis() + 1500L;
            return;
        }

        TaskOrder task = Char.fieldAM(TASK_ID);
        if (this.returningTask) {
            if (task == null) {
                this.returningTask = false;
                this.reloadServerConfirmedProgress();
                System.out.println("AUTO TA THU DAILY: server đã xóa task, hoàn thành="
                        + this.state.questsCompleted + "/" + MAX_DAILY_TASKS);
                this.nextActionAt = System.currentTimeMillis() + 1000L;
                if (this.state.questsCompleted >= MAX_DAILY_TASKS || this.refreshRemainingRuns() <= 0) {
                    this.finishDaily();
                } else if ("fight".equals(TaThuAccountManager.getStage())) {
                    TaThuAccountManager.onTestStageFinished("đã đánh và trả thành công một nhiệm vụ");
                } else if (this.state.ordersUsed < 2) {
                    // A restarted worker may have lost local order state while
                    // the server still has an active task. Only use missing
                    // orders after that task has been safely turned in.
                    this.finished = true;
                    System.out.println("AUTO TA THU DAILY: task đã trả; quay lại kiểm tra Tà Thú Lệnh còn thiếu");
                    AutoTaThuOrders orders = new AutoTaThuOrders(false);
                    orders.fieldAD();
                    Code.fieldAA((Auto) orders);
                }
                return;
            }
            this.nextActionAt = System.currentTimeMillis() + 1000L;
            return;
        }

        if (task == null) {
            this.reloadServerConfirmedProgress();
            if (this.state.mapId >= 0 && this.state.zoneId >= 0 && this.state.killId >= 0
                    && System.currentTimeMillis() - this.startedAt < 8000L) {
                return;
            }
            if (this.state.finished || this.state.questsCompleted >= MAX_DAILY_TASKS) {
                this.finishDaily();
                return;
            }
            int remaining = this.refreshRemainingRuns();
            if (remaining == 0 && this.state.ordersUsed >= 2) {
                this.finishDaily();
                return;
            }
            if (this.state.questsCompleted > 0 && this.state.ordersUsed < 2) {
                this.switchToOrders();
                return;
            }
            if (TileMap.mapID != TASK_MAP) {
                this.fieldAA(TASK_MAP, -1, -1, -1);
                return;
            }
            if (++this.receiveAttempts > 5) {
                System.out.println("AUTO TA THU DAILY: nhận task quá 5 lần chưa thành công; chờ 30 giây rồi thử lại, không đánh dấu hoàn tất");
                this.receiveAttempts = 0;
                this.nextActionAt = System.currentTimeMillis() + 30000L;
                return;
            }
            long sequence = TaThuAccountManager.getMessageSequence();
            System.out.println("AUTO TA THU DAILY: nhận nhiệm vụ tại npc=25 map=1 zone=" + TileMap.zoneID + " menu="
                    + (GameScr.fieldGH + 1) + ",0 lần=" + this.receiveAttempts);
            GameScr.fieldAB(TASK_NPC, GameScr.fieldGH + 1, 0);
            LockGame.fieldAK();
            this.nextActionAt = System.currentTimeMillis() + 1500L;
            String response = TaThuAccountManager.getLastMessageSince(sequence);
            if (isNoRunsMessage(response)) {
                System.out.println("AUTO TA THU DAILY: NPC xác nhận hết lượt: " + response);
                this.finishDaily();
            }
            return;
        }

        this.receiveAttempts = 0;
        this.state.rememberTask(task);
        if ("receive".equals(TaThuAccountManager.getStage())) {
            TaThuAccountManager.onTestStageFinished("đã nhận TaskOrder id=" + task.taskId
                    + " map=" + task.mapId + " killId=" + task.killId
                    + " count=" + task.count + "/" + task.maxCount);
            return;
        }
        if (task.count >= task.maxCount) {
            if (TileMap.mapID != TASK_MAP) {
                this.fieldAA(TASK_MAP, -1, -1, -1);
                return;
            }
            System.out.println("AUTO TA THU DAILY: trả nhiệm vụ count=" + task.count + "/" + task.maxCount
                    + " npc=25 menu=" + (GameScr.fieldGH + 1) + ",2");
            this.returningTask = true;
            GameScr.fieldAB(TASK_NPC, GameScr.fieldGH + 1, 2);
            LockGame.fieldAM();
            this.nextActionAt = System.currentTimeMillis() + 1500L;
            return;
        }

        System.out.println("AUTO TA THU DAILY: tiếp tục task map=" + task.mapId
                + " savedZone=" + this.state.zoneId + " killId=" + task.killId
                + " count=" + task.count + "/" + task.maxCount);
        TaThu fighter = Code.fieldAE;
        fighter.fieldAD();
        Code.fieldAA((Auto) fighter);
    }

    private int refreshRemainingRuns() {
        Char me = Char.getMyChar();
        me.countLoopBoos = -128;
        Service.gI().viewInfo(me.cName, 0);
        long deadline = System.currentTimeMillis() + 4000L;
        while (me.countLoopBoos == -128 && System.currentTimeMillis() < deadline) {
            Auto.fieldAA(100L);
        }
        System.out.println("AUTO TA THU DAILY: server remaining=" + me.countLoopBoos);
        return me.countLoopBoos;
    }

    private void finishDaily() {
        if (this.finished) {
            return;
        }
        this.finished = true;
        this.state.finished = true;
        this.state.clearTaskLocation();
        this.state.save();
        Code.fieldAC();
        TaThuAccountManager.onTaThuDailyFinished();
    }

    private void reloadServerConfirmedProgress() {
        TaThuDailyState latest = TaThuDailyState.loadCurrent();
        if (latest.questsCompleted >= this.state.questsCompleted) {
            this.state = latest;
        }
    }

    private void switchToOrders() {
        this.finished = true;
        System.out.println("AUTO TA THU DAILY: task đã được server xác nhận; kiểm tra Tà Thú Lệnh còn thiếu");
        AutoTaThuOrders orders = new AutoTaThuOrders(false);
        orders.fieldAD();
        Code.fieldAA((Auto) orders);
    }

    private static boolean isNoRunsMessage(String message) {
        if (message == null) {
            return false;
        }
        String lower = message.toLowerCase();
        return (lower.indexOf("tà thú") >= 0 || lower.indexOf("ta thu") >= 0)
                && (lower.indexOf("hết") >= 0 || lower.indexOf("ngày hôm sau") >= 0
                || lower.indexOf("không thể") >= 0 || lower.indexOf("tối đa") >= 0);
    }

    public static int savedZoneFor(int mapId, int killId) {
        if (!TaThuAccountManager.isEnabledRuntime()) {
            return -1;
        }
        TaThuDailyState state = TaThuDailyState.loadCurrent();
        return state.mapId == mapId && state.killId == killId ? state.zoneId : -1;
    }

    public static void rememberTarget(int mapId, int zoneId, int killId, int mobX, int mobY) {
        if (!TaThuAccountManager.isEnabledRuntime()) {
            return;
        }
        TaThuDailyState state = TaThuDailyState.loadCurrent();
        if (state.mapId == mapId && state.killId == killId && state.zoneId < 0) {
            state.zoneId = zoneId;
            state.mobX = mobX;
            state.mobY = mobY;
            state.save();
            System.out.println("AUTO TA THU LOCK: map=" + mapId + " zone=" + zoneId
                    + " killId=" + killId + " mob=" + mobX + "," + mobY);
        }
    }

    public String toString() {
        return "Auto 4 Nhiệm Vụ Tà Thú";
    }
}
