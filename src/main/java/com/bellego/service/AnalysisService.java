package com.bellego.service;

import com.bellego.domain.vo.DailyConsumeVO;
import com.bellego.domain.vo.MemberLevelCountVo;

import java.util.List;
import java.util.Map;

public interface AnalysisService {
    Map<String, Object> rfm();
    Map<String, Object> lifecycle(String period);
    Map<String, Object> orderAmount();
    Map<String, Object> repurchase();
    Map<String, Object> timeDistribution();
    List<MemberLevelCountVo> levelCount();
    List<DailyConsumeVO> dailyConsume();
}

