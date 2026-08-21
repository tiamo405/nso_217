import javax.microedition.lcdui.Image;

public final class InputDlg extends Dialog {
   private String[] info;
   public TField tfInput;
   private int padLeft = 40;

   public InputDlg() {
      if (GameCanvas.w <= 176) {
         this.padLeft = 10;
      }

      this.tfInput = new TField();
      this.tfInput.x = this.padLeft + 10;
      this.tfInput.y = GameCanvas.h - mScreen.ITEM_HEIGHT - 43;
      this.tfInput.width = GameCanvas.w - 2 * (this.padLeft + 10);
      this.tfInput.height = mScreen.ITEM_HEIGHT + 2;
      this.tfInput.isFocus = true;
      super.right = this.tfInput.cmdClear;
   }

   public final void gameAA(String var1, Command var2, int var3) {
      try {
         this.tfInput.setText("");
         this.tfInput.setIputType(var3);
         this.info = mFont.tahoma_8b.gameAB(var1, GameCanvas.w - (this.padLeft << 1));
         super.left = new Command(mResources.gameBH, GameCanvas.gameAA(), 8882, (Object)null);
         super.center = var2;
         if (super.left != null) {
            super.left.x = GameCanvas.w / 2 - 160;
            super.left.y = GameCanvas.h - 26;
         }

         if (super.center != null) {
            super.center.x = GameCanvas.w / 2 - 35;
            super.center.y = GameCanvas.h - 26;
         }

         if (super.right != null) {
            super.right.x = GameCanvas.w / 2 + 88;
            super.right.y = GameCanvas.h - 26;
         }

         GameCanvas.currentDialog = this;
      } catch (Exception var4) {
         var4.printStackTrace();
      }
   }

   public final void paint(mGraphics var1) {
      GameCanvas.paintz.gameAA(var1, this.padLeft, GameCanvas.h - 77 - mScreen.cmdH, GameCanvas.w - (this.padLeft << 1), 69, this.info, (Image)null);
      this.tfInput.paint(var1);
      super.paint(var1);
   }

   public final void gameAA(int var1) {
      this.tfInput.gameAA(var1);
      super.gameAA(var1);
   }

   public final void gameAB() {
      this.tfInput.gameAC();
      super.gameAB();
   }
}
