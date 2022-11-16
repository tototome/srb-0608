package com.atguigu.srb.core.service;

import com.atguigu.srb.core.pojo.entity.BorrowInfo;
import com.atguigu.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务类
 * </p>
 *
 * @author pp
 * @since 2022-10-28
 */
public interface BorrowInfoService extends IService<BorrowInfo> {

    Integer getBorrowInfoStatusByUserId(String userId);

    Long getBorrowAmount(String userId);

    void saveBorrowInfo(BorrowInfo borrowInfo, String userId);

    List<BorrowInfo> getList();

    Map<String, Object> getBorrowInfoDetail(Long borrowId);

    void approval(BorrowInfoApprovalVO borrowInfoApprovalVO);
}
