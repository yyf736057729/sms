package com.sanerzone.common.modules.smscenter.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sanerzone.common.modules.account.dao.BaseAccountDao;
import com.sanerzone.common.modules.phone.dao.GatewayQueueDao;
import com.sanerzone.common.modules.phone.dao.JmsgSmsGatewayTmplDao;
import com.sanerzone.common.modules.phone.dao.JmsgSmsUserTmplDao;
import com.sanerzone.common.modules.phone.entity.GatewayQueue;
//import com.sanerzone.common.modules.phone.service.GatewayQueueService;
import com.sanerzone.common.modules.phone.entity.JmsgSmsGatewayTmpl;
import com.sanerzone.common.modules.phone.entity.JmsgSmsUserTmpl;
import com.sanerzone.common.modules.phone.utils.SmsUtils;
import com.sanerzone.common.support.utils.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.sanerzone.common.modules.account.utils.AccountCacheUtils;
import com.sanerzone.common.modules.smscenter.entity.GatewayResult;
import com.sanerzone.common.modules.smscenter.entity.JmsgGatewayInfo;
import com.sanerzone.common.support.utils.EhCacheUtils;
import com.sanerzone.common.support.utils.Encodes;
import com.sanerzone.common.support.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

//通道工具类
public class GatewayUtils {
//	@Autowired
//	GatewayQueueService gatewayQueueService;

	private static Logger logger = LoggerFactory.getLogger(GatewayUtils.class);

//	private static GatewayQueueDao gatewayQueueDao = SpringContextHolder.getBean(GatewayQueueDao.class);
//	private static JmsgSmsUserTmplDao jmsgSmsUserTmplDao = SpringContextHolder.getBean(JmsgSmsUserTmplDao.class);
//	private static JmsgSmsGatewayTmplDao jmsgSmsGatewayTmplDao= SpringContextHolder.getBean(JmsgSmsGatewayTmplDao.class);

	//匹配通道信息 校验签名
	public static GatewayResult getGateway(String userId,String groupId,String phoneType,String provinceId,String sign, boolean smsYzmFlag,String sendTag,String smsContent){
		return getGateway(userId, groupId, phoneType, provinceId, sign, true, smsYzmFlag,sendTag, smsContent);
	}

	//匹配通道信息  不校验签名
	public static GatewayResult getGateway(String userId,String groupId,String phoneType,String provinceId, boolean smsYzmFlag,String sendTag,String smsContent){
		return getGateway(userId, groupId, phoneType, provinceId, "", false, smsYzmFlag,sendTag, smsContent);
	}

	public static GatewayResult getGateway(String userId,String groupId,String phoneType,
			String provinceId,String sign,boolean signFlag, boolean smsYzmFlag,String sendTag,String smsContent){
		GatewayResult result = new GatewayResult();
		result.setErrorCode("F007");//匹配通道失败   --默认值

		//验证分组是否存在
		if(!GroupUtils.isExists(groupId)){
			result.setErrorCode("F0071");//分组不存在
			return result;
		}

		List<String> list = GatewayGroupUtils.get(groupId, phoneType, provinceId);
		if(list != null && list.size() >0){

			//优先遍历验证码通道
			if(smsYzmFlag) {
				getGatewayResult(result, list, userId, sign, signFlag, 0,sendTag,false,null,smsContent);
				if(result.isExists()) {
					result.setPolicy(1);
					return result;
				}
			}

			int usrYzmFlag = AccountCacheUtils.getIntegerValue(userId, "yzmGatewayFlag", 0);//验证码通道 1:是 0:否
			// 自定义签名且签名不为空，设置了签名优先
			if(!signFlag) {
				if(AccountCacheUtils.getIntegerValue(userId, "firstSign", 0) == 1//签名优先
						&& StringUtils.isNotBlank(sign)) {
					getGatewayResult(result, list, userId, sign, true, (usrYzmFlag == 1)?2:1,sendTag,false,null,smsContent);
					if(result.isExists()) {
						result.setPolicy(2);
						return result;
					}
				}
				Boolean userTmplFlag = AccountCacheUtils.getIntegerValue(userId, "userTmplFlag", 0) == 1;
				//判断内容模板信息优先规则
				if(userTmplFlag){
//					JmsgSmsUserTmpl jmsgSmsUserTmpl =new JmsgSmsUserTmpl();
//					jmsgSmsUserTmpl.setUserId(userId);
					List<JmsgSmsUserTmpl> jmsgSmsUserTmpls = (List<JmsgSmsUserTmpl>)EhCacheUtils.get(SmsUtils.JMSGSMSUSERTMPL,userId);
//					List<JmsgSmsUserTmpl> jmsgSmsUserTmpls = ScattereUtils.jmsgSmsUserTmplDao.selectByUseridTemps(jmsgSmsUserTmpl);
					getGatewayResult(result, list, userId, sign, false, (usrYzmFlag == 1)?2:1,sendTag,true,jmsgSmsUserTmpls,smsContent);
					if(result.isExists()) {
						result.setPolicy(3);
						return result;
					}
				}

			}
			// 默认
			getGatewayResult(result, list, userId, sign, signFlag, (usrYzmFlag == 1)?2:1,sendTag,false,null,smsContent);
			return result;

		}else{
			result.setErrorCode("F0072");//通道分组不存在
			return result;
		}

	}

	private static void getGatewayResult(GatewayResult result, List<String> list,
			String userId,String sign,boolean signFlag, int filterYzm,String sendTag,boolean b,List<JmsgSmsUserTmpl> jmsgSmsUserTmpls,String smsContent){
		for (String gatewayId : list) {
			String spNumber = getSpNumber(userId, gatewayId, sign, signFlag, filterYzm,sendTag,b,jmsgSmsUserTmpls,smsContent);//接入号
			if(StringUtils.isNotBlank(spNumber)){//配到到通道   通道的接入号不为空
				result.setErrorCode("T000");
				result.setGatewayId(gatewayId);
				result.setSpNumber(spNumber);
				return ;
			}
		}
	}

	private static boolean getYzmFlag (Map<String,String> param) {

		if(param == null) {
			return false;
		}

		Object obj = param.get("yzmGateway");
		if(obj == null) {
			return false;
		}

		return StringUtils.equals(String.valueOf(obj), "1");
	}

	//是否到匹配通道
	private static String getSpNumber(String userId,String gatewayId,String sign,boolean signFlag, int filterYzm,String sendTag,boolean b,List<JmsgSmsUserTmpl> jmsgSmsUserTmpls,
									  String smsContent){
		String spNumber = "";
		if(signFlag){//验证签名
			spNumber = SignUtils.get(userId, gatewayId, sign);
			if(StringUtils.isBlank(spNumber) || !StringUtils.isNumeric(spNumber)) {
				return "";
			}
		}

		JmsgGatewayInfo info = GatewayUtils.getGatewayInfo(gatewayId);//通道信息
		if(info != null && StringUtils.isNotBlank(info.getId())){

			boolean yzmGateway = getYzmFlag(info.getParams());
			// 0 只找验证码通道
			// 1 排除验证码通道
			// 2 查找全部

			if(filterYzm == 0 && !yzmGateway) {
				return "";
			}

			if(filterYzm == 1 && yzmGateway) {
				return "";
			}

			if(!"1".equals(info.getStatus())) {
				return"";//停用状态
			}
			String peram1 = filterYzm==0?"1":"";
			String peram2 = sendTag.equals("S") ? "2":"3";
			List<GatewayQueue> gatewayQueues= (List<GatewayQueue>)EhCacheUtils.get(SmsUtils.GATEWAYQUEUE,gatewayId);
//			List<GatewayQueue> gatewayQueues = ScattereUtils.gatewayQueueDao.findByGatewayId(gatewayId);
			if(signFlag){//是否有签名
				for(GatewayQueue gatewayQueue : gatewayQueues){
					if(gatewayQueue.getGateWayId().equals(gatewayId) && gatewayQueue.getStatus().equals("0")){//判断通道队列的状态及是否有通道队列
						if(gatewayQueue.getBusinessType().equals(peram1)){//验证码
							return info.getSpNumber() + spNumber;
						}else{//单发、群发
							if(peram2.equals(gatewayQueue.getBusinessType())){//验证码
								return info.getSpNumber() + spNumber;
							}
							return "";
						}
					}
				}
				return "";
			}else{//没有签名判断整个内容    默认
				if("1".equals(info.getCustomFlag()) && b != true){//是否支持自定义签名
					return getGateway(gatewayQueues, gatewayId, peram1, peram2, info);//直接返回接入号
				}else if(b == true){//
					for(GatewayQueue gatewayQueue : gatewayQueues){
						if(gatewayQueue.getGateWayId().equals(gatewayId) && gatewayQueue.getStatus().equals("0")){//判断通道队列的状态及是否有通道队列
							if(gatewayQueue.getBusinessType().equals(peram1)){
								return getJmsgSmsGatewayTmpl(gatewayId,jmsgSmsUserTmpls,smsContent,info);
							}else{//单发、群发
								if(peram2.equals(gatewayQueue.getBusinessType())){
									return getJmsgSmsGatewayTmpl(gatewayId,jmsgSmsUserTmpls,smsContent,info);
								}
								return "";
							}
						}
					}

					return "";
				}else{
					return "";
				}
			}
		}else{
			return "";
		}
	}

	public static String getJmsgSmsGatewayTmpl(String gatewayId,List<JmsgSmsUserTmpl> jmsgSmsUserTmpls,String smsContent,JmsgGatewayInfo info){
		for(JmsgSmsUserTmpl jmsgSmsUserTmpl1 : jmsgSmsUserTmpls){
			JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl =(JmsgSmsGatewayTmpl)EhCacheUtils.get(SmsUtils.JMSGSMSGATEWAYTMPL,jmsgSmsUserTmpl1.getTemplateId());
//			JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl = ScattereUtils.jmsgSmsGatewayTmplDao.get(jmsgSmsUserTmpl1.getTemplateId());
			String content = jmsgSmsGatewayTmpl.getTemplateContent();
			double similarity = SimilarityUtils.getSimilarity(content, smsContent);
			if(similarity>0.6){//模板相似度
				if(gatewayId.equals(jmsgSmsGatewayTmpl.getGatewayId())){
					return info.getSpNumber();
				}
			}
		}
		return "";
	}

	public static String getGateway(List<GatewayQueue> gatewayQueues,String gatewayId,String peram1,String peram2,JmsgGatewayInfo info){
		for(GatewayQueue gatewayQueue : gatewayQueues){
			if(gatewayQueue.getGateWayId().equals(gatewayId) && gatewayQueue.getStatus().equals("0")){//判断通道队列的状态及是否有通道队列
				if(gatewayQueue.getBusinessType().equals(peram1)){
					return info.getSpNumber();
				}else{//单发、群发
					return info.getSpNumber();
//					if(peram2.equals(gatewayQueue.getBusinessType())){
//					}

				}
			}
		}
		return "";
	}

	//获取应用代码
	public static String getAppCode(String gatewayId){
		JmsgGatewayInfo jmsgGatewayInfo = getGatewayInfo(gatewayId);
		if(jmsgGatewayInfo == null)return "";
		return jmsgGatewayInfo.getAppCode();
	}
	
	//获取通道信息
    public static JmsgGatewayInfo getGatewayInfo(String gatewayId){
    	Object obj = EhCacheUtils.get(CacheKeys.GATEWAY_CACHE, CacheKeys.getCacheGatewayInfoKey(gatewayId));
    	if(obj == null)return null;
    	return (JmsgGatewayInfo)obj;
    }
	
	public static void del(String gatewayId){
		String key = CacheKeys.getCacheGatewayInfoKey(gatewayId);
		EhCacheUtils.remove(CacheKeys.GATEWAY_CACHE, key);
	}
	
	public static void put(String gatewayId,Object value){
		
		JmsgGatewayInfo jmsgGatewayInfo = (JmsgGatewayInfo) value;
		try{
			if(StringUtils.isNotBlank(jmsgGatewayInfo.getExtParam())) {
				String jsonParams = Encodes.unescapeHtml(jmsgGatewayInfo.getExtParam());
		        Map<String, String> paraMap = JSON.parseObject(jsonParams, Map.class);
				jmsgGatewayInfo.setParams(paraMap);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		logger.info("{}, cache update, yzm:{}", gatewayId, getYzmFlag(jmsgGatewayInfo.getParams()));		
		String key = CacheKeys.getCacheGatewayInfoKey(gatewayId);
		EhCacheUtils.put(CacheKeys.GATEWAY_CACHE, key,jmsgGatewayInfo);
	}
	
}
