package com.atguigu.srb.mybatisPlus;

import com.atguigu.srb.mybatisPlus.pojo.entity.User;
import com.atguigu.srb.mybatisPlus.service.UserService;
import com.atguigu.srb.mybatisPlus.service.impl.UserServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Test02 {
    @Autowired
    UserService userService;

    @Test
    public void  pageInfo(){
        IPage<User> iPage=new Page<>(2,3);
        IPage<User> page = userService.page(iPage, null);
        System.out.println(page);
    }
}
