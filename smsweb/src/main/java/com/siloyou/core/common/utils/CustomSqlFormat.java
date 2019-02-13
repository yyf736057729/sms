package com.siloyou.core.common.utils;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

public class CustomSqlFormat implements MessageFormattingStrategy {

    /*
    /**
     * @Description: 格式化sql语句
     * @Description: 适配p6spy 2.1.3
     * @param: connectionId
     * @param: now
     * @param: elapsed
     * @param: category
     * @param: prepared
     * @param: sql
     * @return: String
     * @author: zhanghui
     * @Date: 2019-01-25
     * /
    public String formatMessage(final int connectionId, final String now, final long elapsed, final String category, final String prepared, final String sql) {
        //return now  + "|" + elapsed + "|" + category + "|connection " + connectionId + "|" + P6Util.singleLine(sql);
        //return P6Util.singleLine(sql + ";\n\n");
        //return sql + ";\n";
        String s = sql;
        if(s.equals("")){

        }else{
            s = s + ";\n";
        }
        return s;
    }
    */

    /**
     * @Description: 格式化sql语句
     * @Description: 适配p6spy 3.8.0
     * @param: var1
     * @param: var2
     * @param: var3
     * @param: var5
     * @param: var6
     * @param: var7
     * @param: var8
     * @return: String
     * @author: zhanghui
     * @Date: 2019-01-25
     */
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url){
        String s = sql;
        if(s.equals("")){

        }else{
            s = s + ";\n";
        }
        return s;
    }

}
