public final class MsgDlg extends Dialog {
   private String[] info;
   public boolean isWait;
   public int timeShow;
   private int h;
   private int padLeft = 30;

   public MsgDlg() {
      if (GameCanvas.w <= 176) {
         this.padLeft = 10;
      }

   }

   public final void update() {
      this.gameAA(mResources.PLEASEWAIT, (Command)null, (Command)null, (Command)null);
      GameCanvas.currentDialog = this;
   }

   public final void gameAA(String var1, Command var2, Command var3, Command var4) {
      this.info = mFont.tahoma_8b.gameAB(var1, GameCanvas.w - ((this.padLeft << 1) + 40));
      super.left = var2;
      super.center = var3;
      super.right = var4;
      if (var3 != null) {
         super.center.x = GameCanvas.w / 2 - 35;
         super.center.y = GameCanvas.h - 26;
         if (var2 != null) {
            super.left.x = GameCanvas.w / 2 - 115;
            super.left.y = GameCanvas.h - 26;
         }

         if (var4 != null) {
            super.right.x = GameCanvas.w / 2 + 45;
            super.right.y = GameCanvas.h - 26;
         }
      } else {
         if (var2 != null) {
            super.left.x = GameCanvas.w / 2 - 80;
            super.left.y = GameCanvas.h - 26;
         }

         if (var4 != null) {
            super.right.x = GameCanvas.w / 2 + 10;
            super.right.y = GameCanvas.h - 26;
         }
      }

      this.isWait = false;
      this.h = 80;
      if (this.info.length >= 5) {
         this.h = this.info.length * mFont.tahoma_8b.gameAC() + 20;
      }

   }

   public final void paint(mGraphics var1) {
      int var2 = GameCanvas.h - this.h - 38;
      Paint.gameAA(this.padLeft, var2, GameCanvas.w - (this.padLeft << 1), this.h, var1);
      var2 = var2 + (this.h - this.info.length * mFont.tahoma_8b.gameAC()) / 2 - 2;
      if (this.isWait) {
         var2 += 8;
         GameCanvas.gameAA(GameCanvas.hw, var2 - 12, var1, false);
      }

      int var3 = 0;

      for(var2 = var2; var3 < this.info.length; var2 += mFont.tahoma_8b.gameAC()) {
         mFont.tahoma_8b.gameAA(var1, this.info[var3], GameCanvas.hw, var2, 2);
         ++var3;
      }

      super.paint(var1);
   }

   public final void gameAB() {
      if (this.timeShow > 0) {
         --this.timeShow;
         if (this.timeShow == 1) {
            GameCanvas.gameAJ();
            this.timeShow = 0;
         }
      }

      super.gameAB();
   }
}
