import javax.microedition.lcdui.Image;

public final class FrameImage {
   private int frameWidth;
   private int frameHeight;
   private int nFrame;
   private Image imgFrame;
   private int[] pos;
   private int totalHeight;

   public FrameImage(Image var1, int var2, int var3) {
      this.imgFrame = var1;
      this.frameWidth = var2;
      this.frameHeight = var3;
      this.totalHeight = var1.getHeight();
      this.nFrame = this.totalHeight / var3;
      this.pos = new int[this.nFrame];

      for(int var4 = 0; var4 < this.nFrame; ++var4) {
         this.pos[var4] = var4 * var3;
      }

   }

   public final void gameAA(int var1, int var2, int var3, int var4, int var5, mGraphics var6) {
      if (var1 >= 0 && var1 < this.nFrame) {
         var6.gameAA(this.imgFrame, 0, this.pos[var1], this.frameWidth, this.frameHeight, var4, var2, var3, var5);
      }

   }
}
