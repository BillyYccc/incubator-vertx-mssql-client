package com.billyyccc.mssqlclient.tck;

import com.billyyccc.mssqlclient.junit.MSSQLRule;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.sqlclient.tck.SimpleQueryTestBase;
import org.junit.ClassRule;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class MSSQLBatchTest extends SimpleQueryTestBase {
  @ClassRule
  public static MSSQLRule rule = new MSSQLRule();

  @Override
  protected void initConnector() {
    connector = ClientConfig.CONNECT.connect(vertx, rule.options());
  }

}
