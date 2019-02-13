package com.siloyou.jmsg.modules.mms.task;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.modules.sys.utils.DictUtils;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsReportDao;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsSubmitDao;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsTaskDao;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsTaskDetailDao;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsReport;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsSubmit;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsTask;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsTaskDetail;

//@Service
//@Lazy(false)
public class MmsSendSuccessTask {
	
	public static Logger logger = LoggerFactory.getLogger(MmsSendSuccessTask.class);
	
	private static JmsgMmsTaskDao jmsgMmsTaskDao = SpringContextHolder.getBean(JmsgMmsTaskDao.class);
	
	private static JmsgMmsSubmitDao jmsgMmsSubmitDao = SpringContextHolder.getBean(JmsgMmsSubmitDao.class);
	
	private static JmsgMmsReportDao jmsgMmsReportDao = SpringContextHolder.getBean(JmsgMmsReportDao.class);
	
	private static JmsgMmsTaskDetailDao jmsgMmsTaskDetailDao = SpringContextHolder.getBean(JmsgMmsTaskDetailDao.class);

	
	private static final int BATCH_COMMIT_MAX_COUNT = 500;//批量提交默认值
	
	//每5分钟执行一次 统计彩信发送成功数
	@Scheduled(cron = "0 0/5 * * * ?")
	public void execMmsSendSuccessCount(){
		mmsSendSuccessCount(new Date());
	}
	
	public void mmsSendSuccessCount(Date day){
		//获取任务列表  （任务扣款方式）
		List<JmsgMmsTask> list = jmsgMmsTaskDao.findTaskPayModeList(day);//获取当天
		
		List<JmsgMmsTask> resultList = Lists.newArrayList();
		
		Integer successCount = null;
		if(list != null && list.size() > 0){
			for (JmsgMmsTask jmsgMmsTask : list) {
				String payMode = jmsgMmsTask.getPayMode();
				String taskId = jmsgMmsTask.getId();
				if("1".equals(payMode)){//网关
					successCount = jmsgMmsSubmitDao.findSuccessCount(taskId);
				}else if("2".equals(payMode)){//状态报告
					successCount = jmsgMmsReportDao.findSuccessCount(taskId);
				}else if("3".equals(payMode)){//下载量
					successCount = jmsgMmsTaskDetailDao.queryDownloadCount(taskId);
				}
				jmsgMmsTask.setSuccessCount(successCount == null?0:successCount);
				resultList.add(jmsgMmsTask);
				successCount = null;
			}
		}
		
		//批量修改
		if(resultList.size() > 0){
			SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
			int index = 0;
			try {
				for (JmsgMmsTask entity : resultList) {
					index++;
					sqlSession.insert("com.siloyou.jmsg.modules.mms.dao.JmsgMmsTaskDao.batchUpdateSuccessCount", entity);//批量提交
					if(index % BATCH_COMMIT_MAX_COUNT == 0){
						sqlSession.commit();
					}
				}
				sqlSession.commit();
			}catch(Exception e){
				logger.error("{}", e);
			}finally{
				sqlSession.close();
			}
		}
	}
	
	public void crateDetailAccount(String userId,String createTime, String taskId){
		String payMode = UserUtils.getPayMode(userId, "mms");//扣款方式
		List<JmsgMmsTaskDetail> list = jmsgMmsTaskDetailDao.queryDetailSendList(taskId);
		Map<String,String> map = Maps.newHashMap();
		String result = "";
		if("1".equals(payMode)){//网关
			List<JmsgMmsSubmit> submitList = jmsgMmsSubmitDao.findDetailSendList(taskId);
			if(submitList != null && submitList.size() >0){
				for (JmsgMmsSubmit jmsgMmsSubmit : submitList) {
					map.put(jmsgMmsSubmit.getBizid(), jmsgMmsSubmit.getResult());
				}
			}
			result = statusResult(list, map, "0", true);
		}else if("2".equals(payMode)){//状态报告
			List<JmsgMmsReport> reportList = jmsgMmsReportDao.findDetailSendList(taskId);
			if(reportList != null && reportList.size() >0){
				for (JmsgMmsReport jmsgMmsReport : reportList) {
					map.put(jmsgMmsReport.getBizid(), jmsgMmsReport.getStat());
				}
			}
			result = statusResult(list, map, "DELIVRD", true);
		}else if("3".equals(payMode)){//下载量
			result = statusResult(list, map, null, false);
		}
		
		FileOutputStream outSTr = null;  
        BufferedOutputStream buff = null;  
		try{
			String pathname = Global.getConfig("mms.detail.send.path")+"/"+createTime+"/"+taskId+".txt";
			File file = new File(pathname);
			if(!file.exists()){
				file.createNewFile();
			}
			outSTr = new FileOutputStream(file);
			buff = new BufferedOutputStream(outSTr); 
			buff.write(result.getBytes("UTF-8"));
			buff.flush();  
	        buff.close();  
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				buff.close();
				outSTr.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	private String statusResult(List<JmsgMmsTaskDetail> list,Map<String,String> map,String key,boolean flag){
		StringBuffer sb = new StringBuffer();
		for (JmsgMmsTaskDetail jmsgMmsTaskDetail : list) {
			String phone = jmsgMmsTaskDetail.getPhone();
			if("T0".equals(jmsgMmsTaskDetail.getSendStatus())){
				if(flag){//执行标识
					String bizid = jmsgMmsTaskDetail.getId();
					if(map.containsKey(bizid)){
						String value = map.get(bizid);
						if(key.equals(value)){
							sb.append(phone).append(" ").append("成功").append("\r\n");
						}else{
							sb.append(phone).append(" ").append("失败").append("\r\n");
						}
					}else{
						sb.append(phone).append(" ").append("失败").append("\r\n");
					}
				}else{
					if(jmsgMmsTaskDetail.getCreateDatetime() == null){
						sb.append(phone).append(" ").append("失败").append("\r\n");
					}else{
						sb.append(phone).append(" ").append("成功").append("\r\n");
					}
				}
			}else{
				sb.append(phone).append(" ").append(DictUtils.getDictLabel(jmsgMmsTaskDetail.getSendStatus(), "mms_send_status", "失败")).append("\r\n");
			}
		}
		
		return sb.toString();
	}
	
	
}
