"""
Task: Auto Enter Cave (Đi Hang Động)
Interacts with Kanata (NPC 0) at school to enter level-appropriate cave dungeon.
"""
import logging
from tasks.base_task import BaseTask
from config import config

logger = logging.getLogger("AutoCave")

KANATA_NPC = 0


class CaveTask(BaseTask):
    def run(self) -> bool:
        char = self.controller.character
        level = char.level

        # Determine target cave map
        if level < 40:
            target_map = 91
        elif level < 50:
            target_map = 94
        elif level < 60:
            target_map = 105
        elif level < 70:
            target_map = 114
        elif level < 90:
            target_map = 125
        else:
            target_map = 157

        logger.info(f"AUTO NVHN HANG: chuẩn bị vào hang qua Kanata, charLv={level} -> targetMap={target_map}")

        # Check if already inside cave
        if self.controller.map_state.is_cave() or self.controller.map_state.map_id == target_map:
            logger.info(f"AUTO NVHN HANG: đã vào hang map={self.controller.map_state.map_id}")
            return True

        # 1. Travel to School Map
        school_map = char.get_school_map_id()
        if self.controller.map_state.map_id != school_map:
            self.move_to_map(school_map)

        # 2. Interact with Kanata (NPC 0)
        self.service.open_menu(KANATA_NPC)
        self.sleep(config.ACTION_DELAY_SHORT)

        # Step 1: Open cave menu list (option 2 = "Hang động sau trường")
        self.service.menu(0, KANATA_NPC, 2, 0)
        self.sleep(config.ACTION_DELAY_NORMAL)

        # Step 2: Select enter cave (option 1 = Vào hang)
        self.service.menu(0, KANATA_NPC, 1, 0)
        self.sleep(config.ACTION_DELAY_MAP_CHANGE)

        # Verify entry
        if self.controller.map_state.is_cave() or self.controller.map_state.map_id == target_map:
            logger.info(f"AUTO NVHN HANG: đã vào hang map={self.controller.map_state.map_id} ({self.controller.map_state.map_name})")
            return True
        else:
            logger.info(f"AUTO NVHN HANG: đã gửi yêu cầu vào hang (map={self.controller.map_state.map_id})")
            return True
