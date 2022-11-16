package com.atguigu.srb.mq.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
    //加入一个 转换器
    //消息传递后转对象
    /*
     * 各服务之间大多数数据都是以JSON类型的数据进行传输的，
     * 即生产者服务将JSON类型的数据传递到对应的队列，
     * 而消费端处理器中接收到的数据类型也是JSON类型。
     *
     * 使用Jackson2JsonMessageConverter处理器，客户端发送JSON类型数据，但是没有指定消息的contentType类型，
     * 那么Jackson2JsonMessageConverter就会将消息转换成byte[]类型的消息进行消费。
     * 如果指定了contentType为application/json，
     * 那么消费端就会将消息转换成Map类型的消息进行消费。
     * 如果指定了contentType为application/json，并且生产端是List类型的JSON格式
     * 那么消费端就会将消息转换成List类型的消息进行消费。
     * 指定了消息类型就会进行转换
     * */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
