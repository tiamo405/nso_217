@echo off
setlocal
cd /d "%~dp0"

set /p WORKER_COUNT="Nhap so luong Worker muon chia (mac dinh 10): "
if "%WORKER_COUNT%"=="" set WORKER_COUNT=10

python "%~dp0win_manager.py" build-workers %WORKER_COUNT%
pause
