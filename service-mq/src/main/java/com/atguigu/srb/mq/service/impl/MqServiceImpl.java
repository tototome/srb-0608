package com.atguigu.srb.mq.service.impl;

import com.atguigu.srb.mq.pojo.dto.SmsDTO;
import com.atguigu.srb.mq.service.MqService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqServiceImpl implements MqService {
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Override
    public void sendMessage(String exchange, String routing, Object message) {
        rabbitTemplate.convertAndSend(exchange,routing,message);
    }
}
