package com.atguigu.srb.core.controller.admin;

import com.atguigu.srb.core.controller.IntegralGradeController;
import com.atguigu.srb.core.pojo.entity.IntegralGrade;
import com.atguigu.srb.core.service.IntegralGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/admin/core/integralGrade")
public class AdminIntegralGradeController {
    @Autowired
    IntegralGradeService integralGradeService;
    @GetMapping("/{id}")
    public IntegralGrade all(@PathVariable("id") Integer id) {
        IntegralGrade byId = integralGradeService.getById(id);
        return byId;
    }

}
