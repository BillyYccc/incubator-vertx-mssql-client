package com.billyyccc.mssqlclient.tck;

import com.billyyccc.mssqlclient.junit.MSSQLRule;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.sqlclient.ConnectionTestBase;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class MSSQLConnectionTest extends ConnectionTestBase {
  @ClassRule
  public static MSSQLRule rule = new MSSQLRule();

  @Override
  public void setUp() throws Exception {
    super.setUp();
    options = rule.options().setDatabase("master");
    connector = ClientConfig.CONNECT.connect(vertx, options);
  }

  @Override
  public void tearDown(TestContext ctx) {
    super.tearDown(ctx);
  }

  /*
    TODO enable the tests when we support simple query
   */
  @Ignore
  @Test
  @Override
  public void testCloseWithErrorInProgress(TestContext ctx) {
    super.testCloseWithErrorInProgress(ctx);
  }

  @Ignore
  @Test
  @Override
  public void testCloseWithQueryInProgress(TestContext ctx) {
    super.testCloseWithQueryInProgress(ctx);
  }
}
