"""
Task: Box & Bag Cleaner
Deletes unwanted items from Bag and Box based on delete_items.txt ID list.
"""
import os
import logging
from typing import Set
from tasks.base_task import BaseTask
from config import config

logger = logging.getLogger("BoxCleaner")


class BoxCleanerTask(BaseTask):
    def __init__(self, service, controller):
        super().__init__(service, controller)
        self.delete_ids: Set[int] = self._load_delete_ids()

    def _load_delete_ids(self) -> Set[int]:
        ids = set()
        file_path = config.DELETE_ITEMS_FILE
        if not os.path.exists(file_path):
            logger.warning(f"Delete items file not found at {file_path}")
            return ids

        try:
            with open(file_path, "r", encoding="utf-8") as f:
                content = f.read().strip()
                # Split by semicolon, comma, or newline
                for token in content.replace(";", " ").replace(",", " ").replace("\n", " ").split():
                    token = token.strip()
                    if token and token.isdigit():
                        ids.add(int(token))
            logger.info(f"Loaded {len(ids)} item IDs to delete from config")
        except Exception as e:
            logger.error(f"Error loading delete items: {e}")
        return ids

    def run(self) -> bool:
        if not self.delete_ids:
            return True

        logger.info("AUTO NVHN BOX CLEAN: dọn rương ngay sau khi vào nhân vật, trước khi mua đồ")

        # 1. Delete matching items from bag
        deleted_bag = self._clean_bag()
        free_slots = self.controller.character.count_bag_free_slots()
        logger.info(f"AUTO NVHN BAG CLEAN: hoàn tất, đã xóa={deleted_bag} item rác. Ô trống hành trang={free_slots}/{len(self.controller.character.bag)}")

        # 2. Request box data and clean matching box items
        self._clean_box()
        return True

    def _clean_bag(self) -> int:
        deleted = 0
        bag = self.controller.character.bag
        for i in range(len(bag) - 1, -1, -1):
            item = bag[i]
            if item and item.template_id in self.delete_ids:
                logger.info(f"AUTO NVHN BAG CLEAN: Del đồ item id={item.template_id} slot={item.index_ui} số lượng={item.quantity}")
                self.service.sale_item(item.index_ui, item.quantity)
                bag[i] = None
                deleted += 1
                self.sleep(config.ACTION_DELAY_SHORT)
        return deleted

    def _clean_box(self):
        logger.info("AUTO NVHN BOX CLEAN: yêu cầu dữ liệu rương")
        self.service.request_item(4)  # Shop/Box type 4 = Box
        self.sleep(config.ACTION_DELAY_LONG)

        box = self.controller.character.box
        if not box:
            logger.info("AUTO NVHN BOX CLEAN: rương đồ trống hoặc không có dữ liệu")
            return

        occupied = sum(1 for it in box if it is not None)
        logger.info(f"AUTO NVHN BOX CLEAN: đã mở rương, đang dùng={occupied}/{len(box)}")

        # Move matching items from box to bag and sell
        for i in range(len(box) - 1, -1, -1):
            item = box[i]
            if item and item.template_id in self.delete_ids:
                if self.controller.character.count_bag_free_slots() < 2:
                    logger.info("AUTO NVHN BOX CLEAN: hành trang đầy, tạm dừng rút đồ từ rương")
                    break
                logger.info(f"AUTO NVHN BOX CLEAN: lấy item id={item.template_id} từ rương về túi")
                self.service.item_box_to_bag(item.index_ui)
                box[i] = None
                self.sleep(config.ACTION_DELAY_SHORT)
                # Clean from bag
                self._clean_bag()
