package com.siloyou.jmsg.gateway.smgp.protocol.smgp.message;

import com.siloyou.jmsg.gateway.smgp.protocol.smgp.tlv.TLVByte;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.tlv.TLVString;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.util.ByteUtil;

public class SMGPSubmitMessage extends SMGPBaseMessage {



	private byte msgType=(byte)6; // 1

	private byte needReport=1; // 1

	private byte priority; // 1

	private String serviceId=""; // 10

	private String feeType="00"; // 2

	private String feeCode="000000"; // 6

	private String fixedFee="000000"; // 6

	private byte msgFmt; // 1

	private String validTime=""; // 17

	private String atTime=""; // 17

	private String srcTermId=""; // 21

	private String chargeTermId=""; // 21

	private byte destTermIdCount; // 1

	private String[] destTermIdArray; // 21*destTermIdCount

	private int msgLength; // 1

	private String msgContent;
	
	private byte[] bMsgContent; // msgLength

	private String reserve=""; // 8
	
	
	public SMGPSubmitMessage() {
		this.commandId = SMGPConstants.SMGP_SUBMIT;
		registerOptional(tpPid);
		registerOptional(tpUdhi);
		registerOptional(linkId);
		registerOptional(msgSrc);
		registerOptional(chargeUserType);
		registerOptional(chargeTermType);
		registerOptional(chargeTermPseudo);
		registerOptional(destTermType);
		registerOptional(destTermPseudo);
		registerOptional(pkTotal);
		registerOptional(pkNumber);
		registerOptional(submitMsgType);
		registerOptional(spDealResult);
		registerOptional(mServiceId);
	}
	
	
	private TLVByte     tpPid   =new TLVByte(SMGPConstants.OPT_TP_PID);
	private TLVByte     tpUdhi  =new TLVByte(SMGPConstants.OPT_TP_UDHI);
	private TLVString   linkId  =new TLVString(SMGPConstants.OPT_LINK_ID);
	private TLVString   msgSrc  =new TLVString(SMGPConstants.OPT_MSG_SRC);
	private TLVByte     chargeUserType=new TLVByte(SMGPConstants.OPT_CHARGE_USER_TYPE);
	private TLVByte     chargeTermType=new TLVByte(SMGPConstants.OPT_CHARGE_TERM_TYPE);
	private TLVString   chargeTermPseudo=new TLVString(SMGPConstants.OPT_CHARGE_TERM_PSEUDO);
	private TLVByte     destTermType=new TLVByte(SMGPConstants.OPT_DEST_TERM_TYPE);
	private TLVString   destTermPseudo=new TLVString(SMGPConstants.OPT_DEST_TERM_PSEUDO);
	private TLVByte     pkTotal=new TLVByte(SMGPConstants.OPT_PK_TOTAL);
	private TLVByte     pkNumber=new TLVByte(SMGPConstants.OPT_PK_NUMBER);
	private TLVByte     submitMsgType=new TLVByte(SMGPConstants.OPT_SUBMIT_MSG_TYPE);
	private TLVByte     spDealResult=new TLVByte(SMGPConstants.OPT_SP_DEAL_RESULT);
	private TLVString   mServiceId=new TLVString(SMGPConstants.OPT_M_SERVICE_ID);
	
	public void setTpPid(byte value){
		tpPid.setValue(value);
	}
	public byte getTpPid(){
		return tpPid.getValue();
	}
	public void setTpUdhi(byte value){
		tpUdhi.setValue(value);
	}
	public byte getTpUdhi(){
		return tpUdhi.getValue();
	}
	public void setLinkId(String value){
		linkId.setValue(value);
	}
	public String getLinkId(){
		return linkId.getValue();
	}
	public void setMsgSrc(String value){
		msgSrc.setValue(value);
	}
	public String getMsgSrc(){
		return msgSrc.getValue();
	}
	public void setChargeUserType(byte value){
		chargeUserType.setValue(value);
	}
	public byte getChargeUserType(){
		return chargeUserType.getValue();
	}
	
	public void setChargeTermType(byte value){
		chargeTermType.setValue(value);
	}
	public byte getChargeTermType(){
		return chargeTermType.getValue();
	}
	
	public void setChargeTermPseudo(String value){
		chargeTermPseudo.setValue(value);
	}
	public String getChargeTermPseudo(){
		return chargeTermPseudo.getValue();
	}
	
	public void setDestTermType(byte value){
		destTermType.setValue(value);
	}
	public byte getDestTermType(){
		return destTermType.getValue();
	}
	
	public void setDestTermPseudo(String value){
		destTermPseudo.setValue(value);
	}
	public String getDestTermPseudo(){
		return destTermPseudo.getValue();
	}	
	

	public void setPkTotal(byte value){
		pkTotal.setValue(value);
	}
	public byte getPkTotal(){
		return pkTotal.getValue();
	}
	
	public void setPkNumber(byte value){
		pkNumber.setValue(value);
	}
	public byte getPkNumber(){
		return pkNumber.getValue();
	}
	
	public void setSubmitMsgType(byte value){
		submitMsgType.setValue(value);
	}
	public byte getSubmitMsgType(){
		return submitMsgType.getValue();
	}
	
	public void setSpDealResult(byte value){
		spDealResult.setValue(value);
	}
	public byte getSpDealResult(){
		return spDealResult.getValue();
	}

	public void setMServiceId(String value){
		mServiceId.setValue(value);
	}
	public String getMServiceId(){
		return mServiceId.getValue();
	}	
	
	
	
	@Override
	protected int setBody(byte[] bodyBytes) throws Exception {
		int offset = 0;
		byte[] tmp = null;

		msgType = bodyBytes[offset];
		offset += 1;

		needReport = bodyBytes[offset];
		offset += 1;

		priority = bodyBytes[offset];
		offset += 1;

		tmp = new byte[10];
		System.arraycopy(bodyBytes, offset, tmp, 0, 10);
		serviceId = new String(ByteUtil.rtrimBytes(tmp));
		offset += 10;

		tmp = new byte[2];
		System.arraycopy(bodyBytes, offset, tmp, 0, 2);
		feeType = new String(ByteUtil.rtrimBytes(tmp));
		offset += 2;

		tmp = new byte[6];
		System.arraycopy(bodyBytes, offset, tmp, 0, 6);
		feeCode = new String(ByteUtil.rtrimBytes(tmp));
		offset += 6;

		tmp = new byte[6];
		System.arraycopy(bodyBytes, offset, tmp, 0, 6);
		fixedFee = new String(ByteUtil.rtrimBytes(tmp));
		offset += 6;

		msgFmt = bodyBytes[offset];
		offset += 1;

		tmp = new byte[17];
		System.arraycopy(bodyBytes, offset, tmp, 0, 17);
		validTime = new String(ByteUtil.rtrimBytes(tmp));
		offset += 17;

		tmp = new byte[17];
		System.arraycopy(bodyBytes, offset, tmp, 0, 17);
		atTime = new String(ByteUtil.rtrimBytes(tmp));
		offset += 17;

		tmp = new byte[21];
		System.arraycopy(bodyBytes, offset, tmp, 0, 21);
		srcTermId = new String(ByteUtil.rtrimBytes(tmp));
		offset += 21;

		tmp = new byte[21];
		System.arraycopy(bodyBytes, offset, tmp, 0, 21);
		chargeTermId = new String(ByteUtil.rtrimBytes(tmp));
		offset += 21;

		destTermIdCount = bodyBytes[offset];
		offset += 1;


		if (destTermIdCount >= 100 || destTermIdCount <= 0) {
			throw new Exception("destTermIdCount must be in [1,99],but " + destTermIdCount);
		}
		destTermIdArray = new String[destTermIdCount];
		for (int i = 0; i < destTermIdCount; i++) {
			tmp = new byte[21];
			System.arraycopy(bodyBytes, offset, tmp, 0, 21);
			offset += 21;
			destTermIdArray[i] = new String(ByteUtil.rtrimBytes(tmp));
		}
		byte b = bodyBytes[offset];
		offset += 1;

	     msgLength = b >= 0 ? b : (256 + b); // byte 最大只有128，这种处理可以取得129-140的数据
		if(msgLength>0){
			tmp = new byte[msgLength];
			System.arraycopy(bodyBytes, offset, tmp, 0, msgLength);
			offset += msgLength;
			bMsgContent = tmp;
			try {
				if (msgFmt == 8)
					msgContent = new String(bMsgContent, "iso-10646-ucs-2");
				else if (msgFmt == 15)
					msgContent = new String(bMsgContent, "GBK");
				else
					msgContent = new String(bMsgContent, "iso-8859-1");
				msgContent=msgContent.trim();
			} catch (Exception ex) {
				throw ex;
			}
		}

		tmp = new byte[8];
		System.arraycopy(bodyBytes, offset, tmp, 0, 8);
		reserve = new String(ByteUtil.rtrimBytes(tmp));
		offset += 8;

		return offset;
	}

	@Override
	protected byte[] getBody() throws Exception {
		int len =0+1 + 1 + 1 + 10 + 2 + 6 + 6 + 1 + 17 + 17 + 21 + 21 + 1 + 21* destTermIdCount + 1 + msgLength + 8;
		int offset = 0;
		byte[] bodyBytes = new byte[len];
		bodyBytes[offset] = msgType;
		offset += 1;

		bodyBytes[offset] = needReport;
		offset += 1;

		bodyBytes[offset] = priority;
		offset += 1;

		ByteUtil.rfillBytes(serviceId.getBytes(), 10, bodyBytes, offset);
		offset += 10;

		ByteUtil.rfillBytes(feeType.getBytes(), 2, bodyBytes, offset);
		offset += 2;

		ByteUtil.rfillBytes(feeCode.getBytes(), 6, bodyBytes, offset);
		offset += 6;

		ByteUtil.rfillBytes(fixedFee.getBytes(), 6, bodyBytes, offset);
		offset += 6;

		bodyBytes[offset] = msgFmt;
		offset += 1;

		ByteUtil.rfillBytes(validTime.getBytes(), 17, bodyBytes, offset);
		offset += 17;

		ByteUtil.rfillBytes(atTime.getBytes(), 17, bodyBytes, offset);
		offset += 17;

		ByteUtil.rfillBytes(srcTermId.getBytes(), 21, bodyBytes, offset);
		offset += 21;

		ByteUtil.rfillBytes(chargeTermId.getBytes(), 21, bodyBytes, offset);
		offset += 21;

		bodyBytes[offset] = destTermIdCount;
		offset += 1;

		for (int i = 0; i < destTermIdCount; i++) {
			ByteUtil.rfillBytes(destTermIdArray[i].getBytes(), 21,
					bodyBytes, offset);
			offset += 21;
		}

		bodyBytes[offset] = (byte)msgLength;
		offset += 1;

		if (bMsgContent == null && msgContent!=null) {
			try {
				if (msgFmt == 8)
					bMsgContent = msgContent.getBytes("iso-10646-ucs-2");
				else if (msgFmt == 15)
					bMsgContent = msgContent.getBytes("GBK");
				else
					bMsgContent = msgContent.getBytes("iso-8859-1");
			} catch (Exception ex) {
			}
		}

		if (bMsgContent != null) {
			ByteUtil.rfillBytes(bMsgContent, msgLength, bodyBytes, offset);			
		}
		offset+=msgLength;
		
		ByteUtil.rfillBytes(reserve.getBytes(), 8, bodyBytes, offset);
		offset += 8;

		return bodyBytes;
	}

	public byte getMsgType() {
		return this.msgType;
	}

	public void setMsgType(byte msgType) {
		this.msgType = msgType;
	}

	public byte getNeedReport() {
		return this.needReport;
	}

	public void setNeedReport(byte needReport) {
		this.needReport = needReport;
	}

	public byte getPriority() {
		return this.priority;
	}

	public void setPriority(byte priority) {
		this.priority = priority;
	}

	public String getServiceId() {
		return this.serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getFeeType() {
		return this.feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getFeeCode() {
		return this.feeCode;
	}

	public void setFeeCode(String feeCode) {
		this.feeCode = feeCode;
	}

	public String getFixedFee() {
		return this.fixedFee;
	}

	public void setFixedFee(String fixedFee) {
		this.fixedFee = fixedFee;
	}

	public byte getMsgFmt() {
		return this.msgFmt;
	}

	public void setMsgFmt(byte msgFmt) {
		this.msgFmt = msgFmt;
	}

	public String getValidTime() {
		return this.validTime;
	}

	public void setValidTime(String validTime) {
		this.validTime = validTime;
	}

	public String getAtTime() {
		return this.atTime;
	}

	public void setAtTime(String atTime) {
		this.atTime = atTime;
	}

	public String getSrcTermId() {
		return this.srcTermId;
	}

	public void setSrcTermId(String srcTermId) {
		this.srcTermId = srcTermId;
	}

	public String getChargeTermId() {
		return this.chargeTermId;
	}

	public void setChargeTermId(String chargeTermId) {
		this.chargeTermId = chargeTermId;
	}

	public byte getDestTermIdCount() {
		return this.destTermIdCount;
	}

	public void setDestTermIdCount(byte destTermIdCount) {
		this.destTermIdCount = destTermIdCount;
	}



	public String[] getDestTermIdArray() {
		return destTermIdArray;
	}

	public void setDestTermIdArray(String[] destTermIdArray) {
		this.destTermIdArray = destTermIdArray;
		this.destTermIdCount=(byte)(destTermIdArray==null?0:destTermIdArray.length);
	}

	public String getMsgContent() {
		if(msgContent!=null)
		   return msgContent;
		if(bMsgContent!=null){
			String msg=null;
			try {
				if (msgFmt == 8) {
					msg = new String(bMsgContent, "iso-10646-ucs-2");
				} else if (msgFmt == 15) {
					msg = new String(bMsgContent, "GBK");
				} else {
					msg = new String(bMsgContent, "iso-8859-1");
				}
			} catch (Exception ex) {
			}
			return msg;
		}
		return null;
	}

	public void setMsgContent(String msgContent) {
		if (msgContent != null) {
			this.bMsgContent = msgContent.getBytes();
			this.msgContent = msgContent;
			if (msgContent.getBytes().length == msgContent.length()) {
				this.msgLength = msgContent.length();
			} else {
				this.msgLength = msgContent.length() * 2;
			}
		} else {
			this.msgLength = 0;
			this.bMsgContent = null;
			this.msgContent = null;
		}
	}


	public int getMsgLength() {
		return this.msgLength;
	}

	public void setMsgLength(int msgLength) {
		this.msgLength = msgLength;
	}

	public byte[] getBMsgContent() {
		return this.bMsgContent;
	}

	public void setBMsgContent(byte[] msgContent) {
		bMsgContent = msgContent;
		msgLength = bMsgContent == null ? 0 : bMsgContent.length;
	}

	public String getReserve() {
		return this.reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}
	
	

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("SMGPSubmitMessage:[sequenceNumber=").append(
				sequenceString()).append(",");
		buffer.append("msgType=").append(msgType).append(",");
		buffer.append("needReport=").append(needReport).append(",");
		buffer.append("priority=").append(priority).append(",");
		buffer.append("serviceId=").append(serviceId).append(",");
		buffer.append("feeType=").append(feeType).append(",");
		buffer.append("feeCode=").append(feeCode).append(",");
		buffer.append("fixedFee=").append(fixedFee).append(",");
		buffer.append("msgFmt=").append(msgFmt).append(",");
		buffer.append("validTime=").append(validTime).append(",");
		buffer.append("atTime=").append(atTime).append(",");
		buffer.append("srcTermId=").append(srcTermId).append(",");
		buffer.append("chargeTermId=").append(chargeTermId).append(",");
		buffer.append("destTermIdArray={");
		for (int i = 0; i < destTermIdCount; i++) {
			if (i == 0) {
				buffer.append(destTermIdArray[i]);
			} else {
				buffer.append(";" + destTermIdArray[i]);
			}
		}
		buffer.append("},");
		buffer.append("msgLength=").append(msgLength).append(",");
		buffer.append("msgContent=").append(getMsgContent()).append("]");
		return buffer.toString();
	}
}