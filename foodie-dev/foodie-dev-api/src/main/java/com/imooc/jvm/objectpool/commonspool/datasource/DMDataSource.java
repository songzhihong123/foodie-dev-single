package com.imooc.jvm.objectpool.commonspool.datasource;

import org.apache.commons.pool2.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class DMDataSource implements DataSource {

    private GenericObjectPool<Connection> pool;

    public DMDataSource(){
        this.pool = new GenericObjectPool<>(new ConnectionPooledObjectFactory());
    }

    public GenericObjectPool<Connection> getPool() {
        return pool;
    }

    public void setPool(GenericObjectPool<Connection> pool) {
        this.pool = pool;
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return  this.pool.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("获取连接失败!",e);
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.getConnection();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
      throw new UnsupportedOperationException("不支持的操作");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("不支持的操作");
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException("不支持的操作");
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException("不支持的操作");
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException("不支持的操作");
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("不支持的操作");
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("不支持的操作");
    }
}