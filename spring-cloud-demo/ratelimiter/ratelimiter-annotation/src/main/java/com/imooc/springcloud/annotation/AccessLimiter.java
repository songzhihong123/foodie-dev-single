package com.imooc.springcloud.annotation;

import java.lang.annotation.*;

/**
 * Created by Zhihong Song on 2021/1/15 15:21
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLimiter {

    int limit();

    String methodKey() default "";
}
