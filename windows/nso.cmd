@echo off
rem Goi windows\nso_win.py bang Python (uu tien "py -3", sau do "python").
setlocal
set "PYTHONUTF8=1"
set "NSO_PY="
where py >nul 2>nul && set "NSO_PY=py -3"
if not defined NSO_PY (
    where python >nul 2>nul && set "NSO_PY=python"
)
if not defined NSO_PY (
    echo Khong tim thay Python 3. Cai tu https://www.python.org/downloads/ ^(tick "Add to PATH"^). 1>&2
    exit /b 1
)
%NSO_PY% "%~dp0nso_win.py" %*
exit /b %ERRORLEVEL%
