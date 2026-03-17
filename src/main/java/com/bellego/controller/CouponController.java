package com.bellego.controller;

import com.bellego.aop.LogOperation;
import com.bellego.common.result.PageResult;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.domain.dto.common.StatusUpdateRequest;
import com.bellego.domain.dto.marketing.CouponIssueRequest;
import com.bellego.domain.dto.marketing.CouponQueryRequest;
import com.bellego.domain.dto.marketing.CouponUpsertRequest;
import com.bellego.domain.entity.Coupon;
import com.bellego.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('coupon:view')")
    public Result<PageResult<Coupon>> page(CouponQueryRequest request) {
        return ResultBuilder.success(couponService.page(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('coupon:view')")
    public Result<Coupon> get(@PathVariable String id) {
        return ResultBuilder.success(couponService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('coupon:add')")
    @LogOperation(module = "优惠券", action = "新增优惠券")
    public Result<Void> create(@Valid @RequestBody CouponUpsertRequest request) {
        couponService.create(request);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('coupon:edit')")
    @LogOperation(module = "优惠券", action = "修改优惠券")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody CouponUpsertRequest request) {
        couponService.update(id, request);
        return ResultBuilder.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('coupon:delete')")
    @LogOperation(module = "优惠券", action = "删除优惠券")
    public Result<Void> delete(@PathVariable String id) {
        couponService.delete(id);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('coupon:edit')")
    @LogOperation(module = "优惠券", action = "修改优惠券状态")
    public Result<Void> updateStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest request) {
        couponService.updateStatus(id, request.getStatus());
        return ResultBuilder.success();
    }

    @PostMapping("/{id}/issue")
    @PreAuthorize("hasAuthority('coupon:issue')")
    @LogOperation(module = "优惠券", action = "发放优惠券")
    public Result<Void> issue(@PathVariable String id, @RequestBody CouponIssueRequest request) {
        couponService.issue(id, request);
        return ResultBuilder.success();
    }
}

