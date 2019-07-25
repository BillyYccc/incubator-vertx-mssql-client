package com.billyyccc.mssqlclient.impl.codec;

import com.billyyccc.mssqlclient.impl.MSSQLRowImpl;
import io.netty.buffer.ByteBuf;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.impl.RowDecoder;

import java.util.function.BiConsumer;
import java.util.stream.Collector;

class RowResultDecoder<C, R> implements RowDecoder {

  final Collector<Row, C, R> collector;
  final boolean singleton;
  final BiConsumer<C, Row> accumulator;
  final MSSQLRowDesc desc;

  private int size;
  private C container;
  private Row row;

  RowResultDecoder(Collector<Row, C, R> collector, boolean singleton, MSSQLRowDesc desc) {
    this.collector = collector;
    this.singleton = singleton;
    this.accumulator = collector.accumulator();
    this.desc = desc;
  }

  public int size() {
    return size;
  }

  @Override
  public void decodeRow(int len, ByteBuf in) {
    if (container == null) {
      container = collector.supplier().get();
    }
    if (singleton) {
      if (row == null) {
        row = new MSSQLRowImpl(desc);
      } else {
        row.clear();
      }
    } else {
      row = new MSSQLRowImpl(desc);
    }
    Row row = new MSSQLRowImpl(desc);
    for (int c = 0; c < len; c++) {
      Object decoded = null;
      ColumnData columnData = desc.columnDatas[c];
      decoded = MSSQLDataTypeCodec.decode(columnData.dataType(), in);
      row.addValue(decoded);
    }
    accumulator.accept(container, row);
    size++;
  }

  R complete() {
    if (container == null) {
      container = collector.supplier().get();
    }
    return collector.finisher().apply(container);
  }

  void reset() {
    container = null;
    size = 0;
  }
}
