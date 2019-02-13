/**
 * zoneland.net Inc.
 * Copyright (c) 2002-2012 All Rights Reserved.
 */
package com.siloyou.jmsg.common.util.enums;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gang
 * @version $Id: GateEnum.java, v 0.1 2012-9-1 上午9:49:50 gang Exp $
 */
public enum GateEnum {

    CMPP("CMPP", "移动网关"),
    /**  */
    CMPP30("CMPP30", "移动网关V3"),
    /**  */
    SGIP("SGIP", "联通网关"),
    /**  */
    SMGP("SMGP", "电信网关"),
    /**  */
    SMGP3("SMGP3", "电信网关V3"),
    /**  */
    HTTP("HTTP", "HTTP网关");

    private String value;
    private String description;

    private GateEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static String getDescription(String value) {
        for (GateEnum msg : GateEnum.values()) {
            if (msg.value.equals(value)) {
                return msg.description;
            }
        }
        return "";
    }

    public static String getNameByValue(String value) {
        GateEnum[] msgInStatusEnum = GateEnum.values();
        for (int i = 0, len = msgInStatusEnum.length; i < len; ++i) {
            if (msgInStatusEnum[i].value.equals(value)) {
                return msgInStatusEnum[i].description;
            }
        }
        return null;
    }

    /**
     * 根据类型获取各大运营商enum
     * @param type
     * @return
     */
    public static List<GateEnum> getEnumByType(String type) {
        List<GateEnum> list = new ArrayList<GateEnum>();
        if (type != null && !"".equals(type)) {
            GateEnum[] msgInStatusEnum = GateEnum.values();
            for (int i = 0; i < msgInStatusEnum.length; i++) {
                if (msgInStatusEnum[i].getValue().startsWith(type.toUpperCase())) {
                    list.add(msgInStatusEnum[i]);
                }
            }
        }

        return list;
    }

}
