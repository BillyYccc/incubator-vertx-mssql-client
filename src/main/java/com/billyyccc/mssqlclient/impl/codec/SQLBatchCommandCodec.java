package com.billyyccc.mssqlclient.impl.codec;

import com.billyyccc.mssqlclient.impl.protocol.MessageStatus;
import com.billyyccc.mssqlclient.impl.protocol.MessageType;
import com.billyyccc.mssqlclient.impl.protocol.TdsMessage;
import com.billyyccc.mssqlclient.impl.protocol.datatype.FixedLenDataType;
import com.billyyccc.mssqlclient.impl.protocol.datatype.MSSQLDataType;
import com.billyyccc.mssqlclient.impl.protocol.datatype.TextWithCollationDataType;
import com.billyyccc.mssqlclient.impl.protocol.token.DataPacketStreamTokenType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.impl.RowDesc;
import io.vertx.sqlclient.impl.command.SimpleQueryCommand;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collector;

import static com.billyyccc.mssqlclient.impl.protocol.datatype.MSSQLDataTypeId.*;
import static com.billyyccc.mssqlclient.impl.protocol.datatype.MSSQLDataTypeId.BIGVARCHRTYPE_ID;

class SQLBatchCommandCodec<T> extends MSSQLCommandCodec<Boolean, SimpleQueryCommand<T>> {
  private RowResultDecoder<?, T> rowResultDecoder;

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
          rowResultDecoder = new RowResultDecoder<>(cmd.collector(), false, rowDesc);
          break;
        case DataPacketStreamTokenType.ROW_TOKEN:
          decodeRow(messageBody);
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

  private void encodeTransactionDescriptor(ByteBuf payload, long transactionDescriptor, int outstandingRequestCount) {
    payload.writeIntLE(18); // HeaderLength is always 18
    payload.writeShortLE(0x0002); // HeaderType
    payload.writeLongLE(transactionDescriptor);
    payload.writeIntLE(outstandingRequestCount);
  }

  private MSSQLRowDesc decodeColmetadataToken(ByteBuf payload) {
    int columnCount = payload.readUnsignedShortLE();

    ColumnData[] columnDatas = new ColumnData[columnCount];

    for (int i = 0; i < columnCount; i++) {
      long userType = payload.readUnsignedIntLE();
      int flags = payload.readUnsignedShortLE();
      MSSQLDataType dataType = decodeDataTypeMetadata(payload);
      String columnName = readByteLenVarchar(payload);
      columnDatas[i] = new ColumnData(userType, flags, dataType, columnName);
    }

    return new MSSQLRowDesc(columnDatas);
  }

  private void decodeRow(ByteBuf payload) {
    rowResultDecoder.decodeRow(rowResultDecoder.desc.columnDatas.length, payload);
  }

  private void handleResultSetDone(int affectedRows) {
    this.result = false;
    T result;
    int size;
    RowDesc rowDesc;
    if (rowResultDecoder != null) {
      result = rowResultDecoder.complete();
      rowDesc = rowResultDecoder.desc;
      size = rowResultDecoder.size();
      rowResultDecoder.reset();
    } else {
      result = emptyResult(cmd.collector());
      size = 0;
      rowDesc = null;
    }
    cmd.resultHandler().handleResult(affectedRows, size, rowDesc, result);
  }

  private static <A, T> T emptyResult(Collector<Row, A, T> collector) {
    return collector.finisher().apply(collector.supplier().get());
  }

  private MSSQLDataType decodeDataTypeMetadata(ByteBuf payload) {
    int typeInfo = payload.readUnsignedByte();
    switch (typeInfo){
      /*
       * FixedLen DataType
       */
      case INT1TYPE_ID:
        return FixedLenDataType.INT1TYPE;
      case INT2TYPE_ID:
        return FixedLenDataType.INT2TYPE;
      case INT4TYPE_ID:
        return FixedLenDataType.INT4TYPE;
      case INT8TYPE_ID:
        return FixedLenDataType.INT8TYPE;
      /*
       * Variable Length Data Type
       */
      case BIGVARCHRTYPE_ID:
        int size = payload.readUnsignedShortLE();
        short collateCodepage = payload.readShortLE();
        short collateFlags = payload.readShortLE();
        byte collateCharsetId = payload.readByte();
        return new TextWithCollationDataType(BIGVARCHRTYPE_ID, String.class, null);
      default:
        throw new UnsupportedOperationException("Unsupported type with typeinfo: " + typeInfo);
    }
  }
}
