/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgUserSign;
import com.siloyou.jmsg.modules.sms.service.JmsgUserSignService;

/**
 * 用户签名Controller
 * @author zj
 * @version 2016-09-08
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgUserSign")
public class JmsgUserSignController extends BaseController {

	@Autowired
	private JmsgUserSignService jmsgUserSignService;
	
	@ModelAttribute
	public JmsgUserSign get(@RequestParam(required=false) String id) {
		JmsgUserSign entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgUserSignService.get(id);
		}
		if (entity == null){
			entity = new JmsgUserSign();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgUserSign:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgUserSign jmsgUserSign, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgUserSign> page = jmsgUserSignService.findPage(new Page<JmsgUserSign>(request, response), jmsgUserSign); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgUserSignList";
	}

	@RequiresPermissions("sms:jmsgUserSign:view")
	@RequestMapping(value = "form")
	public String form(JmsgUserSign jmsgUserSign, Model model) {
		model.addAttribute("jmsgUserSign", jmsgUserSign);
		return "modules/sms/jmsgUserSignForm";
	}

	@RequiresPermissions("sms:jmsgUserSign:edit")
	@RequestMapping(value = "save")
	public String save(JmsgUserSign jmsgUserSign, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgUserSign)){
			return form(jmsgUserSign, model);
		}
		jmsgUserSign.setCreateUserId(UserUtils.getUser().getId());
		jmsgUserSign.setCreatetime(new Date());
		jmsgUserSignService.save(jmsgUserSign);
		addMessage(redirectAttributes, "保存用户签名成功");

		String userName = jmsgUserSign.getUser().getName();
		try {
			userName = java.net.URLEncoder.encode(userName,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

        return "redirect:" + Global.getAdminPath() + "/sms/jmsgUserSign/list?user.id=" + jmsgUserSign.getUser().getId()
            + "&user.name=" + userName;
	}
	
	@RequiresPermissions("sms:jmsgUserSign:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgUserSign jmsgUserSign, RedirectAttributes redirectAttributes) {
		jmsgUserSignService.delete(jmsgUserSign);
		addMessage(redirectAttributes, "删除用户签名成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgUserSign/?repage";
	}
	
	@RequiresPermissions("sms:jmsgUserSign:edit")
    @RequestMapping(value = "batchDelete")
    public String batchDelete(JmsgUserSign jmsgUserSign, String ids, RedirectAttributes redirectAttributes) 
	{
	    try
        {
            ids = URLDecoder.decode(ids, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            addMessage(redirectAttributes, e.toString());
        }
	    String[] array = ids.split(";"); 
	    
        for (String param : array) 
        {
            String[] delParam = param.split("_");
            
            if (delParam.length != 3)
            {
                continue;
            }
            
            jmsgUserSign.setId(delParam[0]);
            jmsgUserSign.getUser().setId(delParam[1]);
            jmsgUserSign.setSign(delParam[2]);
            
            jmsgUserSignService.delete(jmsgUserSign);
        }
        
        addMessage(redirectAttributes, "删除用户签名成功");
        return "redirect:"+Global.getAdminPath()+"/sms/jmsgUserSign/?repage";
    }

}