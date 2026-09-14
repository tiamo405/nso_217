@echo off
setlocal
cd /d "%~dp0"

echo Dang dung tat ca cac Worker va Supervisor...
python "%~dp0win_manager.py" stop
pause
