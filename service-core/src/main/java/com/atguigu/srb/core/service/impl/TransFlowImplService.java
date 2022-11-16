package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.core.mapper.UserInfoMapper;
import com.atguigu.srb.core.pojo.BO.TransFlowBO;
import com.atguigu.srb.core.pojo.entity.TransFlow;
import com.atguigu.srb.core.mapper.TransFlowMapper;
import com.atguigu.srb.core.pojo.entity.UserInfo;
import com.atguigu.srb.core.service.TransFlowService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 交易流水表 服务实现类
 * </p>
 *
 * @author pp
 * @since 2022-10-28
 */
@Service
public class TransFlowImplService extends ServiceImpl<TransFlowMapper, TransFlow> implements TransFlowService {
    @Autowired
    UserInfoMapper userInfoMapper;
    @Override
    public void saveTransFlow(TransFlowBO transFlowBO) {
        String bindCode = transFlowBO.getBindCode();
        QueryWrapper<UserInfo> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("bind_code",bindCode);
        UserInfo userInfo = userInfoMapper.selectOne(objectQueryWrapper);
        //设置TransFlow
        TransFlow transFlow = new TransFlow();
        transFlow.setTransNo(transFlowBO.getAgentBillNo());
        transFlow.setTransType(transFlow.getTransType());
        transFlow.setTransAmount(transFlowBO.getAmount());
        transFlow.setTransTypeName(transFlowBO.getTransTypeEnum().getTransTypeName());
        transFlow.setUserId(userInfo.getId());
        transFlow.setUserName(userInfo.getName());
        transFlow.setMemo(transFlowBO.getMemo());
        baseMapper.insert(transFlow);

    }

    @Override
    //判断交易单号是否存在
    public boolean isSaveTransFlow(String agentBillNo) {
        QueryWrapper<TransFlow> transFlowQueryWrapper = new QueryWrapper<>();
        transFlowQueryWrapper.eq("trans_no",agentBillNo);
        Integer count = baseMapper.selectCount(transFlowQueryWrapper);
        if (count>0){
            return true;
        }
        return false;
    }
}
