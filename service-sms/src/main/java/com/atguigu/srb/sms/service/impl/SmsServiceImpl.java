package com.atguigu.srb.sms.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.atguigu.srb.common.exception.BusinessException;
import com.atguigu.srb.common.util.Assert;
import com.atguigu.srb.common.util.R;
import com.atguigu.srb.common.util.RandomUtils;
import com.atguigu.srb.common.util.ResponseEnum;
import com.atguigu.srb.sms.service.SmsService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements SmsService {
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public void sendRegisterCode(String mobile) {
        //根据key存不住判断不存在往下执行 存在  抛出异常还剩多长时间才可以发送短信

        //具体API调用详情可见https://help.aliyun.com/document_detail/431631.html
        DefaultProfile profile = DefaultProfile.getProfile("cn-shenzhen", "LTAI5tBnqSRdeqREx6r1MMvH", "vSGPYtuk38NiqIjQn6kLZYzYmFTRtq");
        IAcsClient iAcsClient = new DefaultAcsClient(profile);
        CommonRequest commonRequest = new CommonRequest();
        //阿里域名
        commonRequest.setSysDomain("dysmsapi.aliyuncs.com");
        commonRequest.setSysAction("SendSms");
        commonRequest.setSysMethod(MethodType.POST);
        commonRequest.setSysVersion("2017-05-25");
        commonRequest.putQueryParameter("PhoneNumbers", mobile);
        commonRequest.putQueryParameter("SignName", "北京课时教育");
        commonRequest.putQueryParameter("TemplateCode", "SMS_217425770");
        Map<String, Object> map = new HashMap<>();
        String fourBitRandom = RandomUtils.getFourBitRandom();
        redisTemplate.opsForValue().set("srb:sms:code"+mobile,fourBitRandom,300, TimeUnit.SECONDS);
        map.put("code",fourBitRandom);
        String TemplateParam = JSON.toJSONString(map);
        commonRequest.putQueryParameter("TemplateParam", TemplateParam);
        // 发送短信
        try {
            CommonResponse   commonResponse = iAcsClient.getCommonResponse(commonRequest);
            boolean success = commonResponse.getHttpResponse().isSuccess();
        } catch (ClientException e) {
            e.printStackTrace();
        }finally {
            iAcsClient.shutdown();
        }

    }
}
