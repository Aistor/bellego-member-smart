package com.bellego.controller;

import com.bellego.aop.LogOperation;
import com.bellego.common.result.PageResult;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.common.util.VoMapper;
import com.bellego.domain.dto.common.StatusUpdateRequest;
import com.bellego.domain.dto.member.MemberQueryRequest;
import com.bellego.domain.dto.member.MemberUpsertRequest;
import com.bellego.domain.vo.MemberVo;
import com.bellego.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;
    private final VoMapper voMapper;

    public MemberController(MemberService memberService, VoMapper voMapper) {
        this.memberService = memberService;
        this.voMapper = voMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('member:view')")
    public Result<PageResult<MemberVo>> page(MemberQueryRequest request) {
        return ResultBuilder.success(memberService.page(request).map(voMapper::toMemberVo));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('member:view')")
    public Result<MemberVo> get(@PathVariable String id) {
        return ResultBuilder.success(voMapper.toMemberVo(memberService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('member:add')")
    @LogOperation(module = "member", action = "create member")
    public Result<Void> create(@Valid @RequestBody MemberUpsertRequest request) {
        memberService.create(request);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('member:edit')")
    @LogOperation(module = "member", action = "update member")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody MemberUpsertRequest request) {
        memberService.update(id, request);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('member:edit')")
    @LogOperation(module = "member", action = "update member status")
    public Result<Void> updateStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest request) {
        memberService.updateStatus(id, request.getStatus());
        return ResultBuilder.success();
    }

    @PostMapping("/import")
    @PreAuthorize("hasAuthority('member:import')")
    @LogOperation(module = "member", action = "import members")
    public Result<Void> importCsv(@RequestPart MultipartFile file) {
        memberService.importCsv(file);
        return ResultBuilder.success();
    }
}