 public final class Scroll {
   private int cmtoX;
   private int cmtoY;
   public int cmx;
   public int cmy;
   private int cmvx;
   private int cmvy;
   private int cmdx;
   private int cmdy;
   public int xPos;
   public int yPos;
   public int width;
   public int height;
   private int cmxLim;
   private int cmyLim;
   public static Scroll me;
   private int pointerDownTime;
   private int pointerDownFirstX;
   private int[] pointerDownLastX = new int[3];
   private boolean pointerIsDowning;
   private boolean isDownWhenRunning;
   private int cmRun;
   private int selectedItem;
   public int ITEM_SIZE;
   private int ITEM_PER_LINE;
   private boolean styleUPDOWN = true;

   public final void gameAA() {
      this.cmtoX = 0;
      this.cmtoY = 0;
      this.cmx = 0;
      this.cmy = 0;
      this.cmvx = 0;
      this.cmvy = 0;
      this.cmdx = 0;
      this.cmdy = 0;
      this.cmxLim = 0;
      this.cmyLim = 0;
      this.width = 0;
      this.height = 0;
   }

   public final void gameAA(mGraphics var1, int var2, int var3, int var4, int var5) {
      var1.gameAD(var2, var3, var4, var5 - 1);
      var1.gameAA(-var1.gameAA(), -var1.gameAB());
      var1.gameAA(-this.cmx, -this.cmy);
   }

   public final void gameAA(mGraphics var1) {
      var1.gameAD(this.xPos, this.yPos, this.width, this.height - 1);
      var1.gameAA(-var1.gameAA(), -var1.gameAB());
      var1.gameAA(-this.cmx, -this.cmy);
   }
   
   public ScrollResult updateKey()
{
	if (styleUPDOWN)
	{
		return gameAB();
	}
	return gameAB();
}

   public final ScrollResult gameAB() {
      int var1;
      int var2;
      int var3;
      int var4;
      Scroll var5;
      byte var6;
      boolean var7;
      ScrollResult var8;
      if (this.styleUPDOWN) {
         var1 = (var5 = this).xPos;
         var2 = var5.yPos;
         var3 = var5.width;
         var4 = var5.height;
         if (GameCanvas.isPointerDown) {
            if (!var5.pointerIsDowning && GameCanvas.gameAC(var1, var2, var3, var4)) {
               for(var3 = 0; var3 < var5.pointerDownLastX.length; ++var3) {
                  var5.pointerDownLastX[0] = GameCanvas.py;
               }

               var5.pointerDownFirstX = GameCanvas.py;
               var5.pointerIsDowning = true;
               var5.selectedItem = -1;
               var5.isDownWhenRunning = var5.cmRun != 0;
               var5.cmRun = 0;
            } else if (var5.pointerIsDowning) {
               ++var5.pointerDownTime;
               if (var5.pointerDownTime > 5 && var5.pointerDownFirstX == GameCanvas.py && !var5.isDownWhenRunning) {
                  var5.pointerDownFirstX = -1000;
                  if (var5.ITEM_PER_LINE > 1) {
                     var3 = (var5.cmtoY + GameCanvas.py - var2) / var5.ITEM_SIZE;
                     var4 = (var5.cmtoX + GameCanvas.px - var1) / var5.ITEM_SIZE;
                     var5.selectedItem = var3 * var5.ITEM_PER_LINE + var4;
                  } else {
                     var5.selectedItem = (var5.cmtoY + GameCanvas.py - var2) / var5.ITEM_SIZE;
                  }
               }

               if ((var3 = GameCanvas.py - var5.pointerDownLastX[0]) != 0 && var5.selectedItem != -1) {
                  var5.selectedItem = -1;
               }

               for(var4 = var5.pointerDownLastX.length - 1; var4 > 0; --var4) {
                  var5.pointerDownLastX[var4] = var5.pointerDownLastX[var4 - 1];
               }

               var5.pointerDownLastX[0] = GameCanvas.py;
               var5.cmtoY -= var3;
               if (var5.cmtoY < 0) {
                  var5.cmtoY = 0;
               }

               if (var5.cmtoY > var5.cmyLim) {
                  var5.cmtoY = var5.cmyLim;
               }

               if (var5.cmy < 0 || var5.cmy > var5.cmyLim) {
                  var3 /= 2;
               }

               var5.cmy -= var3;
            }
         }

         var7 = false;
         if (GameCanvas.isPointerJustRelease && var5.pointerIsDowning) {
            var4 = GameCanvas.py - var5.pointerDownLastX[0];
            GameCanvas.isPointerJustRelease = false;
            if (Res.abs(var4) < 20 && Res.abs(GameCanvas.py - var5.pointerDownFirstX) < 20 && !var5.isDownWhenRunning) {
               var5.cmRun = 0;
               var5.cmtoY = var5.cmy;
               var5.pointerDownFirstX = -1000;
               if (var5.ITEM_PER_LINE > 1) {
                  var2 = (var5.cmtoY + GameCanvas.py - var2) / var5.ITEM_SIZE;
                  var1 = (var5.cmtoX + GameCanvas.px - var1) / var5.ITEM_SIZE;
                  var5.selectedItem = var2 * var5.ITEM_PER_LINE + var1;
               } else {
                  var5.selectedItem = (var5.cmtoY + GameCanvas.py - var2) / var5.ITEM_SIZE;
               }

               var5.pointerDownTime = 0;
               var7 = true;
            } else if (var5.selectedItem != -1 && var5.pointerDownTime > 5) {
               var5.pointerDownTime = 0;
               var7 = true;
            } else if (var5.selectedItem == -1 && !var5.isDownWhenRunning) {
               if (var5.cmy < 0) {
                  var5.cmtoY = 0;
               } else if (var5.cmy > var5.cmyLim) {
                  var5.cmtoY = var5.cmyLim;
               } else {
                  if ((var2 = GameCanvas.py - var5.pointerDownLastX[0] + (var5.pointerDownLastX[0] - var5.pointerDownLastX[1]) + (var5.pointerDownLastX[1] - var5.pointerDownLastX[2])) > 10) {
                     var6 = 10;
                  } else if (var2 < -10) {
                     var6 = -10;
                  } else {
                     var6 = 0;
                  }

                  var5.cmRun = -var6 * 100;
               }
            }

            var5.pointerIsDowning = false;
            var5.pointerDownTime = 0;
            GameCanvas.isPointerJustRelease = false;
         }

         (var8 = new ScrollResult()).selected = var5.selectedItem;
         var8.isFinish = var7;
         var8.isDowning = var5.pointerIsDowning;
         return var8;
      } else {
         var1 = (var5 = this).xPos;
         var2 = var5.yPos;
         var3 = var5.width;
         var4 = var5.height;
         if (GameCanvas.isPointerDown) {
            if (!var5.pointerIsDowning && GameCanvas.gameAC(var1, var2, var3, var4)) {
               for(var3 = 0; var3 < var5.pointerDownLastX.length; ++var3) {
                  var5.pointerDownLastX[0] = GameCanvas.px;
               }

               var5.pointerDownFirstX = GameCanvas.px;
               var5.pointerIsDowning = true;
               var5.selectedItem = -1;
               var5.isDownWhenRunning = var5.cmRun != 0;
               var5.cmRun = 0;
            } else if (var5.pointerIsDowning) {
               ++var5.pointerDownTime;
               if (var5.pointerDownTime > 5 && var5.pointerDownFirstX == GameCanvas.px && !var5.isDownWhenRunning) {
                  var5.pointerDownFirstX = -1000;
                  var5.selectedItem = (var5.cmtoX + GameCanvas.px - var1) / var5.ITEM_SIZE;
               }

               if ((var3 = GameCanvas.px - var5.pointerDownLastX[0]) != 0 && var5.selectedItem != -1) {
                  var5.selectedItem = -1;
               }

               for(var4 = var5.pointerDownLastX.length - 1; var4 > 0; --var4) {
                  var5.pointerDownLastX[var4] = var5.pointerDownLastX[var4 - 1];
               }

               var5.pointerDownLastX[0] = GameCanvas.px;
               var5.cmtoX -= var3;
               if (var5.cmtoX < 0) {
                  var5.cmtoX = 0;
               }

               if (var5.cmtoX > var5.cmxLim) {
                  var5.cmtoX = var5.cmxLim;
               }

               if (var5.cmx < 0 || var5.cmx > var5.cmxLim) {
                  var3 /= 2;
               }

               var5.cmx -= var3;
            }
         }

         var7 = false;
         if (GameCanvas.isPointerJustRelease && var5.pointerIsDowning) {
            var4 = GameCanvas.px - var5.pointerDownLastX[0];
            GameCanvas.isPointerJustRelease = false;
            if (Res.abs(var4) < 20 && Res.abs(GameCanvas.px - var5.pointerDownFirstX) < 20 && !var5.isDownWhenRunning) {
               var5.cmRun = 0;
               var5.cmtoX = var5.cmx;
               var5.pointerDownFirstX = -1000;
               var5.selectedItem = (var5.cmtoX + GameCanvas.px - var1) / var5.ITEM_SIZE;
               var5.pointerDownTime = 0;
               var7 = true;
            } else if (var5.selectedItem != -1 && var5.pointerDownTime > 5) {
               var5.pointerDownTime = 0;
               var7 = true;
            } else if (var5.selectedItem == -1 && !var5.isDownWhenRunning) {
               if (var5.cmx < 0) {
                  var5.cmtoX = 0;
               } else if (var5.cmx > var5.cmxLim) {
                  var5.cmtoX = var5.cmxLim;
               } else {
                  if ((var2 = GameCanvas.px - var5.pointerDownLastX[0] + (var5.pointerDownLastX[0] - var5.pointerDownLastX[1]) + (var5.pointerDownLastX[1] - var5.pointerDownLastX[2])) > 10) {
                     var6 = 10;
                  } else if (var2 < -10) {
                     var6 = -10;
                  } else {
                     var6 = 0;
                  }

                  var5.cmRun = -var6 * 100;
               }
            }

            var5.pointerIsDowning = false;
            var5.pointerDownTime = 0;
            GameCanvas.isPointerJustRelease = false;
         }

         (var8 = new ScrollResult()).selected = var5.selectedItem;
         var8.isFinish = var7;
         var8.isDowning = var5.pointerIsDowning;
         return var8;
      }
   }

   public final void gameAC() {
      if (this.cmRun != 0 && !this.pointerIsDowning) {
         if (this.styleUPDOWN) {
            this.cmtoY += this.cmRun / 100;
            if (this.cmtoY < 0) {
               this.cmtoY = 0;
            } else if (this.cmtoY > this.cmyLim) {
               this.cmtoY = this.cmyLim;
            } else {
               this.cmy = this.cmtoY;
            }
         } else {
            this.cmtoX += this.cmRun / 100;
            if (this.cmtoX < 0) {
               this.cmtoX = 0;
            } else if (this.cmtoX > this.cmxLim) {
               this.cmtoX = this.cmxLim;
            } else {
               this.cmx = this.cmtoX;
            }
         }

         this.cmRun = this.cmRun * 9 / 10;
         if (this.cmRun < 100 && this.cmRun > -100) {
            this.cmRun = 0;
         }
      }

      if (this.cmx != this.cmtoX && !this.pointerIsDowning) {
         this.cmvx = this.cmtoX - this.cmx << 2;
         this.cmdx += this.cmvx;
         this.cmx += this.cmdx >> 4;
         this.cmdx &= 15;
      }

      if (this.cmy != this.cmtoY && !this.pointerIsDowning) {
         this.cmvy = this.cmtoY - this.cmy << 2;
         this.cmdy += this.cmvy;
         this.cmy += this.cmdy >> 4;
         this.cmdy &= 15;
      }

   }

   public final void gameAA(int var1, int var2, int var3, int var4, int var5, int var6, boolean var7, int var8) {
      this.xPos = var3;
      this.yPos = var4;
      this.ITEM_SIZE = var2;
      this.width = var5;
      this.height = var6;
      this.styleUPDOWN = var7;
      this.ITEM_PER_LINE = var8;
      if (var7) {
         this.cmyLim = var1 * var2 - var6;
      } else {
         this.cmxLim = var1 * var2 - var5;
      }

      if (this.cmyLim < 0) {
         this.cmyLim = 0;
      }

      if (this.cmxLim < 0) {
         this.cmxLim = 0;
      }

   }

   public final void gameAA(int var1) {
      if (this.styleUPDOWN) {
         var1 -= (this.height - this.ITEM_SIZE) / 2;
         this.cmtoY = var1;
         if (this.cmtoY < 0) {
            this.cmtoY = 0;
         }

         if (this.cmtoY > this.cmyLim) {
            this.cmtoY = this.cmyLim;
            return;
         }
      } else {
         var1 -= (this.width - this.ITEM_SIZE) / 2;
         this.cmtoX = var1;
         if (this.cmtoX < 0) {
            this.cmtoX = 0;
         }

         if (this.cmtoX > this.cmxLim) {
            this.cmtoX = this.cmxLim;
         }
      }

   }
}
