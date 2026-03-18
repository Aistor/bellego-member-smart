package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bellego.domain.dto.marketing.PointRuleUpsertDto;
import com.bellego.domain.entity.PointRule;
import com.bellego.mapper.PointRuleMapper;
import com.bellego.service.PointRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 积分规则服务实现
 */
@Slf4j
@Service
public class PointRuleServiceImpl implements PointRuleService {
    private final PointRuleMapper pointRuleMapper;

    public PointRuleServiceImpl(PointRuleMapper pointRuleMapper) {
        this.pointRuleMapper = pointRuleMapper;
    }

    @Override
    public List<PointRule> list() {
        log.info("开始查询积分规则列表");
        return pointRuleMapper.selectList(new LambdaQueryWrapper<PointRule>().orderByDesc(PointRule::getCreateTime));
    }

    @Override
    public void create(PointRuleUpsertDto dto) {
        log.info("开始新增积分规则，ruleName={}", dto.getRuleName());
        PointRule rule = new PointRule();
        copy(dto, rule);
        rule.setCreateTime(new Date());
        pointRuleMapper.insert(rule);
    }

    @Override
    public void update(String id, PointRuleUpsertDto dto) {
        log.info("开始修改积分规则，id={}", id);
        PointRule rule = pointRuleMapper.selectById(id);
        copy(dto, rule);
        pointRuleMapper.updateById(rule);
    }

    @Override
    public void delete(String id) {
        log.info("开始删除积分规则，id={}", id);
        pointRuleMapper.deleteById(id);
    }

    @Override
    public void updateStatus(String id, Integer status) {
        log.info("开始修改积分规则状态，id={}, status={}", id, status);
        PointRule rule = pointRuleMapper.selectById(id);
        rule.setStatus(status);
        pointRuleMapper.updateById(rule);
    }

    @Override
    public PointRule matchConsumptionRule(String levelId) {
        log.info("开始匹配消费积分规则，levelId={}", levelId);
        List<PointRule> rules = pointRuleMapper.selectList(new LambdaQueryWrapper<PointRule>().eq(PointRule::getStatus, 1).eq(PointRule::getRuleType, 1).orderByDesc(PointRule::getApplicableLevelId));
        return rules.stream().filter(rule -> rule.getApplicableLevelId() == null || rule.getApplicableLevelId().isBlank() || rule.getApplicableLevelId().equals(levelId)).findFirst().orElse(null);
    }

    private void copy(PointRuleUpsertDto dto, PointRule rule) {
        rule.setRuleName(dto.getRuleName());
        rule.setRuleType(dto.getRuleType());
        rule.setApplicableLevelId(dto.getApplicableLevelId());
        rule.setPointsPerUnit(dto.getPointsPerUnit());
        rule.setMinAmount(dto.getMinAmount());
        rule.setMaxPoints(dto.getMaxPoints());
        rule.setStatus(dto.getStatus());
    }
}