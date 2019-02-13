package com.sanerzone.jmsg.task;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.sanerzone.common.modules.TableNameUtil;
import com.sanerzone.jmsg.dao.JmsgSmsReportSyncDao;
import com.sanerzone.jmsg.entity.JmsgSmsReportSync;

/**
 * 网关提交同步
 * @author zhangjie
 */
public class JmsgSmsSubmitSyncTask {
	Logger logger = LoggerFactory.getLogger(JmsgSmsSubmitSyncTask.class);
	
	private JmsgSmsReportSyncDao jmsgSmsReportSyncDao;
	
	SqlSessionFactory sqlSessionFactory;

	/**
	 * 延时一分钟执行一次
	 */
	public void execSync()
	{
		logger.info("网状提交状态，同步任务执行中...");
		List<JmsgSmsReportSync> list = jmsgSmsReportSyncDao.findList(new JmsgSmsReportSync());
		
		if (null != list)
		{
			SqlSession sqlSession = sqlSessionFactory.openSession();
			
			Map<String,String> map = null;
			
			for (JmsgSmsReportSync para : list)
			{
				map = Maps.newHashMap();
				
				map.put("id", para.getBizid());
				if("DELIVRD".equals(para.getStat())){//成功
					map.put("reportStatus", "T100");
				}else{
					map.put("reportStatus", "F2" + para.getStat());//失败
				}
				map.put("tableName", "jmsg_sms_send_" + TableNameUtil.getTableIndex(para.getBizid()));
				int num = sqlSession.update("com.sanerzone.jmsg.dao.JmsgSmsSendDao.batchUpdateReprotStatus", map);
				
				if (num == 1)
				{
					jmsgSmsReportSyncDao.delete(para);
				}
			}
			
			if (sqlSession != null)
			{
				sqlSession.close();
			}
		}
	}

	public JmsgSmsReportSyncDao getJmsgSmsReportSyncDao() {
		return jmsgSmsReportSyncDao;
	}

	public void setJmsgSmsReportSyncDao(JmsgSmsReportSyncDao jmsgSmsReportSyncDao) {
		this.jmsgSmsReportSyncDao = jmsgSmsReportSyncDao;
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}
}
