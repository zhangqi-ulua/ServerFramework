package org.zhangqi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zhangqi.dao.BaseBattleDAO;
import org.zhangqi.exception.RpcErrorException;
import org.zhangqi.manager.MessageManager;
import org.zhangqi.manager.OnlineClientManager;
import org.zhangqi.msg.NetMessage;
import org.zhangqi.proto.BaseBattle.BattleEventMsgListPush;
import org.zhangqi.proto.BaseBattle.BattleRecordData;
import org.zhangqi.proto.BaseBattle.CurrentTurnInfo;
import org.zhangqi.proto.BaseBattle.EndTurnEvent;
import org.zhangqi.proto.BaseBattle.EventMsg;
import org.zhangqi.proto.BaseBattle.EventMsgList;
import org.zhangqi.proto.BaseBattle.EventTypeEnum;
import org.zhangqi.proto.BaseBattle.GameOverEvent;
import org.zhangqi.proto.BaseBattle.GameOverReasonEnum;
import org.zhangqi.proto.BaseBattle.PlacePiecesEvent;
import org.zhangqi.proto.BaseBattle.StartTurnEvent;
import org.zhangqi.proto.Common.BattleTypeEnum;
import org.zhangqi.proto.RemoteServer.BattleRoomInfo;
import org.zhangqi.proto.Rpc.RpcErrorCodeEnum;
import org.zhangqi.proto.Rpc.RpcNameEnum;
import org.zhangqi.utils.DateTimeUtil;

import com.google.protobuf.GeneratedMessage;

@Service(value = "baseBattleService")
public class BaseBattleService {

	private static final Logger logger = LoggerFactory.getLogger(BaseBattleService.class);

	@Autowired
	private BaseBattleDAO baseBattleDAO;
	@Autowired
	private UserService userService;

	private static final List<Integer> initCellInfo = new ArrayList<Integer>();

	static {
		for (int i = 0; i < 9; ++i) {
			initCellInfo.add(0);
		}
	}

	public void addPlayingBattleId(String battleId, BattleTypeEnum battleType) {
		baseBattleDAO.addPlayingBattleId(battleId, battleType);
	}

	public void removePlayingBattleId(String battleId, BattleTypeEnum battleType) {
		baseBattleDAO.removePlayingBattleId(battleId, battleType);
	}

	public void initOneBattleUserIds(String battleId, List<Integer> userIds) {
		baseBattleDAO.initOneBattleUserIds(battleId, userIds);
	}

	public List<Integer> getOneBattleUserIds(String battleId) {
		return baseBattleDAO.getOneBattleUserIds(battleId);
	}

	public void cleanOneBattleUserIds(String battleId) {
		baseBattleDAO.cleanOneBattleUserIds(battleId);
	}

	public void setBattleCurrentTurnInfo(String battleId, CurrentTurnInfo info) {
		baseBattleDAO.setBattleCurrentTurnInfo(battleId, info);
	}

	public CurrentTurnInfo getBattleCurrentTurnInfo(String battleId) throws Exception {
		return baseBattleDAO.getBattleCurrentTurnInfo(battleId);
	}

	public void removeBattleCurrentTurnInfo(String battleId) {
		baseBattleDAO.removeBattleCurrentTurnInfo(battleId);
	}

	public void setOneBattleCellInfo(String battleId, int index, int value) {
		baseBattleDAO.setOneBattleCellInfo(battleId, index, value);
	}

	public void initAllBattleCellInfo(String battleId, List<Integer> allCellInfo) {
		baseBattleDAO.initAllBattleCellInfo(battleId, allCellInfo);
	}

	public Integer getOneBattleCellInfo(String battleId, int index) {
		return baseBattleDAO.getOneBattleCellInfo(battleId, index);
	}

	public List<Integer> getAllBattleCellInfo(String battleId) {
		return baseBattleDAO.getAllBattleCellInfo(battleId);
	}

	public void cleanAllBattleCellInfo(String battleId) {
		baseBattleDAO.cleanAllBattleCellInfo(battleId);
	}

	public void addOneBattleEvent(String battleId, EventMsg eventMsg) {
		baseBattleDAO.addOneBattleEvent(battleId, eventMsg);
	}

	public int addAndGetNextAvailableEventNum(String battleId) {
		return baseBattleDAO.addAndGetNextAvailableEventNum(battleId);
	}

	public int getLastEventNum(String battleId) {
		return baseBattleDAO.getLastEventNum(battleId);
	}

	public void removeLastEventNum(String battleId) {
		baseBattleDAO.removeLastEventNum(battleId);
	}

	public void setOneBattleStartTimestamp(String battleId, long startTimestamp) {
		baseBattleDAO.setOneBattleStartTimestamp(battleId, startTimestamp);
	}

	public long getOneBattleStartTimestamp(String battleId) {
		return baseBattleDAO.getOneBattleStartTimestamp(battleId);
	}

	public void removeOneBattleStartTimestamp(String battleId) {
		baseBattleDAO.removeOneBattleStartTimestamp(battleId);
	}

	public void initOnebattleNotReadyUserIds(String battleId, List<Integer> userIds) {
		Integer[] array = userIds.toArray(new Integer[userIds.size()]);
		baseBattleDAO.initOnebattleNotReadyUserIds(battleId, array);
	}

	public Set<Integer> getOnebattleNotReadyUserIds(String battleId) {
		return baseBattleDAO.getOnebattleNotReadyUserIds(battleId);
	}

	public void removeOnebattleNotReadyUserId(String battleId, int userId) {
		baseBattleDAO.removeOnebattleNotReadyUserId(battleId, userId);
	}

	public void cleanOnebattleNotReadyUserIds(String battleId) {
		baseBattleDAO.cleanOnebattleNotReadyUserIds(battleId);
	}

	public List<Integer> getOneUserAllOpponentUserIds(String battleId, Integer userId) {
		List<Integer> userIds = new ArrayList<Integer>(baseBattleDAO.getOneBattleUserIds(battleId));
		userIds.remove(userId);
		return userIds;
	}

	public int getOneUserOneOpponentUserId(String battleId, Integer userId) {
		return getOneUserAllOpponentUserIds(battleId, userId).get(0);
	}

	/**
	 * 获取某个玩家在某场战斗的行动顺序（先手为1，依次递增）
	 */
	public int getOneUserSeq(String battleId, Integer userId) {
		List<Integer> userIds = baseBattleDAO.getOneBattleUserIds(battleId);
		return userIds.indexOf(userId) + 1;
	}

	public int getOneUserIdBySeq(String battleId, int seq) {
		List<Integer> userIds = baseBattleDAO.getOneBattleUserIds(battleId);
		return userIds.get(seq - 1);
	}

	/**
	 * 检测并返回某场战斗获胜者的行动顺序
	 * 
	 * @param justPlacePiecesIndex 刚刚落子的位置，只需检测该子上下斜向是否连成而胜利即可
	 * @return -1表示战斗未完，0表示平局，除此之外的数字表示游戏因一方胜利而结束，返回获胜者的行动顺序
	 */
	public int checkAndGetWinnerUserSeq(String battleId, int justPlacePiecesIndex) {
		boolean isWin = true;
		List<Integer> allBattleCellInfo = baseBattleDAO.getAllBattleCellInfo(battleId);
		int justPlacePiecesUserSeq = allBattleCellInfo.get(justPlacePiecesIndex);
		// 检测横行是否连成
		isWin = true;
		int rowNum = justPlacePiecesIndex / 3;
		int thisRowStartIndex = rowNum * 3;
		for (int i = thisRowStartIndex; i < thisRowStartIndex + 3; ++i) {
			if (allBattleCellInfo.get(i) != justPlacePiecesUserSeq) {
				isWin = false;
				break;
			}
		}
		if (isWin == true) {
			return justPlacePiecesUserSeq;
		}
		// 检测竖列是否连成
		isWin = true;
		int columnNum = justPlacePiecesIndex % 3;
		for (int i = columnNum; i <= columnNum + 6; i += 3) {
			if (allBattleCellInfo.get(i) != justPlacePiecesUserSeq) {
				isWin = false;
				break;
			}
		}
		if (isWin == true) {
			return justPlacePiecesUserSeq;
		}
		// 如果该落子位置处于对角线，检测斜向是否连成
		// 检测斜线方向（/）
		isWin = true;
		if (justPlacePiecesIndex == 2 || justPlacePiecesIndex == 4 || justPlacePiecesIndex == 6) {
			for (int i = 2; i <= 6; i += 2) {
				if (allBattleCellInfo.get(i) != justPlacePiecesUserSeq) {
					isWin = false;
					break;
				}
			}
		} else {
			isWin = false;
		}
		if (isWin == true) {
			return justPlacePiecesUserSeq;
		}
		// 检测反斜线方向（\）
		isWin = true;
		if (justPlacePiecesIndex == 0 || justPlacePiecesIndex == 4 || justPlacePiecesIndex == 8) {
			for (int i = 0; i <= 8; i += 4) {
				if (allBattleCellInfo.get(i) != justPlacePiecesUserSeq) {
					isWin = false;
					break;
				}
			}
		} else {
			isWin = false;
		}
		if (isWin == true) {
			return justPlacePiecesUserSeq;
		}

		// 发现没有一方胜利，则根据棋盘是否已满，返回对战未完成或平局
		for (int oneCellInfo : allBattleCellInfo) {
			if (oneCellInfo == 0) {
				return -1;
			}
		}
		return 0;
	}

	public void initBattle(BattleRoomInfo battleRoomInfo) {
		long currentTimestamp = DateTimeUtil.getCurrentTimestamp();
		String battleId = battleRoomInfo.getBattleId();
		List<Integer> userIds = battleRoomInfo.getUserIdsList();
		baseBattleDAO.initOneBattleUserIds(battleId, userIds);
		baseBattleDAO.initAllBattleCellInfo(battleId, initCellInfo);
		baseBattleDAO.setOneBattleStartTimestamp(battleId, currentTimestamp);
		initOnebattleNotReadyUserIds(battleId, userIds);
	}

	public void startFirstTurn(String battleId) throws Exception {
		List<Integer> userIds = baseBattleDAO.getOneBattleUserIds(battleId);
		CurrentTurnInfo.Builder builder = CurrentTurnInfo.newBuilder();
		// 因为在处理startTurnEvent时，会将当前回合玩家切换到下一个玩家，同时检测如果又轮到先手回合时，则回合数+1
		builder.setUserId(userIds.get(userIds.size() - 1));
		builder.setTurnCount(0);
		baseBattleDAO.setBattleCurrentTurnInfo(battleId, builder.build());

		StartTurnEvent.Builder startTurnEventBuilder = StartTurnEvent.newBuilder();
		EventMsg.Builder eventMsgBuilder = buildOneEvent(battleId, EventTypeEnum.EventTypeStartTurn,
				startTurnEventBuilder);
		EventMsgList.Builder eventMsgListBuilder = doEvent(battleId, eventMsgBuilder);
		pushEventMsgListToAllBattlePlayers(battleId, eventMsgListBuilder);
	}

	private List<EventMsg.Builder> doStartTurnEvent(String battleId, EventMsg.Builder eventBuilder) throws Exception {
		StartTurnEvent.Builder startTurnEventBuilder = eventBuilder.getStartTurnEvent().toBuilder();

		CurrentTurnInfo.Builder currentTurnInfoBuilder = baseBattleDAO.getBattleCurrentTurnInfo(battleId).toBuilder();
		currentTurnInfoBuilder.setTurnStartTimestamp(DateTimeUtil.getCurrentTimestamp());
		List<Integer> userIds = baseBattleDAO.getOneBattleUserIds(battleId);
		int index = userIds.indexOf(currentTurnInfoBuilder.getUserId());
		int nextTurnUserIndex = (index + 1) % userIds.size();
		currentTurnInfoBuilder.setUserId(userIds.get(nextTurnUserIndex));
		// 又轮到先手回合时，则回合数+1
		if (nextTurnUserIndex == 0) {
			currentTurnInfoBuilder.setTurnCount(currentTurnInfoBuilder.getTurnCount() + 1);
		}
		CurrentTurnInfo newCurrentTurnInfo = currentTurnInfoBuilder.build();
		baseBattleDAO.setBattleCurrentTurnInfo(battleId, newCurrentTurnInfo);
		startTurnEventBuilder.setCurrentTurnInfo(newCurrentTurnInfo);
		eventBuilder.setStartTurnEvent(startTurnEventBuilder);

		return null;
	}

	private List<EventMsg.Builder> doEndTurnEvent(String battleId, EventMsg.Builder eventBuilder) throws Exception {
		List<EventMsg.Builder> nextEventList = new ArrayList<EventMsg.Builder>();

		// 生成下一个事件为回合开始事件
		StartTurnEvent.Builder startTurnEventBuilder = StartTurnEvent.newBuilder();
		EventMsg.Builder eventMsgBuilder = buildOneEvent(battleId, EventTypeEnum.EventTypeStartTurn,
				startTurnEventBuilder);
		nextEventList.add(eventMsgBuilder);
		return nextEventList;
	}

	private List<EventMsg.Builder> doPlacePiecesEvent(String battleId, EventMsg.Builder eventBuilder) throws Exception {
		List<EventMsg.Builder> nextEventList = new ArrayList<EventMsg.Builder>();

		PlacePiecesEvent placePiecesEvent = eventBuilder.getPlacePiecesEvent();
		int userId = placePiecesEvent.getUserId();
		int index = placePiecesEvent.getIndex();

		baseBattleDAO.setOneBattleCellInfo(battleId, index, getOneUserSeq(battleId, userId));

		// 检测游戏是否结束
		int winnerUserSeq = checkAndGetWinnerUserSeq(battleId, index);
		if (winnerUserSeq != -1) {
			// 检测到游戏结束，下一个事件为游戏结束事件
			GameOverEvent.Builder gameOverEventBuilder = GameOverEvent.newBuilder();
			if (winnerUserSeq == 0) {
				gameOverEventBuilder.setWinnerUserId(0);
				gameOverEventBuilder.setGameOverReason(GameOverReasonEnum.GameOverDraw);
			} else {
				int winnerUserId = getOneUserIdBySeq(battleId, winnerUserSeq);
				gameOverEventBuilder.setWinnerUserId(winnerUserId);
				gameOverEventBuilder.setGameOverReason(GameOverReasonEnum.GameOverPlayerWin);
			}

			EventMsg.Builder eventMsgBuilder = buildOneEvent(battleId, EventTypeEnum.EventTypeGameOver,
					gameOverEventBuilder);
			nextEventList.add(eventMsgBuilder);
		} else {
			// 游戏尚未结束，下一个事件为回合结束事件
			EndTurnEvent.Builder endTurnEventBuilder = EndTurnEvent.newBuilder();
			endTurnEventBuilder.setEndTurnUserId(userId);
			endTurnEventBuilder.setIsForceEndTurn(false);
			EventMsg.Builder eventMsgBuilder = buildOneEvent(battleId, EventTypeEnum.EventTypeEndTurn,
					endTurnEventBuilder);
			nextEventList.add(eventMsgBuilder);
		}

		return nextEventList;
	}

	private List<EventMsg.Builder> doGameOverEvent(String battleId, EventMsg.Builder eventBuilder) throws Exception {
		GameOverEvent gameOverEvent = eventBuilder.getGameOverEvent();
		List<Integer> userIds = baseBattleDAO.getOneBattleUserIds(battleId);
		CurrentTurnInfo currentTurnInfo = baseBattleDAO.getBattleCurrentTurnInfo(battleId);

		// 记录战报
		BattleRecordData.Builder recordBuilder = BattleRecordData.newBuilder();
		recordBuilder.setBattleType(BattleTypeEnum.BattleTypeTwoPlayer);
		recordBuilder.setBattleId(battleId);
		for (int userId : userIds) {
			recordBuilder.addUserBriefInfos(userService.getUserBriefInfo(userId));
		}
		recordBuilder.setBattleStartTimestamp(baseBattleDAO.getOneBattleStartTimestamp(battleId));
		recordBuilder.setBattleEndTimestamp(DateTimeUtil.getCurrentTimestamp());
		recordBuilder.setTurnCount(currentTurnInfo.getTurnCount());
		recordBuilder.setWinnerUserId(gameOverEvent.getWinnerUserId());
		recordBuilder.setGameOverReason(gameOverEvent.getGameOverReason());
		baseBattleDAO.addOneBattleRecord(BattleTypeEnum.BattleTypeTwoPlayer, DateTimeUtil.getTodayZeroClockTimestamp(),
				recordBuilder.build());

		// 取消玩家的对战状态
		for (int userId : userIds) {
			userService.changeUserActionStateToNone(userId);
		}
		// 从进行中的对战中清除
		baseBattleDAO.removePlayingBattleId(battleId, BattleTypeEnum.BattleTypeTwoPlayer);
		// 清除对战产生的临时数据
		baseBattleDAO.cleanOneBattleUserIds(battleId);
		baseBattleDAO.removeBattleCurrentTurnInfo(battleId);
		baseBattleDAO.cleanAllBattleCellInfo(battleId);
		baseBattleDAO.removeLastEventNum(battleId);
		baseBattleDAO.removeOneBattleStartTimestamp(battleId);
		baseBattleDAO.cleanOnebattleNotReadyUserIds(battleId);

		OnlineClientManager.getInstance().removeBattleActor(battleId, userIds);

		return null;
	}

	/**
	 * 当生成一个要做的事件后，执行该事件的处理逻辑，并有可能产生后续事件，直到不再产生新的事件，才算执行完成。然后返回由第1个事件引发的所有事件的列表
	 */
	public EventMsgList.Builder doEvent(String battleId, EventMsg.Builder firstEventBuilder) throws Exception {
		EventMsgList.Builder builder = EventMsgList.newBuilder();

		// 存放目前未处理的消息，处理完之后删除，直到没有任何需要处理的事件之后进行返回
		List<EventMsg.Builder> todoEventList = new ArrayList<EventMsg.Builder>();
		todoEventList.add(firstEventBuilder);

		List<EventMsg.Builder> nextEventList = null;
		while (todoEventList.size() > 0) {
			EventMsg.Builder firstTodoEventBuilder = todoEventList.get(0);
			// 写入事件编号
			int eventNum = baseBattleDAO.addAndGetNextAvailableEventNum(battleId);
			firstTodoEventBuilder.setEventNum(eventNum);

			int todoEventType = firstTodoEventBuilder.getEventType().getNumber();
			switch (todoEventType) {
			case EventTypeEnum.EventTypeGameOver_VALUE: {
				nextEventList = doGameOverEvent(battleId, firstTodoEventBuilder);
				break;
			}
			case EventTypeEnum.EventTypeStartTurn_VALUE: {
				nextEventList = doStartTurnEvent(battleId, firstTodoEventBuilder);
				break;
			}
			case EventTypeEnum.EventTypeEndTurn_VALUE: {
				nextEventList = doEndTurnEvent(battleId, firstTodoEventBuilder);
				break;
			}
			case EventTypeEnum.EventTypePlacePieces_VALUE: {
				nextEventList = doPlacePiecesEvent(battleId, firstTodoEventBuilder);
				break;
			}
			default: {
				logger.error("doEvent error, unsupport eventType = {}", todoEventType);
				throw new RpcErrorException(RpcErrorCodeEnum.ServerError_VALUE);
			}
			}

			todoEventList.remove(0);
			// 直到对应函数中执行完具体事件后再进行事件存储，因为事件中的有些参数可能会在具体执行时才添加完整
			builder.addMsgList(firstTodoEventBuilder);
			baseBattleDAO.addOneBattleEvent(battleId, firstTodoEventBuilder.build());

			if (nextEventList != null) {
				todoEventList.addAll(0, nextEventList);
			}
		}

		return builder;
	}

	@SuppressWarnings("rawtypes")
	public EventMsg.Builder buildOneEvent(String battleId, EventTypeEnum eventType,
			GeneratedMessage.Builder eventBuilder) throws Exception {
		EventMsg.Builder builder = EventMsg.newBuilder();
		builder.setEventType(eventType);

		switch (eventType.getNumber()) {
		case EventTypeEnum.EventTypeGameOver_VALUE: {
			builder.setGameOverEvent((GameOverEvent.Builder) eventBuilder);
			break;
		}
		case EventTypeEnum.EventTypeStartTurn_VALUE: {
			builder.setStartTurnEvent((StartTurnEvent.Builder) eventBuilder);
			break;
		}
		case EventTypeEnum.EventTypeEndTurn_VALUE: {
			builder.setEndTurnEvent((EndTurnEvent.Builder) eventBuilder);
			break;
		}
		case EventTypeEnum.EventTypePlacePieces_VALUE: {
			builder.setPlacePiecesEvent((PlacePiecesEvent.Builder) eventBuilder);
			break;
		}
		default: {
			logger.error("buildOneEvent error, unsupport eventType = {}", eventType);
			throw new RpcErrorException(RpcErrorCodeEnum.ServerError_VALUE);
		}
		}

		return builder;
	}

	public void pushEventMsgListToAllBattlePlayers(String battleId, EventMsgList.Builder eventMsgListBuilder) {
		BattleEventMsgListPush.Builder pushBuilder = BattleEventMsgListPush.newBuilder();
		pushBuilder.setEventMsgList(eventMsgListBuilder);
		NetMessage netMsg = new NetMessage(RpcNameEnum.BattleEventMsgListPush_VALUE, pushBuilder);
		List<Integer> userIds = baseBattleDAO.getOneBattleUserIds(battleId);
		for (int userId : userIds) {
			MessageManager.getInstance().sendNetMsgToOneUser(userId, netMsg, BattleEventMsgListPush.class);
		}
	}

	public void pushEventMsgListToOneBattlePlayer(int userId, EventMsgList.Builder eventMsgListBuilder) {
		BattleEventMsgListPush.Builder pushBuilder = BattleEventMsgListPush.newBuilder();
		pushBuilder.setEventMsgList(eventMsgListBuilder);
		NetMessage netMsg = new NetMessage(RpcNameEnum.BattleEventMsgListPush_VALUE, pushBuilder);
		MessageManager.getInstance().sendNetMsgToOneUser(userId, netMsg, BattleEventMsgListPush.class);
	}

	public void pushEventMsgListToAllOpponentBattlePlayers(String battleId, int userId,
			EventMsgList.Builder eventMsgListBuilder) {
		BattleEventMsgListPush.Builder pushBuilder = BattleEventMsgListPush.newBuilder();
		pushBuilder.setEventMsgList(eventMsgListBuilder);
		NetMessage netMsg = new NetMessage(RpcNameEnum.BattleEventMsgListPush_VALUE, pushBuilder);
		List<Integer> allOpponentUserIds = getOneUserAllOpponentUserIds(battleId, userId);
		for (int oneOpponentUserId : allOpponentUserIds) {
			MessageManager.getInstance().sendNetMsgToOneUser(oneOpponentUserId, netMsg, BattleEventMsgListPush.class);
		}
	}
}
