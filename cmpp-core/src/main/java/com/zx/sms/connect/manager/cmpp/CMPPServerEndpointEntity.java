package com.zx.sms.connect.manager.cmpp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.zx.sms.connect.manager.EndpointEntity;
import com.zx.sms.connect.manager.ServerEndpoint;
/**
 *@author Lihuanghe(18852780@qq.com)
 */
public class CMPPServerEndpointEntity extends EndpointEntity implements ServerEndpoint {

	private Map<String,CMPPServerChildEndpointEntity> childrenEndpoint = new ConcurrentHashMap<String,CMPPServerChildEndpointEntity>() ;

	public List<EndpointEntity> getAllChild() {
		List<EndpointEntity> list = new ArrayList();
		Iterator var2 = this.childrenEndpoint.entrySet().iterator();

		while(var2.hasNext()) {
			Map.Entry<String, CMPPServerChildEndpointEntity> entry = (Map.Entry)var2.next();
			list.add(entry.getValue());
		}

		return list;
	}

	public void addchild(EndpointEntity entity)
	{
		
		childrenEndpoint.put(((CMPPServerChildEndpointEntity)entity).getUserName().trim(), (CMPPServerChildEndpointEntity)entity);
	}
	
	public void removechild(EndpointEntity entity){
		childrenEndpoint.remove(((CMPPServerChildEndpointEntity)entity).getUserName());
	}
	
	public EndpointEntity getChild(String userName)
	{
		return childrenEndpoint.get(userName);
	}

	@Override
	public CMPPServerEndpointConnector buildConnector() {
		return new CMPPServerEndpointConnector(this);
	}

}
