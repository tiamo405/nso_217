@echo off
setlocal
chcp 65001 >nul 2>&1
set PYTHONIOENCODING=utf-8
set PYTHONUTF8=1
cd /d "%~dp0"

echo Dang dung tat ca cac Worker va Supervisor...
python "%~dp0win_manager.py" stop
pause
