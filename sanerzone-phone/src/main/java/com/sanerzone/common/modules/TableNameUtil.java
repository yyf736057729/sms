package com.sanerzone.common.modules;

import java.util.Calendar;
import java.util.Date;

import com.sanerzone.common.support.sequence.MsgId;
import com.sanerzone.common.support.utils.DateUtils;
import com.sanerzone.common.support.utils.StringUtils;

public class TableNameUtil {

	/**
	 * 根据业务ID获取表名下标
	 * @param bizid
	 * @return
	 */
	public static String getTableIndex(String bizid)
	{
		if (StringUtils.isNotBlank(bizid))
		{
			return String.valueOf(new MsgId(bizid).getDay());
		}
		else
		{
			return "";
		}
	}
	
	/**
	 * 根据业务ID获取表名下标
	 * 主要用于有历史表的场景
	 * @param bizid
	 * @return
	 */
	public static String getTableIndexNew(String bizid)
	{
		//当前时间
		Calendar current = Calendar.getInstance();
		
		int curMonth = current.get(Calendar.MONTH) + 1;
		int curDay = current.get(Calendar.DAY_OF_MONTH);
		
		if (StringUtils.isNotBlank(bizid))
		{
			try
			{
				String monthStr = "";
				int day = new MsgId(bizid).getDay();
				int month = new MsgId(bizid).getMonth();
				if (month < 10)
				{
					monthStr = "0" + month;
				}
				else
				{
					monthStr = "" + month;
				}
				
				//业务时间
				Calendar bizDate = Calendar.getInstance();
				
				if (month > curMonth)
				{
					bizDate.set(Calendar.YEAR, current.get(Calendar.YEAR) - 1);
				}
				else if (month == curMonth)
				{
					if (day > curDay)
					{
						bizDate.set(Calendar.YEAR, current.get(Calendar.YEAR) - 1);
					}
					else
					{
						bizDate.set(Calendar.YEAR, current.get(Calendar.YEAR));
					}
				}
				else
				{
					bizDate.set(Calendar.YEAR, current.get(Calendar.YEAR));
				}
				
				bizDate.set(Calendar.MONTH, month - 1);
				bizDate.set(Calendar.DAY_OF_MONTH, day);
				bizDate.set(Calendar.HOUR_OF_DAY, 0);
				bizDate.set(Calendar.MINUTE, 0);
				bizDate.set(Calendar.SECOND, 0);
				bizDate.set(Calendar.MILLISECOND, 0);
				
				Calendar yesterday = Calendar.getInstance();    //3天前  
		        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));  
		        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));  
		        yesterday.set(Calendar.DAY_OF_MONTH, yesterday.get(Calendar.DAY_OF_MONTH)-3);  
		        yesterday.set( Calendar.HOUR_OF_DAY, 0);  
		        yesterday.set( Calendar.MINUTE, 0);  
		        yesterday.set(Calendar.SECOND, 0);  
		        yesterday.set(Calendar.MILLISECOND, 0);
				
		        if (bizDate.after(yesterday) || DateUtils.isSameDay(bizDate, yesterday))
		        {
		        	return String.valueOf(new MsgId(bizid).getDay());
		        }
		        else
		        {
		        	return "history_" + bizDate.get(Calendar.YEAR) + monthStr;
		        }
			}
			catch(Exception e)
			{
				return String.valueOf(current.get(Calendar.DAY_OF_MONTH));
			}
		}
		else
		{
			return String.valueOf(current.get(Calendar.DAY_OF_MONTH));
		}
	}
	
	public static String getTableIndex(int day){
		return String.valueOf(DateUtils.getDayOfMonth(day));
	}
	
	public static String getTableIndex(Date date){
		return String.valueOf(DateUtils.getDayOfMonth(date));
	}
	
	public static void main(String[] args)
	{
		String bizId = "0401174628000212143328";
		System.out.println(bizId +" --------------------> "+ getTableIndexNew(bizId));
		bizId = "0510174628000212143328";
		System.out.println(bizId +" --------------------> "+ getTableIndexNew(bizId));
		bizId = "0509174628000212143328";
		System.out.println(bizId +" --------------------> "+ getTableIndexNew(bizId));
		bizId = "0508174628000212143328";
		System.out.println(bizId +" --------------------> "+ getTableIndexNew(bizId));
		bizId = "0507174628000212143328";
		System.out.println(bizId +" --------------------> "+ getTableIndexNew(bizId));
		bizId = "0506174628000212143328";
		System.out.println(bizId +" --------------------> "+ getTableIndexNew(bizId));
		bizId = "0505174628000212143328";
		System.out.println(bizId +" --------------------> "+ getTableIndexNew(bizId));
		bizId = "0501174628000212143328";
		System.out.println(bizId +" --------------------> "+ getTableIndexNew(bizId));
		bizId = "0101174628000212143328";
		System.out.println(bizId +" --------------------> "+ getTableIndexNew(bizId));
		bizId = "0601174628000212143328";
		System.out.println(bizId +" --------------------> "+ getTableIndexNew(bizId));
		bizId = "asdfghjkloiuytre";
		System.out.println(bizId +" --------------------> "+ getTableIndexNew(bizId));
		bizId = "";
		System.out.println(bizId +" --------------------> "+ getTableIndexNew(bizId));
	}
}
