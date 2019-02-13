package com.siloyou.jmsg.mqtest;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;

/**
 * @author dddd
 * @description 消息生产者
 * @date 2016-04-07
 */
public class Producer {

    /*
     * Constructs a client instance with your account for accessing DefaultMQProducer
     */
    private static DefaultMQProducer producer = new DefaultMQProducer("ProducerGroupName");
    private static int initialState = 0;

    private Producer() {

    }

    public static DefaultMQProducer getDefaultMQProducer(){
        if(producer == null){
            producer = new DefaultMQProducer("ProducerGroupName");
        }

        if(initialState == 0){
            producer.setNamesrvAddr("101.37.117.13:9876");
            try {
                producer.start();
            } catch (MQClientException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }

            initialState = 1;
        }

        return producer;
    }

}