package com.bellego.service;

import com.bellego.domain.dto.marketing.PointRuleUpsertRequest;
import com.bellego.domain.entity.PointRule;

import java.util.List;

public interface PointRuleService {
    List<PointRule> list();
    void create(PointRuleUpsertRequest request);
    void update(String id, PointRuleUpsertRequest request);
    void delete(String id);
    void updateStatus(String id, Integer status);
    PointRule matchConsumptionRule(String levelId);
}

