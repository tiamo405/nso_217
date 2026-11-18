"""Chi tiết một worker NVHN / Tà Thú: mọi tài khoản và từng nhân vật (level, map, NV, yên...).

Chỉ đọc `stdout.log` (4 MB cuối) + `account.csv` của worker, không cần Java ghi file riêng:
  - `đăng nhập tài khoản X (i/n)` / `đăng nhập account i/n (X)`  -> tài khoản hiện tại
  - `AUTO ROSTER: chars=ten:level:phai|...`                         -> mọi nhân vật của tài khoản
    (src/Controller.java in khi server gửi danh sách nhân vật; build cũ không có dòng này)
  - `chọn nhân vật X` / `game ready char=X level=L`                  -> nhân vật đang chơi
  - `AUTO NVHN STATUS: username=.. nv=.. level=.. map=.. nvhn=a/b ..` -> số liệu nhân vật (30 giây/lần)
  - `đã xử lý hang động` / `< 30, bỏ qua` / `TaskOrder .. hoàn thành=a/b` / KILL/LOST/DEATH
"""

from __future__ import annotations

import re
from pathlib import Path
from typing import Any, Iterable

from .logparse import SESSION_RE, TS_RE, parse_account, parse_nvhn_status, read_tail_lines

ROSTER_RE = re.compile(r"^AUTO ROSTER: chars=(.*)$")
SELECT_RE = re.compile(r"^AUTO (?:NVHN|TA THU): chọn nhân vật (\S+)")
READY_RE = re.compile(r"game ready char=(\S+) level=(\d+)(?: map=(\d+))?")
TASK_DONE_RE = re.compile(r"TaskOrder \d+, hoàn thành=(\d+)/(\d+)")
SESSION_DATE_RE = re.compile(r"(\d{4}-\d{2}-\d{2})")
# Bot chuyển sang nhân vật kế tiếp: lý do in ngay trước khi đổi.
SKIP_RE = re.compile(r"bỏ qua nhân vật|, bỏ qua$|không còn trong danh sách server|, chuyển nhân vật(?:\.|$)")
CAVE_DONE = "đã xử lý hang động"
MONEY_RE = re.compile(r"\b(yen|xu|luong)=(-?\d+)")
NVHN_FINISHED = "nhân vật đã hết nhiệm vụ"
TA_THU_RESULT = {"TA THU KILL": "kills", "TA THU LOST": "lost", "TA THU DEATH": "deaths", "TA THU PK": "deaths"}

STATUS_FIELDS = ("level", "map", "state", "yen", "xu", "luong", "hp", "hp_max",
                 "nvhn", "nvhn_max", "progress", "progress_max", "bag", "bag_max")


def account_names(csv_path: Path) -> list[str]:
    """Username trong account.csv của worker (không bao giờ trả password)."""
    try:
        text = csv_path.read_text(encoding="utf-8-sig", errors="replace")
    except OSError:
        return []
    names = [row.split(",", 1)[0].strip() for row in text.splitlines() if row.strip() and "," in row]
    if names and names[0].lower() in {"username", "account"}:
        names = names[1:]
    return [name for name in names if name]


def _yen(value: str | None) -> str | None:
    # "6219346(+466)" -> "6219346"
    return value.split("(", 1)[0] if value else value


def _new_char(name: str) -> dict[str, Any]:
    return {
        "name": name, "level": None, "phai": "", "state": "pending", "note": "",
        "map": None, "nvhn": None, "nvhn_max": None, "progress": None, "progress_max": None,
        "hp": None, "hp_max": None, "bag": None, "bag_max": None, "yen": None, "xu": None, "luong": None, "activity": None,
        "task_done": None, "task_total": None, "kills": 0, "lost": 0, "deaths": 0,
        "at": None, "from_roster": False,
    }


def parse_worker_log(lines: Iterable[str]) -> dict[str, Any]:
    """Duyệt log theo thứ tự thời gian -> {accounts: {user: {...}}, current_account, current_char}."""
    accounts: dict[str, dict[str, Any]] = {}
    order: list[str] = []
    cur_acc: str | None = None
    cur_char: str | None = None
    day: str | None = None
    has_roster = False

    def account(user: str) -> dict[str, Any]:
        if user not in accounts:
            accounts[user] = {"account": user, "index": None, "total": None, "chars": {}, "at": None}
            order.append(user)
        return accounts[user]

    def char(user: str, name: str) -> dict[str, Any]:
        chars = account(user)["chars"]
        if name not in chars:
            chars[name] = _new_char(name)
        return chars[name]

    def finish_current(state: str = "done", note: str = "") -> None:
        if cur_acc and cur_char:
            c = char(cur_acc, cur_char)
            if c["state"] == "playing":
                c["state"] = state
                if note:
                    c["note"] = note

    for raw in lines:
        raw = raw.rstrip("\r\n")
        session = SESSION_RE.match(raw)
        if session:
            found = SESSION_DATE_RE.search(session.group(1))
            day = found.group(1) if found else day
            # Phiên chạy mới (restart worker): nhân vật đang chơi dở không còn chạy nữa.
            finish_current("stopped")
            continue
        ts_match = TS_RE.match(raw)
        ts = ts_match.group(1) if ts_match else None
        msg = raw[ts_match.end():] if ts_match else raw
        if not msg.startswith("AUTO "):
            continue
        stamp = f"{day} {ts}" if day and ts else ts

        acc_info = parse_account(msg) if "đăng nhập" in msg else None
        if acc_info and acc_info.get("username"):
            finish_current("stopped")
            cur_acc, cur_char = acc_info["username"], None
            a = account(cur_acc)
            a["index"], a["total"], a["at"] = acc_info["index"], acc_info["total"], stamp
            continue

        roster = ROSTER_RE.match(msg)
        if roster:
            has_roster = True
            if not cur_acc:
                continue
            for item in roster.group(1).split("|"):
                parts = item.split(":", 2)
                if not parts[0] or parts[0] == "null":
                    continue
                c = char(cur_acc, parts[0])
                c["from_roster"] = True
                if len(parts) > 1 and parts[1].lstrip("-").isdigit() and int(parts[1]) > 0:
                    c["level"] = int(parts[1])
                if len(parts) > 2:
                    c["phai"] = parts[2]
                c["at"] = c["at"] or stamp
            continue

        select = SELECT_RE.match(msg)
        if select:
            if cur_char and cur_char != select.group(1):
                finish_current("done")
            cur_char = select.group(1)
            if cur_acc:
                c = char(cur_acc, cur_char)
                c["state"], c["note"], c["at"] = "playing", "", stamp
            continue

        if msg.startswith("AUTO NVHN STATUS:"):
            data = parse_nvhn_status(msg)
            user = data.get("username") or cur_acc
            name = data.get("char") or cur_char
            if not user or not name:
                continue
            if user != cur_acc:
                cur_acc = user
            cur_char = name
            c = char(user, name)
            for key in STATUS_FIELDS:
                if data.get(key) is not None:
                    c["activity" if key == "state" else key] = data[key]
            c["yen"] = _yen(c["yen"])
            c["state"], c["at"] = "playing", stamp
            continue

        if not cur_acc or not cur_char:
            continue
        c = char(cur_acc, cur_char)
        if c["state"] == "playing":
            c["at"] = stamp
        ready = READY_RE.search(msg)
        if ready and ready.group(1) == cur_char:
            c["level"] = int(ready.group(2))
            if ready.group(3):
                c["map"] = c["map"] or ready.group(3)
            c["at"] = stamp
            continue
        if msg.startswith("AUTO TA THU FIGHT:"):
            money = dict(MONEY_RE.findall(msg))
            for key in ("yen", "xu", "luong"):
                if key in money:
                    c[key] = money[key]
            continue
        tag = next((t for t in TA_THU_RESULT if msg.startswith(f"AUTO {t}")), None)
        if tag:
            c[TA_THU_RESULT[tag]] += 1
            continue
        task = TASK_DONE_RE.search(msg)
        if task:
            c["task_done"], c["task_total"] = int(task.group(1)), int(task.group(2))
            continue
        if NVHN_FINISHED in msg:
            c["note"] = "hết NVHN, đi hang"
            continue
        if CAVE_DONE in msg:
            finish_current("done", "đã đi hang")
            continue
        body = msg.split(":", 1)[1].strip() if ":" in msg else msg
        if msg.startswith(("AUTO NVHN:", "AUTO TA THU:")) and SKIP_RE.search(body):
            finish_current("skipped", body)

    return {"accounts": accounts, "order": order, "current_account": cur_acc,
            "current_char": cur_char, "has_roster": has_roster}


def worker_detail(worker: dict[str, Any] | None, worker_dir: Path, stdout_log: Path) -> dict[str, Any]:
    """Ghép log + account.csv + trạng thái worker (từ /api/status) thành dữ liệu cho tab Thông tin."""
    raw, _ = read_tail_lines(stdout_log, 200_000, max_bytes=4 * 1024 * 1024)
    parsed = parse_worker_log(raw)
    names = account_names(worker_dir / "account.csv")
    state = (worker or {}).get("state")
    live = state == "RUNNING"
    done_all = state == "DONE"
    cur_acc = parsed["current_account"]
    cur_idx = names.index(cur_acc) if cur_acc in names else None

    order = list(dict.fromkeys(names + parsed["order"]))
    accounts = []
    for i, user in enumerate(order):
        info = parsed["accounts"].get(user)
        chars = list((info or {}).get("chars", {}).values())
        for c in chars:
            if c["state"] == "playing" and not (live and user == cur_acc and c["name"] == parsed["current_char"]):
                c["state"] = "stopped"
            if done_all and c["state"] in ("playing", "stopped"):
                c["state"] = "done"
        if done_all or (cur_idx is not None and user in names and names.index(user) < cur_idx):
            acc_state = "done"
        elif user == cur_acc:
            acc_state = "current" if live else "paused"
        elif user in names:
            acc_state = "pending"
        else:
            acc_state = "removed"
        accounts.append({
            "index": names.index(user) + 1 if user in names else None,
            "account": user,
            "state": acc_state,
            "at": (info or {}).get("at"),
            "characters": chars,
        })
    return {
        "worker": worker_dir.name,
        "state": state,
        "current_account": cur_acc,
        "current_char": parsed["current_char"] if live else None,
        "has_roster": parsed["has_roster"],
        "log_exists": stdout_log.is_file(),
        "accounts": accounts,
    }
