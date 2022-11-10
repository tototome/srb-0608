package com.atguigu.srb.core.controller.api;

import com.atguigu.srb.common.util.Assert;
import com.atguigu.srb.common.util.R;
import com.atguigu.srb.common.util.RegexValidateUtils;
import com.atguigu.srb.common.util.ResponseEnum;
import com.atguigu.srb.core.pojo.entity.UserInfo;
import com.atguigu.srb.core.pojo.vo.LoginVO;
import com.atguigu.srb.core.pojo.vo.RegisterVO;
import com.atguigu.srb.core.pojo.vo.UserInfoVO;
import com.atguigu.srb.core.service.UserInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/core/userInfo/")
//@CrossOrigin
@Slf4j
@Api(tags = "用户接口")
public class ApiUserInfoController {
    @Autowired
    UserInfoService userInfoService;
    @PostMapping("register")
    public R register(@RequestBody RegisterVO registerVO) {
        String mobile = registerVO.getMobile();
        String code = registerVO.getCode();
        String password = registerVO.getPassword();
        Integer userType = registerVO.getUserType();
        Assert.isTrue(!StringUtils.isEmpty(mobile) &&
                !StringUtils.isEmpty(code) &&
                !StringUtils.isEmpty(password) &&
                !StringUtils.isEmpty(userType), ResponseEnum.COMMON_ERROR);
        boolean b = RegexValidateUtils.checkCellphone(mobile);
        Assert.isTrue(b,ResponseEnum.MOBILE_ERROR);
        userInfoService.register(registerVO);
        return R.ok();
    }

    @PostMapping("login")
    public R login(@RequestBody LoginVO loginVO, HttpServletRequest httpServletRequest){
        //获取IP 登陆日志记录
        String ip = httpServletRequest.getHeader("X-forwarded-for");
        String mobile = loginVO.getMobile();
        Integer userType = loginVO.getUserType();
        String password = loginVO.getPassword();

        Assert.isTrue(!StringUtils.isEmpty(mobile)&&
                !StringUtils.isEmpty(password)
                &&null!=userType,
                ResponseEnum.COMMON_ERROR
                );
      UserInfoVO userInfoVO =userInfoService.login(loginVO,ip);

      return R.ok().data("userInfoVO",userInfoVO);
    }

    @GetMapping("isMobileExist/{mobile}")
    public boolean isMobileExist(@PathVariable("mobile") String mobile){
       boolean  exist=userInfoService.isMobileExist(mobile);
       return  exist;
    }
}
