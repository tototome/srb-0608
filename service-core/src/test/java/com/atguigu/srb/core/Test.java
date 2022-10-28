package com.atguigu.srb.core;

import com.atguigu.srb.core.pojo.entity.IntegralGrade;

import java.math.BigDecimal;
import java.util.function.Predicate;

public class Test {
    @org.junit.Test
    public void test01() {
        IntegralGrade integralGrade = new IntegralGrade();
        integralGrade.setIntegralEnd(10);
        integralGrade.setIntegralStart(5);
        integralGrade.setBorrowAmount(new BigDecimal(10000));
        Predicate<IntegralGrade> gradePredicate
                = x -> x.getIntegralStart() <= 0 ||
                x.getIntegralEnd() <= 0 ||
                x.getBorrowAmount().intValue() <= 0;
        boolean test = gradePredicate.test(integralGrade);
        System.out.println(test);
    }
}
