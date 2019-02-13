package com.sanerzone;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sanerzone.common.support.utils.JedisClusterUtils;
import com.siloyou.jmsg.common.message.SmsMoMessage;

/**
 * Hello world!
 *
 */
public class AppT 
{
    private static ClassPathXmlApplicationContext context;

    private static Cache<String, Map<String,String>> cache =CacheBuilder.newBuilder().refreshAfterWrite(1, TimeUnit.SECONDS).build(new CacheLoader<String, Map<String,String>>() {  
        public Map<String,String> load(String key) throws InterruptedException {  
        	System.out.println("load");
             return Maps.newHashMap(); 
       }


        @Override  
        public ListenableFuture<Map<String,String>> reload(String key, Map<String,String> oldValue)  
                   throws Exception {  
        	System.out.println("reload");
        	 Map<String,String> map = new HashMap<String,String>();
             return Futures.immediateFuture(map);
       }  
  }  );
   static ConcurrentMap<String, Map<String,String>> map = cache.asMap();
   
   
   public static String encodePhoneNumber(String number)
   {
       int encnum = Integer.parseInt(number.substring(2));
       int prefix = (number.charAt(1) - '2') /2;   // '3'=>0, '5'=>1, '[67]'=>2, '8'=>3
       encnum |= (prefix <<= 30);
       final byte[] raw = new byte[4];
       raw[0] = (byte)(encnum >>> 24);
       raw[1] = (byte)(encnum >>> 16);
       raw[2] = (byte)(encnum >>> 8);
       raw[3] = (byte) encnum;
       return Base64.encodeBase64URLSafeString(raw);
   }
    
	public static void main( String[] args )
    {
		Logger logger = LoggerFactory.getLogger(AppT.class);

		String startType = (args == null || args.length == 0)?"1": args[0];
    	context = new ClassPathXmlApplicationContext("classpath*:spring-*.xml");

    	SmsMoMessage smsMoMessage = new SmsMoMessage();
    	smsMoMessage.setDestTermID("12312312");
    	smsMoMessage.setSrcTermID("11111");
    	smsMoMessage.setMsgContent("\\xF0\\x9F\\x92\\x94");
    	smsMoMessage.setMsgid("11111");
    	smsMoMessage.setGateWayID("11111");
    	smsMoMessage.setUuid("11111");

    	SqlSession sqlSession = context.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
    	// 插入数据库
		sqlSession
				.insert("com.sanerzone.jmsg.dao.JmsgSmsDeliverDao.batchInsert",
						smsMoMessage);
		sqlSession.commit();

		System.out.println(encodePhoneNumber("13666672477"));
		
    }
}
