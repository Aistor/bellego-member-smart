package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.common.exception.BusinessException;
import com.bellego.domain.dto.marketing.CouponIssueDto;
import com.bellego.domain.dto.marketing.CouponQueryDto;
import com.bellego.domain.dto.marketing.CouponUpsertDto;
import com.bellego.domain.entity.Coupon;
import com.bellego.domain.entity.Member;
import com.bellego.domain.entity.MemberCoupon;
import com.bellego.mapper.CouponMapper;
import com.bellego.mapper.MemberCouponMapper;
import com.bellego.mapper.MemberMapper;
import com.bellego.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 优惠券服务实现
 */
@Slf4j
@Service
public class CouponServiceImpl implements CouponService {
    private final CouponMapper couponMapper;
    private final MemberMapper memberMapper;
    private final MemberCouponMapper memberCouponMapper;

    public CouponServiceImpl(CouponMapper couponMapper, MemberMapper memberMapper, MemberCouponMapper memberCouponMapper) {
        this.couponMapper = couponMapper;
        this.memberMapper = memberMapper;
        this.memberCouponMapper = memberCouponMapper;
    }

    @Override
    public IPage<Coupon> page(CouponQueryDto dto) {
        log.info("开始分页查询优惠券，关键字={}, 状态={}", dto.getKeyword(), dto.getStatus());
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<Coupon>()
                .eq(dto.getStatus() != null, Coupon::getStatus, dto.getStatus())
                .and(dto.getKeyword() != null && !dto.getKeyword().isBlank(), q -> q.like(Coupon::getName, dto.getKeyword()))
                .orderByDesc(Coupon::getCreateTime);
        return couponMapper.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
    }

    @Override
    public Coupon getById(String id) {
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            log.error("查询优惠券失败，优惠券不存在，id={}", id);
            throw new BusinessException("优惠券不存在");
        }
        return coupon;
    }

    @Override
    public void create(CouponUpsertDto dto) {
        log.info("开始新增优惠券，名称={}", dto.getName());
        Coupon coupon = new Coupon();
        copy(dto, coupon);
        coupon.setTotalIssued(0);
        coupon.setCreateTime(new Date());
        couponMapper.insert(coupon);
    }

    @Override
    public void update(String id, CouponUpsertDto dto) {
        log.info("开始修改优惠券，id={}", id);
        Coupon coupon = getById(id);
        copy(dto, coupon);
        couponMapper.updateById(coupon);
    }

    @Override
    public void delete(String id) {
        log.info("开始删除优惠券，id={}", id);
        getById(id);
        couponMapper.deleteById(id);
    }

    @Override
    public void updateStatus(String id, Integer status) {
        log.info("开始修改优惠券状态，id={}, status={}", id, status);
        Coupon coupon = getById(id);
        coupon.setStatus(status);
        couponMapper.updateById(coupon);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void issue(String id, CouponIssueDto dto) {
        Coupon coupon = getById(id);
        List<String> memberIds = Boolean.TRUE.equals(dto.getIssueAll())
                ? memberMapper.selectList(new LambdaQueryWrapper<Member>().eq(Member::getStatus, 1)).stream().map(Member::getId).toList()
                : dto.getMemberIds();
        if (memberIds == null || memberIds.isEmpty()) {
            log.error("发放优惠券失败，会员列表为空，couponId={}", id);
            throw new BusinessException("请选择发放会员");
        }
        if (coupon.getStock() < memberIds.size()) {
            log.error("发放优惠券失败，库存不足，couponId={}, stock={}, need={}", id, coupon.getStock(), memberIds.size());
            throw new BusinessException("优惠券库存不足");
        }
        log.info("开始发放优惠券，couponId={}, memberCount={}", id, memberIds.size());
        Date now = new Date();
        for (String memberId : memberIds) {
            MemberCoupon memberCoupon = new MemberCoupon();
            memberCoupon.setMemberId(memberId);
            memberCoupon.setCouponId(coupon.getId());
            memberCoupon.setCode(UUID.randomUUID().toString().replace("-", ""));
            memberCoupon.setStatus(0);
            memberCoupon.setReceiveTime(now);
            memberCoupon.setExpireTime(coupon.getEndTime());
            memberCouponMapper.insert(memberCoupon);
        }
        coupon.setStock(coupon.getStock() - memberIds.size());
        coupon.setTotalIssued((coupon.getTotalIssued() == null ? 0 : coupon.getTotalIssued()) + memberIds.size());
        couponMapper.updateById(coupon);
    }

    private void copy(CouponUpsertDto dto, Coupon coupon) {
        coupon.setName(dto.getName());
        coupon.setType(dto.getType());
        coupon.setCouponValue(dto.getCouponValue());
        coupon.setUseCondition(dto.getUseCondition());
        coupon.setStock(dto.getStock());
        coupon.setStartTime(dto.getStartTime());
        coupon.setEndTime(dto.getEndTime());
        coupon.setStatus(dto.getStatus());
    }
}