package com.atguigu.srb.core.service;

import com.atguigu.srb.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

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
}
