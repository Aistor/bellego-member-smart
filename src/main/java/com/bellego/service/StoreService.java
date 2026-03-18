package com.bellego.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.domain.dto.system.StoreQueryDto;
import com.bellego.domain.dto.system.StoreUpsertDto;
import com.bellego.domain.entity.Store;

public interface StoreService {
    IPage<Store> page(StoreQueryDto dto);
    Store getById(String id);
    void create(StoreUpsertDto dto);
    void update(String id, StoreUpsertDto dto);
    void delete(String id);
    void updateStatus(String id, Integer status);
}