package com.siloyou.jmsg.gateway.wav.message;

/**
 * 通知消息体
 * 
 * @author  zhangjie
 * @version  [版本号, 2016年10月9日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class VoiceMsgBody
{
    private String reqId;           //请求ID，唯一，回执时唯一ID
    private String to;              //通知接收号码
    private String verifyCode;      //验证码，只用来记录
    private String lang;            //通知语言，取值：中文，默认，LANG_ZH：5000, 英文：LANG_EN：5001
    private String playVerifyCode;  //播放的语言文件，最大32个语言文件，用;分隔
    
    public String getPlayVerifyCode()
    {
        return playVerifyCode;
    }
    public void setPlayVerifyCode(String playVerifyCode)
    {
        this.playVerifyCode = playVerifyCode;
    }
    public String getReqId()
    {
        return reqId;
    }
    public void setReqId(String reqId)
    {
        this.reqId = reqId;
    }
    public String getTo()
    {
        return to;
    }
    public void setTo(String to)
    {
        this.to = to;
    }
    public String getVerifyCode()
    {
        return verifyCode;
    }
    public void setVerifyCode(String verifyCode)
    {
        this.verifyCode = verifyCode;
    }
    public String getLang()
    {
        return lang;
    }
    public void setLang(String lang)
    {
        this.lang = lang;
    }
}
