/*
 * Copyright(C) Chris2018998
 * Contact:Chris2018998@tom.com
 *
 * Licensed under GNU General Public License version 3.0.
 */
package cn.beecp.pool;

/**
 * Pool Connection borrower
 *
 * @author Chris.Liao
 * @version 1.0
 */
final class Borrower {
    public volatile Object state;
    public PooledConnection lastUsed;
    public Thread thread = Thread.currentThread();
}