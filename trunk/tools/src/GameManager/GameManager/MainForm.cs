using org.zhangqi.proto;
using System;
using System.Collections.Generic;
using System.Windows.Forms;
using WebSocketSharp;

namespace GameManager
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
            WebSocketManager.AddRpcHandler(GmRpcNameEnum.GmRpcForceOfflinePush, OnForceOfflinePushCallback);
            WebSocketManager.AddRpcHandler(GmRpcNameEnum.GmRpcTextMsgPush, OnTextMsgPushCallback);

            // 与GM服务器建立连接
            WebSocketManager.InitWebSocket();
            if (WebSocketManager.IsConnected() == false)
            {
                string tips = string.Format("无法连接GM服务器，请确保服务器地址为{0}，且处于开启状态", AppValues.GM_SERVER_URL);
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
                MessageBox.Show(this, "与GM服务器中断连接，请重新登录", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
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
            AppValues.GmUserInfo = null;
            string tips = string.Format("连接发生错误，原因为：{0}", e.Message);
            MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }

        private void MainForm_Shown(object sender, EventArgs e)
        {
            if (AppValues.GmUserInfo == null)
                this.Text = "GameManager by 张齐 （未登录）";
            else
                this.Text = string.Format("GameManager by 张齐 （已登录GM用户：{0}）", AppValues.GmUserInfo.Username);
        }

        private void AppendTextToConsole(string text)
        {
            // 让富文本框获取焦点
            rtxConsole.Focus();
            // 设置光标的位置到文本结尾
            rtxConsole.Select(rtxConsole.TextLength, 0);
            // 滚动到富文本框光标处
            rtxConsole.ScrollToCaret();
            // 追加内容
            rtxConsole.AppendText(text);
            // 换行
            rtxConsole.AppendText(Environment.NewLine);
        }

        private void OnForceOfflinePushCallback(GmResponseMsg msg)
        {
            OperateUiDelegate delegateFunc = new OperateUiDelegate(DelegateOnForceOfflinePushCallback);
            this.Invoke(delegateFunc, msg);
        }

        private void DelegateOnForceOfflinePushCallback(Object obj)
        {
            isReceivedForceOfflinePush = true;
            GmResponseMsg msg = obj as GmResponseMsg;
            GmForceOfflinePush forceOfflinePush = GmForceOfflinePush.ParseFrom(msg.ProtoData);
            WebSocketManager.CloseWebSocket();
            string tips = string.Format("被GM服务器踢下线，原因为{0}\n请重新登录", forceOfflinePush.ForceOfflineReason);
            MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
            if (loginForm != null && loginForm.Visible == false)
                loginForm.ShowDialog();
        }

        private void OnTextMsgPushCallback(GmResponseMsg msg)
        {
            OperateUiDelegate delegateFunc = new OperateUiDelegate(DelegateOnTextMsgPushCallback);
            this.Invoke(delegateFunc, msg);
        }

        private void DelegateOnTextMsgPushCallback(Object obj)
        {
            GmResponseMsg msg = obj as GmResponseMsg;
            GmTextMsgPush gmTextMsgPush = GmTextMsgPush.ParseFrom(msg.ProtoData);
            AppendTextToConsole(gmTextMsgPush.Text);
        }

        private void btnGetOnlineUserInfo_Click(object sender, EventArgs e)
        {
            GmGetOnlineUserInfoRequest.Builder builder = GmGetOnlineUserInfoRequest.CreateBuilder();
            WebSocketManager.SendMessage(GmRpcNameEnum.GmRpcGetOnlineUserInfo, builder.Build().ToByteArray(), OnGetOnlineUserInfoCallback);
        }

        private void OnGetOnlineUserInfoCallback(GmResponseMsg msg)
        {
            OperateUiDelegate delegateFunc = new OperateUiDelegate(DelegateOnGetOnlineUserInfoCallback);
            this.Invoke(delegateFunc, msg);
        }

        private void DelegateOnGetOnlineUserInfoCallback(Object obj)
        {
            GmResponseMsg msg = obj as GmResponseMsg;
            if (msg.ErrorCode == GmRpcErrorCodeEnum.GmRpcOk)
            {
                GmGetOnlineUserInfoResponse resp = GmGetOnlineUserInfoResponse.ParseFrom(msg.ProtoData);
                OnlineUserInfoForm form = new OnlineUserInfoForm(resp.OnlineUserInfosList);
                form.ShowDialog();
            }
            else
            {
                string tips = string.Format("获取在线玩家详情失败，errorCode = {0}", msg.ErrorCode);
                MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
        }

        private void btnRefreshServerState_Click(object sender, EventArgs e)
        {
            dgvLogicServerInfo.Rows.Clear();
            dgvBattleServerInfo.Rows.Clear();
            dgvGatewayInfo.Rows.Clear();
            GmGetAllServerStateRequest.Builder builder = GmGetAllServerStateRequest.CreateBuilder();
            WebSocketManager.SendMessage(GmRpcNameEnum.GmRpcGetAllServerState, builder.Build().ToByteArray(), OnGetAllServerStateCallback);
        }

        private void OnGetAllServerStateCallback(GmResponseMsg msg)
        {
            OperateUiDelegate delegateFunc = new OperateUiDelegate(DelegateOnGetAllServerStateCallback);
            this.Invoke(delegateFunc, msg);
        }

        private void DelegateOnGetAllServerStateCallback(Object obj)
        {
            GmResponseMsg msg = obj as GmResponseMsg;
            if (msg.ErrorCode == GmRpcErrorCodeEnum.GmRpcOk)
            {
                GmGetAllServerStateResponse resp = GmGetAllServerStateResponse.ParseFrom(msg.ProtoData);
                // logicServer
                lblLogicServerCount.Text = resp.LogicServerInfosCount.ToString();
                foreach (ServerLoadBalanceInfo info in resp.LogicServerInfosList)
                {
                    int addIndex = dgvLogicServerInfo.Rows.Add();
                    // ID
                    if (info.IsMainLogicServer == true)
                        dgvLogicServerInfo.Rows[addIndex].Cells[0].Value = info.Id + "(主)";
                    else
                        dgvLogicServerInfo.Rows[addIndex].Cells[0].Value = info.Id.ToString();
                    // IP
                    dgvLogicServerInfo.Rows[addIndex].Cells[1].Value = info.Ip;
                    // 负载
                    dgvLogicServerInfo.Rows[addIndex].Cells[2].Value = info.LoadBalance.ToString();
                }
                // battleServer
                lblBattleServerCount.Text = resp.BattleServerInfosCount.ToString();
                foreach (ServerLoadBalanceInfo info in resp.BattleServerInfosList)
                {
                    int addIndex = dgvBattleServerInfo.Rows.Add();
                    // ID
                    dgvBattleServerInfo.Rows[addIndex].Cells[0].Value = info.Id.ToString();
                    // IP
                    dgvBattleServerInfo.Rows[addIndex].Cells[1].Value = info.Ip;
                    // 负载
                    dgvBattleServerInfo.Rows[addIndex].Cells[2].Value = info.LoadBalance.ToString();
                }
                // gateway
                lblGatewayCount.Text = resp.GatewayInfosCount.ToString();
                foreach (ServerLoadBalanceInfo info in resp.GatewayInfosList)
                {
                    int addIndex = dgvGatewayInfo.Rows.Add();
                    // ID
                    dgvGatewayInfo.Rows[addIndex].Cells[0].Value = info.Id.ToString();
                    // IP
                    dgvGatewayInfo.Rows[addIndex].Cells[1].Value = info.Ip;
                    // 客户端连接地址
                    dgvGatewayInfo.Rows[addIndex].Cells[2].Value = info.GatewayConnectPath;
                    // 负载
                    dgvGatewayInfo.Rows[addIndex].Cells[3].Value = info.LoadBalance.ToString();
                }
            }
            else
            {
                string tips = string.Format("获取所有服务器状态失败，errorCode = {0}", msg.ErrorCode);
                MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
        }

        private void OnExecuteCmdCallback(GmResponseMsg msg)
        {
            OperateUiDelegate delegateFunc = new OperateUiDelegate(DelegateOnExecuteCmdCallback);
            this.Invoke(delegateFunc, msg);
        }

        private void DelegateOnExecuteCmdCallback(Object obj)
        {
            GmResponseMsg msg = obj as GmResponseMsg;
            if (msg.ErrorCode != GmRpcErrorCodeEnum.GmRpcOk)
            {
                string tips = string.Format("执行GM命令失败，errorCode = {0}", msg.ErrorCode);
                MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
        }

        private void dgvLogicServerInfo_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            if (e.RowIndex >= 0)
            {
                if (dgvLogicServerInfo.Columns[e.ColumnIndex].Name == "dgvcLogicServerReloadTableConfig" || dgvLogicServerInfo.Columns[e.ColumnIndex].Name == "dgvcLogicServerCloseServer")
                {
                    string idStr = dgvLogicServerInfo.Rows[e.RowIndex].Cells[0].Value.ToString();
                    int leftBracketIndex = idStr.IndexOf('(');
                    if (leftBracketIndex != -1)
                        idStr = idStr.Substring(0, leftBracketIndex);

                    int serverId = int.Parse(idStr);

                    GmExecuteCmdRequest.Builder builder = GmExecuteCmdRequest.CreateBuilder();
                    builder.SetServerType(RemoteServerTypeEnum.ServerTypeLogic);
                    builder.AddServerIds(serverId);

                    if (dgvLogicServerInfo.Columns[e.ColumnIndex].Name == "dgvcLogicServerReloadTableConfig")
                    {
                        string tips = string.Format("确定要重载id为{0}的Logic服务器表格吗？", serverId);
                        if (MessageBox.Show(this, tips, "警告", MessageBoxButtons.OKCancel, MessageBoxIcon.Warning) != DialogResult.OK)
                            return;

                        builder.SetCmdType(GmCmdTypeEnum.GmCmdReloadTableConfig);
                    }
                    else if (dgvLogicServerInfo.Columns[e.ColumnIndex].Name == "dgvcLogicServerCloseServer")
                    {
                        string tips = string.Format("确定要关闭id为{0}的Logic服务器吗？", serverId);
                        if (MessageBox.Show(this, tips, "警告", MessageBoxButtons.OKCancel, MessageBoxIcon.Warning) != DialogResult.OK)
                            return;

                        builder.SetCmdType(GmCmdTypeEnum.GmCmdCloseServer);
                    }

                    WebSocketManager.SendMessage(GmRpcNameEnum.GmRpcExecuteCmd, builder.Build().ToByteArray(), OnExecuteCmdCallback);
                }
            }
        }

        private void dgvBattleServerInfo_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            if (e.RowIndex >= 0)
            {
                if (dgvBattleServerInfo.Columns[e.ColumnIndex].Name == "dgvcBattleServerReloadTableConfig" || dgvBattleServerInfo.Columns[e.ColumnIndex].Name == "dgvcBattleServerCloseServer")
                {
                    string idStr = dgvBattleServerInfo.Rows[e.RowIndex].Cells[0].Value.ToString();
                    int serverId = int.Parse(idStr);

                    GmExecuteCmdRequest.Builder builder = GmExecuteCmdRequest.CreateBuilder();
                    builder.SetServerType(RemoteServerTypeEnum.ServerTypeBattle);
                    builder.AddServerIds(serverId);

                    if (dgvBattleServerInfo.Columns[e.ColumnIndex].Name == "dgvcBattleServerReloadTableConfig")
                    {
                        string tips = string.Format("确定要重载id为{0}的Battle服务器表格吗？", serverId);
                        if (MessageBox.Show(this, tips, "警告", MessageBoxButtons.OKCancel, MessageBoxIcon.Warning) != DialogResult.OK)
                            return;

                        builder.SetCmdType(GmCmdTypeEnum.GmCmdReloadTableConfig);
                    }
                    else if (dgvBattleServerInfo.Columns[e.ColumnIndex].Name == "dgvcBattleServerCloseServer")
                    {
                        string tips = string.Format("确定要关闭id为{0}的Battle服务器吗？", serverId);
                        if (MessageBox.Show(this, tips, "警告", MessageBoxButtons.OKCancel, MessageBoxIcon.Warning) != DialogResult.OK)
                            return;

                        builder.SetCmdType(GmCmdTypeEnum.GmCmdCloseServer);
                    }

                    WebSocketManager.SendMessage(GmRpcNameEnum.GmRpcExecuteCmd, builder.Build().ToByteArray(), OnExecuteCmdCallback);
                }
            }
        }

        private void dgvGatewayInfo_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            if (e.RowIndex >= 0)
            {
                if (dgvGatewayInfo.Columns[e.ColumnIndex].Name == "dgvcGatewayCloseServer")
                {
                    string idStr = dgvGatewayInfo.Rows[e.RowIndex].Cells[0].Value.ToString();
                    int serverId = int.Parse(idStr);

                    string tips = string.Format("确定要关闭id为{0}的Gateway服务器吗？", serverId);
                    if (MessageBox.Show(this, tips, "警告", MessageBoxButtons.OKCancel, MessageBoxIcon.Warning) != DialogResult.OK)
                        return;

                    GmExecuteCmdRequest.Builder builder = GmExecuteCmdRequest.CreateBuilder();
                    builder.SetServerType(RemoteServerTypeEnum.ServerTypeGateway);
                    builder.AddServerIds(serverId);
                    builder.SetCmdType(GmCmdTypeEnum.GmCmdCloseServer);

                    WebSocketManager.SendMessage(GmRpcNameEnum.GmRpcExecuteCmd, builder.Build().ToByteArray(), OnExecuteCmdCallback);
                }
            }
        }

        private void btnLogicServerReloadTableConfig_Click(object sender, EventArgs e)
        {
            if (MessageBox.Show(this, "确定要重载所有Logic服务器的表格吗？", "警告", MessageBoxButtons.OKCancel, MessageBoxIcon.Warning) != DialogResult.OK)
                return;

            GmExecuteCmdRequest.Builder builder = GmExecuteCmdRequest.CreateBuilder();
            builder.SetServerType(RemoteServerTypeEnum.ServerTypeLogic);
            builder.SetCmdType(GmCmdTypeEnum.GmCmdReloadTableConfig);
            WebSocketManager.SendMessage(GmRpcNameEnum.GmRpcExecuteCmd, builder.Build().ToByteArray(), OnExecuteCmdCallback);
        }

        private void btnLogicServerCloseServer_Click(object sender, EventArgs e)
        {
            if (MessageBox.Show(this, "确定要关闭所有Logic服务器吗？", "警告", MessageBoxButtons.OKCancel, MessageBoxIcon.Warning) != DialogResult.OK)
                return;

            GmExecuteCmdRequest.Builder builder = GmExecuteCmdRequest.CreateBuilder();
            builder.SetServerType(RemoteServerTypeEnum.ServerTypeLogic);
            builder.SetCmdType(GmCmdTypeEnum.GmCmdCloseServer);
            WebSocketManager.SendMessage(GmRpcNameEnum.GmRpcExecuteCmd, builder.Build().ToByteArray(), OnExecuteCmdCallback);
        }

        private void btnBattleServerReloadTableConfig_Click(object sender, EventArgs e)
        {
            if (MessageBox.Show(this, "确定要重载所有Battle服务器的表格吗？", "警告", MessageBoxButtons.OKCancel, MessageBoxIcon.Warning) != DialogResult.OK)
                return;

            GmExecuteCmdRequest.Builder builder = GmExecuteCmdRequest.CreateBuilder();
            builder.SetServerType(RemoteServerTypeEnum.ServerTypeBattle);
            builder.SetCmdType(GmCmdTypeEnum.GmCmdReloadTableConfig);
            WebSocketManager.SendMessage(GmRpcNameEnum.GmRpcExecuteCmd, builder.Build().ToByteArray(), OnExecuteCmdCallback);
        }

        private void btnBattleServerCloseServer_Click(object sender, EventArgs e)
        {
            if (MessageBox.Show(this, "确定要关闭所有Battle服务器吗？", "警告", MessageBoxButtons.OKCancel, MessageBoxIcon.Warning) != DialogResult.OK)
                return;

            GmExecuteCmdRequest.Builder builder = GmExecuteCmdRequest.CreateBuilder();
            builder.SetServerType(RemoteServerTypeEnum.ServerTypeBattle);
            builder.SetCmdType(GmCmdTypeEnum.GmCmdCloseServer);
            WebSocketManager.SendMessage(GmRpcNameEnum.GmRpcExecuteCmd, builder.Build().ToByteArray(), OnExecuteCmdCallback);
        }

        private void btnGatewayCloseServer_Click(object sender, EventArgs e)
        {
            if (MessageBox.Show(this, "确定要关闭所有Gateway服务器吗？", "警告", MessageBoxButtons.OKCancel, MessageBoxIcon.Warning) != DialogResult.OK)
                return;

            GmExecuteCmdRequest.Builder builder = GmExecuteCmdRequest.CreateBuilder();
            builder.SetServerType(RemoteServerTypeEnum.ServerTypeGateway);
            builder.SetCmdType(GmCmdTypeEnum.GmCmdCloseServer);
            WebSocketManager.SendMessage(GmRpcNameEnum.GmRpcExecuteCmd, builder.Build().ToByteArray(), OnExecuteCmdCallback);
        }

        private void btnGmServerReloadTableConfig_Click(object sender, EventArgs e)
        {
            if (MessageBox.Show(this, "确定要重载GM服务器的表格吗？", "警告", MessageBoxButtons.OKCancel, MessageBoxIcon.Warning) != DialogResult.OK)
                return;

            GmExecuteCmdRequest.Builder builder = GmExecuteCmdRequest.CreateBuilder();
            builder.SetServerType(RemoteServerTypeEnum.ServerTypeGm);
            builder.SetCmdType(GmCmdTypeEnum.GmCmdReloadTableConfig);
            WebSocketManager.SendMessage(GmRpcNameEnum.GmRpcExecuteCmd, builder.Build().ToByteArray(), OnExecuteCmdCallback);
        }

        private void btnGmServerCloseServer_Click(object sender, EventArgs e)
        {
            if (MessageBox.Show(this, "确定要关闭GM服务器吗？", "警告", MessageBoxButtons.OKCancel, MessageBoxIcon.Warning) != DialogResult.OK)
                return;

            GmExecuteCmdRequest.Builder builder = GmExecuteCmdRequest.CreateBuilder();
            builder.SetServerType(RemoteServerTypeEnum.ServerTypeGm);
            builder.SetCmdType(GmCmdTypeEnum.GmCmdCloseServer);
            WebSocketManager.SendMessage(GmRpcNameEnum.GmRpcExecuteCmd, builder.Build().ToByteArray(), OnExecuteCmdCallback);
        }
    }
}
