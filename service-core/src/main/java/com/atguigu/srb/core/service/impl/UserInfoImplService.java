package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.common.util.Assert;
import com.atguigu.srb.common.util.JwtUtils;
import com.atguigu.srb.common.util.MD5;
import com.atguigu.srb.common.util.ResponseEnum;

import com.atguigu.srb.core.pojo.entity.UserAccount;
import com.atguigu.srb.core.pojo.entity.UserInfo;
import com.atguigu.srb.core.mapper.UserInfoMapper;
import com.atguigu.srb.core.pojo.entity.UserLoginRecord;
import com.atguigu.srb.core.pojo.vo.LoginVO;
import com.atguigu.srb.core.pojo.vo.RegisterVO;
import com.atguigu.srb.core.pojo.vo.UserInfoVO;
import com.atguigu.srb.core.service.UserAccountService;
import com.atguigu.srb.core.service.UserInfoService;
import com.atguigu.srb.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户基本信息 服务实现类
 * </p>
 *
 * @author pp
 * @since 2022-10-28
 */
@Service
public class UserInfoImplService extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {


    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserAccountService userAccountService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    UserLoginRecordService userLoginRecordService;

    @Override
    public void register(RegisterVO registerVO) {
        // 验证验证码
        String cacheCode = (String) redisTemplate.opsForValue().get("srb:sms:code" + registerVO.getMobile());
        Assert.isTrue(cacheCode.equals(registerVO.getCode()), ResponseEnum.CODE_ERROR);

        // 保存用户信息到user_info表
        UserInfo userInfo = new UserInfo();
        userInfo.setUserType(registerVO.getUserType());
        userInfo.setMobile(registerVO.getMobile());
        userInfo.setPassword(MD5.encrypt(registerVO.getPassword()));
        userInfo.setName(registerVO.getMobile());
        userInfo.setNickName(registerVO.getMobile());
        baseMapper.insert(userInfo);

        // 生成一条空的user_account信息
        Long userInfoId = userInfo.getId();
        UserAccount userAccount = new UserAccount();
        userAccount.setUserId(userInfoId);
        userAccountService.save(userAccount);

    }

    @Override
    public UserInfoVO login(LoginVO loginVO, String ip) {
        //判断用户是否存在 存在登陆成功
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        Integer userType = loginVO.getUserType();


        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("user_type", userType);
        userInfoQueryWrapper.eq("mobile", mobile);
        UserInfo userInfo = userInfoService.getOne(userInfoQueryWrapper);

        Assert.isTrue(userInfo.getPassword().equals(MD5.encrypt(password)), ResponseEnum.LOGIN_PASSWORD_ERROR);


        //校验通过 设置token
        UserInfoVO userInfoVO = new UserInfoVO();
        String token = JwtUtils.createToken(userInfo.getId(), userInfo.getName());
        userInfoVO.setToken(token);
        userInfoVO.setUserType(userType);
        userInfoVO.setNickName(userInfo.getNickName());
        userInfoVO.setNickName(userInfo.getName());
        userInfoVO.setMobile(userInfo.getMobile());
        userInfoVO.setHeadImg(userInfo.getHeadImg());
        //设置bindStatus
        Integer bindStatus = userInfo.getBindStatus();
        userInfoVO.setBingStatus(bindStatus);
        //记录登陆日志 这里其实要用异步的方式记录 不会影响我们登陆  消息队列的应用场景
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setIp(ip);
        userLoginRecord.setUserId(userInfo.getId());
        userLoginRecordService.save(userLoginRecord);

        return userInfoVO;
    }

    @Override
    public boolean isMobileExist(String mobile) {
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("mobile",mobile);
        UserInfo one = userInfoService.getOne(userInfoQueryWrapper);

        if (one!=null){
            return true;
        }

        return false;
    }
}
