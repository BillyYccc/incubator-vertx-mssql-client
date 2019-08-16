package com.billyyccc.mssqlclient.impl.codec;

import com.billyyccc.mssqlclient.impl.protocol.datatype.MSSQLDataType;
import com.billyyccc.mssqlclient.impl.protocol.datatype.MSSQLDataTypeId;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

class MSSQLDataTypeCodec {
  private static Map<Class, String> parameterDefinitionsMapping = new HashMap<>();

  static {
    parameterDefinitionsMapping.put(Byte.class, "tinyint");
    parameterDefinitionsMapping.put(Short.class, "smallint");
    parameterDefinitionsMapping.put(Integer.class, "int");
    parameterDefinitionsMapping.put(Long.class, "bigint");
    parameterDefinitionsMapping.put(String.class, "nvarchar(4000)");
    parameterDefinitionsMapping.put(LocalDate.class, "date");
    parameterDefinitionsMapping.put(LocalTime.class, "time");
  }

  static String inferenceParamDefinitionByValueType(Object value) {
    if (value == null) {
      return "nvarchar(4000)";
    } else {
      String paramDefinition = parameterDefinitionsMapping.get(value.getClass());
      if (paramDefinition != null) {
        return paramDefinition;
      } else {
        throw new UnsupportedOperationException("Unsupported type" + value.getClass());
      }
    }
  }

  static Object decode(MSSQLDataType dataType, ByteBuf in) {
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
