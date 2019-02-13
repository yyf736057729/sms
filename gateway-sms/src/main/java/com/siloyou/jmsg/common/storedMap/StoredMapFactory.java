package com.siloyou.jmsg.common.storedMap;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

public interface StoredMapFactory<K,T> {
	
	/**
	 * @param storedpath
	 * 数据文件保存的路径
	 * @param name 
	 * Map的名字
	 */
	Map<K,T> buildMap(String storedpath,String name);
	
	BlockingQueue<T> getQueue(String storedpath,String name);
	
	

}
