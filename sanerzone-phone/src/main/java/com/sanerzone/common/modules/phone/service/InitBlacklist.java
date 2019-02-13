package com.sanerzone.common.modules.phone.service;

import java.util.List;

import com.google.common.collect.Lists;
import com.sanerzone.common.modules.phone.dao.JmsgPhoneBlacklistDao;
import com.sanerzone.common.modules.phone.utils.BlacklistUtils;
import com.sanerzone.common.support.utils.SpringContextHolder;

/**
 * 初始化黑名单 
 * 0:系统黑名单  1:群发黑名单  2:营销黑名单 3:验证码黑名单
 * @author ZHUKC
 *
 */
public class InitBlacklist {
	
	private JmsgPhoneBlacklistDao jmsgPhoneBlacklistDao = SpringContextHolder.getBean(JmsgPhoneBlacklistDao.class);
	
	public void initBlacklist(){
		init(0);//系统黑名单
		init(1);//群发黑名单
		init(2);//营销黑名单
		init(3);//验证码黑名单
	}
	
	//type 0:系统黑名单  1:群发黑名单  2:营销黑名单 3:验证码黑名单
	private void init(int type){
		//系统黑名单
		int pageNo =0;
		int pageSize =1000000;
		boolean flag = true;
		int index = 0;
		while(flag){
			pageNo = index*pageSize;
			List<String> list = Lists.newArrayList();
			if(0 == type){
				list.addAll(jmsgPhoneBlacklistDao.findAllPhone(pageNo));
			}else if(1 == type){
				list.addAll(jmsgPhoneBlacklistDao.findTDPhone(pageNo));
			}else if(2 == type){
				list.addAll(jmsgPhoneBlacklistDao.findMarketPhone(pageNo));
			}else if(3 == type){
				list.addAll(jmsgPhoneBlacklistDao.findYzmPhone(pageNo));
			}
			if(list != null && list.size() >0){
				BlacklistUtils.put(list,type,0);
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
	}
}
