package com.zx.sms.codec.cmpp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.junit.Assert;
import org.junit.Test;

import com.zx.sms.codec.AbstractTestMessageCodec;
import com.zx.sms.codec.cmpp.msg.CmppDeliverRequestMessage;

import static com.zx.sms.common.util.NettyByteBufUtil.*;
/**
 * 数据短信，无法解析
 * */
public class MsgErrUDHIDeliverRequestDecoder extends AbstractTestMessageCodec<CmppDeliverRequestMessage> {
	@Override
	protected int getVersion() {
		return 0x20;
	}

	@Test
	public void testDecode() {
		byte[] expected = prepareMsgData();
		byte[] actuals = new byte[expected.length];
		ByteBuf buf = Unpooled.wrappedBuffer(expected);
		int index = 0;
		ch.writeInbound(buf);
		CmppDeliverRequestMessage result = null;
		while (null != (result = (CmppDeliverRequestMessage) ch.readInbound())) {
			System.out.println(result);
			ByteBuf bytebuf = Unpooled.copiedBuffer(encode(result));
			int length = bytebuf.readableBytes();
			//Assert.assertEquals(expected.length, length);
			System.arraycopy(toArray(bytebuf,bytebuf.readableBytes()), 0, actuals, index,length );
			index = length;
			//Assert.assertArrayEquals(expected, actuals);
		}
		
	}

	// 下面数据截取自现网10085的报文。cmppSubmit2.0协议
	private byte[] prepareMsgData() {
		return new byte[] {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xe1,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,(byte)0x02,(byte)0x21,(byte)0x7a,(byte)0xec,(byte)0x85,(byte)0xcc,(byte)0xe8,(byte)0xc0
				,(byte)0x91,(byte)0x52,(byte)0x00,(byte)0x07,(byte)0x31,(byte)0x30,(byte)0x30,(byte)0x38,(byte)0x35,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00
				,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x4d,(byte)0x43,(byte)0x4e,(byte)0x32,(byte)0x32,(byte)0x31,(byte)0x30
				,(byte)0x31,(byte)0x30,(byte)0x31,(byte)0x00,(byte)0x01,(byte)0x04,(byte)0x38,(byte)0x36,(byte)0x31,(byte)0x35,(byte)0x31,(byte)0x33,(byte)0x37,(byte)0x38,(byte)0x32,(byte)0x33
				,(byte)0x33,(byte)0x32,(byte)0x38,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x8c,(byte)0x0b,(byte)0x00,(byte)0x03
				,(byte)0xba,(byte)0x01,(byte)0x01,(byte)0x05,(byte)0x04,(byte)0x15,(byte)0x8a,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x18,(byte)0x08,(byte)0x07,(byte)0xca,(byte)0xa0
				,(byte)0x00,(byte)0x20,(byte)0x18,(byte)0x04,(byte)0x18,(byte)0x0c,(byte)0x07,(byte)0xc5,(byte)0x50,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x08,(byte)0x3c,(byte)0x00,(byte)0x87
				,(byte)0xc2,(byte)0xa8,(byte)0x00,(byte)0x10,(byte)0x80,(byte)0x08,(byte)0x3c,(byte)0x01,(byte)0x07,(byte)0xc1,(byte)0x54,(byte)0x00,(byte)0x08,(byte)0x40,(byte)0x10,(byte)0x3c
				,(byte)0x0e,(byte)0x07,(byte)0xc0,(byte)0xaa,(byte)0x00,(byte)0x04,(byte)0x38,(byte)0x20,(byte)0x3e,(byte)0x00,(byte)0x0f,(byte)0x81,(byte)0x55,(byte)0x00,(byte)0x02,(byte)0x00
				,(byte)0x40,(byte)0x1f,(byte)0x00,(byte)0x1f,(byte)0x00,(byte)0xaa,(byte)0x00,(byte)0x01,(byte)0x81,(byte)0x80,(byte)0x1f,(byte)0xe0,(byte)0xe0,(byte)0x00,(byte)0x55,(byte)0x00
				,(byte)0x02,(byte)0x7e,(byte)0x40,(byte)0x22,(byte)0x1f,(byte)0x18,(byte)0x00,(byte)0x2a,(byte)0x00,(byte)0x1c,(byte)0x44,(byte)0x30,(byte)0x41,(byte)0x38,(byte)0x96,(byte)0x00
				,(byte)0x15,(byte)0x00,(byte)0xf8,(byte)0xba,(byte)0x3e,(byte)0x49,(byte)0x46,(byte)0xa1,(byte)0x80,(byte)0x0a,(byte)0x03,(byte)0xfc,(byte)0xba,(byte)0x7f,(byte)0xc9,(byte)0x83
				,(byte)0x4e,(byte)0x40,(byte)0x15,(byte)0x07,(byte)0xfe,(byte)0xba,(byte)0xff,(byte)0xaa,(byte)0x92,(byte)0x91,(byte)0x20,(byte)0x0a,(byte)0x0f,(byte)0xff,(byte)0x93,(byte)0xfc
				,(byte)0x54,(byte)0x93,(byte)0xa0,(byte)0x90,(byte)0x05,(byte)0x1f,(byte)0xff,(byte)0xbb,(byte)0xf8,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00
				,(byte)0x00};
	}

}
