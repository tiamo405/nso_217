public final class ServerEffect extends Effect2 {
   private EffectCharPaint eff;
   private int i0;
   private int x;
   private int y;
   private int dir = 1;
   private Char c;
   private short loopCount = 0;
   private long endTime = 0L;

   public static void gameAA(int var0, int var1, int var2, int var3) {
      ServerEffect var4;
      (var4 = new ServerEffect()).eff = GameScr.efs[var0];
      var4.x = var1;
      var4.y = var2;
      var4.loopCount = (short)var3;
      Effect2.vEffect2.addElement(var4);
   }

   public static void gameAA(int var0, int var1, int var2, int var3, byte var4) {
      ServerEffect var5;
      (var5 = new ServerEffect()).eff = GameScr.efs[var0];
      var5.x = var1;
      var5.y = var2;
      var5.loopCount = (short)var3;
      var5.dir = var4;
      Effect2.vEffect2.addElement(var5);
   }

   public static void gameAA(int var0, Char var1, int var2) {
      ServerEffect var3;
      (var3 = new ServerEffect()).eff = GameScr.efs[var0];
      var3.c = var1;
      var3.loopCount = (short)var2;
      Effect2.vEffect2.addElement(var3);
   }

   public static void gameAB(int var0, Char var1, int var2) {
      ServerEffect var3;
      (var3 = new ServerEffect()).eff = GameScr.efs[var0];
      var3.c = var1;
      var3.endTime = System.currentTimeMillis() + (long)(var2 * 1000);
      Effect2.vEffect2.addElement(var3);
   }

   public final void paint(mGraphics var1) {
      if (this.c != null) {
         this.x = this.c.cx;
         this.y = this.c.cy;
      }

      int var2 = this.x + this.eff.arrEfInfo[this.i0].dx * this.dir;
      int var3 = this.y + this.eff.arrEfInfo[this.i0].dy;
      if (GameCanvas.gameAE(var2, var3)) {
         SmallImage.gameAA(var1, this.eff.arrEfInfo[this.i0].idImg, var2, var3, this.dir == 1 ? 0 : 2, 3);
      }

   }

   public final void update() {
      if (this.endTime != 0L) {
         ++this.i0;
         if (this.i0 >= this.eff.arrEfInfo.length) {
            this.i0 = 0;
         }

         if (System.currentTimeMillis() - this.endTime > 0L) {
            if (this.eff.idEf == 120) {
               GameCanvas.isBallEffect = false;
            }

            Effect2.vEffect2.removeElement(this);
         }
      } else {
         ++this.i0;
         if (this.i0 >= this.eff.arrEfInfo.length) {
            --this.loopCount;
            if (this.loopCount <= 0) {
               if (this.eff.idEf == 120) {
                  GameCanvas.isBallEffect = false;
               }

               Effect2.vEffect2.removeElement(this);
            } else {
               this.i0 = 0;
            }
         }
      }

      if (GameCanvas.gameTick % 11 == 0 && this.c != null && this.c != Char.getMyChar() && !GameScr.vCharInMap.contains(this.c)) {
         Effect2.vEffect2.removeElement(this);
      }

   }
}
