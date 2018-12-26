package com.seasy.microservice.core.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatetimeUtil {
	public static final String DEFAULT_PATTERN = "yyyy-MM-dd";
	public static final String DEFAULT_PATTERN_DT = "yyyy-MM-dd HH:mm:ss";

	public static Calendar getCalendar(){
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		
		if(cal.getTimeZone().getID().equalsIgnoreCase("GMT")){
			cal.add(Calendar.HOUR_OF_DAY, 8);
		}
		
		return cal;
	}
	
	public static Timestamp nowTimestamp(){
		Calendar cal = getCalendar();
		cal.set(Calendar.MILLISECOND, 0);
		return new Timestamp(cal.getTimeInMillis());
	}
	
	public static String getToday(String pattern){
		if(StringUtil.isEmpty(pattern)) {
			pattern = DEFAULT_PATTERN;
		}
		
		Calendar cal = getCalendar();
		String today = formatDate(cal.getTime(), pattern);
		
		return today;
	}
	
	public static String formatDate(Date date, String pattern){
		if(date == null) {
			return "";
		}
		
		if(StringUtil.isEmpty(pattern)) {
			pattern = DEFAULT_PATTERN;
		}
		
		try{
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			return sdf.format(date);
		}catch(Exception ex){
			return "";
		}
	}
	
	public static String formatDate(Timestamp timestamp, String pattern){
		if(timestamp == null) {
			return "";
		}
		return formatDate(new Date(timestamp.getTime()), pattern);
	}
	
	public static String formatDate(long longTime, String pattern){
		Calendar calender = getCalendar();
		calender.setTimeInMillis(longTime);
		return formatDate(calender.getTime(), pattern);
	}
	
	public static Timestamp toTimestamp(String date, String pattern){
		try{
			if(StringUtil.isEmpty(date)) return null;
			if(StringUtil.isEmpty(pattern)) pattern = DEFAULT_PATTERN;
			
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			Date dt = sdf.parse(date);
			return new Timestamp(dt.getTime());
		}catch(Exception ex){
			return null;
		}
	}
	
	public static Date toDate(String date, String pattern){
		try{
			if(StringUtil.isEmpty(date)) return null;
			if(StringUtil.isEmpty(pattern)) pattern = DEFAULT_PATTERN;
			
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			return sdf.parse(date);
		}catch(Exception ex){
			return null;
		}
	}
	
	public static Date toDate(Timestamp timestamp){
		try{
			if(timestamp == null) return null;
			Date dt = new Date(timestamp.getTime());
			return dt;
		}catch(Exception ex){
			return null;
		}
	}
	

	/**
	 * 为指定日期增加若干日期数，可正可负
	 * 
	 * @param sDate 日期字符串，格式为：yyyyMMdd or yyyyMM
	 * @param datepart 日期字段，如年、月、日、时、分、秒等
	 * @param value 要增加的日期数
	 * @param outPattern 日期的返回格式
	 */
	public static String add(String sDate, int datepart, int value, String outPattern){
		String ret = "";
		String defaultPattern = "yyyyMMdd";
		
		if(StringUtil.isEmpty(outPattern)) {
			outPattern = defaultPattern;
		}
		
		if(StringUtil.isEmpty(sDate)) {
			sDate = getToday(defaultPattern);
		}
		
		if(sDate.length() == 6) {
			sDate += "01";
		}
		
		Date date = toDate(sDate, defaultPattern);
		
		Calendar cal = getCalendar();
		cal.setTime(date);
		cal.add(datepart, value);
		
		ret = formatDate(cal.getTime(), outPattern);
		return ret;
	}
	
	public static String getSimpleWeekName(){
		String dayNames[] = {"日", "一", "二", "三", "四", "五", "六"};
		int dayOfWeek = getCalendar().get(Calendar.DAY_OF_WEEK) - 1;
		if(dayOfWeek < 0){
			dayOfWeek = 0;
		}
		return dayNames[dayOfWeek];
	}
	
	public static String getFullWeekName(){
		String dayNames[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
		int dayOfWeek = getCalendar().get(Calendar.DAY_OF_WEEK) - 1;
		if(dayOfWeek < 0){
			dayOfWeek = 0;
		}
		return dayNames[dayOfWeek];
	}
	
	public static void main(String[] args) {
		System.out.println(getCalendar().getTimeInMillis());
		System.out.println(formatDate(getCalendar().getTimeInMillis(), "yyyy-MM-dd HH:mm:ss"));
	}
}
