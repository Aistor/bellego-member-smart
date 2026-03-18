package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.common.util.CsvImportUtils;
import com.bellego.domain.dto.marketing.PointDetailQueryDto;
import com.bellego.domain.entity.PointDetail;
import com.bellego.mapper.PointDetailMapper;
import com.bellego.service.PointDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * 积分明细服务实现
 */
@Slf4j
@Service
public class PointDetailServiceImpl implements PointDetailService {
    private final PointDetailMapper pointDetailMapper;
    private final CsvImportUtils csvImportUtils;

    public PointDetailServiceImpl(PointDetailMapper pointDetailMapper, CsvImportUtils csvImportUtils) {
        this.pointDetailMapper = pointDetailMapper;
        this.csvImportUtils = csvImportUtils;
    }

    @Override
    public IPage<PointDetail> page(PointDetailQueryDto dto) {
        log.info("开始分页查询积分明细，memberId={}, type={}", dto.getMemberId(), dto.getType());
        LambdaQueryWrapper<PointDetail> wrapper = new LambdaQueryWrapper<PointDetail>().eq(dto.getMemberId() != null && !dto.getMemberId().isBlank(), PointDetail::getMemberId, dto.getMemberId()).eq(dto.getType() != null, PointDetail::getType, dto.getType()).orderByDesc(PointDetail::getCreateTime);
        return pointDetailMapper.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
    }

    @Override
    public void save(PointDetail pointDetail) {
        log.info("保存积分明细，memberId={}, points={}", pointDetail.getMemberId(), pointDetail.getPoints());
        if (pointDetail.getCreateTime() == null) {
            pointDetail.setCreateTime(new Date());
        }
        pointDetailMapper.insert(pointDetail);
    }

    @Override
    public void importCsv(MultipartFile file) {
        List<PointDetail> list = csvImportUtils.read(file, parts -> {
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
        log.info("开始导入积分明细，数量={}", list.size());
        list.forEach(this::save);
    }
}