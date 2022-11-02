package com.atguigu.srb.sms.service;

import com.atguigu.srb.common.util.R;
import org.springframework.stereotype.Service;


public interface SmsService {

    void sendRegisterCode(String mobile) ;
}
