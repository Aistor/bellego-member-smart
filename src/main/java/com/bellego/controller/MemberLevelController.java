package com.bellego.controller;

import com.bellego.aop.LogOperation;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.domain.dto.common.StatusUpdateRequest;
import com.bellego.domain.dto.member.MemberLevelUpsertRequest;
import com.bellego.domain.entity.MemberLevel;
import com.bellego.service.MemberLevelService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/levels")
public class MemberLevelController {

    private final MemberLevelService memberLevelService;

    public MemberLevelController(MemberLevelService memberLevelService) {
        this.memberLevelService = memberLevelService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('level:view')")
    public Result<List<MemberLevel>> list() {
        return ResultBuilder.success(memberLevelService.list());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('level:view')")
    public Result<MemberLevel> get(@PathVariable String id) {
        return ResultBuilder.success(memberLevelService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('level:add')")
    @LogOperation(module = "会员等级", action = "新增等级")
    public Result<Void> create(@Valid @RequestBody MemberLevelUpsertRequest request) {
        memberLevelService.create(request);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('level:edit')")
    @LogOperation(module = "会员等级", action = "修改等级")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody MemberLevelUpsertRequest request) {
        memberLevelService.update(id, request);
        return ResultBuilder.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('level:delete')")
    @LogOperation(module = "会员等级", action = "删除等级")
    public Result<Void> delete(@PathVariable String id) {
        memberLevelService.delete(id);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('level:edit')")
    @LogOperation(module = "会员等级", action = "修改等级状态")
    public Result<Void> updateStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest request) {
        memberLevelService.updateStatus(id, request.getStatus());
        return ResultBuilder.success();
    }
}

