public final class EPosition {
   public int x;
   public int y;
   public byte follow;
   public byte cou = 0;
   public short index = -1;

   public EPosition(int var1, int var2) {
      this.x = var1;
      this.y = var2;
   }

   public EPosition(int var1, int var2, int var3) {
      this.x = var1;
      this.y = var2;
      this.follow = (byte)var3;
   }

   public EPosition() {
   }
}
