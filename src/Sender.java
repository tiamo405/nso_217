import java.io.IOException;

final class Sender implements Runnable {
   final Session_ME gameAA;
   final OutboundQueue queue = new OutboundQueue();

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

   Runnable connection(final long token) {
      return new Runnable() {
         public void run() {
            try {
               OutboundQueue.Entry entry;
               while ((entry = queue.take(token)) != null) {
                  if (gameAA.sendQueued(entry.message, token)) queue.sent(entry);
               }
            } catch (InterruptedException ignored) {
               Thread.currentThread().interrupt();
            } catch (Exception ex) {
               ex.printStackTrace();
            }
         }
      };
   }
}
