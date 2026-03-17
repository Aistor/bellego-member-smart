package com.bellego.controller;

import com.bellego.aop.LogOperation;
import com.bellego.common.result.PageResult;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.domain.dto.member.ConsumptionCreateRequest;
import com.bellego.domain.dto.member.ConsumptionQueryRequest;
import com.bellego.domain.entity.MemberConsumption;
import com.bellego.service.ConsumptionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/consumptions")
public class ConsumptionController {

    private final ConsumptionService consumptionService;

    public ConsumptionController(ConsumptionService consumptionService) {
        this.consumptionService = consumptionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('consumption:view')")
    public Result<PageResult<MemberConsumption>> page(ConsumptionQueryRequest request) {
        return ResultBuilder.success(consumptionService.page(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('consumption:view')")
    public Result<MemberConsumption> get(@PathVariable String id) {
        return ResultBuilder.success(consumptionService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('consumption:add')")
    @LogOperation(module = "消费记录", action = "新增消费")
    public Result<Void> create(@Valid @RequestBody ConsumptionCreateRequest request) {
        consumptionService.create(request);
        return ResultBuilder.success();
    }

    @PostMapping("/import")
    @PreAuthorize("hasAuthority('consumption:import')")
    @LogOperation(module = "消费记录", action = "导入消费")
    public Result<Void> importCsv(@RequestPart MultipartFile file) {
        consumptionService.importCsv(file);
        return ResultBuilder.success();
    }
}

