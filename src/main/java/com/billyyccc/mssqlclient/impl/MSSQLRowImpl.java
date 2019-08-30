package com.billyyccc.mssqlclient.impl;

import io.vertx.core.buffer.Buffer;
import io.vertx.sqlclient.impl.ArrayTuple;
import io.vertx.sqlclient.impl.RowDesc;
import io.vertx.sqlclient.impl.RowInternal;

import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.UUID;

public class MSSQLRowImpl extends ArrayTuple implements RowInternal {
  private final RowDesc rowDesc;
  MSSQLRowImpl next;

  public MSSQLRowImpl(RowDesc rowDesc) {
    super(rowDesc.columnNames().size());
    this.rowDesc = rowDesc;
  }

  @Override
  public RowInternal getNext() {
    return next;
  }

  @Override
  public void setNext(RowInternal next) {
    this.next = (MSSQLRowImpl) next;
  }

  @Override
  public String getColumnName(int pos) {
    List<String> columnNames = rowDesc.columnNames();
    return pos < 0 || columnNames.size() - 1 < pos ? null : columnNames.get(pos);
  }

  @Override
  public int getColumnIndex(String columnName) {
    if (columnName == null) {
      throw new IllegalArgumentException("Column name can not be null");
    }
    return rowDesc.columnNames().indexOf(columnName);
  }

  @Override
  public <T> T[] getValues(Class<T> type, int idx) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean getBoolean(String columnName) {
    int pos = rowDesc.columnIndex(columnName);
    return pos == -1 ? null : getBoolean(pos);
  }

  @Override
  public Object getValue(String columnName) {
    int pos = rowDesc.columnIndex(columnName);
    return pos == -1 ? null : getValue(pos);
  }

  @Override
  public Short getShort(String columnName) {
    int pos = rowDesc.columnIndex(columnName);
    return pos == -1 ? null : getShort(pos);
  }

  @Override
  public Integer getInteger(String columnName) {
    int pos = rowDesc.columnIndex(columnName);
    return pos == -1 ? null : getInteger(pos);
  }

  @Override
  public Long getLong(String columnName) {
    int pos = rowDesc.columnIndex(columnName);
    return pos == -1 ? null : getLong(pos);
  }

  @Override
  public Float getFloat(String columnName) {
    int pos = rowDesc.columnIndex(columnName);
    return pos == -1 ? null : getFloat(pos);
  }

  @Override
  public Double getDouble(String columnName) {
    int pos = rowDesc.columnIndex(columnName);
    return pos == -1 ? null : getDouble(pos);
  }

  @Override
  public String getString(String columnName) {
    int pos = rowDesc.columnIndex(columnName);
    return pos == -1 ? null : getString(pos);
  }

  @Override
  public Buffer getBuffer(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Temporal getTemporal(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public LocalDate getLocalDate(String columnName) {
    int pos = rowDesc.columnIndex(columnName);
    return pos == -1 ? null : getLocalDate(pos);
  }

  @Override
  public LocalTime getLocalTime(String columnName) {
    int pos = rowDesc.columnIndex(columnName);
    return pos == -1 ? null : getLocalTime(pos);
  }

  @Override
  public LocalDateTime getLocalDateTime(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public OffsetTime getOffsetTime(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public OffsetDateTime getOffsetDateTime(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public UUID getUUID(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BigDecimal getBigDecimal(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Integer[] getIntegerArray(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean[] getBooleanArray(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Short[] getShortArray(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Long[] getLongArray(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Float[] getFloatArray(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Double[] getDoubleArray(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String[] getStringArray(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public LocalDate[] getLocalDateArray(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public LocalTime[] getLocalTimeArray(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public OffsetTime[] getOffsetTimeArray(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public LocalDateTime[] getLocalDateTimeArray(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public OffsetDateTime[] getOffsetDateTimeArray(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Buffer[] getBufferArray(String columnName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public UUID[] getUUIDArray(String columnName) {
    throw new UnsupportedOperationException();
  }
}
