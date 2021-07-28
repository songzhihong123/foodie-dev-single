package com.imooc.springcloud;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

/**
 * Created by Zhihong Song on 2021/1/15 14:28
 */

@Service
@Slf4j
@Deprecated
public class AccessLimiter {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisScript<Boolean> rateLimitLua;

    public void limitAccess(String key,Integer limit){

        //step 1: request Lua script
        boolean acquire = stringRedisTemplate.execute(
                rateLimitLua, // Lua 脚本的真身
                Lists.newArrayList(key), //Lua脚本的key列表
                limit.toString() //Lua脚本的value脚本
                );

        if(!acquire){
            log.error("your access is blocked,key={}",key);
            throw new RuntimeException("Your access is bolcked");
        }

    }


}
