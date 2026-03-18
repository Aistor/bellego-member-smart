package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.common.exception.BusinessException;
import com.bellego.domain.dto.system.StoreQueryDto;
import com.bellego.domain.dto.system.StoreUpsertDto;
import com.bellego.domain.entity.Store;
import com.bellego.mapper.StoreMapper;
import com.bellego.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 门店服务实现
 */
@Slf4j
@Service
public class StoreServiceImpl implements StoreService {
    private final StoreMapper storeMapper;

    public StoreServiceImpl(StoreMapper storeMapper) {
        this.storeMapper = storeMapper;
    }

    @Override
    public IPage<Store> page(StoreQueryDto dto) {
        log.info("开始分页查询门店，关键字={}, 状态={}", dto.getKeyword(), dto.getStatus());
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<Store>().eq(dto.getStatus() != null, Store::getStatus, dto.getStatus()).and(dto.getKeyword() != null && !dto.getKeyword().isBlank(), q -> q.like(Store::getName, dto.getKeyword()).or().like(Store::getCode, dto.getKeyword())).orderByDesc(Store::getCreateTime);
        return storeMapper.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
    }

    @Override
    public Store getById(String id) {
        Store store = storeMapper.selectById(id);
        if (store == null) {
            log.error("查询门店失败，门店不存在，id={}", id);
            throw new BusinessException("门店不存在");
        }
        return store;
    }

    @Override
    public void create(StoreUpsertDto dto) {
        log.info("开始新增门店，code={}", dto.getCode());
        Store store = new Store();
        copy(dto, store);
        store.setCreateTime(new Date());
        storeMapper.insert(store);
    }

    @Override
    public void update(String id, StoreUpsertDto dto) {
        log.info("开始修改门店，id={}", id);
        Store store = getById(id);
        copy(dto, store);
        storeMapper.updateById(store);
    }

    @Override
    public void delete(String id) {
        log.info("开始删除门店，id={}", id);
        getById(id);
        storeMapper.deleteById(id);
    }

    @Override
    public void updateStatus(String id, Integer status) {
        log.info("开始修改门店状态，id={}, status={}", id, status);
        Store store = getById(id);
        store.setStatus(status);
        storeMapper.updateById(store);
    }

    private void copy(StoreUpsertDto dto, Store store) {
        store.setName(dto.getName());
        store.setCode(dto.getCode());
        store.setAddress(dto.getAddress());
        store.setPhone(dto.getPhone());
        store.setStatus(dto.getStatus());
    }
}