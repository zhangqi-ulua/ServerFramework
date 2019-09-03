package org.zhangqi;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.zhangqi.manager.AppConfigManager;
import org.zhangqi.manager.ConfigManager;
import org.zhangqi.manager.CoreManager;
import org.zhangqi.manager.MessageManager;
import org.zhangqi.manager.NettyManager;
import org.zhangqi.manager.OnlineClientManager;
import org.zhangqi.manager.ScheduleManager;
import org.zhangqi.manager.SpringManager;
import org.zhangqi.manager.TableConfigManager;

public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		ConfigurableApplicationContext context = event.getApplicationContext();

		SpringManager.getInstance().setContext(context);

		CoreManager.getInstance().registManager(ConfigManager.getInstance());
		CoreManager.getInstance().registManager(AppConfigManager.getInstance());
		CoreManager.getInstance().registManager(SpringManager.getInstance());
		CoreManager.getInstance().registManager(TableConfigManager.getInstance());
		CoreManager.getInstance().registManager(OnlineClientManager.getInstance());
		CoreManager.getInstance().registManager(MessageManager.getInstance());
		CoreManager.getInstance().registManager(ScheduleManager.getInstance());

		// netty启动会阻塞主线程，需要最后再启动
		CoreManager.getInstance().registManager(NettyManager.getInstance());

		CoreManager.getInstance().init();
	}
}
