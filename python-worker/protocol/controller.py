"""
NSO Controller & Message Dispatcher Layer
Receives, decodes, and dispatches server incoming packets to update models and notify listeners.
"""
import logging
from typing import List, Optional, Callable
from network.message import NSOMessage, MessageReader
from network.constants import (
    SERVER_CMD_SERVER_MESSAGE, SERVER_CMD_MAP_LOAD,
    SERVER_CMD_SUB_COMMAND, SERVER_CMD_NOT_MAP, SERVER_CMD_DIALOG,
    SERVER_CMD_BAG_ITEM_ADD, SERVER_CMD_USE_ITEM, SERVER_CMD_BOX_ITEMS,
    SERVER_CMD_TASK_ADD, SERVER_CMD_TASK_UPDATE, SERVER_CMD_TASK_REMOVE
)
from models.character import Character, CharSummary
from models.item import Item, ItemTemplate
from models.skill import Skill, SkillTemplate
from models.map_data import MapState, NPC, Mob, Waypoint, ItemMap
from models.task import TaskState, TaskOrder

logger = logging.getLogger("NSOController")

# Item metadata is identical for every connection in the same process. Keeping
# one shared table avoids downloading the large -119 resource on every character.
_ITEM_TEMPLATES = {}


class NSOController:
    def __init__(self, service=None):
        self.service = service
        self.character = Character()
        self.character_list: List[CharSummary] = []
        self.map_state = MapState()
        self.task_state = TaskState()
        self.last_server_message = ""
        self.is_game_ready = False
        self.is_in_character_select = False
        self.dialog_open = False
        self.menu_open = False
        self.box_loaded = False
        self.current_username = ""

        # Event Listeners
        self.on_character_list_received: Optional[Callable[[List[CharSummary]], None]] = None
        self.on_game_ready: Optional[Callable[[], None]] = None
        self.on_server_message: Optional[Callable[[str], None]] = None
        self.on_map_changed: Optional[Callable[[MapState], None]] = None

    def reset_for_account(self):
        self.character = Character()
        self.character_list.clear()
        self.map_state = MapState()
        self.task_state = TaskState()
        self.last_server_message = ""
        self.is_game_ready = False
        self.is_in_character_select = False
        self.dialog_open = False
        self.menu_open = False
        self.box_loaded = False

    def handle_message(self, msg: NSOMessage):
        cmd = msg.command
        reader = msg.reader()

        try:
            if cmd == SERVER_CMD_SERVER_MESSAGE:
                self._handle_server_message(reader)
            elif cmd == SERVER_CMD_NOT_MAP:
                self._handle_not_map(reader)
            elif cmd == SERVER_CMD_SUB_COMMAND:
                self._handle_sub_command(reader)
            elif cmd == SERVER_CMD_MAP_LOAD:
                self._handle_map_load(reader)
            elif cmd == SERVER_CMD_DIALOG:
                self._handle_dialog(reader)
            elif cmd == SERVER_CMD_BAG_ITEM_ADD:
                self._handle_bag_item_add(reader)
            elif cmd == SERVER_CMD_USE_ITEM:
                self._handle_use_item(reader)
            elif cmd == SERVER_CMD_BOX_ITEMS:
                self._parse_box_items(reader)
            elif cmd == SERVER_CMD_TASK_ADD:
                self._handle_task_add(reader)
            elif cmd == SERVER_CMD_TASK_UPDATE:
                self._handle_task_update(reader)
            elif cmd == SERVER_CMD_TASK_REMOVE:
                self._handle_task_remove(reader)
        except Exception as e:
            logger.error(f"Error parsing packet cmd={cmd}: {e}")

    def _handle_server_message(self, reader: MessageReader):
        text = reader.read_utf()
        self.last_server_message = text

        # Check NVHN daily limits
        if "Hôm nay con đã làm hết nhiệm vụ ta giao" in text:
            self.task_state.is_finished_today = True
            self.task_state.daily_task_count = 21
            logger.info("AUTO NVHN: nhân vật đã hết nhiệm vụ hôm nay (20/20)")
        elif "Đây là lần nhận nhiệm vụ thứ " in text and " trong ngày hôm nay" in text:
            try:
                part = text.split("Đây là lần nhận nhiệm vụ thứ ")[1].split(" trong ngày hôm nay")[0].strip()
                self.task_state.daily_task_count = int(part)
            except Exception:
                pass

        if self.on_server_message:
            self.on_server_message(text)

    def _handle_dialog(self, reader: MessageReader):
        try:
            text = reader.read_utf()
            self.last_server_message = text
            self.dialog_open = True
        except Exception:
            pass

    def _handle_bag_item_add(self, reader: MessageReader):
        """Parse CMD 8: server added a newly bought/received item to the bag."""
        try:
            bag_index = reader.read_byte()
            template_id = reader.read_short()
            item = Item(type_ui=3, index_ui=bag_index, template_id=template_id)
            item.template = _ITEM_TEMPLATES.get(template_id, ItemTemplate(id=template_id))
            item.is_lock = reader.read_boolean()
            if item.is_type_body() or item.is_type_ngoc_kham():
                item.upgrade = reader.read_byte()
            item.is_expires = reader.read_boolean()
            try:
                item.quantity = reader.read_unsigned_short()
            except Exception:
                item.quantity = 1

            if bag_index >= len(self.character.bag):
                self.character.bag.extend([None] * (bag_index + 1 - len(self.character.bag)))
            self.character.bag[bag_index] = item
        except Exception as e:
            logger.error(f"Error parsing bag item add (8): {e}")

    def _handle_use_item(self, reader: MessageReader):
        """Parse CMD 11: server confirmed an item was used/equipped."""
        try:
            bag_index = reader.read_byte()
            item = self.character.bag[bag_index] if 0 <= bag_index < len(self.character.bag) else None
            # This minimal state update is intentionally limited to the two Noel hats.
            # Other consumables need their real template type/quantity semantics.
            if item is not None and item.template_id in (351, 352):
                self.character.bag[bag_index] = None
                item.type_ui = 5
                item.index_ui = len(self.character.body)
                self.character.body = [body_item for body_item in self.character.body
                                       if body_item is None or body_item.template_id != item.template_id]
                self.character.body.append(item)

            self.character.speed = reader.read_byte()
            self.character.max_hp = reader.read_int()
            self.character.max_mp = reader.read_int()
            reader.read_short()  # eff5BuffHp
            reader.read_short()  # eff5BuffMp
        except Exception as e:
            logger.error(f"Error parsing use item (11): {e}")

    def _handle_task_add(self, reader: MessageReader):
        """Parse CMD 96: Add TaskOrder"""
        try:
            task_id = reader.read_byte()
            count = reader.read_int()
            max_count = reader.read_int()
            name = reader.read_utf()
            desc = reader.read_utf()
            kill_id = reader.read_unsigned_byte()
            map_id = reader.read_unsigned_byte()
            order = TaskOrder(
                task_id=task_id,
                count=count,
                max_count=max_count,
                name=name,
                description=desc,
                kill_id=kill_id,
                map_id=map_id
            )
            self.character.task_orders = [o for o in self.character.task_orders if o.task_id != task_id]
            self.character.task_orders.append(order)
            if task_id == 0:
                self.task_state.current_order = order
                logger.info(f"AUTO NVHN: server đã cấp nhiệm vụ mới [{order.name}], tiêu diệt {order.max_count} quái id={order.kill_id} tại map={order.map_id}")
        except Exception as e:
            logger.error(f"Error parsing TaskOrder add (96): {e}")

    def _handle_task_update(self, reader: MessageReader):
        """Parse CMD 97: Update TaskOrder count"""
        try:
            task_id = reader.read_byte()
            new_count = reader.read_int()
            for order in self.character.task_orders:
                if order.task_id == task_id:
                    order.count = new_count
                    break
        except Exception as e:
            logger.error(f"Error parsing TaskOrder update (97): {e}")

    def _handle_task_remove(self, reader: MessageReader):
        """Parse CMD 98: Remove TaskOrder"""
        try:
            task_id = reader.read_byte()
            self.character.task_orders = [o for o in self.character.task_orders if o.task_id != task_id]
            if task_id == 0:
                self.task_state.current_order = None
        except Exception as e:
            logger.error(f"Error parsing TaskOrder remove (98): {e}")

    def _handle_not_map(self, reader: MessageReader):
        sub_cmd = reader.read_byte()
        if sub_cmd == -123:  # Template / Resource sync
            if self.service:
                if _ITEM_TEMPLATES:
                    self.service.client_ok()
                elif not self.service.request_item_templates():
                    self.service.client_ok()
        elif sub_cmd == -119:  # ItemTemplate resource
            try:
                self._parse_item_templates(reader)
            finally:
                if self.service:
                    self.service.client_ok()
        elif sub_cmd == -126:  # Character List
            char_count = reader.read_byte()
            self.character_list.clear()
            for idx in range(char_count):
                gender = reader.read_byte()
                name = reader.read_utf()
                phai = reader.read_utf()
                level = reader.read_unsigned_byte()
                part_head = reader.read_short()
                part_wp = reader.read_short()
                part_body = reader.read_short()
                part_leg = reader.read_short()
                summary = CharSummary(
                    index=idx,
                    name=name,
                    gender=gender,
                    class_name=phai,
                    level=level,
                    part_head=part_head,
                    part_wp=part_wp,
                    part_body=part_body,
                    part_leg=part_leg
                )
                self.character_list.append(summary)
            self.is_in_character_select = True
            if self.on_character_list_received:
                self.on_character_list_received(self.character_list)

    def _parse_item_templates(self, reader: MessageReader):
        """Parse the same item resource layout as Controller.gameAA(DataInputStream)."""
        reader.read_byte()  # item data version
        option_count = reader.read_unsigned_byte()
        for _ in range(option_count):
            reader.read_utf()   # option name
            reader.read_byte()  # option type

        template_count = reader.read_short()
        if template_count < 0:
            raise ValueError(f"Invalid ItemTemplate count: {template_count}")

        templates = {}
        for template_id in range(template_count):
            item_type = reader.read_byte()
            gender = reader.read_byte()
            name = reader.read_utf()
            reader.read_utf()  # description
            level = reader.read_byte()
            reader.read_short()  # iconID
            part = reader.read_short()
            is_up_to_up = reader.read_boolean()
            templates[template_id] = ItemTemplate(
                id=template_id,
                name=name,
                type=item_type,
                gender=gender,
                level=level,
                part=part,
                is_up_to_up=is_up_to_up,
            )

        _ITEM_TEMPLATES.clear()
        _ITEM_TEMPLATES.update(templates)
        logger.info("[ITEM TEMPLATE] Received %s templates", len(_ITEM_TEMPLATES))

    def _handle_sub_command(self, reader: MessageReader):
        sub_cmd = reader.read_byte()
        if sub_cmd == -127:  # Main Character Info & Game Ready
            self._parse_character_info(reader)

    def _parse_character_info(self, reader: MessageReader):
        c = self.character
        c.char_id = reader.read_int()
        c.clan_name = reader.read_utf()
        if c.clan_name != "":
            reader.read_byte()  # ctypeClan
        reader.read_byte()  # ctaskId
        c.gender = reader.read_byte()
        reader.read_short()  # head
        c.speed = reader.read_byte()
        c.name = reader.read_utf()
        for summary in self.character_list:
            if summary.name == c.name:
                c.level = summary.level
                break
        c.pk = reader.read_byte()
        c.type_pk = reader.read_byte()
        c.max_hp = reader.read_int()
        c.hp = reader.read_int()
        c.max_mp = reader.read_int()
        c.mp = reader.read_int()
        c.exp = reader.read_long()
        reader.read_long()  # cExpDown
        reader.read_short()  # eff5BuffHp
        reader.read_short()  # eff5BuffMp
        c.n_class_id = reader.read_byte()
        reader.read_short()  # pPoint
        reader.read_short()  # potential[0]
        reader.read_short()  # potential[1]
        reader.read_int()    # potential[2]
        reader.read_int()    # potential[3]
        reader.read_short()  # sPoint

        # Parse skills
        skill_count = reader.read_byte()
        c.skills.clear()
        for _ in range(skill_count):
            skill_id = reader.read_short()
            sk = Skill(skill_id=skill_id, point=1, template=SkillTemplate(id=skill_id))
            c.skills.append(sk)
            if c.selected_skill is None:
                c.selected_skill = sk

        c.xu = reader.read_int()
        c.yen = reader.read_int()
        c.luong = reader.read_int()

        # Parse bag items
        bag_len = reader.read_unsigned_byte()
        c.bag = [None] * bag_len
        for i in range(bag_len):
            template_id = reader.read_short()
            if template_id != -1:
                item = Item(type_ui=3, index_ui=i, template_id=template_id)
                item.template = _ITEM_TEMPLATES.get(template_id, ItemTemplate(id=template_id))
                item.is_lock = reader.read_boolean()
                if item.is_type_body() or item.is_type_ngoc_kham():
                    item.upgrade = reader.read_byte()
                item.is_expires = reader.read_boolean()
                item.quantity = reader.read_unsigned_short()
                c.bag[i] = item

        # Parse equipped body items (16 items)
        c.body = []
        try:
            for _ in range(16):
                template_id = reader.read_short()
                if template_id != -1:
                    body_item = Item(type_ui=5, template_id=template_id)
                    body_item.template = ItemTemplate(id=template_id)
                    body_item.is_lock = True
                    body_item.upgrade = reader.read_byte()
                    body_item.sys = reader.read_byte()
                    c.body.append(body_item)
        except Exception as ex:
            logger.debug(f"End of body items parsing: {ex}")

        # Parse character human/nhanban & parts info
        try:
            c.is_human = reader.read_boolean()
            c.is_nhanban = reader.read_boolean()
            c.head_part = reader.read_short()
            c.wp_part = reader.read_short()
            c.body_part = reader.read_short()
            c.leg_part = reader.read_short()
            if c.head_part > -1:
                c.mask_part = c.head_part
        except Exception as ex:
            logger.debug(f"End of part info parsing: {ex}")

        self.is_game_ready = True
        logger.info(f"[GAME READY] Char={c.name} Lv={c.level} Class={c.n_class_id} HP={c.hp}/{c.max_hp} MaskPart={c.mask_part} BagSlots={len(c.bag)} Free={c.count_bag_free_slots()}")
        if self.on_game_ready:
            self.on_game_ready()


    def _parse_box_items(self, reader: MessageReader):
        try:
            # Server sends xuInBox before the number of box slots.
            self.character.xu_in_box = reader.read_int()
            box_len = reader.read_unsigned_byte()
            self.character.box = [None] * box_len
            for i in range(box_len):
                template_id = reader.read_short()
                if template_id != -1:
                    item = Item(type_ui=4, index_ui=i, template_id=template_id)
                    item.template = _ITEM_TEMPLATES.get(template_id, ItemTemplate(id=template_id))
                    item.is_lock = reader.read_boolean()
                    if item.is_type_body() or item.is_type_ngoc_kham():
                        item.upgrade = reader.read_byte()
                    item.is_expires = reader.read_boolean()
                    item.quantity = reader.read_unsigned_short()
                    self.character.box[i] = item
            self.box_loaded = True
            logger.info(f"[BOX] Received box items: {sum(1 for it in self.character.box if it is not None)} items")
        except Exception as e:
            logger.debug(f"Error parsing box items: {e}")

    def _handle_map_load(self, reader: MessageReader):
        m = self.map_state
        m.map_id = reader.read_unsigned_byte()
        m.tile_id = reader.read_byte()
        m.bg_id = reader.read_byte()
        m.type_map = reader.read_byte()
        m.map_name = reader.read_utf()
        m.zone_id = reader.read_byte()

        # Parse char coordinates and objects
        try:
            m.char_x = reader.read_short()
            m.char_y = reader.read_short()

            # Waypoints
            wp_count = reader.read_byte()
            m.waypoints.clear()
            for _ in range(wp_count):
                m.waypoints.append(Waypoint(
                    reader.read_short(), reader.read_short(),
                    reader.read_short(), reader.read_short()
                ))

            # Mobs
            mob_count = reader.read_byte()
            m.mobs.clear()
            for b in range(mob_count):
                # Read mob fields
                is_disabled = reader.read_boolean()
                is_dont_move = reader.read_boolean()
                is_fire = reader.read_boolean()
                is_ice = reader.read_boolean()
                is_wind = reader.read_boolean()
                template_id = reader.read_short()
                sys_type = reader.read_byte()
                hp = reader.read_int()
                level = reader.read_unsigned_byte()
                max_hp = reader.read_int()
                x = reader.read_short()
                y = reader.read_short()
                status = reader.read_byte()
                level_boss = reader.read_byte()
                is_boss = reader.read_boolean()
                m.mobs.append(Mob(
                    mob_id=b, template_id=template_id, hp=hp,
                    max_hp=max_hp, x=x, y=y, level=level, is_boss=is_boss
                ))

            # Skip BuNhin
            bunhin_count = reader.read_byte()
            for _ in range(bunhin_count):
                reader.read_utf()
                reader.read_short()
                reader.read_short()

            # NPCs
            npc_count = reader.read_byte()
            m.npcs.clear()
            for i in range(npc_count):
                status = reader.read_byte()
                x = reader.read_short()
                y = reader.read_short()
                template_id = reader.read_byte()
                m.npcs.append(NPC(status=status, npc_id=i, x=x, y=y, template_id=template_id))

            # ItemMap
            item_count = reader.read_byte()
            m.items.clear()
            for _ in range(item_count):
                item_map_id = reader.read_short()
                item_id = reader.read_short()
                x = reader.read_short()
                y = reader.read_short()
                m.items.append(ItemMap(item_map_id=item_map_id, item_id=item_id, x=x, y=y))

        except Exception as e:
            logger.debug(f"Partial map object parsing: {e}")

        logger.info(f"[MAP LOADED] Map={m.map_id} ({m.map_name}) Zone={m.zone_id} Pos=({m.char_x}, {m.char_y}) Mobs={len(m.mobs)} NPCs={[n.template_id for n in m.npcs]}")
        if self.on_map_changed:
            self.on_map_changed(m)
