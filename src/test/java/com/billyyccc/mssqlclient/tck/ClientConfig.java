package com.billyyccc.mssqlclient.tck;

import com.billyyccc.mssqlclient.MSSQLConnectOptions;
import com.billyyccc.mssqlclient.MSSQLConnection;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Connector;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnectOptions;
import io.vertx.sqlclient.SqlConnection;

public enum ClientConfig {
  CONNECT() {
    @Override
    Connector<SqlConnection> connect(Vertx vertx, SqlConnectOptions options) {
      return new Connector<SqlConnection>() {
        @Override
        public void connect(Handler<AsyncResult<SqlConnection>> handler) {
          //TODO remove this when we have data object support for connect options
          MSSQLConnectOptions connectOptions = new MSSQLConnectOptions()
            .setHost(options.getHost())
            .setPort(options.getPort())
            .setUser(options.getUser())
            .setPassword(options.getPassword())
            .setDatabase(options.getDatabase());
          MSSQLConnection.connect(vertx, connectOptions, ar -> {
            if (ar.succeeded()) {
              handler.handle(Future.succeededFuture(ar.result()));
            } else {
              handler.handle(Future.failedFuture(ar.cause()));
            }
          });
        }

        @Override
        public void close() {
        }
      };
    }
  };

  abstract <C extends SqlClient> Connector<C> connect(Vertx vertx, SqlConnectOptions options);

}
