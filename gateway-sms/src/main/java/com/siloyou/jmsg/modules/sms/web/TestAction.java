package com.siloyou.jmsg.modules.sms.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test")
public class TestAction {

    @RequestMapping("/greeting")
    @ResponseBody
    public Object hello(){
        return "Hello Spring";
    }
}
