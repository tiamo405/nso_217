public final class Task {
   public int index;
   public short[] counts;
   public String[] names;
   public String[] details;
   public String[] subNames;
   public short count;

   public Task(short var1, byte var2, String var3, String var4, String[] var5, short[] var6, short var7) {
      this.index = var2;
      this.names = mFont.tahoma_7b_white.gameAB(var3, 155);
      this.details = mFont.tahoma_7.gameAB(var4, 155);
      this.subNames = var5;
      this.counts = var6;
      this.count = var7;
   }
}
