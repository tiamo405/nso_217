public final class Readmsg {
   public final void gameAA(Message var1) {
      try {
         short var5;
         Message var20;
         int var22;
         Char var23;
         switch(var1.reader().readByte()) {
         case 0:
            var20 = var1;

            try {
               var1 = null;
               var22 = var20.reader().readInt();
               if (Char.getMyChar().charID == var22) {
                  var23 = Char.getMyChar();
               } else {
                  var23 = GameScr.gameAE(var22);
               }

               if (var23 != null) {
                  var23.arrItemViThu = new Item[5];

                  for(int var25 = 0; var25 < var23.arrItemViThu.length; ++var25) {
                     short var27;
                     if ((var27 = var20.reader().readShort()) > -1) {
                        var23.arrItemViThu[var25] = new Item();
                        var23.arrItemViThu[var25].indexUI = var25;
                        var23.arrItemViThu[var25].typeUI = 51;
                        var23.arrItemViThu[var25].template = ItemTemplates.gameAA(var27);
                        var23.arrItemViThu[var25].upgrade = var20.reader().readByte();
                        var23.arrItemViThu[var25].expires = var20.reader().readLong();
                        var23.arrItemViThu[var25].sys = var20.reader().readByte();
                        byte var28 = var20.reader().readByte();
                        var23.arrItemViThu[var25].options = new MyVector();

                        for(int var30 = 0; var30 < var28; ++var30) {
                           var23.arrItemViThu[var25].options.addElement(new ItemOption(var20.reader().readUnsignedByte(), var20.reader().readInt()));
                        }
                     }
                  }
               }
               break;
            } catch (Exception var16) {
               return;
            }
         case 1:
            var20 = var1;

            try {
               var1 = null;
               var22 = var20.reader().readInt();
               if (Char.getMyChar().charID == var22) {
                  var23 = Char.getMyChar();
               } else {
                  var23 = GameScr.gameAE(var22);
               }

               if (var23 != null) {
                  short var24 = var20.reader().readShort();
                  byte var26 = var20.reader().readByte();
                  if (var24 > 0) {
                     var5 = (short)var23.cx;
                     short var29 = (short)(var23.cy - 40);
                     var23.mobVithu = new Mob((short)-1, false, false, false, false, false, var24, 1, 0, 0, 0, var5, var29, (byte)4, (byte)0, var26 != 0, false);
                     var23.mobVithu.status = 5;
                  } else {
                     var23.mobVithu = null;
                  }
               }
               break;
            } catch (Exception var15) {
               var15.printStackTrace();
               return;
            }
         case 2:
            var1 = var1;

            try {
               short var2 = var1.reader().readShort();
               String var3 = var1.reader().readUTF();
               int var4 = var1.reader().readInt();
               var5 = var1.reader().readShort();
               byte var6 = var1.reader().readByte();
               if (var4 >= 0) {
                  short var21 = var2;
                  int var13 = 0;

                  CharCountDown var10000;
                  while(true) {
                     if (var13 >= GameScr.gameAX.size()) {
                        var10000 = null;
                        break;
                     }

                     CharCountDown var14;
                     if ((var14 = (CharCountDown)GameScr.gameAX.elementAt(var13)) != null && var14.gameAD == var21) {
                        var10000 = var14;
                        break;
                     }

                     ++var13;
                  }

                  CharCountDown var19 = var10000;
                  if (var19 == null) {
                     var19 = new CharCountDown(var2, var5, (long)var4, var3, var6);
                     GameScr.gameAX.addElement(var19);
                  }

                  if (var19 != null) {
                     var19.gameAB = var3;
                     var19.gameAE = var5;
                     long var31 = (long)var4;
                     var19.gameAA = System.currentTimeMillis() + var31 * 1000L;
                     if (var6 == -2) {
                        var19.WantDestroy = true;
                        break;
                     }
                  }
               }
            } catch (Exception var17) {
            }

            return;
         default:
            return;
         }
      } catch (Exception var18) {
      }

   }
}
