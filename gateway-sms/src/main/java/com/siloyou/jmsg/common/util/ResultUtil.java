package com.siloyou.jmsg.common.util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
/**
 * @ClassName: ResultUtil
 * @Description: 请求回执封装类
 * @author: yuyunfeng
 * @Date: 2019/1/7
*/
public class ResultUtil {
    public static void writeResult(HttpServletResponse response, String res) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.write(res);
        writer.flush();
    }
}
