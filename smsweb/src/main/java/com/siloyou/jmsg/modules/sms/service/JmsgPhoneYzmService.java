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
import com.siloyou.jmsg.modules.sms.dao.JmsgPhoneYzmDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneYzm;

/**
 * 验证码黑名单Service
 * @author zhukc
 * @version 2017-12-05
 */
@Service
@Transactional(readOnly = true)
public class JmsgPhoneYzmService extends CrudService<JmsgPhoneYzmDao, JmsgPhoneYzm> {

	@DubboReference
	private PhoneConfigInterface phoneConfig;
	
	public JmsgPhoneYzm get(String id) {
		return super.get(id);
	}
	
	public List<JmsgPhoneYzm> findList(JmsgPhoneYzm jmsgPhoneYzm) {
		return super.findList(jmsgPhoneYzm);
	}
	
	public Page<JmsgPhoneYzm> findPage(Page<JmsgPhoneYzm> page, JmsgPhoneYzm jmsgPhoneYzm) {
		return super.findPage(page, jmsgPhoneYzm);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgPhoneYzm jmsgPhoneYzm) {
		String phones = jmsgPhoneYzm.getPhone();
		//获取文本框中的发送号码
        if(StringUtils.isNotBlank(phones)){
        	String remarks = jmsgPhoneYzm.getRemarks();
            String[] tmp = phones.split("\r\n");
            JmsgPhoneYzm param;
            if(tmp != null && tmp.length > 0){
	            for (String phone : tmp){
	            	param = new JmsgPhoneYzm();
	            	param.setPhone(phone);
	            	//param.setType(jmsgPhoneMarket.getType());
	            	param.setRemarks(remarks);
	            	deleteByPhone(phone);
	            	super.save(param);
	            }
	            
	            phoneConfig.configBlacklist(7, "", 0, tmp);
            }
        }
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgPhoneYzm jmsgPhoneYzm) {
		super.delete(jmsgPhoneYzm);
		phoneConfig.configBlacklist(8,jmsgPhoneYzm.getPhone(),0,null);
	}
	
	@Transactional(readOnly = false)
	public void deleteByPhone(String phone) {
		dao.deleteByphone(phone);
		phoneConfig.configBlacklist(8,phone,0,null);
	}
	
}