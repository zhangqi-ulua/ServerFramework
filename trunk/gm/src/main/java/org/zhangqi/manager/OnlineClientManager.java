package org.zhangqi.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.constants.GmSessionDataKeyConstant;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.network.session.ISession;
import org.zhangqi.proto.Gm.GmRpcErrorCodeEnum;
import org.zhangqi.proto.Gm.GmRpcNameEnum;

public class OnlineClientManager implements IManager {

	private static final Logger logger = LoggerFactory.getLogger(OnlineClientManager.class);

	private static OnlineClientManager instance = new OnlineClientManager();

	public static OnlineClientManager getInstance() {
		return instance;
	}

	private final Map<Integer, ISession> onlineGmUserToSessionMap = new ConcurrentHashMap<Integer, ISession>();

	public void addOnlineGmUser(int gmUserId, ISession session) {
		onlineGmUserToSessionMap.put(gmUserId, session);
	}

	public void removeOnlineGmUser(ISession session) {
		if (session.hasData(GmSessionDataKeyConstant.GM_USER_ID_KEY)) {
			int gmUserId = (Integer) session.getData(GmSessionDataKeyConstant.GM_USER_ID_KEY);
			// 注意：这里必须进行如下判断，而不能直接移除。因为同一GM账号二次登录时，会先同步addOnlineGmUser，然后关闭旧的连接
			// 但关闭连接是异步由channelInactive触发的，然后调用本函数。如果不进行session是否相同的判断，就会导致新加入map的新连接也被清除
			if (onlineGmUserToSessionMap.containsKey(gmUserId) && onlineGmUserToSessionMap.get(gmUserId) == session) {
				onlineGmUserToSessionMap.remove(gmUserId);
			}
		}
	}

	public ISession getOnlineGmUserSession(int gmUserId) {
		return onlineGmUserToSessionMap.get(gmUserId);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void pushToOneGmSession(ISession session, NetMessage message, Class protobufClass) {
		GmRpcNameEnum rpcName = GmRpcNameEnum.valueOf(message.getRpcNum());
		GmRpcErrorCodeEnum errorCode = GmRpcErrorCodeEnum.valueOf(message.getErrorCode());
		logger.info("pushToOneGmSession rpcName = {}, errorCode = {}, protobuf = {}", rpcName, errorCode,
				protobufClass == null ? null : message.getProtobufText(protobufClass));
		session.write(message);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void pushToAllGmSession(NetMessage message, Class protobufClass) {
		GmRpcNameEnum rpcName = GmRpcNameEnum.valueOf(message.getRpcNum());
		GmRpcErrorCodeEnum errorCode = GmRpcErrorCodeEnum.valueOf(message.getErrorCode());
		logger.info("pushToAllGmSession rpcName = {}, errorCode = {}, protobuf = {}", rpcName, errorCode,
				protobufClass == null ? null : message.getProtobufText(protobufClass));
		for (ISession session : onlineGmUserToSessionMap.values()) {
			session.write(message);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void pushToOneGmUser(int gmUserId, NetMessage message, Class protobufClass) {
		ISession session = onlineGmUserToSessionMap.get(gmUserId);
		GmRpcNameEnum rpcName = GmRpcNameEnum.valueOf(message.getRpcNum());
		GmRpcErrorCodeEnum errorCode = GmRpcErrorCodeEnum.valueOf(message.getErrorCode());
		if (session == null) {
			logger.warn("pushToOneGmUser fail, gmUser is not online, rpcName = {}, errorCode = {}, protobuf = \n{}",
					rpcName, errorCode, protobufClass == null ? "null" : message.getProtobufText(protobufClass));
		} else {
			logger.info("pushToOneGmSession gmUserId = {}, rpcName = {}, errorCode = {}, protobuf = {}", gmUserId,
					rpcName, errorCode, protobufClass == null ? null : message.getProtobufText(protobufClass));
			session.write(message);
		}
	}

	@Override
	public void init() {
	}

	@Override
	public void shutdown() {
	}
}
