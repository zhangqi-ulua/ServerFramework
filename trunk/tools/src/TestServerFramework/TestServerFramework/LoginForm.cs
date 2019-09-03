using org.zhangqi.proto;
using System;
using System.Security.Cryptography;
using System.Windows.Forms;

namespace TestServerFramework
{
    public partial class LoginForm : Form
    {
        public LoginForm()
        {
            InitializeComponent();
        }

        private void LoginForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            // 用户点击关闭登录窗口，会强制将整个软件关闭
            // 注意：当登录成功，执行LoginForm的Hide方法时，仍旧会触发LoginFrom的closing和closed方法
            // 如果不加上这个判断，就会因为LoginForm的Hide，执行下面的逻辑，导致WebSocket被关闭
            if (e.CloseReason == CloseReason.UserClosing)
            {
                WebSocketManager.CloseWebSocket();
                Environment.Exit(0);
            }
        }

        private void btnLogin_Click(object sender, EventArgs e)
        {
            if (WebSocketManager.IsConnected() == false)
            {
                WebSocketManager.InitWebSocket();
                if (WebSocketManager.IsConnected() == false)
                {
                    string tips = string.Format("无法连接服务器，请确保服务器地址为{0}，且处于开启状态", AppValues.SERVER_URL);
                    MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }
            }

            string inputUsername = txtUsername.Text.Trim();
            string inputPassword = txtPassword.Text.Trim();
            if (string.IsNullOrEmpty(inputUsername))
            {
                MessageBox.Show("用户名不能为空", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            if (string.IsNullOrEmpty(inputPassword))
            {
                MessageBox.Show("密码不能为空", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            LoginRequest.Builder builder = LoginRequest.CreateBuilder();
            builder.SetUsername(inputUsername);
            MD5CryptoServiceProvider md5 = new MD5CryptoServiceProvider();
            string passwordMD5 = BitConverter.ToString(md5.ComputeHash(System.Text.Encoding.UTF8.GetBytes(inputPassword))).Replace("-", "").ToUpper();
            builder.SetPasswordMD5(passwordMD5);
            WebSocketManager.SendMessage(RpcNameEnum.Login, builder.Build().ToByteArray(), OnLoginCallback);
        }

        private void OnLoginCallback(ResponseMsg msg)
        {
            OperateUiDelegate delegateFunc = new OperateUiDelegate(DelegateOnLoginCallback);
            this.Invoke(delegateFunc, msg);
        }

        private void DelegateOnLoginCallback(Object obj)
        {
            ResponseMsg msg = obj as ResponseMsg;
            if (msg.ErrorCode == RpcErrorCodeEnum.Ok)
            {
                LoginResponse resp = LoginResponse.ParseFrom(msg.ProtoData);
                UserInfo userInfo = resp.UserInfo;
                AppValues.UserInfoBuilder = userInfo.ToBuilder();
                this.Hide();
            }
            else
            {
                string tips = string.Format("登录失败，errorCode = {0}", msg.ErrorCode);
                MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
        }

        private void btnRegist_Click(object sender, EventArgs e)
        {
            if (WebSocketManager.IsConnected() == false)
            {
                WebSocketManager.InitWebSocket();
                if (WebSocketManager.IsConnected() == false)
                {
                    string tips = string.Format("无法连接服务器，请确保服务器地址为{0}，且处于开启状态", AppValues.SERVER_URL);
                    MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }
            }

            string inputUsername = txtUsername.Text.Trim();
            string inputPassword = txtPassword.Text.Trim();
            if (string.IsNullOrEmpty(inputUsername))
            {
                MessageBox.Show("用户名不能为空", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            if (string.IsNullOrEmpty(inputPassword))
            {
                MessageBox.Show("密码不能为空", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            RegistRequest.Builder builder = RegistRequest.CreateBuilder();
            builder.SetUsername(inputUsername);
            builder.SetPassword(inputPassword);
            WebSocketManager.SendMessage(RpcNameEnum.Regist, builder.Build().ToByteArray(), OnRegistCallback);
        }

        private void OnRegistCallback(ResponseMsg msg)
        {
            OperateUiDelegate delegateFunc = new OperateUiDelegate(DelegateOnRegistCallback);
            this.Invoke(delegateFunc, msg);
        }

        private void DelegateOnRegistCallback(Object obj)
        {
            ResponseMsg msg = obj as ResponseMsg;
            if (msg.ErrorCode == RpcErrorCodeEnum.Ok)
            {
                MessageBox.Show(this, "注册成功，请您登录", "恭喜", MessageBoxButtons.OK, MessageBoxIcon.Information);
            }
            else
            {
                string tips = string.Format("登录失败，errorCode = {0}", msg.ErrorCode);
                MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
        }
    }
}
