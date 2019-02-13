/**
 * 
 */
package com.zx.sms.codec.cmpp;

import static com.zx.sms.common.util.NettyByteBufUtil.toArray;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

import org.marre.sms.SmsDcs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zx.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import com.zx.sms.codec.cmpp.msg.Message;
import com.zx.sms.codec.cmpp.packet.CmppPacketType;
import com.zx.sms.codec.cmpp.packet.CmppSubmitRequest;
import com.zx.sms.codec.cmpp.packet.PacketType;
import com.zx.sms.codec.cmpp.wap.LongMessageFrameHolder;
import com.zx.sms.common.GlobalConstance;
import com.zx.sms.common.util.CMPPCommonUtil;
import com.zx.sms.common.util.DefaultMsgIdUtil;
/**
 * @author huzorro(huzorro@gmail.com)
 * @author Lihuanghe(18852780@qq.com)
 */
public class CmppSubmitRequestMessageCodec extends MessageToMessageCodec<Message, CmppSubmitRequestMessage> {
	private final static Logger logger = LoggerFactory.getLogger(CmppSubmitRequestMessageCodec.class);
	private PacketType packetType;

	/**
	 * 
	 */
	public CmppSubmitRequestMessageCodec() {
		this(CmppPacketType.CMPPSUBMITREQUEST);
	}

	public CmppSubmitRequestMessageCodec(PacketType packetType) {
		this.packetType = packetType;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
		long commandId = ((Long) msg.getHeader().getCommandId()).longValue();
		if (packetType.getCommandId() != commandId) {
			// 不解析，交给下一个codec
			out.add(msg);
			return;
		}

		CmppSubmitRequestMessage requestMessage = new CmppSubmitRequestMessage(msg.getHeader());

		ByteBuf bodyBuffer = Unpooled.wrappedBuffer(msg.getBodyBuffer());

		requestMessage.setMsgid(DefaultMsgIdUtil.bytes2MsgId(toArray(bodyBuffer,CmppSubmitRequest.MSGID.getLength())));


		requestMessage.setPktotal(bodyBuffer.readUnsignedByte());
		requestMessage.setPknumber(bodyBuffer.readUnsignedByte());

		requestMessage.setRegisteredDelivery(bodyBuffer.readUnsignedByte());
		requestMessage.setMsglevel(bodyBuffer.readUnsignedByte());
		requestMessage.setServiceId(bodyBuffer.readCharSequence(CmppSubmitRequest.SERVICEID.getLength(),GlobalConstance.defaultTransportCharset).toString().trim());
		requestMessage.setFeeUserType(bodyBuffer.readUnsignedByte());

		requestMessage.setFeeterminalId(bodyBuffer.readCharSequence(CmppSubmitRequest.FEETERMINALID.getLength(),GlobalConstance.defaultTransportCharset).toString().trim());

		requestMessage.setFeeterminaltype(bodyBuffer.readUnsignedByte());

		requestMessage.setTppid(bodyBuffer.readUnsignedByte());
		requestMessage.setTpudhi(bodyBuffer.readUnsignedByte());
		requestMessage.setMsgfmt(new SmsDcs((byte)bodyBuffer.readUnsignedByte()));

		requestMessage.setMsgsrc(bodyBuffer.readCharSequence(CmppSubmitRequest.MSGSRC.getLength(),GlobalConstance.defaultTransportCharset).toString().trim());

		requestMessage.setFeeType(bodyBuffer.readCharSequence(CmppSubmitRequest.FEETYPE.getLength(),GlobalConstance.defaultTransportCharset).toString().trim());

		requestMessage.setFeeCode(bodyBuffer.readCharSequence(CmppSubmitRequest.FEECODE.getLength(),GlobalConstance.defaultTransportCharset).toString().trim());

		requestMessage.setValIdTime(bodyBuffer.readCharSequence(CmppSubmitRequest.VALIDTIME.getLength(),GlobalConstance.defaultTransportCharset).toString().trim());

		requestMessage.setAtTime(bodyBuffer.readCharSequence(CmppSubmitRequest.ATTIME.getLength(),GlobalConstance.defaultTransportCharset).toString().trim());

		requestMessage.setSrcId(bodyBuffer.readCharSequence(CmppSubmitRequest.SRCID.getLength(),GlobalConstance.defaultTransportCharset).toString().trim());

		short destUsrtl = bodyBuffer.readUnsignedByte();
		String[] destTermId = new String[destUsrtl];
		for (int i = 0; i < destUsrtl; i++) {
			destTermId[i] = bodyBuffer.readCharSequence(CmppSubmitRequest.DESTTERMINALID.getLength(),GlobalConstance.defaultTransportCharset).toString().trim();
		}
		requestMessage.setDestterminalId(destTermId);

		requestMessage.setDestterminaltype(bodyBuffer.readUnsignedByte());

		short msgLength = (short)(LongMessageFrameHolder.getPayloadLength(requestMessage.getMsgfmt().getAlphabet(),bodyBuffer.readUnsignedByte()) & 0xffff);

		byte[] contentbytes = new byte[msgLength];
		bodyBuffer.readBytes(contentbytes);
		requestMessage.setMsgContentBytes(contentbytes);
		requestMessage.setMsgLength((short)msgLength);

		requestMessage.setLinkID(bodyBuffer.readCharSequence(CmppSubmitRequest.LINKID.getLength(),GlobalConstance.defaultTransportCharset).toString().trim());

		out.add(requestMessage);
		ReferenceCountUtil.release(bodyBuffer);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, CmppSubmitRequestMessage requestMessage, List<Object> out) throws Exception {
			assert(requestMessage.getDestUsrtl()>0);
			ByteBuf bodyBuffer = ctx.alloc().buffer(CmppSubmitRequest.ATTIME.getBodyLength() + requestMessage.getMsgLength() + (requestMessage.getDestUsrtl()-1)
					* CmppSubmitRequest.DESTTERMINALID.getLength());

			bodyBuffer.writeBytes(DefaultMsgIdUtil.msgId2Bytes(requestMessage.getMsgid()));

			bodyBuffer.writeByte(requestMessage.getPktotal());
			bodyBuffer.writeByte(requestMessage.getPknumber());
			bodyBuffer.writeByte(requestMessage.getRegisteredDelivery());
			bodyBuffer.writeByte(requestMessage.getMsglevel());

			bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(requestMessage.getServiceId().getBytes(GlobalConstance.defaultTransportCharset),
					CmppSubmitRequest.SERVICEID.getLength(), 0));

			bodyBuffer.writeByte(requestMessage.getFeeUserType());

			bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(requestMessage.getFeeterminalId().getBytes(GlobalConstance.defaultTransportCharset),
					CmppSubmitRequest.FEETERMINALID.getLength(), 0));
			bodyBuffer.writeByte(requestMessage.getFeeterminaltype());
			bodyBuffer.writeByte(requestMessage.getTppid());
			bodyBuffer.writeByte(requestMessage.getTpudhi());
			bodyBuffer.writeByte(requestMessage.getMsgfmt().getValue());

			bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(requestMessage.getMsgsrc().getBytes(GlobalConstance.defaultTransportCharset),
					CmppSubmitRequest.MSGSRC.getLength(), 0));

			bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(requestMessage.getFeeType().getBytes(GlobalConstance.defaultTransportCharset),
					CmppSubmitRequest.FEETYPE.getLength(), 0));

			bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(requestMessage.getFeeCode().getBytes(GlobalConstance.defaultTransportCharset),
					CmppSubmitRequest.FEECODE.getLength(), 0));

			bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(requestMessage.getValIdTime().getBytes(GlobalConstance.defaultTransportCharset),
					CmppSubmitRequest.VALIDTIME.getLength(), 0));

			bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(requestMessage.getAtTime().getBytes(GlobalConstance.defaultTransportCharset),
					CmppSubmitRequest.ATTIME.getLength(), 0));

			bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(requestMessage.getSrcId().getBytes(GlobalConstance.defaultTransportCharset),
					CmppSubmitRequest.SRCID.getLength(), 0));

			bodyBuffer.writeByte(requestMessage.getDestUsrtl());
			for (int i = 0; i < requestMessage.getDestUsrtl(); i++) {
				String[] destTermId = requestMessage.getDestterminalId();
				bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(destTermId[i].getBytes(GlobalConstance.defaultTransportCharset),
						CmppSubmitRequest.DESTTERMINALID.getLength(), 0));
			}
			bodyBuffer.writeByte(requestMessage.getDestterminaltype());
			
			bodyBuffer.writeByte(requestMessage.getMsgLength());

			bodyBuffer.writeBytes(requestMessage.getMsgContentBytes());

			bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(requestMessage.getLinkID().getBytes(GlobalConstance.defaultTransportCharset),
					CmppSubmitRequest.LINKID.getLength(), 0));

			// bodyBuffer.writeBytes(CMPPCommonUtil.ensureLength(requestMessage.getReserve().getBytes(GlobalConstance.defaultTransportCharset),
			// CmppSubmitRequest.RESERVE.getLength(), 0));/**cmpp3.0 无该字段，不进行编解码
			// */
			requestMessage.setBodyBuffer(toArray(bodyBuffer,bodyBuffer.readableBytes()));
			requestMessage.getHeader().setBodyLength(requestMessage.getBodyBuffer().length);
			out.add(requestMessage);
			ReferenceCountUtil.release(bodyBuffer);
	}
}
