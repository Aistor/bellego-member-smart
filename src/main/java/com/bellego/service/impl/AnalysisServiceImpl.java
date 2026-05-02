package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.bellego.common.exception.BusinessException;
import com.bellego.domain.entity.Member;
import com.bellego.domain.entity.MemberConsumption;
import com.bellego.domain.vo.analysis.DailyConsumeVO;
import com.bellego.domain.vo.analysis.MemberCategoryVO;
import com.bellego.domain.vo.analysis.MemberGrowthVO;
import com.bellego.domain.vo.analysis.MemberLevelCountVO;
import com.bellego.mapper.MemberConsumptionMapper;
import com.bellego.mapper.MemberMapper;
import com.bellego.service.AnalysisService;
import com.bellego.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 分析服务实现
 */
@Slf4j
@Service
public class AnalysisServiceImpl implements AnalysisService {
    private final RedisUtils redisUtils;
    private final MemberMapper memberMapper;
    private final MemberConsumptionMapper consumptionMapper;

    public AnalysisServiceImpl(RedisUtils redisUtils, MemberMapper memberMapper, MemberConsumptionMapper consumptionMapper) {
        this.redisUtils = redisUtils;
        this.memberMapper = memberMapper;
        this.consumptionMapper = consumptionMapper;
    }

    private static final String REDIS_KEY_PREFIX = "analysis:";

    @Value("${bellego.redis.expire-hours}")
    Integer EXPIRE_HOURS;

    @Override
    public Map<String, Object> rfm(String date) {
        log.info("开始执行RFM分析，date={}", date);
        String key = REDIS_KEY_PREFIX + "rfm:" + (StringUtils.isNotBlank(date) ? date : "all");
        Map<String, Object> resultMap = (Map) redisUtils.get(key);
        if (resultMap != null) {
            log.info("缓存数据命中，直接返回");
            return resultMap;
        }
        // 根据date筛选消费记录
        LambdaQueryWrapper<MemberConsumption> consumptionWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Member> memberWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(date)) {
            LocalDate endDate = YearMonth.parse(date, DateTimeFormatter.ofPattern("yyyy-MM")).atEndOfMonth();
            Date endDateTime = Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            consumptionWrapper.le(MemberConsumption::getConsumeTime, endDateTime);
            memberWrapper.le(Member::getLastConsumeTime, endDateTime);
        }
        List<Member> members = memberMapper.selectList(memberWrapper);
        List<MemberConsumption> consumptions = consumptionMapper.selectList(consumptionWrapper);
        Map<String, Long> frequencyMap = consumptions.stream().collect(Collectors.groupingBy(MemberConsumption::getMemberId, Collectors.counting()));
        LocalDate referenceDate = StringUtils.isNotBlank(date)
                ? YearMonth.parse(date, DateTimeFormatter.ofPattern("yyyy-MM")).atEndOfMonth()
                : LocalDate.now();

        // 收集所有R、F、M值
        List<Double> recencyValues = new ArrayList<>();
        List<Double> frequencyValues = new ArrayList<>();
        List<Double> monetaryValues = new ArrayList<>();
        for (Member member : members) {
            long recency = member.getLastConsumeTime() == null ? 999 : ChronoUnit.DAYS.between(member.getLastConsumeTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), referenceDate);
            long frequency = frequencyMap.getOrDefault(member.getId(), 0L);
            BigDecimal monetary = member.getTotalConsumption() == null ? BigDecimal.ZERO : member.getTotalConsumption();
            recencyValues.add((double) recency);
            frequencyValues.add((double) frequency);
            monetaryValues.add(monetary.doubleValue());
        }

        // 计算分位数阈值（20%、40%、60%、80%）
        double[] rThresholds = thresholdsCompute(recencyValues);
        double[] fThresholds = thresholdsCompute(frequencyValues);
        double[] mThresholds = thresholdsCompute(monetaryValues);

        // 基于分位数评分
        Map<String, Object> result = Map.of("totalMembers", members.size(), "segments", members.stream().map(member -> {
            Map<String, Object> item = new HashMap<>();
            long recency = member.getLastConsumeTime() == null ? 999 : ChronoUnit.DAYS.between(member.getLastConsumeTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), referenceDate);
            long frequency = frequencyMap.getOrDefault(member.getId(), 0L);
            BigDecimal monetary = member.getTotalConsumption() == null ? BigDecimal.ZERO : member.getTotalConsumption();
            item.put("memberId", member.getId());
            item.put("memberName", member.getName());
            item.put("recencyDays", recency);
            item.put("frequency", frequency);
            item.put("monetary", monetary);
            item.put("rLevel", getScore(recency, rThresholds, true));
            item.put("fLevel", getScore(frequency, fThresholds, false));
            item.put("mLevel", getScore(monetary.doubleValue(), mThresholds, false));
            return item;
        }).toList());
        // 将结果存入缓存
        redisUtils.set(key, result, EXPIRE_HOURS, TimeUnit.HOURS);
        return result;
    }

    @Override
    public Map<String, Object> orderAmount() {
        log.info("开始执行客单价分布分析");
        String key = REDIS_KEY_PREFIX + "orderAmount";
        Map<String, Object> resultMap = (Map) redisUtils.get(key);
        if (resultMap != null) {
            log.info("缓存数据命中，直接返回");
            return resultMap;
        }
        log.info("无缓存数据，重新计算");
        List<MemberConsumption> consumptions = consumptionMapper.selectList(new LambdaQueryWrapper<>());
        Map<String, Long> bucketCount = new LinkedHashMap<>();
        bucketCount.put("0-100", 0L);
        bucketCount.put("101-300", 0L);
        bucketCount.put("301-500", 0L);
        bucketCount.put("500+", 0L);
        consumptions.forEach(item -> {
            BigDecimal amount = item.getAmount();
            if (amount.compareTo(BigDecimal.valueOf(100)) <= 0) {
                bucketCount.computeIfPresent("0-100", (k, v) -> v + 1);
            } else if (amount.compareTo(BigDecimal.valueOf(300)) <= 0) {
                bucketCount.computeIfPresent("101-300", (k, v) -> v + 1);
            } else if (amount.compareTo(BigDecimal.valueOf(500)) <= 0) {
                bucketCount.computeIfPresent("301-500", (k, v) -> v + 1);
            } else {
                bucketCount.computeIfPresent("500+", (k, v) -> v + 1);
            }
        });
        // 将结果存入缓存
        resultMap = Map.of("buckets", bucketCount, "totalOrders", consumptions.size());
        redisUtils.set(key, resultMap, EXPIRE_HOURS, TimeUnit.HOURS);

        return resultMap;
    }

    @Override
    public Map<String, Object> repurchase() {
        log.info("开始执行复购率分析");
        String key = REDIS_KEY_PREFIX + "repurchase";
        Map<String, Object> resultMap = (Map) redisUtils.get(key);
        if (resultMap != null) {
            log.info("缓存数据命中，直接返回");
            return resultMap;
        }
        List<MemberConsumption> consumptions = consumptionMapper.selectList(new LambdaQueryWrapper<>());
        Map<String, Long> countByMember = consumptions.stream().collect(Collectors.groupingBy(MemberConsumption::getMemberId, Collectors.counting()));
        long repurchaseMembers = countByMember.values().stream().filter(count -> count >= 2).count();
        long totalMembers = countByMember.size();
        double rate = totalMembers == 0 ? 0 : (double) repurchaseMembers / totalMembers;
        resultMap = Map.of("repurchaseMembers", repurchaseMembers, "consumingMembers", totalMembers, "repurchaseRate", rate);
        redisUtils.set(key, resultMap, EXPIRE_HOURS, TimeUnit.HOURS);
        return resultMap;
    }

    @Override
    public Map<String, Object> timeDistribution() {
        log.info("开始执行消费时段分析");
        String key = REDIS_KEY_PREFIX + "timeDistribution";
        Map<String, Object> resultMap = (Map) redisUtils.get(key);
        if (resultMap != null) {
            log.info("缓存数据命中，直接返回");
            return resultMap;
        }
        List<MemberConsumption> consumptions = consumptionMapper.selectList(new LambdaQueryWrapper<>());
        Map<Integer, Long> distribution = consumptions.stream()
                .filter(item -> item.getConsumeTime() != null)
                .collect(Collectors.groupingBy(item -> item.getConsumeTime().toInstant().atZone(ZoneId.systemDefault()).getHour(), TreeMap::new, Collectors.counting()));
        resultMap = Map.of("distribution", distribution);
        redisUtils.set(key, resultMap, EXPIRE_HOURS, TimeUnit.HOURS);
        return resultMap;
    }

    /**
     * 会员等级分布
     * 主页用
     */
    @Override
    public List<MemberLevelCountVO> levelCount() {
        log.info("开始查询会员等级分布");
        return memberMapper.getLevelCount();
    }

    /**
     * 查询最近消费趋势（默认查询最新消费时间往前10天的数据）
     * 主页用
     */
    @Override
    public List<DailyConsumeVO> dailyConsume() {
        log.info("开始查询最近消费趋势");
        // 查询最新消费时间
        MemberConsumption latest = consumptionMapper.selectOne(new LambdaQueryWrapper<MemberConsumption>()
                .orderByDesc(MemberConsumption::getConsumeTime)
                .last("LIMIT 1"));
        if (latest == null || latest.getConsumeTime() == null) {
            return List.of();
        }
        // 计算时间范围
        Date latestConsumeTime = latest.getConsumeTime();
        LocalDate endDate = latestConsumeTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate startDate = endDate.minusDays(9);
        Date startDateTime = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDateTime = Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<DailyConsumeVO> dailyTotalAmount = consumptionMapper.getDailyTotalAmount(startDateTime, endDateTime);
        Map<LocalDate, DailyConsumeVO> dateMap = new LinkedHashMap<>();
        for (DailyConsumeVO vo : dailyTotalAmount) {
            dateMap.put(vo.getConsumeDate(), vo);
        }
        // 转换为VO列表，填充缺失的日期
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

    /**
     * 截止至指定月份的会员类型分析（date为空时，默认为当前时间）
     */
    @Override
    public List<MemberCategoryVO> memberCategory(String date) {
        log.info("开始执行会员类型分析");
        String key = REDIS_KEY_PREFIX + "memberCategory:" + (StringUtils.isBlank(date) ? "all" : date);
        List<MemberCategoryVO> resultList = (List) redisUtils.get(key);
        if (resultList != null) {
            log.info("缓存数据命中，直接返回");
            return resultList;
        }
        log.info("无缓存数据，重新计算");
        // 解析日期，获取统计截止时点
        LocalDate snapshotDate = resolveDate(date);
        // 查询所有会员
        List<Member> members = memberMapper.selectList(new LambdaQueryWrapper<>());
        // 筛选出在snapshotDate之前注册的会员
        List<Member> snapshotMembers = members.stream()
                .filter(member -> member.getCreateTime() != null)
                .filter(member -> !member.getCreateTime().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .isAfter(snapshotDate))
                .toList();
        // 按会员类型分组统计
        Map<String, Long> categoryCount = snapshotMembers.stream()
                .collect(Collectors.groupingBy(member -> {
                    if (isActiveAt(member, snapshotDate)) {
                        return "活跃会员";
                    } else if (isSilentAt(member, snapshotDate)) {
                        return "沉默会员";
                    } else {
                        return "流失会员";
                    }
                }, Collectors.counting()));
        // 转换为VO列表
        List<MemberCategoryVO> result = new ArrayList<>();
        categoryCount.forEach((category, count) -> {
            MemberCategoryVO vo = new MemberCategoryVO();
            vo.setCategory(category);
            vo.setCount(count);
            result.add(vo);
        });

        redisUtils.set(key, result, EXPIRE_HOURS, TimeUnit.HOURS);
        log.info("会员类型分析完成，截止 {}，总会员数：{}", snapshotDate, snapshotMembers.size());
        return result;
    }

    @Override
    public List<MemberGrowthVO> memberGrowth(String date) {
        String key = REDIS_KEY_PREFIX + "memberGrowth:" + (StringUtils.isBlank(date) ? "all" : date);
        List<MemberGrowthVO> resultList = (List) redisUtils.get(key);
        if (resultList != null) {
            log.info("会员增长数，缓存数据命中，直接返回");
            return resultList;
        }
        log.info("会员增长数，无缓存数据，重新计算");
        if (StringUtils.isBlank(date)) {
            // date为空，查询所有月份的会员增长数
            resultList = getMonthsGrowth();
        } else {
            // date不为空，查询指定月份的每日增长数
            resultList = getDailyGrowth(date);
        }
        redisUtils.set(key, resultList, EXPIRE_HOURS, TimeUnit.HOURS);
        return resultList;
    }

    /**
     * 查询所有月份的会员增长数（按月统计）
     */
    private List<MemberGrowthVO> getMonthsGrowth() {
        log.info("开始查询所有月份的会员增长数");
        // 查询所有会员
        List<Member> members = memberMapper.selectList(new LambdaQueryWrapper<>());
        // 按月份分组统计每月新增会员数
        Map<String, Long> monthlyCountMap = members.stream()
                .filter(member -> member.getCreateTime() != null)
                .collect(Collectors.groupingBy(
                        member -> {
                            LocalDate localDate = member.getCreateTime().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            return localDate.getYear() + "-" + String.format("%02d", localDate.getMonthValue());
                        },
                        TreeMap::new,
                        Collectors.counting()
                ));
        // 转换为VO列表
        List<MemberGrowthVO> result = new ArrayList<>();
        monthlyCountMap.forEach((month, count) -> {
            MemberGrowthVO vo = new MemberGrowthVO();
            // 使用月份的第一天作为日期
            String[] parts = month.split("-");
            LocalDate firstDay = YearMonth.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])).atDay(1);
            vo.setDate(firstDay);
            vo.setMonth(month);
            vo.setCount(count);
            result.add(vo);
        });
        log.info("所有月份会员增长统计完成，共 {} 个月份有数据", monthlyCountMap.size());
        return result;
    }

    /**
     * 查询指定月份的每日会员增长数
     */
    private List<MemberGrowthVO> getDailyGrowth(String date) {
        log.info("开始执行会员增长分析，月份：{}", date);
        // 解析年月，获取月初和月末
        YearMonth yearMonth = YearMonth.parse(date, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        // 查询该月份所有新增会员
        List<Member> members = memberMapper.selectList(new LambdaQueryWrapper<Member>()
                .between(Member::getCreateTime, startOfMonth, endOfMonth));
        // 按日期分组统计每日新增会员数
        Map<LocalDate, Long> dailyCountMap = members.stream()
                .filter(member -> member.getCreateTime() != null)
                .collect(Collectors.groupingBy(
                        member -> member.getCreateTime().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                        Collectors.counting()
                ));
        // 补全该月所有日期的数据（没有新增的日期为0）
        List<MemberGrowthVO> result = new ArrayList<>();
        for (LocalDate currentDate = yearMonth.atDay(1);
             !currentDate.isAfter(yearMonth.atEndOfMonth());
             currentDate = currentDate.plusDays(1)) {
            MemberGrowthVO vo = new MemberGrowthVO();
            vo.setDate(currentDate);
            vo.setDay(currentDate.getDayOfMonth());
            vo.setCount(dailyCountMap.getOrDefault(currentDate, 0L));
            result.add(vo);
        }
        log.info("会员增长分析完成，{}月新增会员总数：{}", date, members.size());
        return result;
    }

    /**
     * 获取会员消费数据的起始日期（yyyy-MM）
     */
    public String getStartDate() {
        MemberConsumption consumption = consumptionMapper.selectOne(new LambdaQueryWrapper<MemberConsumption>()
                .orderByAsc(MemberConsumption::getConsumeTime)
                .last("limit 1"));
        Date startDate = consumption.getConsumeTime();
        // 格式化日期（yyyy-MM）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return startDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(formatter);
    }

    /**
     * 计算分位数阈值（20%、40%、60%、80%）
     */
    private double[] thresholdsCompute(List<Double> values) {
        List<Double> sorted = values.stream().sorted().toList();
        return new double[]{
                percentile(sorted, 0.2),
                percentile(sorted, 0.4),
                percentile(sorted, 0.6),
                percentile(sorted, 0.8)
        };
    }

    /**
     * 计算排序后列表的百分位数值（线性插值）
     */
    private double percentile(List<Double> sorted, double p) {
        if (sorted.isEmpty()) return 0;
        double pos = p * (sorted.size() - 1);
        int lower = (int) Math.floor(pos);
        int upper = (int) Math.ceil(pos);
        if (lower == upper) return sorted.get(lower);
        return sorted.get(lower) + (sorted.get(upper) - sorted.get(lower)) * (pos - lower);
    }

    /**
     * 基于分位数阈值评分（1-5分）
     * reverse: true表示值越小得分越高（如R），false表示值越大得分越高（如F、M）
     */
    private int getScore(double value, double[] thresholds, boolean reverse) {
        if (reverse) {
            if (value <= thresholds[0]) return 5;
            if (value <= thresholds[1]) return 4;
            if (value <= thresholds[2]) return 3;
            if (value <= thresholds[3]) return 2;
            return 1;
        } else {
            if (value <= thresholds[0]) return 1;
            if (value <= thresholds[1]) return 2;
            if (value <= thresholds[2]) return 3;
            if (value <= thresholds[3]) return 4;
            return 5;
        }
    }

    private long daysBetween(Date start, LocalDate end) {
        return ChronoUnit.DAYS.between(start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), end);
    }

    /**
     * 解析日期字符串
     */
    private LocalDate resolveDate(String date) {
        if (date == null || date.isBlank()) {
            return LocalDate.now();
        }
        try {
            return YearMonth.parse(date).atEndOfMonth();
        } catch (Exception ex) {
            log.error("date 解析错误，date={}", date);
            throw new BusinessException("date 解析错误");
        }
    }

    /**
     * 判断会员是否在指定时间段内活跃 (最后消费时间距指定时间 <= 30天)
     */
    private boolean isActiveAt(Member member, LocalDate snapshotDate) {
        return member.getLastConsumeTime() != null && daysBetween(member.getLastConsumeTime(), snapshotDate) <= 30;
    }

    /**
     * 判断会员是否在指定时间段内流失 (最后消费时间距指定时间 > 90天)
     */
    private boolean isLostAt(Member member, LocalDate snapshotDate) {
        return member.getLastConsumeTime() == null || daysBetween(member.getLastConsumeTime(), snapshotDate) > 90;
    }

    /**
     * 判断会员是否在指定时间段内沉默 (30天 < 最后消费时间距指定时间 <= 90天)
     */
    private boolean isSilentAt(Member member, LocalDate snapshotDate) {
        return member.getLastConsumeTime() != null
                && daysBetween(member.getLastConsumeTime(), snapshotDate) > 30
                && daysBetween(member.getLastConsumeTime(), snapshotDate) <= 90;
    }
}
