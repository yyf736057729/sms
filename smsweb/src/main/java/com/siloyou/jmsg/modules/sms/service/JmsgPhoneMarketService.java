/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.support.dubbo.spring.annotation.DubboReference;
import com.sanerzone.smscenter.config.PhoneConfigInterface;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgPhoneMarketDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneMarket;

/**
 * 营销黑名单Service
 * @author zhukc
 * @version 2017-12-05
 */
@Service
@Transactional(readOnly = true)
public class JmsgPhoneMarketService extends CrudService<JmsgPhoneMarketDao, JmsgPhoneMarket> {

	@DubboReference
	private PhoneConfigInterface phoneConfig;
	
	public JmsgPhoneMarket get(String id) {
		return super.get(id);
	}
	
	public List<JmsgPhoneMarket> findList(JmsgPhoneMarket jmsgPhoneMarket) {
		return super.findList(jmsgPhoneMarket);
	}
	
	public Page<JmsgPhoneMarket> findPage(Page<JmsgPhoneMarket> page, JmsgPhoneMarket jmsgPhoneMarket) {
		return super.findPage(page, jmsgPhoneMarket);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgPhoneMarket jmsgPhoneMarket) {
		String phones = jmsgPhoneMarket.getPhone();
		//获取文本框中的发送号码
        if(StringUtils.isNotBlank(phones)){
        	String remarks = jmsgPhoneMarket.getRemarks();
            String[] tmp = phones.split("\r\n");
            JmsgPhoneMarket param;
            if(tmp != null && tmp.length > 0){
	            for (String phone : tmp){
	            	param = new JmsgPhoneMarket();
	            	param.setPhone(phone);
	            	//param.setType(jmsgPhoneMarket.getType());
	            	param.setRemarks(remarks);
	            	deleteByPhone(phone);
	            	super.save(param);
	            }
	            phoneConfig.configBlacklist(5,"",0,tmp);
            }
        }
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgPhoneMarket jmsgPhoneMarket) {
		super.delete(jmsgPhoneMarket);
		phoneConfig.configBlacklist(6,jmsgPhoneMarket.getPhone(),0,null);
	}
	
	@Transactional(readOnly = false)
	public void deleteByPhone(String phone){
		dao.deleteByPhone(phone);
		phoneConfig.configBlacklist(6,phone,0,null);
	}
	
}