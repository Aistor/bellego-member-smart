package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bellego.common.exception.BusinessException;
import com.bellego.domain.dto.member.MemberLevelUpsertRequest;
import com.bellego.domain.entity.Member;
import com.bellego.domain.entity.MemberLevel;
import com.bellego.mapper.MemberLevelMapper;
import com.bellego.mapper.MemberMapper;
import com.bellego.service.MemberLevelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
        return memberLevelMapper.selectList(new LambdaQueryWrapper<MemberLevel>().orderByAsc(MemberLevel::getLevel));
    }

    @Override
    public MemberLevel getById(String id) {
        MemberLevel level = memberLevelMapper.selectById(id);
        if (level == null) {
            throw new BusinessException("会员等级不存在");
        }
        return level;
    }

    @Override
    public void create(MemberLevelUpsertRequest request) {
        MemberLevel level = new MemberLevel();
        copy(request, level);
        level.setCreateTime(new Date());
        memberLevelMapper.insert(level);
    }

    @Override
    public void update(String id, MemberLevelUpsertRequest request) {
        MemberLevel level = getById(id);
        copy(request, level);
        memberLevelMapper.updateById(level);
    }

    @Override
    public void delete(String id) {
        getById(id);
        memberLevelMapper.deleteById(id);
    }

    @Override
    public void updateStatus(String id, Integer status) {
        MemberLevel level = getById(id);
        level.setStatus(status);
        memberLevelMapper.updateById(level);
    }

    @Override
    public MemberLevel getDefaultLevel() {
        MemberLevel level = memberLevelMapper.selectOne(new LambdaQueryWrapper<MemberLevel>()
                .eq(MemberLevel::getStatus, 1)
                .orderByAsc(MemberLevel::getLevel)
                .last("limit 1"));
        if (level == null) {
            throw new BusinessException("请先创建可用的会员等级");
        }
        return level;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upgradeMemberLevelIfNeeded(String memberId) {
        Member member = memberMapper.selectById(memberId);
        if (member == null) {
            return;
        }
        List<MemberLevel> levels = memberLevelMapper.selectList(new LambdaQueryWrapper<MemberLevel>()
                .eq(MemberLevel::getStatus, 1)
                .orderByAsc(MemberLevel::getLevel));
        MemberLevel matched = null;
        for (MemberLevel level : levels) {
            boolean pointsOk = member.getTotalPoints() != null && member.getTotalPoints() >= level.getMinPoints();
            boolean amountOk = member.getTotalConsumption() != null && member.getTotalConsumption().compareTo(level.getMinConsumption()) >= 0;
            if (pointsOk && amountOk) {
                matched = level;
            }
        }
        if (matched != null && !matched.getId().equals(member.getLevelId())) {
            member.setLevelId(matched.getId());
            member.setUpdateTime(new Date());
            memberMapper.updateById(member);
        }
    }

    private void copy(MemberLevelUpsertRequest request, MemberLevel level) {
        level.setName(request.getName());
        level.setLevel(request.getLevel());
        level.setMinPoints(request.getMinPoints());
        level.setMinConsumption(request.getMinConsumption() == null ? BigDecimal.ZERO : request.getMinConsumption());
        level.setDiscountRate(request.getDiscountRate());
        level.setPointRate(request.getPointRate());
        level.setStatus(request.getStatus());
    }
}

