package com.billyyccc.mssqlclient.impl.codec;

import com.billyyccc.mssqlclient.impl.command.PreLoginCommand;
import com.billyyccc.mssqlclient.impl.protocol.MessageStatus;
import com.billyyccc.mssqlclient.impl.protocol.MessageType;
import com.billyyccc.mssqlclient.impl.protocol.TdsMessage;
import com.billyyccc.mssqlclient.impl.protocol.client.prelogin.EncryptionOptionToken;
import com.billyyccc.mssqlclient.impl.protocol.client.prelogin.OptionToken;
import com.billyyccc.mssqlclient.impl.protocol.client.prelogin.VersionOptionToken;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.vertx.sqlclient.impl.command.CommandResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PreLoginCommandCodec extends MSSQLCommandCodec<Void, PreLoginCommand> {

  PreLoginCommandCodec(PreLoginCommand cmd) {
    super(cmd);
  }

  @Override
  void encode(TdsMessageEncoder encoder) {
    super.encode(encoder);
    sendPreLoginMessage();
  }

  @Override
  void decodeMessage(TdsMessage message, TdsMessageEncoder encoder) {
    // nothing to do for now?
    completionHandler.handle(CommandResponse.success(null));
  }

  private void sendPreLoginMessage() {
    ChannelHandlerContext chctx = encoder.chctx;

    ByteBuf packet = chctx.alloc().ioBuffer();

    // packet header
    packet.writeByte(MessageType.PRE_LOGIN.value());
    packet.writeByte(MessageStatus.NORMAL.value() | MessageStatus.END_OF_MESSAGE.value());
    int packetLenIdx = packet.writerIndex();
    packet.writeShort(0); // set length later
    packet.writeShort(0x00);
    packet.writeByte(0x00); // FIXME packet ID
    packet.writeByte(0x00);

    // packet data
    int packetDataStartIdx = packet.writerIndex();

    List<OptionToken> optionTokens = cmd.optionTokens();

    int payloadStartIdx = packet.writerIndex();

    int totalLengthOfOptionsData = 0;

      /*
        we use a map to store offset and length related information:
        payload ByteBuf writer position for Option offset ->  Option length
        then we set offset by calculating ByteBuf writer indexes diff later
       */
    Map<Integer, Integer> offsetLengthInfo = new HashMap<>();

    // option token header
    for (OptionToken token : optionTokens) {
      totalLengthOfOptionsData += token.optionLength();
      packet.writeByte(token.tokenType());
      offsetLengthInfo.put(packet.writerIndex(), token.optionLength());
      packet.writeShort(0x00);
      packet.writeShort(token.optionLength());
    }

    // terminator token
    packet.writeByte(0xFF);

    // option token data
    for (OptionToken token : optionTokens) {
      encodeTokenData(token, packet);
    }

    // calculate Option offset
    int totalLengthOfPayload = packet.writerIndex() - payloadStartIdx;
    int offsetStart = totalLengthOfPayload - totalLengthOfOptionsData;

    for (Map.Entry<Integer, Integer> entry : offsetLengthInfo.entrySet()) {
      packet.setShort(entry.getKey(), offsetStart);
      offsetStart += entry.getValue();
    }

    int packetLen = packet.writerIndex() - packetDataStartIdx + 8;
    packet.setShort(packetLenIdx, packetLen);

    chctx.writeAndFlush(packet);
  }

  private void encodeTokenData(OptionToken optionToken, ByteBuf payload) {
    switch (optionToken.tokenType()) {
      case VersionOptionToken.TYPE:
        payload.writeInt(0); // UL_VERSION
        payload.writeShort(0); // US_BUILD
        break;
      case EncryptionOptionToken.TYPE:
        payload.writeByte(((EncryptionOptionToken) optionToken).setting());
        break;
    }
  }
}
