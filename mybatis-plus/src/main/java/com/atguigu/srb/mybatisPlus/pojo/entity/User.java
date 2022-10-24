package com.atguigu.srb.mybatisPlus.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(keepGlobalPrefix = true)
public class User {
    private Long id;
    private String name;
    private Integer age;
    private String email;

}


