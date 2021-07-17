package com.imooc.jvm.objectpool.commonspool.datasource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


// FIXME 使用时开启
//@Configuration
public class DataSourceConfig {


    /**
     *  自定义的DataSource必须托管于Spring
     */

    @Bean
    @Primary
    public DataSource dataSource(){
        return new DMDataSource();
    }

    /**
     *  对外暴露的 endpoint 必选托管于Spring
     *  可以交由 /actuator/endpoint 来监控
     */
    @Bean
    public DataSourceEnpoint dataSourceEnpoint(){

        // 将类上面的 @Configuration 放开，然后放开这行代码即可生效
//        DataSource dataSource = this.dataSource();
        //这里得到的datasource对象是代理对象，需要要反向代理去解包，返回真正的 DMDataSource 这个类才能传入到下面
        // 的 DataSourceEnpoint 构造方法里面，否则会报错
//        return new DataSourceEnpoint((DMDataSource)dataSource);

        return null;
    }

}
