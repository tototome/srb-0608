package com.atguigu.srb.core.service;

import com.atguigu.srb.core.pojo.entity.Borrower;
import com.atguigu.srb.core.pojo.vo.BorrowerApprovalVO;
import com.atguigu.srb.core.pojo.vo.BorrowerDetailVO;
import com.atguigu.srb.core.pojo.vo.BorrowerVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 借款人 服务类
 * </p>
 *
 * @author pp
 * @since 2022-10-28
 */
public interface BorrowerService extends IService<Borrower> {

    Integer getBorrowerStatusByUserId(String userId);

    void saveBorrower(BorrowerVO borrowerVO, String userId);

    IPage<Borrower> getPageList(IPage<Borrower> borrowerPage, String keyword);

    BorrowerDetailVO getBorrower(Long borrowerId);

    void approvalSubmit(BorrowerApprovalVO borrowerApprovalVO);

}
