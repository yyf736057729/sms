/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.utils.excel.ExcelReaderUtil;
import com.siloyou.core.common.utils.excel.ExportExcel;
import com.siloyou.core.common.utils.excel.IRowReader;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgDeliverNumber;
import com.siloyou.jmsg.modules.sms.service.JmsgDeliverNumberService;
import com.siloyou.jmsg.modules.sms.service.excel.DeliverNumberReader;

/**
 * 用户上行接入号Controller
 * @author zhukc
 * @version 2016-08-14
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgDeliverNumber")
public class JmsgDeliverNumberController extends BaseController {

	@Autowired
	private JmsgDeliverNumberService jmsgDeliverNumberService;
	
	@ModelAttribute
	public JmsgDeliverNumber get(@RequestParam(required=false) String id) {
		JmsgDeliverNumber entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgDeliverNumberService.get(id);
		}
		if (entity == null){
			entity = new JmsgDeliverNumber();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgDeliverNumber:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgDeliverNumber jmsgDeliverNumber, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		Page<JmsgDeliverNumber> page = jmsgDeliverNumberService.findPage(new Page<JmsgDeliverNumber>(request, response), jmsgDeliverNumber); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgDeliverNumberList";
	}

	@RequiresPermissions("sms:jmsgDeliverNumber:view")
	@RequestMapping(value = "form")
	public String form(JmsgDeliverNumber jmsgDeliverNumber, Model model) {
		model.addAttribute("jmsgDeliverNumber", jmsgDeliverNumber);
		return "modules/sms/jmsgDeliverNumberForm";
	}
	
	@RequiresPermissions("sms:jmsgDeliverNumber:view")
	@RequestMapping(value = "config")
	public String config(JmsgDeliverNumber jmsgDeliverNumber, Model model) {
		model.addAttribute("jmsgDeliverNumber", jmsgDeliverNumber);
		return "modules/sms/jmsgDeliverNumberConfig";
	}

	@RequiresPermissions("sms:jmsgDeliverNumber:edit")
	@RequestMapping(value = "save")
	public String save(JmsgDeliverNumber jmsgDeliverNumber, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgDeliverNumber)){
			return form(jmsgDeliverNumber, model);
		}
		String msg = "保存用户上行接入号成功";
		int count = jmsgDeliverNumberService.userCount(jmsgDeliverNumber);
		if(count >0){
			msg="接入号已经存在";
		}else{
			jmsgDeliverNumberService.save(jmsgDeliverNumber);
		}
		addMessage(redirectAttributes, msg);
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgDeliverNumber/?repage";
	}
	
	@RequiresPermissions("sms:jmsgDeliverNumber:edit")
	@RequestMapping(value = "batchSave")
	public String batchSave(JmsgDeliverNumber jmsgDeliverNumber, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgDeliverNumber)){
			return form(jmsgDeliverNumber, model);
		}
		StringBuilder msgSb = new StringBuilder();
		for (JmsgDeliverNumber param : jmsgDeliverNumber.getDeliverNumber())
		{
			param.setUser(jmsgDeliverNumber.getUser());
			param.setSpNumber(param.getSpNumber() + param.getExtNumber());
			if (param.getIsNewRecord() && StringUtils.isNotBlank(param.getSpNumber()))
			{
				int count = jmsgDeliverNumberService.userCount(param);
				if(count > 0){
					msgSb.append("保存用户上行接入号失败，用户: " + param.getUser().getName() + " ， 接入号: " + param.getSpNumber() + " 已经存在").append("</br>");
				}
				else
				{
					jmsgDeliverNumberService.save(param);
				}
			}
		}
		
		if (StringUtils.isNotBlank(msgSb.toString()))
		{
			addMessage(redirectAttributes, msgSb.toString());
		}
		else
		{
			addMessage(redirectAttributes, "保存用户上行接入号成功");
		}
		
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgDeliverNumber/?repage";
	}
	
	@RequiresPermissions("sms:jmsgDeliverNumber:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgDeliverNumber jmsgDeliverNumber, RedirectAttributes redirectAttributes) {
		jmsgDeliverNumberService.delete(jmsgDeliverNumber);
		addMessage(redirectAttributes, "删除用户上行接入号成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgDeliverNumber/?repage";
	}
	
	 /**
     * 导入通道签名数据
     * @param file
     * @param redirectAttributes
     * @return
     */
    @RequiresPermissions("sms:jmsgDeliverNumber:edit")
    @RequestMapping(value = "import", method = RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
    	String msg ="";
    	try {
            int successNum = 0;
            int errorNum = 0;
            int errUser = 0;
            int errCf = 0;
            Set<String> uSet = Sets.newHashSet();
            Set<String> cfSet = Sets.newHashSet();
            List<JmsgDeliverNumber> list = Lists.newArrayList();
            String fileType = "";
            String sourceName = file.getOriginalFilename();
	        if(sourceName.toLowerCase().endsWith(ExcelReaderUtil.EXCEL03_EXTENSION)){
	        	fileType = ExcelReaderUtil.EXCEL03_EXTENSION;
	        }
	        if(sourceName.toLowerCase().endsWith(ExcelReaderUtil.EXCEL07_EXTENSION)){
	        	fileType = ExcelReaderUtil.EXCEL07_EXTENSION;
	        }
            if(StringUtils.isNotBlank(fileType)){
	            IRowReader reader = new DeliverNumberReader(list);
	            ExcelReaderUtil.readExcel(reader, file.getInputStream(), fileType);
	            for (JmsgDeliverNumber tmp : list) {
	                if(StringUtils.isNotBlank(tmp.getUserId()) && StringUtils.isNotBlank(tmp.getSpNumber())){
	                	User user = UserUtils.get(tmp.getUserId());
	                	if(user == null){//验证用户是否存在
	                		errUser++;
	                		uSet.add(tmp.getUserId());
	                		continue;
	                	}
	                	int count = jmsgDeliverNumberService.userCount(tmp);
	    				if(count > 0){
	    					errCf++;
	    					cfSet.add(tmp.getSpNumber());
	    					continue;
	    				}
	    				successNum++;
	    				tmp.setUser(user);
	    				jmsgDeliverNumberService.save(tmp);
	                }else{
	                	errorNum++;
	                }
	            }
	            msg = "已成功导入 " + successNum + " 条数据，异常 " + (errorNum+errUser+errCf) + " 条。";
	            if(uSet != null && uSet.size() > 0){
	            	msg=msg+"</br>用户ID无效错误个数"+errUser+"，无效ID:";
	            	String ids = "";
	            	for (String id: uSet) {
	            		ids+=id+";";
					}
	            	msg=msg+ids;
	            }
	            if(cfSet != null && cfSet.size() > 0){
	            	msg=msg+"</br>接入号已存在错误个数"+errCf+"，接入号:";
	            	String number = "";
	            	for (String id: cfSet) {
	            		number+=id+";";
					}
	            	msg=msg+number;
	            }
            }else{
            	msg = "文件格式不对,只支持xls,xlsx";
            }
           
        }catch (Exception e){
           msg = "导入用户上行配置失败！";
        }
        
        addMessage(redirectAttributes, msg);
        return "redirect:"+Global.getAdminPath()+"/sms/jmsgDeliverNumber/?repage";
    }
    
    /**
     * 下载导入模板
     * @param response
     * @param redirectAttributes
     * @return
     * @see [类、类#方法、类#成员]
     */
    @RequiresPermissions("sms:jmsgDeliverNumber:edit")
    @RequestMapping(value = "import/template")
    public String downloadFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            String fileName = "用户上行配置导入模板.xlsx";
            List<JmsgDeliverNumber> list = new ArrayList<JmsgDeliverNumber>();
            JmsgDeliverNumber entity = new JmsgDeliverNumber();
            entity.setUserId("3881");
            entity.setSpNumber("1068888114");
            list.add(entity);
            new ExportExcel("", JmsgDeliverNumber.class, 2).setDataList(list).write(response, fileName).dispose();
            return null;
        } catch (IOException e) {
            addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
        }
        return "redirect:" + adminPath + "/sms/jmsgDeliverNumber/?repage";
    }

}