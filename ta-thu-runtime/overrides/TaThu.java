public final class TaThu extends Auto {
   private TaskOrder fieldAY;
   public int fieldAV;
   public static boolean fieldAW;
   public static long fieldAX;
   private boolean missionZoneLocked;
   private long lastMissionStatusAt;
   private boolean missionTargetSeen;
   private long missionTargetMissingSince;
   private int zoneDeathCount;
   private boolean wasDeadLastTick;
   private long lastMobHpChange;
   private int lastMobHp;
   private int pkAvoidanceZoneOffset;

   public final void fieldAD() {
      super.fieldAD();
      this.fieldAY = Char.fieldAM(1);
      this.zoneDeathCount = 0;
      this.wasDeadLastTick = false;
      this.lastMobHpChange = System.currentTimeMillis();
      this.lastMobHp = -1;
      this.pkAvoidanceZoneOffset = 0;
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
      this.missionZoneLocked = false;
      this.missionTargetSeen = false;
      this.missionTargetMissingSince = 0L;
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
      this.missionZoneLocked = false;
      this.missionTargetSeen = false;
      this.missionTargetMissingSince = 0L;
   }

   public final void fieldAE() {
      this.fieldAY = Char.fieldAM(1);
      super.fieldAE();
   }

   public final void fieldAA() {
      this.checkPkDeath();
      this.updateMissionZoneLock();
      this.logMissionStatus();
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
               this.rememberMissionTargetIfPresent();
               this.checkStuckMob();
               this.fieldAB(this.fieldAV, 8);
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

   private void rememberMissionTargetIfPresent() {
      if (this.missionZoneLocked || !TaThuAccountManager.isEnabledRuntime()) {
         return;
      }
      for (int index = 0; index < GameScr.vMob.size(); ++index) {
         Mob mob = (Mob)GameScr.vMob.elementAt(index);
         if (mob != null && mob.hp > 0 && mob.status != 0 && mob.status != 1
                 && mob.templateId == this.fieldAV && mob.levelBoss == 3) {
            System.out.println("AUTO TA THU LOCK: tìm thấy Tà Thú templateId=" + mob.templateId
                + " levelBoss=" + mob.levelBoss + " hp=" + mob.hp + "/" + mob.maxHp
                + " tại zone=" + super.fieldAC);
            this.missionZoneLocked = true;
            AutoTaThuDaily.rememberTarget(super.fieldAB, super.fieldAC, this.fieldAV, mob.x, mob.y);
            return;
         }
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
         this.missionZoneLocked = false;
         this.missionTargetSeen = false;
         this.missionTargetMissingSince = 0L;
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
         this.missionZoneLocked = false;
         this.missionTargetSeen = false;
         this.missionTargetMissingSince = 0L;
         TaThuDailyState state = TaThuDailyState.loadCurrent();
         if (state.mapId == super.fieldAB && state.killId == this.fieldAV && state.zoneId == oldZone) {
            state.clearLockedZone();
         }
         System.out.println("AUTO TA THU NEXT: Tà Thú không còn ở map=" + super.fieldAB
                 + " zone=" + oldZone + " sau " + grace/1000 + "s, task chưa xong; mở khóa để tìm khu tiếp theo");
      }
   }

   public boolean isMissionZoneLocked() {
      return this.missionZoneLocked;
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
      System.out.println("AUTO TA THU FIGHT: map=" + TileMap.mapID + " targetMap=" + super.fieldAB
              + " zone=" + TileMap.zoneID + " targetZone=" + super.fieldAC
              + " locked=" + this.missionZoneLocked + " killId=" + this.fieldAV
              + " charHp=" + me.cHP + "/" + me.cMaxHP
              + " mob=" + (focus == null ? "none" : focus.templateId + ":" + focus.hp + "/" + focus.maxHp
              + ",mobId=" + focus.mobId + ",levelBoss=" + focus.levelBoss + ",isBoss=" + focus.isBoss)
              + " task=" + (this.fieldAY == null ? "none" : this.fieldAY.count + "/" + this.fieldAY.maxCount));
   }

   public final String toString() {
      return "Auto Tà Thú";
   }

   private void checkPkDeath() {
      if (!this.missionZoneLocked) {
         this.zoneDeathCount = 0;
         return;
      }
      Char me = Char.getMyChar();
      if (me != null && me.cHP <= 0) {
         if (!this.wasDeadLastTick) {
            this.wasDeadLastTick = true;
            this.zoneDeathCount++;
            System.out.println("AUTO TA THU PK: bị chết lần " + this.zoneDeathCount + " tại map=" + super.fieldAB + " zone=" + super.fieldAC);
            if (this.zoneDeathCount >= 2) {
               int oldZone = super.fieldAC;
               this.missionZoneLocked = false;
               this.missionTargetSeen = false;
               this.missionTargetMissingSince = 0L;
               this.zoneDeathCount = 0;
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

   private void checkStuckMob() {
      Char me = Char.getMyChar();
      if (me == null || me.mobFocus == null) {
         this.lastMobHp = -1;
         this.lastMobHpChange = System.currentTimeMillis();
         return;
      }

      int currentHp = me.mobFocus.hp;
      long now = System.currentTimeMillis();

      // Kiểm tra nếu HP thay đổi
      if (currentHp != this.lastMobHp) {
         this.lastMobHp = currentHp;
         this.lastMobHpChange = now;
         return;
      }

      // Nếu mob HP không đổi quá 30 giây và không phải đang đánh đúng Tà Thú
      if (now - this.lastMobHpChange > 30000L) {
         Mob mob = me.mobFocus;
         if (mob.levelBoss != 3 || mob.templateId != this.fieldAV) {
            System.out.println("AUTO TA THU WARN: mob HP stuck " + currentHp + "/" + mob.maxHp
                + " levelBoss=" + mob.levelBoss + " sau 30s, clear target");
            me.mobFocus = null;
            this.lastMobHp = -1;
            this.lastMobHpChange = now;
         } else {
            // Là đúng Tà Thú nhưng HP không giảm → có thể bị bug, reset timer
            System.out.println("AUTO TA THU WARN: Tà Thú HP stuck, tiếp tục đánh");
            this.lastMobHpChange = now;
         }
      }
   }
}
