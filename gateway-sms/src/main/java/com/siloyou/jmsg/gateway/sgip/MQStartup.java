package com.siloyou.jmsg.gateway.sgip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Application;

public class MQStartup
{
    Logger logger = LoggerFactory.getLogger(MQStartup.class);
    
    private String appCode = Application.appCode;
    
    private void init()
    {
        
//        single();
//        batch();
//        if(mmsGateWayMTListener != null) {
//        	logger.info("SGIP 彩信网关启动...");
//        	mms();
//        }
    }
    
//    private void single()
//    {
//        DefaultMQPushConsumer singlePushConsumer = MQCustomerFactory.getPushConsumer(appCode + "-SingleGroup");
//        try
//        {
//            singlePushConsumer.setInstanceName("SmsSingleTaskServiceConsumer");
//            singlePushConsumer.subscribe(Global.getConfig("gateway.single.topic"), appCode);
//            singlePushConsumer.setMessageListener(gateWayMTListener);
//            singlePushConsumer.start();
//            logger.info("{}, 单发消费程序已启动", appCode);
//        }
//        catch (MQClientException e)
//        {
//            logger.error(appCode + "启动单发消费程序异常", e);
//        }
//    }
//    
//    private void batch()
//    {
//        DefaultMQPushConsumer batchPushConsumer = MQCustomerFactory.getPushConsumer(appCode + "-BatchGroup");
//        try
//        {
//            batchPushConsumer.setInstanceName("SmsSingleTaskServiceConsumer");
//            batchPushConsumer.subscribe(Global.getConfig("gateway.batch.topic"), appCode);
//            batchPushConsumer.setMessageListener(gateWayMTListener);
//            batchPushConsumer.start();
//            logger.info("{}, 群发消费程序已启动", appCode);
//        }
//        catch (MQClientException e)
//        {
//            logger.error(appCode + "启动群发消费程序异常", e);
//        }
//    }
    
}
