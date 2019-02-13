package com.siloyou.jmsg.common.utils;
//用户账号工具类

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.sanerzone.common.modules.account.utils.AccountCacheUtils;
import com.sanerzone.common.support.utils.JedisClusterUtils;
import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.PowerUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.entity.Dict;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.DictUtils;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.account.dao.JmsgAccountDao;

public class JmsgAccountUtils {
	
	private static JmsgAccountDao jmsgAccountDao = SpringContextHolder.getBean(JmsgAccountDao.class);

	/**
	 * 获取用户列表
	 * @param appType 账号类型
	 * @return
	 */
	public static List<Dict> getAccountList(String appType){
		
		Map<String,String> pMap = Maps.newHashMap();
		
		User user = UserUtils.getUser();
		if(PowerUtils.adminFlag()){//管理员权限
			pMap.put("userType", "3");//代理商用户
		}else if(PowerUtils.agencyFlag()){//代理权限
			pMap.put("userType", "4");//普通用户
			pMap.put("companyId", user.getCompany().getId());
		}
		pMap.put("userId", user.getId());
		pMap.put("appType", appType);
		
		List<Dict> list = jmsgAccountDao.queryAccountList(pMap);
		return list;
	}
	
	/**
	 * 获取发送结果
	 * @param uPayMode 用户扣费方式
	 * @param payMode send表记录扣费方式
	 * @param sendStatus 发送状态
	 * @param successCnt 状态报告条数
	 * @return
	 */
	public static String getSendResult(String uPayMode,String payMode,String sendStatus,String successCnt){
		if("0".equals(uPayMode) || "0".equals(payMode)){
			return "成功";
		}else if("2".equals(uPayMode)){
			if("T000".equals(sendStatus)){//发送成功 
				if(StringUtils.isBlank(successCnt)){
					return "状态未知";
				}else{
					int cnt = Integer.valueOf(successCnt);
					if(cnt >0){
						return "成功";
					}else{
						return "失败";
					}
				}
			}else{
				return DictUtils.getDictLabel(sendStatus, "mms_send_status", "失败");
			}
		}
		
		return "状态未知";
	}
	
	/**
	 * 获取发送结果
	 * @param payMode send表记录扣费方式
	 * @param sendStatus 发送状态
	 * @param reportStatus 状态报告状态
	 * @return
	 */
	public static String getSendResultNew(String payMode,String sendStatus,String reportStatus){
		
		if(StringUtils.equals(payMode, "0")){//提交计费
			if(sendStatus.startsWith("T")){
				return "成功";
			}else if(sendStatus.startsWith("F")){
				return "失败";
			}
		}else if(StringUtils.equals(payMode, "2")){//状态报告成功
			if(sendStatus.startsWith("F")){
				return "失败("+sendStatus+")";
			}else{
				if(reportStatus.startsWith("T")){
					return "成功(DELIVRD)";
				}else if(reportStatus.startsWith("F")){
					return "失败("+reportStatus.replaceFirst("^F2", "")+")";
				}else if(reportStatus.startsWith("P")){
					return "成功";
				}
			}
		}
		return "状态未知";
	}
	
	public static String getAmount(String userId){
		String key = AccountCacheUtils.getAmountKey("sms", userId);
		return JedisClusterUtils.get(key);
	}
	
	public static String getUserReportStatus(String userId,Date day){
		String dayStr = DateUtils.formatDate(day, "yyyy-MM-dd");
		String key = CacheKeys.getPushReportStatus(userId, dayStr);
		return JedisClusterUtils.get(key);
	}
	
}
