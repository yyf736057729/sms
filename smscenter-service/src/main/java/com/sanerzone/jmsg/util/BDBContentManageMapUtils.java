package com.sanerzone.jmsg.util;

import com.sanerzone.common.support.storedMap.BDBStoredMapFactoryImpl;

import java.io.Serializable;
import java.util.Map;

/**
 * @description: bdb短信控制数据存储
 * @author: Cral
 * @create: 2019-01-09 14:09
 */
public class BDBContentManageMapUtils {
    public static Map<String, Serializable> map = BDBStoredMapFactoryImpl.INS.buildMap("sms_phone_conetxt", "conetxt");

}