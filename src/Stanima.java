import java.util.Calendar;

public final class Stanima extends Auto {
   private boolean fieldBA;
   private boolean fieldBB;
   private int fieldBC;
   private boolean fieldBD;
   public int fieldAV;
   private int fieldBE;
   private int fieldBF;
   private boolean fieldBG;
   private int fieldBH;
   private long fieldBI;
   public int fieldAW;
   private long fieldBJ;
   public static boolean fieldAX;
   public static boolean fieldAY;
   public static boolean fieldAZ;

   public final void fieldAA(int var1, int var2, int var3, boolean var4, boolean var5) {
      super.fieldAD();
      super.fieldAB = var2;
      super.fieldAC = var3;
      this.fieldAW = var1;
      this.fieldBA = var4;
      this.fieldBB = var5;
      this.fieldAV = 0;
      this.fieldBI = 0L;
      this.fieldBD = true;
      this.fieldBC = -1;
      Calendar var6 = Res.fieldAB();
      this.fieldBH = var6.get(11) < 2 ? var6.get(5) - 1 : var6.get(5);
   }

   public final void fieldAA(String var1) {
      if (Code.fieldAH != null && !TileMap.isLangCo(super.fieldAB)) {
         if (var1.startsWith("Thần thú đã xuất hiện tại")) {
            if (Char.getMyChar().cName.equals(Code.fieldAH)) {
               int var2 = Char.getMyChar().clevel;
               MyVector var3 = new MyVector();
               MyVector var4 = new MyVector();
               if (var2 >= 40 && var2 <= 60) {
                  if (var1.indexOf("Đảo Hebi") > 0) {
                     var3.addElement(new Integer(34));
                     var4.addElement(new Integer(32));
                  }

                  if (var1.indexOf("Hang Meiro") > 0) {
                     var3.addElement(new Integer(35));
                     var4.addElement(new Integer(32));
                  }

                  if (var1.indexOf("Rừng Kappa") > 0) {
                     var3.addElement(new Integer(52));
                     var4.addElement(new Integer(48));
                  }

                  if (var1.indexOf("Rừng Aokigahara") > 0) {
                     var3.addElement(new Integer(14));
                     var4.addElement(new Integer(10));
                  }

                  if (var1.indexOf("Rừng Aokigahara") > 0) {
                     var3.addElement(new Integer(14));
                     var4.addElement(new Integer(10));
                  }

                  if (var1.indexOf("Vách núi Ito") > 0) {
                     var3.addElement(new Integer(15));
                     var4.addElement(new Integer(10));
                  }

                  if (var1.indexOf("Núi Anzen") > 0) {
                     var3.addElement(new Integer(68));
                     var4.addElement(new Integer(38));
                  }

                  if (var1.indexOf("Thung lũng Taira") > 0) {
                     var3.addElement(new Integer(16));
                     var4.addElement(new Integer(10));
                  }
               }

               if (var2 >= 50 && var2 <= 70) {
                  if (var1.indexOf("Núi Ontake") > 0) {
                     var3.addElement(new Integer(67));
                     var4.addElement(new Integer(38));
                  }

                  if (var1.indexOf("Đỉnh Okama") > 0) {
                     var3.addElement(new Integer(44));
                     var4.addElement(new Integer(43));
                  }
               }

               if (var2 >= 60 && var2 <= 80) {
                  if (var1.indexOf("Khu đá đỏ Akai") > 0) {
                     var3.addElement(new Integer(41));
                     var4.addElement(new Integer(43));
                  }

                  if (var1.indexOf("Mũi Hone") > 0) {
                     var3.addElement(new Integer(59));
                     var4.addElement(new Integer(38));
                  }

                  if (var1.indexOf("Đỉnh Ichidai") > 0) {
                     var3.addElement(new Integer(24));
                     var4.addElement(new Integer(38));
                  }

                  if (var1.indexOf("Hang núi Kurai") > 0) {
                     var3.addElement(new Integer(45));
                     var4.addElement(new Integer(43));
                  }
               }

               if (var2 >= 70 && var2 <= 100) {
                  if (var1.indexOf("Ngôi đền Orochi") > 0) {
                     var3.addElement(new Integer(19));
                     var4.addElement(new Integer(17));
                  }

                  if (var1.indexOf("Đồng Kisei") > 0) {
                     var3.addElement(new Integer(36));
                     var4.addElement(new Integer(38));
                  }

                  if (var1.indexOf("Đền Harumoto") > 0) {
                     var3.addElement(new Integer(54));
                     var4.addElement(new Integer(43));
                  }
               }

               if (var2 >= 90) {
                  if (var1.indexOf("Sinh Tử Kiều") > 0) {
                     var3.addElement(new Integer(143));
                     var4.addElement(new Integer(this.fieldBC));
                  }

                  if (var1.indexOf("Đoạn Sơn") > 0) {
                     var3.addElement(new Integer(141));
                     var4.addElement(new Integer(this.fieldBC));
                  }
               }

               if (var3.size() > 0) {
                  Code.fieldAA((Auto)(new PKBossS(var3, var4, this.fieldBC)));
                  return;
               }
            } else {
               Code.fieldAA((Auto)(new PKBossS(this.fieldBC)));
            }
         }

      }
   }

   public final void fieldAM() {
      if (this.fieldAV == 0) {
         this.fieldBH = Res.fieldAB().get(5);
         if (!TileMap.isLangCo(super.fieldAB)) {
            this.fieldAV = 2;
            this.fieldBJ = System.currentTimeMillis();
            this.fieldBE = super.fieldAB;
            this.fieldBF = super.fieldAC;
            this.fieldBG = super.fieldAA;
            this.fieldBD = false;
            fieldAY = false;
            fieldAZ = false;
            return;
         }
      } else {
         if (this.fieldAV == 1) {
            this.fieldAV = 3;
            this.fieldBJ = System.currentTimeMillis();
            this.fieldBD = false;
            return;
         }

         if (this.fieldAV == 2) {
            this.fieldAV = 1;
            this.fieldBJ = System.currentTimeMillis();
            fieldAX = false;
            return;
         }

         if (this.fieldAV == 3) {
            this.fieldAV = 0;
            this.fieldBD = false;
            Code.fieldAD();
            return;
         }

         if (this.fieldAV == 4) {
            this.fieldAV = 0;
            this.fieldBD = false;
         }
      }

   }

   public final void fieldAE() {
      this.fieldBI = 0L;
      super.fieldAE();
   }

   public final boolean fieldAN() {
      return Code.fieldAB instanceof Stanima && this.fieldAV == 2 && Char.getMyChar().clevel >= 60 && Char.getMyChar().clevel < 70;
   }

   public final void fieldAA() {
      if (Auto.fieldAF()) {
         Auto.fieldAA(true);
      } else {
         Calendar var1;
         int var2 = (var1 = Res.fieldAB()).get(5);
         int var3 = var1.get(11);
         int var4 = var1.get(12);
         switch(this.fieldAV) {
         case 0:
            if (!this.fieldBD && this.fieldBC > 0) {
               if (TileMap.mapID != this.fieldBC) {
                  this.fieldAA(this.fieldBC, -2, -1, -1);
                  return;
               }

               GameScr.fieldAB(5, 1, 0);
               if (LockGame.fieldAY()) {
                  this.fieldBD = true;
                  super.fieldAB = this.fieldBE;
                  super.fieldAC = this.fieldBF;
                  super.fieldAA = this.fieldBG;
                  super.fieldAD = false;
                  return;
               }

               return;
            }

            if (this.fieldBC < 0 && (TileMap.isLang(TileMap.mapID) || TileMap.isTruong(TileMap.mapID))) {
               this.fieldBC = TileMap.mapID;
            }

            if (var2 == this.fieldBH || var3 < 2 || var4 < 30) {
               if (super.fieldAB != TileMap.mapID || !super.fieldAD && super.fieldAC != TileMap.zoneID) {
                  this.fieldAA(super.fieldAB, super.fieldAC, super.fieldAE, super.fieldAF);
                  return;
               }

               if (Char.fieldEM && Code.fieldAJ() && Char.fieldBF() < 5 && !TileMap.isLangCo(TileMap.mapID)) {
                  Auto.fieldAH();
                  return;
               }

               if (!this.fieldBB && !this.fieldBA) {
                  this.fieldAB(this.fieldAW, this.fieldAA(Char.fieldFD, Char.fieldFE, Char.fieldFF));
                  this.fieldAC(-1);
               } else {
                  Char var12 = Char.getMyChar();
                  Char var8 = GameScr.vParty.size() > 0 ? ((Party)GameScr.vParty.firstElement()).c : null;
                  Skill var15;
                  if (this.fieldBB && GameScr.vParty.size() > 0 && var12.nClass.classId == 6) {
                     for(var3 = 0; var3 < var12.vSkillFight.size(); ++var3) {
                        if ((var15 = (Skill)var12.vSkillFight.elementAt(var3)) != null && var15.template.type == 4) {
                           if (!var15.gameAA()) {
                              for(var3 = 0; var3 < GameScr.vParty.size(); ++var3) {
                                 Party var5;
                                 if ((var5 = (Party)GameScr.vParty.elementAt(var3)).charId != var12.charID && var5.c != null && var5.c.cHP <= 0) {
                                    var3 = var12.cx;
                                    int var9 = var12.cy;
                                    Char var6;
                                    Char.fieldAC((var6 = var5.c).cx, var6.cy);
                                    cuong.sleep(500L);
                                    Service.gI().buffLive(var5.charId);
                                    var15.lastTimeUseThisSkill = System.currentTimeMillis();
                                    var15.paintCanNotUseSkill = true;
                                    var12.gameAB(GameScr.sks[var15.template.id], 0);
                                    cuong.sleep(1000L);
                                    Char.fieldAC(var3, var9);
                                    return;
                                 }
                              }
                           }
                           break;
                        }
                     }
                  }

                  if (this.fieldBA && this.fieldAI() && var8 != null && var12.nClass.classId == 6) {
                     for(var3 = 0; var3 < var12.vSkillFight.size(); ++var3) {
                        if ((var15 = (Skill)var12.vSkillFight.elementAt(var3)) != null && !var15.gameAA() && var15.template.type == 2 && (var15.template.id < 67 || var15.template.id > 72) && (var15.template.id != 47 || var8.cHP < var8.cMaxHP * Char.aHpValue / 100)) {
                           System.currentTimeMillis();

                           int var16;
                           for(var16 = 0; var16 < var8.vEff.size(); ++var16) {
                              var8.vEff.elementAt(var16);
                           }

                           var16 = var12.cx;
                           var3 = var12.cy;
                           Char.fieldAC(var8.cx, var8.cy);
                           Service.gI().selectSkill(var15.template.id);
                           Service.gI().sendUseSkillMyBuff();
                           var15.lastTimeUseThisSkill = System.currentTimeMillis();
                           var15.paintCanNotUseSkill = true;
                           var12.gameAB(GameScr.sks[var15.template.id], 0);
                           cuong.sleep(1000L);
                           Char.fieldAC(var16, var3);
                           break;
                        }
                     }
                  }
               }

               Item var14;
               if (System.currentTimeMillis() - this.fieldBI > 602000L && Char.fieldBF() > 1 && (var14 = Char.fieldAF(38)) != null) {
                  Service.gI().useItem(var14.indexUI);
                  this.fieldBI = System.currentTimeMillis();
                  return;
               }

               return;
            }
            break;
         case 1:
            if (Code.fieldAH == null || System.currentTimeMillis() - this.fieldBJ >= 3600000L) {
               this.fieldAM();
               return;
            }

            if (TileMap.mapID != 1 || TileMap.zoneID != 21) {
               this.fieldAA(1, 21, -1, -1);
               return;
            }

            TaskOrder var10 = Char.fieldAM(1);
            boolean var7 = Char.getMyChar().cName.equals(Code.fieldAH);
            if (fieldAX && var7) {
               this.fieldAM();
               Service.gI().chatParty("sts");
               return;
            }

            if (var10 == null) {
               Npc var11;
               Char.fieldAC((var11 = GameScr.fieldAI(25)).cx, var11.cy);
               LockGame.fieldAA(300000L);
               if (var7) {
                  GameScr.fieldAB(25, GameScr.fieldGH + 1, 0);
                  LockGame.fieldAK();
                  cuong.sleep(2000L);
                  return;
               }
            } else {
               if (var10.count >= var10.maxCount) {
                  GameScr.fieldAB(25, GameScr.fieldGH + 1, 2);
                  LockGame.fieldAM();
                  cuong.sleep(2000L);
                  return;
               }

               if (var7) {
                  TaThu var13;
                  (var13 = Code.fieldAE).fieldAD();
                  var13.fieldAA = true;
                  Code.fieldAA((Auto)var13);
                  Service.gI().chatParty("att " + var13.fieldAB + " " + var13.fieldAC + " " + var13.fieldAV);
                  return;
               }
            }

            return;
         case 2:
            if (Char.getMyChar().clevel < 30 || System.currentTimeMillis() - this.fieldBJ >= 10800000L) {
               this.fieldAM();
               return;
            }

            if (!this.fieldBD) {
               if (TileMap.mapID != 1 || TileMap.zoneID != 21) {
                  this.fieldAA(1, 21, -1, -1);
                  return;
               }

               if (Char.aFoodValue <= 50) {
                  int var10000 = (var2 = Char.fieldAK(Char.aFoodValue == 50 ? 29 : 23 + Char.aFoodValue / 10)) >= 2 ? 0 : 2 - var2;
                  var2 = var10000;
                  if (var10000 > 0) {
                     GameScr.fieldAB(4, 0, 0);
                     if (Char.aFoodValue == 50) {
                        Service.gI().buyItem(9, 7, var2);
                     } else {
                        Service.gI().buyItem(9, Char.aFoodValue / 10, var2);
                     }

                     LockGame.fieldAG();
                  }
               }

               if (var1.get(7) == 2) {
                  GameScr.fieldAB(24, 1, 0);
                  cuong.sleep(2000L);
               }

               this.fieldBD = true;
               if ((var2 = Char.getMyChar().clevel) >= 90) {
                  LockGame.fieldBI();
                  Code.fieldAA((Auto)(new Hd9x()));
                  fieldAY = true;
                  return;
               }

               super.fieldAB = var2 < 40 ? 91 : (var2 < 50 ? 94 : (var2 < 60 ? 105 : (var2 < 70 ? 114 : 125)));
               super.fieldAD = true;
               super.fieldAA = false;
               if (var2 >= 60 && var2 < 70) {
                  if (GameScr.vParty.size() > 1) {
                     Service.gI().outParty();
                     return;
                  }

                  return;
               }

               if (Code.fieldAH != null) {
                  LockGame.fieldBI();
                  return;
               }
            } else {
               if (!fieldAY) {
                  if (TileMap.mapID == super.fieldAB) {
                     this.fieldAB(-1, -1);
                     this.fieldAC(-1);
                     return;
                  }

                  this.fieldAA(super.fieldAB, -2, -1, -1);
                  return;
               }

               if (!TileMap.isTruong(TileMap.mapID)) {
                  return;
               }

               GameScr.fieldAB(0, 2, 0);
               Service.gI().rewardPB();
               this.fieldBD = false;
               fieldAY = false;
               if (!fieldAZ) {
                  return;
               }
            }
            break;
         case 3:
            if (!this.fieldBD) {
               if (TileMap.mapID != 1 || TileMap.zoneID != 21) {
                  this.fieldAA(1, 21, -1, -1);
                  return;
               }

               GameScr.fieldAB(5, 1, 0);
               if (LockGame.fieldAY()) {
                  if (var1.get(7) == 2) {
                     GameScr.fieldAB(24, 1, 0);
                     cuong.sleep(2000L);
                  }

                  this.fieldBD = true;
                  return;
               }

               return;
            }
            break;
         default:
            return;
         }

         this.fieldAM();
      }
   }

   public final String toString() {
      if (this.fieldBA && this.fieldBB) {
         return "Stanima Buff HS";
      } else if (this.fieldBA) {
         return "Stanima Buff";
      } else if (this.fieldBB) {
         return "Stanima HS";
      } else {
         return this.fieldAW >= 0 && this.fieldAW < Mob.arrMobTemplate.length ? "Stanima " + Mob.arrMobTemplate[this.fieldAW].name : "Stanima";
      }
   }
}
