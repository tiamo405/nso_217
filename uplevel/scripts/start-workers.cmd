@echo off
rem Ban Windows cua start-workers.sh (xem windows\README.md).
call "%~dp0..\..\windows\nso.cmd" uplevel start-workers %*
exit /b %ERRORLEVEL%
