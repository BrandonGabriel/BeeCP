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
package cn.beecp;

import cn.beecp.pool.ConnectionPool;
import cn.beecp.pool.ConnectionPoolMonitorVo;
import cn.beecp.pool.FastConnectionPool;
import cn.beecp.pool.ProxyConnectionBase;
import cn.beecp.xa.XaConnectionFactory;
import cn.beecp.xa.XaConnectionWrapper;

import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

import static cn.beecp.pool.PoolStaticCenter.commonLog;
import static cn.beecp.pool.PoolStaticCenter.isBlank;

/**
 * Bee DataSource,there are two pool implementation for it.
 * 1) cn.beecp.pool.FastConnectionPool:base implementation with semaphore
 * 2) cn.beecp.pool.RawConnectionPool:return raw connections to borrowers directly(maybe used for BeeNode)
 * <p>
 * Email:  Chris2018998@tom.com
 * Project: https://github.com/Chris2018998/BeeCP
 *
 * @author Chris.Liao
 * @version 1.0
 */
//fix BeeCP-Starter-#6 Chris-2020-09-01 start
//public final class BeeDataSource extends BeeDataSourceConfig implements DataSource {
public class BeeDataSource extends BeeDataSourceConfig implements DataSource, XADataSource {
    //fix BeeCP-Starter-#6 Chris-2020-09-01 end
    private static final HashMap<String, String> XaConnectionFactoryMap = new HashMap(5);
    private static final SQLException XaConnectionFactoryNotFound = new SQLException("xaConnectionFactory can't be null,please config xaConnectionFactory or xaConnectionFactoryClassName");

    static {
        XaConnectionFactoryMap.put("oracle", "cn.beecp.xa.OracleXaConnectionFactory");
        XaConnectionFactoryMap.put("mariadb", "cn.beecp.xa.MariadbXaConnectionFactory");
        XaConnectionFactoryMap.put("mysql5", "cn.beecp.xa.Mysql5XaConnectionFactory");
        XaConnectionFactoryMap.put("mysql8", "cn.beecp.xa.Mysql8XaConnectionFactory");
        XaConnectionFactoryMap.put("postgresql", "cn.beecp.xa.PostgresXaConnectionFactory");
    }

    //pool initialized
    private boolean inited;
    private ConnectionPool pool;
    private SQLException failedCause;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private XaConnectionFactory xaConnectionFactory;

    public BeeDataSource() {
    }

    public BeeDataSource(final BeeDataSourceConfig config) {
        try {
            config.copyTo(this);
            pool = createPool(this);
            inited = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * constructor
     *
     * @param driver   driver class name
     * @param url      JDBC url
     * @param user     JDBC user name
     * @param password JDBC password
     */
    public BeeDataSource(String driver, String url, String user, String password) {
        super(driver, url, user, password);
    }

    /**
     * borrow a connection from pool
     *
     * @return If exists idle connection in pool,then return one;if not, waiting
     * until other borrower release
     * @throws SQLException if pool is closed or waiting timeout,then throw exception
     */
    public Connection getConnection() throws SQLException {
        if (inited) return pool.getConnection();

        if (writeLock.tryLock()) {
            try {
                if (!inited) {
                    failedCause = null;
                    pool = createPool(this);
                    inited = true;
                }
            } catch (Throwable e) {//why?
                failedCause = (e instanceof SQLException) ? (SQLException) e : new SQLException(e);
                throw failedCause;
            } finally {
                writeLock.unlock();
            }
        } else {
            try {
                readLock.lock();
                if (failedCause != null) throw failedCause;
            } finally {
                readLock.unlock();
            }
        }

        return pool.getConnection();
    }

    public XAConnection getXAConnection() throws SQLException {
        if (xaConnectionFactory == null) throw XaConnectionFactoryNotFound;
        ProxyConnectionBase proxyCon = (ProxyConnectionBase) this.getConnection();
        return new XaConnectionWrapper(xaConnectionFactory.create(proxyCon.getDelegate()), proxyCon);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        throw new SQLException("Not support");
    }

    public XAConnection getXAConnection(String user, String password) throws SQLException {
        throw new SQLException("Not support");
    }

    public void close() {
        if (pool != null) {
            try {
                pool.close();
            } catch (SQLException e) {
                commonLog.error("Error at closing connection pool,cause:", e);
            }
        }
    }

    public boolean isClosed() {
        return (pool != null) ? pool.isClosed() : true;
    }

    public PrintWriter getLogWriter() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    public int getLoginTimeout() throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        throw new SQLFeatureNotSupportedException("Not supported");
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this))
            return (T) this;
        else
            throw new SQLException("Wrapped object was not an instance of " + iface);
    }

    /**
     * @return pool monitor vo
     * @throws SQLException if pool not be initialized
     */
    public ConnectionPoolMonitorVo getPoolMonitorVo() throws SQLException {
        if (pool == null) throw new SQLException("Connection pool not be initialized");
        return pool.getMonitorVo();
    }

    /**
     * clear all pooled connections from pool
     *
     * @throws SQLException if pool under datasource not be initialized
     */
    public void clearAllConnections() throws SQLException {
        this.clearAllConnections(false);
    }

    /**
     * clear all pooled connections from pool
     *
     * @param force close using connection directly
     * @throws SQLException if pool under datasource not be initialized
     */
    public void clearAllConnections(boolean force) throws SQLException {
        if (pool == null) throw new SQLException("Connection pool not be initialized");
        pool.clearAllConnections(force);
    }

    /**
     * create a pool instance by specified class name in configuration,
     * and initialize the pool with configuration
     *
     * @param config pool configuration
     * @return a initialized pool for data source
     */
    private final ConnectionPool createPool(BeeDataSourceConfig config) throws SQLException {
        //1:try to create xaFactory,why at first?
        xaConnectionFactory = tryCreateXAConnectionFactoryByConfig(config);
        if (xaConnectionFactory == null && !isBlank(config.getUrl())) {
            String driverType = getDriverType(config.getUrl());
            if (!isBlank(driverType)) {
                String xaConnectionFactoryClassName = XaConnectionFactoryMap.get(driverType);
                xaConnectionFactory = createXAConnectionFactoryByClassName(xaConnectionFactoryClassName);
            }
        }

        //2:create pool instance and init it with config
        ConnectionPool pool = createPoolInstanceByConfig(config);
        pool.init(config);
        return pool;
    }

    private String getDriverType(String url) {
        try {
            Driver driver = DriverManager.getDriver(url);
            url = url.toLowerCase(Locale.US);
            String urlPrefix = "jdbc:";
            if (url.startsWith(urlPrefix)) {
                int pos = url.indexOf(":", urlPrefix.length());
                if (pos > 0) return url.substring(urlPrefix.length(), pos);
            }

            if (url.indexOf("oracle") > 1) {
                return "oracle";
            } else if (url.indexOf("mysql") > 1) {
                return "mysql" + driver.getMajorVersion();
            } else if (url.indexOf("mariadb") > 1) {
                return "mariadb";
            } else if (url.indexOf("postgresql") > 1) {
                return "postgresql";
            } else {
                return null;
            }
        } catch (Throwable e) {
            return null;
        }
    }

    //try to create connection pool instance by config
    private final ConnectionPool createPoolInstanceByConfig(BeeDataSourceConfig config) throws BeeDataSourceConfigException {
        String poolImplementClassName = config.getPoolImplementClassName();
        if (isBlank(poolImplementClassName)) poolImplementClassName = FastConnectionPool.class.getName();

        try {
            Class<?> poolClass = Class.forName(poolImplementClassName, true, getClass().getClassLoader());
            if (ConnectionPool.class.isAssignableFrom(poolClass)) {
                return (ConnectionPool) poolClass.newInstance();
            } else {
                throw new BeeDataSourceConfigException("poolImplementClassName error,must implement '" + ConnectionPool.class.getName() + "' interface");
            }

        } catch (ClassNotFoundException e) {
            throw new BeeDataSourceConfigException("Not found connection pool class:" + poolImplementClassName);
        } catch (InstantiationException e) {
            throw new BeeDataSourceConfigException("Failed to instantiate connection pool class:" + poolImplementClassName, e);
        } catch (IllegalAccessException e) {
            throw new BeeDataSourceConfigException("Failed to instantiate connection pool class:" + poolImplementClassName, e);
        }
    }

    //try to create XAConnection factory by config
    private final XaConnectionFactory tryCreateXAConnectionFactoryByConfig(BeeDataSourceConfig config) throws BeeDataSourceConfigException {
        if (config.getXaConnectionFactory() != null) return config.getXaConnectionFactory();
        return createXAConnectionFactoryByClassName(config.getXaConnectionFactoryClassName());
    }

    private final XaConnectionFactory createXAConnectionFactoryByClassName(String xaConnectionFactoryClassName) throws BeeDataSourceConfigException {
        if (!isBlank(xaConnectionFactoryClassName)) {
            try {
                Class<?> xaConnectionFactoryClass = Class.forName(xaConnectionFactoryClassName, true, this.getClass().getClassLoader());
                if (XaConnectionFactory.class.isAssignableFrom(xaConnectionFactoryClass)) {
                    return (XaConnectionFactory) xaConnectionFactoryClass.newInstance();
                } else {
                    throw new BeeDataSourceConfigException("xaConnectionFactoryClassName error,must implement '" + XaConnectionFactory.class.getName() + "' interface");
                }
            } catch (ClassNotFoundException e) {
                throw new BeeDataSourceConfigException("Not found XAConnection factory class:" + xaConnectionFactoryClassName);
            } catch (InstantiationException e) {
                throw new BeeDataSourceConfigException("Failed to instantiate XAConnection factory class:" + xaConnectionFactoryClassName, e);
            } catch (IllegalAccessException e) {
                throw new BeeDataSourceConfigException("Failed to instantiate XAConnection factory class:" + xaConnectionFactoryClassName, e);
            }
        }
        return null;
    }
}
