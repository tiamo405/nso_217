public class As20 extends As10 {
   private static final int UplevelNoelMap = 72;
   private static final int UplevelGooshoNpc = 30;
   private static final int UplevelNoelHatMaleId = 351;
   private static final int UplevelNoelHatFemaleId = 352;
   private static final int UplevelTeacherHpId = 14;
   private static final int UplevelTeacherHpQuantity = 10000;
   private static final int UplevelFoodType = 18;
   private static final int UplevelFoodNpc = 4;
   private static final int UplevelFoodStock = 2;
   private int fieldAV;
   private boolean uplevelTask13WeaponEquipped;
   private int uplevelTask14PendingItem = -1;
   private long uplevelTask14PickSentAt;
   private boolean uplevelTeacherHpBuyPending;
   private long uplevelTeacherHpBuySentAt;
   private long uplevelTeacherHpUseSentAt;
   private int uplevelFoodLevel = -1;
   private boolean uplevelFoodBoxRequested;
   private boolean uplevelFoodBoxChecked;
   private int uplevelFoodBoxMoveSlot = -1;
   private long uplevelFoodBoxActionAt;
   private boolean uplevelFoodBuyPending;
   private long uplevelFoodBuySentAt;
   private long uplevelFoodLastBuyAt;
   private long uplevelFoodUseSentAt;
   private boolean uplevelNoelReady;
   private boolean uplevelNoelBoxChecked;
   private boolean uplevelNoelShopRequested;
   private int uplevelNoelUseSlot = -1;
   private long uplevelNoelUseSentAt;
   private int uplevelNoelUseAttempts;
   private long uplevelTask2UseSentAt;
   private long uplevelTask2MissingLogAt;
   private long uplevelCombatPotionUseSentAt;
   private static  int[] fieldAW;
   private static  int[] fieldAX;
   private static  int[] fieldAY;
   private static  int[] fieldAZ;
   private static  int[] fieldBA;
   private static  int[] fieldBB;
   private static  int[] fieldBC;

   private static void fieldAM() {
      fieldAW = new int[]{0, 1, 1, 72, 72, 27, 27};
      fieldAX = new int[]{0, 9, 9, 10, 10, 11, 11};
      fieldAY = new int[]{0, 0, 1, 0, 1, 0, 1};
      fieldAZ = new int[]{0, 94, 114, 99, 109, 105, 119};
      fieldBA = new int[]{-1, 40, 49, 58, 67, 76, 85};
      fieldBB = new int[]{-1, 41, 50, 59, 68, 77, 86};
      fieldBC = new int[]{-1, 42, 51, 60, 69, 78, 87};
   }

   static {
      fieldAM();
   }

   private static boolean uplevelUpgradeCrystal(Item item) {
      return item != null && item.template != null
            && item.template.type == 26 && item.template.id >= 0
            && item.template.id <= 3 && item.template.id < GameScr.upClothe.length;
   }

   public As20(int var1) {
      super.fieldAD();
      this.fieldAV = var1;
   }

   public boolean fieldAA(Char var1) {
      return var1.clevel >= 20;
   }

   private boolean ensureUplevelNoelHat(Char me) {
      if (this.uplevelNoelReady) {
         return true;
      }

      int hatId = this.getUplevelNoelHatId(me.cgender);
      ItemTemplate hatTemplate = ItemTemplates.gameAA((short)hatId);
      if (this.isUplevelNoelActive(me, hatTemplate)) {
         System.out.println("UPLEVEL NOEL active id=" + hatId);
         this.uplevelNoelUseSlot = -1;
         this.uplevelNoelReady = true;
         return true;
      }

      if (this.uplevelNoelUseSlot >= 0) {
         if (this.isUplevelNoelActive(me, hatTemplate)
               || me.arrItemBag == null || this.uplevelNoelUseSlot >= me.arrItemBag.length
               || me.arrItemBag[this.uplevelNoelUseSlot] == null) {
            System.out.println("UPLEVEL NOEL use confirmed id=" + hatId);
            this.uplevelNoelUseSlot = -1;
            this.uplevelNoelReady = true;
            return true;
         }
         if (System.currentTimeMillis() - this.uplevelNoelUseSentAt < 8000L) {
            return false;
         }
         if (this.uplevelNoelUseAttempts >= 3) {
            System.out.println("UPLEVEL NOEL use timeout id=" + hatId + "; continue task");
            this.uplevelNoelUseSlot = -1;
            this.uplevelNoelReady = true;
            return true;
         }
         this.uplevelNoelUseSlot = -1;
      }

      if (TileMap.mapID != UplevelNoelMap) {
         System.out.println("UPLEVEL NOEL route map=" + TileMap.mapID + " -> " + UplevelNoelMap);
         this.fieldAA(UplevelNoelMap, -2, -1, -1);
         return false;
      }

      Item hat = this.findUplevelItem(me.arrItemBag, hatId);
      if (!this.uplevelNoelBoxChecked) {
         this.uplevelNoelBoxChecked = true;
         me.arrItemBox = null;
         Service.gI().requestItem(4);
         this.waitForUplevelBoxItem(me, -1, 5000L);
      }
      if (hat == null) {
         Item boxHat = this.findUplevelItem(me.arrItemBox, hatId);
         if (boxHat != null) {
            System.out.println("UPLEVEL NOEL move box index=" + boxHat.indexUI);
            Service.gI().itemBoxToBag(boxHat.indexUI);
            hat = this.waitForUplevelBagItem(me, hatId, 5000L);
         }
      }

      if (hat == null && !this.uplevelNoelShopRequested) {
         GameScr.arrItemFashion = null;
         GameScr.fieldAB(UplevelGooshoNpc, 0, 0);
         Service.gI().requestItem(32);
         this.uplevelNoelShopRequested = true;
         System.out.println("UPLEVEL NOEL request shop id=" + hatId);
         this.waitForUplevelFashionItem(hatId, 5000L);
      }

      if (hat == null) {
         Item shopHat = this.findUplevelItem(GameScr.arrItemFashion, hatId);
         if (shopHat == null) {
            System.out.println("UPLEVEL NOEL unavailable id=" + hatId + "; continue task");
            this.uplevelNoelReady = true;
            return true;
         }
         System.out.println("UPLEVEL NOEL buy id=" + hatId + " shopIndex=" + shopHat.indexUI);
         Service.gI().buyItem(shopHat.typeUI, shopHat.indexUI, 1);
         hat = this.waitForUplevelBagItem(me, hatId, 5000L);
         if (hat == null) {
            me.arrItemBox = null;
            Service.gI().requestItem(4);
            this.waitForUplevelBoxItem(me, -1, 5000L);
            Item boxHat = this.findUplevelItem(me.arrItemBox, hatId);
            if (boxHat != null) {
               Service.gI().itemBoxToBag(boxHat.indexUI);
               hat = this.waitForUplevelBagItem(me, hatId, 5000L);
            }
         }
      }

      if (hat == null) {
         System.out.println("UPLEVEL NOEL purchase not in bag id=" + hatId + "; continue task");
         this.uplevelNoelReady = true;
         return true;
      }

      System.out.println("UPLEVEL NOEL use id=" + hatId + " bagIndex=" + hat.indexUI
            + " type=" + hat.template.type + " part=" + hat.template.part
            + " itemGender=" + hat.template.gender + " charGender=" + me.cgender);
      this.uplevelNoelUseSlot = hat.indexUI;
      this.uplevelNoelUseSentAt = System.currentTimeMillis();
      ++this.uplevelNoelUseAttempts;
      Service.gI().useItem(hat.indexUI);
      return false;
   }

   private boolean isUplevelNoelActive(Char me, ItemTemplate template) {
      if (template == null) {
         return false;
      }
      if (me.arrItemBody != null && template.type >= 0 && template.type < me.arrItemBody.length
            && me.arrItemBody[template.type] != null
            && me.arrItemBody[template.type].template.id == template.id) {
         return true;
      }
      return template.part >= 0 && me.ID_MAT_NA == template.part;
   }

   private int getUplevelNoelHatId(int characterGender) {
      ItemTemplate male = ItemTemplates.gameAA((short)UplevelNoelHatMaleId);
      ItemTemplate female = ItemTemplates.gameAA((short)UplevelNoelHatFemaleId);
      if (male != null && male.gender == characterGender) {
         return UplevelNoelHatMaleId;
      }
      if (female != null && female.gender == characterGender) {
         return UplevelNoelHatFemaleId;
      }
      System.out.println("UPLEVEL NOEL gender fallback charGender=" + characterGender);
      return characterGender == 0 ? UplevelNoelHatMaleId : UplevelNoelHatFemaleId;
   }

   private Item waitForUplevelBagItem(Char me, int templateId, long timeout) {
      long deadline = System.currentTimeMillis() + timeout;
      Item item;
      while ((item = this.findUplevelItem(me.arrItemBag, templateId)) == null
            && System.currentTimeMillis() < deadline) {
         Auto.fieldAA(100L);
      }
      return item;
   }

   private Item waitForUplevelBoxItem(Char me, int templateId, long timeout) {
      long deadline = System.currentTimeMillis() + timeout;
      Item item;
      while (System.currentTimeMillis() < deadline) {
         item = this.findUplevelItem(me.arrItemBox, templateId);
         if (templateId < 0 ? me.arrItemBox != null : item != null) {
            return item;
         }
         Auto.fieldAA(100L);
      }
      return this.findUplevelItem(me.arrItemBox, templateId);
   }

   private Item waitForUplevelFashionItem(int templateId, long timeout) {
      long deadline = System.currentTimeMillis() + timeout;
      Item item;
      while ((item = this.findUplevelItem(GameScr.arrItemFashion, templateId)) == null
            && GameScr.arrItemFashion == null && System.currentTimeMillis() < deadline) {
         Auto.fieldAA(100L);
      }
      return item;
   }

   private Item findUplevelItem(Item[] items, int templateId) {
      if (items == null) {
         return null;
      }
      for (int i = 0; i < items.length; i++) {
         Item item = items[i];
         if (item != null && item.template != null && item.template.id == templateId) {
            return item;
         }
      }
      return null;
   }

   private int countUplevelItem(Char me, int templateId) {
      int count = 0;
      if (me.arrItemBag == null) {
         return count;
      }
      for (int i = 0; i < me.arrItemBag.length; i++) {
         Item item = me.arrItemBag[i];
         if (item != null && item.template != null && item.template.id == templateId) {
            count += item.quantity;
         }
      }
      return count;
   }

   private boolean useUplevelCombatPotions(Char me) {
      if (me.cHP <= 0) {
         return false;
      }
      long now = System.currentTimeMillis();
      if (now - this.uplevelCombatPotionUseSentAt < 2500L) {
         return false;
      }
      if ((long)me.cHP * 100L < (long)me.cMaxHP * 50L && me.gameAE(16)) {
         this.uplevelCombatPotionUseSentAt = now;
         System.out.println("UPLEVEL COMBAT HP use hp=" + me.cHP + "/" + me.cMaxHP);
         return true;
      }
      if ((long)me.cMP * 100L < (long)me.cMaxMP * 50L && me.gameAE(17)) {
         this.uplevelCombatPotionUseSentAt = now;
         System.out.println("UPLEVEL COMBAT MP use mp=" + me.cMP + "/" + me.cMaxMP);
         return true;
      }
      return false;
   }

   private boolean pickupUplevelConsumable() {
      for (int i = 0; i < GameScr.vItemMap.size(); i++) {
         ItemMap drop = (ItemMap)GameScr.vItemMap.elementAt(i);
         if (drop != null && !drop.fieldAK && drop.template != null
               && (drop.template.type == 16 || drop.template.type == 17 || drop.template.type == 18)) {
            this.fieldAC(drop.template.id);
            System.out.println("UPLEVEL PICK consumable id=" + drop.template.id);
            return true;
         }
      }
      return false;
   }

   private boolean ensureUplevelTeacherHp(Char me) {
      int count = this.countUplevelItem(me, UplevelTeacherHpId);
      if (count > 0) {
         if (this.uplevelTeacherHpBuyPending) {
            System.out.println("UPLEVEL TEACHER HP ready id=" + UplevelTeacherHpId + " count=" + count);
            this.uplevelTeacherHpBuyPending = false;
         }
         return true;
      }

      long now = System.currentTimeMillis();
      if (this.uplevelTeacherHpBuyPending) {
         if (now - this.uplevelTeacherHpBuySentAt < 8000L) {
            return false;
         }
         System.out.println("UPLEVEL TEACHER HP buy timeout id=" + UplevelTeacherHpId + "; continue task");
         this.uplevelTeacherHpBuyPending = false;
         return true;
      }

      System.out.println("UPLEVEL TEACHER HP buy id=" + UplevelTeacherHpId
            + " quantity=" + UplevelTeacherHpQuantity + " npc=3 shop=7 index=1");
      this.uplevelTeacherHpBuyPending = true;
      this.uplevelTeacherHpBuySentAt = now;
      GameScr.fieldAB(3, 0, 0);
      Service.gI().buyItem(7, 1, UplevelTeacherHpQuantity);
      LockGame.fieldAG();
      return false;
   }

   private boolean useUplevelTeacherHp(Char me) {
      if (me.cHP <= 0 || (long)me.cHP * 100L >= (long)me.cMaxHP * 90L) {
         return false;
      }
      Item hp = this.findUplevelItem(me.arrItemBag, UplevelTeacherHpId);
      if (hp == null || hp.quantity <= 0) {
         return false;
      }
      long now = System.currentTimeMillis();
      if (now - this.uplevelTeacherHpUseSentAt < 1000L) {
         return true;
      }
      System.out.println("UPLEVEL TEACHER HP use id=" + UplevelTeacherHpId
            + " bagIndex=" + hp.indexUI + " hp=" + me.cHP + "/" + me.cMaxHP);
      this.uplevelTeacherHpUseSentAt = now;
      Service.gI().useItem(hp.indexUI);
      return true;
   }

   private int getUplevelFoodLevel(Char me) {
      int level = me.clevel / 10 * 10;
      if (level < 10) {
         return 1;
      }
      return level > 50 ? 50 : level;
   }

   private Item findUplevelFood(Item[] items, int level) {
      if (items == null) {
         return null;
      }
      for (int i = 0; i < items.length; i++) {
         Item item = items[i];
         if (item != null && item.template != null && item.template.type == UplevelFoodType
               && item.template.level == level && item.quantity > 0) {
            return item;
         }
      }
      return null;
   }

   private int countUplevelFood(Item[] items, int level) {
      int count = 0;
      if (items == null) {
         return count;
      }
      for (int i = 0; i < items.length; i++) {
         Item item = items[i];
         if (item != null && item.template != null && item.template.type == UplevelFoodType
               && item.template.level == level) {
            count += item.quantity;
         }
      }
      return count;
   }

   private boolean isUplevelFoodActive(Char me) {
      if (me.vEff == null) {
         return false;
      }
      for (int i = 0; i < me.vEff.size(); i++) {
         Effect effect = (Effect)me.vEff.elementAt(i);
         if (effect != null && effect.template != null && effect.template.type == 0) {
            return true;
         }
      }
      return false;
   }

   private int getUplevelSchoolMap(Char me) {
      int classId = me.nClass == null ? 0 : me.nClass.classId;
      if (classId == 0 && this.fieldAV > 0) {
         return this.fieldAV <= 2 ? 1 : (this.fieldAV <= 4 ? 72 : 27);
      }
      return classId <= 2 ? 1 : (classId <= 4 ? 27 : 72);
   }

   private boolean ensureUplevelFood(Char me) {
      int foodLevel = this.getUplevelFoodLevel(me);
      if (this.uplevelFoodLevel != foodLevel) {
         this.uplevelFoodLevel = foodLevel;
         this.uplevelFoodBoxRequested = false;
         this.uplevelFoodBoxChecked = false;
         this.uplevelFoodBoxMoveSlot = -1;
         Char.aFoodValue = foodLevel;
         Char.isAFood = true;
         System.out.println("UPLEVEL FOOD enable level=" + foodLevel);
      } else {
         Char.aFoodValue = foodLevel;
         Char.isAFood = true;
      }

      Item food = this.findUplevelFood(me.arrItemBag, foodLevel);
      if (food != null) {
         if (this.uplevelFoodBuyPending) {
            System.out.println("UPLEVEL FOOD ready level=" + foodLevel
                  + " count=" + this.countUplevelFood(me.arrItemBag, foodLevel));
            this.uplevelFoodBuyPending = false;
         }
         this.uplevelFoodBoxRequested = false;
         this.uplevelFoodBoxChecked = false;
         this.uplevelFoodBoxMoveSlot = -1;
         if (!this.isUplevelFoodActive(me)) {
            long now = System.currentTimeMillis();
            if (now - this.uplevelFoodUseSentAt >= 5000L) {
               this.uplevelFoodUseSentAt = now;
               Service.gI().useItem(food.indexUI);
               System.out.println("UPLEVEL FOOD use level=" + foodLevel
                     + " bagIndex=" + food.indexUI);
               return false;
            }
         }
         return true;
      }

      long now = System.currentTimeMillis();
      if (this.uplevelFoodBoxMoveSlot >= 0) {
         if (this.findUplevelFood(me.arrItemBag, foodLevel) != null
               || me.arrItemBox == null || this.uplevelFoodBoxMoveSlot >= me.arrItemBox.length
               || me.arrItemBox[this.uplevelFoodBoxMoveSlot] == null) {
            this.uplevelFoodBoxMoveSlot = -1;
            this.uplevelFoodBoxChecked = false;
         } else if (now - this.uplevelFoodBoxActionAt < 8000L) {
            return false;
         } else {
            System.out.println("UPLEVEL FOOD chest move timeout level=" + foodLevel);
            this.uplevelFoodBoxMoveSlot = -1;
            this.uplevelFoodBoxChecked = true;
         }
      }

      if (!this.uplevelFoodBoxChecked) {
         if (me.arrItemBox == null) {
            if (!this.uplevelFoodBoxRequested) {
               me.arrItemBox = null;
               Service.gI().requestItem(4);
               this.uplevelFoodBoxRequested = true;
               this.uplevelFoodBoxActionAt = now;
               System.out.println("UPLEVEL FOOD chest check level=" + foodLevel);
            } else if (now - this.uplevelFoodBoxActionAt >= 8000L) {
               this.uplevelFoodBoxRequested = false;
               this.uplevelFoodBoxChecked = true;
               System.out.println("UPLEVEL FOOD chest timeout level=" + foodLevel);
            }
            return false;
         }
         this.uplevelFoodBoxRequested = false;
         Item boxFood = this.findUplevelFood(me.arrItemBox, foodLevel);
         this.uplevelFoodBoxChecked = true;
         if (boxFood != null) {
            System.out.println("UPLEVEL FOOD chest->bag level=" + foodLevel
                  + " chestIndex=" + boxFood.indexUI);
            this.uplevelFoodBoxMoveSlot = boxFood.indexUI;
            this.uplevelFoodBoxActionAt = now;
            Service.gI().itemBoxToBag(boxFood.indexUI);
            return false;
         }
         System.out.println("UPLEVEL FOOD chest empty level=" + foodLevel);
      }

      if (this.uplevelFoodBuyPending) {
         if (now - this.uplevelFoodBuySentAt < 10000L) {
            return false;
         }
         System.out.println("UPLEVEL FOOD buy timeout level=" + foodLevel + "; continue task");
         this.uplevelFoodBuyPending = false;
      }
      if (now - this.uplevelFoodLastBuyAt < 30000L) {
         return true;
      }

      int schoolMap = this.getUplevelSchoolMap(me);
      if (TileMap.mapID != schoolMap) {
         System.out.println("UPLEVEL FOOD route school map=" + TileMap.mapID + " -> " + schoolMap
               + " level=" + foodLevel);
         this.fieldAA(schoolMap, -2, -1, -1);
         return false;
      }

      Npc foodNpc = GameScr.fieldAI(UplevelFoodNpc);
      if (foodNpc == null) {
         return false;
      }
      if (Math.abs(foodNpc.cx - me.cx) > 22 || Math.abs(foodNpc.cy - me.cy) > 22) {
         Char.fieldAC(foodNpc.cx, foodNpc.cy);
         return false;
      }

      int missing = UplevelFoodStock - this.countUplevelFood(me.arrItemBag, foodLevel);
      if (missing < 1) {
         return true;
      }
      int shopIndex = foodLevel == 50 ? 7 : foodLevel / 10;
      GameScr.fieldAB(UplevelFoodNpc, 0, 0);
      Service.gI().buyItem1(9, shopIndex, missing);
      this.uplevelFoodBuyPending = true;
      this.uplevelFoodBuySentAt = now;
      this.uplevelFoodLastBuyAt = now;
      System.out.println("UPLEVEL FOOD buy level=" + foodLevel + " quantity=" + missing
            + " npc=" + UplevelFoodNpc + " shop=9 index=" + shopIndex);
      return false;
   }

   public void fieldAA(Char var1, byte var2, byte var3) {
      int autoItemLevel = this.getUplevelFoodLevel(var1);
      Char.aHpValue = autoItemLevel;
      Char.aMpValue = autoItemLevel;
      Char.aFoodValue = autoItemLevel;
      Char.isAHP = true;
      Char.isAMP = true;
      Char.isAFood = true;
      if (var1.cHP > 0 && (!uplevelGiftDone || (var1.ctaskId >= 9
            && (!uplevelGiftBoxReady || uplevelGiftItemStage < uplevelGiftItems.length + 2)))) {
         if (var1.ctaskId >= 6) {
            this.uplevelPrepareGift(var1);
            return;
         }
      }
      if (var1.taskMaint != null
            && ((var1.ctaskId == 2 && var1.taskMaint.index == 0)
            || (var1.ctaskId == 3 && var1.taskMaint.index == 1))) {
         Item taskItem = null;
         for (int i = 0; var1.arrItemBag != null && i < var1.arrItemBag.length; i++) {
            Item item = var1.arrItemBag[i];
            if (item != null && item.template != null
                  && (var1.ctaskId == 2 ? item.isTypeWeapon() : item.template.type == UplevelFoodType)) {
               taskItem = item;
               break;
            }
         }
         long now = System.currentTimeMillis();
         if (taskItem != null) {
            if (now - this.uplevelTask2UseSentAt >= 1000L) {
               Service.gI().useItem(taskItem.indexUI);
               this.uplevelTask2UseSentAt = now;
               System.out.println("UPLEVEL TASK" + var1.ctaskId + " use slot=" + taskItem.indexUI
                     + " id=" + taskItem.template.id + " type=" + taskItem.template.type
                     + " quantity=" + taskItem.quantity);
            }
         } else if (now - this.uplevelTask2MissingLogAt >= 5000L) {
            this.uplevelTask2MissingLogAt = now;
            System.out.println("UPLEVEL TASK" + var1.ctaskId + " missing required item; skip LockGame.fieldAO()");
         }
         return;
      }
      if (var1.ctaskId < 9 && this.useUplevelCombatPotions(var1)) {
         return;
      }
      if (var1.ctaskId < 9 && this.pickupUplevelConsumable()) {
         return;
      }
      if (var1.ctaskId < 6) {
         super.fieldAA(var1, var2, var3);
         return;
      }
      if (var1.cHP > 0 && var1.ctaskId >= 9 && !this.ensureUplevelNoelHat(var1)) {
         return;
      }
      if (var1.cHP > 0 && var1.ctaskId >= 9 && !this.ensureUplevelFood(var1)) {
         return;
      }
      if (var1.ctaskId < 9) {
         super.fieldAA(var1, var2, var3);
      } else {
         int var5;
         int var7;
         int var8;
         int var9;
         Item var11;
         int var13;
         int var15;
         int var18;
         int var20;
         Item var21;
         switch(var1.ctaskId) {
         case 9:
            if (var1.nClass.classId != 0) {
               if (TileMap.mapID == 28) {
                  this.fieldAC(-1);
                  this.fieldAB(-1, 1);
                  return;
               } else {
                  this.fieldAA(28, -1, -1, -1);
                  return;
               }
            } else if (this.fieldAV == 0) {
               GameScr.fieldAC("Hãy vào lớp!");
               Code.fieldAG();
               return;
            } else {
               var20 = fieldAW[this.fieldAV];
               if (TileMap.mapID != var20) {
                  this.fieldAA(var20, -2, -1, -1);
                  return;
               } else {
                  GameScr.fieldAB(5, 1, 0);

                  for(var5 = 0; var5 < var1.arrItemBag.length; ++var5) {
                     if ((var11 = var1.arrItemBag[var5]) != null && (var11.template.type == 22 || var11.template.type == 27)) {
                        Service.gI().useItem(var11.indexUI);
                     }
                  }

                  cuong.sleep(1000L);
                  if ((var21 = var1.arrItemBody[1]) != null) {
                     Service.gI().sendAttackMobFast((short)var21.template.type);
                     LockGame.fieldAQ();
                  }

                  GameScr.fieldAB(fieldAX[this.fieldAV], 1, fieldAY[this.fieldAV]);

                  do {
                     cuong.sleep(1000L);
                  } while(Char.fieldAF(fieldAZ[this.fieldAV]) == null);

                  if ((var21 = Char.fieldAF(fieldBA[this.fieldAV])) != null) {
                     Service.gI().useItem(var21.indexUI);
                  }

                  if ((var21 = Char.fieldAF(fieldAZ[this.fieldAV])) != null) {
                     Service.gI().useItem(var21.indexUI);
                  }

                  cuong.sleep(1000L);
                  GameScr.fieldAB(4, 0, 0);

                  for(var15 = 0; var15 < var1.arrItemBag.length; ++var15) {
                     if ((var21 = var1.arrItemBag[var15]) != null && (var21.template.type < 10 || var21.template.type == 16 || var21.template.type == 17 || var21.template.id == 23)) {
                        Service.gI().saleItem1(var21.indexUI, var21.indexUI);
                     }
                  }

                  Service.gI().bagSort();
                  LockGame.fieldAS();
                  return;
               }
            }
         case 10:
            if (var1.taskMaint.index == 0) {
               if (TileMap.mapID == 28) {
                  this.fieldAC(-1);
                  this.fieldAB(5, 1);
                  return;
               }

               this.fieldAA(28, -1, -1, -1);
               return;
            }

            if (var1.taskMaint.index == 1) {
               if (TileMap.mapID == 4) {
                  this.fieldAC(-1);
                  this.fieldAB(6, 1);
                  return;
               }

               this.fieldAA(4, -1, -1, -1);
               return;
            }

            if (var1.taskMaint.index == 2) {
               if (TileMap.mapID == 46) {
                  this.fieldAC(-1);
                  this.fieldAB(7, 1);
                  return;
               }

               this.fieldAA(46, -1, -1, -1);
               return;
            }
            break;
         case 11:
            if (var1.taskMaint.index == 0) {
               if (TileMap.mapID == 28) {
                  this.fieldAC(-1);
                  this.fieldAB(-1, 1);
                  return;
               }

               this.fieldAA(28, -1, -1, -1);
               return;
            }

            if (var1.taskMaint.index == 1) {
               for(var20 = 0; var20 < GameScr.vCharInMap.size(); ++var20) {
                  Char var23;
                  if ((var23 = (Char)GameScr.vCharInMap.elementAt(var20)) != null) {
                     Service.gI().requestForgetPass(var23.cName);
                  }
               }

               var15 = super.fieldAC;
               GameScr var19 = GameScr.gI();
               Npc var22;
               if ((var22 = GameScr.fieldAI(13)) != null && var22.statusMe != 15) {
                  if (Math.abs(var22.cx - Char.getMyChar().cx) > 22 || Math.abs(var22.cy - Char.getMyChar().cy) > 22) {
                     Char.fieldAC(var22.cx, var22.cy);
                  }

                  Service.gI().openUIZone();
                  LockGame.fieldAE();
                  var20 = -1;
                  if (var15 < 0) {
                     var15 = var19.zones.length - 1;
                  } else if (var15 >= var19.zones.length) {
                     var15 = 0;
                  }

                  var5 = 0;

                  for(var18 = (var15 + 1) % var19.zones.length; var18 != var15; var18 = (var18 + 1) % var19.zones.length) {
                     if (var19.zones[var18] < 20 && var19.zones[var18] > var5) {
                        var20 = var18;
                        var5 = var19.zones[var18];
                     }
                  }

                  super.fieldAC = var20;
                  Service.gI().requestChangeZone((int)var20, (int)-1);
                  TileMap.fieldAF();
                  cuong.sleep(100L);
                  return;
               }

               super.fieldAC = TileMap.zoneID;
               return;
            }
            break;
         case 12:
            if (var1.taskMaint.index == 0) {
               if (TileMap.mapID == 3) {
                  this.fieldAC(-1);
                  this.fieldAB(-1, 1);
                  return;
               }

               this.fieldAA(3, -1, -1, -1);
               return;
            }

            boolean var16 = false;
            var5 = -1;
            var11 = null;
            if (var1.taskMaint.index == 1) {
               var16 = true;
               var5 = (new int[]{194, 94, 114, 99, 109, 105, 119})[var1.nClass.classId];
               if ((var11 = var1.arrItemBody[1]) == null) {
                  var16 = false;
                  var11 = Char.fieldAF(var5);
               }
            } else if (var1.taskMaint.index == 2) {
               var16 = true;
               var5 = 174;
               if ((var11 = var1.arrItemBody[9]) == null) {
                  var16 = false;
                  var11 = Char.fieldAF(174);
               }
            } else if (var1.taskMaint.index == 3) {
               var16 = true;
               var5 = var1.cgender == 1 ? 124 : 125;
               if ((var11 = var1.arrItemBody[8]) == null) {
                  var16 = false;
                  var11 = Char.fieldAF(var5);
               }
            }

            if (var11 == null) {
               if (TileMap.mapID == 4) {
                  this.fieldAC(var5);
                  this.fieldAB(-1, 1);
                  return;
               }

               this.fieldAA(4, -1, -1, -1);
               return;
            }

            var13 = 0;
            var18 = 0;
            if (var11.isTypeClothe()) {
               var13 = GameScr.upClothe[var11.upgrade] / 2;
               var18 = GameScr.coinUpClothes[var11.upgrade];
            } else if (var11.isTypeAdorn()) {
               var13 = GameScr.upAdorn[var11.upgrade] / 2;
               var18 = GameScr.coinUpAdorns[var11.upgrade];
            } else if (var11.isTypeWeapon()) {
               var13 = GameScr.upWeapon[var11.upgrade] / 2;
               var18 = GameScr.coinUpWeapons[var11.upgrade];
            }

            int usableUpgradeCrystal = 0;
            for (int crystalIndex = 0; crystalIndex < var1.arrItemBag.length; ++crystalIndex) {
               Item crystal = var1.arrItemBag[crystalIndex];
               if (crystal != null && crystal.template.type == 26 && crystal.template.id <= 3) {
                  usableUpgradeCrystal += GameScr.upClothe[crystal.template.id]
                        * (crystal.quantity > 0 ? crystal.quantity : 1);
               }
            }
            if (var13 << 1 > usableUpgradeCrystal || var18 << 1 > var1.yen) {
               if (TileMap.mapID == 46) {
                  this.fieldAC(1);
                  this.fieldAB(-1, 1);
                  return;
               }

               this.fieldAA(46, -1, -1, -1);
               return;
            }

            if (TileMap.mapID != 22) {
               this.fieldAA(22, -2, -1, -1);
               return;
            }

            if (var16) {
               Service.gI().itemBodyToBag((int)var11.template.type);
               LockGame.fieldAQ();
            }

            var7 = var11.upgrade;
            GameScr.fieldAB(6, 0, 0);
            LockGame.fieldAQ();
            GameScr.itemUpGrade = var11;

            for(var8 = 0; var8 < 2 && var11.upgrade == var7; ++var8) {
               GameScr.arrItemUpGrade = new Item[18];
               var9 = 0;
               int var24 = 0;

               for(var20 = 0; var20 < var1.arrItemBag.length && var24 < var13; ++var20) {
                  if ((var21 = var1.arrItemBag[var20]) != null && uplevelUpgradeCrystal(var21)) {
                     int crystalCopies = var21.quantity > 0 ? var21.quantity : 1;
                     for (int crystalCopy = 0; crystalCopy < crystalCopies
                           && var24 < var13 && var9 < GameScr.arrItemUpGrade.length; ++crystalCopy) {
                        GameScr.arrItemUpGrade[var9++] = var21;
                        var24 += GameScr.upClothe[var21.template.id];
                     }
                     var1.arrItemBag[var20] = null;
                  }
               }

               do {
                  cuong.sleep(3000L);
                  Service.gI().upgradeItem(var11, GameScr.arrItemUpGrade, false);
                  LockGame.fieldAQ();
               } while(GameScr.arrItemUpGrade[0] != null);
            }

            GameScr.itemUpGrade = null;
            Service.gI().useItem(var11.indexUI);
            if (var11.upgrade > var7) {
               LockGame.fieldAO();
               return;
            }
            break;
         case 13:
            Item var4;
            if (var1.arrItemBody[1] == null && var1.nClass != null
                  && var1.nClass.classId > 0 && var1.nClass.classId < fieldAZ.length) {
               Item weapon = Char.fieldAF(fieldAZ[var1.nClass.classId]);
               if (weapon != null) {
                  Service.gI().useItem(weapon.indexUI);
                  LockGame.fieldAQ();
                  // ponytail: skip starter weapon upgrade; add material-response handling before enabling it.
                  this.uplevelTask13WeaponEquipped = true;
                  return;
               }
            }
            if (var1.taskMaint.index == 0) {
               if (TileMap.mapID == 4) {
                  this.fieldAC(-1);
                  this.fieldAB(-1, 1);
                  return;
               }

               this.fieldAA(4, -1, -1, -1);
               return;
            }

            var5 = var1.taskMaint.index == 1 ? 56 : (var1.taskMaint.index == 2 ? 0 : 73);
            if (TileMap.mapID != var5) {
               if (TileMap.mapID != var2) {
                  super.fieldAA(var2, -2, -1, -1);
                  return;
               }

               if (var1.taskMaint.index == 1 && !this.ensureUplevelTeacherHp(var1)) {
                  return;
               }

               if (var1.taskMaint.index != 1 && GameScr.hpPotion < 10
                     && var1.yen >= 300 * (10 - GameScr.hpPotion)) {
                  GameScr.fieldAB(3, 0, 0);
                  Service.gI().buyItem(7, 1, 10 - GameScr.hpPotion);
                  LockGame.fieldAG();
                  return;
               }

               Npc taskNpc = GameScr.fieldAI(var3);
               if (taskNpc == null) {
                  return;
               }
               if (Math.abs(taskNpc.cx - var1.cx) > 22 || Math.abs(taskNpc.cy - var1.cy) > 22) {
                  Char.fieldAC(taskNpc.cx, taskNpc.cy);
                  return;
               }
               GameScr.fieldAB(var3, 4, 0);
               cuong.sleep(500L);
               Service.gI().getTask(var3, 0, -1);
               TileMap.fieldAF();
               return;
            }

            if (var1.taskMaint.index == 1 && this.useUplevelTeacherHp(var1)) {
               return;
            }

            if (var1.taskMaint.index != 1 && var1.cHP < var1.cMaxHP / 2 && var1.cHP > 0) {
               var1.gameAE(16);
            }

            if (var1.cMP < var1.cMaxMP / 2 && var1.cHP > 0) {
               var1.gameAE(17);
            }

            Char var14 = null;
            for (int targetIndex = 0; targetIndex < GameScr.vCharInMap.size(); targetIndex++) {
               Char target = (Char)GameScr.vCharInMap.elementAt(targetIndex);
               if (target != null && target.cHP > 0 && target.statusMe != 15 && !target.isInvisible) {
                  var14 = target;
                  break;
               }
            }
            if (var14 != null) {
               Skill var17 = Auto.fieldAL;
               if (var17 == null) {
                  return;
               }
               if (Res.abs(var1.cx - var14.cx) > var17.dx || Res.abs(var1.cy - var14.cy) > var17.dy) {
                  Char.fieldAC(var14.cx < TileMap.pxw ? var14.cx : TileMap.pxw - 50, var14.cy);
                  return;
               }

               if (var1.cTypePk != 3) {
                  Service.gI().changePk(3);
                  return;
               }

               Auto.fieldAP.removeAllElements();
               Auto.fieldAQ.removeAllElements();
               Auto.fieldAQ.addElement(var14);
               Service.gI().selectSkill(var17.template.id);
               Service.gI().sendPlayerAttack((MyVector)Auto.fieldAP, (MyVector)Auto.fieldAQ, (int)1);
               if (System.currentTimeMillis() - var17.lastTimeUseThisSkill >= (long)var17.coolDown) {
                  var17.lastTimeUseThisSkill = System.currentTimeMillis();
                  var17.paintCanNotUseSkill = true;
                  var1.gameAB(GameScr.sks[var17.template.id], 0);
                  return;
               }
            }
            break;
         case 14:
            if (var1.clevel >= 15 && (var11 = Char.fieldAF(fieldBB[var1.nClass.classId])) != null) {
               GameScr.fieldAC("Học sách kĩ năng");
               Service.gI().useItem(var11.indexUI);
               cuong.sleep(1000L);
            }

            if (var1.taskMaint.index == 0) {
               if (TileMap.mapID == 29) {
                  this.fieldAC(-1);
                  this.fieldAB(-1, 1);
                  return;
               }

               this.fieldAA(29, -1, -1, -1);
               return;
            }

            if (var1.taskMaint.index == 1) {
               if (TileMap.mapID == 29) {
                  var5 = -1;
                  ItemMap var12 = null;

                  if (uplevelTask14PendingItem >= 0) {
                     ItemMap pending = null;
                     for (int pendingIndex = 0; pendingIndex < GameScr.vItemMap.size(); ++pendingIndex) {
                        ItemMap candidate = (ItemMap)GameScr.vItemMap.elementAt(pendingIndex);
                        if (candidate != null && candidate.itemMapID == uplevelTask14PendingItem) {
                           pending = candidate;
                           break;
                        }
                     }
                     if (pending == null) {
                        uplevelTask14PendingItem = -1;
                        return;
                     }
                     if (System.currentTimeMillis() - uplevelTask14PickSentAt < 5000L) {
                        return;
                     }
                     pending.fieldAK = false;
                     uplevelTask14PendingItem = -1;
                  }

                  for(var13 = 0; var13 < GameScr.vItemMap.size(); ++var13) {
                     ItemMap var6;
                     var7 = Math.abs((var6 = (ItemMap)GameScr.vItemMap.elementAt(var13)).x - var1.cx);
                     var8 = Math.abs(var6.y - var1.cy);
                     var9 = var7 * var7 + var8 * var8;
                     if (!var6.fieldAK && var6.template.id == 212 && (Char.fieldBF() > 2 || Char.fieldAJ(212)) && (var5 < 0 || var9 < var5)) {
                        var5 = var9;
                        var12 = var6;
                     }
                  }

                  if (var12 == null) {
                     super.fieldAC = (super.fieldAC + 1) % 30;
                     return;
                  }

                  if (Math.abs(var12.xEnd - var1.cx) > 22 || Math.abs(var12.yEnd - var1.cy) > 22) {
                     Char.fieldAC(var12.xEnd, var12.yEnd);
                     return;
                  }
                  uplevelTask14PendingItem = var12.itemMapID;
                  uplevelTask14PickSentAt = System.currentTimeMillis();
                  Service.gI().pickItem(var12.itemMapID);

                  for(var13 = 0; var13 < 5 && !LockGame.fieldAC(); ++var13) {
                  }

                  var12.fieldAK = true;
                  return;
               }

               this.fieldAA(29, super.fieldAC, -1, -1);
               return;
            }

            if (var1.taskMaint.index == 2) {
               if (TileMap.mapID == 40) {
                  this.fieldAB(15, 1);
                  this.fieldAC(213);
                  return;
               }

               this.fieldAA(40, -1, -1, -1);
               return;
            }
            break;
         case 15:
            if (var1.taskMaint.index == 0) {
               if (TileMap.mapID == 8) {
                  this.fieldAC(-1);
                  this.fieldAB(-1, 1);
                  return;
               }

               this.fieldAA(8, -1, -1, -1);
               return;
            }

            if (TileMap.mapID != var2) {
               super.fieldAA(var2, -2, -1, -1);
               return;
            }

            GameScr.fieldAB(var3, 0, 0);
            LockGame.fieldAO();
            Auto.fieldAH();
            return;
         case 16:
            if (var1.clevel >= 20 && (var11 = Char.fieldAF(fieldBC[var1.nClass.classId])) != null) {
               GameScr.fieldAC("Học sách kĩ năng");
               Service.gI().useItem(var11.indexUI);
               cuong.sleep(1000L);
            }

            if (var1.taskMaint.index == 0) {
               if (TileMap.mapID == 8) {
                  this.fieldAC(-1);
                  this.fieldAB(-1, 1);
                  return;
               }

               this.fieldAA(8, -1, -1, -1);
               return;
            }

            if (var1.taskMaint.index == 1) {
               if (TileMap.mapID == 63) {
                  this.fieldAC(-1);
                  this.fieldAB(23, 1);
                  return;
               }

               this.fieldAA(63, -1, -1, -1);
               return;
            }

            if (var1.taskMaint.index == 2) {
               if (TileMap.mapID == 47) {
                  this.fieldAC(-1);
                  this.fieldAB(24, 1);
                  return;
               }

               this.fieldAA(47, -1, -1, -1);
            }
         }

      }
   }

   public String toString() {
      return "Auto Nhiệm Vụ 20";
   }
}
