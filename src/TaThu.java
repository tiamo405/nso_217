public final class TaThu extends Auto {
   private TaskOrder fieldAY;
   public int fieldAV;
   public static boolean fieldAW;
   public static long fieldAX;

   public final void fieldAD() {
      super.fieldAD();
      this.fieldAY = Char.fieldAM(1);
      if (this.fieldAY != null) {
         this.fieldAV = this.fieldAY.killId;
         super.fieldAB = this.fieldAY.mapId;
         if (TileMap.mapID == this.fieldAY.mapId && TileMap.zoneID % 5 == 0) {
            super.fieldAC = TileMap.zoneID;
            return;
         }
      }

      super.fieldAC = 5;
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
   }

   public final void fieldAE() {
      this.fieldAY = Char.fieldAM(1);
      super.fieldAE();
   }

   public final void fieldAA() {
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

   public final String toString() {
      return "Auto Tà Thú";
   }
}
