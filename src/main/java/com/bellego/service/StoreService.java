package com.bellego.service;

import com.bellego.common.result.PageResult;
import com.bellego.domain.dto.system.StoreQueryRequest;
import com.bellego.domain.dto.system.StoreUpsertRequest;
import com.bellego.domain.entity.Store;

public interface StoreService {
    PageResult<Store> page(StoreQueryRequest request);
    Store getById(String id);
    void create(StoreUpsertRequest request);
    void update(String id, StoreUpsertRequest request);
    void delete(String id);
    void updateStatus(String id, Integer status);
}

