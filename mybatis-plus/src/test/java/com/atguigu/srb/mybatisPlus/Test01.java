package com.atguigu.srb.mybatisPlus;

import com.atguigu.srb.mybatisPlus.mapper.UserMapper;
import com.atguigu.srb.mybatisPlus.pojo.entity.User;
import com.atguigu.srb.mybatisPlus.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Test01 {
    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;
    @Test
    public void test01(){

        User byId = userService.getById(1);
        System.out.println(byId);
    }
    @Test
    public  void test02(){
        //条件构造器 Params:
        //queryWrapper – 实体对象封装操作类（可以为 null）
        List<User> userList = userMapper.selectList(null);
        System.out.println(userList);
    }
}
