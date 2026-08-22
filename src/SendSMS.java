public final class SendSMS {
   public static void gameAA(String var0, String var1, Command var2, Command var3) {
      (new Thread(new SMS(var1, var0, var1, var2, var3))).start();
   }

   public static synchronized void gameAA() {
      Thread var0 = Session_ME.instance.fieldAS;
      if (var0 == null) {
         return;
      }

      try {
         var0.start();
      } catch (IllegalThreadStateException var1) {
         System.out.println("AUTO LOGIN TRACE: connection thread đã chạy, bỏ qua start trùng");
      }
   }
}
