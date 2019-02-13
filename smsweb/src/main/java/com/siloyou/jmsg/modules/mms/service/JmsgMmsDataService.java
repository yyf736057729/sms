/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsData;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsDataDao;

/**
 * 彩信素材Service
 * @author zhukc
 * @version 2016-05-20
 */
@Service
@Transactional(readOnly = true)
public class JmsgMmsDataService extends CrudService<JmsgMmsDataDao, JmsgMmsData> {

	public JmsgMmsData get(String id) {
		return super.get(id);
	}
	
	public List<JmsgMmsData> findList(JmsgMmsData jmsgMmsData) {
		return super.findList(jmsgMmsData);
	}
	
	public Page<JmsgMmsData> findPage(Page<JmsgMmsData> page, JmsgMmsData jmsgMmsData) {
		return super.findPage(page, jmsgMmsData);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgMmsData jmsgMmsData) {
		super.save(jmsgMmsData);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgMmsData jmsgMmsData) {
		super.delete(jmsgMmsData);
	}
	
	@Transactional(readOnly = false)
	public void updateCheckStatus(String[] ids,String status,String checkContent){
		JmsgMmsData jmsgMmsData = null;
		for (String id : ids) {
			jmsgMmsData = new JmsgMmsData();
			jmsgMmsData.setId(id);
			jmsgMmsData.setCheckStatus(status);
			if("0".equals(status)){
				jmsgMmsData.setCheckContent(checkContent);
			}
			jmsgMmsData.setCheckUserId(UserUtils.getUser().getId());
			dao.updateCheckStatus(jmsgMmsData);
		}
	}
	
	public JmsgMmsData findCheckInfo(String id){
		return dao.findCheckInfo(id);
	}
	
	public long checkCount(){
		return dao.checkCount();
	}
	
}