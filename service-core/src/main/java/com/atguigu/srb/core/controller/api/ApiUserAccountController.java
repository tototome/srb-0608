package com.atguigu.srb.core.controller.api;

import com.atguigu.srb.common.exception.BusinessException;
import com.atguigu.srb.common.util.R;
import com.atguigu.srb.common.util.ResponseEnum;
import com.atguigu.srb.core.hfb.RequestHelper;
import com.atguigu.srb.core.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/core/userAccount/")
public class ApiUserAccountController {

    @Autowired
    UserAccountService userAccountService;

    @PostMapping("auth/commitCharge")
    public R  commitCharge(@RequestBody String chargeAmt, HttpServletRequest httpServletRequest){
        chargeAmt = "10000";
        String userId = httpServletRequest.getHeader("userId");
        String form = userAccountService.commitCharge(chargeAmt, userId);
        return R.ok().data("form",form);
    }

    @PostMapping("notify")
    //没有说返回值怎么传递 就是在请求头中
    public String notifyCharge(HttpServletRequest httpServletRequest){
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Map<String, Object> returnParamMap = RequestHelper.switchMap(parameterMap);
        //判断签名
        if(RequestHelper.isSignEquals( returnParamMap)){
            //判断充值是否成功
            if (returnParamMap.get("resultCode").equals("0001")){
                //修改账户金额 交易流水
               return userAccountService.notifyCharge(returnParamMap);
            }else {
                System.out.println("充值失败");
                return "success";
            }
        }
        return "fail";

    }
}
