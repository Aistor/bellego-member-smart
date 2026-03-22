package com.bellego.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.aop.LogOperation;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.domain.dto.marketing.MemberCouponQueryDto;
import com.bellego.domain.vo.MemberCouponVo;
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
    public Result<IPage<MemberCouponVo>> page(MemberCouponQueryDto dto) {
        return ResultBuilder.success(memberCouponService.page(dto));
    }

    @PutMapping("/{id}/use")
    @PreAuthorize("hasAuthority('memberCoupon:use')")
    @LogOperation(module = "memberCoupon", action = "use coupon")
    public Result<Void> useCoupon(@PathVariable String id) {
        memberCouponService.useCoupon(id);
        return ResultBuilder.success();
    }
}
