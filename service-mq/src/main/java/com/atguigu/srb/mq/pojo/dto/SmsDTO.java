package com.atguigu.srb.mq.pojo.dto;

import lombok.Data;

@Data
public class SmsDTO {

    private String mobile;

    private String message;
}