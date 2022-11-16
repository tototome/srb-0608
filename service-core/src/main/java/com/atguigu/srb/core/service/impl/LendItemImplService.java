package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.common.exception.BusinessException;
import com.atguigu.srb.common.util.ResponseEnum;
import com.atguigu.srb.core.enums.TransTypeEnum;
import com.atguigu.srb.core.hfb.FormHelper;
import com.atguigu.srb.core.hfb.HfbConst;
import com.atguigu.srb.core.hfb.RequestHelper;
import com.atguigu.srb.core.mapper.*;
import com.atguigu.srb.core.pojo.BO.TransFlowBO;
import com.atguigu.srb.core.pojo.entity.*;
import com.atguigu.srb.core.pojo.vo.InvestVO;
import com.atguigu.srb.core.service.LendItemService;
import com.atguigu.srb.core.service.TransFlowService;
import com.atguigu.srb.core.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务实现类
 * </p>
 *
 * @author pp
 * @since 2022-10-28
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class LendItemImplService extends ServiceImpl<LendItemMapper, LendItem> implements LendItemService {
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    LendMapper lendMapper;
    @Autowired
    BorrowerMapper borrowerMapper;
    @Autowired
    UserAccountMapper userAccountMapper;
    @Autowired
    TransFlowService transFlowService;

    @Override
    public BigDecimal getInterestCount(BigDecimal investAmount, BigDecimal lendYearRate, Integer period, Integer returnMethod) {
        BigDecimal interestCount = new BigDecimal("0");
        if (returnMethod == 1) {
            interestCount = Amount1Helper.getInterestCount(investAmount, lendYearRate, period);
        }
        if (returnMethod == 2) {
            interestCount = Amount2Helper.getInterestCount(investAmount, lendYearRate, period);
        }
        if (returnMethod == 3) {
            interestCount = Amount3Helper.getInterestCount(investAmount, lendYearRate, period);
        }
        if (returnMethod == 4) {
            interestCount = Amount4Helper.getInterestCount(investAmount, lendYearRate, period);

        }
        return interestCount;
    }

    @Override
    public String commitInvest(InvestVO investVO, String userId) {
        /*  agent_id vote_bind_code benefit_bind_code agent_bill_no vote_amt vote_fee_amt notify_url return_url  sign


           agent_project_code agent_project_name

        * */
        UserInfo investInfo = userInfoMapper.selectById(userId);
        Long lendId = investVO.getLendId();
        Lend lend = lendMapper.selectById(lendId);
        Long borrowerId = lend.getUserId();
        UserInfo borrowerInfo = userInfoMapper.selectById(borrowerId);
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id", investInfo.getId());
        UserAccount investUserAccount = userAccountMapper.selectOne(userAccountQueryWrapper);
        //超卖 验证 不能超出投资上限
        BigDecimal investAmount = new BigDecimal(investVO.getInvestAmount());
        BigDecimal sum = lend.getInvestAmount().add(investAmount);
        if (sum.compareTo(lend.getAmount()) == -1) {
            throw new BusinessException(ResponseEnum.LEND_FULL_SCALE_ERROR);
        }
        //余额 验证
        if (investUserAccount.getAmount().compareTo(investAmount) == -1) {
            throw new BusinessException(ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);
        }
        //生成投资信息lendItems
        //投资编号
        String lendItemNo = LendNoUtils.getLendItemNo();
        LendItem lendItem = new LendItem();
        lendItem.setInvestUserId(investInfo.getId());
        lendItem.setLendItemNo(lendItemNo);
        lendItem.setLendId(lendId);
        lendItem.setInvestAmount(investAmount);
        lendItem.setInvestName(investInfo.getName());
        lendItem.setLendYearRate(lend.getLendYearRate());
        lendItem.setInvestTime(LocalDateTime.now());
        lendItem.setLendStartDate(lend.getLendStartDate());
        lendItem.setLendEndDate(lend.getLendEndDate());
        //预期收益
        lendItem.setExpectAmount(getInterestCount(investAmount, lend.getLendYearRate(), lend.getPeriod(), lend.getReturnMethod()));
        lendItem.setRealAmount(new BigDecimal("0"));
        lendItem.setStatus(0); //刚刚创建
        //生成投资信息
        System.out.println("生成投资信息");
        baseMapper.insert(lendItem);

        //传入汇付宝的参数
        Map<String, Object> paramHfb = new HashMap<>();
        paramHfb.put("agentId", HfbConst.AGENT_ID);
        paramHfb.put("agentBillNo", lendItemNo);
        paramHfb.put("voteBindCode", investInfo.getBindCode());
        paramHfb.put("chargeAmt", new BigDecimal(investVO.getInvestAmount()));
        paramHfb.put("benefitBindBode", borrowerInfo.getBindCode());
        paramHfb.put("voteFeeAmt", 0);
        paramHfb.put("voteAmt", investVO.getInvestAmount());
        paramHfb.put("agentProjectCode", lend.getLendNo());
        paramHfb.put("agentProjectName", lend.getTitle());
        paramHfb.put("projectAmt", lend.getAmount());
        paramHfb.put("returnUrl", HfbConst.INVEST_RETURN_URL);
        paramHfb.put("notifyUrl", HfbConst.INVEST_NOTIFY_URL);
        paramHfb.put("timestamp", RequestHelper.getTimestamp());
        paramHfb.put("sign", RequestHelper.getSign(paramHfb));
        String form = FormHelper.buildForm(HfbConst.INVEST_URL, paramHfb);
        //返回汇付宝表单
        return form;

    }

    @Override
    public void notifyLendItem(Map<String, Object> paramMap) {
        String agentBillNo = (String) paramMap.get("agentBillNo");
        boolean saveTransFlow = transFlowService.isSaveTransFlow(agentBillNo);
        if (saveTransFlow) {
            //幂等性校验 一个交易流水只能使用一次
            //没有说明 没有交易流水 后面需要生成
            return;
        }
        String voteBindCode = (String) paramMap.get("voteBindCode");
        BigDecimal voteAmt = new BigDecimal((String) paramMap.get("voteAmt"));
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("bind_code", voteBindCode);
        UserInfo investUserInfo = userInfoMapper.selectOne(userInfoQueryWrapper);
        Long investUserInfoId = investUserInfo.getId();
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id", investUserInfoId);
        UserAccount userAccount = userAccountMapper.selectOne(userAccountQueryWrapper);
        userAccount.setAmount(userAccount.getAmount().subtract(voteAmt));
        userAccount.setFreezeAmount(voteAmt);
        System.out.println("修改投资人的userAccount");
        //修改投资人的userAccount
        userAccountMapper.updateById(userAccount);
        //修改 投资记录lendItem
        QueryWrapper<LendItem> lendItemQueryWrapper = new QueryWrapper<>();
        lendItemQueryWrapper.eq("lend_item_no",agentBillNo);
        LendItem lendItem = baseMapper.selectOne(lendItemQueryWrapper);
        lendItem.setStatus(1);
        System.out.println("修改 投资记录lendItem");
        baseMapper.updateById(lendItem);
        //修改标的信息 标的的投资人数 一已投金额
        Long lendId = lendItem.getLendId();
        Lend lend = lendMapper.selectById(lendId);
        lend.setInvestNum(lend.getInvestNum()+1);
        lend.setInvestAmount(lend.getInvestAmount().add(voteAmt));
        System.out.println("修改标的信息");
        lendMapper.updateById(lend);
        //新增交易流水
        TransFlowBO transFlowBO = new TransFlowBO();
        transFlowBO.setAmount(voteAmt);
        transFlowBO.setAgentBillNo(agentBillNo);
        transFlowBO.setTransTypeEnum(TransTypeEnum.INVEST_LOCK);
        transFlowBO.setBindCode(voteBindCode);
        transFlowBO.setMemo("投资编号"+lend.getLendNo()+"项目名称"+lend.getTitle());
        System.out.println("生成交易流水");
        transFlowService.saveTransFlow(transFlowBO);
        return;
    }

    @Override
    public List<LendItem> getLendItemList() {
        QueryWrapper<LendItem> lendItemQueryWrapper = new QueryWrapper<>();
        lendItemQueryWrapper.eq("is_deleted",0);
        List<LendItem> lendItems = baseMapper.selectList(lendItemQueryWrapper);
        return lendItems;
    }


}
