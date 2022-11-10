package com.atguigu.srb.sms.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//远程调用客户端接口
//openFeign=spring MVC 注解 +  Feign
//Feign=restTemplate+ribbo
//restTemplate= httpClient
//需要在被调用端实现接口
@FeignClient(value = "service-core",fallback = CoreUserInfoClientFallback.class)
public interface CoreUserInfoClient {
    //注意feign 默认超时时间为1s 请求两次 debug时候因为时间太短会出现超时 错误
    //所以建议测试的时候将feign的超时时间设置的长一点
    @GetMapping("/api/core/userInfo/isMobileExist/{mobile}")
    boolean isMobileExist(@PathVariable("mobile") String mobile);
}
