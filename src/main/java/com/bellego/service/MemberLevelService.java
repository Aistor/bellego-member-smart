package com.bellego.service;

import com.bellego.domain.dto.member.MemberLevelUpsertDto;
import com.bellego.domain.entity.MemberLevel;

import java.util.List;

public interface MemberLevelService {
    List<MemberLevel> list();
    MemberLevel getById(String id);
    void create(MemberLevelUpsertDto dto);
    void update(String id, MemberLevelUpsertDto dto);
    void delete(String id);
    void updateStatus(String id, Integer status);
    MemberLevel getDefaultLevel();
    void upgradeMemberLevelIfNeeded(String memberId);
}