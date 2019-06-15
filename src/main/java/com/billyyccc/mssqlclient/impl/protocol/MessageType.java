package com.billyyccc.mssqlclient.impl.protocol;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {

  SQL_BATCH(1),
  PRE_TDS7_LOGIN(2),
  RPC(3),
  TABULAR_RESULT(4),
  ATTENTION_SIGNAL(6),
  BULK_LOAD_DATA(7),
  FEDERATED_AUTHENTICATION_TOKEN(8),
  TRANSACTION_MANAGER_REQUEST(14),
  TDS7_LOGIN(16),
  SSPI(17),
  PRE_LOGIN(18);

  private static final Map<Integer, MessageType> typeMapping = new HashMap<>();

  static {
    typeMapping.put(SQL_BATCH.value, SQL_BATCH);
    typeMapping.put(PRE_TDS7_LOGIN.value, PRE_TDS7_LOGIN);
    typeMapping.put(RPC.value, RPC);
    typeMapping.put(TABULAR_RESULT.value, TABULAR_RESULT);
    typeMapping.put(ATTENTION_SIGNAL.value, ATTENTION_SIGNAL);
    typeMapping.put(BULK_LOAD_DATA.value, BULK_LOAD_DATA);
    typeMapping.put(FEDERATED_AUTHENTICATION_TOKEN.value, FEDERATED_AUTHENTICATION_TOKEN);
    typeMapping.put(TRANSACTION_MANAGER_REQUEST.value, TRANSACTION_MANAGER_REQUEST);
    typeMapping.put(TDS7_LOGIN.value, TDS7_LOGIN);
    typeMapping.put(SSPI.value, SSPI);
    typeMapping.put(PRE_LOGIN.value, PRE_LOGIN);
  }

  private final int value;

  MessageType(int value) {
    this.value = value;
  }

  public static MessageType valueOf(int value) {
    return typeMapping.get(value);
  }

  public int value() {
    return this.value;
  }
}
