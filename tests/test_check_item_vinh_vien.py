import contextlib
import io
import unittest

import check_item_vinh_vien as target


class CheckItemVinhVienTest(unittest.TestCase):
    def test_matching_items_are_split_by_expiry(self):
        item = target._INVENTORY_MODULE.BagItem(
            index=0,
            template_id=851,
            name="permanent",
            quantity=2,
            is_lock=False,
            is_expires=False,
        )
        expiring = target._INVENTORY_MODULE.BagItem(
            index=1,
            template_id=818,
            name="expiring",
            quantity=1,
            is_lock=True,
            is_expires=True,
        )

        class FakeClient:
            bag = [item, expiring, None]

        with contextlib.redirect_stdout(io.StringIO()):
            result = target.check_character(FakeClient(), "user", "char", {851, 818})

        self.assertEqual(
            (result.matched, result.permanent),
            (1, 1),
        )
        self.assertEqual(
            result.items[0].username,
            "user",
        )
        self.assertEqual(result.items[0].character_name, "char")
        self.assertEqual(result.items[0].quantity, 2)


if __name__ == "__main__":
    unittest.main()
