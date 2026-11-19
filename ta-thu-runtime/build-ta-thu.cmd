@echo off
rem Ban Windows cua build-ta-thu.sh (xem windows\README.md).
call "%~dp0..\windows\py.cmd" "%~dp0windows\ta_thu_manager.py" "build" %*
exit /b %ERRORLEVEL%
