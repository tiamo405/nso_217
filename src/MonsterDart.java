import java.util.Vector;

public final class MonsterDart extends Effect2 {
   private int angle;
   private int vx;
   private int vy;
   private short va = 256;
   private int x;
   private int y;
   private int life;
   private boolean isSpeedUp = false;
   private int dame;
   private int dameMp;
   private int typeAtt;
   private Char c;
   private BuNhin b;
   private boolean isBoss;
   private int idBoss;
   private int countangle = 1;
   private static int level;
   private static Vector vDomsang = new Vector();
   private static int regMirro = 0;
   private int frame = 0;
   private int index;
   private static byte[] FRAME = new byte[]{0, 1, 2, 1, 0, 1, 2, 1, 0, 1, 2, 1, 0, 1, 2, 1, 0, 1, 2, 1, 0, 1, 2, 1, 0};
   private static int[] TRANSFORM = new int[]{0, 0, 0, 7, 6, 6, 6, 2, 2, 3, 3, 4, 5, 5, 5, 1};
   private static int[] ARROWINDEX = new int[]{0, 15, 37, 52, 75, 105, 127, 142, 165, 195, 217, 232, 255, 285, 307, 322, 345, 370};

   private void gameAA(int var1) {
      this.angle = var1;
      this.vx = this.va * Res.gameAB(var1) >> 10;
      this.vy = this.va * Res.gameAA(var1) >> 10;
   }

   public static void gameAA(int var0, int var1, boolean var2, short var3, int var4, int var5, int var6, Char var7) {
      level = var3;
      Effect2.vEffect2.addElement(new MonsterDart(var0, var1, var2, var3, var4, var5, var6, var7));
   }

   public static void gameAA(int var0, int var1, BuNhin var2) {
      Effect2.vEffect2.addElement(new MonsterDart(var0, var1, var2));
   }

   private MonsterDart(int var1, int var2, boolean var3, short var4, int var5, int var6, int var7, Char var8) {
      this.typeAtt = 0;
      this.x = var1;
      this.y = var2;
      this.isBoss = var3;
      this.idBoss = var5;
      this.dame = var6;
      this.dameMp = var7;
      this.c = var8;
      if (var3) {
         this.gameAA(this.countangle * 90);
         ++this.countangle;
         if (this.countangle > 3) {
            this.countangle = 1;
            return;
         }
      } else {
         if (var1 > var8.cx) {
            this.gameAA(240);
            return;
         }

         this.gameAA(300);
      }

   }

   private MonsterDart(int var1, int var2, BuNhin var3) {
      this.typeAtt = 1;
      this.x = var1;
      this.y = var2;
      this.b = var3;
      if (var1 > var3.x) {
         this.gameAA(240);
      } else {
         this.gameAA(300);
      }

      this.va = 256;
      this.angle = 180;
      this.vx = this.va * Res.gameAB(this.angle) >> 10;
      this.vy = this.va * Res.gameAA(this.angle) >> 10;
   }

   public final void update() {
      Domsang var1;
      if (level > 100) {
         var1 = new Domsang(this.x, this.y, 2);
         vDomsang.addElement(var1);
      } else if (level > 50 && level <= 100) {
         var1 = new Domsang(this.x, this.y, 5);
         vDomsang.addElement(var1);
      } else if (level > 30 && level <= 50) {
         var1 = new Domsang(this.x, this.y, 4);
         vDomsang.addElement(var1);
      } else if (level > 0 && level <= 30) {
         var1 = new Domsang(this.x, this.y, 3);
         vDomsang.addElement(var1);
      }

      int var2;
      int var3;
      if (this.c != null) {
         var2 = this.c.cx - this.x;
         var3 = this.c.cy - (this.c.ch >> 1) - this.y;
         ++this.life;
         if ((Res.abs(var2) >= 16 || Res.abs(var3) >= 16) && this.life <= 60) {
            int var4;
            if (Math.abs((var4 = Res.gameAA(var2, var3)) - this.angle) < 90 || var2 * var2 + var3 * var3 > 4096) {
               if (Math.abs(var4 - this.angle) < 15) {
                  this.angle = var4;
               } else if ((var4 - this.angle < 0 || var4 - this.angle >= 180) && var4 - this.angle >= -180) {
                  this.angle = Res.gameAC(this.angle - 15);
               } else {
                  this.angle = Res.gameAC(this.angle + 15);
               }
            }

            if (this.va < 8192) {
               this.va = (short)(this.va + 1024);
            }

            this.vx = this.va * Res.gameAB(this.angle) >> 10;
            this.vy = this.va * Res.gameAA(this.angle) >> 10;
            var2 = var2 + this.vx >> 10;
            this.x += var2;
            var3 = var3 + this.vy >> 10;
            this.y += var3;
            var2 = Res.gameAA(var2, -var3);
            var3 = 0;

            int var10001;
            while(true) {
               if (var3 >= ARROWINDEX.length - 1) {
                  var10001 = 0;
                  break;
               }

               if (var2 >= ARROWINDEX[var3] && var2 <= ARROWINDEX[var3 + 1]) {
                  var10001 = var3 >= 16 ? 0 : var3;
                  break;
               }

               ++var3;
            }

            this.index = var10001;
            this.frame = FRAME[this.index];
            regMirro = TRANSFORM[this.index];
         }
      }

      int var6 = 0;
      var2 = 0;
      if (this.typeAtt != 0) {
         if (this.typeAtt == 1) {
            var6 = this.b.x - this.x;
            var2 = this.b.y - 10 - this.y;
            ++this.life;
            if (Res.abs(var6) < 16 && Res.abs(var2) < 16 || this.life > 60) {
               this.b.isInjure = true;
               Effect2.vEffect2.removeElement(this);
               return;
            }
         }
      } else {
         var6 = this.c.cx - this.x;
         var2 = this.c.cy - 10 - this.y;
         ++this.life;
         if (this.c.statusMe == 5 || this.c.statusMe == 14) {
            this.x += (this.c.cx - this.x) / 2;
            this.y += (this.c.cy - this.y) / 2;
         }

         if (Res.abs(var6) < 16 && Res.abs(var2) < 16 || this.life > 60) {
            this.c.gameAA(this.dame, this.dameMp, this.isBoss, this.idBoss);
            Effect2.vEffect2.removeElement(this);
            return;
         }
      }

      if (Math.abs((var3 = Res.gameAA(var6, var2)) - this.angle) < 90 || var6 * var6 + var2 * var2 > 4096) {
         if (Math.abs(var3 - this.angle) < 15) {
            this.angle = var3;
         } else if ((var3 - this.angle < 0 || var3 - this.angle >= 180) && var3 - this.angle >= -180) {
            this.angle = Res.gameAC(this.angle - 15);
         } else {
            this.angle = Res.gameAC(this.angle + 15);
         }
      }

      var6 = var6 + this.vx >> 10;
      this.x += var6;
      var6 = var2 + this.vy >> 10;
      this.y += var6;

      for(int var5 = 0; var5 < vDomsang.size(); ++var5) {
         (var1 = (Domsang) vDomsang.elementAt(var5)).gameAA();
         if (var1.frame > 3) {
            vDomsang.removeElementAt(var5);
         }
      }

   }

   public final void paint(mGraphics var1) {
      if (this.isBoss) {
         byte var2 = 7;
         int var4 = GameCanvas.gameTick % var2;
         if (this.idBoss == 114) {
            if (var4 < 4) {
               SmallImage.gameAA(var1, 1299, this.x, this.y, 0, 3);
               return;
            }

            SmallImage.gameAA(var1, 1307, this.x, this.y, 0, 3);
            return;
         }

         if (this.idBoss == 115) {
            var2 = 20;
            if ((var4 = GameCanvas.gameTick % var2) < 4) {
               SmallImage.gameAA(var1, 1379, this.x, this.y, 0, 3);
               return;
            }

            if (var4 < 8) {
               SmallImage.gameAA(var1, 1380, this.x, this.y, 0, 3);
               return;
            }

            if (var4 < 12) {
               SmallImage.gameAA(var1, 1379, this.x, this.y, 0, 3);
               return;
            }

            if (var4 < 16) {
               SmallImage.gameAA(var1, 1382, this.x, this.y, 0, 3);
               return;
            }
         } else if (this.idBoss == 116) {
            var2 = 17;
            if ((var4 = GameCanvas.gameTick % var2) < 4) {
               SmallImage.gameAA(var1, 1399, this.x, this.y, 0, 3);
               return;
            }

            if (var4 < 8) {
               SmallImage.gameAA(var1, 1400, this.x, this.y, 0, 3);
               return;
            }

            if (var4 < 12) {
               SmallImage.gameAA(var1, 1401, this.x, this.y, 0, 3);
               return;
            }

            if (var4 < 16) {
               SmallImage.gameAA(var1, 1402, this.x, this.y, 0, 3);
               return;
            }
         } else if (this.idBoss == 139) {
            var2 = 20;
            if ((var4 = GameCanvas.gameTick % var2) < 4) {
               SmallImage.gameAA(var1, 1459, this.x, this.y, 0, 3);
               return;
            }

            if (var4 < 8) {
               SmallImage.gameAA(var1, 1380, this.x, this.y, 0, 3);
               return;
            }

            if (var4 < 12) {
               SmallImage.gameAA(var1, 1461, this.x, this.y, 0, 3);
               return;
            }

            if (var4 < 16) {
               SmallImage.gameAA(var1, 1382, this.x, this.y, 0, 3);
               return;
            }
         } else if (this.idBoss == 144 || this.idBoss == 163) {
            var2 = 20;
            if ((var4 = GameCanvas.gameTick % var2) < 4) {
               SmallImage.gameAA(var1, 1459, this.x, this.y, 0, 3);
               return;
            }

            if (var4 < 8) {
               SmallImage.gameAA(var1, 1380, this.x, this.y, 0, 3);
               return;
            }

            if (var4 < 12) {
               SmallImage.gameAA(var1, 1461, this.x, this.y, 0, 3);
               return;
            }

            if (var4 < 16) {
               SmallImage.gameAA(var1, 1382, this.x, this.y, 0, 3);
               return;
            }
         }
      } else {
         if (level > 100) {
            var1.gameAA(GameScr.gameFS, 0, this.frame * 23, 31, 23, regMirro, this.x, this.y, 0);
         } else if (level > 50 && level <= 100) {
            var1.gameAA(GameScr.gameFS, 0, 0, 14, 14, regMirro, this.x, this.y, 0);
         } else if (level > 30 && level <= 50) {
            var1.gameAA(GameScr.gameFS, 0, 0, 8, 8, regMirro, this.x, this.y, 0);
         } else if (level > 0 && level <= 30) {
            var1.gameAA(GameScr.gameFS, 0, 0, 8, 8, regMirro, this.x, this.y, 0);
         }

         for(int var3 = 0; var3 < vDomsang.size(); ++var3) {
            Domsang var5;
            if ((var5 = (Domsang) vDomsang.elementAt(var3)) != null) {
               var5.gameAA(var1);
            }
         }
      }

   }
}
