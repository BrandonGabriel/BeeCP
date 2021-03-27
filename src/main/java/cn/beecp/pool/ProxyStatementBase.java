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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static cn.beecp.pool.PoolStaticCenter.*;

/**
 * ProxyStatementBase
 *
 * @author Chris.Liao
 * @version 1.0
 */
abstract class ProxyStatementBase implements Statement {
    protected Statement delegate;
    protected PooledConnection pConn;//called by subclass to update time
    private ProxyConnectionBase owner;

    private boolean isClosed;
    private boolean registered;
    private ProxyResultSetBase curRe;
    private int resultOpenCode;
    private ArrayList<ProxyResultSetBase> results;

    public ProxyStatementBase(Statement delegate, PooledConnection pConn) {
        this.delegate = delegate;
        this.pConn = pConn;
        this.owner = pConn.proxyConn;
        this.registered = this.owner.registerStatement(this);
        this.resultOpenCode = CLOSE_CURRENT_RESULT;
    }

    private final void checkClosed() throws SQLException {
        if (isClosed) throw StatementClosedException;
    }

    public Connection getConnection() throws SQLException {
        checkClosed();
        return owner;
    }

    public final boolean isClosed() throws SQLException {
        return isClosed;
    }

    public final void close() throws SQLException {
        if (isClosed) return;
        isClosed = true;

        if (curRe != null) curRe.setAsClosed();
        if (results != null) {
            for (int i = 0, l = results.size(); i < l; i++)
                results.get(i).setAsClosed();
            results.clear();
        }

        try {
            delegate.close();
        } finally {
            delegate = CLOSED_CSTM;//why? because Mysql's PreparedStatement just only remark as closed with useServerCache mode
            if (registered) owner.unregisterStatement(this);
        }
    }

    void setAsClosed() {//call by PooledConnection.cleanOpenStatements
        try {
            registered = false;
            close();
        } catch (Throwable e) {
        }
    }

    final void removeOpenResultSet(ProxyResultSetBase resultSet) {//call by ProxyResultSetBase.constructor
        if (resultSet == curRe) {
            curRe = null;
        } else if (results != null) {
            results.remove(resultSet);
        }
    }

    final void setOpenResultSet(ProxyResultSetBase resultSetNew) {//call by ProxyResultSetBase.constructor
        switch (resultOpenCode) {
            case CLOSE_CURRENT_RESULT: {
                if (curRe != null && !curRe.isClosed) curRe.setAsClosed();
                break;
            }
            case KEEP_CURRENT_RESULT: {
                if (curRe != null && !curRe.isClosed) {
                    if (results == null) results = new ArrayList<ProxyResultSetBase>(1);
                    results.add(curRe);
                }
                break;
            }
            case CLOSE_ALL_RESULTS: {
                if (curRe != null && !curRe.isClosed)
                    curRe.setAsClosed();
                if (results != null) {
                    for (int i = 0, l = results.size(); i < l; i++) {
                        ProxyResultSetBase openRe = results.get(i);
                        if (!openRe.isClosed) openRe.setAsClosed();
                    }
                    results.clear();
                }
                break;
            }
            default:
                break;
        }
        this.curRe = resultSetNew;
    }

    public boolean getMoreResults() throws SQLException {
        return getMoreResults(CLOSE_CURRENT_RESULT);
    }

    public boolean getMoreResults(int current) throws SQLException {
        checkClosed();
        resultOpenCode = current;
        return delegate.getMoreResults(current);
    }

    public ResultSet getResultSet() throws SQLException {
        checkClosed();
        ResultSet re = delegate.getResultSet();//raw resultSet
        if (re == null) return null;

        if (curRe != null && curRe.containsDelegate(re)) return curRe;
        if (results != null) {
            for (ProxyResultSetBase resultSetBase : results) {
                if (resultSetBase.containsDelegate(re)) return resultSetBase;
            }
        }

        ProxyResultSetBase resultSetBase = (ProxyResultSetBase) createProxyResultSet(re, this, pConn);
        this.setOpenResultSet(resultSetBase);
        return resultSetBase;
    }

    public void setPoolable(boolean var1) throws SQLException {
        checkClosed();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this))
            return (T) this;
        else
            throw new SQLException("Wrapped object is not an instance of " + iface);
    }
}
