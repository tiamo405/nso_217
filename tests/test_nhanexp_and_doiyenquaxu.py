import contextlib
import csv
import io
import struct
import sys
import tempfile
import unittest
from pathlib import Path
from types import SimpleNamespace
from unittest.mock import patch

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import nhanexp_and_doiyenquaxu as target


def utf(value):
    data = value.encode("utf-8")
    return struct.pack(">H", len(data)) + data


class NhanExpTest(unittest.TestCase):
    def test_no_exp_and_exchange_failure_are_classified(self):
        client = object.__new__(target.OfflineExpClient)
        client.exp_status = target.EXP_NOT_ATTEMPTED
        client.choose_tajima_menu = lambda *_: None
        client.receive = lambda *_: (
            client.CMD_SERVER_ERROR,
            utf("EXP luu tru khong co"),
        )
        status, _ = client.claim_free_offline_exp()
        self.assertEqual(status, target.EXP_NO_DATA)

        client.exchange_status = target.EXCHANGE_NOT_ATTEMPTED
        client.map_state = SimpleNamespace(
            char_y=20,
            find_npc=lambda _: SimpleNamespace(x=10),
        )
        client.move_character = lambda *_: None
        client.request_okanechan_menu = lambda: (["Doi yen qua xu"], None)
        client._choose_npc_menu = lambda *_: None
        client._wait_for_exchange_response = lambda: (
            38,
            struct.pack(">h", target.OKANECHAN_NPC_ID) + utf("khong du yen"),
        )
        with patch.object(target.time, "sleep"):
            ok, message = client.exchange_yen_to_xu()
        self.assertTrue(ok)
        self.assertEqual(client.exchange_status, target.EXCHANGE_NOT_RECEIVED)
        self.assertIn("BỎ QUA", message)

        client._wait_for_exchange_response = lambda: (None, None)
        with patch.object(target.time, "sleep"):
            ok, _ = client.exchange_yen_to_xu()
        self.assertFalse(ok)
        self.assertEqual(client.exchange_status, target.EXCHANGE_UNKNOWN)

        client._wait_for_exchange_response = lambda: (
            client.CMD_SERVER_ERROR,
            utf("Tai khoan cua ban da doi yen sang xu trong tuan nay!"),
        )
        with patch.object(target.time, "sleep"):
            ok, message = client.exchange_yen_to_xu()
        self.assertTrue(ok)
        self.assertEqual(client.exchange_status, target.EXCHANGE_ALREADY_RECEIVED)
        self.assertIn("ĐÃ XỬ LÝ TRONG TUẦN", message)

    def test_retry_and_failed_csv(self):
        with tempfile.TemporaryDirectory(prefix="nhanexp-test-") as directory:
            root = Path(directory)
            input_csv = root / "accounts.csv"
            failed_csv = root / "failed.csv"
            lane_log_dir = root / "lane-logs"
            lane_log_dir.mkdir()
            stale_log = lane_log_dir / "luong99.log"
            stale_log.write_text("old run", encoding="utf-8")
            input_csv.write_text(
                "username,password\nretry-success,p1\nalways-fail,p2\n",
                encoding="utf-8",
            )
            calls = {}

            def fake_attempt(_args, username, _password, **_kwargs):
                calls[username] = calls.get(username, 0) + 1
                ok = username == "retry-success" and calls[username] == 2
                return {
                    "ok": ok,
                    "reason": "synthetic failure" if not ok else "ok",
                    "stats": target._new_stats(),
                    "skipped": username == "retry-success" and ok,
                }

            output = io.StringIO()
            argv = [
                "nhanexp_and_doiyenquaxu.py",
                str(input_csv),
                "--retry-attempts", "1",
                "--retry-delay", "0",
                "--failed-csv", str(failed_csv),
                "--log-dir", str(lane_log_dir),
                "--log-file", str(root / "accounts.log"),
            ]
            with patch.object(target, "process_account_attempt", fake_attempt), \
                    patch.object(target.time, "sleep"), \
                    patch.object(sys, "argv", argv), \
                    contextlib.redirect_stdout(output):
                result = target.main()

            self.assertEqual(result, 1)
            self.assertEqual(calls, {"retry-success": 2, "always-fail": 2})
            with failed_csv.open(encoding="utf-8-sig", newline="") as handle:
                rows = list(csv.reader(handle))
            self.assertEqual(rows[0], ["username", "password", "reason"])
            self.assertEqual(rows[1][0:2], ["always-fail", "p2"])
            self.assertIn("hết 1 lần retry", rows[1][2])
            log_file = root / "accounts.log"
            self.assertTrue(log_file.is_file())
            self.assertIn("TỔNG KẾT", log_file.read_text(encoding="utf-8"))
            self.assertIn("retry-success", log_file.read_text(encoding="utf-8"))
            lane_log = root / "lane-logs" / "luong1.log"
            self.assertTrue(lane_log.is_file())
            self.assertIn("retry-success", lane_log.read_text(encoding="utf-8"))
            self.assertFalse(stale_log.exists())

    def test_exchange_wait_keeps_polling_after_socket_timeout(self):
        client = object.__new__(target.OfflineExpClient)
        responses = [
            (None, None, "timeout"),
            (38, struct.pack(">h", target.OKANECHAN_NPC_ID) + utf("khong du yen"), None),
        ]

        def receive(_timeout):
            command, data, error = responses.pop(0)
            client.last_receive_error = error
            return command, data

        client.receive = receive
        command, data = client._wait_for_exchange_response(timeout=1)
        self.assertEqual(command, 38)
        self.assertTrue(data)

    def test_offline_menu_wait_polls_after_socket_timeout(self):
        client = object.__new__(target.OfflineExpClient)
        client.choose_tajima_menu = lambda *_: None
        responses = [
            (None, None, "timeout"),
            (client.CMD_DYNAMIC_MENU, utf("0 Luong = 100%"), None),
        ]

        def receive(_timeout):
            command, data, error = responses.pop(0)
            client.last_receive_error = error
            return command, data

        client.receive = receive
        options, error = client.request_offline_exp_options(timeout=1)
        self.assertIsNone(error)
        self.assertEqual(options, ["0 Luong = 100%"])

    def test_exchange_server_info_success_is_received(self):
        client = object.__new__(target.OfflineExpClient)
        status, message = client._classify_exchange_response(
            client.CMD_SERVER_INFO,
            utf("Doi yen sang xu thanh cong!"),
        )
        self.assertEqual(status, target.EXCHANGE_RECEIVED)
        self.assertIn("thanh cong", message)

    def test_exchange_server_info_requirement_is_skipped(self):
        client = object.__new__(target.OfflineExpClient)
        status, _ = client._classify_exchange_response(
            client.CMD_SERVER_INFO,
            utf("Ban can toi thieu 30 diem hoat dong de doi yen sang xu!"),
        )
        self.assertEqual(status, target.EXCHANGE_SKIPPED)

    def test_insufficient_activity_is_skipped_without_retry(self):
        client = object.__new__(target.OfflineExpClient)
        client.exchange_status = target.EXCHANGE_NOT_ATTEMPTED
        client.map_state = SimpleNamespace(
            char_y=20,
            find_npc=lambda _: SimpleNamespace(x=10),
        )
        client.move_character = lambda *_: None
        client.request_okanechan_menu = lambda: (["Doi yen qua xu"], None)
        choices = []
        client._choose_npc_menu = lambda *args: choices.append(args)
        client._wait_for_exchange_response = lambda: (
            client.CMD_SERVER_INFO,
            utf("Ban can toi thieu 30 diem hoat dong de doi yen sang xu!"),
        )
        with patch.object(target.time, "sleep"):
            ok, message = client.exchange_yen_to_xu()
        self.assertTrue(ok)
        self.assertEqual(client.exchange_status, target.EXCHANGE_SKIPPED)
        self.assertEqual(len(choices), 1)
        self.assertIn("BỎ QUA", message)

    def test_selected_characters_exchange_in_level_order(self):
        characters = [
            ("low", 10, "school"),
            ("high", 50, "school"),
            ("middle", 30, "school"),
        ]
        exchange_flags = []

        class FakeClient:
            exp_status = target.EXP_NO_DATA
            exchange_status = target.EXCHANGE_NOT_ATTEMPTED

            def connect(self):
                return True

            def login(self, *_):
                return True

            def select_character_and_load_map(self, *_):
                return True

            def disconnect(self):
                pass

        args = SimpleNamespace(
            host="unused",
            port=0,
            character_name=None,
            character_index=None,
            max_characters=3,
            dry_run=False,
            login_delay=0,
            character_delay=0,
        )

        def fake_process(_client, level, _receive_exp=True):
            exchange_flags.append(level)
            return True, "ok"

        with patch.object(target, "discover_characters", return_value=characters), \
                patch.object(target, "OfflineExpClient", return_value=FakeClient()), \
                patch.object(target, "process_character", fake_process), \
                patch.object(target.time, "sleep"):
            result = target.process_account_attempt(args, "user", "pass")

        self.assertTrue(result["ok"])
        self.assertEqual(exchange_flags, [50, 30])

    def test_character_limit_sorts_by_level_descending(self):
        characters = [
            ("lv30", 30, "school"),
            ("lv55", 55, "school"),
            ("lv42", 42, "school"),
            ("lv55-second", 55, "school"),
        ]
        args = SimpleNamespace(
            character_name=None,
            character_index=None,
            max_characters=3,
        )
        self.assertEqual(
            target._select_character_indexes(characters, args),
            [1, 3, 2],
        )

    def test_exp_flag_defaults_on_and_can_be_disabled(self):
        with patch.object(sys, "argv", ["nhanexp_and_doiyenquaxu.py"]):
            self.assertTrue(target.build_parser().parse_args().receive_exp)
        with patch.object(sys, "argv", ["nhanexp_and_doiyenquaxu.py", "--bo-qua-exp"]):
            self.assertFalse(target.build_parser().parse_args().receive_exp)


if __name__ == "__main__":
    unittest.main()
