@echo off
setlocal
chcp 65001 >nul 2>&1
set PYTHONIOENCODING=utf-8:replace
set PYTHONUTF8=1
cd /d "%~dp0"

python "%~dp0win_manager.py" supervise --delay 15 --interval 20
pause
