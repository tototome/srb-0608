package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.core.enums.BorrowerStatusEnum;
import com.atguigu.srb.core.enums.IntegralEnum;
import com.atguigu.srb.core.mapper.BorrowerAttachMapper;
import com.atguigu.srb.core.mapper.UserInfoMapper;
import com.atguigu.srb.core.mapper.UserIntegralMapper;
import com.atguigu.srb.core.pojo.entity.Borrower;
import com.atguigu.srb.core.mapper.BorrowerMapper;
import com.atguigu.srb.core.pojo.entity.BorrowerAttach;
import com.atguigu.srb.core.pojo.entity.UserInfo;
import com.atguigu.srb.core.pojo.entity.UserIntegral;
import com.atguigu.srb.core.pojo.vo.BorrowerApprovalVO;
import com.atguigu.srb.core.pojo.vo.BorrowerAttachVO;
import com.atguigu.srb.core.pojo.vo.BorrowerDetailVO;
import com.atguigu.srb.core.pojo.vo.BorrowerVO;
import com.atguigu.srb.core.service.BorrowerService;
import com.atguigu.srb.core.service.DictService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public Integer getBorrowerStatusByUserId(String userId) {
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.eq("user_id", userId);
        Borrower borrower = baseMapper.selectOne(borrowerQueryWrapper);
        if (borrower == null || borrower.getStatus() == null) {
            return 0;
        }
        return borrower.getStatus();


    }

    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    BorrowerAttachMapper borrowerAttachMapper;
    @Autowired
    DictService dictService;
    @Autowired
    UserIntegralMapper userIntegralMapper;



    @Override
    public void saveBorrower(BorrowerVO borrowerVO, String userId) {

        // 保存borrower
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVO, borrower);
        borrower.setMarry(borrowerVO.getMarry());
        borrower.setUserId(Long.parseLong(userId));
        UserInfo userInfo = userInfoMapper.selectById(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        baseMapper.insert(borrower);

        // 返回borrower主键id
        Long borrowerId = borrower.getId();

        // 根据主键id保存attach集合
        List<BorrowerAttach> borrowerAttachList = borrowerVO.getBorrowerAttachList();
        for (BorrowerAttach borrowerAttach : borrowerAttachList) {
            borrowerAttach.setBorrowerId(borrowerId);
            borrowerAttachMapper.insert(borrowerAttach);
        }
        //更新用户信息
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public IPage<Borrower> getPageList(IPage<Borrower> borrowerPage, String keyword) {
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(keyword)) {
            borrowerQueryWrapper.like("name", keyword)
                    .or().like("mobile", keyword)
                    .or().like("id_card", keyword);
        }
        IPage<Borrower> borrowerIPage = baseMapper.selectPage(borrowerPage, borrowerQueryWrapper);
        return borrowerPage;
    }
    //排除法看错误是不是产生在方法之中 方法全部去掉 再一个一个加入验证
    @Override
    public BorrowerDetailVO getBorrower(Long borrowerId) {
        Borrower borrower = baseMapper.selectById(borrowerId);
        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();
        BeanUtils.copyProperties(borrower,borrowerDetailVO);

        borrowerDetailVO.setMarry(borrower.getMarry()?"是":"否");
        borrowerDetailVO.setSex(borrower.getSex()==0?"男":"女");

        borrowerDetailVO.setEducation(dictService.getNameAndDictCodeAndId("education",borrower.getEducation()));
        borrowerDetailVO.setIndustry(dictService.getNameAndDictCodeAndId("industry",borrower.getIndustry()));
        borrowerDetailVO.setIncome(dictService.getNameAndDictCodeAndId("income",borrower.getIncome()));
        borrowerDetailVO.setReturnSource(dictService.getNameAndDictCodeAndId("returnSource",borrower.getReturnSource()));
        borrowerDetailVO.setContactsRelation(dictService.getNameAndDictCodeAndId("relation",borrower.getContactsRelation()));

        borrowerDetailVO.setStatus(BorrowerStatusEnum.getMsgByStatus(borrower.getStatus()));

        QueryWrapper<BorrowerAttach> borrowerAttachQueryWrapper = new QueryWrapper<>();
        borrowerAttachQueryWrapper.eq("borrower_id",borrowerId);
        List<BorrowerAttach> borrowerAttaches = borrowerAttachMapper.selectList(borrowerAttachQueryWrapper);

        List<BorrowerAttachVO> borrowerAttachVOList = new ArrayList<>();


        for (BorrowerAttach borrowerAttach:borrowerAttaches){
            BorrowerAttachVO borrowerAttachVO = new BorrowerAttachVO();
            borrowerAttachVO.setImageType(borrowerAttach.getImageType());
            borrowerAttachVO.setImageUrl(borrowerAttach.getImageUrl());
            borrowerAttachVOList.add(borrowerAttachVO);
        }
        borrowerDetailVO.setBorrowerAttachVOList(borrowerAttachVOList);
        return  borrowerDetailVO;

    }

    @Override
    public void approvalSubmit(BorrowerApprovalVO borrowerApprovalVO) {
        //修改状态
        Borrower borrower = baseMapper.selectById(borrowerApprovalVO.getBorrowerId());
        borrower.setStatus(borrowerApprovalVO.getStatus());
        baseMapper.updateById(borrower);
        //设置userIntegral
        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setUserId(borrower.getUserId());
        String context="";
        Integer integral=borrowerApprovalVO.getInfoIntegral();
        if (borrowerApprovalVO.getIsCarOk()){
            context=context+ IntegralEnum.BORROWER_CAR.getMsg();
            integral=integral+IntegralEnum.BORROWER_CAR.getIntegral();
        }
        if (borrowerApprovalVO.getIsHouseOk()){
            context=context+IntegralEnum.BORROWER_HOUSE.getMsg();
            integral=integral+IntegralEnum.BORROWER_HOUSE.getIntegral();
        }
        if (borrowerApprovalVO.getIsIdCardOk()){
            context=context+IntegralEnum.BORROWER_IDCARD.getMsg();
            integral=integral+IntegralEnum.BORROWER_IDCARD.getIntegral();
        }
        userIntegral.setIntegral(integral);
        userIntegral.setContent(context);

        UserInfo userInfo = userInfoMapper.selectById(borrower.getUserId());
        userInfo.setIntegral(integral);
        userInfo.setBorrowAuthStatus(borrowerApprovalVO.getStatus());
        userInfoMapper.updateById(userInfo);

        userIntegralMapper.insert(userIntegral);


    }
}
