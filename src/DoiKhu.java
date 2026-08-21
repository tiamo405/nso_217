public final class DoiKhu implements Runnable {
   private final int fieldAA;

   DoiKhu(int var1) {
      this.fieldAA = var1;
   }

   public final void run() {
      Npc var1 = GameScr.fieldAI(13);
      int var2 = -1;
      if (var1 != null && var1.statusMe != 15) {
         if (Math.abs(var1.cx - Char.getMyChar().cx) > 22 || Math.abs(var1.cy - Char.getMyChar().cy) > 22) {
            Char.fieldAC(var1.cx, var1.cy);
         }
      } else {
         if (TileMap.mapID != 99 && TileMap.mapID != 103 && TileMap.mapID != 134 && TileMap.mapID != 135 && TileMap.mapID != 136 && TileMap.mapID != 137) {
            return;
         }

         if ((var2 = Char.fieldAI(37)) < 0 && (var2 = Char.fieldAI(35)) < 0) {
            return;
         }
      }

      Service.gI().requestChangeZone(this.fieldAA, var2);
      TileMap.fieldAF();

      try {
         Thread.sleep(100L);
      } catch (InterruptedException var3) {
         var3.printStackTrace();
      }
   }
}
