package com.atguigu.srb.mybatisPlus;

import com.atguigu.srb.mybatisPlus.mapper.UserMapper;
import com.atguigu.srb.mybatisPlus.pojo.entity.User;
import com.atguigu.srb.mybatisPlus.service.UserService;
import com.baomidou.mybatisplus.annotation.TableName;
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
        //我们没有指定表的名字为什么可以 执行成功
        // sql 动态生成 表名是从哪里来？实体的名称就是表明如果我们没有指定的话？
        // SELECT id,name,age,email FROM user
        // sql语句是怎么生成的 是我们在运行的过程中动态生成的 通过反射机制
        // 解决方式使用在实体类上加入TableName注解指定表名 或者 配置全局表名前缀的方式
        List<User> userList = userMapper.selectList(null);
        System.out.println(userList);
    }
    @Test
    public  void  test03(){
        User user = new User();
        user.setAge(10);
        user.setEmail("aaaa");
        user.setName("tom");
        user.setIsDeleted(0);
        userService.save(user);
    }

}
