/**
 * 
 */
package com.zx.sms.codec.sgip12.packet;

import io.netty.handler.codec.MessageToMessageCodec;

import com.zx.sms.codec.cmpp.packet.PacketStructure;
import com.zx.sms.codec.cmpp.packet.PacketType;
import com.zx.sms.codec.sgip12.codec.SgipBindRequestMessageCodec;
import com.zx.sms.codec.sgip12.codec.SgipBindResponseMessageCodec;
import com.zx.sms.codec.sgip12.codec.SgipDeliverRequestMessageCodec;
import com.zx.sms.codec.sgip12.codec.SgipDeliverResponseMessageCodec;
import com.zx.sms.codec.sgip12.codec.SgipReportRequestMessageCodec;
import com.zx.sms.codec.sgip12.codec.SgipReportResponseMessageCodec;
import com.zx.sms.codec.sgip12.codec.SgipSubmitRequestMessageCodec;
import com.zx.sms.codec.sgip12.codec.SgipSubmitResponseMessageCodec;
import com.zx.sms.codec.sgip12.codec.SgipUnbindRequestMessageCodec;
import com.zx.sms.codec.sgip12.codec.SgipUnbindResponseMessageCodec;

/**
 * @author huzorro(huzorro@gmail.com)
 *
 */
public enum SgipPacketType implements PacketType {
	BINDREQUEST(0x00000001L, SgipBindRequest.class,SgipBindRequestMessageCodec.class),
	BINDRESPONSE(0x80000001L, SgipBindResponse.class,SgipBindResponseMessageCodec.class),
	UNBINDREQUEST(0x00000002L, SgipUnbindRequest.class,SgipUnbindRequestMessageCodec.class),
	UNBINDRESPONSE(0x80000002L, SgipUnbindResponse.class,SgipUnbindResponseMessageCodec.class),
	SUBMITREQUEST(0x00000003L, SgipSubmitRequest.class,SgipSubmitRequestMessageCodec.class),
	SUBMITRESPONSE(0x80000003L, SgipSubmitResponse.class,SgipSubmitResponseMessageCodec.class),
	DELIVERREQUEST(0x00000004L, SgipDeliverRequest.class,SgipDeliverRequestMessageCodec.class),
	DELIVERRESPONSE(0x80000004L, SgipDeliverResponse.class,SgipDeliverResponseMessageCodec.class),
	REPORTREQUEST(0x00000005L, SgipReportRequest.class,SgipReportRequestMessageCodec.class),
	REPORTRESPONSE(0x80000005L, SgipReportResponse.class,SgipReportResponseMessageCodec.class);

    private long commandId;
    private Class<? extends PacketStructure> packetStructure;
    private Class<? extends MessageToMessageCodec> codec;
    private SgipPacketType(long commandId, Class<? extends PacketStructure> packetStructure,Class<? extends MessageToMessageCodec> codec) {
        this.commandId = commandId;
        this.packetStructure = packetStructure;
        this.codec = codec;
    }
    public long getCommandId() {
        return commandId;
    }
    public PacketStructure[] getPacketStructures() {
    	return packetStructure.getEnumConstants();
    }

    public long getAllCommandId() {
        long defaultId = 0x0;
        long allCommandId = 0x0;
        for(SgipPacketType packetType : SgipPacketType.values()) {
            allCommandId |= packetType.commandId;
        }
        return allCommandId ^ defaultId;
    }
	@Override
	public MessageToMessageCodec getCodec() {
		try {
			return codec.newInstance();
		} catch (InstantiationException e) {
			return null;
		}
		catch(  IllegalAccessException e){
			return null;
		}
	}
}
