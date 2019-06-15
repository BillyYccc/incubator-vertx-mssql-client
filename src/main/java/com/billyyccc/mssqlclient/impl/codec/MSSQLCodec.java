package com.billyyccc.mssqlclient.impl.codec;

import io.netty.channel.CombinedChannelDuplexHandler;

import java.util.ArrayDeque;

public class MSSQLCodec extends CombinedChannelDuplexHandler<TdsMessageDecoder, TdsMessageEncoder> {
  private final ArrayDeque<MSSQLCommandCodec<?, ?>> inflight = new ArrayDeque<>();

  public MSSQLCodec() {
    TdsMessageEncoder encoder = new TdsMessageEncoder(inflight);
    TdsMessageDecoder decoder = new TdsMessageDecoder(inflight, encoder);
    init(decoder, encoder);
  }
}
