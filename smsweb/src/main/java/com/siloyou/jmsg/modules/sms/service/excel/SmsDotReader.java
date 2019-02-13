package com.siloyou.jmsg.modules.sms.service.excel;

import java.util.List;

import com.sanerzone.common.support.utils.ValidatorUtils;
import com.siloyou.core.common.utils.excel.IRowReader;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDot;

public class SmsDotReader implements IRowReader{  
	
	private List<JmsgSmsDot> list;
	private String code = "1";
	private JmsgSmsDot jmsgSmsDot;
	
	public SmsDotReader(List<JmsgSmsDot> list){
		this.list = list;
	}
	
    /* 业务逻辑实现方法 
     */  
    public void getRows(int sheetIndex, int curRow, List<String> rowlist) {
    	if("1".equals(code)){
	    	if(rowlist.size() ==2){
	        	jmsgSmsDot = new JmsgSmsDot();
	        	jmsgSmsDot.setPhone(rowlist.get(0));
	        	jmsgSmsDot.setSmsContent(rowlist.get(1));
	        	if(ValidatorUtils.isMobile(jmsgSmsDot.getPhone())){//判断是否手机号码
	        		list.add(jmsgSmsDot);
	        	}
	    	}else{
	    		code = "-1";
	    	}
    	}
    }

	public String getResult() {
		return code;
	}
}  
