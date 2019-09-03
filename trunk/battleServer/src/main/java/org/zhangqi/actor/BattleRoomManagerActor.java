package org.zhangqi.actor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.annotation.MessageMethodMapping;
import org.zhangqi.manager.OnlineClientManager;
import org.zhangqi.manager.SpringManager;
import org.zhangqi.msg.IMessage;
import org.zhangqi.msg.LocalMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.msg.RemoteMessage;
import org.zhangqi.proto.Common.BattleTypeEnum;
import org.zhangqi.proto.Common.UserState;
import org.zhangqi.proto.LocalServer.LocalRpcNameEnum;
import org.zhangqi.proto.RemoteServer.BattleRoomInfo;
import org.zhangqi.proto.RemoteServer.NoticeBattleServerCreateNewBattleRequest;
import org.zhangqi.proto.RemoteServer.NoticeBattleServerCreateNewBattleResponse;
import org.zhangqi.proto.RemoteServer.RemoteRpcNameEnum;
import org.zhangqi.proto.Rpc.RpcErrorCodeEnum;
import org.zhangqi.proto.Rpc.RpcNameEnum;
import org.zhangqi.service.BaseBattleService;
import org.zhangqi.service.LoadBalanceService;
import org.zhangqi.service.UserService;

import akka.actor.ActorRef;
import akka.actor.Props;

public class BattleRoomManagerActor extends BaseMessageActor {

	private static final Logger logger = LoggerFactory.getLogger(BattleRoomManagerActor.class);

	private LoadBalanceService loadBalanceService = SpringManager.getInstance().getBean(LoadBalanceService.class);
	private BaseBattleService baseBattleService = SpringManager.getInstance().getBean(BaseBattleService.class);
	private UserService userService = SpringManager.getInstance().getBean(UserService.class);

	public BattleRoomManagerActor() {
		super();
	}

	public BattleRoomManagerActor(String actionPackageName) {
		super(actionPackageName);
	}

	@MessageMethodMapping(value = { RemoteRpcNameEnum.RemoteRpcNoticeBattleServerCreateNewBattle_VALUE }, isNet = true)
	public void noticeBattleServerCreateNewBattle(RemoteMessage remoteMessage) {
		NoticeBattleServerCreateNewBattleRequest request = remoteMessage
				.getLite(NoticeBattleServerCreateNewBattleRequest.class);
		BattleRoomInfo battleRoomInfo = request.getBattleRoomInfo();
		BattleTypeEnum battleType = battleRoomInfo.getBattleType();
		String battleId = battleRoomInfo.getBattleId();
		List<Integer> userIds = battleRoomInfo.getUserIdsList();
		switch (battleType.getNumber()) {
		case BattleTypeEnum.BattleTypeTwoPlayer_VALUE: {
			// 加入到正在进行的对战集合中
			baseBattleService.addPlayingBattleId(battleId, battleType);
			// 创建对战Actor
			ActorRef battleActor = context()
					.actorOf(Props.create(BaseBattleActor.class, "org.zhangqi.action.battleRoomManager.baseBattle"));
			OnlineClientManager.getInstance().addBattleActor(battleId, battleActor, userIds);
			// 通知对战Actor初始化战斗
			LocalMessage localMessage = new LocalMessage(LocalRpcNameEnum.LocalRpcBattleServerInitBattle_VALUE,
					battleRoomInfo);
			battleActor.tell(localMessage, ActorRef.noSender());
			// 返回给mainLogicServer创建战斗成功
			NoticeBattleServerCreateNewBattleResponse.Builder builder = NoticeBattleServerCreateNewBattleResponse
					.newBuilder();
			builder.setBattleRoomInfo(battleRoomInfo);
			RemoteMessage remoteMsg = new RemoteMessage(
					RemoteRpcNameEnum.RemoteRpcNoticeBattleServerCreateNewBattle_VALUE, builder);
			sender().tell(remoteMsg, ActorRef.noSender());
			break;
		}
		default: {
			logger.error("handle RemoteRpcNoticeBattleServerCreateNewBattle error, unsupport battleType = {}",
					battleType);
			break;
		}
		}
	}

	// 对战相关的请求，都转到BattleRoomManagerActor，然后通过它转发到对应战场的BattleActor中处理
	@MessageMethodMapping(value = { RpcNameEnum.GetBattleInfo_VALUE, RpcNameEnum.Concede_VALUE,
			RpcNameEnum.PlacePieces_VALUE, RpcNameEnum.ForceEndTurn_VALUE, RpcNameEnum.ReadyToStartGame_VALUE,
			RpcNameEnum.ForceReadyToStartGame_VALUE }, isNet = true)
	public void proxyNetMessageInvoke(IMessage message) throws Exception {
		NetMessage netMessage = (NetMessage) message;
		int sessionId = netMessage.getSessionId();
		int userId = netMessage.getUserId();
		String battleId = loadBalanceService.getBattleUserIdToBattleId(userId);
		ActorRef battleActor = OnlineClientManager.getInstance().getBattleActor(battleId);
		if (battleActor == null) {
			// 若无法在本battleServer中找到对应的BattleActor，说明对战之前已创建，但因为之前负责的battleServer下线，而交由本服务器处理
			// 则需要为该战斗重新建立BattleActor
			UserState userState = userService.getUserState(userId);
			BattleTypeEnum battleType = userState.getBattleType();
			switch (battleType.getNumber()) {
			case BattleTypeEnum.BattleTypeTwoPlayer_VALUE: {
				battleActor = context().actorOf(
						Props.create(BaseBattleActor.class, "org.zhangqi.action.battleRoomManager.baseBattle"));
				break;
			}
			default: {
				logger.error("proxyNetMessageInvoke error, recreate battleActor error, unsupport battleType = {}",
						battleType);
				NetMessage errorNetMsg = new NetMessage(netMessage.getRpcNum(), RpcErrorCodeEnum.ServerError_VALUE);
				sender().tell(errorNetMsg, ActorRef.noSender());
				return;
			}
			}

			List<Integer> battleUserIds = baseBattleService.getOneBattleUserIds(battleId);
			OnlineClientManager.getInstance().addBattleActor(battleId, battleActor, battleUserIds);
		}

		OnlineClientManager.getInstance().addSessionIdToGatewayResponseActor(sessionId, sender());
		battleActor.tell(netMessage, sender());
	}
}
