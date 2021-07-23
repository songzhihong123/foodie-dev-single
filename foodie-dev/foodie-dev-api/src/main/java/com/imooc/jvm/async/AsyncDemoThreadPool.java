package com.imooc.jvm.async;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncDemoThreadPool {

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(5 , 10 , 10 , TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100), Executors.defaultThreadFactory() , new ThreadPoolExecutor.AbortPolicy());

    private void subBiz1() throws InterruptedException {
        Thread.sleep(1000L);
        System.out.println(new Date() + "sunBiz1");
    }

    private void saveOpLog() throws InterruptedException {

        executor.submit(new SaveOpLogThread());
    }

    private void subBiz2() throws InterruptedException {
        Thread.sleep(1000L);
        System.out.println(new Date() + "subBiz2");

        System.out.println(new Date() + "执行结束");
    }

    private void biz() throws InterruptedException {
        this.subBiz1();
        this.saveOpLog();
        this.subBiz2();
    }

    public static void main(String[] args) throws InterruptedException {
        new AsyncDemoThreadPool().biz();
    }



}

class SaveOpLogThread implements Runnable{

    @Override
    public void run() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(new Date() + "插入操作日志");
    }
}