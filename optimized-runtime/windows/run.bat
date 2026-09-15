@echo off
setlocal enabledelayedexpansion
chcp 65001 >nul 2>&1
set PYTHONIOENCODING=utf-8:replace
set PYTHONUTF8=1

REM ========================================================
REM  NSO ULTRA-OPTIMIZED RUNTIME - WINDOWS CLI
REM ========================================================

cd /d "%~dp0"

REM Kiem tra python
where python >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [!] Khong tim thay Python trong PATH!
    echo Vui long cai dat Python 3.8 tro len va tich vao 'Add Python to PATH'.
    pause
    exit /b 1
)

REM Goi win_manager.py voi cac tham so truyen vao
python "%~dp0win_manager.py" %*

if %ERRORLEVEL% neq 0 (
    exit /b %ERRORLEVEL%
)
