public final class Info {
   private static MyVector infoWaitToShow = new MyVector();
   private static InfoItem info;
   private static int p1 = 5;
   private static int p2;
   private static int x;
   private static int strWidth;
   private static int limLeft = 2;
   public static int hI = 15;

   public static void gameAA(mGraphics var0) {
      int var1 = GameCanvas.isKiemduyet ? 16 : 0;
      int var2 = GameCanvas.w;
      if (info != null) {
         var0.gameAD(0, 0, GameCanvas.w, GameCanvas.h);
         if (!GameCanvas.isTouch) {
            Paint.gameAA(-6, var1 - 4, var2 + 10, hI + 8, var0);
         } else {
            var0.gameAA(0);
            var0.gameAC(0, var1, var2, hI);
         }

         var0.gameAD(0, var1, var2, hI + 5);
         info.f.gameAA(var0, info.s, x, var1 + 5, 0);
      }
   }

   public static void gameAA() {
      if (GameCanvas.isTouch) {
         hI = 20;
      }

      if (p1 == 0) {
         if ((x += (limLeft - x) / 3) - limLeft < 3) {
            x = limLeft + 2;
            p1 = 2;
            p2 = 0;
            return;
         }
      } else if (p1 == 2) {
         if (++p2 > info.speed) {
            p1 = 3;
            p2 = 0;
            return;
         }
      } else if (p1 == 3) {
         if (x + strWidth < limLeft + GameCanvas.w - 160) {
            x -= 6;
         } else {
            x -= 2;
         }

         if (x + strWidth < limLeft) {
            p1 = 4;
            p2 = 0;
            return;
         }
      } else if (p1 == 4) {
         if (++p2 > 10) {
            p1 = 5;
            p2 = 0;
            return;
         }
      } else if (p1 == 5) {
         if (infoWaitToShow.size() > 0) {
            InfoItem var0 = (InfoItem)infoWaitToShow.firstElement();
            infoWaitToShow.removeElementAt(0);
            if (info != null && var0.s.equals(info.s)) {
               return;
            }

            info = var0;
            strWidth = var0.f.gameAA(info.s);
            p2 = 0;
            p1 = 0;
            x = GameCanvas.w;
            return;
         }

         info = null;
         if (GameCanvas.isTouch) {
            hI = 0;
         }
      }

   }

   public static void gameAA(String var0, int var1, mFont var2) {
      String var3 = var0;
      boolean var10000;
      if (info != null && info.s != null && var0.equals(info.s)) {
         var10000 = true;
      } else if (infoWaitToShow.size() > 0 && var0.equals(((InfoItem)infoWaitToShow.lastElement()).s)) {
         var10000 = true;
      } else {
         label96: {
            if (var0.length() >= 8) {
               String var4;
               String var5;
               if (info != null && info.s != null && p1 < 3 && info.s.length() >= 8) {
                  var4 = var0.substring(0, 8);
                  var5 = info.s.substring(0, 8);
                  if (var4.equals(var5)) {
                     int var8;
                     for(var8 = 7; var8 < var3.length() && var8 < info.s.length() && var3.charAt(var8) == info.s.charAt(var8); ++var8) {
                     }

                     var5 = var3.substring(var8, var3.length());
                     InfoItem var9 = info;
                     var9.s = var9.s + ", " + var5;
                     p1 = 2;
                     p2 = 0;
                     var10000 = true;
                     break label96;
                  }
               }

               if (infoWaitToShow.size() > 0 && (var4 = ((InfoItem)infoWaitToShow.lastElement()).s).length() >= 8) {
                  var5 = var0.substring(0, 8);
                  String var6 = var4.substring(0, 8);
                  if (var5.equals(var6)) {
                     int var7;
                     for(var7 = 7; var7 < var3.length() && var7 < var4.length() && var3.charAt(var7) == var4.charAt(var7); ++var7) {
                     }

                     var3 = var3.substring(var7, var3.length());
                     (new StringBuffer(String.valueOf(var4))).append(", ").append(var3).toString();
                     var10000 = true;
                     break label96;
                  }
               }
            }

            var10000 = false;
         }
      }

      if (!var10000) {
         if (GameCanvas.w == 128) {
            limLeft = 1;
         }

         if (infoWaitToShow.size() > 10) {
            infoWaitToShow.removeElementAt(0);
         }

         infoWaitToShow.addElement(new InfoItem(var0, var2, var1));
      }
   }
}
