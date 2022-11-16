package com.atguigu.srb.core.controller.api;

import com.atguigu.srb.common.util.R;
import com.atguigu.srb.core.pojo.entity.BorrowInfo;
import com.atguigu.srb.core.service.BorrowInfoService;
import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/core/borrowerInfo")
public class ApiBorrowerInfoController {
    @Autowired
    BorrowInfoService borrowInfoService;

    @GetMapping("getBorrowInfoStatusByUserId")
    public R getBorrowInfoStatusByUserId(HttpServletRequest httpServletRequest) {
        String userId = httpServletRequest.getHeader("userId");
        Integer borrowInfoStatus = borrowInfoService.getBorrowInfoStatusByUserId(userId);
        return R.ok().data("borrowInfoStatus", borrowInfoStatus);
    }

    @GetMapping("getBorrowAmount")
    public R getBorrowAmount(HttpServletRequest httpServletRequest) {
        String userId = httpServletRequest.getHeader("userId");
        Long borrowAmount = borrowInfoService.getBorrowAmount(userId);
        return R.ok().data("borrowAmount", borrowAmount);
    }

    @PostMapping("auth/save")
    public R save(@RequestBody BorrowInfo borrowInfo,HttpServletRequest httpServletRequest){
        String userId = httpServletRequest.getHeader("userId");

        borrowInfoService.saveBorrowInfo(borrowInfo,userId);
        return R.ok().message("提交成功");

    }

}
