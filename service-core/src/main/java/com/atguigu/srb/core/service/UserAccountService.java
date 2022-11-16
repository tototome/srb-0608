package com.atguigu.srb.core.service;

import com.atguigu.srb.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 用户账户 服务类
 * </p>
 *
 * @author pp
 * @since 2022-10-28
 */
public interface UserAccountService extends IService<UserAccount> {

    String commitCharge(String chargeAmt, String userId);

    String notifyCharge(Map<String, Object> stringObjectMap);

    UserAccount userAccountById(String userId);
}
