package com.imooc.jvm.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AsyncDemoAsyncAnnotation {

    @Autowired
    private AsyncJob asyncJob;

    private void subBiz1() throws InterruptedException {
        Thread.sleep(1000L);
        System.out.println(new Date() + "sunBiz1");
    }

    private void subBiz2() throws InterruptedException {
        Thread.sleep(1000L);
        System.out.println(new Date() + "subBiz2");

        System.out.println(new Date() + "执行结束");
    }

    private void biz() throws InterruptedException {
        this.subBiz1();
        this.asyncJob.saveOpLog();
        this.subBiz2();
    }

}
