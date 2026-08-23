"""
Data models for Character (MyChar & Character Selection Info)
"""
from dataclasses import dataclass, field
from typing import List, Optional
from models.item import Item
from models.skill import Skill
from models.task import TaskOrder


@dataclass
class CharSummary:
    index: int
    name: str
    gender: int
    class_name: str
    level: int
    part_head: int = 0
    part_wp: int = 0
    part_body: int = 0
    part_leg: int = 0


@dataclass
class Character:
    char_id: int = 0
    name: str = ""
    clan_name: str = ""
    gender: int = 0  # 0: Nam, 1: Nữ
    level: int = 1
    exp: int = 0
    hp: int = 0
    max_hp: int = 0
    mp: int = 0
    max_mp: int = 0
    speed: int = 4
    pk: int = 0
    type_pk: int = 0
    n_class_id: int = 0  # 1: Kiếm, 2: Phi Tiêu, 3: Kunai, 4: Cung, 5: Đao, 6: Quạt
    mask_part: int = -1
    xu: int = 0
    yen: int = 0
    luong: int = 0
    selected_skill: Optional[Skill] = None
    skills: List[Skill] = field(default_factory=list)
    task_orders: List[TaskOrder] = field(default_factory=list)
    bag: List[Optional[Item]] = field(default_factory=list)
    body: List[Optional[Item]] = field(default_factory=list)
    box: List[Optional[Item]] = field(default_factory=list)

    def get_nvhn_task(self) -> Optional[TaskOrder]:
        for order in self.task_orders:
            if order.task_id == 0:
                return order
        return None

    def get_school_map_id(self) -> int:
        if self.n_class_id in (1, 2):
            return 1
        elif self.n_class_id in (3, 4):
            return 27
        else:
            return 72

    def count_bag_free_slots(self) -> int:
        return sum(1 for item in self.bag if item is None)

    def find_bag_item(self, template_id: int) -> Optional[Item]:
        for item in self.bag:
            if item and item.template_id == template_id:
                return item
        return None

    def find_box_item(self, template_id: int) -> Optional[Item]:
        for item in self.box:
            if item and item.template_id == template_id:
                return item
        return None

    def count_bag_item(self, template_id: int) -> int:
        total = 0
        for item in self.bag:
            if item and item.template_id == template_id:
                total += item.quantity
        return total
