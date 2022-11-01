package com.atguigu.srb.core;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class Test01 {

    @Autowired
    RedisTemplate redisTemplate;
    @Test
    public void test01() {
        HashOperations hO = redisTemplate.opsForHash();
        hO.put("map","k1","k2");
        Object o = hO.get("map", "k1");
        //如果不进行序列化就会出现 一些问题 key 和 value 出现此种形式
        // \xAC\xED\x00\x05t\x00\x03map
        //通过序列化进行解决
        System.out.println(o);

    }
}
