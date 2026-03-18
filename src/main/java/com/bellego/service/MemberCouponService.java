package com.bellego.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.domain.dto.marketing.MemberCouponQueryDto;
import com.bellego.domain.entity.MemberCoupon;

public interface MemberCouponService {
    IPage<MemberCoupon> page(MemberCouponQueryDto dto);
    void useCoupon(String id);
}