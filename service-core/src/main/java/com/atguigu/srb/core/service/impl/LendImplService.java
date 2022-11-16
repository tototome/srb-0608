package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.core.enums.BorrowerStatusEnum;
import com.atguigu.srb.core.enums.LendStatusEnum;
import com.atguigu.srb.core.mapper.BorrowerMapper;
import com.atguigu.srb.core.pojo.entity.BorrowInfo;
import com.atguigu.srb.core.pojo.entity.Borrower;
import com.atguigu.srb.core.pojo.entity.Lend;
import com.atguigu.srb.core.mapper.LendMapper;
import com.atguigu.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.atguigu.srb.core.pojo.vo.BorrowerDetailVO;
import com.atguigu.srb.core.service.DictService;
import com.atguigu.srb.core.service.LendService;
import com.atguigu.srb.core.util.LendNoUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务实现类
 * </p>
 *
 * @author pp
 * @since 2022-10-28
 */
@Service
public class LendImplService extends ServiceImpl<LendMapper, Lend> implements LendService {
    @Autowired
    DictService dictService;
    @Autowired
    BorrowerMapper borrowerMapper;
    @Override
    public void creatLend(BorrowInfoApprovalVO borrowInfoApprovalVO, BorrowInfo borrowInfo) {
        Lend lend = new Lend();
        BeanUtils.copyProperties(borrowInfo, lend);
        BeanUtils.copyProperties(borrowInfoApprovalVO, lend);
        lend.setBorrowInfoId(borrowInfo.getId());
        lend.setLendNo(LendNoUtils.getLendNo());
        lend.setLendYearRate(borrowInfoApprovalVO.getLendYearRate().divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP));
        lend.setServiceRate(borrowInfoApprovalVO.getServiceRate().divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP));
        lend.setLowestAmount(new BigDecimal(100));
        lend.setInvestAmount(new BigDecimal(0));
        lend.setInvestNum(0);
        lend.setPublishDate(LocalDateTime.now());
        //起息日期
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate lendStartDate = LocalDate.parse(borrowInfoApprovalVO.getLendStartDate(), dtf);
        lend.setLendStartDate(lendStartDate);
        //结束日期
        LocalDate lendEndDate = lendStartDate.plusMonths(borrowInfo.getPeriod());
        lend.setLendEndDate(lendEndDate);

        //平台预期收益
        //        月年化 = 年化 / 12
        BigDecimal monthRate = lend.getServiceRate().divide(new BigDecimal(12), 8, BigDecimal.ROUND_DOWN);
        //        平台收益 = 标的金额 * 月年化 * 期数
        BigDecimal expectAmount = lend.getAmount().multiply(monthRate).multiply(new BigDecimal(lend.getPeriod()));
        lend.setExpectAmount(expectAmount);
        //实际收益
        lend.setRealAmount(new BigDecimal(0));
        //状态
        lend.setStatus(LendStatusEnum.INVEST_RUN.getStatus());
        //审核时间
        lend.setCheckTime(LocalDateTime.now());
        //审核人
        lend.setCheckAdminId(1L);

        baseMapper.insert(lend);
    }

    @Override
    public List<Lend> getList() {
        QueryWrapper<Lend> lendQueryWrapper = new QueryWrapper<>();
        lendQueryWrapper.eq("is_deleted",0);
        List<Lend> list = baseMapper.selectList(lendQueryWrapper);
        list.forEach(lend -> {
            String returnMethod = dictService.getNameAndDictCodeAndId("returnMethod", lend.getReturnMethod());
            String status = LendStatusEnum.getMsgByStatus(lend.getStatus());
            lend.getParam().put("returnMethod", returnMethod);
            lend.getParam().put("status", status);
        });
        return list;
    }

    @Override
    public Map<String, Object> getLendInfoDetail(Long lendId) {
        Lend lend = baseMapper.selectById(lendId);
        String returnMethod = dictService.getNameAndDictCodeAndId("returnMethod", lend.getReturnMethod());
        String status = LendStatusEnum.getMsgByStatus(lend.getStatus());
        lend.getParam().put("returnMethod", returnMethod);
        lend.getParam().put("status", status);
        Long userId = lend.getUserId();
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.eq("user_id",userId);
        Borrower borrower = borrowerMapper.selectOne(borrowerQueryWrapper);

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

        Map<String,Object> lendDetail = new HashMap<>();

        lendDetail.put("borrower",borrowerDetailVO);
        lendDetail.put("lend",lend);

        return lendDetail;
    }
}
