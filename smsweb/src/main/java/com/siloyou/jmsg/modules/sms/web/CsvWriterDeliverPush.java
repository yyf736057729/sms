package com.siloyou.jmsg.modules.sms.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.formula.functions.T;
import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.excel.CsvFileWriter;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsDeliverPushDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDeliverPush;  

public class CsvWriterDeliverPush extends CsvFileWriter<T>{
	private String uuid;  
	private String fileName;
	private JmsgSmsDeliverPush param;
	private JmsgSmsDeliverPushDao jmsgSmsDeliverPushDao = SpringContextHolder.getBean(JmsgSmsDeliverPushDao.class);
	      
	public CsvWriterDeliverPush(String uuid, String fileName,JmsgSmsDeliverPush param){//外部调用，将UserId，uuid(随机数)，fileName传入  
		this.uuid = uuid;  
		this.fileName = fileName;
		this.param = param;
	}  
	  
	@Override
	protected 
	List<String[]> getCsvContent(int pageNo, int pageSize) {  
		List<String[]> allElements = new ArrayList<String[]>();
		param.setrPageNo(pageNo);
		param.setrPageSize(pageSize);
		List<JmsgSmsDeliverPush> list = jmsgSmsDeliverPushDao.findExportList(param);
		if(list != null && list.size() > 0){
			for (JmsgSmsDeliverPush jmsgSmsDeliverPush : list) {
				allElements.add(writeContent(jmsgSmsDeliverPush));
			}
		}
	    return allElements;
	}  
	  
	protected String[] getCsvTitle() {  
		String[] title = new String[]{  
	       "接入号",  
	       "上行手机号码",  
	       "短信内容",  
	       "创建时间"
	    };  
	    return title;  
	}  
	  
	  
	private String[] writeContent(JmsgSmsDeliverPush jmsgSmsDeliverPush) {  
		String[] content = new String[]{
				jmsgSmsDeliverPush.getSrcTermId()+"\t",
	        	jmsgSmsDeliverPush.getDestTermId(),
	        	jmsgSmsDeliverPush.getMsgContent(),
	        	DateUtils.formatDate(jmsgSmsDeliverPush.getCreatetime(), "yyyy-MM-dd HH:mm:ss") 
		};  
		return content;  
	}
	    
    @Override
	protected 
    String getCsvFileName() {  
        return fileName;  
    }
	  
    @Override
	protected  
    String getUuid() {  
        return uuid;  
    }
}
