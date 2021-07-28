package com.imooc.springcloud;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Created by Zhihong Song on 2021/1/13 14:01
 */
@SpringBootApplication
public class RateLimiterApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(RateLimiterApplication.class)
                .web(WebApplicationType.SERVLET).run(args);
    }

}
