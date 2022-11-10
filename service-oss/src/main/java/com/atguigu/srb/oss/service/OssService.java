package com.atguigu.srb.oss.service;

import org.springframework.web.multipart.MultipartFile;

public interface OssService {


    String uploadImage(MultipartFile multipartFile, String module);

    void removeFile(String url);
}
