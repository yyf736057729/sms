package com.siloyou.jmsg.modules.api.service;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class SmsApiService {
	public final int defaultDoFlushSize = 100;
	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	public void insertFlowBatch(List orderList, String sqlmap, boolean insertFlag ) throws Exception{
		SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(this.sqlSessionFactory, ExecutorType.BATCH , null);
		int i = 0;
		for(Object item: orderList){
			if(insertFlag){
				sqlSessionTemplate.insert(sqlmap, item);
			}else{
				sqlSessionTemplate.update(sqlmap, item);
			}
			if((i + 1) % defaultDoFlushSize == 0) {
				sqlSessionTemplate.flushStatements();
			}
		}
	}
}
