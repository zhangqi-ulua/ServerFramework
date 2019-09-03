package org.zhangqi.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhangqi.tableConfig.SystemConfigVO;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TableConfigManager implements IManager {

	private static final Logger logger = LoggerFactory.getLogger(TableConfigManager.class);

	private static TableConfigManager instance = new TableConfigManager();

	public static TableConfigManager getInstance() {
		return instance;
	}

	public static final String TABLE_CONFIG_FILE_PATH = "tableConfig/";

	private Gson gson = new Gson();
	private ClassLoader classLoader = TableConfigManager.class.getClassLoader();

	// SystemConfig，系统配置表，key:paramKey, value:paramValue
	private Map<String, String> systemConfigMap;

	@Override
	public void init() {
		logger.info("------------ start load table config ------------");
		loadTableConfig();
		logger.info("------------ finish load table config ------------");
	}

	public void reloadTableConfig() {
		logger.info("------------ reload table config ------------");
		loadTableConfig();
	}

	@Override
	public void shutdown() {
	}

	private String inputStreamToString(InputStream inputStream) {
		StringBuilder result = new StringBuilder();
		try {
			InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				result.append(line).append(System.lineSeparator());
			}
			br.close();
		} catch (Exception e) {
			logger.error("inputStreamToString error, error = ", e);
		}

		return result.toString();
	}

	private void loadTableConfig() {
		{
			// SystemConfig，系统配置表，key:paramKey, value:paramValue
			systemConfigMap = new HashMap<String, String>();
			InputStream inputStream = classLoader.getResourceAsStream(TABLE_CONFIG_FILE_PATH + "SystemConfig.txt");
			String json = inputStreamToString(inputStream);
			Map<String, SystemConfigVO> tempSystemConfig = gson.fromJson(json,
					new TypeToken<HashMap<String, SystemConfigVO>>() {
					}.getType());
			for (SystemConfigVO vo : tempSystemConfig.values()) {
				systemConfigMap.put(vo.getKey(), vo.getValue());
			}
			try {
				inputStream.close();
			} catch (IOException e) {
				logger.error("loadTableConfig close inputStream error = ", e);
			}
			logger.info("read table SystemConfig, data count = {}", systemConfigMap.size());
		}
	}

	public String getSystemStringConfigByKey(String key) {
		return systemConfigMap.get(key);
	}

	public Integer getSystemIntConfigByKey(String key) {
		if (systemConfigMap.containsKey(key)) {
			return Integer.parseInt(systemConfigMap.get(key));
		} else {
			return null;
		}
	}
}
