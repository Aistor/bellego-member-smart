package com.bellego.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.domain.dto.system.LogQueryDto;
import com.bellego.domain.entity.OperationLog;

public interface OperationLogService {
    IPage<OperationLog> page(LogQueryDto dto);
    OperationLog getById(String id);
    void saveAsync(OperationLog operationLog);
}