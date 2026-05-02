package com.bellego.controller;

import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.domain.vo.analysis.DailyConsumeVO;
import com.bellego.domain.vo.analysis.MemberCategoryVO;
import com.bellego.domain.vo.analysis.MemberGrowthVO;
import com.bellego.domain.vo.analysis.MemberLevelCountVO;
import com.bellego.service.AnalysisService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public Result<Map<String, Object>> rfm(@RequestParam(required = false) String date) {
        return ResultBuilder.success(analysisService.rfm(date));
    }

    @GetMapping("/order-amount")
    @PreAuthorize("hasAuthority('analysis:view')")
    public Result<Map<String, Object>> orderAmount(@RequestParam(required = false) String storeId) {
        return ResultBuilder.success(analysisService.orderAmount(storeId));
    }

    @GetMapping("/repurchase")
    @PreAuthorize("hasAuthority('analysis:view')")
    public Result<Map<String, Object>> repurchase(@RequestParam(required = false) String storeId) {
        return ResultBuilder.success(analysisService.repurchase(storeId));
    }

    @GetMapping("/time-distribution")
    @PreAuthorize("hasAuthority('analysis:view')")
    public Result<Map<String, Object>> timeDistribution(@RequestParam(required = false) String storeId) {
        return ResultBuilder.success(analysisService.timeDistribution(storeId));
    }

    @GetMapping("/level-count")
    public Result<List<MemberLevelCountVO>> levelCount() {
        return ResultBuilder.success(analysisService.levelCount());
    }

    @GetMapping("/daily-consume")
    public Result<List<DailyConsumeVO>> dailyConsume() {
        return ResultBuilder.success(analysisService.dailyConsume());
    }

    @GetMapping("/category")
    @PreAuthorize("hasAuthority('analysis:view')")
    public Result<List<MemberCategoryVO>> memberCategory(@RequestParam(required = false) String date) {
        return ResultBuilder.success(analysisService.memberCategory(date));
    }

    @GetMapping("/growth")
    @PreAuthorize("hasAuthority('analysis:view')")
    public Result<List<MemberGrowthVO>> memberGrowth(@RequestParam(required = false) String date) {
        return ResultBuilder.success(analysisService.memberGrowth(date));
    }

    @GetMapping("/start-date")
    public Result<String> getStartDate() {
        return ResultBuilder.success(analysisService.getStartDate());
    }

    @GetMapping("/store-consumption")
    public Result<Map<String, Object>> getStoreConsumption(@RequestParam(required = false) String date) {
        return ResultBuilder.success(analysisService.getStoreConsumption(date));
    }
}
