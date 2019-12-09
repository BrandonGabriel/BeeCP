/*
 * Copyright Chris2018998
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.beecp.pool;

import static cn.beecp.pool.PoolExceptionList.ResultSetClosedException;
import static cn.beecp.util.BeecpUtil.oclose;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ResultSet proxy base class
 * 
 * @author Chris.Liao
 * @version 1.0
 */
public abstract class ProxyResultSetBase implements ResultSet {
	private boolean isClosed;
	protected ResultSet delegate;
	private ProxyStatementTop proxyStatement;
	
	public ProxyResultSetBase(ResultSet delegate,ProxyStatementTop proxyStatement) {
		this.delegate = delegate;
		this.proxyStatement = proxyStatement;
	}
	protected void checkClose() throws SQLException {
		if(isClosed)throw ResultSetClosedException;
		if(proxyStatement!=null)proxyStatement.checkClose();
	}
	public Statement getStatement() throws SQLException{
		checkClose();
		return (Statement)proxyStatement;
	}
	public void close() throws SQLException {
		try{
			checkClose();
		}finally{
			isClosed = true;
			oclose(delegate);
			delegate = null;
			proxyStatement=null;
		}
	}
	public final boolean isWrapperFor(Class<?> iface) throws SQLException {
		checkClose();
		return iface.isInstance(delegate);
	}
	@SuppressWarnings("unchecked")
	public final <T> T unwrap(Class<T> iface) throws SQLException{
	  checkClose();
	  if (iface.isInstance(delegate)) {
         return (T)this;
      }else {
    	  throw new SQLException("Wrapped object is not an instance of " + iface);
      } 
	}
}
