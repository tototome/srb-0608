package com.atguigu.srb.mybatisPlus;

import com.atguigu.srb.mybatisPlus.mapper.UserMapper;
import com.atguigu.srb.mybatisPlus.pojo.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.function.Consumer;

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
                .ge("age", 10)
                .le("age", 20)
                .isNotNull("email");
        List<User> userList = userMapper.selectList(userQueryWrapper);
        System.out.println(userList);
    }

    @Test
    public void test02() {
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.set("is_deleted", 0).eq("id", 2);
        userMapper.update(userMapper.selectById(2), userUpdateWrapper);
    }

    @Test
    public void test03() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //queryWrapper.like("name","n")
        //        .and(new Consumer<QueryWrapper<User>>() {
        //            @Override
        //            public void accept(QueryWrapper<User> userQueryWrapper) {
        //                userQueryWrapper.le("age",18).or().isNull("email")
        //            }
        //        });
        queryWrapper.like("name", "n").and((userQueryWrapper) ->
                //这里传入的是一个方法 lambda简写表达式的时候才可以传方法
                userQueryWrapper.le("age", 18).or().isNull("email")
        );
        List<User> userList = userMapper.selectList(queryWrapper);
        User user = new User();
        user.setAge(18);
        user.setEmail("user@atguigu.com");
        userMapper.update(user, queryWrapper);
    }

    @Test
    public void test04() {
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.set("age", 19)
                .set("email", "aaa@qq.com")
                .like("name", "n")
                .and((i) ->
                        //这里传入的是一个方法 lambda简写表达式的时候才可以传方法
                        i.le("age", 18).or().isNull("email"));
        userMapper.update(null, userUpdateWrapper);
    }

    @Test
    public void test05() {
       // final 可以修改前提是不修改地址值的情况下
       final int[] a={1,2,3,4};
       a[3]=5;
       System.out.println(a[3]);
    }
}
