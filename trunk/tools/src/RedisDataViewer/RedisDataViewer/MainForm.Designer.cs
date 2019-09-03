namespace RedisDataViewer
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
            this.components = new System.ComponentModel.Container();
            this.label1 = new System.Windows.Forms.Label();
            this.cmbChooseGroup = new System.Windows.Forms.ComboBox();
            this.label2 = new System.Windows.Forms.Label();
            this.cmbChooseKey = new System.Windows.Forms.ComboBox();
            this.label3 = new System.Windows.Forms.Label();
            this.txtKeyName = new System.Windows.Forms.TextBox();
            this.label4 = new System.Windows.Forms.Label();
            this.txtProtobufClassName = new System.Windows.Forms.TextBox();
            this.label5 = new System.Windows.Forms.Label();
            this.cmbChooseDataType = new System.Windows.Forms.ComboBox();
            this.label6 = new System.Windows.Forms.Label();
            this.lblRemark = new System.Windows.Forms.Label();
            this.grpHashFilter = new System.Windows.Forms.GroupBox();
            this.label9 = new System.Windows.Forms.Label();
            this.txtHashKey = new System.Windows.Forms.TextBox();
            this.label7 = new System.Windows.Forms.Label();
            this.btnSearch = new System.Windows.Forms.Button();
            this.grpListFilter = new System.Windows.Forms.GroupBox();
            this.label11 = new System.Windows.Forms.Label();
            this.txtListRangeEnd = new System.Windows.Forms.TextBox();
            this.label10 = new System.Windows.Forms.Label();
            this.txtListRangeStart = new System.Windows.Forms.TextBox();
            this.label8 = new System.Windows.Forms.Label();
            this.grpZSetFilter = new System.Windows.Forms.GroupBox();
            this.txtZSetItemName = new System.Windows.Forms.TextBox();
            this.label25 = new System.Windows.Forms.Label();
            this.rdoZSetGetItem = new System.Windows.Forms.RadioButton();
            this.label17 = new System.Windows.Forms.Label();
            this.txtZSetRankRangeEnd = new System.Windows.Forms.TextBox();
            this.label16 = new System.Windows.Forms.Label();
            this.txtZSetRankRangeStart = new System.Windows.Forms.TextBox();
            this.label15 = new System.Windows.Forms.Label();
            this.label14 = new System.Windows.Forms.Label();
            this.txtZSetScoreRangeMax = new System.Windows.Forms.TextBox();
            this.label13 = new System.Windows.Forms.Label();
            this.txtZSetScoreRangeMin = new System.Windows.Forms.TextBox();
            this.label12 = new System.Windows.Forms.Label();
            this.rdoZSetGetDataByRank = new System.Windows.Forms.RadioButton();
            this.rdoZSetGetDataByScore = new System.Windows.Forms.RadioButton();
            this.chkIsZSetScoreOrderByAsc = new System.Windows.Forms.CheckBox();
            this.rtxResult = new System.Windows.Forms.RichTextBox();
            this.label18 = new System.Windows.Forms.Label();
            this.label19 = new System.Windows.Forms.Label();
            this.label20 = new System.Windows.Forms.Label();
            this.label21 = new System.Windows.Forms.Label();
            this.label22 = new System.Windows.Forms.Label();
            this.label23 = new System.Windows.Forms.Label();
            this.txtListDataSpliteStr = new System.Windows.Forms.TextBox();
            this.txtSetDataSpliteStr = new System.Windows.Forms.TextBox();
            this.txtHashDataSpliteStr = new System.Windows.Forms.TextBox();
            this.txtHashKeyValuePairsSpliteStr = new System.Windows.Forms.TextBox();
            this.txtZSetDataSpliteStr = new System.Windows.Forms.TextBox();
            this.txtZSetNameScoreSpliteStr = new System.Windows.Forms.TextBox();
            this.label24 = new System.Windows.Forms.Label();
            this.txtZSetRankDataSpliteStr = new System.Windows.Forms.TextBox();
            this.btnFavorite = new System.Windows.Forms.Button();
            this.label26 = new System.Windows.Forms.Label();
            this.lstFavorite = new System.Windows.Forms.ListBox();
            this.cmsFavorite = new System.Windows.Forms.ContextMenuStrip(this.components);
            this.tsmiFavoriteTop = new System.Windows.Forms.ToolStripMenuItem();
            this.tsmiFavoriteDelete = new System.Windows.Forms.ToolStripMenuItem();
            this.tsmiFavoriteClear = new System.Windows.Forms.ToolStripMenuItem();
            this.label27 = new System.Windows.Forms.Label();
            this.lstHistory = new System.Windows.Forms.ListBox();
            this.cmsHistory = new System.Windows.Forms.ContextMenuStrip(this.components);
            this.tsmiHistoryTop = new System.Windows.Forms.ToolStripMenuItem();
            this.tsmiHistoryDelete = new System.Windows.Forms.ToolStripMenuItem();
            this.tsmiHistoryClear = new System.Windows.Forms.ToolStripMenuItem();
            this.grpHashFilter.SuspendLayout();
            this.grpListFilter.SuspendLayout();
            this.grpZSetFilter.SuspendLayout();
            this.cmsFavorite.SuspendLayout();
            this.cmsHistory.SuspendLayout();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(18, 28);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(82, 15);
            this.label1.TabIndex = 0;
            this.label1.Text = "分组筛选：";
            // 
            // cmbChooseGroup
            // 
            this.cmbChooseGroup.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cmbChooseGroup.FormattingEnabled = true;
            this.cmbChooseGroup.Location = new System.Drawing.Point(106, 25);
            this.cmbChooseGroup.Name = "cmbChooseGroup";
            this.cmbChooseGroup.Size = new System.Drawing.Size(485, 23);
            this.cmbChooseGroup.TabIndex = 1;
            this.cmbChooseGroup.SelectedIndexChanged += new System.EventHandler(this.cmbChooseGroup_SelectedIndexChanged);
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(613, 28);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(76, 15);
            this.label2.TabIndex = 2;
            this.label2.Text = "key筛选：";
            // 
            // cmbChooseKey
            // 
            this.cmbChooseKey.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cmbChooseKey.FormattingEnabled = true;
            this.cmbChooseKey.Location = new System.Drawing.Point(695, 25);
            this.cmbChooseKey.Name = "cmbChooseKey";
            this.cmbChooseKey.Size = new System.Drawing.Size(419, 23);
            this.cmbChooseKey.TabIndex = 3;
            this.cmbChooseKey.SelectedIndexChanged += new System.EventHandler(this.cmbChooseKey_SelectedIndexChanged);
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(18, 74);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(61, 15);
            this.label3.TabIndex = 4;
            this.label3.Text = "key名：";
            // 
            // txtKeyName
            // 
            this.txtKeyName.Location = new System.Drawing.Point(85, 71);
            this.txtKeyName.Name = "txtKeyName";
            this.txtKeyName.Size = new System.Drawing.Size(506, 25);
            this.txtKeyName.TabIndex = 5;
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(613, 74);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(101, 15);
            this.label4.TabIndex = 6;
            this.label4.Text = "protobuf类：";
            // 
            // txtProtobufClassName
            // 
            this.txtProtobufClassName.Location = new System.Drawing.Point(720, 71);
            this.txtProtobufClassName.Name = "txtProtobufClassName";
            this.txtProtobufClassName.Size = new System.Drawing.Size(200, 25);
            this.txtProtobufClassName.TabIndex = 7;
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(938, 74);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(82, 15);
            this.label5.TabIndex = 8;
            this.label5.Text = "数据类型：";
            // 
            // cmbChooseDataType
            // 
            this.cmbChooseDataType.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cmbChooseDataType.FormattingEnabled = true;
            this.cmbChooseDataType.Items.AddRange(new object[] {
            "String",
            "Hash",
            "List",
            "Set",
            "ZSet"});
            this.cmbChooseDataType.Location = new System.Drawing.Point(1026, 71);
            this.cmbChooseDataType.Name = "cmbChooseDataType";
            this.cmbChooseDataType.Size = new System.Drawing.Size(88, 23);
            this.cmbChooseDataType.TabIndex = 9;
            this.cmbChooseDataType.SelectedIndexChanged += new System.EventHandler(this.cmbChooseDataType_SelectedIndexChanged);
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(18, 122);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(52, 15);
            this.label6.TabIndex = 10;
            this.label6.Text = "备注：";
            // 
            // lblRemark
            // 
            this.lblRemark.Location = new System.Drawing.Point(82, 122);
            this.lblRemark.Name = "lblRemark";
            this.lblRemark.Size = new System.Drawing.Size(903, 32);
            this.lblRemark.TabIndex = 11;
            // 
            // grpHashFilter
            // 
            this.grpHashFilter.Controls.Add(this.label9);
            this.grpHashFilter.Controls.Add(this.txtHashKey);
            this.grpHashFilter.Controls.Add(this.label7);
            this.grpHashFilter.Location = new System.Drawing.Point(21, 166);
            this.grpHashFilter.Name = "grpHashFilter";
            this.grpHashFilter.Size = new System.Drawing.Size(964, 70);
            this.grpHashFilter.TabIndex = 12;
            this.grpHashFilter.TabStop = false;
            this.grpHashFilter.Text = "Hash型筛选";
            this.grpHashFilter.Visible = false;
            // 
            // label9
            // 
            this.label9.AutoSize = true;
            this.label9.Location = new System.Drawing.Point(538, 30);
            this.label9.Name = "label9";
            this.label9.Size = new System.Drawing.Size(181, 15);
            this.label9.TabIndex = 2;
            this.label9.Text = "注意：留空则查找全部key";
            // 
            // txtHashKey
            // 
            this.txtHashKey.Location = new System.Drawing.Point(146, 27);
            this.txtHashKey.Name = "txtHashKey";
            this.txtHashKey.Size = new System.Drawing.Size(367, 25);
            this.txtHashKey.TabIndex = 1;
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(24, 30);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(116, 15);
            this.label7.TabIndex = 0;
            this.label7.Text = "Hash Key筛选：";
            // 
            // btnSearch
            // 
            this.btnSearch.Font = new System.Drawing.Font("宋体", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnSearch.Location = new System.Drawing.Point(1026, 139);
            this.btnSearch.Name = "btnSearch";
            this.btnSearch.Size = new System.Drawing.Size(88, 54);
            this.btnSearch.TabIndex = 13;
            this.btnSearch.Text = "查询";
            this.btnSearch.UseVisualStyleBackColor = true;
            this.btnSearch.Click += new System.EventHandler(this.btnSearch_Click);
            // 
            // grpListFilter
            // 
            this.grpListFilter.Controls.Add(this.label11);
            this.grpListFilter.Controls.Add(this.txtListRangeEnd);
            this.grpListFilter.Controls.Add(this.label10);
            this.grpListFilter.Controls.Add(this.txtListRangeStart);
            this.grpListFilter.Controls.Add(this.label8);
            this.grpListFilter.Location = new System.Drawing.Point(21, 166);
            this.grpListFilter.Name = "grpListFilter";
            this.grpListFilter.Size = new System.Drawing.Size(964, 70);
            this.grpListFilter.TabIndex = 12;
            this.grpListFilter.TabStop = false;
            this.grpListFilter.Text = "List型筛选";
            this.grpListFilter.Visible = false;
            // 
            // label11
            // 
            this.label11.AutoSize = true;
            this.label11.Location = new System.Drawing.Point(367, 30);
            this.label11.Name = "label11";
            this.label11.Size = new System.Drawing.Size(466, 15);
            this.label11.TabIndex = 4;
            this.label11.Text = "注意：下标从0开始，负数表示倒数第几个，比如-1表示最后一个元素";
            // 
            // txtListRangeEnd
            // 
            this.txtListRangeEnd.Location = new System.Drawing.Point(290, 27);
            this.txtListRangeEnd.Name = "txtListRangeEnd";
            this.txtListRangeEnd.Size = new System.Drawing.Size(53, 25);
            this.txtListRangeEnd.TabIndex = 3;
            this.txtListRangeEnd.Text = "-1";
            // 
            // label10
            // 
            this.label10.AutoSize = true;
            this.label10.Location = new System.Drawing.Point(262, 34);
            this.label10.Name = "label10";
            this.label10.Size = new System.Drawing.Size(22, 15);
            this.label10.TabIndex = 2;
            this.label10.Text = "—";
            // 
            // txtListRangeStart
            // 
            this.txtListRangeStart.Location = new System.Drawing.Point(202, 27);
            this.txtListRangeStart.Name = "txtListRangeStart";
            this.txtListRangeStart.Size = new System.Drawing.Size(53, 25);
            this.txtListRangeStart.TabIndex = 1;
            this.txtListRangeStart.Text = "0";
            // 
            // label8
            // 
            this.label8.AutoSize = true;
            this.label8.Location = new System.Drawing.Point(24, 30);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(174, 15);
            this.label8.TabIndex = 0;
            this.label8.Text = "List元素下标范围筛选：";
            // 
            // grpZSetFilter
            // 
            this.grpZSetFilter.Controls.Add(this.txtZSetItemName);
            this.grpZSetFilter.Controls.Add(this.label25);
            this.grpZSetFilter.Controls.Add(this.rdoZSetGetItem);
            this.grpZSetFilter.Controls.Add(this.label17);
            this.grpZSetFilter.Controls.Add(this.txtZSetRankRangeEnd);
            this.grpZSetFilter.Controls.Add(this.label16);
            this.grpZSetFilter.Controls.Add(this.txtZSetRankRangeStart);
            this.grpZSetFilter.Controls.Add(this.label15);
            this.grpZSetFilter.Controls.Add(this.label14);
            this.grpZSetFilter.Controls.Add(this.txtZSetScoreRangeMax);
            this.grpZSetFilter.Controls.Add(this.label13);
            this.grpZSetFilter.Controls.Add(this.txtZSetScoreRangeMin);
            this.grpZSetFilter.Controls.Add(this.label12);
            this.grpZSetFilter.Controls.Add(this.rdoZSetGetDataByRank);
            this.grpZSetFilter.Controls.Add(this.rdoZSetGetDataByScore);
            this.grpZSetFilter.Controls.Add(this.chkIsZSetScoreOrderByAsc);
            this.grpZSetFilter.Location = new System.Drawing.Point(21, 166);
            this.grpZSetFilter.Name = "grpZSetFilter";
            this.grpZSetFilter.Size = new System.Drawing.Size(964, 126);
            this.grpZSetFilter.TabIndex = 13;
            this.grpZSetFilter.TabStop = false;
            this.grpZSetFilter.Text = "ZSet型筛选";
            this.grpZSetFilter.Visible = false;
            // 
            // txtZSetItemName
            // 
            this.txtZSetItemName.Location = new System.Drawing.Point(325, 87);
            this.txtZSetItemName.Name = "txtZSetItemName";
            this.txtZSetItemName.Size = new System.Drawing.Size(178, 25);
            this.txtZSetItemName.TabIndex = 15;
            // 
            // label25
            // 
            this.label25.AutoSize = true;
            this.label25.Location = new System.Drawing.Point(252, 90);
            this.label25.Name = "label25";
            this.label25.Size = new System.Drawing.Size(67, 15);
            this.label25.TabIndex = 14;
            this.label25.Text = "元素名：";
            // 
            // rdoZSetGetItem
            // 
            this.rdoZSetGetItem.AutoSize = true;
            this.rdoZSetGetItem.Location = new System.Drawing.Point(27, 88);
            this.rdoZSetGetItem.Name = "rdoZSetGetItem";
            this.rdoZSetGetItem.Size = new System.Drawing.Size(208, 19);
            this.rdoZSetGetItem.TabIndex = 13;
            this.rdoZSetGetItem.TabStop = true;
            this.rdoZSetGetItem.Text = "查找指定元素的分数和排名";
            this.rdoZSetGetItem.UseVisualStyleBackColor = true;
            // 
            // label17
            // 
            this.label17.AutoSize = true;
            this.label17.Location = new System.Drawing.Point(544, 63);
            this.label17.Name = "label17";
            this.label17.Size = new System.Drawing.Size(285, 15);
            this.label17.TabIndex = 12;
            this.label17.Text = "注意：下标从0开始，负数表示倒数第几个";
            // 
            // txtZSetRankRangeEnd
            // 
            this.txtZSetRankRangeEnd.Location = new System.Drawing.Point(412, 57);
            this.txtZSetRankRangeEnd.Name = "txtZSetRankRangeEnd";
            this.txtZSetRankRangeEnd.Size = new System.Drawing.Size(122, 25);
            this.txtZSetRankRangeEnd.TabIndex = 11;
            this.txtZSetRankRangeEnd.Text = "-1";
            // 
            // label16
            // 
            this.label16.AutoSize = true;
            this.label16.Location = new System.Drawing.Point(384, 63);
            this.label16.Name = "label16";
            this.label16.Size = new System.Drawing.Size(22, 15);
            this.label16.TabIndex = 10;
            this.label16.Text = "—";
            // 
            // txtZSetRankRangeStart
            // 
            this.txtZSetRankRangeStart.Location = new System.Drawing.Point(255, 57);
            this.txtZSetRankRangeStart.Name = "txtZSetRankRangeStart";
            this.txtZSetRankRangeStart.Size = new System.Drawing.Size(122, 25);
            this.txtZSetRankRangeStart.TabIndex = 9;
            this.txtZSetRankRangeStart.Text = "0";
            // 
            // label15
            // 
            this.label15.AutoSize = true;
            this.label15.Location = new System.Drawing.Point(161, 60);
            this.label15.Name = "label15";
            this.label15.Size = new System.Drawing.Size(82, 15);
            this.label15.TabIndex = 8;
            this.label15.Text = "排名范围：";
            // 
            // label14
            // 
            this.label14.AutoSize = true;
            this.label14.Location = new System.Drawing.Point(544, 33);
            this.label14.Name = "label14";
            this.label14.Size = new System.Drawing.Size(262, 15);
            this.label14.TabIndex = 5;
            this.label14.Text = "注意：分数上下限均可不填表示不限制";
            // 
            // txtZSetScoreRangeMax
            // 
            this.txtZSetScoreRangeMax.Location = new System.Drawing.Point(412, 27);
            this.txtZSetScoreRangeMax.Name = "txtZSetScoreRangeMax";
            this.txtZSetScoreRangeMax.Size = new System.Drawing.Size(122, 25);
            this.txtZSetScoreRangeMax.TabIndex = 7;
            // 
            // label13
            // 
            this.label13.AutoSize = true;
            this.label13.Location = new System.Drawing.Point(384, 33);
            this.label13.Name = "label13";
            this.label13.Size = new System.Drawing.Size(22, 15);
            this.label13.TabIndex = 6;
            this.label13.Text = "—";
            // 
            // txtZSetScoreRangeMin
            // 
            this.txtZSetScoreRangeMin.Location = new System.Drawing.Point(255, 27);
            this.txtZSetScoreRangeMin.Name = "txtZSetScoreRangeMin";
            this.txtZSetScoreRangeMin.Size = new System.Drawing.Size(122, 25);
            this.txtZSetScoreRangeMin.TabIndex = 5;
            // 
            // label12
            // 
            this.label12.AutoSize = true;
            this.label12.Location = new System.Drawing.Point(161, 30);
            this.label12.Name = "label12";
            this.label12.Size = new System.Drawing.Size(82, 15);
            this.label12.TabIndex = 3;
            this.label12.Text = "分数范围：";
            // 
            // rdoZSetGetDataByRank
            // 
            this.rdoZSetGetDataByRank.AutoSize = true;
            this.rdoZSetGetDataByRank.Location = new System.Drawing.Point(27, 58);
            this.rdoZSetGetDataByRank.Name = "rdoZSetGetDataByRank";
            this.rdoZSetGetDataByRank.Size = new System.Drawing.Size(118, 19);
            this.rdoZSetGetDataByRank.TabIndex = 2;
            this.rdoZSetGetDataByRank.TabStop = true;
            this.rdoZSetGetDataByRank.Text = "通过排名筛选";
            this.rdoZSetGetDataByRank.UseVisualStyleBackColor = true;
            // 
            // rdoZSetGetDataByScore
            // 
            this.rdoZSetGetDataByScore.AutoSize = true;
            this.rdoZSetGetDataByScore.Checked = true;
            this.rdoZSetGetDataByScore.Location = new System.Drawing.Point(27, 28);
            this.rdoZSetGetDataByScore.Name = "rdoZSetGetDataByScore";
            this.rdoZSetGetDataByScore.Size = new System.Drawing.Size(118, 19);
            this.rdoZSetGetDataByScore.TabIndex = 1;
            this.rdoZSetGetDataByScore.TabStop = true;
            this.rdoZSetGetDataByScore.Text = "通过分数筛选";
            this.rdoZSetGetDataByScore.UseVisualStyleBackColor = true;
            // 
            // chkIsZSetScoreOrderByAsc
            // 
            this.chkIsZSetScoreOrderByAsc.Checked = true;
            this.chkIsZSetScoreOrderByAsc.CheckState = System.Windows.Forms.CheckState.Checked;
            this.chkIsZSetScoreOrderByAsc.Location = new System.Drawing.Point(855, 50);
            this.chkIsZSetScoreOrderByAsc.Name = "chkIsZSetScoreOrderByAsc";
            this.chkIsZSetScoreOrderByAsc.Size = new System.Drawing.Size(103, 43);
            this.chkIsZSetScoreOrderByAsc.TabIndex = 0;
            this.chkIsZSetScoreOrderByAsc.Text = "按分数\r\n升序排列";
            this.chkIsZSetScoreOrderByAsc.UseVisualStyleBackColor = true;
            // 
            // rtxResult
            // 
            this.rtxResult.Location = new System.Drawing.Point(21, 310);
            this.rtxResult.Name = "rtxResult";
            this.rtxResult.Size = new System.Drawing.Size(1093, 514);
            this.rtxResult.TabIndex = 14;
            this.rtxResult.Text = "";
            // 
            // label18
            // 
            this.label18.AutoSize = true;
            this.label18.Location = new System.Drawing.Point(1147, 28);
            this.label18.Name = "label18";
            this.label18.Size = new System.Drawing.Size(174, 15);
            this.label18.TabIndex = 15;
            this.label18.Text = "List型数据间的分隔符：";
            // 
            // label19
            // 
            this.label19.AutoSize = true;
            this.label19.Location = new System.Drawing.Point(1147, 58);
            this.label19.Name = "label19";
            this.label19.Size = new System.Drawing.Size(166, 15);
            this.label19.TabIndex = 16;
            this.label19.Text = "Set型数据间的分隔符：";
            // 
            // label20
            // 
            this.label20.AutoSize = true;
            this.label20.Location = new System.Drawing.Point(1147, 88);
            this.label20.Name = "label20";
            this.label20.Size = new System.Drawing.Size(174, 15);
            this.label20.TabIndex = 17;
            this.label20.Text = "Hash型数据间的分隔符：";
            // 
            // label21
            // 
            this.label21.AutoSize = true;
            this.label21.Location = new System.Drawing.Point(1147, 118);
            this.label21.Name = "label21";
            this.label21.Size = new System.Drawing.Size(189, 15);
            this.label21.TabIndex = 18;
            this.label21.Text = "Hash型键值对间的分隔符：";
            // 
            // label22
            // 
            this.label22.AutoSize = true;
            this.label22.Location = new System.Drawing.Point(1147, 148);
            this.label22.Name = "label22";
            this.label22.Size = new System.Drawing.Size(174, 15);
            this.label22.TabIndex = 19;
            this.label22.Text = "ZSet型数据间的分隔符：";
            // 
            // label23
            // 
            this.label23.AutoSize = true;
            this.label23.Location = new System.Drawing.Point(1147, 208);
            this.label23.Name = "label23";
            this.label23.Size = new System.Drawing.Size(231, 15);
            this.label23.TabIndex = 20;
            this.label23.Text = "ZSet型name、score间的分隔符：";
            // 
            // txtListDataSpliteStr
            // 
            this.txtListDataSpliteStr.Location = new System.Drawing.Point(1380, 25);
            this.txtListDataSpliteStr.Name = "txtListDataSpliteStr";
            this.txtListDataSpliteStr.Size = new System.Drawing.Size(110, 25);
            this.txtListDataSpliteStr.TabIndex = 21;
            this.txtListDataSpliteStr.Text = "\\n";
            // 
            // txtSetDataSpliteStr
            // 
            this.txtSetDataSpliteStr.Location = new System.Drawing.Point(1380, 55);
            this.txtSetDataSpliteStr.Name = "txtSetDataSpliteStr";
            this.txtSetDataSpliteStr.Size = new System.Drawing.Size(110, 25);
            this.txtSetDataSpliteStr.TabIndex = 22;
            this.txtSetDataSpliteStr.Text = "\\n";
            // 
            // txtHashDataSpliteStr
            // 
            this.txtHashDataSpliteStr.Location = new System.Drawing.Point(1380, 85);
            this.txtHashDataSpliteStr.Name = "txtHashDataSpliteStr";
            this.txtHashDataSpliteStr.Size = new System.Drawing.Size(110, 25);
            this.txtHashDataSpliteStr.TabIndex = 22;
            this.txtHashDataSpliteStr.Text = "\\n";
            // 
            // txtHashKeyValuePairsSpliteStr
            // 
            this.txtHashKeyValuePairsSpliteStr.Location = new System.Drawing.Point(1380, 115);
            this.txtHashKeyValuePairsSpliteStr.Name = "txtHashKeyValuePairsSpliteStr";
            this.txtHashKeyValuePairsSpliteStr.Size = new System.Drawing.Size(110, 25);
            this.txtHashKeyValuePairsSpliteStr.TabIndex = 22;
            this.txtHashKeyValuePairsSpliteStr.Text = ":";
            // 
            // txtZSetDataSpliteStr
            // 
            this.txtZSetDataSpliteStr.Location = new System.Drawing.Point(1380, 145);
            this.txtZSetDataSpliteStr.Name = "txtZSetDataSpliteStr";
            this.txtZSetDataSpliteStr.Size = new System.Drawing.Size(110, 25);
            this.txtZSetDataSpliteStr.TabIndex = 22;
            this.txtZSetDataSpliteStr.Text = "\\n";
            // 
            // txtZSetNameScoreSpliteStr
            // 
            this.txtZSetNameScoreSpliteStr.Location = new System.Drawing.Point(1380, 204);
            this.txtZSetNameScoreSpliteStr.Name = "txtZSetNameScoreSpliteStr";
            this.txtZSetNameScoreSpliteStr.Size = new System.Drawing.Size(110, 25);
            this.txtZSetNameScoreSpliteStr.TabIndex = 22;
            this.txtZSetNameScoreSpliteStr.Text = ":";
            // 
            // label24
            // 
            this.label24.AutoSize = true;
            this.label24.Location = new System.Drawing.Point(1147, 178);
            this.label24.Name = "label24";
            this.label24.Size = new System.Drawing.Size(219, 15);
            this.label24.TabIndex = 23;
            this.label24.Text = "ZSet型排名和数据间的分隔符：";
            // 
            // txtZSetRankDataSpliteStr
            // 
            this.txtZSetRankDataSpliteStr.Location = new System.Drawing.Point(1380, 175);
            this.txtZSetRankDataSpliteStr.Name = "txtZSetRankDataSpliteStr";
            this.txtZSetRankDataSpliteStr.Size = new System.Drawing.Size(110, 25);
            this.txtZSetRankDataSpliteStr.TabIndex = 24;
            this.txtZSetRankDataSpliteStr.Text = ". ";
            // 
            // btnFavorite
            // 
            this.btnFavorite.Location = new System.Drawing.Point(1026, 248);
            this.btnFavorite.Name = "btnFavorite";
            this.btnFavorite.Size = new System.Drawing.Size(88, 31);
            this.btnFavorite.TabIndex = 25;
            this.btnFavorite.Text = "加入收藏";
            this.btnFavorite.UseVisualStyleBackColor = true;
            this.btnFavorite.Visible = false;
            this.btnFavorite.Click += new System.EventHandler(this.btnFavorite_Click);
            // 
            // label26
            // 
            this.label26.AutoSize = true;
            this.label26.Location = new System.Drawing.Point(1147, 310);
            this.label26.Name = "label26";
            this.label26.Size = new System.Drawing.Size(322, 15);
            this.label26.TabIndex = 26;
            this.label26.Text = "收藏夹（双击条目可使用，右键弹出选项菜单）\r\n";
            // 
            // lstFavorite
            // 
            this.lstFavorite.ContextMenuStrip = this.cmsFavorite;
            this.lstFavorite.FormattingEnabled = true;
            this.lstFavorite.ItemHeight = 15;
            this.lstFavorite.Location = new System.Drawing.Point(1150, 331);
            this.lstFavorite.Name = "lstFavorite";
            this.lstFavorite.Size = new System.Drawing.Size(340, 229);
            this.lstFavorite.TabIndex = 27;
            this.lstFavorite.MouseDoubleClick += new System.Windows.Forms.MouseEventHandler(this.lstFavorite_MouseDoubleClick);
            this.lstFavorite.MouseDown += new System.Windows.Forms.MouseEventHandler(this.lstFavorite_MouseDown);
            // 
            // cmsFavorite
            // 
            this.cmsFavorite.ImageScalingSize = new System.Drawing.Size(20, 20);
            this.cmsFavorite.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsmiFavoriteTop,
            this.tsmiFavoriteDelete,
            this.tsmiFavoriteClear});
            this.cmsFavorite.Name = "cmsFavorite";
            this.cmsFavorite.Size = new System.Drawing.Size(115, 82);
            // 
            // tsmiFavoriteTop
            // 
            this.tsmiFavoriteTop.Name = "tsmiFavoriteTop";
            this.tsmiFavoriteTop.Size = new System.Drawing.Size(114, 26);
            this.tsmiFavoriteTop.Text = "置顶";
            this.tsmiFavoriteTop.Click += new System.EventHandler(this.tsmiFavoriteTop_Click);
            // 
            // tsmiFavoriteDelete
            // 
            this.tsmiFavoriteDelete.Name = "tsmiFavoriteDelete";
            this.tsmiFavoriteDelete.Size = new System.Drawing.Size(114, 26);
            this.tsmiFavoriteDelete.Text = "删除";
            this.tsmiFavoriteDelete.Click += new System.EventHandler(this.tsmiFavoriteDelete_Click);
            // 
            // tsmiFavoriteClear
            // 
            this.tsmiFavoriteClear.Name = "tsmiFavoriteClear";
            this.tsmiFavoriteClear.Size = new System.Drawing.Size(114, 26);
            this.tsmiFavoriteClear.Text = "清空";
            this.tsmiFavoriteClear.Click += new System.EventHandler(this.tsmiFavoriteClear_Click);
            // 
            // label27
            // 
            this.label27.AutoSize = true;
            this.label27.Location = new System.Drawing.Point(1147, 571);
            this.label27.Name = "label27";
            this.label27.Size = new System.Drawing.Size(337, 15);
            this.label27.TabIndex = 28;
            this.label27.Text = "历史记录（双击条目可使用，右键弹出选项菜单）";
            // 
            // lstHistory
            // 
            this.lstHistory.ContextMenuStrip = this.cmsHistory;
            this.lstHistory.FormattingEnabled = true;
            this.lstHistory.ItemHeight = 15;
            this.lstHistory.Location = new System.Drawing.Point(1150, 595);
            this.lstHistory.Name = "lstHistory";
            this.lstHistory.Size = new System.Drawing.Size(340, 229);
            this.lstHistory.TabIndex = 29;
            this.lstHistory.MouseDoubleClick += new System.Windows.Forms.MouseEventHandler(this.lstHistory_MouseDoubleClick);
            this.lstHistory.MouseDown += new System.Windows.Forms.MouseEventHandler(this.lstHistory_MouseDown);
            // 
            // cmsHistory
            // 
            this.cmsHistory.ImageScalingSize = new System.Drawing.Size(20, 20);
            this.cmsHistory.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.tsmiHistoryTop,
            this.tsmiHistoryDelete,
            this.tsmiHistoryClear});
            this.cmsHistory.Name = "cmsHistory";
            this.cmsHistory.Size = new System.Drawing.Size(115, 82);
            // 
            // tsmiHistoryTop
            // 
            this.tsmiHistoryTop.Name = "tsmiHistoryTop";
            this.tsmiHistoryTop.Size = new System.Drawing.Size(216, 26);
            this.tsmiHistoryTop.Text = "置顶";
            this.tsmiHistoryTop.Click += new System.EventHandler(this.tsmiHistoryTop_Click);
            // 
            // tsmiHistoryDelete
            // 
            this.tsmiHistoryDelete.Name = "tsmiHistoryDelete";
            this.tsmiHistoryDelete.Size = new System.Drawing.Size(216, 26);
            this.tsmiHistoryDelete.Text = "删除";
            this.tsmiHistoryDelete.Click += new System.EventHandler(this.tsmiHistoryDelete_Click);
            // 
            // tsmiHistoryClear
            // 
            this.tsmiHistoryClear.Name = "tsmiHistoryClear";
            this.tsmiHistoryClear.Size = new System.Drawing.Size(216, 26);
            this.tsmiHistoryClear.Text = "清空";
            this.tsmiHistoryClear.Click += new System.EventHandler(this.tsmiHistoryClear_Click);
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 15F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1518, 842);
            this.Controls.Add(this.lstHistory);
            this.Controls.Add(this.label27);
            this.Controls.Add(this.lstFavorite);
            this.Controls.Add(this.label26);
            this.Controls.Add(this.btnFavorite);
            this.Controls.Add(this.txtZSetRankDataSpliteStr);
            this.Controls.Add(this.label24);
            this.Controls.Add(this.txtZSetNameScoreSpliteStr);
            this.Controls.Add(this.txtZSetDataSpliteStr);
            this.Controls.Add(this.txtHashKeyValuePairsSpliteStr);
            this.Controls.Add(this.txtHashDataSpliteStr);
            this.Controls.Add(this.txtSetDataSpliteStr);
            this.Controls.Add(this.grpZSetFilter);
            this.Controls.Add(this.txtListDataSpliteStr);
            this.Controls.Add(this.label23);
            this.Controls.Add(this.label22);
            this.Controls.Add(this.label21);
            this.Controls.Add(this.label20);
            this.Controls.Add(this.label19);
            this.Controls.Add(this.label18);
            this.Controls.Add(this.rtxResult);
            this.Controls.Add(this.btnSearch);
            this.Controls.Add(this.grpListFilter);
            this.Controls.Add(this.grpHashFilter);
            this.Controls.Add(this.lblRemark);
            this.Controls.Add(this.label6);
            this.Controls.Add(this.cmbChooseDataType);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.txtProtobufClassName);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.txtKeyName);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.cmbChooseKey);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.cmbChooseGroup);
            this.Controls.Add(this.label1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.Fixed3D;
            this.MaximizeBox = false;
            this.Name = "MainForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "Redis数据可视化查看工具 by 张齐";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.MainForm_FormClosing);
            this.grpHashFilter.ResumeLayout(false);
            this.grpHashFilter.PerformLayout();
            this.grpListFilter.ResumeLayout(false);
            this.grpListFilter.PerformLayout();
            this.grpZSetFilter.ResumeLayout(false);
            this.grpZSetFilter.PerformLayout();
            this.cmsFavorite.ResumeLayout(false);
            this.cmsHistory.ResumeLayout(false);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.ComboBox cmbChooseGroup;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.ComboBox cmbChooseKey;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.TextBox txtKeyName;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.TextBox txtProtobufClassName;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.ComboBox cmbChooseDataType;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.Label lblRemark;
        private System.Windows.Forms.GroupBox grpHashFilter;
        private System.Windows.Forms.Button btnSearch;
        private System.Windows.Forms.Label label9;
        private System.Windows.Forms.TextBox txtHashKey;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.GroupBox grpListFilter;
        private System.Windows.Forms.Label label11;
        private System.Windows.Forms.TextBox txtListRangeEnd;
        private System.Windows.Forms.Label label10;
        private System.Windows.Forms.TextBox txtListRangeStart;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.GroupBox grpZSetFilter;
        private System.Windows.Forms.Label label17;
        private System.Windows.Forms.TextBox txtZSetRankRangeEnd;
        private System.Windows.Forms.Label label16;
        private System.Windows.Forms.TextBox txtZSetRankRangeStart;
        private System.Windows.Forms.Label label15;
        private System.Windows.Forms.Label label14;
        private System.Windows.Forms.TextBox txtZSetScoreRangeMax;
        private System.Windows.Forms.Label label13;
        private System.Windows.Forms.TextBox txtZSetScoreRangeMin;
        private System.Windows.Forms.Label label12;
        private System.Windows.Forms.RadioButton rdoZSetGetDataByRank;
        private System.Windows.Forms.RadioButton rdoZSetGetDataByScore;
        private System.Windows.Forms.CheckBox chkIsZSetScoreOrderByAsc;
        private System.Windows.Forms.RichTextBox rtxResult;
        private System.Windows.Forms.Label label18;
        private System.Windows.Forms.Label label19;
        private System.Windows.Forms.Label label20;
        private System.Windows.Forms.Label label21;
        private System.Windows.Forms.Label label22;
        private System.Windows.Forms.Label label23;
        private System.Windows.Forms.TextBox txtListDataSpliteStr;
        private System.Windows.Forms.TextBox txtSetDataSpliteStr;
        private System.Windows.Forms.TextBox txtHashDataSpliteStr;
        private System.Windows.Forms.TextBox txtHashKeyValuePairsSpliteStr;
        private System.Windows.Forms.TextBox txtZSetDataSpliteStr;
        private System.Windows.Forms.TextBox txtZSetNameScoreSpliteStr;
        private System.Windows.Forms.Label label24;
        private System.Windows.Forms.TextBox txtZSetRankDataSpliteStr;
        private System.Windows.Forms.TextBox txtZSetItemName;
        private System.Windows.Forms.Label label25;
        private System.Windows.Forms.RadioButton rdoZSetGetItem;
        private System.Windows.Forms.Button btnFavorite;
        private System.Windows.Forms.Label label26;
        private System.Windows.Forms.ListBox lstFavorite;
        private System.Windows.Forms.Label label27;
        private System.Windows.Forms.ListBox lstHistory;
        private System.Windows.Forms.ContextMenuStrip cmsFavorite;
        private System.Windows.Forms.ContextMenuStrip cmsHistory;
        private System.Windows.Forms.ToolStripMenuItem tsmiFavoriteTop;
        private System.Windows.Forms.ToolStripMenuItem tsmiFavoriteDelete;
        private System.Windows.Forms.ToolStripMenuItem tsmiFavoriteClear;
        private System.Windows.Forms.ToolStripMenuItem tsmiHistoryTop;
        private System.Windows.Forms.ToolStripMenuItem tsmiHistoryDelete;
        private System.Windows.Forms.ToolStripMenuItem tsmiHistoryClear;
    }
}

