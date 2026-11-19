@echo off
rem Ban Windows cua build-workers.sh (xem windows\README.md).
call "%~dp0..\..\windows\py.cmd" "%~dp0..\windows\ta_thu_manager.py" "build-workers" %*
exit /b %ERRORLEVEL%
