using System;
using System.Collections.Generic;
using System.Text;

namespace RedisDataViewer
{
    public class StringUtil
    {
        public static string GetCollectionMemberString<T>(ICollection<T> collection, String splitString = ",")
        {
            if (collection == null)
                return string.Empty;
            else if (collection.Count == 0)
                return string.Empty;
            else
            {
                StringBuilder sb = new StringBuilder();
                foreach (T t in collection)
                    sb.Append(t.ToString()).Append(splitString);

                // 去掉末尾多余的一次分隔符
                sb.Remove(sb.Length - splitString.Length, splitString.Length);
                return sb.ToString();
            }
        }

        public static string GetRepeatString(string str, int repeatCount)
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < repeatCount; ++i)
                sb.Append(str);

            return sb.ToString();
        }
    }
}
