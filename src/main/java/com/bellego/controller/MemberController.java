package com.bellego.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.aop.LogOperation;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.common.util.PageConvertUtils;
import com.bellego.common.util.VoMapper;
import com.bellego.domain.dto.common.StatusUpdateDto;
import com.bellego.domain.dto.member.MemberQueryDto;
import com.bellego.domain.dto.member.MemberUpsertDto;
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
    public Result<IPage<MemberVo>> page(MemberQueryDto dto) {
        return ResultBuilder.success(PageConvertUtils.map(memberService.page(dto), voMapper::toMemberVo));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('member:view')")
    public Result<MemberVo> get(@PathVariable String id) {
        return ResultBuilder.success(voMapper.toMemberVo(memberService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('member:add')")
    @LogOperation(module = "member", action = "create member")
    public Result<Void> create(@Valid @RequestBody MemberUpsertDto dto) {
        memberService.create(dto);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('member:edit')")
    @LogOperation(module = "member", action = "update member")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody MemberUpsertDto dto) {
        memberService.update(id, dto);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('member:edit')")
    @LogOperation(module = "member", action = "update member status")
    public Result<Void> updateStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateDto dto) {
        memberService.updateStatus(id, dto.getStatus());
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