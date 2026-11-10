"""Thông tin tiến trình đa nền tảng cho Windows (không có /proc).

Dùng bởi uplevel_monitor/server.py (khi os.name == "nt") và windows/nso_win.py.
Ưu tiên psutil nếu đã cài (có cmdline để xác minh đúng worker); nếu không thì dùng Win32 API
qua ctypes (chỉ kiểm tra được tên file thực thi java.exe / python.exe).
Trên Linux các hàm cũng chạy được (đọc /proc, gửi signal) để test.
"""
from __future__ import annotations

import os
import signal
import subprocess
import threading
import time
from pathlib import Path

try:  # tùy chọn
    import psutil  # type: ignore
except Exception:  # pragma: no cover
    psutil = None

IS_WINDOWS = os.name == "nt"
# Tên tiến trình chấp nhận khi không đọc được cmdline (ctypes fallback trên Windows).
WORKER_IMAGES = {"java.exe", "javaw.exe", "python.exe", "pythonw.exe", "python3.exe"}

if IS_WINDOWS:
    import ctypes
    from ctypes import wintypes

    _k32 = ctypes.WinDLL("kernel32", use_last_error=True)
    _PROCESS_QUERY_INFORMATION = 0x0400
    _PROCESS_VM_READ = 0x0010
    _PROCESS_QUERY_LIMITED_INFORMATION = 0x1000
    _PROCESS_TERMINATE = 0x0001
    _STILL_ACTIVE = 259

    class _PMC(ctypes.Structure):
        _fields_ = [
            ("cb", wintypes.DWORD),
            ("PageFaultCount", wintypes.DWORD),
            ("PeakWorkingSetSize", ctypes.c_size_t),
            ("WorkingSetSize", ctypes.c_size_t),
            ("QuotaPeakPagedPoolUsage", ctypes.c_size_t),
            ("QuotaPagedPoolUsage", ctypes.c_size_t),
            ("QuotaPeakNonPagedPoolUsage", ctypes.c_size_t),
            ("QuotaNonPagedPoolUsage", ctypes.c_size_t),
            ("PagefileUsage", ctypes.c_size_t),
            ("PeakPagefileUsage", ctypes.c_size_t),
        ]

    _k32.OpenProcess.restype = wintypes.HANDLE
    _k32.OpenProcess.argtypes = [wintypes.DWORD, wintypes.BOOL, wintypes.DWORD]
    _k32.CloseHandle.argtypes = [wintypes.HANDLE]
    _k32.GetExitCodeProcess.argtypes = [wintypes.HANDLE, ctypes.POINTER(wintypes.DWORD)]
    _k32.QueryFullProcessImageNameW.argtypes = [
        wintypes.HANDLE, wintypes.DWORD, wintypes.LPWSTR, ctypes.POINTER(wintypes.DWORD)]
    _k32.GetProcessTimes.argtypes = [wintypes.HANDLE] + [ctypes.POINTER(wintypes.FILETIME)] * 4
    _k32.TerminateProcess.argtypes = [wintypes.HANDLE, wintypes.UINT]
    _k32.K32GetProcessMemoryInfo.argtypes = [wintypes.HANDLE, ctypes.POINTER(_PMC), wintypes.DWORD]

    def _open(pid: int, access: int):
        handle = _k32.OpenProcess(access, False, pid)
        return handle or None

    def _ft(ft) -> float:
        return ((ft.dwHighDateTime << 32) | ft.dwLowDateTime) / 1e7


def _norm(text: str) -> str:
    return text.replace("\\", "/").lower() if IS_WINDOWS else text


# ---------------------------------------------------------------- cơ bản

def alive(pid: int) -> bool:
    if pid <= 0:
        return False
    if psutil is not None:
        try:
            p = psutil.Process(pid)
            return p.is_running() and p.status() != psutil.STATUS_ZOMBIE
        except Exception:
            return False
    if IS_WINDOWS:
        handle = _open(pid, _PROCESS_QUERY_LIMITED_INFORMATION)
        if not handle:
            return False
        try:
            code = wintypes.DWORD()
            return bool(_k32.GetExitCodeProcess(handle, ctypes.byref(code))) and code.value == _STILL_ACTIVE
        finally:
            _k32.CloseHandle(handle)
    try:
        os.kill(pid, 0)
    except OSError:
        return False
    stat = _read_text(Path(f"/proc/{pid}/stat"))
    return not (stat and stat[stat.rfind(")") + 2:].startswith("Z"))


def cmdline(pid: int) -> str | None:
    """Dòng lệnh (nối bằng dấu cách) hoặc None nếu không đọc được."""
    if psutil is not None:
        try:
            return " ".join(psutil.Process(pid).cmdline())
        except Exception:
            return None
    if IS_WINDOWS:
        return None
    try:
        return Path(f"/proc/{pid}/cmdline").read_bytes().replace(b"\0", b" ").decode(errors="replace")
    except OSError:
        return None


def image_name(pid: int) -> str | None:
    if psutil is not None:
        try:
            return psutil.Process(pid).name().lower()
        except Exception:
            return None
    if not IS_WINDOWS:
        return (_read_text(Path(f"/proc/{pid}/comm")) or "").strip() or None
    handle = _open(pid, _PROCESS_QUERY_LIMITED_INFORMATION)
    if not handle:
        return None
    try:
        size = wintypes.DWORD(1024)
        buf = ctypes.create_unicode_buffer(size.value)
        if not _k32.QueryFullProcessImageNameW(handle, 0, buf, ctypes.byref(size)):
            return None
        return os.path.basename(buf.value).lower()
    finally:
        _k32.CloseHandle(handle)


def matches(pid: int, needles: list[str]) -> bool:
    """PID còn sống và cmdline chứa mọi chuỗi trong needles (so path không phân biệt / \\ và hoa thường
    trên Windows). Không đọc được cmdline (Windows, không có psutil) -> chỉ kiểm tra là java/python."""
    if not alive(pid):
        return False
    line = cmdline(pid)
    if line is None:
        return (image_name(pid) or "") in WORKER_IMAGES
    line = _norm(line)
    return all(_norm(n) in line for n in needles)


def _read_text(path: Path) -> str | None:
    try:
        return path.read_text(encoding="utf-8", errors="replace")
    except OSError:
        return None


def read_pid(path: Path) -> int | None:
    raw = (_read_text(path) or "").strip()
    return int(raw) if raw.isdigit() else None


# ---------------------------------------------------------------- CPU / RAM

def cpu_times(pid: int) -> tuple[float, float] | None:
    """(giây CPU user+kernel, thời điểm tạo tiến trình theo epoch)."""
    if psutil is not None:
        try:
            p = psutil.Process(pid)
            t = p.cpu_times()
            return t.user + t.system, p.create_time()
        except Exception:
            return None
    if IS_WINDOWS:
        handle = _open(pid, _PROCESS_QUERY_LIMITED_INFORMATION)
        if not handle:
            return None
        try:
            c, e, k, u = (wintypes.FILETIME() for _ in range(4))
            if not _k32.GetProcessTimes(handle, ctypes.byref(c), ctypes.byref(e), ctypes.byref(k), ctypes.byref(u)):
                return None
            return _ft(k) + _ft(u), _ft(c) - 11644473600.0
        finally:
            _k32.CloseHandle(handle)
    stat = _read_text(Path(f"/proc/{pid}/stat"))
    uptime = _read_text(Path("/proc/uptime"))
    if not stat or not uptime:
        return None
    fields = stat[stat.rfind(")") + 2:].split()
    tck = os.sysconf("SC_CLK_TCK")
    try:
        start = time.time() - float(uptime.split()[0]) + int(fields[19]) / tck
        return (int(fields[11]) + int(fields[12])) / tck, start
    except (IndexError, ValueError):
        return None


_cpu_lock = threading.Lock()
_cpu_samples: dict[int, tuple[float, float, float]] = {}  # pid -> (cpu, monotonic, create_time)


def cpu_percent(pid: int) -> float | None:
    """% của MỘT core giữa hai lần gọi; lần đầu lấy trung bình từ lúc process khởi động."""
    sample = cpu_times(pid)
    if sample is None:
        return None
    cpu, created = sample
    now = time.monotonic()
    with _cpu_lock:
        prev = _cpu_samples.get(pid)
        _cpu_samples[pid] = (cpu, now, created)
        for old in [p for p in _cpu_samples if p != pid and not alive(p)]:
            _cpu_samples.pop(old, None)
    if prev and prev[2] == created and now - prev[1] >= 0.5:
        return max(0.0, (cpu - prev[0]) / (now - prev[1]) * 100)
    elapsed = time.time() - created
    return cpu / elapsed * 100 if elapsed > 0 else None


def rss_mb(pid: int) -> float | None:
    if psutil is not None:
        try:
            return psutil.Process(pid).memory_info().rss / 1024 / 1024
        except Exception:
            return None
    if IS_WINDOWS:
        handle = (_open(pid, _PROCESS_QUERY_INFORMATION | _PROCESS_VM_READ)
                  or _open(pid, _PROCESS_QUERY_LIMITED_INFORMATION))
        if not handle:
            return None
        try:
            pmc = _PMC()
            pmc.cb = ctypes.sizeof(_PMC)
            if not _k32.K32GetProcessMemoryInfo(handle, ctypes.byref(pmc), pmc.cb):
                return None
            return pmc.WorkingSetSize / 1024 / 1024
        finally:
            _k32.CloseHandle(handle)
    for line in (_read_text(Path(f"/proc/{pid}/status")) or "").splitlines():
        if line.startswith("VmRSS:"):
            try:
                return int(line.split()[1]) / 1024
            except (IndexError, ValueError):
                return None
    return None


# ---------------------------------------------------------------- dừng tiến trình

def stop(pid: int, timeout: float = 4.0) -> None:
    """Windows: taskkill /T /F (cả cây tiến trình). Linux: TERM, quá timeout thì KILL."""
    if IS_WINDOWS:
        subprocess.run(["taskkill", "/PID", str(pid), "/T", "/F"],
                       stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=False)
    else:
        try:
            os.kill(pid, signal.SIGTERM)
        except OSError:
            return
    deadline = time.monotonic() + timeout
    while time.monotonic() < deadline:
        if not alive(pid):
            return
        time.sleep(0.2)
    if IS_WINDOWS:
        handle = _open(pid, _PROCESS_TERMINATE)
        if handle:
            _k32.TerminateProcess(handle, 1)
            _k32.CloseHandle(handle)
    else:
        try:
            os.kill(pid, signal.SIGKILL)
        except OSError:
            pass


# ---------------------------------------------------------------- dùng cho uplevel_monitor/server.py

def pid_from_file(path: Path, worker_dir: Path) -> int | None:
    """Như server._pid_from_file: PID còn sống và cmdline chứa <worker>/home."""
    pid = read_pid(path)
    if pid is None:
        return None
    return pid if matches(pid, [str(worker_dir / "home")]) else None
