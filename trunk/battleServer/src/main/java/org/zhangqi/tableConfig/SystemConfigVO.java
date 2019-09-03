package org.zhangqi.tableConfig;

public class SystemConfigVO {
	private String key;
	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public SystemConfigVO() {
	}

	public SystemConfigVO(String key, String value) {
		this.key = key;
		this.value = value;
	}
}
