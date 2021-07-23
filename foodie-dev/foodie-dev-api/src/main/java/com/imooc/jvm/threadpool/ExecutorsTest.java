package com.imooc.jvm.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ExecutorsTest {

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    private static ExecutorService executorService1 = Executors.newFixedThreadPool(5);

    private static ExecutorService executorService2 = Executors.newSingleThreadExecutor();

    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    private static ExecutorService executorService3 = Executors.newWorkStealingPool(5);




    public static void main(String[] args){

        executorService1.execute(() -> {
            System.out.println("aaa");
        });
    }



}
