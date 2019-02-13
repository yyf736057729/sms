package com.siloyou.jmsg.mqtest;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;

/**
 * @author dddd
 * @description 消息消费者
 * @date 2016-04-07
 */
public class Consumer {

    /*
     * Constructs a client instance with your account for accessing DefaultMQConsumer
     */
    private static DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ConsumerGroupName");
    private static int initialState = 0;

    private Consumer() {

    }

    public static DefaultMQPushConsumer getDefaultMQPushConsumer(){
        if(consumer == null){
            consumer = new DefaultMQPushConsumer("ConsumerGroupName");
        }

        if(initialState == 0){
            consumer.setNamesrvAddr("101.37.117.13:9876");
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            initialState = 1;
        }

        return consumer;
    }

}