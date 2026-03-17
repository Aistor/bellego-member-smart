package com.bellego.controller;

import com.bellego.common.result.PageResult;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.domain.dto.system.LogQueryRequest;
import com.bellego.domain.entity.OperationLog;
import com.bellego.service.OperationLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/logs")
public class LogController {

    private final OperationLogService operationLogService;

    public LogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('log:view')")
    public Result<PageResult<OperationLog>> page(LogQueryRequest request) {
        return ResultBuilder.success(operationLogService.page(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('log:view')")
    public Result<OperationLog> get(@PathVariable String id) {
        return ResultBuilder.success(operationLogService.getById(id));
    }
}

