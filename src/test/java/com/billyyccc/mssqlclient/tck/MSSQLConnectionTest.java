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
    connector.close();
    super.tearDown(ctx);
  }

  @Ignore
  @Test
  @Override
  public void testConnect(TestContext ctx) {
    super.testConnect(ctx);
  }

  @Ignore
  @Test
  @Override
  public void testConnectInvalidDatabase(TestContext ctx) {
    super.testConnectInvalidDatabase(ctx);
  }

  @Ignore
  @Test
  @Override
  public void testConnectInvalidPassword(TestContext ctx) {
    super.testConnectInvalidPassword(ctx);
  }

  @Ignore
  @Test
  @Override
  public void testConnectInvalidUsername(TestContext ctx) {
    super.testConnectInvalidUsername(ctx);
  }

  @Ignore
  @Test
  @Override
  public void testClose(TestContext ctx) {
    super.testClose(ctx);
  }

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
