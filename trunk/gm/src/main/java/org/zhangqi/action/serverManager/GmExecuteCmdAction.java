package org.zhangqi.action.serverManager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.zhangqi.action.BaseMessageAction;
import org.zhangqi.annotation.MessageClassMapping;
import org.zhangqi.exception.RpcErrorException;
import org.zhangqi.manager.CoreManager;
import org.zhangqi.manager.MessageManager;
import org.zhangqi.manager.OnlineClientManager;
import org.zhangqi.manager.TableConfigManager;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.msg.RemoteMessage;
import org.zhangqi.network.session.ISession;
import org.zhangqi.proto.Common.GmCmdTypeEnum;
import org.zhangqi.proto.Common.RemoteServerTypeEnum;
import org.zhangqi.proto.Gm.GmExecuteCmdRequest;
import org.zhangqi.proto.Gm.GmExecuteCmdResponse;
import org.zhangqi.proto.Gm.GmRpcErrorCodeEnum;
import org.zhangqi.proto.Gm.GmRpcNameEnum;
import org.zhangqi.proto.Gm.GmTextMsgPush;
import org.zhangqi.proto.RemoteServer.NoticeExecuteGmCmdRequest;
import org.zhangqi.proto.RemoteServer.RemoteRpcNameEnum;

@Controller
@MessageClassMapping(GmRpcNameEnum.GmRpcExecuteCmd_VALUE)
public class GmExecuteCmdAction extends BaseMessageAction {

	private static final Logger logger = LoggerFactory.getLogger(GmExecuteCmdAction.class);

	@Override
	protected void LogRequest(IMessage requestMessage) throws Exception {
		NetMessage req = (NetMessage) requestMessage;
		logger.info("[req GmExecuteCmd]:\n{}", req.getProtobufText(GmExecuteCmdRequest.class));
	}

	@Override
	protected void LogResponse(IMessage responseMessage) throws Exception {
		NetMessage resp = (NetMessage) responseMessage;
		logger.info("[resp GmExecuteCmd]:\n{}", resp.getProtobufText(GmExecuteCmdResponse.class));
	}

	@Override
	protected IMessage doAction(IMessage requestMessage) throws Exception {
		ISession session = ((NetMessage) requestMessage).getSession();
		NetMessage msg = (NetMessage) requestMessage;
		GmExecuteCmdRequest req = msg.getLite(GmExecuteCmdRequest.class);
		RemoteServerTypeEnum serverType = req.getServerType();
		GmCmdTypeEnum cmdType = req.getCmdType();
		List<Integer> serverIdsList = req.getServerIdsList();
		List<String> paramsList = req.getParamsList();
		GmExecuteCmdResponse.Builder builder = GmExecuteCmdResponse.newBuilder();

		NoticeExecuteGmCmdRequest.Builder executeGmCmdReqBuilder = NoticeExecuteGmCmdRequest.newBuilder();
		executeGmCmdReqBuilder.setCmdType(cmdType);
		executeGmCmdReqBuilder.addAllParams(paramsList);
		RemoteMessage remoteMsg = new RemoteMessage(RemoteRpcNameEnum.RemoteRpcNoticeExecuteGmCmd_VALUE,
				executeGmCmdReqBuilder);

		if (serverType == RemoteServerTypeEnum.ServerTypeLogic) {
			if (serverIdsList.size() < 1) {
				MessageManager.getInstance().sendRemoteMsgToAllLogicServer(remoteMsg, NoticeExecuteGmCmdRequest.class);
			} else {
				MessageManager.getInstance().sendRemoteMsgToLogicServer(remoteMsg, serverIdsList,
						NoticeExecuteGmCmdRequest.class);
			}
		} else if (serverType == RemoteServerTypeEnum.ServerTypeBattle) {
			if (serverIdsList.size() < 1) {
				MessageManager.getInstance().sendRemoteMsgToAllBattleServer(remoteMsg, NoticeExecuteGmCmdRequest.class);
			} else {
				MessageManager.getInstance().sendRemoteMsgToBattleServer(remoteMsg, serverIdsList,
						NoticeExecuteGmCmdRequest.class);
			}
		} else if (serverType == RemoteServerTypeEnum.ServerTypeGateway) {
			if (serverIdsList.size() < 1) {
				MessageManager.getInstance().sendRemoteMsgToAllGateway(remoteMsg, NoticeExecuteGmCmdRequest.class);
			} else {
				MessageManager.getInstance().sendRemoteMsgToGateway(remoteMsg, serverIdsList,
						NoticeExecuteGmCmdRequest.class);
			}
		} else if (serverType == RemoteServerTypeEnum.ServerTypeGm) {
			GmTextMsgPush.Builder pushBuilder = GmTextMsgPush.newBuilder();
			if (cmdType == GmCmdTypeEnum.GmCmdCloseServer) {
				pushBuilder.setText("[ServerTypeGm]  start execute gm cmd GmCmdCloseServer");
				NetMessage netMsg = new NetMessage(GmRpcNameEnum.GmRpcTextMsgPush_VALUE, pushBuilder);
				OnlineClientManager.getInstance().pushToAllGmSession(netMsg, GmTextMsgPush.class);
				CoreManager.getInstance().shutdown();
				System.exit(0);
			} else if (cmdType == GmCmdTypeEnum.GmCmdReloadTableConfig) {
				pushBuilder.setText("[ServerTypeGm]  start execute gm cmd GmCmdReloadTableConfig");
				NetMessage netMsg = new NetMessage(GmRpcNameEnum.GmRpcTextMsgPush_VALUE, pushBuilder);
				OnlineClientManager.getInstance().pushToAllGmSession(netMsg, GmTextMsgPush.class);
				TableConfigManager.getInstance().reloadTableConfig();
			} else {
				logger.error("unsupport cmdType = {}", cmdType);
				throw new RpcErrorException(GmRpcErrorCodeEnum.GmRpcServerError_VALUE);
			}
		} else {
			logger.error("unsupport serverType = {}", serverType);
			throw new RpcErrorException(GmRpcErrorCodeEnum.GmRpcServerError_VALUE);
		}

		return super.buildResponseNetMsg(session, GmRpcNameEnum.GmRpcExecuteCmd_VALUE, builder);
	}
}
