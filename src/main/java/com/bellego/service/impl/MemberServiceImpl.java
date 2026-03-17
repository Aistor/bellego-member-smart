package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.common.exception.BusinessException;
import com.bellego.common.result.PageResult;
import com.bellego.common.util.CsvImportUtils;
import com.bellego.domain.dto.member.MemberQueryRequest;
import com.bellego.domain.dto.member.MemberUpsertRequest;
import com.bellego.domain.entity.Member;
import com.bellego.domain.entity.MemberLevel;
import com.bellego.mapper.MemberMapper;
import com.bellego.service.MemberLevelService;
import com.bellego.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final MemberLevelService memberLevelService;
    private final CsvImportUtils csvImportUtils;

    public MemberServiceImpl(MemberMapper memberMapper, MemberLevelService memberLevelService, CsvImportUtils csvImportUtils) {
        this.memberMapper = memberMapper;
        this.memberLevelService = memberLevelService;
        this.csvImportUtils = csvImportUtils;
    }

    @Override
    public PageResult<Member> page(MemberQueryRequest request) {
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<Member>()
                .eq(request.getLevelId() != null && !request.getLevelId().isBlank(), Member::getLevelId, request.getLevelId())
                .eq(request.getStatus() != null, Member::getStatus, request.getStatus())
                .and(request.getKeyword() != null && !request.getKeyword().isBlank(), q -> q.like(Member::getName, request.getKeyword())
                        .or().like(Member::getPhone, request.getKeyword())
                        .or().like(Member::getCardNumber, request.getKeyword()))
                .orderByDesc(Member::getCreateTime);
        Page<Member> page = memberMapper.selectPage(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
        return PageResult.of(page);
    }

    @Override
    public Member getById(String id) {
        Member member = memberMapper.selectById(id);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        return member;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(MemberUpsertRequest request) {
        validateUnique(request.getPhone(), request.getCardNumber(), null);
        MemberLevel level = request.getLevelId() == null || request.getLevelId().isBlank()
                ? memberLevelService.getDefaultLevel()
                : memberLevelService.getById(request.getLevelId());
        Member member = new Member();
        member.setLevelId(level.getId());
        member.setCardNumber(request.getCardNumber());
        member.setName(request.getName());
        member.setPhone(request.getPhone());
        member.setGender(request.getGender());
        member.setBirthday(request.getBirthday());
        member.setStatus(request.getStatus());
        member.setTotalPoints(0);
        member.setTotalConsumption(BigDecimal.ZERO);
        member.setCreateTime(new Date());
        member.setUpdateTime(new Date());
        memberMapper.insert(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(String id, MemberUpsertRequest request) {
        Member member = getById(id);
        validateUnique(request.getPhone(), request.getCardNumber(), id);
        member.setLevelId(request.getLevelId() == null || request.getLevelId().isBlank() ? member.getLevelId() : request.getLevelId());
        member.setCardNumber(request.getCardNumber());
        member.setName(request.getName());
        member.setPhone(request.getPhone());
        member.setGender(request.getGender());
        member.setBirthday(request.getBirthday());
        member.setStatus(request.getStatus());
        member.setUpdateTime(new Date());
        memberMapper.updateById(member);
    }

    @Override
    public void updateStatus(String id, Integer status) {
        Member member = getById(id);
        member.setStatus(status);
        member.setUpdateTime(new Date());
        memberMapper.updateById(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importCsv(MultipartFile file) {
        List<MemberUpsertRequest> requests = csvImportUtils.read(file, parts -> {
            MemberUpsertRequest request = new MemberUpsertRequest();
            request.setCardNumber(parts[0].trim());
            request.setName(parts[1].trim());
            request.setPhone(parts[2].trim());
            request.setGender(Integer.parseInt(parts[3].trim()));
            request.setBirthday(parts.length > 4 && !parts[4].isBlank() ? LocalDate.parse(parts[4].trim()) : null);
            request.setStatus(parts.length > 5 ? Integer.parseInt(parts[5].trim()) : 1);
            return request;
        });
        requests.forEach(this::create);
    }

    private void validateUnique(String phone, String cardNumber, String excludeId) {
        Member phoneMember = memberMapper.selectOne(new LambdaQueryWrapper<Member>().eq(Member::getPhone, phone));
        if (phoneMember != null && !phoneMember.getId().equals(excludeId)) {
            throw new BusinessException("手机号已存在");
        }
        Member cardMember = memberMapper.selectOne(new LambdaQueryWrapper<Member>().eq(Member::getCardNumber, cardNumber));
        if (cardMember != null && !cardMember.getId().equals(excludeId)) {
            throw new BusinessException("会员卡号已存在");
        }
    }
}

