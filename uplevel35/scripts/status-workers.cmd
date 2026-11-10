@echo off
rem Ban Windows cua status-workers.sh (xem windows\README.md).
call "%~dp0..\..\windows\nso.cmd" uplevel35 status-workers %*
exit /b %ERRORLEVEL%
