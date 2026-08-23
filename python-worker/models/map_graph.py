"""
NSO Map Graph and Pathfinding (Ported from TileMap.java fieldBZ)
"""
from collections import deque
from typing import List, Dict, Optional

# Connection graph between maps
MAP_GRAPH: Dict[int, List[int]] = {
    0: [27],
    1: [2, 3, 27, 72, 91, 94, 105, 114, 125, 157, 139, 113, 80],
    2: [6, 1],
    3: [1, 4],
    4: [3, 5],
    5: [7, 4],
    6: [7, 2, 20, 21],
    7: [6, 5, 8],
    8: [7, 9],
    9: [8, 10],
    10: [9, 11, 17, 22, 32, 38, 43, 48, 139],
    11: [12, 10],
    12: [11, 57],
    13: [57, 14],
    14: [13, 15],
    15: [14, 16],
    16: [15, 17],
    17: [16, 18, 10, 22, 32, 38, 43, 48, 139],
    18: [17, 19],
    19: [18, 58],
    20: [6],
    21: [22, 6],
    22: [23, 21, 10, 17, 32, 38, 43, 48, 139],
    23: [22, 69, 25],
    24: [59, 36],
    25: [23, 26],
    26: [27, 25],
    27: [26, 28, 1, 72, 91, 94, 105, 114, 125, 157, 139, 113, 80],
    28: [27, 60],
    29: [60, 30],
    30: [29, 31],
    31: [32, 30],
    32: [31, 61, 10, 17, 22, 38, 43, 48, 139],
    33: [61, 34],
    34: [35, 33],
    35: [34, 66],
    36: [37, 24],
    37: [36],
    38: [67, 68, 10, 17, 22, 32, 43, 48, 139],
    39: [72, 46, 40],
    40: [39, 65, 41],
    41: [42, 40, 43],
    42: [62, 41],
    43: [41, 44, 10, 17, 22, 32, 38, 48, 139],
    44: [43, 45],
    45: [44, 53],
    46: [63, 39, 47],
    47: [46, 48],
    48: [47, 50, 10, 17, 22, 32, 38, 43, 139],
    49: [50, 51],
    50: [48, 49],
    51: [52, 49],
    52: [51, 64],
    53: [54, 45],
    54: [55, 53],
    55: [54],
    56: [72],
    57: [12, 13],
    58: [19],
    59: [68, 24],
    60: [28, 29],
    61: [33, 32],
    62: [42],
    63: [46],
    64: [52],
    65: [40],
    66: [67, 35],
    67: [66, 38],
    68: [59, 38],
    69: [70, 23],
    70: [69, 71],
    71: [72, 70],
    72: [71, 39, 1, 27, 91, 94, 105, 114, 125, 157, 139, 113, 80],
    73: [1],
}

SCHOOL_MAPS = {1, 27, 72}
VILLAGE_MAPS = {10, 17, 22, 32, 38, 43, 48}


def find_map_path(start_map: int, target_map: int) -> Optional[List[int]]:
    """BFS pathfinding from start_map to target_map returning list of map IDs."""
    if start_map == target_map:
        return [start_map]

    queue = deque([[start_map]])
    visited = {start_map}

    while queue:
        path = queue.popleft()
        current = path[-1]

        for neighbor in MAP_GRAPH.get(current, []):
            if neighbor == target_map:
                return path + [neighbor]
            if neighbor not in visited:
                visited.add(neighbor)
                queue.append(path + [neighbor])

    return None
