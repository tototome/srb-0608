package com.atguigu.srb.core.service;

import com.atguigu.srb.core.pojo.entity.UserBind;
import com.atguigu.srb.core.pojo.vo.UserBindVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author pp
 * @since 2022-10-28
 */
public interface UserBindService extends IService<UserBind> {

    String commitBind(UserBindVO userBindVO,String userId);

    void commitBindNotify(Map<String, Object> stringObjectMap);
}
