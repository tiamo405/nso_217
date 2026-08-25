"""
Task: Character Preparer
Handles:
1. Combat Skill selection based on character level.
2. Food purchase & usage based on level (10-50).
3. Noel Hat check, purchase & equip.
4. Lucky Ticket purchase & Card Flip (Lật hình) at Okaza.
5. Save coordinates at school (Kamakura NPC 5).
"""
import logging
from tasks.base_task import BaseTask
from config import config

logger = logging.getLogger("Preparer")

OKAZA_MAP = 72
GOOSHO_NPC = 30
FOOD_NPC = 4
KAMAKURA_NPC = 5
LUCKY_TICKET_ID = 340
REQUIRED_TICKETS = 2
NOEL_HAT_MALE_ID = 351
NOEL_HAT_FEMALE_ID = 352


class PreparerTask(BaseTask):
    def run(self) -> bool:
        char = self.controller.character

        # 1. Chọn Skill chiến đấu
        self._select_combat_skill()

        # 2. Mua thức ăn tại NPC 4 (Tabemono) và tự ăn
        self._configure_and_use_food()

        # 3. Di chuyển tới Okaza (Map 72) để kiểm tra/mua Mũ Noel & lật hình tại Goosho (NPC 30)
        if self.controller.map_state.map_id != OKAZA_MAP:
            logger.info(f"AUTO NVHN: di chuyển tới Okaza map {OKAZA_MAP} để lật hình & Mũ Noel")
            self.move_to_map(OKAZA_MAP)

        self._ensure_noel_hat()
        self._buy_and_flip_lucky_tickets()

        # 4. Di chuyển về trường theo phái (Map 1 / 27 / 72) và lưu tọa độ tại Kamakura NPC 5
        self._save_coordinates_at_school()

        return True

    def _select_combat_skill(self):
        char = self.controller.character
        if not char.skills:
            logger.info("AUTO NVHN SKILL: không có bảng kỹ năng, dùng skill hiện tại")
            return

        selected_skill = None
        # If Lv >= 30 and has at least 5 skills, choose 5th skill (index 4)
        if char.level >= 30 and len(char.skills) > 4:
            selected_skill = char.skills[4]
            logger.info(f"AUTO NVHN SKILL: dùng skill thứ 5 (skillId={selected_skill.skill_id})")
        else:
            selected_skill = char.skills[0]
            logger.info(f"AUTO NVHN SKILL: dùng skill thứ 1 (skillId={selected_skill.skill_id})")

        if selected_skill:
            char.selected_skill = selected_skill
            self.service.select_skill(selected_skill.skill_id)
            self.sleep(config.ACTION_DELAY_SHORT)

    def _configure_and_use_food(self):
        char = self.controller.character
        food_level = (char.level // 10) * 10
        if food_level < 10:
            food_level = 10
        elif food_level > 50:
            food_level = 50

        # Check if bag has food
        food_item = None
        for item in char.bag:
            if item and item.template and item.template.type == 18 and item.template.level == food_level:
                food_item = item
                break

        if not food_item:
            shop_index = 7 if food_level == 50 else (food_level // 10)
            logger.info(f"AUTO NVHN FOOD: bật tự dùng thức ăn level={food_level}, mua 2 thức ăn tại NPC 4")
            self.interact_npc(FOOD_NPC, -1, -1)
            self.service.buy_item1(9, shop_index, 2)
            self.sleep(config.ACTION_DELAY_NORMAL)

            for item in char.bag:
                if item and item.template and item.template.type == 18 and item.template.level == food_level:
                    food_item = item
                    break

        if food_item:
            logger.info(f"AUTO NVHN FOOD: đã sử dụng thức ăn level={food_level}")
            self.service.use_item(food_item.index_ui)
            self.sleep(config.ACTION_DELAY_SHORT)

    def _ensure_noel_hat(self):
        char = self.controller.character
        hat_id = NOEL_HAT_MALE_ID if char.gender == 0 else NOEL_HAT_FEMALE_ID

        # Check if already equipped or active
        if char.is_noel_mask_active(hat_id):
            logger.info(
                f"AUTO NVHN NOEL: Mũ noel đang được sử dụng id={hat_id} "
                f"maskPart={char.mask_part}, không mua"
            )
            return

        # Check in bag
        hat_item = char.find_bag_item(hat_id)

        # Check in box
        if not hat_item:
            box_hat = char.find_box_item(hat_id)
            if box_hat:
                logger.info(f"AUTO NVHN NOEL: tìm thấy Mũ noel trong rương, lấy về túi id={hat_id}")
                self.service.item_box_to_bag(box_hat.index_ui)
                self.sleep(config.ACTION_DELAY_NORMAL)
                hat_item = char.find_bag_item(hat_id)

        # If still not found, buy at Goosho NPC
        if not hat_item:
            logger.info(f"AUTO NVHN NOEL: đang mua Mũ noel id={hat_id} tại Goosho NPC 30")
            self.interact_npc(GOOSHO_NPC, -1, -1)
            self.service.request_item(32)  # Fashion shop
            self.sleep(config.ACTION_DELAY_NORMAL)
            buy_sent = self.service.buy_item(32, 0, 1)
            if not buy_sent:
                logger.warning(f"AUTO NVHN NOEL: không gửi được yêu cầu mua Mũ noel id={hat_id}")
                return
            self.wait_for_condition(lambda: char.find_bag_item(hat_id) is not None, timeout=4.0)
            hat_item = char.find_bag_item(hat_id)
            if hat_item:
                logger.info(
                    f"AUTO NVHN NOEL: đã mua Mũ noel id={hat_id} "
                    f"bagIndex={hat_item.index_ui} tại Goosho NPC 30"
                )
            else:
                logger.warning(
                    f"AUTO NVHN NOEL: chưa xác nhận mua thành công Mũ noel id={hat_id}"
                )

        if hat_item:
            logger.info(
                f"AUTO NVHN NOEL: đang sử dụng Mũ noel id={hat_id} "
                f"bagIndex={hat_item.index_ui} maskPart={char.mask_part}"
            )
            use_sent = self.service.use_item(hat_item.index_ui)
            if not use_sent:
                logger.warning(f"AUTO NVHN NOEL: không gửi được yêu cầu sử dụng Mũ noel id={hat_id}")
                return
            used = self.wait_for_condition(lambda: char.is_noel_mask_active(hat_id), timeout=4.0)
            if used:
                logger.info(
                    f"AUTO NVHN NOEL: đã dùng Mũ noel thành công id={hat_id} "
                    f"maskPart={char.mask_part}"
                )
            else:
                logger.warning(
                    f"AUTO NVHN NOEL: chưa xác nhận sử dụng thành công Mũ noel id={hat_id} "
                    f"maskPart={char.mask_part}"
                )

    def _buy_and_flip_lucky_tickets(self):
        char = self.controller.character

        current_tickets = char.count_bag_item(LUCKY_TICKET_ID)
        missing = REQUIRED_TICKETS - current_tickets
        if missing > 0:
            logger.info(f"AUTO NVHN LAT HINH: mua {missing} Phiếu may mắn tại NPC 30")
            self.interact_npc(GOOSHO_NPC, -1, -1)
            self.service.request_item(14)
            self.sleep(config.ACTION_DELAY_NORMAL)
            self.service.buy_item(14, 0, missing)
            self.sleep(config.ACTION_DELAY_NORMAL)

        # Perform 2 card flips
        flips = min(REQUIRED_TICKETS, char.count_bag_item(LUCKY_TICKET_ID))
        if flips <= 0:
            flips = REQUIRED_TICKETS

        logger.info(f"AUTO NVHN LAT HINH: số phiếu hiện có={current_tickets + max(0, missing)}, sẽ lật={flips} lượt")
        for i in range(flips):
            logger.info(f"AUTO NVHN LAT HINH: lật hình lượt #{i + 1}/{flips}")
            self.service.select_card(0)
            self.sleep(config.ACTION_DELAY_NORMAL)

    def _save_coordinates_at_school(self):
        school_map = self.controller.character.get_school_map_id()
        logger.info(f"AUTO NVHN: đang về trường map {school_map} để lưu tọa độ")
        if self.controller.map_state.map_id != school_map:
            self.move_to_map(school_map)

        if self.controller.map_state.map_id == school_map:
            logger.info("AUTO NVHN: đã tới trường, đang lưu tọa độ tại Kamakura (NPC 5)")
            self.interact_npc(KAMAKURA_NPC, 1, 0)
            self.sleep(config.ACTION_DELAY_NORMAL)
            logger.info("AUTO NVHN: lưu tọa độ xong, chuẩn bị nhận nhiệm vụ hàng ngày")
