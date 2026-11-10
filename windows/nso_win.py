"""Bản Windows của uplevel/scripts/*.sh và uplevel35/scripts/*.sh (chỉ dùng Python stdlib, psutil tùy chọn).

    python windows/nso_win.py <uplevel|uplevel35> <lệnh> [tham số...]

Lệnh: build, build-workers [N], start [...], stop [số...], status [số...], logs [...], check-patches.
Giữ nguyên tham số, biến môi trường, file pid/log/home như bản .sh để uplevel_monitor đọc được.

- build / check-patches: chạy CHÍNH build.sh / check-patches.sh cũ bằng Git Bash (Git for Windows),
  thêm shim (windows/shims) cho python3 (UTF-8, ghi LF) và javac (đổi đường dẫn /c/... trong @sources.txt).
- start/stop/status/logs: viết lại bằng Python vì bản .sh dùng /proc, nohup, kill, classpath ':'.
  worker-loop.sh được thay bằng lệnh nội bộ `_loop` (một tiến trình python ẩn cho mỗi worker).
"""
from __future__ import annotations

import os
import re
import shutil
import subprocess
import sys
import tempfile
import time
from datetime import datetime
from pathlib import Path

WIN_DIR = Path(__file__).resolve().parent
REPO_DIR = WIN_DIR.parent
sys.path.insert(0, str(REPO_DIR))
from uplevel_monitor import winproc  # noqa: E402

IS_WINDOWS = os.name == "nt"
HANDOFF_EXIT_CODE = 3
WORKER_RE = re.compile(r"^worker-\d+$")
# Log Java (System.out) và đọc account.csv theo UTF-8 thay vì cp1252 mặc định của Windows.
JAVA_ENCODING_OPTS = ["-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8",
                      "-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8"]


def env(name: str, default: str) -> str:
    value = os.environ.get(name)
    return default if value is None or value == "" else value


def die(msg: str, code: int = 1) -> None:
    print(msg, file=sys.stderr)
    sys.exit(code)


def now_str() -> str:
    return datetime.now().strftime("%Y-%m-%d %H:%M:%S")


# ---------------------------------------------------------------- cấu hình runtime

class Runtime:
    def __init__(self, name: str) -> None:
        self.name = name
        self.dir = REPO_DIR / name
        self.scripts = self.dir / "scripts"
        if name == "uplevel":
            p = "UPLEVEL_"
            self.csv = Path(env("UPLEVEL_ACCOUNT_CSV", str(REPO_DIR / "account-as20.csv")))
            self.default_workers = 10
            self.state_prefix = "UPLEVEL STATE "
            self.tail_lines = 10
        else:
            p = "UPLEVEL35_"
            self.csv = Path(env("UPLEVEL35_ACCOUNT_CSV", str(REPO_DIR / "account-35.csv")))
            self.default_workers = 1
            self.state_prefix = "UPLEVEL35 STATE "
            self.tail_lines = 200
        self.prefix = p
        self.build_dir = Path(os.path.abspath(env(p + "BUILD_DIR", str(self.dir / "build"))))
        self.classes = self.build_dir / "classes"
        self.workers = Path(env(p + "WORKERS_DIR", str(self.dir / "workers")))

    def worker_dir(self, arg: str) -> Path:
        number = arg[len("worker-"):] if arg.startswith("worker-") else arg
        if not number.isdigit():
            die(f"Worker không hợp lệ: {arg}", 2)
        return self.workers / f"worker-{int(number):02d}"

    def select(self, args: list[str], must_exist: bool = True) -> list[Path]:
        if args:
            dirs = [self.worker_dir(a) for a in args]
            for d in dirs:
                if must_exist and not d.is_dir():
                    die(f"Không tìm thấy {d.name}.")
            return dirs
        if not self.workers.is_dir():
            return []
        return sorted(d for d in self.workers.iterdir() if d.is_dir() and WORKER_RE.match(d.name))

    def needles(self, worker_dir: Path) -> list[str]:
        """Chuỗi phải có trong cmdline của java/loop thuộc worker này (giống is_worker_pid trong .sh)."""
        if self.name == "uplevel":
            return ["OptimizedMain", str(worker_dir)]
        return ["-Dnso.runtime=uplevel35", str(worker_dir / "home")]

    def pid(self, worker_dir: Path, kind: str) -> int | None:
        pid = winproc.read_pid(worker_dir / f"{kind}.pid")
        return pid if pid is not None and winproc.matches(pid, self.needles(worker_dir)) else None


# ---------------------------------------------------------------- build (Git Bash + build.sh cũ)

def find_bash() -> str:
    custom = os.environ.get("NSO_BASH") or os.environ.get("GIT_BASH")
    if custom:
        return custom
    if not IS_WINDOWS:
        found = shutil.which("bash")
        if found:
            return found
        die("Không tìm thấy bash.")
    candidates: list[Path] = []
    git = shutil.which("git")
    if git:
        g = Path(git).resolve()
        # <Git>\cmd\git.exe, <Git>\bin\git.exe hoặc <Git>\mingw64\bin\git.exe
        for root in (g.parent.parent, g.parent.parent.parent):
            candidates.append(root / "bin" / "bash.exe")
    for base in (os.environ.get("ProgramFiles"), os.environ.get("ProgramW6432"),
                 os.environ.get("ProgramFiles(x86)"), os.path.join(os.environ.get("LOCALAPPDATA", ""), "Programs")):
        if base:
            candidates.append(Path(base) / "Git" / "bin" / "bash.exe")
    for c in candidates:
        # Không dùng C:\Windows\System32\bash.exe (WSL): đường dẫn và java khác hẳn.
        if c.is_file() and "system32" not in str(c).lower():
            return str(c)
    die("Không tìm thấy Git Bash. Cài Git for Windows (https://git-scm.com/download/win) "
        "hoặc đặt biến NSO_BASH=C:\\...\\Git\\bin\\bash.exe")
    return ""


def find_javac() -> str:
    exe = "javac.exe" if IS_WINDOWS else "javac"
    java_home = os.environ.get("JAVA_HOME")
    if java_home and (Path(java_home) / "bin" / exe).is_file():
        return str(Path(java_home) / "bin" / exe)
    found = shutil.which("javac")
    if not found:
        die("Không tìm thấy javac. Cài JDK (8+) và thêm vào PATH hoặc đặt JAVA_HOME.")
    return found


def check_lf(script: Path) -> None:
    if b"\r\n" in script.read_bytes():
        die(f"{script} có xuống dòng CRLF (git autocrlf) nên bash không chạy được.\n"
            "Sửa: git config core.autocrlf false && git rm -r --cached -q . && git reset --hard\n"
            "(repo đã có .gitattributes giữ nguyên LF cho *.sh, *.java, *.patch)")


def run_bash_script(script: Path, args: list[str], extra_env: dict[str, str] | None = None) -> int:
    check_lf(script)
    bash = find_bash()
    run_env = os.environ.copy()
    run_env.update(extra_env or {})
    run_env["PYTHONUTF8"] = "1"
    run_env["NSO_REAL_PYTHON"] = sys.executable
    run_env["NSO_REAL_JAVAC"] = find_javac()
    run_env["NSO_SHIM_DIR"] = str(WIN_DIR / "shims")
    run_env.pop("MSYS_NO_PATHCONV", None)  # cần MSYS tự đổi /c/... -> C:/... khi gọi python/javac
    boot = ('p() { if command -v cygpath >/dev/null 2>&1; then cygpath -u "$1"; else printf %s "$1"; fi; }; '
            'export NSO_SHIM_DIR="$(p "$NSO_SHIM_DIR")"; export PATH="$NSO_SHIM_DIR:$PATH"; '
            'command -v patch >/dev/null 2>&1 || echo "CẢNH BÁO: Git Bash thiếu lệnh patch" >&2; '
            's="$(p "$0")"; exec bash "$s" "$@"')
    return subprocess.call([bash, "-c", boot, str(script), *args], env=run_env, cwd=str(REPO_DIR))


def cmd_build(rt: Runtime, args: list[str]) -> int:
    return run_bash_script(rt.scripts / "build.sh", args)


def cmd_check_patches(rt: Runtime, args: list[str]) -> int:
    if rt.name != "uplevel35":
        die("check-patches chỉ có ở uplevel35.")
    return run_bash_script(rt.scripts / "check-patches.sh", args)


def cmd_build_workers(rt: Runtime, args: list[str]) -> int:
    if len(args) > 1 or (args and not re.fullmatch(r"[1-9][0-9]*", args[0])):
        die(f"Usage: build-workers [worker_count]", 2)
    count = int(args[0]) if args else rt.default_workers
    if not rt.csv.is_file():
        die(f"Không tìm thấy account CSV: {rt.csv}")
    for d in rt.select([]):
        pid = rt.pid(d, "loop") or rt.pid(d, "bot")
        if pid:
            die(f"{d.name} đang chạy (PID {pid}); hãy stop trước khi build lại.")

    if rt.name == "uplevel":
        do_build = env("UPLEVEL_BUILD_UPLEVEL", "1") != "0"
    else:
        do_build = env("UPLEVEL35_SKIP_BUILD", "0") != "1"
    if do_build:
        rc = run_bash_script(rt.scripts / "build.sh", [], {rt.prefix + "ACCOUNT_CSV": str(rt.csv)})
        if rc != 0:
            return rc

    lines = rt.csv.read_text(encoding="utf-8-sig", errors="replace").splitlines()
    if not lines:
        die("account CSV rỗng.")
    header = lines[0].rstrip("\r")
    accounts = [ln.rstrip("\r") for ln in lines[1:] if ln.strip()]
    total = len(accounts)
    if total == 0:
        die("account CSV không có tài khoản.")
    if count > total:
        die(f"Có {total} tài khoản nhưng yêu cầu {count} worker.")

    rt.dir.mkdir(parents=True, exist_ok=True)
    staging = Path(tempfile.mkdtemp(prefix=".workers-build.", dir=str(rt.dir)))
    try:
        # Chia xen kẽ (round-robin): acc thứ i vào worker ((i-1) % N) + 1.
        buckets: list[list[str]] = [[header] for _ in range(count)]
        for row, acc in enumerate(accounts):
            buckets[row % count].append(acc)
        for index, rows in enumerate(buckets, 1):
            wd = staging / f"worker-{index:02d}"
            (wd / "home").mkdir(parents=True)
            with open(wd / "account.csv", "w", encoding="utf-8", newline="\n") as fh:
                fh.write("\n".join(rows) + "\n")
            print(f"Đã tạo worker-{index:02d}: {len(rows) - 1} tài khoản")
        if rt.workers.exists():
            backup = rt.dir / f".workers-old.{os.getpid()}"
            os.replace(rt.workers, backup)
            os.replace(staging, rt.workers)
            shutil.rmtree(backup, ignore_errors=True)
        else:
            rt.workers.parent.mkdir(parents=True, exist_ok=True)
            os.replace(staging, rt.workers)
    finally:
        if staging.exists():
            shutil.rmtree(staging, ignore_errors=True)
    print(f"Hoàn tất: {total} tài khoản / {count} worker tại {rt.workers}")
    return 0


# ---------------------------------------------------------------- start / loop

def java_command(rt: Runtime, worker_dir: Path, opts: dict[str, str]) -> list[str]:
    name = worker_dir.name
    java = "java"
    java_home = os.environ.get("JAVA_HOME")
    if java_home and not shutil.which("java"):
        java = str(Path(java_home) / "bin" / ("java.exe" if IS_WINDOWS else "java"))
    cmd = [env("JAVA_BIN", java), "-Xms" + env("JAVA_XMS", "8m"), "-Xmx" + env("JAVA_XMX", "36m")]
    cmd += os.environ.get("JAVA_OPTS", "").split()
    cmd += JAVA_ENCODING_OPTS
    if rt.name == "uplevel":
        run_dir = Path(env("UPLEVEL_RUN_DIR", str(rt.dir / "run")))
        props = [
            ("nso.server", env("UPLEVEL_SERVER", "180.93.114.122:14444")),
            ("nso.as20.mode", opts["mode"]),
            ("nso.worker.name", name),
            ("nso.nvhn.error.dir", str(run_dir / "nvhn-errors")),
            ("nso.uplevel.crystal", env("UPLEVEL_CRYSTAL_ID", "-1")),
            ("nso.uplevel.all.crystals", env("UPLEVEL_ALL_CRYSTALS", "false")),
            ("nso.uplevel.duplicate", env("UPLEVEL_UPGRADE_DUPLICATE", "false")),
            ("nso.uplevel.duplicate.count", env("UPLEVEL_UPGRADE_DUPLICATE_COUNT", "5")),
            ("nso.uplevel.no.split", env("UPLEVEL_NO_SPLIT", "false")),
            ("nso.uplevel.allow.stacks", env("UPLEVEL_ALLOW_STACKS", "false")),
            ("nso.uplevel.upgrade.safe", env("UPLEVEL_UPGRADE_SAFE", "false")),
            ("nso.process.per.character", env("UPLEVEL_PROCESS_PER_CHARACTER", "true")),
        ]
    else:
        props = [
            ("nso.runtime", "uplevel35"),
            ("nso.server", env("UPLEVEL35_SERVER", "180.93.114.122:14444")),
            ("nso.worker.name", name),
            ("nso.nvhn.error.dir", str(rt.dir / "run" / "errors")),
            ("nso.uplevel35.min", env("UPLEVEL35_MIN", "25")),
            ("nso.uplevel35.target", env("UPLEVEL35_TARGET", "35")),
            ("nso.uplevel35.debug", opts["debug"]),
            ("nso.process.per.character", env("UPLEVEL35_PROCESS_PER_CHARACTER", "true")),
        ]
    cmd += [f"-D{k}={v}" for k, v in props]
    cmd += [f"-Duser.home={worker_dir / 'home'}", "-cp", os.pathsep.join([str(worker_dir), str(rt.classes)]),
            "OptimizedMain"]
    return cmd


def parse_start_args(rt: Runtime, args: list[str]) -> tuple[dict[str, str], int, list[str]]:
    usage = ("Usage: start [--mode 1..6] [--delay giây] [số_worker...]" if rt.name == "uplevel"
             else "Usage: start [--delay giây] [--debug] [số_worker...]")
    mode = env("UPLEVEL_MODE", "0")
    delay = env(rt.prefix + "START_DELAY", "3")
    debug = env("UPLEVEL35_DEBUG", "false")
    workers: list[str] = []
    i = 0
    while i < len(args):
        a = args[i]
        if a in ("-h", "--help"):
            print(usage)
            sys.exit(0)
        elif a == "--delay" or (a == "--mode" and rt.name == "uplevel"):
            if i + 1 >= len(args):
                die(usage, 2)
            if a == "--delay":
                delay = args[i + 1]
            else:
                mode = args[i + 1]
            i += 2
            continue
        elif a.startswith("--delay="):
            delay = a.split("=", 1)[1]
        elif a.startswith("--mode=") and rt.name == "uplevel":
            mode = a.split("=", 1)[1]
        elif a == "--debug" and rt.name == "uplevel35":
            debug = "true"
        else:
            num = a[len("worker-"):] if a.startswith("worker-") else a
            if not num.isdigit():
                die(usage, 2)
            workers.append(a)
        i += 1
    if mode == "0":
        mode = "1"
    if rt.name == "uplevel" and not re.fullmatch(r"[1-6]", mode):
        die("Mode phải nằm trong 1..6.")
    if not delay.isdigit():
        die("Delay phải là số nguyên không âm.")
    return {"mode": mode, "debug": debug}, int(delay), workers


def spawn_flags() -> dict:
    if IS_WINDOWS:
        # Cửa sổ console ẩn riêng: đóng cửa sổ cmd đã gõ lệnh / Ctrl+C không giết worker.
        return {"creationflags": subprocess.CREATE_NO_WINDOW | subprocess.CREATE_NEW_PROCESS_GROUP}
    return {"start_new_session": True}


def cmd_start(rt: Runtime, args: list[str]) -> int:
    opts, delay, worker_args = parse_start_args(rt, args)
    if not rt.classes.is_dir():
        die("Chưa có classes; chạy build-workers hoặc build trước.")
    dirs = rt.select(worker_args)
    if not dirs:
        die("Chưa có worker; chạy build-workers trước.")
    if rt.name == "uplevel":
        (Path(env("UPLEVEL_RUN_DIR", str(rt.dir / "run"))) / "nvhn-errors").mkdir(parents=True, exist_ok=True)
    else:
        (rt.dir / "run" / "errors").mkdir(parents=True, exist_ok=True)
    append = env(rt.prefix + "LOG_APPEND", "0") == "1"
    started = running = completed = 0
    for wd in dirs:
        name = wd.name
        if (wd / "home" / "worker.done").exists():
            print(f"{name} đã hoàn tất (xóa home/worker.done để chạy lại)")
            completed += 1
            continue
        pid = rt.pid(wd, "loop") or rt.pid(wd, "bot")
        if pid:
            print(f"{name} đang chạy (PID {pid})")
            running += 1
            continue
        for f in ("bot.pid", "loop.pid"):
            (wd / f).unlink(missing_ok=True)
        (wd / "home").mkdir(parents=True, exist_ok=True)
        tag = "UPLEVEL" if rt.name == "uplevel" else "UPLEVEL35"
        extra = f" mode={opts['mode']}" if rt.name == "uplevel" else ""
        banner = f"\n===== START {tag} {now_str()}{extra} =====\n".encode()
        for log in ("stdout.log", "java-errors.log"):
            with open(wd / log, "ab" if append else "wb") as fh:
                fh.write(banner)
        out = open(wd / "stdout.log", "ab")
        err = open(wd / "java-errors.log", "ab")
        try:
            proc = subprocess.Popen([sys.executable, str(Path(__file__).resolve()), "_loop", str(wd),
                                     *java_command(rt, wd, opts)],
                                    stdin=subprocess.DEVNULL, stdout=out, stderr=err,
                                    cwd=str(REPO_DIR), close_fds=True, **spawn_flags())
        finally:
            out.close()
            err.close()
        (wd / "loop.pid").write_text(f"{proc.pid}\n")
        time.sleep(0.5)
        if winproc.matches(proc.pid, rt.needles(wd)):
            bot = winproc.read_pid(wd / "bot.pid")
            print(f"Đã chạy {name} (loop PID {proc.pid}, java PID {bot or '?'})")
            started += 1
        else:
            print(f"{name} khởi động lỗi; xem {wd / 'java-errors.log'}", file=sys.stderr)
            for f in ("bot.pid", "loop.pid"):
                (wd / f).unlink(missing_ok=True)
        if delay:
            time.sleep(delay)
    print(f"Kết quả: mới={started}, đang chạy={running}, hoàn tất={completed}")
    return 0


def cmd_loop(args: list[str]) -> int:
    """Thay worker-loop.sh: chạy java, thoát mã 3 (còn nhân vật) thì chạy JVM mới."""
    worker_dir = Path(args[0])
    java_cmd = args[1:]
    pid_file = worker_dir / "bot.pid"
    child: list[subprocess.Popen] = []

    def on_stop(*_: object) -> None:
        if child:
            winproc.stop(child[0].pid)
        pid_file.unlink(missing_ok=True)
        os._exit(143)

    if not IS_WINDOWS:
        import signal
        for sig in (signal.SIGTERM, signal.SIGINT, signal.SIGHUP):
            signal.signal(sig, on_stop)
    run = 1
    while True:
        try:
            proc = subprocess.Popen(java_cmd, stdin=subprocess.DEVNULL, **(
                {"creationflags": subprocess.CREATE_NO_WINDOW} if IS_WINDOWS else {}))
        except OSError as exc:
            print(f"Không chạy được java ({java_cmd[0]}): {exc}", file=sys.stderr, flush=True)
            rc = 127
            break
        child[:] = [proc]
        pid_file.write_text(f"{proc.pid}\n")
        rc = proc.wait()
        child.clear()
        if rc == HANDOFF_EXIT_CODE and not (worker_dir / "home" / "worker.done").exists():
            run += 1
            print(f"\n===== NEXT PROCESS #{run} {now_str()} =====", flush=True)
            time.sleep(2)
            continue
        break
    print(f"\n===== WORKER LOOP END rc={rc} {now_str()} =====", flush=True)
    pid_file.unlink(missing_ok=True)
    return 0


# ---------------------------------------------------------------- stop / status / logs

def cmd_stop(rt: Runtime, args: list[str]) -> int:
    stopped = 0
    for wd in rt.select(args, must_exist=False):
        found = False
        # Dừng loop trước để nó không chạy JVM mới, sau đó dừng java còn sót.
        for kind in ("loop", "bot"):
            pid = rt.pid(wd, kind)
            if pid:
                print(f"Dừng {wd.name} ({kind} PID {pid})")
                winproc.stop(pid)
                found = True
            (wd / f"{kind}.pid").unlink(missing_ok=True)
        stopped += found
    print(f"Đã dừng {stopped} worker.")
    return 0


def last_state(log: Path, prefix: str) -> str:
    try:
        with open(log, "rb") as fh:
            fh.seek(0, os.SEEK_END)
            fh.seek(max(0, fh.tell() - 256 * 1024))
            text = fh.read().decode("utf-8", errors="replace")
    except OSError:
        return "-"
    idx = text.rfind(prefix)
    return text[idx + len(prefix):].split("\n", 1)[0].strip() if idx >= 0 else "-"


def cmd_status(rt: Runtime, args: list[str]) -> int:
    dirs = rt.select(args)
    if not dirs:
        die("Chưa có worker.")
    for wd in dirs:
        if (wd / "home" / "worker.done").exists():
            status = "DONE"
        elif (pid := rt.pid(wd, "bot")):
            rss = winproc.rss_mb(pid)
            status = f"RUN pid={pid} rss={int(rss)}MB" if rss is not None else f"RUN pid={pid} rss=?"
        elif (pid := rt.pid(wd, "loop")):
            status = f"RUN loop={pid} (đổi JVM)"
        else:
            status = "STOP"
        print(f"{wd.name:<10} {status:<28} {last_state(wd / 'stdout.log', rt.state_prefix)}")
    return 0


def tail_lines(path: Path, n: int) -> tuple[list[str], int]:
    with open(path, "rb") as fh:
        fh.seek(0, os.SEEK_END)
        size = fh.tell()
        fh.seek(max(0, size - max(64 * 1024, n * 400)))
        data = fh.read().decode("utf-8", errors="replace")
    return data.splitlines()[-n:] if n else [], size


def cmd_logs(rt: Runtime, args: list[str]) -> int:
    name = "stdout.log"
    pattern = None
    rest = []
    for a in args:
        if a == "--errors" and rt.name == "uplevel35":
            name = "java-errors.log"
        elif a == "--grep" and rt.name == "uplevel35":
            pattern = re.compile(r"==>|UPLEVEL35|AUTO NVHN|AUTO LOGIN|Exception")
        else:
            rest.append(a)
    logs = [wd / name for wd in rt.select(rest) if (wd / name).is_file()]
    if not logs:
        die("Chưa có log worker.")
    try:
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")  # type: ignore[attr-defined]
    except Exception:
        pass
    multi = len(logs) > 1
    current: list[Path | None] = [None]

    def emit(path: Path, lines: list[str]) -> None:
        lines = [ln for ln in lines if pattern is None or pattern.search(ln)]
        if not lines:
            return
        if multi and current[0] != path:
            print(f"\n==> {path} <==")
            current[0] = path
        for ln in lines:
            print(ln)
        sys.stdout.flush()

    pos: dict[Path, int] = {}
    pending: dict[Path, bytes] = {}
    for log in logs:
        lines, size = tail_lines(log, rt.tail_lines)
        emit(log, lines)
        pos[log] = size
        pending[log] = b""
    try:
        while True:
            for log in logs:
                try:
                    size = log.stat().st_size
                except OSError:
                    continue
                if size < pos[log]:  # log bị xóa/ghi lại khi start
                    pos[log], pending[log] = 0, b""
                if size == pos[log]:
                    continue
                with open(log, "rb") as fh:
                    fh.seek(pos[log])
                    chunk = fh.read()
                pos[log] += len(chunk)
                buf = pending[log] + chunk
                *complete, pending[log] = buf.split(b"\n")
                emit(log, [c.decode("utf-8", errors="replace").rstrip("\r") for c in complete])
            time.sleep(0.5)
    except KeyboardInterrupt:
        return 0


# ---------------------------------------------------------------- main

COMMANDS = {
    "build": cmd_build,
    "build-workers": cmd_build_workers,
    "start": cmd_start,
    "start-workers": cmd_start,
    "stop": cmd_stop,
    "stop-workers": cmd_stop,
    "status": cmd_status,
    "status-workers": cmd_status,
    "logs": cmd_logs,
    "logs-workers": cmd_logs,
    "check-patches": cmd_check_patches,
}


def main(argv: list[str]) -> int:
    if argv[:1] == ["_loop"]:
        return cmd_loop(argv[1:])
    if len(argv) < 2 or argv[0] not in ("uplevel", "uplevel35") or argv[1] not in COMMANDS:
        print(__doc__)
        return 2
    return COMMANDS[argv[1]](Runtime(argv[0]), argv[2:]) or 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
