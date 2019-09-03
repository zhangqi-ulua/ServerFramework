::生成的proto代码文件临时存在本目录下的temp文件夹中，故每次生成前先生成空的temp文件夹
::再将生成proto代码文件后拷贝到指定目录，最后再删除temp文件夹
rd/s/q temp
md temp
set JAVA_PROTOC=..\tools\3rd\protocGen\protoc.exe --java_out=temp\
set CS_PROTOC=..\tools\3rd\protocGen\protogen.exe -output_directory=temp\

%JAVA_PROTOC% rpc.proto
%JAVA_PROTOC% common.proto
%JAVA_PROTOC% base_battle.proto
%JAVA_PROTOC% local_server.proto
%JAVA_PROTOC% remote_server.proto
%JAVA_PROTOC% gm.proto

%CS_PROTOC% common.proto base_battle.proto local_server.proto remote_server.proto gm.proto
%CS_PROTOC% rpc.proto

@echo 生成完毕，拷贝到各自目录
@echo off
::拷贝gateway所需的proto
xcopy /y temp\org\zhangqi\proto\Rpc.java ..\gateway\src\main\java\org\zhangqi\proto\
xcopy /y temp\org\zhangqi\proto\Common.java ..\gateway\src\main\java\org\zhangqi\proto\
xcopy /y temp\org\zhangqi\proto\LocalServer.java ..\gateway\src\main\java\org\zhangqi\proto\
xcopy /y temp\org\zhangqi\proto\RemoteServer.java ..\gateway\src\main\java\org\zhangqi\proto\

::拷贝GM服务器所需的proto
xcopy /y temp\org\zhangqi\proto\Rpc.java ..\gm\src\main\java\org\zhangqi\proto\
xcopy /y temp\org\zhangqi\proto\Common.java ..\gm\src\main\java\org\zhangqi\proto\
xcopy /y temp\org\zhangqi\proto\LocalServer.java ..\gm\src\main\java\org\zhangqi\proto\
xcopy /y temp\org\zhangqi\proto\RemoteServer.java ..\gm\src\main\java\org\zhangqi\proto\
xcopy /y temp\org\zhangqi\proto\Gm.java ..\gm\src\main\java\org\zhangqi\proto\

::拷贝logicServer所需的proto
xcopy /y temp\org\zhangqi\proto\Rpc.java ..\logicServer\src\main\java\org\zhangqi\proto\
xcopy /y temp\org\zhangqi\proto\Common.java ..\logicServer\src\main\java\org\zhangqi\proto\
xcopy /y temp\org\zhangqi\proto\LocalServer.java ..\logicServer\src\main\java\org\zhangqi\proto\
xcopy /y temp\org\zhangqi\proto\RemoteServer.java ..\logicServer\src\main\java\org\zhangqi\proto\

::拷贝battleServer所需的proto
xcopy /y temp\org\zhangqi\proto\Rpc.java ..\battleServer\src\main\java\org\zhangqi\proto\
xcopy /y temp\org\zhangqi\proto\Common.java ..\battleServer\src\main\java\org\zhangqi\proto\
xcopy /y temp\org\zhangqi\proto\BaseBattle.java ..\battleServer\src\main\java\org\zhangqi\proto\
xcopy /y temp\org\zhangqi\proto\LocalServer.java ..\battleServer\src\main\java\org\zhangqi\proto\
xcopy /y temp\org\zhangqi\proto\RemoteServer.java ..\battleServer\src\main\java\org\zhangqi\proto\

::拷贝GM客户端所需的proto
xcopy /y temp\Common.cs ..\tools\src\GameManager\GameManager\proto\
xcopy /y temp\Gm.cs ..\tools\src\GameManager\GameManager\proto\

::拷贝TestServerFramework所需的proto
xcopy /y temp\Common.cs ..\tools\src\TestServerFramework\TestServerFramework\proto\
xcopy /y temp\Rpc.cs ..\tools\src\TestServerFramework\TestServerFramework\proto\
xcopy /y temp\BaseBattle.cs ..\tools\src\TestServerFramework\TestServerFramework\proto\

::拷贝RedisDataViewer所需的proto
xcopy /y temp\Common.cs ..\tools\src\RedisDataViewer\RedisDataViewer\proto\
xcopy /y temp\BaseBattle.cs ..\tools\src\RedisDataViewer\RedisDataViewer\proto\
xcopy /y temp\LocalServer.cs ..\tools\src\RedisDataViewer\RedisDataViewer\proto\
xcopy /y temp\RemoteServer.cs ..\tools\src\RedisDataViewer\RedisDataViewer\proto\
xcopy /y temp\Gm.cs ..\tools\src\RedisDataViewer\RedisDataViewer\proto\
xcopy /y temp\Rpc.cs ..\tools\src\RedisDataViewer\RedisDataViewer\proto\

rd/s/q temp

@echo 拷贝完毕
pause
