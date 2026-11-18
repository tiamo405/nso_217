import tempfile
import unittest
from pathlib import Path

from web_control.workerdetail import parse_worker_log, worker_detail

NVHN_LOG = """
===== START OPTIMIZED 2026-11-12 13:52:13 =====
13:52:20 AUTO NVHN: đăng nhập tài khoản acc1 (1/3)
13:52:21 AUTO ROSTER: chars=alpha:34:Kiếm|beta:12:|gamma:0:
13:52:22 AUTO NVHN: chọn nhân vật alpha
13:53:00 AUTO NVHN STATUS: username=acc1 nv=alpha level=34 map=34(Đảo Hebi) state=đang đánh Quỷ hoa nvhn=6/20 progress=6/32 hp=3748/3748 yen=6219346(+466) xu=197000 luong=880
13:59:00 AUTO NVHN: nhân vật đã hết nhiệm vụ, bắt đầu vào hang trước khi đổi nhân vật.
14:00:00 AUTO NVHN: đã xử lý hang động, chuyển nhân vật
14:00:05 AUTO NVHN: chọn nhân vật beta
14:00:10 AUTO NVHN: nhân vật beta level=12 < 30, bỏ qua
14:00:15 AUTO NVHN: chọn nhân vật gamma
14:00:20 AUTO NVHN STATUS: username=acc1 nv=gamma level=31 map=1(Trường) state=nhận nhiệm vụ nvhn=0/20 progress=0/0 hp=10/20 yen=5 xu=6 luong=7
===== START OPTIMIZED 2026-11-12 15:00:00 =====
15:00:05 AUTO NVHN: đăng nhập tài khoản acc2 (2/3)
15:00:06 AUTO ROSTER: chars=delta:40:
15:00:07 AUTO NVHN: chọn nhân vật delta
15:00:30 AUTO NVHN STATUS: username=acc2 nv=delta level=40 map=2 state=đi nvhn=1/20 progress=3/10 hp=1/2 yen=1 xu=2 luong=3 bag=4/30
"""

TA_THU_LOG = """
===== START OPTIMIZED 2026-11-12 14:30:00 =====
14:30:57 AUTO TA THU: đăng nhập account 1/1 (acc9)
14:30:58 AUTO TA THU: chọn nhân vật zeta
14:31:00 AUTO TA THU: game ready char=zeta level=34 map=1 zone=0
14:32:00 AUTO TA THU KILL: kết liễu
14:33:00 AUTO TA THU LOST: người khác kết liễu
14:33:30 AUTO TA THU FIGHT: nv=zeta map=35 z=5[L] kill=33 hp=10/20 mp=5/6 boss=none sk=none atk=0(-) ack=0(-,ok) state=OK task=0/1 yen=123456 xu=7000 luong=880
14:34:00 AUTO TA THU PACKET: server xóa TaskOrder 1, hoàn thành=2/4
14:35:00 AUTO TA THU: socket đóng do đang chuyển nhân vật/tài khoản, không reconnect
"""


class ParseWorkerLogTest(unittest.TestCase):
    def test_nvhn_accounts_and_characters(self):
        parsed = parse_worker_log(NVHN_LOG.splitlines())
        self.assertTrue(parsed["has_roster"])
        self.assertEqual(parsed["current_account"], "acc2")
        self.assertEqual(parsed["current_char"], "delta")
        acc1 = parsed["accounts"]["acc1"]["chars"]
        self.assertEqual(list(acc1), ["alpha", "beta", "gamma"])
        self.assertEqual(acc1["alpha"]["state"], "done")
        self.assertEqual(acc1["alpha"]["phai"], "Kiếm")
        self.assertEqual(acc1["alpha"]["nvhn"], 6)
        self.assertEqual(acc1["alpha"]["yen"], "6219346")
        self.assertEqual(acc1["beta"]["state"], "skipped")
        self.assertIn("< 30", acc1["beta"]["note"])
        # Restart giữa nhân vật gamma -> dừng dở, level lấy từ STATUS (roster báo 0 = không rõ).
        self.assertEqual(acc1["gamma"]["state"], "stopped")
        self.assertEqual(acc1["gamma"]["level"], 31)
        # Log cũ không có bag= -> để trống; log mới -> ô trống/tổng ô.
        self.assertIsNone(acc1["alpha"]["bag"])
        delta = parsed["accounts"]["acc2"]["chars"]["delta"]
        self.assertEqual((delta["bag"], delta["bag_max"]), (4, 30))
        self.assertEqual((delta["yen"], delta["xu"], delta["luong"]), ("1", "2", "3"))
        self.assertEqual(parsed["accounts"]["acc2"]["chars"]["delta"]["state"], "playing")
        self.assertEqual(parsed["accounts"]["acc2"]["chars"]["delta"]["at"], "2026-11-12 15:00:30")

    def test_ta_thu_results(self):
        parsed = parse_worker_log(TA_THU_LOG.splitlines())
        zeta = parsed["accounts"]["acc9"]["chars"]["zeta"]
        self.assertEqual(zeta["level"], 34)
        self.assertEqual((zeta["kills"], zeta["lost"]), (1, 1))
        self.assertEqual((zeta["task_done"], zeta["task_total"]), (2, 4))
        self.assertEqual((zeta["yen"], zeta["xu"], zeta["luong"]), ("123456", "7000", "880"))
        self.assertEqual(zeta["state"], "playing")  # dòng socket đóng không phải "bỏ qua nhân vật"


class WorkerDetailTest(unittest.TestCase):
    def test_account_states_follow_csv_order(self):
        with tempfile.TemporaryDirectory() as tmp:
            worker_dir = Path(tmp)
            (worker_dir / "account.csv").write_text("username,password\nacc1,secretpw1\nacc2,secretpw2\nacc3,secretpw3\n")
            (worker_dir / "stdout.log").write_text(NVHN_LOG)
            detail = worker_detail({"state": "RUNNING"}, worker_dir, worker_dir / "stdout.log")
            states = [(a["account"], a["state"]) for a in detail["accounts"]]
            self.assertEqual(states, [("acc1", "done"), ("acc2", "current"), ("acc3", "pending")])
            self.assertNotIn("secretpw", str(detail))  # không lộ password
            stopped = worker_detail({"state": "STOPPED"}, worker_dir, worker_dir / "stdout.log")
            self.assertEqual(stopped["accounts"][1]["state"], "paused")
            self.assertEqual(stopped["accounts"][1]["characters"][0]["state"], "stopped")


if __name__ == "__main__":
    unittest.main()
