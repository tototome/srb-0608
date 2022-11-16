package com.atguigu.srb.mq.config;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MqAckConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @PostConstruct
    //将方法的实现放进去
    public void init(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String errMessage) {
        System.out.println("确认回调，确认消息是否发送成功，无论成或败都会调用");
    }

    @Override
    public void returnedMessage(Message message, int code, String errMessage, String exchange, String routing) {
        System.out.println("投递回调，确认消息是否投递成功，只有失败才会调用");
    }
}
