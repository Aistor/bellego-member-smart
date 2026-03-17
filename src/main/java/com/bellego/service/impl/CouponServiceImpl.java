package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.common.exception.BusinessException;
import com.bellego.common.result.PageResult;
import com.bellego.domain.dto.marketing.CouponIssueRequest;
import com.bellego.domain.dto.marketing.CouponQueryRequest;
import com.bellego.domain.dto.marketing.CouponUpsertRequest;
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
    public PageResult<Coupon> page(CouponQueryRequest request) {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<Coupon>().eq(request.getStatus() != null, Coupon::getStatus, request.getStatus()).and(request.getKeyword() != null && !request.getKeyword().isBlank(), q -> q.like(Coupon::getName, request.getKeyword())).orderByDesc(Coupon::getCreateTime);
        return PageResult.of(couponMapper.selectPage(new Page<>(request.getPageNum(), request.getPageSize()), wrapper));
    }

    @Override
    public Coupon getById(String id) {
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) throw new BusinessException("Coupon not found");
        return coupon;
    }

    @Override
    public void create(CouponUpsertRequest request) {
        log.info("Creating coupon, name={}", request.getName());
        Coupon coupon = new Coupon();
        copy(request, coupon);
        coupon.setTotalIssued(0);
        coupon.setCreateTime(new Date());
        couponMapper.insert(coupon);
    }

    @Override
    public void update(String id, CouponUpsertRequest request) {
        log.info("Updating coupon, id={}", id);
        Coupon coupon = getById(id);
        copy(request, coupon);
        couponMapper.updateById(coupon);
    }

    @Override
    public void delete(String id) {
        log.info("Deleting coupon, id={}", id);
        getById(id);
        couponMapper.deleteById(id);
    }

    @Override
    public void updateStatus(String id, Integer status) {
        log.info("Updating coupon status, id={}, status={}", id, status);
        Coupon coupon = getById(id);
        coupon.setStatus(status);
        couponMapper.updateById(coupon);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void issue(String id, CouponIssueRequest request) {
        Coupon coupon = getById(id);
        List<String> memberIds = Boolean.TRUE.equals(request.getIssueAll()) ? memberMapper.selectList(new LambdaQueryWrapper<Member>().eq(Member::getStatus, 1)).stream().map(Member::getId).toList() : request.getMemberIds();
        if (memberIds == null || memberIds.isEmpty()) throw new BusinessException("Member list is empty");
        if (coupon.getStock() < memberIds.size()) throw new BusinessException("Coupon stock is insufficient");
        log.info("Issuing coupon, couponId={}, receiverCount={}", id, memberIds.size());
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

    private void copy(CouponUpsertRequest request, Coupon coupon) {
        coupon.setName(request.getName());
        coupon.setType(request.getType());
        coupon.setCouponValue(request.getCouponValue());
        coupon.setUseCondition(request.getUseCondition());
        coupon.setStock(request.getStock());
        coupon.setStartTime(request.getStartTime());
        coupon.setEndTime(request.getEndTime());
        coupon.setStatus(request.getStatus());
    }
}