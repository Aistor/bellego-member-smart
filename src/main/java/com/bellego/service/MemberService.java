package com.bellego.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.domain.dto.MemberDto;
import com.bellego.domain.vo.MemberVo;

import java.util.List;

public interface MemberService {
    void saveMember(MemberDto memberDto);

    MemberVo getMemberById(String id);

    IPage<MemberVo> memberList(MemberDto memberDto);

    void updateMember(MemberDto memberDto);

    void deleteById(String id);

    void batchDelete(List<String> ids);
}
