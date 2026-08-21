public final class CreateCharScr extends mScreen implements IActionListener {
   private static CreateCharScr instance;
   private static TField tAddName;
   private static int indexGender;
   private static int indexHair;
   private static int selected;
   private static int[][] hairID = new int[][]{{11, 26, 27, 28}, {2, 23, 24, 25}};
   private static int[] defaultLeg = new int[]{9, 0};
   private static int[] defaultBody = new int[]{10, 1};
   private Command cmdGender;
   private Command cmdHair;
   private int curIndex = 0;

   public static CreateCharScr gameAF() {
      if (instance == null) {
         instance = new CreateCharScr();
      }

      return instance;
   }

   public CreateCharScr() {
      if (GameCanvas.w == 128) {
         GameScr.gameAB(128, 120);
         GameScr.popupX = (GameCanvas.w - 128) / 2;
         GameScr.popupY = 0;
      } else {
         GameScr.gameAB(170, 190);
         GameScr.popupX = (GameCanvas.w - 170) / 2;
         GameScr.popupY = (GameCanvas.h - 220) / 2;
      }

      indexGender = 1;
      (tAddName = new TField()).name = mResources.gameFA;
      tAddName.width = 100;
      tAddName.x = GameScr.popupX + 45;
      tAddName.x = GameScr.popupX + GameScr.popupW / 2 - tAddName.width / 2;
      tAddName.y = GameScr.popupY + 62;
      if (GameCanvas.w == 128) {
         tAddName.width = 60;
      }

      tAddName.height = 26;
      tAddName.isFocus = true;
      tAddName.setIputType(0);
      indexGender = 1;
      indexHair = 0;
      super.center = new Command(mResources.gameEZ, this, 8000, (Object)null);
      super.left = new Command(mResources.gameFE, this, 8001, (Object)null);
      this.cmdGender = new Command("", this, 8002, (Object)null, GameScr.popupX + GameScr.popupW / 2 - mScreen.cmdW / 2, GameScr.popupY + 70 + 30);
      this.cmdHair = new Command("", this, 8003, (Object)null, GameScr.popupX + GameScr.popupW / 2 - mScreen.cmdW / 2, GameScr.popupY + 70 + 70);
      super.right = tAddName.cmdClear;
   }

   public final void update() {
      indexGender = GameCanvas.gameTick % 2;
      indexHair = GameCanvas.gameTick % 4;
      super.update();
   }

   public final void gameAA(int var1) {
      tAddName.gameAA(var1);
   }

   public final void gameAD() {
      if (++GameScr.cmx > GameCanvas.w * 3 + 100) {
         GameScr.cmx = 100;
      }

      if (GameCanvas.isTouch && GameCanvas.w >= 320) {
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
      }

      if (this.cmdHair != null) {
         this.cmdHair.gameAC();
      }

      if (this.cmdGender != null) {
         this.cmdGender.gameAC();
      }

   }

   public final void setOffset() {
      if (GameCanvas.keyPressedz[2] && --selected < 0) {
         selected = mResources.gameMT.length - 1;
      }

      if (GameCanvas.keyPressedz[8] && ++selected >= mResources.gameMT.length) {
         selected = 0;
      }

      if (selected == 0) {
         super.right = tAddName.cmdClear;
         tAddName.gameAC();
      }

      if (selected == 1) {
         if (GameCanvas.keyPressedz[4] && --indexGender < 0) {
            indexGender = mResources.gameMU.length - 1;
         }

         if (GameCanvas.keyPressedz[6] && ++indexGender > mResources.gameMU.length - 1) {
            indexGender = 0;
         }

         super.right = null;
      }

      if (selected == 2) {
         if (GameCanvas.keyPressedz[4] && --indexHair < 0) {
            indexHair = mResources.gameGF[0].length - 1;
         }

         if (GameCanvas.keyPressedz[6] && ++indexHair > mResources.gameGF[0].length - 1) {
            indexHair = 0;
         }
      }

      if (GameCanvas.isPointerJustRelease && GameCanvas.gameAB(GameScr.popupX + 5, GameScr.popupY + 65, GameScr.popupW - 5, mScreen.ITEM_HEIGHT)) {
         selected = 0;
      }

      super.setOffset();
      GameCanvas.gameAI();
      GameCanvas.gameAH();
   }

   public final void paint(mGraphics var1) {
      byte var2 = 0;

      try {
         GameCanvas.paint(var1);
         boolean var10 = true;
         Paint.gameAA(GameScr.popupX, GameScr.popupY, GameScr.popupW, GameScr.popupH, var1);
         byte var3 = 40;
         if (GameCanvas.w == 128) {
            var3 = 20;
         }

         var10 = true;
         int var4 = hairID[indexGender][indexHair];
         int var5 = defaultLeg[indexGender];
         int var6 = defaultBody[indexGender];
         var10 = true;
         Part var11 = GameScr.parts[var4];
         Part var12 = GameScr.parts[var5];
         Part var13 = GameScr.parts[var6];
         int var7 = GameCanvas.w / 2;
         int var8 = GameScr.popupY + 50;
         var10 = true;
         SmallImage.gameAA(var1, var12.pi[Char.CharInfo[0][1][0]].id, var7 + Char.CharInfo[0][1][1] + var12.pi[Char.CharInfo[0][1][0]].dx, var8 - Char.CharInfo[0][1][2] + var12.pi[Char.CharInfo[0][1][0]].dy, 0, 0);
         SmallImage.gameAA(var1, var13.pi[Char.CharInfo[0][2][0]].id, var7 + Char.CharInfo[0][2][1] + var13.pi[Char.CharInfo[0][2][0]].dx, var8 - Char.CharInfo[0][2][2] + var13.pi[Char.CharInfo[0][2][0]].dy, 0, 0);
         SmallImage.gameAA(var1, var11.pi[Char.CharInfo[0][0][0]].id, var7 + Char.CharInfo[0][0][1] + var11.pi[Char.CharInfo[0][0][0]].dx, var8 - Char.CharInfo[0][0][2] + var11.pi[Char.CharInfo[0][0][0]].dy, 0, 0);
         var10 = true;
         if (!GameCanvas.isTouch) {
            for(var4 = 0; var4 < mResources.gameMT.length; ++var4) {
               if (selected == var4) {
                  var5 = var4 > 0 ? -5 : 0;
                  SmallImage.gameAA(var1, 989, GameScr.popupX + 10 + (GameCanvas.gameTick % 7 > 3 ? 1 : 0), GameScr.popupY + 76 + var4 * var3 + var5, 2, StaticObj.VCENTER_HCENTER);
                  SmallImage.gameAA(var1, 989, GameScr.popupX + GameScr.popupW - 15 - (GameCanvas.gameTick % 7 > 3 ? 1 : 0), GameScr.popupY + 76 + var4 * var3 + var5, 0, StaticObj.VCENTER_HCENTER);
               }
            }
         }

         var10 = true;
         if (GameCanvas.isTouch) {
            this.cmdGender.caption = mResources.gameMU[indexGender];
            this.cmdGender.gameAA(var1);
            this.cmdHair.caption = mResources.gameGF[indexGender][indexHair];
            this.cmdHair.gameAA(var1);
         } else {
            Paint.gameAB(this.cmdGender.x, this.cmdGender.y, this.cmdGender.w, this.cmdGender.h, var1);
            if (selected == 1) {
               var1.gameAA(Paint.COLORLIGHT);
               var1.gameAC(this.cmdGender.x, this.cmdGender.y, this.cmdGender.w, this.cmdGender.h);
               Paint.gameAB(this.cmdGender.x, this.cmdGender.y, this.cmdGender.w, this.cmdGender.h, var1);
            }

            Paint.gameAB(this.cmdHair.x, this.cmdHair.y, this.cmdHair.w, this.cmdHair.h, var1);
            if (selected == 2) {
               var1.gameAA(Paint.COLORLIGHT);
               var1.gameAC(this.cmdHair.x, this.cmdHair.y, this.cmdHair.w, this.cmdHair.h);
               Paint.gameAB(this.cmdHair.x, this.cmdHair.y, this.cmdHair.w, this.cmdHair.h, var1);
            }

            mFont.tahoma_7b_white.gameAA(var1, mResources.gameMU[indexGender], GameScr.popupX + 85, GameScr.popupY + 66 + var3 * 1, 2);
            mFont.tahoma_7b_white.gameAA(var1, mResources.gameGF[indexGender][indexHair], GameScr.popupX + 85, GameScr.popupY + 66 + var3 * 2, 2);
         }

         tAddName.x = GameScr.popupX + GameScr.popupW / 2 - tAddName.width / 2;
         tAddName.y = GameScr.popupY + 62;
         tAddName.title_Null = mResources.gameFA;
         tAddName.paint(var1);
         super.paint(var1);
         var1.gameAA(0);
      } catch (Exception var9) {
         System.out.println("CreateCharScr.paint(): " + var2);
         var9.printStackTrace();
      }
   }

   public final void perform(int var1, Object var2) {
      switch(var1) {
      case 8000:
         Service.gI().createChar(tAddName.getText(), indexGender, hairID[indexGender][indexHair]);
         return;
      case 8001:
         if (SelectCharScr.gameAF().isNullChar) {
            GameCanvas.selectsvScr.update();
            return;
         }

         SelectCharScr.gameAF().update();
         return;
      case 8002:
         this.curIndex = 1;
         selected = 1;
         if (this.curIndex == selected && --indexGender < 0) {
            indexGender = mResources.gameMU.length - 1;
            return;
         }
         break;
      case 8003:
         this.curIndex = 2;
         selected = 2;
         if (this.curIndex == selected && ++indexHair > mResources.gameGF[0].length - 1) {
            indexHair = 0;
         }
      }

   }
}
