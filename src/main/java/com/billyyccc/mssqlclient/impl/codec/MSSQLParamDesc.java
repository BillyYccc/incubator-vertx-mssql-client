package com.billyyccc.mssqlclient.impl.codec;

import com.billyyccc.mssqlclient.impl.protocol.datatype.MSSQLDataType;
import io.vertx.sqlclient.impl.ParamDesc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class MSSQLParamDesc extends ParamDesc {
  private final ColumnData[] paramDescriptions;

  public MSSQLParamDesc(ColumnData[] paramDescriptions) {
    this.paramDescriptions = paramDescriptions;
  }

  public ColumnData[] paramDescriptions() {
    return paramDescriptions;
  }

  @Override
  public String prepare(List<Object> values) {
    if (values.size() != paramDescriptions.length){
      return buildReport(values);
    }
    return null;
  }

  // reuse from pg
  private String buildReport(List<Object> values) {
    Stream<Class> types = Stream.of(paramDescriptions).map(ColumnData::dataType).map(MSSQLDataType::mappedJavaType);
    return "Values [" + values.stream().map(String::valueOf).collect(Collectors.joining(", ")) +
      "] cannot be coerced to [" + types
      .map(Class::getSimpleName)
      .collect(Collectors.joining(", ")) + "]";
  }
}
