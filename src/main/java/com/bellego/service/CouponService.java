package com.bellego.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.domain.dto.marketing.CouponIssueDto;
import com.bellego.domain.dto.marketing.CouponQueryDto;
import com.bellego.domain.dto.marketing.CouponUpsertDto;
import com.bellego.domain.entity.Coupon;

public interface CouponService {
    IPage<Coupon> page(CouponQueryDto dto);
    Coupon getById(String id);
    void create(CouponUpsertDto dto);
    void update(String id, CouponUpsertDto dto);
    void delete(String id);
    void updateStatus(String id, Integer status);
    void issue(String id, CouponIssueDto dto);
}