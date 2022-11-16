package com.atguigu.srb.core.controller.admin;

import com.atguigu.srb.common.exception.BusinessException;
import com.atguigu.srb.common.util.Assert;
import com.atguigu.srb.common.util.R;
import com.atguigu.srb.common.util.ResponseEnum;
import com.atguigu.srb.core.controller.IntegralGradeController;
import com.atguigu.srb.core.pojo.entity.IntegralGrade;
import com.atguigu.srb.core.service.IntegralGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Predicate;


@Slf4j
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
        //log.info("info Grade");
        //log.error("error Grade");
        //log.debug("debug Grade");
        //log.trace("trace Grade");
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
        //Assert断言类 一个工具类可以 复制一根自己写也可以用 org.springframework.util.Assert里的;
        Assert.isTrue(!test, ResponseEnum.SAVE_MESSAGE_ERROR);
        boolean save = integralGradeService.save(integralGrade);
        return R.ok();
    }

    @PutMapping("/IG")
    @ApiOperation("更新信息")
    public  R updataIntegralGrade(@RequestBody IntegralGrade integralGrade){
        boolean updateById = integralGradeService.updateById(integralGrade);
        return R.ok();
    }

    @DeleteMapping("IG/{id}")
    @ApiOperation("根据ID删除信息")
    public  R deletedById(@PathVariable("id") Integer id){
        integralGradeService.removeById(id);
        return R.ok();
    }

}
