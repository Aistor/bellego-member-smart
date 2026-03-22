package com.bellego.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.domain.dto.marketing.MemberCouponQueryDto;
import com.bellego.domain.vo.MemberCouponVo;

public interface MemberCouponService {
    IPage<MemberCouponVo> page(MemberCouponQueryDto dto);
    void useCoupon(String id);
}
