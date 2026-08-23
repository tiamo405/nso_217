"""
Data models for Daily Tasks (NVHN)
"""
from dataclasses import dataclass
from typing import Optional


@dataclass
class TaskOrder:
    task_id: int = 0
    count: int = 0
    max_count: int = 0
    name: str = ""
    description: str = ""
    kill_id: int = 0
    map_id: int = 0


@dataclass
class TaskState:
    daily_task_count: int = 0  # 0 to 20
    is_finished_today: bool = False
    current_order: Optional[TaskOrder] = None

