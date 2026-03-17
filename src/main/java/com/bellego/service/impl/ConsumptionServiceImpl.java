package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.common.exception.BusinessException;
import com.bellego.common.result.PageResult;
import com.bellego.common.util.CsvImportUtils;
import com.bellego.domain.dto.member.ConsumptionCreateRequest;
import com.bellego.domain.dto.member.ConsumptionQueryRequest;
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
    public PageResult<MemberConsumption> page(ConsumptionQueryRequest request) {
        LambdaQueryWrapper<MemberConsumption> wrapper = new LambdaQueryWrapper<MemberConsumption>().eq(request.getMemberId() != null && !request.getMemberId().isBlank(), MemberConsumption::getMemberId, request.getMemberId()).eq(request.getStoreId() != null && !request.getStoreId().isBlank(), MemberConsumption::getStoreId, request.getStoreId()).orderByDesc(MemberConsumption::getConsumeTime);
        return PageResult.of(consumptionMapper.selectPage(new Page<>(request.getPageNum(), request.getPageSize()), wrapper));
    }

    @Override
    public MemberConsumption getById(String id) {
        MemberConsumption consumption = consumptionMapper.selectById(id);
        if (consumption == null) throw new BusinessException("Consumption not found");
        return consumption;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ConsumptionCreateRequest request) {
        log.info("Creating consumption, memberId={}, storeId={}, amount={}", request.getMemberId(), request.getStoreId(), request.getAmount());
        Member member = memberMapper.selectById(request.getMemberId());
        if (member == null) throw new BusinessException("Member not found");
        Store store = storeMapper.selectById(request.getStoreId());
        if (store == null) throw new BusinessException("Store not found");
        Date consumeTime = request.getConsumeTime() == null ? new Date() : request.getConsumeTime();
        int earnedPoints = calculatePoints(member, request.getAmount());
        MemberConsumption consumption = new MemberConsumption();
        consumption.setMemberId(member.getId());
        consumption.setStoreId(store.getId());
        consumption.setAmount(request.getAmount());
        consumption.setPointsEarned(earnedPoints);
        consumption.setConsumeTime(consumeTime);
        consumption.setCreateTime(new Date());
        consumptionMapper.insert(consumption);

        member.setTotalConsumption(member.getTotalConsumption().add(request.getAmount()));
        member.setTotalPoints(member.getTotalPoints() + earnedPoints);
        member.setLastConsumeTime(consumeTime);
        member.setUpdateTime(new Date());
        memberMapper.updateById(member);

        PointDetail pointDetail = new PointDetail();
        pointDetail.setMemberId(member.getId());
        pointDetail.setType(1);
        pointDetail.setPoints(earnedPoints);
        pointDetail.setBalance(member.getTotalPoints());
        pointDetail.setSource("consumption");
        pointDetail.setSourceId(consumption.getId());
        pointDetail.setRemark("consumption point income");
        pointDetail.setCreateTime(new Date());
        pointDetailService.save(pointDetail);
        memberLevelService.upgradeMemberLevelIfNeeded(member.getId());
        log.info("Consumption created, id={}, earnedPoints={}", consumption.getId(), earnedPoints);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importCsv(MultipartFile file) {
        List<ConsumptionCreateRequest> requests = csvImportUtils.read(file, parts -> {
            ConsumptionCreateRequest request = new ConsumptionCreateRequest();
            request.setMemberId(parts[0].trim());
            request.setStoreId(parts[1].trim());
            request.setAmount(new BigDecimal(parts[2].trim()));
            return request;
        });
        log.info("Importing consumptions, count={}", requests.size());
        requests.forEach(this::create);
    }

    private int calculatePoints(Member member, BigDecimal amount) {
        PointRule pointRule = pointRuleService.matchConsumptionRule(member.getLevelId());
        if (pointRule == null) return 0;
        if (pointRule.getMinAmount() != null && amount.compareTo(pointRule.getMinAmount()) < 0) return 0;
        int points = amount.multiply(BigDecimal.valueOf(pointRule.getPointsPerUnit())).intValue();
        if (pointRule.getMaxPoints() != null) points = Math.min(points, pointRule.getMaxPoints());
        return Math.max(points, 0);
    }
}