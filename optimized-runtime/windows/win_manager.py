"""NSO Ultra-Optimized Runtime Manager for Windows.

Cross-platform runner designed for Windows Server 2012 R2 / Windows 10/11
using Python 3.8+ and Java 11+.
"""

from __future__ import annotations

import argparse
import csv
import ctypes
import os
import re
import shutil
import subprocess
import sys
import time
from datetime import datetime
from pathlib import Path
from typing import Any, Dict, List, Optional, Set, Tuple

# Thiết lập mã hóa stdout/stderr sang UTF-8 để tránh lỗi UnicodeEncodeError trên Windows (cp1252/cp936)
if hasattr(sys.stdout, "reconfigure"):
    try:
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    except Exception:
        pass
else:
    try:
        import codecs
        if hasattr(sys.stdout, "buffer"):
            sys.stdout = codecs.getwriter("utf-8")(sys.stdout.buffer, "replace")
    except Exception:
        pass

if hasattr(sys.stderr, "reconfigure"):
    try:
        sys.stderr.reconfigure(encoding="utf-8", errors="replace")
    except Exception:
        pass
else:
    try:
        import codecs
        if hasattr(sys.stderr, "buffer"):
            sys.stderr = codecs.getwriter("utf-8")(sys.stderr.buffer, "replace")
    except Exception:
        pass

# ========================================================
# TỰ ĐỘNG TÌM ĐƯỜNG DẪN DỰ ÁN (AUTO-DISCOVER DIRECTORIES)
# ========================================================
def _resolve_project_dirs(start_file: Path) -> Tuple[Path, Path]:
    """Tự động tìm REPO_DIR và RUNTIME_DIR bất kể script nằm ở đâu."""
    curr = start_file.resolve()
    chain = [curr] + list(curr.parents)

    repo_dir: Optional[Path] = None
    runtime_dir: Optional[Path] = None

    # 1. Tìm RUNTIME_DIR (nơi có src/OptimizedMain.java và overrides)
    for p in chain:
        if (p / "src" / "OptimizedMain.java").is_file() and (p / "overrides").is_dir():
            runtime_dir = p
            break
        elif (p / "optimized-runtime" / "src" / "OptimizedMain.java").is_file():
            runtime_dir = p / "optimized-runtime"
            break

    # 2. Tìm REPO_DIR (gốc dự án, nơi có src/map hoặc account.csv hoặc optimized-runtime)
    for p in chain:
        if (p / "optimized-runtime").is_dir() and (p / "src").is_dir():
            repo_dir = p
            break
        if (p / "src" / "map").is_dir() and (p / "account.csv").is_file():
            repo_dir = p
            break

    if repo_dir and not runtime_dir:
        runtime_dir = repo_dir / "optimized-runtime"
    if runtime_dir and not repo_dir:
        repo_dir = runtime_dir.parent

    if not repo_dir or not runtime_dir:
        # Fallback về đường dẫn tương đối
        curr_dir = start_file.resolve().parent
        return curr_dir.parent.parent, curr_dir.parent

    return repo_dir.resolve(), runtime_dir.resolve()


SCRIPT_DIR = Path(__file__).resolve().parent
REPO_DIR, RUNTIME_DIR = _resolve_project_dirs(Path(__file__))

BUILD_DIR = RUNTIME_DIR / "build"
CLASSES_DIR = BUILD_DIR / "classes"
WORKERS_DIR = RUNTIME_DIR / "workers"
ACCOUNT_CSV = REPO_DIR / "account.csv"
DELL_TXT = REPO_DIR / "delllllllllll.txt"

# JVM Tinh chỉnh
JAVA_XMS = os.environ.get("JAVA_XMS", "8m")
JAVA_XMX = os.environ.get("JAVA_XMX", "36m")
NSO_TICK_MS = os.environ.get("NSO_TICK_MS", "100")
START_DELAY = int(os.environ.get("START_DELAY", "3"))
SERVER_NAME = os.environ.get("NSO_SERVER", "tk")
try:
    STALE_LOG_SECONDS = max(0, int(os.environ.get("STALE_LOG_SECONDS", "300")))
except ValueError:
    STALE_LOG_SECONDS = 300

RE_CHAR_STATUS = re.compile(r"AUTO NVHN STATUS:.*?nv=([a-zA-Z0-9_]+)")
RE_CHAR_CHOOSE = re.compile(r"AUTO NVHN: (?:chọn|chuẩn bị) nhân vật ([a-zA-Z0-9_]+)")


def get_java_bin(name: str = "java") -> str:
    """Tìm binary java/javac từ PATH hoặc JAVA_HOME."""
    exe_name = f"{name}.exe" if os.name == "nt" else name
    # Kiểm tra trong PATH
    which_bin = shutil.which(name) or shutil.which(exe_name)
    if which_bin:
        return which_bin

    java_home = os.environ.get("JAVA_HOME")
    if java_home:
        candidate = Path(java_home) / "bin" / exe_name
        if candidate.is_file():
            return str(candidate)

    return exe_name


def is_pid_running(pid: int) -> bool:
    """Kiểm tra tiến trình còn sống hay không trên Windows/Linux."""
    if pid <= 0:
        return False

    # Thử dùng psutil nếu có
    try:
        import psutil  # type: ignore

        if psutil.pid_exists(pid):
            p = psutil.Process(pid)
            return p.is_running() and p.status() != psutil.STATUS_ZOMBIE
        return False
    except ImportError:
        pass

    # Windows native check qua Win32 API
    if os.name == "nt":
        PROCESS_QUERY_LIMITED_INFORMATION = 0x1000
        SYNCHRONIZE = 0x00100000
        kernel32 = ctypes.windll.kernel32
        handle = kernel32.OpenProcess(PROCESS_QUERY_LIMITED_INFORMATION | SYNCHRONIZE, False, pid)
        if handle:
            exit_code = ctypes.c_ulong()
            kernel32.GetExitCodeProcess(handle, ctypes.byref(exit_code))
            kernel32.CloseHandle(handle)
            return exit_code.value == 259  # STILL_ACTIVE
        return False

    # Linux / POSIX fallback
    if os.name != "nt":
        try:
            stat_text = Path(f"/proc/{pid}/stat").read_text(encoding="ascii")
            comm_end = stat_text.rfind(")")
            if comm_end > 0 and stat_text[comm_end + 2 : comm_end + 3] == "Z":
                return False
        except OSError:
            pass

    try:
        os.kill(pid, 0)
        return True
    except (OSError, ValueError):
        return False


def process_cmdline(pid: int) -> Optional[List[str]]:
    """Return a process command line when the platform lets us inspect it."""
    if pid <= 0:
        return []

    try:
        import psutil  # type: ignore

        return psutil.Process(pid).cmdline()
    except ImportError:
        pass
    except Exception:
        return []

    if os.name != "nt":
        try:
            raw = Path(f"/proc/{pid}/cmdline").read_bytes()
            return [part.decode("utf-8", errors="replace") for part in raw.split(b"\0") if part]
        except OSError:
            return []
    return None


def process_ids() -> List[int]:
    """List inspectable process IDs without probing every possible PID."""
    try:
        import psutil  # type: ignore

        return [int(pid) for pid in psutil.pids()]
    except ImportError:
        pass
    except Exception:
        return []

    if os.name != "nt":
        return [int(path.name) for path in Path("/proc").glob("[0-9]*") if path.name.isdigit()]
    return []


def is_expected_worker_process(pid: int, worker_dir: Path) -> bool:
    """Avoid treating a reused/stale bot.pid as this worker."""
    if not is_pid_running(pid):
        return False
    args = process_cmdline(pid)
    if args is None:
        # Keep compatibility with Windows installations without psutil. The
        # documented setup recommends psutil, which enables this ownership
        # check and protects against PID reuse.
        return True
    if not args:
        return False
    command = " ".join(args).replace("\\", "/").casefold()
    expected_dir = str(worker_dir).replace("\\", "/").casefold()
    return "optimizedmain" in command and expected_dir in command


def is_expected_supervisor_process(pid: int) -> bool:
    """Identify this runtime's supervisor, including orphaned instances."""
    if not is_pid_running(pid):
        return False
    args = process_cmdline(pid)
    if args is None or not args:
        return False
    command = " ".join(args).replace("\\", "/").casefold()
    manager_path = str(Path(__file__).resolve()).replace("\\", "/").casefold()
    return manager_path in command and " supervise" in f" {command}"


def kill_pid(pid: int) -> bool:
    """Dừng tiến trình bằng PID."""
    if not is_pid_running(pid):
        return False

    if os.name == "nt":
        res = subprocess.run(
            ["taskkill", "/F", "/T", "/PID", str(pid)],
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
        )
        return res.returncode == 0
    else:
        try:
            os.kill(pid, 9)
            return True
        except OSError:
            return False


def log_age_seconds(log_file: Path) -> Optional[int]:
    """Returns the age of a worker log, or None when it cannot be read."""
    try:
        return max(0, int(time.time() - log_file.stat().st_mtime))
    except OSError:
        return None


def stale_log_reason(log_file: Path) -> Optional[str]:
    """Returns a human-readable stale-log reason when the watchdog should restart."""
    if STALE_LOG_SECONDS <= 0:
        return None
    if not log_file.is_file():
        return "chưa có stdout.log"

    age = log_age_seconds(log_file)
    if age is None:
        return None
    if age >= STALE_LOG_SECONDS:
        return f"stdout.log không đổi {age}s (ngưỡng {STALE_LOG_SECONDS}s)"
    return None


def get_process_stats(pid: int) -> Tuple[Optional[float], Optional[float]]:
    """Lấy CPU% và RAM (MB) của tiến trình nếu có psutil."""
    try:
        import psutil  # type: ignore

        proc = psutil.Process(pid)
        cpu = proc.cpu_percent(interval=None)
        rss_mb = round(proc.memory_info().rss / (1024 * 1024), 1)
        return cpu, rss_mb
    except Exception:
        return None, None


# ==========================================
# 1. BUILD OPTIMIZED RUNTIME
# ==========================================
def _safe_print(msg: str, file=None) -> None:
    """Print an toàn, encode lỗi thành '?' thay vì crash."""
    try:
        if file is None:
            print(msg, flush=True)
        else:
            print(msg, file=file, flush=True)
    except (UnicodeEncodeError, UnicodeDecodeError):
        safe = msg.encode("ascii", errors="replace").decode("ascii")
        if file is None:
            print(safe, flush=True)
        else:
            print(safe, file=file, flush=True)


def cmd_build(args: Any = None) -> int:
    try:
        return _cmd_build_inner(args)
    except Exception as exc:
        import traceback as _tb
        _tb.print_exc()
        sys.stderr.write(f"[!] Exception: {exc}\n")
        return 1


def _cmd_build_inner(args: Any = None) -> int:
    _safe_print("==========================================================")
    _safe_print("[1/4] Chuan bi ma nguon va ap dung Overrides...")
    _safe_print("==========================================================")

    work_src = BUILD_DIR / "src-repo"
    classes_dir = BUILD_DIR / "classes"
    sources_file = BUILD_DIR / "sources.txt"

    BUILD_DIR.mkdir(parents=True, exist_ok=True)
    if work_src.exists():
        shutil.rmtree(work_src)
    work_src.mkdir(parents=True, exist_ok=True)

    # Copy src gốc, optimized src và overrides
    shutil.copytree(REPO_DIR / "src", work_src, dirs_exist_ok=True)
    shutil.copytree(RUNTIME_DIR / "src", work_src, dirs_exist_ok=True)
    shutil.copytree(RUNTIME_DIR / "overrides", work_src, dirs_exist_ok=True)

    # Patch các hàm getResourceAsStream an toàn
    patches = [
        (
            work_src / "TileMap.java",
            '"".getClass().getResourceAsStream("/map/" + var1)',
            'TileMap.class.getResourceAsStream("/map/" + var1)',
        ),
        (
            work_src / "TileMap.java",
            '"".getClass().getResourceAsStream("/map/" + mapID)',
            'TileMap.class.getResourceAsStream("/map/" + mapID)',
        ),
        (
            work_src / "RMS.java",
            '"".getClass().getResourceAsStream(var0)',
            "RMS.class.getResourceAsStream(var0)",
        ),
        (
            work_src / "Res.java",
            '"".getClass().getResourceAsStream(var0)',
            "Res.class.getResourceAsStream(var0)",
        ),
    ]

    for file_path, old_str, new_str in patches:
        if file_path.is_file():
            text = file_path.read_text(encoding="utf-8", errors="replace")
            if old_str in text:
                text = text.replace(old_str, new_str)
                file_path.write_text(text, encoding="utf-8")

    _safe_print("[2/4] Tao danh sach file Java (sources.txt)...")
    java_files = sorted(work_src.rglob("*.java"))
    with open(sources_file, "w", encoding="utf-8") as f:
        for jf in java_files:
            f.write(jf.as_posix() + "\n")

    _safe_print(f"      Total: {len(java_files)} Java files.")

    _safe_print("[3/4] Bien dich ma nguon voi javac...")
    javac_bin = get_java_bin("javac")
    classes_dir.mkdir(parents=True, exist_ok=True)

    compile_cmd = [
        javac_bin,
        "-encoding", "UTF-8",
        "-source", "8",
        "-target", "8",
        "-Xlint:none",
        "-d", str(classes_dir),
        f"@{sources_file.as_posix()}",
    ]

    res = subprocess.run(compile_cmd, stderr=subprocess.PIPE, text=True, encoding="utf-8", errors="replace")
    if res.returncode != 0:
        sys.stderr.write("\n[!] LOI BIEN DICH JAVAC!\n")
        if res.stderr:
            sys.stderr.write(res.stderr)
        return 1

    _safe_print("[4/4] Dong goi tai nguyen tinh (maps, account, configs)...")
    shutil.copytree(work_src, classes_dir, dirs_exist_ok=True)
    for jf in classes_dir.rglob("*.java"):
        jf.unlink()

    # Copy map từ 0 đến 159
    map_dest = classes_dir / "map"
    map_dest.mkdir(parents=True, exist_ok=True)
    src_map = REPO_DIR / "src" / "map"
    for map_id in range(160):
        target = map_dest / str(map_id)
        src_target = src_map / str(map_id)
        if src_target.is_file():
            shutil.copyfile(src_target, target)
        elif not target.is_file():
            target.touch()

    if ACCOUNT_CSV.is_file():
        shutil.copyfile(ACCOUNT_CSV, classes_dir / "account.csv")

    if DELL_TXT.is_file():
        shutil.copyfile(DELL_TXT, classes_dir / "delllllllllll.txt")

    main_class = classes_dir / "OptimizedMain.class"
    if not main_class.is_file():
        sys.stderr.write("[!] Khong tim thay OptimizedMain.class sau khi build!\n")
        sys.stderr.write(f"    REPO_DIR    = {REPO_DIR}\n")
        sys.stderr.write(f"    RUNTIME_DIR = {RUNTIME_DIR}\n")
        sys.stderr.write(f"    classes_dir = {classes_dir}\n")
        return 1

    _safe_print("==========================================================")
    _safe_print(" BUILD THANH CONG!")
    _safe_print(f" Classes dir: {classes_dir}")
    _safe_print(" Entry point: OptimizedMain")
    _safe_print("==========================================================")
    return 0


# ==========================================
# 2. BUILD WORKERS (CHIA TÀI KHOẢN)
# ==========================================
def cmd_build_workers(args: Any) -> int:
    try:
        return _cmd_build_workers_inner(args)
    except Exception as exc:
        import traceback as _tb
        _tb.print_exc()
        sys.stderr.write(f"[!] Exception: {exc}\n")
        return 1


def _cmd_build_workers_inner(args: Any) -> int:
    worker_count = args.count
    source_csv = Path(args.csv).resolve() if args.csv else ACCOUNT_CSV

    if worker_count < 1:
        sys.stderr.write("[!] So worker phai >= 1\n")
        return 1

    if not source_csv.is_file():
        sys.stderr.write(f"[!] Khong tim thay file account: {source_csv}\n")
        return 1

    if WORKERS_DIR.is_dir():
        for pid_file in WORKERS_DIR.glob("worker-*/bot.pid"):
            try:
                pid = int(pid_file.read_text(encoding="utf-8").strip())
                worker_dir = pid_file.parent
                if is_expected_worker_process(pid, worker_dir):
                    sys.stderr.write(f"[!] Worker PID {pid} dang chay. Hay stop truoc khi chia lai.\n")
                    return 1
            except (ValueError, OSError):
                pass

    if args.compile:
        _safe_print("Dang bien dich optimized classes...")
        if cmd_build() != 0:
            return 1

    lines = source_csv.read_text(encoding="utf-8-sig", errors="replace").splitlines()
    accounts = [line.strip() for line in lines[1:] if line.strip()]
    total = len(accounts)

    if total == 0:
        sys.stderr.write("[!] File account.csv khong co tai khoan hop le.\n")
        return 1

    if worker_count > total:
        sys.stderr.write(f"[!] Co {total} tai khoan nhung yeu cau {worker_count} worker.\n")
        return 1

    header = lines[0].strip()
    staging_dir = RUNTIME_DIR / ".workers_staging"
    if staging_dir.exists():
        shutil.rmtree(staging_dir, ignore_errors=True)
    staging_dir.mkdir(parents=True, exist_ok=True)

    base_size = total // worker_count
    extra = total % worker_count
    offset = 0

    _safe_print(f"Dang chia {total} tai khoan thanh {worker_count} workers...")
    for idx in range(1, worker_count + 1):
        worker_name = f"worker-{idx:02d}"
        worker_dir = staging_dir / worker_name
        home_dir = worker_dir / "home"
        home_dir.mkdir(parents=True, exist_ok=True)

        count = base_size + (1 if idx <= extra else 0)
        chunk = accounts[offset : offset + count]
        offset += count

        acc_file = worker_dir / "account.csv"
        with open(acc_file, "w", encoding="utf-8", newline="\n") as f:
            f.write(header + "\n")
            for acc in chunk:
                f.write(acc + "\n")

        _safe_print(f" - {worker_name}: {count} accounts")

    if WORKERS_DIR.exists():
        _safe_print("Dang don dep du lieu worker cu...")
        shutil.rmtree(WORKERS_DIR, ignore_errors=True)

    shutil.move(str(staging_dir), str(WORKERS_DIR))

    _safe_print(f"Hoan tat: Da tao moi {worker_count} worker tai {WORKERS_DIR}")
    return 0


# ==========================================
# 3. START WORKERS
# ==========================================
def cmd_start(args: Any) -> int:
    try:
        return _cmd_start_inner(args)
    except Exception as exc:
        import traceback as _tb
        _tb.print_exc()
        sys.stderr.write(f"[!] Exception: {exc}\n")
        return 1


def _cmd_start_inner(args: Any) -> int:
    if not CLASSES_DIR.is_dir() or not (CLASSES_DIR / "OptimizedMain.class").is_file():
        sys.stderr.write("[!] Chua co classes. Chay lenh build truoc.\n")
        return 1

    delay = args.delay if args.delay is not None else START_DELAY
    target_numbers = set()
    if args.workers:
        for w in args.workers:
            cleaned = str(w).replace("worker-", "")
            if cleaned.isdigit():
                target_numbers.add(int(cleaned))

    worker_dirs = sorted(WORKERS_DIR.glob("worker-*"))
    if not worker_dirs:
        sys.stderr.write("[!] Chua co worker nao. Hay chay build-workers truoc.\n")
        return 1

    started = 0
    running = 0
    completed = 0
    failed = 0

    java_bin = get_java_bin("java")

    for worker_dir in worker_dirs:
        if not worker_dir.is_dir():
            continue

        worker_name = worker_dir.name
        num = int(worker_name.replace("worker-", ""))
        if target_numbers and num not in target_numbers:
            continue

        if (worker_dir / ".paused").is_file():
            _safe_print(f"[{worker_name}] Dang tam dung, bo qua")
            continue

        pid_file = worker_dir / "bot.pid"
        home_dir = worker_dir / "home"
        home_dir.mkdir(parents=True, exist_ok=True)

        if (home_dir / "worker.done").is_file():
            _safe_print(f"[{worker_name}] Da hoan tat toan bo account, bo qua.")
            completed += 1
            continue

        if pid_file.is_file():
            try:
                pid = int(pid_file.read_text(encoding="utf-8").strip())
                if is_expected_worker_process(pid, worker_dir):
                    _safe_print(f"[{worker_name}] Dang chay (PID {pid})")
                    running += 1
                    continue
            except (ValueError, OSError):
                pass
            pid_file.unlink(missing_ok=True)

        worker_pass = 2 if (home_dir / "worker.first-pass.done").is_file() else 1
        now_str = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

        stdout_log = worker_dir / "stdout.log"
        err_log = worker_dir / "java-errors.log"

        with open(stdout_log, "a", encoding="utf-8") as f_out, open(err_log, "a", encoding="utf-8") as f_err:
            f_out.write(f"\n===== START OPTIMIZED {now_str} PASS {worker_pass}/2 =====\n")
            f_err.write(f"\n===== START OPTIMIZED {now_str} PASS {worker_pass}/2 =====\n")

        cp_sep = ";" if os.name == "nt" else ":"
        classpath = f"{worker_dir}{cp_sep}{CLASSES_DIR}"

        cmd = [
            java_bin,
            f"-Xms{JAVA_XMS}",
            f"-Xmx{JAVA_XMX}",
            "-XX:+UseSerialGC",
            "-XX:MinHeapFreeRatio=5",
            "-XX:MaxHeapFreeRatio=10",
            "-Djava.awt.headless=true",
            "-Xss256k",
            "-XX:CICompilerCount=2",
            "-Dnso.optimized=true",
            f"-Dnso.server={SERVER_NAME}",
            f"-Dnso.tick.ms={NSO_TICK_MS}",
            "-Dnso.skip.paint=true",
            "-Dnso.skip.periodic.gc=true",
            "-Dnso.skip.auto.popup=true",
            "-Dnso.skip.decorations=true",
            "-Dnso.lazy.map=true",
            "-Dnso.event.sender=true",
            "-Dnso.nvhn.headless=true",
            f"-Duser.home={home_dir.as_posix()}",
            "-cp", classpath,
            "OptimizedMain",
        ]

        creationflags = 0
        if os.name == "nt":
            creationflags = subprocess.CREATE_NO_WINDOW | subprocess.DETACHED_PROCESS

        try:
            with open(stdout_log, "a", encoding="utf-8") as f_out, open(err_log, "a", encoding="utf-8") as f_err:
                p = subprocess.Popen(
                    cmd,
                    cwd=str(worker_dir),
                    stdout=f_out,
                    stderr=f_err,
                    creationflags=creationflags,
                )

            pid_file.write_text(str(p.pid), encoding="utf-8")
            time.sleep(0.3)

            if is_expected_worker_process(p.pid, worker_dir):
                _safe_print(f"[{worker_name}] Da khoi dong (PID {p.pid})")
                started += 1
            else:
                sys.stderr.write(f"[!] [{worker_name}] Khoi dong that bai. Xem {err_log}\n")
                pid_file.unlink(missing_ok=True)
                failed += 1
        except Exception as e:
            sys.stderr.write(f"[!] [{worker_name}] Loi: {e}\n")
            failed += 1

        if delay > 0:
            time.sleep(delay)

    _safe_print(f"\nKet qua: Khoi dong moi={started}, Dang chay={running}, Hoan tat={completed}, Loi={failed}")
    return 0 if failed == 0 else 1


def restart_stale_worker(worker_dir: Path) -> int:
    """Restarts one live worker whose stdout log has stopped changing."""
    worker_name = worker_dir.name
    pid_file = worker_dir / "bot.pid"
    if not pid_file.is_file():
        return 0

    try:
        pid = int(pid_file.read_text(encoding="utf-8").strip())
    except (ValueError, OSError):
        pid_file.unlink(missing_ok=True)
        return 0

    if not is_expected_worker_process(pid, worker_dir):
        pid_file.unlink(missing_ok=True)
        return 0

    if not kill_pid(pid):
        _safe_print(f"[{worker_name}] Khong dung duoc PID {pid}, bo qua restart.")
        return 1

    deadline = time.monotonic() + 5
    while time.monotonic() < deadline and is_pid_running(pid):
        time.sleep(0.1)
    if is_pid_running(pid):
        _safe_print(f"[{worker_name}] PID {pid} van con song, bo qua khoi dong lai.")
        return 1

    pid_file.unlink(missing_ok=True)

    class DummyArgs:
        pass

    start_req = DummyArgs()
    start_req.workers = [worker_name]
    start_req.delay = 0
    return _cmd_start_inner(start_req)


# ==========================================
# 4. STOP WORKERS
# ==========================================
def cmd_stop(args: Any) -> int:
    try:
        return _cmd_stop_inner(args)
    except Exception as exc:
        import traceback as _tb
        _tb.print_exc()
        sys.stderr.write(f"[!] Exception: {exc}\n")
        return 1


def _cmd_stop_inner(args: Any) -> int:
    target_numbers = set()
    if args.workers:
        for w in args.workers:
            cleaned = str(w).replace("worker-", "")
            if cleaned.isdigit():
                target_numbers.add(int(cleaned))

    supervisor_pid_file = WORKERS_DIR / "supervisor.pid"
    if not target_numbers and supervisor_pid_file.is_file():
        try:
            spid = int(supervisor_pid_file.read_text(encoding="utf-8").strip())
            if is_expected_supervisor_process(spid):
                _safe_print(f"Dang dung Supervisor (PID {spid})...")
                kill_pid(spid)
        except (ValueError, OSError):
            pass
        supervisor_pid_file.unlink(missing_ok=True)

    if not target_numbers:
        # A previous supervisor version could lose supervisor.pid while its
        # process kept running. Find and terminate such orphans as well.
        for candidate in process_ids():
            if is_expected_supervisor_process(candidate):
                _safe_print(f"Dang dung Supervisor mo coi (PID {candidate})...")
                kill_pid(candidate)

    selected_worker_dirs = []
    for worker_dir in sorted(WORKERS_DIR.glob("worker-*")):
        num = int(worker_dir.name.replace("worker-", ""))
        if not target_numbers or num in target_numbers:
            selected_worker_dirs.append(worker_dir)

    stopped = 0
    stopped_pids: Set[int] = set()
    for worker_dir in selected_worker_dirs:
        worker_name = worker_dir.name

        pid_file = worker_dir / "bot.pid"
        if not pid_file.is_file():
            continue

        try:
            pid = int(pid_file.read_text(encoding="utf-8").strip())
            if is_expected_worker_process(pid, worker_dir):
                _safe_print(f"Dang dung {worker_name} (PID {pid})...")
                if kill_pid(pid):
                    stopped += 1
                    stopped_pids.add(pid)
        except (ValueError, OSError):
            pass

        pid_file.unlink(missing_ok=True)

    # A Supervisor can be killed while it is between Popen() and writing
    # bot.pid. Scan command lines as a fallback so an untracked Java worker
    # cannot survive Stop all and be mistaken for a later restart.
    for candidate in process_ids():
        if candidate in stopped_pids:
            continue
        for worker_dir in selected_worker_dirs:
            if is_expected_worker_process(candidate, worker_dir):
                _safe_print(f"Dang dung worker mo coi (PID {candidate})...")
                if kill_pid(candidate):
                    stopped += 1
                    stopped_pids.add(candidate)
                break

    _safe_print(f"Da dung {stopped} worker.")
    return 0


def get_workers_status_dict() -> Dict[str, Any]:
    """Trả về trạng thái toàn bộ workers dạng dict (JSON compatible) cho Web Dashboard."""
    worker_dirs = sorted(WORKERS_DIR.glob("worker-*"))
    workers = []

    for worker_dir in worker_dirs:
        if not worker_dir.is_dir():
            continue
        worker_name = worker_dir.name
        pid_file = worker_dir / "bot.pid"
        home_dir = worker_dir / "home"

        paused = (worker_dir / ".paused").is_file()
        done = (home_dir / "worker.done").is_file()
        first_pass_done = (home_dir / "worker.first-pass.done").is_file()

        pid = None
        if pid_file.is_file():
            try:
                raw_pid = pid_file.read_text(encoding="utf-8").strip()
                if raw_pid.isdigit() and is_expected_worker_process(int(raw_pid), worker_dir):
                    pid = int(raw_pid)
            except (ValueError, OSError):
                pass

        if paused:
            state = "PAUSED"
        elif done:
            state = "DONE"
        elif pid is not None:
            state = "RUNNING"
        else:
            state = "STOPPED"

        cpu = rss_mb = None
        if pid is not None:
            cpu, rss_mb = get_process_stats(pid)

        # Đọc log
        stdout_log = worker_dir / "stdout.log"
        last_log_at = None
        last_log_age_seconds = log_age_seconds(stdout_log)
        last_auto_log = None
        char_name = None
        if stdout_log.is_file():
            try:
                mtime = stdout_log.stat().st_mtime
                last_log_at = datetime.fromtimestamp(mtime).strftime("%Y-%m-%d %H:%M:%S")
                with open(stdout_log, "r", encoding="utf-8", errors="replace") as lf:
                    tail = lf.readlines()[-100:]
                    for line in reversed(tail):
                        if not char_name:
                            m = RE_CHAR_STATUS.search(line) or RE_CHAR_CHOOSE.search(line)
                            if m:
                                char_name = m.group(1)
                        if not last_auto_log and "AUTO NVHN" in line:
                            last_auto_log = line.strip()
                        if char_name and last_auto_log:
                            break
            except Exception:
                pass

        # Đếm account
        acc_count = 0
        acc_file = worker_dir / "account.csv"
        if acc_file.is_file():
            try:
                acc_lines = acc_file.read_text(encoding="utf-8-sig", errors="replace").splitlines()
                acc_count = sum(1 for line in acc_lines[1:] if line.strip())
            except OSError:
                pass

        workers.append({
            "name": worker_name,
            "pid": pid,
            "state": state,
            "char_name": char_name,
            "paused": paused,
            "run_pass": 2 if first_pass_done else 1,
            "run_pass_total": 2,
            "cpu_percent": cpu,
            "rss_mb": rss_mb,
            "elapsed": None,
            "accounts": acc_count,
            "last_auto_log": last_auto_log,
            "last_log_at": last_log_at,
            "last_log_age_seconds": last_log_age_seconds,
            "stdout_log": str(stdout_log),
            "error_log": str(worker_dir / "java-errors.log"),
        })

    totals = {
        "running": sum(w["state"] == "RUNNING" for w in workers),
        "stopped": sum(w["state"] == "STOPPED" for w in workers),
        "paused": sum(w["state"] == "PAUSED" for w in workers),
        "done": sum(w["state"] == "DONE" for w in workers),
        "total": len(workers),
    }

    return {
        "workers_dir": str(WORKERS_DIR),
        "workers": workers,
        "totals": totals,
    }


# ==========================================
# 5. STATUS WORKERS
# ==========================================
def cmd_status(args: Any) -> int:
    try:
        return _cmd_status_inner(args)
    except Exception as exc:
        import traceback as _tb
        _tb.print_exc()
        sys.stderr.write(f"[!] Exception: {exc}\n")
        return 1


def _cmd_status_inner(args: Any) -> int:
    if getattr(args, "json", False):
        import json
        print(json.dumps(get_workers_status_dict(), ensure_ascii=False, indent=2))
        return 0

    worker_dirs = sorted(WORKERS_DIR.glob("worker-*"))
    if not worker_dirs:
        _safe_print("[!] Khong co worker nao trong thu muc.")
        return 0

    _safe_print(f"{'WORKER':<12} {'PID':<8} {'%CPU':<8} {'RSS(MB)':<10} {'PASS':<6} {'STATUS':<12} {'CHARACTER'}")
    _safe_print(f"{'-'*12} {'-'*8} {'-'*8} {'-'*10} {'-'*6} {'-'*12} {'-'*15}")

    for worker_dir in worker_dirs:
        worker_name = worker_dir.name
        pid_file = worker_dir / "bot.pid"
        home_dir = worker_dir / "home"

        status = "STOPPED"
        pid_str = "-"
        cpu_str = "-"
        rss_str = "-"
        char_name = "-"

        stdout_log = worker_dir / "stdout.log"
        if stdout_log.is_file():
            try:
                with open(stdout_log, "r", encoding="utf-8", errors="replace") as lf:
                    tail_lines = lf.readlines()[-200:]
                    for line in reversed(tail_lines):
                        m = RE_CHAR_STATUS.search(line) or RE_CHAR_CHOOSE.search(line)
                        if m:
                            char_name = m.group(1)
                            break
            except Exception:
                pass

        first_pass = (home_dir / "worker.first-pass.done").is_file()
        run_pass = "2/2" if first_pass else "1/2"

        if (home_dir / "worker.done").is_file():
            status = "DONE"
        elif pid_file.is_file():
            try:
                pid = int(pid_file.read_text(encoding="utf-8").strip())
                if is_expected_worker_process(pid, worker_dir):
                    status = "RUNNING"
                    pid_str = str(pid)
                    cpu, rss = get_process_stats(pid)
                    if cpu is not None:
                        cpu_str = f"{cpu:.1f}"
                    if rss is not None:
                        rss_str = f"{rss:.1f}"
            except (ValueError, OSError):
                pass

        _safe_print(f"{worker_name:<12} {pid_str:<8} {cpu_str:<8} {rss_str:<10} {run_pass:<6} {status:<12} {char_name}")

    return 0


# ==========================================
# 6. SUPERVISE WORKERS
# ==========================================
def cmd_supervise(args: Any) -> int:
    try:
        return _cmd_supervise_inner(args)
    except Exception as exc:
        import traceback as _tb
        _tb.print_exc()
        sys.stderr.write(f"[!] Exception: {exc}\n")
        return 1


def _cmd_supervise_inner(args: Any) -> int:
    delay = args.delay if args.delay is not None else 30
    check_interval = args.interval if args.interval is not None else 20

    WORKERS_DIR.mkdir(parents=True, exist_ok=True)
    supervisor_pid_file = WORKERS_DIR / "supervisor.pid"

    if supervisor_pid_file.is_file():
        try:
            old_pid = int(supervisor_pid_file.read_text(encoding="utf-8").strip())
            if is_pid_running(old_pid):
                sys.stderr.write(f"[!] Supervisor da chay tu truoc (PID {old_pid}).\n")
                return 1
        except (ValueError, OSError):
            pass

    current_pid = os.getpid()
    supervisor_pid_file.write_text(str(current_pid), encoding="utf-8")
    _safe_print(f"Optimized Supervisor dang chay (PID {current_pid})... Bam Ctrl+C de dung.")
    _safe_print(f"Gian cach: delay={delay}s, check_interval={check_interval}s\n")

    try:
        while True:
            worker_dirs = sorted(WORKERS_DIR.glob("worker-*"))
            if not worker_dirs:
                _safe_print("Chua co worker nao de giam sat. Doi...")
                time.sleep(check_interval)
                continue

            for worker_dir in worker_dirs:
                if (worker_dir / ".paused").is_file():
                    continue
                done_marker = worker_dir / "home" / "worker.done"
                first_pass_marker = worker_dir / "home" / "worker.first-pass.done"

                if done_marker.is_file() and not first_pass_marker.is_file():
                    shutil.move(str(done_marker), str(first_pass_marker))
                    now_str = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                    _safe_print(f"[{now_str}] {worker_dir.name} hoan tat Luot 1/2 -> Chuan bi chay Luot 2/2.")

            all_done = True
            for worker_dir in worker_dirs:
                if not (worker_dir / "home" / "worker.done").is_file():
                    all_done = False
                    break

            if all_done and len(worker_dirs) > 0:
                _safe_print("\n========================================================")
                _safe_print(" Tat ca worker da hoan tat ca 2 luot! Supervisor ket thuc.")
                _safe_print("========================================================")
                break

            class DummyArgs:
                pass
            start_req = DummyArgs()
            start_req.workers = args.workers
            start_req.delay = delay

            cmd_start(start_req)

            for worker_dir in worker_dirs:
                if (worker_dir / ".paused").is_file():
                    continue
                if (worker_dir / "home" / "worker.done").is_file():
                    continue

                pid_file = worker_dir / "bot.pid"
                if not pid_file.is_file():
                    continue
                try:
                    pid = int(pid_file.read_text(encoding="utf-8").strip())
                except (ValueError, OSError):
                    continue
                if not is_expected_worker_process(pid, worker_dir):
                    continue

                reason = stale_log_reason(worker_dir / "stdout.log")
                if reason is None:
                    continue

                _safe_print(
                    f"[{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}] "
                    f"{worker_dir.name} log im lặng đủ {STALE_LOG_SECONDS}s; đang restart."
                )
                _safe_print(f"Lý do: {reason}")
                restart_stale_worker(worker_dir)

            time.sleep(check_interval)
    except KeyboardInterrupt:
        _safe_print("\nSupervisor nhan lenh dung (Ctrl+C). Thoat.")
    finally:
        supervisor_pid_file.unlink(missing_ok=True)

    return 0


# ==========================================
# 7. RESET WORKERS
# ==========================================
def cmd_reset(args: Any = None) -> int:
    count = 0
    for marker in list(WORKERS_DIR.glob("worker-*/home/worker.done")) + list(
        WORKERS_DIR.glob("worker-*/home/worker.first-pass.done")
    ):
        marker.unlink(missing_ok=True)
        count += 1
        worker_name = marker.parent.parent.name
        _safe_print(f"Da reset {worker_name}: {marker.name}")

    _safe_print(f"Da xoa {count} marker hoan tat.")
    return 0


# ==========================================
# 8. LIVE LOGS
# ==========================================
def cmd_logs(args: Any) -> int:
    if args.worker:
        cleaned = str(args.worker).replace("worker-", "")
        worker_name = f"worker-{int(cleaned):02d}"
        log_file = WORKERS_DIR / worker_name / "stdout.log"
        if not log_file.is_file():
            _safe_print(f"[!] Khong tim thay log tai: {log_file}")
            return 1
        _safe_print(f"=== Dang theo doi log cua {worker_name} (Ctrl+C de thoat) ===")
        return tail_file(log_file)
    else:
        log_files = sorted(WORKERS_DIR.glob("worker-*/stdout.log"))
        if not log_files:
            _safe_print("[!] Chua co log file nao.")
            return 1
        _safe_print(f"=== Dang theo doi {len(log_files)} workers (Ctrl+C de thoat) ===")
        for lf in log_files:
            _safe_print(f"\n--- {lf.parent.name} ---")
            lines = lf.read_text(encoding="utf-8", errors="replace").splitlines()[-10:]
            for line in lines:
                _safe_print(line)
    return 0


def tail_file(file_path: Path) -> int:
    try:
        with open(file_path, "r", encoding="utf-8", errors="replace") as f:
            f.seek(0, os.SEEK_END)
            while True:
                line = f.readline()
                if line:
                    sys.stdout.write(line)
                    sys.stdout.flush()
                else:
                    time.sleep(0.5)
    except KeyboardInterrupt:
        _safe_print("\nDa dung theo doi log.")
    return 0


def main() -> None:
    parser = argparse.ArgumentParser(description="NSO Optimized Runtime Manager for Windows")
    subparsers = parser.add_subparsers(dest="command", required=True)

    # build
    subparsers.add_parser("build", help="Biên dịch source Java thành optimized classes")

    # build-workers
    p_bw = subparsers.add_parser("build-workers", help="Chia account.csv thành N worker")
    p_bw.add_argument("count", type=int, default=10, nargs="?", help="Số worker (mặc định 10)")
    p_bw.add_argument("--csv", type=str, default=None, help="Đường dẫn file account.csv")
    p_bw.add_argument("--no-compile", dest="compile", action="store_false", default=True, help="Không compile lại")

    # start
    p_start = subparsers.add_parser("start", help="Khởi động workers")
    p_start.add_argument("workers", nargs="*", help="Số thứ tự worker cần start (ví dụ 1 2 3)")
    p_start.add_argument("--delay", type=int, default=None, help="Giây giãn cách giữa các worker")
    p_start.add_argument("--server", choices=("ninjamobile", "tk"), default=None, help="Server: ninjamobile hoặc tk")

    # stop
    p_stop = subparsers.add_parser("stop", help="Dừng workers")
    p_stop.add_argument("workers", nargs="*", help="Số thứ tự worker cần dừng")

    # restart
    p_restart = subparsers.add_parser("restart", help="Khởi động lại workers")
    p_restart.add_argument("workers", nargs="*", help="Số thứ tự worker")
    p_restart.add_argument("--server", choices=("ninjamobile", "tk"), default=None, help="Server: ninjamobile hoặc tk")

    # status
    p_status = subparsers.add_parser("status", help="Xem trạng thái workers")
    p_status.add_argument("--json", action="store_true", help="Xuất dữ liệu định dạng JSON")

    # supervise
    p_sup = subparsers.add_parser("supervise", help="Giám sát và tự động chạy lại workers")
    p_sup.add_argument("workers", nargs="*", help="Danh sách worker cần giám sát")
    p_sup.add_argument("--delay", type=int, default=30, help="Giãn cách khởi động giữa các worker (giây)")
    p_sup.add_argument("--interval", type=int, default=20, help="Chu kỳ kiểm tra (giây)")
    p_sup.add_argument("--server", choices=("ninjamobile", "tk"), default=None, help="Server: ninjamobile hoặc tk")

    # reset
    subparsers.add_parser("reset", help="Reset marker hoàn tất để chạy lại")

    # logs
    p_logs = subparsers.add_parser("logs", help="Xem log worker")
    p_logs.add_argument("worker", nargs="?", help="Số worker cần xem log")

    args = parser.parse_args()

    if getattr(args, "server", None):
        global SERVER_NAME
        SERVER_NAME = args.server

    if args.command == "build":
        sys.exit(cmd_build(args))
    elif args.command == "build-workers":
        sys.exit(cmd_build_workers(args))
    elif args.command == "start":
        sys.exit(cmd_start(args))
    elif args.command == "stop":
        sys.exit(cmd_stop(args))
    elif args.command == "restart":
        cmd_stop(args)
        sys.exit(cmd_start(args))
    elif args.command == "status":
        sys.exit(cmd_status(args))
    elif args.command == "supervise":
        sys.exit(cmd_supervise(args))
    elif args.command == "reset":
        sys.exit(cmd_reset(args))
    elif args.command == "logs":
        sys.exit(cmd_logs(args))


if __name__ == "__main__":
    main()
