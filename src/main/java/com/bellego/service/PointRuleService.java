package com.bellego.service;

import com.bellego.domain.dto.marketing.PointRuleUpsertDto;
import com.bellego.domain.entity.PointRule;

import java.util.List;

public interface PointRuleService {
    List<PointRule> list();
    void create(PointRuleUpsertDto dto);
    void update(String id, PointRuleUpsertDto dto);
    void delete(String id);
    void updateStatus(String id, Integer status);
    PointRule matchConsumptionRule(String levelId);
}