package com.siloyou.jmsg.mqtest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.remoting.exception.RemotingException;

public class Test {

    private static final Logger logger = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args){

//        sendMsg();
        System.out.println("开始获取- -----");
        receiveMsg();
    }

    public static void sendMsg(){

        // 获取消息生产者
        DefaultMQProducer producer = Producer.getDefaultMQProducer();

        try {
            for(int i=0;i<2000;i++){
                Message msg = new Message(
                        "SMSMT8900S",                   // topic
                        "cmpp",                         // tag
                        "OrderID00"+i,                  // key
                        ("Hello MetaQ"+i).getBytes());  // body
                SendResult sendResult = producer.send(msg);
                //logger.info("sendResult:{}", sendResult);
            }
        } catch (MQClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RemotingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MQBrokerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        producer.shutdown();
    }

    public static void receiveMsg(){

        // 获取消息生产者
        DefaultMQPushConsumer consumer = Consumer.getDefaultMQPushConsumer();

        // 订阅主体
        try {
//            consumer.subscribe("TopicTest1", "*");
            consumer.subscribe("SMSUMTV1", "*");

            consumer.registerMessageListener(new MessageListenerConcurrently() {

                /**
                 * * 默认msgs里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
                 */
                public ConsumeConcurrentlyStatus consumeMessage(
                        List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                    logger.info("currentThreadName:{} and Receive New Messages:{}",Thread.currentThread().getName(),msgs);

                    MessageExt msg = msgs.get(0);

                    if (msg.getTopic().equals("SMSUMTV1")) {
                        // 执行TopicTest1的消费逻辑
                        if (msg.getTags() != null ) {
                            // 执行TagA的消费
                            logger.info("MsgBody:{}",new String(msg.getBody()));
                        } else if (msg.getTags() != null
                                && msg.getTags().equals("TagC")) {
                            // 执行TagC的消费
                        } else if (msg.getTags() != null
                                && msg.getTags().equals("TagD")) {
                            // 执行TagD的消费
                        }
                    } else if (msg.getTopic().equals("TopicTest2")) {
                        // 执行TopicTest2的消费逻辑
                    }

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });

            /**
             * Consumer对象在使用之前必须要调用start初始化，初始化一次即可<br>
             */
            consumer.start();

            logger.info("Consumer Started.");
        } catch (MQClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}