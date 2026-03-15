package com.bellego.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.domain.dto.MemberDto;
import com.bellego.domain.vo.MemberVo;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface MemberService {
    void saveMember(MemberDto memberDto);

    MemberVo getMemberById(String id);

    IPage<MemberVo> memberList(MemberDto memberDto);

    void updateMember(String id, MemberDto memberDto);

    void deleteById(String id);

    void batchDelete(List<String> ids);
}
