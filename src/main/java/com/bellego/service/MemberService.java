package com.bellego.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.domain.dto.member.MemberQueryDto;
import com.bellego.domain.dto.member.MemberUpsertDto;
import com.bellego.domain.entity.Member;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    IPage<Member> page(MemberQueryDto dto);
    Member getById(String id);
    void create(MemberUpsertDto dto);
    void update(String id, MemberUpsertDto dto);
    void updateStatus(String id, Integer status);
    void importCsv(MultipartFile file);
}