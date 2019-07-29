package com.billyyccc.mssqlclient.impl.protocol.datatype;

/*
  DATE MUST NOT have a TYPE_VARLEN.
  The value is either 3 bytes or 0 bytes (null).
  TIME, DATETIME2, and DATETIMEOFFSET MUST NOT have a TYPE_VARLEN. The lengths are determined by the SCALE as indicated in section 2.2.5.4.2.
  PRECISION and SCALE MUST occur if the type is NUMERIC, NUMERICN, DECIMAL, or DECIMALN.
  SCALE (without PRECISION) MUST occur if the type is TIME, DATETIME2, or DATETIMEOFFSET (introduced in TDS 7.3). PRECISION MUST be less than or equal to decimal 38 and SCALE MUST be less than or equal to the precision value.
  COLLATION occurs only if the type is BIGCHARTYPE, BIGVARCHRTYPE, TEXTTYPE, NTEXTTYPE, NCHARTYPE, or NVARCHARTYPE.
  UDT_INFO always occurs if the type is UDTTYPE.
  XML_INFO always occurs if the type is XMLTYPE.
  USHORTMAXLEN does not occur if PARTLENTYPE is XMLTYPE or UDTTYPE.
 */
public abstract class MSSQLDataType {
  protected final int id;
  protected final Class<?> mappedJavaType;

  public MSSQLDataType(int id, Class<?> mappedJavaType) {
    this.id = id;
    this.mappedJavaType = mappedJavaType;
  }

  public int id() {
    return id;
  }

  public Class<?> mappedJavaType() {
    return mappedJavaType;
  }
}
