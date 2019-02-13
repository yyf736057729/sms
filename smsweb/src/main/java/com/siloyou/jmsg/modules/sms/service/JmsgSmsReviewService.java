/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsReview;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsReviewDao;

/**
 * 短信审核Service
 * @author zhukc
 * @version 2016-07-16
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsReviewService extends CrudService<JmsgSmsReviewDao, JmsgSmsReview> {

	public JmsgSmsReview get(String id) {
		return super.get(id);
	}
	
	public List<JmsgSmsReview> findList(JmsgSmsReview jmsgSmsReview) {
		return super.findList(jmsgSmsReview);
	}
	
	public Page<JmsgSmsReview> findPage(Page<JmsgSmsReview> page, JmsgSmsReview jmsgSmsReview) {
		return super.findPage(page, jmsgSmsReview);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgSmsReview jmsgSmsReview) {
		super.save(jmsgSmsReview);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgSmsReview jmsgSmsReview) {
		super.delete(jmsgSmsReview);
	}
	
}