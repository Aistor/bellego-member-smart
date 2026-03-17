package com.bellego.service;

import com.bellego.common.result.PageResult;
import com.bellego.domain.dto.member.MemberQueryRequest;
import com.bellego.domain.dto.member.MemberUpsertRequest;
import com.bellego.domain.entity.Member;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    PageResult<Member> page(MemberQueryRequest request);
    Member getById(String id);
    void create(MemberUpsertRequest request);
    void update(String id, MemberUpsertRequest request);
    void updateStatus(String id, Integer status);
    void importCsv(MultipartFile file);
}

