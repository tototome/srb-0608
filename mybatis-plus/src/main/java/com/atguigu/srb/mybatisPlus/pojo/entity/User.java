package com.atguigu.srb.mybatisPlus.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(keepGlobalPrefix = true)
public class User {
    @TableId(type= IdType.AUTO)
    private Long id;
    private String name;
    private Integer age;
    private String email;
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    @TableField(value = "is_deleted",fill = FieldFill.INSERT)
    private Integer isDeleted;
    private Integer version;

}


