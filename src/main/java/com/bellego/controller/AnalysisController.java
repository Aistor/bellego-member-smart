package com.bellego.controller;

import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.service.AnalysisService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/rfm")
    @PreAuthorize("hasAuthority('analysis:view')")
    public Result<Map<String, Object>> rfm() {
        return ResultBuilder.success(analysisService.rfm());
    }

    @GetMapping("/lifecycle")
    @PreAuthorize("hasAuthority('analysis:view')")
    public Result<Map<String, Object>> lifecycle(@RequestParam(defaultValue = "DAY") String period) {
        return ResultBuilder.success(analysisService.lifecycle(period));
    }

    @GetMapping("/behavior/order-amount")
    @PreAuthorize("hasAuthority('analysis:view')")
    public Result<Map<String, Object>> orderAmount() {
        return ResultBuilder.success(analysisService.orderAmount());
    }

    @GetMapping("/behavior/repurchase")
    @PreAuthorize("hasAuthority('analysis:view')")
    public Result<Map<String, Object>> repurchase() {
        return ResultBuilder.success(analysisService.repurchase());
    }

    @GetMapping("/behavior/time-distribution")
    @PreAuthorize("hasAuthority('analysis:view')")
    public Result<Map<String, Object>> timeDistribution() {
        return ResultBuilder.success(analysisService.timeDistribution());
    }
}

