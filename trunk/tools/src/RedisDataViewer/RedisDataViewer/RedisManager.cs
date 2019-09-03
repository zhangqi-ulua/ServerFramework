using ServiceStack.Redis;
using System.Collections.Generic;
using System.Text;

namespace RedisDataViewer
{
    public class RedisManager
    {
        private static RedisClient redisClient = new RedisClient(AppValues.REDIS_CONNECT_IP, AppValues.REDIS_CONNECT_PORT, AppValues.REDIS_CONNECT_PASSWORD);

        /// <summary>
        /// 返回与redis是否连接上（先尝试连接，如果连接不上，会重新建立连接后再尝试，若还是连不上才返回连接失败）
        /// </summary>
        public static bool IsConnected()
        {
            bool isConnected = false;
            try
            {
                isConnected = redisClient.Ping();
                if (isConnected == true)
                    return true;
            }
            catch
            {
                redisClient = new RedisClient(AppValues.REDIS_CONNECT_IP, AppValues.REDIS_CONNECT_PORT, AppValues.REDIS_CONNECT_PASSWORD);
            }
            try
            {
                isConnected = redisClient.Ping();
                if (isConnected == true)
                    return true;
                else
                    return false;
            }
            catch
            {
                return false;
            }
        }

        /// <summary>
        /// 获取string型的值类型
        /// </summary>
        public static string GetStringValue(string keyName)
        {
            // 找不到则返回null
            return redisClient.GetValue(keyName);
        }

        /// <summary>
        /// 获取byte[]型的值类型
        /// </summary>
        public static byte[] GetByteArrayValue(string keyName)
        {
            // 找不到则返回null
            return redisClient.Get(keyName);
        }

        /// <summary>
        /// 获取hash结构中所有string型值的键值对
        /// </summary>
        public static Dictionary<string, string> GetAllStringEntriesFromHash(string keyName)
        {
            // 找不到则返回元素个数为0的dict
            Dictionary<string, string> result = redisClient.GetAllEntriesFromHash(keyName);
            return result.Count > 0 ? result : null;
        }

        /// <summary>
        /// 获取hash结构中所有byte[]型值的键值对
        /// </summary>
        public static Dictionary<string, byte[]> GetAllByteArrayEntriesFromHash(string keyName)
        {
            // 这个API返回的byte[][]是按一个键值对的key、value依次排列的，即下标0是第一个键值对的key，下标1是对应的value
            // 找不到则返回元素个数为0的数组
            byte[][] data = redisClient.HGetAll(keyName);
            if (data.Length > 0)
            {
                Dictionary<string, byte[]> result = new Dictionary<string, byte[]>();
                for (int i = 0; i < data.Length - 1; i += 2)
                {
                    string key = Encoding.UTF8.GetString(data[i]);
                    result.Add(key, data[i + 1]);
                }
                return result;
            }
            else
                return null;
        }

        /// <summary>
        /// 获取hash结构中某个key对应的string型值
        /// </summary>
        public static string GetStringValueFromHash(string keyName, string hashKey)
        {
            // 无论keyName或hashKey找不到都返回null
            return redisClient.GetValueFromHash(keyName, hashKey);
        }

        /// <summary>
        /// 获取hash结构中某个key对应的byte[]型值
        /// </summary>
        public static byte[] GetByteArrayValueFromHash(string keyName, string hashKey)
        {
            // 无论keyName或hashKey找不到都返回null
            return redisClient.HGet(keyName, Encoding.UTF8.GetBytes(hashKey));
        }

        /// <summary>
        /// 获取list结构中指定下标位置对应的string型值
        /// </summary>
        public static List<string> GetStringRangeFromList(string keyName, int startIndex, int endIndex)
        {
            // 无论keyName或对应范围内元素找不到都返回元素个数为0的list
            List<string> result = redisClient.GetRangeFromList(keyName, startIndex, endIndex);
            return result.Count > 0 ? result : null;
        }

        /// <summary>
        /// 获取list结构中指定下标位置对应的byte[]型值
        /// </summary>
        public static List<byte[]> GetByteArrayRangeFromList(string keyName, int startIndex, int endIndex)
        {
            // 无论keyName或对应范围内元素找不到都返回元素个数为0的数组
            byte[][] data = redisClient.LRange(keyName, startIndex, endIndex);
            if (data.Length > 0)
                return new List<byte[]>(data);
            else
                return null;
        }

        /// <summary>
        /// 获取set结构中所有string型值的元素
        /// </summary>
        public static HashSet<string> GetAllStringItemsFromSet(string keyName)
        {
            // 找不到则返回元素个数为0的set
            HashSet<string> result = redisClient.GetAllItemsFromSet(keyName);
            return result.Count > 0 ? result : null;
        }

        /// <summary>
        /// 获取set结构中所有byte[]型值的元素
        /// </summary>
        public static HashSet<byte[]> GetAllByteArrayItemsFromSet(string keyName)
        {
            // 找不到则返回元素个数为0的数组
            byte[][] data = redisClient.SMembers(keyName);
            if (data.Length > 0)
                return new HashSet<byte[]>(data);
            else
                return null;

        }

        /// <summary>
        /// 获取zset结构中指定分数范围内排好序的name与score的键值对
        /// </summary>
        public static IDictionary<string, double> GetEntriesFromZSetByScore(string keyName, double minScore, double maxScore, bool isOrderByScoreAsc)
        {
            // 分数从低到高升序排列
            if (isOrderByScoreAsc == true)
            {
                // 找不到则返回元素个数为0的dict
                IDictionary<string, double> result = redisClient.GetRangeWithScoresFromSortedSetByLowestScore(keyName, minScore, maxScore);
                return result.Count > 0 ? result : null;
            }
            // 分数从高到低降序排列
            else
            {
                // 找不到则返回元素个数为0的dict
                IDictionary<string, double> result = redisClient.GetRangeWithScoresFromSortedSetByHighestScore(keyName, minScore, maxScore);
                return result.Count > 0 ? result : null;
            }
        }

        /// <summary>
        /// 获取zset结构中指定排名范围内排好序的name与score的键值对
        /// </summary>
        public static IDictionary<string, double> GetEntriesFromZSetByRank(string keyName, int rankStart, int rankEnd, bool isOrderByScoreAsc)
        {
            // 分数从低到高升序排列
            if (isOrderByScoreAsc == true)
            {
                // 找不到则返回元素个数为0的dict
                IDictionary<string, double> result = redisClient.GetRangeWithScoresFromSortedSet(keyName, rankStart, rankEnd);
                return result.Count > 0 ? result : null;
            }
            else
            {
                // 找不到则返回元素个数为0的dict
                IDictionary<string, double> result = redisClient.GetRangeWithScoresFromSortedSetDesc(keyName, rankStart, rankEnd);
                return result.Count > 0 ? result : null;
            }
        }

        /// <summary>
        /// 获取zset结构中指定item对应的score
        /// </summary>
        public static double GetZSetItemScoreByName(string keyName, string itemName)
        {
            // 找不到则返回double.NaN，但无法用==double.NaN判断是不是NaN，而需要用double.IsNaN(score)
            return redisClient.GetItemScoreInSortedSet(keyName, itemName);
        }

        /// <summary>
        /// 获取zset结构中指定item对应的排名
        /// </summary>
        public static long GetZSetItemRankByName(string keyName, string itemName, bool isOrderByScoreAsc)
        {
            if (isOrderByScoreAsc == true)
                // 找不到则返回-1
                return redisClient.GetItemIndexInSortedSet(keyName, itemName);
            else
                // 找不到则返回-1
                return redisClient.GetItemIndexInSortedSetDesc(keyName, itemName);
        }
    }
}
