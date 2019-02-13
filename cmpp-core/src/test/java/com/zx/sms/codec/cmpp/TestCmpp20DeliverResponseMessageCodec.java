package com.zx.sms.codec.cmpp;

import io.netty.buffer.ByteBuf;

import org.junit.Assert;
import org.junit.Test;

import com.zx.sms.codec.AbstractTestMessageCodec;
import com.zx.sms.codec.cmpp.msg.CmppDeliverResponseMessage;
import com.zx.sms.codec.cmpp.packet.CmppHead;
import com.zx.sms.codec.cmpp20.packet.Cmpp20DeliverResponse;
import com.zx.sms.common.util.MsgId;

public class TestCmpp20DeliverResponseMessageCodec extends AbstractTestMessageCodec<CmppDeliverResponseMessage> {
	@Override
	protected int getVersion(){
		return 0x20;
	}
	@Test
	public void testCode()
	{
		CmppDeliverResponseMessage msg = new CmppDeliverResponseMessage(238L);
		
		msg.setMsgId(new MsgId());
		
		msg.setResult(3413);
		ByteBuf buf = encode(msg);
		ByteBuf copybuf = buf.copy();
		
		int length = buf.readableBytes();
		int expectLength = Cmpp20DeliverResponse.RESULT.getBodyLength() + + CmppHead.COMMANDID.getHeadLength();
		
		Assert.assertEquals(expectLength, length);
		Assert.assertEquals(expectLength, buf.readUnsignedInt());
		Assert.assertEquals(msg.getPacketType().getCommandId(), buf.readUnsignedInt());
		Assert.assertEquals(msg.getHeader().getSequenceId(), buf.readUnsignedInt());
		
		CmppDeliverResponseMessage result = decode(copybuf);
		
		Assert.assertEquals(msg.getHeader().getSequenceId(), result.getHeader().getSequenceId());

		Assert.assertEquals(msg.getMsgId(), result.getMsgId());
		Assert.assertEquals(msg.getResult()&0xff, result.getResult());
	}
}
