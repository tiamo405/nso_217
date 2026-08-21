public final class Clan_ThanThu {
   public String name = "";
   public byte stars;
   public short idIconItem;
   public short idThanThu;
   public int time_aptrung = -1;
   public String str_trungno = "";
   public MyVector vecInfo = new MyVector();
   public int curExp = -1;
   public int maxExp = -1;
   public long timeStartThanThu;

   public Clan_ThanThu(String var1, byte var2, short var3, short var4, int var5, String var6, MyVector var7, int var8, int var9) {
      this.name = var1;
      this.idIconItem = var3;
      this.idThanThu = var4;
      this.time_aptrung = var5;
      this.str_trungno = var6;
      this.vecInfo = var7;
      this.curExp = var8;
      this.maxExp = var9;
      this.timeStartThanThu = System.currentTimeMillis();
      this.stars = var2;
   }
}
