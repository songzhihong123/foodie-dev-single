package com.imooc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = "com.imooc.mapper")
//扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.imooc","org.n3r.idworker"})
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }


}
