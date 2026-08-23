"""
Data models for Skills and SkillTemplates
"""
from dataclasses import dataclass
from typing import Optional


@dataclass
class SkillTemplate:
    id: int
    name: str = ""
    max_point: int = 0
    type: int = 0
    icon_id: int = 0
    desc: str = ""


@dataclass
class Skill:
    skill_id: int
    point: int = 0
    level: int = 0
    cool_down: int = 0
    dx: int = 0
    dy: int = 0
    max_target: int = 0
    template: Optional[SkillTemplate] = None

