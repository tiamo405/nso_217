public final class MobAttack {
   private int x;
   private int y;
   private int vx;
   private int vy;
   private int indexM;
   private int xTo;
   private int yTo;
   private int d;
   private int statusM;
   private static int[] maxIndex;

   static {
      int[] var10000 = new int[]{10, 5, 11};
      var10000 = new int[]{10, 5, 11};
      maxIndex = new int[]{4, 1, 4};
   }

   public final void gameAA() {
      if (this.statusM > 0 && this.statusM < 4) {
         this.x += this.vx;
         this.y += this.vy;
         this.vy += 2;
         if (this.vx < -2) {
            ++this.vx;
         }

         if (this.vx > 2) {
            --this.vx;
         }

         if (this.vy == 6) {
            if (this.statusM == 2) {
               this.statusM = 0;
            } else {
               this.statusM = 4;
            }

            this.d = 11;
            this.vx = Res.gameAB(Res.gameAA(this.xTo - this.x, this.yTo - this.y), this.d);
            this.vy = Res.gameAC(Res.gameAA(this.xTo - this.x, this.yTo - this.y), this.d);
         }

      } else {
         Char var1 = null;
         var1 = (Char)GameScr.vCharInMap.elementAt(0);
         this.xTo = var1.cx;
         this.yTo = var1.cy - var1.chh;
         this.x += this.vx;
         this.y += this.vy;
         ++this.d;
         this.vx = Res.gameAB(Res.gameAA(this.xTo - this.x, this.yTo - this.y), this.d);
         this.vy = Res.gameAC(Res.gameAA(this.xTo - this.x, this.yTo - this.y), this.d);
         ++this.indexM;
         if (this.indexM == maxIndex[0]) {
            this.indexM = 0;
         }

         if (this.x < this.xTo + var1.chw && this.x > this.xTo - var1.chw && this.y < this.yTo + var1.chh && this.y > this.yTo - var1.chh) {
            GameScr.vMobAttack.removeElement(this);
         }
      }
   }
}
