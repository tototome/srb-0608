package com.atguigu.srb.sms.controller;

import com.atguigu.srb.common.util.Assert;
import com.atguigu.srb.common.util.R;
import com.atguigu.srb.common.util.RegexValidateUtils;
import com.atguigu.srb.common.util.ResponseEnum;
import com.atguigu.srb.sms.client.CoreUserInfoClient;
import com.atguigu.srb.sms.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags="短信管理")
@RequestMapping("/api/sms")
@RestController
//@CrossOrigin
public class ApiSmsController {
    @Autowired
    SmsService smsService;

    @Autowired
    CoreUserInfoClient coreUserInfoClient;

    @GetMapping("sendRegisterCode/{mobile}")
    @ApiOperation("发送短信验证码")
    public R sendRegisterCode(@ApiParam("手机号") @PathVariable("mobile") String mobile) {
        boolean mobileExist = coreUserInfoClient.isMobileExist(mobile);
        System.out.println(mobileExist);
        Assert.isTrue(!coreUserInfoClient.isMobileExist(mobile),ResponseEnum.MOBILE_EXIST_ERROR);
        Assert.notNull(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile),ResponseEnum.MOBILE_ERROR);
        smsService.sendRegisterCode(mobile);
        return R.ok();
    }

    @GetMapping("/test/{mobile}")
    public R test(@PathVariable("mobile") String mobile, HttpServletRequest httpServletRequest){
        String userId = httpServletRequest.getHeader("userId");
        System.out.println(userId);
        boolean mobileExist = coreUserInfoClient.isMobileExist(mobile);
        System.out.println(mobileExist);
        return R.ok();
    }


}
