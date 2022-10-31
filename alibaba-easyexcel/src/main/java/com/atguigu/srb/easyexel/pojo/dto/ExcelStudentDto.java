package com.atguigu.srb.easyexel.pojo.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExcelStudentDto {
    @ExcelProperty("ID")
    private Integer id;
    @ExcelProperty("姓名")
    private String name;
    @ExcelProperty("年龄")
    private Integer age;
    @ExcelProperty("邮箱")
    private String email;

}
