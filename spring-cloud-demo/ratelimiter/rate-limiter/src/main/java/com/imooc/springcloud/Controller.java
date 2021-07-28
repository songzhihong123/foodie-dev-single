package com.imooc.springcloud;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * Created by Zhihong Song on 2021/1/13 16:23
 */
@RestController
@Slf4j
public class Controller {

    //每秒钟放出的令牌
    RateLimiter limiter = RateLimiter.create(2.0);

    // 非阻塞限流
    @GetMapping("/tryAcquire")
    public String tryAcquire(Integer count){
        if (limiter.tryAcquire(count)) {
            log.info("success, rate is {}",limiter.getRate());
            return "scuccess";
        }else {
            log.info("fail ,rate is {}",limiter.getRate());
            return "fail";
        }
    }

    //限定时间的费阻塞限流
    @GetMapping("/tryAcquireWithTimeout")
    public String tryAcquireWithTimeout(Integer count,Integer timeout){
        if (limiter.tryAcquire(count,timeout, TimeUnit.SECONDS)) {
            log.info("success, rate is {}",limiter.getRate());
            return "scuccess";
        }else {
            log.info("fail ,rate is {}",limiter.getRate());
            return "fail";
        }
    }

    // 同步阻塞限流
    @GetMapping("/acquire")
    public String acquire(Integer count){
        limiter.acquire(count);
        log.info("success, rate is {}",limiter.getRate());
        return "success";
    }

    // Nginx 专用
    //1.修改hosts文件  -》 www.imooc-trining.com = localhost 127.0.0.1
    //（127.0.0.1  www.imooc-trining.com）
    //2.修改nginx -》 将步骤1中的域名，添加到路由规则当中
    // 配置文件地址 /usr/local/nginx/conf/nginx.conf
    // 3.添加配置项：参考resource文件夹下面的nginx.conf
    // 4.重新加载nginx => nginx -s reload
    @GetMapping("/nginx")
    public  String nginx(){
        log.info("Nginx seccess");
        return "success";
    }

    @GetMapping("/nginx-conn")
    public String nginxConn(@RequestParam(defaultValue = "0") int seconds){
        try {
            Thread.sleep(1000*seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "sccess";
    }



}
