package com.siloyou.jmsg.gateway;

import java.util.concurrent.atomic.AtomicLong;

import com.google.common.util.concurrent.RateLimiter;
import com.siloyou.jmsg.common.util.CachedMillisecondClock;

public class TestLimter {
	public static void main(String[] args) {

		AtomicLong cnt = new AtomicLong();
		RateLimiter limiter = RateLimiter.create(100);
		
		long startTime = CachedMillisecondClock.INS.now();
		
		for(int i=0;i< 10;i ++) {
			
			System.out.println(i + ":"+ limiter.acquire(2));
			cnt.incrementAndGet();
		}
		
		System.out.println(CachedMillisecondClock.INS.now() - startTime );
	}
}
