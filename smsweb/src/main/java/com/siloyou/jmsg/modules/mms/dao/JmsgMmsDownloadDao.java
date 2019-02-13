/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.dao;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsDownload;

/**
 * 彩信下载DAO接口
 * @author zj
 * @version 2016-12-19
 */
@MyBatisDao
public interface JmsgMmsDownloadDao extends CrudDao<JmsgMmsDownload> {
	
}