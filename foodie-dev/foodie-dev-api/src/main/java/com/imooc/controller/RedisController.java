package com.imooc.controller;

import com.imooc.utils.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@Controller
@ApiIgnore
@RestController
@RequestMapping("redis")
public class RedisController {

    final static Logger logger = LoggerFactory.getLogger(RedisController.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/set")
    public Object set(String key,String value){
        redisOperator.set(key,value);
        //redisTemplate.opsForValue().set(key,value);
        return "OK!";
    }

    @GetMapping("/get")
    public String get(String key){
        return redisOperator.get(key);
        //return (String)redisTemplate.opsForValue().get(key);
    }

    @GetMapping("/delete")
    public Object delete(String key){
        //redisTemplate.delete(key);
        redisOperator.del(key);
        return "OK!";
    }

    /**
     * 大量key的查询
     * @param keys
     * @return
     */
    @GetMapping("/getALot")
    public Object getALot(String... keys){
        List<String> result = new ArrayList<>();
        for (String key : keys) {
            result.add(redisOperator.get(key));
        }
        return result;
    }

    /**
     * 批量查询
     * @param keys
     * @return
     */
    @GetMapping("/mget")
    public Object mget(String... keys){
        List<String> keysList = Arrays.asList(keys);
        return redisOperator.mget(keysList);
    }

    /**
     * pipeline
     * @param keys
     * @return
     */
    @GetMapping("/batchGet")
    public Object batchGet(String... keys){
        List<String> keysList = Arrays.asList(keys);
        return redisOperator.batchGet(keysList);
    }


}
