package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.core.pojo.entity.UserAccount;
import com.atguigu.srb.core.mapper.UserAccountMapper;
import com.atguigu.srb.core.service.UserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author pp
 * @since 2022-10-28
 */
@Service
public class UserAccountImplService extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

}
