package org.zhangqi;

import org.zhangqi.manager.ConfigManager;
import org.zhangqi.manager.CoreManager;
import org.zhangqi.manager.MessageManager;
import org.zhangqi.manager.OnlineClientManager;
import org.zhangqi.manager.ScheduleManager;
import org.zhangqi.manager.SpringManager;
import org.zhangqi.manager.TableConfigManager;

public class Main {

	public static void main(String[] args) {
		CoreManager.getInstance().registManager(ConfigManager.getInstance());
		CoreManager.getInstance().registManager(SpringManager.getInstance());
		CoreManager.getInstance().registManager(TableConfigManager.getInstance());
		CoreManager.getInstance().registManager(OnlineClientManager.getInstance());
		CoreManager.getInstance().registManager(MessageManager.getInstance());
		CoreManager.getInstance().registManager(ScheduleManager.getInstance());

		CoreManager.getInstance().init();

		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
