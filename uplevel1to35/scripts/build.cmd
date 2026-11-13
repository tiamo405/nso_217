@echo off
rem Ban Windows cua build.sh (xem windows\README.md).
call "%~dp0..\..\windows\nso.cmd" uplevel1to35 build %*
exit /b %ERRORLEVEL%
