package org.zhangqi.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

public class SpringManager implements IManager {

	private static final Logger logger = LoggerFactory.getLogger(SpringManager.class);

	private static SpringManager instance = new SpringManager();

	public static SpringManager getInstance() {
		return instance;
	}

	private ClassPathXmlApplicationContext context;

	@Override
	public void init() {
		context = new ClassPathXmlApplicationContext("spring.xml");

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
}
