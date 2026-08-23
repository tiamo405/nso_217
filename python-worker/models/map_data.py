"""
Data models for Map, Waypoint, NPC, Mob, and ItemMap
"""
from dataclasses import dataclass, field
from typing import List, Optional


@dataclass
class Waypoint:
    min_x: int
    min_y: int
    max_x: int
    max_y: int


@dataclass
class NPC:
    status: int
    npc_id: int
    x: int
    y: int
    template_id: int


@dataclass
class Mob:
    mob_id: int
    template_id: int
    hp: int
    max_hp: int
    x: int
    y: int
    level: int = 1
    is_boss: bool = False


@dataclass
class ItemMap:
    item_map_id: int
    item_id: int
    x: int
    y: int


@dataclass
class MapState:
    map_id: int = 0
    tile_id: int = 0
    bg_id: int = 0
    type_map: int = 0
    map_name: str = ""
    zone_id: int = 0
    char_x: int = 0
    char_y: int = 0
    waypoints: List[Waypoint] = field(default_factory=list)
    npcs: List[NPC] = field(default_factory=list)
    mobs: List[Mob] = field(default_factory=list)
    items: List[ItemMap] = field(default_factory=list)

    def is_school(self) -> bool:
        return self.map_id in (1, 27, 72)

    def is_cave(self) -> bool:
        return self.map_id in (91, 92, 93, 94, 95, 96, 97, 105, 106, 107, 108, 109, 114, 115, 116, 125, 126, 127, 128, 157, 158, 159)

    def find_npc(self, template_id: int) -> Optional[NPC]:
        best = None
        min_dist = float("inf")
        for npc in self.npcs:
            if npc.template_id == template_id and npc.status != 15:
                dist = abs(self.char_x - npc.x) + abs(self.char_y - npc.y)
                if dist < min_dist:
                    min_dist = dist
                    best = npc
        if best is None:
            for npc in self.npcs:
                if npc.template_id == template_id:
                    return npc
        return best
