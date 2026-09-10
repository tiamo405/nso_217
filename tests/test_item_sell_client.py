"""Unit tests for item_sell_client.py."""
import tempfile
import unittest
from pathlib import Path

from item_sell_client import (
    NSOMessage,
    NSOReader,
    parse_item_ids,
    read_accounts,
    read_item_ids,
)


class ItemSellClientTest(unittest.TestCase):
    def test_parse_item_ids(self):
        content = "761; 736, 403\n738\t790\r\n# comment\n 256 "
        ids = parse_item_ids(content)
        self.assertEqual(ids, {761, 736, 403, 738, 790, 256})

    def test_read_item_ids_file(self):
        with tempfile.TemporaryDirectory() as tmpdir:
            f = Path(tmpdir) / "delllllllllll.txt"
            f.write_text("761;736;403;738", encoding="utf-8")
            ids = read_item_ids(f)
            self.assertEqual(ids, {761, 736, 403, 738})

    def test_read_accounts(self):
        with tempfile.TemporaryDirectory() as tmpdir:
            csv_path = Path(tmpdir) / "accounts.csv"
            csv_path.write_text(
                "username,password\nuser1,pass1\n\nuser2,pass2\ninvalid\n",
                encoding="utf-8-sig",
            )
            accounts = read_accounts(csv_path)
            self.assertEqual(accounts, [("user1", "pass1"), ("user2", "pass2")])

    def test_message_packet(self):
        msg = NSOMessage(14)
        msg.write_byte(5)
        msg.write_int(10)
        pkt = msg.packet()
        self.assertEqual(pkt[0], 14)
        self.assertEqual(int.from_bytes(pkt[1:3], "big"), 5)
        self.assertEqual(pkt[3], 5)
        self.assertEqual(int.from_bytes(pkt[4:8], "big"), 10)

    def test_reader(self):
        data = b"\x05\x00\x0a\x00\x00\x00\x14\x01\x00\x04test"
        r = NSOReader(data)
        self.assertEqual(r.read_byte(), 5)
        self.assertEqual(r.read_short(), 10)
        self.assertEqual(r.read_int(), 20)
        self.assertTrue(r.read_boolean())
        self.assertEqual(r.read_utf(), "test")
        self.assertEqual(r.remaining(), 0)


if __name__ == "__main__":
    unittest.main()
