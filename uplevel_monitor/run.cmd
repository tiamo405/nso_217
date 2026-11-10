@echo off
rem Ban Windows cua run.sh: web monitor cho uplevel\workers va uplevel35\workers.
rem   uplevel_monitor\run.cmd                      -> http://127.0.0.1:8190 (nghe 0.0.0.0 nhu run.sh)
rem   set UPLEVEL_MONITOR_PORT=8091 ^& uplevel_monitor\run.cmd
setlocal
cd /d "%~dp0.."
set "PYTHONUTF8=1"
set "NSO_PY="
where py >nul 2>nul && set "NSO_PY=py -3"
if not defined NSO_PY (
    where python >nul 2>nul && set "NSO_PY=python"
)
if not defined NSO_PY (
    echo Khong tim thay Python 3. 1>&2
    exit /b 1
)
%NSO_PY% -m uplevel_monitor.server
exit /b %ERRORLEVEL%
