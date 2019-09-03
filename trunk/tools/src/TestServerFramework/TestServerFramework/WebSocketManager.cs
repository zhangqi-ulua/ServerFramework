using org.zhangqi.proto;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Timers;
using WebSocketSharp;

namespace TestServerFramework
{
    public delegate void OnReceiveMsgInvoker(ResponseMsg msg);
    public delegate void WebSocketEventInvoker(EventArgs e);

    public class ResponseMsg
    {
        public RpcNameEnum RpcName { get; set; }
        public RpcErrorCodeEnum ErrorCode { get; set; }
        public byte[] ProtoData { get; set; }
    }

    public class WebSocketManager
    {
        private static WebSocket ws;

        private static Timer heartbeatTimer;

        // 服务器返回消息对应的回调处理函数
        private static Dictionary<RpcNameEnum, OnReceiveMsgInvoker> rpcHandlerDict = new Dictionary<RpcNameEnum, OnReceiveMsgInvoker>();
        // 与服务器连接中断的回调处理函数
        private static HashSet<WebSocketEventInvoker> connectionCloseHandlerSet = new HashSet<WebSocketEventInvoker>();
        // 与服务器连接错误的回调处理函数
        private static HashSet<WebSocketEventInvoker> connectionErrorHandlerSet = new HashSet<WebSocketEventInvoker>();
        // 与服务器连接成功的回调处理函数
        private static HashSet<WebSocketEventInvoker> connectionOpenHandlerSet = new HashSet<WebSocketEventInvoker>();

        public static void InitWebSocket()
        {
            if (ws != null)
                ws.Close();

            ws = new WebSocket(AppValues.SERVER_URL);
            ws.OnMessage += (sender, e) =>
            {
                if (e.IsBinary)
                {
                    ResponseMsg msg = new ResponseMsg();
                    int rpcNum = IntEncodingUtil.ByteArrayToIntByBigEndian(e.RawData, 4);
                    RpcNameEnum rpcName = (RpcNameEnum)rpcNum;
                    msg.RpcName = rpcName;
                    int errorCode = IntEncodingUtil.ByteArrayToIntByBigEndian(e.RawData, 8);
                    RpcErrorCodeEnum rpcErrorCode = (RpcErrorCodeEnum)errorCode;
                    msg.ErrorCode = rpcErrorCode;
                    if (rpcErrorCode == RpcErrorCodeEnum.Ok)
                    {
                        int protoDataLength = e.RawData.Length - 12;
                        msg.ProtoData = new byte[protoDataLength];
                        Buffer.BlockCopy(e.RawData, 12, msg.ProtoData, 0, protoDataLength);
                    }
                    if (rpcHandlerDict.ContainsKey(rpcName))
                        rpcHandlerDict[rpcName](msg);
                }
                else
                    Debug.WriteLine("收到非二进制数据");
            };
            ws.OnOpen += (sender, e) =>
            {
                Debug.WriteLine("连接成功");
                // 开启定时心跳计时器，避免长时间空闲被服务器踢下线
                if (heartbeatTimer != null)
                {
                    heartbeatTimer.Close();
                    heartbeatTimer = null;
                }
                if (AppValues.HEARTBEAT_INTERVAL_MSEC > 0)
                {
                    heartbeatTimer = new Timer(AppValues.HEARTBEAT_INTERVAL_MSEC);
                    heartbeatTimer.Elapsed += new ElapsedEventHandler(OnHeartbeatTimer);
                    heartbeatTimer.AutoReset = true;
                    heartbeatTimer.Enabled = true;
                }

                foreach (var hander in connectionOpenHandlerSet)
                    hander(e);
            };
            ws.OnError += (sender, e) =>
            {
                Debug.WriteLine("发生错误：" + e.Message);
                foreach (var hander in connectionErrorHandlerSet)
                    hander(e);
            };
            ws.OnClose += (sender, e) =>
            {
                Debug.WriteLine("连接关闭");
                if (heartbeatTimer != null)
                {
                    heartbeatTimer.Close();
                    heartbeatTimer = null;
                }

                foreach (var hander in connectionCloseHandlerSet)
                    hander(e);
            };

            ws.Connect();
        }

        private static void OnHeartbeatTimer(object sender, ElapsedEventArgs e)
        {
            // ws.IsAlive的源码实现就是向服务器发Ping
            if (ws.IsAlive == false)
            {
                Debug.WriteLine("发心跳时检测到与服务器连接中断");
                if (heartbeatTimer != null)
                {
                    heartbeatTimer.Close();
                    heartbeatTimer = null;
                }
                ws.Close();
            }
        }

        public static void CloseWebSocket()
        {
            if (IsConnected() == true)
                ws.Close();
        }

        public static bool IsConnected()
        {
            // 注意：如果服务器主动执行channel.close()，WebSocketSharp并不会触发OnClose事件
            // 并且ws.IsConnected为true，只是ws.IsAlive为false
            return ws != null && ws.IsConnected && ws.IsAlive;
        }

        public static void SendMessage(RpcNameEnum rpcName, byte[] protoData, OnReceiveMsgInvoker handler)
        {
            if (handler != null)
                AddRpcHandler(rpcName, handler);

            int totalLength = protoData.Length + 12;
            int rpcNum = (int)rpcName;
            int errorCode = (int)RpcErrorCodeEnum.Ok;
            byte[] sendByteArray = new byte[totalLength];
            IntEncodingUtil.IntToByteArrayByBigEndian(totalLength).CopyTo(sendByteArray, 0);
            IntEncodingUtil.IntToByteArrayByBigEndian(rpcNum).CopyTo(sendByteArray, 4);
            IntEncodingUtil.IntToByteArrayByBigEndian(errorCode).CopyTo(sendByteArray, 8);
            protoData.CopyTo(sendByteArray, 12);

            ws.Send(sendByteArray);
        }

        public static void AddRpcHandler(RpcNameEnum rpcName, OnReceiveMsgInvoker handler)
        {
            if (rpcHandlerDict.ContainsKey(rpcName))
                rpcHandlerDict[rpcName] = handler;
            else
                rpcHandlerDict.Add(rpcName, handler);
        }

        public static void AddConnectionCloseHandler(WebSocketEventInvoker handler)
        {
            connectionCloseHandlerSet.Add(handler);
        }

        public static void AddConnectionErrorHandler(WebSocketEventInvoker handler)
        {
            connectionErrorHandlerSet.Add(handler);
        }

        public static void AddConnectionOpenHandler(WebSocketEventInvoker handler)
        {
            connectionOpenHandlerSet.Add(handler);
        }
    }

    public class IntEncodingUtil
    {
        // 以Big Endian方式将int转为byte[]
        public static byte[] IntToByteArrayByBigEndian(int value)
        {
            byte[] byteArray = new byte[4];
            byteArray[0] = (byte)((value >> 24) & 0xFF);
            byteArray[1] = (byte)((value >> 16) & 0xFF);
            byteArray[2] = (byte)((value >> 8) & 0xFF);
            byteArray[3] = (byte)(value & 0xFF);
            return byteArray;
        }

        public static int ByteArrayToIntByBigEndian(byte[] byteArray, int offset)
        {
            return (int)(((byteArray[offset] & 0xFF) << 24)
            | ((byteArray[offset + 1] & 0xFF) << 16)
            | ((byteArray[offset + 2] & 0xFF) << 8)
            | (byteArray[offset + 3] & 0xFF));
        }
    }
}
