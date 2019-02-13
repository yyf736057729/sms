package com.siloyou.jmsg.modules.sms.web;

import com.alibaba.fastjson.JSONObject;
import com.siloyou.jmsg.modules.sms.utils.ResultUtil;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayQueue;
import com.siloyou.jmsg.modules.sms.service.JmsgGatewayQueueService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * @author yuyunfeng
 * @create_time 2019/1/9
 * @describe ${通道队列}
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgGatewayQueue")
public class JmsgSmsGatewayQueueController extends BaseController {

    @Autowired
    private JmsgGatewayQueueService jmsgGatewayQueueService;

    @RequiresPermissions("sms:jmsgGatewayQueue:view")
    @RequestMapping(value = {"list", ""})
    public String list(JmsgGatewayQueue jmsgGatewayQueue, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        Page<JmsgGatewayQueue> pageParam = new Page<JmsgGatewayQueue>(request, response);
        pageParam.setPageSize(30);
        if(jmsgGatewayQueue.getGatewayName()!=null&&!"".equals(jmsgGatewayQueue.getGatewayName())){
            String gatewayid = jmsgGatewayQueue.getGatewayId();
            jmsgGatewayQueue = jmsgGatewayQueueService.searchByGatewayName(jmsgGatewayQueue);
//            jmsgGatewayQueue.setGatewayId(gatewayid);
        }
        Page<JmsgGatewayQueue> page = jmsgGatewayQueueService.findPage(pageParam, jmsgGatewayQueue);
        model.addAttribute("page", page);
        return "modules/sms/jmsgGatewayQueueList";
    }

    @RequiresPermissions("sms:jmsgGatewayQueue:view")
    @RequestMapping(value = "form")
    public String form(JmsgGatewayQueue jmsgGatewayQueue, Model model) {
        model.addAttribute("jmsgGatewayQueue", jmsgGatewayQueue);
        return "modules/sms/jmsgGatewayQueueForm";
    }

    @RequiresPermissions("sms:jmsgGatewayQueue:edit")
    @RequestMapping(value = "save")
    public String save(JmsgGatewayQueue jmsgGatewayQueue, RedirectAttributes redirectAttributes) {
        //先判断是否有相同的gatewayid 和  businessType  通道名和通道类型
        int i= jmsgGatewayQueueService.searchLikeByGateway(jmsgGatewayQueue);
        if(i>0){
            addMessage(redirectAttributes, "保存失败，已经有相同的队列信息");
            return "redirect:"+ Global.getAdminPath()+"/sms/jmsgGatewayQueue/?repage";
        }
        jmsgGatewayQueue.setId(UUID.randomUUID().toString());
        jmsgGatewayQueueService.save(jmsgGatewayQueue);
        addMessage(redirectAttributes, "保存通道队列成功");
        return "redirect:"+ Global.getAdminPath()+"/sms/jmsgGatewayQueue/?repage";
    }

    @RequiresPermissions("sms:jmsgGatewayQueue:edit")
    @RequestMapping(value = "updateStatus")
    public String updateStatus( RedirectAttributes redirectAttributes,HttpServletRequest httpServletRequest) {
        JmsgGatewayQueue jmsgGatewayQueue = new JmsgGatewayQueue();
        jmsgGatewayQueue.setId(httpServletRequest.getParameter("id"));
        jmsgGatewayQueue.setStatus(httpServletRequest.getParameter("status"));
        jmsgGatewayQueueService.update(jmsgGatewayQueue);
        addMessage(redirectAttributes, "修改通道队列成功");
        return "redirect:"+ Global.getAdminPath()+"/sms/jmsgGatewayQueue/?repage";
    }

    @RequiresPermissions("sms:jmsgGatewayQueue:edit")
    @RequestMapping(value = "edit")
    public void edit(JmsgGatewayQueue jmsgGatewayQueue2, RedirectAttributes redirectAttributes,HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) throws IOException {
        JSONObject jsonObject = new JSONObject();
        JmsgGatewayQueue jmsgGatewayQueue = new JmsgGatewayQueue();
        jmsgGatewayQueue.setId(httpServletRequest.getParameter("queueid"));
        jmsgGatewayQueue.setQueueName(httpServletRequest.getParameter("queueName"));
        jmsgGatewayQueue.setWeight(httpServletRequest.getParameter("weight"));
        jmsgGatewayQueue.setRemarks(httpServletRequest.getParameter("remarks"));
        jmsgGatewayQueueService.update(jmsgGatewayQueue);
        addMessage(redirectAttributes, "修改通道队列成功");
        try {
            ResultUtil.writeResult(httpServletResponse ,jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresPermissions("sms:jmsgGatewayQueue:edit")
    @RequestMapping(value = "delete")
    public String delete( RedirectAttributes redirectAttributes,HttpServletRequest httpServletRequest,Model model) {
        JmsgGatewayQueue jmsgGatewayQueue = new JmsgGatewayQueue();
        jmsgGatewayQueue.setId(httpServletRequest.getParameter("id"));
        jmsgGatewayQueueService.delete(jmsgGatewayQueue);
        addMessage(redirectAttributes, "删除通道队列成功");
        return "redirect:"+ Global.getAdminPath()+"/sms/jmsgGatewayQueue/?repage";
    }
}
