import javax.microedition.lcdui.Image;

public final class Menu implements IActionListener {
   public boolean showMenu;
   private MyVector menuItems;
   public int menuSelectedItem;
   private int gameAG;
   private int gameAH;
   private int gameAI;
   private int gameAJ;
   private int gameAK;
   private static int gameAL;
   private static int gameAM;
   private static int gameAN;
   private static int gameAO;
   private Command left;
   private Command right;
   private Command center;
   private static Image gameAS = GameCanvas.gameAC("/hd/btnlBig0.png");
   private static Image gameAT = GameCanvas.gameAC("/hd/btnlBig1.png");
   boolean gameAC;
   public boolean showbyServer;
   private int gameAU;
   private int gameAV;
   private int gameAW;
   private int[] gameAX;
   private boolean gameAY;
   private boolean gameAZ;
   private int gameBA;
   private int gameBB;
   public byte gameAE;
   private int gameBC;
   private int gameBD;

   public Menu() {
      this.left = new Command(mResources.gameEO, 0);
      this.right = GameCanvas.isTouch ? null : new Command(mResources.gameBH, 0, GameCanvas.w - 71, GameCanvas.h - mScreen.cmdH + 1);
      this.center = null;
      this.gameAX = new int[3];
   }
   
   public final void gameAAz(MyVector var1 , int pos) {
      this.gameAC = false;
      this.showbyServer = false;
      ChatPopup.currentMultilineChatPopup = null;
      InfoDlg.gameAB();
      if (var1.size() != 0) {
         this.menuItems = var1;
         this.gameAI = 60;
         this.gameAJ = 60;

         for(int var2 = 0; var2 < var1.size(); ++var2) {
            Command var3 = (Command)var1.elementAt(var2);
            if (mFont.tahoma_7_yellow.gameAA(var3.caption) > this.gameAI - 8) {
               var3.subCaption = mFont.tahoma_7_yellow.gameAB(var3.caption, this.gameAI - 8);
            }
         }

         this.gameAG = (GameCanvas.w - var1.size() * this.gameAI) / 2;
         if (this.gameAG < 1) {
            this.gameAG = 1;
         }

         this.gameAH = GameCanvas.h - this.gameAJ - (Paint.hTab + 1);
         if (GameCanvas.isTouch) {
            this.gameAH -= 3;
         }

         this.gameAK = this.gameAH;
         this.showMenu = true;
         this.menuSelectedItem = 0;
         if ((gameAN = this.menuItems.size() * this.gameAI - GameCanvas.w) < 0) {
            gameAN = 0;
         }

         gameAL = 0;
         gameAM = 0;
         gameAO = 50;
         this.gameAU = var1.size() * this.gameAI - 1;
         if (this.gameAU > GameCanvas.w - 2) {
            this.gameAU = GameCanvas.w - 2;
         }

         if (GameCanvas.isTouch) {
            this.menuSelectedItem = -1;
         }

      }
   }

   public final void gameAA(MyVector var1) {
      this.gameAC = false;
      this.showbyServer = false;
      ChatPopup.currentMultilineChatPopup = null;
      InfoDlg.gameAB();
      if (var1.size() != 0) {
         this.menuItems = var1;
         this.gameAI = 60;
         this.gameAJ = 60;

         for(int var2 = 0; var2 < var1.size(); ++var2) {
            Command var3 = (Command)var1.elementAt(var2);
            if (mFont.tahoma_7_yellow.gameAA(var3.caption) > this.gameAI - 8) {
               var3.subCaption = mFont.tahoma_7_yellow.gameAB(var3.caption, this.gameAI - 8);
            }
         }

         this.gameAG = (GameCanvas.w - var1.size() * this.gameAI) / 2;
         if (this.gameAG < 1) {
            this.gameAG = 1;
         }

         this.gameAH = GameCanvas.h - this.gameAJ - (Paint.hTab + 1);
         if (GameCanvas.isTouch) {
            this.gameAH -= 3;
         }

         this.gameAK = this.gameAH;
         this.showMenu = true;
         this.menuSelectedItem = 0;
         if ((gameAN = this.menuItems.size() * this.gameAI - GameCanvas.w) < 0) {
            gameAN = 0;
         }

         gameAL = 0;
         gameAM = 0;
         gameAO = 50;
         this.gameAU = var1.size() * this.gameAI - 1;
         if (this.gameAU > GameCanvas.w - 2) {
            this.gameAU = GameCanvas.w - 2;
         }

         if (GameCanvas.isTouch) {
            this.menuSelectedItem = -1;
         }

      }
   }

   public final void update() {
      if (this.showMenu) {
         if (this.gameAE > 0) {
            --this.gameAE;
         }

         boolean var1 = false;
         if (!GameCanvas.keyPressedz[2] && !GameCanvas.keyPressedz[4]) {
            if (!GameCanvas.keyPressedz[8] && !GameCanvas.keyPressedz[6]) {
               if (GameCanvas.keyPressedz[5]) {
                  if (this.center != null) {
                     if (this.center.idAction > 0) {
                        if (this.center.actionListener == GameScr.gI()) {
                           GameScr.gI().gameAB(this.center.idAction, this.center.p);
                        } else {
                           this.perform(this.center.idAction, this.center.p);
                        }
                     }
                  } else {
                     this.gameBA = 2;
                  }
               } else if (GameCanvas.keyPressedz[12]) {
                  if (this.left.idAction > 0) {
                     this.perform(this.left.idAction, this.left.p);
                  } else {
                     this.gameBA = 2;
                  }
               } else if (!this.gameAC && (GameCanvas.keyPressedz[13] || mScreen.gameAA(this.right))) {
                  this.showMenu = false;
                  InfoDlg.gameAB();
                  if (this.showbyServer) {
                     Service.gI().menu((byte)1, Char.getMyChar().npcFocus.template.npcTemplateId, GameCanvas.menu.menuSelectedItem, 0);
                     this.showbyServer = false;
                  }
               }
            } else {
               var1 = true;
               ++this.menuSelectedItem;
               if (this.menuSelectedItem > this.menuItems.size() - 1) {
                  this.menuSelectedItem = 0;
               }
            }
         } else {
            var1 = true;
            --this.menuSelectedItem;
            if (this.menuSelectedItem < 0) {
               this.menuSelectedItem = this.menuItems.size() - 1;
            }
         }

         this.center = null;
         if (GameScr.gameDM && !GameCanvas.isTouch) {
            ChatTab var2 = null;
            Command var3 = null;
            if (this.menuSelectedItem != -1) {
               var3 = (Command)this.menuItems.elementAt(this.menuSelectedItem);
               if ((var2 = ChatManager.gameAD().gameAA(var3.caption)) != null && var2.type == 2) {
                  this.center = new Command(mResources.gameBI, this, 1000, var2);
               }
            }
         }

         if (var1) {
            if ((gameAL = this.menuSelectedItem * this.gameAI + this.gameAI - GameCanvas.w / 2) > gameAN) {
               gameAL = gameAN;
            }

            if (gameAL < 0) {
               gameAL = 0;
            }

            if (this.menuSelectedItem == this.menuItems.size() - 1 || this.menuSelectedItem == 0) {
               gameAM = gameAL;
            }
         }

         if (this.gameAE <= 0 && !this.gameAC && GameCanvas.isPointerJustRelease && !GameCanvas.gameAC(this.gameAG, this.gameAH, this.gameAU, this.gameAJ) && !this.gameAY) {
            this.gameAV = this.gameAW = 0;
            this.gameAY = false;
            this.showMenu = false;
            GameCanvas.isPointerJustRelease = false;
            if (this.showbyServer) {
               Service.gI().menu((byte)1, Char.getMyChar().npcFocus.template.npcTemplateId, GameCanvas.menu.menuSelectedItem, 0);
               this.showbyServer = false;
            }

         } else {
            int var4;
            int var5;
            if (GameCanvas.isPointerDown) {
               if (!this.gameAY && GameCanvas.gameAC(this.gameAG, this.gameAH, this.gameAU, this.gameAJ)) {
                  for(var4 = 0; var4 < this.gameAX.length; ++var4) {
                     this.gameAX[0] = GameCanvas.px;
                  }

                  this.gameAW = GameCanvas.px;
                  this.gameAY = true;
                  this.gameAZ = this.gameBB != 0;
                  this.gameBB = 0;
               } else if (this.gameAY) {
                  ++this.gameAV;
                  if (this.gameAV > 5 && this.gameAW == GameCanvas.px && !this.gameAZ) {
                     this.gameAW = -1000;
                     this.menuSelectedItem = (gameAL + GameCanvas.px - this.gameAG) / this.gameAI;
                  }

                  if ((var4 = GameCanvas.px - this.gameAX[0]) != 0 && this.menuSelectedItem != -1) {
                     this.menuSelectedItem = -1;
                  }

                  for(var5 = this.gameAX.length - 1; var5 > 0; --var5) {
                     this.gameAX[var5] = this.gameAX[var5 - 1];
                  }

                  this.gameAX[0] = GameCanvas.px;
                  if ((gameAL -= var4) < 0) {
                     gameAL = 0;
                  }

                  if (gameAL > gameAN) {
                     gameAL = gameAN;
                  }

                  if (gameAM < 0 || gameAM > gameAN) {
                     var4 /= 2;
                  }

                  gameAM -= var4;
               }
            }

            if (GameCanvas.isPointerJustRelease && this.gameAY) {
               var4 = GameCanvas.px - this.gameAX[0];
               GameCanvas.isPointerJustRelease = false;
               if (Res.abs(var4) < 20 && Res.abs(GameCanvas.px - this.gameAW) < 20 && !this.gameAZ) {
                  this.gameBB = 0;
                  gameAL = gameAM;
                  this.gameAW = -1000;
                  this.menuSelectedItem = (gameAL + GameCanvas.px - this.gameAG) / this.gameAI;
                  this.gameAV = 0;
                  this.gameBA = 10;
               } else if (this.menuSelectedItem != -1 && this.gameAV > 5) {
                  this.gameAV = 0;
                  this.gameBA = 1;
               } else if (this.menuSelectedItem == -1 && !this.gameAZ) {
                  if (gameAM < 0) {
                     gameAL = 0;
                  } else if (gameAM > gameAN) {
                     gameAL = gameAN;
                  } else {
                     byte var6;
                     if ((var5 = GameCanvas.px - this.gameAX[0] + (this.gameAX[0] - this.gameAX[1]) + (this.gameAX[1] - this.gameAX[2])) > 10) {
                        var6 = 10;
                     } else if (var5 < -10) {
                        var6 = -10;
                     } else {
                        var6 = 0;
                     }

                     this.gameBB = -var6 * 100;
                  }
               }

               this.gameAY = false;
               this.gameAV = 0;
               GameCanvas.isPointerJustRelease = false;
            }

            GameCanvas.gameAH();
            GameCanvas.gameAI();
         }
      }
   }

   public final void paint(mGraphics var1) {
      try {
         var1.gameAA(-var1.gameAA(), -var1.gameAB());
         var1.gameAA(-gameAM, 0);
         int var2;
         String[] var3;
         int var4;
         int var5;
         if (GameCanvas.isTouch) {
            for(var2 = 0; var2 < this.menuItems.size(); ++var2) {
               if (var2 == this.menuSelectedItem) {
                  var1.gameAA(gameAT, this.gameAG + var2 * this.gameAI + 1, this.gameAK + 1, 0);
               } else {
                  var1.gameAA(gameAS, this.gameAG + var2 * this.gameAI + 1, this.gameAK + 1, 0);
               }

               if ((var3 = ((Command)this.menuItems.elementAt(var2)).subCaption) == null) {
                  var3 = new String[]{((Command)this.menuItems.elementAt(var2)).caption};
               }

               var4 = this.gameAK + (this.gameAJ - var3.length * 14) / 2 + 1;

               for(var5 = 0; var5 < var3.length; ++var5) {
                  if (GameScr.gameDM) {
                     if (ChatManager.gameAD().gameAD(var3[var5])) {
                        if (GameCanvas.gameTick % 10 > 5) {
                           mFont.tahoma_7_red.gameAA(var1, var3[var5], this.gameAG + var2 * this.gameAI + this.gameAI / 2 - 2, var4 + var5 * 14, 2);
                        } else {
                           mFont.tahoma_7_yellow.gameAA(var1, var3[var5], this.gameAG + var2 * this.gameAI + this.gameAI / 2 - 2, var4 + var5 * 14, 2);
                        }
                     } else {
                        mFont.tahoma_7_yellow.gameAA(var1, var3[var5], this.gameAG + var2 * this.gameAI + this.gameAI / 2 - 2, var4 + var5 * 14, 2);
                     }
                  } else {
                     mFont.tahoma_7_yellow.gameAA(var1, var3[var5], this.gameAG + var2 * this.gameAI + this.gameAI / 2 - 2, var4 + var5 * 14, 2);
                  }
               }
            }
         } else {
            for(var2 = 0; var2 < this.menuItems.size(); ++var2) {
               if (var2 == this.menuSelectedItem) {
                  var1.gameAA(gameAT, this.gameAG + var2 * this.gameAI + 1, this.gameAK + 1 - 23, 0);
               } else {
                  var1.gameAA(gameAS, this.gameAG + var2 * this.gameAI + 1, this.gameAK + 1 - 23, 0);
               }

               if ((var3 = ((Command)this.menuItems.elementAt(var2)).subCaption) == null) {
                  var3 = new String[]{((Command)this.menuItems.elementAt(var2)).caption};
               }

               var4 = this.gameAK + (this.gameAJ - var3.length * 14) / 2 + 1 - 23;

               for(var5 = 0; var5 < var3.length; ++var5) {
                  if (GameScr.gameDM) {
                     if (ChatManager.gameAD().gameAD(var3[var5])) {
                        if (GameCanvas.gameTick % 10 > 5) {
                           mFont.tahoma_7_red.gameAA(var1, var3[var5], this.gameAG + var2 * this.gameAI + this.gameAI / 2 - 2, var4 + var5 * 14, 2);
                        } else {
                           mFont.tahoma_7_yellow.gameAA(var1, var3[var5], this.gameAG + var2 * this.gameAI + this.gameAI / 2 - 2, var4 + var5 * 14, 2);
                        }
                     } else {
                        mFont.tahoma_7_yellow.gameAA(var1, var3[var5], this.gameAG + var2 * this.gameAI + this.gameAI / 2 - 2, var4 + var5 * 14, 2);
                     }
                  } else {
                     mFont.tahoma_7_yellow.gameAA(var1, var3[var5], this.gameAG + var2 * this.gameAI + this.gameAI / 2 - 2, var4 + var5 * 14, 2);
                  }
               }
            }
         }

         var1.gameAA(-var1.gameAA(), -var1.gameAB());
      } catch (Exception var6) {
         var6.printStackTrace();
      }
   }

   public final void gameAB() {
      if (this.gameBB != 0 && !this.gameAY) {
         if ((gameAL += this.gameBB / 100) < 0) {
            gameAL = 0;
         } else if (gameAL > gameAN) {
            gameAL = gameAN;
         } else {
            gameAM = gameAL;
         }

         this.gameBB = this.gameBB * 9 / 10;
         if (this.gameBB < 100 && this.gameBB > -100) {
            this.gameBB = 0;
         }
      }

      if (gameAM != gameAL && !this.gameAY) {
         this.gameBC = gameAL - gameAM << 2;
         this.gameBD += this.gameBC;
         gameAM += this.gameBD >> 4;
         this.gameBD &= 15;
      }

      if (this.gameAK > this.gameAH) {
         int var1;
         if ((var1 = this.gameAK - this.gameAH >> 1) < 1) {
            var1 = 1;
         }

         this.gameAK -= var1;
      }

      if (gameAO != 0 && (gameAO >>= 1) < 0) {
         gameAO = 0;
      }

      if (this.gameBA > 0) {
         --this.gameBA;
         GameScr.gameDM = false;
         if (this.gameBA == 0) {
            this.showMenu = false;
            Command var2;
            if (this.menuSelectedItem >= 0 && (var2 = (Command)this.menuItems.elementAt(this.menuSelectedItem)) != null) {
               var2.gameAA();
            }
         }
      }

   }

   public final void perform(int var1, Object var2) {
      if (var1 == 1000) {
         ChatTab var4 = (ChatTab)var2;
         this.menuItems.removeAllElements();
         ChatManager.gameAD().gameAE(var4.ownerName);
         ChatManager.gameAD().chatTabs.removeElement(var4);

         for(var1 = 0; var1 < ChatManager.gameAD().chatTabs.size(); ++var1) {
            ChatTab var5 = (ChatTab)ChatManager.gameAD().chatTabs.elementAt(var1);
            this.menuItems.addElement(new Command(var5.ownerName, (IActionListener)null, 12001, new Integer(var1)));
         }

         this.menuItems.addElement(new Command(mResources.gameUQ, (IActionListener)null, 12006, (Object)null));
         this.menuItems.addElement(new Command(mResources.gameUR, (IActionListener)null, 12008, (Object)null));

         for(var1 = 0; var1 < this.menuItems.size(); ++var1) {
            Command var6 = (Command)this.menuItems.elementAt(var1);
            if (mFont.tahoma_7_yellow.gameAA(var6.caption) > this.gameAI - 8) {
               var6.subCaption = mFont.tahoma_7_yellow.gameAB(var6.caption, this.gameAI - 8);
            }
         }

         gameAN = this.menuItems.size() * this.gameAI - GameCanvas.w;
         if ((gameAL = this.menuSelectedItem * this.gameAI + this.gameAI - GameCanvas.w / 2) > gameAN) {
            gameAL = gameAN;
         }

         if (gameAL < 0) {
            gameAL = 0;
         }

         if (this.menuSelectedItem == this.menuItems.size() - 1 || this.menuSelectedItem == 0) {
            gameAM = gameAL;
         }
      }

   }
}
