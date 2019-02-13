package com.siloyou.jmsg.common.mq;

import com.alibaba.rocketmq.client.consumer.PullTaskContext;

public interface SMSPullTaskCallback {
    public void doPullTask(final SMSMessageQueue mq, final PullTaskContext context);
}
