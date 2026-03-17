package com.bellego.service;

import com.bellego.common.result.PageResult;
import com.bellego.domain.dto.marketing.MemberCouponQueryRequest;
import com.bellego.domain.entity.MemberCoupon;

public interface MemberCouponService {
    PageResult<MemberCoupon> page(MemberCouponQueryRequest request);
    void useCoupon(String id);
}

