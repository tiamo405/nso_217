public abstract class Dialog {
   Command left;
   Command center;
   Command right;

   public void paint(mGraphics var1) {
      var1.gameAA(-var1.gameAA(), -var1.gameAB());
      var1.gameAD(0, 0, GameCanvas.w, GameCanvas.h);
      Paint.gameAA(var1);
      Paint.gameAA(var1, this.left, this.center, this.right);
   }

   public void gameAA(int var1) {
      switch(var1) {
      case -39:
      case -2:
         GameCanvas.keyHold[8] = true;
         GameCanvas.keyPressedz[8] = true;
         return;
      case -38:
      case -1:
         GameCanvas.keyHold[2] = true;
         GameCanvas.keyPressedz[2] = true;
         return;
      case -22:
      case -7:
         GameCanvas.keyHold[13] = true;
         GameCanvas.keyPressedz[13] = true;
         return;
      case -21:
      case -6:
         GameCanvas.keyHold[12] = true;
         GameCanvas.keyPressedz[12] = true;
         return;
      case -5:
      case 10:
         GameCanvas.keyHold[5] = true;
         GameCanvas.keyPressedz[5] = true;
         return;
      default:
      }
   }

   public void gameAB() {
      if (this.center != null && (GameCanvas.keyPressedz[5] || mScreen.gameAA(this.center))) {
         GameCanvas.keyPressedz[5] = false;
         GameCanvas.isPointerClick = false;
         mScreen.keyTouch = -1;
         GameCanvas.isPointerJustRelease = false;
         if (this.center != null) {
            this.center.gameAA();
         }

         mScreen.keyTouch = -1;
      }

      if (this.left != null && (GameCanvas.keyPressedz[12] || mScreen.gameAA(this.left))) {
         GameCanvas.keyPressedz[12] = false;
         GameCanvas.isPointerClick = false;
         mScreen.keyTouch = -1;
         GameCanvas.isPointerJustRelease = false;
         if (this.left != null) {
            this.left.gameAA();
         }

         mScreen.keyTouch = -1;
      }

      if (this.right != null && (GameCanvas.keyPressedz[13] || mScreen.gameAA(this.right))) {
         GameCanvas.keyPressedz[13] = false;
         GameCanvas.isPointerClick = false;
         GameCanvas.isPointerJustRelease = false;
         mScreen.keyTouch = -1;
         if (this.right != null) {
            this.right.gameAA();
         }

         mScreen.keyTouch = -1;
      }

      GameCanvas.gameAH();
      GameCanvas.gameAI();
   }
}
