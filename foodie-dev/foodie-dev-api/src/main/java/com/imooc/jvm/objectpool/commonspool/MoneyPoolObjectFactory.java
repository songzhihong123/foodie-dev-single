package com.imooc.jvm.objectpool.commonspool;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class MoneyPoolObjectFactory implements PooledObjectFactory<Money> {

    public static final Logger logger = LoggerFactory.getLogger(MoneyPoolObjectFactory.class);

    @Override
    public PooledObject<Money> makeObject() throws Exception {
        DefaultPooledObject<Money> object = new DefaultPooledObject<>(new Money("USD", new BigDecimal("1")));
        logger.info("makeObject...state = {}" , object.getState());
        return object;
    }

    @Override
    public void destroyObject(PooledObject<Money> pooledObject) throws Exception {

        logger.info("makeObject...state = {}" , pooledObject.getState());
    }

    @Override
    public boolean validateObject(PooledObject<Money> pooledObject) {


        logger.info("makeObject...state = {}" , pooledObject.getState());
        return true;
    }

    @Override
    public void activateObject(PooledObject<Money> pooledObject) throws Exception {

        logger.info("makeObject...state = {}" , pooledObject.getState());
    }

    @Override
    public void passivateObject(PooledObject<Money> pooledObject) throws Exception {

        logger.info("makeObject...state = {}" , pooledObject.getState());
    }
}
