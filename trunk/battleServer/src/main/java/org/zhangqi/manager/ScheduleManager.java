package org.zhangqi.manager;

public class ScheduleManager implements IManager {

	private static ScheduleManager instance = new ScheduleManager();

	public static ScheduleManager getInstance() {
		return instance;
	}

	@Override
	public void init() {
	}

	@Override
	public void shutdown() {
	}
}
