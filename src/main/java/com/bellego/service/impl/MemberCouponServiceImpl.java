package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.common.exception.BusinessException;
import com.bellego.common.result.PageResult;
import com.bellego.domain.dto.marketing.MemberCouponQueryRequest;
import com.bellego.domain.entity.MemberCoupon;
import com.bellego.mapper.MemberCouponMapper;
import com.bellego.service.MemberCouponService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MemberCouponServiceImpl implements MemberCouponService {

    private final MemberCouponMapper memberCouponMapper;

    public MemberCouponServiceImpl(MemberCouponMapper memberCouponMapper) {
        this.memberCouponMapper = memberCouponMapper;
    }

    @Override
    public PageResult<MemberCoupon> page(MemberCouponQueryRequest request) {
        LambdaQueryWrapper<MemberCoupon> wrapper = new LambdaQueryWrapper<MemberCoupon>()
                .eq(request.getMemberId() != null && !request.getMemberId().isBlank(), MemberCoupon::getMemberId, request.getMemberId())
                .eq(request.getStatus() != null, MemberCoupon::getStatus, request.getStatus())
                .orderByDesc(MemberCoupon::getReceiveTime);
        Page<MemberCoupon> page = memberCouponMapper.selectPage(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
        return PageResult.of(page);
    }

    @Override
    public void useCoupon(String id) {
        MemberCoupon memberCoupon = memberCouponMapper.selectById(id);
        if (memberCoupon == null) {
            throw new BusinessException("会员优惠券不存在");
        }
        if (memberCoupon.getStatus() != 0) {
            throw new BusinessException("优惠券状态不可用");
        }
        if (memberCoupon.getExpireTime() != null && memberCoupon.getExpireTime().before(new Date())) {
            throw new BusinessException("优惠券已过期");
        }
        memberCoupon.setStatus(1);
        memberCoupon.setUseTime(new Date());
        memberCouponMapper.updateById(memberCoupon);
    }
}

