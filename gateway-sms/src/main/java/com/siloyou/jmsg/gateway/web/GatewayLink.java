package com.siloyou.jmsg.gateway.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.util.Constants;
import com.siloyou.jmsg.common.util.ResultUtil;
import com.siloyou.jmsg.gateway.Result;
import com.siloyou.jmsg.gateway.cmpp.CmppGatewayFactory;
import com.siloyou.jmsg.gateway.cmpp.handler.SmsBizHandler;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yuyunfeng
 * @create_time 2019/1/7
 * @describe ${短信链路}
 */
@RestController
@RequestMapping(value = "/api/sms/link")
public class GatewayLink {
    public static final Logger LOG =  Logger.getLogger(GatewayLink.class);

    @RequestMapping(value = "connect", method = RequestMethod.POST)
    public void link(HttpServletRequest request, HttpServletResponse response) {
        JSONObject params = new JSONObject();
        JSONObject result = new JSONObject();
        String gatewayid =  request.getParameter("id");//网关名称
        result.put("spend_map",SmsBizHandler.spend_map.get(gatewayid));//发送速率记录
        result.put("connect_map",CmppGatewayFactory.connect_map.get(gatewayid));//连接状态记录

        try {
            params.put(Constants.RET_MSG,Constants.SUCCESS_MSG);
            params.put(Constants.RET_CODE,Constants.SUCCESS_CODE);
            params.put(Constants.RETURN_PARAMS,result);
            ResultUtil.writeResult(response,params.toString());
        } catch (IOException e) {
           LOG.info("获取短信链路报错:"+e.getMessage());
        }
    }
}
