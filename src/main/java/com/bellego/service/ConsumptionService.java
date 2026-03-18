package com.bellego.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.domain.dto.member.ConsumptionCreateDto;
import com.bellego.domain.dto.member.ConsumptionQueryDto;
import com.bellego.domain.entity.MemberConsumption;
import org.springframework.web.multipart.MultipartFile;

public interface ConsumptionService {
    IPage<MemberConsumption> page(ConsumptionQueryDto dto);
    MemberConsumption getById(String id);
    void create(ConsumptionCreateDto dto);
    void importCsv(MultipartFile file);
}