package com.bellego.service;

import com.bellego.common.result.PageResult;
import com.bellego.domain.dto.marketing.CouponIssueRequest;
import com.bellego.domain.dto.marketing.CouponQueryRequest;
import com.bellego.domain.dto.marketing.CouponUpsertRequest;
import com.bellego.domain.entity.Coupon;

public interface CouponService {
    PageResult<Coupon> page(CouponQueryRequest request);
    Coupon getById(String id);
    void create(CouponUpsertRequest request);
    void update(String id, CouponUpsertRequest request);
    void delete(String id);
    void updateStatus(String id, Integer status);
    void issue(String id, CouponIssueRequest request);
}

