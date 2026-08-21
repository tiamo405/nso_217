public final class GoMap implements Runnable {
   private final int fieldAA;

   GoMap(int var1) {
      this.fieldAA = var1;
   }

   public final void run() {
      try {
         TileMap.fieldAK(this.fieldAA);
      } catch (Exception var1) {
         var1.printStackTrace();
      }

      System.gc();
      if (Session_ME.gI().connected) {
         GameScr.gI().update();
      }

      GameCanvas.gameAJ();
      GameCanvas.isLoading = false;
   }
}
