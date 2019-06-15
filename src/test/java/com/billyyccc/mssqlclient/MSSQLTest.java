package com.billyyccc.mssqlclient;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MSSQLServerContainer;

public class MSSQLTest {
  @Rule
  public MSSQLServerContainer mssqlserver = new MSSQLServerContainer() {
    @Override
    protected void configure() {
      this.addExposedPort(MSSQLServerContainer.MS_SQL_SERVER_PORT);
      this.addEnv("ACCEPT_EULA", "Y");
      this.addEnv("SA_PASSWORD", this.getPassword());
    }
  };

  @Test
  public void test() {
    String url = mssqlserver.getJdbcUrl();
    Assert.assertFalse(url.isEmpty());
  }
}
