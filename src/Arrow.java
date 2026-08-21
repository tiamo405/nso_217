public final class Arrow {
   public int life = 0;
   public int ax;
   public int ay;
   private int axTo;
   private int ayTo;
   private int avx;
   private int avy;
   private int adx;
   private int ady;
   private Char charBelong;
   private Arrowpaint arrp = null;
   private static byte[] FRAME = new byte[]{0, 1, 2, 1, 0, 1, 2, 1, 0, 1, 2, 1, 0, 1, 2, 1, 0, 1, 2, 1, 0, 1, 2, 1, 0};
   private static int[] ARROWINDEX = new int[]{0, 15, 37, 52, 75, 105, 127, 142, 165, 195, 217, 232, 255, 285, 307, 322, 345, 370};
   private static int[] TRANSFORM = new int[]{0, 0, 0, 7, 6, 6, 6, 2, 2, 3, 3, 4, 5, 5, 5, 1};

   public Arrow(Char var1, Arrowpaint var2) {
      this.charBelong = var1;
      this.arrp = var2;
   }

   public final void gameAA() {
      if (this.charBelong.mobFocus != null || this.charBelong.charFocus != null) {
         if (this.charBelong.mobFocus != null) {
            this.axTo = this.charBelong.mobFocus.x;
            this.ayTo = this.charBelong.mobFocus.y - this.charBelong.mobFocus.h / 4;
         } else if (this.charBelong.charFocus != null) {
            this.axTo = this.charBelong.charFocus.cx;
            this.ayTo = this.charBelong.charFocus.cy - this.charBelong.charFocus.ch / 4;
         }

         int var1 = this.axTo - this.ax;
         int var2 = this.ayTo - this.ay;
         byte var3 = 4;
         if (var1 + var2 < 60) {
            var3 = 3;
         } else if (var1 + var2 < 30) {
            var3 = 2;
         }

         if (this.ax != this.axTo) {
            if (var1 > 0 && var1 < 5) {
               this.ax = this.axTo;
            } else if (var1 < 0 && var1 > -5) {
               this.ax = this.axTo;
            } else {
               this.avx = this.axTo - this.ax << 2;
               this.adx += this.avx;
               this.ax += this.adx >> var3;
               this.adx &= 15;
            }
         }

         if (this.ay != this.ayTo) {
            if (var2 > 0 && var2 < 5) {
               this.ay = this.ayTo;
            } else if (var2 < 0 && var2 > -5) {
               this.ay = this.ayTo;
            } else {
               this.avy = this.ayTo - this.ay << 2;
               this.ady += this.avy;
               this.ay += this.ady >> var3;
               this.ady &= 15;
            }
         }

         var1 = 0;
         var2 = 0;
         int var5 = 0;
         int var4 = 0;
         if (this.charBelong.mobFocus != null) {
            var1 = this.axTo - this.charBelong.mobFocus.w / 4;
            var5 = this.axTo + this.charBelong.mobFocus.w / 4;
            var2 = this.ayTo - this.charBelong.mobFocus.h / 4;
            var4 = this.ayTo + this.charBelong.mobFocus.h / 4;
         } else if (this.charBelong.charFocus != null) {
            var1 = this.axTo - this.charBelong.charFocus.cw / 4;
            var5 = this.axTo + this.charBelong.charFocus.cw / 4;
            var2 = this.ayTo - this.charBelong.charFocus.ch / 4;
            var4 = this.ayTo + this.charBelong.charFocus.ch / 4;
         }

         if (this.life > 0) {
            --this.life;
         }

         if (this.life != 0 && (this.ax < var1 || this.ax > var5 || this.ay < var2 || this.ay > var4)) {
            return;
         }
      }

      this.gameAB();
   }

   private void gameAB() {
      this.charBelong.arr = null;
      this.ax = this.ay = this.axTo = this.ayTo = this.avx = this.avy = this.adx = this.ady = 0;
      this.charBelong.gameAN();
      if (this.charBelong.me) {
         this.charBelong.gameAI();
      }

   }

   public final void gameAA(mGraphics var1) {
      int var2 = this.axTo - this.ax;
      int var3 = this.ayTo - this.ay;
      var2 = Res.gameAA(var2, -var3);
      var3 = 0;

      int var10000;
      while(true) {
         if (var3 >= ARROWINDEX.length - 1) {
            var10000 = 0;
            break;
         }

         if (var2 >= ARROWINDEX[var3] && var2 <= ARROWINDEX[var3 + 1]) {
            var10000 = var3 >= 16 ? 0 : var3;
            break;
         }

         ++var3;
      }

      var2 = var10000;
      SmallImage.gameAA(var1, this.arrp.imgId[FRAME[var2]], this.ax, this.ay, TRANSFORM[var2], 3);
   }
}
