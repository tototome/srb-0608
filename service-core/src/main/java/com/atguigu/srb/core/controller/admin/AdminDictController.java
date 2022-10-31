package com.atguigu.srb.core.controller.admin;


import com.atguigu.srb.common.util.R;
import com.atguigu.srb.core.service.DictService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/admin/core/dict")
@Api(tags = "数据字典接口")
public class AdminDictController {
    @Autowired
    DictService dictService;
    @PostMapping("/import")
    public R importDictExcel(@RequestParam("file") MultipartFile multipartFile) {
        dictService.importDictExcel(multipartFile);
        return R.ok();
    }
}
