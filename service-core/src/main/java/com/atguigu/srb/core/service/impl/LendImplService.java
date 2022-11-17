package com.atguigu.srb.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.srb.common.util.Assert;
import com.atguigu.srb.common.util.ResponseEnum;
import com.atguigu.srb.core.enums.BorrowerStatusEnum;
import com.atguigu.srb.core.enums.LendStatusEnum;
import com.atguigu.srb.core.enums.TransTypeEnum;
import com.atguigu.srb.core.hfb.FormHelper;
import com.atguigu.srb.core.hfb.HfbConst;
import com.atguigu.srb.core.hfb.RequestHelper;
import com.atguigu.srb.core.mapper.*;
import com.atguigu.srb.core.pojo.BO.TransFlowBO;
import com.atguigu.srb.core.pojo.entity.*;
import com.atguigu.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.atguigu.srb.core.pojo.vo.BorrowerDetailVO;
import com.atguigu.srb.core.service.DictService;
import com.atguigu.srb.core.service.LendService;
import com.atguigu.srb.core.util.*;
import com.atguigu.srb.mq.service.MqService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    @Autowired
    LendItemMapper lendItemMapper;
    @Autowired
    UserAccountMapper userAccountMapper;
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    TransFlowMapper transFlowMapper;
    @Autowired
    MqService mqService;
    @Autowired
    LendReturnMapper lendReturnMapper;
    @Autowired
    LendItemReturnMapper lelendItemReturnMapper;

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
        lendQueryWrapper.eq("is_deleted", 0);
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
        borrowerQueryWrapper.eq("user_id", userId);
        Borrower borrower = borrowerMapper.selectOne(borrowerQueryWrapper);

        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();
        BeanUtils.copyProperties(borrower, borrowerDetailVO);

        borrowerDetailVO.setMarry(borrower.getMarry() ? "是" : "否");
        borrowerDetailVO.setSex(borrower.getSex() == 0 ? "男" : "女");

        borrowerDetailVO.setEducation(dictService.getNameAndDictCodeAndId("education", borrower.getEducation()));
        borrowerDetailVO.setIndustry(dictService.getNameAndDictCodeAndId("industry", borrower.getIndustry()));
        borrowerDetailVO.setIncome(dictService.getNameAndDictCodeAndId("income", borrower.getIncome()));
        borrowerDetailVO.setReturnSource(dictService.getNameAndDictCodeAndId("returnSource", borrower.getReturnSource()));
        borrowerDetailVO.setContactsRelation(dictService.getNameAndDictCodeAndId("relation", borrower.getContactsRelation()));
        borrowerDetailVO.setStatus(BorrowerStatusEnum.getMsgByStatus(borrower.getStatus()));

        Map<String, Object> lendDetail = new HashMap<>();

        lendDetail.put("borrower", borrowerDetailVO);
        lendDetail.put("lend", lend);

        return lendDetail;
    }

    @Override
    public void makeLoan(Long lendId) {
        String loanNo = LendNoUtils.getLoanNo();
        Lend lend = baseMapper.selectById(lendId);
        QueryWrapper<LendItem> lendItemQueryWrapper = new QueryWrapper<>();
        lendItemQueryWrapper.eq("lend_id", lendId);
        //一个标的 可能会有多个人投资 所以是有多个投资项
        List<LendItem> lendItemList = lendItemMapper.selectList(lendItemQueryWrapper);
        List<Long> investUserIdList = new ArrayList<>();
        for (LendItem lendItem : lendItemList) {
            investUserIdList.add(lendItem.getInvestUserId());
        }
        //调用汇付宝接口放款
        Map<String, Object> paramMap = new HashMap<>();
        //      sign
        paramMap.put("agentId", HfbConst.AGENT_ID);
        //放款项目编号 也就是标的单号
        paramMap.put("agentProjectCode", lend.getLendNo());
        //放款单号 同时也是 借款人 放款的交易流水单号
        paramMap.put("agentNillNo", loanNo);
        //手续费 也就是商家的预期收益
        paramMap.put("mchFee", lend.getExpectAmount());
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));
        System.out.println("调用汇付宝接口");
        JSONObject jsonObject = RequestHelper.sendRequest(paramMap,HfbConst.MAKE_LOAD_URL);
        //汇付宝返回结果
        String resultCode = (String) jsonObject.get("resultCode");
        BigDecimal voteAmt = new BigDecimal((String) jsonObject.get("voteAmt"));
        //判断放开给你是否成功
        Assert.isTrue("0000".equals(resultCode), ResponseEnum.COMMON_ERROR);
        //成功 修改标的信息 状态 放款人Id 放款时间
        lend.setStatus(LendStatusEnum.PAY_RUN.getStatus());
        lend.setPaymentTime(LocalDateTime.now());
        lend.setPaymentAdminId(1l);
        System.out.println("更新标的信息");
        baseMapper.updateById(lend);
        //生成账户交易流水 修改账户信息
        //修改借款人账户生成交易流水
        Long borrowUserId = lend.getUserId();
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id", borrowUserId);
        UserAccount borrowUserAccount = userAccountMapper.selectOne(userAccountQueryWrapper);
        borrowUserAccount.setAmount(voteAmt);
        System.out.println("更新借款人账户");
        userAccountMapper.updateById(borrowUserAccount);
        //借款人交易流水
        UserInfo borrowUserInfo = userInfoMapper.selectById(borrowUserId);
        TransFlow borrowTransFlow = new TransFlow();
        borrowTransFlow.setMemo("放款成功");
        borrowTransFlow.setUserName(borrowUserInfo.getName());
        borrowTransFlow.setTransNo(loanNo);
        borrowTransFlow.setTransType(TransTypeEnum.BORROW_BACK.getTransType());
        borrowTransFlow.setTransAmount(voteAmt);
        borrowTransFlow.setUserId(borrowUserId);
        borrowTransFlow.setTransTypeName(TransTypeEnum.BORROW_BACK.getTransTypeName());
        System.out.println("生成借款人交易流水");
        transFlowMapper.insert(borrowTransFlow);
        //修改投资人账户生成交易流水
        int i = 0;
        for (LendItem lendItem : lendItemList) {
            i++;
            Long investUserId = lendItem.getInvestUserId();
            UserInfo investUserInfo = userInfoMapper.selectById(investUserId);
            UpdateWrapper<UserAccount> investUserAccountUpdateWrapper = new UpdateWrapper<>();
            investUserAccountUpdateWrapper
                    .eq("user_id", investUserId)
                    .setSql("amount = amount + " + 0 + ",freeze_amount=freeze_amount + " + lendItem.getInvestAmount().negate());
            System.out.println("更新投资人账户");
            userAccountMapper.update(null, investUserAccountUpdateWrapper);
            //生成交易流水
            TransFlow investTransFlow = new TransFlow();
            investTransFlow.setMemo("放款扣账");
            investTransFlow.setUserName(investUserInfo.getName());
            //使用的是子单号 因为有多个投资人
            investTransFlow.setTransNo(loanNo + i);
            investTransFlow.setTransType(TransTypeEnum.INVEST_UNLOCK.getTransType());
            investTransFlow.setTransAmount(lendItem.getInvestAmount());
            investTransFlow.setUserId(investUserId);
            investTransFlow.setTransTypeName(TransTypeEnum.INVEST_UNLOCK.getTransTypeName());
            System.out.println("生成投资人交易流水");
            transFlowMapper.insert(investTransFlow);
            // 发送短信
            //mqService.send
        }
        //生成还款计划,
        creatLendReturnPlan(lend, lendItemList);
        //回款计划
    }

    //还款计划每期的还款金额等于是 回款计划每个投资人的每期收到的本金加利息
    //汇款计划有需要有 还款计划的ID
    //所以先创建出还款计划 还款的金额根据 回款计划的金额进行赋值
    private void creatLendReturnPlan(Lend lend, List<LendItem> lendItemList) {
        Integer period = lend.getPeriod();
        for (int i = 1; i <= period; i++) {
            LendReturn lendReturn = new LendReturn();
            lendReturn.setLendId(lend.getId());
            lendReturn.setBorrowInfoId(lend.getBorrowInfoId());
            lendReturn.setReturnNo(LendNoUtils.getReturnNo());
            lendReturn.setUserId(lend.getUserId());
            lendReturn.setAmount(lend.getAmount());
            if (i == lend.getPeriod()) {
                lendReturn.setLast(true);
            } else {
                lendReturn.setLast(false);
            }
            lendReturn.setCurrentPeriod(i);
            lendReturn.setLendYearRate(lend.getLendYearRate());
            lendReturn.setReturnMethod(lend.getReturnMethod());
            lendReturn.setStatus(0);
            lendReturn.setFee(new BigDecimal(0));
            lendReturn.setReturnDate(lend.getLendStartDate().plusMonths(i)); //第二个月开始还款
            lendReturn.setOverdue(false);
            lendReturn.setBaseAmount(getBaseAmount(lend.getId(), lend.getAmount(), i));
            System.out.println("生成还款计划");
            lendReturnMapper.insert(lendReturn);
            //计算本金和利息
            BigDecimal currentPrincipal = new BigDecimal("0");
            BigDecimal currentInterest = new BigDecimal("0");

            //生成回款计划 计算本金和利息
            // 每一期的投资人的回款本金 回款利息加起来就是我们 本期要还的金额
            for (LendItem lendItem : lendItemList) {
                System.out.println("创建回款计划入口");
                Map<String, BigDecimal> currentReturnMap = createLendItemReturnPlan(lend, lendItem, lendReturn, i);
                currentPrincipal = currentPrincipal.add(currentReturnMap.get("currentPrincipal"));
                currentInterest = currentInterest.add(currentReturnMap.get("currentInterest"));
            }

            lendReturn.setPrincipal(currentPrincipal);
            lendReturn.setInterest(currentInterest);
            System.out.println("更新还款计划本金利息");
            lendReturnMapper.updateById(lendReturn);

        }
    }

    private BigDecimal getBaseAmount(Long id, BigDecimal amount, int i) {

        QueryWrapper<LendReturn> lendReturnQueryWrapper = new QueryWrapper<>();
        lendReturnQueryWrapper.eq("lend_id", id);
        lendReturnQueryWrapper.lt("current_period", i);
        List<LendReturn> lendReturnList = lendReturnMapper.selectList(lendReturnQueryWrapper);
        if (lendReturnList != null && lendReturnList.size() > 0) {
            for (LendReturn lendReturn : lendReturnList) {
                //lendReturn.getPrincipal() 当期要还的本金
                return amount.subtract(lendReturn.getPrincipal());
            }
        }
        return amount;
    }

    private Map<String, BigDecimal> createLendItemReturnPlan(Lend lend, LendItem lendItem, LendReturn lendReturn, int i) {
        Map<String, BigDecimal> currentItemReturnMap = new HashMap<>();
        LendItemReturn lendItemReturn = new LendItemReturn();

        lendItemReturn.setLendReturnId(lendReturn.getId());
        lendItemReturn.setLendItemId(lendItem.getLendId());
        lendItemReturn.setLendId(lend.getId());
        lendItemReturn.setInvestUserId(lendItem.getInvestUserId());
        lendItemReturn.setInvestAmount(lendItem.getInvestAmount());
        lendItemReturn.setCurrentPeriod(i);
        lendItemReturn.setLendYearRate(lendItem.getLendYearRate());
        lendItemReturn.setReturnMethod(lend.getReturnMethod());
        lendItemReturn.setFee(new BigDecimal("0"));
        lendItemReturn.setReturnDate(lendReturn.getReturnDate());
        lendItemReturn.setOverdue(false);
        lendItemReturn.setStatus(0);

        Map<Integer, BigDecimal> perMonthPrincipal = new HashMap<>();
        Map<Integer, BigDecimal> perMonthInterest = new HashMap<>();
        if (lend.getReturnMethod() == 1) {
            perMonthPrincipal = Amount1Helper.getPerMonthPrincipal(lendItem.getInvestAmount(), lend.getLendYearRate(), lend.getPeriod());
            perMonthInterest = Amount1Helper.getPerMonthInterest(lendItem.getInvestAmount(), lend.getLendYearRate(), lend.getPeriod());
        }
        if (lend.getReturnMethod() == 2) {
            perMonthPrincipal = Amount2Helper.getPerMonthPrincipal(lendItem.getInvestAmount(), lend.getLendYearRate(), lend.getPeriod());
            perMonthInterest = Amount2Helper.getPerMonthInterest(lendItem.getInvestAmount(), lend.getLendYearRate(), lend.getPeriod());
        }
        if (lend.getReturnMethod() == 3) {
            perMonthPrincipal = Amount3Helper.getPerMonthPrincipal(lendItem.getInvestAmount(), lend.getLendYearRate(), lend.getPeriod());
            perMonthInterest = Amount3Helper.getPerMonthInterest(lendItem.getInvestAmount(), lend.getLendYearRate(), lend.getPeriod());
        }
        if (lend.getReturnMethod() == 4) {
            perMonthPrincipal = Amount4Helper.getPerMonthPrincipal(lendItem.getInvestAmount(), lend.getLendYearRate(), lend.getPeriod());
            perMonthInterest = Amount4Helper.getPerMonthInterest(lendItem.getInvestAmount(), lend.getLendYearRate(), lend.getPeriod());
        }
        BigDecimal currentMonthPrincipal = perMonthPrincipal.get(i);
        BigDecimal currentMonthInterest = perMonthInterest.get(i);
        currentItemReturnMap.put("currentInterest", currentMonthInterest);
        currentItemReturnMap.put("currentPrincipal", currentMonthPrincipal);
        lendItemReturn.setPrincipal(currentMonthPrincipal);
        lendItemReturn.setInterest(currentMonthInterest);
        System.out.println("回款计划生成");
        lendItemReturn.setTotal(currentMonthInterest.add(currentMonthInterest));
        lelendItemReturnMapper.insert(lendItemReturn);
        return currentItemReturnMap;

    }


}
