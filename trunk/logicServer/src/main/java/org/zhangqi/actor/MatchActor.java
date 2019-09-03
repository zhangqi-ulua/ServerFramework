package org.zhangqi.actor;

import java.util.List;

import org.zhangqi.annotation.MessageMethodMapping;
import org.zhangqi.manager.MessageManager;
import org.zhangqi.manager.SpringManager;
import org.zhangqi.msg.LocalMessage;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.msg.RemoteMessage;
import org.zhangqi.proto.Common.MatchResultPush;
import org.zhangqi.proto.LocalServer.LocalRpcNameEnum;
import org.zhangqi.proto.RemoteServer.BattleRoomInfo;
import org.zhangqi.proto.RemoteServer.NoticeBattleServerCreateNewBattleResponse;
import org.zhangqi.proto.RemoteServer.RemoteRpcNameEnum;
import org.zhangqi.proto.Rpc.RpcNameEnum;
import org.zhangqi.service.MatchService;
import org.zhangqi.service.UserService;

public class MatchActor extends BaseMessageActor {

	private final LocalMessage localMsgMatch = new LocalMessage(LocalRpcNameEnum.LocalRpcLogicServerMatch_VALUE);

	private MatchService matchService = SpringManager.getInstance().getBean(MatchService.class);
	private UserService userService = SpringManager.getInstance().getBean(UserService.class);

	public MatchActor() {
		super();
	}

	public MatchActor(String actionPackageName) {
		super(actionPackageName);
	}

	@Override
	public void preStart() {
		super.preStart();

		// 每隔1秒给自己发匹配消息，从而实现间隔1秒进行1次匹配计算
		super.schedule(0, 1, localMsgMatch);
	}

	@MessageMethodMapping(LocalRpcNameEnum.LocalRpcLogicServerMatch_VALUE)
	public void doMatch(LocalMessage localMessage) throws Exception {
		matchService.doMatch();
	}

	@MessageMethodMapping(RemoteRpcNameEnum.RemoteRpcNoticeBattleServerCreateNewBattle_VALUE)
	public void onReceivedNoticeBattleServerCreateNewBattle(RemoteMessage remoteMsg) throws Exception {
		NoticeBattleServerCreateNewBattleResponse response = remoteMsg
				.getLite(NoticeBattleServerCreateNewBattleResponse.class);
		BattleRoomInfo battleRoomInfo = response.getBattleRoomInfo();
		List<Integer> userIds = battleRoomInfo.getUserIdsList();
		MatchResultPush.Builder pushBuilder = MatchResultPush.newBuilder();
		pushBuilder.setIsSuccess(true);
		pushBuilder.setBattleType(battleRoomInfo.getBattleType());
		pushBuilder.setBattleId(battleRoomInfo.getBattleId());
		for (int userId : userIds) {
			pushBuilder.addUserBriefInfos(userService.getUserBriefInfo(userId));
		}

		NetMessage netMsg = new NetMessage(RpcNameEnum.MatchResultPush_VALUE, pushBuilder);
		for (int userId : userIds) {
			MessageManager.getInstance().sendNetMsgToOneUser(userId, netMsg, MatchResultPush.class);
		}
	}
}
