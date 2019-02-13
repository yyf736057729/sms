/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsDataDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsTaskDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsData;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsTask;

/**
 * 短信素材Service
 * @author zhukc
 * @version 2016-07-18
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsDataService extends CrudService<JmsgSmsDataDao, JmsgSmsData> {
	
	@Autowired
	private JmsgSmsTaskDao jmsgSmsTaskDao;

	public JmsgSmsData get(String id) {
		return super.get(id);
	}
	
	public List<JmsgSmsData> findList(JmsgSmsData jmsgSmsData) {
		return super.findList(jmsgSmsData);
	}
	
	public Page<JmsgSmsData> findPage(Page<JmsgSmsData> page, JmsgSmsData jmsgSmsData) {
		return super.findPage(page, jmsgSmsData);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgSmsData jmsgSmsData) {
		super.save(jmsgSmsData);
	}
	
	
	@Transactional(readOnly = false)
	public String save(String content,String type,String userId,String contentKey,String reviewStatus,Date reviewDatetime,String yxbz,String templateFlag){
		JmsgSmsData jmsgSmsData = new JmsgSmsData();
		jmsgSmsData.setContent(content);
		jmsgSmsData.setType(type);
		jmsgSmsData.setUser(new User(userId));
		jmsgSmsData.setContentKey(contentKey);
		jmsgSmsData.setReviewStatus(reviewStatus);
		jmsgSmsData.setReviewDatetime(reviewDatetime);
		jmsgSmsData.setYxbz(yxbz);
		jmsgSmsData.setTemplateFlag(templateFlag);
		dao.insert(jmsgSmsData);
		return jmsgSmsData.getId();
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgSmsData jmsgSmsData) {
		super.delete(jmsgSmsData);
	}
	
	public JmsgSmsData findJmsgSmsDataByContentKey(String userId,String contentKey){
		JmsgSmsData jmsgSmsData = new JmsgSmsData();
		jmsgSmsData.setUser(new User(userId));
		jmsgSmsData.setContentKey(contentKey);
		logger.info(xmlName + "===>>>findListNew");
		return dao.findJmsgSmsDataByContentKey(jmsgSmsData);
	}
	
	@Transactional(readOnly = false)
	public void updateReviewStatus(String[] ids,String status,String reviewContent){
		JmsgSmsData jmsgSmsData = null;
		boolean reviewFlag = "0".equals(status)?true:false;//审核不通过
		
		for (String id : ids) {
			jmsgSmsData = new JmsgSmsData();
			jmsgSmsData.setId(id);
			jmsgSmsData.setReviewStatus(status);
			if(reviewFlag){
				jmsgSmsData.setReviewContent(reviewContent);
			}
			jmsgSmsData.setReviewUserId(UserUtils.getUser().getId());
			dao.updateReviewStatus(jmsgSmsData);//审核
			
			if(!reviewFlag){//审核通过
				JmsgSmsTask jmsgSmsTask = new JmsgSmsTask();
				jmsgSmsTask.setDataId(id);
				jmsgSmsTask.setStatus("1");//待发
				jmsgSmsTaskDao.updateJmsgSmsTaskByDataId(jmsgSmsTask);
			}
		}
	}
	
}