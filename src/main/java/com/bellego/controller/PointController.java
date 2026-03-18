package com.bellego.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.aop.LogOperation;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.common.util.PageConvertUtils;
import com.bellego.common.util.VoMapper;
import com.bellego.domain.dto.common.StatusUpdateDto;
import com.bellego.domain.dto.marketing.PointDetailQueryDto;
import com.bellego.domain.dto.marketing.PointRuleUpsertDto;
import com.bellego.domain.vo.PointDetailVo;
import com.bellego.domain.vo.PointRuleVo;
import com.bellego.service.PointDetailService;
import com.bellego.service.PointRuleService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class PointController {
    private final PointRuleService pointRuleService;
    private final PointDetailService pointDetailService;
    private final VoMapper voMapper;

    public PointController(PointRuleService pointRuleService, PointDetailService pointDetailService, VoMapper voMapper) {
        this.pointRuleService = pointRuleService;
        this.pointDetailService = pointDetailService;
        this.voMapper = voMapper;
    }

    @GetMapping("/api/v1/point-rules")
    @PreAuthorize("hasAuthority('pointRule:view')")
    public Result<List<PointRuleVo>> listRules() {
        return ResultBuilder.success(pointRuleService.list().stream().map(voMapper::toPointRuleVo).toList());
    }

    @PostMapping("/api/v1/point-rules")
    @PreAuthorize("hasAuthority('pointRule:add')")
    @LogOperation(module = "pointRule", action = "create point rule")
    public Result<Void> createRule(@Valid @RequestBody PointRuleUpsertDto dto) {
        pointRuleService.create(dto);
        return ResultBuilder.success();
    }

    @PutMapping("/api/v1/point-rules/{id}")
    @PreAuthorize("hasAuthority('pointRule:edit')")
    @LogOperation(module = "pointRule", action = "update point rule")
    public Result<Void> updateRule(@PathVariable String id, @Valid @RequestBody PointRuleUpsertDto dto) {
        pointRuleService.update(id, dto);
        return ResultBuilder.success();
    }

    @DeleteMapping("/api/v1/point-rules/{id}")
    @PreAuthorize("hasAuthority('pointRule:delete')")
    @LogOperation(module = "pointRule", action = "delete point rule")
    public Result<Void> deleteRule(@PathVariable String id) {
        pointRuleService.delete(id);
        return ResultBuilder.success();
    }

    @PutMapping("/api/v1/point-rules/{id}/status")
    @PreAuthorize("hasAuthority('pointRule:edit')")
    @LogOperation(module = "pointRule", action = "update point rule status")
    public Result<Void> updateRuleStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateDto dto) {
        pointRuleService.updateStatus(id, dto.getStatus());
        return ResultBuilder.success();
    }

    @GetMapping("/api/v1/point-details")
    @PreAuthorize("hasAuthority('pointDetail:view')")
    public Result<IPage<PointDetailVo>> pageDetails(PointDetailQueryDto dto) {
        return ResultBuilder.success(PageConvertUtils.map(pointDetailService.page(dto), voMapper::toPointDetailVo));
    }

    @PostMapping("/api/v1/point-details/import")
    @PreAuthorize("hasAuthority('pointDetail:import')")
    @LogOperation(module = "pointDetail", action = "import point details")
    public Result<Void> importDetails(@RequestPart MultipartFile file) {
        pointDetailService.importCsv(file);
        return ResultBuilder.success();
    }
}