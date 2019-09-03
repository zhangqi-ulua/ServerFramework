using org.zhangqi.proto;
using System.Collections.Generic;

namespace TestServerFramework
{
    public class AppValues
    {
        public const string SERVER_URL = "ws://127.0.0.1:10001/websocket";
        // 发送心跳的间隔毫秒数（填0为不需要发心跳）
        public const double HEARTBEAT_INTERVAL_MSEC = 50000d;

        public static UserInfo.Builder UserInfoBuilder { get; set; }

        public static List<int> CellInfos { get; set; }
        public static CurrentTurnInfo CurrentTurnInfo { get; set; }
        public static List<UserBriefInfo> BattleUserBriefInfos { get; set; }
        public static int LastEventNum { get; set; }
    }
}
