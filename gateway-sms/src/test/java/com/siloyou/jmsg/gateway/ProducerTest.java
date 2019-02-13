package com.siloyou.jmsg.gateway;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;

import java.util.Properties;

public class ProducerTest {
    public static void main(String[] args) throws InterruptedException {
        Producer producer = null;
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.AccessKey, "O9bt72vIz4jaym6N");
            properties.put(PropertyKeyConst.SecretKey, "6wVq6GXCxMokagacOQVm38r7pkvdCf");
            properties.put(PropertyKeyConst.NAMESRV_ADDR, "http://onsaddr.mq-internet-access.mq-internet.aliyuncs.com:80");
            producer = ONSFactory.createProducer(properties);
            producer.start();
            int i=0;

            while (true) {
                String str = "Hello MQ"+i++;
                System.out.println(str);
                Message msg = new Message( //
                        "SMSUMTV2",
                        "TagA","ORDERID_100",
                        str.getBytes());
                SendResult sendResult = producer.send(msg);
            }
    }
}