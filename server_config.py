"""Server game duy nhất, dùng chung cho web control Ubuntu và Windows."""

from __future__ import annotations


GAME_HOST = "180.93.114.122"
GAME_PORT = 14444
# Giá trị truyền cho Java qua -Dnso.server / NSO_SERVER (dạng host:port).
DEFAULT_SERVER = f"{GAME_HOST}:{GAME_PORT}"
SERVER_LABELS = {DEFAULT_SERVER: f"NSO 217 ({DEFAULT_SERVER})"}


def normalize_server(value: str | None = None) -> str:
    """Luôn trả về server duy nhất.

    Giá trị cũ ("tk", "ninjamobile", ...) trong state/schedule.json vẫn được
    chấp nhận để không lỗi khi đọc cấu hình đã lưu, nhưng đều quy về IP mới.
    """
    return DEFAULT_SERVER
