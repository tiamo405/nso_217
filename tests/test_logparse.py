from __future__ import annotations

import tempfile
import unittest
from pathlib import Path

from web_control.logparse import parse_line, parse_lines, read_new_lines, summarize

FIGHT = (
    "11:02:03 AUTO TA THU FIGHT: nv=khongkhongkhong map=52 z=10[L] kill=35 pos=(534,168) "
    "hp=3750/3750 mp=2328/2420 boss=id36(dx=0,dy=0,hp=702261/3040000(23%)) "
    "sk=5:47(cd=200/500ms) atk=14(0.3s) ack=12(0.1s,mob=36,ok) state=OK task=0/1"
)


class LogParseTest(unittest.TestCase):
    def test_fight_line(self) -> None:
        entry = parse_line(FIGHT)
        self.assertEqual(entry["ts"], "11:02:03")
        self.assertEqual(entry["kind"], "fight")
        data = entry["data"]
        self.assertEqual((data["map"], data["zone"], data["locked"]), (52, 10, True))
        self.assertEqual(data["boss"]["hp"], 702261)
        self.assertEqual(data["boss"]["hp_max"], 3040000)
        self.assertEqual((data["atk"], data["ack"], data["ack_ok"]), (14, 12, True))
        self.assertEqual((data["task"], data["task_max"]), (0, 1))

    def test_old_compact_fight_format(self) -> None:
        data = parse_line("AUTO TA THU FIGHT: nv=a map=1->52 z=5->10[U] boss=id1(dx=1,dy=2,hp=2.9M/3M) task=0/1")["data"]
        self.assertEqual((data["map"], data["map_target"], data["zone_target"], data["locked"]), (1, 52, 10, False))
        self.assertEqual(data["boss"]["hp"], 2_900_000)

    def test_levels(self) -> None:
        cases = {
            "AUTO TA THU KILL: mình kết liễu Tà Thú map=52": "success",
            "AUTO TA THU LOST: người khác kết liễu Tà Thú map=35": "warn",
            "AUTO NVHN HANG SERVER DYNAMIC MENU[0]: Binh khí": "debug",
            "AUTO LOGIN SOCKET CLOSED: java.net.SocketException: Socket closed": "debug",
            "AUTO LOGIN SOCKET CLOSED: java.io.EOFException": "warn",
            "java.lang.NullPointerException: boom": "error",
            "Kết quả: mới chạy=1, lỗi=0": "info",
            "Kết quả: mới chạy=0, lỗi=2": "warn",
            "AUTO NVHN: đang về trường": "info",
        }
        for line, level in cases.items():
            with self.subTest(line=line):
                self.assertEqual(parse_line(line)["level"], level)

    def test_summary_counts_only_last_session(self) -> None:
        entries = parse_lines([
            "AUTO TA THU KILL: mình kết liễu",
            "===== START OPTIMIZED TA THU 2026-11-12 10:00:00 PASS 1/1 =====",
            "AUTO TA THU: game ready char=testbon level=32 map=33 zone=0",
            "AUTO TA THU LOST: người khác kết liễu",
            "AUTO TA THU DEATH: chết do quái",
            FIGHT,
        ])
        summary = summarize(entries)
        self.assertEqual((summary["kills"], summary["lost"], summary["deaths"]), (0, 1, 1))
        self.assertEqual(summary["char_name"], "khongkhongkhong")
        self.assertEqual(summary["fight"]["boss"]["id"], 36)
        self.assertEqual(summary["last_event"]["tag"], "TA THU DEATH")

    def test_read_new_lines_keeps_partial_line_and_utf8(self) -> None:
        with tempfile.TemporaryDirectory() as tmp:
            path = Path(tmp) / "stdout.log"
            text = "dòng một\ndòng h".encode("utf-8")
            path.write_bytes(text[:-1])  # cắt giữa ký tự UTF-8 cuối
            lines, position, reset = read_new_lines(path, 0)
            self.assertEqual(lines, ["dòng một"])
            self.assertFalse(reset)
            path.write_bytes("dòng một\ndòng hai\n".encode("utf-8"))
            lines, position, _ = read_new_lines(path, position)
            self.assertEqual(lines, ["dòng hai"])
            path.write_bytes(b"x\n")  # file bị tạo lại ngắn hơn
            lines, _, reset = read_new_lines(path, position)
            self.assertTrue(reset)
            self.assertEqual(lines, ["x"])


if __name__ == "__main__":
    unittest.main()
