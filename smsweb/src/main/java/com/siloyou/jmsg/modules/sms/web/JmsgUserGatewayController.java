/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.common.utils.HttpRequest;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.jmsg.modules.Result;
import com.siloyou.jmsg.modules.sms.entity.JmsgUserGateway;
import com.siloyou.jmsg.modules.sms.service.JmsgUserGatewayService;

/**
 * 用户通道Controller
 * @author zhukc
 * @version 2016-08-28
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgUserGateway")
public class JmsgUserGatewayController extends BaseController {

	@Autowired
	private JmsgUserGatewayService jmsgUserGatewayService;
	
	@ModelAttribute
	public JmsgUserGateway get(@RequestParam(required=false) String id) {
		JmsgUserGateway entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgUserGatewayService.get(id);
		}
		if (entity == null){
			entity = new JmsgUserGateway();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgUserGateway:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgUserGateway jmsgUserGateway, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgUserGateway> page = jmsgUserGatewayService.findPage(new Page<JmsgUserGateway>(request, response), jmsgUserGateway); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgUserGatewayList";
	}

	@RequiresPermissions("sms:jmsgUserGateway:view")
	@RequestMapping(value = "form")
	public String form(JmsgUserGateway jmsgUserGateway, Model model) {
		model.addAttribute("jmsgUserGateway", jmsgUserGateway);
		return "modules/sms/jmsgUserGatewayForm";
	}

	@RequiresPermissions("sms:jmsgUserGateway:edit")
	@RequestMapping(value = "save")
	public String save(JmsgUserGateway jmsgUserGateway, String oldUsername,Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgUserGateway)){
			return form(jmsgUserGateway, model);
		}
		if (!"true".equals(checkName(oldUsername, jmsgUserGateway.getUsername()))){
			addMessage(model, "保存用户通道'" + jmsgUserGateway.getUsername() + "'失败，用户名称已存在");
			return form(jmsgUserGateway, model);
		}
		jmsgUserGatewayService.save(jmsgUserGateway);
		addMessage(redirectAttributes, "保存用户通道成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgUserGateway/?repage";
	}
	
	@RequiresPermissions("sms:jmsgUserGateway:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgUserGateway jmsgUserGateway, RedirectAttributes redirectAttributes) {
		jmsgUserGatewayService.delete(jmsgUserGateway);
		addMessage(redirectAttributes, "删除用户通道成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgUserGateway/?repage";
	}
	
	/**
	 * 验证登录名是否有效
	 * @param oldLoginName
	 * @param loginName
	 * @return
	 */
	@ResponseBody
	@RequiresPermissions("sms:jmsgUserGateway:edit")
	@RequestMapping(value = "checkName")
	public String checkName(String oldLoginName, String loginName) {
		if (loginName !=null && loginName.equals(oldLoginName)) {
			return "true";
		} else if (loginName !=null && jmsgUserGatewayService.getUserGatewayByUsername(loginName) == null) {
			return "true";
		}
		return "false";
	}
	
	/**
     * 通道关闭
     * @param jmsgGatewayInfo
     * @param gatewayId
     * @param redirectAttributes
     * @return
     * @see [类、类#方法、类#成员]
     */
    @RequiresPermissions("sms:jmsgUserGateway:edit")
    @RequestMapping(value = "close")
    public String gateWayClose(JmsgUserGateway jmsgUserGateway, String userid, RedirectAttributes redirectAttributes) 
    {
        jmsgUserGateway = jmsgUserGatewayService.getUserGatewayByUserid(userid);
        if (null != jmsgUserGateway && StringUtils.isNotBlank(jmsgUserGateway.getAppHost()) && StringUtils.isNotBlank(jmsgUserGateway.getAppCode()))
        {
            StringBuffer strBuf = new StringBuffer();
            strBuf.append("http://")
                .append(jmsgUserGateway.getAppHost())
                .append(":")
                .append(jmsgUserGateway.getAppCode())
                .append("/api/cmpp/gateway/close?userid=")
                .append(userid);
            
            Result result = JSON.parseObject(HttpRequest.sendPost(strBuf.toString(), null, null), Result.class);
            
            if (result.isSuccess())
            {
                // 网关运行状态 1：运行 0 ：停止
                jmsgUserGateway.setStatus("0");
                jmsgUserGatewayService.updateStateById(jmsgUserGateway);
                addMessage(redirectAttributes, "通道关闭成功");
            }
            else
            {
                addMessage(redirectAttributes, result.getErrorMsg());
            }
        }
        else
        {
            addMessage(redirectAttributes, "通道关闭失败！通道不存在或通道应用配置错误。");
        }
        return "redirect:"+Global.getAdminPath()+"/sms/jmsgUserGateway/?repage";
    }

    /**
     * 通道开启
     * @param jmsgGatewayInfo
     * @param gatewayId
     * @param redirectAttributes
     * @return
     * @see [类、类#方法、类#成员]
     */
    @RequiresPermissions("sms:jmsgUserGateway:edit")
    @RequestMapping(value = "open")
    public String gateWayOpen(JmsgUserGateway jmsgUserGateway, String userid, RedirectAttributes redirectAttributes) {
        
        jmsgUserGateway = jmsgUserGatewayService.getUserGatewayByUserid(userid);
        if (null != jmsgUserGateway && StringUtils.isNotBlank(jmsgUserGateway.getAppHost()) && StringUtils.isNotBlank(jmsgUserGateway.getAppCode()))
        {
            StringBuffer strBuf = new StringBuffer();
            strBuf.append("http://")
                .append(jmsgUserGateway.getAppHost())
                .append(":")
                .append(jmsgUserGateway.getAppCode())
                .append("/api/cmpp/gateway/open?userid=")
                .append(userid);
            
            Result result = JSON.parseObject(HttpRequest.sendPost(strBuf.toString(), null, null), Result.class);
            
            if (result.isSuccess())
            {
                // 网关运行状态 1：运行 0 ：停止
                jmsgUserGateway.setStatus("1");
                jmsgUserGatewayService.updateStateById(jmsgUserGateway);
                addMessage(redirectAttributes, "通道开启成功");
            }
            else
            {
                addMessage(redirectAttributes, result.getErrorMsg());
            }
        }
        else
        {
            addMessage(redirectAttributes, "通道开启失败！通道不存在或通道应用配置错误。");
        }
        return "redirect:"+Global.getAdminPath()+"/sms/jmsgUserGateway/?repage";
    }
}