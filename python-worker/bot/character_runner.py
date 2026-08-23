"""
Character Runner
Coordinates and executes the full sequence of tasks for a single character:
1. Box & Bag Cleaner
2. Preparer (Skill, Food, Noel Hat, Lucky Card, Coordinates)
3. Auto NVHN (20/20)
4. Auto Enter Cave
"""
import logging
from protocol.service import NSOService
from protocol.controller import NSOController
from tasks.box_cleaner import BoxCleanerTask
from tasks.preparer import PreparerTask
from tasks.nvhn import NVHNTask
from tasks.cave import CaveTask

logger = logging.getLogger("CharRunner")


class CharacterRunner:
    def __init__(self, service: NSOService, controller: NSOController):
        self.service = service
        self.controller = controller

    def run(self) -> bool:
        char = self.controller.character
        try:
            # 1. Clean Box & Bag
            BoxCleanerTask(self.service, self.controller).run()

            # 2. Preparation (Skill, Food, Noel Hat, Lucky Cards, Save Coordinate)
            PreparerTask(self.service, self.controller).run()

            # 3. Auto NVHN (20/20)
            NVHNTask(self.service, self.controller).run()

            # 4. Auto Enter Cave
            CaveTask(self.service, self.controller).run()

            return True

        except Exception as e:
            logger.error(f"AUTO NVHN: lỗi trong quá trình xử lý nhân vật {char.name}: {e}", exc_info=True)
            return False
