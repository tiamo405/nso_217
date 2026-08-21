import java.io.DataInputStream;
import java.util.Random;

public final class NinjaUtil {
   private static Random randomz = new Random();

   public static int gameAA(int var0) {
      return randomz.nextInt(var0);
   }

   public static int gameAA(int var0, int var1) {
      return -7 + randomz.nextInt(14);
   }

   public static byte[] gameAA(Message var0) {
      try {
         byte[] var1 = new byte[var0.reader().readInt()];
         var0.reader().read(var1);
         return var1;
      } catch (Exception var2) {
         return null;
      }
   }

   public static byte[] gameAB(Message var0) {
      try {
         byte[] var1 = new byte[var0.reader().readInt()];
         var0.reader().read(var1);
         return var1;
      } catch (Exception var2) {
         return null;
      }
   }

   public static byte[] gameAA(DataInputStream var0) {
      try {
         byte[] var1 = new byte[var0.readInt()];
         var0.read(var1);
         return var1;
      } catch (Exception var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static String replace(String var0, String var1, String var2) {
      StringBuffer var3 = new StringBuffer();

      int var5;
      for(boolean var4 = false; (var5 = var0.indexOf(var1)) != -1; var0 = var0.substring(var5 + var1.length())) {
         var3.append(var0.substring(0, var5) + var2);
      }

      var3.append(var0);
      return var3.toString();
   }

   public static String gameAA(String var0) {
      String var1 = "";
      String var2 = "";
      if (var0.equals("")) {
         return var1;
      } else {
         if (var0.charAt(0) == '-') {
            var2 = "-";
            var0 = var0.substring(1);
         }

         for(int var3 = var0.length() - 1; var3 >= 0; --var3) {
            if ((var0.length() - 1 - var3) % 3 == 0 && var0.length() - 1 - var3 > 0) {
               var1 = var0.charAt(var3) + "." + var1;
            } else {
               var1 = var0.charAt(var3) + var1;
            }
         }

         return var2 + var1;
      }
   }

   public static void gameAA(String var0, short var1) {
      SendSMS.gameAA(var0, "sms://" + var1, new Command("", GameCanvas.gameAA(), 88827, (Object)null), new Command("", GameCanvas.gameAA(), 88828, (Object)null));
   }

   public static void gameAB(String var0) {
      try {
         GameMidlet.instance.platformRequest(var0);
      } catch (Exception var3) {
         var3.printStackTrace();
      } finally {
         GameMidlet.instance.notifyDestroyed();
      }

   }

   public static String gameAB(int var0) {
      int var1 = 0;
      if (var0 > 60) {
         var1 = var0 / 60;
         var0 %= 60;
      }

      int var2 = 0;
      if (var1 > 60) {
         var2 = var1 / 60;
         var1 %= 60;
      }

      int var3 = 0;
      if (var2 > 24) {
         var3 = var2 / 24;
         var2 %= 24;
      }

      String var4 = "";
      if (var3 > 0) {
         var4 = var4 + var3;
         var4 = var4 + "d";
         var4 = var4 + var2 + "h";
      } else if (var2 > 0) {
         var4 = var4 + var2;
         var4 = var4 + "h";
         var4 = var4 + var1 + "'";
      } else {
         if (var1 > 9) {
            var4 = var4 + var1;
         } else {
            var4 = var4 + "0" + var1;
         }

         var4 = var4 + ":";
         if (var0 > 9) {
            var4 = var4 + var0;
         } else {
            var4 = var4 + "0" + var0;
         }
      }

      return var4;
   }

   public static String[] gameAA(String var0, String var1) {
      MyVector var2 = new MyVector();

      for(int var3 = var0.indexOf(var1); var3 >= 0; var3 = (var0 = var0.substring(var3 + var1.length())).indexOf(var1)) {
         var2.addElement(var0.substring(0, var3));
      }

      var2.addElement(var0);
      String[] var4 = new String[var2.size()];
      if (var2.size() > 0) {
         for(int var5 = 0; var5 < var2.size(); ++var5) {
            var4[var5] = (String)var2.elementAt(var5);
         }
      }

      return var4;
   }
}
