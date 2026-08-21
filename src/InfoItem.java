public final class InfoItem {
   public String s;
   mFont f;
   public int speed;

   public InfoItem(String var1) {
      this.f = mFont.tahoma_7_white;
      this.s = var1;
      this.speed = 20;
   }

   public InfoItem(String var1, mFont var2, int var3) {
      this.f = var2;
      this.s = var1;
      this.speed = var3;
   }
}
