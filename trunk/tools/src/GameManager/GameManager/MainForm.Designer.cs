namespace GameManager
{
    partial class MainForm
    {
        /// <summary>
        /// 必需的设计器变量。
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// 清理所有正在使用的资源。
        /// </summary>
        /// <param name="disposing">如果应释放托管资源，为 true；否则为 false。</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows 窗体设计器生成的代码

        /// <summary>
        /// 设计器支持所需的方法 - 不要修改
        /// 使用代码编辑器修改此方法的内容。
        /// </summary>
        private void InitializeComponent()
        {
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle3 = new System.Windows.Forms.DataGridViewCellStyle();
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle4 = new System.Windows.Forms.DataGridViewCellStyle();
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle5 = new System.Windows.Forms.DataGridViewCellStyle();
            this.btnRefreshServerState = new System.Windows.Forms.Button();
            this.grpLogicServerManager = new System.Windows.Forms.GroupBox();
            this.btnLogicServerCloseServer = new System.Windows.Forms.Button();
            this.btnLogicServerReloadTableConfig = new System.Windows.Forms.Button();
            this.lblLogicServerCount = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.dgvLogicServerInfo = new System.Windows.Forms.DataGridView();
            this.grpBattleServerManager = new System.Windows.Forms.GroupBox();
            this.btnBattleServerCloseServer = new System.Windows.Forms.Button();
            this.dgvBattleServerInfo = new System.Windows.Forms.DataGridView();
            this.btnBattleServerReloadTableConfig = new System.Windows.Forms.Button();
            this.label4 = new System.Windows.Forms.Label();
            this.lblBattleServerCount = new System.Windows.Forms.Label();
            this.grpGatewayManager = new System.Windows.Forms.GroupBox();
            this.btnGatewayCloseServer = new System.Windows.Forms.Button();
            this.label3 = new System.Windows.Forms.Label();
            this.lblGatewayCount = new System.Windows.Forms.Label();
            this.dgvGatewayInfo = new System.Windows.Forms.DataGridView();
            this.rtxConsole = new System.Windows.Forms.RichTextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.grpGmServerManager = new System.Windows.Forms.GroupBox();
            this.btnGmServerReloadTableConfig = new System.Windows.Forms.Button();
            this.btnGmServerCloseServer = new System.Windows.Forms.Button();
            this.dgvcLogicServerId = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcLogicServerIp = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcLogicServerLoadBalance = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcLogicServerReloadTableConfig = new System.Windows.Forms.DataGridViewButtonColumn();
            this.dgvcLogicServerCloseServer = new System.Windows.Forms.DataGridViewButtonColumn();
            this.dgvcBattleServerId = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcBattleServerIp = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcBattleServerLoadBalance = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcBattleServerReloadTableConfig = new System.Windows.Forms.DataGridViewButtonColumn();
            this.dgvcBattleServerCloseServer = new System.Windows.Forms.DataGridViewButtonColumn();
            this.dgvcGatewayServerId = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcGatewayServerIp = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcGatewayClientConnectPath = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcGatewayLoadBalance = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.dgvcGatewayCloseServer = new System.Windows.Forms.DataGridViewButtonColumn();
            this.btnGetOnlineUserInfo = new System.Windows.Forms.Button();
            this.grpLogicServerManager.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvLogicServerInfo)).BeginInit();
            this.grpBattleServerManager.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvBattleServerInfo)).BeginInit();
            this.grpGatewayManager.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvGatewayInfo)).BeginInit();
            this.grpGmServerManager.SuspendLayout();
            this.SuspendLayout();
            // 
            // btnRefreshServerState
            // 
            this.btnRefreshServerState.Location = new System.Drawing.Point(12, 12);
            this.btnRefreshServerState.Name = "btnRefreshServerState";
            this.btnRefreshServerState.Size = new System.Drawing.Size(190, 28);
            this.btnRefreshServerState.TabIndex = 1;
            this.btnRefreshServerState.Text = "刷新各服务器状态";
            this.btnRefreshServerState.UseVisualStyleBackColor = true;
            this.btnRefreshServerState.Click += new System.EventHandler(this.btnRefreshServerState_Click);
            // 
            // grpLogicServerManager
            // 
            this.grpLogicServerManager.Controls.Add(this.btnLogicServerCloseServer);
            this.grpLogicServerManager.Controls.Add(this.btnLogicServerReloadTableConfig);
            this.grpLogicServerManager.Controls.Add(this.lblLogicServerCount);
            this.grpLogicServerManager.Controls.Add(this.label2);
            this.grpLogicServerManager.Controls.Add(this.dgvLogicServerInfo);
            this.grpLogicServerManager.Location = new System.Drawing.Point(12, 122);
            this.grpLogicServerManager.Name = "grpLogicServerManager";
            this.grpLogicServerManager.Size = new System.Drawing.Size(725, 234);
            this.grpLogicServerManager.TabIndex = 2;
            this.grpLogicServerManager.TabStop = false;
            this.grpLogicServerManager.Text = "Logic服务器管理";
            // 
            // btnLogicServerCloseServer
            // 
            this.btnLogicServerCloseServer.Location = new System.Drawing.Point(518, 25);
            this.btnLogicServerCloseServer.Name = "btnLogicServerCloseServer";
            this.btnLogicServerCloseServer.Size = new System.Drawing.Size(188, 28);
            this.btnLogicServerCloseServer.TabIndex = 8;
            this.btnLogicServerCloseServer.Text = "关闭所有Logic服务器";
            this.btnLogicServerCloseServer.UseVisualStyleBackColor = true;
            this.btnLogicServerCloseServer.Click += new System.EventHandler(this.btnLogicServerCloseServer_Click);
            // 
            // btnLogicServerReloadTableConfig
            // 
            this.btnLogicServerReloadTableConfig.Location = new System.Drawing.Point(281, 25);
            this.btnLogicServerReloadTableConfig.Name = "btnLogicServerReloadTableConfig";
            this.btnLogicServerReloadTableConfig.Size = new System.Drawing.Size(212, 28);
            this.btnLogicServerReloadTableConfig.TabIndex = 7;
            this.btnLogicServerReloadTableConfig.Text = "重载所有Logic服务器表格";
            this.btnLogicServerReloadTableConfig.UseVisualStyleBackColor = true;
            this.btnLogicServerReloadTableConfig.Click += new System.EventHandler(this.btnLogicServerReloadTableConfig_Click);
            // 
            // lblLogicServerCount
            // 
            this.lblLogicServerCount.AutoSize = true;
            this.lblLogicServerCount.Location = new System.Drawing.Point(121, 32);
            this.lblLogicServerCount.Name = "lblLogicServerCount";
            this.lblLogicServerCount.Size = new System.Drawing.Size(15, 15);
            this.lblLogicServerCount.TabIndex = 2;
            this.lblLogicServerCount.Text = "0";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(18, 32);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(97, 15);
            this.label2.TabIndex = 1;
            this.label2.Text = "服务器数量：";
            // 
            // dgvLogicServerInfo
            // 
            this.dgvLogicServerInfo.AllowUserToAddRows = false;
            this.dgvLogicServerInfo.AllowUserToDeleteRows = false;
            this.dgvLogicServerInfo.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.Fill;
            this.dgvLogicServerInfo.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgvLogicServerInfo.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvcLogicServerId,
            this.dgvcLogicServerIp,
            this.dgvcLogicServerLoadBalance,
            this.dgvcLogicServerReloadTableConfig,
            this.dgvcLogicServerCloseServer});
            this.dgvLogicServerInfo.Location = new System.Drawing.Point(21, 69);
            this.dgvLogicServerInfo.Name = "dgvLogicServerInfo";
            this.dgvLogicServerInfo.RowTemplate.Height = 27;
            this.dgvLogicServerInfo.Size = new System.Drawing.Size(685, 146);
            this.dgvLogicServerInfo.TabIndex = 0;
            this.dgvLogicServerInfo.CellContentClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvLogicServerInfo_CellContentClick);
            // 
            // grpBattleServerManager
            // 
            this.grpBattleServerManager.Controls.Add(this.btnBattleServerCloseServer);
            this.grpBattleServerManager.Controls.Add(this.dgvBattleServerInfo);
            this.grpBattleServerManager.Controls.Add(this.btnBattleServerReloadTableConfig);
            this.grpBattleServerManager.Controls.Add(this.label4);
            this.grpBattleServerManager.Controls.Add(this.lblBattleServerCount);
            this.grpBattleServerManager.Location = new System.Drawing.Point(12, 362);
            this.grpBattleServerManager.Name = "grpBattleServerManager";
            this.grpBattleServerManager.Size = new System.Drawing.Size(725, 234);
            this.grpBattleServerManager.TabIndex = 3;
            this.grpBattleServerManager.TabStop = false;
            this.grpBattleServerManager.Text = "Battle服务器管理";
            // 
            // btnBattleServerCloseServer
            // 
            this.btnBattleServerCloseServer.Location = new System.Drawing.Point(518, 25);
            this.btnBattleServerCloseServer.Name = "btnBattleServerCloseServer";
            this.btnBattleServerCloseServer.Size = new System.Drawing.Size(188, 28);
            this.btnBattleServerCloseServer.TabIndex = 12;
            this.btnBattleServerCloseServer.Text = "关闭所有Battle服务器";
            this.btnBattleServerCloseServer.UseVisualStyleBackColor = true;
            this.btnBattleServerCloseServer.Click += new System.EventHandler(this.btnBattleServerCloseServer_Click);
            // 
            // dgvBattleServerInfo
            // 
            this.dgvBattleServerInfo.AllowUserToAddRows = false;
            this.dgvBattleServerInfo.AllowUserToDeleteRows = false;
            this.dgvBattleServerInfo.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.Fill;
            this.dgvBattleServerInfo.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgvBattleServerInfo.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvcBattleServerId,
            this.dgvcBattleServerIp,
            this.dgvcBattleServerLoadBalance,
            this.dgvcBattleServerReloadTableConfig,
            this.dgvcBattleServerCloseServer});
            this.dgvBattleServerInfo.Location = new System.Drawing.Point(21, 69);
            this.dgvBattleServerInfo.Name = "dgvBattleServerInfo";
            this.dgvBattleServerInfo.RowTemplate.Height = 27;
            this.dgvBattleServerInfo.Size = new System.Drawing.Size(685, 146);
            this.dgvBattleServerInfo.TabIndex = 0;
            this.dgvBattleServerInfo.CellContentClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvBattleServerInfo_CellContentClick);
            // 
            // btnBattleServerReloadTableConfig
            // 
            this.btnBattleServerReloadTableConfig.Location = new System.Drawing.Point(281, 25);
            this.btnBattleServerReloadTableConfig.Name = "btnBattleServerReloadTableConfig";
            this.btnBattleServerReloadTableConfig.Size = new System.Drawing.Size(212, 28);
            this.btnBattleServerReloadTableConfig.TabIndex = 11;
            this.btnBattleServerReloadTableConfig.Text = "重载所有Battle服务器表格";
            this.btnBattleServerReloadTableConfig.UseVisualStyleBackColor = true;
            this.btnBattleServerReloadTableConfig.Click += new System.EventHandler(this.btnBattleServerReloadTableConfig_Click);
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(18, 32);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(97, 15);
            this.label4.TabIndex = 9;
            this.label4.Text = "服务器数量：";
            // 
            // lblBattleServerCount
            // 
            this.lblBattleServerCount.AutoSize = true;
            this.lblBattleServerCount.Location = new System.Drawing.Point(121, 32);
            this.lblBattleServerCount.Name = "lblBattleServerCount";
            this.lblBattleServerCount.Size = new System.Drawing.Size(15, 15);
            this.lblBattleServerCount.TabIndex = 10;
            this.lblBattleServerCount.Text = "0";
            // 
            // grpGatewayManager
            // 
            this.grpGatewayManager.Controls.Add(this.btnGatewayCloseServer);
            this.grpGatewayManager.Controls.Add(this.label3);
            this.grpGatewayManager.Controls.Add(this.lblGatewayCount);
            this.grpGatewayManager.Controls.Add(this.dgvGatewayInfo);
            this.grpGatewayManager.Location = new System.Drawing.Point(12, 602);
            this.grpGatewayManager.Name = "grpGatewayManager";
            this.grpGatewayManager.Size = new System.Drawing.Size(725, 236);
            this.grpGatewayManager.TabIndex = 4;
            this.grpGatewayManager.TabStop = false;
            this.grpGatewayManager.Text = "Gateway服务器管理";
            // 
            // btnGatewayCloseServer
            // 
            this.btnGatewayCloseServer.Location = new System.Drawing.Point(518, 27);
            this.btnGatewayCloseServer.Name = "btnGatewayCloseServer";
            this.btnGatewayCloseServer.Size = new System.Drawing.Size(188, 28);
            this.btnGatewayCloseServer.TabIndex = 13;
            this.btnGatewayCloseServer.Text = "关闭所有Gateway服务器";
            this.btnGatewayCloseServer.UseVisualStyleBackColor = true;
            this.btnGatewayCloseServer.Click += new System.EventHandler(this.btnGatewayCloseServer_Click);
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(18, 34);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(97, 15);
            this.label3.TabIndex = 11;
            this.label3.Text = "服务器数量：";
            // 
            // lblGatewayCount
            // 
            this.lblGatewayCount.AutoSize = true;
            this.lblGatewayCount.Location = new System.Drawing.Point(121, 34);
            this.lblGatewayCount.Name = "lblGatewayCount";
            this.lblGatewayCount.Size = new System.Drawing.Size(15, 15);
            this.lblGatewayCount.TabIndex = 12;
            this.lblGatewayCount.Text = "0";
            // 
            // dgvGatewayInfo
            // 
            this.dgvGatewayInfo.AllowUserToAddRows = false;
            this.dgvGatewayInfo.AllowUserToDeleteRows = false;
            this.dgvGatewayInfo.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.Fill;
            this.dgvGatewayInfo.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgvGatewayInfo.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.dgvcGatewayServerId,
            this.dgvcGatewayServerIp,
            this.dgvcGatewayClientConnectPath,
            this.dgvcGatewayLoadBalance,
            this.dgvcGatewayCloseServer});
            this.dgvGatewayInfo.Location = new System.Drawing.Point(21, 69);
            this.dgvGatewayInfo.Name = "dgvGatewayInfo";
            this.dgvGatewayInfo.RowTemplate.Height = 27;
            this.dgvGatewayInfo.Size = new System.Drawing.Size(685, 146);
            this.dgvGatewayInfo.TabIndex = 0;
            this.dgvGatewayInfo.CellContentClick += new System.Windows.Forms.DataGridViewCellEventHandler(this.dgvGatewayInfo_CellContentClick);
            // 
            // rtxConsole
            // 
            this.rtxConsole.Location = new System.Drawing.Point(762, 58);
            this.rtxConsole.Name = "rtxConsole";
            this.rtxConsole.Size = new System.Drawing.Size(577, 780);
            this.rtxConsole.TabIndex = 5;
            this.rtxConsole.Text = "";
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(759, 19);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(188, 15);
            this.label1.TabIndex = 6;
            this.label1.Text = "接收GM服务器推送的信息：";
            // 
            // grpGmServerManager
            // 
            this.grpGmServerManager.Controls.Add(this.btnGmServerCloseServer);
            this.grpGmServerManager.Controls.Add(this.btnGmServerReloadTableConfig);
            this.grpGmServerManager.Location = new System.Drawing.Point(12, 58);
            this.grpGmServerManager.Name = "grpGmServerManager";
            this.grpGmServerManager.Size = new System.Drawing.Size(725, 58);
            this.grpGmServerManager.TabIndex = 7;
            this.grpGmServerManager.TabStop = false;
            this.grpGmServerManager.Text = "GM服务器管理";
            // 
            // btnGmServerReloadTableConfig
            // 
            this.btnGmServerReloadTableConfig.Location = new System.Drawing.Point(400, 19);
            this.btnGmServerReloadTableConfig.Name = "btnGmServerReloadTableConfig";
            this.btnGmServerReloadTableConfig.Size = new System.Drawing.Size(158, 28);
            this.btnGmServerReloadTableConfig.TabIndex = 9;
            this.btnGmServerReloadTableConfig.Text = "重载GM服务器表格";
            this.btnGmServerReloadTableConfig.UseVisualStyleBackColor = true;
            this.btnGmServerReloadTableConfig.Click += new System.EventHandler(this.btnGmServerReloadTableConfig_Click);
            // 
            // btnGmServerCloseServer
            // 
            this.btnGmServerCloseServer.Location = new System.Drawing.Point(579, 19);
            this.btnGmServerCloseServer.Name = "btnGmServerCloseServer";
            this.btnGmServerCloseServer.Size = new System.Drawing.Size(127, 28);
            this.btnGmServerCloseServer.TabIndex = 9;
            this.btnGmServerCloseServer.Text = "关闭GM服务器";
            this.btnGmServerCloseServer.UseVisualStyleBackColor = true;
            this.btnGmServerCloseServer.Click += new System.EventHandler(this.btnGmServerCloseServer_Click);
            // 
            // dgvcLogicServerId
            // 
            this.dgvcLogicServerId.FillWeight = 80F;
            this.dgvcLogicServerId.HeaderText = "ID";
            this.dgvcLogicServerId.Name = "dgvcLogicServerId";
            this.dgvcLogicServerId.ReadOnly = true;
            // 
            // dgvcLogicServerIp
            // 
            this.dgvcLogicServerIp.FillWeight = 120F;
            this.dgvcLogicServerIp.HeaderText = "IP";
            this.dgvcLogicServerIp.Name = "dgvcLogicServerIp";
            this.dgvcLogicServerIp.ReadOnly = true;
            // 
            // dgvcLogicServerLoadBalance
            // 
            this.dgvcLogicServerLoadBalance.FillWeight = 80F;
            this.dgvcLogicServerLoadBalance.HeaderText = "负载";
            this.dgvcLogicServerLoadBalance.Name = "dgvcLogicServerLoadBalance";
            this.dgvcLogicServerLoadBalance.ReadOnly = true;
            // 
            // dgvcLogicServerReloadTableConfig
            // 
            dataGridViewCellStyle1.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleCenter;
            dataGridViewCellStyle1.NullValue = "重载表格";
            this.dgvcLogicServerReloadTableConfig.DefaultCellStyle = dataGridViewCellStyle1;
            this.dgvcLogicServerReloadTableConfig.FillWeight = 80F;
            this.dgvcLogicServerReloadTableConfig.HeaderText = "重载表格";
            this.dgvcLogicServerReloadTableConfig.Name = "dgvcLogicServerReloadTableConfig";
            this.dgvcLogicServerReloadTableConfig.ReadOnly = true;
            this.dgvcLogicServerReloadTableConfig.Resizable = System.Windows.Forms.DataGridViewTriState.True;
            this.dgvcLogicServerReloadTableConfig.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Automatic;
            this.dgvcLogicServerReloadTableConfig.Text = "重载表格";
            // 
            // dgvcLogicServerCloseServer
            // 
            dataGridViewCellStyle2.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleCenter;
            dataGridViewCellStyle2.NullValue = "关闭";
            this.dgvcLogicServerCloseServer.DefaultCellStyle = dataGridViewCellStyle2;
            this.dgvcLogicServerCloseServer.FillWeight = 80F;
            this.dgvcLogicServerCloseServer.HeaderText = "关闭";
            this.dgvcLogicServerCloseServer.Name = "dgvcLogicServerCloseServer";
            this.dgvcLogicServerCloseServer.ReadOnly = true;
            this.dgvcLogicServerCloseServer.Resizable = System.Windows.Forms.DataGridViewTriState.True;
            this.dgvcLogicServerCloseServer.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Automatic;
            this.dgvcLogicServerCloseServer.Text = "关闭";
            // 
            // dgvcBattleServerId
            // 
            this.dgvcBattleServerId.FillWeight = 80F;
            this.dgvcBattleServerId.HeaderText = "ID";
            this.dgvcBattleServerId.Name = "dgvcBattleServerId";
            this.dgvcBattleServerId.ReadOnly = true;
            // 
            // dgvcBattleServerIp
            // 
            this.dgvcBattleServerIp.FillWeight = 120F;
            this.dgvcBattleServerIp.HeaderText = "IP";
            this.dgvcBattleServerIp.Name = "dgvcBattleServerIp";
            this.dgvcBattleServerIp.ReadOnly = true;
            // 
            // dgvcBattleServerLoadBalance
            // 
            this.dgvcBattleServerLoadBalance.FillWeight = 80F;
            this.dgvcBattleServerLoadBalance.HeaderText = "负载";
            this.dgvcBattleServerLoadBalance.Name = "dgvcBattleServerLoadBalance";
            this.dgvcBattleServerLoadBalance.ReadOnly = true;
            // 
            // dgvcBattleServerReloadTableConfig
            // 
            dataGridViewCellStyle3.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleCenter;
            dataGridViewCellStyle3.NullValue = "重载表格";
            this.dgvcBattleServerReloadTableConfig.DefaultCellStyle = dataGridViewCellStyle3;
            this.dgvcBattleServerReloadTableConfig.FillWeight = 80F;
            this.dgvcBattleServerReloadTableConfig.HeaderText = "重载表格";
            this.dgvcBattleServerReloadTableConfig.Name = "dgvcBattleServerReloadTableConfig";
            this.dgvcBattleServerReloadTableConfig.ReadOnly = true;
            this.dgvcBattleServerReloadTableConfig.Resizable = System.Windows.Forms.DataGridViewTriState.True;
            this.dgvcBattleServerReloadTableConfig.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Automatic;
            this.dgvcBattleServerReloadTableConfig.Text = "重载表格";
            // 
            // dgvcBattleServerCloseServer
            // 
            dataGridViewCellStyle4.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleCenter;
            dataGridViewCellStyle4.NullValue = "关闭";
            this.dgvcBattleServerCloseServer.DefaultCellStyle = dataGridViewCellStyle4;
            this.dgvcBattleServerCloseServer.FillWeight = 80F;
            this.dgvcBattleServerCloseServer.HeaderText = "关闭";
            this.dgvcBattleServerCloseServer.Name = "dgvcBattleServerCloseServer";
            this.dgvcBattleServerCloseServer.ReadOnly = true;
            this.dgvcBattleServerCloseServer.Resizable = System.Windows.Forms.DataGridViewTriState.True;
            this.dgvcBattleServerCloseServer.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Automatic;
            this.dgvcBattleServerCloseServer.Text = "关闭";
            // 
            // dgvcGatewayServerId
            // 
            this.dgvcGatewayServerId.FillWeight = 80F;
            this.dgvcGatewayServerId.HeaderText = "ID";
            this.dgvcGatewayServerId.Name = "dgvcGatewayServerId";
            this.dgvcGatewayServerId.ReadOnly = true;
            // 
            // dgvcGatewayServerIp
            // 
            this.dgvcGatewayServerIp.FillWeight = 150F;
            this.dgvcGatewayServerIp.HeaderText = "IP";
            this.dgvcGatewayServerIp.Name = "dgvcGatewayServerIp";
            this.dgvcGatewayServerIp.ReadOnly = true;
            // 
            // dgvcGatewayClientConnectPath
            // 
            this.dgvcGatewayClientConnectPath.FillWeight = 150F;
            this.dgvcGatewayClientConnectPath.HeaderText = "客户端连接地址";
            this.dgvcGatewayClientConnectPath.Name = "dgvcGatewayClientConnectPath";
            this.dgvcGatewayClientConnectPath.ReadOnly = true;
            // 
            // dgvcGatewayLoadBalance
            // 
            this.dgvcGatewayLoadBalance.FillWeight = 80F;
            this.dgvcGatewayLoadBalance.HeaderText = "负载";
            this.dgvcGatewayLoadBalance.Name = "dgvcGatewayLoadBalance";
            this.dgvcGatewayLoadBalance.ReadOnly = true;
            // 
            // dgvcGatewayCloseServer
            // 
            dataGridViewCellStyle5.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleCenter;
            dataGridViewCellStyle5.NullValue = "关闭";
            this.dgvcGatewayCloseServer.DefaultCellStyle = dataGridViewCellStyle5;
            this.dgvcGatewayCloseServer.FillWeight = 80F;
            this.dgvcGatewayCloseServer.HeaderText = "关闭";
            this.dgvcGatewayCloseServer.Name = "dgvcGatewayCloseServer";
            this.dgvcGatewayCloseServer.ReadOnly = true;
            this.dgvcGatewayCloseServer.Resizable = System.Windows.Forms.DataGridViewTriState.True;
            this.dgvcGatewayCloseServer.SortMode = System.Windows.Forms.DataGridViewColumnSortMode.Automatic;
            this.dgvcGatewayCloseServer.Text = "关闭";
            // 
            // btnGetOnlineUserInfo
            // 
            this.btnGetOnlineUserInfo.Location = new System.Drawing.Point(227, 12);
            this.btnGetOnlineUserInfo.Name = "btnGetOnlineUserInfo";
            this.btnGetOnlineUserInfo.Size = new System.Drawing.Size(161, 28);
            this.btnGetOnlineUserInfo.TabIndex = 8;
            this.btnGetOnlineUserInfo.Text = "获取在线玩家详情";
            this.btnGetOnlineUserInfo.UseVisualStyleBackColor = true;
            this.btnGetOnlineUserInfo.Click += new System.EventHandler(this.btnGetOnlineUserInfo_Click);
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 15F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1351, 852);
            this.Controls.Add(this.btnGetOnlineUserInfo);
            this.Controls.Add(this.grpGmServerManager);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.rtxConsole);
            this.Controls.Add(this.grpGatewayManager);
            this.Controls.Add(this.grpBattleServerManager);
            this.Controls.Add(this.grpLogicServerManager);
            this.Controls.Add(this.btnRefreshServerState);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.Fixed3D;
            this.MaximizeBox = false;
            this.Name = "MainForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "GameManager by 张齐";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.MainForm_FormClosing);
            this.Load += new System.EventHandler(this.MainForm_Load);
            this.Shown += new System.EventHandler(this.MainForm_Shown);
            this.grpLogicServerManager.ResumeLayout(false);
            this.grpLogicServerManager.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvLogicServerInfo)).EndInit();
            this.grpBattleServerManager.ResumeLayout(false);
            this.grpBattleServerManager.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvBattleServerInfo)).EndInit();
            this.grpGatewayManager.ResumeLayout(false);
            this.grpGatewayManager.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgvGatewayInfo)).EndInit();
            this.grpGmServerManager.ResumeLayout(false);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion
        private System.Windows.Forms.Button btnRefreshServerState;
        private System.Windows.Forms.GroupBox grpLogicServerManager;
        private System.Windows.Forms.DataGridView dgvLogicServerInfo;
        private System.Windows.Forms.GroupBox grpBattleServerManager;
        private System.Windows.Forms.DataGridView dgvBattleServerInfo;
        private System.Windows.Forms.GroupBox grpGatewayManager;
        private System.Windows.Forms.DataGridView dgvGatewayInfo;
        private System.Windows.Forms.RichTextBox rtxConsole;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Button btnLogicServerCloseServer;
        private System.Windows.Forms.Button btnLogicServerReloadTableConfig;
        private System.Windows.Forms.Label lblLogicServerCount;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Button btnBattleServerCloseServer;
        private System.Windows.Forms.Button btnBattleServerReloadTableConfig;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label lblBattleServerCount;
        private System.Windows.Forms.Button btnGatewayCloseServer;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label lblGatewayCount;
        private System.Windows.Forms.GroupBox grpGmServerManager;
        private System.Windows.Forms.Button btnGmServerCloseServer;
        private System.Windows.Forms.Button btnGmServerReloadTableConfig;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcLogicServerId;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcLogicServerIp;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcLogicServerLoadBalance;
        private System.Windows.Forms.DataGridViewButtonColumn dgvcLogicServerReloadTableConfig;
        private System.Windows.Forms.DataGridViewButtonColumn dgvcLogicServerCloseServer;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcBattleServerId;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcBattleServerIp;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcBattleServerLoadBalance;
        private System.Windows.Forms.DataGridViewButtonColumn dgvcBattleServerReloadTableConfig;
        private System.Windows.Forms.DataGridViewButtonColumn dgvcBattleServerCloseServer;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcGatewayServerId;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcGatewayServerIp;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcGatewayClientConnectPath;
        private System.Windows.Forms.DataGridViewTextBoxColumn dgvcGatewayLoadBalance;
        private System.Windows.Forms.DataGridViewButtonColumn dgvcGatewayCloseServer;
        private System.Windows.Forms.Button btnGetOnlineUserInfo;
    }
}

