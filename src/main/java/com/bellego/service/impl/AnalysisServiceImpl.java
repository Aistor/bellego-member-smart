package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bellego.domain.entity.Member;
import com.bellego.domain.entity.MemberConsumption;
import com.bellego.domain.vo.DailyConsumeVO;
import com.bellego.domain.vo.MemberLevelCountVo;
import com.bellego.mapper.MemberConsumptionMapper;
import com.bellego.mapper.MemberMapper;
import com.bellego.service.AnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 分析服务实现
 */
@Slf4j
@Service
public class AnalysisServiceImpl implements AnalysisService {
    private final MemberMapper memberMapper;
    private final MemberConsumptionMapper consumptionMapper;

    public AnalysisServiceImpl(MemberMapper memberMapper, MemberConsumptionMapper consumptionMapper) {
        this.memberMapper = memberMapper;
        this.consumptionMapper = consumptionMapper;
    }

    @Override
    public Map<String, Object> rfm() {
        log.info("开始执行RFM分析");
        List<Member> members = memberMapper.selectList(new LambdaQueryWrapper<>());
        List<MemberConsumption> consumptions = consumptionMapper.selectList(new LambdaQueryWrapper<>());
        Map<String, Long> frequencyMap = consumptions.stream().collect(Collectors.groupingBy(MemberConsumption::getMemberId, Collectors.counting()));
        return Map.of("totalMembers", members.size(), "segments", members.stream().map(member -> {
            Map<String, Object> item = new HashMap<>();
            long recency = member.getLastConsumeTime() == null ? 999 : ChronoUnit.DAYS.between(member.getLastConsumeTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now());
            long frequency = frequencyMap.getOrDefault(member.getId(), 0L);
            BigDecimal monetary = member.getTotalConsumption() == null ? BigDecimal.ZERO : member.getTotalConsumption();
            item.put("memberId", member.getId());
            item.put("memberName", member.getName());
            item.put("recencyDays", recency);
            item.put("frequency", frequency);
            item.put("monetary", monetary);
            item.put("rLevel", scoreRecency(recency));
            item.put("fLevel", scoreFrequency(frequency));
            item.put("mLevel", scoreMonetary(monetary));
            return item;
        }).toList());
    }

    @Override
    public Map<String, Object> lifecycle(String period) {
        log.info("开始执行会员生命周期分析，period={}", period);
        List<Member> members = memberMapper.selectList(new LambdaQueryWrapper<>());
        Map<String, Long> newTrend = members.stream().filter(member -> member.getCreateTime() != null).collect(Collectors.groupingBy(member -> bucket(member.getCreateTime(), period), LinkedHashMap::new, Collectors.counting()));
        long active = members.stream().filter(member -> member.getLastConsumeTime() != null && daysBetween(member.getLastConsumeTime(), new Date()) <= 30).count();
        long lost = members.stream().filter(member -> member.getLastConsumeTime() == null || daysBetween(member.getLastConsumeTime(), new Date()) > 90).count();
        return Map.of("newTrend", newTrend, "activeCount", active, "lostCount", lost, "totalMembers", members.size());
    }

    @Override
    public Map<String, Object> orderAmount() {
        log.info("开始执行客单价分布分析");
        List<MemberConsumption> consumptions = consumptionMapper.selectList(new LambdaQueryWrapper<>());
        Map<String, Long> bucketCount = new LinkedHashMap<>();
        bucketCount.put("0-100", 0L);
        bucketCount.put("101-300", 0L);
        bucketCount.put("301-500", 0L);
        bucketCount.put("500+", 0L);
        consumptions.forEach(item -> {
            BigDecimal amount = item.getAmount();
            if (amount.compareTo(BigDecimal.valueOf(100)) <= 0) bucketCount.computeIfPresent("0-100", (k, v) -> v + 1);
            else if (amount.compareTo(BigDecimal.valueOf(300)) <= 0)
                bucketCount.computeIfPresent("101-300", (k, v) -> v + 1);
            else if (amount.compareTo(BigDecimal.valueOf(500)) <= 0)
                bucketCount.computeIfPresent("301-500", (k, v) -> v + 1);
            else bucketCount.computeIfPresent("500+", (k, v) -> v + 1);
        });
        return Map.of("buckets", bucketCount, "totalOrders", consumptions.size());
    }

    @Override
    public Map<String, Object> repurchase() {
        log.info("开始执行复购率分析");
        List<MemberConsumption> consumptions = consumptionMapper.selectList(new LambdaQueryWrapper<>());
        Map<String, Long> countByMember = consumptions.stream().collect(Collectors.groupingBy(MemberConsumption::getMemberId, Collectors.counting()));
        long repurchaseMembers = countByMember.values().stream().filter(count -> count >= 2).count();
        long totalMembers = countByMember.size();
        double rate = totalMembers == 0 ? 0 : (double) repurchaseMembers / totalMembers;
        return Map.of("repurchaseMembers", repurchaseMembers, "consumingMembers", totalMembers, "repurchaseRate", rate);
    }

    @Override
    public Map<String, Object> timeDistribution() {
        log.info("开始执行消费时段分析");
        List<MemberConsumption> consumptions = consumptionMapper.selectList(new LambdaQueryWrapper<>());
        Map<Integer, Long> distribution = consumptions.stream().filter(item -> item.getConsumeTime() != null).collect(Collectors.groupingBy(item -> item.getConsumeTime().toInstant().atZone(ZoneId.systemDefault()).getHour(), TreeMap::new, Collectors.counting()));
        return Map.of("distribution", distribution);
    }

    @Override
    public List<MemberLevelCountVo> levelCount() {
        log.info("开始查询会员等级分布");
        return memberMapper.getLevelCount();
    }

    @Override
    public List<DailyConsumeVO> dailyConsume() {
        log.info("开始查询最近消费趋势");
        // 查询最新消费时间
        MemberConsumption latest = consumptionMapper.selectOne(new LambdaQueryWrapper<MemberConsumption>()
                .orderByDesc(MemberConsumption::getConsumeTime)
                .last("LIMIT 1"));
        Date latestConsumeTime = latest.getConsumeTime();
        LocalDate endDate = latestConsumeTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // 计算起始日期并转换为 Date
        LocalDate startDate = endDate.minusDays(9);
        Date startDateTime = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDateTime = Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()); // 下一天的 00:00:00

        List<DailyConsumeVO> dailyTotalAmount = consumptionMapper.getDailyTotalAmount(startDateTime, endDateTime);

        // 填充缺失的日期
        Map<LocalDate, DailyConsumeVO> dateMap = new LinkedHashMap<>();
        for (DailyConsumeVO vo : dailyTotalAmount) {
            dateMap.put(vo.getConsumeDate(), vo);
        }
        List<DailyConsumeVO> result = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DailyConsumeVO vo = dateMap.get(date);
            if (vo == null) {
                vo = new DailyConsumeVO();
                vo.setConsumeDate(date);
                vo.setTotalAmount(BigDecimal.ZERO);
            }
            result.add(vo);
        }
        return result;
    }

    private int scoreRecency(long days) {
        if (days <= 7) return 5;
        if (days <= 30) return 4;
        if (days <= 60) return 3;
        if (days <= 90) return 2;
        return 1;
    }

    private int scoreFrequency(long count) {
        if (count >= 20) return 5;
        if (count >= 10) return 4;
        if (count >= 5) return 3;
        if (count >= 2) return 2;
        return 1;
    }

    private int scoreMonetary(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.valueOf(3000)) >= 0) return 5;
        if (amount.compareTo(BigDecimal.valueOf(1500)) >= 0) return 4;
        if (amount.compareTo(BigDecimal.valueOf(800)) >= 0) return 3;
        if (amount.compareTo(BigDecimal.valueOf(200)) >= 0) return 2;
        return 1;
    }

    private long daysBetween(Date start, Date end) {
        return ChronoUnit.DAYS.between(start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    private String bucket(Date date, String period) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if ("MONTH".equalsIgnoreCase(period))
            return localDate.getYear() + "-" + String.format("%02d", localDate.getMonthValue());
        return localDate.toString();
    }
}