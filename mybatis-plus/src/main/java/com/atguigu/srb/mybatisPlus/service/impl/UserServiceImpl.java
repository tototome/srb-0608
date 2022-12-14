package com.atguigu.srb.mybatisPlus.service.impl;

import com.atguigu.srb.mybatisPlus.mapper.UserMapper;
import com.atguigu.srb.mybatisPlus.pojo.entity.User;
import com.atguigu.srb.mybatisPlus.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    public  List<User> getUserByName(String name){
        List<User> userList = baseMapper.selectUserByName(name);
        return userList;
    }

    public IPage<User> getUserByNamePage(String name, IPage<User> iPage) {
       IPage<User> iPage1 = baseMapper.selectUserByNamePage(name, iPage);
        return iPage1;
    }


}
