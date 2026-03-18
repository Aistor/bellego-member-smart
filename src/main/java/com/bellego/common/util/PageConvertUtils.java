package com.bellego.common.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.function.Function;

public final class PageConvertUtils {

    private PageConvertUtils() {
    }

    public static <T, R> IPage<R> map(IPage<T> source, Function<T, R> mapper) {
        Page<R> target = new Page<>(source.getCurrent(), source.getSize(), source.getTotal());
        target.setRecords(source.getRecords().stream().map(mapper).toList());
        return target;
    }
}