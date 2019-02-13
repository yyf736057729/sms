/**
 * zoneland.net Inc.
 * Copyright (c) 2002-2013 All Rights Reserved.
 */
package com.siloyou.jmsg.gateway.ums.handler;

/**
 * 
 * @author gang
 * @version $Id: MethodTimeoutException.java, v 0.1 2013-2-20 上午11:34:48 gang Exp $
 */
public class MethodTimeoutException extends RuntimeException {

    /**  */
    private static final long serialVersionUID = -2557230951215953301L;

    /**
     * 
     */
    public MethodTimeoutException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public MethodTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public MethodTimeoutException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public MethodTimeoutException(Throwable cause) {
        super(cause);
    }

}
