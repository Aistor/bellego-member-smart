package com.bellego.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.aop.LogOperation;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.common.util.PageConvertUtils;
import com.bellego.common.util.VoMapper;
import com.bellego.domain.dto.member.ConsumptionCreateDto;
import com.bellego.domain.dto.member.ConsumptionQueryDto;
import com.bellego.domain.vo.ConsumptionVo;
import com.bellego.service.ConsumptionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/consumptions")
public class ConsumptionController {
    private final ConsumptionService consumptionService;
    private final VoMapper voMapper;

    public ConsumptionController(ConsumptionService consumptionService, VoMapper voMapper) {
        this.consumptionService = consumptionService;
        this.voMapper = voMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('consumption:view')")
    public Result<IPage<ConsumptionVo>> page(ConsumptionQueryDto dto) {
        return ResultBuilder.success(PageConvertUtils.map(consumptionService.page(dto), voMapper::toConsumptionVo));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('consumption:view')")
    public Result<ConsumptionVo> get(@PathVariable String id) {
        return ResultBuilder.success(voMapper.toConsumptionVo(consumptionService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('consumption:add')")
    @LogOperation(module = "consumption", action = "create consumption")
    public Result<Void> create(@Valid @RequestBody ConsumptionCreateDto dto) {
        consumptionService.create(dto);
        return ResultBuilder.success();
    }

    @PostMapping("/import")
    @PreAuthorize("hasAuthority('consumption:import')")
    @LogOperation(module = "consumption", action = "import consumptions")
    public Result<Void> importCsv(@RequestPart MultipartFile file) {
        consumptionService.importCsv(file);
        return ResultBuilder.success();
    }
}