package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bellego.common.exception.BusinessException;
import com.bellego.domain.dto.member.MemberLevelUpsertDto;
import com.bellego.domain.entity.Member;
import com.bellego.domain.entity.MemberLevel;
import com.bellego.mapper.MemberLevelMapper;
import com.bellego.mapper.MemberMapper;
import com.bellego.service.MemberLevelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 会员等级服务实现
 */
@Slf4j
@Service
public class MemberLevelServiceImpl implements MemberLevelService {
    private final MemberLevelMapper memberLevelMapper;
    private final MemberMapper memberMapper;

    public MemberLevelServiceImpl(MemberLevelMapper memberLevelMapper, MemberMapper memberMapper) {
        this.memberLevelMapper = memberLevelMapper;
        this.memberMapper = memberMapper;
    }

    @Override
    public List<MemberLevel> list() {
        log.info("开始查询会员等级列表");
        return memberLevelMapper.selectList(new LambdaQueryWrapper<MemberLevel>().orderByAsc(MemberLevel::getLevel));
    }

    @Override
    public MemberLevel getById(String id) {
        MemberLevel level = memberLevelMapper.selectById(id);
        if (level == null) {
            log.error("查询会员等级失败，等级不存在，id={}", id);
            throw new BusinessException("会员等级不存在");
        }
        return level;
    }

    @Override
    public void create(MemberLevelUpsertDto dto) {
        log.info("开始新增会员等级，名称={}, 等级值={}", dto.getName(), dto.getLevel());
        MemberLevel level = new MemberLevel();
        copy(dto, level);
        level.setCreateTime(new Date());
        memberLevelMapper.insert(level);
    }

    @Override
    public void update(String id, MemberLevelUpsertDto dto) {
        log.info("开始修改会员等级，id={}", id);
        MemberLevel level = getById(id);
        copy(dto, level);
        memberLevelMapper.updateById(level);
    }

    @Override
    public void delete(String id) {
        log.info("开始删除会员等级，id={}", id);
        getById(id);
        memberLevelMapper.deleteById(id);
    }

    @Override
    public void updateStatus(String id, Integer status) {
        log.info("开始修改会员等级状态，id={}, status={}", id, status);
        MemberLevel level = getById(id);
        level.setStatus(status);
        memberLevelMapper.updateById(level);
    }

    @Override
    public MemberLevel getDefaultLevel() {
        MemberLevel level = memberLevelMapper.selectOne(new LambdaQueryWrapper<MemberLevel>().eq(MemberLevel::getStatus, 1).orderByAsc(MemberLevel::getLevel).last("limit 1"));
        if (level == null) {
            log.error("获取默认会员等级失败，当前没有启用中的等级");
            throw new BusinessException("请先配置启用中的会员等级");
        }
        return level;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upgradeMemberLevelIfNeeded(String memberId) {
        Member member = memberMapper.selectById(memberId);
        if (member == null) {
            log.error("升级会员等级失败，会员不存在，memberId={}", memberId);
            return;
        }
        List<MemberLevel> levels = memberLevelMapper.selectList(new LambdaQueryWrapper<MemberLevel>().eq(MemberLevel::getStatus, 1).orderByAsc(MemberLevel::getLevel));
        MemberLevel matched = null;
        for (MemberLevel level : levels) {
            boolean pointsOk = member.getTotalPoints() != null && member.getTotalPoints() >= level.getMinPoints();
            boolean amountOk = member.getTotalConsumption() != null && member.getTotalConsumption().compareTo(level.getMinConsumption()) >= 0;
            if (pointsOk && amountOk) {
                matched = level;
            }
        }
        if (matched != null && !matched.getId().equals(member.getLevelId())) {
            log.info("会员等级满足升级条件，memberId={}, oldLevelId={}, newLevelId={}", memberId, member.getLevelId(), matched.getId());
            member.setLevelId(matched.getId());
            member.setUpdateTime(new Date());
            memberMapper.updateById(member);
        }
    }

    private void copy(MemberLevelUpsertDto dto, MemberLevel level) {
        level.setName(dto.getName());
        level.setLevel(dto.getLevel());
        level.setMinPoints(dto.getMinPoints());
        level.setMinConsumption(dto.getMinConsumption() == null ? BigDecimal.ZERO : dto.getMinConsumption());
        level.setDiscountRate(dto.getDiscountRate());
        level.setPointRate(dto.getPointRate());
        level.setStatus(dto.getStatus());
    }
}