@echo off
rem Ban Windows cua reset-completed-workers.sh (xem windows\README.md).
call "%~dp0..\..\windows\py.cmd" "%~dp0..\windows\win_manager.py" "reset" %*
exit /b %ERRORLEVEL%
