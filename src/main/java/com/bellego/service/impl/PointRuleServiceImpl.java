package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bellego.domain.dto.marketing.PointRuleUpsertRequest;
import com.bellego.domain.entity.PointRule;
import com.bellego.mapper.PointRuleMapper;
import com.bellego.service.PointRuleService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PointRuleServiceImpl implements PointRuleService {

    private final PointRuleMapper pointRuleMapper;

    public PointRuleServiceImpl(PointRuleMapper pointRuleMapper) {
        this.pointRuleMapper = pointRuleMapper;
    }

    @Override
    public List<PointRule> list() {
        return pointRuleMapper.selectList(new LambdaQueryWrapper<PointRule>().orderByDesc(PointRule::getCreateTime));
    }

    @Override
    public void create(PointRuleUpsertRequest request) {
        PointRule pointRule = new PointRule();
        copy(request, pointRule);
        pointRule.setCreateTime(new Date());
        pointRuleMapper.insert(pointRule);
    }

    @Override
    public void update(String id, PointRuleUpsertRequest request) {
        PointRule pointRule = pointRuleMapper.selectById(id);
        copy(request, pointRule);
        pointRuleMapper.updateById(pointRule);
    }

    @Override
    public void delete(String id) {
        pointRuleMapper.deleteById(id);
    }

    @Override
    public void updateStatus(String id, Integer status) {
        PointRule pointRule = pointRuleMapper.selectById(id);
        pointRule.setStatus(status);
        pointRuleMapper.updateById(pointRule);
    }

    @Override
    public PointRule matchConsumptionRule(String levelId) {
        List<PointRule> rules = pointRuleMapper.selectList(new LambdaQueryWrapper<PointRule>()
                .eq(PointRule::getStatus, 1)
                .eq(PointRule::getRuleType, 1)
                .orderByDesc(PointRule::getApplicableLevelId));
        return rules.stream()
                .filter(rule -> rule.getApplicableLevelId() == null || rule.getApplicableLevelId().isBlank() || rule.getApplicableLevelId().equals(levelId))
                .findFirst()
                .orElse(null);
    }

    private void copy(PointRuleUpsertRequest request, PointRule pointRule) {
        pointRule.setRuleName(request.getRuleName());
        pointRule.setRuleType(request.getRuleType());
        pointRule.setApplicableLevelId(request.getApplicableLevelId());
        pointRule.setPointsPerUnit(request.getPointsPerUnit());
        pointRule.setMinAmount(request.getMinAmount());
        pointRule.setMaxPoints(request.getMaxPoints());
        pointRule.setStatus(request.getStatus());
    }
}

