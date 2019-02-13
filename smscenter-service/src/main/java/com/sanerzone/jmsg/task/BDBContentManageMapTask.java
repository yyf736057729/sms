package com.sanerzone.jmsg.task;

import com.sanerzone.jmsg.util.BDBContentManageMapUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @description: BDB存储冗余数据
 * @author: Cral
 * @create: 2019-01-09 18:21
 */
public class BDBContentManageMapTask {
    public void exec(){
        Iterator<Map.Entry<String, Serializable>> entries = BDBContentManageMapUtils.map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Serializable> entry = entries.next();
            Serializable serializable = entry.getValue();
            String phoneNumbers = (String)serializable;
            String[] s = phoneNumbers.split("_");
            long cache_time =  Long.parseLong(s[1]);//要存在的分钟数
            long current = Long.parseLong(s[2]);//当前时间的毫秒数
            long c = System.currentTimeMillis();
            boolean diff = (cache_time*1000*60)>(c-current);
            if(!diff){
                entries.remove();
//                BDBContentManageMapUtils.map.remove("hash_" + hash);
            }

        }
    }
}