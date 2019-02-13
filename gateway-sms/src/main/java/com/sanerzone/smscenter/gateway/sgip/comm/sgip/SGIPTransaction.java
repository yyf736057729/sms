// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2005-7-16 19:21:47
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   SGIPTransaction.java

package com.sanerzone.smscenter.gateway.sgip.comm.sgip;

import com.sanerzone.smscenter.gateway.sgip.comm.PException;
import com.sanerzone.smscenter.gateway.sgip.comm.PLayer;
import com.sanerzone.smscenter.gateway.sgip.comm.PMessage;
import com.sanerzone.smscenter.gateway.sgip.comm.sgip.message.SGIPMessage;
import com.sanerzone.smscenter.gateway.sgip.util.Debug;

// Referenced classes of package com.huawei.insa2.comm.sgip:
//            SGIPConnection, SGIPConstant

public class SGIPTransaction extends PLayer {

	public SGIPTransaction(PLayer connection) {
		super(connection);
		sequenceId = super.id;
	}

	public void setSPNumber(int spNumber) {
		src_nodeid = spNumber;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public synchronized void onReceive(PMessage msg) {
		receive = (SGIPMessage) msg;
		src_nodeid = receive.getSrcNodeId();
		timestamp = receive.getTimeStamp();
		sequenceId = receive.getSequenceId();
		if (SGIPConstant.debug)
			Debug.dump("Recv==>" + receive.toString());
		notifyAll();
	}

	public void send(PMessage message) throws PException {
		SGIPMessage mes = (SGIPMessage) message;
		mes.setSrcNodeId(src_nodeid);
		mes.setTimeStamp(timestamp);
		mes.setSequenceId(sequenceId);
		super.parent.send(message);
		if (SGIPConstant.debug)
			Debug.dump("Send==>" + mes.toString());
	}

	public SGIPMessage getResponse() {
		return receive;
	}

	public boolean isChildOf(PLayer connection) {
		if (super.parent == null)
			return false;
		else
			return connection == super.parent;
	}

	public PLayer getParent() {
		return super.parent;
	}

	public synchronized void waitResponse() {
		
		int transactionTimeout = ((SGIPConnection) super.parent).getTransactionTimeout();
		int sleepTime = 0;
		while (receive == null) {
			try {
				wait(2);
				if(receive == null)
					wait(8);
				
				sleepTime = sleepTime + 10;
				if(receive != null || sleepTime >= transactionTimeout) {
					System.out.println("接收成功：" + sleepTime);
					break;
				}
				
			} catch (InterruptedException interruptedexception) {
			}
		}

//		if (receive == null)
//			try {
//				wait(((SGIPConnection) super.parent).getTransactionTimeout());
//			} catch (InterruptedException interruptedexception) {
//			}
	}

	public String getSubmitMsgId() {
		return String.valueOf(src_nodeid) + String.valueOf(timestamp) + String.valueOf(sequenceId);
	}

	private SGIPMessage receive;
	private int src_nodeid;
	private int timestamp;
	private int sequenceId;
}