/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.sanerzone.common.support.sequence.MsgId;
import com.sanerzone.common.support.utils.Encodes;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.utils.excel.ExportExcel;
import com.siloyou.core.common.utils.excel.ImportExcel;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.utils.GatewayUtils;
import com.siloyou.jmsg.common.utils.MQUtils;
import com.siloyou.jmsg.common.utils.MsgIdUtits;
import com.siloyou.jmsg.common.utils.PhoneUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayInfo;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewaySign;
import com.siloyou.jmsg.modules.sms.service.JmsgGatewaySignService;

/**
 * 通道签名Controller
 * @author zhukc
 * @version 2016-07-29
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgGatewaySign")
public class JmsgGatewaySignController extends BaseController {

	@Autowired
	private JmsgGatewaySignService jmsgGatewaySignService;
	
	@Autowired
	public MQUtils mQUtils;
	
	@ModelAttribute
	public JmsgGatewaySign get(@RequestParam(required=false) String id) {
		JmsgGatewaySign entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgGatewaySignService.get(id);
		}
		if (entity == null){
			entity = new JmsgGatewaySign();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgGatewaySign:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgGatewaySign jmsgGatewaySign, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		Page<JmsgGatewaySign> pageParam = new Page<JmsgGatewaySign>(request, response);
		pageParam.setPageSize(100);
		Page<JmsgGatewaySign> page = jmsgGatewaySignService.findPage(pageParam, jmsgGatewaySign); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgGatewaySignList";
	}
	
	@RequiresPermissions("sms:jmsgGatewaySign:view")
    @RequestMapping(value = "toUserGatewayList")
    public String toUserGatewayList(JmsgGatewaySign jmsgGatewaySign, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<JmsgGatewaySign> page = jmsgGatewaySignService.getUserGatewaySingList(new Page<JmsgGatewaySign>(request, response), jmsgGatewaySign); 
        model.addAttribute("page", page);
        return "modules/sms/jmsgUserGatewaySignList";
    }

	@RequiresPermissions("sms:jmsgGatewaySign:view")
	@RequestMapping(value = "form")
	public String form(JmsgGatewaySign jmsgGatewaySign, Model model) {
		model.addAttribute("jmsgGatewaySign", jmsgGatewaySign);
		return "modules/sms/jmsgGatewaySignForm";
	}
	
	@RequiresPermissions("sms:jmsgGatewaySign:view")
	@RequestMapping(value = "config")
	public String config(JmsgGatewaySign jmsgGatewaySign, Model model) {
		model.addAttribute("jmsgGatewaySign", jmsgGatewaySign);
		return "modules/sms/jmsgGatewaySignConfig";
	}

	@RequiresPermissions("sms:jmsgGatewaySign:edit")
	@RequestMapping(value = "save")
	public String save(JmsgGatewaySign jmsgGatewaySign, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgGatewaySign)){
			return form(jmsgGatewaySign, model);
		}
		
		//新增
		if (jmsgGatewaySign.getIsNewRecord())
		{
			JmsgGatewaySign entity = jmsgGatewaySignService.getByParam(jmsgGatewaySign);
			
			if (entity != null){
				addMessage(model, "操作失败，通道签名已经存在");
				return form(jmsgGatewaySign, model);
			}
		}
		
		//jmsgGatewaySign.setSpNumber(jmsgGatewaySign.getSpNumber() + jmsgGatewaySign.getExtNumber());
		jmsgGatewaySign.setSpNumber(jmsgGatewaySign.getExtNumber());
		jmsgGatewaySignService.save(jmsgGatewaySign);
		addMessage(redirectAttributes, "保存通道签名成功");
		//return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewaySign/form";
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewaySign/list";
	}
	
	@RequiresPermissions("sms:jmsgGatewaySign:edit")
	@RequestMapping(value = "batchSave")
	public String batchSave(JmsgGatewaySign jmsgGatewaySign, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgGatewaySign)){
			return form(jmsgGatewaySign, model);
		}
		
		StringBuilder msgSb = new StringBuilder();
		for (JmsgGatewaySign gatewaySign : jmsgGatewaySign.getGatewaySignList())
		{
			gatewaySign.setUser(jmsgGatewaySign.getUser());
			gatewaySign.setSpNumber(gatewaySign.getExtNumber());
			gatewaySign.setSign(Encodes.unescapeHtml(gatewaySign.getSign()));
			
			//新增
			if (gatewaySign.getIsNewRecord() && StringUtils.isNotBlank(gatewaySign.getSpNumber()))
			{
				JmsgGatewaySign entity = jmsgGatewaySignService.getByParam(gatewaySign);
				
				if (entity != null){
					msgSb.append("保存通道签名失败，通道: " + entity.getGatewayName() + " ， 签名: " + entity.getSign() + " 已经存在").append("</br>");
				}
				else
				{
					jmsgGatewaySignService.save(gatewaySign);
				}
			}
		}
		
		if (StringUtils.isNotBlank(msgSb.toString()))
		{
			addMessage(redirectAttributes, msgSb.toString());
		}
		else
		{
			addMessage(redirectAttributes, "保存通道签名成功");
		}
		
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewaySign/list";
	}
	
	@RequiresPermissions("sms:jmsgGatewaySign:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgGatewaySign jmsgGatewaySign, String tmpSign, String tmpGatewayId, String userId,RedirectAttributes redirectAttributes) {
	    try
        {
            jmsgGatewaySign.setSign(URLDecoder.decode(tmpSign, "UTF-8"));
            jmsgGatewaySign.setGatewayId(tmpGatewayId);
            jmsgGatewaySign.setUser(new User(userId));
        }
        catch (UnsupportedEncodingException e)
        {
            addMessage(redirectAttributes, e.toString());
        }
		jmsgGatewaySignService.delete(jmsgGatewaySign);
		addMessage(redirectAttributes, "删除通道签名成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewaySign/?repage";
	}
	
	@RequiresPermissions("sms:jmsgGatewaySign:edit")
    @RequestMapping(value = "batchDelete")
    public String batchDelete(JmsgGatewaySign jmsgGatewaySign, String ids, RedirectAttributes redirectAttributes) {
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
            
            if (delParam.length != 4)
            {
                continue;
            }
            
            jmsgGatewaySign.setId(delParam[0]);
            jmsgGatewaySign.setUser(new User(delParam[1]));
            jmsgGatewaySign.setGatewayId(delParam[2]);
            jmsgGatewaySign.setSign(delParam[3]);
            
            jmsgGatewaySignService.delete(jmsgGatewaySign);
        }
        
        addMessage(redirectAttributes, "删除通道签名成功");
        return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewaySign/?repage";
    }
	
    /**
     * 导入通道签名数据
     * @param file
     * @param redirectAttributes
     * @return
     */
    @RequiresPermissions("sms:jmsgGatewaySign:edit")
    @RequestMapping(value = "import", method = RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes)
    {
        if (Global.isDemoMode())
        {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sms/jmsgGatewaySign/list?repage";
        }
        try
        {
            int successNum = 0;
            int errorNum = 0;
            
            ImportExcel ei = new ImportExcel(file, 1, 0);
            
            List<JmsgGatewaySign> list = ei.getDataList(JmsgGatewaySign.class);
            
            JmsgGatewaySign jmsgGatewaySign = null;
            
            for (JmsgGatewaySign tmp : list)
            {
                jmsgGatewaySign = new JmsgGatewaySign();
                jmsgGatewaySign.setGatewayId(tmp.getGatewayId());
                jmsgGatewaySign.setSign(tmp.getSign());
                jmsgGatewaySign.setSpNumber(tmp.getSpNumber());
                jmsgGatewaySign.setUser(new User(tmp.getUserId()));
                jmsgGatewaySign.setNote(tmp.getNote());
                //jmsgGatewaySign.getUser().setId(tmp.getUserId());
                
                // 判断通道是否存在
                JmsgGatewayInfo jmsgGatewayInfo = GatewayUtils.getGatewayInfo(jmsgGatewaySign.getGatewayId());
                
                if (null != jmsgGatewayInfo)
                {
                    jmsgGatewaySign.setGatewayName(jmsgGatewayInfo.getGatewayName());
                    
                    // 先判断：用户ID + 通道 + 签名，是否存在，存在先删除
                    List<JmsgGatewaySign> jmsgGatewaySignList = jmsgGatewaySignService.getUserGatewaySign(jmsgGatewaySign);
                    
                    if (null != jmsgGatewaySignList && jmsgGatewaySignList.size() > 0)
                    {
                        for (JmsgGatewaySign jgs : jmsgGatewaySignList)
                        {
                            jmsgGatewaySignService.delete(jgs);
                        }
                    }
                    
                    // 再根据通道ID、签名、接入号，判断接入号的唯一
                    JmsgGatewaySign entity = jmsgGatewaySignService.getByParam(jmsgGatewaySign);
                    
                    // 已经存在，覆盖
                    if (entity != null)
                    {
                        jmsgGatewaySignService.delete(entity);
                        jmsgGatewaySignService.save(jmsgGatewaySign);
                    }
                    else
                    {
                        successNum ++;
                        
                        jmsgGatewaySignService.save(jmsgGatewaySign);
                    }
                }
                else
                {
                    errorNum ++;
                }
            }
            
            addMessage(redirectAttributes, "已成功导入 " + successNum + " 条通道签名，异常 " + errorNum + " 条。");
        }
        catch (Exception e)
        {
            addMessage(redirectAttributes, "导入通道签名失败！失败信息：" + e.getMessage());
        }
        
        return "redirect:" + adminPath + "/sms/jmsgGatewaySign/list?repage";
    }
    
    /**
     * 导入通道签名数据（txt格式）
     * @param file
     * @param redirectAttributes
     * @return
     */
    @RequiresPermissions("sms:jmsgGatewaySign:edit")
    @RequestMapping(value = "importTxtFile", method = RequestMethod.POST)
    public String importTxtFile(MultipartFile file, RedirectAttributes redirectAttributes)
    {
        if (Global.isDemoMode())
        {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sms/jmsgGatewaySign/list?repage";
        }
        try
        {
            int successNum = 0;
            int repeatNum = 0;
            int errorNum = 0;
            
            List<JmsgGatewaySign> jmsgGatewaySignList = new ArrayList<JmsgGatewaySign>();
            JmsgGatewaySign jmsgGatewaySign = null;
            
            if (StringUtils.isNotBlank(file.getName()))
            {
                //InputStreamReader reader = new InputStreamReader(file.getInputStream(), "UTF-8");
                //InputStreamReader reader = new InputStreamReader(file.getInputStream(), codeString(file));
                InputStreamReader reader = new InputStreamReader(file.getInputStream(), getFileCharset(file));
                BufferedReader br = new BufferedReader(reader);
                for (String line = br.readLine(); line != null; line = br.readLine())
                {
                    if (StringUtils.isNotBlank(line))
                    {
                        // 格式：通道编号，通道名称，接入号，签名
                        String[] strValues = line.split(",");
                        
                        if (strValues.length != 3)
                        {
                            errorNum ++;
                            continue;
                        }
                        
                        jmsgGatewaySign = new JmsgGatewaySign();
                        jmsgGatewaySign.setGatewayId(strValues[0]);
                        jmsgGatewaySign.setSpNumber(strValues[1]);
                        jmsgGatewaySign.setSign(strValues[2]);
                        
                        // 判断通道是否存在
                        JmsgGatewayInfo jmsgGatewayInfo = GatewayUtils.getGatewayInfo(jmsgGatewaySign.getGatewayId());
                        
                        if (null != jmsgGatewayInfo)
                        {
                            jmsgGatewaySign.setGatewayName(jmsgGatewayInfo.getGatewayName());
                            
                            JmsgGatewaySign entity = jmsgGatewaySignService.getByParam(jmsgGatewaySign);
                            
                            // 已经存在，重复
                            if (entity != null)
                            {
                                repeatNum ++;
                            }
                            else
                            {
                                successNum ++;
                                
                                jmsgGatewaySignList.add(jmsgGatewaySign);
                                
                                jmsgGatewaySignService.save(jmsgGatewaySign);
                            }
                        }
                        else
                        {
                            errorNum ++;
                        }
                    }
                }
            }
            
            addMessage(redirectAttributes, "已成功导入 " + successNum + " 条通道签名，重复 " + repeatNum + " 条，异常 " + errorNum + " 条。");
        }
        catch (Exception e)
        {
            addMessage(redirectAttributes, "导入通道签名失败！失败信息：" + e.getMessage());
        }
        
        return "redirect:" + adminPath + "/sms/jmsgGatewaySign/list?repage";
    }
    
    /**
     * 下载导入模板
     * @param response
     * @param redirectAttributes
     * @return
     * @see [类、类#方法、类#成员]
     */
    @RequiresPermissions("sms:jmsgGatewaySign:view")
    @RequestMapping(value = "import/template")
    public String downloadFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) 
    {
        try
        {
            String fileName = "通道签名导入模板.xlsx";
            List<JmsgGatewaySign> list = new ArrayList<JmsgGatewaySign>();
            new ExportExcel("通道签名", JmsgGatewaySign.class, 2).setDataList(list).write(response, fileName).dispose();
            return null;
        }
        catch (IOException e)
        {
            addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
        }
        return "redirect:" + adminPath + "/sms/jmsgGatewaySign/list?repage";
    }
    
    /** 
     * 判断文件的编码格式 
     * @param fileName :file 
     * @return 文件编码格式 
     * @throws Exception 
     */
    public static String codeString(MultipartFile file)
        throws Exception
    {
        BufferedInputStream bin = new BufferedInputStream(file.getInputStream());
        int p = (bin.read() << 8) + bin.read();
        
        String code = null;
        
        switch (p)
        {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }
        
        return code;
    }
    
    private static String getFileCharset(MultipartFile file)
    {
        String charset = "GBK";
        
        byte[] first3Bytes = new byte[3];
        
        try
        {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(file.getInputStream());
            
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            
            if (read == -1)
            {
                // 文件编码为 ANSI
                return charset;
            }
            else if (first3Bytes[0] == (byte)0xFF && first3Bytes[1] == (byte)0xFE)
            {
                // 文件编码为 Unicode
                charset = "UTF-16LE";
                checked = true;
            }
            else if (first3Bytes[0] == (byte)0xFE && first3Bytes[1] == (byte)0xFF)
            {
                // 文件编码为 Unicode big endian
                charset = "UTF-16BE";
                checked = true;
            }
            else if (first3Bytes[0] == (byte)0xEF && first3Bytes[1] == (byte)0xBB && first3Bytes[2] == (byte)0xBF)
            {
                // 文件编码为 UTF-8
                charset = "UTF-8";
                checked = true;
            }
            
            bis.reset();
            
            if (!checked)
            {
                while ((read = bis.read()) != -1)
                {
                    if (read >= 0xF0)
                    {
                        break;
                    }
                    if (0x80 <= read && read <= 0xBF)
                    {
                        break;
                    }
                    if (0xC0 <= read && read <= 0xDF)
                    {
                        read = bis.read();
                        
                        if (0x80 <= read && read <= 0xBF)
                        {
                            continue;
                        }
                        else
                        {
                            break;
                        }
                    }
                    else if (0xE0 <= read && read <= 0xEF)
                    {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)
                        {
                            charset = "UTF-8";
                            break;
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        break;
                    }
                }
            }
            
            bis.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        return charset;
    }
    
    /**
     * 通道测试
     * @param sendId
     * @param gatewayId
     * @param recvId
     * @param content
     * @param redirectAttributes
     * @return
     * @see [类、类#方法、类#成员]
     */
    @RequiresPermissions("sms:jmsgGatewaySign:edit")
    @RequestMapping(value = "send")
    public String sendTest(String userId, String sendId, String gatewayId, String recvId, String content, String tdSpNumber, String smsType, String wapUrl, RedirectAttributes redirectAttributes, HttpServletResponse response) 
    {
    	String msg = "";
    	try
    	{
    		User user = UserUtils.get(userId);
        	if (null == user)
        	{
        		user = UserUtils.getUser();
        	}
        	
        	String taskId = MsgIdUtits.msgId("T");//生成任务ID
        	String pushFlag = "9";//待推送
            if(StringUtils.isBlank(user.getCallbackUrl())){
                pushFlag = "0";//无需推送
            }
        	
        	Map<String,String> phoneMap = PhoneUtils.get(recvId);
            String phoneType = PhoneUtils.getPhoneType(phoneMap);//运营商
            String cityCode = PhoneUtils.getCityCode(phoneMap);//省市代码
            
        	SmsMtMessage mtMsg = new SmsMtMessage();
        	mtMsg.setId(new MsgId().toString());
        	mtMsg.setGateWayID(gatewayId);
    		mtMsg.setSendStatus("T000");
    		mtMsg.setTaskid(taskId);
    		mtMsg.setUserid(userId);
    		mtMsg.setPayType("2");
    		mtMsg.setCstmOrderID("");
    		mtMsg.setUserReportNotify(pushFlag);
    		mtMsg.setUserReportGateWayID("HTTP");
    		mtMsg.setMsgContent(content);
    		mtMsg.setPhone(recvId);
    		mtMsg.setSmsType("sms");
    		mtMsg.setContentSize(1);
    		mtMsg.setPhoneType(phoneType);
    		mtMsg.setCityCode(cityCode);
    		mtMsg.setSpNumber(tdSpNumber);
    		
    		String appCode = GatewayUtils.getAppCode(mtMsg.getGateWayID());// 应用代码
    		String topic = "SMSMT" + appCode + "S";
    		String msgLevel = "HIGH";
    		
    		String msgid = mQUtils.sendSmsMT(topic, mtMsg.getGateWayID() + "_" + msgLevel, mtMsg.getId(), FstObjectSerializeUtil.write(mtMsg));
    		if (StringUtils.equals(msgid, "-1"))
    		{
    			msg = "签名测试失败";
    		}
    		else
    		{
    			msg = "通道测试成功";
    		}
    	}
    	catch (Exception e)
    	{
    		msg = "签名测试失败";
    	}
    	
    	addMessage(redirectAttributes, msg);
        return renderString(response, msg);
    }
}