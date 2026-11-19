@echo off
rem Ban Windows cua run-web-control.sh: Web Dashboard Windows (web_control_win), port NSO_WEB_PORT (mac dinh 8080).
call "%~dp0..\windows\run-web.bat" %*
exit /b %ERRORLEVEL%
