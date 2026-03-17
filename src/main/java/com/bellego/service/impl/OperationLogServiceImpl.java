package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.common.result.PageResult;
import com.bellego.domain.dto.system.LogQueryRequest;
import com.bellego.domain.entity.OperationLog;
import com.bellego.mapper.OperationLogMapper;
import com.bellego.service.OperationLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    public OperationLogServiceImpl(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public PageResult<OperationLog> page(LogQueryRequest request) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<OperationLog>()
                .like(request.getOperatorName() != null && !request.getOperatorName().isBlank(), OperationLog::getOperatorName, request.getOperatorName())
                .like(request.getModule() != null && !request.getModule().isBlank(), OperationLog::getModule, request.getModule())
                .orderByDesc(OperationLog::getCreateTime);
        Page<OperationLog> page = operationLogMapper.selectPage(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
        return PageResult.of(page);
    }

    @Override
    public OperationLog getById(String id) {
        return operationLogMapper.selectById(id);
    }

    @Override
    @Async
    public void saveAsync(OperationLog operationLog) {
        operationLogMapper.insert(operationLog);
    }
}

