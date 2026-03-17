package com.bellego.controller;

import com.bellego.aop.LogOperation;
import com.bellego.common.result.PageResult;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.domain.dto.common.StatusUpdateRequest;
import com.bellego.domain.dto.marketing.PointDetailQueryRequest;
import com.bellego.domain.dto.marketing.PointRuleUpsertRequest;
import com.bellego.domain.entity.PointDetail;
import com.bellego.domain.entity.PointRule;
import com.bellego.service.PointDetailService;
import com.bellego.service.PointRuleService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class PointController {

    private final PointRuleService pointRuleService;
    private final PointDetailService pointDetailService;

    public PointController(PointRuleService pointRuleService, PointDetailService pointDetailService) {
        this.pointRuleService = pointRuleService;
        this.pointDetailService = pointDetailService;
    }

    @GetMapping("/api/v1/point-rules")
    @PreAuthorize("hasAuthority('pointRule:view')")
    public Result<List<PointRule>> listRules() {
        return ResultBuilder.success(pointRuleService.list());
    }

    @PostMapping("/api/v1/point-rules")
    @PreAuthorize("hasAuthority('pointRule:add')")
    @LogOperation(module = "积分规则", action = "新增积分规则")
    public Result<Void> createRule(@Valid @RequestBody PointRuleUpsertRequest request) {
        pointRuleService.create(request);
        return ResultBuilder.success();
    }

    @PutMapping("/api/v1/point-rules/{id}")
    @PreAuthorize("hasAuthority('pointRule:edit')")
    @LogOperation(module = "积分规则", action = "修改积分规则")
    public Result<Void> updateRule(@PathVariable String id, @Valid @RequestBody PointRuleUpsertRequest request) {
        pointRuleService.update(id, request);
        return ResultBuilder.success();
    }

    @DeleteMapping("/api/v1/point-rules/{id}")
    @PreAuthorize("hasAuthority('pointRule:delete')")
    @LogOperation(module = "积分规则", action = "删除积分规则")
    public Result<Void> deleteRule(@PathVariable String id) {
        pointRuleService.delete(id);
        return ResultBuilder.success();
    }

    @PutMapping("/api/v1/point-rules/{id}/status")
    @PreAuthorize("hasAuthority('pointRule:edit')")
    @LogOperation(module = "积分规则", action = "修改积分规则状态")
    public Result<Void> updateRuleStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest request) {
        pointRuleService.updateStatus(id, request.getStatus());
        return ResultBuilder.success();
    }

    @GetMapping("/api/v1/point-details")
    @PreAuthorize("hasAuthority('pointDetail:view')")
    public Result<PageResult<PointDetail>> pageDetails(PointDetailQueryRequest request) {
        return ResultBuilder.success(pointDetailService.page(request));
    }

    @PostMapping("/api/v1/point-details/import")
    @PreAuthorize("hasAuthority('pointDetail:import')")
    @LogOperation(module = "积分明细", action = "导入积分明细")
    public Result<Void> importDetails(@RequestPart MultipartFile file) {
        pointDetailService.importCsv(file);
        return ResultBuilder.success();
    }
}

