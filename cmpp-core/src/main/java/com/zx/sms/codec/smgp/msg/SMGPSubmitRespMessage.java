package com.zx.sms.codec.smgp.msg;

import com.zx.sms.codec.smgp.util.ByteUtil;
import com.zx.sms.codec.smgp.util.SMGPMsgIdUtil;

public class SMGPSubmitRespMessage extends SMGPBaseMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2439336812942466396L;

	public SMGPSubmitRespMessage() {
		this.commandId = SMGPConstants.SMGP_SUBMIT_RESP;
	}

	private MsgId msgId = new MsgId(); // 10

	private int status; // 4

	@Override
	protected int setBody(byte[] bodyBytes) throws Exception {
		int offset = 0;
		
		byte[]msgId=new byte[10];
		System.arraycopy(bodyBytes, offset, msgId, 0, 10);
		this.msgId = SMGPMsgIdUtil.bytes2MsgId(msgId);
		offset += 10;

		status = ByteUtil.byte2int(bodyBytes, offset);
		offset += 4;

		return offset;
	}

	@Override
	protected byte[] getBody() throws Exception {
		int len = +10 + 4;
		int offset = 0;
		byte[] bodyBytes = new byte[len];
		byte[] b_msgId = SMGPMsgIdUtil.msgId2Bytes(msgId);
		System.arraycopy(b_msgId, 0, bodyBytes, offset, 10);
		offset += 10;

		ByteUtil.int2byte(status, bodyBytes, offset);
		offset += 4;

		return bodyBytes;
	}

	public MsgId getMsgId() {
		return this.msgId;
	}

	public void setMsgId(MsgId msgId) {
		this.msgId = msgId;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	private String msgIdString(){
		return msgId.toString();
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SMGPSubmitRespMessage:[sequenceNumber=").append(
				sequenceString()).append(",");
		buffer.append("msgId=").append(msgIdString()).append(",");
		buffer.append("status=").append(status).append("]");
		return buffer.toString();
	}
}