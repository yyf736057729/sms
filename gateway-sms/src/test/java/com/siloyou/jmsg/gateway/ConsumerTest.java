package com.siloyou.jmsg.gateway;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class ConsumerTest {
    public static void main(String[] args) {
        Consumer consumer = null ;
//        try {
            Properties properties = new Properties();
            properties.put("GROUP_ID", "GID_TopicTestMQ3");
            properties.setProperty(PropertyKeyConst.ConsumerId, "P-4_1");
            properties.put(PropertyKeyConst.AccessKey, "O9bt72vIz4jaym6N");
            properties.put(PropertyKeyConst.SecretKey, "6wVq6GXCxMokagacOQVm38r7pkvdCf");
            properties.put(PropertyKeyConst.ONSAddr, "http://onsaddr-internet.aliyun.com/rocketmq/nsaddr4client-internet");
            consumer = ONSFactory.createConsumer(properties);
            consumer.subscribe("SMSUMTV2", "*", new MessageListener() {
                public Action consume(Message message, ConsumeContext context) {
                    String body = "";
                    try {
                        body = new String(message.getBody(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //这里传入body参数进行业务操作
                    System.out.println(body);
                    return Action.CommitMessage;
                }
            });

            consumer.start();
            System.out.println("Consumer Started");
    }
}