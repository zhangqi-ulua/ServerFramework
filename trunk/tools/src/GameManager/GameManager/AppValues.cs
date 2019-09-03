using org.zhangqi.proto;

namespace GameManager
{
    public class AppValues
    {
        public const string GM_SERVER_URL = "ws://127.0.0.1:20001/websocket";
        // 发送心跳的间隔毫秒数（填0为不需要发心跳）
        public const double HEARTBEAT_INTERVAL_MSEC = 50000d;

        public static GmUserInfo GmUserInfo { get; set; }
    }
}
