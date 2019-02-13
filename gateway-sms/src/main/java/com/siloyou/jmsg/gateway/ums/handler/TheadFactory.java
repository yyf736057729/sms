/**
 * zoneland.net Inc.
 * Copyright (c) 2002-2013 All Rights Reserved.
 */
package com.siloyou.jmsg.gateway.ums.handler;

import java.util.concurrent.atomic.AtomicInteger;

import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.util.enums.GateEnum;

/**
 *
 * @author gang
 * @version $Id: TheadFactory.java, v 0.1 2013-4-12 上午10:30:02 gang Exp $
 */
public class TheadFactory {

    private static AtomicInteger sgipTheadCount = new AtomicInteger(0);

    private static AtomicInteger smgpTheadCount = new AtomicInteger(0);

    public static final int      maxCount       = 15;

    public static SendThread newSendThread(MessageFactory messageFactory, SmsMtMessage msg,
                                           GateEnum gateEnum) {
        if (GateEnum.SGIP == gateEnum) {
            if (sgipTheadCount.get() > maxCount) {
                return null;
            } else {
                sgipTheadCount.addAndGet(1);
                return new SendThread(messageFactory, msg);
            }
        } else {
            if (smgpTheadCount.get() > maxCount) {
                return null;
            } else {
                smgpTheadCount.addAndGet(1);
                return new SendThread(messageFactory, msg);
            }
        }
    }

    public static void releaseThread(GateEnum gateEnum) {
        if (GateEnum.SGIP == gateEnum) {
            sgipTheadCount.decrementAndGet();
        } else {
            smgpTheadCount.decrementAndGet();
        }
    }

}
