package com.billyyccc.mssqlclient;

import com.billyyccc.mssqlclient.junit.MSSQLRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;

public abstract class MSSQLTestBase {

  @ClassRule
  public static MSSQLRule rule = new MSSQLRule();

  protected static MSSQLConnectOptions options;

  @BeforeClass
  public static void before() {
    options = rule.options();
  }
}
