public final class ItemTemplates {
   public static mHashtable itemTemplates = new mHashtable();

   public static void gameAA(ItemTemplate var0) {
      itemTemplates.gameAA(new Short(var0.id), var0);
   }

   public static ItemTemplate gameAA(short var0) {
      return (ItemTemplate)itemTemplates.gameAA(new Short(var0));
   }

   public static short gameAB(short var0) {
      return gameAA(var0).iconID;
   }
}
