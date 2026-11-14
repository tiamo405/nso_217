public class As20 extends As10 {
   private static final int UplevelNoelMap = 72;
   private static final int UplevelGooshoNpc = 30;
   // Nấm linh chi: x2 kinh nghiệm trong 5 giờ, mua ở Goosho -> Cửa hàng (nút đầu tiên, shop 14).
   private static final int UplevelMushroomItemId = 248;
   private static final int UplevelMushroomPrice = 10;
   private static final int UplevelMushroomShop = 14;
   private static final long UplevelMushroomDuration = 5L * 60L * 60L * 1000L;
   private static final long UplevelMushroomRetryDelay = 10L * 60L * 1000L;
   // Effect id của nấm: 22 (type 18, 18000s) — đo trên acctest2 ngày 2026-11-07.
   // Vẫn tự học lại nếu đặt -1; không reset theo acc.
   private static int learnedMushroomEffectId = 22;
   private static final int UplevelNoelHatMaleId = 351;
   private static final int UplevelNoelHatFemaleId = 352;
   private static final int UplevelTeacherHpId = 14;
   private static final int UplevelTeacherHpQuantity = 10000;
   private static final int UplevelFoodType = 18;
   private static final int UplevelFoodNpc = 4;
   private static final int UplevelFoodStock = 2;
   private static final int UplevelTask16LevelTarget = 22;
   private static final int UplevelTask16ScorpionMob = 23;
   private static final int UplevelTask16SnakeMob = 24;
   private static final int UplevelTask16MobCount = 250;
   private static final int UplevelTask17ToneMap = 22;
   private static final int UplevelTask17FishingVillageMap = 32;
   private static final int UplevelTask17NaoNpc = 16;
   private static final int UplevelTask17LevelTarget = 25;
   private static final int UplevelTask17LevelMap = 33;
   private static final int UplevelTask17JaianNpc = 17;
   private static final int UplevelTask17PotionNpc = 3;
   private static final int UplevelTask17PotionShop = 7;
   private static final int UplevelTask17PotionStock = 20;
   private static final int UplevelTask17FollowDistance = 90;
   private static final int UplevelTask17FightRadius = 100;
   private int fieldAV;
   private boolean uplevelTask13WeaponEquipped;
   // Nhiệm vụ 12 (nâng cấp trang bị): 0 = đạt cấp 12, 1 = vũ khí, 2 = trang sức 174, 3 = y phục 124/125,
   // 4 = báo cáo hiệu trưởng (As10). Đồ và đá cấp 6 (id 5) lấy từ hành trang; thiếu thì treo + log.
   private static final int UplevelTask12Level = 12;
   private static final int UplevelTask12StoneId = 5;
   private static final int UplevelTask12AdornId = 174;
   private static final int UplevelTask12UpgradeNpc = 6;
   private static final int UplevelTask12UpgradeMap = 22;
   private static final long UplevelTask12WaitMs = 10000L;
   private static final long UplevelTask12HangLogMs = 30000L;
   private long uplevelTask12ActionAt;
   private int uplevelTask12UpgradeFrom = -1;
   private Item uplevelTask12UpgradeItem;
   private Item[] uplevelTask12UpgradeStones;
   private long uplevelTask12HangLogAt;
   private int uplevelTask12Tries;
   private static long uplevelTask12MessageUntil;
   // TEST: -Duplevel.testWeaponMax=N → sau khi đủ cấp 12, đập vũ khí phái lên +N bằng đá cấp 6 (kiểm tra
   // nhánh đập thất bại / tách đá mới). 0 = tắt.
   private static final int UplevelTestWeaponMax = Integer.getInteger("uplevel.testWeaponMax", 0);
   // Món cần đập khi test: 1 = vũ khí, 2 = trang sức 174, 3 = y phục 124/125.
   private static final int UplevelTestPhase = Integer.getInteger("uplevel.testPhase", 1);
   private boolean uplevelTestWeaponDone;
   private int uplevelTask14PendingItem = -1;
   private long uplevelTask14PickSentAt;
   // Nhiệm vụ 14 bước 1: Khoáng thạch (212) tự xuất hiện ở map 29, nhiều bot chung khu phải chờ nhau
   // -> khu hiện tại không còn Khoáng thạch thì chuyển sang khu kế tiếp (bỏ qua khu đầy).
   private static final int UplevelTask14ZoneNpc = 13;
   // Client tô đỏ khu có >= 20 người (GameScr paint danh sách khu) = khu đầy.
   private static final int UplevelTask14ZoneFullPlayers = 20;
   private static final long UplevelTask14ZoneEmptyWait = 2500L;
   private static final long UplevelTask14ZoneBlockMs = 60000L;
   private final long[] uplevelTask14ZoneBlockedUntil = new long[128];
   private long uplevelTask14ZoneEmptySince;
   private int uplevelTask14ZoneEmptyZone = -1;
   private long uplevelTask14ZoneLogAt;
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
   private int uplevelTask16LoggedIndex = -1;
   private int uplevelTask16LoggedLevel = -1;
   private int uplevelTask16LoggedCount = -1;
   private int uplevelTask17LoggedIndex = -1;
   private int uplevelTask17LoggedCount = -1;
   private long uplevelTask17WaitingLogAt;
   private long uplevelTask17EntryLogAt;
   private long uplevelTask17RequestSentAt;
   private long uplevelTask17NpcLogAt;
   private boolean uplevelTask17PotionReady;
   private boolean uplevelTask17ShopRequested;
   private long uplevelTask17ShopActionAt;
   private int uplevelTask17BuyType = -1;
   private int uplevelTask17BuyBefore;
   private boolean uplevelTask17ReindeerDone;
   private int uplevelTask17ReindeerAttempts;
   private long uplevelTask17ReindeerSentAt;
   private boolean uplevelTask17Talked;
   private long uplevelTask17TalkAt;
   private int uplevelTask17TalkAttempts;
   private boolean uplevelTask17EscortSeen;
   private long uplevelTask17EscortLostAt;
   private long uplevelTask17EscortLogAt;
   private long uplevelTask17AttackAt;
   private long uplevelTask17PotionUseAt;
   private long uplevelTask17Phase2LogAt;
   private long uplevelMushroomUseSentAt;
   private int uplevelMushroomUseAttempts;
   private int uplevelMushroomQuantityBefore;
   private int[] uplevelMushroomEffectsBefore;
   private long uplevelMushroomConfirmedAt;
   private long uplevelMushroomRetryAt;
   private long uplevelMushroomLogAt;
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

   // Hoàn thành khi đã trả nhiệm vụ 17 (hộ tống Jaian) cho Bà Rei: server chuyển sang nhiệm vụ 18.
   private static final int UplevelDoneTaskId = 18;

   public boolean fieldAA(Char var1) {
      return var1.ctaskId >= UplevelDoneTaskId;
   }

   /**
    * Nhiệm vụ 14 bước 1 ở map 29: khu hiện tại không còn Khoáng thạch (212) quá
    * UplevelTask14ZoneEmptyWait thì mở danh sách khu (NPC 13 hoặc item 35/37) và chuyển
    * sang khu kế tiếp (zoneID + 1, vòng lại 0). Bỏ qua khu đầy (>= 20 người) và khu vừa
    * vào không được (server từ chối, chặn 60s).
    */
   private void switchUplevelTask14Zone(Char me) {
      long now = System.currentTimeMillis();
      if (this.uplevelTask14ZoneEmptyZone != TileMap.zoneID || this.uplevelTask14ZoneEmptySince == 0L) {
         this.uplevelTask14ZoneEmptyZone = TileMap.zoneID;
         this.uplevelTask14ZoneEmptySince = now;
         return;
      }
      if (now - this.uplevelTask14ZoneEmptySince < UplevelTask14ZoneEmptyWait) return;

      int itemIndex = -1;
      Npc zoneNpc = GameScr.fieldAI(UplevelTask14ZoneNpc);
      if (zoneNpc == null || zoneNpc.statusMe == 15) {
         if ((itemIndex = Char.fieldAI(37)) < 0) itemIndex = Char.fieldAI(35);
         if (itemIndex < 0) {
            if (now - this.uplevelTask14ZoneLogAt >= 10000L) {
               this.uplevelTask14ZoneLogAt = now;
               System.out.println("UPLEVEL TASK14 zone switch unavailable map=" + TileMap.mapID
                     + " zone=" + TileMap.zoneID + " (no npc " + UplevelTask14ZoneNpc + ", no item 35/37)");
            }
            return;
         }
      } else if (Math.abs(zoneNpc.cx - me.cx) > 22 || Math.abs(zoneNpc.cy - me.cy) > 22) {
         Char.fieldAC(zoneNpc.cx, zoneNpc.cy);
         return;
      }

      GameScr screen = GameScr.gI();
      int[] before = screen.zones;
      Service.gI().openUIZone();
      LockGame.fieldAE();
      int[] zones = screen.zones;
      if (zones == null || zones.length == 0 || zones == before) {
         if (now - this.uplevelTask14ZoneLogAt >= 5000L) {
            this.uplevelTask14ZoneLogAt = now;
            System.out.println("UPLEVEL TASK14 zone list not received map=" + TileMap.mapID
                  + " zone=" + TileMap.zoneID);
         }
         return;
      }

      int current = TileMap.zoneID;
      int target = -1;
      StringBuilder skipped = new StringBuilder();
      for (int step = 1; step < zones.length; step++) {
         int zone = (current + step) % zones.length;
         boolean blocked = zone < this.uplevelTask14ZoneBlockedUntil.length
               && this.uplevelTask14ZoneBlockedUntil[zone] > now;
         if (blocked || zones[zone] >= UplevelTask14ZoneFullPlayers) {
            skipped.append(' ').append(zone).append(blocked ? "(blocked)" : "(" + zones[zone] + ")");
            continue;
         }
         target = zone;
         break;
      }
      if (target < 0) {
         screen.resetButton();
         this.uplevelTask14ZoneEmptySince = now;
         System.out.println("UPLEVEL TASK14 zone all full/blocked; stay zone=" + current
               + " skipped=" + skipped.toString().trim());
         return;
      }

      System.out.println("UPLEVEL TASK14 zone switch " + current + " -> " + target
            + " players=" + zones[target] + "/" + zones.length + " zones"
            + (skipped.length() > 0 ? " skipped=" + skipped.toString().trim() : "")
            + (itemIndex >= 0 ? " item=" + itemIndex : " npc=" + UplevelTask14ZoneNpc));
      Service.gI().requestChangeZone(target, itemIndex);
      TileMap.fieldAF();

      if (TileMap.zoneID == target) {
         System.out.println("UPLEVEL TASK14 zone entered " + target + " map=" + TileMap.mapID);
      } else {
         screen.resetButton();
         if (target < this.uplevelTask14ZoneBlockedUntil.length) {
            this.uplevelTask14ZoneBlockedUntil[target] = System.currentTimeMillis() + UplevelTask14ZoneBlockMs;
         }
         System.out.println("UPLEVEL TASK14 zone refused " + target + " stay=" + TileMap.zoneID
               + " waited=" + (System.currentTimeMillis() - now) + "ms; block "
               + (UplevelTask14ZoneBlockMs / 1000L) + "s");
      }
      this.uplevelTask14ZoneEmptyZone = TileMap.zoneID;
      this.uplevelTask14ZoneEmptySince = System.currentTimeMillis();
   }

   private void logUplevelTask16(Char me) {
      if (me.taskMaint == null) return;
      int index = me.taskMaint.index;
      if (index == this.uplevelTask16LoggedIndex
            && (index != 0 || me.clevel == this.uplevelTask16LoggedLevel)
            && me.taskMaint.count == this.uplevelTask16LoggedCount) {
         return;
      }
      this.uplevelTask16LoggedIndex = index;
      this.uplevelTask16LoggedLevel = me.clevel;
      this.uplevelTask16LoggedCount = me.taskMaint.count;
      if (index == 0) {
         System.out.println("UPLEVEL TASK16 phase=level " + UplevelTask16LevelTarget
               + " map=8 currentLevel=" + me.clevel);
      } else if (index == 1) {
         System.out.println("UPLEVEL TASK16 phase=map63 mob=" + UplevelTask16ScorpionMob
               + " count=" + UplevelTask16MobCount + " progress=" + me.taskMaint.count);
      } else if (index == 2) {
         System.out.println("UPLEVEL TASK16 phase=map47 mob=" + UplevelTask16SnakeMob
               + " count=" + UplevelTask16MobCount + " progress=" + me.taskMaint.count);
      } else {
         System.out.println("UPLEVEL TASK16 phase index=" + index
               + " progress=" + me.taskMaint.count);
      }
   }

   private void logUplevelTask17(Char me) {
      if (me.taskMaint == null) return;
      int index = me.taskMaint.index;
      int count = me.taskMaint.count;
      if (index == this.uplevelTask17LoggedIndex && count == this.uplevelTask17LoggedCount) {
         return;
      }
      this.uplevelTask17LoggedIndex = index;
      this.uplevelTask17LoggedCount = count;
      StringBuilder log = new StringBuilder("UPLEVEL TASK17 index=").append(index);
      if (me.taskMaint.subNames != null) {
         for (int i = 0; i < me.taskMaint.subNames.length; i++) {
            log.append(" phase").append(i).append("=\"")
                  .append(me.taskMaint.subNames[i]).append("\"");
            if (me.taskMaint.counts != null && i < me.taskMaint.counts.length) {
               log.append(" target=").append(me.taskMaint.counts[i]);
            }
         }
      }
      log.append(" progress=").append(count);
      if (me.taskMaint.names != null) {
         log.append(" name=\"").append(String.join(" ", me.taskMaint.names)).append('"');
      }
      if (me.taskMaint.details != null) {
         log.append(" detail=\"").append(String.join(" ", me.taskMaint.details)).append('"');
      }
      System.out.println(log.toString());
   }

   /**
    * Map 33 nằm cạnh Làng chài (32). Đi thẳng bằng tìm đường tự động sẽ chọn
    * tuyến 47 -> 48 mà server chặn ở nhiệm vụ 17, nên đi theo tuyến đã dùng
    * khi nhận nhiệm vụ: trường -> Tone (22) -> Làng chài (32) -> 33.
    */
   private void routeUplevelTask17LevelMap(Char me) {
      int schoolMap = this.getUplevelSchoolMap(me);
      int next;
      if (TileMap.mapID == UplevelTask17FishingVillageMap) {
         next = UplevelTask17LevelMap;
      } else if (TileMap.mapID == UplevelTask17ToneMap) {
         next = UplevelTask17FishingVillageMap;
      } else if (TileMap.mapID == schoolMap) {
         next = UplevelTask17ToneMap;
      } else {
         next = schoolMap;
      }
      long now = System.currentTimeMillis();
      if (now - this.uplevelTask17WaitingLogAt >= 5000L) {
         this.uplevelTask17WaitingLogAt = now;
         System.out.println("UPLEVEL TASK17 route level map=" + TileMap.mapID + " -> " + next
               + " target=" + UplevelTask17LevelMap + " level=" + me.clevel
               + "/" + UplevelTask17LevelTarget);
      }
      this.fieldAA(next, -2, -1, -1);
   }

   // ===== Nhiệm vụ 17 bước 1: hộ tống Jaian (NPC 17, map 33) =====

   private static String lower(String v) {
      return v == null ? "" : v.toLowerCase();
   }

   private static String describeUplevelNpc(Npc npc) {
      return (npc.template == null ? -1 : npc.template.npcTemplateId) + ":"
            + (npc.template == null ? "?" : npc.template.name) + "/" + npc.cName
            + "@" + npc.cx + "," + npc.cy;
   }

   private static String snapshotUplevelMap() {
      StringBuilder sb = new StringBuilder("npcs=[");
      for (int i = 0; i < GameScr.vNpc.size(); i++) {
         Npc npc = (Npc)GameScr.vNpc.elementAt(i);
         if (npc == null) continue;
         if (i > 0) sb.append(", ");
         sb.append(describeUplevelNpc(npc));
      }
      sb.append("] chars=[");
      for (int i = 0; i < GameScr.vCharInMap.size(); i++) {
         Char c = (Char)GameScr.vCharInMap.elementAt(i);
         if (c == null) continue;
         if (i > 0) sb.append(", ");
         sb.append(c.cName).append("@").append(c.cx).append(",").append(c.cy);
      }
      return sb.append("]").toString();
   }

   /**
    * Jaian hộ tống là nhân vật (vCharInMap) tên "Jaian" do server sinh ra sau khi nói
    * chuyện với NPC 17; NPC 17 gốc vẫn đứng nguyên. cName chỉ là "Jaian" cho mọi người
    * chơi; Jaian của mình có charID == -myChar.charID (client dựa vào đó để vẽ dòng
    * "do <tên> hộ tống", Char.java). Jaian của người khác bỏ qua.
    */
   private static Char findUplevelEscort(Char me) {
      for (int i = 0; i < GameScr.vCharInMap.size(); i++) {
         Char c = (Char)GameScr.vCharInMap.elementAt(i);
         if (c == null || c == me) continue;
         if (me.charID != 0 && c.charID == -me.charID) return c;
      }
      return null;
   }

   /** Chẩn đoán: liệt kê mọi Jaian trên map, mine=true khi charID == -myChar.charID. */
   private static String describeUplevelJaians(Char me) {
      StringBuilder sb = new StringBuilder("myId=" + me.charID + " jaians=[");
      int n = 0;
      for (int i = 0; i < GameScr.vCharInMap.size(); i++) {
         Char c = (Char)GameScr.vCharInMap.elementAt(i);
         if (c == null || c == me || !lower(c.cName).contains("jaian")) continue;
         if (n++ > 0) sb.append(", ");
         sb.append(c.cName).append("#").append(c.charID).append("@").append(c.cx).append(",").append(c.cy)
               .append(" hp=").append(c.cHP).append("/").append(c.cMaxHP)
               .append(" mine=").append(c.charID == -me.charID);
      }
      return sb.append("]").toString();
   }

   private static Item bestUplevelPotion(Item[] items, int type, int maxLevel) {
      Item best = null;
      for (int i = 0; items != null && i < items.length; i++) {
         Item item = items[i];
         if (item == null || item.template == null || item.template.type != type
               || item.template.level > maxLevel) continue;
         if (best == null || item.template.level > best.template.level) best = item;
      }
      return best;
   }

   private static int countUplevelPotion(Char me, int type) {
      int total = 0;
      for (int i = 0; me.arrItemBag != null && i < me.arrItemBag.length; i++) {
         Item item = me.arrItemBag[i];
         if (item != null && item.template != null && item.template.type == type
               && item.template.level <= me.clevel) total += item.quantity;
      }
      return total;
   }

   /** Mua đủ bình HP (type 16) ở NPC 3, shop 7 (không dùng bình MP). */
   private boolean ensureUplevelTask17Potions(Char me) {
      if (this.uplevelTask17PotionReady) return true;
      long now = System.currentTimeMillis();
      int hp = countUplevelPotion(me, 16);

      if (this.uplevelTask17BuyType >= 0) {
         int current = hp;
         if (current > this.uplevelTask17BuyBefore) {
            System.out.println("UPLEVEL TASK17 potion bought type=" + this.uplevelTask17BuyType
                  + " count=" + this.uplevelTask17BuyBefore + "->" + current + " yen=" + me.yen);
            this.uplevelTask17BuyType = -1;
         } else if (now - this.uplevelTask17ShopActionAt < 8000L) {
            return false;
         } else {
            System.out.println("UPLEVEL TASK17 potion buy timeout type=" + this.uplevelTask17BuyType
                  + " count=" + current + " yen=" + me.yen + "; bỏ qua mua thêm");
            this.uplevelTask17BuyType = -1;
            this.uplevelTask17PotionReady = true;
            return true;
         }
      }

      if (hp >= UplevelTask17PotionStock) {
         System.out.println("UPLEVEL TASK17 potion ready hp=" + hp);
         this.uplevelTask17PotionReady = true;
         return true;
      }

      if (!TileMap.isLang(TileMap.mapID) && !TileMap.isTruong(TileMap.mapID)) {
         if (now - this.uplevelTask17WaitingLogAt >= 5000L) {
            this.uplevelTask17WaitingLogAt = now;
            System.out.println("UPLEVEL TASK17 potion route map=" + TileMap.mapID + " -> "
                  + UplevelTask17FishingVillageMap + " hp=" + hp);
         }
         this.fieldAA(UplevelTask17FishingVillageMap, -2, -1, -1);
         return false;
      }

      Npc seller = GameScr.fieldAI(UplevelTask17PotionNpc);
      if (seller == null) {
         if (now - this.uplevelTask17NpcLogAt >= 5000L) {
            this.uplevelTask17NpcLogAt = now;
            System.out.println("UPLEVEL TASK17 potion npc missing npc=" + UplevelTask17PotionNpc
                  + " map=" + TileMap.mapID + " " + snapshotUplevelMap());
         }
         return false;
      }
      if (Math.abs(seller.cx - me.cx) > 22 || Math.abs(seller.cy - me.cy) > 22) {
         Char.fieldAC(seller.cx, seller.cy);
         return false;
      }

      if (!this.uplevelTask17ShopRequested) {
         GameScr.arrItemStackLock = null;
         GameScr.fieldAB(UplevelTask17PotionNpc, 0, 0);
         Service.gI().requestItem(UplevelTask17PotionShop);
         this.uplevelTask17ShopRequested = true;
         this.uplevelTask17ShopActionAt = now;
         System.out.println("UPLEVEL TASK17 potion shop open npc=" + UplevelTask17PotionNpc
               + " shop=" + UplevelTask17PotionShop + " map=" + TileMap.mapID);
         return false;
      }
      if (GameScr.arrItemStackLock == null) {
         if (now - this.uplevelTask17ShopActionAt >= 8000L) {
            System.out.println("UPLEVEL TASK17 potion shop timeout; thử lại");
            this.uplevelTask17ShopRequested = false;
         }
         return false;
      }

      int type = 16;
      Item offer = bestUplevelPotion(GameScr.arrItemStackLock, type, me.clevel);
      if (offer == null) {
         StringBuilder shop = new StringBuilder();
         for (int i = 0; i < GameScr.arrItemStackLock.length; i++) {
            Item it = GameScr.arrItemStackLock[i];
            if (it == null || it.template == null) continue;
            shop.append(' ').append(it.template.id).append(':').append(it.template.name)
                  .append("/t").append(it.template.type).append("/lv").append(it.template.level);
         }
         System.out.println("UPLEVEL TASK17 potion not found type=" + type + " shop=" + shop
               + "; bỏ qua mua thêm");
         this.uplevelTask17PotionReady = true;
         return true;
      }
      int current = hp;
      int quantity = UplevelTask17PotionStock - current;
      this.uplevelTask17BuyType = type;
      this.uplevelTask17BuyBefore = current;
      this.uplevelTask17ShopActionAt = now;
      Service.gI().buyItem(offer.typeUI, offer.indexUI, quantity);
      System.out.println("UPLEVEL TASK17 potion buy type=" + type + " item=" + offer.template.id
            + ":" + offer.template.name + " lv=" + offer.template.level
            + " quantity=" + quantity + " shopIndex=" + offer.indexUI + " yen=" + me.yen);
      return false;
   }

   /** Dùng item Tuần lộc (tăng HP/MP) một lần trước khi hộ tống. */
   private boolean ensureUplevelTask17Reindeer(Char me) {
      if (this.uplevelTask17ReindeerDone) return true;
      Item reindeer = null;
      for (int i = 0; me.arrItemBag != null && i < me.arrItemBag.length; i++) {
         Item item = me.arrItemBag[i];
         if (item != null && item.template != null
               && lower(item.template.name).contains("tuần lộc")) {
            reindeer = item;
            break;
         }
      }
      long now = System.currentTimeMillis();
      if (reindeer == null) {
         System.out.println("UPLEVEL TASK17 reindeer "
               + (this.uplevelTask17ReindeerAttempts > 0 ? "used" : "not in bag")
               + " hp=" + me.cHP + "/" + me.cMaxHP + " mp=" + me.cMP + "/" + me.cMaxMP);
         this.uplevelTask17ReindeerDone = true;
         return true;
      }
      if (this.uplevelTask17ReindeerAttempts > 0 && now - this.uplevelTask17ReindeerSentAt < 3000L) {
         return false;
      }
      if (this.uplevelTask17ReindeerAttempts >= 3) {
         System.out.println("UPLEVEL TASK17 reindeer still in bag after 3 uses id="
               + reindeer.template.id + " quantity=" + reindeer.quantity + "; tiếp tục nhiệm vụ");
         this.uplevelTask17ReindeerDone = true;
         return true;
      }
      ++this.uplevelTask17ReindeerAttempts;
      this.uplevelTask17ReindeerSentAt = now;
      Service.gI().useItem(reindeer.indexUI);
      System.out.println("UPLEVEL TASK17 reindeer use id=" + reindeer.template.id + ":"
            + reindeer.template.name + " bagIndex=" + reindeer.indexUI
            + " quantity=" + reindeer.quantity + " hp=" + me.cHP + "/" + me.cMaxHP
            + " mp=" + me.cMP + "/" + me.cMaxMP);
      return false;
   }

   private boolean useUplevelTask17Potions(Char me) {
      if (me.cHP <= 0) return false;
      long now = System.currentTimeMillis();
      if (now - this.uplevelTask17PotionUseAt < 1000L) return false;
      Item potion = null;
      String kind = null;
      if ((long)me.cHP * 100L < (long)me.cMaxHP * 80L) {
         potion = bestUplevelPotion(me.arrItemBag, 16, me.clevel);
         kind = "HP";
      }
      if (potion == null) return false;
      this.uplevelTask17PotionUseAt = now;
      Service.gI().useItem(potion.indexUI);
      System.out.println("UPLEVEL TASK17 potion use " + kind + " id=" + potion.template.id
            + " hp=" + me.cHP + "/" + me.cMaxHP + " mp=" + me.cMP + "/" + me.cMaxMP
            + " left=" + (potion.quantity - 1));
      return true;
   }

   private void doUplevelTask17Escort(Char me) {
      long now = System.currentTimeMillis();
      Char escort = TileMap.mapID == UplevelTask17LevelMap ? findUplevelEscort(me) : null;

      if (escort != null) {
         if (!this.uplevelTask17EscortSeen) {
            System.out.println("UPLEVEL TASK17 escort start name=" + escort.cName
                  + "#" + escort.charID + " pos=" + escort.cx + "," + escort.cy
                  + " picked_mine=" + (escort.charID == -me.charID) + " " + describeUplevelJaians(me));
         }
         this.uplevelTask17EscortSeen = true;
         this.uplevelTask17EscortLostAt = 0L;
         this.followUplevelTask17Escort(me, escort, now);
         return;
      }

      if (this.uplevelTask17EscortSeen) {
         // Jaian biến mất khỏi map: chờ server chuyển bước (index 1 -> 2).
         if (this.uplevelTask17EscortLostAt == 0L) {
            this.uplevelTask17EscortLostAt = now;
            System.out.println("UPLEVEL TASK17 escort gone map=" + TileMap.mapID
                  + " pos=" + me.cx + "," + me.cy + " progress=" + me.taskMaint.count
                  + " index=" + me.taskMaint.index
                  + " " + snapshotUplevelMap());
         }
         if (now - this.uplevelTask17EscortLostAt < 30000L) {
            this.useUplevelTask17Potions(me);
            return;
         }
         System.out.println("UPLEVEL TASK17 escort gone but task still index=1; "
               + "coi như thất bại, nhận lại hộ tống");
         this.uplevelTask17EscortSeen = false;
         this.uplevelTask17Talked = false;
         this.uplevelTask17TalkAttempts = 0;
         this.uplevelTask17EscortLostAt = 0L;
         return;
      }

      // Chuẩn bị: bình HP/MP rồi dùng Tuần lộc.
      if (!this.ensureUplevelTask17Potions(me)) return;
      if (!this.ensureUplevelTask17Reindeer(me)) return;

      if (TileMap.mapID != UplevelTask17LevelMap) {
         this.uplevelTask17Talked = false;
         this.routeUplevelTask17LevelMap(me);
         return;
      }

      Npc jaian = GameScr.fieldAI(UplevelTask17JaianNpc);
      if (jaian == null) {
         if (now - this.uplevelTask17NpcLogAt >= 5000L) {
            this.uplevelTask17NpcLogAt = now;
            System.out.println("UPLEVEL TASK17 jaian missing npc=" + UplevelTask17JaianNpc
                  + " map=" + TileMap.mapID + " " + snapshotUplevelMap());
         }
         return;
      }
      if (Math.abs(jaian.cx - me.cx) > 22 || Math.abs(jaian.cy - me.cy) > 22) {
         Char.fieldAC(jaian.cx, jaian.cy);
         return;
      }
      if (this.uplevelTask17Talked && now - this.uplevelTask17TalkAt < 6000L) {
         return;
      }
      if (this.uplevelTask17Talked) {
         this.uplevelTask17Talked = false;
         System.out.println("UPLEVEL TASK17 talk no escort after 6s attempt="
               + this.uplevelTask17TalkAttempts + " " + snapshotUplevelMap());
      }
      // Hồi HP trước khi nhận hộ tống.
      if ((long)me.cHP * 100L < (long)me.cMaxHP * 80L) {
         this.useUplevelTask17Potions(me);
         if (now - this.uplevelTask17WaitingLogAt >= 5000L) {
            this.uplevelTask17WaitingLogAt = now;
            System.out.println("UPLEVEL TASK17 wait hp before talk hp=" + me.cHP + "/" + me.cMaxHP);
         }
         return;
      }
      if (this.uplevelTask17TalkAttempts >= 3) {
         if (now - this.uplevelTask17NpcLogAt >= 10000L) {
            this.uplevelTask17NpcLogAt = now;
            System.out.println("UPLEVEL TASK17 talk failed 3 times; dừng chờ kiểm tra menu="
                  + (jaian.template == null || jaian.template.menu == null ? "null"
                  : java.util.Arrays.deepToString(jaian.template.menu)));
         }
         return;
      }
      ++this.uplevelTask17TalkAttempts;
      this.uplevelTask17Talked = true;
      this.uplevelTask17TalkAt = now;
      System.out.println("UPLEVEL TASK17 talk npc=" + UplevelTask17JaianNpc + " attempt="
            + this.uplevelTask17TalkAttempts + " npc=" + describeUplevelNpc(jaian) + " menu="
            + (jaian.template == null || jaian.template.menu == null ? "null"
            : java.util.Arrays.deepToString(jaian.template.menu))
            + " hp=" + me.cHP + "/" + me.cMaxHP);
      GameScr.fieldAB(UplevelTask17JaianNpc, 0, 0);
      Service.gI().getTask(UplevelTask17JaianNpc, 0, -1);
   }

   private void followUplevelTask17Escort(Char me, Char escort, long now) {
      int dist = Res.gameAA(me.cx, me.cy, escort.cx, escort.cy);
      if (now - this.uplevelTask17EscortLogAt >= 3000L) {
         this.uplevelTask17EscortLogAt = now;
         System.out.println("UPLEVEL TASK17 escort follow me=" + me.cx + "," + me.cy
               + " jaian=" + escort.cx + "," + escort.cy + " dist=" + dist
               + " jaianHp=" + escort.cHP + "/" + escort.cMaxHP
               + " hp=" + me.cHP + "/" + me.cMaxHP + " mp=" + me.cMP + "/" + me.cMaxMP
               + " hpPotion=" + countUplevelPotion(me, 16)
               + " target=" + (me.mobFocus == null ? "none" : me.mobFocus.mobId + "@" + me.mobFocus.x
               + "," + me.mobFocus.y + " hp=" + me.mobFocus.hp + "/" + me.mobFocus.maxHp)
               + " mobsNear=" + this.countUplevelMobsNear(escort)
               + " picked=" + escort.charID + " picked_mine=" + (escort.charID == -me.charID)
               + " " + describeUplevelJaians(me));
      }
      this.useUplevelTask17Potions(me);

      // Ưu tiên giữ khoảng cách: xa hơn ngưỡng thì chạy về phía Jaian, không đánh quái.
      if (dist > UplevelTask17FollowDistance) {
         Char.fieldAC(escort.cx, escort.cy);
         return;
      }

      // Đánh quái quanh Jaian (ưu tiên con gần Jaian nhất), không rời Jaian quá bán kính.
      Mob target = null;
      int best = -1;
      for (int i = 0; i < GameScr.vMob.size(); i++) {
         Mob mob = (Mob)GameScr.vMob.elementAt(i);
         if (mob == null || mob.status == 0 || mob.status == 1 || mob.hp <= 0) continue;
         int d = Res.gameAA(mob.x, mob.y, escort.cx, escort.cy);
         if (d > UplevelTask17FightRadius) continue;
         if (best == -1 || d < best) {
            best = d;
            target = mob;
         }
      }
      if (target == null) return;
      Skill skill = me.myskill;
      if (skill == null || skill.template == null) {
         if (now - this.uplevelTask17AttackAt >= 5000L) {
            this.uplevelTask17AttackAt = now;
            System.out.println("UPLEVEL TASK17 escort no attack skill selected");
         }
         return;
      }
      me.mobFocus = target;
      int rangeX = skill.dx > 0 ? skill.dx : 40;
      int rangeY = skill.dy > 0 ? skill.dy : 30;
      if (Math.abs(target.x - me.cx) > rangeX || Math.abs(target.y - me.cy) > rangeY) {
         Char.fieldAC(target.x, target.y);
         return;
      }
      if (now - skill.lastTimeUseThisSkill < (long)skill.coolDown) return;
      skill.lastTimeUseThisSkill = now;
      this.uplevelTask17AttackAt = now;
      MyVector mobs = new MyVector();
      mobs.addElement(target);
      int maxFight = skill.maxFight > 0 ? skill.maxFight : 1;
      for (int i = 0; i < GameScr.vMob.size() && mobs.size() < maxFight; i++) {
         Mob mob = (Mob)GameScr.vMob.elementAt(i);
         if (mob == null || mob == target || mob.status == 0 || mob.status == 1 || mob.hp <= 0) continue;
         if (Math.abs(mob.x - me.cx) <= rangeX && Math.abs(mob.y - me.cy) <= rangeY) {
            mobs.addElement(mob);
         }
      }
      Service.gI().selectSkill(skill.template.id);
      Service.gI().sendPlayerAttack(mobs, new MyVector(), 1);
   }

   private int countUplevelMobsNear(Char escort) {
      int count = 0;
      for (int i = 0; i < GameScr.vMob.size(); i++) {
         Mob mob = (Mob)GameScr.vMob.elementAt(i);
         if (mob != null && mob.status != 0 && mob.status != 1 && mob.hp > 0
               && Res.gameAA(mob.x, mob.y, escort.cx, escort.cy) <= UplevelTask17FightRadius) {
            ++count;
         }
      }
      return count;
   }

   private boolean receiveUplevelTask17(Char me, int targetNpc) {
      int schoolMap = this.getUplevelSchoolMap(me);
      if (TileMap.mapID == schoolMap) {
         System.out.println("UPLEVEL TASK17 route Tone map=" + TileMap.mapID
               + " -> " + UplevelTask17ToneMap);
         this.fieldAA(UplevelTask17ToneMap, -2, -1, -1);
         return false;
      }
      if (TileMap.mapID == UplevelTask17ToneMap) {
         System.out.println("UPLEVEL TASK17 route fishing village map=" + TileMap.mapID
               + " -> " + UplevelTask17FishingVillageMap);
         this.fieldAA(UplevelTask17FishingVillageMap, -2, -1, -1);
         return false;
      }
      if (TileMap.mapID != UplevelTask17FishingVillageMap) {
         System.out.println("UPLEVEL TASK17 route school map=" + TileMap.mapID + " -> " + schoolMap);
         this.fieldAA(schoolMap, -2, -1, -1);
         return false;
      }

      int npcId = targetNpc >= 0 ? targetNpc : UplevelTask17NaoNpc;
      Npc nao = GameScr.fieldAI(npcId);
      if (nao == null) {
         long now = System.currentTimeMillis();
         if (now - this.uplevelTask17NpcLogAt >= 5000L) {
            this.uplevelTask17NpcLogAt = now;
            StringBuilder npcs = new StringBuilder();
            for (int i = 0; i < GameScr.vNpc.size(); i++) {
               Npc npc = (Npc)GameScr.vNpc.elementAt(i);
               if (npc != null && npc.template != null) {
                  if (npcs.length() > 0) npcs.append(',');
                  npcs.append(npc.template.npcTemplateId).append(':').append(npc.template.name);
               }
            }
            System.out.println("UPLEVEL TASK17 target missing npc=" + npcId
                  + " map=" + TileMap.mapID + " npcs=" + npcs);
         }
         return false;
      }
      if (Math.abs(nao.cx - me.cx) > 22 || Math.abs(nao.cy - me.cy) > 22) {
         Char.fieldAC(nao.cx, nao.cy);
         return false;
      }

      long now = System.currentTimeMillis();
      if (now - this.uplevelTask17RequestSentAt < 5000L) {
         return false;
      }
      this.uplevelTask17RequestSentAt = now;
      GameScr.fieldAB(npcId, 0, 0);
      Service.gI().getTask(npcId, 0, -1);
      LockGame.fieldAO();
      System.out.println("UPLEVEL TASK17 receive npc=" + npcId
            + " map=" + UplevelTask17FishingVillageMap);
      return false;
   }

   private int[] uplevelEffectIds(Char me) {
      int size = me.vEff == null ? 0 : me.vEff.size();
      int[] ids = new int[size];
      for (int i = 0; i < size; i++) {
         Effect effect = (Effect)me.vEff.elementAt(i);
         ids[i] = effect == null || effect.template == null ? -1 : effect.template.id;
      }
      return ids;
   }

   private String uplevelEffectsLog(Char me) {
      StringBuilder out = new StringBuilder("[");
      int nowSec = (int)(System.currentTimeMillis() / 1000L);
      for (int i = 0; me.vEff != null && i < me.vEff.size(); i++) {
         Effect effect = (Effect)me.vEff.elementAt(i);
         if (effect == null || effect.template == null) continue;
         if (out.length() > 1) out.append(',');
         out.append(effect.template.id).append("/t").append(effect.template.type)
               .append("/left").append(effect.timeLenght - (nowSec - effect.timeStart)).append('s');
      }
      return out.append(']').toString();
   }

   private Effect findUplevelMushroomEffect(Char me) {
      if (learnedMushroomEffectId < 0 || me.vEff == null) return null;
      for (int i = 0; i < me.vEff.size(); i++) {
         Effect effect = (Effect)me.vEff.elementAt(i);
         if (effect != null && effect.template != null && effect.template.id == learnedMushroomEffectId) {
            return effect;
         }
      }
      return null;
   }

   private boolean isUplevelMushroomActive(Char me) {
      if (learnedMushroomEffectId >= 0) {
         return this.findUplevelMushroomEffect(me) != null;
      }
      return this.uplevelMushroomConfirmedAt > 0L
            && System.currentTimeMillis() - this.uplevelMushroomConfirmedAt < UplevelMushroomDuration;
   }

   private int countUplevelItem(Item[] items, int templateId) {
      int count = 0;
      for (int i = 0; items != null && i < items.length; i++) {
         Item item = items[i];
         if (item != null && item.template != null && item.template.id == templateId) {
            count += item.quantity;
         }
      }
      return count;
   }

   /** Giữ hiệu ứng x2 kinh nghiệm của Nấm linh chi; hết hiệu ứng thì mua (10 lượng) và dùng lại. */
   private boolean ensureUplevelMushroom(Char me) {
      long now = System.currentTimeMillis();
      if (this.uplevelMushroomUseSentAt > 0L) {
         int[] before = this.uplevelMushroomEffectsBefore == null ? new int[0] : this.uplevelMushroomEffectsBefore;
         int[] after = this.uplevelEffectIds(me);
         int added = -1;
         for (int i = 0; i < after.length && added < 0; i++) {
            boolean seen = false;
            for (int j = 0; j < before.length; j++) {
               if (before[j] == after[i]) seen = true;
            }
            if (!seen && after[i] >= 0) added = after[i];
         }
         int quantity = this.countUplevelItem(me.arrItemBag, UplevelMushroomItemId);
         if (added >= 0 || quantity < this.uplevelMushroomQuantityBefore
               || this.findUplevelMushroomEffect(me) != null) {
            if (added >= 0 && learnedMushroomEffectId < 0) {
               learnedMushroomEffectId = added;
               System.out.println("UPLEVEL MUSHROOM effect learned id=" + added);
            }
            System.out.println("UPLEVEL MUSHROOM use confirmed luong=" + me.luong
                  + " newEffect=" + added + " effects=" + this.uplevelEffectsLog(me));
            this.uplevelMushroomUseSentAt = 0L;
            this.uplevelMushroomUseAttempts = 0;
            this.uplevelMushroomConfirmedAt = now;
            return true;
         }
         if (now - this.uplevelMushroomUseSentAt < 8000L) {
            return false;
         }
         this.uplevelMushroomUseSentAt = 0L;
         System.out.println("UPLEVEL MUSHROOM use timeout attempt=" + this.uplevelMushroomUseAttempts
               + " effects=" + this.uplevelEffectsLog(me));
         if (this.uplevelMushroomUseAttempts >= 3) {
            this.uplevelMushroomUseAttempts = 0;
            this.uplevelMushroomRetryAt = now + UplevelMushroomRetryDelay;
            System.out.println("UPLEVEL MUSHROOM use failed; retry after 10 minutes");
            return true;
         }
      }

      if (this.isUplevelMushroomActive(me)) {
         return true;
      }
      if (now < this.uplevelMushroomRetryAt) {
         return true;
      }

      Item mushroom = this.findUplevelItem(me.arrItemBag, UplevelMushroomItemId);
      if (mushroom == null) {
         if (me.luong < UplevelMushroomPrice) {
            System.out.println("UPLEVEL MUSHROOM not enough luong=" + me.luong
                  + " need=" + UplevelMushroomPrice + "; retry after 10 minutes");
            this.uplevelMushroomRetryAt = now + UplevelMushroomRetryDelay;
            return true;
         }
         // Map hiện tại có Goosho thì mua tại chỗ, không có mới về map 72.
         if (GameScr.fieldAI(UplevelGooshoNpc) == null && TileMap.mapID != UplevelNoelMap) {
            if (now - this.uplevelMushroomLogAt >= 5000L) {
               this.uplevelMushroomLogAt = now;
               System.out.println("UPLEVEL MUSHROOM route map=" + TileMap.mapID + " -> " + UplevelNoelMap);
            }
            this.fieldAA(UplevelNoelMap, -2, -1, -1);
            return false;
         }
         GameScr.arrItemStore = null;
         GameScr.fieldAB(UplevelGooshoNpc, 0, 0);
         Service.gI().requestItem(UplevelMushroomShop);
         long deadline = System.currentTimeMillis() + 5000L;
         while (GameScr.arrItemStore == null && System.currentTimeMillis() < deadline) {
            Auto.fieldAA(100L);
         }
         Item offer = this.findUplevelItem(GameScr.arrItemStore, UplevelMushroomItemId);
         if (offer == null) {
            System.out.println("UPLEVEL MUSHROOM not found in Goosho shop=" + UplevelMushroomShop
                  + " storeLoaded=" + (GameScr.arrItemStore != null) + "; retry after 10 minutes");
            this.uplevelMushroomRetryAt = now + UplevelMushroomRetryDelay;
            return true;
         }
         int price = offer.buyGold > 0 ? offer.buyGold : UplevelMushroomPrice;
         if (me.luong < price) {
            System.out.println("UPLEVEL MUSHROOM not enough luong=" + me.luong
                  + " price=" + price + "; retry after 10 minutes");
            this.uplevelMushroomRetryAt = now + UplevelMushroomRetryDelay;
            return true;
         }
         System.out.println("UPLEVEL MUSHROOM buy id=" + UplevelMushroomItemId + " shopIndex=" + offer.indexUI
               + " price=" + price + " luong=" + me.luong);
         Service.gI().buyItem(offer.typeUI, offer.indexUI, 1);
         mushroom = this.waitForUplevelBagItem(me, UplevelMushroomItemId, 5000L);
         if (mushroom == null) {
            System.out.println("UPLEVEL MUSHROOM purchase not in bag luong=" + me.luong + "; retry after 1 minute");
            this.uplevelMushroomRetryAt = now + 60000L;
            return true;
         }
      }

      this.uplevelMushroomEffectsBefore = this.uplevelEffectIds(me);
      this.uplevelMushroomQuantityBefore = this.countUplevelItem(me.arrItemBag, UplevelMushroomItemId);
      this.uplevelMushroomUseSentAt = now;
      ++this.uplevelMushroomUseAttempts;
      System.out.println("UPLEVEL MUSHROOM use id=" + UplevelMushroomItemId + " bagIndex=" + mushroom.indexUI
            + " quantity=" + mushroom.quantity + " effects=" + this.uplevelEffectsLog(me));
      Service.gI().useItem(mushroom.indexUI);
      return false;
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

   // Ghi lại thông báo server trong lúc bot đang thao tác ở nhiệm vụ 12 (gọi từ hook thông báo server).
   static void uplevelTask12Message(String message) {
      if (message != null && System.currentTimeMillis() < uplevelTask12MessageUntil) {
         System.out.println("UPLEVEL TASK12 server=[" + message + "]");
      }
   }

   private static int uplevelTask12TargetId(Char me, int phase) {
      if (phase == 1) {
         return (new int[]{194, 94, 114, 99, 109, 105, 119})[me.nClass.classId];
      }
      if (phase == 2) {
         return UplevelTask12AdornId;
      }
      return me.cgender == 1 ? 124 : 125;
   }

   private static Item uplevelTask12Body(Char me, int templateId) {
      for (int i = 0; me.arrItemBody != null && i < me.arrItemBody.length; i++) {
         Item item = me.arrItemBody[i];
         if (item != null && item.template != null && item.template.id == templateId) {
            return item;
         }
      }
      return null;
   }

   private void uplevelTask12Hang(Char me, String reason) {
      long now = System.currentTimeMillis();
      if (now - this.uplevelTask12HangLogAt >= UplevelTask12HangLogMs) {
         this.uplevelTask12HangLogAt = now;
         System.out.println("UPLEVEL TASK12 HANG name=" + me.cName
               + " level=" + me.clevel + " phase=" + me.taskMaint.index + " map=" + TileMap.mapID
               + ": " + reason);
      }
   }

   private boolean uplevelTask12Busy() {
      return System.currentTimeMillis() - this.uplevelTask12ActionAt < 1500L;
   }

   private void uplevelTask12Act(String log) {
      long now = System.currentTimeMillis();
      this.uplevelTask12ActionAt = now;
      uplevelTask12MessageUntil = now + UplevelTask12WaitMs;
      System.out.println("UPLEVEL TASK12 " + log);
   }

   // Mặc món đồ của phase trước nếu đang nằm trong hành trang. true = vừa gửi lệnh mặc.
   private boolean uplevelTask12Equip(Char me, int templateId) {
      if (uplevelTask12Body(me, templateId) != null) {
         return false;
      }
      Item item = Char.fieldAF(templateId);
      if (item == null) {
         return false;
      }
      Service.gI().useItem(item.indexUI);
      this.uplevelTask12Act("equip id=" + templateId + " upgrade=" + item.upgrade + " slot=" + item.indexUI);
      return true;
   }

   private void doUplevelTask12(Char me) {
      int phase = me.taskMaint.index;
      // Phase 0: đạt cấp 12, vẫn cầm vũ khí đánh quái. Chưa lv12 thì không tháo/mua/nâng gì.
      if (phase <= 0 || me.clevel < UplevelTask12Level) {
         if (TileMap.mapID == 3) {
            this.fieldAC(-1);
            this.uplevelFight(-1);
         } else {
            this.fieldAA(3, -1, -1, -1);
         }
         return;
      }
      if (phase > 3 || this.uplevelTask12Busy()) {
         return;
      }
      if (this.uplevelTask12UpgradeItem != null) {
         this.uplevelTask12CheckUpgrade(me);
         return;
      }
      // Mặc lần lượt đồ đã nâng ở các phase trước (phòng khi mất kết nối ngay sau khi nâng).
      for (int done = 1; done < phase; done++) {
         if (this.uplevelTask12Equip(me, uplevelTask12TargetId(me, done))) {
            return;
         }
      }

      int targetId = uplevelTask12TargetId(me, phase);
      // Tháo món cần nâng vào hành trang.
      Item worn = uplevelTask12Body(me, targetId);
      if (worn != null) {
         Service.gI().itemBodyToBag((int)worn.template.type);
         this.uplevelTask12Act("unequip phase=" + phase + " id=" + targetId + " upgrade=" + worn.upgrade);
         return;
      }
      // Mua sẵn trang sức và y phục (không đánh quái).
      for (int buy = Math.max(phase, 2); buy <= 3; buy++) {
         int buyId = uplevelTask12TargetId(me, buy);
         if (uplevelTask12Body(me, buyId) != null || Char.fieldAF(buyId) != null) {
            continue;
         }
         if (buy == 2 ? this.uplevelPrepareShop(me, buyId, 19, 2, 4)
               : this.uplevelPrepareShop(me, buyId, me.cgender == 1 ? 28 : 29, 1, 5)) {
            uplevelTask12MessageUntil = System.currentTimeMillis() + UplevelTask12WaitMs;
            return;
         }
      }

      Item item = Char.fieldAF(targetId);
      if (item == null) {
         this.uplevelTask12Hang(me, "không có đồ id=" + targetId + " trên người/hành trang");
         return;
      }
      this.uplevelTask12Upgrade(me, item, phase, targetId);
   }

   // TEST đập vũ khí lên +UplevelTestWeaponMax. true = đang xử lý, bỏ qua logic nhiệm vụ tick này.
   private boolean doUplevelTestWeapon(Char me) {
      if (UplevelTestWeaponMax <= 0 || this.uplevelTestWeaponDone || me.clevel < UplevelTask12Level
            || me.ctaskId <= 12) {
         return false;
      }
      if (this.uplevelTask12Busy()) {
         return true;
      }
      if (this.uplevelTask12UpgradeItem != null) {
         this.uplevelTask12CheckUpgrade(me);
         return true;
      }
      int targetId = uplevelTask12TargetId(me, UplevelTestPhase);
      Item worn = uplevelTask12Body(me, targetId);
      Item item = worn != null ? worn : Char.fieldAF(targetId);
      if (this.countUplevelItem(me, UplevelTask12StoneId) <= 0) {
         System.out.println("UPLEVEL TEST hết đá; dừng test id=" + targetId);
         this.uplevelTestWeaponDone = true;
         return false;
      }
      if (item == null) {
         System.out.println("UPLEVEL TEST weapon id=" + targetId + " không tìm thấy; dừng test");
         this.uplevelTestWeaponDone = true;
         return false;
      }
      if (item.upgrade >= UplevelTestWeaponMax) {
         if (worn == null) {
            this.uplevelTask12Equip(me, targetId);
            return true;
         }
         System.out.println("UPLEVEL TEST weapon id=" + targetId + " đạt +" + item.upgrade + "; xong test, đá còn="
               + this.countUplevelItem(me, UplevelTask12StoneId));
         this.uplevelTestWeaponDone = true;
         return false;
      }
      if (worn != null) {
         Service.gI().itemBodyToBag((int)worn.template.type);
         this.uplevelTask12Act("TEST unequip id=" + targetId + " upgrade=" + worn.upgrade);
         return true;
      }
      this.uplevelTask12Upgrade(me, item, 0, targetId);
      return true;
   }

   // Gom/tách đá cấp 6 rồi gửi lệnh nâng item (đang nằm trong hành trang) lên 1 cấp.
   private void uplevelTask12Upgrade(Char me, Item item, int phase, int targetId) {
      int need = item.isTypeWeapon() ? GameScr.upWeapon[item.upgrade]
            : (item.isTypeAdorn() ? GameScr.upAdorn[item.upgrade] : GameScr.upClothe[item.upgrade]);
      int stones = this.countUplevelItem(me, UplevelTask12StoneId);
      if (stones <= 0) {
         this.uplevelTask12Hang(me, "hết đá cấp 6 id=" + UplevelTask12StoneId + " trong hành trang (phase="
               + phase + " id=" + targetId + " +" + item.upgrade + " đã thử " + this.uplevelTask12Tries + " lần)");
         return;
      }
      int need2 = item.isTypeWeapon() ? GameScr.coinUpWeapons[item.upgrade]
            : (item.isTypeAdorn() ? GameScr.coinUpAdorns[item.upgrade] : GameScr.coinUpClothes[item.upgrade]);
      if (me.yen < need2) {
         this.uplevelTask12Hang(me, "không đủ yên " + me.yen + "/" + need2 + " để nâng id=" + targetId);
         return;
      }
      if (TileMap.mapID != UplevelTask12UpgradeMap) {
         this.fieldAA(UplevelTask12UpgradeMap, -2, -1, -1);
         return;
      }

      // Gom đá lẻ đủ giá trị; thiếu thì tách 1 viên từ chồng.
      int stoneValue = GameScr.upClothe[UplevelTask12StoneId];
      Item[] put = new Item[18];
      int count = 0;
      int value = 0;
      Item stack = null;
      for (int i = 0; i < me.arrItemBag.length && value < need && count < put.length; i++) {
         Item stone = me.arrItemBag[i];
         if (stone == null || stone.template == null || stone.template.id != UplevelTask12StoneId) {
            continue;
         }
         if (stone.quantity <= 1) {
            put[count++] = stone;
            value += stoneValue;
         } else if (stack == null) {
            stack = stone;
         }
      }
      if (value < need) {
         if (stack == null) {
            this.uplevelTask12Hang(me, "đá cấp 6 không đủ: có " + value + " cần " + need + " (id=" + targetId
                  + " +" + item.upgrade + ")");
            return;
         }
         if (uplevelCrystalSplitBusy()) {
            return;
         }
         uplevelCrystalSplitStart(stack);
         Service.gI().inputNumSplit(stack.indexUI, 1);
         this.uplevelTask12Act("split stone slot=" + stack.indexUI + " quantity=" + stack.quantity
               + " value=" + value + "/" + need);
         return;
      }

      GameScr.fieldAB(UplevelTask12UpgradeNpc, 0, 0);
      GameScr.itemUpGrade = item;
      GameScr.arrItemUpGrade = put;
      this.uplevelTask12UpgradeItem = item;
      this.uplevelTask12UpgradeFrom = item.upgrade;
      this.uplevelTask12UpgradeStones = put;
      this.uplevelTask12Tries++;
      Service.gI().upgradeItem(item, put, false);
      this.uplevelTask12Act("upgrade phase=" + phase + " id=" + targetId + " +" + item.upgrade
            + " stones=" + count + " value=" + value + "/" + need + " yen=" + me.yen + " try=" + this.uplevelTask12Tries);
   }

   // Chờ kết quả nâng cấp: server xoá arrItemUpGrade và ghi cấp mới vào itemUpGrade.
   private void uplevelTask12CheckUpgrade(Char me) {
      Item item = this.uplevelTask12UpgradeItem;
      boolean answered = GameScr.arrItemUpGrade == null || GameScr.arrItemUpGrade[0] == null;
      if (!answered && System.currentTimeMillis() - this.uplevelTask12ActionAt < UplevelTask12WaitMs) {
         return;
      }
      Item[] usedStones = this.uplevelTask12UpgradeStones;
      GameScr.itemUpGrade = null;
      GameScr.arrItemUpGrade = null;
      this.uplevelTask12UpgradeItem = null;
      this.uplevelTask12UpgradeStones = null;
      // Server luôn tiêu đá (thành công hay thất bại) nhưng bot đặt thẳng arrItemUpGrade mà không gỡ đá
      // khỏi arrItemBag → đá "ma" còn lại, gửi lại thì server báo "Hãy chọn đá nâng cấp!" lặp vô hạn.
      // Xoá đá đã gửi khỏi hành trang sau mỗi kết quả (sau 1 lần thất bại có thể còn 1 lượt bị từ chối,
      // không tốn yên, lượt đó sẽ xoá nốt đá ma rồi bot tách đá mới).
      if (answered && usedStones != null) {
         for (int i = 0; i < usedStones.length; i++) {
            Item stone = usedStones[i];
            if (stone == null) {
               continue;
            }
            if (me.arrItemBag != null && stone.indexUI >= 0 && stone.indexUI < me.arrItemBag.length
                  && me.arrItemBag[stone.indexUI] == stone) {
               me.arrItemBag[stone.indexUI] = null;
            }
         }
      }
      if (!answered) {
         System.out.println("UPLEVEL TASK12 upgrade no response id=" + item.template.id + "; retry");
         return;
      }
      if (item.upgrade > this.uplevelTask12UpgradeFrom) {
         this.uplevelTask12Tries = 0;
         Item bagItem = Char.fieldAF(item.template.id);
         if (bagItem != null) {
            Service.gI().useItem(bagItem.indexUI);
         }
         this.uplevelTask12Act("upgrade ok id=" + item.template.id + " +" + this.uplevelTask12UpgradeFrom
               + " -> +" + item.upgrade + "; equip");
         LockGame.fieldAO();
      } else {
         this.uplevelTask12Act("upgrade fail id=" + item.template.id + " +" + this.uplevelTask12UpgradeFrom
               + " -> +" + item.upgrade + "; retry");
      }
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
      if (var1.ctaskId == 17) {
         long now = System.currentTimeMillis();
         if (now - this.uplevelTask17EntryLogAt >= 5000L) {
            this.uplevelTask17EntryLogAt = now;
            System.out.println("UPLEVEL TASK17 enter task=" + (var1.taskMaint == null
                  ? "null" : String.valueOf(var1.taskMaint.index))
                  + " map=" + TileMap.mapID + " hp=" + var1.cHP
                  + " pos=" + var1.cx + "," + var1.cy
                  + " neighbors=" + java.util.Arrays.toString(TileMap.fieldBZ[TileMap.mapID]));
         }
      }
      int autoItemLevel = this.getUplevelFoodLevel(var1);
      Char.aHpValue = autoItemLevel;
      Char.aMpValue = autoItemLevel;
      Char.aFoodValue = autoItemLevel;
      Char.isAHP = true;
      Char.isAMP = true;
      Char.isAFood = true;
      if (var1.ctaskId == 17 && var1.taskMaint == null) {
         this.receiveUplevelTask17(var1, var3);
         return;
      }
      // Đang hộ tống Jaian: không để bước mua thức ăn/mũ Noel/quà kéo nhân vật rời map.
      if (var1.ctaskId == 17 && var1.taskMaint != null && var1.taskMaint.index == 1
            && var1.cHP > 0 && (this.uplevelTask17EscortSeen
            || (TileMap.mapID == UplevelTask17LevelMap && findUplevelEscort(var1) != null))) {
         this.doUplevelTask17Escort(var1);
         return;
      }
      if (var1.cHP > 0 && (!uplevelGiftDone || (var1.ctaskId >= 9
            && (!uplevelGiftBoxReady || uplevelGiftItemsPending())))) {
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
      if (var1.cHP > 0 && var1.ctaskId >= 9 && !this.ensureUplevelMushroom(var1)) {
         return;
      }
      if (var1.cHP > 0 && var1.ctaskId >= 9 && !this.ensureUplevelNoelHat(var1)) {
         return;
      }
      if (var1.cHP > 0 && var1.ctaskId >= 9 && !this.ensureUplevelFood(var1)) {
         return;
      }
      if (var1.cHP > 0 && this.doUplevelTestWeapon(var1)) {
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
                  this.uplevelFight(-1);
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
                  this.uplevelFight(5);
                  return;
               }

               this.fieldAA(28, -1, -1, -1);
               return;
            }

            if (var1.taskMaint.index == 1) {
               if (TileMap.mapID == 4) {
                  this.fieldAC(-1);
                  this.uplevelFight(6);
                  return;
               }

               this.fieldAA(4, -1, -1, -1);
               return;
            }

            if (var1.taskMaint.index == 2) {
               if (TileMap.mapID == 46) {
                  this.fieldAC(-1);
                  this.uplevelFight(7);
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
                  this.uplevelFight(-1);
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
            // Logic cũ bên dưới không chạy nữa (giữ lại để các patch build.sh vẫn khớp).
            this.doUplevelTask12(var1);
            if (var1 != null) {
               return;
            }
            if (var1.taskMaint.index == 0) {
               if (TileMap.mapID == 3) {
                  this.fieldAC(-1);
                  this.uplevelFight(-1);
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
                  this.uplevelFight(-1);
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
                  this.uplevelFight(-1);
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
                  this.uplevelFight(-1);
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
                  this.uplevelFight(-1);
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
                     this.switchUplevelTask14Zone(var1);
                     return;
                  }
                  this.uplevelTask14ZoneEmptySince = 0L;

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

               // Chỉ đi tới map 29; chọn khu do switchUplevelTask14Zone lo khi đã đứng trong map.
               this.fieldAA(29, -2, -1, -1);
               return;
            }

            if (var1.taskMaint.index == 2) {
               if (TileMap.mapID == 40) {
                  this.uplevelFight(15);
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
                  this.uplevelFight(-1);
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
            this.logUplevelTask16(var1);
            if (var1.clevel >= 20 && (var11 = Char.fieldAF(fieldBC[var1.nClass.classId])) != null) {
               GameScr.fieldAC("Học sách kĩ năng");
               Service.gI().useItem(var11.indexUI);
               cuong.sleep(1000L);
            }

            if (var1.taskMaint.index == 0) {
               if (var1.clevel >= UplevelTask16LevelTarget) {
                  return;
               }
               if (TileMap.mapID == 8) {
                  this.fieldAC(-1);
                  this.uplevelFight(-1);
                  return;
               }

               this.fieldAA(8, -1, -1, -1);
               return;
            }

            if (var1.taskMaint.index == 1) {
               if (TileMap.mapID == 63) {
                  this.fieldAC(-1);
                  this.uplevelFight(UplevelTask16ScorpionMob);
                  return;
               }

               this.fieldAA(63, -1, -1, -1);
               return;
            }

            if (var1.taskMaint.index == 2) {
               if (TileMap.mapID == 47) {
                  this.fieldAC(-1);
                  this.uplevelFight(UplevelTask16SnakeMob);
                  return;
               }

               this.fieldAA(47, -1, -1, -1);
            }
            break;
         case 17:
            if (var1.taskMaint == null) {
               long now = System.currentTimeMillis();
               if (now - this.uplevelTask17WaitingLogAt >= 5000L) {
                  this.uplevelTask17WaitingLogAt = now;
                  Npc targetNpc = GameScr.fieldAI(var3);
                  System.out.println("UPLEVEL TASK17 waiting task map=" + TileMap.mapID
                        + " targetMap=" + var2 + " targetNpc=" + var3
                        + " npcPresent=" + (targetNpc != null));
               }
            }
            this.logUplevelTask17(var1);
            if (var1.taskMaint != null && var1.taskMaint.index == 0
                  && var1.clevel < UplevelTask17LevelTarget) {
               if (TileMap.mapID == UplevelTask17LevelMap) {
                  this.fieldAC(-1);
                  this.uplevelFight(-1);
                  return;
               }
               this.routeUplevelTask17LevelMap(var1);
               return;
            }
            if (var1.taskMaint != null && var1.taskMaint.index == 1) {
               this.doUplevelTask17Escort(var1);
               return;
            }
            if (var1.taskMaint != null && var1.taskMaint.index >= 2) {
               long now = System.currentTimeMillis();
               if (now - this.uplevelTask17Phase2LogAt >= 10000L) {
                  this.uplevelTask17Phase2LogAt = now;
                  System.out.println("UPLEVEL TASK17 phase=" + var1.taskMaint.index
                        + " chưa có logic; chờ hướng dẫn map=" + TileMap.mapID);
               }
            }
            return;
         }

      }
   }

   public String toString() {
      return "Auto Nhiệm Vụ 20";
   }
}
