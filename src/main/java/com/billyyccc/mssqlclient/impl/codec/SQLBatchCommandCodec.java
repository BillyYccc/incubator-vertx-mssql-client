package com.billyyccc.mssqlclient.impl.codec;

import com.billyyccc.mssqlclient.impl.protocol.MessageStatus;
import com.billyyccc.mssqlclient.impl.protocol.MessageType;
import com.billyyccc.mssqlclient.impl.protocol.TdsMessage;
import com.billyyccc.mssqlclient.impl.protocol.token.DataPacketStreamTokenType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.vertx.sqlclient.impl.command.SimpleQueryCommand;

import java.nio.charset.StandardCharsets;

class SQLBatchCommandCodec<T> extends QueryCommandBaseCodec<T, SimpleQueryCommand<T>> {
  SQLBatchCommandCodec(SimpleQueryCommand cmd) {
    super(cmd);
  }

  @Override
  void encode(TdsMessageEncoder encoder) {
    super.encode(encoder);
    sendBatchClientRequest();
  }

  @Override
  void decodeMessage(TdsMessage message, TdsMessageEncoder encoder) {
    ByteBuf messageBody = message.content();
    while (messageBody.isReadable()) {
      int tokenByte = messageBody.readUnsignedByte();
      switch (tokenByte) {
        case DataPacketStreamTokenType.COLMETADATA_TOKEN:
          MSSQLRowDesc rowDesc = decodeColmetadataToken(messageBody);
          rowResultDecoder = new RowResultDecoder<>(cmd.collector(), rowDesc);
          break;
        case DataPacketStreamTokenType.ROW_TOKEN:
          handleRow(messageBody);
          break;
        case DataPacketStreamTokenType.DONE_TOKEN:
          short status = messageBody.readShortLE();
          short curCmd = messageBody.readShortLE();
          long doneRowCount = messageBody.readLongLE();
          handleResultSetDone((int) doneRowCount);
          handleDoneToken();
          break;
        case DataPacketStreamTokenType.INFO_TOKEN:
          int infoTokenLength = messageBody.readUnsignedShortLE();
          //TODO not used for now
          messageBody.skipBytes(infoTokenLength);
          break;
        case DataPacketStreamTokenType.ERROR_TOKEN:
          handleErrorToken(messageBody);
          break;
        default:
          throw new UnsupportedOperationException("Unsupported token: " + tokenByte);
      }
    }
  }

  private void sendBatchClientRequest() {
    ChannelHandlerContext chctx = encoder.chctx;

    ByteBuf packet = chctx.alloc().ioBuffer();

    // packet header
    packet.writeByte(MessageType.SQL_BATCH.value());
    packet.writeByte(MessageStatus.NORMAL.value() | MessageStatus.END_OF_MESSAGE.value());
    int packetLenIdx = packet.writerIndex();
    packet.writeShort(0); // set length later
    packet.writeShort(0x00);
    packet.writeByte(0x00); // FIXME packet ID
    packet.writeByte(0x00);

    int start = packet.writerIndex();
    packet.writeIntLE(0x00); // TotalLength for ALL_HEADERS
    encodeTransactionDescriptor(packet, 0, 1);
    // set TotalLength for ALL_HEADERS
    packet.setIntLE(start, packet.writerIndex() - start);

    // SQLText
    packet.writeCharSequence(cmd.sql(), StandardCharsets.UTF_16LE);

    int packetLen = packet.writerIndex() - packetLenIdx + 2;
    packet.setShort(packetLenIdx, packetLen);

    chctx.writeAndFlush(packet);
  }
}
