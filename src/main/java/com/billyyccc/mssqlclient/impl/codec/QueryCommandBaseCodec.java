package com.billyyccc.mssqlclient.impl.codec;

import com.billyyccc.mssqlclient.impl.protocol.datatype.FixedLenDataType;
import com.billyyccc.mssqlclient.impl.protocol.datatype.MSSQLDataType;
import com.billyyccc.mssqlclient.impl.protocol.datatype.TextWithCollationDataType;
import io.netty.buffer.ByteBuf;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.impl.RowDesc;
import io.vertx.sqlclient.impl.command.QueryCommandBase;

import java.util.stream.Collector;

import static com.billyyccc.mssqlclient.impl.protocol.datatype.MSSQLDataTypeId.*;

abstract class QueryCommandBaseCodec<T, C extends QueryCommandBase<T>> extends MSSQLCommandCodec<Boolean, C> {
  protected RowResultDecoder<?, T> rowResultDecoder;

  QueryCommandBaseCodec(C cmd) {
    super(cmd);
  }

  private static <A, T> T emptyResult(Collector<Row, A, T> collector) {
    return collector.finisher().apply(collector.supplier().get());
  }

  protected void encodeTransactionDescriptor(ByteBuf payload, long transactionDescriptor, int outstandingRequestCount) {
    payload.writeIntLE(18); // HeaderLength is always 18
    payload.writeShortLE(0x0002); // HeaderType
    payload.writeLongLE(transactionDescriptor);
    payload.writeIntLE(outstandingRequestCount);
  }

  protected MSSQLRowDesc decodeColmetadataToken(ByteBuf payload) {
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

  protected void decodeRow(ByteBuf payload) {
    rowResultDecoder.decodeRow(rowResultDecoder.desc.columnDatas.length, payload);
  }

  protected void handleResultSetDone(int affectedRows) {
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

  private MSSQLDataType decodeDataTypeMetadata(ByteBuf payload) {
    int typeInfo = payload.readUnsignedByte();
    switch (typeInfo) {
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

