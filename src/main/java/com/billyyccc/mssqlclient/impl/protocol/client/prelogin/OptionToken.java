package com.billyyccc.mssqlclient.impl.protocol.client.prelogin;

public abstract class OptionToken {
  private final byte type;
  int optionLength;

  public OptionToken(byte type) {
    this.type = type;
  }

  public byte tokenType() {
    return type;
  }

  public int optionLength() {
    return optionLength;
  }
}
