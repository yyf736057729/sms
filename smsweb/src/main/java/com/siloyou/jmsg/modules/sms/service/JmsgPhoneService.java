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
import com.siloyou.jmsg.common.utils.BlacklistUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgPhoneDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhone;

/**
 * 动态黑名单Service
 * @author zj
 * @version 2016-09-26
 */
@Service
@Transactional(readOnly = true)
public class JmsgPhoneService extends CrudService<JmsgPhoneDao, JmsgPhone> {

	@DubboReference
	private PhoneConfigInterface phoneConfig;
	
	public JmsgPhone get(String id) {
		return super.get(id);
	}
	
	public List<JmsgPhone> findList(JmsgPhone jmsgPhone) {
		return super.findList(jmsgPhone);
	}
	
	public Page<JmsgPhone> findPage(Page<JmsgPhone> page, JmsgPhone jmsgPhone) {
		return super.findPage(page, jmsgPhone);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgPhone jmsgPhone) {
		String phones = jmsgPhone.getPhone();
		//获取文本框中的发送号码
        if(StringUtils.isNotBlank(phones)){
        	String remarks = jmsgPhone.getRemarks();
            String[] tmp = phones.split("\r\n");
            JmsgPhone param;
            if(tmp != null && tmp.length > 0){
	            for (String phone : tmp){
	            	param = new JmsgPhone();
	            	param.setPhone(phone);
	            	param.setScope(jmsgPhone.getScope());
	            	param.setType(jmsgPhone.getType());
	            	param.setRemarks(remarks);
	            	deleteByPhone(phone);
	            	BlacklistUtils.put(phone, 1, 1);
	            	super.save(param);
	            }
	    		phoneConfig.configBlacklist(3,"",1,tmp);
            }
        }
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgPhone jmsgPhone) {
		super.delete(jmsgPhone);
		BlacklistUtils.del(jmsgPhone.getPhone(),1);
		phoneConfig.configBlacklist(4,jmsgPhone.getPhone(),1,null);
	}
	
	@Transactional(readOnly = false)
	public void deleteByPhone(String phone){
		dao.deleteByPhone(phone);
		BlacklistUtils.del(phone,1);
		phoneConfig.configBlacklist(4,phone,1,null);
	}
	
	@Transactional(readOnly = false)
	public void cacheBlackList(int type,String phone,int value,String[] phones) {
		phoneConfig.configBlacklist(type,phone,value,phones);
	}
	
}