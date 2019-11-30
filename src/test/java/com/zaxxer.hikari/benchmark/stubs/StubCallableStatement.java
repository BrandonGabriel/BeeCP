package com.zaxxer.hikari.benchmark.stubs;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public class StubCallableStatement extends StubPreparedStatement implements CallableStatement {

	public StubCallableStatement(Connection con) {
		super(con);
	}

	public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {

	}

	public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {

	}

	public boolean wasNull() throws SQLException {
		return true;
	}

	public String getString(int parameterIndex) throws SQLException {
		return "aString";
	}

	public boolean getBoolean(int parameterIndex) throws SQLException {
		return true;
	}

	public byte getByte(int parameterIndex) throws SQLException {
		return 1;
	}

	public short getShort(int parameterIndex) throws SQLException {
		return 1;
	}

	public int getInt(int parameterIndex) throws SQLException {
		return 1;
	}

	public long getLong(int parameterIndex) throws SQLException {
		return 1;
	}

	public float getFloat(int parameterIndex) throws SQLException {
		return 1;
	}

	public double getDouble(int parameterIndex) throws SQLException {
		return 1;
	}

	@Deprecated
	public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
		return new BigDecimal(1);
	}

	public byte[] getBytes(int parameterIndex) throws SQLException {
		return new byte[] { 1 };
	}

	public java.sql.Date getDate(int parameterIndex) throws SQLException {
		return new Date(System.currentTimeMillis());
	}

	public java.sql.Time getTime(int parameterIndex) throws SQLException {
		return new Time(System.currentTimeMillis());
	}

	public java.sql.Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return new Timestamp(System.currentTimeMillis());
	}

	public Object getObject(int parameterIndex) throws SQLException {
		return "1";
	}

	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		return new BigDecimal(1);
	}

	public Object getObject(int parameterIndex, java.util.Map<String, Class<?>> map) throws SQLException {
		return "1";
	}

	public Ref getRef(int parameterIndex) throws SQLException {
		return null;
	}

	public Blob getBlob(int parameterIndex) throws SQLException {
		return null;
	}

	public Clob getClob(int parameterIndex) throws SQLException {
		return null;
	}

	public Array getArray(int parameterIndex) throws SQLException {
		return null;
	}

	public java.sql.Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		return new Date(System.currentTimeMillis());
	}

	public java.sql.Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		return new Time(System.currentTimeMillis());
	}

	public java.sql.Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
		return new Timestamp(System.currentTimeMillis());
	}

	public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {

	}

	public void registerOutParameter(String parameterName, int sqlType) throws SQLException {

	}

	public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {

	}

	public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {

	}

	public java.net.URL getURL(int parameterIndex) throws SQLException {
		return null;
	}

	public void setURL(String parameterName, java.net.URL val) throws SQLException {
	}

	public void setNull(String parameterName, int sqlType) throws SQLException {
	}

	public void setBoolean(String parameterName, boolean x) throws SQLException {
	}

	public void setByte(String parameterName, byte x) throws SQLException {
	}

	public void setShort(String parameterName, short x) throws SQLException {
	}

	public void setInt(String parameterName, int x) throws SQLException {
	}

	public void setLong(String parameterName, long x) throws SQLException {
	}

	public void setFloat(String parameterName, float x) throws SQLException {
	}

	public void setDouble(String parameterName, double x) throws SQLException {
	}

	public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
	}

	public void setString(String parameterName, String x) throws SQLException {
	}

	public void setBytes(String parameterName, byte x[]) throws SQLException {
	}

	public void setDate(String parameterName, java.sql.Date x) throws SQLException {
	}

	public void setTime(String parameterName, java.sql.Time x) throws SQLException {
	}

	public void setTimestamp(String parameterName, java.sql.Timestamp x) throws SQLException {
	}

	public void setAsciiStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
	}

	public void setBinaryStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
	}

	public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
	}

	public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
	}

	public void setObject(String parameterName, Object x) throws SQLException {
	}

	public void setCharacterStream(String parameterName, java.io.Reader reader, int length) throws SQLException {
	}

	public void setDate(String parameterName, java.sql.Date x, Calendar cal) throws SQLException {
	}

	public void setTime(String parameterName, java.sql.Time x, Calendar cal) throws SQLException {
	}

	public void setTimestamp(String parameterName, java.sql.Timestamp x, Calendar cal) throws SQLException {
	}

	public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
	}

	public String getString(String parameterName) throws SQLException {
		return "123";
	}

	public boolean getBoolean(String parameterName) throws SQLException {
		return true;
	}

	public byte getByte(String parameterName) throws SQLException {
		return 1;
	}

	public short getShort(String parameterName) throws SQLException {
		return 1;
	}

	public int getInt(String parameterName) throws SQLException {
		return 1;
	}

	public long getLong(String parameterName) throws SQLException {
		return 1;
	}

	public float getFloat(String parameterName) throws SQLException {
		return 1;
	}

	public double getDouble(String parameterName) throws SQLException {
		return 1;
	}

	public byte[] getBytes(String parameterName) throws SQLException {
		return new byte[] { 1 };
	}

	public java.sql.Date getDate(String parameterName) throws SQLException {
		return new Date(System.currentTimeMillis());
	}

	public java.sql.Time getTime(String parameterName) throws SQLException {
		return new Time(System.currentTimeMillis());
	}

	public java.sql.Timestamp getTimestamp(String parameterName) throws SQLException {
		return new Timestamp(System.currentTimeMillis());
	}

	public Object getObject(String parameterName) throws SQLException {
		return "123";
	}

	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		return new BigDecimal(1);
	}

	public Object getObject(String parameterName, java.util.Map<String, Class<?>> map) throws SQLException {
		return "123";
	}

	public Ref getRef(String parameterName) throws SQLException {
		return null;
	}

	public Blob getBlob(String parameterName) throws SQLException {
		return null;
	}

	public Clob getClob(String parameterName) throws SQLException {
		return null;
	}

	public Array getArray(String parameterName) throws SQLException {
		return null;
	}

	public java.sql.Date getDate(String parameterName, Calendar cal) throws SQLException {
		return new Date(System.currentTimeMillis());
	}

	public java.sql.Time getTime(String parameterName, Calendar cal) throws SQLException {
		return new Time(System.currentTimeMillis());
	}

	public java.sql.Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
		return new Timestamp(System.currentTimeMillis());
	}

	public java.net.URL getURL(String parameterName) throws SQLException {
		return null;
	}

	public RowId getRowId(int parameterIndex) throws SQLException {
		return null;
	}

	public RowId getRowId(String parameterName) throws SQLException {
		return null;
	}

	public void setRowId(String parameterName, RowId x) throws SQLException {

	}

	public void setNString(String parameterName, String value) throws SQLException {
	}

	public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
	}

	public void setNClob(String parameterName, NClob value) throws SQLException {
	}

	public void setClob(String parameterName, Reader reader, long length) throws SQLException {
	}

	public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
	}

	public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
	}

	public NClob getNClob(int parameterIndex) throws SQLException {
		return null;
	}

	public NClob getNClob(String parameterName) throws SQLException {
		return null;
	}

	public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
	}

	public SQLXML getSQLXML(int parameterIndex) throws SQLException {
		return null;
	}

	public SQLXML getSQLXML(String parameterName) throws SQLException {
		return null;
	}

	public String getNString(int parameterIndex) throws SQLException {
		return null;
	}

	public String getNString(String parameterName) throws SQLException {
		return null;
	}

	public java.io.Reader getNCharacterStream(int parameterIndex) throws SQLException {
		return null;
	}

	public java.io.Reader getNCharacterStream(String parameterName) throws SQLException {
		return null;
	}

	public java.io.Reader getCharacterStream(int parameterIndex) throws SQLException {
		return null;
	}

	public java.io.Reader getCharacterStream(String parameterName) throws SQLException {
		return null;
	}

	public void setBlob(String parameterName, Blob x) throws SQLException {

	}

	public void setClob(String parameterName, Clob x) throws SQLException {
	}

	public void setAsciiStream(String parameterName, java.io.InputStream x, long length) throws SQLException {
	}

	public void setBinaryStream(String parameterName, java.io.InputStream x, long length) throws SQLException {
	}

	public void setCharacterStream(String parameterName, java.io.Reader reader, long length) throws SQLException {
	}

	public void setAsciiStream(String parameterName, java.io.InputStream x) throws SQLException {
	}

	public void setBinaryStream(String parameterName, java.io.InputStream x) throws SQLException {
	}

	public void setCharacterStream(String parameterName, java.io.Reader reader) throws SQLException {
	}

	public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
	}

	public void setClob(String parameterName, Reader reader) throws SQLException {
	}

	public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
	}

	public void setNClob(String parameterName, Reader reader) throws SQLException {
	}

	public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
		return null;
	}

	public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
		return null;
	}

}
