@echo off
rem Ban Windows cua check-patches.sh (xem windows\README.md).
call "%~dp0..\..\windows\nso.cmd" uplevel35 check-patches %*
exit /b %ERRORLEVEL%
