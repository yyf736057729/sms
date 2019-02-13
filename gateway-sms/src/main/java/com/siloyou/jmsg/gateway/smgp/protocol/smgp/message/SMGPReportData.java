package com.siloyou.jmsg.gateway.smgp.protocol.smgp.message;

import com.siloyou.jmsg.gateway.smgp.protocol.smgp.util.ByteUtil;



public class SMGPReportData {

	
	public static final int LENGTH=10+3+3+10+10+7+3+20 +   "id: sub: dlvrd: submit date: done date: stat: err: text:".length();
	
	private byte[] msgId=new byte[10]; //10
	private String sub="001"; //3
	private String dlvrd="001"; //3
	private String subTime; //10;
	private String doneTime; //10;
	private String stat; //7
	private String err=""; //3
	private String txt=""; //20
	
	//id:XXXXXXXXXX sub:000 dlvrd:000 submit date:0901151559 done date:0901151559 stat:DELIVRD err:000 text:
	public boolean fromBytes(byte[] bytes) throws Exception{

		int offset=0;
		byte[] tmp = null;
		
		offset+="id:".length();
		
		msgId=new byte[10];
		System.arraycopy(bytes, offset, msgId, 0, 10);
		offset+=10;
		
		offset+=" sub:".length();
		
		tmp = new byte[3];
		System.arraycopy(bytes, offset, tmp, 0, 3);
		sub = new String(ByteUtil.rtrimBytes(tmp));
		offset += 3;

		offset+=" dlvrd:".length();
		
		tmp = new byte[3];
		System.arraycopy(bytes, offset, tmp, 0, 3);
		dlvrd = new String(ByteUtil.rtrimBytes(tmp));
		offset += 3;

		offset+=" submit date:".length();
		
		tmp = new byte[10];
		System.arraycopy(bytes, offset, tmp, 0, 10);
		subTime = new String(ByteUtil.rtrimBytes(tmp));
		offset += 10;

		offset+=" done date:".length();
		
		tmp = new byte[10];
		System.arraycopy(bytes, offset, tmp, 0, 10);
		doneTime = new String(ByteUtil.rtrimBytes(tmp));
		offset += 10;
		
		offset+=" stat:".length();

		tmp = new byte[7];
		System.arraycopy(bytes, offset, tmp, 0, 7);
		stat = new String(ByteUtil.rtrimBytes(tmp));
		offset += 7;
		
		offset+=" err:".length();

		tmp = new byte[3];
		System.arraycopy(bytes, offset, tmp, 0, 3);
		err = new String(ByteUtil.rtrimBytes(tmp));
		offset += 3;
		
//		offset+=" text:".length();
//
//		tmp = new byte[20];
//		System.arraycopy(bytes, offset, tmp, 0, 20);
//		txt = new String(ByteUtil.rtrimBytes(tmp));
//		offset += 20;

		
		return true;
	}
	
	public byte[] toBytes()throws Exception{
	    int offset=0;
	    byte[] bytes=new byte[LENGTH];
	    
	    System.arraycopy("id:".getBytes(), 0, bytes, offset, "id:".length());
	    offset+="id:".length();
	    
	    System.arraycopy(msgId, 0, bytes, offset, 10);
		offset += 10;
		
		System.arraycopy(" sub:".getBytes(), 0, bytes, offset, " sub:".length());
	    offset+=" sub:".length();
	    
	    
		ByteUtil.rfillBytes(sub.getBytes(), 3, bytes, offset);
		offset += 3;

		System.arraycopy(" dlvrd:".getBytes(), 0, bytes, offset, " dlvrd:".length());
	    offset+=" dlvrd:".length();
	    
	    
		ByteUtil.rfillBytes(dlvrd.getBytes(), 3, bytes, offset);
		offset += 3;

		System.arraycopy(" submit date:".getBytes(), 0, bytes, offset, " submit date:".length());
	    offset+=" submit date:".length();
	    
	    
		ByteUtil.rfillBytes(subTime.getBytes(), 10, bytes, offset);
		offset += 10;

		System.arraycopy(" done date:".getBytes(), 0, bytes, offset, " done date:".length());
	    offset+=" done date:".length();
	    
	    
		ByteUtil.rfillBytes(doneTime.getBytes(), 10, bytes, offset);
		offset += 10;

		System.arraycopy(" stat:".getBytes(), 0, bytes, offset, " stat:".length());
	    offset+=" stat:".length();
	    
	    
		ByteUtil.rfillBytes(stat.getBytes(), 7, bytes, offset);
		offset += 7;

		System.arraycopy(" err:".getBytes(), 0, bytes, offset, " err:".length());
	    offset+=" err:".length();
	    
	    
		ByteUtil.rfillBytes(err.getBytes(), 3, bytes, offset);
		offset += 3;

		System.arraycopy(" text:".getBytes(), 0, bytes, offset, " text:".length());
	    offset+=" text:".length();
	    
	    
		ByteUtil.rfillBytes(txt.getBytes(), 20, bytes, offset);
		offset += 20;

	    return bytes;
	}

	public String getDlvrd() {
		return dlvrd;
	}

	public void setDlvrd(String dlvrd) {
		this.dlvrd = dlvrd;
	}

	public String getDoneTime() {
		return doneTime;
	}

	public void setDoneTime(String doneTime) {
		this.doneTime = doneTime;
	}

	public String getErr() {
		return err;
	}

	public void setErr(String err) {
		this.err = err;
	}

	public byte[] getMsgId() {
		return msgId;
	}

	public void setMsgId(byte[] msgId) {
		this.msgId = msgId;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

	public String getSubTime() {
		return subTime;
	}

	public void setSubTime(String subTime) {
		this.subTime = subTime;
	}

	public String getTxt() {
		return txt;
	}

	public void setTxt(String txt) {
		this.txt = txt;
	}
	
	public String msgIdString(){
		return ByteUtil.byteArrayToHexString(msgId);
	}
	
	public String toString(){
		StringBuffer buffer=new StringBuffer();
		buffer.append("{msgId=").append(msgIdString()).append(",")
		      .append("sub=").append(sub).append(",")
		      .append("dlvrd=").append(dlvrd).append(",")
		      .append("subTime=").append(subTime).append(",")
		      .append("doneTime=").append(doneTime).append(",")
		      .append("stat=").append(stat).append(",")
		      .append("err=").append(err).append(",")
		      .append("text=").append(txt).append("}");
		return buffer.toString();
	}
}
