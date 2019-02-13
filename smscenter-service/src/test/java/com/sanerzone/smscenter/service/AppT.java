package com.sanerzone.smscenter.service;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.rocketmq.client.QueryResult;
import com.alibaba.rocketmq.client.admin.MQAdminExtInner;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.impl.MQAdminImpl;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.sanerzone.common.support.rocketmq.MQProducerFactory;
import com.sanerzone.smscenter.utils.MQUtils;
import com.siloyou.jmsg.common.message.SmsMoMessage;

/**
 * Hello world!
 *
 */
public class AppT 
{
    private static ClassPathXmlApplicationContext context;

	public static void main( String[] args )
    {
		Logger logger = LoggerFactory.getLogger(AppT.class);
		
		String startType = (args == null || args.length == 0)?"1": args[0];
    	context = new ClassPathXmlApplicationContext("classpath*:spring-*.xml");
    	
    	SmsMoMessage smsMoMessage = new SmsMoMessage();
    	smsMoMessage.setDestTermID("12312312");
    	smsMoMessage.setSrcTermID("11111");
    	smsMoMessage.setMsgContent("哦哦，没事，我问了我们班好几个人都说没登，我实在想不着是谁登的，但等着用，我同学就给我说可以在自助服务系统上把在线的下线😁");
    	smsMoMessage.setMsgid("11111");
    	smsMoMessage.setGateWayID("11111");
    	smsMoMessage.setUuid("11111");
    	
//    	try {
//			MQUtils mq = new MQUtils();
//    		QueryResult result = MQProducerFactory.getMQProducer("", "").queryMessage("SMSMOV1", "", 0, 0, 0);
//    		result.getMessageList().get(0).getBody();
//		} catch (MQClientException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
    	SqlSession sqlSession = context.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
    	// 插入数据库
		sqlSession
				.insert("com.sanerzone.jmsg.dao.JmsgSmsDeliverDao.batchInsert",
						smsMoMessage);
		sqlSession.commit();
		System.out.println("========");
    }
}
