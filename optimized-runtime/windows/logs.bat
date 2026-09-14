@echo off
setlocal
cd /d "%~dp0"

set /p WNUM="Nhap so worker muon xem log (vi du: 1, 2, hoac de trong de xem tong quat): "

if "%WNUM%"=="" (
    python "%~dp0win_manager.py" logs
) else (
    python "%~dp0win_manager.py" logs %WNUM%
)
pause
