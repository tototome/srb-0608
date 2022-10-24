package com.atguigu.srb.mybatisPlus.mapper;

import com.atguigu.srb.mybatisPlus.pojo.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper

public interface UserMapper extends BaseMapper<User> {
     List<User> selectUserByName(@Param("name") String name);
}
