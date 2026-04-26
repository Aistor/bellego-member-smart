package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.common.exception.BusinessException;
import com.bellego.common.util.CsvImportUtils;
import com.bellego.domain.dto.member.MemberQueryDto;
import com.bellego.domain.dto.member.MemberUpsertDto;
import com.bellego.domain.entity.Member;
import com.bellego.domain.entity.MemberLevel;
import com.bellego.mapper.MemberMapper;
import com.bellego.service.MemberLevelService;
import com.bellego.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 会员服务实现
 */
@Slf4j
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

    /**
     * 分页查询会员
     */
    @Override
    public IPage<Member> page(MemberQueryDto dto) {
        log.info("开始分页查询会员，关键字={}, 等级ID={}, 状态={}", dto.getKeyword(), dto.getLevelId(), dto.getStatus());
        LambdaQueryWrapper<Member> wrapper = new LambdaQueryWrapper<Member>()
                .eq(dto.getLevelId() != null && !dto.getLevelId().isBlank(), Member::getLevelId, dto.getLevelId())
                .eq(dto.getStatus() != null, Member::getStatus, dto.getStatus())
                .and(dto.getKeyword() != null && !dto.getKeyword().isBlank(), q -> q.like(Member::getName, dto.getKeyword())
                        .or().like(Member::getPhone, dto.getKeyword())
                        .or().like(Member::getCardNumber, dto.getKeyword()))
                .orderByDesc(Member::getCreateTime);
        return memberMapper.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
    }

    /**
     * 查询会员详情
     */
    @Override
    public Member getById(String id) {
        Member member = memberMapper.selectById(id);
        if (member == null) {
            log.error("查询会员失败，会员不存在，id={}", id);
            throw new BusinessException("会员不存在");
        }
        return member;
    }

    /**
     * 新增会员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(MemberUpsertDto dto) {
        log.info("开始新增会员，手机号={}", dto.getPhone());
        validateUnique(dto.getPhone(), null);
        MemberLevel level = dto.getLevelId() == null || dto.getLevelId().isBlank()
                ? memberLevelService.getDefaultLevel()
                : memberLevelService.getById(dto.getLevelId());
        Member member = new Member();
        member.setLevelId(level.getId());
        member.setCardNumber(generateUniqueCardNumber());
        member.setName(dto.getName());
        member.setPhone(dto.getPhone());
        member.setGender(dto.getGender());
        member.setBirthday(dto.getBirthday());
        member.setStatus(dto.getStatus());
        member.setTotalPoints(0);
        member.setTotalConsumption(BigDecimal.ZERO);
        member.setCreateTime(new Date());
        member.setUpdateTime(new Date());
        memberMapper.insert(member);
        log.info("会员新增成功，会员ID={}", member.getId());
    }

    /**
     * 修改会员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(String id, MemberUpsertDto dto) {
        log.info("开始修改会员，会员ID={}", id);
        Member member = getById(id);
        validateUnique(dto.getPhone(), id);
        member.setLevelId(dto.getLevelId() == null || dto.getLevelId().isBlank() ? member.getLevelId() : dto.getLevelId());
        member.setName(dto.getName());
        member.setPhone(dto.getPhone());
        member.setGender(dto.getGender());
        member.setBirthday(dto.getBirthday());
        member.setStatus(dto.getStatus());
        member.setUpdateTime(new Date());
        memberMapper.updateById(member);
        log.info("会员修改成功，会员ID={}", id);
    }

    /**
     * 修改会员状态
     */
    @Override
    public void updateStatus(String id, Integer status) {
        log.info("开始修改会员状态，会员ID={}, 状态={}", id, status);
        Member member = getById(id);
        member.setStatus(status);
        member.setUpdateTime(new Date());
        memberMapper.updateById(member);
    }

    /**
     * 导入会员数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importCsv(MultipartFile file) {
        List<MemberUpsertDto> list = csvImportUtils.read(file, parts -> {
            MemberUpsertDto dto = new MemberUpsertDto();
            dto.setName(parts[0].trim());
            dto.setPhone(parts[1].trim());
            dto.setGender(Integer.parseInt(parts[2].trim()));
            dto.setBirthday(parts.length > 3 && !parts[3].isBlank() ? LocalDate.parse(parts[3].trim()) : null);
            dto.setStatus(parts.length > 4 ? Integer.parseInt(parts[4].trim()) : 1);
            return dto;
        });
        log.info("开始导入会员，数量={}", list.size());
        list.forEach(this::create);
    }

    /**
     * 生成16位唯一会员卡号
     */
    private String generateUniqueCardNumber() {
        Random random = new Random();
        String cardNumber;
        do {
            StringBuilder sb = new StringBuilder(16);
            for (int i = 0; i < 16; i++) {
                sb.append(random.nextInt(10));
            }
            cardNumber = sb.toString();
        } while (memberMapper.selectOne(
                new LambdaQueryWrapper<Member>().eq(Member::getCardNumber, cardNumber)) != null);
        return cardNumber;
    }

    private void validateUnique(String phone, String excludeId) {
        Member phoneMember = memberMapper.selectOne(new LambdaQueryWrapper<Member>().eq(Member::getPhone, phone));
        if (phoneMember != null && !phoneMember.getId().equals(excludeId)) {
            log.error("手机号重复，phone={}", phone);
            throw new BusinessException("手机号已存在");
        }
    }
}