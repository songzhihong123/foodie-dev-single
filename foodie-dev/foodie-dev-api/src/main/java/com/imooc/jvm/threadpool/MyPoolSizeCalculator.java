package com.imooc.jvm.threadpool;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MyPoolSizeCalculator extends PoolSizeCalculator {

    public static void main(String[] args) {
        MyPoolSizeCalculator calculator = new MyPoolSizeCalculator();
        calculator.calculateBoundaries(
                //  CPU 目标利用率
                new BigDecimal(1.0),
                //  BlockingQueue 占用的内存大小 byte
                new BigDecimal(100000));
    }

    protected long getCurrentThreadCPUTime() {
        // 当前线程占用的总时间
        return ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
    }

    protected Runnable creatTask() {
        return new AsynchronousTask();
    }

    protected BlockingQueue createWorkQueue() {
        return new LinkedBlockingQueue<>();
    }

}
class AsynchronousTask implements Runnable{

    @Override
    public void run() {
//        System.out.println(Thread.currentThread().getName());
    }
}
