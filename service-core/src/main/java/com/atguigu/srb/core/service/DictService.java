package com.atguigu.srb.core.service;

import com.atguigu.srb.core.pojo.dto.ExcelDictDTO;
import com.atguigu.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author pp
 * @since 2022-10-28
 */
public interface DictService extends IService<Dict> {

    void importDictExcel(MultipartFile multipartFile);

    List<ExcelDictDTO> ListData();

    List<Dict>  getDictListByParentId(Integer parentId);


    List<Dict> getDictListByDictCode(String dictCode);

    String getNameAndDictCodeAndId(String dictCode, Integer value);
}
