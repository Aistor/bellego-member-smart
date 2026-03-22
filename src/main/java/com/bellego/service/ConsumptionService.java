package com.bellego.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.domain.dto.member.ConsumptionCreateDto;
import com.bellego.domain.dto.member.ConsumptionQueryDto;
import com.bellego.domain.entity.MemberConsumption;
import com.bellego.domain.vo.ConsumptionVo;
import org.springframework.web.multipart.MultipartFile;

public interface ConsumptionService {
    IPage<ConsumptionVo> page(ConsumptionQueryDto dto);
    MemberConsumption getById(String id);
    void create(ConsumptionCreateDto dto);
    void importCsv(MultipartFile file);
}
