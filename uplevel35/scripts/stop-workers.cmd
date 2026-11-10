@echo off
rem Ban Windows cua stop-workers.sh (xem windows\README.md).
call "%~dp0..\..\windows\nso.cmd" uplevel35 stop-workers %*
exit /b %ERRORLEVEL%
