package com.billyyccc.mssqlclient.impl.codec;

import com.billyyccc.mssqlclient.impl.protocol.datatype.MSSQLDataType;
import com.billyyccc.mssqlclient.impl.protocol.datatype.MSSQLDataTypeId;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class MSSQLDataTypeCodec {
  public static Object decode(MSSQLDataType dataType, ByteBuf in) {
    switch (dataType.id()) {
      case MSSQLDataTypeId.INT1TYPE_ID:
        return in.readUnsignedByte();
      case MSSQLDataTypeId.INT2TYPE_ID:
        return in.readShortLE();
      case MSSQLDataTypeId.INT4TYPE_ID:
        return in.readIntLE();
      case MSSQLDataTypeId.INT8TYPE_ID:
        return in.readLongLE();
      case MSSQLDataTypeId.BIGVARCHRTYPE_ID:
        int length = in.readUnsignedShortLE();
        return in.readCharSequence(length, StandardCharsets.UTF_8);
      default:
        throw new UnsupportedOperationException("Unsupported datatype: " + dataType);
    }
  }
}
