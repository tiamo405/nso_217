import java.io.IOException;

final class Sender implements Runnable {
   final Session_ME gameAA;

   public Sender(Session_ME var1) {
      this.gameAA = var1;
      Session_ME.gameAA(var1, new MyVector());
   }

   public final void run() {
      while(this.gameAA.connected) {
         try {
            if (this.gameAA.getKeyComplete) {
               while(Session_ME.gameAB(this.gameAA).size() > 0) {
                  GameScr.gI();
                  Message var1 = (Message)Session_ME.gameAB(this.gameAA).elementAt(0);
                  Session_ME.gameAB(this.gameAA).removeElementAt(0);
                  Session_ME.gameAA(this.gameAA, var1);
               }
            }

            try {
               Thread.sleep(10L);
            } catch (InterruptedException var2) {
            }
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }

   }
}
