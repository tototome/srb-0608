package com.atguigu.srb.oss.service;

import org.springframework.web.multipart.MultipartFile;

public interface OssService {


    void upLoad(MultipartFile multipartFile);
}
