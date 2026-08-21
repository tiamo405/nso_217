public final class Lightning extends Effect2 {
   private int[] color = new int[]{16579837, 11188220};
   private MyVector listPos = new MyVector();
   private MyVector[] list;
   private EPosition posangle;
   private long timeDel = 0L;
   private boolean isContinue = false;
   private boolean isRemove = true;
   private int tick = 0;
   private int cou = 0;
   private int dem = 0;
   private int aa = 7;

   public static void gameAA(MyVector var0, EPosition var1, boolean var2, int var3) {
      Lightning var5;
      (var5 = new Lightning()).color[1] = var3;

      for(var3 = 0; var3 < var0.size(); ++var3) {
         EPosition var4;
         if ((var4 = (EPosition)var0.elementAt(var3)) != null && (Res.abs(var4.x - var1.x) >= 100 || Res.abs(var4.y - var1.y) >= 50)) {
            var0.removeElementAt(var3);
         }
      }

      var5.gameAA(var0, var1, true);
      Effect2.vEffect2.addElement(var5);
   }

   private void gameAA(MyVector var1, EPosition var2, boolean var3) {
      if (var1.size() != 0) {
         this.isContinue = var3;
         MyVector var4;
         int var8;
         int var9;
         EPosition var10;
         int var11;
         if (!var3) {
            var4 = var1;
            var8 = var1.size();

            for(var9 = 0; var9 < var8 - 1; ++var9) {
               var10 = (EPosition)var4.elementAt(var9);

               for(var11 = var9 + 1; var11 < var8; ++var11) {
                  EPosition var5 = (EPosition)var4.elementAt(var11);
                  if (var10.x > var5.x) {
                     var4.setElementAt(var10, var11);
                     var4.setElementAt(var5, var9);
                     var10 = var5;
                  }
               }
            }
         }

         this.listPos = var1;
         this.posangle = var2;
         this.list = new MyVector[var1.size()];

         int var16;
         for(var16 = 0; var16 < this.list.length; ++var16) {
            this.list[var16] = new MyVector();
         }

         var2.follow = -1;
         this.list[0].addElement(var2);
         var16 = -1;
         int var17 = 0;

         int var6;
         int var12;
         int var14;
         int var15;
         EPosition var19;
         int var23;
         while(var17 < var1.size()) {
            var6 = var2.x;
            int var7 = var2.y;
            if (var3 && var16 != -1) {
               var6 = (var19 = (EPosition)var1.elementAt(var16)).x;
               var7 = var19.y;
            }

            int var20;
            if (!var3) {
               var4 = var1;
               var8 = 0;

               for(var9 = 0; var9 < var4.size(); ++var9) {
                  if (((EPosition)var4.elementAt(var9)).index == -1) {
                     ++var8;
                  }
               }

               int var10000;
               if (var8 != 0) {
                  var8 = Res.gameAD(var8);
                  var9 = 0;
                  var20 = 0;

                  while(true) {
                     if (var20 >= var4.size()) {
                        var10000 = -1;
                        break;
                     }

                     EPosition var22;
                     if ((var22 = (EPosition)var4.elementAt(var20)).index == -1) {
                        if (var8 == var9) {
                           var22.index = 0;
                           var10000 = var20;
                           break;
                        }

                        ++var9;
                     }

                     ++var20;
                  }
               } else {
                  var10000 = -1;
               }

               var16 = var10000;
            } else {
               ++var16;
            }

            var8 = this.list[var16].size() - 1;
            EPosition var21;
            var20 = Res.gameAA((var21 = (EPosition)var1.elementAt(var16)).x - var6, -(var21.y - var7));
            var11 = Res.gameAD(15) + 10;
            var12 = 0;
            boolean var13 = false;

            while(true) {
               var23 = 0;
               if (var12 != 0) {
                  var23 = var20 - 5 + Res.gameAD(10);
               }

               var23 = Res.gameAC(var23);
               var14 = var11 * var12 * Res.gameAB(var23) >> 10;
               var15 = -(var11 * var12 * Res.gameAA(var23)) >> 10;
               EPosition var24 = new EPosition(var6 + var14, var7 + var15, var8++);
               this.list[var16].addElement(var24);
               if (Res.gameAA(var6, var7, var6 + var14, var7 + var15) >= Res.gameAA(var6, var7, var21.x, var21.y) - 20) {
                  ++var17;
                  break;
               }

               ++var12;
            }
         }

         for(var17 = 0; var17 < this.list.length; ++var17) {
            var6 = this.list[var17].size();
            EPosition var18;
            (var18 = (EPosition)var1.elementAt(var17)).follow = (byte)(this.list[var17].size() - 1);
            var18.index = -1;
            (var19 = new EPosition(var18.x, var18.y, var18.follow)).index = -1;
            this.list[var17].addElement(var19);

            for(var9 = 1; var9 < var6; ++var9) {
               var10 = (EPosition)this.list[var17].elementAt(var9);
               var11 = Res.gameAD(2);

               for(var12 = 0; var12 < var11; ++var12) {
                  var23 = 180 + Res.gameAD(180);
                  var15 = (var14 = 5 + Res.gameAD(10)) * Res.gameAB(Res.gameAC(var23)) >> 10;
                  var23 = -(var14 * Res.gameAA(Res.gameAC(var23))) >> 10;
                  (var2 = new EPosition(var10.x + var15, var10.y + var23, var9)).index = 0;
                  this.list[var17].addElement(var2);
               }
            }
         }

      }
   }

   public final void update() {
      if (this.posangle == null) {
         Effect2.vRemoveEffect2.addElement(this);
      } else {
         try {
            if (GameCanvas.gameTick % 2 == 1) {
               this.posangle.follow = -1;
               this.posangle.index = -1;

               for(int var1 = 0; var1 < this.listPos.size(); ++var1) {
                  EPosition var2;
                  (var2 = (EPosition)this.listPos.elementAt(var1)).index = -1;
                  var2.follow = -1;
               }

               if (this.isContinue && this.isRemove && this.listPos.size() > 1 && System.currentTimeMillis() / 10L - this.timeDel > 30L) {
                  this.timeDel = System.currentTimeMillis() / 10L;
                  this.posangle = (EPosition)this.listPos.elementAt(0);
                  this.listPos.removeElementAt(0);
               }

               this.gameAA(this.listPos, this.posangle, this.isContinue);
               if (this.tick > 3) {
                  this.aa = 7;
                  Effect2.vEffect2.removeElement(this);
               }

               ++this.tick;
               return;
            }
         } catch (Exception var3) {
            var3.printStackTrace();
         }

      }
   }

   public final void paint(mGraphics var1) {
      this.dem = 0;
      ++this.cou;
      if (this.cou >= 12) {
         this.cou = 0;
      }

      if (this.list != null) {
         for(int var2 = 0; var2 < this.list.length; ++var2) {
            for(int var3 = 0; var3 < this.list[var2].size(); ++var3) {
               EPosition var4;
               if ((var4 = (EPosition)this.list[var2].elementAt(var3)).follow >= 0 && var4.follow < this.list[var2].size()) {
                  EPosition var5 = (EPosition)this.list[var2].elementAt(var4.follow);
                  if (GameCanvas.gameAE(var4.x, var4.y) && GameCanvas.gameAE(var5.x, var5.y)) {
                     var1.gameAA(this.color[0]);
                     var1.gameAA(var4.x, var4.y, var5.x, var5.y);
                     if (var4.index == -1) {
                        var1.gameAA(this.color[1]);
                        var1.gameAA(var4.x - 1, var4.y, var5.x - 1, var5.y);
                        if (this.isContinue && this.isRemove) {
                           var1.gameAA(var4.x + 1, var4.y, var5.x + 1, var5.y);
                        }
                     }
                  }

                  if (this.isContinue && this.isRemove) {
                     ++this.dem;
                     if (this.dem >= this.aa) {
                        this.aa += 7;
                        return;
                     }
                  }
               }
            }

            EPosition var8;
            ++(var8 = (EPosition)this.listPos.elementAt(var2)).cou;
            if (var8.cou >= 12) {
               var8.cou = 0;
            }
         }
      }

   }
}
