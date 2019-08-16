package com.billyyccc.mssqlclient.impl.codec;

import com.billyyccc.mssqlclient.impl.protocol.MessageStatus;
import com.billyyccc.mssqlclient.impl.protocol.MessageType;
import com.billyyccc.mssqlclient.impl.protocol.TdsMessage;
import com.billyyccc.mssqlclient.impl.protocol.client.rpc.ProcId;
import com.billyyccc.mssqlclient.impl.protocol.datatype.MSSQLDataTypeId;
import com.billyyccc.mssqlclient.impl.protocol.token.DataPacketStreamTokenType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.impl.command.ExtendedQueryCommand;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.billyyccc.mssqlclient.impl.codec.MSSQLDataTypeCodec.inferenceParamDefinitionByValueType;

class ExtendedQueryCommandCodec<T> extends QueryCommandBaseCodec<T, ExtendedQueryCommand<T>> {

  ExtendedQueryCommandCodec(ExtendedQueryCommand cmd) {
    super(cmd);
  }

  @Override
  void encode(TdsMessageEncoder encoder) {
    super.encode(encoder);
    sendPrepexecRequest();
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
          messageBody.skipBytes(12); // this should only be after ERROR_TOKEN?
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
        case DataPacketStreamTokenType.DONEINPROC_TOKEN:
          short status = messageBody.readShortLE();
          short curCmd = messageBody.readShortLE();
          long doneRowCount = messageBody.readLongLE();
          handleResultSetDone((int) doneRowCount);
          handleDoneToken();
          break;
        case DataPacketStreamTokenType.RETURNSTATUS_TOKEN:
          messageBody.skipBytes(4);
          break;
        case DataPacketStreamTokenType.RETURNVALUE_TOKEN:
          messageBody.skipBytes(messageBody.readableBytes()); // FIXME
          break;
        default:
          throw new UnsupportedOperationException("Unsupported token: " + tokenByte);
      }
    }
  }

  private void sendPrepexecRequest() {
    ChannelHandlerContext chctx = encoder.chctx;

    ByteBuf packet = chctx.alloc().ioBuffer();

    // packet header
    packet.writeByte(MessageType.RPC.value());
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

    /*
      RPCReqBatch
     */
    packet.writeShortLE(0xFFFF);
    packet.writeShortLE(ProcId.Sp_PrepExec);

    // Option flags
    packet.writeShortLE(0x0000);

    // Parameter

    // OUT Parameter
    packet.writeByte(0x00);
    packet.writeByte(0x01); // By reference
    packet.writeByte(MSSQLDataTypeId.INTNTYPE_ID);
    packet.writeByte(0x04);
    packet.writeByte(0x04);
    packet.writeIntLE(0x00);

    Tuple params = cmd.params();

    // Param definitions
    String paramDefinitions = parseParamDefinitions(params);
    encodeNVarcharParameter(packet, paramDefinitions);

    // SQL text
    encodeNVarcharParameter(packet, cmd.sql());

    // Param values
    for (int i = 0; i < params.size(); i++) {
      encodeParamValue(packet, params.getValue(i));
    }

    int packetLen = packet.writerIndex() - packetLenIdx + 2;
    packet.setShort(packetLenIdx, packetLen);

    chctx.writeAndFlush(packet);
  }

  private String parseParamDefinitions(Tuple params) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < params.size(); i++) {
      Object param = params.getValue(i);
      stringBuilder.append("@P").append(i + 1).append(" ");
      stringBuilder.append(inferenceParamDefinitionByValueType(param));
      if (i != params.size() - 1) {
        stringBuilder.append(",");
      }
    }
    return stringBuilder.toString();
  }

  private void encodeNVarcharParameter(ByteBuf payload, String value) {
    payload.writeByte(0x00); // name length
    payload.writeByte(0x00); // status flags
    payload.writeByte(MSSQLDataTypeId.NVARCHARTYPE_ID);
    payload.writeShortLE(8000); // maximal length
    payload.writeByte(0x09);
    payload.writeByte(0x04);
    payload.writeByte(0xd0);
    payload.writeByte(0x00);
    payload.writeByte(0x34); // Collation for param definitions TODO always this value?
    writeUnsignedShortLenVarChar(payload, value);
  }

  private void encodeParamValue(ByteBuf payload, Object value) {
    if (value == null) {
      encodeNullParameter(payload);
    } else if (value instanceof Byte) {
      encodeIntNParameter(payload, 1, value);
    } else if (value instanceof Short) {
      encodeIntNParameter(payload, 2, value);
    } else if (value instanceof Integer) {
      encodeIntNParameter(payload, 4, value);
    } else if (value instanceof Long) {
      encodeIntNParameter(payload, 8, value);
    } else if (value instanceof String) {
      encodeNVarcharParameter(payload, (String) value);
    } else if (value instanceof LocalDate) {
      throw new UnsupportedOperationException("Unsupported type");
    } else if (value instanceof LocalTime) {
      throw new UnsupportedOperationException("Unsupported type");
    } else {
      throw new UnsupportedOperationException("Unsupported type");
    }
  }

  private void encodeNullParameter(ByteBuf payload) {
    payload.writeByte(0x00);
    payload.writeByte(0x00);
    payload.writeByte(MSSQLDataTypeId.NVARCHARTYPE_ID);
    payload.writeShortLE(8000); // maximal length
    payload.writeByte(0x09);
    payload.writeByte(0x04);
    payload.writeByte(0xd0);
    payload.writeByte(0x00);
    payload.writeByte(0x34); // Collation for param definitions TODO always this value?
    payload.writeShortLE(0xFFFF);
  }

  private void encodeIntNParameter(ByteBuf payload, int n, Object value) {
    payload.writeByte(0x00);
    payload.writeByte(0x00);
    payload.writeByte(MSSQLDataTypeId.INTNTYPE_ID);
    payload.writeByte(n);
    payload.writeByte(n);
    switch (n) {
      case 1:
        payload.writeByte((Integer) value);
        break;
      case 2:
        payload.writeShortLE((Integer) value);
        break;
      case 4:
        payload.writeIntLE((Integer) value);
        break;
      case 8:
        payload.writeLongLE((Long) value);
        break;
      default:
        throw new UnsupportedOperationException();
    }
  }
}
