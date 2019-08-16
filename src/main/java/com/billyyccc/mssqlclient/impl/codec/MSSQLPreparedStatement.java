package com.billyyccc.mssqlclient.impl.codec;

import io.vertx.sqlclient.impl.ParamDesc;
import io.vertx.sqlclient.impl.PreparedStatement;
import io.vertx.sqlclient.impl.RowDesc;

import java.util.List;

public class MSSQLPreparedStatement implements PreparedStatement {
  final String sql;
  final MSSQLParamDesc paramDesc;

  public MSSQLPreparedStatement(String sql, MSSQLParamDesc paramDesc) {
    this.sql = sql;
    this.paramDesc = paramDesc;
  }

  @Override
  public ParamDesc paramDesc() {
    return paramDesc;
  }

  @Override
  public RowDesc rowDesc() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String sql() {
    return sql;
  }

  @Override
  public String prepare(List<Object> values) {
//    return paramDesc.prepare(values);
    return null;
  }
}
