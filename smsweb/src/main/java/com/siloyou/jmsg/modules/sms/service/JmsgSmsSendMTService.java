/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.service.CrudService;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.utils.BlacklistUtils;
import com.siloyou.jmsg.common.utils.GatewayUtils;
import com.siloyou.jmsg.common.utils.MsgId;
import com.siloyou.jmsg.common.utils.PhoneUtils;
import com.siloyou.jmsg.common.utils.SignUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsTaskDao;
import com.siloyou.jmsg.modules.sms.entity.GatewayResult;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsTask;

/**
 * 短信接口发送Service
 * 
 * @author  zhangjie
 * @version  [版本号, 2016年11月11日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsSendMTService extends CrudService<JmsgSmsTaskDao, JmsgSmsTask> {
	@Autowired
    private SmsSendService smsSendService;
	
	@Transactional(readOnly = false)
    public void createSendDetail(String dataId,String taskId,String userId,String phones,String smsContent,String reportGaetewayId,String spNumber,String pushFlag,String topic){
	    SmsMtMessage mtMsg = new SmsMtMessage();
	    mtMsg.setId(new MsgId().toString());
//        mtMsg.setSendTime(System.currentTimeMillis());
        mtMsg.setTaskid(taskId);
        mtMsg.setUserid(userId);
        mtMsg.setPhone(phones);
        mtMsg.setMsgContent(smsContent);
        mtMsg.setUserReportGateWayID(reportGaetewayId);
        mtMsg.setUserReportNotify(pushFlag);
        mtMsg.setSpNumber(spNumber);
	    createSendDetail(mtMsg);
	}
	
	@Transactional(readOnly = false)
    public void createSendDetail(SmsMtMessage mtMsg)
    {
        User user = UserUtils.get(mtMsg.getUserid());//获取用户信息
        String sign = SignUtils.get(mtMsg.getMsgContent());//获取签名
        String[] phoneList = mtMsg.getPhone().split(",");
        for (String phone : phoneList)
        {
            String sendStatus = "P000";//待发
            
            //  1：校验 0： 不校验
            if (user.getSysBlacklistFlag() == 1 && BlacklistUtils.isExistSysBlackList(phone))
            {
                // 判断是否系统黑名单
                sendStatus = "F002";
            }
            else if (user.getUserBlacklistFlag() == 1 && BlacklistUtils.isExistUserBlackList(phone))
            {
                // 判断是否营销黑名单
                sendStatus = "F008";
            }
            
            //TODO 校验敏感词
            Map<String, String> phoneMap = PhoneUtils.get(phone);
            String phoneType = "";
            String cityCode = "";
            if (phoneMap == null || phoneMap.size() < 2)
            {
                sendStatus = "F0170";//号段匹配异常
            }
            else
            {
                phoneType = PhoneUtils.getPhoneType(phoneMap);//运营商
                cityCode = PhoneUtils.getCityCode(phoneMap);//省市代码
                
                if (StringUtils.isBlank(cityCode) || StringUtils.isBlank(phoneType))
                {
                    sendStatus = "F0170";//号段匹配异常
                }
                else
                {
                    GatewayResult gatewayResult =
                        gatewayMap(user.getSignFlag(), user.getGroupId(), phoneType, cityCode.substring(0, 2), sign, mtMsg.getUserid());
                    if (gatewayResult.isExists())
                    {
                        mtMsg.setGateWayID(gatewayResult.getGatewayId());//通道代码
                        String tdSpNumber = gatewayResult.getSpNumber();
                        if (StringUtils.isNotBlank(mtMsg.getSpNumber()))
                        {
                            if (mtMsg.getSpNumber().startsWith(tdSpNumber))
                            {
                                tdSpNumber = mtMsg.getSpNumber();
                            }
                        }
                        mtMsg.setSpNumber(tdSpNumber);//接入号
                    }
                    else
                    {
                        sendStatus = gatewayResult.getErrorCode();
                    }
                }
            }
            
            mtMsg.setPhoneType(phoneType);//运营商
            mtMsg.setCityCode(cityCode);//省市代码
            mtMsg.setPhone(phone);
            mtMsg.setSendStatus(sendStatus);
            //Map<String, String> map = smsSendService.sendHandler(mtMsg);
            smsSendService.sendHandler(mtMsg);//发送短信处理
        }
    }
	
	//获取通道代码  signFlag 1:验证签名 0:自定义签名 
	private GatewayResult gatewayMap(int signFlag,String groupId,String phoneType,String provinceId,String sign,String userId){
		
		GatewayResult entity = new GatewayResult();
		if(1 == signFlag){
			entity = GatewayUtils.getGateway(userId, groupId, phoneType, provinceId, sign);
		}else{
			entity = GatewayUtils.getGateway(groupId, phoneType, provinceId);
		}
		
		return entity;
		
	}
}