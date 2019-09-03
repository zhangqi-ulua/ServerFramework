package org.zhangqi.actor;

import org.zhangqi.annotation.MessageMethodMapping;
import org.zhangqi.manager.SpringManager;
import org.zhangqi.msg.LocalMessage;
import org.zhangqi.proto.LocalServer.LocalRpcNameEnum;
import org.zhangqi.proto.RemoteServer.BattleRoomInfo;
import org.zhangqi.service.BaseBattleService;

public class BaseBattleActor extends BaseMessageActor {

	private BaseBattleService baseBattleService = SpringManager.getInstance().getBean(BaseBattleService.class);

	public BaseBattleActor() {
		super();
	}

	public BaseBattleActor(String actionPackageName) {
		super(actionPackageName);
	}

	@MessageMethodMapping(LocalRpcNameEnum.LocalRpcBattleServerInitBattle_VALUE)
	public void initBattle(LocalMessage localMessage) throws Exception {
		BattleRoomInfo battleRoomInfo = (BattleRoomInfo) localMessage.getLite();
		baseBattleService.initBattle(battleRoomInfo);
	}
}
