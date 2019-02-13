package com.siloyou.jmsg.modules.api.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.siloyou.core.common.mapper.JsonMapper;
import com.siloyou.core.common.utils.Base64Util;
import com.siloyou.core.common.utils.HttpRequest;
import com.siloyou.core.common.utils.IPUtils;
import com.siloyou.core.common.utils.IdGen;
import com.siloyou.core.common.utils.JedisClusterUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StreamUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.web.BaseController;
import com.siloyou.core.modules.sys.dao.UserDao;
import com.siloyou.core.modules.sys.entity.Dict;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.DictUtils;
import com.siloyou.jmsg.common.utils.MQUtils;
import com.siloyou.jmsg.modules.account.service.JmsgAccountService;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsData;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsTask;
import com.siloyou.jmsg.modules.mms.service.JmsgMmsDataService;
import com.siloyou.jmsg.modules.mms.service.JmsgMmsTaskService;

@Controller
@RequestMapping(value = "${apiPath}")
public class MmsApiController extends BaseController {
	
	private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
	
	@Autowired
	private JmsgAccountService jmsgAccountService;
	
	@Autowired
	private JmsgMmsTaskService jmsgMmsTaskService;
	
	@Autowired
	private JmsgMmsDataService jmsgMmsDataService;
	
	@Autowired
	private MQUtils mQUtils;
	
	@SuppressWarnings("rawtypes")
	public Map getPostDataMap(HttpServletRequest request){
		try{
			String charEncoding = request.getCharacterEncoding();
			if (charEncoding == null) {
				charEncoding = "UTF-8";
			}
			String respText = StreamUtils.InputStreamTOString(request.getInputStream(), charEncoding);
			if( StringUtils.isNotBlank(respText)) {
				String jsonString = new String(Base64Util.decode(respText));
				return (Map) JsonMapper.fromJsonString(jsonString, Map.class);
			}
		}catch(IOException e) {
			
		}
		return null;
	}
	
	//创建彩信
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "mms/create")
	public String mmsCreate(HttpServletRequest request, HttpServletResponse response) {
		Map map = Maps.newHashMap();
		try{
			String userId = request.getParameter("appid");
			
			map = validate(userId, request,true);
			
			if(StringUtils.equals(String.valueOf(map.get("code")),"T")){//验证通过
				
				String nocheck = (String)map.get("nocheck");
				map.remove("nocheck");
				
				Map content = getPostDataMap(request);
				if( content != null && !content.isEmpty()) {
					
					String mmsContent= "";
					String mmsTitle = (String)content.get("mms_title");
					if(StringUtils.isBlank(mmsTitle)){
						map.put("code", "F0209");
						map.put("msg", "彩信主题为空");
						return renderString(response, map);
					}else{
						if(mmsTitle.length() >20){
							map.put("code", "F0210");
							map.put("msg", "彩信主题超过20个字符限制");
							return renderString(response, map);
						}
					}
					
					String mmsType = (String)content.get("mms_type");
					List<Map> mapList= (List<Map>) content.get("mmsbody");
					for (int i = 0; i < mapList.size(); i++) {
						Map body =  (Map)mapList.get(i);
						String type = (String)body.get("content_type");
						String data = (String)body.get("content_data");
						if(StringUtils.equals(type,"text/plain")){
							mmsContent=mmsContent+"<div>"+data.replace("\r\n", "<BR>")+"</div>";
						} else if(StringUtils.equals(type,"image/gif") || StringUtils.equals(type,"image/jpeg") ){
							mmsContent=mmsContent+"<img src=\"data:"+type+";base64,"+data+"\"/>";
						} else {
							map.put("code", "F0201");
							map.put("msg", "彩信素材只支持image/gif和image/jpeg");
							return renderString(response, map);
						}
					}
					
					if(mmsContent.length() > 102400){
						map.put("code", "F0202");
						map.put("msg", "彩信大小超出100K限制");
						return renderString(response, map);
					}
					
					//保存彩信
					JmsgMmsData jmsgMmsData = new JmsgMmsData();
					jmsgMmsData.setMmsTitle(mmsTitle);
					jmsgMmsData.setContent(mmsContent);
					jmsgMmsData.setMmsCode(IdGen.randomBase62(6));
					User user = new User(userId);
					jmsgMmsData.setCreateBy(user);
					jmsgMmsData.setUpdateBy(user);
					if("1".equals(nocheck)){
						jmsgMmsData.setCheckStatus("8");//免审
					}else{
						jmsgMmsData.setCheckStatus("9");//待审
					}
					jmsgMmsDataService.save(jmsgMmsData);
					
					//审核
					List<Dict> emails = DictUtils.getDictList("review_mms_emails");
					if(emails != null && !emails.isEmpty()) {
						StringBuffer sbfEmails = new StringBuffer();
						for(Dict dict : emails) {
							sbfEmails.append(dict.getValue()).append(",");
						}
						mQUtils.sendCreateMmsMQ(jmsgMmsData.getId(), "{\"toaddress\":\""+sbfEmails.toString()+"\",\"subject\":\""+jmsgMmsData.getMmsTitle()+"\"}");
					}
					
					Map<String,String> taskinfo = Maps.newConcurrentMap();
					String mmsId = jmsgMmsData.getId();
					taskinfo.put("mms_id", mmsId);
					taskinfo.put("status", jmsgMmsData.getCheckStatus());
					
					map.put("data", taskinfo);
					return renderString(response, map);
				} else {
					map.put("code", "F0107");
					map.put("msg", "业务参数缺失");
					return renderString(response, map);
				}
					
			}
			return renderString(response, map);
		}catch(Exception e){
			e.printStackTrace();
			map.put("code", "F9999");
			map.put("msg", "系统错误");
			return renderString(response, map);
		}
	}
	
	//彩信发送任务
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "mms/send")
	public String mmsSend(HttpServletRequest request, HttpServletResponse response) {
		Map map = Maps.newHashMap();
		try{
			String userId = request.getParameter("appid");
			
			map = validate(userId, request,false);
			if(StringUtils.equals(String.valueOf(map.get("code")),"T")){//验证通过
				
				Map content = getPostDataMap(request);
				if( content != null && !content.isEmpty()) {
					String mmsfrom = StringUtils.valueof(content.get("mms_from"));
					String mmsId = StringUtils.valueof(content.get("mms_id"));
					String phones = StringUtils.valueof(content.get("phones"));
					if(StringUtils.isBlank(phones)) {
						map.put("code", "F0101");
						map.put("msg","phones参数缺失");
						return renderString(response, map);
					} else {
						if(phones.length() > 48) {
							map.put("code", "F0203");
							map.put("msg","号码个数超过限制");
							return renderString(response, map);
						}
					}
					
					if(StringUtils.isBlank(mmsId)){
						map.put("code", "F0101");
						map.put("msg","mmsid参数缺失");
						return renderString(response, map);
					}
				
					JmsgMmsData jmsgMmsData = jmsgMmsDataService.get(mmsId);
					if(jmsgMmsData != null && userId.equals(jmsgMmsData.getCreateBy().getId())){//验证彩信是否存在
						
						//判断彩信状态
						if(!StringUtils.equals(jmsgMmsData.getCheckStatus(), "1")
								&& !StringUtils.equals(jmsgMmsData.getCheckStatus(), "8")) {
							map.put("code", "F0208");
							if (StringUtils.equals(jmsgMmsData.getCheckStatus(), "9"))//待审状态
								map.put("msg","彩信审核中");
							else 
								map.put("msg","彩信未通过审核，原因:"+ jmsgMmsData.getCheckContent());
							return renderString(response, map);
						}
						
						String[] phoneArray = phones.split(",");
						Set<String> phoneList = Sets.newHashSet();
						CollectionUtils.addAll(phoneList,phoneArray);//数组转SET
						if(phoneList.size() >0){
							Long money = jmsgAccountService.findUserMoeny(userId, "mms");
							if(money >= phoneList.size()){//验证余额是否足够
								String taskId = jmsgMmsTaskService.insertTaskByInterface(phoneList, userId, mmsfrom,jmsgMmsData);//生成任务
								if(StringUtils.isBlank(taskId)){
									map.put("code", "F9999");
									map.put("msg", "系统错误");
								}else{
									map.put("data",taskId);
								}
							}else{
								map.put("code", "F0204");
								map.put("msg","余额不足");
								return renderString(response, map);
							}
						}else{
							map.put("code", "F0205");
							map.put("msg","号码不能为空");
							return renderString(response, map);
						}
					}else{
						map.put("code", "F0206");
						map.put("msg","彩信不存在");
						return renderString(response, map);
					}
				} else {
					map.put("code", "F0107");
					map.put("msg", "业务参数缺失");
					return renderString(response, map);
				}
			}
			return renderString(response, map);
		}catch(Exception e){
			e.printStackTrace();
			map.put("code", "F9999");
			map.put("msg", "系统错误");
		}
		return renderString(response, map);
	}
	
	
	//余额查询
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "mms/balance")
	public String mmsSendBalance(HttpServletRequest request, HttpServletResponse response) {
		Map<String,String> map = Maps.newHashMap();
		try{
			String userId = request.getParameter("appid");

			map = validate(userId, request,false);
			if(StringUtils.equals(String.valueOf(map.get("code")),"T")){//验证通过
				map.put("data", String.valueOf(jmsgAccountService.findUserMoeny(userId, "mms")));
			}
			return renderString(response, map);
		}catch(Exception e){
			e.printStackTrace();
			map.put("code", "F9999");
			map.put("msg", "系统错误");
		}
		
		return renderString(response, map);
	}
	
	//彩信任务状态查询
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "mms/taskStatus")
	public String mmsTaskStatus(HttpServletRequest request, HttpServletResponse response) {
		Map map = Maps.newHashMap();
		try{
			String userId = request.getParameter("appid");
			String taskId = request.getParameter("taskid");//任务ID
			if(StringUtils.isBlank(taskId)){
				map.put("code", "F0101");
				map.put("msg","taskid参数缺失");
				return renderString(response, map);
			}
			map = validate(userId, request,false);
			if(StringUtils.equals(String.valueOf(map.get("code")),"T")){//验证通过
				Map<String,String> mapData = Maps.newHashMap();
				JmsgMmsTask jmsgMmsTask =  jmsgMmsTaskService.get(taskId);
				if(jmsgMmsTask == null){
					map.put("code", "F0207");
					map.put("msg","任务不存在");
					return renderString(response, map);
				}else{
					mapData.put("status", jmsgMmsTask.getStatus());
					mapData.put("content", DictUtils.getDictLabel(jmsgMmsTask.getStatus(), "task_send_status", "未知"));
				}
				map.put("data", mapData);
			}
			return renderString(response, map);
		}catch(Exception e){
			e.printStackTrace();
			map.put("code", "F9999");
			map.put("msg", "系统错误");
		}
		
		return renderString(response, map);
	}
	
	//彩信审核结果查询
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "mms/reviewResult")
	public String reviewResult(HttpServletRequest request, HttpServletResponse response) {
		@SuppressWarnings("rawtypes")
		Map map = Maps.newHashMap();
		try{
			String userId = request.getParameter("appid");
			String mmsId = request.getParameter("mmsid");//彩信ID
			if(StringUtils.isBlank(mmsId)){
				map.put("code", "F0101");
				map.put("msg","mmsid参数缺失");
				return renderString(response, map);
			}
			map = validate(userId, request,false);
			if(StringUtils.equals(String.valueOf(map.get("code")),"T")){//验证通过
				Map<String,String> dataMap = Maps.newHashMap();
				JmsgMmsData jmsgMmsData = jmsgMmsDataService.get(mmsId);
				if(jmsgMmsData != null && userId.equals(jmsgMmsData.getCreateBy().getId())){
					String status = jmsgMmsData.getCheckStatus();
					dataMap.put("status", status);
					dataMap.put("content", DictUtils.getDictLabel(status, "check_status", "未知")+("0".equals(status)?"("+jmsgMmsData.getCheckContent()+")":""));
				}else{
					map.put("code", "F0206");
					map.put("msg","彩信不存在");
					return renderString(response, map);
				}
				
				map.put("data", dataMap);
			}
			return renderString(response, map);
		}catch(Exception e){
			e.printStackTrace();
			map.put("code", "F9999");
			map.put("msg", "系统错误");
		}
		
		return renderString(response, map);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map validate(String userId,HttpServletRequest request,boolean flag){
		Map map = Maps.newHashMap();
		
		// 校验 appid
		if(StringUtils.isBlank(userId)){
			map.put("code", "F0101");
			map.put("msg", "appid参数缺失");
			return map;
		}
		
		// 校验 appid
		long timestamp = 0;
		String pTimestamp = request.getParameter("timestamp");
		if(StringUtils.isNotBlank(pTimestamp)){
			timestamp = Long.valueOf(pTimestamp);
		} else {
			map.put("code", "F0101");
			map.put("msg", "timestamp参数缺失");
			return map;
		}
		
		long now = System.currentTimeMillis();
		if(Math.abs(timestamp- now) > 3600000){//验证时间戳 误差一个小时内
			map.put("code", "F0102");
			map.put("msg", "时间戳相差过大");
			return map;
		}
		
		// 校验 sign
		String sign = request.getParameter("sign");
		if(StringUtils.isBlank(sign)){
			map.put("code", "F0101");
			map.put("msg", "sign参数缺失");
			return map;
		}
		
		String ip = IPUtils.getIpAddr(request);
		String key = userId+"_"+timestamp;
		if(StringUtils.isBlank(JedisClusterUtils.get(key))){//正常提交
			JedisClusterUtils.set(key, "1", 86400);//缓存一天
			User user = userDao.get(userId);
			if(user == null){//用户不存在
				map.put("code", "F0103");
				map.put("msg", "用户不存在");
			}else{
				String whiteIP = user.getWhiteIP();
				if(StringUtils.isBlank(whiteIP) || (","+whiteIP+",").indexOf(","+ip+",") >= 0){//验证IP
					String apikey = user.getApikey();
					String md5 = HttpRequest.md5(apikey+userId+timestamp+apikey);//MD5加密 (apikey + appid + timestamp + apikey)
					if(sign.equals(md5)){//签名验证
						map.put("code", "T");//验证通过
						map.put("msg", "成功");
						if(flag){
							map.put("nocheck", user.getNoCheck());
						}
					}else{
						map.put("code", "F0104");
						map.put("msg", "sign验签不正确");
					}
				}else{
					map.put("code", "F0105");
					map.put("msg", "IP限制");
				}
			}
		}else{//重复提交
			map.put("code", "F0106");
			map.put("msg", "重复提交");
		}
		
		return map;
	}
}
