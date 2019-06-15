package com.billyyccc.mssqlclient.impl;

import com.billyyccc.mssqlclient.impl.codec.MSSQLCodec;
import com.billyyccc.mssqlclient.impl.command.PreLoginCommand;
import io.netty.channel.ChannelPipeline;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.impl.NetSocketInternal;
import io.vertx.sqlclient.impl.Connection;
import io.vertx.sqlclient.impl.SocketConnectionBase;
import io.vertx.sqlclient.impl.command.CommandResponse;
import io.vertx.sqlclient.impl.command.InitCommand;

class MSSQLSocketConnection extends SocketConnectionBase {
  private MSSQLCodec codec;

  MSSQLSocketConnection(NetSocketInternal socket,
                        boolean cachePreparedStatements,
                        int preparedStatementCacheSize,
                        int preparedStatementCacheSqlLimit,
                        int pipeliningLimit,
                        Context context) {
    super(socket, cachePreparedStatements, preparedStatementCacheSize, preparedStatementCacheSqlLimit, pipeliningLimit, context);
  }

  // command response should show what capabilities server provides
  void sendPreLoginMessage(boolean ssl, Handler<? super CommandResponse<Void>> completionHandler) {
    PreLoginCommand cmd = new PreLoginCommand(ssl);
    cmd.handler = completionHandler;
    schedule(cmd);
  }

  void sendLoginMessage(String username, String password, String database, Handler<? super CommandResponse<Connection>> completionHandler) {
    InitCommand cmd = new InitCommand(this, username, password, database);
    cmd.handler = completionHandler;
    schedule(cmd);
  }

  @Override
  public void init() {
    codec = new MSSQLCodec();
    ChannelPipeline pipeline = socket.channelHandlerContext().pipeline();
    pipeline.addBefore("handler", "codec", codec);
    super.init();
  }
}
