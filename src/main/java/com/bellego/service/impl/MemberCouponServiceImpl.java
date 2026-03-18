package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.common.exception.BusinessException;
import com.bellego.domain.dto.marketing.MemberCouponQueryDto;
import com.bellego.domain.entity.MemberCoupon;
import com.bellego.mapper.MemberCouponMapper;
import com.bellego.service.MemberCouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 会员优惠券服务实现
 */
@Slf4j
@Service
public class MemberCouponServiceImpl implements MemberCouponService {
    private final MemberCouponMapper memberCouponMapper;

    public MemberCouponServiceImpl(MemberCouponMapper memberCouponMapper) {
        this.memberCouponMapper = memberCouponMapper;
    }

    @Override
    public IPage<MemberCoupon> page(MemberCouponQueryDto dto) {
        log.info("开始分页查询会员优惠券，memberId={}, status={}", dto.getMemberId(), dto.getStatus());
        LambdaQueryWrapper<MemberCoupon> wrapper = new LambdaQueryWrapper<MemberCoupon>().eq(dto.getMemberId() != null && !dto.getMemberId().isBlank(), MemberCoupon::getMemberId, dto.getMemberId()).eq(dto.getStatus() != null, MemberCoupon::getStatus, dto.getStatus()).orderByDesc(MemberCoupon::getReceiveTime);
        return memberCouponMapper.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
    }

    @Override
    public void useCoupon(String id) {
        log.info("开始核销会员优惠券，id={}", id);
        MemberCoupon coupon = memberCouponMapper.selectById(id);
        if (coupon == null) {
            log.error("核销失败，会员优惠券不存在，id={}", id);
            throw new BusinessException("会员优惠券不存在");
        }
        if (coupon.getStatus() != 0) {
            log.error("核销失败，优惠券状态不可用，id={}, status={}", id, coupon.getStatus());
            throw new BusinessException("优惠券状态不可用");
        }
        if (coupon.getExpireTime() != null && coupon.getExpireTime().before(new Date())) {
            log.error("核销失败，优惠券已过期，id={}", id);
            throw new BusinessException("优惠券已过期");
        }
        coupon.setStatus(1);
        coupon.setUseTime(new Date());
        memberCouponMapper.updateById(coupon);
    }
}