package com.bellego.controller;

import com.bellego.common.result.PageResult;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.common.util.VoMapper;
import com.bellego.domain.dto.system.LogQueryRequest;
import com.bellego.domain.vo.OperationLogVo;
import com.bellego.service.OperationLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/logs")
public class LogController {
    private final OperationLogService operationLogService;
    private final VoMapper voMapper;

    public LogController(OperationLogService operationLogService, VoMapper voMapper) {
        this.operationLogService = operationLogService;
        this.voMapper = voMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('log:view')")
    public Result<PageResult<OperationLogVo>> page(LogQueryRequest request) {
        return ResultBuilder.success(operationLogService.page(request).map(voMapper::toOperationLogVo));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('log:view')")
    public Result<OperationLogVo> get(@PathVariable String id) {
        return ResultBuilder.success(voMapper.toOperationLogVo(operationLogService.getById(id)));
    }
}