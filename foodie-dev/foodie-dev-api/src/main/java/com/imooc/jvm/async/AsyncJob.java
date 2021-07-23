package com.imooc.jvm.async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AsyncJob {

    @Async
    public void saveOpLog() throws InterruptedException {
        Thread.sleep(1000L);
        System.out.println(new Date() + "插入操作日志");
    }

}
