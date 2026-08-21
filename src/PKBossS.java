public final class PKBossS extends Auto {
   public int fieldAV;
   public int fieldAW;
   private int fieldAX;
   private MyVector fieldAY;
   private MyVector fieldAZ;
   private long fieldBA;

   public PKBossS(MyVector var1, MyVector var2, int var3) {
      this.fieldAV = 0;
      this.fieldAW = -1;
      this.fieldAX = var3;
      this.fieldAY = var1;
      this.fieldAZ = var2;
   }

   public PKBossS(int var1) {
      this.fieldAV = 1;
      this.fieldAW = -1;
      this.fieldAX = var1;
      this.fieldBA = System.currentTimeMillis();
   }

   protected final void fieldAA() {
      if (Char.getMyChar().cHP <= 0) {
         Auto.fieldAA(true);
      } else {
         switch(this.fieldAV) {
         case 0:
            if (TileMap.mapID == 38 && TileMap.zoneID == 21) {
               LockGame.fieldBJ();
               this.fieldAV = 2;
               return;
            }

            this.fieldAA(38, 21, -1, -1);
            return;
         case 1:
            if (System.currentTimeMillis() - this.fieldBA > 180000L) {
               Code.fieldAC();
               return;
            }

            if (TileMap.mapID != 38 || TileMap.zoneID != 21) {
               this.fieldAA(38, 21, -1, -1);
               return;
            }
            break;
         case 2:
            if (this.fieldAY.size() > 0) {
               int var1 = this.fieldAY.size() - 1;
               super.fieldAB = ((Integer)this.fieldAY.elementAt(var1)).intValue();
               this.fieldAW = ((Integer)this.fieldAZ.elementAt(var1)).intValue();
               this.fieldAY.removeElementAt(var1);
               this.fieldAZ.removeElementAt(var1);
               this.fieldAV = 3;
               Service.gI().chatParty("pkms " + super.fieldAB + " " + this.fieldAW);
               return;
            }

            this.fieldAV = 4;
            Service.gI().chatParty("pkes");
            return;
         case 3:
            if (this.fieldAW > 0) {
               if (TileMap.mapID != this.fieldAW) {
                  this.fieldAA(this.fieldAW, -2, -1, -1);
                  return;
               }

               GameScr.fieldAB(5, 1, 0);
               if (LockGame.fieldAY()) {
                  this.fieldAV = 5;
                  Code.fieldAA((Auto)(new PkBoss(super.fieldAB)));
                  return;
               }
            }
            break;
         case 4:
            if (this.fieldAX > 0) {
               if (TileMap.mapID != this.fieldAX) {
                  this.fieldAA(this.fieldAX, -2, -1, -1);
                  return;
               }

               GameScr.fieldAB(5, 1, 0);
               if (LockGame.fieldAY()) {
                  Code.fieldAC();
                  return;
               }
            } else {
               this.fieldAX = 22;
            }
            break;
         case 5:
            cuong.sleep(10000L);
            if (Code.fieldAH != null && !Char.getMyChar().cName.equals(Code.fieldAH)) {
               this.fieldAV = 1;
               this.fieldBA = System.currentTimeMillis();
               return;
            } else {
               this.fieldAV = 2;
               return;
            }
         }

      }
   }

   public final String toString() {
      return "PKBoss S";
   }
}
