package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.common.util.R;
import com.atguigu.srb.core.enums.BorrowAuthEnum;
import com.atguigu.srb.core.enums.BorrowInfoStatusEnum;
import com.atguigu.srb.core.enums.BorrowerStatusEnum;
import com.atguigu.srb.core.mapper.BorrowerMapper;
import com.atguigu.srb.core.mapper.IntegralGradeMapper;
import com.atguigu.srb.core.mapper.UserInfoMapper;
import com.atguigu.srb.core.pojo.entity.*;
import com.atguigu.srb.core.mapper.BorrowInfoMapper;
import com.atguigu.srb.core.pojo.vo.BorrowInfoApprovalVO;
import com.atguigu.srb.core.pojo.vo.BorrowerDetailVO;
import com.atguigu.srb.core.service.BorrowInfoService;
import com.atguigu.srb.core.service.DictService;
import com.atguigu.srb.core.service.LendService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author pp
 * @since 2022-10-28
 */
@Service
public class BorrowInfoImplService extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {

    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    IntegralGradeMapper integralGradeMapper;
    @Autowired
    DictService dictService;
    @Autowired
    BorrowerMapper borrowerMapper;
    @Autowired
    LendService lendService;


    @Override
    public Integer getBorrowInfoStatusByUserId(String userId) {
        QueryWrapper<BorrowInfo> borrowInfoQueryWrapper = new QueryWrapper<>();
        borrowInfoQueryWrapper.eq("user_id",userId);
        BorrowInfo borrowInfo = baseMapper.selectOne(borrowInfoQueryWrapper);
        if (borrowInfo == null) {
            return 0;
        } else if (borrowInfo.getStatus() == null) {
            return 0;
        }

        return borrowInfo.getStatus();
    }

    /*
    * lt：less than 小于
      le：less than or equal to 小于等于
      eq：equal to 等于
      ne：not equal to 不等于
      ge：greater than or equal to 大于等于
      gt：greater than 大于
    * */
    @Override
    public Long getBorrowAmount(String userId) {
        //贷款额度
        UserInfo userInfo = userInfoMapper.selectById(userId);
        //获取总积分
        Integer integral = userInfo.getIntegral();
        //根据积分等级表区间查找额度
        QueryWrapper<IntegralGrade> integralGradeQueryWrapper = new QueryWrapper<>();
        integralGradeQueryWrapper.le("integral_start",integral)
                .gt("integral_end",integral);
        List<IntegralGrade> integralGrades = integralGradeMapper.selectList(integralGradeQueryWrapper);
        //如果区间有重复 会查出多条记录 那么我们就取出额度最大的
        if (integralGrades!=null&&integralGrades.size()==1){
            return integralGrades.get(0).getBorrowAmount().longValue();
        }else if (integralGrades==null)
            return 0l;
        List<IntegralGrade> collect = integralGrades.stream()
                .sorted(Comparator.comparing(IntegralGrade::getBorrowAmount).reversed())
                .collect(Collectors.toList());
        return collect.get(0).getBorrowAmount().longValue();
    }

    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, String userId) {
        //获取userInfo的用户数据
        UserInfo userInfo = userInfoMapper.selectById(userId);

        //如果我要借两次 怎么判断第二次是否还有额度 判断借款额度是否足够
        //TODO
        Long borrowAmount = this.getBorrowAmount(userId);

        borrowInfo.setUserId(Long.parseLong(userId));
        //设置借款状态
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());
        //传递过来的是12 不是百分数 需要转换一下
        borrowInfo.setBorrowYearRate(borrowInfo.getBorrowYearRate().divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP));
        baseMapper.insert(borrowInfo);
    }

    @Override
    public List<BorrowInfo> getList() {
        QueryWrapper<BorrowInfo> borrowInfoQueryWrapper = new QueryWrapper<>();

        List<BorrowInfo> borrowInfos = baseMapper.selectBorrowInfoList();
        for(BorrowInfo borrowInfo:borrowInfos){
            Long userId = borrowInfo.getUserId();
            UserInfo userInfo = userInfoMapper.selectById(userId);
            borrowInfo.setName(userInfo.getNickName());
            borrowInfo.setMobile(userInfo.getMobile());
            Map<String, Object> param = new HashMap<>();
            String returnMethod = dictService.getNameAndDictCodeAndId("returnMethod", borrowInfo.getReturnMethod());
            String moneyUse = dictService.getNameAndDictCodeAndId("moneyUse", borrowInfo.getMoneyUse());
            param.put("returnMethod",returnMethod);
            param.put("moneyUse",moneyUse);
            param.put("status",BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus()));
            borrowInfo.setParam(param);
        }
        return borrowInfos;
    }

    @Override
    public Map<String,Object> getBorrowInfoDetail(Long borrowId) {
        BorrowInfo borrowInfo = baseMapper.selectById(borrowId);
        Long userId = borrowInfo.getUserId();
        UserInfo userInfo = userInfoMapper.selectById(userId);
        borrowInfo.setName(userInfo.getNickName());
        borrowInfo.setMobile(userInfo.getMobile());
        Map<String, Object> param = new HashMap<>();
        String returnMethod = dictService.getNameAndDictCodeAndId("returnMethod", borrowInfo.getReturnMethod());
        String moneyUse = dictService.getNameAndDictCodeAndId("moneyUse", borrowInfo.getMoneyUse());
        param.put("returnMethod",returnMethod);
        param.put("moneyUse",moneyUse);
        param.put("status", BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus()));
        borrowInfo.setParam(param);
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

        Map<String, Object> borrowInfoDetailMap = new HashMap<>();
        borrowInfoDetailMap.put("borrowInfo",borrowInfo);
        borrowInfoDetailMap.put("borrower",borrowerDetailVO);

        return borrowInfoDetailMap;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approval(BorrowInfoApprovalVO borrowInfoApprovalVO) {
        //修改借款信息状态  审核通过 审核不通过
        Long borrowInfoId = borrowInfoApprovalVO.getId();
        BorrowInfo borrowInfo = baseMapper.selectById(borrowInfoId);
        borrowInfo.setStatus(borrowInfoApprovalVO.getStatus());
        baseMapper.updateById(borrowInfo);
        //审核通过创建标的
        if (borrowInfoApprovalVO.getStatus().intValue()==BorrowInfoStatusEnum.CHECK_OK.getStatus()){
            lendService.creatLend(borrowInfoApprovalVO,borrowInfo);
        }

    }


}
