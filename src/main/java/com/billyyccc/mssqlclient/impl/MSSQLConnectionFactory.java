package com.billyyccc.mssqlclient.impl;

import com.billyyccc.mssqlclient.MSSQLConnectOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.impl.NetSocketInternal;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.sqlclient.impl.Connection;

class MSSQLConnectionFactory {
  private final NetClient netClient;
  private final Context context;

  private final String host;
  private final int port;
  private final String username;
  private final String password;
  private final String database;

  public MSSQLConnectionFactory(Context context, MSSQLConnectOptions options) {
    NetClientOptions netClientOptions = new NetClientOptions(options);

    this.context = context;
    this.host = options.getHost();
    this.port = options.getPort();
    this.username = options.getUser();
    this.password = options.getPassword();
    this.database = options.getDatabase();

    this.netClient = context.owner().createNetClient(netClientOptions);
  }

  public void create(Handler<AsyncResult<Connection>> completionHandler) {
    Promise<NetSocket> promise = Promise.promise();
    promise.future().setHandler(connect -> {
      if (connect.succeeded()) {
        NetSocketInternal socket = (NetSocketInternal) connect.result();
        MSSQLSocketConnection conn = new MSSQLSocketConnection(socket, false, 0, 0, 1, context);
        conn.init();
        conn.sendPreLoginMessage(false, preLogin -> {
          if (preLogin.succeeded()) {
            conn.sendLoginMessage(username, password, database, completionHandler);
          } else {
            completionHandler.handle(Future.failedFuture(preLogin.cause()));
          }
        });
      } else {
        completionHandler.handle(Future.failedFuture(connect.cause()));
      }
    });
    netClient.connect(port, host, promise);
  }
}
