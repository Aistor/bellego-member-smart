package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.domain.dto.marketing.PointRuleQueryDto;
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
    public IPage<PointRule> page(PointRuleQueryDto dto) {
        log.info("开始分页查询积分规则，ruleName={}, status={}", dto.getRuleName(), dto.getStatus());
        LambdaQueryWrapper<PointRule> wrapper = new LambdaQueryWrapper<PointRule>()
                .like(dto.getRuleName() != null && !dto.getRuleName().isBlank(), PointRule::getRuleName, dto.getRuleName())
                .eq(dto.getStatus() != null, PointRule::getStatus, dto.getStatus())
                .orderByDesc(PointRule::getCreateTime);
        return pointRuleMapper.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
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
        List<PointRule> rules = pointRuleMapper.selectList(new LambdaQueryWrapper<PointRule>()
                .eq(PointRule::getStatus, 1)
                .eq(PointRule::getRuleType, 1)
                .orderByDesc(PointRule::getApplicableLevelId));
        return rules.stream()
                .filter(rule -> rule.getApplicableLevelId() == null || rule.getApplicableLevelId().isBlank() || rule.getApplicableLevelId().equals(levelId))
                .findFirst()
                .orElse(null);
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
