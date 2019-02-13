/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.core.common.utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 * @author ThinkGem
 * @version 2014-4-15
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
	
	private static String[] parsePatterns = {
		"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", 
		"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
		"yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}
	
	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}
	
	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date, Object... pattern) {
		String formatDate = null;
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString());
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
		}
		return formatDate;
	}
	
	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}
	
	/**
	 * 日期型字符串转化为日期 格式
	 * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", 
	 *   "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
	 *   "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
	 */
	public static Date parseDate(Object str) {
		if (str == null){
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(24*60*60*1000);
	}

	/**
	 * 获取过去的小时
	 * @param date
	 * @return
	 */
	public static long pastHour(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(60*60*1000);
	}
	
	/**
	 * 获取过去的分钟
	 * @param date
	 * @return
	 */
	public static long pastMinutes(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(60*1000);
	}
	
	/**
	 * 转换为时间（天,时:分:秒.毫秒）
	 * @param timeMillis
	 * @return
	 */
    public static String formatDateTime(long timeMillis){
		long day = timeMillis/(24*60*60*1000);
		long hour = (timeMillis/(60*60*1000)-day*24);
		long min = ((timeMillis/(60*1000))-day*24*60-hour*60);
		long s = (timeMillis/1000-day*24*60*60-hour*60*60-min*60);
		long sss = (timeMillis-day*24*60*60*1000-hour*60*60*1000-min*60*1000-s*1000);
		return (day>0?day+",":"")+hour+":"+min+":"+s+"."+sss;
    }
	
	/**
	 * 获取两个日期之间的天数
	 * 
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceOfTwoDate(Date before, Date after) {
		long beforeTime = before.getTime();
		long afterTime = after.getTime();
		return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
	}
	
	//当月最大的时间（秒）
	public static Long getMonthMaxDayTime(){
		//获取当前月最后一天
        Calendar ca = Calendar.getInstance();    
        ca.set(Calendar.DATE, ca.getActualMaximum(Calendar.DATE));
        ca.set(Calendar.HOUR_OF_DAY, 23);  
        //将分钟至59
        ca.set(Calendar.MINUTE, 59);  
        //将秒至59
        ca.set(Calendar.SECOND,59); 
        return ca.getTimeInMillis();
	}
	
	public static Date getDay(Date date,int hour,int minute,int second){
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
        ca.set(Calendar.HOUR_OF_DAY, hour);  
        //将分钟至59
        ca.set(Calendar.MINUTE, minute);  
        //将秒至59
        ca.set(Calendar.SECOND,second);
        return ca.getTime();
	}
	
	public static Date getDay(int hour,int minute,int second){
		Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, hour);  
        //将分钟至59
        ca.set(Calendar.MINUTE, minute);  
        //将秒至59
        ca.set(Calendar.SECOND,second); 
        return ca.getTime();
	}
	
	
	//当天最大的时间（秒）
	public static Long getMaxDayTime(){
        return getDay(23, 59, 59).getTime();
	}
	
	/**
	 * 
	 * @return 当前时间距当天最大时间相隔时间（秒）
	 */
	public static int getEndDayTime(){
		Long second = (getMaxDayTime() - System.currentTimeMillis())/1000;
		return second.intValue();
	}
	
	/**
	 * 
	 * @return 当前时间距当月最后一天相隔的时间（秒）
	 */
	public static int getEndMonthTime(){
		Long second = (getMonthMaxDayTime()- System.currentTimeMillis())/1000;
		return second.intValue();
	}
	
	/**
	 * 获取当前日期  负数表示N天前  正数表示N天后
	 * @param index 
	 * @return
	 */
    public static Date getDay(int index) {  
        Calendar calendar = Calendar.getInstance();  
        calendar.add(Calendar.DATE, index);  
        return calendar.getTime();  
    }
    
    public static int getDayOfMonth(int index){
    	Calendar calendar = Calendar.getInstance();  
        calendar.add(Calendar.DATE, index);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
    
    public static int getDayOfMonth(Date date){
    	Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
    
    /**
	 * 获取当前日期所在月的第一天
	 * 
	 * @return
	 */
	public static Date getFirstDayOfMonth(){
		Calendar currentDate = Calendar.getInstance();   
		currentDate.set(Calendar.DAY_OF_MONTH,1);  
	    return (Date)currentDate.getTime().clone();
	}
	
	/**
	 * 获取当前日期所在月的最后一天
	 * 
	 * @return
	 */
	public static Date getEndDayOfMonth(){
		Calendar currentDate = Calendar.getInstance();   
		currentDate.set(Calendar.DAY_OF_MONTH, currentDate.getActualMaximum(Calendar.DAY_OF_MONTH));  
	    return (Date)currentDate.getTime().clone();
	}
	
	//获取表下标
	public static String getTableIndex(Date day){
		Calendar calendar = Calendar.getInstance();
		if(day == null){
			day = calendar.getTime();
		}
		double dis = getDistanceOfTwoDate(day, calendar.getTime());
		if(dis >=4){
			return "history_"+formatDate(day, "yyyyMM");
		}
		return formatDate(day, "d");
	}
	
	/**
	 * 获取指定日期所在月的日期列表
	 * @param date
	 * @return
	 */
	public static List<Date> getAllTheDateOftheMonth(Date date)
	{
		List<Date> list = new ArrayList<Date>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DATE, 1);
		int month = cal.get(Calendar.MONTH);
		while(cal.get(Calendar.MONTH) == month){
			list.add(cal.getTime());
			cal.add(Calendar.DATE, 1);
		}
		return list;
	}
	
	/**
	 * 获取指定日期偏移量的日期列表
	 * @param date
	 * @param days
	 * @return
	 */
	public static List<Date> getAllTheDate(Date date, int days)
	{
		List<Date> list = new ArrayList<Date>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		//int day;
		
		if (days > 0)
		{
			/*day = cal.get(Calendar.DAY_OF_MONTH);
			cal.add(Calendar.DAY_OF_MONTH, days);
			
			while(cal.get(Calendar.DAY_OF_MONTH) >= day){
				list.add(cal.getTime());
				cal.add(Calendar.DAY_OF_MONTH, -1);
			}*/
			
			cal.add(Calendar.DAY_OF_MONTH, days);
			while(days >= 0)
			{
				list.add(cal.getTime());
				cal.add(Calendar.DAY_OF_MONTH, -1);
				days --;
			}
		}
		else if (days < 0)
		{
			/*day = cal.get(Calendar.DAY_OF_MONTH);
			cal.add(Calendar.DAY_OF_MONTH, days);
			
			while(cal.get(Calendar.DAY_OF_MONTH) <= day){
				list.add(cal.getTime());
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}*/
			
			cal.add(Calendar.DAY_OF_MONTH, days);
			while(days <= 0)
			{
				list.add(cal.getTime());
				cal.add(Calendar.DAY_OF_MONTH, 1);
				days ++;
			}
		}
		else
		{
			list.add(date);
		}
		
		return list;
	}
	
	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		String day = "2017-06-09";
		System.out.println(formatDate(DateUtils.parseDate(day),"d"));
		long l = 1496246400l;
		System.out.println(DateUtils.formatDateTime(l));
	}
	

}
