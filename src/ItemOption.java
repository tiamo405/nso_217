public final class ItemOption {
   public int param;
   public byte active;
   public ItemOptionTemplate optionTemplate;

   public ItemOption() {
   }

   public ItemOption(int var1, int var2) {
      this.param = var2;
      this.optionTemplate = GameScr.iOptionTemplates[var1];
   }

   public final String getOptionString() {
      if (this.optionTemplate.type == 9) {
         String var1 = getValuePercent(this.param);
         return NinjaUtil.replace(this.optionTemplate.name, "#", var1);
      } else {
         return NinjaUtil.replace(this.optionTemplate.name, "#", String.valueOf(this.param));
      }
   }

   private static String getValuePercent(int var0) {
      if (var0 % 100 == 0) {
         return String.valueOf(var0 / 100);
      } else {
         return var0 % 10 == 0 ? var0 / 100 + "." + var0 % 100 / 10 : var0 / 100 + "." + var0 % 100 / 10 + var0 % 10;
      }
   }

   public final String getOptionShopString() {
      String var1 = null;
      boolean var2 = false;
      int var3;
      if (this.optionTemplate.id != 0 && this.optionTemplate.id != 1 && this.optionTemplate.id != 21 && this.optionTemplate.id != 22 && this.optionTemplate.id != 23 && this.optionTemplate.id != 24 && this.optionTemplate.id != 25 && this.optionTemplate.id != 26) {
         if (this.optionTemplate.id != 6 && this.optionTemplate.id != 7 && this.optionTemplate.id != 8 && this.optionTemplate.id != 9 && this.optionTemplate.id != 19) {
            if (this.optionTemplate.id != 2 && this.optionTemplate.id != 3 && this.optionTemplate.id != 4 && this.optionTemplate.id != 5 && this.optionTemplate.id != 10 && this.optionTemplate.id != 11 && this.optionTemplate.id != 12 && this.optionTemplate.id != 13 && this.optionTemplate.id != 14 && this.optionTemplate.id != 15 && this.optionTemplate.id != 17 && this.optionTemplate.id != 18 && this.optionTemplate.id != 20) {
               if (this.optionTemplate.id == 16) {
                  var3 = this.param - 3 + 1;
                  var1 = NinjaUtil.replace(this.optionTemplate.name, "#", String.valueOf(this.param)) + " (" + var3 + "-" + this.param + ")";
               } else if (this.optionTemplate.type == 9) {
                  var1 = getValuePercent(this.param);
                  var1 = NinjaUtil.replace(this.optionTemplate.name, "#", var1);
               } else {
                  var1 = NinjaUtil.replace(this.optionTemplate.name, "#", String.valueOf(this.param));
               }
            } else {
               var3 = this.param - 5 + 1;
               var1 = NinjaUtil.replace(this.optionTemplate.name, "#", String.valueOf(this.param)) + " (" + var3 + "-" + this.param + ")";
            }
         } else {
            var3 = this.param - 10 + 1;
            var1 = NinjaUtil.replace(this.optionTemplate.name, "#", String.valueOf(this.param)) + " (" + var3 + "-" + this.param + ")";
         }
      } else {
         var3 = this.param - 50 + 1;
         var1 = NinjaUtil.replace(this.optionTemplate.name, "#", String.valueOf(this.param)) + " (" + var3 + "-" + this.param + ")";
      }

      return var1;
   }
}
