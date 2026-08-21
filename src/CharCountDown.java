public final class CharCountDown {
   long gameAA;
   public String gameAB;
   public boolean WantDestroy;
   public short gameAD;
   public short gameAE;
   private byte gameAF;

   public CharCountDown(short var1, short var2, long var3, String var5, byte var6) {
      this.gameAA = System.currentTimeMillis() + var3 * 1000L;
      this.gameAB = var5;
      this.gameAD = var1;
      this.gameAE = var2;
      this.gameAF = var6;
      if (this.gameAF == 0) {
         this.gameAA = var3;
      }

   }

   public final void gameAA(mGraphics var1, int var2, int var3) {
      if (this.gameAE == -1) {
         if (this.gameAF != 1) {
            mFont.tahoma_7.gameAA(var1, this.gameAB, var2 + 1, var3 + 1, 1);
            mFont.tahoma_7_white.gameAA(var1, this.gameAB, var2, var3, 1);
            return;
         }

         int var4;
         if ((var4 = (int)((this.gameAA - System.currentTimeMillis()) / 1000L)) > 0) {
            mFont.tahoma_7.gameAA(var1, this.gameAB + " : " + gameAA(var4), var2 - 5, var3 + 1, 1);
            mFont.tahoma_7_white.gameAA(var1, this.gameAB + " : " + gameAA(var4), var2 - 4, var3, 1);
            return;
         }
      } else {
         MyImage var7;
         if ((var7 = (MyImage)SmallImage.imgNew.get(String.valueOf(this.gameAD))) == null) {
            var7 = new MyImage();
            SmallImage.imgNew.put(String.valueOf(this.gameAD), var7);
            var7.img = Controller.gameAA(RMS.gameAA(String.valueOf(this.gameAD)));
            if (var7.img == null) {
               var7.timerequest = System.currentTimeMillis();
               Service.gI().requestIcon(this.gameAD);
            }
         } else if (var7.img == null && System.currentTimeMillis() - var7.timerequest > 60000L) {
            var7.timerequest = System.currentTimeMillis();
            Service.gI().requestIcon(this.gameAD);
         }

         if (var7 != null && var7.img != null) {
            int var5;
            if (this.gameAF == 0) {
               var5 = mFont.tahoma_7.gameAA(this.gameAB + " : ");
               var1.gameAA(var7.img, var2 - var5 - (var7.img.getWidth() << 1), var3 + mGraphics.gameAB(var7.img) / 4, 0);
               mFont.tahoma_7.gameAA(var1, this.gameAB, var2 - var5 - (var7.img.getWidth() << 1) + 1 + mGraphics.gameAA(var7.img), var3 + 1 + mGraphics.gameAB(var7.img) / 4, 0);
               mFont.tahoma_7_white.gameAA(var1, this.gameAB, var2 - var5 - (var7.img.getWidth() << 1) + mGraphics.gameAA(var7.img), var3 + mGraphics.gameAB(var7.img) / 4, 0);
               return;
            }

            if (this.gameAF == 1) {
               var5 = (int)((this.gameAA - System.currentTimeMillis()) / 1000L);
               int var6 = mFont.tahoma_7.gameAA(gameAA(var5) + ":");
               var1.gameAA(var7.img, var2 - var6 - (var7.img.getWidth() << 1), var3 + mGraphics.gameAB(var7.img) / 4, 0);
               mFont.tahoma_7.gameAA(var1, " : " + gameAA(var5), var2 - var6 - (var7.img.getWidth() << 1) + 1 + mGraphics.gameAA(var7.img), var3 + 1 + mGraphics.gameAB(var7.img) / 4, 0);
               mFont.tahoma_7_white.gameAA(var1, " : " + gameAA(var5), var2 - var6 - (var7.img.getWidth() << 1) + mGraphics.gameAA(var7.img), var3 + mGraphics.gameAB(var7.img) / 4, 0);
            }
         }
      }

   }

   public final void gameAA() {
      if (this.gameAF == 1 && System.currentTimeMillis() - this.gameAA >= 0L) {
         this.WantDestroy = true;
      }

   }

   private static String gameAA(int var0) {
      int var1 = var0 % 60;
      int var2 = (var0 /= 60) % 60;
      if ((var0 /= 60) > 0) {
         return var0 + ":" + var2;
      } else if (var2 > 0) {
         return var2 + ":" + var1;
      } else {
         return var1 < 0 ? "0:" + var1 : String.valueOf(var1);
      }
   }
}
