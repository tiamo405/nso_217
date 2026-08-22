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
               while(true) {
                  MyVector var1 = Session_ME.gameAB(this.gameAA);
                  Message var2;
                  synchronized(var1) {
                     if (var1.size() == 0) {
                        break;
                     }

                     var2 = (Message)var1.elementAt(0);
                     var1.removeElementAt(0);
                  }

                  GameScr.gI();
                  Session_ME.gameAA(this.gameAA, var2);
               }
            }

            try {
               Thread.sleep(10L);
            } catch (InterruptedException var3) {
            }
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }

   }
}
