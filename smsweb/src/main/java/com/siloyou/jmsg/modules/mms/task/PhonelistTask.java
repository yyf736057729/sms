package com.siloyou.jmsg.modules.mms.task;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.siloyou.jmsg.common.utils.PhoneUtils;
import com.siloyou.jmsg.common.utils.WhitelistUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgPhoneWhitelistDao;

//定时任务
@Service
@Lazy(false)
public class PhonelistTask {
	
	
	@Autowired
	private JmsgPhoneWhitelistDao jmsgPhoneWhitelistDao;
	
	//初始化黑名单
	@PostConstruct
	public void init(){
		initWhitelist();//初始化白名单
		PhoneUtils.initPhoneType();//初始化号段
	}
	
	
	//初始化白名单
	public void initWhitelist(){ 
		long s = System.currentTimeMillis();
		System.out.println("init初始化白名单开始");
		List<String> list = jmsgPhoneWhitelistDao.findAllPhone();
		System.out.println("查询耗时:"+(System.currentTimeMillis()-s)/1000);
		WhitelistUtils.initWhitelist(list);
		System.out.println("end初始化名单结束，共："+list.size()+"个,耗时:"+(System.currentTimeMillis()-s)/1000);
	}
	
	
	/**
	//每1分钟执行一次 黑名单
	@Scheduled(cron = "0 0/1 * * * ?")
	public void execBlacklist(){
		Map<String,String> map = JedisClusterUtils.getJedisInstance().hgetAll(BlacklistUtils.BLACKLIST_CACHE);
		if(map != null && map.size() >0){
			for (String phone : map.keySet()) {
				  String value = map.get(phone);
				  String[] array = value.split(",");
				  String type = "";
				  String userId = "1";
				  type = array[0];
				  if(array.length == 2){
					  userId = array[1];
				  }
				  if("1".equals(type)){//新增号码
					  if(!BlacklistUtils.isExist(phone)){
						  JmsgPhoneBlacklist jmsgPhoneBlacklist = new JmsgPhoneBlacklist();
						  jmsgPhoneBlacklist.setPhone(phone);
						  jmsgPhoneBlacklist.setScope("0");
						  jmsgPhoneBlacklist.setCreateBy(new User(userId));
						  jmsgPhoneBlacklistDao.insert(jmsgPhoneBlacklist);
						  BlacklistUtils.put(phone,0);
					  }
				  }else{//删除号码
					  jmsgPhoneBlacklistDao.deleteByPhone(phone);
					  BlacklistUtils.del(phone);
				  }
				  JedisClusterUtils.getJedisInstance().hdel(BlacklistUtils.BLACKLIST_CACHE, phone);
			}
		}
	}
	
	//每1分钟执行一次 白名单
	@Scheduled(cron = "0 0/1 * * * ?")
	public void execWhitelist(){
		Map<String,String> map = JedisClusterUtils.getJedisInstance().hgetAll(WhitelistUtils.WHITELIST_CACHE);
		if(map != null && map.size() >0){
			for (String phone : map.keySet()) {
				  String value = map.get(phone);
				  String[] array = value.split(",");
				  String type = "";
				  String userId = "1";
				  type = array[0];
				  if(array.length == 2){
					  userId = array[1];
				  }
				if("1".equals(type)){//新增号码
					if(!WhitelistUtils.isExist(phone)){
						JmsgPhoneBlacklist jmsgPhoneWhitelist = new JmsgPhoneBlacklist();
						jmsgPhoneWhitelist.setPhone(phone);
						jmsgPhoneWhitelist.setScope("0");
						jmsgPhoneWhitelist.setCreateBy(new User(userId));
						jmsgPhoneWhitelistDao.insert(jmsgPhoneWhitelist);
						WhitelistUtils.put(phone);
					}
				}else{//删除号码
					jmsgPhoneWhitelistDao.deleteByPhone(phone);
					WhitelistUtils.del(phone);
				}
				JedisClusterUtils.getJedisInstance().hdel(WhitelistUtils.WHITELIST_CACHE, phone);
			}
		}
	}**/

}
