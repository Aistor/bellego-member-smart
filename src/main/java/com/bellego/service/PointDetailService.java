package com.bellego.service;

import com.bellego.common.result.PageResult;
import com.bellego.domain.dto.marketing.PointDetailQueryRequest;
import com.bellego.domain.entity.PointDetail;
import org.springframework.web.multipart.MultipartFile;

public interface PointDetailService {
    PageResult<PointDetail> page(PointDetailQueryRequest request);
    void save(PointDetail pointDetail);
    void importCsv(MultipartFile file);
}

