package com.atguigu.srb.sms.listener;

import com.atguigu.srb.mq.config.MQConst;
import com.atguigu.srb.mq.pojo.dto.SmsDTO;
import com.atguigu.srb.sms.service.SmsService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
// mq 监听
public class SmsReceiver {

    @Resource
    private SmsService smsService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = MQConst.QUEUE_SMS_ITEM)
            , exchange = @Exchange(value = MQConst.EXCHANGE_TOPIC_SMS)
            , key = {MQConst.ROUTING_SMS_ITEM}
    ))
    public void smsSend(Channel channel, Message message, SmsDTO smsDTO){
        byte[] body = message.getBody();
        String mes = new String(body);
        System.out.println(mes);
        System.out.println("sms消费，发送充值短信");

        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);//commit
        } catch (IOException e) {
            e.printStackTrace();
        }
        //channel.basicNack();//rollback
    }
}
