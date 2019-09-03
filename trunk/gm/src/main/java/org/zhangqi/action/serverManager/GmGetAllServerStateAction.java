package org.zhangqi.action.serverManager;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.zhangqi.action.BaseMessageAction;
import org.zhangqi.annotation.MessageClassMapping;
import org.zhangqi.constants.GmSessionDataKeyConstant;
import org.zhangqi.exception.RpcErrorException;
import org.zhangqi.manager.MessageManager;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.network.session.ISession;
import org.zhangqi.proto.Common.RemoteServerTypeEnum;
import org.zhangqi.proto.Gm.GmGetAllServerStateRequest;
import org.zhangqi.proto.Gm.GmGetAllServerStateResponse;
import org.zhangqi.proto.Gm.GmRpcErrorCodeEnum;
import org.zhangqi.proto.Gm.GmRpcNameEnum;
import org.zhangqi.proto.Gm.ServerLoadBalanceInfo;
import org.zhangqi.service.LoadBalanceService;

@Controller
@MessageClassMapping(GmRpcNameEnum.GmRpcGetAllServerState_VALUE)
public class GmGetAllServerStateAction extends BaseMessageAction {

	private static final Logger logger = LoggerFactory.getLogger(GmGetAllServerStateAction.class);

	@Autowired
	private LoadBalanceService loadBalanceService;

	@Override
	protected void LogRequest(IMessage requestMessage) throws Exception {
		NetMessage req = (NetMessage) requestMessage;
		logger.info("[req GmGetAllServerState gmUserId = {}]:\n{}",
				req.getSession().getData(GmSessionDataKeyConstant.GM_USER_ID_KEY),
				req.getProtobufText(GmGetAllServerStateRequest.class));
	}

	@Override
	protected void LogResponse(IMessage responseMessage) throws Exception {
		NetMessage resp = (NetMessage) responseMessage;
		logger.info("[resp GmGetAllServerState gmUserId = {}]:\n{}",
				resp.getSession().getData(GmSessionDataKeyConstant.GM_USER_ID_KEY),
				resp.getProtobufText(GmGetAllServerStateResponse.class));
	}

	@Override
	protected IMessage doAction(IMessage requestMessage) throws Exception {
		ISession session = ((NetMessage) requestMessage).getSession();
		GmGetAllServerStateResponse.Builder builder = GmGetAllServerStateResponse.newBuilder();

		// logicServer
		int mainLogicServerId = loadBalanceService.getMainLogicServerId();
		Map<Integer, Integer> allLogicServerLoadBalance = loadBalanceService.getAllLogicServerLoadBalance();
		for (Map.Entry<Integer, Integer> entry : allLogicServerLoadBalance.entrySet()) {
			int serverId = entry.getKey();
			int loadBalance = entry.getValue();
			String ip = MessageManager.getInstance().getServerIpWithPort(RemoteServerTypeEnum.ServerTypeLogic,
					serverId);
			if (ip == null) {
				logger.error("can't find logicServer ip, serverId = {}", serverId);
				throw new RpcErrorException(GmRpcErrorCodeEnum.GmRpcServerError_VALUE);
			}
			ServerLoadBalanceInfo.Builder oneLogicServerBuilder = ServerLoadBalanceInfo.newBuilder();
			oneLogicServerBuilder.setId(serverId);
			oneLogicServerBuilder.setIp(ip);
			oneLogicServerBuilder.setLoadBalance(loadBalance);
			oneLogicServerBuilder.setIsMainLogicServer(serverId == mainLogicServerId);

			builder.addLogicServerInfos(oneLogicServerBuilder);
		}

		// battleServer
		Map<Integer, Integer> allBattleServerLoadBalance = loadBalanceService.getAllBattleServerLoadBalance();
		for (Map.Entry<Integer, Integer> entry : allBattleServerLoadBalance.entrySet()) {
			int serverId = entry.getKey();
			int loadBalance = entry.getValue();
			String ip = MessageManager.getInstance().getServerIpWithPort(RemoteServerTypeEnum.ServerTypeBattle,
					serverId);
			if (ip == null) {
				logger.error("can't find battleServer ip, serverId = {}", serverId);
				throw new RpcErrorException(GmRpcErrorCodeEnum.GmRpcServerError_VALUE);
			}
			ServerLoadBalanceInfo.Builder oneBattleServerBuilder = ServerLoadBalanceInfo.newBuilder();
			oneBattleServerBuilder.setId(serverId);
			oneBattleServerBuilder.setIp(ip);
			oneBattleServerBuilder.setLoadBalance(loadBalance);

			builder.addBattleServerInfos(oneBattleServerBuilder);
		}

		// gateway
		Map<Integer, Integer> allGatewayLoadBalance = loadBalanceService.getAllGatewayLoadBalance();
		for (Map.Entry<Integer, Integer> entry : allGatewayLoadBalance.entrySet()) {
			int serverId = entry.getKey();
			int loadBalance = entry.getValue();
			String ip = MessageManager.getInstance().getServerIpWithPort(RemoteServerTypeEnum.ServerTypeGateway,
					serverId);
			if (ip == null) {
				logger.error("can't find gateway ip, serverId = {}", serverId);
				throw new RpcErrorException(GmRpcErrorCodeEnum.GmRpcServerError_VALUE);
			}
			String connectPath = loadBalanceService.getOneGatewayIdToConnectPath(serverId);
			if (connectPath == null) {
				logger.error("can't find gateway connectPath, serverId = {}", serverId);
				throw new RpcErrorException(GmRpcErrorCodeEnum.GmRpcServerError_VALUE);
			}
			ServerLoadBalanceInfo.Builder oneGatewayBuilder = ServerLoadBalanceInfo.newBuilder();
			oneGatewayBuilder.setId(serverId);
			oneGatewayBuilder.setIp(ip);
			oneGatewayBuilder.setLoadBalance(loadBalance);
			oneGatewayBuilder.setGatewayConnectPath(connectPath);

			builder.addGatewayInfos(oneGatewayBuilder);
		}

		return super.buildResponseNetMsg(session, GmRpcNameEnum.GmRpcGetAllServerState_VALUE, builder);
	}
}
