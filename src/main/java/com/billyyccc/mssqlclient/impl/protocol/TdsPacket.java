package com.billyyccc.mssqlclient.impl.protocol;

import io.netty.buffer.ByteBuf;

public final class TdsPacket {
  public static final int PACKET_HEADER_SIZE = 8;
  public static final int MAX_PACKET_DATA_SIZE = 0xFFFF - 8;

  private final MessageType type;
  private final MessageStatus status;
  private final int length;
  private final int processId;
  private final short packetId;
  private final ByteBuf data;

  private TdsPacket(MessageType type, MessageStatus status, int length, int processId, short packetId, ByteBuf data) {
    this.type = type;
    this.status = status;
    this.length = length;
    this.processId = processId;
    this.packetId = packetId;
    this.data = data;
  }

  public static TdsPacket newTdsPacket(MessageType type, MessageStatus status, int length, int processId, short packetId, ByteBuf data) {
    return new TdsPacket(type, status, length, processId, packetId, data);
  }

  public MessageType type() {
    return type;
  }

  public MessageStatus status() {
    return status;
  }

  public int length() {
    return length;
  }

  public int processId() {
    return processId;
  }

  public short packetId() {
    return packetId;
  }

  public ByteBuf data() {
    return data;
  }
}
