package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.core.enums.TransTypeEnum;
import com.atguigu.srb.core.hfb.FormHelper;
import com.atguigu.srb.core.hfb.HfbConst;
import com.atguigu.srb.core.hfb.RequestHelper;
import com.atguigu.srb.core.mapper.UserInfoMapper;
import com.atguigu.srb.core.pojo.BO.TransFlowBO;
import com.atguigu.srb.core.pojo.entity.UserAccount;
import com.atguigu.srb.core.mapper.UserAccountMapper;
import com.atguigu.srb.core.pojo.entity.UserInfo;
import com.atguigu.srb.core.service.TransFlowService;
import com.atguigu.srb.core.service.UserAccountService;
import com.atguigu.srb.core.util.LendNoUtils;
import com.atguigu.srb.mq.config.MQConst;
import com.atguigu.srb.mq.pojo.dto.SmsDTO;
import com.atguigu.srb.mq.service.MqService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.object.UpdatableSqlQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author pp
 * @since 2022-10-28
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class UserAccountImplService extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    TransFlowService transFlowService;
    @Autowired
    MqService mqService;

    @Override
    public String commitCharge(String chargeAmt, String userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();

        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id", userId);
        UserAccount userAccount = baseMapper.selectOne(userAccountQueryWrapper);

        Map<String, Object> paramHfb = new HashMap<>();

        //充值单号 每次充值 生成一个单号 一个单号只能充值一次 防止多次冲值后面会有幂等性校验
        paramHfb.put("agentBillNo", LendNoUtils.getChargeNo());
        paramHfb.put("bindCode", bindCode);
        //charge_amt  fee_amt notify_url  return_url timestamp sign
        paramHfb.put("chargeAmt", new BigDecimal(chargeAmt));
        paramHfb.put("feeAmt", 0);
        paramHfb.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        paramHfb.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);
        paramHfb.put("timestamp", RequestHelper.getTimestamp());
        paramHfb.put("sign", RequestHelper.getSign(paramHfb));
        String form = FormHelper.buildForm(HfbConst.RECHARGE_URL, paramHfb);
        //返回汇付宝表单
        return form;
    }

    @Override
    public String notifyCharge(Map<String, Object> returnParamMap) {
        String bindCode = (String) returnParamMap.get("bindCode");
        String resultCode = (String) returnParamMap.get("resultCode");
        BigDecimal chargeAmt = new BigDecimal((String) returnParamMap.get("chargeAmt"));
        String agentBillNo = (String) returnParamMap.get("agentBillNo");
        boolean isSave = transFlowService.isSaveTransFlow(agentBillNo);
        if (isSave) {
            //说明交易已经 是完成的
            log.warn("幂等性返回");
            return "success";
        }
        QueryWrapper<UserInfo> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("bind_code", bindCode);
        UserInfo userInfo = userInfoMapper.selectOne(objectQueryWrapper);
        Long userInfoId = userInfo.getId();
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id", userInfoId);
        UserAccount userAccount = baseMapper.selectOne(userAccountQueryWrapper);
        //更新用户 userAccount
        UpdateWrapper<UserAccount> userAccountUpdateWrapper = new UpdateWrapper<>();
        userAccountUpdateWrapper
                .eq("user_id", userInfoId)
                .setSql("amount = amount + " + chargeAmt + ",freeze_amount=freeze_amount + " + 0);
        baseMapper.update(null, userAccountUpdateWrapper);
        //增加交易流水  幂等性操作 防止多此提交
        //TransFlow 设置了单号唯一索引重复添加会出现错误 从而回滚数据

        TransFlowBO transFlowBO = new TransFlowBO(
                agentBillNo,
                bindCode,
                chargeAmt,
                TransTypeEnum.RECHARGE,
                "充值");
        transFlowService.saveTransFlow(transFlowBO);
        //发送消息 通知完成
        SmsDTO smsDTO = new SmsDTO();
        smsDTO.setMobile(userInfo.getMobile());
        smsDTO.setMessage("充值成功");
        mqService.sendMessage(MQConst.EXCHANGE_TOPIC_SMS, MQConst.ROUTING_SMS_ITEM, smsDTO);

        return "success";
    }

    @Override
    public UserAccount userAccountById(String userId) {
        QueryWrapper<UserAccount> userAccountQueryWrapperQuery = new QueryWrapper<>();
        userAccountQueryWrapperQuery.eq("user_id", userId);
        return baseMapper.selectOne(userAccountQueryWrapperQuery);
    }
}
