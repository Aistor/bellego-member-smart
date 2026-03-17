package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.common.exception.BusinessException;
import com.bellego.common.result.PageResult;
import com.bellego.domain.dto.system.StoreQueryRequest;
import com.bellego.domain.dto.system.StoreUpsertRequest;
import com.bellego.domain.entity.Store;
import com.bellego.mapper.StoreMapper;
import com.bellego.service.StoreService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreMapper storeMapper;

    public StoreServiceImpl(StoreMapper storeMapper) {
        this.storeMapper = storeMapper;
    }

    @Override
    public PageResult<Store> page(StoreQueryRequest request) {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<Store>()
                .eq(request.getStatus() != null, Store::getStatus, request.getStatus())
                .and(request.getKeyword() != null && !request.getKeyword().isBlank(), q -> q.like(Store::getName, request.getKeyword()).or().like(Store::getCode, request.getKeyword()))
                .orderByDesc(Store::getCreateTime);
        Page<Store> page = storeMapper.selectPage(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
        return PageResult.of(page);
    }

    @Override
    public Store getById(String id) {
        Store store = storeMapper.selectById(id);
        if (store == null) {
            throw new BusinessException("门店不存在");
        }
        return store;
    }

    @Override
    public void create(StoreUpsertRequest request) {
        Store store = new Store();
        copy(request, store);
        store.setCreateTime(new Date());
        storeMapper.insert(store);
    }

    @Override
    public void update(String id, StoreUpsertRequest request) {
        Store store = getById(id);
        copy(request, store);
        storeMapper.updateById(store);
    }

    @Override
    public void delete(String id) {
        getById(id);
        storeMapper.deleteById(id);
    }

    @Override
    public void updateStatus(String id, Integer status) {
        Store store = getById(id);
        store.setStatus(status);
        storeMapper.updateById(store);
    }

    private void copy(StoreUpsertRequest request, Store store) {
        store.setName(request.getName());
        store.setCode(request.getCode());
        store.setAddress(request.getAddress());
        store.setPhone(request.getPhone());
        store.setStatus(request.getStatus());
    }
}

