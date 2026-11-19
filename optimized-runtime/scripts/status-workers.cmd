@echo off
rem Ban Windows cua status-workers.sh (xem windows\README.md).
call "%~dp0..\..\windows\py.cmd" "%~dp0..\windows\win_manager.py" "status" %*
exit /b %ERRORLEVEL%
