package com.imooc;

import com.imooc.jvm.objectpool.commonspool.datasource.DMDataSource;
import com.imooc.jvm.objectpool.commonspool.datasource.DataSourceEnpoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
//扫描 mybatis 通用mapper所在的包
@MapperScan(basePackages = "com.imooc.mapper")
//@EnableTransactionManagement
//扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.imooc","org.n3r.idworker"})
@EnableScheduling  //开启定时任务
@EnableRedisHttpSession  //开启使用Redis作为Spring session
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }



}
