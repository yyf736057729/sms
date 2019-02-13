package com.sanerzone.smscenter.gateway.sgip;

import com.sanerzone.smscenter.gateway.sgip.comm.sgip.message.SGIPDeliverMessage;
import com.sanerzone.smscenter.gateway.sgip.comm.sgip.message.SGIPReportMessage;
import com.sanerzone.smscenter.gateway.sgip.util.Args;

public interface MoListener {
	public void onDeliver(SGIPDeliverMessage deliver, Args args);
	
	public void onReport(SGIPReportMessage report, Args args);
	
	public void onTerminate(Args args);
}
