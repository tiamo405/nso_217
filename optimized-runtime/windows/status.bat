@echo off
setlocal
cd /d "%~dp0"

python "%~dp0win_manager.py" status
echo.
pause
