package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.common.exception.BusinessException;
import com.bellego.domain.dto.marketing.MemberCouponQueryDto;
import com.bellego.domain.entity.Coupon;
import com.bellego.domain.entity.Member;
import com.bellego.domain.entity.MemberCoupon;
import com.bellego.domain.vo.MemberCouponVo;
import com.bellego.mapper.CouponMapper;
import com.bellego.mapper.MemberCouponMapper;
import com.bellego.mapper.MemberMapper;
import com.bellego.service.MemberCouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 会员优惠券服务实现
 */
@Slf4j
@Service
public class MemberCouponServiceImpl implements MemberCouponService {
    private final MemberCouponMapper memberCouponMapper;
    private final MemberMapper memberMapper;
    private final CouponMapper couponMapper;

    public MemberCouponServiceImpl(MemberCouponMapper memberCouponMapper, MemberMapper memberMapper, CouponMapper couponMapper) {
        this.memberCouponMapper = memberCouponMapper;
        this.memberMapper = memberMapper;
        this.couponMapper = couponMapper;
    }

    @Override
    public IPage<MemberCouponVo> page(MemberCouponQueryDto dto) {
        log.info("开始分页查询会员优惠券，memberId={}, status={}", dto.getMemberId(), dto.getStatus());
        LambdaQueryWrapper<MemberCoupon> wrapper = new LambdaQueryWrapper<MemberCoupon>()
                .eq(dto.getMemberId() != null && !dto.getMemberId().isBlank(), MemberCoupon::getMemberId, dto.getMemberId())
                .eq(dto.getStatus() != null, MemberCoupon::getStatus, dto.getStatus())
                .orderByDesc(MemberCoupon::getReceiveTime);
        Page<MemberCoupon> page = memberCouponMapper.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        List<MemberCoupon> records = page.getRecords();
        Map<String, String> memberNameMap = records.isEmpty()
                ? Collections.emptyMap()
                : memberMapper.selectList(new LambdaQueryWrapper<Member>().in(Member::getId, records.stream().map(MemberCoupon::getMemberId).distinct().toList()))
                        .stream()
                        .collect(Collectors.toMap(Member::getId, Member::getName, (left, right) -> left));
        Map<String, String> couponNameMap = records.isEmpty()
                ? Collections.emptyMap()
                : couponMapper.selectList(new LambdaQueryWrapper<Coupon>().in(Coupon::getId, records.stream().map(MemberCoupon::getCouponId).distinct().toList()))
                        .stream()
                        .collect(Collectors.toMap(Coupon::getId, Coupon::getName, (left, right) -> left));

        Page<MemberCouponVo> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(records.stream().map(item -> {
            MemberCouponVo vo = new MemberCouponVo();
            vo.setId(item.getId());
            vo.setMemberId(item.getMemberId());
            vo.setMemberName(memberNameMap.get(item.getMemberId()));
            vo.setCouponId(item.getCouponId());
            vo.setCouponName(couponNameMap.get(item.getCouponId()));
            vo.setCode(item.getCode());
            vo.setStatus(item.getStatus());
            vo.setReceiveTime(item.getReceiveTime());
            vo.setUseTime(item.getUseTime());
            vo.setExpireTime(item.getExpireTime());
            return vo;
        }).toList());
        return result;
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
