package com.atguigu.srb.core.controller.admin;

import com.atguigu.srb.common.util.R;
import com.atguigu.srb.core.pojo.entity.Borrower;
import com.atguigu.srb.core.pojo.vo.BorrowerApprovalVO;
import com.atguigu.srb.core.pojo.vo.BorrowerDetailVO;
import com.atguigu.srb.core.pojo.vo.BorrowerVO;
import com.atguigu.srb.core.service.BorrowerService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin/core/borrower")
public class AdminBorrowerController {

    @Autowired
    BorrowerService borrowerService;
    ///admin/core/borrower/getPageList/${limit}/${page}
    @GetMapping("/getPageList/{limit}/{page}")
    public R getPageList(@PathVariable("limit")Long limt, @PathVariable("page") Long page, String keyword){
        IPage<Borrower>  borrowerPage=new Page<>();

        borrowerPage.setCurrent(page);
        borrowerPage.setSize(limt);

        borrowerPage=borrowerService.getPageList(borrowerPage,keyword);

        return  R.ok().data("borrowerPage",borrowerPage);
    }

    @GetMapping("/getBorrower/{borrowerId}")
    public R getBorrower(@PathVariable("borrowerId") Long borrowerId){
      BorrowerDetailVO borrowerDetailVO =borrowerService.getBorrower(borrowerId);
        return R.ok().data("borrowerDetailVO",borrowerDetailVO);
    }

    //其实用map接收我觉得也可以
    @PostMapping("/approvalSubmit")
    public R approvalSubmit(@RequestBody BorrowerApprovalVO borrowerApprovalVO){
       borrowerService.approvalSubmit(borrowerApprovalVO);
        return  R.ok();
    }

}
