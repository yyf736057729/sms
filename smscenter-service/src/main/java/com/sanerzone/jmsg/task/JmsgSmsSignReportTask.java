package com.sanerzone.jmsg.task;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sanerzone.common.support.utils.DateUtils;
import com.sanerzone.jmsg.entity.JmsgSmsReportSign;
import com.sanerzone.jmsg.util.UserSignUtils;

public class JmsgSmsSignReportTask {
	Logger logger = LoggerFactory.getLogger(JmsgSmsSignReportTask.class);
	
	private SqlSessionFactory sqlSessionFactory;
	
	public void exec(){
		try{
			logger.info("用户签名统计任务执行中...");
			Map<String,Serializable> map = UserSignUtils.allMap();//获取所有数据
			if(map != null && map.size() > 0){
				SqlSession sqlSession = sqlSessionFactory.openSession();
				String day = DateFormatUtils.format(DateUtils.getDay(-1), "yyyyMMdd");
				JmsgSmsReportSign entity = null;
				for(Map.Entry<String, Serializable> entry : map.entrySet()){
					String key = entry.getKey();
					if(key.startsWith(day)){
						String[] array = key.split("\\|\\|");
						if(array != null && array.length == 3){
							entity = new JmsgSmsReportSign();
							entity.setDay(day);
							entity.setUserId(array[1]);
							entity.setSmsSign(array[2]);
							entity.setSendCount((int)entry.getValue());
							try{
								sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsReportSignDao.insert", entity);
							}catch(Exception e1){
								
							}
							try{
								sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsReportSignDao.insertUsedSign", entity);
							}catch(Exception e2){
								
							}
							UserSignUtils.del(key);
						}
					}
				}
				if (sqlSession != null){
					sqlSession.close();
				}
			}
		}catch(Exception e){
				//logger.info("重复签名："+);
		}
		logger.info("用户签名统计任务执行结束");

	}
	

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}
}
