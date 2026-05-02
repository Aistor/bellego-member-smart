package com.bellego.service;

import com.bellego.domain.vo.analysis.DailyConsumeVO;
import com.bellego.domain.vo.analysis.MemberCategoryVO;
import com.bellego.domain.vo.analysis.MemberGrowthVO;
import com.bellego.domain.vo.analysis.MemberLevelCountVO;

import java.util.List;
import java.util.Map;

public interface AnalysisService {
    Map<String, Object> rfm(String date);
    Map<String, Object> orderAmount(String storeId);
    Map<String, Object> repurchase(String storeId);
    Map<String, Object> timeDistribution(String storeId);
    List<MemberLevelCountVO> levelCount();
    List<DailyConsumeVO> dailyConsume();
    List<MemberGrowthVO> memberGrowth(String date);
    List<MemberCategoryVO> memberCategory(String date);
    String getStartDate();
    Map<String, Object> getStoreConsumption(String date);
}
