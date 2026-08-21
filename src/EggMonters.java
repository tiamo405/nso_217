public final class EggMonters {
   public int x;
   public int y;
   public int frame = 0;
   public byte status = 0;
   public int vy;
   public static Mob ownerEgg;

   public final boolean gameAA() {
      if (this.x < GameScr.cmx) {
         return false;
      } else if (this.x > GameScr.cmx + GameScr.gW) {
         return false;
      } else if (this.y < GameScr.cmy) {
         return false;
      } else if (this.y > GameScr.cmy + GameScr.gH + 30) {
         return false;
      } else {
         return ownerEgg == null || ownerEgg.status != 8;
      }
   }

   public EggMonters(int var1, int var2) {
      this.x = var1;
      this.y = var2;

      for(int var3 = 0; var3 < GameScr.vMob.size(); ++var3) {
         Mob var4;
         if ((var4 = (Mob)GameScr.vMob.elementAt(var3)).templateId == 202) {
            ownerEgg = var4;
         }
      }

   }
}
