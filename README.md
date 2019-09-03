# ServerFramework
## 它是什么？
是可用作全球同服或滚服的分布式Java游戏服务器框架，附带若干实用工具和游戏客户端。与客户端通讯使用Netty框架，采用WebSocket，适合手游和H5游戏（如微信小游戏），也可以简单修改转而使用Socket等。框架中的服务器类型分为GM服务器（GM）、逻辑服务器（LogicServer）、战斗服务器（BattleServer）、网关服务器（Gateway），服务器之间的通讯使用Akka框架，处理服务器并发问题，也是利用Akka框架的Actor模型思想
## 为什么要做它并开源它？
入行至今，很高兴能作为游戏制作人和后端主程序，做了两款原创玩法的策略竞技游戏[<b>《萌猪突击》</b>](https://www.taptap.com/app/150942)和[<b>《卡牌串串》</b>](https://www.taptap.com/app/174662)。我热爱需要动脑的游戏类型（比如CCG、DBG、解谜、桌游等），更喜欢创新最好是全新玩法的游戏。虽然行业的同质化现象越来越严重，抄袭屡见不鲜，“微创新”成为至理名言。但近年来，TapTap上涌现出越来越多的个人或几人的小团队，在不断推出原创玩法的新颖游戏。他们都有着做原创好游戏的理想，有些朋友甚至为了实现游戏，从完全不相干的行业中跳出来，从0开始自己学习编程来实现。但技术上毕竟是不能一蹴而就的，于是开测即炸服基本成了小团队发布游戏的现状，而炸服并不是由于玩家热情太高，涌入访客太大，很大程度上还是技术方面不过关，几百甚至几十的在线，都会出现服务器明显卡顿的问题。相比之下，程序bug相对成熟团队也多太多。所以，我想将我做这两款游戏时所用的服务器框架进行重新整理和开源，希望尽一份微薄之力，能帮助想做原创游戏，但被技术方面问题困扰的战友们。希望这个框架能简化服务器的开发，让大家更专注于游戏玩法的实现，而不是头疼于服务器框架的搭建
## 交流与反馈
我的QQ及对应邮箱是：2246549866@qq.com   我所有开源项目的反馈交流QQ群为：132108644  （含之前XlsxToLua、国际化工具等）<br/>
由于本人能力有限，如果您对这个框架有任何改进建议，期待您能不吝赐教
## 服务器框架介绍（其他细节参看doc目录下的“说明.txt”，之后会有更详细的Wiki）
### 服务器功能说明
#### GM服务器（GM）：
1、作为中心管理的服务器，其他各类服务器在启动后，需要连接GM服务器进行注册（类似自己实现了一个精简够用的ZooKeeper）。2、游戏客户端要连上游戏服务器时，先访问GM服务器，获取一个最空闲的Gateway服务器地址。3、GM客户端通过登录验证后，向GM服务器发送命令，由GM服务器管理其他指定服务器执行具体GM命令（比如对线上服务器进行表格更新、停服维护操作，对指定玩家或系统模块执行GM操作，修改各项数据等）
#### 逻辑服务器（LogicServer）：
处理游戏逻辑相关的请求，其中需要指定一台为主逻辑服务器，只有主逻辑服务器处理玩家注册、游戏对战匹配。其他逻辑服务器可以有若干台，支持分布式扩展
#### 战斗服务器（BattleServer）：
处理游戏对战相关的请求，可以有若干台，支持分布式扩展
#### 网关服务器（Gateway）：
由客户端进行连接，根据负载分配给玩家连接的逻辑服务器。将客户端请求根据类型分别发往对应逻辑服务器或战斗服务器，可以有若干台，支持分布式扩展
### 数据存储
因为在数据存储上不同开发者的使用习惯往往很不相同，不同类型游戏的数据存储需求也不同，本框架这一部分仅采用Redis作为中心数据库演示服务器框架的作用，各位开发者可根据具体需求进行替换
### 通讯协议
客户端与服务器间的二进制通讯数据包：<br/>
第1个4字节（1个int）为整个数据包的总字节数（含所有部分，包括这4字节）<br/>
第2个4字节为协议名对应的数字<br/>
第3个4字节为errorCode对应的数字<br/>
剩下的部分为要发送的具体protobuf协议转为的二进制内容<br/>
其中errorCode为客户端与服务器约定的已知错误码，如果客户端向服务器所发请求，经过服务器验证认为有误，服务器会返回对应错误码给客户端。客户端与服务器具体请求与回应或是推送的内容，通过protobuf协议进行定义，并最终转为字节流发送
### 提供的样例
目前框架中，除了实现各服务器间通讯协作的基本框架功能以外，还实现了一些具体功能为您提供参考<br/>
1、玩家注册、登录，通过主服务器进行对战匹配，2个玩家匹配成功后，进行回合制的实时PVP的井字棋对战<br/>
2、GM功能，控制各个服务器执行比如重载表格、关服维护等操作。也可以查看在线玩家详情
## 附带开源工具及客户端介绍
### RedisDataViewer：
虽然市面上有一些第三方的Redis可视化查询操作工具，但一是筛选功能比较弱，比如对ZSet类型，无法限定查询指定index范围内或者指定分数范围内的数据，二是对于存储的protobuf类型转为的二进制数据，没有提供进行反序列化为可读文本查看的功能。因此，我做了这个工具解决上面2个问题。对Hash、List、ZSet均提供具体查询功能，对protobuf序列化存储的数据，可以反序列化为可读文本查看<br/>
使用方法：在“redisKeyConfig.txt”配置文件中，根据格式要求（唯一id@分组（不分组则留空）@描述@key名格式@protobuf数据所对应的protobuf类名（非protobuf数据留空）@redis的数据类型（string、hash、list、set、zset）@备注信息）进行配置。就可以在打开工具时，选择这些配置对应的Redis数据进行查看。另外，默认配置文件中，已经将服务器框架中所用到的所有Redis数据都进行了配置登记，可以直接使用，也作为配置的实例供您参考<br/>
![](https://github.com/zhangqi-ulua/ServerFramework/blob/master/doc/screenshots/RedisDataViewer1.png)<br/>
![](https://github.com/zhangqi-ulua/ServerFramework/blob/master/doc/screenshots/RedisDataViewer2.png)<br/>
![](https://github.com/zhangqi-ulua/ServerFramework/blob/master/doc/screenshots/RedisDataViewer3.png)<br/>
### TestServerFramework（游戏客户端实现）
用C#.NET做的WinForm程序，实现了游戏客户端（井字棋）功能<br/>
![](https://github.com/zhangqi-ulua/ServerFramework/blob/master/doc/screenshots/TestServerFramework1.png)<br/>
### GameManager：
用C#.NET做的WinForm程序，实现了GM客户端，可管理服务器执行GM命令，查询在线玩家详情<br/>
![](https://github.com/zhangqi-ulua/ServerFramework/blob/master/doc/screenshots/GameManager1.png)<br/>
![](https://github.com/zhangqi-ulua/ServerFramework/blob/master/doc/screenshots/GameManager2.png)<br/>
### 自动编译打包及运行
在trunk\tools\release\AutoBuildServer中，执行“build.bat”可自动将所有类型的服务器通过Maven进行打包（需提前安装并配置好Maven的环境变量）执行“onekey run.bat”可自动启动Redis数据库并启动各个服务器<br/>
![](https://github.com/zhangqi-ulua/ServerFramework/blob/master/doc/screenshots/AutoBuildAndRun.png)<br/>
## 其他说明
### protobuf的编译
在trunk\proto中执行“compile.bat”，可编译服务器所需Java版本的protobuf以及客户端工具所需的C#版本。另外，通过“编译使用proto的工具.bat”，会调用Visual Studio将全部客户端工具重新编译为可执行程序
### 配置表格的使用
在trunk\tableConfig下放置所需的配置表格。导表工具使用我开源的[<b>XlsxToLua</b>](https://github.com/zhangqi-ulua/XlsxToLua)（具体使用方法请移步查看），导出路径和参数等已经做好了默认配置
