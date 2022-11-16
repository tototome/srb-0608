package com.atguigu.srb.core.controller.api;


import com.atguigu.srb.common.util.R;
import com.atguigu.srb.core.pojo.entity.Lend;
import com.atguigu.srb.core.pojo.entity.UserAccount;
import com.atguigu.srb.core.service.LendService;
import com.atguigu.srb.core.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/core/lend")
public class ApiLendController {
    @Autowired
    LendService lendService;
    @Autowired
    UserAccountService userAccountService;

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
    @GetMapping("userAccountById")
    public R userAccountById(HttpServletRequest request){
        String userId = request.getHeader("userId");
        UserAccount userAccount =userAccountService.userAccountById(userId);
        BigDecimal amount = userAccount.getAmount();
        return  R.ok().data("amount",amount);
    }

}
