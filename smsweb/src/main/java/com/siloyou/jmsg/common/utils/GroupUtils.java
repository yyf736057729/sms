package com.siloyou.jmsg.common.utils;

import java.util.List;

import com.siloyou.core.common.utils.EhCacheUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgGroupDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGroup;


//分组工具类
public class GroupUtils {
	
	private static JmsgGroupDao jmsgGroupDao = SpringContextHolder.getBean(JmsgGroupDao.class);
	
	//初始化分组信息
	public static void initGroup(){
		JmsgGroup entity = new JmsgGroup();
		//entity.setStatus("1");
		List<JmsgGroup> list = jmsgGroupDao.findList(entity);
		for (JmsgGroup jmsgGroup : list) {
			//put(jmsgGroup.getId());
			put(jmsgGroup);
		}
	}
	
	//缓存分组
	/*public static void put(String groupId){
		put(groupId,"1");
	}*/
	
	//缓存分组
	/*public static void put(String groupId,String value){
		String key = CacheKeys.getCacheGroupKey(groupId);
		EhCacheUtils.put(CacheKeys.GATEWAY_CACHE, key, value);
	}*/
	
	//缓存分组
    public static void put(JmsgGroup jmsgGroup){
        String key = CacheKeys.getCacheGroupKey(jmsgGroup.getId());
        EhCacheUtils.put(CacheKeys.GATEWAY_CACHE, key, jmsgGroup);
    }
	
	//删除分组
	public static void del(String groupId){
		String key = CacheKeys.getCacheGroupKey(groupId);
		EhCacheUtils.remove(CacheKeys.GATEWAY_CACHE, key);
	}
	
	//判断分组是否存在
	public static boolean isExists(String groupId){
	    JmsgGroup jmsgGroup = getJmsgGroup(groupId);
	    if (null != jmsgGroup)
        {
            if ("1".equals(jmsgGroup.getStatus()))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
	    else
	    {
	        return false;
	    }
	    
		/*String value  = get(groupId);
		if("1".equals(value))return true;
		return false;*/
	}
	
	
	//获取分组信息 缓存中不存在，则获取数据库并缓存
	public static String get(String groupId){
	    JmsgGroup jmsgGroup = getJmsgGroup(groupId);
	    
	    return jmsgGroup.getStatus() == null ? "0" : jmsgGroup.getStatus();
	    
		/*String key = CacheKeys.getCacheGroupKey(groupId);
		Object obj = EhCacheUtils.get(CacheKeys.GATEWAY_CACHE, key);
		if(obj == null){
			JmsgGroup entity = jmsgGroupDao.get(groupId);
			if(entity == null){
				EhCacheUtils.put(CacheKeys.GATEWAY_CACHE, key, "0");
				return "0";
			}else{
				put(groupId,entity.getStatus());
				return entity.getStatus();
			}
		}
		return String.valueOf(obj);*/
	}
	
	//获取分组信息 缓存中不存在，则获取数据库并缓存
    public static JmsgGroup getJmsgGroup(String groupId){
        String key = CacheKeys.getCacheGroupKey(groupId);
        Object obj = EhCacheUtils.get(CacheKeys.GATEWAY_CACHE, key);
        if(obj == null){
            JmsgGroup entity = jmsgGroupDao.get(groupId);
            if(entity == null){
                entity = new JmsgGroup();
                EhCacheUtils.put(CacheKeys.GATEWAY_CACHE, key, entity);
                return entity;
            }else{
                put(entity);
                return entity;
            }
        }
        if(obj instanceof  JmsgGroup){
            return (JmsgGroup)obj;
        }else {
            //System.out.println(((JmsgGroup)obj).getId() + "-" + ((JmsgGroup)obj).getStatus() + "-" + ((JmsgGroup)obj).getName());
            com.sanerzone.common.modules.smscenter.entity.JmsgGroup jmsgGroup = (com.sanerzone.common.modules.smscenter.entity.JmsgGroup)obj;
            String updateBy = jmsgGroup.getUpdateBy();
            String createBy = jmsgGroup.getCreateBy();
            User user = new User();
            user.setId(updateBy);
            User user1 = new User();
            user1.setId(createBy);
            JmsgGroup jmsgGroup1 = new JmsgGroup();
            jmsgGroup1.setUpdateBy(user);
            jmsgGroup1.setCreateBy(user1);
            jmsgGroup1.setName(jmsgGroup.getName());
            return jmsgGroup1;
        }
    }

    //根据userid获取分组
    public static JmsgGroup getJmsgGroupByUserId(String userId){
        if("".equals(userId)){
            return new JmsgGroup();
        }
        User user= UserUtils.get(userId);
        return getJmsgGroup(user.getGroupId());
    }

    public static String getSmsType(String smsType){
        //0:单发 1:验证码  2：群发
        if("0".equals(smsType)){
            return "单发";
        }else if("1".equals(smsType)){
            return "验证码";
        }else {
            return "群发";
        }
    }

}
