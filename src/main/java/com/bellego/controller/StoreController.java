package com.bellego.controller;

import com.bellego.aop.LogOperation;
import com.bellego.common.result.PageResult;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.domain.dto.common.StatusUpdateRequest;
import com.bellego.domain.dto.system.StoreQueryRequest;
import com.bellego.domain.dto.system.StoreUpsertRequest;
import com.bellego.domain.entity.Store;
import com.bellego.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('store:view')")
    public Result<PageResult<Store>> page(StoreQueryRequest request) {
        return ResultBuilder.success(storeService.page(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('store:view')")
    public Result<Store> get(@PathVariable String id) {
        return ResultBuilder.success(storeService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('store:add')")
    @LogOperation(module = "门店管理", action = "新增门店")
    public Result<Void> create(@Valid @RequestBody StoreUpsertRequest request) {
        storeService.create(request);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('store:edit')")
    @LogOperation(module = "门店管理", action = "修改门店")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody StoreUpsertRequest request) {
        storeService.update(id, request);
        return ResultBuilder.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('store:delete')")
    @LogOperation(module = "门店管理", action = "删除门店")
    public Result<Void> delete(@PathVariable String id) {
        storeService.delete(id);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('store:edit')")
    @LogOperation(module = "门店管理", action = "修改门店状态")
    public Result<Void> updateStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest request) {
        storeService.updateStatus(id, request.getStatus());
        return ResultBuilder.success();
    }
}

