package com.bellego.service;

import java.util.Map;

public interface AnalysisService {
    Map<String, Object> rfm();
    Map<String, Object> lifecycle(String period);
    Map<String, Object> orderAmount();
    Map<String, Object> repurchase();
    Map<String, Object> timeDistribution();
}

