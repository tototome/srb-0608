package com.atguigu.srb.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@MapperScan("com.atguigu.srb.core.mapper")
@ComponentScan({"com.atguigu.srb.common","com.atguigu.srb.core","com.atguigu.srb.base","com.atguigu.srb.mq"})
@EnableDiscoveryClient
@EnableTransactionManagement
//没有设置默认打印的是info级别的日志信息
public class ServiceCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCoreApplication.class,args);
    }
}
