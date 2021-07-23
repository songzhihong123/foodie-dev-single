package com.imooc.jvm.threadpool;

import java.util.concurrent.*;

/**
 * @author szh
 */
public class ThreadPoolTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(
                        10,
                        10,
                        // 默认情况下非核心线程的空闲时间
                        // 如果allowCoreThreadTimeOut=true: 核心线程或非核心线程允许的空闲时间
                        10L,
                        TimeUnit.SECONDS ,
                        new LinkedBlockingDeque<>(),
                        Executors.defaultThreadFactory(),
                        new ThreadPoolExecutor.AbortPolicy()
                );
        executor.allowCoreThreadTimeOut(true);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程池测试1");
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程池测试2");
            }
        });

        Future<String> submit = executor.submit(new Callable<String>() {

            @Override
            public String call() throws Exception {
                return "测试submit";
            }
        });
        String s = submit.get();
        System.out.println(s);

    }





}











