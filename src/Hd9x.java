public final class Hd9x extends Auto {
   private int fieldAV;
   private int fieldAW;
   private boolean[] fieldAX;

   public Hd9x() {
      super.fieldAD();
      super.fieldAB = 157;
      super.fieldAD = true;
      super.fieldAC = -2;
      this.fieldAV = 199;
      this.fieldAW = 0;
      this.fieldAX = new boolean[3];
   }

   public final void fieldAA() {
      if (Auto.fieldAF()) {
         Auto.fieldAA(true);
      } else if (super.fieldAB != TileMap.mapID) {
         this.fieldAA(super.fieldAB, super.fieldAC, super.fieldAE, super.fieldAF);
      } else {
         Char var2 = Char.getMyChar();
         if (!Auto.fieldAL()) {
            label387: {
               Mob var3;
               int var5;
               Mob var6;
               if ((var3 = var2.mobFocus) == null || var3.status == 0 || !var3.isBoss || System.currentTimeMillis() - super.fieldAR > 5000L) {
                  Hd9x var10 = this;
                  MyVector var4 = GameScr.vMob;
                  var5 = 0;

                  Mob var10000;
                  while(true) {
                     if (var5 >= var4.size()) {
                        var10000 = null;
                        break;
                     }

                     if ((var6 = (Mob)var4.elementAt(var5)) != null && var6.hp > 0 && var6.status != 0 && var6.status != 1 && var6.templateId == var10.fieldAV && var6.levelBoss == var10.fieldAW) {
                        var10000 = var6;
                        break;
                     }

                     ++var5;
                  }

                  var3 = var10000;
                  if (var10000 == null && System.currentTimeMillis() - super.fieldAR > 1000L && !this.fieldAJ()) {
                     var4 = GameScr.vMob;
                     var5 = 0;

                     boolean var18;
                     while(true) {
                        if (var5 >= var4.size()) {
                           var18 = true;
                           break;
                        }

                        if ((var6 = (Mob)var4.elementAt(var5)) != null && var6.hp > 0 && var6.status != 0 && var6.status != 1) {
                           var18 = false;
                           break;
                        }

                        ++var5;
                     }

                     if (var18) {
                        this.fieldAX[this.fieldAV - 198] = true;
                        if (this.fieldAX[0] && this.fieldAX[1] && this.fieldAX[2]) {
                           Code.fieldAC();
                           break label387;
                        }
                     }

                     switch(this.fieldAV) {
                     case 198:
                        this.fieldAV = 199;
                        super.fieldAB = 157;
                        if (this.fieldAW == 0) {
                           this.fieldAW = 4;
                        }
                        break label387;
                     case 199:
                        this.fieldAV = 200;
                        super.fieldAB = 158;
                        break label387;
                     case 200:
                        this.fieldAV = 198;
                        super.fieldAB = 159;
                     default:
                        break label387;
                     }
                  }
               }

               if (Char.fieldFI && GameScr.vParty.size() > 0 && var2.nClass.classId == 6 && var2.cHP > 0) {
                  for(int var11 = 0; var11 < var2.vSkillFight.size(); ++var11) {
                     Skill var13;
                     if ((var13 = (Skill)var2.vSkillFight.elementAt(var11)) != null && var13.template.type == 4) {
                        if (!var13.gameAA()) {
                           for(int var14 = 0; var14 < GameScr.vParty.size(); ++var14) {
                              Party var7;
                              if ((var7 = (Party)GameScr.vParty.elementAt(var14)).charId != var2.charID && var7.c != null && var7.c.cHP <= 0) {
                                 Char var8 = var7.c;
                                 if (Math.abs(var2.cx - var8.cx) > 50 || Math.abs(var2.cy - var8.cy) > 50) {
                                    Char.fieldAC(var8.cx, var8.cy);
                                 }

                                 cuong.sleep(500L);
                                 Service.gI().buffLive(var7.charId);
                                 var13.lastTimeUseThisSkill = System.currentTimeMillis();
                                 var13.paintCanNotUseSkill = true;
                                 var2.gameAB(GameScr.sks[var13.template.id], 0);
                                 cuong.sleep(1000L);
                                 break label387;
                              }
                           }
                        }
                        break;
                     }
                  }
               }

               if (Auto.fieldAL != null && var3 != null && var3.isBoss && var3.templateId == this.fieldAV && var3.levelBoss == this.fieldAW) {
                  Skill var12;
                  if ((var12 = Auto.fieldAL).gameAA() && Char.isABuff) {
                     label371: {
                        var5 = 0;

                        Skill var15;
                        label266:
                        while(true) {
                           if (var5 >= var2.vSkillFight.size()) {
                              break label371;
                           }

                           if ((var15 = (Skill)var2.vSkillFight.elementAt(var5)) != null && System.currentTimeMillis() - var15.lastTimeUseThisSkill >= (long)var15.coolDown - 300L) {
                              if (var15.template.type == 2) {
                                 if ((var2.fieldAA == null && Char.fieldEG || var15.template.id < 67 || var15.template.id > 72) && (Char.fieldEH || var15.template.id != 31) && (var15.template.id != 15 || var2.cHP < var2.cMaxHP * Char.aHpValue / 100)) {
                                    int var16 = (int)(System.currentTimeMillis() / 1000L);
                                    int var17 = 0;

                                    while(true) {
                                       if (var17 >= var2.vEff.size()) {
                                          break label266;
                                       }

                                       Effect var9;
                                       if ((var9 = (Effect)var2.vEff.elementAt(var17)) != null && (var9.template.iconId == var15.template.iconId || var15.template.id == 58 && var9.template.type == 7) && var9.timeLenght - (var16 - var9.timeStart) >= 2) {
                                          break;
                                       }

                                       ++var17;
                                    }
                                 }
                              } else if (var15.template.type == 3 && var3.levelBoss == 0 && var3.hp > var3.maxHp / 2) {
                                 if (var15.template.id != 4 || Char.fieldEI && var2.cHP < var2.cMaxHP * Char.aHpValue / 100) {
                                    break;
                                 }
                              } else if ((var15.template.id == 7 || var15.template.id == 16 || var15.template.id == 25 || var15.template.id == 34 || var15.template.id == 43) && (var3.levelBoss != 0 || var3.hp >= var3.maxHp / 2) && (var15.template.id != 7 && var15.template.id != 16 || !var3.isFire) && (var15.template.id != 25 && var15.template.id != 34 || var3.isIce) && (var15.template.id != 43 || var3.isWind)) {
                                 break;
                              }
                           }

                           ++var5;
                        }

                        var12 = var15;
                        cuong.sleep(500L);
                     }
                  }

                  if ((var12.template.type == 1 || var12.template.type == 3) && (Res.abs(var2.cx - var3.xFirst) > var12.dx || Res.abs(var2.cy - var3.yFirst) > var12.dy)) {
                     this.fieldAC(var3);
                  }

                  Service.gI().selectSkill(var12.template.id);
                  if (var12.template.type == 2) {
                     Service.gI().sendUseSkillMyBuff();
                  } else {
                     Auto.fieldAP.removeAllElements();
                     Auto.fieldAQ.removeAllElements();
                     if (var3 != null) {
                        Auto.fieldAP.addElement(var3);

                        for(var5 = 0; var5 < GameScr.vMob.size() && Auto.fieldAP.size() + Auto.fieldAQ.size() < var12.maxFight; ++var5) {
                           if ((var6 = (Mob)GameScr.vMob.elementAt(var5)).status != 0 && var6.status != 1 && !var6.equals(var3) && var3.xFirst - 100 <= var6.xFirst && var6.xFirst <= var3.xFirst + 100 && var3.yFirst - 50 <= var6.yFirst && var6.yFirst <= var3.yFirst + 50) {
                              Auto.fieldAP.addElement(var6);
                           }
                        }
                     }

                     Service.gI().sendPlayerAttack((MyVector)Auto.fieldAP, (MyVector)Auto.fieldAQ, (int)1);
                  }

                  if (System.currentTimeMillis() - var12.lastTimeUseThisSkill >= (long)var12.coolDown) {
                     var12.lastTimeUseThisSkill = System.currentTimeMillis();
                     var12.paintCanNotUseSkill = true;
                     if (!Code.fieldBF) {
                        var2.gameAB(GameScr.sks[var12.template.id], 0);
                     }
                  }

                  super.fieldAR = System.currentTimeMillis();
                  if (var12.template.id == 15) {
                     cuong.sleep(2000L);
                  }
               }
            }
         }

         this.fieldAC(-1);
      }
   }

   public final String toString() {
      return "Hang 9x Lvl " + this.fieldAW;
   }
}
