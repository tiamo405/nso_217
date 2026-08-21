public final class Buff extends Auto {
   private boolean fieldAV;
   private boolean fieldAW;
   private long fieldAX = 0L;

   public final void fieldAA(int var1, int var2, boolean var3, boolean var4) {
      super.fieldAD();
      super.fieldAB = var1;
      super.fieldAC = var2;
      super.fieldAD = TileMap.isHang(var1);
      this.fieldAV = var3;
      this.fieldAW = var4;
      super.fieldAA = true;
   }

   public final void fieldAA() {
      if (Auto.fieldAF()) {
         Auto.fieldAA(true);
      } else {
         if (super.fieldAB == TileMap.mapID && (TileMap.isHang(super.fieldAB) || super.fieldAC == TileMap.zoneID)) {
            Char var1 = Char.getMyChar();
            int var4;
            int var5;
            int var6;
            Char var9;
            if (this.fieldAW && (GameScr.vParty.size() > 1 || Code.fieldAJ.size() > 0) && var1.nClass.classId == 6) {
               for(int var2 = 0; var2 < var1.vSkillFight.size(); ++var2) {
                  Skill var3;
                  if ((var3 = (Skill)var1.vSkillFight.elementAt(var2)) != null && var3.template.type == 4) {
                     if (System.currentTimeMillis() - this.fieldAX > 3000L) {
                        for(var4 = 0; var4 < GameScr.vParty.size(); ++var4) {
                           Party var8;
                           if ((var8 = (Party)GameScr.vParty.elementAt(var4)).charId != var1.charID && var8.c != null && var8.c.cHP <= 0) {
                              var5 = var1.cx;
                              var6 = var1.cy;
                              Char var14;
                              Char.fieldAC((var14 = var8.c).cx, var14.cy);
                              cuong.sleep(500L);
                              if (Auto.fieldAC(var14)) {
                                 this.fieldAX = System.currentTimeMillis();
                                 Service.gI().buffLive(var8.charId);
                                 var3.lastTimeUseThisSkill = System.currentTimeMillis();
                                 var3.paintCanNotUseSkill = true;
                                 var1.gameAB(GameScr.sks[var3.template.id], 0);
                                 cuong.sleep(1000L);
                              }

                              Char.fieldAC(var5, var6);
                              return;
                           }
                        }

                        for(var4 = 0; var4 < GameScr.vCharInMap.size(); ++var4) {
                           if ((var9 = (Char)GameScr.vCharInMap.elementAt(var4)) != null && Auto.fieldAC(var9) && Code.fieldAA(var9.cName)) {
                              var5 = var1.cx;
                              var6 = var1.cy;
                              Char.fieldAC(var9.cx, var9.cy);
                              cuong.sleep(500L);
                              if (Auto.fieldAC(var9)) {
                                 this.fieldAX = System.currentTimeMillis();
                                 Service.gI().buffLive(var9.charID);
                                 var3.lastTimeUseThisSkill = System.currentTimeMillis();
                                 var3.paintCanNotUseSkill = true;
                                 var1.gameAB(GameScr.sks[var3.template.id], 0);
                                 cuong.sleep(1000L);
                              }

                              Char.fieldAC(var5, var6);
                              return;
                           }
                        }
                     }
                     break;
                  }
               }
            }

            var9 = GameScr.vParty.size() > 0 ? ((Party)GameScr.vParty.firstElement()).c : null;
            if (this.fieldAV && this.fieldAI() && var9 != null && var1.nClass.classId == 6) {
               for(int var10 = 0; var10 < var1.vSkillFight.size(); ++var10) {
                  Skill var15;
                  if ((var15 = (Skill)var1.vSkillFight.elementAt(var10)) != null && !var15.gameAA() && var15.template.type == 2 && (var15.template.id < 67 || var15.template.id > 72) && (var15.template.id != 47 || var9.cHP < var9.cMaxHP * Char.aHpValue / 100)) {
                     System.currentTimeMillis();

                     for(var5 = 0; var5 < var9.vEff.size(); ++var5) {
                        var9.vEff.elementAt(var5);
                     }

                     var5 = var1.cx;
                     var6 = var1.cy;
                     Char.fieldAC(var9.cx, var9.cy);
                     Service.gI().selectSkill(var15.template.id);
                     Service.gI().sendUseSkillMyBuff();
                     var15.lastTimeUseThisSkill = System.currentTimeMillis();
                     var15.paintCanNotUseSkill = true;
                     var1.gameAB(GameScr.sks[var15.template.id], 0);
                     cuong.sleep(1000L);
                     Char.fieldAC(var5, var6);
                     return;
                  }
               }
            }

            if (Code.fieldBO) {
               Char var12;
               if (((var12 = var1.charFocus) == null || !SavePk.fieldAC(var12.cName) && !Auto.fieldAA(var1, var12)) && (var12 = this.fieldAA((Char)var1, -1)) == null) {
                  var12 = Auto.fieldAE(var1);
               }

               boolean var16 = var12 != null && SavePk.fieldAC(var12.cName);
               if (var12 == null && super.fieldAU) {
                  Service.gI().changePk(0);
                  super.fieldAU = false;
               }

               if (var1.cPk >= 5 && System.currentTimeMillis() - super.fieldAT > 5000L) {
                  Item var11;
                  if ((var11 = Char.fieldAF(257)) != null && var11.template.id == 257) {
                     Service.gI().useItem(var11.indexUI);
                  }

                  super.fieldAT = System.currentTimeMillis();
               }

               if (var12 != null && var12 != null && !Auto.fieldAC(var12) && (var16 || Auto.fieldAA(var1, var12))) {
                  Skill var13 = Auto.fieldAL;
                  if (var16) {
                     if ((var13.template.type == 1 || var13.template.type == 3) && (Res.abs(var1.cx - var12.cx) > var13.dx + 30 || Res.abs(var1.cy - var12.cy) > var13.dy + 30) && System.currentTimeMillis() - super.fieldAS > 1500L) {
                        Auto.fieldAD(var12);
                        super.fieldAS = System.currentTimeMillis();
                     }

                     if (var12.killCharId != var1.charID && var12.cTypePk != 3) {
                        super.fieldAU = true;
                        Service.gI().changePk(3);
                     }
                  }

                  var5 = var13.dx;
                  var6 = var13.dy;
                  Auto.fieldAP.removeAllElements();
                  Auto.fieldAQ.removeAllElements();
                  Auto.fieldAQ.addElement(var12);

                  for(var4 = 0; var4 < GameScr.vCharInMap.size() && Auto.fieldAP.size() + Auto.fieldAQ.size() < var13.maxFight; ++var4) {
                     Char var7;
                     if ((var7 = (Char)GameScr.vCharInMap.elementAt(var4)).cHP > 0 && var7.statusMe != 14 && var7.statusMe != 5 && var7.statusMe != 15 && !var7.equals(var12) && (var7.cTypePk == 3 || var1.cTypePk == 3 || var7.cTypePk == 1 && var1.cTypePk == 1 || var1.killCharId >= 0 && var1.killCharId == var7.charID || var1.testCharId >= 0 && var1.testCharId == var7.charID) && !Code.fieldAD(var7.cName) && var12.cx - var5 <= var7.cx && var7.cx <= var12.cx + var5 && var12.cy - var6 <= var7.cy && var7.cy <= var12.cy + var6) {
                        Auto.fieldAQ.addElement(var7);
                     }
                  }

                  Service.gI().sendPlayerAttack((MyVector)Auto.fieldAP, (MyVector)Auto.fieldAQ, (int)2);
                  if (System.currentTimeMillis() - var13.lastTimeUseThisSkill >= (long)var13.coolDown) {
                     var13.lastTimeUseThisSkill = System.currentTimeMillis();
                     var13.paintCanNotUseSkill = true;
                     if (!Code.fieldBF) {
                        var1.gameAB(GameScr.sks[var13.template.id], 0);
                     }
                  }

                  super.fieldAR = System.currentTimeMillis();
                  return;
               }
            }
         } else {
            this.fieldAA(super.fieldAB, super.fieldAC, -1, -1);
         }

      }
   }

   public final String toString() {
      if (this.fieldAV && this.fieldAW) {
         return "Buff HS Xa";
      } else {
         return this.fieldAW ? "HSinh Xa" : "Buff Xa";
      }
   }
}
