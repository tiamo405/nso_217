public final class TaThu extends Auto {
   private TaskOrder fieldAY;
   public int fieldAV;
   public static boolean fieldAW;
   public static long fieldAX;
   private boolean missionZoneLocked;
   private long lastMissionStatusAt;
   private boolean missionTargetSeen;
   private long missionTargetMissingSince;
   private int pkDeathCount;
   private boolean wasDeadLastTick;
   private long lastPlayerAttackAt;
   private int lastPlayerAttackerId;
   private String lastPlayerAttackerName;
   private long lastMobHpChange;
   private int lastMobHp;
   private int pkAvoidanceZoneOffset;
   private int missionAttackCount;
   private long lastMissionAttackAt;
   private int lastSentSkillTemplateId;
   private short lastSentSkillId;
   private int serverAttackAckCount;
   private long lastServerAttackAckAt;
   private int lastServerSkillTemplateId;
   private int lastServerTargetMobId;
   private boolean lastServerAckIncludedMissionTarget;
   private long foodSupplyCheckAfter;
   private long foodMissingSince;
   private boolean supplyingFood;

   // Static field để Auto.fieldAA(int,int) biết đang lock boss Tà Thú level 3
   public static int targetTaThuTemplateId = -1;
   public static int targetTaThuLevelBoss = -1;

   public final void fieldAD() {
      super.fieldAD();
      this.fieldAY = Char.fieldAM(1);
      this.pkDeathCount = 0;
      this.wasDeadLastTick = false;
      this.clearPlayerAttackMarker();
      this.lastMobHpChange = System.currentTimeMillis();
      this.lastMobHp = -1;
      this.pkAvoidanceZoneOffset = 0;
      this.missionAttackCount = 0;
      this.lastMissionAttackAt = 0L;
      this.resetSkillTracking();
      this.foodSupplyCheckAfter = System.currentTimeMillis() + 3000L;
      this.foodMissingSince = 0L;
      this.supplyingFood = false;
      if (this.fieldAY != null) {
         this.fieldAV = this.fieldAY.killId;
         super.fieldAB = this.fieldAY.mapId;
         int savedZone = AutoTaThuDaily.savedZoneFor(super.fieldAB, this.fieldAV);
         if (savedZone >= 0) {
            super.fieldAC = savedZone;
            this.missionZoneLocked = true;
            this.missionTargetSeen = false;
            this.missionTargetMissingSince = 0L;
            System.out.println("AUTO TA THU RESUME: khóa lại map=" + super.fieldAB
                    + " zone=" + savedZone + " killId=" + this.fieldAV);
            return;
         }
         if (TileMap.mapID == this.fieldAY.mapId && TileMap.zoneID % 5 == 0) {
            super.fieldAC = TileMap.zoneID;
            return;
         }
      }

      super.fieldAC = 5;
      this.unlockMissionZone();
      fieldAW = false;
   }

   public final void fieldAC(int var1, int var2) {
      super.fieldAD();
      this.fieldAY = null;
      this.fieldAV = var2;
      super.fieldAB = var1;
      if (TileMap.mapID == var1 && TileMap.zoneID % 5 == 0) {
         super.fieldAC = TileMap.zoneID;
      } else {
         super.fieldAC = 5;
      }
      this.unlockMissionZone();
   }

   public final void fieldAE() {
      this.fieldAY = Char.fieldAM(1);
      super.fieldAE();
   }

   public final void fieldAA() {
      this.checkPkDeath();
      this.updateMissionZoneLock();
      this.syncMissionFocus();
      this.logMissionStatus();
      if (this.startFoodSupplyIfNeeded()) {
         return;
      }
      if (super.fieldAB >= 0 && (!(super.fieldAJ instanceof Stanima) || System.currentTimeMillis() - super.fieldAI < 3600000L)) {
         boolean var10000;
         int var1;
         if (Auto.fieldAF()) {
            if (Char.fieldFI && TileMap.mapID == super.fieldAB && TileMap.zoneID == super.fieldAC && Char.getMyChar().mobFocus != null && Char.getMyChar().mobFocus.hp < Char.getMyChar().mobFocus.maxHp / 20) {
               var1 = 0;

               while(true) {
                  if (var1 >= GameScr.vParty.size()) {
                     var10000 = false;
                     break;
                  }

                  Party var2;
                  if ((var2 = (Party)GameScr.vParty.elementAt(var1)).c != null && var2.c.cHP > 0) {
                     var10000 = true;
                     break;
                  }

                  ++var1;
               }
            } else {
               var10000 = false;
            }

            if (!var10000) {
               Auto.fieldAA(true);
               return;
            }
         } else if (TileMap.mapID == super.fieldAB && TileMap.zoneID == super.fieldAC) {
            if (this.fieldAY != null && this.fieldAY.count >= this.fieldAY.maxCount) {
               GameScr.fieldAC("Xong Tà Thú");
               Code.fieldAC();
               return;
            }

            if (Char.getMyChar().cName.equals(Code.fieldAH)) {
               if (Char.getMyChar().mobFocus != null && Char.getMyChar().mobFocus.hp < Char.getMyChar().mobFocus.maxHp / 10) {
                  if (!LockGame.fieldBG()) {
                     Service.gI().chatParty("waitGr");
                     LockGame.fieldAA(200000L);
                     Service.gI().chatParty("notifyGr");
                  }

                  var10000 = false;
               } else {
                  var10000 = false;
               }
            } else {
               if (fieldAW && System.currentTimeMillis() - fieldAX > 120000L) {
                  fieldAW = false;
               }

               var10000 = fieldAW;
            }

            if (!var10000) {
               boolean justLocked = this.rememberMissionTargetIfPresent();
               Mob boss = this.findLiveMissionTarget();
               if (boss != null) {
                  // Luôn ép focus về đúng object boss còn sống trong vMob. Không
                  // tin mobFocus cũ vì packet attack có thể ghi đè bằng quái
                  // thường cùng templateId.
                  Char.getMyChar().mobFocus = boss;
                  // Gọi fieldAB chỉ để di chuyển và gửi đòn đánh — KHÔNG dùng
                  // templateId làm filter vì selector sẽ chọn cả quái thường
                  // cùng templateId (levelBoss=0). Thay vào đó dùng templateId=-1
                  // để selector fallback vào mobFocus hiện tại (đã được gán boss).
                  this.fieldAB(-1, 8);
                  // Sau khi fieldAB chạy, đảm bảo mobFocus vẫn là boss
                  if (boss.hp > 0 && boss.status != 0 && boss.status != 1) {
                     Char.getMyChar().mobFocus = boss;
                  }
               } else {
                  this.clearStaleMobFocus();
                  // Khi đã lock mà boss tạm biến mất: chờ grace 20 giây,
                  // không chọn quái thường. Khi chưa lock: dùng selector
                  // chung với templateId để tìm và di chuyển về phía Tà Thú.
                  // Việc lock boss levelBoss=3 được thực hiện bởi
                  // rememberMissionTargetIfPresent() ở tick khi boss xuất hiện.
                  if (!this.missionZoneLocked && !justLocked) {
                     this.fieldAB(this.fieldAV, 8);
                  }
               }
               // Trường hợp vừa lock nhưng object đã biến mất ngay trong tick,
               // không gọi selector chung để tránh focus rơi sang mob thường.
               if (justLocked && this.findLiveMissionTarget() == null) {
                  Char.getMyChar().mobFocus = null;
               }
            }

            if (Char.getMyChar().cMP < Char.getMyChar().cMaxMP * Char.aMpValue / 100) {
               Char.getMyChar().gameAE(17);
            }

            if (Char.getMyChar().cHP < Char.getMyChar().cMaxHP * Char.aHpValue / 100) {
               var1 = (int)(System.currentTimeMillis() / 1000L);

               for(int var4 = 0; var4 < Char.getMyChar().vEff.size(); ++var4) {
                  Effect var3;
                  if ((var3 = (Effect)Char.getMyChar().vEff.elementAt(var4)).template.id == 21 && var3.timeLenght - (var1 - var3.timeStart) >= 2) {
                     return;
                  }
               }

               Char.getMyChar().gameAE(16);
               return;
            }
         } else {
            this.fieldAA(super.fieldAB, super.fieldAC, super.fieldAE, super.fieldAF);
         }

      } else {
         Code.fieldAC();
      }
   }

   /**
    * Tìm Tà Thú (levelBoss=3) trong vMob hiện tại, lock zone và gán mobFocus.
    * @return true nếu vừa tìm thấy và lock được boss trong tick này (caller nên skip fieldAB())
    */
   private boolean rememberMissionTargetIfPresent() {
      if (this.missionZoneLocked || !TaThuAccountManager.isEnabledRuntime()) {
         return false;
      }
      for (int index = 0; index < GameScr.vMob.size(); ++index) {
         Mob mob = (Mob)GameScr.vMob.elementAt(index);
         if (mob != null && mob.hp > 0 && mob.status != 0 && mob.status != 1
                 && mob.templateId == this.fieldAV && mob.levelBoss == 3) {
            System.out.println("AUTO TA THU LOCK: tìm thấy Tà Thú templateId=" + mob.templateId
                + " levelBoss=" + mob.levelBoss + " hp=" + mob.hp + "/" + mob.maxHp
                + " tại zone=" + super.fieldAC);
            this.missionZoneLocked = true;
            // Set static fields để Auto.fieldAA(int,int) biết đang lock boss level 3
            targetTaThuTemplateId = mob.templateId;
            targetTaThuLevelBoss = 3;
            // Gán mobFocus ngay — phải làm trước fieldAB() gọi, và caller sẽ skip fieldAB()
            // trong tick này để tránh bị ghi đè bằng quái thường (levelBoss=0).
            Char.getMyChar().mobFocus = mob;
            this.lastMobHp = mob.hp;
            this.lastMobHpChange = System.currentTimeMillis();
            this.missionAttackCount = 0;
            this.lastMissionAttackAt = 0L;
            this.resetSkillTracking();
            AutoTaThuDaily.rememberTarget(super.fieldAB, super.fieldAC, this.fieldAV, mob.x, mob.y);
            return true;
         }
      }
      return false;
   }

   /** Trả về đúng Tà Thú nhiệm vụ còn sống trong zone hiện tại. */
   private Mob findLiveMissionTarget() {
      for (int index = 0; index < GameScr.vMob.size(); ++index) {
         Mob mob = (Mob)GameScr.vMob.elementAt(index);
         if (mob != null && mob.hp > 0 && mob.status != 0 && mob.status != 1
                 && mob.templateId == this.fieldAV && mob.levelBoss == 3) {
            return mob;
         }
      }
      return null;
   }

   /** Đồng bộ focus trước cả log trạng thái và nhánh chiến đấu của mỗi tick. */
   private void syncMissionFocus() {
      if (!this.missionZoneLocked) {
         return;
      }
      Char me = Char.getMyChar();
      if (me == null) {
         return;
      }
      Mob boss = this.findLiveMissionTarget();
      if (boss != null) {
         me.mobFocus = boss;
      } else if (me.mobFocus != null
              && (me.mobFocus.templateId != this.fieldAV || me.mobFocus.levelBoss != 3)) {
         me.mobFocus = null;
      }
   }

   private void updateMissionZoneLock() {
      if (!this.missionZoneLocked || TileMap.mapID != super.fieldAB || TileMap.zoneID != super.fieldAC) {
         return;
      }
      boolean found = false;
      for (int index = 0; index < GameScr.vMob.size(); ++index) {
         Mob mob = (Mob)GameScr.vMob.elementAt(index);
         if (mob != null && mob.hp > 0 && mob.status != 0 && mob.status != 1
                 && mob.templateId == this.fieldAV && mob.levelBoss == 3) {
            found = true;
            break;
         }
      }
      if (found) {
         this.missionTargetSeen = true;
         this.missionTargetMissingSince = 0L;
         return;
      }
      if (this.missionTargetMissingSince == 0L) {
         this.missionTargetMissingSince = System.currentTimeMillis();
         return;
      }
      // If the task count already advanced (server confirmed the kill),
      // the zone is done — release it immediately regardless of grace time.
      if (this.fieldAY != null && this.fieldAY.count >= this.fieldAY.maxCount) {
         int oldZone = super.fieldAC;
         this.unlockMissionZone();
         System.out.println("AUTO TA THU NEXT: task server đã xác nhận count="
                 + this.fieldAY.count + "/" + this.fieldAY.maxCount
                 + " map=" + super.fieldAB + " zone=" + oldZone + "; mở khóa");
         return;
      }
      // Keep the exact zone across death/reconnect, but once the live target
      // has disappeared and this task is still unfinished, another worker got
      // the last hit. Use a longer grace when we have seen the target to give
      // the server time to send the task progress packet before switching zone.
      long grace = this.missionTargetSeen ? 20000L : 12000L;
      if (System.currentTimeMillis() - this.missionTargetMissingSince >= grace) {
         int oldZone = super.fieldAC;
         this.unlockMissionZone();
         TaThuDailyState state = TaThuDailyState.loadCurrent();
         if (state.mapId == super.fieldAB && state.killId == this.fieldAV && state.zoneId == oldZone) {
            state.clearLockedZone();
         }
         System.out.println("AUTO TA THU NEXT: Tà Thú không còn ở map=" + super.fieldAB
                 + " zone=" + oldZone + " sau " + grace/1000 + "s, task chưa xong"
                 + "; attacksSent=" + this.missionAttackCount
                 + "; lastAttackAgo=" + (this.lastMissionAttackAt == 0L ? -1L
                 : System.currentTimeMillis() - this.lastMissionAttackAt) + "ms"
                 + "; mở khóa để tìm khu tiếp theo");
      }
   }

   public boolean isMissionZoneLocked() {
      return this.missionZoneLocked;
   }

   private boolean startFoodSupplyIfNeeded() {
      long now = System.currentTimeMillis();
      if (this.supplyingFood || now < this.foodSupplyCheckAfter || Auto.fieldAF()) {
         return false;
      }
      if (!AutoTaThuFoodSupply.needsSupply()) {
         this.foodMissingSince = 0L;
         return false;
      }
      // The automatic food user may receive the item-consumed packet just
      // before the new effect packet. Debounce to avoid leaving combat during
      // that short, valid transition.
      if (this.foodMissingSince == 0L) {
         this.foodMissingSince = now;
         return false;
      }
      if (now - this.foodMissingSince < 2000L) {
         return false;
      }
      this.supplyingFood = true;
      this.foodMissingSince = 0L;
      Char me = Char.getMyChar();
      if (me != null) {
         me.mobFocus = null;
      }
      // Do not let time spent shopping count toward the locked-zone missing
      // grace period when this same fighter instance resumes.
      this.missionTargetMissingSince = 0L;
      System.out.println("AUTO TA THU FOOD: hết hiệu ứng và trong túi không còn thức ăn level="
              + Char.aFoodValue + "; tạm dừng đánh, giữ khóa map=" + super.fieldAB
              + " zone=" + super.fieldAC + " killId=" + this.fieldAV);
      AutoTaThuFoodSupply supply = new AutoTaThuFoodSupply(this);
      supply.fieldAD();
      Code.fieldAA((Auto)supply);
      return true;
   }

   public void onFoodSupplyFinished() {
      this.supplyingFood = false;
      this.foodSupplyCheckAfter = System.currentTimeMillis() + 3000L;
      this.foodMissingSince = 0L;
      this.missionTargetMissingSince = 0L;
      this.missionTargetSeen = false;
      Char me = Char.getMyChar();
      if (me != null) {
         me.mobFocus = null;
      }
      System.out.println("AUTO TA THU FOOD: khôi phục fighter map=" + super.fieldAB
              + " zone=" + super.fieldAC + " killId=" + this.fieldAV
              + "; sẽ chỉ focus boss levelBoss=3 đúng nhiệm vụ");
   }

   /**
    * Helper: unlock zone và reset static tracking fields
    */
   private void unlockMissionZone() {
      this.missionZoneLocked = false;
      this.missionTargetSeen = false;
      this.missionTargetMissingSince = 0L;
      targetTaThuTemplateId = -1;
      targetTaThuLevelBoss = -1;
   }

   private void logMissionStatus() {
      if (!TaThuAccountManager.isEnabledRuntime()) {
         return;
      }
      long now = System.currentTimeMillis();
      if (now - this.lastMissionStatusAt < 10000L) {
         return;
      }
      this.lastMissionStatusAt = now;
      Char me = Char.getMyChar();
      Mob focus = me.mobFocus;
      // Tìm boss thực tế trong vMob để log vị trí so với nhân vật
      Mob actualBoss = this.findLiveMissionTarget();
      Skill configuredSkill = Auto.fieldAL;
      Skill selectedSkill = me.myskill;
      long cooldownRemain = configuredSkill == null ? -1L
              : configuredSkill.coolDown - (now - configuredSkill.lastTimeUseThisSkill);
      if (cooldownRemain < 0L && configuredSkill != null) {
         cooldownRemain = 0L;
      }
      String skillState = this.describeSkillState(me, configuredSkill, selectedSkill, actualBoss, now);
      String bossPos = "none";
      if (actualBoss != null) {
         int dx = actualBoss.x - me.cx;
         int dy = actualBoss.y - me.cy;
         bossPos = "x=" + actualBoss.x + ",y=" + actualBoss.y
                 + ",dx=" + dx + ",dy=" + dy
                 + ",hp=" + actualBoss.hp + "/" + actualBoss.maxHp
                 + ",mobId=" + actualBoss.mobId;
      }
      System.out.println("AUTO TA THU FIGHT: map=" + TileMap.mapID + " targetMap=" + super.fieldAB
              + " zone=" + TileMap.zoneID + " targetZone=" + super.fieldAC
              + " locked=" + this.missionZoneLocked + " killId=" + this.fieldAV
              + " charPos=(" + me.cx + "," + me.cy + ")"
              + " charHp=" + me.cHP + "/" + me.cMaxHP
              + " charMp=" + me.cMP + "/" + me.cMaxMP
              + " focus=" + (focus == null ? "none" : focus.templateId + ":" + focus.hp + "/" + focus.maxHp
              + ",mobId=" + focus.mobId + ",levelBoss=" + focus.levelBoss)
              + " boss=" + bossPos
              + " auto=" + (Code.fieldAB == null ? "none" : Code.fieldAB.getClass().getName())
              + " configuredSkill=" + this.skillDescription(configuredSkill)
              + " selectedSkill=" + this.skillDescription(selectedSkill)
              + " cooldownRemain=" + cooldownRemain + "ms"
              + " lastSentSkill=" + this.lastSentSkillTemplateId + ":" + this.lastSentSkillId
              + " attacksSent=" + this.missionAttackCount
              + " lastAttackAgo=" + (this.lastMissionAttackAt == 0L ? -1L : now - this.lastMissionAttackAt) + "ms"
              + " serverAcks=" + this.serverAttackAckCount
              + " lastServerAckAgo=" + (this.lastServerAttackAckAt == 0L ? -1L : now - this.lastServerAttackAckAt) + "ms"
              + " serverSkill=" + this.lastServerSkillTemplateId
              + " serverTargetMobId=" + this.lastServerTargetMobId
              + " serverTargetOk=" + this.lastServerAckIncludedMissionTarget
              + " skillState=" + skillState
              + " task=" + (this.fieldAY == null ? "none" : this.fieldAY.count + "/" + this.fieldAY.maxCount));
   }

   /** Được Auto gọi đúng tại thời điểm gửi packet đánh mob. */
   public void onMissionAttackSent(Mob target, Skill skill) {
      if (!this.missionZoneLocked || target == null || skill == null) {
         return;
      }
      if (target.templateId == this.fieldAV && target.levelBoss == 3) {
         ++this.missionAttackCount;
         this.lastMissionAttackAt = System.currentTimeMillis();
         this.lastSentSkillTemplateId = skill.template == null ? -1 : skill.template.id;
         this.lastSentSkillId = skill.skillId;
      }
   }

   /** Packet 60 server echo: chính nhân vật vừa thực hiện một đòn đánh mob. */
   public void onServerMobAttackAck(int skillTemplateId, int targetMobId,
                                    boolean missionTargetIncluded) {
      ++this.serverAttackAckCount;
      this.lastServerAttackAckAt = System.currentTimeMillis();
      this.lastServerSkillTemplateId = skillTemplateId;
      this.lastServerTargetMobId = targetMobId;
      this.lastServerAckIncludedMissionTarget = missionTargetIncluded;
   }

   public boolean isMissionTargetMobId(int mobId) {
      Mob boss = this.findLiveMissionTarget();
      return boss != null && boss.mobId == mobId;
   }

   private void resetSkillTracking() {
      this.lastSentSkillTemplateId = -1;
      this.lastSentSkillId = -1;
      this.serverAttackAckCount = 0;
      this.lastServerAttackAckAt = 0L;
      this.lastServerSkillTemplateId = -1;
      this.lastServerTargetMobId = -1;
      this.lastServerAckIncludedMissionTarget = false;
   }

   private String skillDescription(Skill skill) {
      if (skill == null) {
         return "none";
      }
      String name = skill.template == null || skill.template.name == null
              ? "?" : skill.template.name;
      int templateId = skill.template == null ? -1 : skill.template.id;
      return templateId + ":" + skill.skillId + ":" + name
              + ":mana=" + skill.manaUse + ":cd=" + skill.coolDown;
   }

   private String describeSkillState(Char me, Skill configuredSkill, Skill selectedSkill,
                                     Mob boss, long now) {
      if (Code.fieldAB != this) {
         return "AUTO_REPLACED";
      }
      if (me.cHP <= 0) {
         return "DEAD";
      }
      if (configuredSkill == null || configuredSkill.template == null) {
         return "NO_CONFIGURED_SKILL";
      }
      if (selectedSkill == null || selectedSkill.template == null
              || selectedSkill.template.id != configuredSkill.template.id) {
         return "SKILL_MISMATCH";
      }
      if (me.cMP < configuredSkill.manaUse) {
         return "NO_MP";
      }
      if (boss != null && this.lastMissionAttackAt > 0L
              && now - this.lastMissionAttackAt > 3000L) {
         return "SEND_STALLED";
      }
      if (boss != null && this.lastMissionAttackAt > 0L
              && now - this.lastMissionAttackAt <= 3000L
              && (this.lastServerAttackAckAt == 0L || now - this.lastServerAttackAckAt > 3000L)) {
         return "SERVER_ACK_STALLED";
      }
      if (this.lastServerAttackAckAt > 0L && !this.lastServerAckIncludedMissionTarget) {
         return "SERVER_TARGET_MISMATCH";
      }
      if (this.lastServerAttackAckAt > 0L
              && this.lastServerSkillTemplateId != configuredSkill.template.id) {
         return "SERVER_SKILL_MISMATCH";
      }
      return "OK";
   }

   public final String toString() {
      return "Auto Tà Thú";
   }

   /**
    * Chỉ Controller của ta-thu-runtime gọi hook này khi packet attack của
    * một nhân vật khác có nhân vật chính trong danh sách mục tiêu.
    * Runtime khác không bị ảnh hưởng vì hook được chèn bằng patch lúc build.
    */
   public static void recordPlayerAttack(Char attacker, Char[] targets) {
      if (!(Code.fieldAB instanceof TaThu) || attacker == null || targets == null) {
         return;
      }
      Char me = Char.getMyChar();
      if (me == null || attacker.charID == me.charID) {
         return;
      }
      for (int index = 0; index < targets.length; ++index) {
         Char target = targets[index];
         if (target == me || target != null && target.charID == me.charID) {
            TaThu auto = (TaThu)Code.fieldAB;
            auto.lastPlayerAttackAt = System.currentTimeMillis();
            auto.lastPlayerAttackerId = attacker.charID;
            auto.lastPlayerAttackerName = attacker.cName;
            return;
         }
      }
   }

   private void clearPlayerAttackMarker() {
      this.lastPlayerAttackAt = 0L;
      this.lastPlayerAttackerId = -1;
      this.lastPlayerAttackerName = null;
   }

   private void checkPkDeath() {
      if (!this.missionZoneLocked) {
         this.pkDeathCount = 0;
         this.clearPlayerAttackMarker();
         return;
      }
      Char me = Char.getMyChar();
      if (me != null && me.cHP <= 0) {
         if (!this.wasDeadLastTick) {
            this.wasDeadLastTick = true;
            // Reset mobFocus ngay khi chết để tránh bám vào quái cũ sau hồi sinh
            me.mobFocus = null;
            this.lastMobHp = -1;
            long now = System.currentTimeMillis();
            boolean playerKill = this.lastPlayerAttackAt > 0L && now - this.lastPlayerAttackAt <= 5000L;
            if (!playerKill) {
               System.out.println("AUTO TA THU DEATH: chết do quái/môi trường tại map="
                       + super.fieldAB + " zone=" + super.fieldAC + "; giữ khu và tiếp tục nhiệm vụ");
               this.clearPlayerAttackMarker();
               return;
            }

            this.pkDeathCount++;
            System.out.println("AUTO TA THU PK: bị "
                    + (this.lastPlayerAttackerName == null ? "player" : this.lastPlayerAttackerName)
                    + " (charId=" + this.lastPlayerAttackerId + ") đánh chết lần " + this.pkDeathCount
                    + " tại map=" + super.fieldAB + " zone=" + super.fieldAC);
            this.clearPlayerAttackMarker();
            if (this.pkDeathCount >= 2) {
               int oldZone = super.fieldAC;
               this.missionZoneLocked = false;
               this.missionTargetSeen = false;
               this.missionTargetMissingSince = 0L;
               this.pkDeathCount = 0;
               TaThuDailyState state = TaThuDailyState.loadCurrent();
               if (state.mapId == super.fieldAB && state.killId == this.fieldAV && state.zoneId == oldZone) {
                  state.clearLockedZone();
               }
               System.out.println("AUTO TA THU NEXT: bị PK chết 2 lần tại map=" + super.fieldAB + " zone=" + oldZone + "; mở khóa chuyển khu né PK!");

               // FIX: Đổi zone ngay lập tức để tránh bị PK lại
               this.pkAvoidanceZoneOffset++;
               int nextZone = this.findNextAvailableZone(oldZone);
               super.fieldAC = nextZone;
               System.out.println("AUTO TA THU PK FIX: đổi ngay sang zone " + nextZone + " để né PK");

               // Delay ngắn để map load và tránh lock lại zone cũ ngay lập tức
               Auto.fieldAA(2000L);
            }
         }
      } else {
         this.wasDeadLastTick = false;
      }
   }

   private int findNextAvailableZone(int currentZone) {
      // Tìm zone tiếp theo theo pattern 0, 5, 10, 15, 20, ... 55
      // Tránh quay lại zone cũ bằng cách offset
      int baseZone = ((currentZone / 5) + 1 + this.pkAvoidanceZoneOffset) % 12;
      int nextZone = baseZone * 5;
      if (nextZone == currentZone) {
         nextZone = ((baseZone + 1) % 12) * 5;
      }
      return nextZone;
   }

   /**
    * Xóa mobFocus nếu nó không phải Tà Thú đúng (levelBoss=3, templateId khớp).
    * Gọi mỗi tick TRƯỚC khi chọn mob để đảm bảo bot không bám vào quái sai.
    * Bao gồm cả trường hợp vừa hồi sinh (cHP=0 → vừa live lại).
    */
   private void clearStaleMobFocus() {
      Char me = Char.getMyChar();
      if (me == null) return;
      Mob focus = me.mobFocus;
      if (focus == null) return;

      // Mob đã chết hoặc biến mất
      if (focus.hp <= 0 || focus.status == 0 || focus.status == 1) {
         me.mobFocus = null;
         this.lastMobHp = -1;
         return;
      }

      // Không phải Tà Thú đúng loại → xóa ngay, không chờ 30s
      // (chỉ log 1 lần, không spam mỗi tick)
      if (focus.levelBoss != 3 || focus.templateId != this.fieldAV) {
         if (this.lastMobHp != -999) {
            System.out.println("AUTO TA THU FIX: xóa mobFocus sai"
                + " templateId=" + focus.templateId + " levelBoss=" + focus.levelBoss
                + " (cần templateId=" + this.fieldAV + " levelBoss=3)");
            this.lastMobHp = -999; // sentinel: đã log rồi
         }
         me.mobFocus = null;
         this.lastMobHpChange = System.currentTimeMillis();
         return;
      }
      // Nếu vừa clear xong và giờ tìm được đúng boss → reset sentinel
      if (this.lastMobHp == -999) {
         this.lastMobHp = -1;
      }

      // Đang đánh đúng Tà Thú — kiểm tra HP có giảm không (stuck detection)
      int currentHp = focus.hp;
      long now = System.currentTimeMillis();
      if (currentHp != this.lastMobHp) {
         this.lastMobHp = currentHp;
         this.lastMobHpChange = now;
      } else if (now - this.lastMobHpChange > 30000L) {
         System.out.println("AUTO TA THU WARN: Tà Thú HP stuck " + currentHp + "/" + focus.maxHp + " sau 30s, tiếp tục đánh");
         this.lastMobHpChange = now;
      }
   }

   private void checkStuckMob() {
      // Logic stuck detection đã chuyển vào clearStaleMobFocus(), giữ lại để tương thích
   }
}
