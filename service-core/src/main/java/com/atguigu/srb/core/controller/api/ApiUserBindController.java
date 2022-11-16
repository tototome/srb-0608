package com.atguigu.srb.core.controller.api;

import com.atguigu.srb.common.util.R;
import com.atguigu.srb.core.hfb.RequestHelper;
import com.atguigu.srb.core.pojo.vo.UserBindVO;
import com.atguigu.srb.core.service.UserBindService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/core/userBind/")
//@CrossOrigin
@Slf4j
@Api(tags = "账户绑定接口")
public class ApiUserBindController {
    @Autowired
    UserBindService userBindService;


    @PostMapping("commitBind")
    public R commitBind(@RequestBody UserBindVO userBindVO, HttpServletRequest request) {
        String userId = request.getHeader("userId");
        //调用业务层 生成汇付宝的表单
        String from = userBindService.commitBind(userBindVO,userId);
        return R.ok().data("from", from);
    }

    @PostMapping("notify")
    public String userBindNotify(HttpServletRequest httpServletRequest){
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        Map<String, Object> returnParamMap = RequestHelper.switchMap(parameterMap);
        userBindService.commitBindNotify(returnParamMap);
        return  "success";
    }

}
