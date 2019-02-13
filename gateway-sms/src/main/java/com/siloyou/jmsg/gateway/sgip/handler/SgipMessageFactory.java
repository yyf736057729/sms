package com.siloyou.jmsg.gateway.sgip.handler;

import java.io.Serializable;
import java.util.Map;

import com.siloyou.jmsg.common.storedMap.BDBStoredMapFactoryImpl;

public class SgipMessageFactory
{
    private static Map<String, Serializable> storedMap ;
    public static Map<String, Serializable> getStoredMap(){
        if(storedMap == null) {
            storedMap = BDBStoredMapFactoryImpl.INS.buildMap("sgip", "message");
        }
        return storedMap;
    }
}
