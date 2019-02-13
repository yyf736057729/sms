package com.siloyou.jmsg.modules.mms.task;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.sanerzone.common.support.config.Global;
import com.siloyou.jmsg.common.utils.BlacklistUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgPhoneBlacklistDao;

//定时任务
@Service
@Lazy(false)
public class InitBlackPhoneTask {
	
	@Autowired
	private JmsgPhoneBlacklistDao jmsgPhoneBlacklistDao;
	
	//初始化黑名单
	@PostConstruct
	public void init(){
		String initBlackList = Global.getConfig("system.blacklist.init");
		if(StringUtils.equals(initBlackList, "1")) {
			initBlacklist();//初始化黑名单
		}
	}
	
	public void initBlacklist(){
		long s = System.currentTimeMillis();
		System.out.println("init初始化黑名单开始");
		System.out.println("查询耗时:"+(System.currentTimeMillis()-s)/1000);
		int pageNo =0;
		int pageSize =1000000;
		boolean flag = true;
		int index = 0;
		while(flag){
			pageNo = index*pageSize;
			List<String> list = jmsgPhoneBlacklistDao.findAllPhone(pageNo);
			if(list != null && list.size() >0){
				BlacklistUtils.put(list,0,0);
				index++;
			}else{
				flag=false;
				break;
			}
			if(list.size() <pageSize){
				flag=false;
				break;
			}
		}
		
		pageNo =0;
		pageSize =1000000;
		flag = true;
		index = 0;
		while(flag){
			pageNo = index*pageSize;
			List<String> tdList = jmsgPhoneBlacklistDao.findTDPhone(pageNo);//退订黑名单
			if(tdList != null && tdList.size() >0){
				BlacklistUtils.put(tdList,1,1);
				index++;
			}else{
				flag=false;
				break;
			}
			if(tdList.size() <pageSize){
				flag=false;
				break;
			}
		}
		
		System.out.println("end初始化黑名单结束,耗时:"+(System.currentTimeMillis()-s)/1000);
		
	}
}
