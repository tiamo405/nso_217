public final class NextMap implements Runnable {
   private final int fieldAA;

   NextMap(int var1) {
      this.fieldAA = var1;
   }

   public final void run() {
      try {
         int var1;
         if ((var1 = this.fieldAA) < 0) {
            var1 = 0;
         }

         if (var1 >= TileMap.vGo.size()) {
            var1 = TileMap.vGo.size() - 1;
         }

         TileMap.fieldAJ(var1);
      } catch (Exception var2) {
         var2.printStackTrace();
      }

      System.gc();
      if (Session_ME.gI().connected) {
         GameScr.gI().update();
      }

      GameCanvas.gameAJ();
      GameCanvas.isLoading = false;
   }
}
