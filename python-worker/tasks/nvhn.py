"""
Task: Auto NVHN (Nhiệm Vụ Hàng Ngày - 20/20)
Receives tasks from School NPC 25, completes combat objectives, and returns task for 20 rounds.
Handles disconnections gracefully by resuming existing in-progress tasks.
"""
import time
import logging
from tasks.base_task import BaseTask
from config import config

logger = logging.getLogger("AutoNVHN")

RIKUDOU_NPC = 25
MAX_DAILY_TASKS = 20


class NVHNTask(BaseTask):
    def run(self) -> bool:
        school_map = self.controller.character.get_school_map_id()
        completed_count = self.controller.task_state.daily_task_count

        while not self.controller.task_state.is_finished_today and completed_count < MAX_DAILY_TASKS:
            # Check connection status
            if not self.service.client.connected:
                logger.warning("AUTO NVHN: mất kết nối khi đang làm nhiệm vụ, tạm dừng để reconnect...")
                return False

            task = self.controller.character.get_nvhn_task()

            # ─── BƯỚC 1: Chưa có bất kỳ nhiệm vụ nào -> Về trường gặp NPC Rikudo (25) nhận NV mới ───
            if task is None:
                if self.controller.map_state.map_id != school_map:
                    self.move_to_map(school_map)

                if self.controller.map_state.map_id == school_map:
                    current_task_num = completed_count + 1
                    logger.info(f"AUTO NVHN: nhận nhiệm vụ ({current_task_num}/{MAX_DAILY_TASKS}) tại NPC Rikudo ({RIKUDOU_NPC})")
                    self.interact_npc(RIKUDOU_NPC, 1, 0)  # Menu 1, Option 0: Nhận NV

                    # Chờ server gửi TaskOrder (CMD 96)
                    self.wait_for_condition(lambda: self.controller.character.get_nvhn_task() is not None or self.controller.task_state.is_finished_today, timeout=3.0)

                    if self.controller.task_state.is_finished_today:
                        break

                    task = self.controller.character.get_nvhn_task()
                    if task is None:
                        self.sleep(1.0)
                        continue
                else:
                    logger.warning(f"AUTO NVHN: chưa thể về trường {school_map} (hiện tại map {self.controller.map_state.map_id}), tạm nghỉ để thử lại...")
                    self.sleep(2.0)
                    continue

            # ─── BƯỚC 2: Đã có nhiệm vụ và đã ĐỦ mục tiêu -> Về trường trả NV ───
            if task.count >= task.max_count:
                if self.controller.map_state.map_id != school_map:
                    self.move_to_map(school_map)

                if self.controller.map_state.map_id == school_map:
                    self.interact_npc(RIKUDOU_NPC, 1, 2)  # Menu 1, Option 2: Hoàn thành NV

                    # Chờ server xóa TaskOrder (CMD 98)
                    self.wait_for_condition(lambda: self.controller.character.get_nvhn_task() is None, timeout=3.0)

                    completed_count += 1
                    self.controller.task_state.daily_task_count = completed_count
                    logger.info(f"AUTO NVHN: hoàn thành nhiệm vụ lần {completed_count}/{MAX_DAILY_TASKS}")
                    self.sleep(config.ACTION_DELAY_NORMAL)
                    continue
                else:
                    logger.warning(f"AUTO NVHN: chưa thể về trường {school_map} để trả NV (hiện tại map {self.controller.map_state.map_id}), tạm nghỉ...")
                    self.sleep(2.0)
                    continue

            # ─── BƯỚC 3: Đang có nhiệm vụ DỞ DANG -> Nhờ NPC Rikudo chuyển sang map quái và đánh tiếp ───
            logger.info(f"AUTO NVHN: tiếp tục nhiệm vụ [{task.name}], tiến độ hiện tại: {task.count}/{task.max_count} (quái ID {task.kill_id} tại map {task.map_id})")

            # Nếu map chưa khớp mapId của nhiệm vụ:
            if task.map_id > 0 and self.controller.map_state.map_id != task.map_id:
                # 1. Nếu chưa ở trường, chuyển về trường trước
                if self.controller.map_state.map_id != school_map:
                    self.move_to_map(school_map)

                # 2. Ở trường, trò chuyện với NPC Rikudo chọn 'Đi làm NV' (Menu 1, Option 3) để dịch chuyển tức thì sang map quái
                if self.controller.map_state.map_id == school_map:
                    logger.info(f"AUTO NVHN: trò chuyện với NPC Rikudo ({RIKUDOU_NPC}) -> 'Đi làm NV' để dịch chuyển tức thì sang map {task.map_id}")
                    self.interact_npc(RIKUDOU_NPC, 1, 3)  # Menu 1, Option 3: Đi làm NV
                    self.wait_for_condition(lambda: self.controller.map_state.map_id == task.map_id, timeout=5.0)

                # 3. Nếu vẫn chưa tới, thử di chuyển bằng move_to_map
                if self.controller.map_state.map_id != task.map_id:
                    logger.info(f"AUTO NVHN: di chuyển tới map {task.map_id} để tiếp tục đánh quái")
                    self.move_to_map(task.map_id)

            # CHỈ đánh quái khi đã thực sự ở đúng map nhiệm vụ (hoặc task không chỉ định map)
            if task.map_id <= 0 or self.controller.map_state.map_id == task.map_id:
                combat_ok = self._do_combat_for_task(task)
                if not combat_ok:
                    return False
            else:
                logger.warning(f"AUTO NVHN: chưa thể tới map nhiệm vụ {task.map_id} (hiện tại map {self.controller.map_state.map_id}), tạm nghỉ để thử lại...")
                self.sleep(2.0)
                continue

            # Khi đủ số lượng mục tiêu, ghi log chuẩn bị về trường trả NV
            if task.count >= task.max_count:
                logger.info(f"AUTO NVHN: đã đủ mục tiêu {task.count}/{task.max_count}, về trường map {school_map} để trả nhiệm vụ")

        if completed_count >= MAX_DAILY_TASKS or self.controller.task_state.is_finished_today:
            logger.info(f"AUTO NVHN: nhân vật đã hết nhiệm vụ ({completed_count}/{MAX_DAILY_TASKS}), bắt đầu vào hang trước khi đổi nhân vật.")

        return True

    def _do_combat_for_task(self, task) -> bool:
        """Đánh quái và nhặt vật phẩm liên tục cho tới khi hoàn thành mục tiêu nhiệm vụ"""
        logger.info(f"AUTO NVHN: đang đánh quái (id={task.kill_id}) tại map {self.controller.map_state.map_id} ({self.controller.map_state.map_name}), tiến độ: {task.count}/{task.max_count}")
        char = self.controller.character
        skill_id = char.selected_skill.skill_id if char.selected_skill else (char.skills[0].skill_id if char.skills else 0)

        max_combat_time = 300.0  # Tối đa 5 phút cho 1 nhiệm vụ
        start_time = time.time()
        last_log_time = start_time

        while task.count < task.max_count and time.time() - start_time < max_combat_time:
            now = time.time()
            if now - last_log_time >= 10.0:
                logger.info(f"AUTO NVHN TIẾN ĐỘ: đã tiêu diệt {task.count}/{task.max_count} ({task.name}) tại map {self.controller.map_state.map_id}")
                last_log_time = now

            # Kiểm tra kết nối
            if not self.service.client.connected:
                logger.warning("AUTO NVHN: mất kết nối trong quá trình đánh quái!")
                return False

            # 1. Tìm quái mục tiêu phù hợp trong map
            mobs = self.controller.map_state.mobs
            matching_mobs = [m for m in mobs if (task.kill_id == 0 or m.template_id == task.kill_id) and m.hp > 0] if mobs else []

            # 2. Di chuyển và tấn công quái
            if matching_mobs:
                for target_mob in matching_mobs:
                    if not self.service.client.connected or task.count >= task.max_count:
                        break
                    self.char_move_to(target_mob.x, target_mob.y)
                    if skill_id > 0:
                        self.service.select_skill(skill_id)
                    self.service.attack_mob([target_mob.mob_id])
                    self.service.attack_mob_fast(target_mob.mob_id)
                    self.sleep(0.2)
            else:
                # Chờ quái xuất hiện / respawn
                self.sleep(0.4)

            # 3. Nhặt vật phẩm rơi trên mặt đất
            items = self.controller.map_state.items
            if items:
                for item_map in list(items):
                    self.service.pick_item(item_map.item_map_id)
                    self.sleep(config.ACTION_DELAY_SHORT)

            # 4. Kiểm tra nếu nhân vật hết máu / chết
            if char.hp <= 0:
                self.sleep(1.0)

        return True
