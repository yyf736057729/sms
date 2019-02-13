package com.siloyou.jmsg.modules.sms.task.impl;

import com.alibaba.fastjson.JSON;
import com.sanerzone.common.modules.TableNameUtil;
import com.sanerzone.common.modules.smscenter.utils.JmsgCacheKeys;
import com.sanerzone.common.support.config.Global;
import com.sanerzone.common.support.mapper.JsonMapper;
import com.sanerzone.common.support.utils.Base64Util;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.IdGen;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.utils.excel.ExportExcel;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.utils.MQUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgReportStatusTask;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsReport;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSendReport;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsReportService;
import com.siloyou.jmsg.modules.sms.task.IPublicCustomTask;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;

/**
 * @ClassName: JmsgSearchSendListTask
 * @Description: 短信记录下行查询任务，查询然后保存至服务器
 * @author: yuyunfeng
 * @Date: 2019/1/30
*/
public class JmsgSearchSendListTask implements IPublicCustomTask{
	
	private static Logger logger = LoggerFactory.getLogger(JmsgSearchSendListTask.class);
	
	private JmsgSmsSendDao jmsgSmsSendDao = SpringContextHolder.getBean(JmsgSmsSendDao.class);

	private JmsgSmsReportService jmsgSmsReportService = SpringContextHolder.getBean(JmsgSmsReportService.class);

	

	@Override
	public String taskRun(String param) {
		String msg;
		String dayPath = com.siloyou.core.common.config.Global.getConfig("smsTask_sendRecord_dir")+File.separator+ DateUtils.getDate();
		File dayFile = new File(dayPath);
		if(!dayFile.exists()){
			dayFile.mkdirs();
		}
		String path = dayPath+File.separator+IdGen.uuid()+".xls";
		JmsgSmsSend jmsgSmsSend=JSON.parseObject(param, JmsgSmsSend.class);
        List<JmsgSmsSend> list = jmsgSmsSendDao.getSmsList(jmsgSmsSend);
        for(JmsgSmsSend jmsgSmsSend1 : list){
			jmsgSmsSend1.setUserId(jmsgSmsSend1.getUser().getId());
			jmsgSmsSend1.setName(UserUtils.get(jmsgSmsSend1.getUser().getId()).getName());
			if(StringUtils.startsWith(jmsgSmsSend1.getSendStatus(),"T")){
				jmsgSmsSend1.setSendStatus("成功");
			}else {
				jmsgSmsSend1.setSendStatus("失败");
			}

			if("0".equals(jmsgSmsSend1.getReportStatus())){
				jmsgSmsSend1.setReportStatus("成功");
			}else {
				jmsgSmsSend1.setReportStatus("待返回");
			}
			if(jmsgSmsSend1.getSubmitTime()!=null&&!"".equals(jmsgSmsSend1.getSubmitTime())){
				jmsgSmsSend1.setSubmitTime_excle(DateUtils.formatDateTime(jmsgSmsSend1.getSubmitTime()));
			}


		}
		ExportExcel e =new ExportExcel("下行短信明细", JmsgSmsSend.class).setDataList(list);
    	OutputStream stream=null;
		try {
			stream = new FileOutputStream(new File(path));
			e.write(stream);
			msg = "执行成功,<a href=\""+ "http://"+com.siloyou.core.common.config.Global.getConfig("sys.ip.port")+"/"+
					com.siloyou.core.common.config.Global.getConfig("adminPath")+"/sms/jmsgSmsSend/download?path="+ Base64Util.encode(path.getBytes()) +"\">请下载</a>";
		} catch (FileNotFoundException e1) {
				msg = "执行失败";
				e1.printStackTrace();
			} catch (IOException e2) {
				msg = "执行失败";
				e2.printStackTrace();
			} finally {
				if (stream != null) ;
				try {
					stream.close();
				} catch (IOException e3) {
					msg = "执行失败";
					e3.printStackTrace();
				}
		}
				return msg;
	}
}
