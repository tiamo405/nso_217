"""
Account Runner
Manages the complete lifecycle for a single account:
- Socket connection & key exchange
- Login
- Iterates sequentially through all 3 characters
- Enforces Level >= 30 condition (skips < 30)
- Executes character pipeline for each eligible character
- Clean logout & teardown
"""
import time
import logging
from network.socket_client import NSOSocketClient
from protocol.service import NSOService
from protocol.controller import NSOController
from bot.character_runner import CharacterRunner
from config import config

logger = logging.getLogger("AccountRunner")


class AccountRunner:
    def __init__(self, username: str, password: str, host: str = config.DEFAULT_HOST, port: int = config.DEFAULT_PORT,
                 account_index: int = 0, total_accounts: int = 1):
        self.username = username
        self.password = password
        self.host = host
        self.port = port
        self.account_index = account_index
        self.total_accounts = total_accounts
        self.client = NSOSocketClient(self.host, self.port, timeout=config.SOCKET_TIMEOUT)
        self.service = NSOService(self.client)
        self.controller = NSOController(service=self.service)
        self.client.on_message_callback = self.controller.handle_message

    def run(self) -> bool:
        logger.info(f"AUTO NVHN: đăng nhập tài khoản {self.username} ({self.account_index + 1}/{self.total_accounts})")

        # 1. Connect & Login with retry
        login_success = False
        for login_attempt in range(1, 6):
            # Ensure we have a live socket connection. If connection fails, retry.
            if not self.client.connected:
                if not self.client.connect():
                    logger.error(f"AUTO NVHN: kết nối thất bại cho tài khoản {self.username}, thử lại.")
                    time.sleep(3.0)
                    continue
            # Connection is ready – proceed with login

            self.controller.reset_for_account()
            self.service.send_login(self.username, self.password, config.DEFAULT_CLIENT_VERSION)

            # Wait for character list
            deadline = time.time() + 10.0
            while not self.controller.is_in_character_select and time.time() < deadline:
                time.sleep(0.1)

            if self.controller.is_in_character_select and self.controller.character_list:
                login_success = True
                break

            msg = self.controller.last_server_message if self.controller.last_server_message else "Timeout"
            if "đã có người đăng nhập" in msg.lower() or "đang đăng nhập" in msg.lower() or msg == "Timeout":
                if login_attempt < 3:
                    logger.info(f"AUTO LOGIN: server báo '{msg}', thử lại sau 5 giây (lần {login_attempt}/3)...")
                    self.client.disconnect()
                    time.sleep(5.0)
                    continue

            logger.error(f"AUTO NVHN: đăng nhập không thành công, chuyển tài khoản. {msg}")
            self.client.disconnect()
            return False

        if not login_success:
            self.client.disconnect()
            return False

        char_list = list(self.controller.character_list)
        logger.info(f"AUTO NVHN: tài khoản {self.username} có {len(char_list)} nhân vật: {[c.name + ' (Lv ' + str(c.level) + ')' for c in char_list]}")

        # 3. Iterate sequentially through all characters (0, 1, 2)
        for idx, char_summary in enumerate(char_list):
            # Check Level >= 30
            if char_summary.level < 30:
                logger.info(f"AUTO NVHN: bỏ qua nhân vật {char_summary.name} (cấp {char_summary.level} < 30), chuyển nhân vật tiếp theo.")
                continue

            # Process eligible character
            success = self._process_single_character(char_summary.name, char_summary.level, idx, len(char_list))
            if not success:
                logger.warning(f"AUTO NVHN: nhân vật {char_summary.name} kết thúc với cảnh báo.")

        self.client.disconnect()
        logger.info(f"AUTO NVHN: tài khoản {self.username} đã xử lý xong {len(char_list)} nhân vật.")
        return True

    def _process_single_character(self, char_name: str, char_level: int, char_idx: int, total_chars: int) -> bool:
        """Selects and runs the pipeline for a single character with auto-reconnect & resume support."""
        max_retries = 3
        for attempt in range(1, max_retries + 1):
            if not self.client.connected or not self.controller.is_game_ready:
                if attempt > 1:
                    delay = min(30.0, 5.0 * (attempt - 1))
                    logger.info(f"AUTO LOGIN: mất kết nối, đăng nhập lại tài khoản hiện tại sau {int(delay)} giây (lần {attempt}/{max_retries})...")
                    time.sleep(delay)

                if not self.client.connected:
                    if not self.client.connect():
                        continue
                    self.controller.reset_for_account()
                logger.debug(f"Sending login for user={self.username!r} pwd={self.password!r} version={config.DEFAULT_CLIENT_VERSION}")
                # Wait a bit longer after key exchange to ensure server is ready
                time.sleep(2.0)
                self.service.send_login(self.username, self.password, config.DEFAULT_CLIENT_VERSION)
                deadline = time.time() + 10.0
                while not self.controller.is_in_character_select and time.time() < deadline:
                    time.sleep(0.1)

                self.controller.is_game_ready = False
                logger.info(f"AUTO NVHN: chọn nhân vật {char_name} (Lv {char_level}) [{char_idx + 1}/{total_chars}]")
                self.service.select_character(char_name)

                # Wait for Game Ready
                deadline = time.time() + 10.0
                while not self.controller.is_game_ready and time.time() < deadline:
                    time.sleep(0.1)

                if not self.controller.is_game_ready:
                    logger.error(f"AUTO NVHN: timeout chờ vào game nhân vật {char_name}")
                    self.client.disconnect()
                    continue

            # Double check in-game level
            if self.controller.character.level < 30:
                logger.info(f"AUTO NVHN: bỏ qua nhân vật {char_name} (Lv={self.controller.character.level} < 30)")
                self.client.disconnect()
                return True

            logger.info(f"AUTO NVHN: chuẩn bị nhân vật {char_name} (Lv {self.controller.character.level}, {self.controller.character.hp}/{self.controller.character.max_hp} HP)")

            # Run Bot tasks (will automatically resume existing TaskOrder if in-progress)
            runner = CharacterRunner(self.service, self.controller)
            result = runner.run()

            if result:
                logger.info(f"AUTO NVHN: hoàn tất nhân vật {char_name} [{char_idx + 1}/{total_chars}], chuyển nhân vật tiếp theo.")
                self.client.disconnect()
                time.sleep(1.5)
                return True
            else:
                # Nếu bị ngắt kết nối giữa chừng, lặp lại để đăng nhập lại và làm tiếp
                if not self.client.connected:
                    continue
                else:
                    self.client.disconnect()
                    time.sleep(1.5)
                    return False

        logger.error(f"AUTO NVHN: đã thử kết nối lại {max_retries} lần không thành công cho nhân vật {char_name}")
        self.client.disconnect()
        return False
