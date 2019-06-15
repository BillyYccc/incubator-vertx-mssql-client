package com.billyyccc.mssqlclient.impl.protocol;

import java.util.HashMap;
import java.util.Map;

public enum MessageStatus {

  NORMAL(0x00),
  END_OF_MESSAGE(0x01),
  IGNORE_THIS_EVENT(0x02),
  RESET_CONNECTION(0x08),
  RESET_CONNECTION_SKIP_TRAN(0x10);

  private static final Map<Integer, MessageStatus> typeMapping = new HashMap<>();

  static {
    typeMapping.put(NORMAL.value, NORMAL);
    typeMapping.put(END_OF_MESSAGE.value, END_OF_MESSAGE);
    typeMapping.put(IGNORE_THIS_EVENT.value, IGNORE_THIS_EVENT);
    typeMapping.put(RESET_CONNECTION.value, RESET_CONNECTION);
    typeMapping.put(RESET_CONNECTION_SKIP_TRAN.value, RESET_CONNECTION_SKIP_TRAN);
  }

  private final int value;

  MessageStatus(int value) {
    this.value = value;
  }

  public static MessageStatus valueOf(int value) {
    return typeMapping.get(value);
  }

  public int value() {
    return this.value;
  }
}
