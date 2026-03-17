package com.bellego.controller;

import com.bellego.aop.LogOperation;
import com.bellego.common.result.PageResult;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.domain.dto.marketing.MemberCouponQueryRequest;
import com.bellego.domain.entity.MemberCoupon;
import com.bellego.service.MemberCouponService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/member-coupons")
public class MemberCouponController {

    private final MemberCouponService memberCouponService;

    public MemberCouponController(MemberCouponService memberCouponService) {
        this.memberCouponService = memberCouponService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('memberCoupon:view')")
    public Result<PageResult<MemberCoupon>> page(MemberCouponQueryRequest request) {
        return ResultBuilder.success(memberCouponService.page(request));
    }

    @PutMapping("/{id}/use")
    @PreAuthorize("hasAuthority('memberCoupon:use')")
    @LogOperation(module = "会员优惠券", action = "核销优惠券")
    public Result<Void> useCoupon(@PathVariable String id) {
        memberCouponService.useCoupon(id);
        return ResultBuilder.success();
    }
}

