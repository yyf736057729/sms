package com.sanerzone.smscenter.task;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanerzone.common.support.storedMap.BDBStoredMapFactoryImpl;

public class SmsSRQueueFactory
{
    public static Logger logger = LoggerFactory.getLogger(SmsSRQueueFactory.class);
    
    public static BlockingQueue<Serializable> submitRespQueue;
    
    public static BlockingQueue<Serializable> getSmsSendQueue()
    {
        if (submitRespQueue == null)
        {
            synchronized (SmsSRQueueFactory.class)
            {
                submitRespQueue = BDBStoredMapFactoryImpl.INS.getQueue("SMS", "SMSSRUPDATE");
            }
            
        }
        
        return submitRespQueue;
    }
}
