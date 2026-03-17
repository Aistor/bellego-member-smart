package com.bellego.controller;

import com.bellego.aop.LogOperation;
import com.bellego.common.result.PageResult;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.common.util.VoMapper;
import com.bellego.domain.dto.common.StatusUpdateRequest;
import com.bellego.domain.dto.system.StoreQueryRequest;
import com.bellego.domain.dto.system.StoreUpsertRequest;
import com.bellego.domain.vo.StoreVo;
import com.bellego.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stores")
public class StoreController {
    private final StoreService storeService;
    private final VoMapper voMapper;

    public StoreController(StoreService storeService, VoMapper voMapper) {
        this.storeService = storeService;
        this.voMapper = voMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('store:view')")
    public Result<PageResult<StoreVo>> page(StoreQueryRequest request) {
        return ResultBuilder.success(storeService.page(request).map(voMapper::toStoreVo));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('store:view')")
    public Result<StoreVo> get(@PathVariable String id) {
        return ResultBuilder.success(voMapper.toStoreVo(storeService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('store:add')")
    @LogOperation(module = "store", action = "create store")
    public Result<Void> create(@Valid @RequestBody StoreUpsertRequest request) {
        storeService.create(request);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('store:edit')")
    @LogOperation(module = "store", action = "update store")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody StoreUpsertRequest request) {
        storeService.update(id, request);
        return ResultBuilder.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('store:delete')")
    @LogOperation(module = "store", action = "delete store")
    public Result<Void> delete(@PathVariable String id) {
        storeService.delete(id);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('store:edit')")
    @LogOperation(module = "store", action = "update store status")
    public Result<Void> updateStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest request) {
        storeService.updateStatus(id, request.getStatus());
        return ResultBuilder.success();
    }
}