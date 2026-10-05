#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
UPLEVEL_DIR=$(cd -- "$SCRIPT_DIR/.." && pwd)
REPO_DIR=$(cd -- "$UPLEVEL_DIR/.." && pwd)
BUILD_DIR=$(realpath -m -- "${UPLEVEL_BUILD_DIR:-"$UPLEVEL_DIR/build"}")
ACCOUNT_CSV=${UPLEVEL_ACCOUNT_CSV:-"$REPO_DIR/account-as20.csv"}

if [[ ! -f "$ACCOUNT_CSV" ]]; then
  echo "Không tìm thấy account CSV: $ACCOUNT_CSV" >&2
  exit 1
fi
if [[ "$BUILD_DIR" == / || "$BUILD_DIR" == "$UPLEVEL_DIR" || "$BUILD_DIR" == "$REPO_DIR" ]]; then
  echo "UPLEVEL_BUILD_DIR không an toàn: $BUILD_DIR" >&2
  exit 1
fi

BUILD_PARENT=$(dirname -- "$BUILD_DIR")
BUILD_NAME=$(basename -- "$BUILD_DIR")
mkdir -p "$BUILD_PARENT"
STAGING_DIR=$(mktemp -d "$BUILD_PARENT/.${BUILD_NAME}.build.XXXXXX")
WORK_SRC_DIR="$STAGING_DIR/src"
CLASSES_DIR="$STAGING_DIR/classes"
SOURCES_FILE="$STAGING_DIR/sources.txt"
BACKUP_DIR="$BUILD_PARENT/.${BUILD_NAME}.old.$$"

cleanup() {
  rm -rf -- "$STAGING_DIR" "$BACKUP_DIR"
}
trap cleanup EXIT

mkdir -p "$WORK_SRC_DIR" "$CLASSES_DIR"
cp -R "$REPO_DIR/src"/. "$WORK_SRC_DIR"/
cp -R "$REPO_DIR/optimized-runtime/src"/. "$WORK_SRC_DIR"/
cp -R "$REPO_DIR/optimized-runtime/overrides"/. "$WORK_SRC_DIR"/
cp -R "$UPLEVEL_DIR/overrides"/. "$WORK_SRC_DIR"/

python3 - "$WORK_SRC_DIR/As20.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
if not source.startswith("import java.io.DataInputStream;"):
    source = "import java.io.DataInputStream;\nimport java.io.IOException;\n\n" + source
path.write_text(source)
UPLEVELPY

sed -i 's#"".getClass().getResourceAsStream("/map/" + var1)#TileMap.class.getResourceAsStream("/map/" + var1)#g' "$WORK_SRC_DIR/TileMap.java"
sed -i 's#"".getClass().getResourceAsStream("/map/" + mapID)#TileMap.class.getResourceAsStream("/map/" + mapID)#g' "$WORK_SRC_DIR/TileMap.java"
sed -i 's#"".getClass().getResourceAsStream(var0)#RMS.class.getResourceAsStream(var0)#g' "$WORK_SRC_DIR/RMS.java"
sed -i 's#"".getClass().getResourceAsStream(var0)#Res.class.getResourceAsStream(var0)#g' "$WORK_SRC_DIR/Res.java"
sed -i 's/Service.gI().requestForgetPass(var23.cName);/Service.gI().addFriend(var23.cName);/' "$WORK_SRC_DIR/As20.java"
sed -i 's/this.optionTemplate = GameScr.iOptionTemplates\[var1\];/this.optionTemplate = var1 >= 0 \&\& var1 < GameScr.iOptionTemplates.length ? GameScr.iOptionTemplates[var1] : null;/' "$WORK_SRC_DIR/ItemOption.java"
sed -i '/public class As20 extends As10 {/a\   private static boolean uplevelForcedEquip;\n   private static boolean uplevelAdornEquipPending;\n   private static long uplevelAdornEquipSentAt;\n   private static boolean uplevelBodyToBagPending;\n   private static long uplevelBodyToBagSentAt;\n   private static int uplevelBodyToBagTarget = -1;\n   private static Item uplevelBodyToBagItem;\n   private static int uplevelPreparedTarget = -1;\n   private static boolean uplevelCrystalSplitPending;\n   static void uplevelEquipDone() { uplevelAdornEquipPending = false; uplevelAdornEquipSentAt = 0L; }\n   static void uplevelBodyToBagStart(Item item) { uplevelBodyToBagItem = item; }\n   static Item uplevelBodyToBagFallback(int slot) { return slot == 9 ? uplevelBodyToBagItem : null; }\n   static void uplevelBodyToBagDone() { uplevelBodyToBagPending = false; uplevelBodyToBagSentAt = 0L; uplevelPreparedTarget = uplevelBodyToBagTarget; uplevelBodyToBagItem = null; }\n   static void uplevelCrystalSplitDone() { uplevelCrystalSplitPending = false; }' "$WORK_SRC_DIR/As20.java"
python3 - "$WORK_SRC_DIR/As20.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = "   private int fieldAV;\n"
replacement = '''   private static int uplevelShopTarget = -1;
   private static int uplevelShopType = -1;
   private static int uplevelShopNpc = -1;
   private static int uplevelShopOption = -1;
   private static boolean uplevelShopListRequested;
   private static boolean uplevelShopInfoRequested;
   private static boolean uplevelShopBuySent;
   private static boolean uplevelShopSlotFreed;
   private static boolean uplevelShopSlotFreePending;
   private static long uplevelShopLastAction;

   private static void uplevelShopReset() {
      uplevelShopTarget = -1;
      uplevelShopType = -1;
      uplevelShopNpc = -1;
      uplevelShopOption = -1;
      uplevelShopListRequested = false;
      uplevelShopInfoRequested = false;
      uplevelShopBuySent = false;
      uplevelShopSlotFreed = false;
      uplevelShopSlotFreePending = false;
      uplevelShopLastAction = 0L;
   }

   private static boolean uplevelShopFreeSlot(Char me) {
      if (uplevelShopSlotFreed) return false;
      if (uplevelShopSlotFreePending
              && System.currentTimeMillis() - uplevelShopLastAction < 10000L) return true;
      for (int i = 0; me.arrItemBag != null && i < me.arrItemBag.length; i++) {
         Item item = me.arrItemBag[i];
         if (item != null && item.template != null && item.template.id == 194) {
            Service.gI().saleItem(item.indexUI, item.quantity);
            uplevelShopSlotFreePending = true;
            uplevelShopLastAction = System.currentTimeMillis();
            System.out.println("UPLEVEL SHOP free slot by selling item=194 slot=" + item.indexUI);
            return true;
         }
      }
      System.out.println("UPLEVEL SHOP no safe discard item id=194");
      uplevelShopSlotFreed = true;
      return false;
   }

   static void uplevelShopSaleDone() {
      if (!uplevelShopSlotFreePending) return;
      uplevelShopSlotFreePending = false;
      uplevelShopSlotFreed = true;
      uplevelShopLastAction = 0L;
      System.out.println("UPLEVEL SHOP slot freed; retry purchase");
   }

   private static Item uplevelShopOffer(int type, int target) {
      Item[] items;
      if (type == 19) {
         items = GameScr.arrItemPhu;
      } else if (type == 28) {
         items = GameScr.arrItemGiayNam;
      } else if (type == 29) {
         items = GameScr.arrItemGiayNu;
      } else {
         return null;
      }
      for (int i = 0; items != null && i < items.length; i++) {
         Item item = items[i];
         if (item != null && item.template != null && item.template.id == target) return item;
      }
      return null;
   }

   private boolean uplevelPrepareShop(Char me, int target) {
      if (me.taskMaint.index != 2 && me.taskMaint.index != 3) return false;
      if (Char.fieldAF(target) != null) {
         uplevelShopReset();
         return false;
      }

      int type = me.taskMaint.index == 2 ? 19 : (me.cgender == 1 ? 28 : 29);
      int npc = me.taskMaint.index == 2 ? 2 : 1;
      int option = me.taskMaint.index == 2 ? 4 : 5;
      if (uplevelShopTarget != target || uplevelShopType != type) {
         uplevelShopReset();
         uplevelShopTarget = target;
         uplevelShopType = type;
         uplevelShopNpc = npc;
         uplevelShopOption = option;
      }

      if (TileMap.mapID != 27) {
         System.out.println("UPLEVEL SHOP route map=" + TileMap.mapID + " target=" + target
                 + " npc=" + npc + " type=" + type);
         this.fieldAA(27, -2, -1, -1);
         return true;
      }

      if (GameScr.fieldAI(npc) == null) {
         System.out.println("UPLEVEL SHOP NPC missing npc=" + npc + " target=" + target);
         return true;
      }

      if (!uplevelShopListRequested) {
         GameScr.fieldAB(npc, 0, option);
         Service.gI().requestItem(type);
         uplevelShopListRequested = true;
         uplevelShopLastAction = System.currentTimeMillis();
         System.out.println("UPLEVEL SHOP list npc=" + npc + " row=0 option=" + option
                 + " type=" + type + " target=" + target);
         return true;
      }

      Item offer = uplevelShopOffer(type, target);
      if (offer == null) {
         if (System.currentTimeMillis() - uplevelShopLastAction > 5000L) {
            System.out.println("UPLEVEL SHOP target missing type=" + type + " target=" + target);
            uplevelShopLastAction = System.currentTimeMillis();
         }
         return true;
      }

      if (!uplevelShopInfoRequested) {
         Service.gI().requestItemInfo(offer.typeUI, offer.indexUI);
         uplevelShopInfoRequested = true;
         uplevelShopLastAction = System.currentTimeMillis();
         System.out.println("UPLEVEL SHOP info type=" + offer.typeUI + " index=" + offer.indexUI
                 + " target=" + target);
         return true;
      }

      if (System.currentTimeMillis() - uplevelShopLastAction < 1000L) return true;
      if (me.xu < offer.buyCoin) {
         System.out.println("UPLEVEL SHOP insufficient xu=" + me.xu + " price=" + offer.buyCoin
                 + " target=" + target);
         return true;
      }
      if (uplevelShopFreeSlot(me)) return true;
      if (!uplevelShopBuySent) {
         Service.gI().buyItem(offer.typeUI, offer.indexUI, 1);
         uplevelShopBuySent = true;
         uplevelShopLastAction = System.currentTimeMillis();
         System.out.println("UPLEVEL SHOP buy target=" + target + " type=" + offer.typeUI
                 + " index=" + offer.indexUI + " xu=" + me.xu + " price=" + offer.buyCoin);
      }
      return true;
   }

   private int fieldAV;
'''
if source.count(needle) != 1:
    raise SystemExit("expected As20 fieldAV")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
python3 - "$WORK_SRC_DIR/As20.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = "   private int fieldAV;\n"
replacement = '''   private static boolean uplevelGiftStarted;
   private static boolean uplevelGiftCodeRequested;
   private static boolean uplevelGiftCodeSent;
   private static boolean uplevelGiftMessageSeen;
   private static boolean uplevelGiftMailListRequested;
   private static boolean uplevelGiftMailReadRequested;
   private static boolean uplevelGiftMailClaimRequested;
   private static boolean uplevelGiftMailReceived;
   private static boolean uplevelGiftDone;
   private static int uplevelGiftMailId = -1;
   private static long uplevelGiftLastAction;
   private static boolean uplevelGiftSlotFreed;
   private static boolean uplevelGiftSlotFreePending;
   private static final int[] uplevelGiftItems = new int[]{215, 229};
   private static int uplevelGiftItemStage;
   private static boolean uplevelGiftShopRequested;
   private static int uplevelGiftPurchaseTarget = -1;
   private static int uplevelGiftUseSlot = -1;
   private static int uplevelGiftUseQuantity;
   private static long uplevelGiftItemLastAction;

   private static Item uplevelGiftFreeSlotItem(Char me) {
      for (int i = 0; me.arrItemBag != null && i < me.arrItemBag.length; i++) {
         Item item = me.arrItemBag[i];
         if (item != null && item.template != null && item.template.id == 198) return item;
      }
      return null;
   }

   private static boolean uplevelGiftFreeSlot(Char me) {
      if (uplevelGiftSlotFreed) return false;
      if (uplevelGiftSlotFreePending
              && System.currentTimeMillis() - uplevelGiftLastAction < 10000L) return true;
      Item item = uplevelGiftFreeSlotItem(me);
      if (item == null) {
         System.out.println("UPLEVEL GIFT no safe discard item id=198");
         return false;
      }
      Service.gI().saleItem(item.indexUI, item.quantity);
      uplevelGiftSlotFreePending = true;
      uplevelGiftLastAction = System.currentTimeMillis();
      System.out.println("UPLEVEL GIFT free slot by selling item=198 slot=" + item.indexUI);
      return true;
   }

   static void uplevelGiftSaleDone() {
      if (!uplevelGiftSlotFreePending) return;
      uplevelGiftSlotFreePending = false;
      uplevelGiftSlotFreed = true;
      uplevelGiftMailListRequested = false;
      uplevelGiftMailReadRequested = false;
      uplevelGiftMailClaimRequested = false;
      uplevelGiftLastAction = 0L;
      System.out.println("UPLEVEL GIFT slot freed; retry mail claim");
   }

   private static boolean uplevelGiftMailTitle(String title) {
      String text = title == null ? "" : title.toLowerCase();
      return text.indexOf("giftcode") >= 0 || text.indexOf("gift code") >= 0
              || text.indexOf("tanthu") >= 0 || text.indexOf("ma qua tang") >= 0;
   }

   private static boolean uplevelGiftMessage(String message) {
      String text = message == null ? "" : message.toLowerCase();
      return text.indexOf("thu moi") >= 0 || text.indexOf("thư mới") >= 0
              || text.indexOf("giftcode") >= 0 || text.indexOf("gift code") >= 0
              || text.indexOf("da su dung") >= 0 || text.indexOf("đã sử dụng") >= 0;
   }

   private static void uplevelGiftRequestMailList() {
      Service.gI().uplevelMailAction(0, -1);
      uplevelGiftMailListRequested = true;
      uplevelGiftMailReadRequested = false;
      uplevelGiftMailClaimRequested = false;
      uplevelGiftLastAction = System.currentTimeMillis();
      System.out.println("UPLEVEL GIFT mail list request");
   }

   private static Item uplevelGiftBagItem(Char me, int templateId) {
      for (int i = 0; me.arrItemBag != null && i < me.arrItemBag.length; i++) {
         Item item = me.arrItemBag[i];
         if (item != null && item.template != null && item.template.id == templateId) return item;
      }
      return null;
   }

   private static Item uplevelGiftStoreItem(int templateId) {
      for (int i = 0; GameScr.arrItemStore != null && i < GameScr.arrItemStore.length; i++) {
         Item item = GameScr.arrItemStore[i];
         if (item != null && item.template != null && item.template.id == templateId) return item;
      }
      return null;
   }

   static void uplevelGiftItemUseDone(int slot) {
      if (slot != uplevelGiftUseSlot) return;
      uplevelGiftUseSlot = -1;
      uplevelGiftUseQuantity = 0;
      uplevelGiftItemStage++;
      uplevelGiftItemLastAction = System.currentTimeMillis();
      System.out.println("UPLEVEL GIFT use done item=" + uplevelGiftItems[(uplevelGiftItemStage - 2) / 2]);
   }

   private boolean uplevelPrepareGiftItems(Char me) {
      if (uplevelGiftItemStage >= uplevelGiftItems.length + 2) return false;
      if (TileMap.mapID != 72) {
         this.fieldAA(72, -2, -1, -1);
         return true;
      }

      if (uplevelGiftItemStage == 0 || uplevelGiftItemStage == 2) {
         int target = uplevelGiftItems[uplevelGiftItemStage / 2];
         if (uplevelGiftBagItem(me, target) != null) {
            uplevelGiftItemStage++;
            uplevelGiftPurchaseTarget = -1;
            uplevelGiftItemLastAction = System.currentTimeMillis();
            return true;
         }
         if (!uplevelGiftShopRequested) {
            GameScr.arrItemStore = null;
            GameScr.fieldAB(30, 0, 0);
            Service.gI().requestItem(14);
            uplevelGiftShopRequested = true;
            uplevelGiftItemLastAction = System.currentTimeMillis();
            System.out.println("UPLEVEL GIFT shop npc=30 item=" + target);
            return true;
         }
         Item offer = uplevelGiftStoreItem(target);
         if (offer == null) {
            if (System.currentTimeMillis() - uplevelGiftItemLastAction > 5000L) {
               uplevelGiftShopRequested = false;
               uplevelGiftItemLastAction = System.currentTimeMillis();
               System.out.println("UPLEVEL GIFT item not found in shop id=" + target);
            }
            return true;
         }
         if (uplevelGiftPurchaseTarget == target
                 && System.currentTimeMillis() - uplevelGiftItemLastAction < 8000L) return true;
         Service.gI().buyItem(offer.typeUI, offer.indexUI, 1);
         uplevelGiftPurchaseTarget = target;
         uplevelGiftItemLastAction = System.currentTimeMillis();
         System.out.println("UPLEVEL GIFT buy item=" + target + " shopIndex=" + offer.indexUI);
         return true;
      }

      int target = uplevelGiftItems[(uplevelGiftItemStage - 1) / 2];
      Item item = uplevelGiftBagItem(me, target);
      if (uplevelGiftUseSlot >= 0
              && (item == null || item.quantity < uplevelGiftUseQuantity)) {
         uplevelGiftItemUseDone(uplevelGiftUseSlot);
         return true;
      }
      if (item == null) {
         System.out.println("UPLEVEL GIFT missing bag item=" + target + ", retry purchase");
         uplevelGiftItemStage = target == 215 ? 0 : 2;
         uplevelGiftShopRequested = false;
         uplevelGiftPurchaseTarget = -1;
         return true;
      }
      if (uplevelGiftUseSlot < 0) {
         uplevelGiftUseSlot = item.indexUI;
         uplevelGiftUseQuantity = item.quantity;
         uplevelGiftItemLastAction = System.currentTimeMillis();
         Service.gI().useItem(item.indexUI);
         System.out.println("UPLEVEL GIFT use item=" + target + " bagIndex=" + item.indexUI);
         return true;
      }
      if (System.currentTimeMillis() - uplevelGiftItemLastAction > 10000L) {
         System.out.println("UPLEVEL GIFT use timeout item=" + target);
         uplevelGiftItemUseDone(uplevelGiftUseSlot);
      }
      return true;
   }

   private boolean uplevelPrepareGift(Char me) {
      if (uplevelGiftDone) {
         return uplevelPrepareGiftItems(me);
      }
      if (me.xu > 0L) {
         uplevelGiftDone = true;
         return uplevelPrepareGiftItems(me);
      }
      if (!uplevelGiftStarted) {
         uplevelGiftStarted = true;
         System.out.println("UPLEVEL GIFT start code=tanthu");
      }
      if (TileMap.mapID != 22) {
         this.fieldAA(22, -2, -1, -1);
         return true;
      }
      if (!uplevelGiftCodeRequested) {
         GameScr.fieldAB(24, 4, 0);
         uplevelGiftCodeRequested = true;
         uplevelGiftLastAction = System.currentTimeMillis();
         System.out.println("UPLEVEL GIFT menu npc=24 row=4 option=0");
         return true;
      }
      if (uplevelGiftCodeSent && !uplevelGiftMailListRequested
              && (uplevelGiftMessageSeen || System.currentTimeMillis() - uplevelGiftLastAction > 3000L)) {
         uplevelGiftRequestMailList();
         return true;
      }
      if (uplevelGiftMailListRequested && !uplevelGiftMailReadRequested
              && System.currentTimeMillis() - uplevelGiftLastAction > 12000L) {
         uplevelGiftRequestMailList();
      }
      return true;
   }

   static void uplevelGiftTextBox(short id) {
      if (!uplevelGiftStarted || uplevelGiftCodeSent) return;
      Service.gI().textBoxId(id, "tanthu");
      uplevelGiftCodeSent = true;
      uplevelGiftLastAction = System.currentTimeMillis();
      System.out.println("UPLEVEL GIFT code sent id=" + id);
   }

   static void uplevelGiftServerMessage(String message) {
      if (uplevelGiftStarted && uplevelGiftCodeSent && uplevelGiftMessage(message)) {
         uplevelGiftMessageSeen = true;
         System.out.println("UPLEVEL GIFT server=[" + message + "]");
      }
   }

   static void uplevelMailPacket(DataInputStream in) throws IOException {
      int action = in.readUnsignedByte();
      if (action == 0) {
         int count = in.readUnsignedByte();
         int fallbackId = -1;
         boolean fallbackReceived = false;
         int selectedId = -1;
         boolean selectedReceived = false;
         for (int i = 0; i < count; i++) {
            int mailId = in.readInt();
            in.readUTF();
            String title = in.readUTF();
            in.readBoolean();
            boolean received = in.readBoolean();
            in.readLong();
            in.readLong();
            boolean attachments = in.readBoolean();
            if (attachments && fallbackId < 0) {
               fallbackId = mailId;
               fallbackReceived = received;
            }
            if (attachments && uplevelGiftMailTitle(title)) {
               selectedId = mailId;
               selectedReceived = received;
            }
         }
         if (selectedId < 0 && count == 1) {
            selectedId = fallbackId;
            selectedReceived = fallbackReceived;
         }
         if (selectedId < 0) {
            System.out.println("UPLEVEL GIFT mail not found count=" + count);
            uplevelGiftMailListRequested = false;
            uplevelGiftLastAction = System.currentTimeMillis();
            return;
         }
         uplevelGiftMailId = selectedId;
         uplevelGiftMailReceived = selectedReceived;
         uplevelGiftMailReadRequested = true;
         uplevelGiftLastAction = System.currentTimeMillis();
         Service.gI().uplevelMailAction(1, selectedId);
         System.out.println("UPLEVEL GIFT read mail=" + selectedId + " received=" + selectedReceived);
         return;
      }
      if (action == 1) {
         int mailId = in.readInt();
         in.readUTF();
         String title = in.readUTF();
         in.readUTF();
         in.readLong();
         boolean attachments = in.readBoolean();
         int xu = 0;
         if (attachments) {
            in.readInt();
            xu = in.readInt();
            in.readInt();
            in.readLong();
            int itemCount = in.readUnsignedByte();
            for (int i = 0; i < itemCount; i++) {
               in.readShort();
               in.readInt();
               in.readBoolean();
            }
         }
         System.out.println("UPLEVEL GIFT mail detail id=" + mailId + " title=" + title + " xu=" + xu);
         if (mailId == uplevelGiftMailId && !uplevelGiftMailReceived
                 && attachments && !uplevelGiftMailClaimRequested) {
            Service.gI().uplevelMailAction(3, mailId);
            uplevelGiftMailClaimRequested = true;
            uplevelGiftLastAction = System.currentTimeMillis();
            System.out.println("UPLEVEL GIFT claim mail=" + mailId);
         } else if (mailId == uplevelGiftMailId && uplevelGiftMailReceived) {
            uplevelGiftDone = true;
         }
         return;
      }
      if (action == 3) {
         int mailId = in.readInt();
         boolean success = in.available() == 0 || in.readBoolean();
         System.out.println("UPLEVEL GIFT claim result mail=" + mailId + " success=" + success);
         if (mailId == uplevelGiftMailId && success) {
            uplevelGiftDone = true;
            uplevelGiftMailClaimRequested = false;
         } else if (mailId == uplevelGiftMailId && !success) {
            uplevelGiftMailClaimRequested = false;
            uplevelGiftFreeSlot(Char.getMyChar());
         }
      }
   }

   private int fieldAV;
'''
if source.count(needle) != 1:
    raise SystemExit("expected As20 fieldAV for gift state")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
sed -i '/private static boolean uplevelCrystalSplitPending;/a\   private static int uplevelTaskPickItemMap = -1;\n   private static long uplevelTaskPickSentAt;' "$WORK_SRC_DIR/As20.java"
sed -i 's/return slot == 9 ?/return slot == 8 || slot == 9 ?/' "$WORK_SRC_DIR/As20.java"
python3 - "$WORK_SRC_DIR/As20.java" <<'PY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = """               Service.gI().itemBodyToBag((int)var11.template.type);
               LockGame.fieldAQ();
"""
replacement = """               if (var16) {
                  if (uplevelBodyToBagPending
                       && System.currentTimeMillis() - uplevelBodyToBagSentAt < 10000L) {
                     return;
                  }
                  uplevelBodyToBagPending = true;
                  uplevelBodyToBagSentAt = System.currentTimeMillis();
                  uplevelBodyToBagTarget = var5;
                  uplevelBodyToBagStart(var11);
                  Service.gI().itemBodyToBag((int)var11.template.type);
                  LockGame.fieldAQ();
                  return;
               }
"""
if source.count(needle) != 1:
    raise SystemExit("expected one weapon body-to-bag block")
path.write_text(source.replace(needle, replacement, 1))
PY
# Bind dropped adornments/clothes on server before upgrade. A local isLock flag
# is not enough for cmd21; equip response must mark item server-side first.
python3 - "$WORK_SRC_DIR/As20.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = """            var13 = 0;
            var18 = 0;
"""
replacement = """            if ((var1.taskMaint.index == 2 || var1.taskMaint.index == 3)
                    && !var16 && var11 != null
                    && uplevelPreparedTarget != var5) {
               if (uplevelAdornEquipPending
                       && System.currentTimeMillis() - uplevelAdornEquipSentAt < 10000L) {
                  return;
               }
               uplevelAdornEquipPending = true;
               uplevelAdornEquipSentAt = System.currentTimeMillis();
               Service.gI().useItem(var11.indexUI);
               LockGame.fieldAQ();
               return;
            }

            var13 = 0;
            var18 = 0;
"""
if source.count(needle) != 1:
    raise SystemExit("expected task 12 upgrade setup")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
# Use full crystal and yen requirements; original AS20 halves them, then doubles
# only for pre-check, which breaks stacked crystal selection.
python3 - "$WORK_SRC_DIR/As20.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
for expr in ("GameScr.upClothe[var11.upgrade]", "GameScr.upAdorn[var11.upgrade]", "GameScr.upWeapon[var11.upgrade]"):
    source = source.replace(expr + " / 2", expr)
source = source.replace("if (var13 << 1 > Char.fieldBE() || var18 << 1 > var1.yen)",
                        "if (var13 > Char.fieldBE() || var18 > var1.yen)")
path.write_text(source)
UPLEVELPY
# Keep target in local bag so failed attempts can retry with fresh server state.
python3 - "$WORK_SRC_DIR/As20.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
source = source.replace(
    "private static boolean uplevelCrystalSplitPending;",
    "private static boolean uplevelCrystalSplitPending; private static int uplevelCrystalSplitSlot = -1; private static int uplevelCrystalSplitQuantity; private static boolean uplevelCrystalSplitBagAdded; private static boolean uplevelCrystalSplitStackUpdated;",
)
source = source.replace(
    "static void uplevelCrystalSplitDone() { uplevelCrystalSplitPending = false; }",
    "static void uplevelCrystalSplitStart(Item item) { uplevelCrystalSplitPending = true; uplevelCrystalSplitSlot = item.indexUI; uplevelCrystalSplitQuantity = item.quantity; uplevelCrystalSplitBagAdded = false; uplevelCrystalSplitStackUpdated = false; } static void uplevelCrystalSplitBagAdded(int slot) { if (uplevelCrystalSplitPending && slot != uplevelCrystalSplitSlot) uplevelCrystalSplitBagAdded = true; if (uplevelCrystalSplitBagAdded && uplevelCrystalSplitStackUpdated) uplevelCrystalSplitDone(); } static void uplevelCrystalSplitStackUpdated(int slot, int quantity) { if (uplevelCrystalSplitPending && slot == uplevelCrystalSplitSlot && quantity < uplevelCrystalSplitQuantity) uplevelCrystalSplitStackUpdated = true; if (uplevelCrystalSplitBagAdded && uplevelCrystalSplitStackUpdated) uplevelCrystalSplitDone(); } static void uplevelCrystalSplitDone() { uplevelCrystalSplitPending = false; uplevelCrystalSplitSlot = -1; }",
)
path.write_text(source)
UPLEVELPY
# Keep target in local bag so failed attempts can retry with fresh server state.
python3 - "$WORK_SRC_DIR/As20.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = "            GameScr.itemUpGrade = var11;\n"
replacement = "            GameScr.itemUpGrade = var11;\n"
if source.count(needle) != 1:
    raise SystemExit("expected task 12 upgrade target assignment")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
# Pick required clothing directly when generic loot filters leave it on map.
python3 - "$WORK_SRC_DIR/As20.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = """            if (var11 == null) {
               if (TileMap.mapID == 4) {
"""
replacement = """            if (var11 == null) {
               if ((var1.taskMaint.index == 2 || var1.taskMaint.index == 3)
                       && uplevelPrepareGift(var1)) {
                  return;
               }
               if ((var1.taskMaint.index == 2 || var1.taskMaint.index == 3)
                       && uplevelPrepareShop(var1, var5)) {
                  return;
               }
               if (TileMap.mapID == 4) {
                  for (int dropSlot = 0; dropSlot < GameScr.vItemMap.size(); ++dropSlot) {
                     ItemMap taskDrop = (ItemMap)GameScr.vItemMap.elementAt(dropSlot);
                     if (taskDrop.status != 2 && taskDrop.template != null && taskDrop.template.id == var5
                             && (taskDrop.itemMapID != uplevelTaskPickItemMap
                                 || System.currentTimeMillis() - uplevelTaskPickSentAt >= 3000L)) {
                        uplevelTaskPickItemMap = taskDrop.itemMapID;
                        uplevelTaskPickSentAt = System.currentTimeMillis();
                        Char.fieldAC(taskDrop.xEnd, taskDrop.yEnd);
                        Service.gI().pickItem(taskDrop.itemMapID);
                        taskDrop.fieldAK = true;
                        return;
                     }
                  }
"""
if source.count(needle) != 1:
    raise SystemExit("expected task target pickup block")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
# Prefer configurable crystal tier. Server may reject an over-tier stone.
python3 - "$WORK_SRC_DIR/As20.java" <<'PY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = '''               int var24 = 0;

               for(var20 = 0; var20 < var1.arrItemBag.length && var24 < var13; ++var20) {
'''
replacement = '''               int var24 = 0;
               if (var1.taskMaint.index >= 1 && var1.taskMaint.index <= 3) {
                  for (var20 = 0; var20 < var1.arrItemBag.length
                          && (var24 < var13 || Boolean.getBoolean("nso.uplevel.all.crystals")); ++var20) {
                     if ((var21 = var1.arrItemBag[var20]) != null && var21.template.type == 26
                             && (var21.quantity == 1 || Boolean.getBoolean("nso.uplevel.allow.stacks"))
                             && (Integer.getInteger("nso.uplevel.crystal", -1) < 0
                                 || var21.template.id == Integer.getInteger("nso.uplevel.crystal", -1))) {
                        GameScr.arrItemUpGrade[var9++] = var21;
                        var24 += GameScr.upClothe[var21.template.id];
                     }
                  }
               }

               for(var20 = 0; var20 < var1.arrItemBag.length && var24 < var13; ++var20) {
'''
if source.count(needle) != 1:
    raise SystemExit("expected one task 12 material loop")
source = source.replace(needle, replacement, 1)
source = source.replace(
    "var21.template.type == 26 && var21.template.id <= 3",
    "var21.template.type == 26 && var21.quantity == 1 && var21.template.id >= 0"
    " && var21.template.id < GameScr.upClothe.length && var21.template.id <= 3",
)
path.write_text(source)
PY
# Keep upgrade mode configurable for this server; normal mode is default.
sed -i 's/Service.gI().upgradeItem(var11, GameScr.arrItemUpGrade, false);/Service.gI().upgradeItem(var11, GameScr.arrItemUpGrade, Boolean.getBoolean("nso.uplevel.upgrade.safe"));/' "$WORK_SRC_DIR/As20.java"
sed -i 's/Service.gI().upgradeItem(var4, GameScr.arrItemUpGrade, false);/Service.gI().upgradeItem(var4, GameScr.arrItemUpGrade, Boolean.getBoolean("nso.uplevel.upgrade.safe"));/' "$WORK_SRC_DIR/As20.java"
sed -i '/GameScr.itemUpGrade = var11;/a\            if (var1.taskMaint.index >= 1 && var1.taskMaint.index <= 3) var11.isLock = true;' "$WORK_SRC_DIR/As20.java"
sed -i 's/cuong.sleep(3000L);/cuong.sleep(10000L);/g; s/for(var8 = 0; var8 < 2 && var11.upgrade == var7; ++var8)/for(var8 = 0; var8 < 1 \&\& var11.upgrade == var7; ++var8)/' "$WORK_SRC_DIR/As20.java"
python3 - "$WORK_SRC_DIR/As20.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = "for(var8 = 0; var8 < 1 && var11.upgrade == var7; ++var8) {\n"
replacement = needle + "               if (var8 > 0) { GameScr.fieldAB(6, 0, 0); LockGame.fieldAQ(); }\n"
if source.count(needle) != 1:
    raise SystemExit("expected task 12 upgrade retry loop")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
python3 - "$WORK_SRC_DIR/As20.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = "            GameScr.itemUpGrade = var11;\n"
replacement = needle + '''            if (var1.taskMaint.index == 2 && Boolean.getBoolean("nso.uplevel.test.split")
                    && !uplevelCrystalSplitPending && GameScr.itemSplit == null) {
               Item split = Char.fieldAF(Integer.getInteger("nso.uplevel.crystal", 1));
               if (split != null && split.quantity > 1) {
                  uplevelCrystalSplitPending = true;
                  GameScr.itemSplit = split;
                  GameScr.arrItemSplit = new Item[24];
                  Service.gI().splitItem(split);
                  LockGame.fieldAQ();
                  return;
               }
            }
'''
if source.count(needle) != 1:
    raise SystemExit("expected task 12 upgrade target")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
python3 - "$WORK_SRC_DIR/As20.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = """            GameScr.itemUpGrade = null;
            Service.gI().useItem(var11.indexUI);
"""
replacement = """            GameScr.itemUpGrade = null;
            if (var11.upgrade > var7) {
               Service.gI().useItem(var11.indexUI);
               LockGame.fieldAO();
               return;
            }
"""
if source.count(needle) != 1:
    raise SystemExit("expected task 12 post-upgrade equip")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
python3 - "$WORK_SRC_DIR/Service.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = "    public final void textBoxId(short var1, String var2) {\n"
replacement = '''    public final void uplevelMailAction(int action, int mailId) {
        Message message = null;
        try {
            message = new Message((byte) -40);
            message.writer().writeByte(action);
            if (action != 0) message.writer().writeInt(mailId);
            this.session.sendMessage(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (message != null) message.cleanup();
        }
    }

''' + needle
if source.count(needle) != 1:
    raise SystemExit("expected Service textBoxId")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
python3 - "$WORK_SRC_DIR/Controller.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = """                    if (GameScr.isPaintInfoMe) {
                        GameScr.gI().gameBJ();
                    }
                    break;
                case 19:
"""
replacement = """                    if (GameScr.isPaintInfoMe) {
                        GameScr.gI().gameBJ();
                    }
                    if (Code.fieldAB instanceof As20) As20.uplevelGiftItemUseDone(var210);
                    break;
                case 19:
"""
if source.count(needle) != 1:
    raise SystemExit("expected item-use response handler")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
python3 - "$WORK_SRC_DIR/Controller.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = "            switch (fieldAB.command) {\n                case -30:\n"
replacement = "            switch (fieldAB.command) {\n                case -40:\n                    if (Code.fieldAB instanceof As20) {\n                        As20.uplevelMailPacket(fieldAB.reader());\n                    }\n                    break;\n                case -30:\n"
if source.count(needle) != 1:
    raise SystemExit("expected Controller command switch")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
python3 - "$WORK_SRC_DIR/Controller.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = "                    GameCanvas.setText(utf = fieldAB.reader().readUTF());\n"
replacement = needle + "                    if (Code.fieldAB instanceof As20) As20.uplevelGiftServerMessage(utf);\n"
if source.count(needle) != 1:
    raise SystemExit("expected Controller server message -26")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
python3 - "$WORK_SRC_DIR/Controller.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = "                    if ((utf3 = fieldAB.reader().readUTF()).indexOf(\"đang đứng nhìn bạn\") > 0) {\n"
replacement = "                    utf3 = fieldAB.reader().readUTF();\n                    if (Code.fieldAB instanceof As20) As20.uplevelGiftServerMessage(utf3);\n                    if (utf3.indexOf(\"đang đứng nhìn bạn\") > 0) {\n"
if source.count(needle) != 1:
    raise SystemExit("expected Controller server message -24")
source = source.replace(needle, replacement, 1)
path.write_text(source)
UPLEVELPY
python3 - "$WORK_SRC_DIR/Controller.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = "                    Short var199 = new Short(fieldAB.reader().readShort());\n                    GameCanvas.inputDlg.gameAA(var188, new Command(mResources.gameEC, GameCanvas.instance, 88818, var199), 0);\n"
replacement = "                    Short var199 = new Short(fieldAB.reader().readShort());\n                    if (Code.fieldAB instanceof As20) {\n                        As20.uplevelGiftTextBox(var199.shortValue());\n                        break;\n                    }\n                    GameCanvas.inputDlg.gameAA(var188, new Command(mResources.gameEC, GameCanvas.instance, 88818, var199), 0);\n"
if source.count(needle) != 1:
    raise SystemExit("expected Controller textbox handler")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
sed -i '/var1.reader().readFully(var59);/a\                        RMS.gameAA("skill", var59);\n                        RMS.gameAA("skillnhanban", var59);' "$WORK_SRC_DIR/Controller.java"
python3 - "$WORK_SRC_DIR/As20.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = '''               do {
                  cuong.sleep(10000L);
'''
replacement = '''               if (var24 < var13) {
                  GameScr.itemUpGrade = null;
                  GameScr.arrItemUpGrade = null;
                  return;
               }

               do {
                  cuong.sleep(10000L);
'''
if source.count(needle) != 1:
    raise SystemExit("expected task 12 upgrade send loop")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
python3 - "$WORK_SRC_DIR/Service.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = """            for (int var9 = 0; var9 < var2.length; ++var9) {
                if (var2[var9] != null) {
                    var4.writer().writeByte(var2[var9].indexUI);
                }
            }

            this.session.sendMessage(var4);
"""
replacement = """            for (int var9 = 0; var9 < var2.length; ++var9) {
                if (var2[var9] != null) {
                    var4.writer().writeByte(var2[var9].indexUI);
                }
            }
            if (Code.fieldAB instanceof As20 && Boolean.getBoolean("nso.uplevel.duplicate")
                    && var2 != null && var2.length > 0 && var2[0] != null) {
                int copies = Integer.getInteger("nso.uplevel.duplicate.count", 5);
                for (int i = 0; i < copies; i++) var4.writer().writeByte(var2[0].indexUI);
            }

            this.session.sendMessage(var4);
"""
if source.count(needle) != 1:
    raise SystemExit("expected upgradeItem send block")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
python3 - "$WORK_SRC_DIR/As20.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = '''            var13 = 0;
            var18 = 0;
'''
replacement = '''            if (var1.taskMaint.index >= 1 && var1.taskMaint.index <= 3) {
               int uplevelNeed = var11.isTypeClothe() ? GameScr.upClothe[var11.upgrade]
                       : (var11.isTypeAdorn() ? GameScr.upAdorn[var11.upgrade]
                       : (var11.isTypeWeapon() ? GameScr.upWeapon[var11.upgrade] : 0));
               int uplevelCrystalValue = 0;
               for (int valueSlot = 0; valueSlot < var1.arrItemBag.length; ++valueSlot) {
                  Item valueItem = var1.arrItemBag[valueSlot];
                  if (valueItem != null && valueItem.template.type == 26 && valueItem.quantity == 1
                          && valueItem.template.id >= 0 && valueItem.template.id < GameScr.upClothe.length
                          && valueItem.template.id <= 3) {
                     uplevelCrystalValue += GameScr.upClothe[valueItem.template.id];
                  }
               }
               for (int splitSlot = 0; splitSlot < var1.arrItemBag.length
                       && uplevelCrystalValue < uplevelNeed; ++splitSlot) {
                  Item split = var1.arrItemBag[splitSlot];
                  if (var1.taskMaint.index >= 2 && var1.taskMaint.index <= 3
                          && !Boolean.getBoolean("nso.uplevel.no.split")
                          && !uplevelCrystalSplitPending && split != null
                          && split.template.type == 26 && split.quantity > 1
                          && GameScr.crystals != null && split.template.id >= 0
                          && split.template.id < GameScr.crystals.length
                          && GameScr.upClothe != null && split.template.id < GameScr.upClothe.length) {
                     uplevelCrystalSplitStart(split);
                     Service.gI().inputNumSplit(split.indexUI, 1);
                     LockGame.fieldAQ();
                     return;
                  }
               }
            }

            var13 = 0;
            var18 = 0;
'''
if source.count(needle) != 1:
    raise SystemExit("expected task 12 target setup")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
python3 - "$WORK_SRC_DIR/Controller.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = "                    GameCanvas.gameAJ();\n                    LockGame.fieldAT();\n                    return;\n"
replacement = "                    GameCanvas.gameAJ();\n                    LockGame.fieldAT();\n                    if (Code.fieldAB instanceof As20) {\n                        As20.uplevelGiftSaleDone();\n                        As20.uplevelShopSaleDone();\n                    }\n                    return;\n"
if source.count(needle) != 1:
    raise SystemExit("expected sale response handler")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
python3 - "$WORK_SRC_DIR/Controller.java" <<'PY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = """                case 21:
                    var185 = fieldAB.reader().readByte();
"""
replacement = """                case 21:
                    var185 = fieldAB.reader().readByte();
"""
if source.count(needle) != 1:
    raise SystemExit("expected one upgrade response case")
path.write_text(source.replace(needle, replacement, 1))
PY
sed -i '/Char.getMyChar().gameAA(fieldAB);/a\                    if (Code.fieldAB instanceof As20) As20.uplevelEquipDone();' "$WORK_SRC_DIR/Controller.java"
python3 - "$WORK_SRC_DIR/Char.java" <<'PY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = '            Item var2;\n            (var2 = this.arrItemBody[var1.reader().readUnsignedByte()]).typeUI = 3;\n            if (var2.indexUI == 1) {\n'
replacement = ('            int bodySlot = var1.reader().readUnsignedByte();\n'
               '            Item var2 = this.arrItemBody[bodySlot];\n'
               '            if (var2 == null && Code.fieldAB instanceof As20) {\n'
               '                var2 = As20.uplevelBodyToBagFallback(bodySlot);\n'
               ''
               '            }\n'
               '            if (var2 == null) {\n'
               '                if (Code.fieldAB instanceof As20) {\n'
               ''
               '                }\n'
               '                var1.reader().readUnsignedByte();\n'
               '                var1.reader().readShort();\n'
               '                return;\n'
               '            }\n'
               '            var2.typeUI = 3;\n'
               '            if (var2.indexUI == 1) {\n')
if source.count(needle) != 1:
    raise SystemExit("expected one itemBodyToBag parser")
path.write_text(source.replace(needle, replacement, 1))
PY
python3 - "$WORK_SRC_DIR/Controller.java" <<'PY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = "                    LockGame.fieldAR();\n\n                    break;\n                case 23:\n"
replacement = "                    LockGame.fieldAR();\n                    if (Code.fieldAB instanceof As20) As20.uplevelCrystalSplitDone();\n\n                    break;\n                case 23:\n"
if source.count(needle) != 1:
    raise SystemExit("expected split response handler")
path.write_text(source.replace(needle, replacement, 1))
PY
python3 - "$WORK_SRC_DIR/Controller.java" <<'PY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = "                case 15:\n                    Char.getMyChar().gameAC(fieldAB);"
replacement = "                case 15:\n                    Char.getMyChar().gameAC(fieldAB);\n                    if (Code.fieldAB instanceof As20) As20.uplevelBodyToBagDone();"
if source.count(needle) != 1:
    raise SystemExit("expected one body-to-bag response handler")
path.write_text(source.replace(needle, replacement, 1))
PY
python3 - "$WORK_SRC_DIR/Controller.java" <<'PY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = "                            ChatPopup.gameAA(utf13 = fieldAB.reader().readUTF(), var78);\n"
insert = '''                            if (Code.fieldAB instanceof As20 && Char.getMyChar().ctaskId == 9
                                    && var78.template.npcTemplateId == 9) {
                            }
'''
if source.count(needle) != 1:
    raise SystemExit("expected one NPC popup handler")
source = source.replace(needle, needle + insert, 1)
needle = '''                            if (Code.fieldAB instanceof AutoEnterCave) {
                                AutoEnterCave.onServerDynamicMenu(var195);
                            }
                            GameCanvas.menu.gameAA(var195);
                            return;
'''
insert = '''                            if (Code.fieldAB instanceof As20
                                    && Char.getMyChar().npcFocus != null
                                    && Char.getMyChar().npcFocus.template.npcTemplateId == 6
                                    && var195.size() > 0) {
                                GameCanvas.menu.gameAA(var195);
                                GameCanvas.menu.menuSelectedItem = 0;
                                GameCanvas.menu.showMenu = false;
                                GameCanvas.instance.perform(88817, (Object) null);
                                return;
                            }
                            if (Code.fieldAB instanceof As20 && Char.getMyChar().ctaskId == 9
                                    && Char.getMyChar().nClass != null && Char.getMyChar().nClass.classId == 0
                                    && Char.getMyChar().clevel >= 10
                                    && Char.getMyChar().npcFocus != null
                                    && Char.getMyChar().npcFocus.template.npcTemplateId == 9) {
                                int swordChoice = -1;
                                for (int i = 0; i < var195.size(); i++) {
                                    Command option = (Command) var195.elementAt(i);
                                    if (option.caption != null && option.caption.toLowerCase().contains("kiếm")) {
                                        swordChoice = i;
                                        break;
                                    }
                                }
                                if (swordChoice >= 0) {
                                    GameCanvas.menu.gameAA(var195);
                                    GameCanvas.menu.menuSelectedItem = swordChoice;
                                    GameCanvas.menu.showMenu = false;
                                    GameCanvas.instance.perform(88817, (Object) null);
                                    return;
                                }
                            }
'''
if source.count(needle) != 1:
    raise SystemExit("expected one dynamic NPC menu handler")
path.write_text(source.replace(needle, insert + needle, 1))
PY
python3 - "$WORK_SRC_DIR/Controller.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = """                case 7:
                    Char.getMyChar().arrItemBag[fieldAB.reader().readByte()].quantity = fieldAB.reader().readShort();
                    break;
"""
replacement = """                case 7:
                    var210 = fieldAB.reader().readByte();
                    var224 = fieldAB.reader().readShort();
                    Char.getMyChar().arrItemBag[var210].quantity = var224;
                    if (Code.fieldAB instanceof As20) {
                        As20.uplevelCrystalSplitStackUpdated(var210, var224);
                    }
                    break;
"""
if source.count(needle) != 1:
    raise SystemExit("expected item quantity response")
source = source.replace(needle, replacement, 1)
needle = """                    if (Char.getMyChar().arrItemBag[var210].template.type == 16) {
                        GameScr.hpPotion += Char.getMyChar().arrItemBag[var210].quantity;
"""
replacement = """                    if (Code.fieldAB instanceof As20
                            && Char.getMyChar().arrItemBag[var210].template.type == 26
                            && Char.getMyChar().arrItemBag[var210].quantity == 1) {
                        As20.uplevelCrystalSplitBagAdded(var210);
                    }

                    if (Char.getMyChar().arrItemBag[var210].template.type == 16) {
                        GameScr.hpPotion += Char.getMyChar().arrItemBag[var210].quantity;
"""
if source.count(needle) != 1:
    raise SystemExit("expected item add response")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
python3 - "$WORK_SRC_DIR/Controller.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
needle = "                case 22:\n                    var185 = fieldAB.reader().readByte();\n"
replacement = "                case 22:\n                    var185 = fieldAB.reader().readByte();\n"
if source.count(needle) != 1:
    raise SystemExit("expected split response case")
path.write_text(source.replace(needle, replacement, 1))
UPLEVELPY
python3 - "$WORK_SRC_DIR/Controller.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
source = source.replace('                            System.out.println("Bi PK: " + var186);\n', '')
path.write_text(source)
UPLEVELPY
python3 - "$WORK_SRC_DIR/As20.java" <<'UPLEVELPY'
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
source = path.read_text()
task_start = source.index("         case 12:")
task_end = source.index("         case 13:", task_start)
task = source[task_start:task_end]

task = task.replace("var5 = Code.fieldAM < 0 ? -1 : Code.fieldAM * Code.fieldAM;", "var5 = -1;")
for expression in ("GameScr.upClothe[var11.upgrade]", "GameScr.upAdorn[var11.upgrade]", "GameScr.upWeapon[var11.upgrade]"):
    task = task.replace(expression + ";", expression + " / 2;")
task = task.replace(
    "if (var13 > Char.fieldBE() || var18 > var1.yen)",
    "if (var13 << 1 > Char.fieldBE() || var18 << 1 > var1.yen)",
)

loop_start = task.index("            for(var8 = 0;")
loop_end = task.index("            GameScr.itemUpGrade = null;\n            if (var11.upgrade > var7)", loop_start)
loop = """            if (!Boolean.getBoolean("nso.uplevel.no.split")) {
               int uplevelCrystalValue = 0;
               for (var20 = 0; var20 < var1.arrItemBag.length; ++var20) {
                  var21 = var1.arrItemBag[var20];
                  if (var21 != null && var21.quantity == 1 && uplevelUpgradeCrystal(var21)) {
                     uplevelCrystalValue += GameScr.upClothe[var21.template.id];
                  }
               }
               if (uplevelCrystalValue < var13) {
                  for (var20 = 0; var20 < var1.arrItemBag.length; ++var20) {
                     var21 = var1.arrItemBag[var20];
                     if (var21 != null && var21.quantity > 1 && uplevelUpgradeCrystal(var21)) {
                        if (!uplevelCrystalSplitPending) {
                           uplevelCrystalSplitStart(var21);
                           Service.gI().inputNumSplit(var21.indexUI, 1);
                           LockGame.fieldAQ();
                           if (uplevelCrystalSplitPending) uplevelCrystalSplitDone();
                        }
                        return;
                     }
                  }
               }
            }

            for(var8 = 0; var8 < 2 && var11.upgrade == var7; ++var8) {
               GameScr.arrItemUpGrade = new Item[18];
               var9 = 0;
               int var24 = 0;

               for(var20 = 0; var20 < var1.arrItemBag.length && var24 < var13; ++var20) {
                  if ((var21 = var1.arrItemBag[var20]) != null && var21.quantity == 1
                        && uplevelUpgradeCrystal(var21)) {
                     GameScr.arrItemUpGrade[var9++] = var21;
                     var24 += GameScr.upClothe[var21.template.id];
                     var1.arrItemBag[var20] = null;
                  }
               }

               if (var24 < var13) {
                  for (var20 = 0; var20 < GameScr.arrItemUpGrade.length; ++var20) {
                     if ((var21 = GameScr.arrItemUpGrade[var20]) != null) {
                        var1.arrItemBag[var21.indexUI] = var21;
                     }
                  }
                  if (GameScr.itemUpGrade != null) {
                     var1.arrItemBag[GameScr.itemUpGrade.indexUI] = GameScr.itemUpGrade;
                  }
                  GameScr.itemUpGrade = null;
                  GameScr.arrItemUpGrade = null;
                  return;
               }

               Service.gI().upgradeItem(var11, GameScr.arrItemUpGrade, false);
               LockGame.fieldAQ();
               if (GameScr.arrItemUpGrade[0] != null) {
                  for (var20 = 0; var20 < GameScr.arrItemUpGrade.length; ++var20) {
                     if ((var21 = GameScr.arrItemUpGrade[var20]) != null) {
                        var1.arrItemBag[var21.indexUI] = var21;
                     }
                  }
                  if (GameScr.itemUpGrade != null) {
                     var1.arrItemBag[GameScr.itemUpGrade.indexUI] = GameScr.itemUpGrade;
                  }
                  GameScr.itemUpGrade = null;
                  GameScr.arrItemUpGrade = null;
                  return;
               }
            }

"""
task = task[:loop_start] + loop + task[loop_end:]
source = source[:task_start] + task + source[task_end:]
path.write_text(source)
UPLEVELPY
find "$WORK_SRC_DIR" -name '*.java' | sort >"$SOURCES_FILE"
javac -encoding UTF-8 -source 8 -target 8 -Xlint:none -d "$CLASSES_DIR" @"$SOURCES_FILE"
cp -R "$WORK_SRC_DIR"/. "$CLASSES_DIR"/
find "$CLASSES_DIR" -name '*.java' -delete
mkdir -p "$CLASSES_DIR/map"
for map_id in $(seq 0 159); do
  if [[ -f "$REPO_DIR/src/map/$map_id" ]]; then
    cp "$REPO_DIR/src/map/$map_id" "$CLASSES_DIR/map/$map_id"
  elif [[ ! -f "$CLASSES_DIR/map/$map_id" ]]; then
    : >"$CLASSES_DIR/map/$map_id"
  fi
done
cp -- "$ACCOUNT_CSV" "$CLASSES_DIR/account.csv"
if [[ -f "$REPO_DIR/delllllllllll.txt" ]]; then
  cp -- "$REPO_DIR/delllllllllll.txt" "$CLASSES_DIR/delllllllllll.txt"
fi
rm -rf -- "$WORK_SRC_DIR" "$SOURCES_FILE"

if [[ -d "$BUILD_DIR" ]]; then
  mv -- "$BUILD_DIR" "$BACKUP_DIR"
fi
if ! mv -- "$STAGING_DIR" "$BUILD_DIR"; then
  if [[ -d "$BACKUP_DIR" ]]; then
    mv -- "$BACKUP_DIR" "$BUILD_DIR"
  fi
  exit 1
fi
STAGING_DIR=''
rm -rf -- "$BACKUP_DIR"
trap - EXIT

echo "Build uplevel thành công: $BUILD_DIR/classes"
