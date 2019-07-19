package com.billyyccc.mssqlclient.impl.codec;

import com.billyyccc.mssqlclient.impl.protocol.MessageStatus;
import com.billyyccc.mssqlclient.impl.protocol.MessageType;
import com.billyyccc.mssqlclient.impl.protocol.TdsMessage;
import com.billyyccc.mssqlclient.impl.protocol.TdsPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayDeque;

class TdsMessageDecoder extends ChannelInboundHandlerAdapter {
  private final ArrayDeque<MSSQLCommandCodec<?, ?>> inflight;
  private final TdsMessageEncoder encoder;

  private TdsMessage message;

  TdsMessageDecoder(ArrayDeque<MSSQLCommandCodec<?, ?>> inflight, TdsMessageEncoder encoder) {
    this.inflight = inflight;
    this.encoder = encoder;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ByteBuf in = (ByteBuf) msg;
    // decoding a packet
    if (in.readableBytes() > TdsPacket.PACKET_HEADER_SIZE) {
      int packetStartIdx = in.readerIndex();
      int packetLen = in.getUnsignedShort(packetStartIdx + 2);

      if (in.readableBytes() >= packetLen) {
        MessageType type = MessageType.valueOf(in.readUnsignedByte());
        MessageStatus status = MessageStatus.valueOf(in.readUnsignedByte());
        in.skipBytes(2); // packet length
        int processId = in.readUnsignedShort();
        short packetId = in.readUnsignedByte();
        in.skipBytes(1); // unused window

        ByteBuf packetData = in.readSlice(packetLen - TdsPacket.PACKET_HEADER_SIZE);

        // assemble packets
        if (status == MessageStatus.END_OF_MESSAGE) {
          if (message == null) {
            decodeMessage(TdsMessage.newTdsMessage(type, status, processId, packetData), encoder);
          } else {
            // fragmented packet of this message
            CompositeByteBuf messageData = (CompositeByteBuf) message.content();
            messageData.addComponent(true, packetData);
            decodeMessage(message, encoder);
            message = null;
          }
        } else {
          CompositeByteBuf messageData = ctx.alloc().compositeBuffer();
          messageData.addComponent(true, packetData);
          message = TdsMessage.newTdsMessage(type, status, processId, messageData);
        }
      }
    }
  }

  private void decodeMessage(TdsMessage message, TdsMessageEncoder encoder) {
    inflight.peek().decodeMessage(message, encoder);
    message.release();
  }
}
