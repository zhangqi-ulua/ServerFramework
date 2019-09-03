package org.zhangqi.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

	private static final ZoneOffset zdefaultZoneOffset = OffsetDateTime.now().getOffset();
	private static final ZoneId defaultZoneId = ZoneId.systemDefault();
	private static final DateTimeFormatter defaultDateTimeFormatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static long instantToTimestamp(Instant instant) {
		return instant.toEpochMilli();
	}

	public static Instant timestampToInstant(long timestamp) {
		return Instant.ofEpochMilli(timestamp);
	}

	public static Instant localDateTimeToInstant(LocalDateTime localDateTime) {
		return localDateTime.toInstant(zdefaultZoneOffset);
	}

	public static LocalDateTime instantTolocalDateTime(Instant instant) {
		return LocalDateTime.ofInstant(instant, defaultZoneId);
	}

	public static LocalDateTime timestampToLocalDateTime(long timestamp) {
		return instantTolocalDateTime(timestampToInstant(timestamp));
	}

	public static long localDateTimeToTimestamp(LocalDateTime localDateTime) {
		return instantToTimestamp(localDateTimeToInstant(localDateTime));
	}

	public static String localDateTimeToDateTimeString(LocalDateTime localDateTime) {
		return localDateTime.format(defaultDateTimeFormatter);
	}

	public static LocalDateTime dateTimeStringToLocalDateTime(String dateTimeString) {
		return LocalDateTime.parse(dateTimeString, defaultDateTimeFormatter);
	}

	public static long getCurrentTimestamp() {
		return Instant.now().toEpochMilli();
	}

	public static long getTodayZeroClockTimestamp() {
		LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
		return localDateTimeToTimestamp(localDateTime);
	}
}
