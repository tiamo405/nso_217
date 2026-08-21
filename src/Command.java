import javax.microedition.lcdui.Image;

public final class Command {
   public String caption;
   public String[] subCaption;
   public IActionListener actionListener;
   public int idAction;
   private Image back;
   private Image focus;
   public Image img;
   public int x = 0;
   public int y = 0;
   public int w;
   public int h;
   public boolean isFocus;
   public Object p;

   public Command(String var1, IActionListener var2, int var3, Object var4, int var5, int var6) {
      this.w = mScreen.cmdW;
      this.h = mScreen.cmdH;
      this.isFocus = false;
      this.caption = var1;
      this.idAction = var3;
      this.actionListener = var2;
      this.p = var4;
      this.x = var5;
      this.y = var6;
      this.w = mScreen.cmdW;
      this.h = mScreen.cmdH;
      this.back = null;
      this.focus = null;
   }

   public Command(String var1, IActionListener var2, int var3, Object var4) {
      this.w = mScreen.cmdW;
      this.h = mScreen.cmdH;
      this.isFocus = false;
      this.caption = var1;
      this.idAction = var3;
      this.actionListener = var2;
      this.p = var4;
   }

   public Command(String var1, int var2, Object var3) {
      this.w = mScreen.cmdW;
      this.h = mScreen.cmdH;
      this.isFocus = false;
      this.caption = var1;
      this.idAction = var2;
      this.p = var3;
   }

   public Command(String var1, int var2) {
      this.w = mScreen.cmdW;
      this.h = mScreen.cmdH;
      this.isFocus = false;
      this.caption = var1;
      this.idAction = var2;
   }

   public Command(String var1, int var2, int var3, int var4) {
      this.w = mScreen.cmdW;
      this.h = mScreen.cmdH;
      this.isFocus = false;
      this.caption = var1;
      this.idAction = 0;
      this.x = var3;
      this.y = var4;
   }

   public final void gameAA() {
      if (this.idAction > 0) {
         if (this.actionListener != null) {
            this.actionListener.perform(this.idAction, this.p);
            return;
         }

         GameScr.gI().gameAB(this.idAction, this.p);
      }

   }

   public final void gameAA(mGraphics var1) {
      if (this.img != null) {
         var1.gameAA(this.img, this.x + mGraphics.gameAA(this.img) / 2, this.y + mGraphics.gameAB(this.img) / 2, 3);
      } else {
         if (this.caption != "") {
            if (!this.isFocus) {
               var1.gameAA(GameScr.imgLbtn, this.x, this.y, 0);
            } else {
               var1.gameAA(GameScr.imgLbtnFocus, this.x, this.y, 0);
            }
         }

         mFont.tahoma_7b_yellow.gameAA(var1, this.caption, this.x + 36, this.y + 6, 2);
      }
   }

   public final boolean gameAB() {
      this.isFocus = false;
      if (GameCanvas.gameAB(this.x - 3, this.y - 3, this.w + 6, this.h + 6)) {
         if (GameCanvas.isPointerDown) {
            this.isFocus = true;
         }

         if (GameCanvas.isPointerJustRelease && GameCanvas.isPointerClick) {
            return true;
         }
      }

      return false;
   }

   public final void gameAC() {
      if (this.x > 0 && this.y > 0 && this.gameAB()) {
         this.gameAA();
         GameCanvas.isPointerDown = false;
         this.isFocus = false;
      }

   }
}
