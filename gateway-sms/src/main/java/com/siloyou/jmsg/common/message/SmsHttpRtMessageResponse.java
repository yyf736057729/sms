package com.siloyou.jmsg.common.message;

import java.io.Serializable;

public class SmsHttpRtMessageResponse implements Serializable
{
    private static final long serialVersionUID = 1L;
    public static String CONTENTTYPE_TEXTPLAIN = "text/plain";
    public static String CONTENTTYPE_JSON = "application/json";
    
    String result ;
    String contentType ;
    String characterEncoding ;
    
    public SmsHttpRtMessageResponse(String result){
    	this(result, CONTENTTYPE_TEXTPLAIN, "utf-8");
    }
    
    public SmsHttpRtMessageResponse(String result, String contentType, String characterEncoding){
    	this.characterEncoding = characterEncoding;
    	this.contentType = contentType;
    	this.result = result;
    }
    
    public String getResult()
    {
        return result;
    }
    public void setResult(String result)
    {
        this.result = result;
    }
    public String getContentType()
    {
        return contentType;
    }
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }
    public String getCharacterEncoding()
    {
        return characterEncoding;
    }
    public void setCharacterEncoding(String characterEncoding)
    {
        this.characterEncoding = characterEncoding;
    }
    
}
