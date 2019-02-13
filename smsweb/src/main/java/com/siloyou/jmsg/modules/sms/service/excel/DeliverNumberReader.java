package com.siloyou.jmsg.modules.sms.service.excel;

import java.util.List;

import com.siloyou.core.common.utils.excel.IRowReader;
import com.siloyou.jmsg.modules.sms.entity.JmsgDeliverNumber;

public class DeliverNumberReader implements IRowReader{  
	
	private List<JmsgDeliverNumber> list;
	private String code = "1";
	private JmsgDeliverNumber jmsgDeliverNumber;
	
	public DeliverNumberReader(List<JmsgDeliverNumber> list){
		this.list = list;
	}
	
    /* 业务逻辑实现方法 
     */  
    public void getRows(int sheetIndex, int curRow, List<String> rowlist) {
    	if("1".equals(code)){
	    	if(rowlist.size() ==2){
	    		jmsgDeliverNumber = new JmsgDeliverNumber();
	    		jmsgDeliverNumber.setUserId(rowlist.get(0));
	    		jmsgDeliverNumber.setSpNumber(rowlist.get(1));
	        	list.add(jmsgDeliverNumber);
	    	}else{
	    		code = "-1";
	    	}
    	}
    }

	public String getResult() {
		return code;
	}
}  
