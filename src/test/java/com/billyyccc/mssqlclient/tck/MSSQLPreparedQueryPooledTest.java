package com.billyyccc.mssqlclient.tck;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class MSSQLPreparedQueryPooledTest extends MSSQLPreparedQueryTestBase {
  @Override
  public void setUp(TestContext ctx) throws Exception {
    vertx = Vertx.vertx();
    initConnector();
    cleanTestTable(ctx); // need to use batch instead of prepared statements
  }

  @Override
  protected void initConnector() {
    options = rule.options();
    connector = ClientConfig.POOLED.connect(vertx, options);
  }
}
