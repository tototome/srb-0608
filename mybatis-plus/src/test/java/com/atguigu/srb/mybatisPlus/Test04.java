package com.atguigu.srb.mybatisPlus;

import com.atguigu.srb.mybatisPlus.mapper.UserMapper;
import com.atguigu.srb.mybatisPlus.pojo.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Test04 {
    /*lt：less than 小于
    le：less than or equal to 小于等于
    eq：equal to 等于
    ne：not equal to 不等于
    ge：greater than or equal to 大于等于
    gt：greater than 大于
    */
    @Autowired
    private UserMapper userMapper;

    @Test
    public void test01() {
        //条件查询
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.like("name", "n")
                        .ge("age",10)
                                .le("age",20)
                                        .isNotNull("email");
        List<User> userList = userMapper.selectList(userQueryWrapper);
        System.out.println(userList);
    }
    @Test
    public  void  test02(){
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.set("is_deleted",0).eq("id",2);
        userMapper.update(userMapper.selectById(2),userUpdateWrapper);
    }


}
