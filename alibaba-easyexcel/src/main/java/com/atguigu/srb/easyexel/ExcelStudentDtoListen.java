package com.atguigu.srb.easyexel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.atguigu.srb.easyexel.pojo.dto.ExcelStudentDto;

import java.util.ArrayList;
import java.util.List;

public class ExcelStudentDtoListen extends AnalysisEventListener<ExcelStudentDto> {

    //@Override
    //public void invoke(ExcelStudentDto data, AnalysisContext analysisContext) {
    //    System.out.println("每读取一行该数据执行一次");
    //    System.out.println(data);
    //}

    Integer count = 0;
    List<ExcelStudentDto> ExcelStudentDtoCar = new ArrayList<>();

    @Override
    //批次处理
    public void invoke(ExcelStudentDto data, AnalysisContext analysisContext) {
        ExcelStudentDtoCar.add(data);
        if (ExcelStudentDtoCar.size() >= 10) {
            System.out.println("十行读一次"+ ++count);
            System.out.println(ExcelStudentDtoCar);
            System.out.println("打开数据库进行写入");
            ExcelStudentDtoCar.clear();
        }
    }

    @Override
    //当读取完之后后面没有不满足10个 全部打印
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println("垫底方法");
        System.out.println(ExcelStudentDtoCar);
    }
}
