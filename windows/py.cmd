@echo off
rem Chay mot script Python bang Python 3 (uu tien "py -3", sau do "python").
rem   call windows\py.cmd <script.py> [tham so...]
setlocal
set "PYTHONUTF8=1"
set "PYTHONIOENCODING=utf-8:replace"
chcp 65001 >nul 2>&1
set "NSO_PY="
where py >nul 2>nul && set "NSO_PY=py -3"
if not defined NSO_PY (
    where python >nul 2>nul && set "NSO_PY=python"
)
if not defined NSO_PY (
    echo Khong tim thay Python 3. Cai tu https://www.python.org/downloads/ ^(tick "Add to PATH"^). 1>&2
    exit /b 1
)
%NSO_PY% %*
exit /b %ERRORLEVEL%
