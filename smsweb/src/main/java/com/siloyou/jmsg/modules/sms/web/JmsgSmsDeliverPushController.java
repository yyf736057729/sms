/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.formula.functions.T;
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
import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.FileUtils;
import com.siloyou.core.common.utils.IdGen;
import com.siloyou.core.common.utils.PowerUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.utils.excel.CsvFileWriter;
import com.siloyou.core.common.utils.excel.ExportExcel;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDeliverPush;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsDeliverPushService;

/**
 * 上行推送信息Controller
 * @author zhukc
 * @version 2016-08-14
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgSmsDeliverPush")
public class JmsgSmsDeliverPushController extends BaseController {

	@Autowired
	private JmsgSmsDeliverPushService jmsgSmsDeliverPushService;
	
	@ModelAttribute
	public JmsgSmsDeliverPush get(@RequestParam(required=false) String id) {
		JmsgSmsDeliverPush entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgSmsDeliverPushService.get(id);
		}
		if (entity == null){
			entity = new JmsgSmsDeliverPush();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgSmsDeliverPush:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgSmsDeliverPush jmsgSmsDeliverPush, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(PowerUtils.adminFlag()){
		}else{
			jmsgSmsDeliverPush.setUser(UserUtils.getUser());
		}
		Page<JmsgSmsDeliverPush> page = jmsgSmsDeliverPushService.findPage(new Page<JmsgSmsDeliverPush>(request, response), jmsgSmsDeliverPush); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsDeliverPushList";
	}
	
	@RequiresPermissions("sms:jmsgSmsDeliverPush:view")
	@RequestMapping(value = "listV2")
	public String listV2(JmsgSmsDeliverPush jmsgSmsDeliverPush, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgSmsDeliverPush> page = jmsgSmsDeliverPushService.findPageNew(new Page<JmsgSmsDeliverPush>(request, response), jmsgSmsDeliverPush); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgSmsDeliverListV2";
	}

	@RequiresPermissions("sms:jmsgSmsDeliverPush:view")
	@RequestMapping(value = "form")
	public String form(JmsgSmsDeliverPush jmsgSmsDeliverPush, Model model) {
		model.addAttribute("jmsgSmsDeliverPush", jmsgSmsDeliverPush);
		return "modules/sms/jmsgSmsDeliverPushForm";
	}

	@RequiresPermissions("sms:jmsgSmsDeliverPush:edit")
	@RequestMapping(value = "save")
	public String save(JmsgSmsDeliverPush jmsgSmsDeliverPush, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgSmsDeliverPush)){
			return form(jmsgSmsDeliverPush, model);
		}
		jmsgSmsDeliverPushService.save(jmsgSmsDeliverPush);
		addMessage(redirectAttributes, "保存上行推送信息成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsDeliverPush/?repage";
	}
	
	@RequiresPermissions("sms:jmsgSmsDeliverPush:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgSmsDeliverPush jmsgSmsDeliverPush, RedirectAttributes redirectAttributes) {
		jmsgSmsDeliverPushService.delete(jmsgSmsDeliverPush);
		addMessage(redirectAttributes, "删除上行推送信息成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgSmsDeliverPush/?repage";
	}
	
    @RequiresPermissions("sms:jmsgSmsDeliverPush:view")
    @RequestMapping(value = "exportOld")
    public String exportFile(JmsgSmsDeliverPush jmsgSmsDeliverPush, HttpServletRequest request,
        HttpServletResponse response, Model model)
    {
        try
        {
            String fileName = "用户上行短信_"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            List<JmsgSmsDeliverPush> page =
                jmsgSmsDeliverPushService.findList(jmsgSmsDeliverPush);
            new ExportExcel("用户上行短信", JmsgSmsDeliverPush.class).setDataList(page).write(response, fileName).dispose();
            return null;
        }
        catch (IOException e)
        {
            addMessage(model, "导出用户上行短信失败！失败信息："+e.getMessage());
        }
        
        return "modules/sms/jmsgSmsDeliverPushList";
    }
    
    @RequiresPermissions("sms:jmsgSmsDeliverPush:view")
    @RequestMapping(value = "export")
    public String exportFile1(JmsgSmsDeliverPush jmsgSmsDeliverPush, HttpServletRequest request,HttpServletResponse response, Model model){
    	if(PowerUtils.adminFlag()){
		}else{
			jmsgSmsDeliverPush.setUser(UserUtils.getUser());
		}
    	String uuid = IdGen.uuid();
    	String fileName = uuid+".csv";
    	CsvFileWriter<T> csvFileWriter = new CsvWriterDeliverPush(uuid,".csv", jmsgSmsDeliverPush);
    	OutputStream out = null;
    	FileInputStream in = null;
    	File export = null;
    	File srcFile = null;
    	try {
			String filePath = csvFileWriter.execute();
			String descFileName = Global.getConfig("smsTask.phoneList.dir")+File.separator+uuid+".zip";
			FileUtils.zipFiles(Global.getConfig("smsTask.phoneList.dir"), fileName, descFileName);
			export = new File(descFileName);
			srcFile = new File(filePath);
	        out = response.getOutputStream();  
	  
	        byte[] buffer = new byte[1024];  
	        int len = 0;  
	        response.setContentType("application/zip;charset=UTF-8");  
	        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("用户上行短信_"+DateUtils.getDate("yyyyMMddHHmmss")+".zip", "UTF-8"));  
	        response.setCharacterEncoding("UTF-8");  
	        in = new FileInputStream(export);  
	  
	        while ((len = in.read(buffer)) > 0) {  
	            out.write(buffer, 0, len);  
	        }  
		} catch (Exception e) {
			addMessage(model, "导出用户上行短信失败！失败信息："+e.getMessage());
		} finally{
			try{
				if(out != null){
					out.flush();
					out.close();
				}
				if(in != null){
					in.close();
				}
				if(export != null){// 删除原文件  
					export.delete();
				}
				if(srcFile != null){// 删除原文件
					srcFile.delete();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
        return null;
    }
    
    @RequiresPermissions("sms:jmsgSmsDeliverPush:view")
    @RequestMapping(value = "exportV2")
    public String exportFileV2(JmsgSmsDeliverPush jmsgSmsDeliverPush, HttpServletRequest request,
        HttpServletResponse response, Model model)
    {
        try
        {
            String fileName = "用户上行短信_"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            List<JmsgSmsDeliverPush> page =
                jmsgSmsDeliverPushService.findListV2(jmsgSmsDeliverPush);
            new ExportExcel("用户上行短信", JmsgSmsDeliverPush.class).setDataList(page).write(response, fileName).dispose();
            return null;
        }
        catch (IOException e)
        {
            addMessage(model, "导出用户上行短信失败！失败信息："+e.getMessage());
        }
        
        return "modules/sms/jmsgSmsDeliverListV2";
    }
}