package com.siloyou.jmsg.gateway.api;

import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.siloyou.jmsg.common.storedMap.BDBStoredMapFactoryImpl;

public class GateWayQueueFactory
{
    public static BlockingQueue<Serializable> submitRespQueue;
    
    public static BlockingQueue<Serializable> getSubmitRespQueue() {
        if(submitRespQueue == null)
        {
            submitRespQueue = BDBStoredMapFactoryImpl.INS.getQueue("SMS", "SMSSUBMIT");
            //submitRespQueue = new ArrayBlockingQueue<Serializable>(10000);
        }
        
        return submitRespQueue;
    }
    
}
