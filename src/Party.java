public final class Party {
   public int charId;
   public int level;
   public short iconId;
   public String name;
   public boolean isLock;
   public Char c;
   public int size;

   public Party(byte var1, int var2, String var3, int var4) {
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

      this.name = var3;
      this.level = var2;
      this.size = var4;
   }

   public Party(int var1, byte var2, String var3, boolean var4) {
      this.charId = var1;
      this.isLock = var4;
      switch(var2) {
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

      this.name = var3;
      if (var1 == Char.getMyChar().charID) {
         this.c = Char.getMyChar();
      } else {
         this.c = GameScr.gameAE(var1);
      }
   }

   public static void gameAA() {
      for(int var0 = 0; var0 < GameScr.vParty.size(); ++var0) {
         Party var1;
         if ((var1 = (Party)GameScr.vParty.elementAt(var0)).charId != Char.getMyChar().charID) {
            var1.c = GameScr.gameAE(var1.charId);
         }
      }

   }

   public static void gameAA(int var0) {
      for(int var1 = 0; var1 < GameScr.vParty.size(); ++var1) {
         Party var2;
         if ((var2 = (Party)GameScr.vParty.elementAt(var1)).charId == var0) {
            var2.c = null;
            return;
         }
      }

   }
}
