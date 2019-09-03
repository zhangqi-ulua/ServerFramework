call "..\..\3rd\Redis-x64-3.2.100\start redis.bat"

cd /d %~dp0
start "GM" cmd /k call "run gm.bat"
TIMEOUT /T 5 /NOBREAK

start "Logic" cmd /k call "run logicServer.bat"
start "Battle" cmd /k call "run battleServer.bat"
start "Gateway" cmd /k call "run gateway.bat"