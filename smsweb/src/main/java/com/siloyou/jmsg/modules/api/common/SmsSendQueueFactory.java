package com.siloyou.jmsg.modules.api.common;

import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siloyou.jmsg.common.storedMap.BDBStoredMapFactoryImpl;

public class SmsSendQueueFactory
{
    public static Logger logger = LoggerFactory.getLogger(SmsSendQueueFactory.class);
    
    public static BlockingQueue<Serializable> submitRespQueue;
    
    public static BlockingQueue<Serializable> getSmsSendQueue()
    {
        if (submitRespQueue == null)
        {
            synchronized (SmsSendQueueFactory.class)
            {
                submitRespQueue = new ArrayBlockingQueue<Serializable>(10000);//BDBStoredMapFactoryImpl.INS.getQueue("SMS", "SMSSEND");
            }
            
        }
        
        return submitRespQueue;
    }
}
