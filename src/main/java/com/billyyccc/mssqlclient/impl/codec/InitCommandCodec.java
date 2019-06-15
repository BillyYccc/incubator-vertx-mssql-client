package com.billyyccc.mssqlclient.impl.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import com.billyyccc.mssqlclient.impl.protocol.MessageStatus;
import com.billyyccc.mssqlclient.impl.protocol.MessageType;
import com.billyyccc.mssqlclient.impl.protocol.TdsMessage;
import com.billyyccc.mssqlclient.impl.protocol.client.login.LoginPacket;
import com.billyyccc.mssqlclient.impl.protocol.token.DataPacketStreamTokenType;
import com.billyyccc.mssqlclient.impl.utils.Utils;
import io.vertx.sqlclient.impl.Connection;
import io.vertx.sqlclient.impl.command.InitCommand;

import java.util.HashMap;
import java.util.Map;

class InitCommandCodec extends MSSQLCommandCodec<Connection, InitCommand> {
  InitCommandCodec(InitCommand cmd) {
    super(cmd);
  }

  @Override
  void encode(TdsMessageEncoder encoder) {
    super.encode(encoder);
    sendLoginMessage();
  }

  @Override
  void decodeMessage(TdsMessage message, TdsMessageEncoder encoder) {
    ByteBuf messageBody = message.content();
    while (messageBody.isReadable()) {
      byte tokenType = messageBody.readByte();
      switch (tokenType) {
        //FIXME complete all the logic here
        case DataPacketStreamTokenType.LOGINACK_TOKEN:
          result = cmd.connection();
          break;
        case DataPacketStreamTokenType.ERROR_TOKEN:
          handleErrorToken(messageBody);
          break;
        case DataPacketStreamTokenType.INFO_TOKEN:
          break;
        case DataPacketStreamTokenType.ENVCHANGE_TOKEN:
          break;
        case DataPacketStreamTokenType.DONE_TOKEN:
          handleDoneToken();
          break;
      }
    }
  }

  private void sendLoginMessage() {
    ChannelHandlerContext chctx = encoder.chctx;

    ByteBuf packet = chctx.alloc().ioBuffer();

    // packet header
    packet.writeByte(MessageType.TDS7_LOGIN.value());
    packet.writeByte(MessageStatus.NORMAL.value() | MessageStatus.END_OF_MESSAGE.value());
    int packetLenIdx = packet.writerIndex();
    packet.writeShort(0); // set length later
    packet.writeShort(0x00);
    packet.writeByte(0x00); // FIXME packet ID
    packet.writeByte(0x00);

    int startIdx = packet.writerIndex(); // Length
    packet.writeInt(0x00); // set length later by calculating
    packet.writeInt(LoginPacket.SQL_SERVER_2017_VERSION); // TDSVersion
    packet.writeIntLE(LoginPacket.DEFAULT_PACKET_SIZE); // PacketSize
    packet.writeIntLE(0x00); // ClientProgVer
    packet.writeIntLE(0x00); // ClientPID
    packet.writeIntLE(0x00); // ConnectionID
    packet.writeByte(LoginPacket.DEFAULT_OPTION_FLAGS1
      | LoginPacket.OPTION_FLAGS1_DUMPLOAD_OFF); // OptionFlags1
    packet.writeByte(LoginPacket.DEFAULT_OPTION_FLAGS2); // OptionFlags2
    packet.writeByte(LoginPacket.DEFAULT_TYPE_FLAGS); // TypeFlags
    packet.writeByte(LoginPacket.DEFAULT_OPTION_FLAGS3); // OptionFlags3
    packet.writeIntLE(0x00); // ClientTimeZone
    packet.writeIntLE(0x00); // ClientLCID

      /*
        OffsetLength part:
        we use a map to store offset related information:
        parameter name -> packet ByteBuf writer position for offset
        then we set offset by calculating ByteBuf writer indexes diff later
       */
    Map<String, Integer> offsetInfo = new HashMap<>();
    //FIXME all info

    // HostName
    String hostName = Utils.getHostName();
    offsetInfo.put("HostName", packet.writerIndex());
    packet.writeShortLE(0x00); // offset
    packet.writeShortLE(hostName.length());

    // UserName
    String userName = cmd.username();
    offsetInfo.put("UserName", packet.writerIndex());
    packet.writeShortLE(0x00); // offset
    packet.writeShortLE(userName.length());

    // Password
    String password = cmd.password();
    offsetInfo.put("Password", packet.writerIndex());
    packet.writeShortLE(0x00); // offset
    packet.writeShortLE(password.length());

    // AppName
    CharSequence appName = AsciiString.cached("vertx-mssql-client");
    offsetInfo.put("AppName", packet.writerIndex());
    packet.writeShortLE(0x00); // offset
    packet.writeShortLE(appName.length());

    // ServerName
    String serverName = cmd.connection().socket().remoteAddress().host();
    offsetInfo.put("ServerName", packet.writerIndex());
    packet.writeShortLE(0x00); // offset
    packet.writeShortLE(serverName.length());

    // Unused or Extension
    offsetInfo.put("Unused", packet.writerIndex());
    packet.writeShortLE(0x00); // offset
    packet.writeShortLE(0);

    // CltIntName
    CharSequence interfaceLibraryName = AsciiString.cached("vertx");
    offsetInfo.put("CltIntName", packet.writerIndex());
    packet.writeShortLE(0x00); // offset
    packet.writeShortLE(interfaceLibraryName.length());

    // Language
    offsetInfo.put("Language", packet.writerIndex());
    packet.writeShortLE(0x00); // offset
    packet.writeShortLE(0);

    // Database
    String database = cmd.database();
    offsetInfo.put("Database", packet.writerIndex());
    packet.writeShortLE(0x00); // offset
    packet.writeShortLE(database.length());

    // ClientID
    // 6 BYTE
    packet.writeIntLE(0x00);
    packet.writeShortLE(0x00);

    // SSPI
    offsetInfo.put("SSPI", packet.writerIndex());
    packet.writeShortLE(0x00); // offset
    packet.writeShortLE(0x00);

    // AtchDBFile
    offsetInfo.put("AtchDBFile", packet.writerIndex());
    packet.writeShortLE(0x00); // offset
    packet.writeShortLE(0x00);

    // ChangePassword
    offsetInfo.put("ChangePassword", packet.writerIndex());
    packet.writeShortLE(0x00); // offset
    packet.writeShortLE(0x00);

    // SSPILong
    packet.writeIntLE(0x00);

      /*
        Data part: note we should set offset by calculation before writing data
       */
    packet.setShortLE(offsetInfo.get("HostName"), packet.writerIndex() - startIdx);
    packet.writeCharSequence(hostName, CharsetUtil.UTF_16LE);

    packet.setShortLE(offsetInfo.get("UserName"), packet.writerIndex() - startIdx);
    packet.writeCharSequence(userName, CharsetUtil.UTF_16LE);

    packet.setShortLE(offsetInfo.get("Password"), packet.writerIndex() - startIdx);
    writePassword(packet, password);

    packet.setShortLE(offsetInfo.get("AppName"), packet.writerIndex() - startIdx);
    packet.writeCharSequence(appName, CharsetUtil.UTF_16LE);

    packet.setShortLE(offsetInfo.get("ServerName"), packet.writerIndex() - startIdx);
    packet.writeCharSequence(serverName, CharsetUtil.UTF_16LE);

    packet.setShortLE(offsetInfo.get("Unused"), packet.writerIndex() - startIdx);

    packet.setShortLE(offsetInfo.get("CltIntName"), packet.writerIndex() - startIdx);
    packet.writeCharSequence(interfaceLibraryName, CharsetUtil.UTF_16LE);

    packet.setShortLE(offsetInfo.get("Language"), packet.writerIndex() - startIdx);

    packet.setShortLE(offsetInfo.get("Database"), packet.writerIndex() - startIdx);
    packet.writeCharSequence(database, CharsetUtil.UTF_16LE);

    packet.setShortLE(offsetInfo.get("SSPI"), packet.writerIndex() - startIdx);

    packet.setShortLE(offsetInfo.get("AtchDBFile"), packet.writerIndex() - startIdx);

    packet.setShortLE(offsetInfo.get("ChangePassword"), packet.writerIndex() - startIdx);

    // set length
    packet.setIntLE(startIdx, packet.writerIndex() - startIdx);

    int packetLen = packet.writerIndex() - startIdx + 8;
    packet.setShort(packetLenIdx, packetLen);

    chctx.writeAndFlush(packet);

  }

  /*
    Before submitting a password from the client to the server,
    for every byte in the password buffer starting with the position pointed to by ibPassword or ibChangePassword,
    the client SHOULD first swap the four high bits with the four low bits and then do a bit-XOR with 0xA5 (10100101).
    After reading a submitted password, for every byte in the password buffer starting with the position pointed to by ibPassword or ibChangePassword,
    the server SHOULD first do a bit-XOR with 0xA5 (10100101) and then swap the four high bits with the four low bits.
   */
  private void writePassword(ByteBuf payload, String password) {
    byte[] bytes = password.getBytes(CharsetUtil.UTF_16LE);
    for (byte b : bytes) {
      payload.writeByte((b >> 4 | ((b & 0x0F) << 4)) ^ 0xA5);
    }
  }
}
