@echo off
rem Ban Windows cua logs-workers.sh (xem windows\README.md).
call "%~dp0..\..\windows\py.cmd" "%~dp0..\windows\win_manager.py" "logs" %*
exit /b %ERRORLEVEL%
