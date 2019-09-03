@echo off

set JAVA_HOME=%JAVA_HOME%
if "%JAVA_HOME%"=="" (
    color 04
    echo 错误：未配置环境变量JAVA_HOME，请配置后再运行
    pause
    exit
)

set MAVEN_HOME=%MAVEN_HOME%
if "%MAVEN_HOME%"=="" (
    color 04
    echo 错误：未配置环境变量MAVEN_HOME，请配置后再运行
    pause
    exit
)

set CLASSPATH=.;%JAVA_HOME%\lib\dt.jar;%JAVA_HOME%\lib\tools.jar
set Path=%JAVA_HOME%\bin;%JAVA_HOME%\jar\bin;%MAVEN_HOME%\bin

color 0E
echo 警告：请务必确保自己svn中gm、gateway、logicServer、battleServer是最新版本后再出包，确定后按任意键继续
pause
color 07

::先清理掉之前的
::echo 清理掉之前已打完的包
::rmdir /S /Q target\gm\target
::rmdir /S /Q target\logicServer\target
::rmdir /S /Q target\battleServer\target
::rmdir /S /Q target\gateway\target

echo 打包gm服务器
cd target\gm
start mvn package

echo 打包logicServer
cd ..\..\
cd target\logicServer
start mvn package

echo 打包battleServer
cd ..\..\
cd target\battleServer
start mvn package

echo 打包gateway
cd ..\..\
cd target\gateway
start mvn package

echo maven编译均已启动，请等待各类服务器编译成功后手工关闭各个窗口，注意查看是否编译成功
pause