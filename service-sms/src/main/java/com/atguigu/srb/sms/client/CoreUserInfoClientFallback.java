package com.atguigu.srb.sms.client;


import org.springframework.stereotype.Service;

@Service
public class CoreUserInfoClientFallback implements CoreUserInfoClient{
    @Override
    public boolean isMobileExist(String mobile) {
        System.out.println("兜底方法");
        return false;
    }
}
