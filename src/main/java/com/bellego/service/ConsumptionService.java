package com.bellego.service;

import com.bellego.common.result.PageResult;
import com.bellego.domain.dto.member.ConsumptionCreateRequest;
import com.bellego.domain.dto.member.ConsumptionQueryRequest;
import com.bellego.domain.entity.MemberConsumption;
import org.springframework.web.multipart.MultipartFile;

public interface ConsumptionService {
    PageResult<MemberConsumption> page(ConsumptionQueryRequest request);
    MemberConsumption getById(String id);
    void create(ConsumptionCreateRequest request);
    void importCsv(MultipartFile file);
}

