package com.atguigu.srb.core.controller.admin;


import com.alibaba.excel.EasyExcel;
import com.atguigu.srb.common.util.R;
import com.atguigu.srb.core.pojo.dto.ExcelDictDTO;
import com.atguigu.srb.core.pojo.entity.Dict;
import com.atguigu.srb.core.service.DictService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/admin/core/dict")
@Api(tags = "数据字典接口")
public class AdminDictController {
    @Autowired
    DictService dictService;

    @PostMapping("/import")
    @ApiOperation("数据导入")
    public R importDictExcel(@RequestParam("file") MultipartFile multipartFile) {
        dictService.importDictExcel(multipartFile);
        return R.ok();
    }

    @GetMapping("/export")
    @ApiOperation("数据导出")
    public void exportDictExcel(HttpServletResponse response) {

        try {
            // 这里注意 使用swagger 会导致各种问题，请直接用浏览器或者用postman
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("mydict", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), ExcelDictDTO.class).sheet("数据字典").doWrite(dictService.ListData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @GetMapping("/getDictListByParentId/{parentId}")
    @ApiOperation("根据父节点获取子节点列表")
    public R getDictListByParentId(@PathVariable("parentId") Integer id){

        List<Dict> list = dictService.getDictListByParentId(id);

        return  R.ok().data("list",list);
    }
    @GetMapping("/test")
    public R test(){
        return R.ok().data("test","testTest");
    }
}
