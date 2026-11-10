@echo off
rem Ban Windows cua logs-workers.sh (xem windows\README.md).
call "%~dp0..\..\windows\nso.cmd" uplevel logs-workers %*
exit /b %ERRORLEVEL%
