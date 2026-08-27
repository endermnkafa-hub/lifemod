@echo off
cd /d "%~dp0"

title Auto GitHub Push

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0auto-push.ps1"

pause