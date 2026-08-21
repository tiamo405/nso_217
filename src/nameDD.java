import java.io.InputStream;

public class nameDD implements IActionListener {
   private static nameDD fieldAA;

   public static nameDD fieldAA() {
      if (fieldAA == null) {
         fieldAA = new nameDD();
      }

      return fieldAA;
   }

   private static String fieldAA(String var0) {
      InputStream var1 = RMS.fieldAA("/".concat(String.valueOf(var0)));

      try {
         byte[] var2 = new byte[var1.available()];
         var1.read(var2);
         var0 = new String(var2, "UTF-8");
      } catch (Exception var3) {
         var0 = "";
      }

      return var0;
   }

   public final void perform(int var1, Object var2) {
      if (var1 == 1) {
         GameCanvas.inputDlg.gameAA("Tốc độ NextMap", new Command("Đặt", this, 2, (Object)null), 1);
         GameCanvas.inputDlg.tfInput.setText(String.valueOf(CodePhu.fieldAJ));
      } else if (var1 == 2) {
         var1 = CodePhu.fieldAJ;

         try {
            var1 = Integer.parseInt(GameCanvas.inputDlg.tfInput.getText());
         } catch (Exception var4) {
         }

         if (var1 > 0 && var1 <= 99) {
            CodePhu.fieldAJ = var1;
            RMS.gameAA("nextmap", String.valueOf(CodePhu.fieldAJ));
         } else {
            GameScr.fieldAC("Tốc độ nextmap từ 1 đến 99");
         }

         GameCanvas.gameAJ();
      } else if (var1 == 3) {
         GameCanvas.inputDlg.gameAA("Màu nền Background", new Command("Đặt", this, 4, (Object)null), 1);
         GameCanvas.inputDlg.tfInput.setText(String.valueOf(CodePhu.fieldAK));
      } else if (var1 == 4) {
         var1 = CodePhu.fieldAK;

         try {
            var1 = Integer.parseInt(GameCanvas.inputDlg.tfInput.getText());
         } catch (Exception var5) {
         }

         if (var1 >= -1 && var1 <= 16777216) {
            CodePhu.fieldAK = var1;
            RMS.gameAA("background", String.valueOf(CodePhu.fieldAK));
         } else {
            GameScr.fieldAC("Màu nền từ -1 đến 16777216");
         }

         GameCanvas.gameAJ();
      } else {
         if (var1 == 5) {
            GameScr.gI().gameAA("Hướng dẫn", fieldAA("hd.txt"), true);
         }

      }
   }
}
