package com.siloyou.jmsg.common.utils;

import java.util.List;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.EhCacheUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.entity.Dict;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.jmsg.modules.sms.dao.JmsgGatewayInfoDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgGroupDao;
import com.siloyou.jmsg.modules.sms.entity.GatewayResult;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayInfo;

//通道工具类
public class GatewayUtils {
	
	
	private static JmsgGatewayInfoDao jmsgGatewayInfoDao = SpringContextHolder.getBean(JmsgGatewayInfoDao.class);
	
	private static JmsgGroupDao jmsgGroupDao = SpringContextHolder.getBean(JmsgGroupDao.class);
	
	
	//匹配通道信息 校验签名
	public static GatewayResult getGateway(String userId,String groupId,String phoneType,String provinceId,String sign){
		return getGateway(userId, groupId, phoneType, provinceId, sign, true);
	}
	
	//匹配通道信息  不校验签名
	public static GatewayResult getGateway(String groupId,String phoneType,String provinceId){
		return getGateway("", groupId, phoneType, provinceId, "", false);
	}
	
	public static GatewayResult getGateway(String userId,String groupId,String phoneType,String provinceId,String sign,boolean signFlag){
		GatewayResult result = new GatewayResult();
		result.setErrorCode("F007");//匹配通道失败
		
		//验证分组是否存在
		if(!GroupUtils.isExists(groupId)){
			result.setErrorCode("F0071");//分组不存在
			return result;
		}
		
		List<String> list = GatewayGroupUtils.get(groupId, phoneType, provinceId);
		if(list != null && list.size() >0){
			for (String gatewayId : list) {
				String spNumber = getSpNumber(userId, gatewayId, sign, signFlag);//接入号
				if(StringUtils.isNotBlank(spNumber)){//配到到通道
					result.setErrorCode("T000");
					result.setGatewayId(gatewayId);
					result.setSpNumber(spNumber);
					return result;
				}
			}
		}else{
			result.setErrorCode("F0072");//通道分组不存在
			return result;
		}
		
		return result;
		
	}
	
	//是否到匹配通道
	private static String getSpNumber(String userId,String gatewayId,String sign,boolean signFlag){
		String spNumber = "";
		if(signFlag){//验证签名
			spNumber = SignUtils.get(userId, gatewayId, sign);
			if(StringUtils.isBlank(spNumber) || !StringUtils.isNumeric(spNumber)) {
				return "";
			}
		}
		
		JmsgGatewayInfo info = GatewayUtils.getGatewayInfo(gatewayId);
		if(info != null && StringUtils.isNotBlank(info.getId())){
			if(!"1".equals(info.getStatus()))return"";//停用状态
			if(signFlag){
				return info.getSpNumber() + spNumber;
			}else{
				if("1".equals(info.getCustomFlag())){
					return info.getSpNumber();
				}else{
					return "";
				}
			}
		}else{
			return "";
		}
	}
	
	//初始化通道信息
	public static void initGatewayInfo(){
		List<JmsgGatewayInfo> list = jmsgGatewayInfoDao.findList(new JmsgGatewayInfo());
		if(list != null && list.size()>0){
			for (JmsgGatewayInfo jmsgGatewayInfo : list) {
				String key = CacheKeys.getCacheGatewayInfoKey(jmsgGatewayInfo.getId());
				EhCacheUtils.put(CacheKeys.GATEWAY_CACHE, key, jmsgGatewayInfo);
			}
		}
	}
	
	//获取应用代码
	public static String getAppCode(String gatewayId){
		JmsgGatewayInfo jmsgGatewayInfo = getGatewayInfo(gatewayId);
		if(jmsgGatewayInfo ==null)return "";
		return jmsgGatewayInfo.getAppCode();
	}
	
	//获取通道信息
    public static JmsgGatewayInfo getGatewayInfo(String gatewayId){
    	String key = CacheKeys.getCacheGatewayInfoKey(gatewayId);
    	Object obj = EhCacheUtils.get(CacheKeys.GATEWAY_CACHE, key);
    	//if(true){
		if(obj == null){
    		JmsgGatewayInfo entity = jmsgGatewayInfoDao.get(gatewayId);
    		if(entity == null)entity = new JmsgGatewayInfo();
    		put(gatewayId, entity);
    		return entity;
    	}else{
    		if(obj instanceof  JmsgGatewayInfo){
				return (JmsgGatewayInfo)obj;
			}else{
    			//类型转换
				com.sanerzone.common.modules.smscenter.entity.JmsgGatewayInfo jmsgGatewayInfo1 = (com.sanerzone.common.modules.smscenter.entity.JmsgGatewayInfo)obj;
				JmsgGatewayInfo jmsgGatewayInfo = new JmsgGatewayInfo();

				User user = new User();
				user.setId(jmsgGatewayInfo1.getCreateBy());
				jmsgGatewayInfo.setCreateBy(user);

				User updateby = new User();
				updateby.setId(jmsgGatewayInfo1.getUpdateBy());
				jmsgGatewayInfo.setUpdateBy(updateby);

				jmsgGatewayInfo.setCreateDate(jmsgGatewayInfo1.getCreateDate());
				System.out.println(jmsgGatewayInfo);
				jmsgGatewayInfo.setGatewayName(jmsgGatewayInfo1.getGatewayName());
				jmsgGatewayInfo.setGatewayState(jmsgGatewayInfo1.getGatewayName());
				jmsgGatewayInfo.setType(jmsgGatewayInfo1.getType());
				jmsgGatewayInfo.setSpNumber(jmsgGatewayInfo1.getSpNumber());
				jmsgGatewayInfo.setHost(jmsgGatewayInfo1.getHost());
				jmsgGatewayInfo.setPort(jmsgGatewayInfo1.getPort());
				jmsgGatewayInfo.setLocalHost(jmsgGatewayInfo1.getLocalHost());
				jmsgGatewayInfo.setLocalPort(jmsgGatewayInfo1.getLocalPort());
				jmsgGatewayInfo.setSourceAddr(jmsgGatewayInfo1.getSourceAddr());
				jmsgGatewayInfo.setSharedSecret(jmsgGatewayInfo1.getSharedSecret());
				jmsgGatewayInfo.setVersion(jmsgGatewayInfo1.getVersion());
				jmsgGatewayInfo.setReadTimeout(jmsgGatewayInfo1.getReadTimeout());
				jmsgGatewayInfo.setReconnectInterval(jmsgGatewayInfo1.getReconnectInterval());
				jmsgGatewayInfo.setTransactionTimeout(jmsgGatewayInfo1.getTransactionTimeout());
				jmsgGatewayInfo.setHeartbeatInterval(jmsgGatewayInfo1.getHeartbeatInterval());
				jmsgGatewayInfo.setHeartbeatNoresponseout(jmsgGatewayInfo1.getHeartbeatNoresponseout());
				jmsgGatewayInfo.setDebug(jmsgGatewayInfo1.getDebug());
				jmsgGatewayInfo.setCorpId(jmsgGatewayInfo1.getCorpId());
				jmsgGatewayInfo.setStatus(jmsgGatewayInfo1.getStatus());
				jmsgGatewayInfo.setCreateTime(jmsgGatewayInfo1.getCreateTime());
				jmsgGatewayInfo.setCreateTimeQ(jmsgGatewayInfo1.getCreateTimeQ());
				jmsgGatewayInfo.setCreateTimeZ(jmsgGatewayInfo1.getCreateTimeZ());
				jmsgGatewayInfo.setModifieTime(jmsgGatewayInfo1.getModifieTime());
				jmsgGatewayInfo.setIsOutProv(jmsgGatewayInfo1.getIsOutProv());
				jmsgGatewayInfo.setExtClass(jmsgGatewayInfo1.getExtClass());
				jmsgGatewayInfo.setExtParam(jmsgGatewayInfo1.getExtParam());
				jmsgGatewayInfo.setAppHost(jmsgGatewayInfo1.getAppHost());
				jmsgGatewayInfo.setAppCode(jmsgGatewayInfo1.getAppCode());
				jmsgGatewayInfo.setReportGetFlag(jmsgGatewayInfo1.getReportGetFlag());
				jmsgGatewayInfo.setGatewaySign(jmsgGatewayInfo1.getGatewaySign());
				jmsgGatewayInfo.setSupportLongMsg(jmsgGatewayInfo1.getSupportLongMsg());
				jmsgGatewayInfo.setReadLimit(jmsgGatewayInfo1.getReadLimit());
				jmsgGatewayInfo.setWriteLimit(jmsgGatewayInfo1.getWriteLimit());
				jmsgGatewayInfo.setCustomFlag(jmsgGatewayInfo1.getCustomFlag());
				jmsgGatewayInfo.setBatchSize(jmsgGatewayInfo1.getBatchSize());
				jmsgGatewayInfo.setRemark(jmsgGatewayInfo1.getRemark());
				jmsgGatewayInfo.setParams(jmsgGatewayInfo1.getParams());
				jmsgGatewayInfo.setFeeUserType(jmsgGatewayInfo1.getFeeUserType());
				jmsgGatewayInfo.setFeeCode(jmsgGatewayInfo1.getFeeCode());
				jmsgGatewayInfo.setFeeTerminalId(jmsgGatewayInfo1.getFeeTerminalId());
				jmsgGatewayInfo.setFeeTerminalType(jmsgGatewayInfo1.getFeeTerminalType());
				jmsgGatewayInfo.setGwCorpId(jmsgGatewayInfo1.getGwCorpId());
				jmsgGatewayInfo.setGwServiceId(jmsgGatewayInfo1.getGwServiceId());
				jmsgGatewayInfo.setPhoneType(jmsgGatewayInfo1.getPhoneType());
				jmsgGatewayInfo.setRemark(jmsgGatewayInfo1.getRemark());
				jmsgGatewayInfo.setUpdateDate(jmsgGatewayInfo1.getUpdateDate());
				jmsgGatewayInfo.setDelFlag(jmsgGatewayInfo1.getDelFlag());
				jmsgGatewayInfo.setId(jmsgGatewayInfo1.getId());
//				com.sanerzone.common.support.persistence.Page<com.sanerzone.common.modules.smscenter.entity.JmsgGatewayInfo> page1 = jmsgGatewayInfo1.getPage();
//				jmsgGatewayInfo.setPage();
				jmsgGatewayInfo.setSqlMap(jmsgGatewayInfo1.getSqlMap());
				jmsgGatewayInfo.setIsNewRecord(jmsgGatewayInfo1.getIsNewRecord());
				return jmsgGatewayInfo;
			}
    	}
    }
	
	public static void del(String gatewayId){
		String key = CacheKeys.getCacheGatewayInfoKey(gatewayId);
		EhCacheUtils.remove(CacheKeys.GATEWAY_CACHE, key);
	}
	
	public static void put(String gatewayId,Object value){
		String key = CacheKeys.getCacheGatewayInfoKey(gatewayId);
		EhCacheUtils.put(CacheKeys.GATEWAY_CACHE, key,value);
	}
	
	
	//获取网关列表
	public static List<Dict> getGatewayList(){
		return jmsgGatewayInfoDao.findLabelValue();
	}
	
	//获取分组列表
	public static List<Dict> getGroupList(){
		return jmsgGroupDao.findLabelValue();
	}
	
	//获取分组列表 备注
	public static List<Dict> getGroupListBz(){
		return jmsgGroupDao.findLabelValueBz();
	}
}
