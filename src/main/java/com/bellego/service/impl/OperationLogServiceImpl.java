package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.domain.dto.system.LogQueryDto;
import com.bellego.domain.entity.OperationLog;
import com.bellego.mapper.OperationLogMapper;
import com.bellego.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务实现
 */
@Slf4j
@Service
public class OperationLogServiceImpl implements OperationLogService {
    private final OperationLogMapper operationLogMapper;

    public OperationLogServiceImpl(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public IPage<OperationLog> page(LogQueryDto dto) {
        log.info("开始分页查询操作日志，操作人={}, 模块={}", dto.getOperatorName(), dto.getModule());
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<OperationLog>().like(dto.getOperatorName() != null && !dto.getOperatorName().isBlank(), OperationLog::getOperatorName, dto.getOperatorName()).like(dto.getModule() != null && !dto.getModule().isBlank(), OperationLog::getModule, dto.getModule()).orderByDesc(OperationLog::getCreateTime);
        return operationLogMapper.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
    }

    @Override
    public OperationLog getById(String id) {
        return operationLogMapper.selectById(id);
    }

    @Override
    @Async
    public void saveAsync(OperationLog operationLog) {
        log.info("异步写入操作日志，module={}, operation={}", operationLog.getModule(), operationLog.getOperation());
        operationLogMapper.insert(operationLog);
    }
}