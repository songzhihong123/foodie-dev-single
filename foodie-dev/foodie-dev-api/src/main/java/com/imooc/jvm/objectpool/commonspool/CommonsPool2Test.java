package com.imooc.jvm.objectpool.commonspool;

import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * @author szh
 */
public class CommonsPool2Test {

    public static void main(String[] args) throws Exception {
        GenericObjectPool<Money> pool = new GenericObjectPool<>(new MoneyPoolObjectFactory());
        Money money = pool.borrowObject();
        money.setType("RMB");
        pool.returnObject(money);

    }

}
