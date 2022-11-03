package com.atguigu.srb.sms;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class Test01 {

    //测试短信发送
    @Test
    public void test01() throws ClientException {
        DefaultProfile defaultProfile = DefaultProfile.getProfile("cn-shenzhen", "id", "secret key");
        IAcsClient iAcsClient =new DefaultAcsClient(defaultProfile);
        CommonRequest commonRequest = new CommonRequest();
        commonRequest.setSysAction("SendSms");
        commonRequest.setSysMethod(MethodType.POST);
        commonRequest.setSysVersion("2017-05-25");
        commonRequest.setSysDomain("dysmsapi.aliyuncs.com");// 阿里网关
        commonRequest.putQueryParameter("PhoneNumbers","18937559860");
        commonRequest.putQueryParameter("SignName","北京课时教育");
        commonRequest.putQueryParameter("TemplateCode","SMS_217425770");
        Map<String,Object> map = new HashMap<>();
        map.put("code","hello");
        String TemplateParam = JSON.toJSONString(map);
        commonRequest.putQueryParameter("TemplateParam",TemplateParam);


        // 发送短信
        CommonResponse commonResponse = iAcsClient.getCommonResponse(commonRequest);

        // 解析返回结果
        boolean success = commonResponse.getHttpResponse().isSuccess();



    }
}
