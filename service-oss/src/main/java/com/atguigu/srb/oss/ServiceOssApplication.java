package com.atguigu.srb.oss;


import com.atguigu.srb.oss.util.OssProperties;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan({"com.atguigu.srb.common","com.atguigu.srb.oss","com.atguigu.srb.base"})
public class ServiceOssApplication {
    public static void main(String[] args) {
       SpringApplication.run(ServiceOssApplication.class, args);
    }

}
