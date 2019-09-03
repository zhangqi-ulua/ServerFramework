@echo off
cd /d %~dp0
start  redis-server.exe redis.windows.conf
