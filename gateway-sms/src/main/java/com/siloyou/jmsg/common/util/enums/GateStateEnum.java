/**
 * zoneland.net Inc.
 * Copyright (c) 2002-2012 All Rights Reserved.
 */
package com.siloyou.jmsg.common.util.enums;

/**
 *
 * @author wangyong
 * @version $Id: GateStateEnum.java, v 0.1 2012-9-10 下午6:36:01 wangyong Exp $
 */
public enum GateStateEnum {

    ENABLED("1", "可用"), DISABLED("2", "不可用"), ERROR("0", "启用异常");

    private String value;
    private String description;

    private GateStateEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.description;
    }

    public static String getDescriptionByValue(String value) {
        return "";
    }
}
