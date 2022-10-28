package com.atguigu.srb.core.controller.admin;

import com.atguigu.srb.common.exception.BusinessException;
import com.atguigu.srb.common.util.R;
import com.atguigu.srb.core.controller.IntegralGradeController;
import com.atguigu.srb.core.pojo.entity.IntegralGrade;
import com.atguigu.srb.core.service.IntegralGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Predicate;


@RestController
@RequestMapping("/admin/core/integralGrade")
@Api(tags = "积分等级管理接口")
public class AdminIntegralGradeController {
    @Autowired
    IntegralGradeService integralGradeService;
    @GetMapping("/IG/{id}")
    @ApiOperation("根据ID查询")
    public R all(@PathVariable("id") Integer id) {
        IntegralGrade byId = integralGradeService.getById(id);
        return R.ok().data("integralGrade",byId);
    }
    @GetMapping("/IG")
    @ApiOperation("查询所有")
    public R listIntegralGrade(){
        List<IntegralGrade> list = integralGradeService.list();
        return R.ok().data("list",list);
    }

    @PostMapping("/IG")
    @ApiOperation("添加信息")
    public  R saveIntegralGrade(@RequestBody IntegralGrade integralGrade){
        Predicate<IntegralGrade> gradePredicate
                = x -> x.getIntegralStart() <= 0 ||
                x.getIntegralEnd() <= 0 ||
                x.getBorrowAmount().intValue() <= 0;
        boolean test = gradePredicate.test(integralGrade);
        if (test)
            throw new BusinessException("asdas");
        boolean save = integralGradeService.save(integralGrade);
        return R.ok();
    }

    @PutMapping("/IG")
    @ApiOperation("更新信息")
    public  R updataIntegralGrade(@RequestBody IntegralGrade integralGrade){
        boolean updateById = integralGradeService.updateById(integralGrade);
        return R.ok();
    }

}
