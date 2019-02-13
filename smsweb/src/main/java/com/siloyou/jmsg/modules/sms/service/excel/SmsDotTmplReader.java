package com.siloyou.jmsg.modules.sms.service.excel;

import java.util.List;

import com.sanerzone.common.support.utils.ValidatorUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.utils.excel.IRowReader;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDot;

public class SmsDotTmplReader implements IRowReader{  
	
	private List<JmsgSmsDot> list;
	private int phoneRow;//手机所在列
	private String smsContent;//发送内容
	private int maxRow;//最大列
	private String code = "1";
	
	public SmsDotTmplReader(List<JmsgSmsDot> list,String phoneRow,String smsContent){
		this.list = list;
		this.phoneRow = StringUtils.isBlank(phoneRow)?0:Integer.valueOf(phoneRow);
		this.smsContent = smsContent;
	}
	
    /* 业务逻辑实现方法 
     */  
    public void getRows(int sheetIndex, int curRow, List<String> rowlist) {
    	if("1".equals(code)){
    		if(curRow  == 0 ){
    			maxRow = rowlist.size();
    		}
    		if(maxRow != rowlist.size()){
    			code = "-1";//导入excel信息失败，列长度不一致
    		}else{
    			if(rowlist.size() > phoneRow){
    				JmsgSmsDot jmsgSmsDot = new JmsgSmsDot();
    	        	jmsgSmsDot.setPhone(rowlist.get(phoneRow));
    	        	if(ValidatorUtils.isMobile(jmsgSmsDot.getPhone())){//判断是否手机号码
	    	        	String realContent = smsContent;
	    	        	for (int i = 0; i < rowlist.size(); i++) {
	    	        		realContent = smsContent(realContent, i, rowlist.get(i));
						}
	    	        	jmsgSmsDot.setSmsContent(realContent);
	    	        	list.add(jmsgSmsDot);
    	        	}
    			}else{
    				code = "-2";//导入excel信息失败，获取手机号码失败
    			}
    		}
    	}
    }
    
    private String smsContent(String content,int index, String replacement){
    	String key ="\\[\\("+Integer.toString(index+10, 36).toUpperCase()+"\\)\\]";
    	content = content.replaceAll(key, replacement);
    	return content;
    }
    

	public String getResult() {
		return code;
	}
}  
