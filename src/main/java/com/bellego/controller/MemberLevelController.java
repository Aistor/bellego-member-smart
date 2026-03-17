package com.bellego.controller;

import com.bellego.aop.LogOperation;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.common.util.VoMapper;
import com.bellego.domain.dto.common.StatusUpdateRequest;
import com.bellego.domain.dto.member.MemberLevelUpsertRequest;
import com.bellego.domain.vo.MemberLevelVo;
import com.bellego.service.MemberLevelService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/levels")
public class MemberLevelController {

    private final MemberLevelService memberLevelService;
    private final VoMapper voMapper;

    public MemberLevelController(MemberLevelService memberLevelService, VoMapper voMapper) {
        this.memberLevelService = memberLevelService;
        this.voMapper = voMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('level:view')")
    public Result<List<MemberLevelVo>> list() {
        return ResultBuilder.success(memberLevelService.list().stream().map(voMapper::toMemberLevelVo).toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('level:view')")
    public Result<MemberLevelVo> get(@PathVariable String id) {
        return ResultBuilder.success(voMapper.toMemberLevelVo(memberLevelService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('level:add')")
    @LogOperation(module = "level", action = "create level")
    public Result<Void> create(@Valid @RequestBody MemberLevelUpsertRequest request) {
        memberLevelService.create(request);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('level:edit')")
    @LogOperation(module = "level", action = "update level")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody MemberLevelUpsertRequest request) {
        memberLevelService.update(id, request);
        return ResultBuilder.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('level:delete')")
    @LogOperation(module = "level", action = "delete level")
    public Result<Void> delete(@PathVariable String id) {
        memberLevelService.delete(id);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('level:edit')")
    @LogOperation(module = "level", action = "update level status")
    public Result<Void> updateStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest request) {
        memberLevelService.updateStatus(id, request.getStatus());
        return ResultBuilder.success();
    }
}