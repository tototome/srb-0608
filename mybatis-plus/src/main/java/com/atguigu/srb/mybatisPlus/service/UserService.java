package com.atguigu.srb.mybatisPlus.service;

import com.atguigu.srb.mybatisPlus.pojo.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserService extends IService<User> {
    List<User> getUserByName(String name);
    //注意封装类型 虽然是返回值是IPge 但是后面的框架会帮我做封装 我们再mapper中的返回值类型应该还是User类型
    IPage<User> getUserByNamePage(@Param("name") String name, IPage<User> iPage);

}
