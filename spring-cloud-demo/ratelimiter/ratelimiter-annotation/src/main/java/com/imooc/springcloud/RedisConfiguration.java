package com.imooc.springcloud;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

/**
 * Created by Zhihong Song on 2021/1/15 14:40
 */
@Configuration
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String, String>  redisTemplate(RedisConnectionFactory factory){
        return new StringRedisTemplate(factory);
    }


    @Bean
    public DefaultRedisScript loadRedisScript(){
        DefaultRedisScript redisScript = new DefaultRedisScript();
        redisScript.setLocation(new ClassPathResource("ratelimiter.lua"));
        redisScript.setResultType(Boolean.class);
        return redisScript;
    }


}
