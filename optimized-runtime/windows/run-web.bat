@echo off
setlocal
chcp 65001 >nul 2>&1
set PYTHONIOENCODING=utf-8:replace
set PYTHONUTF8=1
cd /d "%~dp0\..\.."

echo ===========================================================
echo   NSO OPTIMIZED RUNTIME - WEB DASHBOARD (WINDOWS SERVER)
echo ===========================================================
echo.

echo [1/3] Kiem tra FastAPI, Uvicorn va psutil...
python -c "import fastapi, uvicorn, psutil" >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [!] Chua cai dat thu vien web. Dang cai dat tu requirements.txt...
    pip install -r web_control\requirements.txt
    if %ERRORLEVEL% neq 0 (
        echo [!] Cai dat that bai. Vui long kiem tra lai Python va pip tren VPS.
        pause
        exit /b 1
    )
)

set PORT=%NSO_WEB_PORT%
if "%PORT%"=="" set PORT=8080

echo [2/3] Khoi dong Web Control Windows tai port %PORT%...
echo -----------------------------------------------------------
echo   Truy cap tu trinh duyet tren VPS:   http://127.0.0.1:%PORT%
echo   Hoac truy cap qua IP VPS mang ngoai: http://0.0.0.0:%PORT%
echo -----------------------------------------------------------
echo Nhan Ctrl+C de dung server web.
echo.

python -m uvicorn web_control_win.app:app --host 0.0.0.0 --port %PORT% --workers 1
pause
