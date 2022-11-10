package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.core.enums.BorrowerStatusEnum;
import com.atguigu.srb.core.enums.UserBindEnum;
import com.atguigu.srb.core.hfb.FormHelper;
import com.atguigu.srb.core.hfb.HfbConst;
import com.atguigu.srb.core.hfb.RequestHelper;
import com.atguigu.srb.core.mapper.UserInfoMapper;
import com.atguigu.srb.core.pojo.entity.UserBind;
import com.atguigu.srb.core.mapper.UserBindMapper;
import com.atguigu.srb.core.pojo.entity.UserInfo;
import com.atguigu.srb.core.pojo.vo.UserBindVO;
import com.atguigu.srb.core.service.UserBindService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author pp
 * @since 2022-10-28
 */
@Service
public class UserBindImplService extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {
    @Autowired
    UserInfoMapper userInfoMapper;
    @Override
    public String commitBind(UserBindVO userBindVO,String userId) {
        //保存用户绑定信息
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id",userId);
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        //这里要做一个判断 用户可能修改数据多次提交
        if (userBind!=null){

            BeanUtils.copyProperties(userBindVO,userBind);
            baseMapper.updateById(userBind);
        }else {
            //不为空则 将数据保存到数据库中
            userBind = new UserBind();
            BeanUtils.copyProperties(userBindVO,userBind);
            userBind.setUserId(Long.parseLong(userId));
            userBind.setStatus(BorrowerStatusEnum.NO_AUTH.getStatus());
            baseMapper.insert(userBind);
        }
        // 生成汇付宝表单
        Map<String, Object> paramHfb=new HashMap<>();
        paramHfb.put("agentId", HfbConst.AGENT_ID);
        paramHfb.put("agentUserId",userId);
        paramHfb.put("idCard",userBindVO.getIdCard());
        paramHfb.put("personalName", userBindVO.getName());
        paramHfb.put("bankType", userBindVO.getBankType());
        paramHfb.put("bankNo", userBindVO.getBankNo());
        paramHfb.put("mobile", userBindVO.getMobile());
        //汇付宝绑定成功后返回的地址
        paramHfb.put("returnUrl",HfbConst.USERBIND_RETURN_URL);
        //回调结果 需要返回success
        paramHfb.put("notifyUrl", HfbConst.USERBIND_NOTIFY_URL);
        paramHfb.put("timestamp", RequestHelper.getTimestamp());
        paramHfb.put("sign", RequestHelper.getSign(paramHfb));

        String form = FormHelper.buildForm(HfbConst.USERBIND_URL, paramHfb);

        return form;
    }

    @Override
    public void commitBindNotify(Map<String, Object> returnParamMap) {
        String userId = (String) returnParamMap.get("agentUserId");
        String bindCode = (String) returnParamMap.get("bindCode");

        //修改user bind 的bindCode和status
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id",userId);
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
        userBind.setBindCode(bindCode);
        baseMapper.updateById(userBind);
        //修改userinfo
        UserInfo userInfo = userInfoMapper.selectById(userId);
        userInfo.setName(userBind.getName());
        userInfo.setNickName(userBind.getName());
        userInfo.setBindCode(bindCode);
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
        userInfo.setIdCard(userBind.getIdCard());
        userInfoMapper.updateById(userInfo);

    }
}
