@echo off
set devnevExePath="C:\Program Files (x86)\Microsoft Visual Studio\2017\Professional\Common7\IDE\devenv.exe"
if not exist %devnevExePath% (
    @echo 未检测到Visual Studio 2007专业版的编译程序，脚本无法继续执行，被迫退出。请将devenv.exe路径修改为本机中实际的路径
    pause
    exit
)
set devnev="C:\Program Files (x86)\Microsoft Visual Studio\2017\Professional\Common7\IDE\devenv"
set toolsReleasePath=..\tools\release
set toolsSrcPath=..\tools\src

::GameManager
set GameManagerSlnPath=%toolsSrcPath%\GameManager\GameManager.sln
%devnev% %GameManagerSlnPath% /Build "Debug"

::RedisDataViewer
set RedisDataViewerSlnPath=%toolsSrcPath%\RedisDataViewer\RedisDataViewer.sln
%devnev% %RedisDataViewerSlnPath% /Build "Debug"

::TestServerFramework
set TestServerFrameworkSlnPath=%toolsSrcPath%\TestServerFramework\TestServerFramework.sln
%devnev% %TestServerFrameworkSlnPath% /Build "Debug"

@echo 编译完毕，按任意键退出
pause