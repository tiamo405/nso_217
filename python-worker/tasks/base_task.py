"""
Base Task Interface for NSO Bot Tasks
"""
import time
import logging
from typing import Optional
from protocol.service import NSOService
from protocol.controller import NSOController
from config import config

logger = logging.getLogger("NSOTask")


class BaseTask:
    def __init__(self, service: NSOService, controller: NSOController):
        self.service = service
        self.controller = controller

    def run(self) -> bool:
        """Executes the task. Returns True if successful, False otherwise."""
        raise NotImplementedError

    def sleep(self, seconds: float):
        time.sleep(seconds)

    def wait_for_condition(self, condition_func, timeout: float = 5.0, step: float = 0.1) -> bool:
        deadline = time.time() + timeout
        while time.time() < deadline:
            if condition_func():
                return True
            time.sleep(step)
        return False

    def move_to_map(self, target_map_id: int, max_steps: int = 15) -> bool:
        """Helper to travel to target map (e.g. school or task map) using map graph pathfinding."""
        if self.controller.map_state.map_id == target_map_id:
            return True

        current_map = self.controller.map_state.map_id
        logger.info(f"AUTO NVHN: di chuyển tới map {target_map_id} (hiện tại map {current_map})")

        # 1. Thử dùng bùa dịch chuyển về trường nếu mục tiêu là trường
        if target_map_id in (1, 27, 72):
            for scroll_id in (194, 218, 537, 538):
                scroll = self.controller.character.find_bag_item(scroll_id)
                if scroll:
                    logger.info(f"AUTO NVHN: dùng bùa về trường (item {scroll_id})")
                    self.service.use_item(scroll.index_ui)
                    self.sleep(config.ACTION_DELAY_MAP_CHANGE)
                    if self.controller.map_state.map_id == target_map_id:
                        return True
                    current_map = self.controller.map_state.map_id

        # 2. Nếu đang ở trường và mục tiêu là trường khác -> dùng NPC 8
        if current_map in (1, 27, 72) and target_map_id in (1, 27, 72):
            shinwa_idx = 0 if target_map_id == 1 else (1 if target_map_id == 27 else 2)
            logger.info(
                f"AUTO NVHN: chuyển trường qua NPC 8, map {current_map} -> {target_map_id} "
                f"(menu={shinwa_idx}, option=0)"
            )
            if not self.interact_npc(8, shinwa_idx, 0):
                logger.warning(f"AUTO NVHN: không tìm thấy NPC 8 tại map {current_map}")
                return False
            if self.wait_for_condition(
                    lambda: self.controller.map_state.map_id == target_map_id,
                    timeout=5.0):
                return True
            logger.warning(
                f"AUTO NVHN: NPC 8 chưa chuyển tới map {target_map_id} "
                f"(hiện tại map {self.controller.map_state.map_id})"
            )
            return False

        # 3. Nếu đang ở làng và mục tiêu là trường (1, 27, 72) -> dùng Kanata NPC 0 hoặc Xa phu NPC 13
        if target_map_id in (1, 27, 72):
            school_idx = 0 if target_map_id == 1 else (1 if target_map_id == 27 else 2)
            if self.controller.map_state.find_npc(0):
                self.interact_npc(0, school_idx, 0)
                self.sleep(config.ACTION_DELAY_MAP_CHANGE)
                if self.controller.map_state.map_id == target_map_id:
                    return True
            if self.controller.map_state.find_npc(13):
                self.interact_npc(13, school_idx, 0)
                self.sleep(config.ACTION_DELAY_MAP_CHANGE)
                if self.controller.map_state.map_id == target_map_id:
                    return True

        # 4. Pathfinding theo MAP_GRAPH (Dynamic BFS)
        from models.map_graph import find_map_path, MAP_GRAPH

        step_count = 0
        while self.controller.map_state.map_id != target_map_id and step_count < max_steps:
            cur = self.controller.map_state.map_id
            if cur == target_map_id:
                return True

            path = find_map_path(cur, target_map_id)
            if not path or len(path) < 2:
                break
            next_map = path[1]
            step_count += 1

            # Chuyển giữa các trường bằng Shinwa
            if cur in (1, 27, 72) and next_map in (1, 27, 72):
                shinwa_idx = 0 if next_map == 1 else (1 if next_map == 27 else 2)
                logger.info(
                    f"AUTO NVHN: chuyển trường qua NPC 8, map {cur} -> {next_map} "
                    f"(menu={shinwa_idx}, option=0)"
                )
                if not self.interact_npc(8, shinwa_idx, 0):
                    logger.warning(f"AUTO NVHN: không tìm thấy NPC 8 tại map {cur}")
                    return False
                if self.wait_for_condition(
                        lambda: self.controller.map_state.map_id == next_map,
                        timeout=5.0):
                    continue
                logger.warning(
                    f"AUTO NVHN: NPC 8 chưa chuyển tới map {next_map} "
                    f"(hiện tại map {self.controller.map_state.map_id})"
                )
                return False

            # Di chuyển qua waypoint tương ứng
            waypoints = self.controller.map_state.waypoints
            cur_neighbors = MAP_GRAPH.get(cur, [])
            wp_idx = cur_neighbors.index(next_map) if next_map in cur_neighbors else -1

            if waypoints and 0 <= wp_idx < len(waypoints):
                wp = waypoints[wp_idx]
                target_x = (wp.min_x + wp.max_x) // 2
                target_y = wp.max_y
                self.char_move_to(target_x, target_y)
                self.service.request_change_map()
                self.sleep(config.ACTION_DELAY_MAP_CHANGE)
            elif waypoints:
                for wp in waypoints:
                    target_x = (wp.min_x + wp.max_x) // 2
                    target_y = wp.max_y
                    self.char_move_to(target_x, target_y)
                    self.service.request_change_map()
                    self.sleep(config.ACTION_DELAY_MAP_CHANGE)
                    if self.controller.map_state.map_id != cur:
                        break
            else:
                self.service.request_change_map()
                self.sleep(config.ACTION_DELAY_MAP_CHANGE)

        return self.controller.map_state.map_id == target_map_id

    def char_move_to(self, target_x: int, target_y: int):
        """Moves character to target (x, y) coordinates."""
        self.service.char_move(target_x, target_y)
        self.controller.map_state.char_x = target_x
        self.controller.map_state.char_y = target_y
        self.sleep(config.ACTION_DELAY_SHORT)

    def interact_npc(self, npc_id: int, menu_id: int = 0, option_id: int = 0) -> bool:
        """Moves near NPC position, opens NPC menu, and selects option."""
        npc = self.controller.map_state.find_npc(npc_id)
        if not npc:
            return False
        self.char_move_to(npc.x, npc.y)
        self.sleep(config.ACTION_DELAY_SHORT)
        if menu_id >= 0 and option_id >= 0:
            self.service.menu(0, npc_id, menu_id, option_id)
            self.sleep(config.ACTION_DELAY_NORMAL)
        return True
