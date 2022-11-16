package com.atguigu.srb.core.controller.api;


import com.atguigu.srb.common.util.R;



import com.atguigu.srb.core.pojo.vo.BorrowerVO;
import com.atguigu.srb.core.service.BorrowerService;
import com.atguigu.srb.core.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/core/borrower")
public class ApiBorrowerController {

    @Autowired
    BorrowerService borrowerService;

    @Autowired
    DictService dictService;

    @GetMapping("/getBorrowerStatusByUserId")
    public R getBorrowerStatusByUserId(HttpServletRequest httpServletRequest) {
        String userId = httpServletRequest.getHeader("userId");
        Integer borrowerStatus = borrowerService.getBorrowerStatusByUserId(userId);
        return R.ok().data("borrowerStatus", borrowerStatus);
    }

    @PostMapping("save")
    public R saveBorrower(@RequestBody BorrowerVO borrowerVO,HttpServletRequest request){
        //可以加一个判断 判断 用户是否多次提交  对信息进行修改
        String userId = request.getHeader("userId");
        borrowerService.saveBorrower(borrowerVO,userId);
        return R.ok();
    }

}
