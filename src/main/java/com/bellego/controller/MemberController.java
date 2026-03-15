package com.bellego.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.domain.dto.MemberDto;
import com.bellego.domain.vo.MemberVo;
import com.bellego.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping
    public Result<Void> saveMember(@RequestBody MemberDto memberDto) {
        memberService.saveMember(memberDto);
        return ResultBuilder.success();
    }

    @GetMapping("/{id}")
    public Result<MemberVo> getMemberById(@PathVariable String id) {
        MemberVo memberVo = memberService.getMemberById(id);
        return ResultBuilder.success(memberVo);
    }

    @GetMapping("/list")
    public Result<IPage<MemberVo>> memberList(MemberDto memberDto) {
        IPage<MemberVo> pageList = memberService.memberList(memberDto);
        return ResultBuilder.success(pageList);
    }

    @PutMapping
    public Result<Void> updateMember(@RequestBody MemberDto memberDto) {
        memberService.updateMember(memberDto);
        return ResultBuilder.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable String id) {
        memberService.deleteById(id);
        return ResultBuilder.success();
    }

    @PostMapping("/batch/delete")
    public Result<Void> batchDelete(@RequestBody List<String> ids) {
        memberService.batchDelete(ids);
        return ResultBuilder.success();
    }
}
