/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.web;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.siloyou.jmsg.modules.sms.utils.ResultUtil;
import com.siloyou.jmsg.common.utils.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.sanerzone.common.modules.TableNameUtil;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.HttpRequest;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.modules.Result;
import com.siloyou.jmsg.modules.account.service.JmsgAccountService;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayInfo;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.service.JmsgGatewayInfoService;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsSendService;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsTaskService;

/**
 * 通道信息Controller
 * @author zhukc
 * @version 2016-07-29
 */
@Controller
@RequestMapping(value = "${adminPath}/sms/jmsgGatewayInfo")
public class JmsgGatewayInfoController extends BaseController {

	@Autowired
	private JmsgGatewayInfoService jmsgGatewayInfoService;
	
	@Autowired
    private JmsgSmsTaskService jmsgSmsTaskService;
	
    @Autowired
    private JmsgAccountService jmsgAccountService;
    
    @Autowired
    private JmsgSmsSendService jmsgSmsSendService;
	
	@ModelAttribute
	public JmsgGatewayInfo get(@RequestParam(required=false) String id) {
		JmsgGatewayInfo entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = jmsgGatewayInfoService.get(id);
		}
		if (entity == null){
			entity = new JmsgGatewayInfo();
		}
		return entity;
	}
	
	@RequiresPermissions("sms:jmsgGatewayInfo:view")
	@RequestMapping(value = {"list", ""})
	public String list(JmsgGatewayInfo jmsgGatewayInfo, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<JmsgGatewayInfo> page = jmsgGatewayInfoService.findPage(new Page<JmsgGatewayInfo>(request, response), jmsgGatewayInfo); 
		model.addAttribute("page", page);
		return "modules/sms/jmsgGatewayInfoList";
	}

	@RequiresPermissions("sms:jmsgGatewayInfo:view")
	@RequestMapping(value = "form")
	public String form(JmsgGatewayInfo jmsgGatewayInfo, Model model) {
		model.addAttribute("jmsgGatewayInfo", jmsgGatewayInfo);
		return "modules/sms/jmsgGatewayInfoForm";
	}

	@RequiresPermissions("sms:jmsgGatewayInfo:edit")
	@RequestMapping(value = "save")
	public String save(JmsgGatewayInfo jmsgGatewayInfo, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, jmsgGatewayInfo)){
			return form(jmsgGatewayInfo, model);
		}
		jmsgGatewayInfoService.save(jmsgGatewayInfo);
		addMessage(redirectAttributes, "保存通道信息成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewayInfo/?repage";
	}
	
	@RequiresPermissions("sms:jmsgGatewayInfo:edit")
	@RequestMapping(value = "delete")
	public String delete(JmsgGatewayInfo jmsgGatewayInfo, RedirectAttributes redirectAttributes) {
		jmsgGatewayInfoService.delete(jmsgGatewayInfo);
		addMessage(redirectAttributes, "删除通道信息成功");
		return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewayInfo/?repage";
	}
	
	/**
	 * 通道关闭
	 * @param jmsgGatewayInfo
	 * @param gatewayId
	 * @param redirectAttributes
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	@RequiresPermissions("sms:jmsgGatewayInfo:edit")
    @RequestMapping(value = "close")
    public String gateWayClose(JmsgGatewayInfo jmsgGatewayInfo, String gatewayId, RedirectAttributes redirectAttributes) {
	    
	    JmsgGatewayInfo entity = GatewayUtils.getGatewayInfo(gatewayId);
	    if (null != entity && StringUtils.isNotBlank(entity.getAppHost()) && StringUtils.isNotBlank(entity.getAppCode()))
	    {
	        StringBuffer strBuf = new StringBuffer();
            strBuf.append("http://")
                .append(entity.getAppHost())
                .append(":")
                .append(entity.getAppCode())
                .append("/api/sms/gateway/close?id=")
                .append(gatewayId);
            
	        Result result = JSON.parseObject(HttpRequest.sendPost(strBuf.toString(), null, null), Result.class);
	        
	        if (result.isSuccess())
	        {
	            // 网关运行状态 1：运行 0 ：停止
	            entity.setGatewayState("0");
	            jmsgGatewayInfoService.updateGatewayState(entity);
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
        return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewayInfo/?repage";
    }
	
	/**
     * 通道开启
     * @param jmsgGatewayInfo
     * @param gatewayId
     * @param redirectAttributes
     * @return
     * @see [类、类#方法、类#成员]
     */
    @RequiresPermissions("sms:jmsgGatewayInfo:edit")
    @RequestMapping(value = "open")
    public String gateWayOpen(JmsgGatewayInfo jmsgGatewayInfo, String gatewayId, RedirectAttributes redirectAttributes) {
        
        JmsgGatewayInfo entity = GatewayUtils.getGatewayInfo(gatewayId);
        if (null != entity && StringUtils.isNotBlank(entity.getAppHost()) && StringUtils.isNotBlank(entity.getAppCode()))
        {
            StringBuffer strBuf = new StringBuffer();
            strBuf.append("http://")
                .append(entity.getAppHost())
                .append(":")
                .append(entity.getAppCode())
                .append("/api/sms/gateway/open?id=")
                .append(gatewayId);
            
            Result result = JSON.parseObject(HttpRequest.sendPost(strBuf.toString(), null, null), Result.class);
            
            if (result.isSuccess())
            {
                // 网关运行状态 1：运行 0 ：停止
                entity.setGatewayState("1");
                jmsgGatewayInfoService.updateGatewayState(entity);
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
        return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewayInfo/?repage";
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
    @RequiresPermissions("sms:jmsgGatewayInfo:edit")
    @RequestMapping(value = "send")
    public String sendTest(String sendId, String gatewayId, String recvId, String content, String tdSpNumber, String smsType, String wapUrl, RedirectAttributes redirectAttributes, HttpServletResponse response) {
        
        JmsgGatewayInfo entity = GatewayUtils.getGatewayInfo(gatewayId);
        String msg = "";
        
        if (null != entity && StringUtils.isNotBlank(recvId) && StringUtils.isNotBlank(content))
        {
            User user = UserUtils.getUser();
            boolean runFlag = true;
            
            if(runFlag){//执行

                String keywords = KeywordsUtils.keywords(content);
                
                if(StringUtils.isBlank(keywords)){
                    String taskId = MsgIdUtits.msgId("T");//生成任务ID
                    String pushFlag = "9";//待推送
                    if(StringUtils.isBlank(user.getCallbackUrl())){
                        pushFlag = "0";//无需推送
                    }
                    
                    JmsgSmsSend jmsgSmsSend = new JmsgSmsSend();
                    jmsgSmsSend.setId(new MsgId().toString());
                    jmsgSmsSend.setDataId("99");
                    jmsgSmsSend.setTaskId(taskId);//任务我ID
                    jmsgSmsSend.setPhone(recvId);//手机号码
                    jmsgSmsSend.setSmsContent(content);//短信内容
                    jmsgSmsSend.setSmsType(smsType);//短信类型
                    jmsgSmsSend.setPayCount(1);//扣费条数
                    jmsgSmsSend.setUser(user);//用户ID,公司ID
                    jmsgSmsSend.setChannelCode(gatewayId);//通道代码
                    jmsgSmsSend.setSpNumber(tdSpNumber);//接入号
                    
                    Map<String,String> phoneMap = PhoneUtils.get(recvId);
                    String phoneType = PhoneUtils.getPhoneType(phoneMap);//运营商
                    String cityCode = PhoneUtils.getCityCode(phoneMap);//省市代码
                    
                    jmsgSmsSend.setPhoneType(phoneType);//运营商
                    jmsgSmsSend.setAreaCode(cityCode);//省市代码
                    jmsgSmsSend.setPayType("2");//扣费方式
                    jmsgSmsSend.setPayStatus("1");//扣费状态
                    jmsgSmsSend.setPushFlag(pushFlag);//推送标识
                    
                    jmsgSmsSend.setSendStatus("T000");//发送状态
                    jmsgSmsSend.setSubmitMode("HTTP");//提交方式 WEB,API
                    jmsgSmsSend.setTopic(CacheKeys.getSmsSingleTopic());//发送队列
                    jmsgSmsSend.setReportGatewayId("HTTP");
                    Date d = new Date();
                    jmsgSmsSend.setSendDatetime(d);
                    jmsgSmsSend.setCreateDatetime(d);
                    jmsgSmsSend.setCustomerOrderId("1");
                    jmsgSmsSend.setSendStatus("1");
                    jmsgSmsSend.setSubmitMode("1");
                    
                    jmsgSmsSend.setTableName("jmsg_sms_send_" + TableNameUtil.getTableIndex(jmsgSmsSend.getId()));
                    jmsgSmsSend.setPayTime(d);
                    jmsgSmsSend.setMsgid("");
                    jmsgSmsSend.setSendDatetimeZ(d);
                    jmsgSmsSend.setSendDatetimeQ(d);
                    jmsgSmsSend.setCreateDatetimeQ(d);
                    jmsgSmsSend.setCreateDatetimeZ(d);
                    jmsgSmsSendService.insert(jmsgSmsSend);
                    
                    /*jmsgSmsTaskService.createSendDetail("1", taskId, userId, recvId, content, "HTTP", sendId, pushFlag, CacheKeys.getSmsSingleTopic());
                    
                    JmsgSmsSend param = new JmsgSmsSend();
                    param.setTaskId(taskId);
                    param.setPhone(recvId);
                    param = jmsgSmsSendService.queryJmsgSmsSend(param);*/
                    SmsMtMessage mt = new SmsMtMessage();
                    
                    if (null != jmsgSmsSend)
                    {
                        mt.setId(jmsgSmsSend.getId());
                        mt.setTaskid(jmsgSmsSend.getTaskId());
                        mt.setUserid(jmsgSmsSend.getUser().getId());
                        mt.setGateWayID(jmsgSmsSend.getChannelCode());
                        mt.setPayType(jmsgSmsSend.getPayType());
                        mt.setCstmOrderID(jmsgSmsSend.getCustomerOrderId());
                        mt.setUserReportNotify(jmsgSmsSend.getPushFlag());
                        mt.setUserReportGateWayID(jmsgSmsSend.getReportGatewayId());
                        mt.setMsgContent(jmsgSmsSend.getSmsContent());
                        mt.setPhone(jmsgSmsSend.getPhone());
                        mt.setSpNumber(jmsgSmsSend.getSpNumber());
                        mt.setSmsType(jmsgSmsSend.getSmsType());
                        /*if (null != jmsgSmsSend.getSendDatetime())
                        {
                            mt.setSendTime(jmsgSmsSend.getSendDatetime().getTime());
                        }
                        else
                        {
                            mt.setSendTime(System.currentTimeMillis());
                        }
                        
                        mt.setSubmitTime(jmsgSmsSend.getCreateDatetime().getTime());*/
                    }
                    
                    StringBuffer strBuf = new StringBuffer();
                    strBuf.append("http://")
                        .append(entity.getAppHost())
                        .append(":")
                        .append(entity.getAppCode())
                        .append("/api/sms/gateway/send");
                    
                    StringBuffer paramBuf = new StringBuffer();
                    paramBuf.append("id="+mt.getId())
                        .append("&gateWayID="+mt.getGateWayID())
                        .append("&userid="+mt.getUserid())
                        .append("&taskid="+mt.getTaskid())
                        .append("&payType="+mt.getPayType())
                        .append("&cstmOrderID=")
                        .append("&userReportNotify="+mt.getUserReportNotify())
                        .append("&userReportGateWayID="+mt.getUserReportGateWayID())
                        .append("&msgContent="+mt.getMsgContent())
                        .append("&phone="+mt.getPhone())
                        .append("&spNumber="+mt.getSpNumber())
                        .append("&smsType="+mt.getSmsType())
                        .append("&wapUrl="+wapUrl);
                        /*.append("&sendTime="+mt.getSendTime())
                        .append("&submitTime="+mt.getSubmitTime());*/
                    
                    Result result = JSON.parseObject(HttpRequest.sendPost(strBuf.toString(), paramBuf.toString(), null), Result.class);
                    
                    if (result.isSuccess())
                    {
                        msg = "通道测试成功";
                    }
                    else
                    {
                        msg = result.getErrorMsg();
                    }
                }else{
                    msg = "发送内容包含敏感词["+keywords+"]";
                }
            }
        }
        else
        {
            msg = "通道测试失败！通道不存在或通道应用配置错误。";
        }
        //return "redirect:"+Global.getAdminPath()+"/sms/jmsgGatewayInfo/?repage";
        addMessage(redirectAttributes, msg);
        return renderString(response, msg);
    }
	
	/**
	 * 根据ID获取网关信息
	 * @param gatewayId
	 * @param response
	 * @param request
	 * @param model
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	@RequiresPermissions("sms:jmsgGatewayInfo:view")
    @RequestMapping(value = "getGatewayInfo")
    public String getGatewayInfo(String gatewayId, HttpServletResponse response, HttpServletRequest request, Model model)
    {
        JmsgGatewayInfo entity = GatewayUtils.getGatewayInfo(gatewayId);
        
        if (entity == null || StringUtils.isBlank(entity.getSpNumber()))
        {
            entity = jmsgGatewayInfoService.get(gatewayId);
        }
        
        return renderString(response, entity);
    }

	/**
	 * 获取网关信息列表
	 * @param jmsgGatewayInfo
	 * @param response
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "getGatewayInfoList")
	public String getGatewayInfoList(JmsgGatewayInfo jmsgGatewayInfo, String type, HttpServletResponse response, HttpServletRequest request, Model model)
	{
		if (StringUtils.isNotBlank(type))
		{
			jmsgGatewayInfo.setType(type);
		}
		
		List<JmsgGatewayInfo> list = jmsgGatewayInfoService.findList(jmsgGatewayInfo);
		return renderString(response, list);
	}

       /**
       * @Description: TODO
       * @param:
       * @return:
       * @author: yuyunfeng
       * @Date: 2019/1/7
       */
       @RequiresPermissions("sms:jmsgGatewayInfo:view")
       @RequestMapping(value = "link")
       public String getGatewayLink( HttpServletResponse response, HttpServletRequest request, Model model)
       {
           JSONObject result= getGatewayLink(request.getParameter("id"));
               if(result.get(Constants.RET_CODE).equals(Constants.SUCCESS_CODE)){
                   JSONObject params = (JSONObject) result.get(Constants.RETURN_PARAMS);
                   List<String>spend_list_record = (List<String>) params.get("spend_map");//发送速率记录
                   String connect_record = (String) params.get("connect_map");//连接状态记录
                   model.addAttribute("spend_list_record", spend_list_record);
                   model.addAttribute("connect_record", connect_record);
                   model.addAttribute("id",request.getParameter("id"));
                   if(spend_list_record!=null){
                       model.addAttribute("status","1");
                   }else {
                       model.addAttribute("status","-1");
                   }
                   return "modules/sms/jmsgSmsGatewayLink";
               }

           return null;
       }

    /**
     * @Description: TODO
     * @param:
     * @return:
     * @author: yuyunfeng
     * @Date: 2019/1/7
     */
    @RequiresPermissions("sms:jmsgGatewayInfo:view")
    @RequestMapping(value = "link_post",method = RequestMethod.POST)
    public void getGatewayLinkPost( HttpServletResponse response, HttpServletRequest request, Model model) throws IOException {
        JSONObject data = new JSONObject();
        JSONObject result= getGatewayLink(request.getParameter("id"));
            if(result.get(Constants.RET_CODE).equals(Constants.SUCCESS_CODE)){
                JSONObject params = (JSONObject) result.get(Constants.RETURN_PARAMS);
                List<String>spend_list_record = (List<String>) params.get("spend_map");//发送速率记录
                String connect_record = (String) params.get("connect_map");//连接状态记录
                data.put("spend_list_record", spend_list_record);
                data.put("connect_record", connect_record);
                data.put("id",request.getParameter("id"));
                if(spend_list_record!=null){
                    data.put("status","1");
                }else {
                    data.put("status","-1");
                }
            }
        ResultUtil.writeResult(response,data.toString());
        }



    JSONObject getGatewayLink(String id) {
        JmsgGatewayInfo entity = GatewayUtils.getGatewayInfo(id);
        if (null != entity && StringUtils.isNotBlank(entity.getAppHost()) && StringUtils.isNotBlank(entity.getAppCode())) {
            StringBuffer strBuf = new StringBuffer();
            strBuf.append("http://")
                    .append(entity.getAppHost())
                    .append(":")
                    .append(entity.getAppCode())
                    .append("/api/sms/link/connect?id=")
                    .append(id);
            return JSONObject.parseObject(HttpRequest.sendPost(strBuf.toString(), null, null));
        }
        return null;
    }
}