"""
NSO Service Layer
Constructs and sends client requests to the game server.
"""
import logging
from network.socket_client import NSOSocketClient
from network.message import NSOMessage
from network.constants import (
    CMD_LOGIN, CMD_NOT_LOGIN, CMD_NOT_MAP, CMD_CHAR_MOVE, CMD_USE_ITEM, CMD_BUY_ITEM,
    CMD_SALE_ITEM, CMD_ITEM_BOX_TO_BAG, CMD_ITEM_BAG_TO_BOX,
    CMD_REQUEST_CHANGE_MAP, CMD_REQUEST_CHANGE_ZONE, CMD_MENU, CMD_OPEN_MENU,
    CMD_SELECT_SKILL, CMD_PICK_ITEM, CMD_ATTACK_MOB_FAST,
    SUB_CMD_SET_CLIENT, SUB_CMD_LOGIN, SUB_CMD_SELECT_CARD, SUB_CMD_BUY_ITEM1, SUB_CMD_REQUEST_ITEM, SUB_CMD_SELECT_CHAR_PLAY
)

logger = logging.getLogger("NSOService")


class NSOService:
    def __init__(self, client: NSOSocketClient):
        self.client = client

    def set_client_type(self) -> bool:
        """Sends client device info (CMD -29, sub -125)"""
        msg = NSOMessage(CMD_NOT_LOGIN)
        w = msg.writer()
        w.write_byte(SUB_CMD_SET_CLIENT)
        w.write_byte(1)           # CLIENT_TYPE
        w.write_byte(1)           # zoomLevel
        w.write_boolean(True)     # isGPRS
        w.write_int(480)          # width
        w.write_int(800)          # height
        w.write_boolean(True)     # isQwerty
        w.write_boolean(True)     # isTouch
        w.write_utf("Nokia6300/2.0 (06.01) Profile/MIDP-2.0 Configuration/CLDC-1.1")
        w.write_byte(0)
        w.write_int(0)
        w.write_byte(0)           # languageID
        w.write_int(0)            # userProvider
        w.write_utf("0")          # clientAgent
        return self.client.send_message(msg)

    def send_login(self, username: str, password: str, version: str = "2.1.7") -> bool:
        """Sends login request (CMD -29, sub -127)"""
        self.set_client_type()
        msg = NSOMessage(CMD_NOT_LOGIN)
        w = msg.writer()
        w.write_byte(SUB_CMD_LOGIN)
        w.write_utf(username)
        w.write_utf(password)
        w.write_utf(version)
        w.write_utf("")
        w.write_utf("")
        w.write_utf("123456789012")  # random device id
        w.write_byte(0)               # serverLogin
        w.write_utf("VALID_CLIENT_KEY")
        return self.client.send_message(msg)

    def client_ok(self) -> bool:
        """Sends clientOk confirmation (CMD -28, sub -101)"""
        msg = NSOMessage(CMD_NOT_MAP)
        w = msg.writer()
        w.write_byte(-101)
        return self.client.send_message(msg)

    def select_character(self, name: str) -> bool:
        """Selects a character to play (CMD -28, sub -126)"""
        msg = NSOMessage(CMD_NOT_MAP)
        w = msg.writer()
        w.write_byte(SUB_CMD_SELECT_CHAR_PLAY)
        w.write_utf(name)
        return self.client.send_message(msg)

    def open_menu(self, npc_id: int) -> bool:
        """Opens NPC dialog/menu (CMD 40)"""
        msg = NSOMessage(CMD_OPEN_MENU)
        w = msg.writer()
        w.write_short(npc_id)
        return self.client.send_message(msg)

    def menu(self, menu_type: int, npc_id: int, menu_id: int, option_id: int) -> bool:
        """Selects an option in NPC menu (CMD 29)"""
        msg = NSOMessage(CMD_MENU)
        w = msg.writer()
        w.write_byte(menu_type)
        w.write_byte(npc_id)
        w.write_byte(menu_id)
        w.write_byte(option_id)
        return self.client.send_message(msg)

    def request_item(self, shop_or_box_type: int) -> bool:
        """Requests store items or box items (CMD -28, sub 4)"""
        msg = NSOMessage(CMD_NOT_MAP)
        w = msg.writer()
        w.write_byte(SUB_CMD_REQUEST_ITEM)
        w.write_byte(shop_or_box_type)
        return self.client.send_message(msg)

    def buy_item(self, type_ui: int, index_ui: int, quantity: int = 1) -> bool:
        """Buys item from store (CMD 13)"""
        msg = NSOMessage(CMD_BUY_ITEM)
        w = msg.writer()
        w.write_byte(type_ui)
        w.write_byte(index_ui)
        if quantity > 1:
            w.write_short(quantity)
        return self.client.send_message(msg)

    def buy_item1(self, shop_type: int, index_ui: int, quantity: int = 1) -> bool:
        """Buys item via food/potion shop (CMD -28, sub 122)"""
        msg = NSOMessage(CMD_NOT_MAP)
        w = msg.writer()
        w.write_byte(SUB_CMD_BUY_ITEM1)
        w.write_byte(shop_type)
        w.write_byte(index_ui)
        w.write_short(quantity)
        return self.client.send_message(msg)

    def use_item(self, bag_index: int) -> bool:
        """Uses item in inventory (CMD 11)"""
        msg = NSOMessage(CMD_USE_ITEM)
        w = msg.writer()
        w.write_byte(bag_index)
        return self.client.send_message(msg)

    def sale_item(self, bag_index: int, quantity: int = 1) -> bool:
        """Sells/deletes item from inventory (CMD 14)"""
        msg = NSOMessage(CMD_SALE_ITEM)
        w = msg.writer()
        w.write_byte(bag_index)
        if quantity > 1:
            w.write_int(quantity)
        return self.client.send_message(msg)

    def item_box_to_bag(self, box_index: int) -> bool:
        """Moves item from box to bag (CMD 16)"""
        msg = NSOMessage(CMD_ITEM_BOX_TO_BAG)
        w = msg.writer()
        w.write_byte(box_index)
        return self.client.send_message(msg)

    def select_card(self, select_index: int = 0) -> bool:
        """Performs lucky card flip (CMD -28, sub -72)"""
        msg = NSOMessage(CMD_NOT_MAP)
        w = msg.writer()
        w.write_byte(SUB_CMD_SELECT_CARD)
        w.write_byte(select_index)
        return self.client.send_message(msg)

    def select_skill(self, skill_id: int) -> bool:
        """Selects combat skill (CMD 41)"""
        msg = NSOMessage(CMD_SELECT_SKILL)
        w = msg.writer()
        w.write_short(skill_id)
        return self.client.send_message(msg)

    def char_move(self, x: int, y: int) -> bool:
        """Moves character to coordinate (CMD 1)"""
        msg = NSOMessage(CMD_CHAR_MOVE)
        w = msg.writer()
        w.write_short(x)
        w.write_short(y)
        return self.client.send_message(msg)

    def request_change_map(self) -> bool:
        """Requests map change via waypoint (CMD -17)"""
        msg = NSOMessage(CMD_REQUEST_CHANGE_MAP)
        return self.client.send_message(msg)

    def request_change_zone(self, zone_id: int, index: int = 0) -> bool:
        """Requests change zone (CMD 28)"""
        msg = NSOMessage(CMD_REQUEST_CHANGE_ZONE)
        w = msg.writer()
        w.write_byte(zone_id)
        w.write_byte(index)
        return self.client.send_message(msg)

    def attack_mob_fast(self, mob_id: int) -> bool:
        """Fast attack monster (CMD 119)"""
        msg = NSOMessage(CMD_ATTACK_MOB_FAST)
        w = msg.writer()
        w.write_byte(mob_id)
        return self.client.send_message(msg)

    def attack_mob(self, mob_ids) -> bool:
        """Standard attack monster (CMD 60)"""
        if isinstance(mob_ids, int):
            mob_ids = [mob_ids]
        msg = NSOMessage(60)
        w = msg.writer()
        return self.client.send_message(msg)

    def pick_item(self, item_map_id: int) -> bool:
        """Picks dropped item from map (CMD -14)"""
        msg = NSOMessage(CMD_PICK_ITEM)
        w = msg.writer()
        w.write_short(item_map_id)
        return self.client.send_message(msg)
