@echo off
rem Ban Windows cua build-workers.sh (xem windows\README.md).
call "%~dp0..\..\windows\nso.cmd" uplevel35 build-workers %*
exit /b %ERRORLEVEL%
