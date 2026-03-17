package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.common.result.PageResult;
import com.bellego.common.util.CsvImportUtils;
import com.bellego.domain.dto.marketing.PointDetailQueryRequest;
import com.bellego.domain.entity.PointDetail;
import com.bellego.mapper.PointDetailMapper;
import com.bellego.service.PointDetailService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
public class PointDetailServiceImpl implements PointDetailService {

    private final PointDetailMapper pointDetailMapper;
    private final CsvImportUtils csvImportUtils;

    public PointDetailServiceImpl(PointDetailMapper pointDetailMapper, CsvImportUtils csvImportUtils) {
        this.pointDetailMapper = pointDetailMapper;
        this.csvImportUtils = csvImportUtils;
    }

    @Override
    public PageResult<PointDetail> page(PointDetailQueryRequest request) {
        LambdaQueryWrapper<PointDetail> wrapper = new LambdaQueryWrapper<PointDetail>()
                .eq(request.getMemberId() != null && !request.getMemberId().isBlank(), PointDetail::getMemberId, request.getMemberId())
                .eq(request.getType() != null, PointDetail::getType, request.getType())
                .orderByDesc(PointDetail::getCreateTime);
        Page<PointDetail> page = pointDetailMapper.selectPage(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
        return PageResult.of(page);
    }

    @Override
    public void save(PointDetail pointDetail) {
        if (pointDetail.getCreateTime() == null) {
            pointDetail.setCreateTime(new Date());
        }
        pointDetailMapper.insert(pointDetail);
    }

    @Override
    public void importCsv(MultipartFile file) {
        List<PointDetail> details = csvImportUtils.read(file, parts -> {
            PointDetail detail = new PointDetail();
            detail.setMemberId(parts[0].trim());
            detail.setType(Integer.parseInt(parts[1].trim()));
            detail.setPoints(Integer.parseInt(parts[2].trim()));
            detail.setBalance(Integer.parseInt(parts[3].trim()));
            detail.setSource(parts.length > 4 ? parts[4].trim() : "import");
            detail.setSourceId(parts.length > 5 ? parts[5].trim() : null);
            detail.setRemark(parts.length > 6 ? parts[6].trim() : "批量导入");
            detail.setCreateTime(new Date());
            return detail;
        });
        details.forEach(this::save);
    }
}

