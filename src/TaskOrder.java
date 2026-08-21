public final class TaskOrder {
   public int taskId;
   public int count;
   public int maxCount;
   public String name;
   public String description;
   public int killId;
   public int mapId;

   public TaskOrder(byte var1, int var2, int var3, String var4, String var5, int var6, int var7) {
      this.count = var2;
      this.maxCount = var3;
      this.taskId = var1;
      this.name = var4;
      this.description = var5;
      this.killId = var6;
      this.mapId = var7;
   }
}
