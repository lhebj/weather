package com.weather.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class DateUtil {

	public static String YEAR = "year";
	public static String MONTH = "month";
	public static String DAY = "day";
	public static String HOUR = "hour";
	public static String MINUTE = "minute";
	public static String SECOND = "second";

	public static int INTERVAL_JUST = 0;
	public static int INTERVAL_10M = 1;
	public static int INTERVAL_30M = 2;
	public static int INTERVAL_1H = 3;
	public static int INTERVAL_2H = 4;
	public static int INTERVAL_3H = 5;
	public static int INTERVAL_5H = 6;
	public static int INTERVAL_1D = 7;

	public static final SimpleDateFormat DEFAULT_YEAR_FORMAT = new SimpleDateFormat("yyyy");
	public static final SimpleDateFormat DEFAULT_YEAR_MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");
	public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new  SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat DEFAULT_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static final String DefaultPattern = "yyyy-MM-dd";
	 
	public static Map<String, Object> getIntervalOfDates(long second) {
		Map<String, Object> map = new HashMap<String, Object>();
		long minute = second / 60;
		long hour = minute / 60;
		long day = hour / 24;
		if (day > 0) {
			map.put("type", "day");
			map.put("interval", day);
		} else if (hour > 0) {
			map.put("type", "hour");
			map.put("interval", hour);
		} else {
			map.put("type", "minute");
			map.put("interval", minute);
		}
		return map;
	}

	public static boolean compareDateIsLarger(Date beginDate, Date endDate) {
		if (beginDate == null) {
			return true;
		}
		if (endDate == null) {
			return false;
		}
		long endTime = endDate.getTime();
		long beginTime = beginDate.getTime();
		return endTime > beginTime;
	}

	public static long getIntervalOfTwoDates(Date beginDate, Date endDate) {
		if (beginDate == null || endDate == null)
			return 0;
		long endTime = endDate.getTime();
		long beginTime = beginDate.getTime();
		long intervalMilliSecond = endTime > beginTime ? endTime - beginTime : 0;
		long intervalSecond = intervalMilliSecond / 1000;
		return intervalSecond;
	}
	/**
	 * 几�?时�?，几分钟�?，几秒�?
	 * @param date
	 * @return
	 */
	public static String convert(Date date) {
		String ret = "";
		long intervalMilliSecond = new Date().getTime() - date.getTime();
		if (intervalMilliSecond  >= 86400000) {
			ret = deSerialize(date, "MM月dd日");
		}else if (intervalMilliSecond  >= 3600000) {
			ret = intervalMilliSecond / 3600000 + "�?时�?";
		}else if (intervalMilliSecond  >= 60000) {
			ret = intervalMilliSecond / 60000 + "分钟�?";
		}else {
			ret = intervalMilliSecond / 1000 + "秒�?";
		} 
		return ret;
	}
	
	/**
	 * 将秒计数方�?转化为HH:mm:ss格�?
	 * @param seconds 总秒数
	 */
	public static String convert(Long seconds) {
		if(seconds==null){
			seconds = 0L;
		}
		int hour = (int)(seconds/3600);
		int minute = (int)((seconds-hour*3600)/60);
		int second = (int)((seconds-hour*3600-minute*60));
		
		String strHour = hour < 10 ? "0" + hour : "" + hour;
		String strMinute = minute < 10 ? "0" + minute : "" + minute;
		String strSecond = second < 10 ? "0" + second : "" + second;
		
		return strHour + ":" + strMinute + ":" + strSecond;
	}

	public static Date getDateBeforeHours(Date comparedDate, int cursor, String unit) {
		if (unit.equalsIgnoreCase("hour")) {
			long interval = new Long(cursor);
			long millisecond = comparedDate.getTime() - interval * 3600 * 1000;
			return new Date(millisecond);
		} else if (unit.equalsIgnoreCase("day")) {
			long interval = new Long(cursor);
			long millisecond = comparedDate.getTime() - interval * 3600 * 1000 * 24;
			return new Date(millisecond);
		} else {
			long millisecond = comparedDate.getTime() - cursor * 1000;
			return new Date(millisecond);
		}
	}

	public static Date getBefore(Date comparedDate, int cursor, String unit) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(comparedDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int date = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		if (unit.equalsIgnoreCase(SECOND)) {
			second = second - cursor;
		} else if (unit.equalsIgnoreCase(MINUTE)) {
			minute = minute - cursor;
		} else if (unit.equalsIgnoreCase(HOUR)) {
			hour = hour - cursor;
		} else if (unit.equalsIgnoreCase(DAY)) {
			date = date - cursor;
		} else if (unit.equalsIgnoreCase(MONTH)) {
			month = month - cursor;
		} else if (unit.equalsIgnoreCase(YEAR)) {
			year = year - cursor;
		}
		calendar.set(year, month, date, hour, minute, second);
		return calendar.getTime();
	}

	public static Date getAfter(Date comparedDate, int cursor, String unit) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(comparedDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int date = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		if (unit.equalsIgnoreCase(SECOND)) {
			second += cursor;
		} else if (unit.equalsIgnoreCase(MINUTE)) {
			minute += cursor;
		} else if (unit.equalsIgnoreCase(HOUR)) {
			hour += cursor;
		} else if (unit.equalsIgnoreCase(DAY)) {
			date += cursor;
		} else if (unit.equalsIgnoreCase(MONTH)) {
			month += cursor;
		} else if (unit.equalsIgnoreCase(YEAR)) {
			year += cursor;
		}
		calendar.set(year, month, date, hour, minute, second);
		return calendar.getTime();
	}

	public static String deSerialize(Date date, String pattern) {
		if (date == null)
			return "";
		String defaultPattern = "yyyy-MM-dd HH:mm";
		if (pattern == null)
			pattern = defaultPattern;
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(date);
	}

	public static Date serialize(String dateStr, String pattern) throws ParseException {
		String defaultPattern = "yyyy-MM-dd HH:mm";
		if (pattern == null)
			pattern = defaultPattern;
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.parse(dateStr);
	}
	
	/**
	 * 
	  * 标准化输入的结�?�日期，在原有日期的基础上添加23:59:59
	  * 例如原始时间是2012-1-21 标准化为2012-1-21 23:59:59
	  *
	  * @modify: gangwang  Jan 23, 2013 4:17:11 PM
	  * @param stringDate
	  * @return    
	  * @return Date
	 */
	public static Date serializeEndDateTime(String stringDate){
		Date date = null;
		try {
			date = serialize(stringDate, DefaultPattern);
			date = getAfter(date, 1, "DAY");
			date = getBefore(date, 1, "SECOND");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static int getIntervalTypeOfTwoDates(Date beginDate, Date endDate) {
		if (beginDate == null || endDate == null)
			return INTERVAL_JUST;
		long endTime = endDate.getTime();
		long beginTime = beginDate.getTime();
		long intervalMilliSecond = endTime > beginTime ? endTime - beginTime : 0;
		long intervalSecond = intervalMilliSecond / 1000;
		if (intervalSecond < 600) {
			return INTERVAL_JUST;
		} else if (intervalSecond > 600 && intervalSecond < 1800) {
			return INTERVAL_10M;
		} else if (intervalSecond > 1800 && intervalSecond < 3600) {
			return INTERVAL_30M;
		} else if (intervalSecond > 3600 && intervalSecond < 7200) {
			return INTERVAL_1H;
		} else if (intervalSecond > 7200 && intervalSecond < 10800) {
			return INTERVAL_2H;
		} else if (intervalSecond > 10800 && intervalSecond < 18000) {
			return INTERVAL_3H;
		} else if (intervalSecond > 18000 && intervalSecond < 3600 * 24) {
			return INTERVAL_5H;
		} else if (intervalSecond > 3600 * 24) {
			return INTERVAL_1D;
		}
		return INTERVAL_JUST;
	}
	
	/**
	 * 获得date日期该天的开始时间 例如：2011-04-27 00:00:00是2011-04-27的开始时间
	 */
	public static Date getBeginTimeForDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}
	
	/**
	 * 获得date日期该天的结�?�时间 例如：2011-04-28 00:00:00是2011-04-27的结�?�时间
	 */
	public static Date getEndTimeForDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}
	
	/**
	 * 获得指定date.getTime()到现在的年数
	 * @param ms
	 * @return
	 */
	public static long getIntervalOfYears(long ms) {
		long interval = new Date().getTime() - ms;
		long years = interval / (1000l * 60 * 60 * 24 * 365);
		long reminder = interval % (1000l * 60 * 60 * 24 * 365);
		return reminder > 0l ? years + 1 : years;
	}
	
	public static Date parseYear(String yearStr){
		return parse(yearStr, DEFAULT_YEAR_FORMAT);
	}
	
	public static Date parseDate(String dateStr){
		return parse(dateStr, DEFAULT_DATE_FORMAT);
	}
	
	public static Date parseDatetime(String datetimeStr){
		return parse(datetimeStr, DEFAULT_DATETIME_FORMAT);
	}
	
	public static Date parse(String str, String formatStr){
		if(StringUtil.isNull(str) || StringUtil.isNull(formatStr)){
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat(formatStr);
		return parse(str, format);
	}
	
	/**
	 * 字符串转Date
	 */
	public static Date parse(String str, DateFormat format){
		if(StringUtil.isNull(str)){
			return null;
		}
		try {
			return format.parse(str);
		} catch (ParseException e) {
		}
		return null;
	}
	
	public static String formatDate(Date date){
		return format(date, DEFAULT_DATE_FORMAT);
	}
	
	public static String formatYear(Date date){
		return format(date, DEFAULT_YEAR_FORMAT);
	}
	
	public static String formatDatetime(Date date){
		return format(date, DEFAULT_DATETIME_FORMAT);
	}
	
	/**
	 * Date转字符串
	 */
	public static String format(Date date, DateFormat format){
		if(date == null || format == null){
			return "";
		}
		return format.format(date);
	}
	

}
