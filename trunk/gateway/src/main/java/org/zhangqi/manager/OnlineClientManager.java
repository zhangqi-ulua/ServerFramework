package org.zhangqi.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.msg.RemoteMessage;
import org.zhangqi.proto.RemoteServer.GatewayNoticeClientOfflinePush;
import org.zhangqi.proto.RemoteServer.RemoteRpcNameEnum;
import org.zhangqi.service.LoadBalanceService;
import org.zhangqi.service.UserService;

import io.netty.channel.Channel;

public class OnlineClientManager implements IManager {

	private static final Logger logger = LoggerFactory.getLogger(OnlineClientManager.class);

	private static OnlineClientManager instance = new OnlineClientManager();

	public static OnlineClientManager getInstance() {
		return instance;
	}

	// key:sessionId, value:玩家对应的ChannelActor
	private final Map<Integer, Channel> sessionIdToChannelMap = new ConcurrentHashMap<Integer, Channel>();

	private LoadBalanceService loadBalanceService;
	private UserService userService;

	public void addSession(int sessionId, Channel channel) {
		sessionIdToChannelMap.put(sessionId, channel);
		loadBalanceService.setOneSessionIdToGatewayId(sessionId, ConfigManager.GATEWAY_ID);
		if (MessageManager.getInstance().isAvailableForUpdateLoadBalance() == true) {
			loadBalanceService.setOneGatewayLoadBalance(ConfigManager.GATEWAY_ID, sessionIdToChannelMap.size());
		}
	}

	public void removeSession(int sessionId) {
		sessionIdToChannelMap.remove(sessionId);
		loadBalanceService.removeOneSessionIdToGatewayId(sessionId);
		Integer userId = loadBalanceService.getOneSessionIdToUserId(sessionId);
		loadBalanceService.removeOneSessionIdToUserId(sessionId);
		if (userId != null) {
			// 这里必须判断当前userId对应的最新sessionId是不是这个sessionId，因为处理同一账号二次登录后旧连接断开的情况时，不应该删除userId与最新sessionId的对应
			int userIdToSessionId = loadBalanceService.getOneUserIdToSessionId(userId);
			if (userIdToSessionId == sessionId) {
				loadBalanceService.removeOneUserIdToSessionId(userId);
				// 修改玩家状态为离线
				try {
					userService.changeUserOnlineState(userId, false);
				} catch (Exception e) {
					logger.error("removeSession error, changeUserOnlineState fail, error = ", e);
				}
				noticeClientOffline(sessionId, userId, true);
			} else {
				noticeClientOffline(sessionId, userId, false);
			}
		} else {
			noticeClientOffline(sessionId, null, false);
		}
		if (MessageManager.getInstance().isAvailableForUpdateLoadBalance() == true) {
			loadBalanceService.setOneGatewayLoadBalance(ConfigManager.GATEWAY_ID, sessionIdToChannelMap.size());
		}
	}

	public Channel getChannel(int sessionId) {
		return sessionIdToChannelMap.get(sessionId);
	}

	/**
	 * 如果离线的客户端已连上logicServer或battleServer，需要进行通知
	 * 
	 * @param userId        如果该session对应的客户端登录成功，则需传此字段
	 * @param isUserOffline 是否是玩家也要下线（同一账号二次登录导致旧session断开，但对应玩家仍在线）
	 */
	private void noticeClientOffline(Integer sessionId, Integer userId, boolean isUserOffline) {
		Integer connectedLogicServerId = loadBalanceService.getOneSessionIdToLogicServerId(sessionId);
		if (connectedLogicServerId != null) {
			GatewayNoticeClientOfflinePush.Builder pushBuilder = GatewayNoticeClientOfflinePush.newBuilder();
			pushBuilder.setSessionId(sessionId);
			if (userId != null) {
				pushBuilder.setUserId(userId);
			}
			pushBuilder.setIsUserOffline(isUserOffline);
			RemoteMessage remoteMsg = new RemoteMessage(RemoteRpcNameEnum.RemoteRpcGatewayNoticeClientOfflinePush_VALUE,
					pushBuilder);
			if (MessageManager.getInstance().sendRemoteMsgToLogicServer(remoteMsg, connectedLogicServerId) == false) {
				logger.error(
						"noticeClientOffline error, can't send client offline push to logicServer, sessionId = {}, logicServerId = {}",
						sessionId, connectedLogicServerId);
			}
		}
		if (userId != null) {
			String battleId = loadBalanceService.getBattleUserIdToBattleId(userId);
			if (battleId != null) {
				Integer connectedBattleServerId = loadBalanceService.getOneBattleIdToBattleServerId(battleId);
				if (connectedBattleServerId != null) {
					GatewayNoticeClientOfflinePush.Builder pushBuilder = GatewayNoticeClientOfflinePush.newBuilder();
					pushBuilder.setSessionId(sessionId);
					pushBuilder.setUserId(userId);
					pushBuilder.setIsUserOffline(isUserOffline);
					RemoteMessage remoteMsg = new RemoteMessage(
							RemoteRpcNameEnum.RemoteRpcGatewayNoticeClientOfflinePush_VALUE, pushBuilder);
					if (MessageManager.getInstance().sendRemoteMsgToBattleServer(remoteMsg,
							connectedLogicServerId) == false) {
						logger.error(
								"noticeClientOffline error, can't send client offline push to battleServer, sessionId = {}, userId = {}, battleId = {}, battleServerId = {}",
								sessionId, userId, battleId, connectedBattleServerId);
					}
				}
			}
		}
	}

	public int getOnlineSessionCount() {
		return sessionIdToChannelMap.size();
	}

	@Override
	public void init() {
		loadBalanceService = SpringManager.getInstance().getBean(LoadBalanceService.class);
		userService = SpringManager.getInstance().getBean(UserService.class);
	}

	@Override
	public void shutdown() {
		// 服务器关闭时，清除服务器连接地址、负载信息
		loadBalanceService.removeOneGatewayLoadBalance(ConfigManager.GATEWAY_ID);
		loadBalanceService.removeOneGatewayIdToAkkaPath(ConfigManager.GATEWAY_ID);
		loadBalanceService.removeOneGatewayIdToConnectPath(ConfigManager.GATEWAY_ID);
		// 移除所有session，清除sessionId与gatewayId的对应信息，并向客户端连上的logicServer或battleServer发出通知
		for (int sessionId : sessionIdToChannelMap.keySet()) {
			loadBalanceService.removeOneSessionIdToGatewayId(sessionId);
			Integer userId = loadBalanceService.getOneSessionIdToUserId(sessionId);
			loadBalanceService.removeOneSessionIdToUserId(sessionId);
			if (userId != null) {
				// 这里必须判断当前userId对应的最新sessionId是不是这个sessionId，因为处理同一账号二次登录后旧连接断开的情况时，不应该删除userId与最新sessionId的对应
				int userIdToSessionId = loadBalanceService.getOneUserIdToSessionId(userId);
				if (userIdToSessionId == sessionId) {
					loadBalanceService.removeOneUserIdToSessionId(userId);
					// 修改玩家状态为离线
					try {
						userService.changeUserOnlineState(userId, false);
					} catch (Exception e) {
						logger.error("removeSession error, changeUserOnlineState fail, error = ", e);
					}
					noticeClientOffline(sessionId, userId, true);
				} else {
					noticeClientOffline(sessionId, userId, false);
				}
			} else {
				noticeClientOffline(sessionId, null, false);
			}
		}
	}
}
