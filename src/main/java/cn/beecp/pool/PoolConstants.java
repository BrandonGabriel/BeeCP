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

import javax.transaction.xa.XAException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.*;

/**
 * Pool Constants definition
 *
 * @author Chris.Liao
 * @version 1.0
 */
public class PoolConstants {
    //POOL STATE
    public static final int POOL_UNINIT = 1;
    public static final int POOL_NORMAL = 2;
    public static final int POOL_CLOSED = 3;
    public static final int POOL_RESTING = 4;
    //POOLED CONNECTION STATE
    public static final int CONNECTION_IDLE = 1;
    public static final int CONNECTION_USING = 2;
    public static final int CONNECTION_CLOSED = 3;
    //ADD CONNECTION THREAD STATE
    public static final int THREAD_WORKING = 1;
    public static final int THREAD_WAITING = 2;
    public static final int THREAD_DEAD = 3;
    //BORROWER STATE
    public static final Object BORROWER_NORMAL = new Object();
    public static final Object BORROWER_WAITING = new Object();

    //Connection reset pos
    public static final int Pos_AutoCommitInd = 0;
    public static final int Pos_TransactionIsolationInd = 1;
    public static final int Pos_ReadOnlyInd = 2;
    public static final int Pos_CatalogInd = 3;
    public static final int Pos_SchemaInd = 4;
    public static final int Pos_NetworkTimeoutInd = 5;
    public static final boolean[] DEFAULT_IND = new boolean[6];

    public static final SQLTimeoutException RequestTimeoutException = new SQLTimeoutException("Request timeout");
    public static final SQLException RequestInterruptException = new SQLException("Request interrupt");
    public static final SQLException PoolCloseException = new SQLException("Pool has been closed or in resetting");
    public static final XAException XaConnectionClosedException = new XAException("No operations allowed after connection closed.");
    public static final SQLException ConnectionClosedException = new SQLException("No operations allowed after connection closed.");
    public static final SQLException StatementClosedException = new SQLException("No operations allowed after statement closed.");
    public static final SQLException ResultSetClosedException = new SQLException("No operations allowed after resultSet closed.");
    public static final SQLException AutoCommitChangeForbiddenException = new SQLException("Execute 'commit' or 'rollback' before this operation");
    public static final SQLException DriverNotSupportNetworkTimeoutException = new SQLException("Driver not support 'networkTimeout'");

    public static final Connection CLOSED_CON = (Connection) Proxy.newProxyInstance(
            PoolConstants.class.getClassLoader(),
            new Class[]{Connection.class},
            new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return call(method.getName(), 1);
                }
            }
    );

    public static final CallableStatement CLOSED_CSTM = (CallableStatement) Proxy.newProxyInstance(
            PoolConstants.class.getClassLoader(),
            new Class[]{CallableStatement.class},
            new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return call(method.getName(), 2);
                }
            }
    );

    public static final ResultSet CLOSED_RSLT = (ResultSet) Proxy.newProxyInstance(
            PoolConstants.class.getClassLoader(),
            new Class[]{ResultSet.class},
            new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    return call(method.getName(), 3);
                }
            }
    );

    private static final Object call(String methodName, int type) throws SQLException {
        switch (type) {
            case 1:
                throw ConnectionClosedException;
            case 2:
                throw StatementClosedException;
            case 3:
                throw ResultSetClosedException;
            default:
                throw ConnectionClosedException;
        }
    }
}