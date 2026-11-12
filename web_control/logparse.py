"""Phân tích log worker NVHN / Tà Thú thành dòng có cấu trúc cho dashboard.

Mỗi dòng trả về dict:
    ts     "HH:MM:SS" (từ prefix -Dnso.log.timestamp) hoặc None
    level  debug | info | success | warn | error
    kind   event | status | fight | session | trace
    tag    ví dụ "NVHN HANG", "TA THU FIGHT", "LOGIN" (có thể rỗng)
    msg    nội dung sau khi bỏ prefix giờ
    data   (tùy chọn) số liệu tách từ dòng FIGHT / NVHN STATUS
"""

from __future__ import annotations

import os
import re
from collections import deque
from pathlib import Path
from typing import Any, Iterable

TS_RE = re.compile(r"^(\d{2}:\d{2}:\d{2}) ")
SESSION_RE = re.compile(r"^===== START (.+?) =====\s*$")
TAG_RE = re.compile(r"^AUTO ([A-Z][A-Z0-9 ]*?)(?:\[\d+\])?\s*:")
NPC_TAG_RE = re.compile(r"^AUTO (TA THU NPC|NVHN NPC|TA THU SERVER|NVHN SERVER)\s*(\d*)")

LEVELS = ("debug", "info", "success", "warn", "error")

# Dòng kỹ thuật lặp nhiều, mặc định ẩn trên dashboard.
_DEBUG_PREFIXES = (
    "******",
    "GET UPDATE_",
    "lent",
    "TypeI=",
    "NotifyDV",
    "UPDATE INFO ME",
    "Load Auto",
    "AGENT:",
    "HEADLESS CONFIG",
    "HEADLESS TUNING",
    "==========",
    "NSO ULTRA",
    "Tick:",
    "Nhan sat thuong",
)
_DEBUG_FRAGMENTS = (
    "LOGIN TRACE",
    "SERVER DYNAMIC MENU",
    "SERVER COMMAND",
    "TA THU FILTER",
)
_ERROR_FRAGMENTS = ("exception", "error:", "caused by", "không tạo được", "không đọc được", "không ghi được")
_WARN_FRAGMENTS = (
    "thất bại",
    "lỗi",
    "không được",
    "không thể",
    "từ chối",
    "warn",
    "thử lại",
    "chết",
    "bị pk",
    "mất kết nối",
    "socket closed",
    "stuck",
    "quá ",
    "không dọn được",
    "hết hiệu ứng",
    "bỏ qua hang",
    "không khớp",
)
_SUCCESS_FRAGMENTS = (
    "hoàn tất",
    "thành công",
    "kết liễu",
    "đã xử lý",
    "trả nhiệm vụ",
    "hoàn thành=",
    "đạt ",
    "xong",
    "game ready",
)

_FIGHT_KV_RE = re.compile(r"(\w+)=((?:\([^)]*\)|\S)+)")
_RATIO_RE = re.compile(r"^(-?[\d.]+[kM]?)/(-?[\d.]+[kM]?)")


def _number(text: str) -> int | None:
    text = text.strip()
    try:
        if text.endswith("M"):
            return int(float(text[:-1]) * 1_000_000)
        if text.endswith("k"):
            return int(float(text[:-1]) * 1_000)
        return int(float(text))
    except ValueError:
        return None


def _ratio(value: str) -> tuple[int | None, int | None]:
    match = _RATIO_RE.match(value)
    if not match:
        return None, None
    return _number(match.group(1)), _number(match.group(2))


def parse_fight(msg: str) -> dict[str, Any]:
    """Tách dòng `AUTO TA THU FIGHT: nv=... boss=id36(dx=..,hp=a/b(23%)) ...`."""
    body = msg.split(":", 1)[1] if ":" in msg else msg
    data: dict[str, Any] = {}
    for key, value in _FIGHT_KV_RE.findall(body):
        if key == "nv":
            data["char"] = value
        elif key == "map":
            current, _, target = value.partition("->")
            data["map"] = _number(current)
            data["map_target"] = _number(target) if target else None
        elif key == "z":
            locked = value.endswith("[L]")
            zone = value.rstrip("]").rsplit("[", 1)[0]
            current, _, target = zone.partition("->")
            data["zone"] = _number(current)
            data["zone_target"] = _number(target) if target else None
            data["locked"] = locked
        elif key == "kill":
            data["kill_id"] = _number(value)
        elif key in ("hp", "mp"):
            cur, maximum = _ratio(value)
            data[key] = cur
            data[f"{key}_max"] = maximum
        elif key == "boss":
            if value == "none":
                data["boss"] = None
            else:
                boss: dict[str, Any] = {}
                id_match = re.match(r"id(\d+)", value)
                if id_match:
                    boss["id"] = int(id_match.group(1))
                hp_match = re.search(r"hp=([\d.]+[kM]?)/([\d.]+[kM]?)", value)
                if hp_match:
                    boss["hp"] = _number(hp_match.group(1))
                    boss["hp_max"] = _number(hp_match.group(2))
                    if boss["hp"] is not None and boss["hp_max"]:
                        boss["pct"] = round(boss["hp"] * 100 / boss["hp_max"], 1)
                dist = re.search(r"dx=(-?\d+),dy=(-?\d+)", value)
                if dist:
                    boss["dx"] = int(dist.group(1))
                    boss["dy"] = int(dist.group(2))
                data["boss"] = boss
        elif key == "atk":
            data["atk"] = _number(value.split("(", 1)[0])
        elif key == "ack":
            data["ack"] = _number(value.split("(", 1)[0])
            data["ack_ok"] = ",ok)" in value
        elif key == "state":
            data["state"] = value
        elif key == "task":
            cur, maximum = _ratio(value)
            data["task"] = cur
            data["task_max"] = maximum
        elif key == "sk":
            data["skill"] = value
    return data


_STATUS_KV_RE = re.compile(r"(\w+)=([^=]*?)(?=\s+\w+=|$)")


def parse_nvhn_status(msg: str) -> dict[str, Any]:
    body = msg.split(":", 1)[1] if ":" in msg else msg
    data: dict[str, Any] = {}
    for key, value in _STATUS_KV_RE.findall(body.strip()):
        value = value.strip()
        if key == "nv":
            data["char"] = value
        elif key in ("nvhn", "progress", "hp"):
            cur, maximum = _ratio(value)
            data[key] = cur
            data[f"{key}_max"] = maximum
        elif key == "level":
            data["level"] = _number(value)
        elif key in ("username", "map", "state", "yen", "xu", "luong"):
            data[key] = value
    return data


def classify(msg: str) -> tuple[str, str, str]:
    """Trả về (level, kind, tag) cho một dòng đã bỏ prefix giờ."""
    stripped = msg.strip()
    if not stripped:
        return "debug", "trace", ""
    session = SESSION_RE.match(stripped)
    if session:
        return "info", "session", "START"

    tag = ""
    tag_match = TAG_RE.match(stripped)
    if tag_match:
        tag = tag_match.group(1).strip()
    else:
        npc_match = NPC_TAG_RE.match(stripped)
        if npc_match:
            tag = npc_match.group(1)
    lower = stripped.lower()

    if msg.startswith(("\tat ", "    at ")) or stripped.startswith("at ") and "(" in stripped:
        return "error", "trace", "JAVA"
    if tag == "TA THU FIGHT":
        return "debug", "fight", tag
    if tag == "NVHN STATUS":
        return "info", "status", tag
    if any(stripped.startswith(prefix) for prefix in _DEBUG_PREFIXES) or any(
        fragment in stripped for fragment in _DEBUG_FRAGMENTS
    ):
        return "debug", "event", tag
    if tag.endswith("PACKET") and not tag.startswith("TA THU"):
        return "debug", "event", tag
    if tag == "LOGIN SOCKET CLOSED":
        # "Socket closed" là client tự đóng (đổi account/xong việc) => bình thường;
        # Connection reset / EOF là mất kết nối phía server => cảnh báo.
        detail = lower.split(":", 1)[1] if ":" in lower else lower
        return ("debug" if "socket closed" in detail else "warn"), "event", tag
    if tag == "TA THU KILL":
        return "success", "event", tag
    if tag in ("TA THU LOST", "TA THU DEATH", "TA THU PK", "TA THU WARN"):
        return "warn", "event", tag
    if any(fragment in lower for fragment in _ERROR_FRAGMENTS) or re.match(
        r"^[\w.$]+(Exception|Error)\b", stripped
    ):
        return "error", "event", tag or "JAVA"
    # Dòng tổng kết script kiểu "lỗi=0" không phải cảnh báo.
    lower = re.sub(r"lỗi=0\b", "", lower)
    if any(fragment in lower for fragment in _WARN_FRAGMENTS):
        return "warn", "event", tag
    if any(fragment in lower for fragment in _SUCCESS_FRAGMENTS):
        return "success", "event", tag
    return "info", "event", tag


def parse_line(raw: str) -> dict[str, Any]:
    line = raw.rstrip("\r\n")
    ts = None
    match = TS_RE.match(line)
    if match:
        ts = match.group(1)
        line = line[match.end():]
    level, kind, tag = classify(line)
    entry: dict[str, Any] = {"ts": ts, "level": level, "kind": kind, "tag": tag, "msg": line}
    if kind == "fight":
        entry["data"] = parse_fight(line)
    elif kind == "status":
        entry["data"] = parse_nvhn_status(line)
    elif kind == "event" and "đăng nhập" in line:
        account = parse_account(line)
        if account:
            entry["data"] = {"account": account}
    elif kind == "session":
        entry["data"] = {"label": SESSION_RE.match(line.strip()).group(1)}
    return entry


def parse_lines(lines: Iterable[str]) -> list[dict[str, Any]]:
    return [parse_line(line) for line in lines if line.strip()]


def level_at_least(level: str, minimum: str) -> bool:
    try:
        return LEVELS.index(level) >= LEVELS.index(minimum)
    except ValueError:
        return True


# ----------------------------------------------------------------- file helpers

def read_tail_lines(path: Path, max_lines: int, max_bytes: int = 512 * 1024) -> tuple[list[str], int]:
    """Đọc tối đa max_lines dòng hoàn chỉnh cuối file. Trả về (lines, size)."""
    try:
        with path.open("rb") as stream:
            stream.seek(0, os.SEEK_END)
            size = stream.tell()
            start = max(0, size - max_bytes)
            stream.seek(start)
            content = stream.read(size - start)
    except OSError:
        return [], 0
    lines = content.split(b"\n")
    if start > 0 and lines:
        lines = lines[1:]  # dòng đầu có thể bị cắt giữa chừng
    if lines and lines[-1] == b"":
        lines = lines[:-1]
    tail = deque(lines, maxlen=max(1, max_lines))
    return [line.decode("utf-8", errors="replace") for line in tail], size


def read_new_lines(path: Path, position: int, max_bytes: int = 256 * 1024) -> tuple[list[str], int, bool]:
    """Đọc các dòng hoàn chỉnh mới từ byte offset.

    Trả về (lines, new_position, reset). Phần dòng chưa có '\n' được giữ lại
    để lần sau đọc tiếp, nên không cắt giữa dòng hay giữa ký tự UTF-8.
    reset=True khi file bị cắt ngắn/xoay vòng.
    """
    try:
        size = path.stat().st_size
    except OSError:
        return [], position, False
    reset = False
    if size < position:
        position = 0
        reset = True
    if size == position:
        return [], position, reset
    try:
        with path.open("rb") as stream:
            stream.seek(position)
            chunk = stream.read(min(size - position, max_bytes))
    except OSError:
        return [], position, reset
    last_newline = chunk.rfind(b"\n")
    if last_newline < 0:
        if len(chunk) >= max_bytes:  # một dòng quá dài: cắt cứng
            return [chunk.decode("utf-8", errors="replace")], position + len(chunk), reset
        return [], position, reset
    complete = chunk[: last_newline + 1]
    lines = complete.decode("utf-8", errors="replace").split("\n")[:-1]
    return lines, position + len(complete), reset


# ------------------------------------------------------------- worker summary

# NVHN: "đăng nhập tài khoản acctest1 (1/2)"; Tà Thú: "đăng nhập account 1/2 (acctest1)".
_ACCOUNT_RES = (
    re.compile(r"đăng nhập tài khoản (?P<user>\S+) \((?P<index>\d+)/(?P<total>\d+)\)"),
    re.compile(r"đăng nhập account (?P<index>\d+)/(?P<total>\d+)(?: \((?P<user>[^)]+)\))?"),
)
_USERNAME_RE = re.compile(r"\busername=(\S+)")


def parse_account(msg: str) -> dict[str, Any] | None:
    for pattern in _ACCOUNT_RES:
        found = pattern.search(msg)
        if found:
            return {
                "index": int(found.group("index")),
                "total": int(found.group("total")),
                "username": found.group("user"),
            }
    return None


_CHAR_PATTERNS = (
    re.compile(r"game ready char=(\S+)"),
    re.compile(r"chọn nhân vật (\S+)"),
    re.compile(r"chuẩn bị nhân vật (\S+)"),
    re.compile(r"\bnv=([A-Za-z0-9_]+)"),
)


def summarize(entries: list[dict[str, Any]]) -> dict[str, Any]:
    """Tóm tắt phiên chạy gần nhất cho bảng worker.

    Chỉ xét các dòng sau dấu `===== START` cuối cùng.
    """
    start = 0
    for index in range(len(entries) - 1, -1, -1):
        if entries[index]["kind"] == "session":
            start = index
            break
    session = entries[start:]
    summary: dict[str, Any] = {
        "last_event": None,
        "fight": None,
        "status": None,
        "char_name": None,
        "account": None,
        "kills": 0,
        "lost": 0,
        "deaths": 0,
        "pk": 0,
        "warnings": 0,
        "errors": 0,
    }
    for entry in session:
        tag = entry["tag"]
        if tag == "TA THU KILL":
            summary["kills"] += 1
        elif tag == "TA THU LOST":
            summary["lost"] += 1
        elif tag == "TA THU DEATH":
            summary["deaths"] += 1
        elif tag == "TA THU PK":
            summary["pk"] += 1
            summary["deaths"] += 1
        if entry["level"] == "warn":
            summary["warnings"] += 1
        elif entry["level"] == "error":
            summary["errors"] += 1
    for entry in reversed(session):
        if summary["last_event"] is None and entry["kind"] == "event" and entry["level"] != "debug":
            summary["last_event"] = {k: entry[k] for k in ("ts", "level", "tag", "msg")}
        if summary["fight"] is None and entry["kind"] == "fight":
            summary["fight"] = {"ts": entry["ts"], **entry.get("data", {})}
        if summary["status"] is None and entry["kind"] == "status":
            summary["status"] = {"ts": entry["ts"], **entry.get("data", {})}
        if summary["char_name"] is None:
            for pattern in _CHAR_PATTERNS:
                found = pattern.search(entry["msg"])
                if found:
                    summary["char_name"] = found.group(1)
                    break
        if summary["account"] is None and entry["kind"] == "event":
            summary["account"] = parse_account(entry["msg"])
        if all(summary[key] is not None for key in ("last_event", "fight", "status", "char_name", "account")):
            break
    if summary["account"] and not summary["account"]["username"]:
        # Log Tà Thú cũ chưa in username: lấy từ dòng NVHN STATUS nếu có.
        status_user = (summary["status"] or {}).get("username")
        if status_user:
            summary["account"]["username"] = status_user
    return summary


class SummaryCache:
    """Cache tóm tắt theo (path, mtime, size) để polling 5s không parse lại."""

    def __init__(self, tail_lines: int = 1500) -> None:
        self.tail_lines = tail_lines
        self._cache: dict[str, tuple[tuple[float, int], dict[str, Any]]] = {}

    def get(self, path: Path) -> dict[str, Any] | None:
        try:
            stat = path.stat()
        except OSError:
            return None
        key = (stat.st_mtime, stat.st_size)
        cached = self._cache.get(str(path))
        if cached and cached[0] == key:
            return cached[1]
        lines, _ = read_tail_lines(path, self.tail_lines)
        summary = summarize(parse_lines(lines))
        self._cache[str(path)] = (key, summary)
        return summary
