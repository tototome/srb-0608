package com.atguigu.srb.easyexel;

import com.alibaba.excel.EasyExcel;
import com.atguigu.srb.easyexel.pojo.dto.ExcelStudentDto;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class test1 {


    @Test
    //写入测试
    public  void test01(){

     List<ExcelStudentDto> excelStudentDtoList=new ArrayList<>();

        for (int i = 0; i < 108; i++) {
            ExcelStudentDto excelStudentDto = new ExcelStudentDto();
            excelStudentDto.setAge(i+20);
            excelStudentDto.setName("tom"+i);
            excelStudentDto.setId(i);
            excelStudentDto.setEmail("2480@"+i+".com");
            excelStudentDtoList.add(excelStudentDto);
        }

    EasyExcel.write("D:/SoftWare/a.xlsx",ExcelStudentDto.class).sheet("学生信息列表").doWrite(excelStudentDtoList);
    }
    @Test
    //读入测试
    public void  test02(){
        EasyExcel.read("D:/SoftWare/a.xlsx",ExcelStudentDto.class,new ExcelStudentDtoListen()).sheet("学生信息列表").doRead();
    }
}


