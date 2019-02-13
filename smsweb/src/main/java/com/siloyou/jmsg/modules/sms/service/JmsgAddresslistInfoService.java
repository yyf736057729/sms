/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgAddresslistInfoDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgAddresslistGroup;
import com.siloyou.jmsg.modules.sms.entity.JmsgAddresslistInfo;

/**
 * 联系人列表Service
 * @author zhukc
 * @version 2017-04-01
 */
@Service
@Transactional(readOnly = true)
public class JmsgAddresslistInfoService extends CrudService<JmsgAddresslistInfoDao, JmsgAddresslistInfo> {
	
	private static final int BATCH_COMMIT_MAX_COUNT = 500;//批量提交默认值

	public JmsgAddresslistInfo get(String id) {
		return super.get(id);
	}
	
	public List<JmsgAddresslistInfo> findList(JmsgAddresslistInfo jmsgAddresslistInfo) {
		return super.findList(jmsgAddresslistInfo);
	}
	
	public Page<JmsgAddresslistInfo> findPage(Page<JmsgAddresslistInfo> page, JmsgAddresslistInfo jmsgAddresslistInfo) {
		return super.findPage(page, jmsgAddresslistInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgAddresslistInfo jmsgAddresslistInfo) {
		super.save(jmsgAddresslistInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgAddresslistInfo jmsgAddresslistInfo) {
		super.delete(jmsgAddresslistInfo);
	}
	
	@Transactional(readOnly = false)
	public void deleteByGroup(String groupId){
		dao.deleteByGroupId(groupId);
	}
	
	@Transactional(readOnly = false)
	public void batchSave(List<JmsgAddresslistInfo> list,String groupId){
		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
		
		try{
			if(list != null && list.size() >0){
				int index = 0;
				User user = UserUtils.getUser();
				JmsgAddresslistGroup group = new JmsgAddresslistGroup(groupId);
				for (JmsgAddresslistInfo jmsgAddresslistInfo : list) {
					if(StringUtils.isBlank(jmsgAddresslistInfo.getPhone())){
						continue;
					}
					index++;
					jmsgAddresslistInfo.setGroup(group);
					jmsgAddresslistInfo.setCreateBy(user);
					sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgAddresslistInfoDao.insert", jmsgAddresslistInfo);//批量提交
					if(index % BATCH_COMMIT_MAX_COUNT == 0){
						sqlSession.commit();
					}
				}
				sqlSession.commit();
			}
		}finally{
			if(sqlSession != null)sqlSession.close();
		}
	}
	
}