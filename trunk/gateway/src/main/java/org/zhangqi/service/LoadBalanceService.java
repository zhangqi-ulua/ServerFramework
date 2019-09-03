package org.zhangqi.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zhangqi.dao.LoadBalanceDAO;

@Service(value = "loadBalanceService")
public class LoadBalanceService {

	@Autowired
	private LoadBalanceDAO loadBalanceDAO;

	public int addAndGetNextAvailableSessionId() {
		return loadBalanceDAO.addAndGetNextAvailableSessionId();
	}

	public void setOneSessionIdToGatewayId(int sessionId, int gatewayId) {
		loadBalanceDAO.setOneSessionIdToGatewayId(sessionId, gatewayId);
	}

	public Integer getOneSessionIdToGatewayId(int sessionId) {
		return loadBalanceDAO.getOneSessionIdToGatewayId(sessionId);
	}

	public void removeOneSessionIdToGatewayId(int sessionId) {
		loadBalanceDAO.removeOneSessionIdToGatewayId(sessionId);
	}

	public void setOneSessionIdToLogicServerId(int sessionId, int logicServerId) {
		loadBalanceDAO.setOneSessionIdToLogicServerId(sessionId, logicServerId);
	}

	public Integer getOneSessionIdToLogicServerId(int sessionId) {
		return loadBalanceDAO.getOneSessionIdToLogicServerId(sessionId);
	}

	public void removeOneSessionIdToLogicServerId(int sessionId) {
		loadBalanceDAO.removeOneSessionIdToLogicServerId(sessionId);
	}

	public Integer getOneUserIdToSessionId(int userId) {
		return loadBalanceDAO.getOneUserIdToSessionId(userId);
	}

	public Map<Integer, Integer> getAllUserIdToSessionId() {
		return loadBalanceDAO.getAllUserIdToSessionId();
	}

	public void setOneUserIdToSessionId(int userId, int sessionId) {
		loadBalanceDAO.setOneUserIdToSessionId(userId, sessionId);
	}

	public void removeOneUserIdToSessionId(int userId) {
		loadBalanceDAO.removeOneUserIdToSessionId(userId);
	}

	public Integer getOneSessionIdToUserId(int sessionId) {
		return loadBalanceDAO.getOneSessionIdToUserId(sessionId);
	}

	public void setOneSessionIdToUserId(int sessionId, int userId) {
		loadBalanceDAO.setOneSessionIdToUserId(sessionId, userId);
	}

	public void removeOneSessionIdToUserId(int sessionId) {
		loadBalanceDAO.removeOneSessionIdToUserId(sessionId);
	}

	public void setBattleUserIdToBattleId(int userId, String battleId) {
		loadBalanceDAO.setBattleUserIdToBattleId(userId, battleId);
	}

	public String getBattleUserIdToBattleId(int userId) {
		return loadBalanceDAO.getBattleUserIdToBattleId(userId);
	}

	public void removeBattleUserIdToBattleId(int userId) {
		loadBalanceDAO.removeBattleUserIdToBattleId(userId);
	}

	public Integer getOneBattleIdToBattleServerId(String battleId) {
		return loadBalanceDAO.getOneBattleIdToBattleServerId(battleId);
	}

	public void setOneBattleIdToBattleServerId(String battleId, int battleServerId) {
		loadBalanceDAO.setOneBattleIdToBattleServerId(battleId, battleServerId);
	}

	public void removeOneBattleIdToBattleServerId(String battleId) {
		loadBalanceDAO.removeOneBattleIdToBattleServerId(battleId);
	}

	public void setOneLogicServerLoadBalance(int logicServerId, int count) {
		loadBalanceDAO.setOneLogicServerLoadBalance(logicServerId, count);
	}

	public void changeOneLogicServerLoadBalance(int logicServerId, int changeCount) {
		loadBalanceDAO.changeOneLogicServerLoadBalance(logicServerId, changeCount);
	}

	public void removeOneLogicServerLoadBalance(int logicServerId) {
		loadBalanceDAO.removeOneLogicServerLoadBalance(logicServerId);
	}

	public Map<Integer, Integer> getAllLogicServerLoadBalance() {
		return loadBalanceDAO.getAllLogicServerLoadBalance();
	}

	public void cleanLogicServerLoadBalance() {
		loadBalanceDAO.cleanLogicServerLoadBalance();
	}

	public Integer getLeisureLogicServerId() {
		return loadBalanceDAO.getLeisureLogicServerId();
	}

	public void setOneGatewayLoadBalance(int gatewayId, int count) {
		loadBalanceDAO.setOneGatewayLoadBalance(gatewayId, count);
	}

	public void changeOneGatewayLoadBalance(int gatewayId, int changeCount) {
		loadBalanceDAO.changeOneGatewayLoadBalance(gatewayId, changeCount);
	}

	public void removeOneGatewayLoadBalance(int gatewayId) {
		loadBalanceDAO.removeOneGatewayLoadBalance(gatewayId);
	}

	public Map<Integer, Integer> getAllGatewayLoadBalance() {
		return loadBalanceDAO.getAllGatewayLoadBalance();
	}

	public void cleanGatewayLoadBalance() {
		loadBalanceDAO.cleanGatewayLoadBalance();
	}

	public Integer getLeisureGatewayId() {
		return loadBalanceDAO.getLeisureGatewayId();
	}

	public void setOneBattleServerLoadBalance(int battleServerId, int count) {
		loadBalanceDAO.setOneBattleServerLoadBalance(battleServerId, count);
	}

	public void changeOneBattleServerLoadBalance(int battleServerId, int changeCount) {
		loadBalanceDAO.changeOneBattleServerLoadBalance(battleServerId, changeCount);
	}

	public void removeOneBattleServerLoadBalance(int battleServerId) {
		loadBalanceDAO.removeOneBattleServerLoadBalance(battleServerId);
	}

	public Map<Integer, Integer> getAllBattleServerLoadBalance() {
		return loadBalanceDAO.getAllBattleServerLoadBalance();
	}

	public void cleanBattleServerLoadBalance() {
		loadBalanceDAO.cleanBattleServerLoadBalance();
	}

	public Integer getLeisureBattleServerId() {
		return loadBalanceDAO.getLeisureBattleServerId();
	}

	public void setOneLogicServerIdToAkkaPath(int logicServerId, String akkaPath) {
		loadBalanceDAO.setOneLogicServerIdToAkkaPath(logicServerId, akkaPath);
	}

	public String getOneLogicServerIdToAkkaPath(int logicServerId) {
		return loadBalanceDAO.getOneLogicServerIdToAkkaPath(logicServerId);
	}

	public Map<Integer, String> getAllLogicServerIdToAkkaPath() {
		return loadBalanceDAO.getAllLogicServerIdToAkkaPath();
	}

	public void removeOneLogicServerIdToAkkaPath(int logicServerId) {
		loadBalanceDAO.removeOneLogicServerIdToAkkaPath(logicServerId);
	}

	public void cleanLogicServerIdToAkkaPath() {
		loadBalanceDAO.cleanLogicServerIdToAkkaPath();
	}

	public void setOneGatewayIdToAkkaPath(int gatewayId, String akkaPath) {
		loadBalanceDAO.setOneGatewayIdToAkkaPath(gatewayId, akkaPath);
	}

	public String getOneGatewayIdToAkkaPath(int gatewayId) {
		return loadBalanceDAO.getOneGatewayIdToAkkaPath(gatewayId);
	}

	public Map<Integer, String> getAllGatewayIdToAkkaPath() {
		return loadBalanceDAO.getAllGatewayIdToAkkaPath();
	}

	public void removeOneGatewayIdToAkkaPath(int gatewayId) {
		loadBalanceDAO.removeOneGatewayIdToAkkaPath(gatewayId);
	}

	public void cleanGatewayIdToAkkaPath() {
		loadBalanceDAO.cleanGatewayIdToAkkaPath();
	}

	public void setOneBattleServerIdToAkkaPath(int battleServerId, String akkaPath) {
		loadBalanceDAO.setOneBattleServerIdToAkkaPath(battleServerId, akkaPath);
	}

	public String getOneBattleServerIdToAkkaPath(int battleServerId) {
		return loadBalanceDAO.getOneBattleServerIdToAkkaPath(battleServerId);
	}

	public Map<Integer, String> getAllBattleServerIdToAkkaPath() {
		return loadBalanceDAO.getAllBattleServerIdToAkkaPath();
	}

	public void removeOneBattleServerIdToAkkaPath(int battleServerId) {
		loadBalanceDAO.removeOneBattleServerIdToAkkaPath(battleServerId);
	}

	public void cleanBattleServerIdToAkkaPath() {
		loadBalanceDAO.cleanBattleServerIdToAkkaPath();
	}

	public void setOneGatewayIdToConnectPath(int gatewayId, String connectPath) {
		loadBalanceDAO.setOneGatewayIdToConnectPath(gatewayId, connectPath);
	}

	public String getOneGatewayIdToConnectPath(int gatewayId) {
		return loadBalanceDAO.getOneGatewayIdToConnectPath(gatewayId);
	}

	public void removeOneGatewayIdToConnectPath(int gatewayId) {
		loadBalanceDAO.removeOneGatewayIdToConnectPath(gatewayId);
	}

	public void cleanGatewayIdToConnectPath() {
		loadBalanceDAO.cleanGatewayIdToConnectPath();
	}

	public void setMainLogicServerId(int mainLogicServerId) {
		loadBalanceDAO.setMainLogicServerId(mainLogicServerId);
	}

	public int getMainLogicServerId() {
		return loadBalanceDAO.getMainLogicServerId();
	}
}
