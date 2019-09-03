package org.zhangqi.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.zhangqi.service.GmUserService;

public class SpringManager implements IManager {

	private static final Logger logger = LoggerFactory.getLogger(SpringManager.class);

	private static SpringManager instance = new SpringManager();

	public static SpringManager getInstance() {
		return instance;
	}

	private ConfigurableApplicationContext context;

	@Override
	public void init() {
		// 检查redis是否已连接
		try {
			JedisConnectionFactory factory = getBean(JedisConnectionFactory.class);
			RedisConnection connection = factory.getConnection();
			if ("PONG".equals(connection.ping()) == false) {
				logger.error("redis connect fail");
				System.exit(-1);
			}
		} catch (Exception e) {
			logger.error("redis connect fail, error = ", e);
			System.exit(-1);
		}

		// 检查GM默认管理员账号是否创建，如果没有则需要新建
		GmUserService gmUserService = SpringManager.getInstance().getBean(GmUserService.class);
		if (gmUserService.hasGmUsername(AppConfigManager.GM_ADMIN_USERNAME) == false) {
			logger.info("GM default admin is not exist, then create it");
			gmUserService.createNewGmUser(AppConfigManager.GM_ADMIN_USERNAME, AppConfigManager.GM_ADMIN_PASSWORD_MD5);
		}
	}

	@Override
	public void shutdown() {
	}

	public Object getBean(String beanName) {
		return context.getBean(beanName);
	}

	public <T> T getBean(Class<T> object) {
		return context.getBean(object);
	}

	public void setContext(ConfigurableApplicationContext context) {
		this.context = context;
	}
}
