package com.siloyou.jmsg.gateway;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.google.common.collect.Maps;
import com.siloyou.jmsg.common.storedMap.BDBStoredMapFactoryImpl;
import com.sleepycat.collections.StoredSortedMap;

public class QueueTest {

	public static void main(String[] args) {
//		BlockingQueue<Serializable> queue= BDBStoredMapFactoryImpl.INS.getQueue("TEST", "queue1");
//		Map<String,String> m1 = Maps.newConcurrentMap();
//		queue.clear();
//		for(int i=0;i<1000;i ++) {
//			m1.put("key" + i, "abc");
//			queue.offer((Serializable)m1);
//			
//			m1.clear();
//		}
//		System.out.println(queue.size());
		
//		BlockingQueue<Message> queue= BDBStoredMapFactoryImpl.INS.getQueue("TEST", "queue1");
//		Map<String,String> m = Maps.newConcurrentMap();
//		for(int i=0;i<1000;i ++) {
//			queue.offer(new CmppActiveTestRequestMessage());
//		}
//		System.out.println(queue.size());
		
		Map<String, Serializable> map = BDBStoredMapFactoryImpl.INS.buildMap("YD5002", "map1");
//		Map<String,String> m = Maps.newConcurrentMap();
//		for(int i=0;i<1000;i ++) {
//			m.put("key" + i, "abc");
//			map.put("key"+map.size() + i, (Serializable)m);
//			
//			m.clear();
//		}
		System.out.println(map.size());
		
		map.clear();
		System.out.println(map.size());
		
		
		
//		StoredSortedMap<Long, Serializable> sortedStoredmap = BDBStoredMapFactoryImpl.INS.buildStoredSortedMap("TEST", "Trans_map1");
//		sortedStoredmap.clear();
//		Map<String,String> m = Maps.newConcurrentMap();
//		Long key = 1L;
//		for(int i=1;i<=1000;i ++) {
//			m.put("key" + i, "abc");
//			Long lastKey = (Long) sortedStoredmap.lastKey();
//			System.out.println(lastKey);
//			lastKey = (lastKey == null) ? 1L : ++ lastKey;
//			sortedStoredmap.put( lastKey, (Serializable)m);
//			try {
//				Thread.sleep(10l);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			m.clear();
//		}
//		System.out.println(sortedStoredmap.size());
		
//		while(!queue.isEmpty()) {
//			try {
//				System.out.println((Serializable)queue.take());
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}

	}

}
