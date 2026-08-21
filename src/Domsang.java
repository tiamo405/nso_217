public final class Domsang {
   private int xS;
   private int yS;
   public int frame = 0;
   private int typeEff;
   private int dem = 0;

   public Domsang(int var1, int var2, int var3) {
      this.xS = var1;
      this.yS = var2;
      this.typeEff = var3;
   }

   public final void gameAA() {
      if (this.typeEff == 1) {
         ++this.dem;
         if (this.dem % 2 == 0) {
            ++this.frame;
            return;
         }
      } else {
         if (this.typeEff == 0) {
            ++this.frame;
            return;
         }

         if (this.typeEff == 2 || this.typeEff == 3 || this.typeEff == 4 || this.typeEff == 5) {
            ++this.dem;
            if (this.dem % 2 == 0) {
               ++this.frame;
            }
         }
      }

   }

   public final void gameAA(mGraphics var1) {
      if (this.typeEff == 0) {
         var1.gameAA(GameScr.imgMatcho, 0, this.frame * 3, 3, 3, 0, this.xS, this.yS, 0);
      } else if (this.typeEff == 1) {
         var1.gameAA(GameScr.imgFiremoto, 0, this.frame * 20, 20, 20, 0, this.xS, this.yS, 33);
      } else if (this.typeEff == 2) {
         var1.gameAA(GameScr.imgsmokeFollow, 0, this.frame * 15, 14, 15, 0, this.xS + 20, this.yS + 4, 20);
      } else if (this.typeEff == 3) {
         var1.gameAA(GameScr.imgEffMob1, 0, this.frame << 3, 8, 8, 0, this.xS + 20, this.yS + 4, 20);
      } else if (this.typeEff == 4) {
         var1.gameAA(GameScr.imgEffMob2, 0, this.frame << 3, 8, 8, 0, this.xS + 20, this.yS + 4, 20);
      } else {
         if (this.typeEff == 5) {
            var1.gameAA(GameScr.imgEffMob3, 0, this.frame * 14, 14, 14, 0, this.xS + 20, this.yS + 4, 20);
         }

      }
   }
}
