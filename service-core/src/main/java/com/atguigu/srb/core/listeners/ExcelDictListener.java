package com.atguigu.srb.core.listeners;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.atguigu.srb.core.mapper.DictMapper;
import com.atguigu.srb.core.pojo.dto.ExcelDictDTO;
import com.atguigu.srb.core.pojo.entity.Dict;

import java.util.ArrayList;
import java.util.List;

public class ExcelDictListener extends AnalysisEventListener<ExcelDictDTO> {

    //通过构造方法进行注入
    private DictMapper dictMapper;

    //传入mapper对象
    public ExcelDictListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    List<ExcelDictDTO> excelDictDTOS = new ArrayList<>();
    private static Long count = 0L;
    @Override
    public void invoke(ExcelDictDTO data, AnalysisContext context) {
        excelDictDTOS.add(data);
        if(excelDictDTOS.size()>=10){
            count++;
            System.out.println("每读取十行数据，执行"+count+"次");
            System.out.println(excelDictDTOS);
            System.out.println("打开数据库，写入数据");
            dictMapper.insertBatch(excelDictDTOS);
            excelDictDTOS.clear();
        }
    }
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("垫底方法");
        dictMapper.insertBatch(excelDictDTOS);
        System.out.println(excelDictDTOS);
    }
}
