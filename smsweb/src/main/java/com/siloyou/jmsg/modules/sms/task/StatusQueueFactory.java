package com.siloyou.jmsg.modules.sms.task;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;

import com.siloyou.jmsg.common.storedMap.BDBStoredMapFactoryImpl;

public class StatusQueueFactory{
//    public static BlockingQueue<Serializable> statusRespQueue;
//    public static BlockingQueue<Serializable> uTRespQueue;
//    
//    public static BlockingQueue<Serializable> getStatusRespQueue() {
//        if(statusRespQueue == null)
//        	statusRespQueue = BDBStoredMapFactoryImpl.INS.getQueue("SMS", "SMSMQSTATUS");//短信发送状态
////        	statusRespQueue = new ArrayBlockingQueue<Serializable>(10000);
//        return statusRespQueue;
//    }
//    
//    public static BlockingQueue<Serializable> getuTRespQueue() {
//        if(uTRespQueue == null)
//        	uTRespQueue = BDBStoredMapFactoryImpl.INS.getQueue("SMS", "SMSUTSTATUS");//推送短信状态报告 队列
////        	uTRespQueue = new ArrayBlockingQueue<Serializable>(10000);
//        return uTRespQueue;
//    }
    
}
