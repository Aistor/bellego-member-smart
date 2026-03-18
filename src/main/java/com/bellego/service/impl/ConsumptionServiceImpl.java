package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.common.exception.BusinessException;
import com.bellego.common.util.CsvImportUtils;
import com.bellego.domain.dto.member.ConsumptionCreateDto;
import com.bellego.domain.dto.member.ConsumptionQueryDto;
import com.bellego.domain.entity.Member;
import com.bellego.domain.entity.MemberConsumption;
import com.bellego.domain.entity.PointDetail;
import com.bellego.domain.entity.PointRule;
import com.bellego.domain.entity.Store;
import com.bellego.mapper.MemberConsumptionMapper;
import com.bellego.mapper.MemberMapper;
import com.bellego.mapper.StoreMapper;
import com.bellego.service.ConsumptionService;
import com.bellego.service.MemberLevelService;
import com.bellego.service.PointDetailService;
import com.bellego.service.PointRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 消费记录服务实现
 */
@Slf4j
@Service
public class ConsumptionServiceImpl implements ConsumptionService {
    private final MemberConsumptionMapper consumptionMapper;
    private final MemberMapper memberMapper;
    private final StoreMapper storeMapper;
    private final PointRuleService pointRuleService;
    private final PointDetailService pointDetailService;
    private final MemberLevelService memberLevelService;
    private final CsvImportUtils csvImportUtils;

    public ConsumptionServiceImpl(MemberConsumptionMapper consumptionMapper, MemberMapper memberMapper, StoreMapper storeMapper, PointRuleService pointRuleService, PointDetailService pointDetailService, MemberLevelService memberLevelService, CsvImportUtils csvImportUtils) {
        this.consumptionMapper = consumptionMapper;
        this.memberMapper = memberMapper;
        this.storeMapper = storeMapper;
        this.pointRuleService = pointRuleService;
        this.pointDetailService = pointDetailService;
        this.memberLevelService = memberLevelService;
        this.csvImportUtils = csvImportUtils;
    }

    @Override
    public IPage<MemberConsumption> page(ConsumptionQueryDto dto) {
        log.info("开始分页查询消费记录，memberId={}, storeId={}", dto.getMemberId(), dto.getStoreId());
        LambdaQueryWrapper<MemberConsumption> wrapper = new LambdaQueryWrapper<MemberConsumption>()
                .eq(dto.getMemberId() != null && !dto.getMemberId().isBlank(), MemberConsumption::getMemberId, dto.getMemberId())
                .eq(dto.getStoreId() != null && !dto.getStoreId().isBlank(), MemberConsumption::getStoreId, dto.getStoreId())
                .orderByDesc(MemberConsumption::getConsumeTime);
        return consumptionMapper.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
    }

    @Override
    public MemberConsumption getById(String id) {
        MemberConsumption consumption = consumptionMapper.selectById(id);
        if (consumption == null) {
            log.error("查询消费记录失败，记录不存在，id={}", id);
            throw new BusinessException("消费记录不存在");
        }
        return consumption;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ConsumptionCreateDto dto) {
        log.info("开始新增消费记录，memberId={}, storeId={}, amount={}", dto.getMemberId(), dto.getStoreId(), dto.getAmount());
        Member member = memberMapper.selectById(dto.getMemberId());
        if (member == null) {
            log.error("新增消费失败，会员不存在，memberId={}", dto.getMemberId());
            throw new BusinessException("会员不存在");
        }
        Store store = storeMapper.selectById(dto.getStoreId());
        if (store == null) {
            log.error("新增消费失败，门店不存在，storeId={}", dto.getStoreId());
            throw new BusinessException("门店不存在");
        }
        Date consumeTime = dto.getConsumeTime() == null ? new Date() : dto.getConsumeTime();
        int earnedPoints = calculatePoints(member, dto.getAmount());
        MemberConsumption consumption = new MemberConsumption();
        consumption.setMemberId(member.getId());
        consumption.setStoreId(store.getId());
        consumption.setAmount(dto.getAmount());
        consumption.setPointsEarned(earnedPoints);
        consumption.setConsumeTime(consumeTime);
        consumption.setCreateTime(new Date());
        consumptionMapper.insert(consumption);
        member.setTotalConsumption(member.getTotalConsumption().add(dto.getAmount()));
        member.setTotalPoints(member.getTotalPoints() + earnedPoints);
        member.setLastConsumeTime(consumeTime);
        member.setUpdateTime(new Date());
        memberMapper.updateById(member);
        PointDetail detail = new PointDetail();
        detail.setMemberId(member.getId());
        detail.setType(1);
        detail.setPoints(earnedPoints);
        detail.setBalance(member.getTotalPoints());
        detail.setSource("consumption");
        detail.setSourceId(consumption.getId());
        detail.setRemark("消费积分入账");
        detail.setCreateTime(new Date());
        pointDetailService.save(detail);
        memberLevelService.upgradeMemberLevelIfNeeded(member.getId());
        log.info("消费记录新增成功，recordId={}, earnedPoints={}", consumption.getId(), earnedPoints);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importCsv(MultipartFile file) {
        List<ConsumptionCreateDto> list = csvImportUtils.read(file, parts -> {
            ConsumptionCreateDto dto = new ConsumptionCreateDto();
            dto.setMemberId(parts[0].trim());
            dto.setStoreId(parts[1].trim());
            dto.setAmount(new BigDecimal(parts[2].trim()));
            return dto;
        });
        log.info("开始导入消费记录，数量={}", list.size());
        list.forEach(this::create);
    }

    private int calculatePoints(Member member, BigDecimal amount) {
        PointRule rule = pointRuleService.matchConsumptionRule(member.getLevelId());
        if (rule == null) {
            return 0;
        }
        if (rule.getMinAmount() != null && amount.compareTo(rule.getMinAmount()) < 0) {
            return 0;
        }
        int points = amount.multiply(BigDecimal.valueOf(rule.getPointsPerUnit())).intValue();
        if (rule.getMaxPoints() != null) {
            points = Math.min(points, rule.getMaxPoints());
        }
        return Math.max(points, 0);
    }
}