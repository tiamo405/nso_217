import javax.microedition.lcdui.Image;

public final class ItemTree {
   public int idTree;
   private int xTree;
   private int yTree;

   public ItemTree(int var1, int var2) {
      this.xTree = var1 * 24 + 12;
      this.yTree = var2 * 24 + 24 + 3;
   }

   private Object gameAA() {
      Object var1;
      return (var1 = TileMap.itemMap.gameAA(String.valueOf(this.idTree))) != null ? var1 : null;
   }

   public final void gameAA(mGraphics var1) {
      if (!GameCanvas.lowGraphic) {
         if (this.gameAA() != null) {
            var1.gameAA((Image)this.gameAA(), this.xTree, this.yTree, 33);
         }

      }
   }
}
