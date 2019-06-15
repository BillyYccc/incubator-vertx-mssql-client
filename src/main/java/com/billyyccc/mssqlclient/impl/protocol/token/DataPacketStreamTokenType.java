package com.billyyccc.mssqlclient.impl.protocol.token;

public final class DataPacketStreamTokenType {
  public static final byte ALTMETADATA_TOKEN = (byte) 0x88;
  public static final byte ALTROW_TOKEN = (byte) 0xD3;
  public static final byte COLMETADATA_TOKEN = (byte) 0x81;
  public static final byte COLINFO_TOKEN = (byte) 0xA5;
  public static final byte DONE_TOKEN = (byte) 0xFD;
  public static final byte DONEPROC_TOKEN = (byte) 0xFE;
  public static final byte DONEINPROC_TOKEN = (byte) 0xFF;
  public static final byte ENVCHANGE_TOKEN = (byte) 0xE3;
  public static final byte ERROR_TOKEN = (byte) 0xAA;
  public static final byte FEATUREEXTACK = (byte) 0xAE;
  public static final byte FEDAUTHINFO_TOKEN = (byte) 0xEE;
  public static final byte INFO_TOKEN = (byte) 0xAB;
  public static final byte LOGINACK_TOKEN = (byte) 0xAD;
  public static final byte NBCROW_TOKEN = (byte) 0xD2;
  public static final byte ORDER_TOKEN = (byte) 0xA9;
  public static final byte RETURNSTATUS_TOKEN = 0x79;
  public static final byte RETURNVALUE_TOKEN = (byte) 0xAC;
  public static final byte ROW_TOKEN = (byte) 0xD1;
  public static final byte SESSIONSTATE_TOKEN = (byte) 0xE4;
  public static final byte SSPI_TOKEN = (byte) 0xED;
  public static final byte TABNAME_TOKEN = (byte) 0xA4;

  public static final byte OFFSET_TOKEN = 0x78;
}
