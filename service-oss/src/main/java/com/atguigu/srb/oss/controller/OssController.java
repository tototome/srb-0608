package com.atguigu.srb.oss.controller;

import com.atguigu.srb.common.util.R;
import com.atguigu.srb.oss.service.OssService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Api(tags = "阿里云文件管理")
@RequestMapping("/api/oss/file")
@RestController
public class OssController {
    @Autowired
    OssService ossService;

    @PostMapping("/uploadImage")
    public  R uploadImage(@RequestParam("file")MultipartFile multipartFile,@RequestParam("module")String module){

       String url=ossService.uploadImage(multipartFile,module);

       return  R.ok().data("url",url);

    }


}
