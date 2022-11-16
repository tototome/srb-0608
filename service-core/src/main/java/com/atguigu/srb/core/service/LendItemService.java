package com.atguigu.srb.core.service;

import com.atguigu.srb.core.pojo.entity.LendItem;
import com.atguigu.srb.core.pojo.vo.InvestVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务类
 * </p>
 *
 * @author pp
 * @since 2022-10-28
 */
public interface LendItemService extends IService<LendItem> {

    BigDecimal getInterestCount(BigDecimal investAmount, BigDecimal lendYearRate, Integer period, Integer returnMethod);

    String commitInvest(InvestVO investVO, String userId);

    void notifyLendItem(Map<String, Object> paramMap);

    List<LendItem> getLendItemList();
}
