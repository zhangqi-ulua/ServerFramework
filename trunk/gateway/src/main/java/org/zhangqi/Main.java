package org.zhangqi;

import org.zhangqi.manager.ConfigManager;
import org.zhangqi.manager.CoreManager;
import org.zhangqi.manager.MessageManager;
import org.zhangqi.manager.NettyManager;
import org.zhangqi.manager.OnlineClientManager;
import org.zhangqi.manager.SpringManager;

public class Main {

	public static void main(String[] args) {
		CoreManager.getInstance().registManager(ConfigManager.getInstance());
		CoreManager.getInstance().registManager(SpringManager.getInstance());
		CoreManager.getInstance().registManager(OnlineClientManager.getInstance());
		CoreManager.getInstance().registManager(MessageManager.getInstance());

		// netty启动会阻塞主线程，需要最后再启动
		CoreManager.getInstance().registManager(NettyManager.getInstance());

		CoreManager.getInstance().init();
	}
}
