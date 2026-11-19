@echo off
rem Ban Windows cua build-optimized.sh (xem windows\README.md).
call "%~dp0..\windows\py.cmd" "%~dp0windows\win_manager.py" "build" %*
exit /b %ERRORLEVEL%
