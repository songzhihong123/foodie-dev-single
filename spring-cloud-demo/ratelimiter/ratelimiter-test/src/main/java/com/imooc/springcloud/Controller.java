package com.imooc.springcloud;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Zhihong Song on 2021/1/15 15:07
 */
@RestController
@Slf4j
public class Controller {

    @Autowired
    private AccessLimiter accessLimiter;

    @GetMapping("/test")
    public String test(){
        accessLimiter.limitAccess("ratelimiter-test",1);
        return "sccuess";
    }

    // 提醒！ 注意配置扫包路径
    @GetMapping("/test-annotation")
    @com.imooc.springcloud.annotation.AccessLimiter(limit = 1)
    public String testAnnotation(){
        return "access";
    }


}
