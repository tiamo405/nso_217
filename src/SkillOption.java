public final class SkillOption {
   public int param;
   public SkillOptionTemplate optionTemplate;
   private String optionString;

   public final String gameAA() {
      if (this.optionString == null) {
         if (this.optionTemplate.id != 62 && this.optionTemplate.id != 64 && this.optionTemplate.id != 70) {
            this.optionString = NinjaUtil.replace(this.optionTemplate.name, "#", String.valueOf(this.param));
         } else {
            float var1 = (float)this.param / 1000.0F;
            this.optionString = NinjaUtil.replace(this.optionTemplate.name, "#", String.valueOf(var1));
         }
      }

      return this.optionString;
   }
}
