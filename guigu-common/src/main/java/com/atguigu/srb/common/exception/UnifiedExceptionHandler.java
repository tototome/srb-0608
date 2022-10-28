package com.atguigu.srb.common.exception;



import com.atguigu.srb.common.util.R;

import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Component
//这个注解的作用是切入点 restController的通知
@RestControllerAdvice
public class UnifiedExceptionHandler {

    @ExceptionHandler(Exception.class)
    public R commonException(Exception e){
        System.out.println("通用异常处理器");
        return R.error().message("通用异常："+e.getMessage());
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public R commonException(BadSqlGrammarException e){
        System.out.println("sql异常处理器");
        return R.error().message("sql异常："+e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public R commonException(BusinessException e){
        System.out.println("业务异常处理器");
        return R.error().message("业务异常："+e.getMessage());
    }

}
