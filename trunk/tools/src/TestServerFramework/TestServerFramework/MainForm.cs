using org.zhangqi.proto;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using WebSocketSharp;

namespace TestServerFramework
{
    public delegate void OperateUiDelegate(Object obj);

    public partial class MainForm : Form
    {
        private LoginForm loginForm;
        private bool isFormClosing = false;
        private bool isReceivedForceOfflinePush = false;

        public MainForm()
        {
            InitializeComponent();

            // 为棋子按钮注册点击事件
            for (int i = 0; i < 9; ++i)
            {
                Button btn = this.grpBattleArena.Controls["btnCell_" + i] as Button;
                btn.Click += new System.EventHandler(this.OnCellButtonClick);
            }
        }

        private void MainForm_Load(object sender, EventArgs e)
        {
            // 注册连接打开的响应函数
            WebSocketManager.AddConnectionOpenHandler(OnConnectionOpen);
            // 注册连接中断的响应函数
            WebSocketManager.AddConnectionCloseHandler(OnConnectionClose);
            // 注册连接错误的响应函数
            WebSocketManager.AddConnectionErrorHandler(OnConnectionError);

            // 注册一些服务器会主动push的消息
            WebSocketManager.AddRpcHandler(RpcNameEnum.ForceOfflinePush, OnForceOfflinePushCallback);
            WebSocketManager.AddRpcHandler(RpcNameEnum.MatchResultPush, OnMatchResultPushCallback);
            WebSocketManager.AddRpcHandler(RpcNameEnum.BattleEventMsgListPush, OnBattleEventMsgListPushCallback);

            // 与服务器建立连接
            WebSocketManager.InitWebSocket();
            if (WebSocketManager.IsConnected() == false)
            {
                string tips = string.Format("无法连接服务器，请确保服务器地址为{0}，且处于开启状态", AppValues.SERVER_URL);
                MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }

            // 强制弹出登录界面
            loginForm = new LoginForm();
            loginForm.ShowDialog();
        }

        private void MainForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (e.CloseReason == CloseReason.UserClosing)
            {
                isFormClosing = true;
                WebSocketManager.CloseWebSocket();
            }
        }

        private void OnConnectionOpen(EventArgs e)
        {
            OperateUiDelegate delegateFunc = new OperateUiDelegate(DelegateOnConnectionOpen);
            this.Invoke(delegateFunc, e);
        }

        private void DelegateOnConnectionOpen(Object obj)
        {
        }

        private void OnConnectionClose(EventArgs e)
        {
            OperateUiDelegate delegateFunc = new OperateUiDelegate(DelegateOnConnectionClose);
            this.Invoke(delegateFunc, e);
        }

        private void DelegateOnConnectionClose(Object obj)
        {
            if (isReceivedForceOfflinePush == true)
            {
                isReceivedForceOfflinePush = false;
                return;
            }
            if (loginForm != null && loginForm.Visible == false && isFormClosing == false)
            {
                MessageBox.Show(this, "与服务器中断连接，请重新登录", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                loginForm.ShowDialog();
            }
        }

        private void OnConnectionError(EventArgs e)
        {
            OperateUiDelegate delegateFunc = new OperateUiDelegate(DelegateOnConnectionError);
            this.Invoke(delegateFunc, e);
        }

        private void DelegateOnConnectionError(Object obj)
        {
            ErrorEventArgs e = obj as ErrorEventArgs;
            AppValues.UserInfoBuilder = null;
            string tips = string.Format("连接发生错误，原因为：{0}", e.Message);
            MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }

        private void MainForm_Shown(object sender, EventArgs e)
        {
            if (AppValues.UserInfoBuilder == null)
                this.Text = "测试服务器框架工具 by 张齐 （未登录）";
            else
                this.Text = string.Format("测试服务器框架工具 by 张齐 （已登录用户：{0}）", AppValues.UserInfoBuilder.Username);

            RefreshUiForUserState();
            // 如果已经处于对战中，就要获取当前战场信息
            if (AppValues.UserInfoBuilder.UserState.ActionState == UserActionStateEnum.Playing)
            {
                GetBattleInfoRequest.Builder builder = GetBattleInfoRequest.CreateBuilder();
                WebSocketManager.SendMessage(RpcNameEnum.GetBattleInfo, builder.Build().ToByteArray(), OnGetBattleInfoCallback);
            }
        }

        private void OnForceOfflinePushCallback(ResponseMsg msg)
        {
            OperateUiDelegate delegateFunc = new OperateUiDelegate(DelegateOnForceOfflinePushCallback);
            this.Invoke(delegateFunc, msg);
        }

        private void DelegateOnForceOfflinePushCallback(Object obj)
        {
            isReceivedForceOfflinePush = true;
            ResponseMsg msg = obj as ResponseMsg;
            ForceOfflinePush forceOfflinePush = ForceOfflinePush.ParseFrom(msg.ProtoData);
            WebSocketManager.CloseWebSocket();
            string tips = string.Format("被服务器踢下线，原因为{0}\n请重新登录", forceOfflinePush.ForceOfflineReason);
            MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
            if (loginForm != null && loginForm.Visible == false)
                loginForm.ShowDialog();
        }

        private void OnMatchResultPushCallback(ResponseMsg msg)
        {
            OperateUiDelegate delegateFunc = new OperateUiDelegate(DelegateOnMatchResultPushCallback);
            this.Invoke(delegateFunc, msg);
        }

        private void DelegateOnMatchResultPushCallback(Object obj)
        {
            ResponseMsg msg = obj as ResponseMsg;
            MatchResultPush matchResultPush = MatchResultPush.ParseFrom(msg.ProtoData);
            if (matchResultPush.IsSuccess == false)
                MessageBox.Show(this, "匹配失败", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
            else
            {
                UserState.Builder userStateBuilder = AppValues.UserInfoBuilder.UserState.ToBuilder();
                userStateBuilder.SetActionState(UserActionStateEnum.Playing);
                userStateBuilder.SetBattleType(matchResultPush.BattleType);
                userStateBuilder.SetBattleId(matchResultPush.BattleId);
                AppValues.UserInfoBuilder.SetUserState(userStateBuilder);

                RefreshUiForUserState();

                // 请求获取战场信息
                GetBattleInfoRequest.Builder builder = GetBattleInfoRequest.CreateBuilder();
                WebSocketManager.SendMessage(RpcNameEnum.GetBattleInfo, builder.Build().ToByteArray(), OnGetBattleInfoCallback);
            }
        }

        private void OnGetBattleInfoCallback(ResponseMsg msg)
        {
            OperateUiDelegate delegateFunc = new OperateUiDelegate(DelegateOnGetBattleInfoCallback);
            this.Invoke(delegateFunc, msg);
        }

        private void DelegateOnGetBattleInfoCallback(Object obj)
        {
            ResponseMsg msg = obj as ResponseMsg;
            if (msg.ErrorCode == RpcErrorCodeEnum.Ok)
            {
                GetBattleInfoResponse resp = GetBattleInfoResponse.ParseFrom(msg.ProtoData);
                BattleInfo battleInfo = resp.BattleInfo;
                AppValues.CurrentTurnInfo = battleInfo.CurrentTurnInfo;
                AppValues.CellInfos = new List<int>(battleInfo.BattleCellInfoList);
                AppValues.BattleUserBriefInfos = new List<UserBriefInfo>(battleInfo.UserBriefInfosList);
                AppValues.LastEventNum = battleInfo.LastEventNum;
                RefreshUiForBattleInfo();
                // 如果自己没有确认准备开始游戏，需要准备开始
                if (battleInfo.NotReadyUserIdsList != null && battleInfo.NotReadyUserIdsList.Contains(AppValues.UserInfoBuilder.UserId))
                {
                    // 发出准备好开始对战的请求
                    ReadyToStartGameRequest.Builder builder = ReadyToStartGameRequest.CreateBuilder();
                    WebSocketManager.SendMessage(RpcNameEnum.ReadyToStartGame, builder.Build().ToByteArray(), OnReadyToStartGameCallback);
                }
            }
            else
            {
                string tips = string.Format("获取战场信息失败，errorCode = {0}", msg.ErrorCode);
                MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
        }

        private void OnBattleEventMsgListPushCallback(ResponseMsg msg)
        {
            OperateUiDelegate delegateFunc = new OperateUiDelegate(DelegateOnBattleEventMsgListPushCallback);
            this.Invoke(delegateFunc, msg);
        }

        private void DelegateOnBattleEventMsgListPushCallback(Object obj)
        {
            ResponseMsg msg = obj as ResponseMsg;
            BattleEventMsgListPush battleEventMsgListPush = BattleEventMsgListPush.ParseFrom(msg.ProtoData);
            foreach (EventMsg eventMsg in battleEventMsgListPush.EventMsgList.MsgListList)
                doOneEvent(eventMsg);
        }

        private void doOneEvent(EventMsg eventMsg)
        {
            AppValues.LastEventNum = eventMsg.EventNum;
            switch (eventMsg.EventType)
            {
                case EventTypeEnum.EventTypeStartTurn:
                    {
                        AppValues.CurrentTurnInfo = eventMsg.StartTurnEvent.CurrentTurnInfo;
                        RefreshUiForCurrentTurnInfo();
                        break;
                    }
                case EventTypeEnum.EventTypeEndTurn:
                    {
                        break;
                    }
                case EventTypeEnum.EventTypePlacePieces:
                    {
                        PlacePiecesEvent placePiecesEvent = eventMsg.PlacePiecesEvent;
                        int value = 0;
                        if (placePiecesEvent.UserId == AppValues.BattleUserBriefInfos[0].UserId)
                            value = 1;
                        else
                            value = 2;
                        AppValues.CellInfos[placePiecesEvent.Index] = value;
                        RefreshUiForOneCell(placePiecesEvent.Index);
                        break;
                    }
                case EventTypeEnum.EventTypeGameOver:
                    {
                        GameOverEvent gameOverEvent = eventMsg.GameOverEvent;
                        if (gameOverEvent.WinnerUserId == 0)
                            MessageBox.Show(this, "对战结束，平局了", "对战结束", MessageBoxButtons.OK, MessageBoxIcon.Information);
                        else
                        {
                            string tips = string.Format("对战结束，您{0}了！\n结束原因为：{1}", gameOverEvent.WinnerUserId == AppValues.UserInfoBuilder.UserId ? "赢" : "输", gameOverEvent.GameOverReason);
                            MessageBox.Show(this, tips, "对战结束", MessageBoxButtons.OK, MessageBoxIcon.Information);
                        }
                        AppValues.CellInfos.Clear();
                        AppValues.CurrentTurnInfo = null;
                        UserState.Builder userStateBuilder = AppValues.UserInfoBuilder.UserState.ToBuilder();
                        userStateBuilder.SetActionState(UserActionStateEnum.ActionNone);
                        userStateBuilder.ClearBattleType();
                        userStateBuilder.ClearBattleId();
                        AppValues.UserInfoBuilder.SetUserState(userStateBuilder);
                        RefreshUiForUserState();

                        break;
                    }
                default:
                    {
                        MessageBox.Show(this, "doOneEvent中收到未支持的EventType = " + eventMsg.EventType, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                        return;
                    }
            }
        }

        private void OnReadyToStartGameCallback(ResponseMsg msg)
        {
            OperateUiDelegate delegateFunc = new OperateUiDelegate(DelegateOnReadyToStartGameCallback);
            this.Invoke(delegateFunc, msg);
        }

        private void DelegateOnReadyToStartGameCallback(Object obj)
        {
            ResponseMsg msg = obj as ResponseMsg;
            if (msg.ErrorCode != RpcErrorCodeEnum.Ok)
            {
                string tips = string.Format("请求准备开始游戏失败，errorCode = {0}", msg.ErrorCode);
                MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
        }

        private void btnMatch_Click(object sender, EventArgs e)
        {
            MatchRequest.Builder builder = MatchRequest.CreateBuilder();
            builder.SetBattleType(BattleTypeEnum.BattleTypeTwoPlayer);
            WebSocketManager.SendMessage(RpcNameEnum.Match, builder.Build().ToByteArray(), OnMatchCallback);
        }

        private void OnMatchCallback(ResponseMsg msg)
        {
            OperateUiDelegate delegateFunc = new OperateUiDelegate(DelegateOnMatchCallback);
            this.Invoke(delegateFunc, msg);
        }

        private void DelegateOnMatchCallback(Object obj)
        {
            ResponseMsg msg = obj as ResponseMsg;
            if (msg.ErrorCode == RpcErrorCodeEnum.Ok)
            {
                UserState.Builder userStateBuilder = AppValues.UserInfoBuilder.UserState.ToBuilder();
                userStateBuilder.SetActionState(UserActionStateEnum.Matching);
                userStateBuilder.SetBattleType(BattleTypeEnum.BattleTypeTwoPlayer);
                AppValues.UserInfoBuilder.SetUserState(userStateBuilder);

                RefreshUiForUserState();
            }
            else
            {
                string tips = string.Format("匹配失败，errorCode = {0}", msg.ErrorCode);
                MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
        }

        private void btnCancelMatch_Click(object sender, EventArgs e)
        {
            CancelMatchRequest.Builder builder = CancelMatchRequest.CreateBuilder();
            WebSocketManager.SendMessage(RpcNameEnum.CancelMatch, builder.Build().ToByteArray(), OnCancelMatchCallback);
        }

        private void OnCancelMatchCallback(ResponseMsg msg)
        {
            OperateUiDelegate delegateFunc = new OperateUiDelegate(DelegateOnCancelMatchCallback);
            this.Invoke(delegateFunc, msg);
        }

        private void DelegateOnCancelMatchCallback(Object obj)
        {
            ResponseMsg msg = obj as ResponseMsg;
            if (msg.ErrorCode == RpcErrorCodeEnum.Ok)
            {
                UserState.Builder userStateBuilder = AppValues.UserInfoBuilder.UserState.ToBuilder();
                userStateBuilder.SetActionState(UserActionStateEnum.ActionNone);
                AppValues.UserInfoBuilder.SetUserState(userStateBuilder);

                RefreshUiForUserState();
            }
            else
            {
                string tips = string.Format("取消匹配失败，errorCode = {0}", msg.ErrorCode);
                MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
        }

        private void RefreshUiForBattleInfo()
        {
            // 先后手
            if (AppValues.BattleUserBriefInfos[0].UserId == AppValues.UserInfoBuilder.UserId)
                lblPlaySeqTips.Text = "您是先手，棋子为×";
            else
                lblPlaySeqTips.Text = "您是后手，棋子为〇";

            // 回合数
            RefreshUiForCurrentTurnInfo();
            // 棋盘
            RefreshUiForAllCells();
        }

        private void RefreshUiForCurrentTurnInfo()
        {
            StringBuilder sb = new StringBuilder();
            sb.Append("第").Append(AppValues.CurrentTurnInfo.TurnCount).Append("回合").AppendLine();
            if (AppValues.CurrentTurnInfo.UserId == AppValues.UserInfoBuilder.UserId)
                sb.Append("请您行动");
            else
                sb.Append("请您等待对手行动");

            lblCurrentTurnInfo.Text = sb.ToString();
        }

        private void RefreshUiForUserState()
        {
            UserState userState = AppValues.UserInfoBuilder.UserState;
            lblActionState.Text = userState.ActionState.ToString();
            if (userState.ActionState == UserActionStateEnum.Playing)
            {
                lblBattleId.Text = userState.BattleId;
                lblBattleType.Text = userState.BattleType.ToString();
                grpBattleArena.Visible = true;
                btnMatch.Enabled = false;
                btnCancelMatch.Enabled = false;
            }
            else if (userState.ActionState == UserActionStateEnum.Matching)
            {
                lblBattleId.Text = string.Empty;
                lblBattleType.Text = userState.BattleType.ToString();
                grpBattleArena.Visible = false;
                btnMatch.Enabled = false;
                btnCancelMatch.Enabled = true;
            }
            else
            {
                lblBattleId.Text = string.Empty;
                lblBattleType.Text = string.Empty;
                grpBattleArena.Visible = false;
                btnMatch.Enabled = true;
                btnCancelMatch.Enabled = false;
            }
        }

        private void RefreshUiForAllCells()
        {
            for (int i = 0; i < 9; ++i)
                RefreshUiForOneCell(i);
        }

        private void RefreshUiForOneCell(int cellIndex)
        {
            int value = AppValues.CellInfos[cellIndex];
            Button btn = this.grpBattleArena.Controls["btnCell_" + cellIndex] as Button;
            btn.Enabled = (value == 0);
            if (value == 1)
                btn.Text = "×";
            else if (value == 2)
                btn.Text = "〇";
            else
                btn.Text = string.Empty;
        }

        private void OnCellButtonClick(object sender, EventArgs e)
        {
            Button btn = sender as Button;
            int underlineIndex = btn.Name.LastIndexOf('_');
            int index = int.Parse(btn.Name.Substring(underlineIndex + 1, btn.Name.Length - underlineIndex - 1));
            // 校验是不是自己的回合
            if (AppValues.CurrentTurnInfo.UserId != AppValues.UserInfoBuilder.UserId)
            {
                MessageBox.Show(this, "当前是对方回合", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            // 校验该位置是否没有放置棋子
            if (AppValues.CellInfos[index] != 0)
            {
                MessageBox.Show(this, "该位置已放置棋子", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            PlacePiecesRequest.Builder builder = PlacePiecesRequest.CreateBuilder();
            builder.SetLastEventNum(AppValues.LastEventNum);
            builder.SetIndex(index);
            WebSocketManager.SendMessage(RpcNameEnum.PlacePieces, builder.Build().ToByteArray(), OnPlacePiecesCallback);
        }

        private void OnPlacePiecesCallback(ResponseMsg msg)
        {
            OperateUiDelegate delegateFunc = new OperateUiDelegate(DelegateOnPlacePiecesCallback);
            this.Invoke(delegateFunc, msg);
        }

        private void DelegateOnPlacePiecesCallback(Object obj)
        {
            ResponseMsg msg = obj as ResponseMsg;
            if (msg.ErrorCode == RpcErrorCodeEnum.Ok)
            {
                PlacePiecesResponse resp = PlacePiecesResponse.ParseFrom(msg.ProtoData);
                foreach (EventMsg eventMsg in resp.EventList.MsgListList)
                    doOneEvent(eventMsg);
            }
            else
            {
                string tips = string.Format("落子操作错误，errorCode = {0}", msg.ErrorCode);
                MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
        }
    }
}
