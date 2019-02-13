package com.siloyou.jmsg.modules.sms.web;

import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.jmsg.modules.sms.entity.JmsgAddresslistInfo;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewaySign;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsUserTmpl;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsUserTmplService;
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
 * @ClassName: JmsgSmsUserTmplController
 * @Description: 用户模板管理
 * @author: yuyunfeng
 * @Date: 2019/1/11
*/

@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgSmsUserTmpl")
public class JmsgSmsUserTmplController extends BaseController {

    @Autowired
    private JmsgSmsUserTmplService jmsgSmsUserTmplService;

    @ModelAttribute
    public JmsgSmsUserTmpl get(@RequestParam(required=false) String id) {
        JmsgSmsUserTmpl entity = null;
        if (StringUtils.isNotBlank(id)){
            entity = jmsgSmsUserTmplService.get(id);
        }
        if (entity == null){
            entity = new JmsgSmsUserTmpl();
        }
        return entity;
    }

    @RequiresPermissions("sms:jmsgSmsUserTmpl:view")
    @RequestMapping(value = {"list", ""})
    public String list(JmsgSmsUserTmpl jmsgSmsUserTmpl, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
        Page<JmsgSmsUserTmpl> pageParam = new Page<JmsgSmsUserTmpl>(request, response);
        pageParam.setPageSize(30);
        if(jmsgSmsUserTmpl.getUserId()==null||"".equals(jmsgSmsUserTmpl.getUserId())){
            jmsgSmsUserTmpl.setUserId(request.getParameter("userId_2"));

        }
        Page<JmsgSmsUserTmpl> page = jmsgSmsUserTmplService.findPage(pageParam, jmsgSmsUserTmpl);
        model.addAttribute("page", page);
        return "modules/sms/jmsgSmsUserTmplList";
    }

    @RequiresPermissions("sms:jmsgSmsUserTmpl:view")
    @RequestMapping(value = "form")
    public String form(JmsgSmsUserTmpl jmsgSmsUserTmpl, Model model) {
        model.addAttribute("jmsgSmsUserTmpl", jmsgSmsUserTmpl);
        return "modules/sms/jmsgSmsUserTmplForm";
    }

    @RequiresPermissions("sms:jmsgSmsUserTmpl:view")
    @RequestMapping(value = "config")
    public String config(JmsgSmsUserTmpl jmsgSmsUserTmpl, Model model) {
        model.addAttribute("jmsgSmsUserTmpl", jmsgSmsUserTmpl);
        return "modules/sms/jmsgSmsUserTmplConfig";
    }

//    @RequiresPermissions("sms:jmsgSmsUserTmpl:edit")
//    @RequestMapping(value = "save")
//    public String save(JmsgSmsUserTmpl jmsgSmsUserTmpl, Model model, RedirectAttributes redirectAttributes) {
//        if (!beanValidator(model, jmsgSmsUserTmpl)){
//            return form(jmsgSmsUserTmpl, model);
//        }
//        if (jmsgSmsUserTmpl.getId()==null||"".equals(jmsgSmsUserTmpl.getId())) {
//            jmsgSmsUserTmpl.setId(UUID.randomUUID().toString());
//        }
//        if (jmsgSmsUserTmpl.getIsNewRecord())
//        {
//            JmsgSmsUserTmpl entity = jmsgSmsUserTmplService.get(jmsgSmsUserTmpl);
//
//            if (entity != null){
//                addMessage(model, "操作失败，用户模板已经存在");
//                return form(jmsgSmsUserTmpl, model);
//            }
//        }
//
//        jmsgSmsUserTmplService.save(jmsgSmsUserTmpl);
//        addMessage(redirectAttributes, "保存用户模板成功");
//        return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsUserTmpl/list";
//    }

    @RequiresPermissions("sms:jmsgSmsUserTmpl:edit")
    @RequestMapping(value = "edit")
    public String edit(JmsgSmsUserTmpl jmsgSmsUserTmpl, Model model, RedirectAttributes redirectAttributes,HttpServletRequest httpServletRequest) {
        if (!beanValidator(model, jmsgSmsUserTmpl)){
            return form(jmsgSmsUserTmpl, model);
        }
        String tempid = httpServletRequest.getParameter("tempid");
        if (tempid==null||"".equals(tempid)) {
            jmsgSmsUserTmpl.setId(UUID.randomUUID().toString());
        }else {
            jmsgSmsUserTmpl.setId(tempid);
        }
        if (jmsgSmsUserTmpl.getIsNewRecord())
        {
            JmsgSmsUserTmpl entity = jmsgSmsUserTmplService.get(jmsgSmsUserTmpl);

            if (entity != null){
                addMessage(model, "操作失败，用户模板已经存在");
                return form(jmsgSmsUserTmpl, model);
            }
        }

        jmsgSmsUserTmplService.update(jmsgSmsUserTmpl);
        addMessage(redirectAttributes, "保存用户模板成功");
        return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsUserTmpl/list";
    }

    @RequiresPermissions("sms:jmsgGatewaySign:edit")
    @RequestMapping(value = "batchSave")
    public String batchSave(JmsgSmsUserTmpl jmsgSmsUserTmpl, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, jmsgSmsUserTmpl)){
            return form(jmsgSmsUserTmpl, model);
        }

        StringBuilder msgSb = new StringBuilder();
        for (JmsgSmsUserTmpl jmsgSmsUserTmpl1 : jmsgSmsUserTmpl.getJmsgSmsUserTmplList())
        {
            if(jmsgSmsUserTmpl1.getJoinNumber()==null||"".equals(jmsgSmsUserTmpl1.getJoinNumber())){
                break;
            }
            JmsgSmsUserTmpl jmsgSmsUserTmpl2 = new JmsgSmsUserTmpl();
            jmsgSmsUserTmpl2.setId(UUID.randomUUID().toString());
            jmsgSmsUserTmpl2.setTemplateId(jmsgSmsUserTmpl1.getTemplateId());
            jmsgSmsUserTmpl2.setRemarks(jmsgSmsUserTmpl1.getRemarks());
            jmsgSmsUserTmpl2.setUserId(jmsgSmsUserTmpl.getUserId());
            jmsgSmsUserTmpl2.setJoinNumber(jmsgSmsUserTmpl1.getJoinNumber());
            jmsgSmsUserTmpl2.setTemplateId(jmsgSmsUserTmpl1.getId().replaceAll(",",""));
            //新增
                JmsgSmsUserTmpl entity = jmsgSmsUserTmplService.selectByUseridTemp(jmsgSmsUserTmpl2);

                if (entity != null){
                    msgSb.append("保存用户模板失败，模板: " + entity.getTemplateName() + " ， 用户: " + entity.getUserName() + " 已经存在").append("</br>");
                }
                else
                {
                    jmsgSmsUserTmplService.save(jmsgSmsUserTmpl2);
                }
        }

        if (StringUtils.isNotBlank(msgSb.toString()))
        {
            addMessage(redirectAttributes, msgSb.toString());
        }
        else
        {
            addMessage(redirectAttributes, "保存用户模板成功");
        }

        return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsUserTmpl/list";
    }


    @RequiresPermissions("sms:jmsgSmsUserTmpl:edit")
    @RequestMapping(value = "delete")
    public String delete( RedirectAttributes redirectAttributes,HttpServletRequest httpServletRequest,Model model) {
        JmsgSmsUserTmpl jmsgSmsUserTmpl = new JmsgSmsUserTmpl();
        jmsgSmsUserTmpl.setId(httpServletRequest.getParameter("id"));
        jmsgSmsUserTmplService.delete(jmsgSmsUserTmpl);
        addMessage(redirectAttributes, "删除用户模板成功");
        return "redirect:"+ Global.getAdminPath()+"/sms/jmsgSmsUserTmpl/?repage";
    }


    @RequiresPermissions("sms:jmsgSmsUserTmpl:edit")
    @RequestMapping(value = "batchDelete")
    public String batchDelete(String ids, RedirectAttributes redirectAttributes) {
        String[] array = ids.split(";");
        JmsgSmsUserTmpl param;
        if(array.length > 0){
            for (String id : array) {
                param = new JmsgSmsUserTmpl();
                param.setId(id);
                jmsgSmsUserTmplService.delete(param);
            }
        }
        addMessage(redirectAttributes, "删除用户模板成功");
        return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsUserTmpl/?repage";
    }
}
