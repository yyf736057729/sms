/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import java.util.List;

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
import com.siloyou.core.common.utils.HttpRequest;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.jmsg.common.utils.GatewayUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayInfo;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayMonitor;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsWarn;
import com.siloyou.jmsg.modules.sms.service.JmsgGatewayMonitorService;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsWarnService;

/**
 * 告警表Controller
 * @author zj
 * @version 2016-10-15
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgSmsWarn")
public class JmsgSmsWarnController extends BaseController {

	@Autowired
	private JmsgSmsWarnService jmsgSmsWarnService;
	
	@Autowired
	private JmsgGatewayMonitorService jmsgGatewayMonitorService;
	
	@ModelAttribute
	public JmsgSmsWarn get(@RequestParam(required=false) String id) {
		JmsgSmsWarn entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgSmsWarnService.get(id);
		}
		if (entity == null){
			entity = new JmsgSmsWarn();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgSmsWarn:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgSmsWarn jmsgSmsWarn, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgSmsWarn> page = jmsgSmsWarnService.findPage(new Page<JmsgSmsWarn>(request, response), jmsgSmsWarn); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsWarnList";
	}
	
	@RequiresPermissions("sms:jmsgSmsWarn:view")
    @RequestMapping(value = {"warnList", ""})
    public String warnList(JmsgSmsWarn jmsgSmsWarn, HttpServletRequest request, HttpServletResponse response, Model model) {
        //Page<JmsgSmsWarn> page = jmsgSmsWarnService.findPage(new Page<JmsgSmsWarn>(request, response), jmsgSmsWarn); 
	    jmsgSmsWarn.setWarnStatus("0");    //未处理
        List<JmsgSmsWarn> list = jmsgSmsWarnService.findList(jmsgSmsWarn);
        Page<JmsgGatewayMonitor> page = jmsgGatewayMonitorService.findPage(new Page<JmsgGatewayMonitor>(request, response), new JmsgGatewayMonitor());
        
        for (JmsgGatewayMonitor jmsgGatewayMonitor : page.getList())
        {
            jmsgGatewayMonitor.setGatewayStatus(getGatewayStatus(jmsgGatewayMonitor.getGatewayId()));
        }
        model.addAttribute("page", page);
        model.addAttribute("list", list);
        return "modules/sms/jmsgSmsWarnList";
    }

	@RequiresPermissions("sms:jmsgSmsWarn:view")
	@RequestMapping(value = "form")
	public String form(JmsgSmsWarn jmsgSmsWarn, Model model) {
		model.addAttribute("jmsgSmsWarn", jmsgSmsWarn);
		return "modules/sms/jmsgSmsWarnForm";
	}

	@RequiresPermissions("sms:jmsgSmsWarn:edit")
	@RequestMapping(value = "save")
	public String save(JmsgSmsWarn jmsgSmsWarn, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgSmsWarn)){
			return form(jmsgSmsWarn, model);
		}
		jmsgSmsWarnService.save(jmsgSmsWarn);
		addMessage(redirectAttributes, "保存告警表成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsWarn/?repage";
	}
	
	@RequiresPermissions("sms:jmsgSmsWarn:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgSmsWarn jmsgSmsWarn, RedirectAttributes redirectAttributes) {
		jmsgSmsWarnService.delete(jmsgSmsWarn);
		addMessage(redirectAttributes, "删除告警表成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsWarn/?repage";
	}
	
	/**
     * 根据ID修改告警处理状态 
     * @param map
     * @see [类、类#方法、类#成员]
     */
	@RequiresPermissions("sms:jmsgSmsWarn:edit")
    @RequestMapping(value = "updateStatus")
    public String updateStatusById(JmsgSmsWarn jmsgSmsWarn)
    {
	    jmsgSmsWarnService.updateStatusById(jmsgSmsWarn);
	    return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsWarn/warnList?repage";
    }
	
	/**
	 * 调用接口获取网关状态
	 * @param gatewayId
	 * @see [类、类#方法、类#成员]
	 */
	private String getGatewayStatus(String gatewayId)
	{
	    JmsgGatewayInfo entity = GatewayUtils.getGatewayInfo(gatewayId);
        if (null != entity && StringUtils.isNotBlank(entity.getAppHost()) && StringUtils.isNotBlank(entity.getAppCode()))
        {
            StringBuffer strBuf = new StringBuffer();
            strBuf.append("http://")
                .append(entity.getAppHost())
                .append(":")
                .append(entity.getAppCode())
                .append("/api/sms/gateway/status?id=")
                .append(gatewayId);
            
            String result = HttpRequest.sendPost(strBuf.toString(), null, null);
            //String jsonParams = Encodes.unescapeHtml(HttpRequest.sendPost(strBuf.toString(), null, null));
            
            if (StringUtils.equals(result, "true"))
            {
                return "1";
            }
        }
        
        return "0";
	}

}