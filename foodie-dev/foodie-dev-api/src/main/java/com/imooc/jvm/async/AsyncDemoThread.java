package com.imooc.jvm.async;

import java.util.Date;

public class AsyncDemoThread {

    private void subBiz1() throws InterruptedException {
        Thread.sleep(1000L);
        System.out.println(new Date() + "sunBiz1");
    }

    private void saveOpLog() throws InterruptedException {
       new Thread(new SaveOpLogThread()).start();
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
        new AsyncDemoThread().biz();
    }



}