package com.atguigu.srb.mybatisPlus;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@MapperScan("com.atguigu.srb.mybatisPlus.mapper")
@SpringBootApplication
//如果没有在配置文件中写数据库信息则会报错
//因为springboot会自动初始化数据源 可以在注解后面加入exclude
public class MyBatisPlusApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyBatisPlusApplication.class,args);
    }
}
