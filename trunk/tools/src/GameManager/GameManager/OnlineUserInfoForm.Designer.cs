namespace GameManager
{
    partial class OnlineUserInfoForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.label1 = new System.Windows.Forms.Label();
            this.lblOnlineUserCount = new System.Windows.Forms.Label();
            this.dgvOnlineUserInfo = new System.Windows.Forms.DataGridView();
            this.dgvcUserId = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcChannel = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcUsername = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcNickname = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcUserActionState = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcSessionId = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcGatewayId = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcLogicServerId = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcBattleServerId = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcOnlineTime = new System.Windows.Forms.DataGridViewTextBoxColumn();
            ((System.ComponentModel.ISupportInitialize)(this.dgvOnlineUserInfo)).BeginInit();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(24, 25);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(127, 15);
            this.label1.TabIndex = 0;
            this.label1.Text = "已登录玩家数量：";
            // 
            // lblOnlineUserCount
            // 
            this.lblOnlineUserCount.AutoSize = true;
            this.lblOnlineUserCount.Location = new System.Drawing.Point(157, 25);
            this.lblOnlineUserCount.Name = "lblOnlineUserCount";
            this.lblOnlineUserCount.Size = new System.Drawing.Size(15, 15);
            this.lblOnlineUserCount.TabIndex = 1;
            this.lblOnlineUserCount.Text = "0";
            // 
            // dgvOnlineUserInfo
            // 
            this.dgvOnlineUserInfo.AllowUserToAddRows = false;
            this.dgvOnlineUserInfo.AllowUserToDeleteRows = false;
            this.dgvOnlineUserInfo.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.Fill;
            this.dgvOnlineUserInfo.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgvOnlineUserInfo.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvcUserId,
            this.dgvcChannel,
            this.dgvcUsername,
            this.dgvcNickname,
            this.dgvcUserActionState,
            this.dgvcSessionId,
            this.dgvcGatewayId,
            this.dgvcLogicServerId,
            this.dgvcBattleServerId,
            this.dgvcOnlineTime});
            this.dgvOnlineUserInfo.Location = new System.Drawing.Point(27, 58);
            this.dgvOnlineUserInfo.Name = "dgvOnlineUserInfo";
            this.dgvOnlineUserInfo.RowTemplate.Height = 27;
            this.dgvOnlineUserInfo.Size = new System.Drawing.Size(1147, 561);
            this.dgvOnlineUserInfo.TabIndex = 2;
            // 
            // dgvcUserId
            // 
            this.dgvcUserId.FillWeight = 80F;
            this.dgvcUserId.HeaderText = "UserId";
            this.dgvcUserId.Name = "dgvcUserId";
            this.dgvcUserId.ReadOnly = true;
            // 
            // dgvcChannel
            // 
            this.dgvcChannel.FillWeight = 80F;
            this.dgvcChannel.HeaderText = "渠道";
            this.dgvcChannel.Name = "dgvcChannel";
            this.dgvcChannel.ReadOnly = true;
            // 
            // dgvcUsername
            // 
            this.dgvcUsername.HeaderText = "Username";
            this.dgvcUsername.Name = "dgvcUsername";
            this.dgvcUsername.ReadOnly = true;
            // 
            // dgvcNickname
            // 
            this.dgvcNickname.HeaderText = "昵称";
            this.dgvcNickname.Name = "dgvcNickname";
            this.dgvcNickname.ReadOnly = true;
            // 
            // dgvcUserActionState
            // 
            this.dgvcUserActionState.FillWeight = 120F;
            this.dgvcUserActionState.HeaderText = "玩家行为";
            this.dgvcUserActionState.Name = "dgvcUserActionState";
            this.dgvcUserActionState.ReadOnly = true;
            // 
            // dgvcSessionId
            // 
            this.dgvcSessionId.FillWeight = 80F;
            this.dgvcSessionId.HeaderText = "SessionId";
            this.dgvcSessionId.Name = "dgvcSessionId";
            this.dgvcSessionId.ReadOnly = true;
            // 
            // dgvcGatewayId
            // 
            this.dgvcGatewayId.FillWeight = 80F;
            this.dgvcGatewayId.HeaderText = "所连Gateway";
            this.dgvcGatewayId.Name = "dgvcGatewayId";
            this.dgvcGatewayId.ReadOnly = true;
            // 
            // dgvcLogicServerId
            // 
            this.dgvcLogicServerId.FillWeight = 80F;
            this.dgvcLogicServerId.HeaderText = "所连LogicServer";
            this.dgvcLogicServerId.Name = "dgvcLogicServerId";
            this.dgvcLogicServerId.ReadOnly = true;
            // 
            // dgvcBattleServerId
            // 
            this.dgvcBattleServerId.FillWeight = 80F;
            this.dgvcBattleServerId.HeaderText = "所连BattleServer";
            this.dgvcBattleServerId.Name = "dgvcBattleServerId";
            this.dgvcBattleServerId.ReadOnly = true;
            // 
            // dgvcOnlineTime
            // 
            this.dgvcOnlineTime.FillWeight = 80F;
            this.dgvcOnlineTime.HeaderText = "在线时长";
            this.dgvcOnlineTime.Name = "dgvcOnlineTime";
            this.dgvcOnlineTime.ReadOnly = true;
            // 
            // OnlineUserInfoForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 15F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1197, 636);
            this.Controls.Add(this.dgvOnlineUserInfo);
            this.Controls.Add(this.lblOnlineUserCount);
            this.Controls.Add(this.label1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.Fixed3D;
            this.MaximizeBox = false;
            this.Name = "OnlineUserInfoForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
            this.Text = "在线玩家详情";
            ((System.ComponentModel.ISupportInitialize)(this.dgvOnlineUserInfo)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label lblOnlineUserCount;
        private System.Windows.Forms.DataGridView dgvOnlineUserInfo;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcUserId;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcChannel;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcUsername;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcNickname;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcUserActionState;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcSessionId;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcGatewayId;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcLogicServerId;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcBattleServerId;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcOnlineTime;
    }
}