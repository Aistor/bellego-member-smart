package com.bellego.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@Builder
public class PageResult<T> {
    private long pageNum;
    private long pageSize;
    private long total;
    private List<T> records;

    public static <T> PageResult<T> of(IPage<T> page) {
        return PageResult.<T>builder()
                .pageNum(page.getCurrent())
                .pageSize(page.getSize())
                .total(page.getTotal())
                .records(page.getRecords())
                .build();
    }

    public static <T> PageResult<T> of(long pageNum, long pageSize, long total, List<T> records) {
        return PageResult.<T>builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .total(total)
                .records(records == null ? Collections.emptyList() : records)
                .build();
    }
}

