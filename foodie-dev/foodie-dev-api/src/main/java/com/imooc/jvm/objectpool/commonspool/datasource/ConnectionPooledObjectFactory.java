package com.imooc.jvm.objectpool.commonspool.datasource;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.sql.*;

public class ConnectionPooledObjectFactory implements PooledObjectFactory<Connection> {

    @Override
    public PooledObject<Connection> makeObject() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/foodie-shop-dev?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=UTC",
                "root",
                "briup"
        );
        return new DefaultPooledObject<>(connection);
    }

    @Override
    public void destroyObject(PooledObject<Connection> pooledObject) throws Exception {
        pooledObject.getObject().close();
    }

    @Override
    public boolean validateObject(PooledObject<Connection> pooledObject) {
        Connection connection = pooledObject.getObject();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select 1");
            ResultSet resultSet = preparedStatement.executeQuery();
            int anInt = resultSet.getInt(1);
            return anInt == 1;
        } catch (SQLException throwables) {
            return false;
        }
    }

    @Override
    public void activateObject(PooledObject<Connection> pooledObject) throws Exception {
            // 重新初始化对象调用
            // 可以把connection的格外配置放到这里
    }

    @Override
    public void passivateObject(PooledObject<Connection> pooledObject) throws Exception {
        //取消初始化对象的时候调用
    }
}
