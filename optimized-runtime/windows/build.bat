@echo off
setlocal
cd /d "%~dp0"

echo [1/3] Kiem tra moi truong Java ^& Python...
where javac >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [!] Khong tim thay 'javac'. Vui long cai JDK va them vao PATH hoac set JAVA_HOME.
    pause
    exit /b 1
)

where python >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [!] Khong tim thay 'python'. Vui long cai Python 3.8+ va them vao PATH.
    pause
    exit /b 1
)

echo [2/3] Dang bien dich Optimized Runtime cho Windows...
python "%~dp0win_manager.py" build

if %ERRORLEVEL% equ 0 (
    echo [3/3] Thanh cong!
) else (
    echo [!] Build that bai!
)
pause
