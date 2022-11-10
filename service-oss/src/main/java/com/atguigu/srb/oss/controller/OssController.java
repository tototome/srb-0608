package com.atguigu.srb.oss.controller;

import com.atguigu.srb.common.util.R;
import com.atguigu.srb.oss.service.OssService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Api(tags = "阿里云文件管理")
@RequestMapping("/api/oss/file")
@RestController
public class OssController {
    @Autowired
    OssService ossService;

    @PostMapping("/uploadImage")
    public R uploadImage(@RequestParam("file") MultipartFile multipartFile, @RequestParam("module") String module) {
        //module 记录上传的是正面还是 反面
        //返回URL 到页面  后续需要和其他信息一起提交 写到数据库
        String url = ossService.uploadImage(multipartFile, module);

        return R.ok().data("url", url);

    }

    @DeleteMapping("/removeFile")
    public R removeFile(@RequestParam("url") String url) {
        ossService.removeFile(url);
        return R.ok();
    }


}
