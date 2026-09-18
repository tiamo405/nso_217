public final class AutoNvhn extends Auto {
   public static volatile boolean fieldAV;
   private static String[] fieldAW;
   private static volatile int fieldAX;
   private volatile TaskOrder fieldAY;
   private long lastStatusLog;
   private int lastYen;
   private int lastXu;
   private int lastLuong;
   private volatile boolean waitingForNewTask;
   private volatile boolean taskAcceptanceRecoveryUsed;
   private long lastReturnTaskLog;
   private boolean didDailyWorkThisRun;

   private static void fieldAM() {
      fieldAV = false;
      fieldAW = new String[]{"Hôm nay con đã làm hết nhiệm vụ ta giao. Hãy quay lại vào ngày hôm sau.", "Đây là lần nhận nhiệm vụ thứ ", " trong ngày hôm nay. Mỗi ngày được nhận tối đa 20 lần con nhé."};
   }

   static {
      fieldAM();
   }

   public static void fieldAA(String var0) {
      if (isDailyLimitMessage(var0)) {
         fieldAX = 21;
         LockGame.fieldAL();
         AccountAutoManager.onDailyLimitReached();
      } else {
         int var1;
         if ((var1 = var0.indexOf(fieldAW[1])) >= 0) {
            var0 = var0.substring(var1 + fieldAW[1].length(), var0.indexOf(fieldAW[2])).trim();

            try {
               fieldAX = Integer.parseInt(var0);
               return;
            } catch (NumberFormatException var2) {
            }
         }

      }
   }

   public static boolean isDailyLimitMessage(String message) {
      return message != null && message.indexOf("Hôm nay con đã làm hết nhiệm vụ ta giao") >= 0;
   }

   public static boolean isBelowLevel30Message(String message) {
      return message != null
              && message.indexOf("cấp 30") >= 0
              && (message.indexOf("luyện tập") >= 0 || message.indexOf("quay lại đây") >= 0);
   }

   /**
    * NPC 25 sends this when the character has not unlocked enough main-story
    * areas for a daily mission. Retrying the same menu request cannot change
    * that prerequisite, so the account runner must skip this character.
    */
   public static boolean isDailyTaskUnavailableMessage(String message) {
      return message != null
              && message.indexOf("không có nhiệm vụ phù hợp với cấp độ và tiến trình") >= 0
              && message.indexOf("hoàn thành nhiệm vụ chính tuyến") >= 0;
   }

   public static boolean isTaskAcceptanceRequiredMessage(String message) {
      return message != null
              && message.indexOf("Hãy nhận nhiệm vụ mỗi ngày từ ta") >= 0
              && message.indexOf("sử dụng tính năng này") >= 0;
   }

   public final void fieldAD() {
      fieldAX = 0;
      this.fieldAY = Char.fieldAM(0);
      Char me = Char.getMyChar();
      this.lastStatusLog = 0L;
      this.lastYen = me.yen;
      this.lastXu = me.xu;
      this.lastLuong = me.luong;
      this.waitingForNewTask = false;
      this.taskAcceptanceRecoveryUsed = false;
      this.lastReturnTaskLog = 0L;
      this.didDailyWorkThisRun = this.fieldAY != null;
      super.fieldAD();
   }

   public final boolean didDailyWorkThisRun() {
      return this.didDailyWorkThisRun;
   }

   public final void fieldAE() {
      this.fieldAY = Char.fieldAM(0);
   }

   public final void fieldAA() {
      this.logStatus();
      if (fieldAX <= 20 && (!(super.fieldAJ instanceof Stanima) || System.currentTimeMillis() - super.fieldAI < 3600000L)) {
         if (Char.getMyChar().cHP <= 0) {
            Auto.fieldAA(false);
            return;
         }

         if (TileMap.isTruong(TileMap.mapID)) {
            if (this.fieldAY == null) {
               GameScr.fieldAC("Nhận NV " + (fieldAX + 1) + "/20");
               // NPC25 có thể có các mục menu động đứng trước nhóm NVHN.
               // fieldGH là offset do server gửi về; submenu NVHN là:
               // 0=Nhận, 1=Hủy, 2=Hoàn thành, 3=Đi làm NV.
               GameScr.fieldAB(25, GameScr.fieldGH, 0);
               LockGame.fieldAK();
               this.fieldAY = Char.fieldAM(0);
               if (this.waitingForNewTask && this.fieldAY != null
                       && this.fieldAY.count < this.fieldAY.maxCount) {
                  this.didDailyWorkThisRun = true;
                  this.waitingForNewTask = false;
                  if (fieldAX < 20) {
                     ++fieldAX;
                  }
                  System.out.println("AUTO NVHN: server đã cấp nhiệm vụ mới, bộ đếm=" + fieldAX + "/20");
               }
               return;
            }

            if (this.fieldAY.count >= this.fieldAY.maxCount) {
               this.didDailyWorkThisRun = true;
               if (Char.fieldBF() <= 0) {
                  GameScr.fieldAC("Hành trang đầy");
                  return;
               }

               GameScr.fieldAC("Hoàn thành NV " + fieldAX + "/20");
               BotMetrics.returningTask();
               GameScr.fieldAB(25, GameScr.fieldGH, 2);
               this.waitingForNewTask = true;
               this.fieldAY = null;
               return;
            }

            GameScr.fieldAC("Đi làm NV " + fieldAX + "/20");
            // fieldAY đã tồn tại nghĩa là server đã cấp nhiệm vụ. Chọn đúng
            // nút "Đi làm NV" để server chuyển nhân vật sang map nhiệm vụ.
            GameScr.fieldAB(25, GameScr.fieldGH, 3);
            TileMap.fieldAF();
            this.fieldAB(super.fieldAC);
            return;
         }

         if (this.fieldAY == null) {
            int classId = Char.getMyChar().nClass.classId;
            int schoolMap = classId <= 2 ? 1 : (classId <= 4 ? 27 : 72);
            this.fieldAA(schoolMap, -2, -1, -1);
            return;
         }

         if (TileMap.mapID != this.fieldAY.mapId) {
            this.fieldAA(this.fieldAY.mapId, -2, -1, -1);
            return;
         }

         if (this.fieldAY.count >= this.fieldAY.maxCount) {
            int schoolMap = this.getCharacterSchoolMap();
            GameScr.fieldAC("Về trường trả NV " + fieldAX + "/20");
            this.logReturnToSchool(schoolMap);
            this.fieldAA(schoolMap, -2, -1, -1);
            return;
         }

         this.fieldAB(this.fieldAY.killId, 1);
         this.didDailyWorkThisRun = true;
         this.fieldAC(-1);
         if (fieldAV) {
            GameScr.fieldAC("Nhiệm vụ " + fieldAX + "/20: " + this.fieldAY.count + "/" + this.fieldAY.maxCount + " " + Mob.arrMobTemplate[this.fieldAY.killId].name);
            fieldAV = false;
            return;
         }
      } else {
         GameScr.fieldAC("Hoàn thành!");
         Code.fieldAC();
         AccountAutoManager.onDailyTasksFinished();
      }

   }

   /**
    * Recovers one stale local task after NPC25 says the daily task must be
    * accepted first. The next tick will request a fresh task from the server.
    * A second occurrence is handled by AccountAutoManager as a terminal
    * character failure, so this cannot become another retry loop.
    */
   public final boolean recoverTaskAcceptanceRequired() {
      if (this.taskAcceptanceRecoveryUsed) {
         return false;
      }
      this.taskAcceptanceRecoveryUsed = true;
      MyVector tasks = Char.getMyChar().taskOrders;
      for (int index = tasks.size() - 1; index >= 0; --index) {
         TaskOrder current = (TaskOrder) tasks.elementAt(index);
         if (current != null && current.taskId == 0 && current.count < current.maxCount) {
            tasks.removeElementAt(index);
         }
      }
      this.fieldAY = null;
      this.waitingForNewTask = false;
      fieldAV = false;
      Char.getMyChar().gameAC(21);
      LockGame.fieldAL();
      return true;
   }

   /** Clears the one-shot recovery gate after the server sends a new task. */
   public final void onDailyTaskReceived() {
      this.taskAcceptanceRecoveryUsed = false;
   }

   private int getCharacterSchoolMap() {
      int classId = Char.getMyChar().nClass.classId;
      return classId <= 2 ? 1 : (classId <= 4 ? 27 : 72);
   }

   private void logReturnToSchool(int schoolMap) {
      long now = System.currentTimeMillis();
      if (now - this.lastReturnTaskLog < 30000L) {
         return;
      }
      this.lastReturnTaskLog = now;
      System.out.println("AUTO NVHN: đã đủ mục tiêu "
              + this.fieldAY.count + "/" + this.fieldAY.maxCount
              + ", về trường map " + schoolMap + " để trả nhiệm vụ");
   }

   private void logStatus() {
      long now = System.currentTimeMillis();
      if (now - this.lastStatusLog < 30000L) {
         return;
      }
      this.lastStatusLog = now;

      Char me = Char.getMyChar();
      int yenDelta = me.yen - this.lastYen;
      int xuDelta = me.xu - this.lastXu;
      int luongDelta = me.luong - this.lastLuong;
      this.lastYen = me.yen;
      this.lastXu = me.xu;
      this.lastLuong = me.luong;

      String state;
      String progress = "-";
      if (TileMap.isTruong(TileMap.mapID)) {
         if (this.fieldAY == null) {
            state = "đang nhận nhiệm vụ";
         } else if (this.fieldAY.count >= this.fieldAY.maxCount) {
            state = "đang trả nhiệm vụ";
         } else {
            state = "đang tới map nhiệm vụ";
            progress = this.fieldAY.count + "/" + this.fieldAY.maxCount;
         }
      } else if (this.fieldAY == null) {
         state = "đang về trường";
      } else if (TileMap.mapID != this.fieldAY.mapId) {
         state = "đang di chuyển tới map " + this.fieldAY.mapId;
         progress = this.fieldAY.count + "/" + this.fieldAY.maxCount;
      } else {
         state = "đang đánh " + Mob.arrMobTemplate[this.fieldAY.killId].name;
         progress = this.fieldAY.count + "/" + this.fieldAY.maxCount;
      }

      System.out.println("AUTO NVHN STATUS: username=" + AccountAutoManager.getCurrentUsername()
              + " nv=" + me.cName
              + " level=" + me.clevel
              + " map=" + TileMap.mapID + "(" + TileMap.mapName + ")"
              + " state=" + state
              + " nvhn=" + fieldAX + "/20"
              + " progress=" + progress
              + " hp=" + me.cHP + "/" + me.cMaxHP
              + " yen=" + me.yen + formatDelta(yenDelta)
              + " xu=" + me.xu + formatDelta(xuDelta)
              + " luong=" + me.luong + formatDelta(luongDelta));
   }

   private static String formatDelta(int value) {
      if (value > 0) {
         return "(+" + value + ")";
      }
      if (value < 0) {
         return "(" + value + ")";
      }
      return "";
   }

   public final String toString() {
      return "Auto Nvhn: " + fieldAX + "/20";
   }
}
