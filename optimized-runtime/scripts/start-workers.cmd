@echo off
rem Ban Windows cua start-workers.sh (xem windows\README.md).
call "%~dp0..\..\windows\py.cmd" "%~dp0..\windows\win_manager.py" "start" %*
exit /b %ERRORLEVEL%
