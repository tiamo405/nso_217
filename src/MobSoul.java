public final class MobSoul {
   private int x;
   private int y;
   private int xdest;
   private int ydest;
   private int status;
   private int timeFollow;
   private int type;
   private Char c;
   private int[] x0;
   private int[] y0;
   private int[] dir;

   public MobSoul(int var1, int var2, Char var3) {
      this.x = var1;
      this.y = var2;
      this.xdest = var1;
      this.ydest = var2 - (Res.gameAD(40) + 20);
      this.status = 1;
      this.type = 1;
      this.c = var3;
   }

   public MobSoul(int var1, int var2) {
      this.status = 1;
      this.type = 2;
      this.xdest = var1;
      this.ydest = var2;
      this.x0 = new int[5];
      this.y0 = new int[5];
      this.dir = new int[5];
      this.timeFollow = 300;

      for(int var3 = 0; var3 < this.x0.length; ++var3) {
         this.x0[var3] = Res.gameAD(var1 - 20, var1 + 20);
         this.y0[var3] = var2 - 10;
         this.dir[var3] = var3 % 2 == 0 ? 1 : -1;
      }

   }

   public final void gameAA() {
      if (this.type == 1) {
         if (this.c == null) {
            GameScr.vMobSoul.removeElement(this);
            return;
         }

         if (this.status == 1) {
            if (this.y > this.ydest) {
               this.y -= 2;
               this.x += 1 - GameCanvas.gameTick % 3;
            } else {
               this.status = 2;
            }

            this.timeFollow = 100;
            return;
         }

         --this.timeFollow;
         if (Res.abs(this.c.cx - this.x) >= 50 && Res.abs(this.c.cy - this.y) >= 50) {
            this.x += (this.c.cx - this.x) / 10;
            this.y += (this.c.cy - this.y) / 10;
         } else {
            this.x += (this.c.cx - this.x) / 4;
            this.y += (this.c.cy - this.y) / 4;
         }

         if (this.timeFollow < 0) {
            this.x = this.c.cx;
            this.y = this.c.cy - this.c.ch / 2;
            if (this.timeFollow < -5) {
               GameScr.vMobSoul.removeElement(this);
               return;
            }
         } else if (Res.abs(this.c.cx - this.x) < 10 && Res.abs(this.c.cy - this.y) < 10) {
            GameScr.vMobSoul.removeElement(this);
            return;
         }
      } else if (this.type == 2) {
         for(int var1 = 0; var1 < this.x0.length; ++var1) {
            int[] var10000 = this.y0;
            var10000[var1] -= GameCanvas.gameTick % 5;
            int var10002;
            if (this.dir[var1] == -1) {
               var10002 = this.x0[var1]--;
            } else {
               var10002 = this.x0[var1]++;
            }

            if (this.x0[var1] <= this.xdest - 20 || this.x0[var1] >= this.xdest + 20) {
               this.dir[var1] = -this.dir[var1];
            }

            if (this.y0[var1] < 0) {
               GameScr.vMobSoul.removeElement(this);
            }
         }
      }

   }

   public final void gameAA(mGraphics var1) {
      if (this.type == 1) {
         if (GameCanvas.gameTick % 5 > 2) {
            SmallImage.gameAA(var1, 1433, this.x, this.y, 0, 3);
         } else {
            SmallImage.gameAA(var1, 1434, this.x, this.y, 0, 3);
         }
      } else {
         for(int var2 = 0; var2 < this.x0.length; ++var2) {
            if (GameCanvas.gameTick % 5 > 2) {
               SmallImage.gameAA(var1, 1433, this.x0[var2], this.y0[var2], 0, 3);
            } else {
               SmallImage.gameAA(var1, 1434, this.x0[var2], this.y0[var2], 0, 3);
            }
         }

      }
   }
}
