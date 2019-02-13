package com.siloyou.jmsg.gateway.ums.handler;

import java.util.concurrent.Callable;

import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.gateway.ums.handler.MessageFactory;
import com.siloyou.jmsg.gateway.ums.handler.ThreadMonitor;

public class SendThread implements Callable<Boolean> {

    private MessageFactory messageFactory;

    private SmsMtMessage        msg;

    public SendThread(MessageFactory messageFactory, SmsMtMessage msg) {
        this.msg = msg;
        this.messageFactory = messageFactory;
    }

    /** 
     * @see java.util.concurrent.Callable#call()
     */
    public Boolean call() throws Exception {
        return doSend(msg);
    }

    public Boolean doSend(SmsMtMessage msg) throws Exception {
        //监控线程的执行时间，对于超时的线程进行中断，默认8秒超时
        ThreadMonitor m = new ThreadMonitor(Thread.currentThread());
        m.start();
        try {

            return messageFactory.sendMsg(msg);
        } finally {
            m.stop();
        }

    }

}
