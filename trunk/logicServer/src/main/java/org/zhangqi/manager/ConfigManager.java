package org.zhangqi.manager;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigManager implements IManager {

	private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

	private static ConfigurationFactory factory;
	private static Configuration config;

	private static ConfigManager instance = new ConfigManager();

	public static ConfigManager getInstance() {
		return instance;
	}

	public static String REMOTE_GM_AKKA_PATH;
	public static int LOGIC_SERVER_ID;
	public static boolean IS_MAIN_LOGIC_SERVER;
	public static String LOGIC_SERVER_AKKA_PATH;

	public static int ZONE_ID;
	public static int ZONE_ID_TIMES;

	public ConfigManager() {
	}

	@Override
	public void init() {
		logger.info("------------ start load config ------------");
		loadConfig();
		logger.info("------------ finish load config ------------");
	}

	@Override
	public void shutdown() {
	}

	private void loadConfig() {
		try {
			factory = new ConfigurationFactory("propertyConfig.xml");
			config = factory.getConfiguration();
		} catch (ConfigurationException e) {
			logger.error("config init fail, exception = ", e);
			System.exit(-1);
		}

		REMOTE_GM_AKKA_PATH = config.getString("remote.gm.akka.path");
		LOGIC_SERVER_ID = config.getInt("logic.server.id");
		IS_MAIN_LOGIC_SERVER = config.getBoolean("is.main.logic.server");
		LOGIC_SERVER_AKKA_PATH = config.getString("logic.server.akka.path");
		ZONE_ID = config.getInt("zone.id");
		ZONE_ID_TIMES = config.getInt("zone.id.times");
	}
}
