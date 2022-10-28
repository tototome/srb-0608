package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.core.pojo.entity.Borrower;
import com.atguigu.srb.core.mapper.BorrowerMapper;
import com.atguigu.srb.core.service.BorrowerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author pp
 * @since 2022-10-28
 */
@Service
public class BorrowerImplService extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

}
