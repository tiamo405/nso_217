"""
Worker Main Entry Point
Runs a dedicated batch of 4-5 accounts for a specific worker process.
"""
import os
import sys
import csv
import time
import signal
import argparse
import logging
from typing import List, Tuple

# Add parent directory to sys.path
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from bot.account_runner import AccountRunner
from config import config

try:
    sys.stdout.reconfigure(line_buffering=True)
    sys.stderr.reconfigure(line_buffering=True)
except Exception:
    pass

# Setup logging with clean format
logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s %(levelname)s %(message)s',
    datefmt='%H:%M:%S',
    stream=sys.stdout
)
logger = logging.getLogger("WorkerMain")

is_terminated = False


def signal_handler(signum, frame):
    global is_terminated
    logger.info(f"AUTO NVHN: nhận tín hiệu dừng {signum}, chuẩn bị dừng worker...")
    is_terminated = True


def excepthook(exc_type, exc_value, exc_traceback):
    logger.error("AUTO NVHN: Unhandled exception:", exc_info=(exc_type, exc_value, exc_traceback))

sys.excepthook = excepthook

signal.signal(signal.SIGTERM, signal_handler)
signal.signal(signal.SIGINT, signal_handler)


def parse_accounts(csv_file: str) -> List[Tuple[str, str]]:
    accounts = []
    if not os.path.exists(csv_file):
        logger.error(f"AUTO NVHN: không tìm thấy file account.csv: {csv_file}")
        return accounts

    with open(csv_file, mode='r', encoding='utf-8') as f:
        reader = csv.reader(f)
        for row in reader:
            if not row:
                continue
            if row[0].strip().lower() in ('username', 'user', 'acc', 'tai_khoan'):
                continue
            if len(row) >= 2:
                u = row[0].strip()
                p = row[1].strip()
                if u and p:
                    accounts.append((u, p))
    return accounts


def main():
    parser = argparse.ArgumentParser(description="NSO Python Headless Bot Worker")
    parser.add_argument("--worker-dir", type=str, default=".", help="Working directory for this worker instance")
    parser.add_argument("--csv", type=str, default="", help="Path to accounts.csv file")
    parser.add_argument("--host", type=str, default=config.DEFAULT_HOST, help="NSO Server Host")
    parser.add_argument("--port", type=int, default=config.DEFAULT_PORT, help="NSO Server Port")
    parser.add_argument("--account-delay", type=float, default=2.0, help="Delay in seconds between accounts")

    args = parser.parse_args()

    worker_dir = os.path.abspath(args.worker_dir)
    os.makedirs(worker_dir, exist_ok=True)

    # PID tracking
    pid_file = os.path.join(worker_dir, "bot.pid")
    with open(pid_file, "w") as f:
        f.write(str(os.getpid()))

    # Determine CSV path
    csv_path = args.csv
    if not csv_path:
        csv_path = os.path.join(worker_dir, "accounts.csv")
        if not os.path.exists(csv_path):
            csv_path = os.path.join(config.BASE_DIR, "accounts.csv")

    accounts = parse_accounts(csv_path)
    logger.info(f"AUTO NVHN: khởi động worker (PID {os.getpid()}), nạp {len(accounts)} tài khoản")

    completed_count = 0

    try:
        for idx, (username, password) in enumerate(accounts):
            if is_terminated:
                logger.info("AUTO NVHN: dừng xử lý do nhận yêu cầu ngắt.")
                break

            runner = AccountRunner(username, password, host=args.host, port=args.port,
                                   account_index=idx, total_accounts=len(accounts))
            try:
                success = runner.run()
                if success:
                    completed_count += 1
            except Exception as e:
                logger.error(f"AUTO NVHN: lỗi xử lý tài khoản {username}: {e}", exc_info=True)

            if idx < len(accounts) - 1 and not is_terminated:
                time.sleep(args.account_delay)

        # Write completion marker
        if not is_terminated and completed_count == len(accounts) and len(accounts) > 0:
            marker_file = os.path.join(worker_dir, "completed.marker")
            with open(marker_file, "w") as f:
                f.write(f"Completed {completed_count}/{len(accounts)} accounts at {time.ctime()}\n")
            logger.info(f"AUTO NVHN: đã xử lý hết toàn bộ tài khoản và nhân vật, dừng worker.")

    finally:
        if os.path.exists(pid_file):
            try:
                os.remove(pid_file)
            except Exception:
                pass
        logger.info("AUTO NVHN: tiến trình worker kết thúc.")


if __name__ == "__main__":
    main()

