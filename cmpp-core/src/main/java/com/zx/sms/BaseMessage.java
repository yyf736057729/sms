package com.zx.sms;

import java.io.Serializable;

public interface BaseMessage extends Serializable{
	public boolean isRequest();
	public boolean isResponse();
	public boolean isTerminated();
    public void setRequest(BaseMessage message);
    public BaseMessage getRequest();
    public long getSequenceNo();
    public void setSequenceNo(long seq);
}
