@echo off
setlocal
cd /d "%~dp0"

python "%~dp0win_manager.py" supervise --delay 30 --interval 20
pause
