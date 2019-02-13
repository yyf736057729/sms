package com.zx.sms.codec.cmpp;

import static com.zx.sms.common.util.NettyByteBufUtil.toArray;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

import com.zx.sms.codec.cmpp.msg.CmppConnectResponseMessage;
import com.zx.sms.codec.cmpp.msg.Message;
import com.zx.sms.codec.cmpp.packet.CmppConnectResponse;
import com.zx.sms.codec.cmpp.packet.CmppPacketType;
import com.zx.sms.codec.cmpp.packet.PacketType;
import com.zx.sms.common.NotSupportedException;
/**
 *
 * @author huzorro(huzorro@gmail.com)
 * @author Lihuanghe(18852780@qq.com)
 */
public class CmppConnectResponseMessageCodec extends MessageToMessageCodec<Message, CmppConnectResponseMessage> {
	private PacketType packetType;

	/**
     * 
     */
	public CmppConnectResponseMessageCodec() {
		this(CmppPacketType.CMPPCONNECTRESPONSE);
	}

	public CmppConnectResponseMessageCodec(PacketType packetType) {
		this.packetType = packetType;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
		long commandId = ((Long) msg.getHeader().getCommandId()).longValue();
		if (commandId != packetType.getCommandId())
		{
			//不解析，交给下一个codec
			out.add(msg);
			return;
		}
		CmppConnectResponseMessage responseMessage = new CmppConnectResponseMessage(msg.getHeader());
		byte[] body = msg.getBodyBuffer();
		if(body.length == 21){
			ByteBuf bodyBuffer = Unpooled.wrappedBuffer(msg.getBodyBuffer());

			responseMessage.setStatus(bodyBuffer.readUnsignedInt());
			responseMessage.setAuthenticatorISMG(toArray(bodyBuffer,CmppConnectResponse.AUTHENTICATORISMG.getLength()));
			responseMessage.setVersion(bodyBuffer.readUnsignedByte());
			
			ReferenceCountUtil.release(bodyBuffer);
			out.add(responseMessage);
		}else{
			if(body.length == 18)
				throw new NotSupportedException("error cmpp version . shoud use cmpp2.0 ");
			else
				throw new NotSupportedException("error cmpp CmppConnectResponseMessage data .");
		}
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, CmppConnectResponseMessage msg, List<Object> out) throws Exception {

		ByteBuf bodyBuffer = ctx.alloc().buffer(CmppConnectResponse.AUTHENTICATORISMG.getBodyLength());

		bodyBuffer.writeInt((int) msg.getStatus());
		bodyBuffer.writeBytes(msg.getAuthenticatorISMG());
		bodyBuffer.writeByte(msg.getVersion());

		msg.setBodyBuffer(toArray(bodyBuffer,bodyBuffer.readableBytes()));
		msg.getHeader().setBodyLength(msg.getBodyBuffer().length);
		ReferenceCountUtil.release(bodyBuffer);
		out.add(msg);

	}

}
