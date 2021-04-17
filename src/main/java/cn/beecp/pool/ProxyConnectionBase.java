/*
 * Copyright(C) Chris2018998
 * Contact:Chris2018998@tom.com
 *
 * Licensed under GNU General Public License version 3.0.
 */
package cn.beecp.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executor;

import static cn.beecp.pool.PoolStaticCenter.*;
import static java.lang.System.currentTimeMillis;

/**
 * raw connection wrapper
 *
 * @author Chris.Liao
 * @version 1.0
 */
public abstract class ProxyConnectionBase implements Connection {
    protected Connection delegate;
    protected PooledConnection pCon;//called by subclass to update time
    private boolean isClosed;

    public ProxyConnectionBase(PooledConnection pCon) {
        this.pCon = pCon;
        pCon.proxyCon = this;
        this.delegate = pCon.rawCon;
    }

    public Connection getDelegate() throws SQLException {
        checkClosed();
        return delegate;
    }

    public final boolean isClosed() throws SQLException {
        return isClosed;
    }

    protected final void checkClosed() throws SQLException {
        if (isClosed) throw ConnectionClosedException;
    }

    public final void close() throws SQLException {
        synchronized (this) {//safe close
            if (isClosed) return;
            isClosed = true;
            delegate = CLOSED_CON;
            if (pCon.traceIdx > 0)
                pCon.clearStatement();
        }
        pCon.recycleSelf();
    }

    final void trySetAsClosed() {//called from FastConnectionPool
        try {
            close();
        } catch (Throwable e) {
        }
    }

    /************* statement trace :logic from mysql driver******************************/
    synchronized final void registerStatement(ProxyStatementBase st) {
        pCon.registerStatement(st);
    }

    synchronized final void unregisterStatement(ProxyStatementBase st) {
        pCon.unregisterStatement(st);
    }

    /************* statement trace ******************************/

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        checkClosed();
        if (!pCon.curAutoCommit && pCon.commitDirtyInd)
            throw AutoCommitChangeForbiddenException;
        delegate.setAutoCommit(autoCommit);
        pCon.curAutoCommit = autoCommit;
        if (autoCommit) pCon.commitDirtyInd = false;
        pCon.setResetInd(PS_AUTO, autoCommit != pCon.defAutoCommit);
    }

    public void setTransactionIsolation(int level) throws SQLException {
        delegate.setTransactionIsolation(level);
        pCon.setResetInd(PS_TRANS, level != pCon.defTransactionIsolationCode);
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        delegate.setReadOnly(readOnly);
        pCon.setResetInd(PS_READONLY, readOnly != pCon.defReadOnly);
    }

    public void setCatalog(String catalog) throws SQLException {
        delegate.setCatalog(catalog);
        pCon.setResetInd(PS_CATALOG, !PoolStaticCenter.equals(catalog, pCon.defCatalog));
    }

    public boolean isValid(int timeout) throws SQLException {
        return delegate.isValid(timeout);
    }

    //for JDK1.7 begin
    public void setSchema(String schema) throws SQLException {
        delegate.setSchema(schema);
        pCon.setResetInd(PS_SCHEMA, !PoolStaticCenter.equals(schema, pCon.defSchema));
    }

    public void abort(Executor executor) throws SQLException {
        checkClosed();
        if (executor == null) throw new SQLException("executor can't be null");
        executor.execute(new Runnable() {
            public void run() {
                ProxyConnectionBase.this.trySetAsClosed();
            }
        });
    }

    public int getNetworkTimeout() throws SQLException {
        return delegate.getNetworkTimeout();
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        checkClosed();
        if (pCon.supportNetworkTimeout()) {
            delegate.setNetworkTimeout(executor, milliseconds);
            pCon.setResetInd(PS_NETWORK, milliseconds != pCon.defNetworkTimeout);
        } else {
            throw DriverNotSupportNetworkTimeoutException;
        }
    }
    //for JDK1.7 end

    public void commit() throws SQLException {
        delegate.commit();
        pCon.lastAccessTime = currentTimeMillis();
        pCon.commitDirtyInd = false;
    }

    public void rollback() throws SQLException {
        delegate.rollback();
        pCon.lastAccessTime = currentTimeMillis();
        pCon.commitDirtyInd = false;
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
