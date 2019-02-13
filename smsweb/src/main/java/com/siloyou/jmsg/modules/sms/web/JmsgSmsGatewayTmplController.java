package com.siloyou.jmsg.modules.sms.web;

import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsGatewayTmpl;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsGatewayTmplService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @ClassName: JmsgSmsGatewayTmplController
 * @Description: 通道模板管理
 * @author: yuyunfeng
 * @Date: 2019/1/11
*/

@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgSmsGatewayTmpl")
public class JmsgSmsGatewayTmplController extends BaseController {

    @Autowired
    private JmsgSmsGatewayTmplService jmsgSmsGatewayTmplService;

    @ModelAttribute
    public JmsgSmsGatewayTmpl get(@RequestParam(required=false) String id) {
        JmsgSmsGatewayTmpl entity = null;
        if (StringUtils.isNotBlank(id)){
            entity = jmsgSmsGatewayTmplService.get(id);
        }
        if (entity == null){
            entity = new JmsgSmsGatewayTmpl();
        }
        return entity;
    }

    @RequiresPermissions("sms:jmsgSmsGatewayTmpl:view")
    @RequestMapping(value = {"list", ""})
    public String list(JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        Page<JmsgSmsGatewayTmpl> pageParam = new Page<JmsgSmsGatewayTmpl>(request, response);
        pageParam.setPageSize(30);
        if(jmsgSmsGatewayTmpl.getGatewayName()!=null&&!"".equals(jmsgSmsGatewayTmpl.getGatewayName())){
            String gatewayid = jmsgSmsGatewayTmpl.getGatewayId();
            jmsgSmsGatewayTmpl = jmsgSmsGatewayTmplService.searchByGatewayName(jmsgSmsGatewayTmpl);
//            jmsgGatewayQueue.setGatewayId(gatewayid);
        }
        Page<JmsgSmsGatewayTmpl> page = jmsgSmsGatewayTmplService.findPage(pageParam, jmsgSmsGatewayTmpl);
        model.addAttribute("page", page);
        return "modules/sms/jmsgSmsGatewayTmplList";
    }

    @RequiresPermissions("sms:jmsgSmsGatewayTmpl:view")
    @RequestMapping(value = "form")
    public String form(JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl, Model model) {
        model.addAttribute("jmsgSmsGatewayTmpl", jmsgSmsGatewayTmpl);
        return "modules/sms/jmsgSmsGatewayTmplForm";
    }

    @RequiresPermissions("sms:jmsgSmsGatewayTmpl:edit")
    @RequestMapping(value = "save")
    public String save(JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl, RedirectAttributes redirectAttributes) {
        if(jmsgSmsGatewayTmpl.getId()!=null&&"".equals(jmsgSmsGatewayTmpl.getId())){
            jmsgSmsGatewayTmpl.setId(UUID.randomUUID().toString());
        }
        jmsgSmsGatewayTmplService.save(jmsgSmsGatewayTmpl);
        addMessage(redirectAttributes, "保存通道模板成功");
        return "redirect:"+ Global.getAdminPath()+"/sms/jmsgSmsGatewayTmpl/?repage";
    }


    @RequiresPermissions("sms:jmsgSmsGatewayTmpl:edit")
    @RequestMapping(value = "delete")
    public String delete( RedirectAttributes redirectAttributes,HttpServletRequest httpServletRequest,Model model) {
        JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl = new JmsgSmsGatewayTmpl();
        jmsgSmsGatewayTmpl.setId(httpServletRequest.getParameter("id"));
        jmsgSmsGatewayTmplService.delete(jmsgSmsGatewayTmpl);
        addMessage(redirectAttributes, "删除通道模板成功");
        return "redirect:"+ Global.getAdminPath()+"/sms/jmsgSmsGatewayTmpl/?repage";
    }


    @RequiresPermissions("sms:jmsgSmsUserTmpl:edit")
    @RequestMapping(value = "batchDelete")
    public String batchDelete(String ids, RedirectAttributes redirectAttributes) {
        String[] array = ids.split(";");
        JmsgSmsGatewayTmpl param;
        if(array.length > 0){
            for (String id : array) {
                param = new JmsgSmsGatewayTmpl();
                param.setId(id);
                jmsgSmsGatewayTmplService.delete(param);
            }
        }
        addMessage(redirectAttributes, "删除通道模板成功");
        return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsGatewayTmpl/?repage";
    }
}
