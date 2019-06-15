package com.billyyccc.mssqlclient.impl.protocol.client.prelogin;

public final class VersionOptionToken extends OptionToken {
  public static final byte TYPE = 0x00;

  private final short majorVersion;
  private final short minorVersion;
  private final int buildNumber;
  private final int subBuildNumber;

  private final int optionLength;

  public VersionOptionToken(short majorVersion, short minorVersion, int buildNumber, int subBuildNumber) {
    super(TYPE);
    this.majorVersion = majorVersion;
    this.minorVersion = minorVersion;
    this.buildNumber = buildNumber;
    this.subBuildNumber = subBuildNumber;
    this.optionLength = 6; // 1 byte + 1 byte + 2 byte + 2 byte
  }

  public short majorVersion() {
    return majorVersion;
  }

  public short minorVersion() {
    return minorVersion;
  }

  public int buildNumber() {
    return buildNumber;
  }

  public int subBuildNumber() {
    return subBuildNumber;
  }

  public int optionLength() {
    return optionLength;
  }
}
