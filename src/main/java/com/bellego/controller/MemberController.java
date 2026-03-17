package com.bellego.controller;

import com.bellego.aop.LogOperation;
import com.bellego.common.result.PageResult;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.domain.dto.common.StatusUpdateRequest;
import com.bellego.domain.dto.member.MemberQueryRequest;
import com.bellego.domain.dto.member.MemberUpsertRequest;
import com.bellego.domain.entity.Member;
import com.bellego.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('member:view')")
    public Result<PageResult<Member>> page(MemberQueryRequest request) {
        return ResultBuilder.success(memberService.page(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('member:view')")
    public Result<Member> get(@PathVariable String id) {
        return ResultBuilder.success(memberService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('member:add')")
    @LogOperation(module = "会员管理", action = "新增会员")
    public Result<Void> create(@Valid @RequestBody MemberUpsertRequest request) {
        memberService.create(request);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('member:edit')")
    @LogOperation(module = "会员管理", action = "修改会员")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody MemberUpsertRequest request) {
        memberService.update(id, request);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('member:edit')")
    @LogOperation(module = "会员管理", action = "修改会员状态")
    public Result<Void> updateStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest request) {
        memberService.updateStatus(id, request.getStatus());
        return ResultBuilder.success();
    }

    @PostMapping("/import")
    @PreAuthorize("hasAuthority('member:import')")
    @LogOperation(module = "会员管理", action = "批量导入会员")
    public Result<Void> importCsv(@RequestPart MultipartFile file) {
        memberService.importCsv(file);
        return ResultBuilder.success();
    }
}

