using Google.ProtocolBuffers;
using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Text;
using System.Windows.Forms;

namespace RedisDataViewer
{
    public partial class MainForm : Form
    {
        // key:组名, value:对应的分组信息
        private Dictionary<string, GroupNode> groupInfoDict = new Dictionary<string, GroupNode>();

        // key:分组ComboBox中的index, value:对应的GroupNode
        private Dictionary<int, GroupNode> groupComboBoxIndexToGroupNodeDict = new Dictionary<int, GroupNode>();
        // key:选中一个分组后，在key筛选ComboBox中生成项目的index, value:对应的redisKeyId
        private Dictionary<int, int> keyComboBoxIndexToKeyIdDict = new Dictionary<int, int>();

        // 收藏夹中的redisKeyId
        List<int> favoriteList = new List<int>();
        // 最近使用历史中的redisKeyId
        List<int> historyList = new List<int>();

        // key:redisKeyId, value:对应配置文件中的行号
        private Dictionary<int, int> redisKeyIdToLineNumDict = new Dictionary<int, int>();
        // key:desc, value:对应配置文件中的行号
        private Dictionary<string, int> descToLineNumDict = new Dictionary<string, int>();
        // key:protobufClassName, value:该protobuf类对应的ParseFrom函数
        private Dictionary<string, MethodInfo> protobufClassNameToParseFromMethodDict = new Dictionary<string, MethodInfo>();
        // key:redisKeyId, value:该条配置对应的RedisKeyInfo类
        private Dictionary<int, RedisKeyInfo> redisKeyIdToRedisKeyInfoDict = new Dictionary<int, RedisKeyInfo>();

        public MainForm()
        {
            InitializeComponent();

            InitRedisKeyConfig();
            if (groupInfoDict.Count > 0)
            {
                lstFavorite.Visible = true;
                lstHistory.Visible = true;

                InitGroupComboBox();
                InitFavorite();
                InitHistory();
            }
            else
            {
                btnFavorite.Visible = false;
                lstFavorite.Visible = false;
                lstHistory.Visible = false;
            }
        }

        private MethodInfo GetProtobufParseFromMethod(string protobufClassName)
        {
            if (protobufClassNameToParseFromMethodDict.ContainsKey(protobufClassName))
                return protobufClassNameToParseFromMethodDict[protobufClassName];
            else
            {
                try
                {
                    Type protoClassType = Type.GetType(AppValues.PROTOBUF_PACKAGE_NAME + protobufClassName, true, false);
                    if (protoClassType == null)
                        return null;

                    MethodInfo protoParseFromMethod = protoClassType.GetMethod("ParseFrom", BindingFlags.Public | BindingFlags.Static, null, new Type[] { typeof(byte[]) }, null);
                    protobufClassNameToParseFromMethodDict.Add(protobufClassName, protoParseFromMethod);
                    return protoParseFromMethod;
                }
                catch
                {
                    return null;
                }
            }
        }

        // 读取预设的redisKey配置
        private void InitRedisKeyConfig()
        {
            string configPath = Path.Combine(AppValues.PROGRAM_FOLDER_PATH, AppValues.REDIS_KEY_CONFIG_FILE_NAME);
            if (File.Exists(configPath) == true)
            {
                using (StreamReader reader = new StreamReader(configPath, Encoding.Unicode))
                {
                    string line = null;
                    int lineNum = 0;
                    while ((line = reader.ReadLine()) != null)
                    {
                        line = line.Trim();
                        ++lineNum;

                        // 忽略空行和以#开头的注释行
                        if (string.IsNullOrEmpty(line) || line.StartsWith("#"))
                            continue;

                        string[] paramStr = line.Split(new char[] { '@' }, StringSplitOptions.None);
                        if (paramStr.Length != 7)
                        {
                            string tips = string.Format("RedisKey配置文件第{0}行错误，参数个数错误，格式应为“唯一id@分组（不分组则留空）@描述@key名格式@protobuf数据所对应的protobuf类名（非protobuf数据留空）@redis的数据类型（string、hash、list、set、zset）@备注信息”", lineNum);
                            MessageBox.Show(tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            Environment.Exit(-1);
                        }
                        // keyId
                        int keyId;
                        if (int.TryParse(paramStr[0].Trim(), out keyId) == false)
                        {
                            string tips = string.Format("RedisKey配置文件第{0}行错误，第1个参数错误，输入的唯一id不是int型合法数字， 输入值为{1}", lineNum, paramStr[0]);
                            MessageBox.Show(tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            Environment.Exit(-1);
                        }
                        if (redisKeyIdToLineNumDict.ContainsKey(keyId))
                        {
                            string tips = string.Format("RedisKey配置文件第{0}行错误，第1个参数错误，输入的唯一id重复， 与第{1}行配置的id均为{2}", lineNum, redisKeyIdToLineNumDict[keyId], keyId);
                            MessageBox.Show(tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            Environment.Exit(-1);
                        }
                        redisKeyIdToLineNumDict.Add(keyId, lineNum);
                        // 分组
                        GroupNode groupNode = null;
                        string groupStr = paramStr[1].Trim();
                        if (string.IsNullOrEmpty(groupStr) == false)
                        {
                            string[] groupParamStr = groupStr.Split(new char[] { '|' }, StringSplitOptions.RemoveEmptyEntries);
                            Dictionary<string, GroupNode> tempGroupInfo = groupInfoDict;
                            GroupNode tempNode = null;
                            foreach (string tempOneGroup in groupParamStr)
                            {
                                string oneGroupName = tempOneGroup.Trim();
                                if (tempGroupInfo.ContainsKey(oneGroupName) == false)
                                {
                                    GroupNode newNode = new GroupNode(oneGroupName);
                                    tempGroupInfo.Add(oneGroupName, newNode);
                                }
                                tempNode = tempGroupInfo[oneGroupName];
                                tempGroupInfo = tempNode.ChildGroup;
                            }
                            tempNode.KeyIds.Add(keyId);
                            groupNode = tempNode;
                        }
                        else
                        {
                            if (groupInfoDict.ContainsKey(string.Empty) == false)
                            {
                                GroupNode newNode = new GroupNode(string.Empty);
                                groupInfoDict.Add(string.Empty, newNode);
                            }
                            groupInfoDict[string.Empty].KeyIds.Add(keyId);
                            groupNode = groupInfoDict[string.Empty];
                        }
                        // 描述
                        string desc = paramStr[2].Trim();
                        if (string.IsNullOrEmpty(desc))
                        {
                            string tips = string.Format("RedisKey配置文件第{0}行错误，第3个参数错误，描述必须填写，不能为空", lineNum);
                            MessageBox.Show(tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            Environment.Exit(-1);
                        }
                        if (descToLineNumDict.ContainsKey(desc) == true)
                        {
                            string tips = string.Format("RedisKey配置文件第{0}行错误，第3个参数错误，输入的key描述重复， 与第{1}行配置的描述均为{2}", lineNum, descToLineNumDict[desc], desc);
                            MessageBox.Show(tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            Environment.Exit(-1);
                        }
                        descToLineNumDict.Add(desc, lineNum);
                        // key名
                        string keyName = paramStr[3].Trim();
                        if (string.IsNullOrEmpty(keyName))
                        {
                            string tips = string.Format("RedisKey配置文件第{0}行错误，第4个参数错误，key名必须填写，不能为空", lineNum);
                            MessageBox.Show(tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            Environment.Exit(-1);
                        }
                        // protobuf类名
                        string protobufClassName = paramStr[4].Trim();
                        if (string.IsNullOrEmpty(protobufClassName) == false && protobufClassNameToParseFromMethodDict.ContainsKey(protobufClassName) == false)
                        {
                            try
                            {
                                Type protoClassType = Type.GetType(AppValues.PROTOBUF_PACKAGE_NAME + protobufClassName, true, false);
                                if (protoClassType == null)
                                {
                                    string tips = string.Format("RedisKey配置文件第{0}行错误，第5个参数错误，不存在名为{1}的protobuf类，请注意区分大小写", lineNum, protobufClassName);
                                    MessageBox.Show(tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                                    return;
                                }
                                MethodInfo protoParseFromMethod = protoClassType.GetMethod("ParseFrom", BindingFlags.Public | BindingFlags.Static, null, new Type[] { typeof(byte[]) }, null);
                                protobufClassNameToParseFromMethodDict.Add(protobufClassName, protoParseFromMethod);
                            }
                            catch (Exception e)
                            {
                                string tips = string.Format("RedisKey配置文件第{0}行错误，第5个参数错误，解析名为{1}的protobuf类出错，错误原因为：{2}", lineNum, protobufClassName, e);
                                MessageBox.Show(tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                                return;
                            }
                        }
                        // redis数据类型
                        RedisDataTypeEnum dataType = RedisDataTypeEnum.String;
                        string redisDataTypeStr = paramStr[5].Trim();
                        if (string.IsNullOrEmpty(redisDataTypeStr))
                        {
                            string tips = string.Format("RedisKey配置文件第{0}行错误，第6个参数错误，redis数据类型必须填写，不能为空", lineNum);
                            MessageBox.Show(tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            Environment.Exit(-1);
                        }
                        if ("string".Equals(redisDataTypeStr, StringComparison.CurrentCultureIgnoreCase))
                            dataType = RedisDataTypeEnum.String;
                        else if ("hash".Equals(redisDataTypeStr, StringComparison.CurrentCultureIgnoreCase))
                            dataType = RedisDataTypeEnum.Hash;
                        else if ("list".Equals(redisDataTypeStr, StringComparison.CurrentCultureIgnoreCase))
                            dataType = RedisDataTypeEnum.List;
                        else if ("set".Equals(redisDataTypeStr, StringComparison.CurrentCultureIgnoreCase))
                            dataType = RedisDataTypeEnum.Set;
                        else if ("zset".Equals(redisDataTypeStr, StringComparison.CurrentCultureIgnoreCase))
                            dataType = RedisDataTypeEnum.ZSet;
                        else
                        {
                            string tips = string.Format("RedisKey配置文件第{0}行错误，第6个参数错误，redis数据类型非法，输入值为{1}，而redis的数据类型只有string、hash、list、set、zset", lineNum, redisDataTypeStr);
                            MessageBox.Show(tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            Environment.Exit(-1);
                        }
                        // 备注
                        string remark = paramStr[6].Trim();

                        RedisKeyInfo redisKeyInfo = new RedisKeyInfo();
                        redisKeyInfo.KeyId = keyId;
                        redisKeyInfo.GroupNode = groupNode;
                        redisKeyInfo.Desc = desc;
                        redisKeyInfo.RedisKeyName = keyName;
                        redisKeyInfo.ProtobufClassName = protobufClassName;
                        redisKeyInfo.RedisDataType = dataType;
                        redisKeyInfo.Remark = remark;
                        redisKeyIdToRedisKeyInfoDict.Add(keyId, redisKeyInfo);
                    }
                }
            }
            else
            {
                string tips = string.Format("未找到本工具对应的{0}配置文件，将无法载入预设的RedsKey配置，也无法加载收藏夹和使用历史", AppValues.REDIS_KEY_CONFIG_FILE_NAME);
                MessageBox.Show(this, tips, "警告", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                return;
            }
        }

        private void InitGroupComboBox()
        {
            // 根据预设key配置，生成分组菜单
            int index = 0;
            int level = 0;
            // 没有分组的放在最前面
            if (groupInfoDict.ContainsKey(string.Empty))
            {
                groupComboBoxIndexToGroupNodeDict.Add(index, groupInfoDict[string.Empty]);
                cmbChooseGroup.Items.Add("(未分组)");
                ++index;
            }
            // 然后按顺序添加分组菜单
            Dictionary<string, GroupNode> tempGroupInfo = groupInfoDict;
            foreach (GroupNode node in tempGroupInfo.Values)
            {
                if (string.IsNullOrEmpty(node.GroupName))
                    continue;

                AddGroupComboBox(node, ref index, ref level);
            }
        }

        private void AddGroupComboBox(GroupNode node, ref int index, ref int level)
        {
            groupComboBoxIndexToGroupNodeDict.Add(index, node);
            string groupName;
            if (level == 0)
                groupName = node.GroupName;
            else
                groupName = string.Concat(StringUtil.GetRepeatString("   ", level), "|--", node.GroupName);

            cmbChooseGroup.Items.Add(groupName);
            ++index;
            if (node.ChildGroup.Count > 0)
            {
                ++level;
                foreach (GroupNode childNode in node.ChildGroup.Values)
                    AddGroupComboBox(childNode, ref index, ref level);

                --level;
            }
        }

        private void InitFavorite()
        {
            string favoritePath = Path.Combine(AppValues.PROGRAM_FOLDER_PATH, AppValues.FAVORITE_FILE_NAME);
            if (File.Exists(favoritePath) == true)
            {
                // 已经失效的redisKeyId
                List<int> expireRedisKeyIds = new List<int>();
                using (StreamReader reader = new StreamReader(favoritePath, Encoding.Unicode))
                {
                    string line = null;
                    int lineNum = 0;
                    while ((line = reader.ReadLine()) != null)
                    {
                        line = line.Trim();
                        ++lineNum;
                        if (string.IsNullOrEmpty(line))
                            continue;

                        int keyId;
                        if (int.TryParse(line, out keyId) == false)
                        {
                            string tips = string.Format("收藏夹配置文件中，第{0}行存在非法keyId，请修正后重新打开本工具", lineNum);
                            MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            Environment.Exit(-1);
                        }
                        if (redisKeyIdToRedisKeyInfoDict.ContainsKey(keyId) == false)
                            expireRedisKeyIds.Add(keyId);
                        else if (favoriteList.Contains(keyId) == false)
                            favoriteList.Add(keyId);
                    }
                    foreach (int keyId in favoriteList)
                        lstFavorite.Items.Add(redisKeyIdToRedisKeyInfoDict[keyId].Desc);

                    if (expireRedisKeyIds.Count > 0)
                    {
                        string tips = string.Format("读取收藏夹文件时，发现以下keyId在{0}配置文件中找不到，将进行忽略：\n{1}", AppValues.REDIS_KEY_CONFIG_FILE_NAME, StringUtil.GetCollectionMemberString<int>(expireRedisKeyIds));
                        MessageBox.Show(this, tips, "警告", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    }
                }
            }
        }

        private void InitHistory()
        {
            string favoritePath = Path.Combine(AppValues.PROGRAM_FOLDER_PATH, AppValues.HISTORY_FILE_NAME);
            if (File.Exists(favoritePath) == true)
            {
                // 已经失效的redisKeyId
                List<int> expireRedisKeyIds = new List<int>();
                using (StreamReader reader = new StreamReader(favoritePath, Encoding.Unicode))
                {
                    string line = null;
                    int lineNum = 0;
                    while ((line = reader.ReadLine()) != null)
                    {
                        line = line.Trim();
                        ++lineNum;
                        if (string.IsNullOrEmpty(line))
                            continue;

                        int keyId;
                        if (int.TryParse(line, out keyId) == false)
                        {
                            string tips = string.Format("历史记录文件中，第{0}行存在非法keyId，请修正后重新打开本工具", lineNum);
                            MessageBox.Show(this, tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            Environment.Exit(-1);
                        }
                        if (redisKeyIdToRedisKeyInfoDict.ContainsKey(keyId) == false)
                            expireRedisKeyIds.Add(keyId);
                        else if (historyList.Contains(keyId) == false)
                            historyList.Add(keyId);
                    }
                    foreach (int keyId in historyList)
                        lstHistory.Items.Add(redisKeyIdToRedisKeyInfoDict[keyId].Desc);

                    if (expireRedisKeyIds.Count > 0)
                    {
                        string tips = string.Format("读取历史记录时，发现以下keyId在{0}配置文件中找不到，将进行忽略：\n{1}", AppValues.REDIS_KEY_CONFIG_FILE_NAME, StringUtil.GetCollectionMemberString<int>(expireRedisKeyIds));
                        MessageBox.Show(this, tips, "警告", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    }
                }
            }
        }

        private void cmbChooseGroup_SelectedIndexChanged(object sender, EventArgs e)
        {
            keyComboBoxIndexToKeyIdDict.Clear();
            cmbChooseKey.Items.Clear();

            int selectedIndex = cmbChooseGroup.SelectedIndex;
            GroupNode groupNode = groupComboBoxIndexToGroupNodeDict[selectedIndex];
            int index = 0;
            foreach (int keyId in groupNode.KeyIds)
            {
                keyComboBoxIndexToKeyIdDict.Add(index, keyId);
                cmbChooseKey.Items.Add(redisKeyIdToRedisKeyInfoDict[keyId].Desc);
                ++index;
            }
        }

        private void RefreshFavoriteButton()
        {
            if (groupInfoDict.Count > 0)
            {
                int selectedIndex = cmbChooseKey.SelectedIndex;
                int keyId = keyComboBoxIndexToKeyIdDict[selectedIndex];

                if (favoriteList.Contains(keyId))
                    btnFavorite.Text = "取消收藏";
                else
                    btnFavorite.Text = "加入收藏";

                btnFavorite.Visible = true;
            }
        }

        private void cmbChooseKey_SelectedIndexChanged(object sender, EventArgs e)
        {
            int selectedIndex = cmbChooseKey.SelectedIndex;
            int keyId = keyComboBoxIndexToKeyIdDict[selectedIndex];
            RedisKeyInfo keyInfo = redisKeyIdToRedisKeyInfoDict[keyId];
            txtKeyName.Text = keyInfo.RedisKeyName;
            txtProtobufClassName.Text = keyInfo.ProtobufClassName;
            cmbChooseDataType.SelectedIndex = (int)keyInfo.RedisDataType;
            lblRemark.Text = keyInfo.Remark;

            if (groupInfoDict.Count > 0)
            {
                // 根据是否已加入收藏，调整收藏按钮的文字和功能
                RefreshFavoriteButton();

                // 加入使用历史
                // 如果已经在历史中，将其置顶
                if (historyList.Contains(keyId))
                {
                    historyList.Remove(keyId);
                    lstHistory.Items.Remove(keyInfo.Desc);
                }
                historyList.Insert(0, keyId);
                lstHistory.Items.Insert(0, keyInfo.Desc);
            }
        }

        private void cmbChooseDataType_SelectedIndexChanged(object sender, EventArgs e)
        {
            grpHashFilter.Visible = (cmbChooseDataType.SelectedIndex == (int)RedisDataTypeEnum.Hash);
            grpListFilter.Visible = (cmbChooseDataType.SelectedIndex == (int)RedisDataTypeEnum.List);
            grpZSetFilter.Visible = (cmbChooseDataType.SelectedIndex == (int)RedisDataTypeEnum.ZSet);
        }

        private void btnSearch_Click(object sender, EventArgs e)
        {
            rtxResult.Clear();
            if (cmbChooseDataType.SelectedIndex < 0)
            {
                MessageBox.Show(this, "未选择key的redis数据类型", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            string inputKeyName = txtKeyName.Text.Trim();
            if (string.IsNullOrEmpty(inputKeyName))
            {
                MessageBox.Show(this, "未输入key格式", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
            string inputProtobufClassName = txtProtobufClassName.Text.Trim();

            StringBuilder sb = new StringBuilder();
            RedisDataTypeEnum chooseDataType = (RedisDataTypeEnum)cmbChooseDataType.SelectedIndex;
            if (chooseDataType == RedisDataTypeEnum.String)
            {
                if (string.IsNullOrEmpty(inputProtobufClassName))
                {
                    string value = RedisManager.GetStringValue(inputKeyName);
                    if (value != null)
                        sb.Append(value);
                    else
                        MessageBox.Show(this, "找不到对应数据", "警告", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                }
                else
                {
                    byte[] byteArray = RedisManager.GetByteArrayValue(inputKeyName);
                    if (byteArray != null)
                    {
                        MethodInfo parseFromMethod = GetProtobufParseFromMethod(inputProtobufClassName);
                        if (parseFromMethod != null)
                            sb.Append(TextFormat.PrintToString((IMessageLite)parseFromMethod.Invoke(null, new object[] { byteArray })));
                        else
                        {
                            string tips = string.Format("不存在名为{0}的protobuf类，请注意区分大小写", inputProtobufClassName);
                            MessageBox.Show(tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                        }
                    }
                    else
                        MessageBox.Show(this, "找不到对应数据", "警告", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                }
            }
            else if (chooseDataType == RedisDataTypeEnum.Hash)
            {
                string inputHashKey = txtHashKey.Text.Trim();
                string hashKeyValuePairsSpliteStr = txtHashKeyValuePairsSpliteStr.Text.Replace("\\n", "\n").Replace("\\t", "\t");
                string hashDataSpliteStr = txtHashDataSpliteStr.Text.Replace("\\n", "\n").Replace("\\t", "\t");
                if (string.IsNullOrEmpty(inputHashKey))
                {
                    if (string.IsNullOrEmpty(inputProtobufClassName))
                    {
                        Dictionary<string, string> keyValuePairs = RedisManager.GetAllStringEntriesFromHash(inputKeyName);
                        if (keyValuePairs != null)
                        {
                            sb.AppendFormat("总键值对个数：{0}\n\n", keyValuePairs.Count);
                            foreach (var pairs in keyValuePairs)
                            {
                                sb.Append(pairs.Key);
                                sb.Append(hashKeyValuePairsSpliteStr);
                                sb.Append(pairs.Value);
                                sb.Append(hashDataSpliteStr);
                            }
                        }
                        else
                            MessageBox.Show(this, "找不到对应数据", "警告", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    }
                    else
                    {
                        Dictionary<string, byte[]> keyValuePairs = RedisManager.GetAllByteArrayEntriesFromHash(inputKeyName);
                        if (keyValuePairs != null)
                        {
                            sb.AppendFormat("总键值对个数：{0}\n\n", keyValuePairs.Count);
                            MethodInfo parseFromMethod = GetProtobufParseFromMethod(inputProtobufClassName);
                            if (parseFromMethod != null)
                            {
                                foreach (var pairs in keyValuePairs)
                                {
                                    sb.Append(pairs.Key);
                                    sb.Append(hashKeyValuePairsSpliteStr);
                                    sb.Append(TextFormat.PrintToString((IMessageLite)parseFromMethod.Invoke(null, new object[] { pairs.Value })));
                                    sb.Append(hashDataSpliteStr);
                                }
                            }
                            else
                            {
                                string tips = string.Format("不存在名为{0}的protobuf类，请注意区分大小写", inputProtobufClassName);
                                MessageBox.Show(tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            }
                        }
                        else
                            MessageBox.Show(this, "找不到对应数据", "警告", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    }
                }
                else
                {
                    if (string.IsNullOrEmpty(inputProtobufClassName))
                    {
                        string value = RedisManager.GetStringValueFromHash(inputKeyName, inputHashKey);
                        if (value != null)
                        {
                            sb.Append(inputHashKey);
                            sb.Append(hashKeyValuePairsSpliteStr);
                            sb.Append(value);
                        }
                        else
                            MessageBox.Show(this, "找不到对应数据", "警告", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    }
                    else
                    {
                        byte[] byteArray = RedisManager.GetByteArrayValueFromHash(inputKeyName, inputHashKey);
                        if (byteArray != null)
                        {
                            MethodInfo parseFromMethod = GetProtobufParseFromMethod(inputProtobufClassName);
                            if (parseFromMethod != null)
                            {
                                sb.Append(inputHashKey);
                                sb.Append(hashKeyValuePairsSpliteStr);
                                sb.Append(TextFormat.PrintToString((IMessageLite)parseFromMethod.Invoke(null, new object[] { byteArray })));
                            }
                            else
                            {
                                string tips = string.Format("不存在名为{0}的protobuf类，请注意区分大小写", inputProtobufClassName);
                                MessageBox.Show(tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            }
                        }
                        else
                            MessageBox.Show(this, "找不到对应数据", "警告", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    }
                }
            }
            else if (chooseDataType == RedisDataTypeEnum.List)
            {
                int inputRangeStart;
                int inputRangeEnd;
                string listDataSpliteStr = txtListDataSpliteStr.Text.Replace("\\n", "\n").Replace("\\t", "\t");

                string inputRangeStartStr = txtListRangeStart.Text.Trim();
                if (string.IsNullOrEmpty(inputRangeStartStr))
                {
                    MessageBox.Show("未输入list型范围起始下标", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }
                if (int.TryParse(inputRangeStartStr, out inputRangeStart) == false)
                {
                    MessageBox.Show("输入的list型范围起始下标不是int型合法数字", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }
                string inputRangeEndStr = txtListRangeEnd.Text.Trim();
                if (string.IsNullOrEmpty(inputRangeEndStr))
                {
                    MessageBox.Show("未输入list型范围结束下标", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }
                if (int.TryParse(inputRangeEndStr, out inputRangeEnd) == false)
                {
                    MessageBox.Show("输入的list型范围结束下标不是int型合法数字", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }

                if (string.IsNullOrEmpty(inputProtobufClassName))
                {
                    List<string> result = RedisManager.GetStringRangeFromList(inputKeyName, inputRangeStart, inputRangeEnd);
                    if (result != null)
                    {
                        sb.AppendFormat("元素个数：{0}\n\n", result.Count);
                        foreach (string value in result)
                        {
                            sb.Append(value);
                            sb.Append(listDataSpliteStr);
                        }
                    }
                    else
                        MessageBox.Show(this, "找不到对应数据", "警告", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                }
                else
                {
                    List<byte[]> result = RedisManager.GetByteArrayRangeFromList(inputKeyName, inputRangeStart, inputRangeEnd);
                    if (result != null)
                    {
                        MethodInfo parseFromMethod = GetProtobufParseFromMethod(inputProtobufClassName);
                        if (parseFromMethod != null)
                        {
                            foreach (byte[] byteArray in result)
                            {
                                sb.Append(TextFormat.PrintToString((IMessageLite)parseFromMethod.Invoke(null, new object[] { byteArray })));
                                sb.Append(listDataSpliteStr);
                            }
                        }
                        else
                        {
                            string tips = string.Format("不存在名为{0}的protobuf类，请注意区分大小写", inputProtobufClassName);
                            MessageBox.Show(tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                        }
                    }
                    else
                        MessageBox.Show(this, "找不到对应数据", "警告", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                }
            }
            else if (chooseDataType == RedisDataTypeEnum.Set)
            {
                string setDataSpliteStr = txtSetDataSpliteStr.Text.Replace("\\n", "\n").Replace("\\t", "\t");

                if (string.IsNullOrEmpty(inputProtobufClassName))
                {
                    HashSet<string> result = RedisManager.GetAllStringItemsFromSet(inputKeyName);
                    if (result != null)
                    {
                        sb.AppendFormat("元素个数：{0}\n\n", result.Count);
                        foreach (string value in result)
                        {
                            sb.Append(value);
                            sb.Append(setDataSpliteStr);
                        }
                    }
                    else
                        MessageBox.Show(this, "找不到对应数据", "警告", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                }
                else
                {
                    HashSet<byte[]> result = RedisManager.GetAllByteArrayItemsFromSet(inputKeyName);
                    if (result != null)
                    {
                        MethodInfo parseFromMethod = GetProtobufParseFromMethod(inputProtobufClassName);
                        if (parseFromMethod != null)
                        {
                            foreach (byte[] byteArray in result)
                            {
                                sb.Append(TextFormat.PrintToString((IMessageLite)parseFromMethod.Invoke(null, new object[] { byteArray })));
                                sb.Append(setDataSpliteStr);
                            }
                        }
                        else
                        {
                            string tips = string.Format("不存在名为{0}的protobuf类，请注意区分大小写", inputProtobufClassName);
                            MessageBox.Show(tips, "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                        }
                    }
                    else
                        MessageBox.Show(this, "找不到对应数据", "警告", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                }
            }
            else if (chooseDataType == RedisDataTypeEnum.ZSet)
            {
                string dataSpliteStr = txtZSetDataSpliteStr.Text.Replace("\\n", "\n").Replace("\\t", "\t");
                string rankDataSpliteStr = txtZSetRankDataSpliteStr.Text.Replace("\\n", "\n").Replace("\\t", "\t");
                string nameScoreSpliteStr = txtZSetNameScoreSpliteStr.Text.Replace("\\n", "\n").Replace("\\t", "\t");

                if (string.IsNullOrEmpty(inputProtobufClassName))
                {
                    // 通过分数筛选
                    if (rdoZSetGetDataByScore.Checked == true)
                    {
                        double inputScoreRangeMin;
                        double inputScoreRangeMax;

                        string inputScoreRangeMinStr = txtZSetScoreRangeMin.Text.Trim();
                        if (string.IsNullOrEmpty(inputScoreRangeMinStr))
                            inputScoreRangeMin = double.MinValue;
                        else
                        {
                            if (double.TryParse(inputScoreRangeMinStr, out inputScoreRangeMin) == false)
                            {
                                MessageBox.Show("输入的分数下限不是double型合法数字", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                                return;
                            }
                        }
                        string inputScoreRangeMaxStr = txtZSetScoreRangeMax.Text.Trim();
                        if (string.IsNullOrEmpty(inputScoreRangeMaxStr))
                            inputScoreRangeMax = double.MaxValue;
                        else
                        {
                            if (double.TryParse(inputScoreRangeMaxStr, out inputScoreRangeMax) == false)
                            {
                                MessageBox.Show("输入的分数上限不是double型合法数字", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                                return;
                            }
                        }
                        if (inputScoreRangeMin > inputScoreRangeMax)
                        {
                            MessageBox.Show("输入的分数下限不能高于上限", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            return;
                        }

                        IDictionary<string, double> result = RedisManager.GetEntriesFromZSetByScore(inputKeyName, inputScoreRangeMin, inputScoreRangeMax, chkIsZSetScoreOrderByAsc.Checked);
                        if (result != null)
                        {
                            sb.AppendFormat("元素个数：{0}\n\n", result.Count);
                            // 查到首个元素的排名
                            var enumerator = result.GetEnumerator();
                            enumerator.MoveNext();
                            KeyValuePair<string, double> firstItem = enumerator.Current;
                            long index = RedisManager.GetZSetItemRankByName(inputKeyName, firstItem.Key, chkIsZSetScoreOrderByAsc.Checked);
                            foreach (var keyValuePair in result)
                            {
                                sb.Append(index);
                                sb.Append(rankDataSpliteStr);
                                sb.Append(keyValuePair.Key);
                                sb.Append(nameScoreSpliteStr);
                                sb.Append(keyValuePair.Value);
                                sb.Append(dataSpliteStr);
                                ++index;
                            }
                        }
                        else
                            MessageBox.Show(this, "找不到对应数据", "警告", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    }
                    // 通过排名筛选
                    else if (rdoZSetGetDataByRank.Checked == true)
                    {
                        int inputRangeStart;
                        int inputRangeEnd;

                        string inputRangeStartStr = txtListRangeStart.Text.Trim();
                        if (string.IsNullOrEmpty(inputRangeStartStr))
                        {
                            MessageBox.Show("未输入zset型排名范围起始下标", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            return;
                        }
                        if (int.TryParse(inputRangeStartStr, out inputRangeStart) == false)
                        {
                            MessageBox.Show("输入的zset型排名范围起始下标不是int型合法数字", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            return;
                        }
                        string inputRangeEndStr = txtListRangeEnd.Text.Trim();
                        if (string.IsNullOrEmpty(inputRangeEndStr))
                        {
                            MessageBox.Show("未输入zset型排名范围结束下标", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            return;
                        }
                        if (int.TryParse(inputRangeEndStr, out inputRangeEnd) == false)
                        {
                            MessageBox.Show("输入的zset型排名范围结束下标不是int型合法数字", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                            return;
                        }

                        IDictionary<string, double> result = RedisManager.GetEntriesFromZSetByRank(inputKeyName, inputRangeStart, inputRangeEnd, chkIsZSetScoreOrderByAsc.Checked);
                        if (result != null)
                        {
                            sb.AppendFormat("元素个数：{0}\n\n", result.Count);
                            int index = inputRangeStart;
                            foreach (var keyValuePair in result)
                            {
                                sb.Append(index);
                                sb.Append(rankDataSpliteStr);
                                sb.Append(keyValuePair.Key);
                                sb.Append(nameScoreSpliteStr);
                                sb.Append(keyValuePair.Value);
                                sb.Append(dataSpliteStr);
                                ++index;
                            }
                        }
                        else
                            MessageBox.Show(this, "找不到对应数据", "警告", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    }
                    // 查找指定元素的分数和排名
                    else
                    {
                        string inputItemName = txtZSetItemName.Text;
                        double score = RedisManager.GetZSetItemScoreByName(inputKeyName, inputItemName);
                        if (double.IsNaN(score) == false)
                        {
                            long index = RedisManager.GetZSetItemRankByName(inputKeyName, inputItemName, chkIsZSetScoreOrderByAsc.Checked);
                            sb.Append(index);
                            sb.Append(rankDataSpliteStr);
                            sb.Append(inputItemName);
                            sb.Append(nameScoreSpliteStr);
                            sb.Append(score);
                            sb.Append(dataSpliteStr);
                        }
                        else
                            MessageBox.Show(this, "找不到对应数据", "警告", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                    }
                }
                else
                {
                    MessageBox.Show("zset数据结构中不支持存储protobuf类对应的byte[]，请将protobuf类文本框留空", "错误", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }
            }

            rtxResult.Text = sb.ToString();
        }

        private void lstFavorite_MouseDoubleClick(object sender, MouseEventArgs e)
        {
            int index = lstFavorite.IndexFromPoint(e.Location);
            if (index != ListBox.NoMatches)
            {
                int selectedIndex = lstFavorite.SelectedIndex;
                int keyId = favoriteList[selectedIndex];
                // 选中keyId对应分组ComboBox中的项
                RedisKeyInfo redisKeyInfo = redisKeyIdToRedisKeyInfoDict[keyId];
                int groupComboBoxIndex = -1;
                foreach (var keyValuePair in groupComboBoxIndexToGroupNodeDict)
                {
                    if (keyValuePair.Value == redisKeyInfo.GroupNode)
                    {
                        groupComboBoxIndex = keyValuePair.Key;
                        break;
                    }
                }
                cmbChooseGroup.SelectedIndex = groupComboBoxIndex;
                // 选中keyId对应key的ComboBox中的项
                int keyComboBoxIndex = -1;
                foreach (var keyValuePair in keyComboBoxIndexToKeyIdDict)
                {
                    if (keyValuePair.Value == keyId)
                    {
                        keyComboBoxIndex = keyValuePair.Key;
                        break;
                    }
                }
                cmbChooseKey.SelectedIndex = keyComboBoxIndex;
            }
            else
                lstFavorite.SelectedIndex = ListBox.NoMatches;
        }

        private void lstHistory_MouseDoubleClick(object sender, MouseEventArgs e)
        {
            int index = lstHistory.IndexFromPoint(e.Location);
            if (index != ListBox.NoMatches)
            {
                int selectedIndex = lstHistory.SelectedIndex;
                int keyId = historyList[selectedIndex];
                // 选中keyId对应分组ComboBox中的项
                RedisKeyInfo redisKeyInfo = redisKeyIdToRedisKeyInfoDict[keyId];
                int groupComboBoxIndex = -1;
                foreach (var keyValuePair in groupComboBoxIndexToGroupNodeDict)
                {
                    if (keyValuePair.Value == redisKeyInfo.GroupNode)
                    {
                        groupComboBoxIndex = keyValuePair.Key;
                        break;
                    }
                }
                cmbChooseGroup.SelectedIndex = groupComboBoxIndex;
                // 选中keyId对应key的ComboBox中的项
                int keyComboBoxIndex = -1;
                foreach (var keyValuePair in keyComboBoxIndexToKeyIdDict)
                {
                    if (keyValuePair.Value == keyId)
                    {
                        keyComboBoxIndex = keyValuePair.Key;
                        break;
                    }
                }
                cmbChooseKey.SelectedIndex = keyComboBoxIndex;
            }
            else
                lstHistory.SelectedIndex = ListBox.NoMatches;
        }

        private void btnFavorite_Click(object sender, EventArgs e)
        {
            int redisKeyId = keyComboBoxIndexToKeyIdDict[cmbChooseKey.SelectedIndex];
            // 取消收藏
            if (favoriteList.Contains(redisKeyId))
            {
                favoriteList.Remove(redisKeyId);
                lstFavorite.Items.Remove(redisKeyIdToRedisKeyInfoDict[redisKeyId].Desc);
            }
            // 加入收藏
            else
            {
                favoriteList.Insert(0, redisKeyId);
                lstFavorite.Items.Insert(0, redisKeyIdToRedisKeyInfoDict[redisKeyId].Desc);
            }

            RefreshFavoriteButton();
        }

        private void tsmiFavoriteTop_Click(object sender, EventArgs e)
        {
            int selectedIndex = lstFavorite.SelectedIndex;
            if (selectedIndex > -1)
            {
                int keyId = favoriteList[selectedIndex];
                RedisKeyInfo keyInfo = redisKeyIdToRedisKeyInfoDict[keyId];
                favoriteList.RemoveAt(selectedIndex);
                lstFavorite.Items.RemoveAt(selectedIndex);
                favoriteList.Insert(0, keyId);
                lstFavorite.Items.Insert(0, keyInfo.Desc);
            }
        }

        private void tsmiFavoriteDelete_Click(object sender, EventArgs e)
        {
            int selectedIndex = lstFavorite.SelectedIndex;
            if (selectedIndex > -1)
            {
                favoriteList.RemoveAt(selectedIndex);
                lstFavorite.Items.RemoveAt(selectedIndex);
                RefreshFavoriteButton();
            }
        }

        private void tsmiFavoriteClear_Click(object sender, EventArgs e)
        {
            favoriteList.Clear();
            lstFavorite.Items.Clear();
            RefreshFavoriteButton();
        }

        private void lstFavorite_MouseDown(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Right)
            {
                int index = lstFavorite.IndexFromPoint(e.Location);
                if (index != ListBox.NoMatches)
                    lstFavorite.SelectedIndex = index;
            }
        }

        private void tsmiHistoryTop_Click(object sender, EventArgs e)
        {
            int selectedIndex = lstHistory.SelectedIndex;
            if (selectedIndex > -1)
            {
                int keyId = historyList[selectedIndex];
                RedisKeyInfo keyInfo = redisKeyIdToRedisKeyInfoDict[keyId];
                historyList.RemoveAt(selectedIndex);
                lstHistory.Items.RemoveAt(selectedIndex);
                historyList.Insert(0, keyId);
                lstHistory.Items.Insert(0, keyInfo.Desc);
            }
        }

        private void tsmiHistoryDelete_Click(object sender, EventArgs e)
        {
            int selectedIndex = lstHistory.SelectedIndex;
            if (selectedIndex > -1)
            {
                historyList.RemoveAt(selectedIndex);
                lstHistory.Items.RemoveAt(selectedIndex);
                RefreshFavoriteButton();
            }
        }

        private void tsmiHistoryClear_Click(object sender, EventArgs e)
        {
            historyList.Clear();
            lstHistory.Items.Clear();
            RefreshFavoriteButton();
        }

        private void lstHistory_MouseDown(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Right)
            {
                int index = lstHistory.IndexFromPoint(e.Location);
                if (index != ListBox.NoMatches)
                    lstHistory.SelectedIndex = index;
            }
        }

        private void MainForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            // 关闭本工具时，保存收藏夹和历史记录
            if (groupInfoDict.Count > 0)
            {
                string favoritePath = Path.Combine(AppValues.PROGRAM_FOLDER_PATH, AppValues.FAVORITE_FILE_NAME);
                using (StreamWriter writer = new StreamWriter(favoritePath, false, Encoding.Unicode))
                {
                    string content = StringUtil.GetCollectionMemberString<int>(favoriteList, Environment.NewLine);
                    writer.Write(content);
                    writer.Flush();
                    writer.Close();
                }
                string historyPath = Path.Combine(AppValues.PROGRAM_FOLDER_PATH, AppValues.HISTORY_FILE_NAME);
                using (StreamWriter writer = new StreamWriter(historyPath, false, Encoding.Unicode))
                {
                    string content = StringUtil.GetCollectionMemberString<int>(historyList, Environment.NewLine);
                    writer.Write(content);
                    writer.Flush();
                    writer.Close();
                }
            }
        }
    }

    public class RedisKeyInfo
    {
        public int KeyId { get; set; }
        public GroupNode GroupNode { get; set; }
        public string Desc { get; set; }
        public string RedisKeyName { get; set; }
        public string ProtobufClassName { get; set; }
        public RedisDataTypeEnum RedisDataType { get; set; }
        public string Remark { get; set; }
    }

    public enum RedisDataTypeEnum
    {
        String,
        Hash,
        List,
        Set,
        ZSet,
    }

    /// <summary>
    /// 一层分组的信息
    /// </summary>
    public class GroupNode
    {
        // 分组名
        public string GroupName { get; set; }
        // 下属的子组（key:groupName, value:子组的GroupNode）
        public Dictionary<string, GroupNode> ChildGroup { get; set; }
        // 本层分组下包含的keyId
        public List<int> KeyIds { get; set; }

        public GroupNode(string groupName)
        {
            this.GroupName = groupName;
            ChildGroup = new Dictionary<string, GroupNode>();
            KeyIds = new List<int>();
        }
    }
}
