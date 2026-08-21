public final class SendSMS {
   public static void gameAA(String var0, String var1, Command var2, Command var3) {
      (new Thread(new SMS(var1, var0, var1, var2, var3))).start();
   }

   public static void gameAA() {
      Session_ME.instance.fieldAS.start();
   }
}
