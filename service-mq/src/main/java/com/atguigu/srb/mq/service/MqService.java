package com.atguigu.srb.mq.service;

import com.atguigu.srb.mq.pojo.dto.SmsDTO;

public interface MqService {
    void sendMessage(String exchange, String routing, Object message);
}
