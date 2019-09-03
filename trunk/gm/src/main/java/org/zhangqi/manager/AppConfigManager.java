package org.zhangqi.manager;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfigManager implements IManager {

	private static final Logger logger = LoggerFactory.getLogger(AppConfigManager.class);

	private static ConfigurationFactory factory;
	private static Configuration config;

	private static AppConfigManager instance = new AppConfigManager();

	public static AppConfigManager getInstance() {
		return instance;
	}

	public static String GM_ADMIN_USERNAME;
	public static String GM_ADMIN_PASSWORD_MD5;

	public AppConfigManager() {
	}

	@Override
	public void init() {
		try {
			factory = new ConfigurationFactory("propertyConfig.xml");
			config = factory.getConfiguration();
		} catch (ConfigurationException e) {
			logger.error("config init fail, exception = ", e);
			System.exit(-1);
		}

		GM_ADMIN_USERNAME = config.getString("gm.admin.username");
		GM_ADMIN_PASSWORD_MD5 = config.getString("gm.admin.passwordMD5");
	}

	@Override
	public void shutdown() {
	}
}
