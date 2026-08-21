import javax.microedition.io.Connector;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

final class SMS implements Runnable {
   private final String gameAA;
   private final String gameAB;
   private final String gameAC;
   private final Command gameAD;
   private final Command gameAE;

   SMS(String var1, String var2, String var3, Command var4, Command var5) {
      this.gameAA = var1;
      this.gameAB = var2;
      this.gameAC = var3;
      this.gameAD = var4;
      this.gameAE = var5;
   }

   public final void run() {
      try {
         MessageConnection var1 = null;
         TextMessage var2;
         (var2 = (TextMessage)(var1 = (MessageConnection)Connector.open(this.gameAA)).newMessage("text")).setAddress(this.gameAA);
         var2.setPayloadText(this.gameAB);
         var1.send(var2);
         System.out.println("SMS data: " + this.gameAB + ", to: " + this.gameAC);
         this.gameAD.gameAA();
      } catch (Exception var3) {
         this.gameAE.gameAA();
      }
   }
}
