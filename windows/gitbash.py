"""Chạy CHÍNH các script build *.sh của Linux bằng Git Bash (Git for Windows).

Dùng chung cho uplevel/uplevel35/uplevel1to35 (windows/nso_win.py), optimized-runtime
(optimized-runtime/windows/win_manager.py) và Tà Thú (ta-thu-runtime/windows/build_ta_thu.py),
để bản build trên Windows giống hệt bản Ubuntu thay vì duy trì một bản viết lại bằng Python.

Thêm shim (windows/shims) vào đầu PATH:
- python3: gọi python thật, -X utf8, ghi file giữ xuống dòng LF.
- javac: đổi đường dẫn /c/... trong file @sources.txt sang C:/... (MSYS không tự đổi nội dung file).
"""
from __future__ import annotations

import os
import shutil
import subprocess
import sys
from pathlib import Path

WIN_DIR = Path(__file__).resolve().parent
REPO_DIR = WIN_DIR.parent
IS_WINDOWS = os.name == "nt"


class GitBashError(RuntimeError):
    """Thiếu Git Bash / javac hoặc script bị CRLF."""


def find_bash(required: bool = True) -> str | None:
    custom = os.environ.get("NSO_BASH") or os.environ.get("GIT_BASH")
    if custom:
        if Path(custom).is_file():
            return custom
        if not required:
            return None
        raise GitBashError(f"NSO_BASH/GIT_BASH trỏ tới file không tồn tại: {custom}")
    if not IS_WINDOWS:
        found = shutil.which("bash")
        if found or not required:
            return found
        raise GitBashError("Không tìm thấy bash.")
    candidates: list[Path] = []
    git = shutil.which("git")
    if git:
        g = Path(git).resolve()
        # <Git>\cmd\git.exe, <Git>\bin\git.exe hoặc <Git>\mingw64\bin\git.exe
        for root in (g.parent.parent, g.parent.parent.parent):
            candidates.append(root / "bin" / "bash.exe")
    for base in (os.environ.get("ProgramFiles"), os.environ.get("ProgramW6432"),
                 os.environ.get("ProgramFiles(x86)"),
                 os.path.join(os.environ.get("LOCALAPPDATA", ""), "Programs")):
        if base:
            candidates.append(Path(base) / "Git" / "bin" / "bash.exe")
    for c in candidates:
        # Không dùng C:\Windows\System32\bash.exe (WSL): đường dẫn và java khác hẳn.
        if c.is_file() and "system32" not in str(c).lower():
            return str(c)
    if not required:
        return None
    raise GitBashError(
        "Không tìm thấy Git Bash. Cài Git for Windows (https://git-scm.com/download/win) "
        "hoặc đặt biến NSO_BASH=C:\\...\\Git\\bin\\bash.exe")


def find_javac() -> str:
    exe = "javac.exe" if IS_WINDOWS else "javac"
    java_home = os.environ.get("JAVA_HOME")
    if java_home and (Path(java_home) / "bin" / exe).is_file():
        return str(Path(java_home) / "bin" / exe)
    found = shutil.which("javac")
    if not found:
        raise GitBashError("Không tìm thấy javac. Cài JDK (8+) và thêm vào PATH hoặc đặt JAVA_HOME.")
    return found


def check_lf(script: Path) -> None:
    if b"\r\n" in script.read_bytes():
        raise GitBashError(
            f"{script} có xuống dòng CRLF (git autocrlf) nên bash không chạy được.\n"
            "Sửa: git config core.autocrlf false && git rm -r --cached -q . && git reset --hard\n"
            "(repo đã có .gitattributes giữ nguyên LF cho *.sh, *.java, *.patch)")


def _hidden_kwargs() -> dict:
    # Không bật cửa sổ console khi được gọi từ tiến trình nền (web/supervisor).
    # Nếu tiến trình cha đã có console (cmd đang mở) thì bash dùng chung console đó.
    if not IS_WINDOWS:
        return {}
    try:
        has_console = bool(__import__("ctypes").windll.kernel32.GetConsoleWindow())
    except Exception:
        has_console = True
    return {} if has_console else {"creationflags": subprocess.CREATE_NO_WINDOW}


def run_bash_script(script: Path, args: list[str] | tuple[str, ...] = (),
                    extra_env: dict[str, str] | None = None) -> int:
    """Chạy script .sh bằng Git Bash; trả về exit code. Ném GitBashError nếu thiếu công cụ."""
    script = Path(script)
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
    sys.stdout.flush()
    sys.stderr.flush()
    return subprocess.call([bash, "-c", boot, str(script), *args], env=run_env,
                           cwd=str(REPO_DIR), stdin=subprocess.DEVNULL, **_hidden_kwargs())


def to_bash_path(path: Path | str) -> str:
    """Đường dẫn truyền qua biến môi trường cho script .sh: C:/x/y (MSYS hiểu được)."""
    return Path(path).as_posix()
