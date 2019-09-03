@echo off
::本bat的功能是将gm、gateway、logicServer、battleServer中共用的代码进行同步更新

::以logicServer的dao包为基准，同步到gm、battleServer。gateway只需要LoadBalanceDAO、UserDAO
xcopy /s /e /y logicServer\src\main\java\org\zhangqi\dao gm\src\main\java\org\zhangqi\dao\
xcopy /s /e /y logicServer\src\main\java\org\zhangqi\dao battleServer\src\main\java\org\zhangqi\dao\
xcopy /y logicServer\src\main\java\org\zhangqi\dao\LoadBalanceDAO.java gateway\src\main\java\org\zhangqi\dao\
xcopy /y logicServer\src\main\java\org\zhangqi\dao\UserDAO.java gateway\src\main\java\org\zhangqi\dao\

::以logicServer的service包为基准，同步到gm、battleServer（排除MatchService）。gateway只需要LoadBalanceService、UserService
xcopy /s /e /y /EXCLUDE:exclude.txt logicServer\src\main\java\org\zhangqi\service gm\src\main\java\org\zhangqi\service\
xcopy /s /e /y /EXCLUDE:exclude.txt logicServer\src\main\java\org\zhangqi\service battleServer\src\main\java\org\zhangqi\service\
xcopy /y logicServer\src\main\java\org\zhangqi\service\LoadBalanceService.java gateway\src\main\java\org\zhangqi\service\
xcopy /y logicServer\src\main\java\org\zhangqi\service\UserService.java gateway\src\main\java\org\zhangqi\service\

::以logicServer的constants包为基准，同步到gm、gateway、battleServer
xcopy /s /e /y logicServer\src\main\java\org\zhangqi\constants gm\src\main\java\org\zhangqi\constants\
xcopy /s /e /y logicServer\src\main\java\org\zhangqi\constants gateway\src\main\java\org\zhangqi\constants\
xcopy /s /e /y logicServer\src\main\java\org\zhangqi\constants battleServer\src\main\java\org\zhangqi\constants\

::以logicServer的utils包为基准，同步到gm、gateway、battleServer
xcopy /s /e /y logicServer\src\main\java\org\zhangqi\utils gm\src\main\java\org\zhangqi\utils\
xcopy /s /e /y logicServer\src\main\java\org\zhangqi\utils gateway\src\main\java\org\zhangqi\utils\
xcopy /s /e /y logicServer\src\main\java\org\zhangqi\utils battleServer\src\main\java\org\zhangqi\utils\

::以logicServer的manager\TableConfigManager为基准，同步到gm、battleServer
xcopy /y logicServer\src\main\java\org\zhangqi\manager\TableConfigManager.java gm\src\main\java\org\zhangqi\manager\
xcopy /y logicServer\src\main\java\org\zhangqi\manager\TableConfigManager.java battleServer\src\main\java\org\zhangqi\manager\

::以logicServer的action\BaseMessageAction为基准，同步到battleServer
xcopy /y logicServer\src\main\java\org\zhangqi\action\BaseMessageAction.java battleServer\src\main\java\org\zhangqi\action\

::以logicServer的actor\BaseMessageActor为基准，同步到battleServer
xcopy /y logicServer\src\main\java\org\zhangqi\actor\BaseMessageActor.java battleServer\src\main\java\org\zhangqi\actor\

pause