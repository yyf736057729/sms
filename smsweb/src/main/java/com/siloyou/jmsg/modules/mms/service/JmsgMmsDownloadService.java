/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsDownloadDao;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsDownload;

/**
 * 彩信下载Service
 * @author zj
 * @version 2016-12-19
 */
@Service
@Transactional(readOnly = true)
public class JmsgMmsDownloadService extends CrudService<JmsgMmsDownloadDao, JmsgMmsDownload> {

	public JmsgMmsDownload get(String id) {
		return super.get(id);
	}
	
	public List<JmsgMmsDownload> findList(JmsgMmsDownload jmsgMmsDownload) {
		return super.findList(jmsgMmsDownload);
	}
	
	public Page<JmsgMmsDownload> findPage(Page<JmsgMmsDownload> page, JmsgMmsDownload jmsgMmsDownload) {
		return super.findPage(page, jmsgMmsDownload);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgMmsDownload jmsgMmsDownload) {
		super.save(jmsgMmsDownload);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgMmsDownload jmsgMmsDownload) {
		super.delete(jmsgMmsDownload);
	}
	
}