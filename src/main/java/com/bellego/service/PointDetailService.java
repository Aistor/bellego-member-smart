package com.bellego.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.domain.dto.marketing.PointDetailQueryDto;
import com.bellego.domain.entity.PointDetail;
import org.springframework.web.multipart.MultipartFile;

public interface PointDetailService {
    IPage<PointDetail> page(PointDetailQueryDto dto);
    void save(PointDetail pointDetail);
    void importCsv(MultipartFile file);
}