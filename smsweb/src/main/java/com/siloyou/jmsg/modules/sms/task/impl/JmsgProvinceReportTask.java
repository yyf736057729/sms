package com.siloyou.jmsg.modules.sms.task.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.IdGen;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.modules.sys.utils.DictUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsSendService;
import com.siloyou.jmsg.modules.sms.task.IPublicCustomTask;

public class JmsgProvinceReportTask implements IPublicCustomTask{

	private JmsgSmsSendService jmsgSmsSendService = SpringContextHolder.getBean(JmsgSmsSendService.class);
	
	private final static int pageSize=1000;
	
	@Override
	public String taskRun(String param) {
		Map<String,Object> map = JSON.parseObject(param);
		map.put("pageSize", pageSize);
		Map<String,Object> result = Maps.newHashMap();
		int sumAll = 0;
		String maxId = "0";
		while(true){
			map.put("id", maxId);
			List<JmsgSmsSend> list = jmsgSmsSendService.findJmsgSmsSendListByProvinceReport(map);
			if(list != null && list.size() > 0){
				for (JmsgSmsSend jmsgSmsSend : list) {
					String area = jmsgSmsSend.getAreaCode();
					sumAll+=jmsgSmsSend.getPayCount();
					if(result.containsKey(area)){
						int curCount = (int)result.get(area);
						curCount += jmsgSmsSend.getPayCount();
						result.put(area, curCount);
					}else{
						result.put(area, jmsgSmsSend.getPayCount());
					}
				}
				
			}else{
				break;
			}
			
			if(list.size() < pageSize)break;
			JmsgSmsSend idEntity = list.get(pageSize-1);
			if(idEntity == null)break;
			maxId = idEntity.getId();
		}
		
		String msg = "";
		if(result != null && result.size() > 0){
			StringBuffer sb = new StringBuffer("");
			for (Map.Entry<String, Object> entry: result.entrySet()) {
				sb.append(DictUtils.getDictLabel(entry.getKey(), "phone_province", "")).append(" ").append(entry.getValue()).append(" ").append(zb(sumAll, entry.getValue())).append("\r\n");
			}
			
			FileOutputStream outSTr = null;  
	        BufferedOutputStream buff = null;  
			try{
				String dayPath = Global.getConfig("sms.report.path")+File.separator+DateUtils.getDate();
				
				File dayFile = new File(dayPath);
				if(!dayFile.exists()){
					dayFile.mkdir();
				}
				String fileName = IdGen.uuid();
				String path = dayPath+File.separator+fileName+".txt";
				File file = new File(path);
				if(!file.exists()){
					file.createNewFile();
				}
				
				outSTr = new FileOutputStream(file);
				buff = new BufferedOutputStream(outSTr); 
				buff.write(sb.toString().getBytes("UTF-8"));
				buff.flush();  
		        buff.close();
		        msg = "执行成功,<a href=\""+Global.getConfig("adminPath")+"/sms/jmsgCustomTask/download?path="+path+"\">请下载</a>";
			}catch(Exception e){
				e.printStackTrace();
				msg = "执行失败";
			}finally{
				try{
					buff.close();
					outSTr.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		}else{
			msg = "执行成功，没有数据";
		}
		
		return msg;
	}
	
	private String zb(int sum,Object payCount){
		double pay = Double.valueOf(String.valueOf(payCount));
		double d = pay*100/sum;
		DecimalFormat df = new DecimalFormat("######0.00");
		return df.format(d)+"%";
	}

}
