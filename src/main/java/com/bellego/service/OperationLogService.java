package com.bellego.service;

import com.bellego.common.result.PageResult;
import com.bellego.domain.dto.system.LogQueryRequest;
import com.bellego.domain.entity.OperationLog;

public interface OperationLogService {
    PageResult<OperationLog> page(LogQueryRequest request);
    OperationLog getById(String id);
    void saveAsync(OperationLog operationLog);
}

