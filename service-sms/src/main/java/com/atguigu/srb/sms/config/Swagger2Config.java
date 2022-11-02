package com.atguigu.srb.sms.config;


import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {
    @Bean
    public Docket adminDocket(){
        Docket doc = new Docket(DocumentationType.SWAGGER_2)
                .groupName("SMS")
                .select()
                //Predicate 断言用于进行条件判断，只有断言都返回真，才会真正的执行路由
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();
        return doc;
    }

    private ApiInfo getApiInfo() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("尚融宝SMS验证码API")
                .description("尚融宝SMS接口")
                .version("1.0")
                .contact(new Contact("pp", "http://atguigu.com", "112233"))
                .build();
        return apiInfo;
    }
}
