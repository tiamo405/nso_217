public final class Skills {
   private static mHashtable skills = new mHashtable();

   public static void gameAA(Skill var0) {
      skills.gameAA(new Short(var0.skillId), var0);
   }

   public static Skill gameAA(short var0) {
      return (Skill)skills.gameAA(new Short(var0));
   }
}
