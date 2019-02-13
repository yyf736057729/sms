package com.sanerzone.jmsg.task;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.sanerzone.common.modules.TableNameUtil;
import com.sanerzone.jmsg.dao.JmsgSmsSubmitSyncDao;
import com.sanerzone.jmsg.entity.JmsgSmsSubmitSync;

/**
 * 状态报告同步任务
 * @author zhangjie
 */
public class JmsgSmsReportSyncTask {
	Logger logger = LoggerFactory.getLogger(JmsgSmsReportSyncTask.class);
	
	private JmsgSmsSubmitSyncDao jmsgSmsSubmitSyncDao;
	
    private SqlSessionFactory sqlSessionFactory;
	
	/**
	 * 延时一分钟执行一次
	 */
	public void execSync()
	{
		logger.info("网状状态回执，同步任务执行中...");
		List<JmsgSmsSubmitSync> list = jmsgSmsSubmitSyncDao.findList(new JmsgSmsSubmitSync());
		if (null != list)
		{
			SqlSession sqlSession = sqlSessionFactory.openSession();
		
			Map<String,String> map = Maps.newHashMap();
		
			for (JmsgSmsSubmitSync para : list)
			{
				map = Maps.newHashMap();
                map.put("id", para.getBizid());
                map.put("reportStatus", para.getResult());
                map.put("tableName", "jmsg_sms_send_" + TableNameUtil.getTableIndex(para.getBizid()));
                int update = sqlSession.update("com.sanerzone.jmsg.dao.JmsgSmsSendDao.batchUpdateReprotStatus", map);
                
                if (update == 1)
                {
                	jmsgSmsSubmitSyncDao.delete(para);
                }
			}
			
			if (sqlSession != null)
			{
				sqlSession.close();
			}
		}
	}

	public JmsgSmsSubmitSyncDao getJmsgSmsSubmitSyncDao() {
		return jmsgSmsSubmitSyncDao;
	}

	public void setJmsgSmsSubmitSyncDao(JmsgSmsSubmitSyncDao jmsgSmsSubmitSyncDao) {
		this.jmsgSmsSubmitSyncDao = jmsgSmsSubmitSyncDao;
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

}
