@echo off
rem Ban Windows cua stop-workers.sh (xem windows\README.md).
call "%~dp0..\..\windows\py.cmd" "%~dp0..\windows\ta_thu_manager.py" "stop" %*
exit /b %ERRORLEVEL%
