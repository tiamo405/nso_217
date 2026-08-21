public final class CharPartInfo {
   public static mHashtable gameAA = new mHashtable();
   public static mHashtable gameAB = new mHashtable();
   public static mHashtable gameAC = new mHashtable();
   public static mHashtable gameAD = new mHashtable();
   public static mHashtable gameAE = new mHashtable();
   public static mHashtable gameAF = new mHashtable();
   private static mHashtable gameAL = new mHashtable();
   public mHashtable gameAG = new mHashtable();
   public static mHashtable gameAH = new mHashtable();
   public int gameAI = 0;
   public int gameAJ = 0;
   public int gameAK;

   public static void gameAA(Message var0) {
      try {
         gameAC.gameAA.clear();
         gameAA.gameAA.clear();
         gameAB.gameAA.clear();
         gameAF.gameAA.clear();
         gameAD.gameAA.clear();
         gameAE.gameAA.clear();
         gameAH.gameAA.clear();
         int var1 = var0.reader().readUnsignedByte();

         int var2;
         byte var3;
         short var4;
         short var5;
         CharPartInfo var6;
         short var7;
         short var8;
         short var9;
         CharPartInfo var10;
         int var13;
         for(var2 = 0; var2 < var1; ++var2) {
            var3 = var0.reader().readByte();
            var4 = var0.reader().readShort();
            var5 = var0.reader().readShort();
            (var6 = new CharPartInfo()).gameAK = var5;

            for(var13 = 0; var13 < var3 - 2; var13 += 3) {
               var7 = var0.reader().readShort();
               var8 = var0.reader().readShort();
               var9 = var0.reader().readShort();
               (var10 = new CharPartInfo()).gameAI = var8;
               var10.gameAJ = var9;
               var6.gameAG.gameAA(String.valueOf(var7), var10);
            }

            gameAA.gameAA(String.valueOf(var4), var6);
         }

         for(var2 = 0; var2 < var1; ++var2) {
            var3 = var0.reader().readByte();
            var4 = var0.reader().readShort();
            var5 = var0.reader().readShort();
            (var6 = new CharPartInfo()).gameAK = var5;

            for(var13 = 0; var13 < var3 - 2; var13 += 3) {
               var7 = var0.reader().readShort();
               var8 = var0.reader().readShort();
               var9 = var0.reader().readShort();
               (var10 = new CharPartInfo()).gameAI = var8;
               var10.gameAJ = var9;
               var6.gameAG.gameAA(String.valueOf(var7), var10);
            }

            gameAB.gameAA(String.valueOf(var4), var6);
         }

         for(var2 = 0; var2 < var1; ++var2) {
            var3 = var0.reader().readByte();
            var4 = var0.reader().readShort();
            var5 = var0.reader().readShort();
            (var6 = new CharPartInfo()).gameAK = var5;

            for(var13 = 0; var13 < var3 - 2; var13 += 3) {
               var7 = var0.reader().readShort();
               var8 = var0.reader().readShort();
               var9 = var0.reader().readShort();
               (var10 = new CharPartInfo()).gameAI = var8;
               var10.gameAJ = var9;
               var6.gameAG.gameAA(String.valueOf(var7), var10);
            }

            gameAC.gameAA(String.valueOf(var4), var6);
         }

         var1 = var0.reader().readUnsignedByte();

         for(var2 = 0; var2 < var1; var2 += 2) {
            short var12 = var0.reader().readShort();
            var4 = var0.reader().readShort();
            CharPartInfo var16;
            (var16 = new CharPartInfo()).gameAK = var4;
            gameAL.gameAA(String.valueOf(var12), var16);
         }

         var1 = var0.reader().readUnsignedByte();

         for(var2 = 0; var2 < var1; ++var2) {
            var3 = var0.reader().readByte();
            var4 = var0.reader().readShort();
            var5 = var0.reader().readShort();
            (var6 = new CharPartInfo()).gameAK = var5;

            for(var13 = 0; var13 < var3 - 2; var13 += 3) {
               var7 = var0.reader().readShort();
               var8 = var0.reader().readShort();
               var9 = var0.reader().readShort();
               (var10 = new CharPartInfo()).gameAI = var8;
               var10.gameAJ = var9;
               var6.gameAG.gameAA(String.valueOf(var7), var10);
            }

            gameAD.gameAA(String.valueOf(var4), var6);
         }

         for(var2 = 0; var2 < var1; ++var2) {
            var3 = var0.reader().readByte();
            var4 = var0.reader().readShort();
            var5 = var0.reader().readShort();
            (var6 = new CharPartInfo()).gameAK = var5;

            for(var13 = 0; var13 < var3 - 2; var13 += 3) {
               var7 = var0.reader().readShort();
               var8 = var0.reader().readShort();
               var9 = var0.reader().readShort();
               (var10 = new CharPartInfo()).gameAI = var8;
               var10.gameAJ = var9;
               var6.gameAG.gameAA(String.valueOf(var7), var10);
            }

            gameAE.gameAA(String.valueOf(var4), var6);
         }

         for(var2 = 0; var2 < var1; ++var2) {
            var3 = var0.reader().readByte();
            var4 = var0.reader().readShort();
            var5 = var0.reader().readShort();
            (var6 = new CharPartInfo()).gameAK = var5;

            for(var13 = 0; var13 < var3 - 2; var13 += 3) {
               var7 = var0.reader().readShort();
               var8 = var0.reader().readShort();
               var9 = var0.reader().readShort();
               (var10 = new CharPartInfo()).gameAI = var8;
               var10.gameAJ = var9;
               var6.gameAG.gameAA(String.valueOf(var7), var10);
            }

            gameAF.gameAA(String.valueOf(var4), var6);
         }

         byte var15 = var0.reader().readByte();

         for(int var14 = 0; var14 < var15; ++var14) {
            var4 = var0.reader().readShort();
            int[][] var17 = new int[6][];

            for(var13 = 0; var13 < 6; ++var13) {
               byte var18 = var0.reader().readByte();
               var17[var13] = new int[var18];

               for(int var19 = 0; var19 < var18; ++var19) {
                  var17[var13][var19] = var0.reader().readShort();
               }
            }

            gameAH.gameAA(String.valueOf(var4), var17);
         }

      } catch (Exception var11) {
      }
   }
}
