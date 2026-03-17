package com.bellego.controller;

import com.bellego.aop.LogOperation;
import com.bellego.common.result.PageResult;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.common.util.VoMapper;
import com.bellego.domain.dto.marketing.MemberCouponQueryRequest;
import com.bellego.domain.vo.MemberCouponVo;
import com.bellego.service.MemberCouponService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/member-coupons")
public class MemberCouponController {
    private final MemberCouponService memberCouponService;
    private final VoMapper voMapper;

    public MemberCouponController(MemberCouponService memberCouponService, VoMapper voMapper) {
        this.memberCouponService = memberCouponService;
        this.voMapper = voMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('memberCoupon:view')")
    public Result<PageResult<MemberCouponVo>> page(MemberCouponQueryRequest request) {
        return ResultBuilder.success(memberCouponService.page(request).map(voMapper::toMemberCouponVo));
    }

    @PutMapping("/{id}/use")
    @PreAuthorize("hasAuthority('memberCoupon:use')")
    @LogOperation(module = "memberCoupon", action = "use coupon")
    public Result<Void> useCoupon(@PathVariable String id) {
        memberCouponService.useCoupon(id);
        return ResultBuilder.success();
    }
}