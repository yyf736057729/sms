/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.dao.JmsgUserSignDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgUserSign;

/**
 * 用户签名Service
 * @author zj
 * @version 2016-09-08
 */
@Service
@Transactional(readOnly = true)
public class JmsgUserSignService extends CrudService<JmsgUserSignDao, JmsgUserSign> {

	public JmsgUserSign get(String id) {
		return super.get(id);
	}
	
	public List<JmsgUserSign> findList(JmsgUserSign jmsgUserSign) {
		return super.findList(jmsgUserSign);
	}
	
	public Page<JmsgUserSign> findPage(Page<JmsgUserSign> page, JmsgUserSign jmsgUserSign) {
		return super.findPage(page, jmsgUserSign);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgUserSign jmsgUserSign) {
	    if (StringUtils.isNotBlank(jmsgUserSign.getSign()))
	    {
	        JmsgUserSign newJmsgUserSign = null;
	        String[] signs = jmsgUserSign.getSign().split("\n|\r\n|\r");
	        
	        for (String str : signs)
	        {
	            newJmsgUserSign = new JmsgUserSign();
	            newJmsgUserSign.setUser(jmsgUserSign.getUser());
	            newJmsgUserSign.setCreateUserId(jmsgUserSign.getCreateUserId());
	            newJmsgUserSign.setCreatetime(jmsgUserSign.getCreatetime());
	            newJmsgUserSign.setSign(str);
	            
	             super.save(newJmsgUserSign);
	        }
	    }
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgUserSign jmsgUserSign) {
		super.delete(jmsgUserSign);
	}
	
}