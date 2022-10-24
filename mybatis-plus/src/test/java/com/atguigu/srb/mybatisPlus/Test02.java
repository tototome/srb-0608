package com.atguigu.srb.mybatisPlus;

import com.atguigu.srb.mybatisPlus.pojo.entity.User;
import com.atguigu.srb.mybatisPlus.service.impl.UserServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Test02 {
    @Autowired
    UserServiceImpl userService;

    @Test
    public void  pageInfo(){
        //使用分页插件
        IPage<User> iPage=new Page<>(2,3);
        IPage<User> page = userService.page(iPage, null);
        List<User> records = page.getRecords();
        System.out.println(records);


    }
    @Test
    public void pageInfo01(){
        IPage<User> iPage=new Page<>(1,1);
        IPage<User> j = userService.getUserByNamePage("j", iPage);
        System.out.println(j.getRecords());
    }
}
