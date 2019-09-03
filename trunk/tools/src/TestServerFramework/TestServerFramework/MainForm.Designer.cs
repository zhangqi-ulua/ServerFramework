namespace TestServerFramework
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
            this.label1 = new System.Windows.Forms.Label();
            this.lblActionState = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.lblBattleId = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.lblBattleType = new System.Windows.Forms.Label();
            this.btnMatch = new System.Windows.Forms.Button();
            this.btnCancelMatch = new System.Windows.Forms.Button();
            this.btnCell_0 = new System.Windows.Forms.Button();
            this.lblPlaySeqTips = new System.Windows.Forms.Label();
            this.grpBattleArena = new System.Windows.Forms.GroupBox();
            this.btnCell_1 = new System.Windows.Forms.Button();
            this.btnCell_2 = new System.Windows.Forms.Button();
            this.btnCell_5 = new System.Windows.Forms.Button();
            this.btnCell_4 = new System.Windows.Forms.Button();
            this.btnCell_3 = new System.Windows.Forms.Button();
            this.btnCell_8 = new System.Windows.Forms.Button();
            this.btnCell_7 = new System.Windows.Forms.Button();
            this.btnCell_6 = new System.Windows.Forms.Button();
            this.lblCurrentTurnInfo = new System.Windows.Forms.Label();
            this.grpBattleArena.SuspendLayout();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(37, 38);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(82, 15);
            this.label1.TabIndex = 0;
            this.label1.Text = "当前行为：";
            // 
            // lblActionState
            // 
            this.lblActionState.AutoSize = true;
            this.lblActionState.Location = new System.Drawing.Point(125, 38);
            this.lblActionState.Name = "lblActionState";
            this.lblActionState.Size = new System.Drawing.Size(87, 15);
            this.lblActionState.TabIndex = 1;
            this.lblActionState.Text = "ActionNone";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(41, 64);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(78, 15);
            this.label2.TabIndex = 2;
            this.label2.Text = "batteId：";
            // 
            // lblBattleId
            // 
            this.lblBattleId.AutoSize = true;
            this.lblBattleId.Location = new System.Drawing.Point(125, 64);
            this.lblBattleId.Name = "lblBattleId";
            this.lblBattleId.Size = new System.Drawing.Size(39, 15);
            this.lblBattleId.TabIndex = 3;
            this.lblBattleId.Text = "null";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(37, 95);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(82, 15);
            this.label3.TabIndex = 4;
            this.label3.Text = "对战类型：";
            // 
            // lblBattleType
            // 
            this.lblBattleType.AutoSize = true;
            this.lblBattleType.Location = new System.Drawing.Point(125, 95);
            this.lblBattleType.Name = "lblBattleType";
            this.lblBattleType.Size = new System.Drawing.Size(39, 15);
            this.lblBattleType.TabIndex = 5;
            this.lblBattleType.Text = "null";
            // 
            // btnMatch
            // 
            this.btnMatch.Font = new System.Drawing.Font("宋体", 10.8F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnMatch.Location = new System.Drawing.Point(456, 37);
            this.btnMatch.Name = "btnMatch";
            this.btnMatch.Size = new System.Drawing.Size(107, 42);
            this.btnMatch.TabIndex = 6;
            this.btnMatch.Text = "匹配";
            this.btnMatch.UseVisualStyleBackColor = true;
            this.btnMatch.Click += new System.EventHandler(this.btnMatch_Click);
            // 
            // btnCancelMatch
            // 
            this.btnCancelMatch.Font = new System.Drawing.Font("宋体", 10.8F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnCancelMatch.Location = new System.Drawing.Point(456, 85);
            this.btnCancelMatch.Name = "btnCancelMatch";
            this.btnCancelMatch.Size = new System.Drawing.Size(107, 42);
            this.btnCancelMatch.TabIndex = 7;
            this.btnCancelMatch.Text = "取消匹配";
            this.btnCancelMatch.UseVisualStyleBackColor = true;
            this.btnCancelMatch.Click += new System.EventHandler(this.btnCancelMatch_Click);
            // 
            // btnCell_0
            // 
            this.btnCell_0.Font = new System.Drawing.Font("宋体", 36F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnCell_0.Location = new System.Drawing.Point(124, 71);
            this.btnCell_0.Name = "btnCell_0";
            this.btnCell_0.Size = new System.Drawing.Size(90, 90);
            this.btnCell_0.TabIndex = 8;
            this.btnCell_0.UseVisualStyleBackColor = true;
            // 
            // lblPlaySeqTips
            // 
            this.lblPlaySeqTips.AutoSize = true;
            this.lblPlaySeqTips.Location = new System.Drawing.Point(381, 33);
            this.lblPlaySeqTips.Name = "lblPlaySeqTips";
            this.lblPlaySeqTips.Size = new System.Drawing.Size(142, 15);
            this.lblPlaySeqTips.TabIndex = 9;
            this.lblPlaySeqTips.Text = "您是先手，棋子为×";
            // 
            // grpBattleArena
            // 
            this.grpBattleArena.Controls.Add(this.lblCurrentTurnInfo);
            this.grpBattleArena.Controls.Add(this.btnCell_8);
            this.grpBattleArena.Controls.Add(this.btnCell_7);
            this.grpBattleArena.Controls.Add(this.btnCell_6);
            this.grpBattleArena.Controls.Add(this.btnCell_5);
            this.grpBattleArena.Controls.Add(this.btnCell_4);
            this.grpBattleArena.Controls.Add(this.btnCell_3);
            this.grpBattleArena.Controls.Add(this.btnCell_2);
            this.grpBattleArena.Controls.Add(this.btnCell_1);
            this.grpBattleArena.Controls.Add(this.lblPlaySeqTips);
            this.grpBattleArena.Controls.Add(this.btnCell_0);
            this.grpBattleArena.Location = new System.Drawing.Point(40, 140);
            this.grpBattleArena.Name = "grpBattleArena";
            this.grpBattleArena.Size = new System.Drawing.Size(540, 388);
            this.grpBattleArena.TabIndex = 10;
            this.grpBattleArena.TabStop = false;
            this.grpBattleArena.Text = "对战区";
            // 
            // btnCell_1
            // 
            this.btnCell_1.Font = new System.Drawing.Font("宋体", 36F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnCell_1.Location = new System.Drawing.Point(220, 71);
            this.btnCell_1.Name = "btnCell_1";
            this.btnCell_1.Size = new System.Drawing.Size(90, 90);
            this.btnCell_1.TabIndex = 10;
            this.btnCell_1.UseVisualStyleBackColor = true;
            // 
            // btnCell_2
            // 
            this.btnCell_2.Font = new System.Drawing.Font("宋体", 36F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnCell_2.Location = new System.Drawing.Point(316, 71);
            this.btnCell_2.Name = "btnCell_2";
            this.btnCell_2.Size = new System.Drawing.Size(90, 90);
            this.btnCell_2.TabIndex = 11;
            this.btnCell_2.UseVisualStyleBackColor = true;
            // 
            // btnCell_5
            // 
            this.btnCell_5.Font = new System.Drawing.Font("宋体", 36F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnCell_5.Location = new System.Drawing.Point(316, 167);
            this.btnCell_5.Name = "btnCell_5";
            this.btnCell_5.Size = new System.Drawing.Size(90, 90);
            this.btnCell_5.TabIndex = 14;
            this.btnCell_5.UseVisualStyleBackColor = true;
            // 
            // btnCell_4
            // 
            this.btnCell_4.Font = new System.Drawing.Font("宋体", 36F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnCell_4.Location = new System.Drawing.Point(220, 167);
            this.btnCell_4.Name = "btnCell_4";
            this.btnCell_4.Size = new System.Drawing.Size(90, 90);
            this.btnCell_4.TabIndex = 13;
            this.btnCell_4.UseVisualStyleBackColor = true;
            // 
            // btnCell_3
            // 
            this.btnCell_3.Font = new System.Drawing.Font("宋体", 36F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnCell_3.Location = new System.Drawing.Point(124, 167);
            this.btnCell_3.Name = "btnCell_3";
            this.btnCell_3.Size = new System.Drawing.Size(90, 90);
            this.btnCell_3.TabIndex = 12;
            this.btnCell_3.UseVisualStyleBackColor = true;
            // 
            // btnCell_8
            // 
            this.btnCell_8.Font = new System.Drawing.Font("宋体", 36F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnCell_8.Location = new System.Drawing.Point(316, 263);
            this.btnCell_8.Name = "btnCell_8";
            this.btnCell_8.Size = new System.Drawing.Size(90, 90);
            this.btnCell_8.TabIndex = 17;
            this.btnCell_8.UseVisualStyleBackColor = true;
            // 
            // btnCell_7
            // 
            this.btnCell_7.Font = new System.Drawing.Font("宋体", 36F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnCell_7.Location = new System.Drawing.Point(220, 263);
            this.btnCell_7.Name = "btnCell_7";
            this.btnCell_7.Size = new System.Drawing.Size(90, 90);
            this.btnCell_7.TabIndex = 16;
            this.btnCell_7.UseVisualStyleBackColor = true;
            // 
            // btnCell_6
            // 
            this.btnCell_6.Font = new System.Drawing.Font("宋体", 36F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.btnCell_6.Location = new System.Drawing.Point(124, 263);
            this.btnCell_6.Name = "btnCell_6";
            this.btnCell_6.Size = new System.Drawing.Size(90, 90);
            this.btnCell_6.TabIndex = 15;
            this.btnCell_6.UseVisualStyleBackColor = true;
            // 
            // lblCurrentTurnInfo
            // 
            this.lblCurrentTurnInfo.Location = new System.Drawing.Point(182, 23);
            this.lblCurrentTurnInfo.Name = "lblCurrentTurnInfo";
            this.lblCurrentTurnInfo.Size = new System.Drawing.Size(171, 35);
            this.lblCurrentTurnInfo.TabIndex = 18;
            this.lblCurrentTurnInfo.Text = "第1回合\r\n请您等待对手行动";
            this.lblCurrentTurnInfo.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(8F, 15F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(617, 559);
            this.Controls.Add(this.grpBattleArena);
            this.Controls.Add(this.btnCancelMatch);
            this.Controls.Add(this.btnMatch);
            this.Controls.Add(this.lblBattleType);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.lblBattleId);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.lblActionState);
            this.Controls.Add(this.label1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.Fixed3D;
            this.MaximizeBox = false;
            this.Name = "MainForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "测试服务器框架工具 by 张齐";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.MainForm_FormClosing);
            this.Load += new System.EventHandler(this.MainForm_Load);
            this.Shown += new System.EventHandler(this.MainForm_Shown);
            this.grpBattleArena.ResumeLayout(false);
            this.grpBattleArena.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label lblActionState;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label lblBattleId;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label lblBattleType;
        private System.Windows.Forms.Button btnMatch;
        private System.Windows.Forms.Button btnCancelMatch;
        private System.Windows.Forms.Button btnCell_0;
        private System.Windows.Forms.Label lblPlaySeqTips;
        private System.Windows.Forms.GroupBox grpBattleArena;
        private System.Windows.Forms.Button btnCell_1;
        private System.Windows.Forms.Button btnCell_5;
        private System.Windows.Forms.Button btnCell_4;
        private System.Windows.Forms.Button btnCell_3;
        private System.Windows.Forms.Button btnCell_2;
        private System.Windows.Forms.Button btnCell_8;
        private System.Windows.Forms.Button btnCell_7;
        private System.Windows.Forms.Button btnCell_6;
        private System.Windows.Forms.Label lblCurrentTurnInfo;
    }
}

