package com.billyyccc.mssqlclient.impl.protocol.client.prelogin;

public final class EncryptionOptionToken extends OptionToken {
  public static final byte TYPE = 0x01;

  public static final byte ENCRYPT_OFF = 0x00;
  public static final byte ENCRYPT_ON = 0x01;
  public static final byte ENCRYPT_NOT_SUP = 0x02;
  public static final byte ENCRYPT_REQ = 0x03;

  private final byte setting;

  public EncryptionOptionToken(byte setting) {
    super(TYPE);
    this.setting = setting;
    this.optionLength = 1; // 1 byte
  }

  public byte setting() {
    return setting;
  }

  public int optionLength() {
    return optionLength;
  }
}
