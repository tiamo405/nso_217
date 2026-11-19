@echo off
rem Ban Windows cua restart-workers.sh (xem windows\README.md).
call "%~dp0..\..\windows\py.cmd" "%~dp0..\windows\win_manager.py" "restart" %*
exit /b %ERRORLEVEL%
