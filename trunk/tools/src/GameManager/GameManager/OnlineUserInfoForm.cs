using org.zhangqi.proto;
using System;
using System.Collections.Generic;
using System.Windows.Forms;

namespace GameManager
{
    public partial class OnlineUserInfoForm : Form
    {
        public OnlineUserInfoForm(IList<OnlineUserInfo> list)
        {
            InitializeComponent();

            ShowData(list);
        }

        private void ShowData(IList<OnlineUserInfo> list)
        {
            long currentTimestamp = (long)(DateTime.Now - TimeZone.CurrentTimeZone.ToLocalTime(new DateTime(1970, 1, 1, 0, 0, 0))).TotalMilliseconds;
            foreach (OnlineUserInfo oneInfo in list)
            {
                int addIndex = dgvOnlineUserInfo.Rows.Add();
                // userId
                dgvOnlineUserInfo.Rows[addIndex].Cells[0].Value = oneInfo.UserId.ToString();
                // 渠道
                dgvOnlineUserInfo.Rows[addIndex].Cells[1].Value = oneInfo.Channel.ToString();
                // username
                dgvOnlineUserInfo.Rows[addIndex].Cells[2].Value = oneInfo.HasUsername ? oneInfo.Username : string.Empty;
                // 昵称
                dgvOnlineUserInfo.Rows[addIndex].Cells[3].Value = oneInfo.HasNickname ? oneInfo.Nickname : string.Empty;
                // 玩家行为
                string userActionStateDesc = string.Empty;
                UserActionStateEnum userActionState = oneInfo.UserState.ActionState;
                if (userActionState == UserActionStateEnum.ActionNone)
                    userActionStateDesc = "无";
                else if (userActionState == UserActionStateEnum.Matching)
                {
                    if (oneInfo.UserState.BattleType == BattleTypeEnum.BattleTypeTwoPlayer)
                        userActionStateDesc = "匹配中（二人）";
                    else
                        userActionStateDesc = "匹配中";
                }
                else if (userActionState == UserActionStateEnum.Playing)
                {
                    if (oneInfo.UserState.BattleType == BattleTypeEnum.BattleTypeTwoPlayer)
                        userActionStateDesc = "对战中（二人）";
                    else
                        userActionStateDesc = "对战中";
                }
                dgvOnlineUserInfo.Rows[addIndex].Cells[4].Value = userActionStateDesc;
                // sessionId
                dgvOnlineUserInfo.Rows[addIndex].Cells[5].Value = oneInfo.SessionId.ToString();
                // 所连gatewayId
                dgvOnlineUserInfo.Rows[addIndex].Cells[6].Value = oneInfo.HasConnectGatewayId ? oneInfo.ConnectGatewayId.ToString() : string.Empty;
                // 所连logicServerId
                dgvOnlineUserInfo.Rows[addIndex].Cells[7].Value = oneInfo.HasConnectLogicServerId ? oneInfo.ConnectLogicServerId.ToString() : string.Empty;
                // 所连battleServerId
                dgvOnlineUserInfo.Rows[addIndex].Cells[8].Value = oneInfo.HasConnectBattleServerId ? oneInfo.ConnectBattleServerId.ToString() : string.Empty;
                // 在线时长
                int passSecond = (int)((currentTimestamp - oneInfo.LoginedTimestamp) / 1000);
                int temp = passSecond;
                int hour = passSecond / 60 / 60;
                temp = temp - hour * 60 * 60;
                int minute = temp / 60;
                temp = temp - minute * 60;
                int second = temp;
                string passSecondStr = string.Format("{0}:{1}:{2}", hour.ToString().PadLeft(2, '0'), minute.ToString().PadLeft(2, '0'), second.ToString().PadLeft(2, '0'));
                dgvOnlineUserInfo.Rows[addIndex].Cells[9].Value = passSecondStr;
            }
        }
    }
}
