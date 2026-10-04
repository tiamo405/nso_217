#!/usr/bin/env python3
"""Check item permanent IDs in every character inventory."""

import argparse
import importlib.util
import logging
import sys
import threading
import time
from concurrent.futures import ThreadPoolExecutor, as_completed
from dataclasses import dataclass, field
from pathlib import Path
from typing import Optional, Sequence


ROOT_DIR = Path(__file__).resolve().parent
DEFAULT_CSV = ROOT_DIR / "account-bodo.csv"
DEFAULT_ITEMS_FILE = ROOT_DIR / "item_vinh_vien.txt"
DEFAULT_LOG_DIR = ROOT_DIR / "log" / "check_item_vinh_vien"
DEFAULT_HOST = "Nsm4.ninjasm.net"
DEFAULT_PORT = 14444


def _load_inventory_module():
    source = ROOT_DIR / "del-item-hanhtrang.py"
    spec = importlib.util.spec_from_file_location("check_item_inventory", source)
    if spec is None or spec.loader is None:
        raise ImportError(f"Khong the nap parser hanh trang: {source}")
    module = importlib.util.module_from_spec(spec)
    sys.modules[spec.name] = module
    spec.loader.exec_module(module)
    return module


_INVENTORY_MODULE = _load_inventory_module()
_INVENTORY_TEMPLATE_LOCK = threading.RLock()
_ORIGINAL_PARSE_ITEM_TEMPLATES = _INVENTORY_MODULE.NSOClient._parse_item_templates
_ORIGINAL_PARSE_BAG = _INVENTORY_MODULE.NSOClient._parse_character_info_and_bag


def _threadsafe_parse_item_templates(client, data):
    with _INVENTORY_TEMPLATE_LOCK:
        return _ORIGINAL_PARSE_ITEM_TEMPLATES(client, data)


def _threadsafe_parse_bag(client, reader):
    with _INVENTORY_TEMPLATE_LOCK:
        return _ORIGINAL_PARSE_BAG(client, reader)


_INVENTORY_MODULE.NSOClient._parse_item_templates = _threadsafe_parse_item_templates
_INVENTORY_MODULE.NSOClient._parse_character_info_and_bag = _threadsafe_parse_bag


_PRINT_LOCK = threading.Lock()
_LOG_HANDLE = None
_THREAD_LOG = threading.local()


def configure_log(path: Path):
    global _LOG_HANDLE
    if _LOG_HANDLE is not None:
        _LOG_HANDLE.close()
    _LOG_HANDLE = path.open("w", encoding="utf-8", buffering=1)


def close_log():
    global _LOG_HANDLE
    if _LOG_HANDLE is not None:
        _LOG_HANDLE.close()
        _LOG_HANDLE = None


def configure_lane_log(path: Path):
    _THREAD_LOG.handle = path.open("w", encoding="utf-8", buffering=1)


def close_lane_log():
    handle = getattr(_THREAD_LOG, "handle", None)
    if handle is not None:
        handle.close()
        _THREAD_LOG.handle = None


def clear_lane_logs(path: Path):
    for target in path.glob("luong*.log"):
        if target.is_file() or target.is_symlink():
            target.unlink()


def log(*args, **kwargs):
    kwargs.setdefault("flush", True)
    with _PRINT_LOCK:
        print(*args, **kwargs)
        handle = getattr(_THREAD_LOG, "handle", None) or _LOG_HANDLE
        if handle is not None:
            file_kwargs = dict(kwargs)
            file_kwargs["file"] = handle
            print(*args, **file_kwargs)


class _InventoryLogHandler(logging.Handler):
    def emit(self, record):
        log(f"{record.levelname}: {record.getMessage()}")


_INVENTORY_MODULE.logger.handlers.clear()
_INVENTORY_MODULE.logger.propagate = False
_INVENTORY_MODULE.logger.addHandler(_InventoryLogHandler())


@dataclass
class FoundItem:
    username: str
    character_name: str
    slot: int
    template_id: int
    name: str
    quantity: int
    is_lock: bool


@dataclass
class CheckResult:
    account_ok: bool = False
    characters: int = 0
    matched: int = 0
    permanent: int = 0
    failed: int = 0
    items: list[FoundItem] = field(default_factory=list)


def check_character(client, username: str, character_name: str, item_ids) -> CheckResult:
    result = CheckResult(account_ok=True, characters=1)
    targets = [
        item for item in client.bag
        if item is not None
        and item.template_id in item_ids
        and not item.is_expires
    ]
    result.matched = len(targets)
    result.permanent = len(targets)
    result.items.extend(
        FoundItem(
            username=username,
            character_name=character_name,
            slot=item.index,
            template_id=item.template_id,
            name=item.name,
            quantity=item.quantity,
            is_lock=item.is_lock,
        )
        for item in targets
    )

    log(
        f"  Hanh trang {username}/{character_name}: "
        f"{len(client.bag)} slot, khop={result.matched}, "
        f"vinh vien={result.permanent}"
    )
    for item in targets:
        log(
            f"    [VINH VIEN] slot={item.index:02d} | id={item.template_id} | "
            f"{item.name} | so luong={item.quantity} | khoa={item.is_lock} | "
            f"isExpires={item.is_expires}"
        )
    if not targets:
        log("    Khong tim thay item ID trong danh sach")
    return result


def process_account(username: str, password: str, args, item_ids) -> CheckResult:
    client = _INVENTORY_MODULE.NSOClient(
        args.host, args.port, timeout=args.socket_timeout
    )
    total = CheckResult()
    try:
        log(f"Dang nhap tai khoan: {username}")
        if not client.login(username, password, login_timeout=args.login_timeout):
            log(f"LOI: Khong dang nhap duoc tai khoan {username}")
            total.failed += 1
            return total

        char_names = list(client.characters)
        if args.max_characters > 0:
            char_names = char_names[:args.max_characters]
        if not char_names:
            log(f"LOI: Tai khoan {username} khong co nhan vat")
            total.failed += 1
            return total
        log(f"Tai khoan {username}: {len(char_names)} nhan vat")

        for index, character_name in enumerate(char_names):
            if index > 0:
                client.disconnect()
                if args.character_delay > 0:
                    time.sleep(args.character_delay)
                client = _INVENTORY_MODULE.NSOClient(
                    args.host, args.port, timeout=args.socket_timeout
                )
                if not client.login(
                    username, password, login_timeout=args.login_timeout
                ):
                    log(f"LOI: Dang nhap lai that bai cho {character_name}")
                    total.failed += 1
                    continue

            log(
                f"Vao nhan vat: {character_name} "
                f"({index + 1}/{len(char_names)})"
            )
            if not client.select_character(
                character_name, timeout=args.game_timeout
            ):
                log(f"LOI: Khong vao duoc nhan vat {character_name}")
                total.failed += 1
                continue

            if args.ready_delay > 0:
                time.sleep(args.ready_delay)
            char_result = check_character(
                client, username, character_name, item_ids
            )
            total.account_ok = True
            total.characters += char_result.characters
            total.matched += char_result.matched
            total.permanent += char_result.permanent
            total.items.extend(char_result.items)
    except Exception as exc:
        log(f"LOI: Xu ly tai khoan {username}: {exc}")
        total.failed += 1
    finally:
        client.disconnect()
    return total


def run_account_lane(args, lane_accounts, total_accounts, lane_number, log_dir):
    results = []
    configure_lane_log(log_dir / f"luong{lane_number}.log")
    try:
        for position, (account_number, username, password) in enumerate(lane_accounts):
            log(f"\n[{account_number}/{total_accounts}] Tai khoan {username}")
            try:
                result = process_account(username, password, args, args.item_ids)
            except Exception as exc:
                log(f"LOI: Worker tai khoan {username}: {exc}")
                result = CheckResult(failed=1)
            results.append(result)
            if position + 1 < len(lane_accounts) and args.account_delay > 0:
                time.sleep(args.account_delay)
        return results
    finally:
        close_lane_log()


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description="Kiem tra item vinh vien trong hanh trang"
    )
    parser.add_argument(
        "csv_file", nargs="?", type=Path, default=DEFAULT_CSV,
        help=f"CSV tai khoan (mac dinh: {DEFAULT_CSV.name})",
    )
    parser.add_argument(
        "--max-accounts", type=int, default=0,
        help="Chi xu ly N tai khoan dau tien; 0 la tat ca",
    )
    parser.add_argument(
        "--max-characters", type=int, default=1,
        help="Gioi han so nhan vat moi tai khoan; 0 la tat ca",
    )
    parser.add_argument(
        "--luong", type=int, default=1,
        help="So luong tai khoan xu ly song song",
    )
    parser.add_argument("--host", default=DEFAULT_HOST)
    parser.add_argument("--port", type=int, default=DEFAULT_PORT)
    parser.add_argument("--socket-timeout", type=float, default=12.0)
    parser.add_argument("--login-timeout", type=float, default=15.0)
    parser.add_argument("--game-timeout", type=float, default=15.0)
    parser.add_argument("--ready-delay", type=float, default=1.0)
    parser.add_argument("--character-delay", type=float, default=3.0)
    parser.add_argument("--account-delay", type=float, default=3.0)
    parser.add_argument("--log-dir", type=Path, default=DEFAULT_LOG_DIR)
    parser.add_argument("--log-file", type=Path)
    return parser


def add_result(total: CheckResult, result: CheckResult):
    total.account_ok = total.account_ok or result.account_ok
    total.characters += result.characters
    total.matched += result.matched
    total.permanent += result.permanent
    total.failed += result.failed
    total.items.extend(result.items)


def main(argv: Optional[Sequence[str]] = None) -> int:
    args = build_parser().parse_args(argv)
    log_dir = args.log_dir
    log_file = args.log_file or log_dir / "main.log"

    if not args.csv_file.is_file():
        print(f"Khong tim thay CSV: {args.csv_file}")
        return 2
    if not DEFAULT_ITEMS_FILE.is_file():
        print(f"Khong tim thay file item: {DEFAULT_ITEMS_FILE}")
        return 2
    if args.max_accounts < 0 or args.max_characters < 0:
        print("--max-accounts va --max-characters khong duoc am")
        return 2
    if args.luong < 1:
        print("--luong phai lon hon 0")
        return 2

    try:
        accounts = _INVENTORY_MODULE.read_accounts(args.csv_file)
        item_ids = _INVENTORY_MODULE.read_item_ids(DEFAULT_ITEMS_FILE)
    except Exception as exc:
        print(f"Khong doc duoc du lieu dau vao: {exc}")
        return 2
    if args.max_accounts:
        accounts = accounts[:args.max_accounts]
    if not accounts:
        print("CSV khong co tai khoan hop le")
        return 2
    if not item_ids:
        print(f"{DEFAULT_ITEMS_FILE.name} khong co item ID hop le")
        return 2

    log_dir.mkdir(parents=True, exist_ok=True)
    log_file.parent.mkdir(parents=True, exist_ok=True)
    clear_lane_logs(log_dir)
    configure_log(log_file)
    try:
        log(
            f"CHECK ITEM VINH VIEN: tai khoan={len(accounts)} | "
            f"item ID={len(item_ids)} | luong={min(args.luong, len(accounts))} | "
            f"max-characters={args.max_characters} | log-dir={log_dir}"
        )
        effective_workers = min(args.luong, len(accounts))
        lanes = [[] for _ in range(effective_workers)]
        for account_number, account in enumerate(accounts, start=1):
            lanes[(account_number - 1) % effective_workers].append(
                (account_number, account[0], account[1])
            )

        args.item_ids = item_ids
        total = CheckResult()
        accounts_done = 0
        with ThreadPoolExecutor(
            max_workers=effective_workers,
            thread_name_prefix="check-item-vinh-vien",
        ) as executor:
            jobs = {
                executor.submit(
                    run_account_lane,
                    args,
                    lane,
                    len(accounts),
                    lane_number,
                    log_dir,
                ): lane
                for lane_number, lane in enumerate(lanes, start=1)
            }
            for future in as_completed(jobs):
                try:
                    results = future.result()
                except Exception as exc:
                    log(f"LOI: Worker: {exc}")
                    total.failed += len(jobs[future])
                    continue
                for result in results:
                    add_result(total, result)
                    accounts_done += int(result.account_ok)

        log(
            f"HOAN TAT: tai khoan co ket qua={accounts_done}/{len(accounts)} | "
            f"nhan vat={total.characters} | khop ID={total.matched} | "
            f"vinh vien={total.permanent} | "
            f"that bai={total.failed}"
        )
        log("CHI TIET ITEM VINH VIEN:")
        if total.items:
            for item in sorted(
                total.items,
                key=lambda value: (
                    value.username,
                    value.character_name,
                    value.slot,
                ),
            ):
                log(
                    f"  tai khoan={item.username} | nhan vat={item.character_name} | "
                    f"item={item.name} | id={item.template_id} | "
                    f"so luong={item.quantity} | slot={item.slot:02d} | "
                    f"khoa={item.is_lock}"
                )
        else:
            log("  Khong co item vinh vien nao trong hanh trang")
        log(f"MAIN LOG: {log_file}")
        return 1 if total.failed else 0
    finally:
        close_log()


if __name__ == "__main__":
    raise SystemExit(main())
