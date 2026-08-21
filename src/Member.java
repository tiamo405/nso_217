public final class Member {
   public int iconId;
   public int level;
   public int type;
   public String name;
   public boolean isOnline;
   public int pointClan;
   public int pointClanWeek;

   public Member(int var1, int var2, int var3, String var4, int var5, boolean var6) {
      switch(var1) {
      case 0:
         this.iconId = 647;
         break;
      case 1:
         this.iconId = 1182;
         break;
      case 2:
         this.iconId = 1181;
         break;
      case 3:
         this.iconId = 643;
         break;
      case 4:
         this.iconId = 645;
         break;
      case 5:
         this.iconId = 676;
         break;
      case 6:
         this.iconId = 1119;
      }

      this.level = var2;
      this.type = var3;
      this.name = var4;
      this.pointClan = var5;
      this.isOnline = var6;
   }
}
