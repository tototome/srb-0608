package com.atguigu.srb.core.controller.api;

import com.atguigu.srb.common.util.R;
import com.atguigu.srb.core.hfb.RequestHelper;
import com.atguigu.srb.core.pojo.entity.LendItem;
import com.atguigu.srb.core.pojo.vo.InvestVO;
import com.atguigu.srb.core.service.LendItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/core/lendItem")
public class ApiLendItemController {
    @Autowired
    LendItemService lendItemService;
    @GetMapping("getInterestCount/{investAmount}/{lendYearRate}/{period}/{returnMethod}")
    public R getInterestCount(@PathVariable BigDecimal investAmount,@PathVariable BigDecimal lendYearRate,@PathVariable Integer period,@PathVariable Integer returnMethod){
         BigDecimal   interestCount=lendItemService.getInterestCount(investAmount,lendYearRate,period,returnMethod);
         return R.ok().data("interestCount",interestCount);
    }

    @PostMapping("auth/commitInvest")
    public R commitInvest(@RequestBody InvestVO investVO, HttpServletRequest request){
        String userId = request.getHeader("userId");
        System.out.println(investVO);
        //生成汇付宝表单
        String formStr =lendItemService.commitInvest(investVO,userId);
        return R.ok().data("formStr",formStr);
    }

    @PostMapping("notify")
    public  String notifyLendItem(HttpServletRequest httpServletRequest){
        Map<String, Object> paramMap = RequestHelper.switchMap(httpServletRequest.getParameterMap());
        if (RequestHelper.isSignEquals(paramMap)){
            if ("0001".equals(paramMap.get("resultCode"))){
                lendItemService.notifyLendItem(paramMap);
            }else {
                System.out.println("投标失败");
                return "fail";
            }
        }else {
            System.out.println("签名错误");
            return  "fail";
        }
        return "success";
    }
    @GetMapping("getLendItemList")
    public R getLendItemList(){
       List<LendItem> lendItemList =lendItemService.getLendItemList();
       return R.ok().data("lendItemList",lendItemList);
    }

}
