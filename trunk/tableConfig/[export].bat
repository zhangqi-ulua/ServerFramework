@echo off
rd/s/q temp
md temp

"../tools/release/XlsxToLua/XlsxToLua.exe" "../tableConfig" "-notExportLua" "-noClient" "-noLang" "-autoNameCsvClassParam(classNamePostfix=VO)" "-exportJavaClass($all)" "-exportJavaClassParam(exportPath=temp|package=org.zhangqi.tableConfig|import=|isUseDate=true|isGenerateConstructorWithoutFields=true|isGenerateConstructorWithAllFields=true)" "-exportJson($all)" "-exportJsonParam(exportPath=temp|extension=txt|isFormat=false|isExportJsonArrayFormat=false|isMapIncludeKeyColumnValue=true)" 

set errorLevel = %errorlevel%
if not errorLevel == 0 (
    @echo 导出失败，请修正错误后重新运行
    pause
    exit
) 

::拷贝到GM服务器
xcopy /y temp\*.java ..\gm\src\main\java\org\zhangqi\tableConfig\
xcopy /y temp\*.txt ..\gm\src\main\resources\tableConfig

::拷贝到logicServer
xcopy /y temp\*.java ..\logicServer\src\main\java\org\zhangqi\tableConfig\
xcopy /y temp\*.txt ..\logicServer\src\main\resources\tableConfig

::拷贝到battleServer
xcopy /y temp\*.java ..\battleServer\src\main\java\org\zhangqi\tableConfig\
xcopy /y temp\*.txt ..\battleServer\src\main\resources\tableConfig

rd/s/q temp
@echo 拷贝成功
pause