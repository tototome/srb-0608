package com.atguigu.srb.core.controller.admin;

import com.atguigu.srb.common.util.R;
import com.atguigu.srb.core.pojo.entity.BorrowInfo;
import com.atguigu.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.atguigu.srb.core.service.BorrowInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/core/borrowInfo")
public class AdminBorrowInfoController {

    @Autowired
    BorrowInfoService borrowInfoService;
    @GetMapping("list")
    public R getList(){
       List<BorrowInfo>  borrowInfoList=borrowInfoService.getList();
        return R.ok().data("list",borrowInfoList);
    }

    @GetMapping("getBorrowInfoDetail/{borrowId}")
    public R getBorrowInfoDetail(@PathVariable("borrowId")Long borrowId){
        Map<String,Object> borrowInfoDetail =borrowInfoService.getBorrowInfoDetail(borrowId);
        return R.ok().data("borrowInfoDetail",borrowInfoDetail);
    }
    @PostMapping("approval")
    public R approval(@RequestBody BorrowInfoApprovalVO borrowInfoApprovalVO){
        borrowInfoService.approval(borrowInfoApprovalVO);
        return R.ok().message("审核完成");
    }
}
