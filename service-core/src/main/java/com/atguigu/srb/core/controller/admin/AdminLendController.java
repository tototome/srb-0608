package com.atguigu.srb.core.controller.admin;

import com.atguigu.srb.common.util.R;
import com.atguigu.srb.core.pojo.entity.Lend;
import com.atguigu.srb.core.service.LendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/core/lend")
public class AdminLendController {

    @Autowired
    LendService lendService;

    @GetMapping("getList")
    public R getList() {
        List<Lend> list = lendService.getList();
        return R.ok().data("list", list);
    }

    @GetMapping("/getLendInfoDetail/{lendId}")
    public R getLendInfoDetail(@PathVariable("lendId")Long lendId) {

       Map<String,Object> lendInfoDetail =lendService.getLendInfoDetail(lendId);
        return R.ok().data("lendDetail",lendInfoDetail);
    }

    @GetMapping("/makeLoan/{lendId}")
    public R  makeLoan(@PathVariable("lendId")Long lendId){
        lendService.makeLoan(lendId);
        return R.ok();
    }
}
