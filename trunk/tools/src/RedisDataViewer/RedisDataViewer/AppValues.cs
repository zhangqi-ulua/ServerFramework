using System;

namespace RedisDataViewer
{
    public class AppValues
    {
        public static string PROGRAM_FOLDER_PATH = AppDomain.CurrentDomain.SetupInformation.ApplicationBase;
        public const string PROTOBUF_PACKAGE_NAME = "org.zhangqi.proto.";
        public const string REDIS_KEY_CONFIG_FILE_NAME = "redisKeyConfig.txt";
        public const string FAVORITE_FILE_NAME = "favorite.txt";
        public const string HISTORY_FILE_NAME = "history.txt";

        public const string REDIS_CONNECT_IP = "127.0.0.1";
        public const int REDIS_CONNECT_PORT = 6379;
        public const string REDIS_CONNECT_PASSWORD = "foobared";
    }
}
