"""
Data models for Items and ItemTemplates
"""
from dataclasses import dataclass, field
from typing import List, Optional


@dataclass
class ItemOption:
    option_type: int
    param: int


@dataclass
class ItemTemplate:
    id: int
    name: str = ""
    type: int = 0
    gender: int = 0
    level: int = 0
    part: int = -1
    is_up_to_up: bool = False


@dataclass
class Item:
    type_ui: int = 3
    index_ui: int = 0
    template_id: int = -1
    template: Optional[ItemTemplate] = None
    is_lock: bool = False
    upgrade: int = 0
    is_expires: bool = False
    quantity: int = 1
    expires: int = 0
    options: List[ItemOption] = field(default_factory=list)

    def is_type_body(self) -> bool:
        if not self.template:
            return False
        return 0 <= self.template.type <= 15

