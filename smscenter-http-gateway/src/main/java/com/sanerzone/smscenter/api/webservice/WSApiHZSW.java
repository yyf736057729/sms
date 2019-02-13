package com.sanerzone.smscenter.api.webservice;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.sanerzone.smscenter.common.tools.MQHelper;

@WebService  
public class WSApiHZSW {  
	
	public static Logger logger = LoggerFactory.getLogger(WSApiHZSW.class);
	
	@Autowired
    private MQHelper mQUtils;
     
    public String sendMsg(String platCode, String sendNumber, String msgContent){ 
    	return "ok";
    }  
    
    public String getReport(String msgId) {
    	return msgId;
    }
}  