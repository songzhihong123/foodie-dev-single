package com.imooc.springcloud.annotation;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Zhihong Song on 2021/1/15 15:34
 */
@Aspect
@Component
@Slf4j
public class AccessLimiterAspect {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisScript<Boolean> rateLimitLua;


    @Pointcut("@annotation(com.imooc.springcloud.annotation.AccessLimiter)")
    public void cut(){
        log.info("cut");
    }

    @Before("cut()")
    public void before(JoinPoint joinPoint){

        // 1.获得方法签名，作为method Key
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();

        AccessLimiter annotation = method.getAnnotation(AccessLimiter.class);
        if (annotation == null){
            return;
        }
        String key = annotation.methodKey();
        Integer limit = annotation.limit();

        //如果没有设置methodkey，从调用方法签名自动生成一个key
        if(StringUtils.isEmpty(key)){
            Class[] types = method.getParameterTypes();
            key = method.getName();
            if(types != null){
                String paramTypes = Arrays.stream(types).map(Class::getName).collect(Collectors.joining(","));
                log.info("params types: " + paramTypes);
                key += "#" + paramTypes;
            }
        }

        //2.调用redis
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
