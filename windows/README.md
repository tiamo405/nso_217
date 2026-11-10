# Chạy uplevel / uplevel35 / uplevel_monitor trên Windows

Mỗi script `.sh` có bản `.cmd` cùng tên, cùng tham số và cùng biến môi trường. File pid/log/home giữ
nguyên vị trí (`workers\worker-XX\stdout.log`, `bot.pid`, `loop.pid`, `home\status.properties`) nên
web monitor đọc được như trên Ubuntu. Các script `.sh` cũ không bị sửa.

## Cần cài

| Phần mềm | Dùng cho | Ghi chú |
|---|---|---|
| JDK 8+ (`java`, `javac`) | build + chạy bot | thêm `bin` vào PATH hoặc đặt `JAVA_HOME` |
| Python 3.8+ | mọi lệnh `.cmd`, monitor | bản python.org, tick "Add to PATH" (dùng `py -3` nếu có) |
| Git for Windows | chỉ lệnh **build** | build chạy chính `build.sh` cũ bằng Git Bash (cần `bash`, `sed`, `find`, `patch`) |
| `psutil` (tùy chọn) | xác minh PID đúng worker, CPU/RAM | `py -3 -m pip install psutil`; không có thì dùng Win32 API |

Git Bash tự tìm theo `git.exe` hoặc `C:\Program Files\Git\bin\bash.exe`; đường khác thì
`set NSO_BASH=D:\Git\bin\bash.exe`. Không dùng `bash` của WSL.

### Xuống dòng (quan trọng)

Git for Windows mặc định `core.autocrlf=true` đổi file sang CRLF → bash báo `$'\r': command not found`
và patch uplevel35 không khớp. Repo đã có `.gitattributes` giữ nguyên LF cho `*.sh *.java *.patch *.py`.
Nếu đã clone trước khi có file này:

```bat
git config core.autocrlf false
git rm -r --cached -q .
git reset --hard
```

(Copy thư mục từ Ubuntu sang thì không bị.) Lệnh build tự báo lỗi nếu `build.sh` còn CRLF.

## Lệnh (chạy trong cmd/PowerShell tại thư mục repo)

```bat
rem ===== uplevel (lv 1 -> 25, account-as20.csv) =====
uplevel\scripts\build-workers.cmd 4
uplevel\scripts\start-workers.cmd --mode 1
uplevel\scripts\status-workers.cmd
uplevel\scripts\logs-workers.cmd 1
uplevel\scripts\stop-workers.cmd

rem ===== uplevel35 (lv 25 -> 35, account-35.csv) =====
uplevel35\scripts\build-workers.cmd 4
uplevel35\scripts\start-workers.cmd
uplevel35\scripts\start-workers.cmd 2 --debug
uplevel35\scripts\status-workers.cmd
uplevel35\scripts\logs-workers.cmd 1 --grep
uplevel35\scripts\logs-workers.cmd --errors
uplevel35\scripts\stop-workers.cmd
uplevel35\scripts\check-patches.cmd

rem ===== web monitor =====
uplevel_monitor\run.cmd            &rem http://127.0.0.1:8190
```

Biến môi trường đặt bằng `set` trước lệnh (cmd) hoặc `$env:X="..."` (PowerShell):

```bat
set UPLEVEL_SERVER=180.93.114.122:14444
set JAVA_OPTS=-Dnso.uplevel35.testmap=63
uplevel35\scripts\start-workers.cmd 1
```

Monitor: giống `run.sh`, nghe `0.0.0.0:8190` (lần đầu Windows Firewall hỏi cho phép Python; chỉ xem trên
máy này thì `set UPLEVEL_MONITOR_HOST=127.0.0.1`). Đổi cổng: `set UPLEVEL_MONITOR_PORT=8091`.

## Khác bản Linux ở đâu

| Linux (.sh) | Windows | Lý do |
|---|---|---|
| `-cp "$worker:$classes"` | `-cp worker;classes` (`os.pathsep`) | Java trên Windows tách classpath bằng `;` |
| `/proc/<pid>/cmdline`, `/proc/<pid>/stat` | psutil hoặc Win32 API (`uplevel_monitor/winproc.py`) | Windows không có `/proc` |
| `nohup worker-loop.sh ... &` | `python windows/nso_win.py _loop` chạy ẩn (CREATE_NO_WINDOW) | đóng cửa sổ cmd không làm chết worker |
| `kill -TERM` rồi `kill -KILL` | `taskkill /PID <loop> /T /F` (cả cây: loop + java) | Windows không có signal |
| `tail -f` | Python đọc theo dõi file, Ctrl+C để thoát | |
| stdout Java mặc định | thêm `-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 ...` | Windows mặc định cp1252 → log/acc tiếng Việt hỏng |
| `python3 - file <<EOF` trong build.sh | shim `windows/shims/python3` → python thật, `-X utf8`, `write_text` ghi LF | Windows thường không có `python3`, mặc định cp1252 + CRLF |
| `javac @sources.txt` | shim `windows/shims/javac` đổi `/c/...` → `C:/...` trong file @ | MSYS không đổi đường dẫn bên trong file |

Đường dẫn trong Java (`user.home`, `nso.nvhn.error.dir`) truyền dạng Windows (`C:\...`); Java tự xử lý.
`UplevelStatus.java` và `AccountAutoManager` đã có fallback `delete()` + `renameTo()` nên ghi
`status.properties` / tiến độ trên Windows vẫn chạy.

Đã kiểm tra trên Linux: build qua `nso_win.py` (đi qua shim) cho `build/` giống hệt `build.sh` gốc ở cả
uplevel và uplevel35; start/handoff mã 3/status/logs/stop và monitor chạy với java giả.
Nhánh Win32 API (ctypes, taskkill) chưa chạy thử trên máy Windows thật.
