package com.atguigu.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.srb.core.listeners.ExcelDictListener;
import com.atguigu.srb.core.pojo.dto.ExcelDictDTO;
import com.atguigu.srb.core.pojo.entity.Dict;
import com.atguigu.srb.core.mapper.DictMapper;
import com.atguigu.srb.core.service.DictService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    RedisTemplate redisTemplate;
    @Override
    public List<Dict> getDictListByParentId(Integer parentId) {


        List<Dict> dictList=null;

        dictList = (List<Dict>)redisTemplate.opsForValue().get("srb:core:dictList:" + parentId);

        if(dictList != null){
            return dictList;
        }else {
            //防止缓存穿透
            redisTemplate.opsForValue().set("srb:core:dictList"+ parentId,new ArrayList<>(),10,TimeUnit.SECONDS);
        }

        QueryWrapper<Dict>  byParentId=new QueryWrapper<>();
        byParentId.eq("parent_id",parentId);
         dictList = baseMapper.selectList(byParentId);
        for (Dict dict : dictList) {
            byParentId.clear();
            byParentId.eq("parent_id",dict.getId());
            Integer integer = baseMapper.selectCount(byParentId);
            if (integer>0){
                dict.setHasChildren(true);
            }
        }
        redisTemplate.opsForValue().set("srb:core:dictList:" + parentId, dictList, 5, TimeUnit.MINUTES);

        return dictList;
    }

    @Override
    public List<Dict> getDictListByDictCode(String dictCode) {
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("dict_code",dictCode);
        Dict dict = baseMapper.selectOne(dictQueryWrapper);
        Long parentId = dict.getId();
        dictQueryWrapper.clear();
        dictQueryWrapper.eq("parent_id",parentId);
        List<Dict> dictList = baseMapper.selectList(dictQueryWrapper);
        return dictList;
    }

    @Override
    public String getNameAndDictCodeAndId(String dictCode, Integer value) {
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("dict_code",dictCode);
        Dict dict = baseMapper.selectOne(dictQueryWrapper);
        dictQueryWrapper.clear();
        dictQueryWrapper.eq("parent_id",dict.getId())
                .eq("value",value);
        Dict dict1 = baseMapper.selectOne(dictQueryWrapper);

        return dict1.getName();
    }

    @Override
    public void importDictExcel(MultipartFile multipartFile) {
        try {
            EasyExcel.read(multipartFile.getInputStream(), ExcelDictDTO.class,new ExcelDictListener(baseMapper)).sheet("数据字典").doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ExcelDictDTO> ListData() {

        List<Dict> dictList = baseMapper.selectList(null);

       ArrayList<ExcelDictDTO> excelDictDTOList=new ArrayList<>(dictList.size());

        dictList.forEach(dict -> {
            ExcelDictDTO excelDictDTO = new ExcelDictDTO();
            BeanUtils.copyProperties(dict,excelDictDTO);
            excelDictDTOList.add(excelDictDTO);
        });

        return excelDictDTOList;
    }
}
