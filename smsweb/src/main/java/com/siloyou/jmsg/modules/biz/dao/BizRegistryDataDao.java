/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.biz.dao;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.biz.entity.BizRegistryData;

/**
 * 活动登录短信发送记录DAO接口
 * @author huangjie
 * @version 2017-07-20
 */
@MyBatisDao
public interface BizRegistryDataDao extends CrudDao<BizRegistryData> {

public BizRegistryData getBizRegistryDataByphone(BizRegistryData bizRegistryData);
	

}