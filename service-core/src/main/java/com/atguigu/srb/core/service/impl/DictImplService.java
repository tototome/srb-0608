package com.atguigu.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.srb.core.listeners.ExcelDictListener;
import com.atguigu.srb.core.pojo.dto.ExcelDictDTO;
import com.atguigu.srb.core.pojo.entity.Dict;
import com.atguigu.srb.core.mapper.DictMapper;
import com.atguigu.srb.core.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author pp
 * @since 2022-10-28
 */
@Service
public class DictImplService extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Override
    public void importDictExcel(MultipartFile multipartFile) {
        try {
            EasyExcel.read(multipartFile.getInputStream(), ExcelDictDTO.class,new ExcelDictListener()).sheet("数据字典").doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
