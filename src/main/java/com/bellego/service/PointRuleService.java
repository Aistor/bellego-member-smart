package com.bellego.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.domain.dto.marketing.PointRuleQueryDto;
import com.bellego.domain.dto.marketing.PointRuleUpsertDto;
import com.bellego.domain.entity.PointRule;

public interface PointRuleService {
    IPage<PointRule> page(PointRuleQueryDto dto);
    void create(PointRuleUpsertDto dto);
    void update(String id, PointRuleUpsertDto dto);
    void delete(String id);
    void updateStatus(String id, Integer status);
    PointRule matchConsumptionRule(String levelId);
}
