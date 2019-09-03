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

	public static int CLIENT_PORT;
	public static String WEBSOCKET_PATH;
	public static int WEBSOCKET_READER_IDLE_TIME;
	public static int WEBSOCKET_WRITER_IDLE_TIME;
	public static int WEBSOCKET_ALL_IDLE_TIME;

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

		CLIENT_PORT = config.getInt("client.port");
		WEBSOCKET_PATH = config.getString("websocket.path");
		WEBSOCKET_READER_IDLE_TIME = config.getInt("websocket.reader.idle.time");
		WEBSOCKET_WRITER_IDLE_TIME = config.getInt("websocket.writer.idle.time");
		WEBSOCKET_ALL_IDLE_TIME = config.getInt("websocket.all.idle.time");
	}
}
